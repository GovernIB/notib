package es.caib.notib.logic.service.ws;

import com.google.common.base.Strings;
import com.itextpdf.text.pdf.codec.Base64;
import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.logic.cacheable.OrganGestorCachable;
import es.caib.notib.logic.helper.CacheHelper;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.MessageHelper;
import es.caib.notib.logic.intf.dto.DocumentValidDto;
import es.caib.notib.logic.intf.dto.ProcSerTipusEnum;
import es.caib.notib.logic.intf.dto.notificacio.Document;
import es.caib.notib.logic.intf.dto.notificacio.EntregaPostal;
import es.caib.notib.logic.intf.dto.notificacio.Enviament;
import es.caib.notib.logic.intf.dto.notificacio.Notificacio;
import es.caib.notib.logic.intf.dto.notificacio.Persona;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum;
import es.caib.notib.logic.intf.util.EidasValidator;
import es.caib.notib.logic.intf.util.MimeUtils;
import es.caib.notib.logic.intf.util.NifHelper;
import es.caib.notib.logic.intf.util.PdfUtils;
import es.caib.notib.persist.entity.AplicacioEntity;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.GrupEntity;
import es.caib.notib.persist.entity.OrganGestorEntity;
import es.caib.notib.persist.entity.ProcSerEntity;
import es.caib.notib.persist.repository.AplicacioRepository;
import es.caib.notib.persist.repository.GrupProcSerRepository;
import es.caib.notib.persist.repository.GrupRepository;
import es.caib.notib.persist.repository.ProcSerRepository;
import es.caib.notib.plugin.cie.TipusImpressio;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.io.IOException;
import java.util.ArrayList;
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

//@Component
@RequiredArgsConstructor
public class NotificacioValidator implements Validator {

    public static final Pattern EMAIL_REGEX = Pattern.compile("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$", Pattern.CASE_INSENSITIVE);

    private final AplicacioRepository aplicacioRepository;
    private final GrupRepository grupRepository;
    private final ProcSerRepository procSerRepository;
    private final GrupProcSerRepository grupProcSerRepository;
    private final MessageHelper messageHelper;
    private final CacheHelper cacheHelper;
    private final OrganGestorCachable organGestorCachable;
    private final ConfigHelper configHelper;

    @Setter
    private Notificacio notificacio;
    @Setter
    private EntitatEntity entitat;
    @Setter
    private ProcSerEntity procediment;
    @Setter
    private OrganGestorEntity organGestor;
    @Setter
    private DocumentValidDto[] documents;
    @Setter
    private Errors errors;
    @Setter @Getter
    private Errors warns;
    @Setter
    private Locale locale;
    @Setter
    private boolean validarDocuments = true;
    @Setter
    private boolean massiva;

    private boolean cieActiu;

    @Override
    public boolean supports(Class<?> clazz) {
        return Notificacio.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Notificacio notificacio = (Notificacio) target;

    }

    public void validate() {

        if (locale == null) {
            locale = new Locale("rest");
        }
        validateEntitat();
        if (errors.hasErrors()) {
            return;
        }
        validateAplicacio();
        validateProcediment();
        validateOrgan();
        validateDadesBasiquesNotificacio();
        validateUsuari();
        validateEnviaments();
        validateDocuments();
    }

    private void validateEntitat() {

        var emisorDir3Codi = notificacio.getEmisorDir3Codi();
        // Emisor
        if (Strings.isNullOrEmpty(emisorDir3Codi)) {
            errors.rejectValue("emisorDir3Codi", error(EMISOR_DIR3_NULL, locale));
        } else if (emisorDir3Codi.length() > 9) {
            errors.rejectValue("emisorDir3Codi", error(EMISOR_DIR3_SIZE, locale));
        }
        // Entitat
        if (!Strings.isNullOrEmpty(emisorDir3Codi) && entitat == null) {
            errors.rejectValue("emisorDir3Codi", error(EMISOR_DIR3_NO_EXIST, locale, emisorDir3Codi));
        } else if (entitat != null && !entitat.isActiva()) {
            errors.rejectValue("emisorDir3Codi", error(ENTITAT_INACTIVA, locale));
        }
    }

    private void validateAplicacio() {

        if (massiva) {
            return;
        }
        var usuariCodi = SecurityContextHolder.getContext().getAuthentication().getName();
        AplicacioEntity aplicacio = null;
        if (entitat != null && usuariCodi != null) {
            aplicacio = aplicacioRepository.findByEntitatIdAndUsuariCodi(entitat.getId(), usuariCodi);
        }
        if (aplicacio == null) {
            errors.reject(error(APLICACIO_NO_EXIST, locale, usuariCodi, notificacio.getEmisorDir3Codi()));
            return;
        }
        if (!aplicacio.isActiva()) {
            errors.reject(error(APLICACIO_INACTIVA, locale, usuariCodi));
        }
    }

    private void validateProcediment() {

        // Procediment
        var procCodi = "procedimentCodi";
        var procedimentCodi = notificacio.getProcedimentCodi();
        if ((EnviamentTipus.COMUNICACIO.equals(notificacio.getEnviamentTipus()) || EnviamentTipus.SIR.equals(notificacio.getEnviamentTipus()))
                && procediment == null && Strings.isNullOrEmpty(procedimentCodi)) {
            return;
        }
        if (EnviamentTipus.NOTIFICACIO.equals(notificacio.getEnviamentTipus())) {
            if (Strings.isNullOrEmpty(procedimentCodi)) {
                errors.rejectValue(procCodi, error(PROCSER_NULL, locale));
            } else if (procediment == null) {
                errors.rejectValue(procCodi, error(PROCSER_NO_EXIST, locale));
            }
        }
        if (!Strings.isNullOrEmpty(procedimentCodi) && procedimentCodi.length() > 9) {
            errors.rejectValue(procCodi, error(PROCSER_SIZE, locale));
        }
        if (procediment == null ) {
            errors.rejectValue(procCodi, error(PROCSER_NO_EXIST, locale));
        } else {
            if(!procediment.isActiu()) {
                errors.rejectValue(procCodi, error(PROCSER_INACTIU, locale));
            }
            if (ProcSerTipusEnum.SERVEI.equals(procediment.getTipus()) && EnviamentTipus.NOTIFICACIO.equals(notificacio.getEnviamentTipus())) {
                errors.reject(error(SERVEI_EN_NOTIFICACIO, locale));
            }
            var cieActiuPerProcComuOrgan = procediment.isComu() && organGestor.getEntregaCie() != null;
            if (!procediment.isEntregaCieActivaAlgunNivell() && !cieActiuPerProcComuOrgan) {
                int i = 0;
                for (var enviament : notificacio.getEnviaments()) {
                    if (enviament.isEntregaPostalActiva()) {
                        var error = error(POSTAL_ENTREGA_INACTIVA, locale, "Enviament " + (i + 1) + " - ");
                        errors.rejectValue("enviaments[" + i + "].entregaPostalActiva", error);
                        break;
                    }
                    i++;
                }
            }
        }
        if (Strings.isNullOrEmpty(notificacio.getGrupCodi())) {
            return;
        }
        // Grup
        var grupCodi = "grupCodi";
        if (notificacio.getGrupCodi().length() > 64) {
            errors.rejectValue(grupCodi, error(GRUPCODI_SIZE, locale));
        }
        if (procediment == null || !procediment.isAgrupar()) {
            return;
        }
        grupCodi = notificacio.getGrupCodi();
        var grupNotificacio = grupRepository.findByCodiAndEntitat(notificacio.getGrupCodi(), entitat);
//        var grupNotificacio = grupRepository.findByCodiAndEntitat(grupCodi, entitat);
        if (grupNotificacio == null) {
            errors.rejectValue(grupCodi, error(GRUP_INEXISTENT, locale, notificacio.getGrupCodi()));
            return;
        }
        var grupsProcediment = getGrupsByProcedimentAndUsuari(procediment.getId());
        if (grupsProcediment == null || grupsProcediment.isEmpty()) {
            errors.rejectValue(grupCodi, error(GRUP_EN_PROCEDIMENT_NO_AGRUPADA, locale));
            return;
        }
        if(!grupsProcediment.contains(grupNotificacio)) {
            errors.rejectValue(grupCodi, error(GRUP_NO_ASSIGNAT, locale, notificacio.getGrupCodi()));
        }
    }

    private List<GrupEntity> getGrupsByProcedimentAndUsuari(Long procedimentId) {
        List<GrupEntity> grups = new ArrayList<>();
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var procediment = procSerRepository.findById(procedimentId).orElseThrow();
        var grupsProcediment = grupProcSerRepository.findByProcSer(procediment);
        for (var grupProcediment : grupsProcediment) {
            var usuariGrup = cacheHelper.findUsuariAmbCodi(auth.getName());
            if (usuariGrup == null) {
                continue;
            }
            var rols = cacheHelper.findRolsUsuariAmbCodi(usuariGrup.getCodi());
            if (grupProcediment.getGrup() != null && rols.contains(grupProcediment.getGrup().getCodi())) {
                grups.add(grupProcediment.getGrup());
            }
        }
        return grups;
    }

    private void validateOrgan() {

        if (!EnviamentTipus.NOTIFICACIO.equals(notificacio.getEnviamentTipus()) && Strings.isNullOrEmpty(notificacio.getProcedimentCodi())
                && Strings.isNullOrEmpty(notificacio.getOrganGestor())) {

            errors.rejectValue("organGestor", error(ORGAN_NULL, locale));
        }
        if (!Strings.isNullOrEmpty(notificacio.getOrganGestor()) && notificacio.getOrganGestor().length() > 64) {
            errors.rejectValue("organGestor", error(ORGAN_SIZE, locale));
        }
        if (!Strings.isNullOrEmpty(notificacio.getOrganGestor()) && organGestor != null && !notificacio.getOrganGestor().equals(organGestor.getCodi())) {
            errors.rejectValue("organGestor", error(ORGAN_DIFF_AL_DEL_PROCEDIMENT, locale));
        }
        if (organGestor == null) {
            errors.rejectValue("organGestor", error(ORGAN_ALTRE_ENTITAT, locale));
            return;
        }

        if (!OrganGestorEstatEnum.V.equals(organGestor.getEstat())) {
            errors.rejectValue("organGestor", error(ORGAN_NO_VIGENT, locale));
        }

        if (EnviamentTipus.SIR.equals(notificacio.getEnviamentTipus()) && entitat != null && !entitat.isOficinaEntitat() && Strings.isNullOrEmpty(organGestor.getOficina())) {
            errors.rejectValue("organGestor", error(ORGAN_I_ENTITA_SENSE_OFICINA_EN_SIR, locale));
        }
    }

    private void validateDadesBasiquesNotificacio() {

        // concepte
        validateConcepte();
        // descripcio
        validateDescripcio();
        // tipus enviament
        validateTipusEnviament();
        // data programada i caducitat
        validateDates();
        // número d'expedient
        validateExpedient();
    }

    private void validateConcepte() {

        // Concepte
        var concepte = "concepte";
        if (Strings.isNullOrEmpty(notificacio.getConcepte())) {
            errors.rejectValue(concepte, error(CONCEPTE_NULL, locale));
            return;
        }
        if (notificacio.getConcepte().length() > 240) {
            errors.rejectValue(concepte, error(CONCEPTE_SIZE, locale));
        }
        var caractersNoValids = validFormat(notificacio.getConcepte());
        if (!caractersNoValids.isEmpty()) {
            String invalidChars = caractersNoValids.stream().map(String::valueOf).collect(Collectors.joining(", "));
            errors.rejectValue(concepte, error(CONCEPTE_INVALID_CHARS, locale, invalidChars));
        }
    }

    private void validateDescripcio() {

        // Descripcio
        if (Strings.isNullOrEmpty(notificacio.getDescripcio())) {
            return;
        }
        if (notificacio.getDescripcio().length() > 1000) {
            errors.rejectValue("descripcio", error(DESCRIPCIO_SIZE, locale));
        }
        var caractersNoValids = validFormat(notificacio.getDescripcio());
        if (!caractersNoValids.isEmpty()) {
            var invalidChars = caractersNoValids.stream().map(String::valueOf).collect(Collectors.joining(", "));
            errors.rejectValue("descripcio", error(DESCRIPCIO_INVALID_CHARS, locale, invalidChars));
        }
        if (hasSaltLinia(notificacio.getDescripcio())) {
            errors.rejectValue("descripcio", error(DESCRIPCIO_SALTS_LINIA, locale));
        }
    }

    private void validateTipusEnviament() {

        // Tipus d'enviament
        if (notificacio.getEnviamentTipus() == null) {
            errors.rejectValue("enviamentTipus", error(TIPUS_ENVIAMENT_NULL, locale));
        }
    }

    private void validateDates() {

        var now = new Date();
        var dataProg = notificacio.getEnviamentDataProgramada();
        var dataCaducitat = notificacio.getCaducitat();
        if (dataProg != null && dataProg.before(now) && !DateUtils.isSameDay(dataProg, now)) {
            errors.rejectValue("enviamentDataProgramada", error(DATA_PROGRAMADA_ANTERIOR_A_AVUI, locale));
        }
        if (dataCaducitat != null && dataCaducitat.before(now) && !DateUtils.isSameDay(dataCaducitat, now)) {
            errors.rejectValue("caducitat", error(DATA_CADUCITAT_ANTERIOR_A_AVUI, locale));
        }
        if (dataProg != null && dataCaducitat != null && dataCaducitat.before(dataProg)) {
            errors.rejectValue("caducitat", error(DATA_CADUCITAT_ANTERIOR_A_LA_PROGRAMADA, locale));
        }
    }

    private void validateExpedient() {

        if (!Strings.isNullOrEmpty(notificacio.getNumExpedient()) && notificacio.getNumExpedient().length() > 80) {
            errors.rejectValue("numExpedient", error(NUM_EXPEDIENT_SIZE, locale));
        }
    }

    private void validateUsuari() {

        if (Strings.isNullOrEmpty(notificacio.getUsuariCodi())) {
            errors.rejectValue("usuariCodi", error(USUARI_CODI_NULL, locale));
            return;
        }
        if (notificacio.getUsuariCodi().length() > 64) {
            errors.rejectValue("usuariCodi", error(USUARI_CODI_SIZE, locale));
        }
        var dades = cacheHelper.findUsuariAmbCodi(notificacio.getUsuariCodi());
        if (dades == null || Strings.isNullOrEmpty(dades.getCodi())) {
            errors.rejectValue("usuariCodi", error(USUARI_INEXISTENT, locale));
        }
    }

    private void validateDocuments() {

        if (!validarDocuments) {
            return;
        }
        if (notificacio.getDocument() == null) {
            errors.reject(error(DOCUMENT_NULL, locale));
            return;
        }
        var document = notificacio.getDocument();
        var enviamentTipus = notificacio.getEnviamentTipus();
        validateDocument(document, documents[0], enviamentTipus, 1, errors, locale);
        if (!EnviamentTipus.SIR.equals(enviamentTipus)) {
            if (notificacio.getDocument2() != null || notificacio.getDocument3() != null || notificacio.getDocument4() != null || notificacio.getDocument5() != null) {
                errors.reject(error(MULTIPLES_DOCUMENTS_EN_NOTCOM, locale));
            }
            // Midal màxima
            if(documents[0] != null && documents[0].getMida() != null) {
                Long maxFileSize = getMaxSizeFile();
                if(documents[0].getMida() > maxFileSize) {
                    errors.rejectValue("document", error(DOCUMENT_MASSA_GRAN, locale, maxFileSize / 1048576));
                }
            }
            return;
        }
        if (notificacio.getDocument2() != null && !notificacio.getDocument2().isEmpty()) {
            validateDocument(notificacio.getDocument2(), documents[1], enviamentTipus, 2, errors, locale);
        }
        if (notificacio.getDocument3() != null && !notificacio.getDocument3().isEmpty()) {
            validateDocument(notificacio.getDocument3(), documents[2], enviamentTipus, 3, errors, locale);
        }
        if (notificacio.getDocument4() != null && !notificacio.getDocument4().isEmpty()) {
            validateDocument(notificacio.getDocument4(), documents[3], enviamentTipus, 4, errors, locale);
        }
        if (notificacio.getDocument5() != null && !notificacio.getDocument5().isEmpty()) {
            validateDocument(notificacio.getDocument5(), documents[4], enviamentTipus, 5, errors, locale);
        }
        // Midal màxima
        var totalFileSize = Arrays.asList(documents).stream().filter(d -> d != null && d.getMida() != null).map(d -> d.getMida()).reduce(0L, (d1, d2) -> d1 + d2);
        var maxTotalFileSize = getMaxTotalSizeFile();
        if (totalFileSize > maxTotalFileSize) {
            errors.rejectValue("document", error(DOCUMENTS_SIR_MASSA_GRANS, locale, maxTotalFileSize / 1048576));
        }
    }

    private void validateDocument(Document document, DocumentValidDto dto, EnviamentTipus enviamentTipus, int numDocument, Errors errors, Locale l) {

        var doc = "document" + (numDocument > 1 ? numDocument : "");
        var prefix = "Document " + numDocument + " - ";

        //TODO: Revisar la validación del nom. Para CSV/UUid en el formulario web NO se pide un nombre; se recupera posteriormente del plugin.
        if (Strings.isNullOrEmpty(document.getArxiuNom())) {
            errors.rejectValue(doc + ".arxiuNom", error(DOCUMENT_NOM_NULL, l, prefix));
        } else if (document.getArxiuNom().length() > 200) {
            errors.rejectValue(doc + ".arxiuNom", error(DOCUMENT_NOM_SIZE, l, prefix));
        }
        if (Strings.isNullOrEmpty(document.getContingutBase64()) && Strings.isNullOrEmpty(document.getCsv()) && Strings.isNullOrEmpty(document.getUuid())) {
            errors.rejectValue(doc + ".arxiuNom", error(DOCUMENT_SOURCE_NULL, l, prefix));
        }
        // Limitar que només s'empleni un dels camps
        if (document.hasMultipleSources()) {
            errors.rejectValue(doc + ".arxiuNom", error(DOCUMENT_SOURCE_MULTIPLE, l, prefix));
        }
        // Document
        if (dto == null) {
            return;
        }
        // Format (Mime)
        if (!Strings.isNullOrEmpty(dto.getMediaType()) && !documentMimeValid(enviamentTipus, dto.getMediaType())) {
            var errorTipus = cieActiu ? DOCUMENT_FORMAT_CIE_INVALID : EnviamentTipus.SIR.equals(enviamentTipus)
                                ? DOCUMENT_FORMAT_SIR_INVALID : DOCUMENT_FORMAT_INVALID;
            errors.rejectValue(doc + ".arxiuNom", error(errorTipus, l, prefix));
        }
        if (dto.isErrorFitxer()) {
            errors.rejectValue(doc + ".arxiuNom", error(DOCUMENT_ERROR_OBTENINT, l, prefix));
        }
        if (dto.isErrorMetadades()) {
            errors.rejectValue(doc + ".arxiuNom", error(DOCUMENT_ERROR_OBTENINT_METADADES, l, prefix));
        }
        if (dto.isErrorFirma()) {
            errors.rejectValue(doc + ".arxiuNom", error(DOCUMENT_ERROR_VALIDANT_FIRMA, l, prefix));
        }

        if (cieActiu) {
            try {
                validarDocumentCIE(document, errors, doc, prefix);
            } catch (Exception ex) {

            }
        }
        // Metadades
        if (Strings.isNullOrEmpty(document.getContingutBase64())) {
            return;
        }
        // registreNotificaHelper.isSendDocumentsActive()
        if (dto.getOrigen() == null) {
            errors.rejectValue(doc + ".origen", error(DOCUMENT_METADADES_ORIGEN_NULL, l, prefix));
        }
        if (dto.getValidesa() == null) {
            errors.rejectValue(doc + ".validesa", error(DOCUMENT_METADADES_VALIDESA_NULL, l, prefix));
        }
        if (dto.getTipoDocumental() == null) {
            errors.rejectValue(doc + ".tipoDocumental", error(DOCUMENT_METADADES_TIPUS_DOCUMENTAL_NULL, l, prefix));
        }
        if (dto.getModoFirma() == null) {
            errors.rejectValue(doc + ".modoFirma", error(DOCUMENT_METADADES_MODE_FIRMA_NULL, l, prefix));
        }
    }

    public void validarDocumentCIE(Document document, Errors errors, String doc, String prefix) throws IOException {

        if (!procediment.getEntregaCieEfectiva().getCie().isCieExtern()) {
            return;
        }
        var bytes = Base64.decode(document.getContingutBase64());
        if (bytes.length > 5242880) {
            errors.rejectValue(doc + ".arxiuNom", error(DOCUMENT_CIE_PDF_MIDA_MAX, locale, prefix));
        }

        var pdf = new PdfUtils(bytes);
        var versio = "7"; // 1.7
        if (pdf.versionGreaterThan(versio)) {
            errors.rejectValue(doc + ".arxiuNom", error(DOCUMENT_CIE_PDF_VERSIO_INVALID, locale, prefix, "1.7"));
        }
        if (!pdf.isDinA4()) {
            errors.rejectValue(doc + ".arxiuNom", error(DOCUMENT_CIE_PDF_DINA4_INVALID, locale, prefix));
        }
        if (!pdf.maxPages(TipusImpressio.SIMPLEX.name())) {
            errors.rejectValue(doc + ".arxiuNom", error(DOCUMENT_CIE_PDF_MAX_PAGES_INVALID, locale, prefix));
        }
        if (pdf.isEditBlocked()) {
            errors.rejectValue(doc + ".arxiuNom", error(DOCUMENT_CIE_PDF_EDICIO_BLOQUEJADA, locale, prefix));
        }
        if (pdf.hasNoneEmbeddedFonts() && !pdf.hasBaseFonts()) {
            errors.rejectValue(doc + ".arxiuNom", error(DOCUMENT_CIE_PDF_FONTS_EMBEDED, locale, prefix));
        }
        if (!Strings.isNullOrEmpty(pdf.getJavaScript())) {
            errors.rejectValue(doc + ".arxiuNom", error(DOCUMENT_CIE_PDF_JAVA_SCRIPT, locale, prefix));
        }
        if (pdf.hasExternalLinks()) {
            errors.rejectValue(doc + ".arxiuNom", error(DOCUMENT_CIE_PDF_EXTERNAL_LINKS, locale, prefix));
        }
        if (pdf.hasTransparency()) {
            errors.rejectValue(doc + ".arxiuNom", error(DOCUMENT_CIE_PDF_TRANSPARENCY, locale, prefix));
        }
        if (pdf.hasAttachedFiles()) {
            errors.rejectValue(doc + ".arxiuNom", error(DOCUMENT_CIE_PDF_ATTACHED_FILES, locale, prefix));
        }
        if (pdf.hasMultimedia()) {
            errors.rejectValue(doc + ".arxiuNom", error(DOCUMENT_CIE_PDF_MULTIMEDIA, locale, prefix));
        }
        if (pdf.hasNonPrintableAnnotations()) {
            errors.rejectValue(doc + ".arxiuNom", error(DOCUMENT_CIE_PDF_NONE_PRINTABLE_ANNOTATIONS, locale, prefix));
        }
//        if (pdf.hasForms()) {
//            errors.rejectValue(doc + ".arxiuNom", error(DOCUMENT_CIE_PDF_INTERACTIVE_FORMS, locale, prefix));
//        }
        if (pdf.hasNoneEmbeddedImages()) {
            errors.rejectValue(doc + ".arxiuNom", error(DOCUMENT_CIE_PDF_NONE_EMBEDDED_IMAGES, locale, prefix));
        }
        if (pdf.isPrintingAllowed()) {
            errors.rejectValue(doc + ".arxiuNom", error(DOCUMENT_CIE_PDF_PRINTING_ALLOWED, locale, prefix));
        }
        if (pdf.isModifyAllowed()) {
            errors.rejectValue(doc + ".arxiuNom", error(DOCUMENT_CIE_PDF_MODIFY_ALLOWED, locale, prefix));
        }
        if (!pdf.isMaxRightMarginOk()) {
            errors.rejectValue(doc + ".arxiuNom", error(DOCUMENT_CIE_PDF_MAX_RIGHT_MARGIN, locale, prefix));
        }
        pdf.close();
    }

    private void validateEnviaments() {

        var enviaments = notificacio.getEnviaments();
        var emisor = notificacio.getEmisorDir3Codi();
        if (enviaments == null || enviaments.isEmpty()) {
            errors.reject(error(ENVIAMENTS_NULL, locale));
            return;
        }
        boolean entregaPostalActiva = entitat != null && entitat.getEntregaCie() != null
                || organGestor != null && organGestor.getEntregaCie() != null
                || procediment != null && procediment.getEntregaCie() != null;
        boolean entregaDehActiva = entitat != null && entitat.isAmbEntregaDeh();
        var enviamentTipus = notificacio.getEnviamentTipus();
        cieActiu = false;
        Enviament env;
        for (var i=0; i<enviaments.size(); i++) {
            env = enviaments.get(i);
            validateEnviament(env, enviamentTipus, emisor, entregaPostalActiva, entregaDehActiva, i, errors, locale);
            cieActiu = cieActiu || env.isEntregaPostalActiva();
        }
        if (!cieActiu) {
            notificacio.setRetard(0);
            if (isGreaterThanZero(notificacio.getRetard()) || (notificacio.getRetard() == null && procediment != null && isGreaterThanZero(procediment.getRetard()))) {
                warns.reject(error(RETARD_CIE_INACTIU, locale));
            }
        }
        // Nifs repetis
        // TODO: Eliminar la condició que no permet NIFs repetits?
        if (!EnviamentTipus.SIR.equals(enviamentTipus)) {
            List<String> nifs = notificacio.getNifsEnviaments();
            if (!nifs.stream().allMatch(new HashSet<>()::add)) {
                errors.reject(error(ENVIAMENT_NIFS_REPETITS, locale));
            }
        }
        // Comunicacions SIR que no van adreçades a administracions
        if (!EnviamentTipus.SIR.equals(enviamentTipus)) {
            return;
        }
        for(var enviament: enviaments) {
            if (enviament.getTitular() != null && !ADMINISTRACIO.equals(enviament.getTitular().getInteressatTipus())) {
                errors.reject(error(ENVIAMENT_SIR_TITULAR_NO_ADMINISTRACIO, locale));
                break;
            }
        }

    }

    private boolean isGreaterThanZero(Integer number) {
        return number != null && number > 0;
    }

    private void validateEnviament(Enviament enviament, EnviamentTipus enviamentTipus, String emisorDir3Codi, boolean entregaPostalActiva, boolean entregaDehActiva, int numEnviament, Errors errors, Locale l) {

        var envName = "enviaments[" + numEnviament + "]";
        var prefix = "Enviament " + (numEnviament + 1) + " - ";
        // Dades bàsiques
        validateDadesEnviament(enviament, envName, prefix, errors, l);
        // Titular
        validateTitular(enviament, enviamentTipus, emisorDir3Codi, envName, prefix, errors, l);
        // Destinatari
        validateDestinataris(enviament, enviamentTipus, envName, prefix, errors, l);
        // Interessats sense Nifs
        if (enviament.getTitular() != null && !FISICA_SENSE_NIF.equals(enviament.getTitular().getInteressatTipus())) {
            var senseNif = Strings.isNullOrEmpty(enviament.getTitular().getNif());
            if (senseNif && enviament.getDestinataris() != null) {
                for (var destinatari : enviament.getDestinataris()) {
                    senseNif = senseNif && Strings.isNullOrEmpty(destinatari.getNif());
                }
            }
            if (senseNif && !notificacio.isSir()) {
                errors.rejectValue(envName, error(ENVIAMENT_MINIM_UN_NIF, l, prefix));
            }
        }
        // Entrega postal
        if (enviament.isEntregaPostalActiva()) {
            if (!entregaPostalActiva) {
                errors.rejectValue(envName, error(ENVIAMENT_POSTAL_INACTIU, l, prefix));
            } else {
                validateEntregaPostal(enviament.getEntregaPostal(), envName, prefix, errors, l);
            }
        }
        // Entrega DEH
        if (enviament.isEntregaDehActiva()) {
            if (!entregaDehActiva) {
                errors.rejectValue(envName, error(ENVIAMENT_DEH_INACTIU, l, prefix));
            } else {
                validateEntregaDeh(enviament, envName, prefix, errors, l);
            }
        }
    }

    private void validateDadesEnviament(Enviament enviament, String envName, String prefix, Errors errors, Locale l) {
        // Servei tipus
        if(enviament.getServeiTipus() == null) {
            errors.rejectValue(envName + ".serveiTipus", error(SERVEI_TIPUS_NULL, l, prefix));
        }
    }

    private void validateTitular(Enviament enviament, EnviamentTipus enviamentTipus, String emisorDir3Codi, String envName, String prefix, Errors errors, Locale l) {

        if (enviament.getTitular() == null) {
            errors.rejectValue(envName + ".titular", error(TITULAR_NULL, l, prefix));
            return;
        }

        validatePersona(enviament.getTitular(), enviamentTipus, envName + ".titular", prefix + "Titular - ", errors, emisorDir3Codi, l);

        // - Incapacitat
        if (enviament.getTitular().isIncapacitat() && (enviament.getDestinataris() == null || enviament.getDestinataris().isEmpty())) {
            errors.rejectValue(envName + ".titular", error(TITULAR_INCAPACITAT_SENSE_DESTINATARI, l, prefix));
        }
        // Email obligatori si no té destinataris amb nif o enviament postal
        if (FISICA_SENSE_NIF.equals(enviament.getTitular().getInteressatTipus())
                && Strings.isNullOrEmpty(enviament.getTitular().getEmail())
                && (enviament.getDestinataris() == null || enviament.getDestinataris().isEmpty())
                && !enviament.isEntregaPostalActiva() ) {
            errors.rejectValue(envName + ".titular", error(TITULAR_SENSE_NIF_NI_EMAIL, l, prefix));
        }
    }

    private void validateDestinataris(Enviament enviament, EnviamentTipus enviamentTipus, String envName, String prefix, Errors errors, Locale l) {
        List<Persona> destinataris = enviament.getDestinataris();
        if (destinataris == null || destinataris.isEmpty()) {
            return;
        }
        // Multiples destinataris
        if (!isMultipleDestinataris() && destinataris.size() > 1) {
            errors.rejectValue(envName, error(DESTINATARIS_1_MAX, l, prefix));
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
                errors.rejectValue(envName + ".interessatTipus", error(DESTINATARI_TIPUS_INVALID, l, prefix, tipus));
                return;
            }
            // No administracio
            if (ADMINISTRACIO.equals(destinatari.getInteressatTipus())) {
                errors.rejectValue(envName + ".interessatTipus", error(DESTINATARI_TIPUS_INVALID, l, prefix, tipus));
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
            errors.rejectValue(envName + ".interessatTipus", error(PERSONA_TIPUS_NULL, l, prefix));
        }

        // - Nom
        if(Strings.isNullOrEmpty(persona.getNom()) && (isPersonaFisica || isPersonaSenseNif)) {
            errors.rejectValue(envName + ".nom", error(PERSONA_NOM_NULL, l, prefix, tipus));
        }
        if(!Strings.isNullOrEmpty(persona.getNom())) {
            if (!isAdministracio && persona.getNom().length() > 30) {
                errors.rejectValue(envName + ".nom", error(PERSONA_NOM_SIZE, l, prefix, tipus, 30));
            }
            if (isAdministracio && persona.getNom().length() > 255) {
                errors.rejectValue(envName + ".nom", error(PERSONA_NOM_SIZE, l, prefix, tipus, 255));
            }
        }

        // - Llinatge 1
        if ((isPersonaFisica || isPersonaSenseNif) && Strings.isNullOrEmpty(persona.getLlinatge1())) {
            errors.rejectValue(envName + ".llinatge1", error(PERSONA_LLINATGE1_NULL, l, prefix, tipus));
        }
        if (!Strings.isNullOrEmpty(persona.getLlinatge1()) && persona.getLlinatge1().length() > 30) {
            errors.rejectValue(envName + ".llinatge1", error(PERSONA_LLINATGE1_SIZE, l, prefix, tipus, 30));
        }

        // - Llinatge 2
        if (!Strings.isNullOrEmpty(persona.getLlinatge2()) && persona.getLlinatge2().length() > 30) {
            errors.rejectValue(envName + ".llinatge2", error(PERSONA_LLINATGE2_SIZE, l, prefix, tipus, 30));
        }

        // - Nif
//        if(!Strings.isNullOrEmpty(persona.getNif()) && persona.getNif().length() > 9) {
//            errors.rejectValue(envName + ".nif", error(PERSONA_NIF_SIZE, l, prefix, tipus, 9));
//        }
        if (!isPersonaSenseNif) {
            if (Strings.isNullOrEmpty(persona.getNif())) {
                if (isPersonaFisica || isPersonaJuridica) {
                    errors.rejectValue(envName + ".interessatTipus", error(PERSONA_NIF_NULL, l, prefix, tipus));
                }
            }
            if (!Strings.isNullOrEmpty(persona.getNif())) {
                if(isPersonaFisica) {
                    if (!NifHelper.isValidNifNie(persona.getNif()) && !EidasValidator.validateEidas(persona.getNif())) {
                        errors.rejectValue(envName + ".nif", error(PERSONA_NIF_INVALID, l, prefix, tipus, "Només s'admet NIF/NIE/Identificador EIDAS"));
                    }
                } else if (isPersonaJuridica) {
                    if (!NifHelper.isValidCif(persona.getNif()) && !EidasValidator.validateEidas(persona.getNif())) {
                        errors.rejectValue(envName + ".nif", error(PERSONA_NIF_INVALID, l, prefix, tipus, "Només s'admet CIF/Identificador EIDAS"));
                    }
                } else {
                    if (!NifHelper.isvalid(persona.getNif())) {
                        errors.rejectValue(envName + ".nif", error(PERSONA_NIF_INVALID, l, prefix, tipus, ""));
                    }
                }
            }
        }

        // - Email
        if (!Strings.isNullOrEmpty(persona.getEmail())) {
            if(persona.getEmail().length() > 160) {
                errors.rejectValue(envName + ".email", error(PERSONA_EMAIL_SIZE, l, prefix, 160));
            }
            if (!isEmailValid(persona.getEmail())) {
                errors.rejectValue(envName + ".email", error(PERSONA_EMAIL_INVALID, l, prefix));
            }
        }
        // - Telèfon
        if (!Strings.isNullOrEmpty(persona.getTelefon()) && persona.getTelefon().length() > 16) {
            errors.rejectValue(envName + ".telefon", error(PERSONA_TELEFON_SIZE, l, prefix, 16));
        }
        // - Raó social
        if ((isPersonaJuridica || isAdministracio) && Strings.isNullOrEmpty(persona.getRaoSocial()) && Strings.isNullOrEmpty(persona.getNom()))  {
            errors.rejectValue(envName + ".raoSocial", error(PERSONA_RAO_SOCIAL_NULL, l, prefix, tipus));
        }
        if (!Strings.isNullOrEmpty(persona.getRaoSocial()) && persona.getRaoSocial().length() > 255) {
            errors.rejectValue(envName + ".raoSocial", error(PERSONA_RAO_SOCIAL_SIZE, l, prefix, 255));
        }
        // - Codi Dir3
        if (Strings.isNullOrEmpty(persona.getDir3Codi()) && isAdministracio) {
            errors.rejectValue(envName + ".dir3Codi", error(PERSONA_DIR3CODI_NULL, l, prefix, tipus));
        }
        if (!Strings.isNullOrEmpty(persona.getDir3Codi())) {
            if (persona.getDir3Codi().length() > 9) {
                errors.rejectValue(envName + ".dir3Codi", error(PERSONA_DIR3CODI_SIZE, l, prefix, 9));
            }
            if(isAdministracio) {
                OrganGestorDto organDir3 = cacheHelper.unitatPerCodi(persona.getDir3Codi());
                if (organDir3 == null) {
                    errors.rejectValue(envName + ".dir3Codi", error(PERSONA_DIR3CODI_INVALID, l, prefix, persona.getDir3Codi()));
                } else {
                    if (EnviamentTipus.SIR.equals(enviamentTipus)) {
                        if (organDir3.getSir() == null || !organDir3.getSir()) {
                            errors.rejectValue(envName + ".dir3Codi", error(PERSONA_DIR3CODI_SENSE_OFICINA_SIR, l, prefix, persona.getDir3Codi()));
                        }
                        if (emisorDir3Codi != null && !isPermesComunicacionsSirPropiaEntitat()) {
                            var organigramaByEntitat = organGestorCachable.findOrganigramaByEntitat(emisorDir3Codi);
                            if (organigramaByEntitat.containsKey(persona.getDir3Codi())){
                                errors.rejectValue(envName + ".dir3Codi", error(PERSONA_DIR3CODI_PROPIA_ENTITAT, l, prefix, persona.getDir3Codi()));
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
            errors.rejectValue(envName + ".entregaDeh", error(DEH_NULL, l, prefix));
        }
        if (Strings.isNullOrEmpty(enviament.getTitular().getNif())) {
            errors.rejectValue(envName + ".entregaDeh", error(DEH_NIF_NULL, l, prefix));
        }
    }

    private void validateEntregaPostal(EntregaPostal entregaPostal, String envName, String prefix, Errors errors, Locale l) {
        if (entregaPostal == null) {
            errors.rejectValue(envName + ".entregaPostal", error(POSTAL_NULL, l, prefix));
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
            errors.rejectValue(envName + ".tipus", error(POSTAL_TIPUS_NULL, l, prefix));
        }
        if(Strings.isNullOrEmpty(entregaPostal.getCodiPostal())) {
            errors.rejectValue(envName + ".codiPostal", error(POSTAL_CP_NULL, l, prefix));
        }

        if (!Strings.isNullOrEmpty(entregaPostal.getViaNom()) && entregaPostal.getViaNom().length() > 50) {
            errors.rejectValue(envName + ".viaNom", error(POSTAL_VIA_NOM_SIZE, l, prefix, 50));
        }
        if (!Strings.isNullOrEmpty(entregaPostal.getNumeroCasa()) && entregaPostal.getNumeroCasa().length() > 5) {
            errors.rejectValue(envName + ".numeroCasa", error(POSTAL_NUM_CASA_SIZE, l, prefix, 5));
        }
        if (!Strings.isNullOrEmpty(entregaPostal.getPuntKm()) && entregaPostal.getPuntKm().length() > 5) {
            errors.rejectValue(envName + ".puntKm", error(POSTAL_PUNT_KM_SIZE, l, prefix, 5));
        }
        if (!Strings.isNullOrEmpty(entregaPostal.getPortal()) && entregaPostal.getPortal().length() > 3) {
            errors.rejectValue(envName + ".portal", error(POSTAL_PORTAL_SIZE, l, prefix, 3));
        }
        if (!Strings.isNullOrEmpty(entregaPostal.getPorta()) && entregaPostal.getPorta().length() > 3) {
            errors.rejectValue(envName + ".porta", error(POSTAL_PORTA_SIZE, l, prefix, 3));
        }
        if (!Strings.isNullOrEmpty(entregaPostal.getEscala()) && entregaPostal.getEscala().length() > 3) {
            errors.rejectValue(envName + ".escala", error(POSTAL_ESCALA_SIZE, l, prefix, 3));
        }
        if (!Strings.isNullOrEmpty(entregaPostal.getPlanta()) && entregaPostal.getPlanta().length() > 3) {
            errors.rejectValue(envName + ".planta", error(POSTAL_PLANTA_SIZE, l, prefix, 3));
        }
        if (!Strings.isNullOrEmpty(entregaPostal.getBloc()) && entregaPostal.getBloc().length() > 3) {
            errors.rejectValue(envName + ".bloc", error(POSTAL_BLOC_SIZE, l, prefix, 3));
        }
        if (!Strings.isNullOrEmpty(entregaPostal.getComplement()) && entregaPostal.getComplement().length() > 40) {
            errors.rejectValue(envName + ".complement", error(POSTAL_COMPLEMENT_SIZE, l, prefix, 40));
        }
        if (!Strings.isNullOrEmpty(entregaPostal.getNumeroQualificador()) && entregaPostal.getNumeroQualificador().length() > 3) {
            errors.rejectValue(envName + ".numeroQualificador", error(POSTAL_NUM_QUALIFICADOR_SIZE, l, prefix, 3));
        }
        if(!Strings.isNullOrEmpty(entregaPostal.getCodiPostal()) && entregaPostal.getCodiPostal().length() > 10) {
            errors.rejectValue(envName + ".codiPostal", error(POSTAL_CP_SIZE, l, prefix, 10));
        }
        if(!Strings.isNullOrEmpty(entregaPostal.getApartatCorreus()) && entregaPostal.getApartatCorreus().length() > 10) {
            errors.rejectValue(envName + ".apartatCorreus", error(POSTAL_APARTAT_CORREUS_SIZE, l, prefix, 10));
        }
        if (!Strings.isNullOrEmpty(entregaPostal.getMunicipiCodi()) && entregaPostal.getMunicipiCodi().length() > 6) {
            errors.rejectValue(envName + ".municipiCodi", error(POSTAL_MUNICIPI_CODI_SIZE, l, prefix, 6));
        }
        if (!Strings.isNullOrEmpty(entregaPostal.getProvincia()) && entregaPostal.getProvincia().length() > 2) {
            errors.rejectValue(envName + ".provincia", error(POSTAL_PROVINCIA_SIZE, l, prefix, 2));
        }
        if (!Strings.isNullOrEmpty(entregaPostal.getPaisCodi()) && entregaPostal.getPaisCodi().length() > 2) {
            errors.rejectValue(envName + ".paisCodi", error(POSTAL_PAIS_CODI_SIZE, l, prefix, 2));
        }
        if (!Strings.isNullOrEmpty(entregaPostal.getPoblacio()) && entregaPostal.getPoblacio().length() > 255) {
            errors.rejectValue(envName + ".poblacio", error(POSTAL_POBLACIO_SIZE, l, prefix, 255));
        }
        if (!Strings.isNullOrEmpty(entregaPostal.getLinea1()) && entregaPostal.getLinea1().length() > 50) {
            errors.rejectValue(envName + ".linea1", error(POSTAL_LINIA1_SIZE, l, prefix, 50));
        }
        if (!Strings.isNullOrEmpty(entregaPostal.getLinea2()) && entregaPostal.getLinea2().length() > 50) {
            errors.rejectValue(envName + ".linea2", error(POSTAL_LINIA2_SIZE, l, prefix, 50));
        }
        if(isNacional) {
            if (entregaPostal.getViaTipus() == null) {
//                errors.reject(messageHelper.getMessage("error.validacio.via.tipus.entrega.nacional.normalitzat"));
                errors.rejectValue(envName + ".viaTipus", error(POSTAL_VIA_TIPUS_NULL, l, prefix, tipus));
            }
            if (Strings.isNullOrEmpty(entregaPostal.getViaNom())) {
                errors.rejectValue(envName + ".viaNom", error(POSTAL_VIA_NOM_NULL, l, prefix, tipus));
            }
            if (Strings.isNullOrEmpty(entregaPostal.getPuntKm()) && Strings.isNullOrEmpty(entregaPostal.getNumeroCasa())) {
                errors.rejectValue(envName + ".numeroCasa", error(POSTAL_NUM_KM_NULL, l, prefix, tipus));
            }
            if (Strings.isNullOrEmpty(entregaPostal.getMunicipiCodi())) {
                errors.rejectValue(envName + ".municipiCodi", error(POSTAL_MUNICIPI_CODI_NULL, l, prefix, tipus));
            }
            if (Strings.isNullOrEmpty(entregaPostal.getProvincia())) {
                errors.rejectValue(envName + ".provincia", error(POSTAL_PROVINCIA_NULL, l, prefix, tipus));
            }
            if (Strings.isNullOrEmpty(entregaPostal.getPoblacio())) {
                errors.rejectValue(envName + ".poblacio", error(POSTAL_POBLACIO_NULL, l, prefix, tipus));
            }
            if (Strings.isNullOrEmpty(entregaPostal.getPaisCodi())) {
                errors.rejectValue(envName + ".paisCodi", error(POSTAL_PAIS_CODI_NULL, l, prefix, tipus));
            }
        }
        if(isEstranger) {
            if (Strings.isNullOrEmpty(entregaPostal.getViaNom())) {
                errors.rejectValue(envName + ".viaNom", error(POSTAL_VIA_NOM_NULL, l, prefix, tipus));
            }
            if (Strings.isNullOrEmpty(entregaPostal.getPaisCodi())) {
                errors.rejectValue(envName + ".paisCodi", error(POSTAL_PAIS_CODI_NULL, l, prefix, tipus));
            }
            if (Strings.isNullOrEmpty(entregaPostal.getPoblacio())) {
                errors.rejectValue(envName + ".poblacio", error(POSTAL_POBLACIO_NULL, l, prefix, tipus));
            }
        }
        if(isApartatCorreus) {
            if (Strings.isNullOrEmpty(entregaPostal.getApartatCorreus())) {
                errors.rejectValue(envName + ".apartatCorreus", error(POSTAL_APARTAT_CORREUS_NULL, l, prefix, tipus));
            }
            if (Strings.isNullOrEmpty(entregaPostal.getMunicipiCodi())) {
                errors.rejectValue(envName + ".municipiCodi", error(POSTAL_MUNICIPI_CODI_NULL, l, prefix, tipus));
            }
            if (Strings.isNullOrEmpty(entregaPostal.getProvincia())) {
                errors.rejectValue(envName + ".provincia", error(POSTAL_PROVINCIA_NULL, l, prefix, tipus));
            }
            if (Strings.isNullOrEmpty(entregaPostal.getPoblacio())) {
                errors.rejectValue(envName + ".poblacio", error(POSTAL_POBLACIO_NULL, l, prefix, tipus));
            }
        }
        if(isSenseNormalitzar) {
            if (Strings.isNullOrEmpty(entregaPostal.getLinea1())) {
                errors.rejectValue(envName + ".linea1", error(POSTAL_LINIA1_NULL, l, prefix, tipus));
            }
            if (Strings.isNullOrEmpty(entregaPostal.getLinea2())) {
                errors.rejectValue(envName + ".linea2", error(POSTAL_LINIA2_NULL, l, prefix, tipus));
            }
        }
    }

    private boolean documentMimeValid(EnviamentTipus enviamentTipus, String mime) {
        return EnviamentTipus.SIR.equals(enviamentTipus) ? MimeUtils.isMimeValidSIR(mime)
                : cieActiu ? MimeUtils.isMimeValidCIE(mime) : MimeUtils.isMimeValidNoSIR(mime);
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

    protected String error(int code, Locale locale, Object... arguments) {
        return messageHelper.getMessage("error.validacio." + code, arguments, locale);
    }
    protected String error(int code, String prefix, Locale locale, Object... arguments) {
        return prefix + ": " + messageHelper.getMessage("error.validacio." + code, arguments, locale);
    }
}
