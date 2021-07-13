package es.caib.notib.core.service;

import com.codahale.metrics.Timer;
import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.api.dto.notificacio.*;
import es.caib.notib.core.api.dto.organisme.OrganGestorDto;
import es.caib.notib.core.api.exception.*;
import es.caib.notib.core.api.service.NotificacioMassivaService;
import es.caib.notib.core.api.ws.notificacio.OrigenEnum;
import es.caib.notib.core.api.ws.notificacio.TipusDocumentalEnum;
import es.caib.notib.core.api.ws.notificacio.ValidesaEnum;
import es.caib.notib.core.entity.*;
import es.caib.notib.core.exception.DocumentNotFoundException;
import es.caib.notib.core.helper.*;
import es.caib.notib.core.repository.NotificacioMassivaRepository;
import es.caib.notib.core.repository.NotificacioTableViewRepository;
import es.caib.notib.core.repository.PagadorPostalRepository;
import es.caib.notib.core.repository.ProcedimentRepository;
import es.caib.notib.core.utils.CSVReader;
import es.caib.notib.core.utils.ZipFileUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

import javax.annotation.Resource;
import java.io.*;
import java.net.URLConnection;
import java.nio.file.NoSuchFileException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementació del servei de gestió de notificacions.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Service
public class NotificacioMassivaServiceImpl implements NotificacioMassivaService {
    @Autowired
    private EntityComprovarHelper entityComprovarHelper;
    @Autowired
    private ConversioTipusHelper conversioTipusHelper;
    @Autowired
    private PluginHelper pluginHelper;
    @Autowired
    private RegistreNotificaHelper registreNotificaHelper;
    @Resource
    private MetricsHelper metricsHelper;
    @Autowired
    private NotificacioHelper notificacioHelper;
    @Autowired
    private EmailNotificacioMassivaHelper emailNotificacioMassivaHelper;
    @Autowired
    private PaginacioHelper paginacioHelper;
    @Autowired
    private NotificacioValidatorHelper notificacioValidatorHelper;
    @Autowired
    private NotificacioMassivaHelper notificacioMassivaHelper;

    @Autowired
    private ProcedimentRepository procedimentRepository;
    @Autowired
    private PagadorPostalRepository pagadorPostalRepository;
    @Autowired
    private NotificacioMassivaRepository notificacioMassivaRepository;
    @Autowired
    private NotificacioTableViewRepository notificacioTableViewRepository;
    @Autowired
    private NotificacioListHelper notificacioListHelper;
    @Autowired
    private ConfigHelper configHelper;

    private static final int MAX_ENVIAMENTS = 999;

    @Override
    public NotificacioMassivaDataDto findById(Long entitatId, Long id) {
        entityComprovarHelper.comprovarEntitat(entitatId);
        NotificacioMassivaEntity notificacioMassiva = notificacioMassivaRepository.findOne(id);
        return conversioTipusHelper.convertir(notificacioMassiva, NotificacioMassivaDataDto.class);
    }

    @Override
    public NotificacioMassivaInfoDto getNotificacioMassivaInfo(Long entitatId, Long notificacioMassivaId) {
        entityComprovarHelper.comprovarEntitat(entitatId);
        NotificacioMassivaEntity notificacioMassiva = notificacioMassivaRepository.findOne(notificacioMassivaId);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        pluginHelper.gestioDocumentalGet(
                notificacioMassiva.getResumGesdocId(),
                PluginHelper.GESDOC_AGRUPACIO_MASSIUS_INFORMES,
                baos);
        List<String[]> linies = CSVReader.readFile(baos.toByteArray());
        List<NotificacioMassivaInfoDto.NotificacioInfo> info = new ArrayList<>();
        for (String[] linea : linies) {
            NotificacioMassivaInfoDto.NotificacioInfo.NotificacioInfoBuilder builder =
                    NotificacioMassivaInfoDto.NotificacioInfo.builder()
                        .codiDir3UnidadRemisora(linea[0])
                        .concepto(linea[1])
                        .enviamentTipus(linea[2])
                        .referenciaEmisor(linea[3])
                        .nombreFichero(linea[4])
                        .normalizado(linea[5])
                        .prioridadServicio(linea[6])
                        .nombre(linea[7])
                        .apellidos(linea[8])
                        .cifNif(linea[9])
                        .email(linea[10])
                        .codigoDestino(linea[11])
                        .linea1(linea[12])
                        .linea2(linea[13])
                        .codigoPostal(linea[14])
                        .retardoPostal(linea[15])
                        .codigoProcedimiento(linea[16])
                        .fechaEnvioProgramado(linea[17]);

            if (linea.length >=23) { // si hi ha les metadades
                builder.origen(linea[18])
                       .estadoElaboracion(linea[19])
                       .tipoDocumental(linea[20])
                       .pdfFirmado(linea[21])
                       .errores(linea[22]);
            } else {
                builder.errores(linea[18]);
            }

            info.add(builder.build());
        }
        NotificacioMassivaInfoDto dto = conversioTipusHelper.convertir(notificacioMassiva, NotificacioMassivaInfoDto.class);
        dto.setSummary(info);
        return dto;
    }

    @Override
    public PaginaDto<NotificacioTableItemDto> findNotificacions(
            Long entitatId,
            Long notificacioMassivaId,
            NotificacioFiltreDto filtre,
            PaginacioParamsDto paginacioParams) {
        EntitatEntity entitatActual = entityComprovarHelper.comprovarEntitat(
                entitatId,
                false,
                false,
                false);

        Pageable pageable = notificacioListHelper.getMappeigPropietats(paginacioParams);

        NotificacioListHelper.NotificacioFiltre filtreNetejat = notificacioListHelper.getFiltre(filtre);

        Page<NotificacioTableEntity> notificacions = notificacioTableViewRepository.findAmbFiltreByNotificacioMassiva(
                filtreNetejat.getEntitatId().isNull(),
                filtreNetejat.getEntitatId().getField(),
                notificacioMassivaRepository.findOne(notificacioMassivaId),
                filtreNetejat.getEnviamentTipus().isNull(),
                filtreNetejat.getEnviamentTipus().getField(),
                filtreNetejat.getConcepte().isNull(),
                filtreNetejat.getConcepte().getField(),
                filtreNetejat.getEstat().isNull(),
                filtreNetejat.getEstat().getField(),
                !filtreNetejat.getEstat().isNull() ?
                        NotificacioEnviamentEstatEnumDto.valueOf(filtreNetejat.getEstat().getField().toString()) : null,
                filtreNetejat.getDataInici().isNull(),
                filtreNetejat.getDataInici().getField(),
                filtreNetejat.getDataFi().isNull(),
                filtreNetejat.getDataFi().getField(),
                filtreNetejat.getTitular().isNull(),
                filtreNetejat.getTitular().isNull() ? "" : filtreNetejat.getTitular().getField(),
                filtreNetejat.getOrganGestor().isNull(),
                filtreNetejat.getOrganGestor().isNull() ? "" : filtreNetejat.getOrganGestor().getField().getCodi(),
                filtreNetejat.getProcediment().isNull(),
                filtreNetejat.getProcediment().isNull() ? "" : filtreNetejat.getProcediment().getField().getCodi(),
                filtreNetejat.getTipusUsuari().isNull(),
                filtreNetejat.getTipusUsuari().getField(),
                filtreNetejat.getNumExpedient().isNull(),
                filtreNetejat.getNumExpedient().getField(),
                filtreNetejat.getCreadaPer().isNull(),
                filtreNetejat.getCreadaPer().getField(),
                filtreNetejat.getIdentificador().isNull(),
                filtreNetejat.getIdentificador().getField(),
                filtreNetejat.getNomesAmbErrors().getField(),
                filtreNetejat.getNomesSenseErrors().getField(),
                filtreNetejat.getHasZeronotificaEnviamentIntent().isNull(),
                filtreNetejat.getHasZeronotificaEnviamentIntent().getField(),
                pageable);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return notificacioListHelper.complementaNotificacions(entitatActual, auth.getName(), notificacions);
    }

    @Transactional(rollbackFor=Exception.class)
    @Override
    public NotificacioMassivaDataDto create(
            Long entitatId,
            @NonNull String usuariCodi,
            @NonNull NotificacioMassivaDto notificacioMassiva) throws RegistreNotificaException {

        Timer.Context timer = metricsHelper.iniciMetrica();
        try {
            log.info("[NOT-MASSIVA] Alta de nova notificacio massiva (usuari: {}). Fitxer csv: {}",
                    usuariCodi, notificacioMassiva.getFicheroCsvNom());

            EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);

            List<String> csvHeader = CSVReader.readHeader(notificacioMassiva.getFicheroCsvBytes());
            List<String[]> linies = CSVReader.readFile(notificacioMassiva.getFicheroCsvBytes());
            checkCSVContent(linies, csvHeader);

            List<String> fileNames = ZipFileUtils.readZipFileNames(notificacioMassiva.getFicheroZipBytes());
            Map<String, Long> documentsProcessatsMassiu = new HashMap<String, Long>(); // key: csv/uuid/arxiuFisicoNom - value: documentEntity.getId()

            StringWriter writerListErrors = new StringWriter();
            StringWriter writerListInforme = new StringWriter();

            csvHeader.add("Errores");
            ICsvListWriter listWriterErrors = initCsvWritter(writerListErrors);
            ICsvListWriter listWriterInforme = initCsvWritter(writerListInforme);
            writeCsvHeader(listWriterErrors, csvHeader.toArray(new String[]{}));
            writeCsvHeader(listWriterInforme, csvHeader.toArray(new String[]{}));

            int numAltes = 0;
            NotificacioMassivaEntity notificacioMassivaEntity = registrarNotificacioMassiva(entitat, notificacioMassiva);
            for (String[] linia : linies) {
                if (linia.length < numberRequiredColumns()) {
                    break;
                }
                NotificacioDatabaseDto notificacio = csvToNotificaDatabaseDto(
                        linia,
                        notificacioMassiva.getCaducitat(),
                        entitat,
                        usuariCodi,
                        fileNames,
                        notificacioMassiva.getFicheroZipBytes(),
                        documentsProcessatsMassiu);
                String keyDocument = getKeyDocument(notificacio);
                if (keyDocument != null && !documentsProcessatsMassiu.containsKey(keyDocument)) {
                    documentsProcessatsMassiu.put(keyDocument, null);
                }

                List<String> errors = notificacioValidatorHelper.validarNotificacioMassiu(
                        notificacio, entitat,
                        documentsProcessatsMassiu);
                try {
                    ProcedimentEntity procediment = procedimentRepository.findByCodiAndEntitat(notificacio.getProcediment().getCodi(), entitat);
                    if (procediment == null) {
                        errors.add("[1330] No s'ha trobat cap procediment amb el codi indicat.");
                    } else {
                        notificacio.setProcediment(conversioTipusHelper.convertir(procediment, ProcedimentDto.class));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    errors.add(String.format("[1331] Error obtenint el procediment amb el codi %s.", notificacio.getProcediment().getCodi()));
                }


                if (errors.size() == 0) {
                    try {
                        crearNotificacio(entitat, notificacio, notificacioMassivaEntity, documentsProcessatsMassiu);
                    } catch (DocumentNotFoundException|NoDocumentException ex) {
                        errors.add("[1064] No s'ha pogut obtenir el document de l'arxiu.");
                    } catch (NoMetadadesException ex) {
                        errors.add("[1066] Error en les metadades del document. No s'han obtingut de la consulta a l'arxiu ni de el fitxer CSV de càrrega.");
                    }
                }

                if (errors.size() > 0) {
                    log.debug("[NOT-MASSIVA] Alta errònea de la notificació de la nova notificacio massiva");
                    writeCsvLinia(listWriterErrors,linia, errors);
                    writeCsvLinia(listWriterInforme,linia, errors);
                } else {
                    log.debug("[NOT-MASSIVA] Alta satisfactoria de la notificació de la nova notificacio massiva");
                    List<String> ok = Collections.singletonList("OK");
                    writeCsvLinia(listWriterInforme,linia, ok);
                    numAltes++;
                }
            }
            if (numAltes == 0) {
                notificacioMassivaEntity.updateProgress(-1);
            }

            try {
                listWriterInforme.flush();
                listWriterErrors.flush();
                byte[] fileResumContent = writerListInforme.toString().getBytes();
                byte[] fileErrorsContent = writerListErrors.toString().getBytes();
                pluginHelper.gestioDocumentalUpdate(
                        notificacioMassivaEntity.getErrorsGesdocId(),
                        PluginHelper.GESDOC_AGRUPACIO_MASSIUS_ERRORS,
                        fileErrorsContent);
                pluginHelper.gestioDocumentalUpdate(
                        notificacioMassivaEntity.getResumGesdocId(),
                        PluginHelper.GESDOC_AGRUPACIO_MASSIUS_INFORMES,
                        fileResumContent);

                enviarCorreuElectronic(notificacioMassivaEntity, fileResumContent, fileErrorsContent);
            } catch (IOException e) {
                log.error("[NOT-MASSIVA] Hi ha hagut un error al intentar guardar els documents de l'informe i del error.");
                e.printStackTrace();
            } catch (Exception e) {
                log.error("[NOT-MASSIVA] Hi ha hagut un error al intentar enviar el correu electrònic.");
            }

            writeCsvClose(listWriterErrors);
            writeCsvClose(listWriterInforme);

            if (getPrioritatNotificacioMassiva().equals(NotificacioMassivaPrioritatDto.BAIXA)) {
                notificacioMassivaHelper.posposarNotificacions(notificacioMassivaEntity.getId());
            }

            return conversioTipusHelper.convertir(notificacioMassivaEntity, NotificacioMassivaDataDto.class);
        } finally {
            metricsHelper.fiMetrica(timer);
        }
    }
    @Override
    public void delete(
            Long entitatId,
            Long notificacioMassivaId) {
        entityComprovarHelper.comprovarEntitat(entitatId);
        NotificacioMassivaEntity notificacioMassiva = notificacioMassivaRepository.findOne(notificacioMassivaId);
        if (notificacioMassiva.getNotificacions().size() == 0) {
            notificacioMassivaRepository.delete(notificacioMassivaId);
        }
    }

    @Override
    public PaginaDto<NotificacioMassivaTableItemDto> findAmbFiltrePaginat(
            Long entitatId,
            NotificacioMassivaFiltreDto filtre,
            RolEnumDto rol,
            PaginacioParamsDto paginacioParams) {
        EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatId);

        Page<NotificacioMassivaEntity> pageNotificacionsMassives;
        if (RolEnumDto.tothom.equals(rol)){
            pageNotificacionsMassives = findAmbFiltrePaginatByUser(entitat, filtre, paginacioParams);
        } else if (RolEnumDto.NOT_ADMIN.equals(rol)){
            pageNotificacionsMassives = findAmbFiltrePaginatByAdminEntitat(entitat, filtre, paginacioParams);
        } else {
            throw new AccessDeniedException("Només es poden consultar les notificacions massives amb els rols " +
                    "d'usuari o d'administrador d'entitat");
        }

        return paginacioHelper.toPaginaDto(
                pageNotificacionsMassives,
                NotificacioMassivaTableItemDto.class);
    }

    @Transactional
    @Override
    public void posposar(Long entitatId, Long notificacioMassivaId) {
        entityComprovarHelper.comprovarEntitat(entitatId);
        notificacioMassivaHelper.posposarNotificacions(notificacioMassivaId);
    }

    @Transactional
    @Override
    public void reactivar(Long entitatId, Long notificacioMassivaId) {
        entityComprovarHelper.comprovarEntitat(entitatId);
        notificacioMassivaHelper.reactivarNotificacions(notificacioMassivaId);
    }

    @Override
    public FitxerDto getCSVFile(Long entitatId, Long notificacioMassivaId) {
        entityComprovarHelper.comprovarEntitat(entitatId);
        NotificacioMassivaEntity notificacioMassiva = notificacioMassivaRepository.findOne(notificacioMassivaId);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        pluginHelper.gestioDocumentalGet(
                notificacioMassiva.getCsvGesdocId(),
                PluginHelper.GESDOC_AGRUPACIO_MASSIUS_CSV,
                baos);
        FitxerDto fitxer = FitxerDto.builder()
                .nom(notificacioMassiva.getCsvFilename())
                .contentType("text")
                .contingut(baos.toByteArray())
                .tamany(baos.size())
                .build();
        return fitxer;
    }

    @Override
    public FitxerDto getZipFile(Long entitatId, Long notificacioMassivaId) {
        entityComprovarHelper.comprovarEntitat(entitatId);
        NotificacioMassivaEntity notificacioMassiva = notificacioMassivaRepository.findOne(notificacioMassivaId);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        pluginHelper.gestioDocumentalGet(
                notificacioMassiva.getZipGesdocId(),
                PluginHelper.GESDOC_AGRUPACIO_MASSIUS_ZIP,
                baos);
        FitxerDto fitxer = FitxerDto.builder()
                .nom(notificacioMassiva.getZipFilename())
                .contentType("text")
                .contingut(baos.toByteArray())
                .tamany(baos.size())
                .build();
        return fitxer;
    }

    @Override
    public FitxerDto getResumFile(Long entitatId, Long notificacioMassivaId) {
        entityComprovarHelper.comprovarEntitat(entitatId);
        NotificacioMassivaEntity notificacioMassiva = notificacioMassivaRepository.findOne(notificacioMassivaId);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        pluginHelper.gestioDocumentalGet(
                notificacioMassiva.getResumGesdocId(),
                PluginHelper.GESDOC_AGRUPACIO_MASSIUS_INFORMES,
                baos);
        FitxerDto fitxer = FitxerDto.builder()
                .nom("resum.csv")
                .contentType("text")
                .contingut(baos.toByteArray())
                .tamany(baos.size())
                .build();
        return fitxer;
    }

    @Override
    public FitxerDto getErrorsFile(Long entitatId, Long notificacioMassivaId) {
        entityComprovarHelper.comprovarEntitat(entitatId);
        NotificacioMassivaEntity notificacioMassiva = notificacioMassivaRepository.findOne(notificacioMassivaId);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        pluginHelper.gestioDocumentalGet(
                notificacioMassiva.getErrorsGesdocId(),
                PluginHelper.GESDOC_AGRUPACIO_MASSIUS_ERRORS,
                baos);
        FitxerDto fitxer = FitxerDto.builder()
                .nom("errors.csv")
                .contentType("text")
                .contingut(baos.toByteArray())
                .tamany(baos.size())
                .build();
        return fitxer;
    }

    @Transactional
    @Override
    public byte[] getModelDadesCarregaMassiuCSV() throws NoSuchFileException, IOException{
        Timer.Context timer = metricsHelper.iniciMetrica();
        try {
            InputStream input;
            if (registreNotificaHelper.isSendDocumentsActive()) {
                input = this.getClass().getClassLoader().getResourceAsStream("es/caib/notib/core/plantillas/modelo_datos_carga_masiva_metadades.csv");
            } else {
                input = this.getClass().getClassLoader().getResourceAsStream("es/caib/notib/core/plantillas/modelo_datos_carga_masiva.csv");
            }
            assert input != null;
            return IOUtils.toByteArray(input);
        } finally {
            metricsHelper.fiMetrica(timer);
        }
    }


    private void checkCSVContent(List<String[]> linies, List<String> csvHeader) {
        if (linies == null || csvHeader == null) {
            throw new InvalidCSVFileException("S'ha produït un error processant el fitxer CSV indicat.");
        }
        if (linies.isEmpty()) {
            throw new InvalidCSVFileNotificacioMassivaException("El fitxer CSV està buid.");
        }
        if (linies.size() > MAX_ENVIAMENTS) {
            log.debug(String.format("[NOT-MASSIVA] El fitxer CSV conté més de les %d línies permeses.", MAX_ENVIAMENTS));
            throw new MaxLinesExceededException(
                    String.format("S'ha superat el màxim nombre de línies permès (%d) per al CSV de càrrega massiva.", MAX_ENVIAMENTS));
        }
        if (csvHeader.size() < numberRequiredColumns()) {
            throw new InvalidCSVFileNotificacioMassivaException(
                    String.format("El fitxer CSV no conté totes les columnes necessaries. " +
                            "Nombre de columnes requerides: %d. Nombre de columnes trobades %d",
                            numberRequiredColumns(), csvHeader.size())
            );
        }

    }

    public int numberRequiredColumns() {
        if (registreNotificaHelper.isSendDocumentsActive()) {
            return 22;
        } else {
            return 18;
        }
    }

    private void crearNotificacio(EntitatEntity entitat,
                                  NotificacioDatabaseDto notificacio,
                                  NotificacioMassivaEntity notMassiva,
                                  Map<String, Long> documentsProcessatsMassiu) throws RegistreNotificaException {
        log.debug("[NOT-MASSIVA] Creació de notificació de nova notificacio massiva");
        NotificacioEntity notificacioEntity = notificacioHelper.saveNotificacio(entitat, notificacio,
                false,
                notMassiva,
                documentsProcessatsMassiu);

        log.debug("[NOT-MASSIVA] Alta notificació de nova notificacio massiva");
        notificacioHelper.altaNotificacioWeb(entitat, notificacioEntity, notificacio.getEnviaments());
        notMassiva.joinNotificacio(notificacioEntity);
    }

    private Page<NotificacioMassivaEntity> findAmbFiltrePaginatByUser(EntitatEntity entitat,
                                                                                 NotificacioMassivaFiltreDto filtre,
                                                                                 PaginacioParamsDto paginacioParams){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth.getName() == null) {
            throw new AccessDeniedException("No s'ha pogut consultar el llistat de notificacions massives");
        }
        Page<NotificacioMassivaEntity> pageNotificacionsMassives = notificacioMassivaRepository.findUserRolePage(
                entitat,
                auth.getName(),
                filtre.getDataInici() == null,
                filtre.getDataInici(),
                filtre.getDataFi() == null,
                filtre.getDataFi(),
                filtre.getEstat() == null,
                filtre.getEstat() == null ? "" : filtre.getEstat().name(),
                paginacioHelper.toSpringDataPageable(paginacioParams)
        );
        return pageNotificacionsMassives;
    }

    private Page<NotificacioMassivaEntity> findAmbFiltrePaginatByAdminEntitat(
            EntitatEntity entitat,
            NotificacioMassivaFiltreDto filtre,
            PaginacioParamsDto paginacioParams){

        Page<NotificacioMassivaEntity> pageNotificacionsMassives = notificacioMassivaRepository.findEntitatAdminRolePage(
                entitat,
                filtre.getDataInici() == null,
                filtre.getDataInici(),
                filtre.getDataFi() == null,
                filtre.getDataFi(),
                filtre.getEstat() == null,
                filtre.getEstat() == null ? "" : filtre.getEstat().name(),
                filtre.getCreatedByCodi() == null || filtre.getCreatedByCodi().isEmpty(),
                filtre.getCreatedByCodi(),
                paginacioHelper.toSpringDataPageable(paginacioParams)
        );
        return pageNotificacionsMassives;
    }

    private boolean enviarCorreuElectronic(NotificacioMassivaEntity notMassiva,
                                           @NonNull byte[] fileResumContent,
                                           @NonNull byte[] fileErrorsContent) throws Exception {
        if (notMassiva.getEmail() == null || notMassiva.getEmail().isEmpty()) {
            return false;
        }
        emailNotificacioMassivaHelper.sendMail(notMassiva, notMassiva.getEmail(),
                fileResumContent, fileErrorsContent);
        return true;
    }

    private NotificacioMassivaEntity registrarNotificacioMassiva(EntitatEntity entitat, NotificacioMassivaDto notMassivaDto) {
        String csvGesdocId = pluginHelper.gestioDocumentalCreate(PluginHelper.GESDOC_AGRUPACIO_MASSIUS_CSV,
                notMassivaDto.getFicheroCsvBytes());
        String zipGesdocId = pluginHelper.gestioDocumentalCreate(PluginHelper.GESDOC_AGRUPACIO_MASSIUS_ZIP,
                notMassivaDto.getFicheroZipBytes());
        String informeGesdocId = pluginHelper.gestioDocumentalCreate(PluginHelper.GESDOC_AGRUPACIO_MASSIUS_INFORMES,
                new byte[0]);
        String errorsGesdocId = pluginHelper.gestioDocumentalCreate(PluginHelper.GESDOC_AGRUPACIO_MASSIUS_ERRORS,
                new byte[0]);
        PagadorPostalEntity pagadorPostal = null;
        if (notMassivaDto.getPagadorPostalId() != null)
            pagadorPostal = pagadorPostalRepository.findOne(notMassivaDto.getPagadorPostalId());
        NotificacioMassivaEntity notMassiva =  NotificacioMassivaEntity.builder()
                .entitat(entitat)
                .csvGesdocId(csvGesdocId)
                .zipGesdocId(zipGesdocId)
                .zipFilename(notMassivaDto.getFicheroZipNom())
                .csvFilename(notMassivaDto.getFicheroCsvNom())
                .caducitat(notMassivaDto.getCaducitat())
                .email(notMassivaDto.getEmail())
                .pagadorPostal(pagadorPostal)
                .resumGesdocId(informeGesdocId)
                .errorsGesdocId(errorsGesdocId)
                .build();
        notificacioMassivaRepository.saveAndFlush(notMassiva);
        return notMassiva;
    }

    private String getKeyDocument(NotificacioDatabaseDto notificacio) {
        if (notificacio.getDocument() == null) {
            return null;
        } else if (notificacio.getDocument().getContingutBase64() != null && !notificacio.getDocument().getContingutBase64().isEmpty()) {//arxiu
            return notificacio.getDocument().getArxiuNom();
        } else if (notificacio.getDocument().getUuid() != null) {
            return notificacio.getDocument().getUuid();
        } else if (notificacio.getDocument().getCsv() != null) {
            return notificacio.getDocument().getCsv();
        }
        return null;
    }

    private NotificacioDatabaseDto csvToNotificaDatabaseDto(String[] linia, Date caducitat, EntitatEntity entitat,
                                                            String usuariCodi, List<String> fileNames, byte[] ficheroZipBytes,
                                                            Map<String, Long> documentsProcessatsMassiu) {

        log.debug("[NOT-MASSIVA] Construeix notificació de les dades del fitxer CSV");
        NotificacioDatabaseDto notificacio = new NotificacioDatabaseDto();
        NotificacioEnviamentDtoV2 enviament = new NotificacioEnviamentDtoV2();
        List<NotificacioEnviamentDtoV2> enviaments = new ArrayList<NotificacioEnviamentDtoV2>();
        DocumentDto document = new DocumentDto();

        notificacio.setCaducitat(caducitat);
        notificacio.setOrganGestorCodi(linia[0]);
        notificacio.setEmisorDir3Codi(entitat.getDir3Codi());
        notificacio.setConcepte(linia[1]);
        notificacio.setDescripcio(null);
        if (linia[2] !=null) {
            if (linia[2].toUpperCase(Locale.ROOT).charAt(0) == 'C')
                notificacio.setEnviamentTipus(NotificaEnviamentTipusEnumDto.COMUNICACIO);
            else {
                notificacio.setEnviamentTipus(NotificaEnviamentTipusEnumDto.NOTIFICACIO);
            }
        }
        notificacio.setGrup(null);
        notificacio.setIdioma(null);
        notificacio.setNumExpedient(null);
        notificacio.setUsuariCodi(usuariCodi);
        notificacio.setRetard(Integer.valueOf(linia[15]));

        // Procediment
        ProcedimentDto procediment = new ProcedimentDto();
        procediment.setCodi(linia[16]);
        notificacio.setProcediment(procediment);

        // Fecha envío programado
        try {//viene de CSV y es opcional pero NO sabemos formato
            if (linia[17] != null && !linia[17].isEmpty()) {
                notificacio.setEnviamentDataProgramada(new SimpleDateFormat("dd/MM/yyyy").parse(linia[17]));
            } else {
                notificacio.setEnviamentDataProgramada(null);
            }
        } catch (ParseException e) {
            notificacio.setEnviamentDataProgramada(null);
        }

        // Document
        if (linia[4] == null || linia[4].isEmpty()) {
            notificacio.setDocument(null);
        } else {
            if (fileNames.contains(linia[4])) { // Archivo físico
                document.setArxiuNom(linia[4]);
                byte[] arxiuBytes;
                if (documentsProcessatsMassiu.isEmpty() || !documentsProcessatsMassiu.containsKey(document.getArxiuNom()) ||
                        (documentsProcessatsMassiu.containsKey(document.getArxiuNom()) &&
                                documentsProcessatsMassiu.get(document.getArxiuNom()) == null)) {
                    arxiuBytes = ZipFileUtils.readZipFile(ficheroZipBytes, linia[4]);
                    document.setContingutBase64(Base64.encodeBase64String(arxiuBytes));
                    document.setNormalitzat("Si".equalsIgnoreCase(linia[5]));
                    document.setGenerarCsv(false);
                    document.setMediaType(URLConnection.guessContentTypeFromName(linia[4]));
                    document.setMida(Long.valueOf(arxiuBytes.length));
                    if (registreNotificaHelper.isSendDocumentsActive()) {
                        leerMetadadesDelCsv(document, linia);
                    }
                }
            } else {
                String[] docSplit = linia[4].split("\\.");
                if (docSplit.length > 1 && Arrays.asList("JPG", "JPEG", "ODT", "ODP", "ODS", "ODG", "DOCX", "XLSX", "PPTX",
                        "PDF", "PNG", "RTF", "SVG", "TIFF", "TXT", "XML", "XSIG", "CSIG", "HTML", "CSV").contains(docSplit[1].toUpperCase())) {
                    notificacio.setDocument(null);

                } else {
                    String uuidPattern = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-5][0-9a-f]{3}-[089ab][0-9a-f]{3}-[0-9a-f]{12}$";
                    Pattern pUuid = Pattern.compile(uuidPattern);
                    Matcher mUuid = pUuid.matcher(linia[4]);
                    if (mUuid.matches()) {
                        // Uuid
                        document.setUuid(linia[4]);
                        document.setNormalitzat("Si".equalsIgnoreCase(linia[5]));
                        document.setGenerarCsv(false);
                        if (registreNotificaHelper.isSendDocumentsActive()) {
                            leerMetadadesDelCsv(document, linia);
                        }
                    } else {
                        // Csv
                        document.setCsv(linia[4]);
                        document.setNormalitzat("Si".equalsIgnoreCase(linia[5]));
                        document.setGenerarCsv(false);
                        if (registreNotificaHelper.isSendDocumentsActive()) {
                            leerMetadadesDelCsv(document, linia);
                        }
                    }
                }
            }
            notificacio.setDocument(document);
        }

        // Enviaments
        enviament.setNotificaReferencia((linia[3] != null && !linia[3].isEmpty()) ? linia[3] : null); //si no se envía, Notific@ genera una
        enviament.setEntregaDehActiva(false); // De momento dejamos false

        if (entitat.isAmbEntregaCie() && linia[12] != null && !linia[12].isEmpty() && // Si vienen Línea 1 y Código Postal
                linia[14] != null && !linia[14].isEmpty()) {
            enviament.setEntregaPostalActiva(true);
            EntregaPostalDto entregaPostal = new EntregaPostalDto();
            entregaPostal.setActiva(true);
            entregaPostal.setLinea1(linia[12]);
            entregaPostal.setLinea2(linia[13]);
            entregaPostal.setCodiPostal(linia[14]);
            entregaPostal.setTipus(NotificaDomiciliConcretTipusEnumDto.SENSE_NORMALITZAR);
            enviament.setEntregaPostal(entregaPostal);
        } else {
            enviament.setEntregaPostalActiva(false);
        }


        enviament.setServeiTipus((linia[6] != null && !linia[6].isEmpty()) ?
                ServeiTipusEnumDto.valueOf(linia[6].trim().toUpperCase()) : ServeiTipusEnumDto.NORMAL);

        PersonaDto titular = new PersonaDto();
        titular.setNom(linia[7]);
        titular.setLlinatge1(linia[8]); // vienen ap1 y ap2 juntos
        titular.setLlinatge2(null);
        titular.setNif(linia[9]);
        titular.setEmail(linia[10]);
        titular.setDir3Codi(linia[11]);
        titular.setIncapacitat(false);
        // TODO:  Igual lo hemos planteado mal. Si es un nif, podria ser el Nif de la administración.
        //Entiendo que el "Código destino" = linia[11] solo se informará en caso de ser una administración
        //Si es persona física o jurídica no tiene sentido
        //Entonces podriamos utilizar este campo para saber si es una administración
        if (NifHelper.isValidCif(linia[9])) {
            titular.setInteressatTipus(InteressatTipusEnumDto.JURIDICA);
        } else if (NifHelper.isValidNifNie(linia[9])) {
            titular.setInteressatTipus(InteressatTipusEnumDto.FISICA);
        } else {
            List<OrganGestorDto> lista = pluginHelper.unitatsPerCodi(linia[9]);
            if (lista != null && lista.size() > 0) {
                titular.setInteressatTipus(InteressatTipusEnumDto.ADMINISTRACIO);
            }
        }
        enviament.setTitular(titular);
        enviaments.add(enviament);
        notificacio.setEnviaments(enviaments);


        return notificacio;
    }

    private void leerMetadadesDelCsv(DocumentDto document, String[] linia) {
        document.setOrigen((linia[18] != null && !linia[18].isEmpty()) ?
                OrigenEnum.valueOf(linia[18].trim().toUpperCase()): null);
        document.setValidesa((linia[19] != null && !linia[19].isEmpty()) ?
                ValidesaEnum.valueOf(linia[19].trim().toUpperCase()) : null);
        document.setTipoDocumental((linia[20] != null && !linia[20].isEmpty()) ?
                TipusDocumentalEnum.valueOf(linia[20].trim().toUpperCase()) : null);
        document.setModoFirma((linia[21] != null && !linia[21].isEmpty()) ?
                Boolean.valueOf(linia[21]) : Boolean.FALSE);
    }

    private ICsvListWriter initCsvWritter(Writer writer)
    {
        return new CsvListWriter(writer,
                CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE);
    }
    private ICsvListWriter writeCsvHeader(ICsvListWriter listWriter, String[] csvHeader) {
        try {
            listWriter.writeHeader(csvHeader);
            return listWriter;
        } catch (IOException e) {
            log.error("S'ha produït un error a l'escriure la capçalera de l'fitxer CSV.", e);
            throw new WriteCsvException("No s'ha pogut escriure la capçalera de l'fitxer CSV.");
        }
    }

    private void writeCsvLinia(ICsvListWriter listWriter, String[] linia, List<String> errors) {

        List<String> liniaAmbErrors = new ArrayList<>(Arrays.asList(linia));

        StringBuffer sbErrors = new StringBuffer();
        for (String error : errors) {
            sbErrors.append(error);
        }

        liniaAmbErrors.add(sbErrors.toString());

        try {
            listWriter.write(liniaAmbErrors);
        } catch (IOException e) {
            log.error("S'ha produït un error a l'escriure la línia en el fitxer CSV.", e);
            throw new WriteCsvException("No s'ha pogut escriure la línia en el fitxer CSV.");
        }
    }

    private void writeCsvClose(ICsvListWriter listWriter) {
        try {
            if( listWriter != null ) {
                listWriter.close();
            }
        } catch (IOException e) {
            log.error("S'ha produït un error a l'tancar el fitxer CSV.", e);
            throw new WriteCsvException("No s'ha pogut tancar el fitxer CSV.");
        }
    }


    private NotificacioMassivaPrioritatDto getPrioritatNotificacioMassiva(){
        NotificacioMassivaPrioritatDto tipus = NotificacioMassivaPrioritatDto.BAIXA;

        try {
            String tipusStr = configHelper.getConfig("es.caib.notib.enviament.massiu.prioritat");
            if (tipusStr != null && !tipusStr.isEmpty())
                tipus = NotificacioMassivaPrioritatDto.valueOf(tipusStr);
        } catch (Exception ex) {
            log.error("No s'ha pogut obtenir la prioritat de la notificació massiva per defecte. S'utilitzarà la BAIXA.");
        }

        return tipus;
    }
}
