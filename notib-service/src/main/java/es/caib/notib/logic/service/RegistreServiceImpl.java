package es.caib.notib.logic.service;

import es.caib.notib.logic.helper.IntegracioHelper;
import es.caib.notib.logic.helper.RegistreHelper;
import es.caib.notib.logic.helper.SubsistemesHelper;
import es.caib.notib.logic.intf.dto.AccioParam;
import es.caib.notib.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioCodi;
import es.caib.notib.logic.intf.dto.IntegracioInfo;
import es.caib.notib.logic.intf.dto.adviser.sir.RespostaSirAdviser;
import es.caib.notib.logic.intf.dto.adviser.sir.SirAdviser;
import es.caib.notib.logic.intf.service.RegistreService;
import es.caib.notib.logic.intf.statemachine.events.ConsultaSirRequest;
import es.caib.notib.persist.repository.EntitatRepository;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import joptsimple.internal.Strings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.StringJoiner;

import static es.caib.notib.logic.helper.SubsistemesHelper.SubsistemesEnum.CSR;

@Slf4j
@RequiredArgsConstructor
@Service
public class RegistreServiceImpl implements RegistreService {

    private final NotificacioEnviamentRepository notificacioEnviamentRepository;
    private final RegistreHelper registreHelper;
    private final IntegracioHelper integracioHelper;
    private final EntitatRepository entitatRepository;

    @Transactional
    @Override
    public RespostaSirAdviser sincronitzarEnviamentSir(SirAdviser adviser) {

        long start = System.currentTimeMillis();
        var info = new IntegracioInfo(IntegracioCodi.REGISTRE, "Recepció de canvi d'estat via Adviser", IntegracioAccioTipusEnumDto.RECEPCIO,
                new AccioParam("Num.Registre", adviser.getRegistreNumero()),
                new AccioParam("Entitat DIR3", adviser.getEntitatDir3Codi()));
        var entitat = entitatRepository.findByDir3Codi(adviser.getEntitatDir3Codi());
        if (entitat == null) {
            var error = "No existeix l'entitat " + adviser.getEntitatDir3Codi();
            info.setCodiEntitat(adviser.getEntitatDir3Codi());
            integracioHelper.addAccioError(info, error);
            return RespostaSirAdviser.builder().ok(false).errorDescripcio(error).build();
        }
        info.setCodiEntitat(entitat.getCodi());
        try {
            var errorsValidacio = validarAdviserSir(adviser);
            if (!Strings.isNullOrEmpty(errorsValidacio)) {
                return RespostaSirAdviser.builder().ok(false).errorDescripcio(errorsValidacio).build();
            }
            var enviament = notificacioEnviamentRepository.findByEntitatDir3CodiAndRegistreNumeroFormatat(adviser.getEntitatDir3Codi(), adviser.getRegistreNumero()).orElse(null);
            if (enviament == null) {
                var desc = "No existeix l'enviament amb el número de registre " + adviser.getRegistreNumero() + " per la entitat amb codi DIR3 " + adviser.getEntitatDir3Codi();
                integracioHelper.addAccioError(info, desc);
                return RespostaSirAdviser.builder().ok(false).errorDescripcio(desc).build();
            }
			var consulta = ConsultaSirRequest.builder().id(enviament.getId()).build();
            registreHelper.enviamentRefrescarEstatRegistre(consulta);
            integracioHelper.addAccioOk(info);
            SubsistemesHelper.addSuccessOperation(CSR, System.currentTimeMillis() - start);
            return RespostaSirAdviser.builder().ok(true).build();
        } catch (Exception ex) {
            var error = "[SIR ADVISER] Error sincronitzant l'enviament SIR ";
            log.error(error, ex);
            integracioHelper.addAccioError(info, error + ex.getMessage());
            SubsistemesHelper.addErrorOperation(CSR);
            return RespostaSirAdviser.builder().ok(false).errorDescripcio("Error inesperat al sincronitzar l'enviament SIR " + ex.getMessage()).build();
        }
    }

	private String validarAdviserSir(SirAdviser adviser) {

		var error = new StringJoiner(". ");
		if (Strings.isNullOrEmpty(adviser.getRegistreNumero())) {
			error.add("El número de registre no pot ser null");
		}
		if (Strings.isNullOrEmpty(adviser.getEntitatDir3Codi())) {
			error.add("El codi DIR3 de la entitat no pot ser null");
		}
		return error.toString();
	}

}
