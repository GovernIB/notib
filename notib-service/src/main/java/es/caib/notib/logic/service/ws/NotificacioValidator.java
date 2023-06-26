package es.caib.notib.logic.service.ws;

import com.google.common.base.Strings;
import es.caib.notib.client.domini.DocumentV2;
import es.caib.notib.client.domini.EntregaPostal;
import es.caib.notib.client.domini.Enviament;
import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.client.domini.NotificacioV2;
import es.caib.notib.client.domini.Persona;
import es.caib.notib.logic.cacheable.OrganGestorCachable;
import es.caib.notib.logic.helper.CacheHelper;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.MessageHelper;
import es.caib.notib.logic.intf.dto.DocumentValidDto;
import es.caib.notib.logic.intf.dto.GrupDto;
import es.caib.notib.logic.intf.dto.ProcSerTipusEnum;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.service.GrupService;
import es.caib.notib.logic.intf.util.NifHelper;
import es.caib.notib.logic.utils.MimeUtils;
import es.caib.notib.persist.entity.AplicacioEntity;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.OrganGestorEntity;
import es.caib.notib.persist.entity.ProcSerEntity;
import es.caib.notib.persist.repository.AplicacioRepository;
import es.caib.notib.plugin.usuari.DadesUsuari;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static es.caib.notib.client.domini.InteressatTipus.*;
import static es.caib.notib.client.domini.NotificaDomiciliConcretTipus.*;
import static es.caib.notib.logic.intf.util.ValidacioErrorCodes.*;

@Component
@RequiredArgsConstructor
public class NotificacioValidator implements Validator {

    public static final Pattern EMAIL_REGEX = Pattern.compile("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$", Pattern.CASE_INSENSITIVE);

    private final AplicacioRepository aplicacioRepository;
    private final GrupService grupService;
    private final MessageHelper messageHelper;
    private final CacheHelper cacheHelper;
    private final OrganGestorCachable organGestorCachable;
    private final ConfigHelper configHelper;


    @Override
    public boolean supports(Class<?> clazz) {
        return NotificacioV2.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        NotificacioV2 notificacio = (NotificacioV2) target;

    }

    public void validate(NotificacioV2 notificacio,
                         EntitatEntity entitat,
                         ProcSerEntity procediment,
                         OrganGestorEntity organGestor,
                         DocumentValidDto[] documents,
                         Errors errors,
                         Locale locale) {

        Locale l = new Locale("rest");
        EnviamentTipus enviamentTipus = notificacio.getEnviamentTipus();
        boolean entregaPostalActiva = entitat != null && entitat.getEntregaCie() != null 
                || organGestor != null && organGestor.getEntregaCie() != null 
                || procediment != null && procediment.getEntregaCie() != null;
        boolean entregaDehActiva = entitat != null && entitat.isAmbEntregaDeh();

        // Entitat (Emisor)
        validateEntitat(notificacio, entitat, errors, locale);
        if (errors.hasErrors())
            return;
        // Aplicació
        validateAplicacio(notificacio, entitat, errors, locale);
        // Procediment i grup
        validateProcediment(notificacio, entitat, procediment, enviamentTipus, errors, locale);
        // Òrgan
        validateOrgan(notificacio, entitat, organGestor, enviamentTipus, errors, locale);
        // Dades bàsiques de la notificació
        validateDadesBasiquesNotificacio(notificacio, errors, locale);
        // Usuari
        validateUsuari(notificacio, errors, locale);
        // Documents
        validateDocuments(notificacio, documents, enviamentTipus, errors, locale);
        // Enviaments
        validateEnviaments(notificacio, enviamentTipus, entregaPostalActiva, entregaDehActiva, errors, locale);

    }

    private void validateEntitat(NotificacioV2 notificacio, EntitatEntity entitat, Errors errors, Locale l) {
        String emisorDir3Codi = notificacio.getEmisorDir3Codi();
        // Emisor
        if (Strings.isNullOrEmpty(emisorDir3Codi)) {
            errors.rejectValue("emisorDir3Codi", error(EMISOR_DIR3_NULL, l));
        } else if (emisorDir3Codi.length() > 9) {
            errors.rejectValue("emisorDir3Codi", error(EMISOR_DIR3_SIZE, l));
        }
        // Entitat
        if (!Strings.isNullOrEmpty(emisorDir3Codi) && entitat == null) {
            errors.rejectValue("emisorDir3Codi", error(EMISOR_DIR3_NO_EXIST, l, emisorDir3Codi));
        } else if (entitat != null && !entitat.isActiva()) {
            errors.rejectValue("emisorDir3Codi", error(ENTITAT_INACTIVA, l));
        }
    }

    private void validateAplicacio(NotificacioV2 notificacio, EntitatEntity entitat, Errors errors, Locale l) {
        String usuariCodi = SecurityContextHolder.getContext().getAuthentication().getName();
        AplicacioEntity aplicacio = null;
        if (entitat != null && usuariCodi != null) {
            aplicacio = aplicacioRepository.findByEntitatIdAndUsuariCodi(entitat.getId(), usuariCodi);
        }
        if (aplicacio == null) {
            errors.reject(error(APLICACIO_NO_EXIST, l, notificacio.getEmisorDir3Codi()));
        }
    }

    private void validateProcediment(NotificacioV2 notificacio, EntitatEntity entitat, ProcSerEntity procediment, EnviamentTipus enviamentTipus, Errors errors, Locale l) {
        // Procediment
        String procedimentCodi = notificacio.getProcedimentCodi();
        if (EnviamentTipus.NOTIFICACIO.equals(enviamentTipus)) {
            if (Strings.isNullOrEmpty(procedimentCodi)) {
                errors.rejectValue("procedimentCodi", error(PROCSER_NULL, l));
            } else if (procediment == null) {
                errors.rejectValue("procedimentCodi", error(PROCSER_NO_EXIST, l));
            }
        }
        if (!Strings.isNullOrEmpty(procedimentCodi) && procedimentCodi.length() > 9) {
            errors.rejectValue("procedimentCodi", error(PROCSER_SIZE, l));
        }
        if (procediment != null) {
            if(!procediment.isActiu()) {
                errors.rejectValue("procedimentCodi", error(PROCSER_INACTIU, l));
            }
            if (ProcSerTipusEnum.SERVEI.equals(procediment.getTipus()) && EnviamentTipus.NOTIFICACIO.equals(enviamentTipus)) {
                errors.reject(error(SERVEI_EN_NOTIFICACIO, l));
            }
            if (!procediment.isEntregaCieActivaAlgunNivell()) {
                int i = 0;
                for (Enviament enviament : notificacio.getEnviaments()) {
                    if (enviament.isEntregaPostalActiva()) {
                        errors.rejectValue("enviaments[" + i + "].entregaPostalActiva", error(POSTAL_ENTREGA_INACTIVA, l, new Object[]{"Enviament " + (i + 1) + " - "}));
                        break;
                    }
                    i++;
                }
            }
        }

        // Grup
        if (!Strings.isNullOrEmpty(notificacio.getGrupCodi())) {
            if (notificacio.getGrupCodi().length() > 64) {
                errors.rejectValue("grupCodi", error(GRUPCODI_SIZE, l));
            }
            if (procediment != null && procediment.isAgrupar()) {
                GrupDto grupNotificacio = grupService.findByCodi(notificacio.getGrupCodi(), entitat.getId());
                if (grupNotificacio == null) {
                    errors.rejectValue("grupCodi", error(GRUP_INEXISTENT, l, new Object[]{notificacio.getGrupCodi()}));
                } else {
                    List<GrupDto> grupsProcediment = grupService.findByProcedimentAndUsuariGrups(procediment.getId());
                    if (grupsProcediment == null || grupsProcediment.isEmpty()) {
                        errors.rejectValue("grupCodi", error(GRUP_EN_PROCEDIMENT_NO_AGRUPADA, l));
                    } else {
                        if(!grupsProcediment.contains(grupNotificacio)) {
                            errors.rejectValue("grupCodi", error(GRUP_NO_ASSIGNAT, l, new Object[]{notificacio.getGrupCodi()}));
                        }
                    }
                }
            }
        }
    }

    private void validateOrgan(NotificacioV2 notificacio, EntitatEntity entitat, OrganGestorEntity organGestor, EnviamentTipus enviamentTipus, Errors errors, Locale l) {
        if (!EnviamentTipus.NOTIFICACIO.equals(enviamentTipus)) {
            if (Strings.isNullOrEmpty(notificacio.getProcedimentCodi()) && Strings.isNullOrEmpty(notificacio.getOrganGestor())){
                errors.rejectValue("organGestor", error(ORGAN_NULL, l));
            }
        }
        if (!Strings.isNullOrEmpty(notificacio.getOrganGestor()) && notificacio.getOrganGestor().length() > 64) {
            errors.rejectValue("organGestor", error(ORGAN_SIZE, l));
        }
        if (!Strings.isNullOrEmpty(notificacio.getOrganGestor()) && organGestor != null && !notificacio.getOrganGestor().equals(organGestor.getCodi())) {
            errors.rejectValue("organGestor", error(ORGAN_DIFF_AL_DEL_PROCEDIMENT, l));
        }
        if (organGestor == null) {
            errors.rejectValue("organGestor", error(ORGAN_ALTRE_ENTITAT, l));
        } else {
            if (EnviamentTipus.SIR.equals(enviamentTipus) && entitat != null && !entitat.isOficinaEntitat() && Strings.isNullOrEmpty(organGestor.getOficina())) {
                errors.rejectValue("organGestor", error(ORGAN_I_ENTITA_SENSE_OFICINA_EN_SIR, l));
            }
        }
    }

    private void validateDadesBasiquesNotificacio(NotificacioV2 notificacio, Errors errors, Locale l) {
        // concepte
        validateConcepte(notificacio, errors, l);
        // descripcio
        validateDescripcio(notificacio, errors, l);
        // tipus enviament
        validateTipusEnviament(notificacio, errors, l);
        // data programada i caducitat
        validateDates(notificacio, errors, l);
        // número d'expedient
        validateExpedient(notificacio, errors, l);
    }

    private void validateConcepte(NotificacioV2 notificacio, Errors errors, Locale l) {
        // Concepte
        if (Strings.isNullOrEmpty(notificacio.getConcepte())) {
            errors.rejectValue("concepte", error(CONCEPTE_NULL, l));
        } else {
            if (notificacio.getConcepte().length() > 240) {
                errors.rejectValue("concepte", error(CONCEPTE_SIZE, l));
            }
            var caractersNoValids = validFormat(notificacio.getConcepte());
            if (!caractersNoValids.isEmpty()) {
                String invalidChars = caractersNoValids.stream().map(String::valueOf).collect(Collectors.joining(", "));
                errors.rejectValue("concepte", error(CONCEPTE_INVALID_CHARS, l, new Object[] {invalidChars}));
            }
        }
    }

    private void validateDescripcio(NotificacioV2 notificacio, Errors errors, Locale l) {
        // Descripcio
        if (!Strings.isNullOrEmpty(notificacio.getDescripcio())) {
            if (notificacio.getDescripcio().length() > 1000) {
                errors.rejectValue("descripcio", error(DESCRIPCIO_SIZE, l));
            }
            var caractersNoValids = validFormat(notificacio.getDescripcio());
            if (!caractersNoValids.isEmpty()) {
                String invalidChars = caractersNoValids.stream().map(String::valueOf).collect(Collectors.joining(", "));
                errors.rejectValue("descripcio", error(DESCRIPCIO_INVALID_CHARS, l, new Object[] {invalidChars}));
            }
            if (hasSaltLinia(notificacio.getDescripcio())) {
                errors.rejectValue("descripcio", error(DESCRIPCIO_SALTS_LINIA, l));
            }
        }
    }

    private void validateTipusEnviament(NotificacioV2 notificacio, Errors errors, Locale l) {
        // Tipus d'enviament
        if (notificacio.getEnviamentTipus() == null) {
            errors.rejectValue("enviamentTipus", error(TIPUS_ENVIAMENT_NULL, l));
        }
    }

    private void validateDates(NotificacioV2 notificacio, Errors errors, Locale l) {
        Date now = new Date();
        Date dataProg = notificacio.getEnviamentDataProgramada();
        Date dataCaducitat = notificacio.getCaducitat();
        if (dataProg != null && dataProg.before(now) && !DateUtils.isSameDay(dataProg, now)) {
            errors.rejectValue("enviamentDataProgramada", error(DATA_PROGRAMADA_ANTERIOR_A_AVUI, l));
        }
        if (dataCaducitat != null && dataCaducitat.before(now) && !DateUtils.isSameDay(dataCaducitat, now)) {
            errors.rejectValue("caducitat", error(DATA_CADUCITAT_ANTERIOR_A_AVUI, l));
        }
        if (dataProg != null && dataCaducitat != null && dataCaducitat.before(dataProg)) {
            errors.rejectValue("caducitat", error(DATA_CADUCITAT_ANTERIOR_A_LA_PROGRAMADA, l));
        }
    }

    private void validateExpedient(NotificacioV2 notificacio, Errors errors, Locale l) {
        if (!Strings.isNullOrEmpty(notificacio.getNumExpedient()) && notificacio.getNumExpedient().length() > 80) {
            errors.rejectValue("numExpedient", error(NUM_EXPEDIENT_SIZE, l));
        }
    }

    private void validateUsuari(NotificacioV2 notificacio, Errors errors, Locale l) {
        if (Strings.isNullOrEmpty(notificacio.getUsuariCodi())) {
            errors.rejectValue("usuariCodi", error(USUARI_CODI_NULL, l));
        } else {
            if (notificacio.getUsuariCodi().length() > 64) {
                errors.rejectValue("usuariCodi", error(USUARI_CODI_SIZE, l));
            }
            DadesUsuari dades = cacheHelper.findUsuariAmbCodi(notificacio.getUsuariCodi());
            if (dades == null || Strings.isNullOrEmpty(dades.getCodi())) {
                errors.rejectValue("usuariCodi", error(USUARI_INEXISTENT, l));
            }
        }
    }

    private void validateDocuments(NotificacioV2 notificacio, DocumentValidDto[] documents, EnviamentTipus enviamentTipus, Errors errors, Locale l) {
        if (notificacio.getDocument() == null) {
            errors.reject(error(DOCUMENT_NULL, l));
            return;
        }
        DocumentV2 document = notificacio.getDocument();
        validateDocument(document, documents[0], enviamentTipus, 1, errors, l);

        if (EnviamentTipus.SIR.equals(enviamentTipus)) {
            if (notificacio.getDocument2() != null && !notificacio.getDocument2().isEmpty()) {
                validateDocument(notificacio.getDocument2(), documents[1], enviamentTipus, 2, errors, l);
            }
            if (notificacio.getDocument3() != null && !notificacio.getDocument3().isEmpty()) {
                validateDocument(notificacio.getDocument3(), documents[2], enviamentTipus, 3, errors, l);
            }
            if (notificacio.getDocument4() != null && !notificacio.getDocument4().isEmpty()) {
                validateDocument(notificacio.getDocument4(), documents[3], enviamentTipus, 4, errors, l);
            }
            if (notificacio.getDocument5() != null && !notificacio.getDocument5().isEmpty()) {
                validateDocument(notificacio.getDocument5(), documents[4], enviamentTipus, 5, errors, l);
            }

            // Midal màxima
            Long totalFileSize = Arrays.asList(documents).stream().filter(d -> d != null && d.getMida() != null).map(d -> d.getMida()).reduce(0L, (d1, d2) -> d1 + d2);
            Long maxTotalFileSize = getMaxTotalSizeFile();
            if (totalFileSize > maxTotalFileSize) {
                errors.rejectValue("document", error(DOCUMENTS_SIR_MASSA_GRANS, l, new Object[]{maxTotalFileSize / 1048576}));
            }
        } else {
            if (notificacio.getDocument2() != null || notificacio.getDocument3() != null || notificacio.getDocument4() != null || notificacio.getDocument5() != null) {
                errors.reject(error(MULTIPLES_DOCUMENTS_EN_NOTCOM, l));
            }

            // Midal màxima
            if(documents[0] != null && documents[0].getMida() != null) {
                Long maxFileSize = getMaxSizeFile();
                if(documents[0].getMida() > maxFileSize) {
                    errors.rejectValue("document", error(DOCUMENT_MASSA_GRAN, l, new Object[]{maxFileSize / 1048576}));
                }
            }
        }
    }

    private void validateDocument(DocumentV2 document, DocumentValidDto dto, EnviamentTipus enviamentTipus, int numDocument, Errors errors, Locale l) {
        String doc = "document" + (numDocument > 1 ? numDocument : "");
        String prefix = "Document " + numDocument + " - ";

        //TODO: Revisar la validación del nom. Para CSV/UUid en el formulario web NO se pide un nombre; se recupera posteriormente del plugin.
        if (Strings.isNullOrEmpty(document.getArxiuNom())) {
            errors.rejectValue(doc + ".arxiuNom", error(DOCUMENT_NOM_NULL, l, new Object[]{prefix}));
        } else if (document.getArxiuNom().length() > 200) {
            errors.rejectValue(doc + ".arxiuNom", error(DOCUMENT_NOM_SIZE, l, new Object[]{prefix}));
        }
        if (Strings.isNullOrEmpty(document.getContingutBase64()) &&
                Strings.isNullOrEmpty(document.getCsv()) &&
                Strings.isNullOrEmpty(document.getUuid())) {
            errors.rejectValue(doc + ".arxiuNom", error(DOCUMENT_SOURCE_NULL, l, new Object[]{prefix}));
        }
        // Limitar que només s'empleni un dels camps
        if (document.hasMultipleSources()) {
            errors.rejectValue(doc + ".arxiuNom", error(DOCUMENT_SOURCE_MULTIPLE, l, new Object[]{prefix}));
        }
        // Document
        if (dto != null) {
            // Format (Mime)
            if (!Strings.isNullOrEmpty(dto.getMediaType()) && !documentMimeValid(enviamentTipus, dto.getMediaType())) {
                if (EnviamentTipus.SIR.equals(enviamentTipus)) {
                    errors.rejectValue(doc + ".arxiuNom", error(DOCUMENT_FORMAT_SIR_INVALID, l, new Object[]{prefix}));
                } else {
                    errors.rejectValue(doc + ".arxiuNom", error(DOCUMENT_FORMAT_INVALID, l, new Object[]{prefix}));
                }
            }
            if (dto.isErrorFitxer()) {
                errors.rejectValue(doc + ".arxiuNom", error(DOCUMENT_ERROR_OBTENINT, l, new Object[]{prefix}));
            }
            if (dto.isErrorMetadades()) {
                errors.rejectValue(doc + ".arxiuNom", error(DOCUMENT_ERROR_OBTENINT_METADADES, l, new Object[]{prefix}));
            }
            if (dto.isErrorFirma()) {
                errors.rejectValue(doc + ".arxiuNom", error(DOCUMENT_ERROR_VALIDANT_FIRMA, l, new Object[]{prefix}));
            }
            // Metadades
            if (!Strings.isNullOrEmpty(document.getContingutBase64())) {
                if (dto.getOrigen() == null) {
                    errors.rejectValue(doc + ".origen", error(DOCUMENT_METADADES_ORIGEN_NULL, l, new Object[]{prefix}));
                }
                if (dto.getValidesa() == null) {
                    errors.rejectValue(doc + ".validesa", error(DOCUMENT_METADADES_VALIDESA_NULL, l, new Object[]{prefix}));
                }
                if (dto.getTipoDocumental() == null) {
                    errors.rejectValue(doc + ".tipoDocumental", error(DOCUMENT_METADADES_TIPUS_DOCUMENTAL_NULL, l, new Object[]{prefix}));
                }
                if (dto.getModoFirma() == null) {
                    errors.rejectValue(doc + ".modoFirma", error(DOCUMENT_METADADES_MODE_FIRMA_NULL, l, new Object[]{prefix}));
                }
            }
        }
    }

    private void validateEnviaments(NotificacioV2 notificacio, EnviamentTipus enviamentTipus, boolean entregaPostalActiva, boolean entregaDehActiva, Errors errors, Locale l) {
        List<Enviament> enviaments = notificacio.getEnviaments();
        String emisor = notificacio.getEmisorDir3Codi();

        if (enviaments == null || enviaments.isEmpty()) {
            errors.reject(error(ENVIAMENTS_NULL, l));
            return;
        }
        IntStream.range(0, enviaments.size())
                .forEach(i -> validateEnviament(enviaments.get(i), enviamentTipus, emisor, entregaPostalActiva, entregaDehActiva, i, errors, l));

        // Nifs repetis
        // TODO: Eliminar la condició que no permet NIFs repetits?
        if (!EnviamentTipus.SIR.equals(enviamentTipus)) {
            List<String> nifs = notificacio.getNifsEnviaments();
            if (!nifs.stream().allMatch(new HashSet<>()::add)) {
                errors.reject(error(ENVIAMENT_NIFS_REPETITS, l));
            }
        }

        // Comunicacions SIR que no van adreçades a administracions
        if (EnviamentTipus.SIR.equals(enviamentTipus)) {
            for(var enviament: enviaments) {
                if (enviament.getTitular() != null && !ADMINISTRACIO.equals(enviament.getTitular().getInteressatTipus())) {
                    errors.reject(error(ENVIAMENT_SIR_TITULAR_NO_ADMINISTRACIO, l));
                    break;
                }
            }
        }
    }

    private void validateEnviament(Enviament enviament, EnviamentTipus enviamentTipus, String emisorDir3Codi, boolean entregaPostalActiva, boolean entregaDehActiva, int numEnviament, Errors errors, Locale l) {
        String envName = "enviaments[" + numEnviament + "]";
        String prefix = "Enviament " + (numEnviament + 1) + " - ";
        // Dades bàsiques
        validateDadesEnviament(enviament, envName, prefix, errors, l);
        // Titular
        validateTitular(enviament, enviamentTipus, emisorDir3Codi, envName, prefix, errors, l);
        // Destinatari
        validateDestinataris(enviament, enviamentTipus, envName, prefix, errors, l);
        // Interessats sense Nifs
        if (enviament != null && enviament.getTitular() != null && !FISICA_SENSE_NIF.equals(enviament.getTitular().getInteressatTipus())) {
            boolean senseNif = Strings.isNullOrEmpty(enviament.getTitular().getNif());
            if (senseNif && enviament.getDestinataris() != null) {
                for (var destinatari : enviament.getDestinataris()) {
                    senseNif = senseNif && Strings.isNullOrEmpty(destinatari.getNif());
                }
            }
            if (senseNif) {
                errors.rejectValue(envName, error(ENVIAMENT_MINIM_UN_NIF, l, new Object[]{prefix}));
            }
        }
        // Entrega postal
        if (enviament.isEntregaPostalActiva()) {
            if (!entregaPostalActiva) {
                errors.rejectValue(envName, error(ENVIAMENT_POSTAL_INACTIU, l, new Object[]{prefix}));
            } else {
                validateEntregaPostal(enviament.getEntregaPostal(), envName, prefix, errors, l);
            }
        }
        // Entrega DEH
        if (enviament.isEntregaDehActiva()) {
            if (!entregaDehActiva) {
                errors.rejectValue(envName, error(ENVIAMENT_DEH_INACTIU, l, new Object[]{prefix}));
            } else {
                validateEntregaDeh(enviament, envName, prefix, errors, l);
            }
        }

    }

    private void validateDadesEnviament(Enviament enviament, String envName, String prefix, Errors errors, Locale l) {
        // Servei tipus
        if(enviament.getServeiTipus() == null) {
            errors.rejectValue(envName + ".serveiTipus", error(SERVEI_TIPUS_NULL, l, new Object[]{prefix}));
        }
    }

    private void validateTitular(Enviament enviament, EnviamentTipus enviamentTipus, String emisorDir3Codi, String envName, String prefix, Errors errors, Locale l) {
        if (enviament.getTitular() == null) {
            errors.rejectValue(envName + ".titular", error(TITULAR_NULL, l, new Object[]{prefix}));
            return;
        }

        validatePersona(enviament.getTitular(), enviamentTipus, envName + ".titular", prefix + "Titular - ", errors, emisorDir3Codi, l);

        // - Incapacitat
        if (enviament.getTitular().isIncapacitat() && (enviament.getDestinataris() == null || enviament.getDestinataris().isEmpty())) {
            errors.rejectValue(envName + ".titular", error(TITULAR_INCAPACITAT_SENSE_DESTINATARI, l, new Object[]{prefix}));
        }
        // Email obligatori si no té destinataris amb nif o enviament postal
        if (FISICA_SENSE_NIF.equals(enviament.getTitular().getInteressatTipus())
                && Strings.isNullOrEmpty(enviament.getTitular().getEmail())
                && (enviament.getDestinataris() == null || enviament.getDestinataris().isEmpty())
                && !enviament.isEntregaPostalActiva() ) {
            errors.rejectValue(envName + ".titular", error(TITULAR_SENSE_NIF_NI_EMAIL, l, new Object[]{prefix}));
        }
    }

    private void validateDestinataris(Enviament enviament, EnviamentTipus enviamentTipus, String envName, String prefix, Errors errors, Locale l) {
        List<Persona> destinataris = enviament.getDestinataris();
        if (destinataris == null || destinataris.isEmpty()) {
            return;
        }
        // Multiples destinataris
        if (!isMultipleDestinataris() && destinataris.size() > 1) {
            errors.rejectValue(envName, error(DESTINATARIS_1_MAX, l, new Object[]{prefix}));
        }

        IntStream.range(0, destinataris.size())
                .forEach(i -> validateDestinatari(destinataris.get(i), enviamentTipus, envName + ".destinataris[" + i + "]", prefix + "Destinatari " + (i + 1) + " - ", errors, l));

        // No administracio
    }

    private void validateDestinatari(Persona destinatari, EnviamentTipus enviamentTipus, String envName, String prefix, Errors errors, Locale l) {
        if (destinatari == null) {
            return;
        }
        // No fisica sense nif
        if (destinatari.getInteressatTipus() != null) {
            String tipus = messageHelper.getMessage("interessatTipusEnumDto." + destinatari.getInteressatTipus().name(), l);
            if (FISICA_SENSE_NIF.equals(destinatari.getInteressatTipus())) {
                errors.rejectValue(envName + ".interessatTipus", error(DESTINATARI_TIPUS_INVALID, l, new Object[]{prefix, tipus}));
                return;
            }
            // No administracio
            if (ADMINISTRACIO.equals(destinatari.getInteressatTipus())) {
                errors.rejectValue(envName + ".interessatTipus", error(DESTINATARI_TIPUS_INVALID, l, new Object[]{prefix, tipus}));
                return;
            }
        }

        validatePersona(destinatari, enviamentTipus, envName, prefix, errors, null, l);
    }

    private void validatePersona(Persona persona, EnviamentTipus enviamentTipus, String envName, String prefix, Errors errors, String emisorDir3Codi, Locale l) {

        boolean isPersonaFisica = persona.getInteressatTipus() != null && FISICA.equals(persona.getInteressatTipus());
        boolean isPersonaJuridica = persona.getInteressatTipus() != null && JURIDICA.equals(persona.getInteressatTipus());
        boolean isAdministracio = persona.getInteressatTipus() != null && ADMINISTRACIO.equals(persona.getInteressatTipus());
        boolean isPersonaSenseNif = persona.getInteressatTipus() != null && FISICA_SENSE_NIF.equals(persona.getInteressatTipus());
        boolean teTipus = isPersonaFisica || isPersonaJuridica || isAdministracio || isPersonaSenseNif;
        String tipus = teTipus ? messageHelper.getMessage("interessatTipusEnumDto." + persona.getInteressatTipus().name(), l) : "";

        // Tipus
        if(persona.getInteressatTipus() == null) {
            errors.rejectValue(envName + ".interessatTipus", error(PERSONA_TIPUS_NULL, l, new Object[]{prefix}));
        }

        // - Nom
        if(Strings.isNullOrEmpty(persona.getNom())) {
            if(isPersonaFisica || isPersonaSenseNif || isAdministracio) {
                errors.rejectValue(envName + ".nom", error(PERSONA_NOM_NULL, l, new Object[]{prefix, tipus}));
            }
        }
        if(!Strings.isNullOrEmpty(persona.getNom())) {
            if (!isAdministracio && persona.getNom().length() > 30) {
                errors.rejectValue(envName + ".nom", error(PERSONA_NOM_SIZE, l, new Object[]{prefix, tipus, 30}));
            }
            if (isAdministracio && persona.getNom().length() > 255) {
                errors.rejectValue(envName + ".nom", error(PERSONA_NOM_SIZE, l, new Object[]{prefix, tipus, 255}));
            }
        }

        // - Llinatge 1
        if ((isPersonaFisica || isPersonaSenseNif) && Strings.isNullOrEmpty(persona.getLlinatge1())) {
            errors.rejectValue(envName + ".llinatge1", error(PERSONA_LLINATGE1_NULL, l, new Object[]{prefix, tipus}));
        }
        if (!Strings.isNullOrEmpty(persona.getLlinatge1()) && persona.getLlinatge1().length() > 30) {
            errors.rejectValue(envName + ".llinatge1", error(PERSONA_LLINATGE1_SIZE, l, new Object[]{prefix, tipus, 30}));
        }

        // - Llinatge 2
        if (!Strings.isNullOrEmpty(persona.getLlinatge2()) && persona.getLlinatge2().length() > 30) {
            errors.rejectValue(envName + ".llinatge2", error(PERSONA_LLINATGE2_SIZE, l, new Object[]{prefix, tipus, 30}));
        }

        // - Nif
        if(!Strings.isNullOrEmpty(persona.getNif()) && persona.getNif().length() > 9) {
            errors.rejectValue(envName + ".nif", error(PERSONA_NIF_SIZE, l, new Object[]{prefix, tipus, 9}));
        }
        if (!isPersonaSenseNif) {
            if (Strings.isNullOrEmpty(persona.getNif())) {
                if (isPersonaFisica || isPersonaJuridica) {
                    errors.rejectValue(envName + ".interessatTipus", error(PERSONA_NIF_NULL, l, new Object[]{prefix, tipus}));
                }
            }
            if (!Strings.isNullOrEmpty(persona.getNif())) {
                if(isPersonaFisica) {
                    if (!NifHelper.isValidNifNie(persona.getNif())) {
                        errors.rejectValue(envName + ".nif", error(PERSONA_NIF_INVALID, l, new Object[]{prefix, tipus, "Només s'admet NIF/NIE"}));
                    }
                } else if (isPersonaJuridica) {
                    if (!NifHelper.isValidCif(persona.getNif())) {
                        errors.rejectValue(envName + ".nif", error(PERSONA_NIF_INVALID, l, new Object[]{prefix, tipus, "Només s'admet CIF"}));
                    }
                } else {
                    if (!NifHelper.isvalid(persona.getNif())) {
                        errors.rejectValue(envName + ".nif", error(PERSONA_NIF_INVALID, l, new Object[]{prefix, tipus, ""}));
                    }
                }
            }
        }

        // - Email
        if (!Strings.isNullOrEmpty(persona.getEmail())) {
            if(persona.getEmail().length() > 160) {
                errors.rejectValue(envName + ".email", error(PERSONA_EMAIL_SIZE, l, new Object[]{prefix, 160}));
            }
            if (!isEmailValid(persona.getEmail())) {
                errors.rejectValue(envName + ".email", error(PERSONA_EMAIL_INVALID, l, new Object[]{prefix}));
            }
        }
        // - Telèfon
        if (!Strings.isNullOrEmpty(persona.getTelefon()) && persona.getTelefon().length() > 16) {
            errors.rejectValue(envName + ".email", error(PERSONA_TELEFON_SIZE, l, new Object[]{prefix, 16}));
        }
        // - Raó social
        if (isPersonaJuridica && Strings.isNullOrEmpty(persona.getRaoSocial()) && Strings.isNullOrEmpty(persona.getNom()))  {
            errors.rejectValue(envName + ".email", error(PERSONA_RAO_SOCIAL_NULL, l, new Object[]{prefix, tipus}));
        }
        if (!Strings.isNullOrEmpty(persona.getRaoSocial()) && persona.getRaoSocial().length() > 80) {
            errors.rejectValue(envName + ".email", error(PERSONA_RAO_SOCIAL_SIZE, l, new Object[]{prefix, 80}));
        }
        // - Codi Dir3
        if (Strings.isNullOrEmpty(persona.getDir3Codi()) && isAdministracio) {
            errors.rejectValue(envName + ".dir3Codi", error(PERSONA_DIR3CODI_NULL, l, new Object[]{prefix, tipus}));
        }
        if (!Strings.isNullOrEmpty(persona.getDir3Codi())) {
            if (persona.getDir3Codi().length() > 9) {
                errors.rejectValue(envName + ".dir3Codi", error(PERSONA_DIR3CODI_SIZE, l, new Object[]{prefix, 9}));
            }
            if(isAdministracio) {
                OrganGestorDto organDir3 = cacheHelper.unitatPerCodi(persona.getDir3Codi());
                if (organDir3 == null) {
                    errors.rejectValue(envName + ".dir3Codi", error(PERSONA_DIR3CODI_INVALID, l, new Object[]{prefix, persona.getDir3Codi()}));
                } else {
                    if (EnviamentTipus.SIR.equals(enviamentTipus)) {
                        if (organDir3.getSir() == null || !organDir3.getSir()) {
                            errors.rejectValue(envName + ".dir3Codi", error(PERSONA_DIR3CODI_SENSE_OFICINA_SIR, l, new Object[]{prefix, persona.getDir3Codi()}));
                        }
                        if (emisorDir3Codi != null && !isPermesComunicacionsSirPropiaEntitat()) {
                            var organigramaByEntitat = organGestorCachable.findOrganigramaByEntitat(emisorDir3Codi);
                            if (organigramaByEntitat.containsKey(persona.getDir3Codi())){
                                errors.rejectValue(envName + ".dir3Codi", error(PERSONA_DIR3CODI_PROPIA_ENTITAT, l, new Object[]{prefix, persona.getDir3Codi()}));
                            }
                        }
                    }
                    if (Strings.isNullOrEmpty(persona.getNif())) {
                        persona.setNif(organDir3.getCif());
                    }
                }
            }
        }

    }

    private void validateEntregaDeh(Enviament enviament, String envName, String prefix, Errors errors, Locale l) {
        if (enviament.getEntregaDeh() == null) {
            errors.rejectValue(envName + ".entregaDeh", error(DEH_NULL, l, new Object[]{prefix}));
        }
        if (Strings.isNullOrEmpty(enviament.getTitular().getNif())) {
            errors.rejectValue(envName + ".entregaDeh", error(DEH_NIF_NULL, l, new Object[]{prefix}));
        }
    }

    private void validateEntregaPostal(EntregaPostal entregaPostal, String envName, String prefix, Errors errors, Locale l) {
        if (entregaPostal == null) {
            errors.rejectValue(envName + ".entregaPostal", error(POSTAL_NULL, l, new Object[]{prefix}));
            return;
        }

        envName = envName + ".entregaPostal";
        boolean isNacional = entregaPostal.getTipus() != null && NACIONAL.equals(entregaPostal.getTipus());
        boolean isEstranger = entregaPostal.getTipus() != null && ESTRANGER.equals(entregaPostal.getTipus());
        boolean isApartatCorreus = entregaPostal.getTipus() != null && APARTAT_CORREUS.equals(entregaPostal.getTipus());
        boolean isSenseNormalitzar = entregaPostal.getTipus() != null && SENSE_NORMALITZAR.equals(entregaPostal.getTipus());
        boolean teTipus = isNacional || isEstranger || isApartatCorreus || isSenseNormalitzar;
        String tipus = teTipus ? entregaPostal.getTipus().name() : "";

        if (entregaPostal.getTipus() == null) {
            errors.rejectValue(envName + ".tipus", error(POSTAL_TIPUS_NULL, l, new Object[]{prefix}));
        }
        if(Strings.isNullOrEmpty(entregaPostal.getCodiPostal())) {
            errors.rejectValue(envName + ".codiPostal", error(POSTAL_CP_NULL, l, new Object[]{prefix}));
        }

        if (!Strings.isNullOrEmpty(entregaPostal.getViaNom()) && entregaPostal.getViaNom().length() > 50) {
            errors.rejectValue(envName + ".viaNom", error(POSTAL_VIA_NOM_SIZE, l, new Object[]{prefix, 50}));
        }
        if (!Strings.isNullOrEmpty(entregaPostal.getNumeroCasa()) && entregaPostal.getNumeroCasa().length() > 5) {
            errors.rejectValue(envName + ".numeroCasa", error(POSTAL_NUM_CASA_SIZE, l, new Object[]{prefix, 5}));
        }
        if (!Strings.isNullOrEmpty(entregaPostal.getPuntKm()) && entregaPostal.getPuntKm().length() > 5) {
            errors.rejectValue(envName + ".puntKm", error(POSTAL_PUNT_KM_SIZE, l, new Object[]{prefix, 5}));
        }
        if (!Strings.isNullOrEmpty(entregaPostal.getPortal()) && entregaPostal.getPortal().length() > 3) {
            errors.rejectValue(envName + ".portal", error(POSTAL_PORTAL_SIZE, l, new Object[]{prefix, 3}));
        }
        if (!Strings.isNullOrEmpty(entregaPostal.getPorta()) && entregaPostal.getPorta().length() > 3) {
            errors.rejectValue(envName + ".porta", error(POSTAL_PORTA_SIZE, l, new Object[]{prefix, 3}));
        }
        if (!Strings.isNullOrEmpty(entregaPostal.getEscala()) && entregaPostal.getEscala().length() > 3) {
            errors.rejectValue(envName + ".escala", error(POSTAL_ESCALA_SIZE, l, new Object[]{prefix, 3}));
        }
        if (!Strings.isNullOrEmpty(entregaPostal.getPlanta()) && entregaPostal.getPlanta().length() > 3) {
            errors.rejectValue(envName + ".planta", error(POSTAL_PLANTA_SIZE, l, new Object[]{prefix, 3}));
        }
        if (!Strings.isNullOrEmpty(entregaPostal.getBloc()) && entregaPostal.getBloc().length() > 3) {
            errors.rejectValue(envName + ".bloc", error(POSTAL_BLOC_SIZE, l, new Object[]{prefix, 3}));
        }
        if (!Strings.isNullOrEmpty(entregaPostal.getComplement()) && entregaPostal.getComplement().length() > 40) {
            errors.rejectValue(envName + ".complement", error(POSTAL_COMPLEMENT_SIZE, l, new Object[]{prefix, 40}));
        }
        if (!Strings.isNullOrEmpty(entregaPostal.getNumeroQualificador()) && entregaPostal.getNumeroQualificador().length() > 3) {
            errors.rejectValue(envName + ".numeroQualificador", error(POSTAL_NUM_QUALIFICADOR_SIZE, l, new Object[]{prefix, 3}));
        }
        if(!Strings.isNullOrEmpty(entregaPostal.getCodiPostal()) && entregaPostal.getCodiPostal().length() > 10) {
            errors.rejectValue(envName + ".codiPostal", error(POSTAL_CP_SIZE, l, new Object[]{prefix, 10}));
        }
        if(!Strings.isNullOrEmpty(entregaPostal.getApartatCorreus()) && entregaPostal.getApartatCorreus().length() > 10) {
            errors.rejectValue(envName + ".apartatCorreus", error(POSTAL_APARTAT_CORREUS_SIZE, l, new Object[]{prefix, 10}));
        }
        if (!Strings.isNullOrEmpty(entregaPostal.getMunicipiCodi()) && entregaPostal.getMunicipiCodi().length() > 6) {
            errors.rejectValue(envName + ".municipiCodi", error(POSTAL_MUNICIPI_CODI_SIZE, l, new Object[]{prefix, 6}));
        }
        if (!Strings.isNullOrEmpty(entregaPostal.getProvincia()) && entregaPostal.getProvincia().length() > 2) {
            errors.rejectValue(envName + ".provincia", error(POSTAL_PROVINCIA_SIZE, l, new Object[]{prefix, 2}));
        }
        if (!Strings.isNullOrEmpty(entregaPostal.getPaisCodi()) && entregaPostal.getPaisCodi().length() > 2) {
            errors.rejectValue(envName + ".paisCodi", error(POSTAL_PAIS_CODI_SIZE, l, new Object[]{prefix, 2}));
        }
        if (!Strings.isNullOrEmpty(entregaPostal.getPoblacio()) && entregaPostal.getPoblacio().length() > 255) {
            errors.rejectValue(envName + ".poblacio", error(POSTAL_POBLACIO_SIZE, l, new Object[]{prefix, 255}));
        }
        if (!Strings.isNullOrEmpty(entregaPostal.getLinea1()) && entregaPostal.getLinea1().length() > 50) {
            errors.rejectValue(envName + ".linea1", error(POSTAL_LINIA1_SIZE, l, new Object[]{prefix, 50}));
        }
        if (!Strings.isNullOrEmpty(entregaPostal.getLinea2()) && entregaPostal.getLinea2().length() > 50) {
            errors.rejectValue(envName + ".linea2", error(POSTAL_LINIA2_SIZE, l, new Object[]{prefix, 50}));
        }
        if(isNacional) {
            if (entregaPostal.getViaTipus() == null) {
                errors.reject(messageHelper.getMessage("error.validacio.via.tipus.entrega.nacional.normalitzat"));
                errors.rejectValue(envName + ".viaTipus", error(POSTAL_VIA_TIPUS_NULL, l, new Object[]{prefix, tipus}));
            }
            if (Strings.isNullOrEmpty(entregaPostal.getViaNom())) {
                errors.rejectValue(envName + ".viaNom", error(POSTAL_VIA_NOM_NULL, l, new Object[]{prefix, tipus}));
            }
            if (Strings.isNullOrEmpty(entregaPostal.getPuntKm()) && Strings.isNullOrEmpty(entregaPostal.getNumeroCasa())) {
                errors.rejectValue(envName + ".numeroCasa", error(POSTAL_NUM_KM_NULL, l, new Object[]{prefix, tipus}));
            }
            if (Strings.isNullOrEmpty(entregaPostal.getMunicipiCodi())) {
                errors.rejectValue(envName + ".municipiCodi", error(POSTAL_MUNICIPI_CODI_NULL, l, new Object[]{prefix, tipus}));
            }
            if (Strings.isNullOrEmpty(entregaPostal.getProvincia())) {
                errors.rejectValue(envName + ".provincia", error(POSTAL_PROVINCIA_NULL, l, new Object[]{prefix, tipus}));
            }
            if (Strings.isNullOrEmpty(entregaPostal.getPoblacio())) {
                errors.rejectValue(envName + ".poblacio", error(POSTAL_POBLACIO_NULL, l, new Object[]{prefix, tipus}));
            }
        }
        if(isEstranger) {
            if (Strings.isNullOrEmpty(entregaPostal.getViaNom())) {
                errors.rejectValue(envName + ".viaNom", error(POSTAL_VIA_NOM_NULL, l, new Object[]{prefix, tipus}));
            }
            if (Strings.isNullOrEmpty(entregaPostal.getPaisCodi())) {
                errors.rejectValue(envName + ".paisCodi", error(POSTAL_PAIS_CODI_NULL, l, new Object[]{prefix, tipus}));
            }
            if (Strings.isNullOrEmpty(entregaPostal.getPoblacio())) {
                errors.rejectValue(envName + ".poblacio", error(POSTAL_POBLACIO_NULL, l, new Object[]{prefix, tipus}));
            }
        }
        if(isApartatCorreus) {
            if (Strings.isNullOrEmpty(entregaPostal.getApartatCorreus())) {
                errors.rejectValue(envName + ".apartatCorreus", error(POSTAL_APARTAT_CORREUS_NULL, l, new Object[]{prefix, tipus}));
            }
            if (Strings.isNullOrEmpty(entregaPostal.getMunicipiCodi())) {
                errors.rejectValue(envName + ".municipiCodi", error(POSTAL_MUNICIPI_CODI_NULL, l, new Object[]{prefix, tipus}));
            }
            if (Strings.isNullOrEmpty(entregaPostal.getProvincia())) {
                errors.rejectValue(envName + ".provincia", error(POSTAL_PROVINCIA_NULL, l, new Object[]{prefix, tipus}));
            }
            if (Strings.isNullOrEmpty(entregaPostal.getPoblacio())) {
                errors.rejectValue(envName + ".poblacio", error(POSTAL_POBLACIO_NULL, l, new Object[]{prefix, tipus}));
            }
        }
        if(isSenseNormalitzar) {
            if (Strings.isNullOrEmpty(entregaPostal.getLinea1())) {
                errors.rejectValue(envName + ".linea1", error(POSTAL_LINIA1_NULL, l, new Object[]{prefix, tipus}));
            }
            if (Strings.isNullOrEmpty(entregaPostal.getLinea2())) {
                errors.rejectValue(envName + ".linea2", error(POSTAL_LINIA2_NULL, l, new Object[]{prefix, tipus}));
            }
        }
    }

    private boolean documentMimeValid(EnviamentTipus enviamentTipus, String mime) {
        return EnviamentTipus.SIR.equals(enviamentTipus) ? MimeUtils.isMimeValidSIR(mime) : MimeUtils.isMimeValidNoSIR(mime);
    }

    private Set<Character> validFormat(String value) {
        String CONTROL_CARACTERS = " aàáäbcçdeèéëfghiìíïjklmnñoòóöpqrstuùúüvwxyzAÀÁÄBCÇDEÈÉËFGHIÌÍÏJKLMNÑOÒÓÖPQRSTUÙÚÜVWXYZ0123456789-_'\"/:().,¿?!¡;·";
        Set<Character> charsNoValids = new HashSet<>();
        char[] chars = value.replace("\n", "").replace("\r", "").toCharArray();

        boolean esCaracterValid = true;
        for (int i = 0; i < chars.length; i++) {
            esCaracterValid = !(CONTROL_CARACTERS.indexOf(chars[i]) < 0);
            if (!esCaracterValid) {
                charsNoValids.add(chars[i]);
            }
        }
        return charsNoValids;
    }

    private boolean hasSaltLinia(String value) {
        return value.contains("\r") || value.contains("\n") || value.contains("\r\n");
    }

    private boolean isEmailValid(String email) {
        try {
            Matcher matcher = EMAIL_REGEX.matcher(email);
            return matcher.find();
        } catch (Exception e) {
            return false;
        }
    }

    private Long getMaxTotalSizeFile() {
        return configHelper.getConfigAsLong("es.caib.notib.notificacio.document.total.size");
    }

    private Long getMaxSizeFile() {
        return configHelper.getConfigAsLong("es.caib.notib.notificacio.document.size");
    }

    private boolean isPermesComunicacionsSirPropiaEntitat() {
        return configHelper.getConfigAsBoolean("es.caib.notib.comunicacions.sir.internes");
    }

    private Boolean isMultipleDestinataris() {
        String property = "es.caib.notib.destinatari.multiple";
        return configHelper.getConfigAsBoolean(property);
    }

    private String error(int code, Locale locale, Object... arguments) {
        return messageHelper.getMessage("error.validacio." + code, arguments, locale);
    }
    private String error(int code, String prefix, Locale locale, Object... arguments) {
        return prefix + ": " + messageHelper.getMessage("error.validacio." + code, arguments, locale);
    }
}
