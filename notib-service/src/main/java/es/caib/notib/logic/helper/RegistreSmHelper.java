/**
 * 
 */
package es.caib.notib.logic.helper;

import com.google.common.base.Strings;
import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.logic.intf.dto.AccioParam;
import es.caib.notib.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioCodi;
import es.caib.notib.logic.intf.dto.IntegracioInfo;
import es.caib.notib.logic.intf.dto.TipusUsuariEnumDto;
import es.caib.notib.logic.intf.exception.RegistreNotificaException;
import es.caib.notib.logic.intf.service.AuditService;
import es.caib.notib.logic.statemachine.mappers.EnviamentRegistreMapper;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.plugin.registre.RespostaConsultaRegistre;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper per a interactuar amb la versió 2 del servei web de Notific@.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class RegistreSmHelper {

	private final PluginHelper pluginHelper;
	private final IntegracioHelper integracioHelper;
	private final EnviamentTableHelper enviamentTableHelper;
	private final NotificacioEventHelper notificacioEventHelper;
	private final ConfigHelper configHelper;
	private final AuditHelper auditHelper;
	private final CallbackHelper callbackHelper;
	private final EnviamentRegistreMapper enviamentRegistreMapper;

	public static Map<Long, String[]> llibreOficina = new HashMap<>();

	public boolean registrarEnviament(NotificacioEnviamentEntity enviament, Integer numIntent) throws RegistreNotificaException {

		log.info(" [REG] Assentament sortida (registre) per l'enviament " + enviament.getId());
		long startTime = System.nanoTime();
		var success = false;

		var notificacio = enviament.getNotificacio();
		var entitat = notificacio.getEntitat();

		var sirActivat = isSirActivat();
		var enviamentSir = sirActivat && EnviamentTipus.SIR.equals(notificacio.getEnviamentTipus());
		var tipusOperacio = sirActivat ? (EnviamentTipus.NOTIFICACIO.equals(notificacio.getEnviamentTipus()) ? 1L : 2L) : null; //### [SIR-DESACTIVAT = registre normal, SIR-ACTIVAT = notificació/comunicació]

		// Informació del monitor
		var descInfo = "Inici procés registrar [Id: " + enviament.getId() + ", Estat: " + notificacio.getEstat() + "]";
		var tipusEnvInfo = new AccioParam("Tipus enviament: ", notificacio.getEnviamentTipus().name());
		var sirActivatInfo = new AccioParam("Sir activat", String.valueOf(isSirActivat()));
		var codiDir3Registre = !Strings.isNullOrEmpty(entitat.getDir3CodiReg()) ? entitat.getDir3CodiReg() : entitat.getDir3Codi();
		var accioInfo = new AccioParam("Procés descripció: ", " [REG] Realitzant nou assentament registral" + (enviamentSir ? " SIR " : " ") + "de l'enviament: " + enviament.getId());
		var info = new IntegracioInfo(IntegracioCodi.REGISTRE, descInfo, IntegracioAccioTipusEnumDto.ENVIAMENT, tipusEnvInfo, sirActivatInfo, accioInfo);
		info.setNotificacioId(notificacio.getId());
		info.setAplicacio(notificacio.getTipusUsuari(), notificacio.getCreatedBy().get().getCodi());
		// Registre SIR
		try {
			var request = ReqAssentamentRegistral.builder()
					.enviament(enviament)
					.dir3CodiRegistre(codiDir3Registre)
					.enviamentSir(enviamentSir)
					.tipusOperacio(tipusOperacio)
					.numIntent(numIntent).build();
			success = crearAssentamentRegistral(request);
		} catch (Exception ex) {
			var errorDescripcio = "Hi ha hagut un error registrant l'enviament + " + enviament.getId();
			log.error(errorDescripcio, ex);
			integracioHelper.addAccioError(info, errorDescripcio, ex);
			throw new RegistreNotificaException(ex.getMessage(), ex);
		}

		double elapsedTime = (System.nanoTime() - startTime) / 10e6;
		log.info(" [REG] Creació assentament registral per notificació [Id: " + enviament.getId() + "]: " + elapsedTime + " ms");
		log.info(" [REG] Fi procés de registrr [Id: " + enviament.getId() + ", Estat: " + notificacio.getEstat() + "]");

		integracioHelper.addAccioOk(info);
		return success;
	}

	private boolean crearAssentamentRegistral(ReqAssentamentRegistral request){

		log.info(" [REG] >>> Nou assentament registral" + (request.isEnviamentSir() ? " SIR..." : "..."));
		RespostaConsultaRegistre arbResposta;
		var enviament = request.getEnviament();
		var notificacio = enviament.getNotificacio();
		var success = false;

		try {
			arbResposta = pluginHelper.crearAsientoRegistral(
					request.getDir3CodiRegistre(),
					enviamentRegistreMapper.toAsientoRegistral(enviament),
					request.getTipusOperacio(),
					enviament.getNotificacio().getId(),
					enviament.getId().toString(),
					isGenerarJustificantActive() || request.isEnviamentSir());
		} catch (Exception e) {
			arbResposta = new RespostaConsultaRegistre();
			arbResposta.setErrorCodi("ERROR");
			arbResposta.setErrorDescripcio(e.getMessage());
		}
		//Registrar event
		String errorDescripcio = null;
		var errorMaxReintents = false;
		if(arbResposta.getErrorCodi() != null) {
			log.info(" >>> ... ERROR: (" + arbResposta.getErrorCodi() + ") " + arbResposta.getErrorDescripcio());
			errorDescripcio = getErrorDescripcio(arbResposta.getErrorCodi(), arbResposta.getErrorDescripcio());
			errorMaxReintents = request.getNumIntent() >= pluginHelper.getRegistreReintentsMaxProperty();
		} else {
			log.info(" >>> ... OK");
			finalitzaRegistre(arbResposta, enviament, request.isEnviamentSir());
			success = true;
		}
		var data = arbResposta.getRegistreData() != null ? arbResposta.getRegistreData() : new Date();
		var eventInfo = NotificacioEventHelper.EventInfo.builder().data(data).enviament(enviament).error(!success).errorDescripcio(errorDescripcio).fiReintents(errorMaxReintents).build();

		if (request.isEnviamentSir()) {
			notificacioEventHelper.addSirEnviamentEvent(eventInfo);
		} else {
			notificacioEventHelper.addRegistreEnviamentEvent(eventInfo);
		}
		callbackHelper.crearCallback(notificacio, enviament, !success, errorDescripcio);
		enviamentTableHelper.actualitzarRegistre(enviament);
		auditHelper.auditaEnviament(enviament, AuditService.TipusOperacio.UPDATE, "RegistreSmHelper.registrarEnviament");
		return success;
	}

	private void finalitzaRegistre(RespostaConsultaRegistre arbResposta, NotificacioEnviamentEntity enviament, boolean isEnviamentSir) {

		if (arbResposta == null) {
			return;
		}
		var registreNum = arbResposta.getRegistreNumeroFormatat();
		var registreData = arbResposta.getRegistreData();
		var registreEstat = arbResposta.getEstat();
		Date sirRecepcioData = null;
		Date sirDestiData = null;
		if (isEnviamentSir) {
			enviament.setNotificaEstat(EnviamentEstat.ENVIAT_SIR);
			// És possible que el registre ja retorni estats finals al registrar SIR?
			sirRecepcioData = arbResposta.getSirRecepecioData();
			sirDestiData = arbResposta.getSirRegistreDestiData();
		}
		enviament.updateRegistreEstat(registreEstat, registreData, sirRecepcioData, sirDestiData, registreNum, arbResposta.getMotivo());
		var valors = llibreOficina.get(enviament.getNotificacio().getId());
		if (valors == null) {
			return;
		}
		enviament.getNotificacio().setRegistreLlibreNom(valors[0]);
		enviament.getNotificacio().setRegistreOficinaNom(valors[1]);
		llibreOficina.remove(enviament.getNotificacio().getId());
	}


	private String getErrorDescripcio(String codi, String descripcio) {

		var errorDescripcio = "Codi error: " + (codi != null ? codi : "Codi no proporcionat") + "\n";
		errorDescripcio += descripcio != null ? descripcio : "El registre no aporta cap descripció de l'error";
		return errorDescripcio;
	}

	private boolean isSirActivat() {
		return configHelper.getConfigAsBoolean("es.caib.notib.emprar.sir");
	}

	private boolean isGenerarJustificantActive() {
		return configHelper.getConfigAsBoolean("es.caib.notib.plugin.registre.generar.justificant");
	}


	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ReqAssentamentRegistral {
		private NotificacioEnviamentEntity enviament;
		private String dir3CodiRegistre;
		private Integer numIntent;
		private boolean enviamentSir;
		private Long tipusOperacio;
	}
}
