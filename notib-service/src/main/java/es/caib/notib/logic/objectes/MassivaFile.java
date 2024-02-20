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

    public MassivaFile(
            ConfigHelper configHelper,
            MessageHelper messageHelper,
            PluginHelper pluginHelper) {

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

    public void initCreate(
            NotificacioMassivaDto notificacioMassiva,
            EntitatEntity entitat,
            String usuari) {
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

    public void initInfo(
            byte[] contingutCsv,
            List<NotificacioEntity> notificacions,
            NotificacioEventRepository notificacioEventRepository) {
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
        for (List<String> enviamentCsv: enviamentsCsv) {
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

            NotificacioInfo enviamentInfo = NotificacioInfo.builder()
                    .codiDir3UnidadRemisora(enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.UNITAT_EMISORA)))
                    .concepto(enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.CONCEPTE)))
                    .enviamentTipus(enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.TIPUS_ENV)))
                    .referenciaEmisor(enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.REF_EMISOR)))
                    .nombreFichero(enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.FITXER_NOM)))
                    .uuidFichero(enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.FITXER_UUID)))
                    .csvFichero(enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.FITXER_CSV)))
                    .normalizado(enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.FITXER_NORMAL)))
                    .prioridadServicio(enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.PRIORITAT)))
                    .nombre(enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.DEST_NOM)))
                    .apellidos(enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.DEST_LLINATGES)))
                    .cifNif(enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.DEST_DOC)))
                    .email(enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.DEST_EMAIL)))
                    .codigoDestino(enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.UNITAT_DESTI)))
                    .linea1(enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.ADDR_LIN1)))
                    .linea2(enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.ADDR_LIN2)))
                    .codigoPostal(enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.ADDR_CP)))
                    .retardoPostal(enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.RETARD)))
                    .codigoProcedimiento(enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.PROCEDIMENT)))
                    .fechaEnvioProgramado(enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.DATA_PROG)))
                    .descripcio(enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.DESCRIPCIO)))
                    .origen(enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.META_ORIGEN)))
                    .estadoElaboracion(enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.META_ESTAT_ELAB)))
                    .tipoDocumental(enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.META_TIPUS_DOC)))
                    .pdfFirmado(enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.META_FIRMAT)))
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
        for (List<String> enviamentCsv: enviamentsCsv) {
            InteressatTipus interessatTipus = getInteressatTipus(
                    enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.DEST_DOC)),
                    enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.DEST_EMAIL)),
                    enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.UNITAT_DESTI)));
            boolean perEmail = InteressatTipus.FISICA_SENSE_NIF.equals(interessatTipus);
            EntregaPostal entregaPostal = null;
            if (!perEmail) {
                var linia1 = enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.ADDR_LIN1));
                var linia2 = enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.ADDR_LIN2));
                var cp = enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.ADDR_CP));
                if (entitat.getEntregaCie() != null && !Strings.isNullOrEmpty(linia1) && !Strings.isNullOrEmpty(cp)) {
                    entregaPostal = EntregaPostal.builder()
                            .tipus(NotificaDomiciliConcretTipus.SENSE_NORMALITZAR)
                            .linea1(linia1)
                            .linea2(linia2)
                            .codiPostal(cp)
                            .build();
                }
            }
            Persona titular = Persona.builder()
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
            Enviament enviament = Enviament.builder()
                    .serveiTipus(getServeiTipus(enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.PRIORITAT))))
                    .entregaDehActiva(false)
                    .entregaPostalActiva(entregaPostal != null)
                    .entregaPostal(entregaPostal)
                    .titular(titular)
                    .perEmail(perEmail)
                    .build();
            Notificacio notificacio = Notificacio.builder()
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
        if (Strings.isNullOrEmpty(numDocument) && Strings.isNullOrEmpty(email)) {
            return null;
        }
        if (NifHelper.isValidCif(numDocument)) {
            return InteressatTipus.JURIDICA;
        }
        if (NifHelper.isValidNifNie(numDocument)) {
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

    private Map<String, Document> getDocuments(byte[] contingutZip) {
        Map<String, Document> documentMap = new HashMap<>();
        boolean requereixMetadades = getRequereixMetadades();
        var fileNames = ZipFileUtils.readZipFileNames(contingutZip);

        for (List<String> enviamentCsv: enviamentsCsv) {
            Document document = null;

            var fitxerNomIndex = headerColumns.get(MassivaColumnsEnum.FITXER_NOM);
            var fitxerUuidIndex = headerColumns.get(MassivaColumnsEnum.FITXER_UUID);
            var fitxerCsvIndex = headerColumns.get(MassivaColumnsEnum.FITXER_CSV);
            var documentNom = fitxerNomIndex != null ? enviamentCsv.get(fitxerNomIndex) : null;
            var documentUuid = fitxerUuidIndex != null ? enviamentCsv.get(fitxerUuidIndex) : null;
            var documentCsv = fitxerCsvIndex != null ? enviamentCsv.get(fitxerCsvIndex) : null;
            var normalitzat = getBoolea(enviamentCsv.get(headerColumns.get(MassivaColumnsEnum.FITXER_NORMAL)));

            // Document físic
            if (!Strings.isNullOrEmpty(documentNom)) {
                var arxiuNom = fileNames.stream().filter(nom -> nom.equals(documentNom)).findFirst().orElse(null);
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
//        var columnes = new HashMap<MassivaColumnsEnum, Integer>();
//        IntStream.range(0, header.size()).forEach(idx -> {
//            MassivaColumnsEnum columna = MassivaColumnsEnum.fromNom(header.get(idx));
//            if (columna != null) {
//                columnes.put(columna, idx);
//            }
//        });
//        return columnes;

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
            throw new InvalidCSVFileException("S'ha produït un error processant el fitxer CSV indicat: sense contingut");
        }
        if (enviamentsCsv.isEmpty()) {
            throw new InvalidCSVFileNotificacioMassivaException("El fitxer CSV està buid.");
        }
        var maxEnviaments = getMaximEnviaments();
        if (enviamentsCsv.size() > maxEnviaments) {
            log.debug(String.format("[NOT-MASSIVA] El fitxer CSV conté més de les %d línies permeses.", maxEnviaments));
            throw new MaxLinesExceededException(String.format("S'ha superat el màxim nombre de línies permès (%d) per al CSV de càrrega massiva.", maxEnviaments));
        }

        if (!misingColumns.isEmpty() && !checkMissingColumnsFileColumns()) {
            var msg = "El fitxer CSV no conté totes les columnes necessaries. Columnes que falten: " +
                    getMissingColumns().stream().map(MassivaColumnsEnum::getNom).collect(Collectors.joining(", "));
            throw new InvalidCSVFileNotificacioMassivaException(msg);
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

//    private ICsvListWriter writeCsvHeader(ICsvListWriter listWriter) {
//
//        var csvHeader = new ArrayList<>(headerCsv);
//        csvHeader.add("Errores");
//
//        try {
//            listWriter.writeHeader(csvHeader.toArray(new String[0]));
//            return listWriter;
//        } catch (IOException e) {
//            log.error("S'ha produït un error a l'escriure la capçalera de l'fitxer CSV.", e);
//            throw new WriteCsvException(messageHelper.getMessage("error.escriure.capcalera.fitxer.csv"));
//        }
//    }
//
//    private void writeCsvLinia(ICsvListWriter listWriter, List<String> linia, List<String> errors) {
//
//        var liniaAmbErrors = new ArrayList<>(linia);
//        if (errors != null && !errors.isEmpty()) {
//            liniaAmbErrors.add(errors.stream().collect(Collectors.joining(", ")));
//        }
//
//        try {
//            listWriter.write(liniaAmbErrors);
//        } catch (IOException e) {
//            log.error("S'ha produït un error a l'escriure la línia en el fitxer CSV.", e);
//            throw new WriteCsvException(messageHelper.getMessage("error.escriure.linia.fitxer.csv"));
//        }
//    }

    private Long getMaximEnviaments() {
        return configHelper.getConfigAsLong("es.caib.notib.massives.maxim.files", MAX_ENVIAMENTS);
    }
    private boolean getRequereixMetadades() {
        return configHelper.getConfigAsBoolean("es.caib.notib.plugin.registre.documents.enviar", false);
    }

}
