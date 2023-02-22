package es.caib.notib.core.helper;

import es.caib.notib.core.api.dto.TipusUsuariEnumDto;
import es.caib.notib.core.api.dto.notificacio.NotTableUpdate;
import es.caib.notib.core.api.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.core.api.service.AuditService.TipusEntitat;
import es.caib.notib.core.api.service.AuditService.TipusOperacio;
import es.caib.notib.core.aspect.Audita;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.repository.NotificacioRepository;
import es.caib.notib.plugin.registre.RespostaConsultaRegistre;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;


/**
 * Conté totes les funcions per a crear, editar o eliminar noves notificacions.
 * Totes les funcions que s'afageixin al fitxer han de començar amb les paraules
 * desa o update
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class AuditNotificacioHelper {

	@Autowired
	private NotificacioRepository notificacioRepository;
	@Resource
	private PluginHelper pluginHelper;
	@Autowired
	private NotificacioEventHelper notificacioEventHelper;
	@Autowired
	private NotificacioTableHelper notificacioTableHelper;
	@Autowired
	private EnviamentTableHelper enviamentTableHelper;

	@Audita(entityType = TipusEntitat.NOTIFICACIO, operationType = TipusOperacio.CREATE)
	public NotificacioEntity desaNotificacio(NotificacioEntity notificacioEntity) {
		notificacioEntity = notificacioRepository.saveAndFlush(notificacioEntity);
		return notificacioEntity;
	}

	@Audita(entityType = TipusEntitat.NOTIFICACIO, operationType = TipusOperacio.UPDATE)
	public NotificacioEntity updateNotificacio(NotificacioEntity notificacio, NotificacioHelper.NotificacioData data) {

		notificacio.update(
				data.getEntitat(),
				data.getNotificacio().getEmisorDir3Codi(),
				data.getOrganGestor(),
				pluginHelper.getNotibTipusComunicacioDefecte(),
				data.getNotificacio().getEnviamentTipus(),
				data.getNotificacio().getConcepte(),
				data.getNotificacio().getDescripcio(),
				data.getNotificacio().getEnviamentDataProgramada(),
				data.getNotificacio().getRetard(),
				data.getNotificacio().getCaducitat(),
				data.getNotificacio().getUsuariCodi(),
				data.getProcSer() != null ? data.getProcSer().getCodi() : null,
				data.getProcSer(),
				data.getGrupNotificacio() != null ? data.getGrupNotificacio().getCodi() : null,
				data.getNotificacio().getNumExpedient(),
				TipusUsuariEnumDto.INTERFICIE_WEB,
				data.getDocumentEntity(),
				data.getDocument2Entity(),
				data.getDocument3Entity(),
				data.getDocument4Entity(),
				data.getDocument5Entity(),
				data.getProcedimentOrgan(),
				data.getNotificacio().getIdioma());
		notificacioTableHelper.actualitzarRegistre(notificacio);
		return notificacio;
	}

	@Audita(entityType = TipusEntitat.NOTIFICACIO, operationType = TipusOperacio.UPDATE)
	public NotificacioEntity updateNotificacioProcessada(NotificacioEntity notificacio, String motiu) {

		notificacio.updateEstat(NotificacioEstatEnumDto.PROCESSADA);
		notificacio.updateEstatProcessatDate(new Date());
		notificacio.updateMotiu(motiu);
		NotTableUpdate not = NotTableUpdate.builder().id(notificacio.getId()).estat(NotificacioEstatEnumDto.PROCESSADA).estatProcessatDate(new Date()).build();
		notificacioTableHelper.actualitzar(not);
		return notificacio;
	}


	@Audita(entityType = TipusEntitat.NOTIFICACIO, operationType = TipusOperacio.UPDATE)
	public NotificacioEntity updateEstatAFinalitzada(String notificaEstatNom, NotificacioEntity notificacio) {

		notificacio.updateEstat(NotificacioEstatEnumDto.FINALITZADA);
		notificacio.updateMotiu(notificaEstatNom);
		notificacio.updateEstatDate(new Date());
		notificacioEventHelper.clearOldUselessEvents(notificacio);
		NotTableUpdate not = NotTableUpdate.builder().id(notificacio.getId()).estat(NotificacioEstatEnumDto.FINALITZADA).estatDate(new Date()).build();
		notificacioTableHelper.actualitzar(not);
		return notificacio;
	}

	@Audita(entityType = TipusEntitat.NOTIFICACIO, operationType = TipusOperacio.UPDATE)
	public NotificacioEntity updateEstatAFinalitzadaAmbError(String notificaEstatNom, NotificacioEntity notificacio) {

		notificacio.updateEstat(NotificacioEstatEnumDto.FINALITZADA_AMB_ERRORS);
		notificacio.updateMotiu(notificaEstatNom);
		notificacio.updateEstatDate(new Date());
		notificacioEventHelper.clearOldUselessEvents(notificacio);
		NotTableUpdate not = NotTableUpdate.builder().id(notificacio.getId()).estat(NotificacioEstatEnumDto.FINALITZADA_AMB_ERRORS).estatDate(new Date()).build();
		notificacioTableHelper.actualitzar(not);
		return notificacio;
	}

	@Audita(entityType = TipusEntitat.NOTIFICACIO, operationType = TipusOperacio.UPDATE)
	public NotificacioEntity updateLastCallbackError(NotificacioEntity notificacio, boolean error) {

		notificacio.updateLastCallbackError(error);
		notificacioTableHelper.actualitzarRegistre(notificacio);
		return notificacio;
	}
	
	@Audita(entityType = TipusEntitat.NOTIFICACIO, operationType = TipusOperacio.UPDATE)
	public NotificacioEntity updateNotificacioRegistre(RespostaConsultaRegistre arbResposta, NotificacioEntity notificacio) {

		notificacio.updateRegistreNumero(Integer.parseInt(arbResposta.getRegistreNumero()));
		notificacio.updateRegistreNumeroFormatat(arbResposta.getRegistreNumeroFormatat());
		notificacio.updateRegistreData(arbResposta.getRegistreData());
		notificacio.updateEstat(NotificacioEstatEnumDto.REGISTRADA);
		NotTableUpdate not = NotTableUpdate.builder().id(notificacio.getId()).estat(NotificacioEstatEnumDto.REGISTRADA).build();
		notificacioTableHelper.actualitzar(not);
		return notificacio;
	}

	@Audita(entityType = TipusEntitat.NOTIFICACIO, operationType = TipusOperacio.UPDATE)
	public NotificacioEntity updateRegistreNouEnviament(NotificacioEntity notificacio, int reintentsPeriode) {

		notificacio.updateRegistreNouEnviament(reintentsPeriode);
		for (NotificacioEnviamentEntity env: notificacio.getEnviaments()) {
			enviamentTableHelper.actualitzarRegistre(env);
		}
		NotTableUpdate not = NotTableUpdate.builder()
				.id(notificacio.getId())
				.estat(notificacio.getEstat())
				.reintentsRegistre(notificacio.getRegistreEnviamentIntent()).build();
		notificacioTableHelper.actualitzar(not);
		return notificacio;
	}

	@Audita(entityType = TipusEntitat.NOTIFICACIO, operationType = TipusOperacio.UPDATE)
	public NotificacioEntity updateNotificacioMixtaEnviadaNotifica(NotificacioEntity notificacio) {
		notificacioEventHelper.clearOldNotificaUselessEvents(notificacio);
		notificacioTableHelper.actualitzarRegistre(notificacio);
		return notificacio;
	}

	@Audita(entityType = TipusEntitat.NOTIFICACIO, operationType = TipusOperacio.UPDATE)
	public NotificacioEntity updateNotificacioEnviada(NotificacioEntity notificacio) {

		notificacio.updateEstat(NotificacioEstatEnumDto.ENVIADA);
		notificacioEventHelper.clearOldUselessEvents(notificacio);
		NotTableUpdate not = NotTableUpdate.builder().id(notificacio.getId()).estat(NotificacioEstatEnumDto.ENVIADA).build();
		notificacioTableHelper.actualitzar(not);
		return notificacio;
	}

	@Audita(entityType = TipusEntitat.NOTIFICACIO, operationType = TipusOperacio.UPDATE)
	public NotificacioEntity updateNotificacioEnviadaEmail(NotificacioEntity notificacio) {

		notificacio.updateEstat(NotificacioEstatEnumDto.FINALITZADA);
		notificacioEventHelper.clearOldUselessEvents(notificacio);
		NotTableUpdate not = NotTableUpdate.builder().id(notificacio.getId()).estat(NotificacioEstatEnumDto.FINALITZADA).build();
		notificacioTableHelper.actualitzar(not);
		return notificacio;
	}

	@Audita(entityType = TipusEntitat.NOTIFICACIO, operationType = TipusOperacio.UPDATE)
	public NotificacioEntity updateNotificacioEnviadaAmbErrors(NotificacioEntity notificacio) {

		notificacio.updateEstat(NotificacioEstatEnumDto.ENVIADA_AMB_ERRORS);
		NotTableUpdate not = NotTableUpdate.builder().id(notificacio.getId()).estat(NotificacioEstatEnumDto.ENVIADA_AMB_ERRORS).build();
		notificacioTableHelper.actualitzar(not);
		return notificacio;
	}

	@Audita(entityType = TipusEntitat.NOTIFICACIO, operationType = TipusOperacio.UPDATE)
	public NotificacioEntity updateNotificacioFinalitzadaAmbErrors(NotificacioEntity notificacio) {

		notificacio.updateEstat(NotificacioEstatEnumDto.FINALITZADA_AMB_ERRORS);
		NotTableUpdate not = NotTableUpdate.builder().id(notificacio.getId()).estat(NotificacioEstatEnumDto.FINALITZADA_AMB_ERRORS).build();
		notificacioTableHelper.actualitzar(not);
		return notificacio;
	}

	@Audita(entityType = TipusEntitat.NOTIFICACIO, operationType = TipusOperacio.UPDATE)
	public NotificacioEntity updateNotificacioReintentaFinalitzadaAmbErrors(NotificacioEntity notificacio) {

		notificacio.updateEstat(NotificacioEstatEnumDto.ENVIADA_AMB_ERRORS);
		notificacio.resetIntentsNotificacio();
		NotTableUpdate not = NotTableUpdate.builder().estat(NotificacioEstatEnumDto.ENVIADA_AMB_ERRORS).build();
		notificacioTableHelper.actualitzar(not);
		return notificacio;
	}

	@Audita(entityType = TipusEntitat.NOTIFICACIO, operationType = TipusOperacio.UPDATE)
	public NotificacioEntity updateNotificacioRefreshRegistreNotificacio(NotificacioEntity notificacio) {

		notificacio.refreshRegistre();
		notificacioRepository.saveAndFlush(notificacio);
		notificacioTableHelper.actualitzarRegistre(notificacio);
		return notificacio;
	}

	@Audita(entityType = TipusEntitat.NOTIFICACIO, operationType = TipusOperacio.DELETE)
	public NotificacioEntity deleteNotificacio(NotificacioEntity notificacio) {
		notificacioRepository.delete(notificacio);
		notificacioRepository.flush();
		return notificacio;
	}

}
