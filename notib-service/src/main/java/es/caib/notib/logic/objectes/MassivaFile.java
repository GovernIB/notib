package es.caib.notib.logic.objectes;

import com.google.common.base.Strings;
import es.caib.notib.client.domini.DocumentTipus;
import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.client.domini.InteressatTipus;
import es.caib.notib.client.domini.NotificaDomiciliConcretTipus;
import es.caib.notib.client.domini.OrigenEnum;
import es.caib.notib.client.domini.ServeiTipus;
import es.caib.notib.client.domini.TipusDocumentalEnum;
import es.caib.notib.client.domini.ValidesaEnum;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.MessageHelper;
import es.caib.notib.logic.helper.PluginHelper;
import es.caib.notib.logic.intf.dto.notificacio.Document;
import es.caib.notib.logic.intf.dto.notificacio.EntregaPostal;
import es.caib.notib.logic.intf.dto.notificacio.Enviament;
import es.caib.notib.logic.intf.dto.notificacio.Notificacio;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioMassivaDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioMassivaInfoDto.NotificacioInfo;
import es.caib.notib.logic.intf.dto.notificacio.Persona;
import es.caib.notib.logic.intf.exception.InvalidCSVFileException;
import es.caib.notib.logic.intf.exception.InvalidCSVFileNotificacioMassivaException;
import es.caib.notib.logic.intf.exception.FilaDiferentSizeHeader;
import es.caib.notib.logic.intf.exception.MaxLinesExceededException;
import es.caib.notib.logic.intf.util.NifHelper;
import es.caib.notib.logic.utils.CSVReader;
import es.caib.notib.logic.utils.MimeUtils;
import es.caib.notib.logic.utils.ZipFileUtils;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.entity.NotificacioEventEntity;
import es.caib.notib.persist.repository.NotificacioEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class MassivaFile {

    private static final Long MAX_ENVIAMENTS = 999L;

    // Notificacions
    private List<String> headerCsv;
    private List<List<String>> enviamentsCsv;
    private Map<MassivaColumnsEnum, Integer> headerColumns = new HashMap<>();
    private List<MassivaColumnsEnum> misingColumns = new ArrayList<>();
    private List<List<String>> errors;
    private List<Notificacio> notificacions;
    // Documents
    private Map<String, Document> documents = new HashMap<>();
    private Map<String, Long> documentsProcessatsMassiu = new HashMap<>();
//    // Informes
//    private ICsvListWriter listWriterInforme;
//    private ICsvListWriter listWriterErrors;
    // Info
    List<NotificacioInfo> enviamentsInfo;

    // Serveis

    // Helpers
    private ConfigHelper configHelper;
    private MessageHelper messageHelper;
    private PluginHelper pluginHelper;

    public MassivaFile(ConfigHelper configHelper, MessageHelper messageHelper, PluginHelper pluginHelper) {

        this.configHelper = configHelper;
        this.messageHelper = messageHelper;
        this.pluginHelper = pluginHelper;

//        try (   var writerListErrors = new StringWriter();
//                var writerListInforme = new StringWriter()){
//            var listWriterErrors = new CsvListWriter(writerListErrors, CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE);
//            var listWriterInforme = new CsvListWriter(writerListInforme, CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE);
//            writeCsvHeader(listWriterErrors, csvHeader.toArray(new String[]{}));
//            writeCsvHeader(listWriterInforme, csvHeader.toArray(new String[]{}));
//        } catch (Throwable t) {
//            log.error("[NOT-MASSIVA] Error no controlat en l'enviament massiu", t);
//            throw new NotificacioMassivaException(t.getMessage(), t);
//        }
    }

    public void initCreate(NotificacioMassivaDto notificacioMassiva, EntitatEntity entitat, String usuari) throws Exception {

        // Obtenim les línies del fitxer CSV
        var csvLinies = CSVReader.readFile(notificacioMassiva.getFicheroCsvBytes());
        // Capçalera
        this.headerCsv = csvLinies.get(0);
        this.headerColumns = getHeaderColumns();
        this.misingColumns = getMissingColumns();
        // Enviaments CSV
        this.enviamentsCsv = getLiniesEnviaments(csvLinies);
        // Validar CSV
        checkCSVContent();
        // Documents
        this.documents = getDocuments(notificacioMassiva.getFicheroZipBytes());
        // Notificacions
        this.notificacions = getNotificacions(entitat, usuari, notificacioMassiva.getCaducitat());
    }

    public void initInfo(byte[] contingutCsv, List<NotificacioEntity> notificacions, NotificacioEventRepository notificacioEventRepository) {

        // Obtenim les línies del fitxer CSV
        var csvLinies = CSVReader.readFile(contingutCsv);
        // Capçalera
        this.headerCsv = csvLinies.get(0);
        this.headerColumns = getHeaderColumns();
        // Enviaments CSV
        this.enviamentsCsv = getLiniesEnviaments(csvLinies);
        // Info
        this.enviamentsInfo = generateEnviamentsInfo(notificacions, notificacioEventRepository);
    }

    public List<String> getHeader() {
        return this.headerCsv;
    }

    public Integer getNombreEnviamentsCsv() {
        return enviamentsCsv.size();
    }

    public List<Notificacio> getNotificacions() {
        return notificacions;
    }

    public List<List<String>> getEnviamentsCsv() {
        return enviamentsCsv;
    }

    public List<NotificacioInfo> getEnviamentsInfo() {
        return enviamentsInfo;
    }


    public List<NotificacioInfo> generateEnviamentsInfo(List<NotificacioEntity> notificacions, NotificacioEventRepository notificacioEventRepository) {

        List<NotificacioInfo> enviamentsInfo = new ArrayList<>();
        if (enviamentsCsv == null) {
            return enviamentsInfo;
        }

        var numNotificacio = 0;
        for (var enviamentCsv: enviamentsCsv) {
            var errors = enviamentCsv.get(enviamentCsv.size() - 1);
            var cancelada = messageHelper.getMessage("notificacio.massiva.cancelada").equals(errors);
            String errorsExecucio = null;
            if (notificacions != null && !notificacions.isEmpty() && "OK".equalsIgnoreCase(errors)) {
                List<NotificacioEventEntity> events = notificacioEventRepository.findByNotificacioIdAndErrorIsTrue(notificacions.get(numNotificacio++).getId());
                errorsExecucio = events.stream()
                        .filter(event -> !Strings.isNullOrEmpty(event.getErrorDescripcio()))
                        .map(event -> event.getErrorDescripcio())
                        .collect(Collectors.joining("\n"));
            }

            var unitatEmiIndex = headerColumns.get(MassivaColumnsEnum.UNITAT_EMISORA);
            var concepteIndex = headerColumns.get(MassivaColumnsEnum.CONCEPTE);
            var tipusEnvIndex = headerColumns.get(MassivaColumnsEnum.TIPUS_ENV);
            var refEmisorIndex = headerColumns.get(MassivaColumnsEnum.REF_EMISOR);
            var fitxerNomIndex = headerColumns.get(MassivaColumnsEnum.FITXER_NOM);
            var fitxerUuidIndex = headerColumns.get(MassivaColumnsEnum.FITXER_UUID);
            var fitxerCsvIndex = headerColumns.get(MassivaColumnsEnum.FITXER_CSV);
            var normalitzatIndex = headerColumns.get(MassivaColumnsEnum.FITXER_NORMAL);
            var prioritatIndex = headerColumns.get(MassivaColumnsEnum.PRIORITAT);
            var nomIndex = headerColumns.get(MassivaColumnsEnum.DEST_NOM);
            var llinatgesIndex = headerColumns.get(MassivaColumnsEnum.DEST_LLINATGES);
            var numDocIndex = headerColumns.get(MassivaColumnsEnum.DEST_DOC);
            var emailIndex = headerColumns.get(MassivaColumnsEnum.DEST_EMAIL);
            var unitatDestIndex = headerColumns.get(MassivaColumnsEnum.UNITAT_DESTI);
            var linia1Index = headerColumns.get(MassivaColumnsEnum.ADDR_LIN1);
            var linia2Index = headerColumns.get(MassivaColumnsEnum.ADDR_LIN2);
            var cpIndex = headerColumns.get(MassivaColumnsEnum.ADDR_CP);
            var retardIndex = headerColumns.get(MassivaColumnsEnum.RETARD);
            var procedimentIndex = headerColumns.get(MassivaColumnsEnum.PROCEDIMENT);
            var dataProgIndex = headerColumns.get(MassivaColumnsEnum.DATA_PROG);
            var descripcioIndex = headerColumns.get(MassivaColumnsEnum.DESCRIPCIO);
            var origenIndex = headerColumns.get(MassivaColumnsEnum.META_ORIGEN);
            var estatElaIndex = headerColumns.get(MassivaColumnsEnum.META_ESTAT_ELAB);
            var tipusDocIndex = headerColumns.get(MassivaColumnsEnum.META_TIPUS_DOC);
            var firmatIndex = headerColumns.get(MassivaColumnsEnum.META_FIRMAT);
            NotificacioInfo enviamentInfo = NotificacioInfo.builder()
                    .codiDir3UnidadRemisora(unitatEmiIndex != null ? enviamentCsv.get(unitatEmiIndex) : null)
                    .concepto(concepteIndex != null ? enviamentCsv.get(concepteIndex) : null)
                    .enviamentTipus(tipusEnvIndex != null ? enviamentCsv.get(tipusEnvIndex) : null)
                    .referenciaEmisor(refEmisorIndex != null ? enviamentCsv.get(refEmisorIndex) : null)
                    .nombreFichero(fitxerNomIndex != null ? enviamentCsv.get(fitxerNomIndex)  : null)
                    .uuidFichero(fitxerUuidIndex != null ? enviamentCsv.get(fitxerUuidIndex) : null)
                    .csvFichero(fitxerCsvIndex != null ? enviamentCsv.get(fitxerCsvIndex) : null)
                    .normalizado(normalitzatIndex != null ? enviamentCsv.get(normalitzatIndex) : null)
                    .prioridadServicio(prioritatIndex != null ? enviamentCsv.get(prioritatIndex) : null)
                    .nombre(nomIndex != null ? enviamentCsv.get(nomIndex) : null)
                    .apellidos(llinatgesIndex != null ? enviamentCsv.get(llinatgesIndex) : null)
                    .cifNif(numDocIndex != null ? enviamentCsv.get(numDocIndex) : null)
                    .email(emailIndex != null ? enviamentCsv.get(emailIndex) : null)
                    .codigoDestino(unitatDestIndex != null ? enviamentCsv.get(unitatDestIndex) : null)
                    .linea1(linia1Index != null ? enviamentCsv.get(linia1Index) : null)
                    .linea2(linia2Index != null ? enviamentCsv.get(linia2Index) : null)
                    .codigoPostal(cpIndex != null ? enviamentCsv.get(cpIndex) : null)
                    .retardoPostal(retardIndex != null ? enviamentCsv.get(retardIndex) : null)
                    .codigoProcedimiento(procedimentIndex != null ? enviamentCsv.get(procedimentIndex) : null)
                    .fechaEnvioProgramado(dataProgIndex != null ? enviamentCsv.get(dataProgIndex) : null)
                    .descripcio(descripcioIndex != null ? enviamentCsv.get(descripcioIndex) : null)
                    .origen(origenIndex != null ? enviamentCsv.get(origenIndex) : null)
                    .estadoElaboracion(estatElaIndex != null ? enviamentCsv.get(estatElaIndex) : null)
                    .tipoDocumental(tipusDocIndex != null ? enviamentCsv.get(tipusDocIndex) : null)
                    .pdfFirmado(firmatIndex != null ? enviamentCsv.get(firmatIndex) : null)
                    .errores(errors)
                    .cancelada(cancelada)
                    .errorsExecucio(errorsExecucio)
                    .build();
            enviamentsInfo.add(enviamentInfo);
        }

        return enviamentsInfo;
    }

    public List<String> getEnviamentCsv(int index) {

        if (enviamentsCsv == null || enviamentsCsv.size() < index) {
            return null;
        }
        return enviamentsCsv.get(index);
    }

    private List<Notificacio> getNotificacions(EntitatEntity entitat, String usuari, Date caducitat) {

        var notis = new ArrayList<Notificacio>();
        InteressatTipus interessatTipus;
        boolean perEmail;
        EntregaPostal entregaPostal;
        Persona titular;
        Enviament enviament;
        Notificacio notificacio;
        String numDocument;
        String email;
        String dir3Codi;
        for (var enviamentCsv: enviamentsCsv) {

            numDocument = enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.DEST_DOC));
            email = enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.DEST_EMAIL));
            dir3Codi = enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.UNITAT_DESTI));
            interessatTipus = getInteressatTipus(numDocument, email, dir3Codi);

            perEmail = InteressatTipus.FISICA_SENSE_NIF.equals(interessatTipus);
            entregaPostal = null;
            if (!perEmail) {
                var linia1 = enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.ADDR_LIN1));
                var linia2 = enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.ADDR_LIN2));
                var cp = enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.ADDR_CP));
                if (entitat.getEntregaCie() != null && !Strings.isNullOrEmpty(linia1) && !Strings.isNullOrEmpty(cp)) {
                    entregaPostal = EntregaPostal.builder().tipus(NotificaDomiciliConcretTipus.SENSE_NORMALITZAR).linea1(linia1).linea2(linia2).codiPostal(cp).build();
                }
            }
            titular = Persona.builder()
                    .interessatTipus(interessatTipus)
                    .nom(enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.DEST_NOM)))
                    .llinatge1(enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.DEST_LLINATGES)))
                    .documentTipus(InteressatTipus.FISICA_SENSE_NIF.equals(interessatTipus) ?
                            getDocumentTipus(enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.TIPUS_DOC))) : null)
                    .nif(enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.DEST_DOC)))
                    .dir3Codi(enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.UNITAT_DESTI)))
                    .email(enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.DEST_EMAIL)))
                    .incapacitat(false)
                    .build();
            enviament = Enviament.builder()
                    .serveiTipus(getServeiTipus(enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.PRIORITAT))))
                    .entregaDehActiva(false)
                    .entregaPostalActiva(entregaPostal != null)
                    .entregaPostal(entregaPostal)
                    .titular(titular)
                    .perEmail(perEmail)
                    .build();
            notificacio = Notificacio.builder()
                    .emisorDir3Codi(entitat.getDir3Codi())
                    .organGestor(enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.UNITAT_EMISORA)))
                    .procedimentCodi(enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.PROCEDIMENT)))
                    .concepte(enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.CONCEPTE)))
                    .descripcio(enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.DESCRIPCIO)))
                    .numExpedient(enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.REF_EMISOR)))
                    .enviamentTipus(getEnviamentTipus(enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.TIPUS_ENV))))
                    .caducitat(caducitat)
                    .retard(getRetard(enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.RETARD))))
                    .enviamentDataProgramada(getData(enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.DATA_PROG))))
                    .document(documents.get(getDocumentKey(enviamentCsv)))
                    .enviaments(List.of(enviament))
                    .usuariCodi(usuari)
                    .build();
            notis.add(notificacio);
        }
        return notis;
    }

    private DocumentTipus getDocumentTipus(String tipus) {

        if (Strings.isNullOrEmpty(tipus)) {
            return DocumentTipus.ALTRE;
        }
        switch (tipus.toUpperCase()) {
            case "PASSAPORT":
            case "PASAPORTE":
                return DocumentTipus.PASSAPORT;
            case "ESTRANGER":
            case "EXTRANJERO":
                return DocumentTipus.ESTRANGER;
            default:
                return DocumentTipus.ALTRE;
        }
    }

    private ServeiTipus getServeiTipus(String tipus) {

        if (Strings.isNullOrEmpty(tipus)) {
            return null;
        }
        switch (tipus.toUpperCase()) {
            case "URGENT":
            case "URGENTE":
                return ServeiTipus.URGENT;
            default:
                return ServeiTipus.NORMAL;
        }
    }

    private InteressatTipus getInteressatTipus(String numDocument, String email, String dir3Codi) {

        if (Strings.isNullOrEmpty(numDocument) && !Strings.isNullOrEmpty(email)) {
            return InteressatTipus.FISICA_SENSE_NIF;
        }
//        if (Strings.isNullOrEmpty(numDocument) && Strings.isNullOrEmpty(email)) {
//            return null;
//        }
        if (!Strings.isNullOrEmpty(numDocument) && NifHelper.isValidCif(numDocument)) {
            return InteressatTipus.JURIDICA;
        }
        if (!Strings.isNullOrEmpty(numDocument) && NifHelper.isValidNifNie(numDocument)) {
            return InteressatTipus.FISICA;
        }
        var lista = pluginHelper.unitatsPerCodi(dir3Codi);
        if (lista != null && !lista.isEmpty()) {
            return InteressatTipus.ADMINISTRACIO;
        }
        return null;
    }

    private EnviamentTipus getEnviamentTipus(String strTipus) {

        if (Strings.isNullOrEmpty(strTipus)) {
            return null;
        }
        switch (StringUtils.stripAccents(strTipus).toUpperCase()) {
            case "C":
            case "COMUNICACIO":
            case "COMUNICACION":
                return EnviamentTipus.COMUNICACIO;
            case "N":
            case "NOTIFICACIO":
            case "NOTIFICACION":
                return EnviamentTipus.NOTIFICACIO;
            case "S":
            case "SIR":
                return EnviamentTipus.SIR;
            default:
                return null;
        }
    }

    private Integer getRetard(String strRetard) {

        if (Strings.isNullOrEmpty(strRetard)) {
            return null;
        }
        try {
            return Integer.parseInt(strRetard);
        } catch (NumberFormatException nfe) { }
        return null;
    }

    private Date getData(String strData) {

        try {
            return !Strings.isNullOrEmpty(strData) ? new SimpleDateFormat("dd/MM/yyyy").parse(strData) : null;
        } catch (ParseException e) { }
        return null;
    }

    private Map<String, Document> getDocuments(byte[] contingutZip) throws Exception {

        Map<String, Document> documentMap = new HashMap<>();
        boolean requereixMetadades = getRequereixMetadades();
        var fileNames = ZipFileUtils.readZipFileNames(contingutZip);

        int linia = 0;
        Document document = null;
        for (List<String> enviamentCsv: enviamentsCsv) {

            linia++;
            document = null;
            var fitxerNomIndex = headerColumns.get(MassivaColumnsEnum.FITXER_NOM);
            var fitxerUuidIndex = headerColumns.get(MassivaColumnsEnum.FITXER_UUID);
            var fitxerCsvIndex = headerColumns.get(MassivaColumnsEnum.FITXER_CSV);
            var documentNom = fitxerNomIndex != null ? enviamentCsv.get(fitxerNomIndex) : null;
            var documentUuid = fitxerUuidIndex != null ? enviamentCsv.get(fitxerUuidIndex) : null;
            var documentCsv = fitxerCsvIndex != null ? enviamentCsv.get(fitxerCsvIndex) : null;
            var normalitzat = getBoolea(enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.FITXER_NORMAL)));

            // Document físic
            if (!Strings.isNullOrEmpty(documentNom)) {
                int liniaLamda = linia;
                var arxiuNom = fileNames.stream().filter(nom -> nom.equals(documentNom)).findFirst()
                        .orElseThrow(() -> new Exception(messageHelper.getMessage("notificacio.massiva.nom.fitxer.no.coincident", new Object[]{liniaLamda})));
                if (arxiuNom != null && !documentMap.containsKey(arxiuNom)) {
                    var arxiuBytes = ZipFileUtils.readZipFile(contingutZip, arxiuNom);
                    var mime = MimeUtils.getMimeTypeFromContingut(arxiuNom, arxiuBytes);
                    document = Document.builder()
                            .arxiuNom(arxiuNom)
                            .contingutBase64(Base64.encodeBase64String(arxiuBytes))
                            .normalitzat(normalitzat != null ? normalitzat : false)
                            .generarCsv(false)
                            .mediaType(mime)
                            .mida(Long.valueOf(arxiuBytes.length))
                            .build();
                    documentMap.put(documentNom, document);
                }
            }

            // UUID
            if (document == null && !Strings.isNullOrEmpty(documentUuid) && !documentMap.containsKey(documentUuid)) {
//                var uuidPattern = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-5][0-9a-f]{3}-[089ab][0-9a-f]{3}-[0-9a-f]{12}$";
//                var pUuid = Pattern.compile(uuidPattern);
//                var mUuid = pUuid.matcher(documentUuid);
//                if (mUuid.matches() && !documents.containsKey(documentUuid)) {
                document = Document.builder()
                        .uuid(documentUuid)
                        .normalitzat(normalitzat != null ? normalitzat : false)
                        .generarCsv(false)
                        .build();
                documentMap.put(documentUuid, document);
//                }
            }

            // CSV
            if (document == null && !Strings.isNullOrEmpty(documentCsv) && !documentMap.containsKey(documentCsv)) {
                document = Document.builder()
                        .csv(documentCsv)
                        .normalitzat(normalitzat != null ? normalitzat : false)
                        .generarCsv(false)
                        .build();
                documentMap.put(documentCsv, document);
            }

            if (document != null) {
                document.setOrigen(requereixMetadades ? getOrigen(enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.META_ORIGEN))) : OrigenEnum.ADMINISTRACIO);
                document.setValidesa(requereixMetadades ? getValidesa(enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.META_ESTAT_ELAB))) : ValidesaEnum.ORIGINAL);
                document.setTipoDocumental(requereixMetadades ? getTipusDocumental(enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.META_TIPUS_DOC))) : TipusDocumentalEnum.ALTRES);
                document.setModoFirma(requereixMetadades ? getBoolea(enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.META_FIRMAT))) : false);
            }
        }

        return documentMap;
    }

    private Boolean getBoolea(String valor) {

        if (Strings.isNullOrEmpty(valor)) {
            return false;
        }
        switch (valor.toUpperCase()) {
            case "SI":
            case "TRUE":
                return true;
            case "NO":
            case "FALSE":
                return false;
            default:
                return null;
        }
    }

    private TipusDocumentalEnum getTipusDocumental(String tipus) {

        if (Strings.isNullOrEmpty(tipus)) {
            return null;
        }

        var tipo = TipusDocumentalEnum.ALTRES;
        try {
            tipo = TipusDocumentalEnum.valueOf(tipus.toUpperCase());
        } catch (IllegalArgumentException e) { }
        return tipo;
    }

    private ValidesaEnum getValidesa(String validesa) {
        if (Strings.isNullOrEmpty(validesa)) {
            return null;
        }
        switch (StringUtils.stripAccents(validesa).toUpperCase()) {
            case "COPIA":
                return ValidesaEnum.COPIA;
            case "COPIA AUTENTICA":
                return ValidesaEnum.COPIA_AUTENTICA;
            default:
                return ValidesaEnum.ORIGINAL;
        }
    }

    private OrigenEnum getOrigen(String origen) {
        if (Strings.isNullOrEmpty(origen)) {
            return null;
        }
        switch (StringUtils.stripAccents(origen).toUpperCase()) {
            case "ADMINISTRACIO":
            case "ADMINISTRACION":
                return OrigenEnum.ADMINISTRACIO;
            default:
                return OrigenEnum.CIUTADA;
        }
    }

    private String getDocumentKey(List<String> enviamentCsv) {
        var documentNom = enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.FITXER_NOM));
        if (isValidDocumentKey(documentNom)) {
            return documentNom;
        }

        var documentUuid = enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.FITXER_UUID));
        if (isValidDocumentKey(documentUuid)) {
            return documentUuid;
        }
        var documentCsv = enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.FITXER_CSV));
        return documentCsv;
    }

    private boolean isValidDocumentKey(String documentKey) {
        return !Strings.isNullOrEmpty(documentKey) && documents.containsKey(documentKey);
    }

    private List<List<String>> getLiniesEnviaments(List<List<String>> csvLinies) {
        return csvLinies.stream()
                .skip(1) // La primera fila és la capçalera
                .map(this::trimLine)
                .collect(Collectors.toList());
    }

    private List<String> trimLine(List<String> linia) {
        return linia.stream().map(camp -> camp != null && !camp.isBlank() ? camp.trim() : null).collect(Collectors.toList());
    }

    private Map<MassivaColumnsEnum, Integer> getHeaderColumns() {
        return IntStream.range(0, headerCsv.size()).boxed()
                .filter(this::columnExist)
                .collect(Collectors.toMap(idx -> MassivaColumnsEnum.fromNom(headerCsv.get(idx)), Function.identity()));
    }

    private boolean columnExist(Integer index) {
        return MassivaColumnsEnum.fromNom(headerCsv.get(index)) != null;
    }

    private List<MassivaColumnsEnum> getMissingColumns() {
        final boolean requereixMetadades = getRequereixMetadades();
        return Arrays.stream(MassivaColumnsEnum.values())
                .filter(c -> isNeededAndNotPresent(c, requereixMetadades))
                .collect(Collectors.toList());
    }

    private boolean isNeededAndNotPresent(MassivaColumnsEnum column, boolean requereixMetadades) {
        return !headerColumns.containsKey(column) && (requereixMetadades || !column.name().startsWith("META_"));
    }

    private void checkCSVContent() {

        if (enviamentsCsv == null || headerCsv == null) {
            throw new InvalidCSVFileException(messageHelper.getMessage("notificacio.massiva.error.csv.sense.contingut"));
        }
        if (enviamentsCsv.isEmpty()) {
            throw new InvalidCSVFileNotificacioMassivaException(messageHelper.getMessage("notificacio.massiva.error.csv.buit"));
        }
        var maxEnviaments = getMaximEnviaments();
        if (enviamentsCsv.size() > maxEnviaments) {
            log.debug(String.format("[NOT-MASSIVA] El fitxer CSV conté més de les %d línies permeses.", maxEnviaments));
            throw new MaxLinesExceededException(String.format(messageHelper.getMessage("notificacio.massiva.max.linies.permeses", new Object[]{maxEnviaments})));
        }

        if (!misingColumns.isEmpty() && !checkMissingColumnsFileColumns()) {
            var msg = messageHelper.getMessage("notificacio.massiva.columnes.faltants") +
                    getMissingColumns().stream().map(MassivaColumnsEnum::getNom).collect(Collectors.joining(", "));
            throw new InvalidCSVFileNotificacioMassivaException(msg);
        }

        var fila = 1;
        for (var enviament : enviamentsCsv) {
            if (enviament.size() != headerCsv.size()) {
                throw new FilaDiferentSizeHeader(messageHelper.getMessage("notificacio.massiva.fila.mida.diferent.header", new Object[]{fila}));
            }
            fila++;
        }
    }

    public boolean checkMissingColumnsFileColumns() {
        long countNeededColumns = misingColumns.stream()
                .filter(column -> column.equals(MassivaColumnsEnum.FITXER_NOM) ||
                        column.equals(MassivaColumnsEnum.FITXER_UUID) ||
                        column.equals(MassivaColumnsEnum.FITXER_CSV))
                .count();
        return countNeededColumns <= 2 && misingColumns.size() == countNeededColumns;
    }

    private Long getMaximEnviaments() {

        return configHelper.getConfigAsLong("es.caib.notib.massives.maxim.files", MAX_ENVIAMENTS);
    }

    private boolean getRequereixMetadades() {
        return configHelper.getConfigAsBoolean("es.caib.notib.plugin.registre.documents.enviar", false);
    }

}
