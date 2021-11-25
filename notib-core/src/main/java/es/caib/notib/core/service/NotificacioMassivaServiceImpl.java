package es.caib.notib.core.service;

import com.codahale.metrics.Timer;
import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.api.dto.cie.EntregaPostalDto;
import es.caib.notib.core.api.dto.notenviament.NotEnviamentDatabaseDto;
import es.caib.notib.core.api.dto.notificacio.*;
import es.caib.notib.core.api.dto.organisme.OrganGestorDto;
import es.caib.notib.core.api.dto.procediment.ProcSerDto;
import es.caib.notib.core.api.exception.*;
import es.caib.notib.core.api.service.NotificacioMassivaService;
import es.caib.notib.core.api.ws.notificacio.OrigenEnum;
import es.caib.notib.core.api.ws.notificacio.TipusDocumentalEnum;
import es.caib.notib.core.api.ws.notificacio.ValidesaEnum;
import es.caib.notib.core.entity.*;
import es.caib.notib.core.entity.cie.PagadorPostalEntity;
import es.caib.notib.core.exception.DocumentNotFoundException;
import es.caib.notib.core.helper.*;
import es.caib.notib.core.repository.*;
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
    private ProcSerRepository procSerRepository;
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

    @Transactional(rollbackFor=Exception.class, timeout = 900)
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
            int fila = 1;
            NotificacioMassivaEntity notificacioMassivaEntity = registrarNotificacioMassiva(entitat, notificacioMassiva, linies.size());
            for (String[] linia : linies) {
                if (linia.length < numberRequiredColumns()) {
                    break;
                }
                linia = trim(linia);
                NotificacioDatabaseDto notificacio = csvToNotificaDatabaseDto(
                        linia,
                        notificacioMassiva.getCaducitat(),
                        entitat,
                        usuariCodi,
                        fileNames,
                        notificacioMassiva.getFicheroZipBytes(),
                        documentsProcessatsMassiu,
                        fila++);
                String keyDocument = getKeyDocument(notificacio);
                if (keyDocument != null && !documentsProcessatsMassiu.containsKey(keyDocument)) {
                    documentsProcessatsMassiu.put(keyDocument, null);
                }

                List<String> errors = notificacioValidatorHelper.validarNotificacioMassiu(
                        notificacio,
                        entitat,
                        documentsProcessatsMassiu);
                try {
                    ProcSerEntity procediment = procSerRepository.findByCodiAndEntitat(
                            notificacio.getProcediment().getCodi(),
                            entitat);
                    if (procediment == null) {
                        errors.add("[1330] No s'ha trobat cap procediment/servei amb el codi indicat.");
                    } else if (ProcSerTipusEnum.SERVEI.equals(procediment.getTipus()) && NotificaEnviamentTipusEnumDto.NOTIFICACIO.equals(notificacio.getEnviamentTipus())) {
                        errors.add("[1331] No es pot donar d'alta una notificació amb un servei. Els serveis només s'admeten en comunicacions.");
                    } else {
                        notificacio.setProcediment(conversioTipusHelper.convertir(procediment, ProcSerDto.class));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    errors.add(String.format("[1332] Error obtenint el procediment amb el codi %s.", notificacio.getProcediment().getCodi()));
                }


                if (errors.size() == 0) {
                    try {
                        crearNotificacio(entitat, notificacio, notificacioMassivaEntity, documentsProcessatsMassiu);
                    } catch (DocumentNotFoundException | NoDocumentException ex) {
                        errors.add("[1064] No s'ha pogut obtenir el document de l'arxiu.");
                    } catch (NoMetadadesException ex) {
                        errors.add("[1066] Error en les metadades del document. No s'han obtingut de la consulta a l'arxiu ni de el fitxer CSV de càrrega.");
                    }
                }

                if (errors.size() > 0) {
                    log.debug("[NOT-MASSIVA] Alta errònea de la notificació de la nova notificacio massiva");
                    writeCsvLinia(listWriterErrors, linia, errors);
                    writeCsvLinia(listWriterInforme, linia, errors);
                } else {
                    log.debug("[NOT-MASSIVA] Alta satisfactoria de la notificació de la nova notificacio massiva");
                    List<String> ok = Collections.singletonList("OK");
                    writeCsvLinia(listWriterInforme, linia, ok);
                    numAltes++;
                }
            }
            notificacioMassivaEntity.updateEstatValidacio(numAltes);

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
                log.error("[NOT-MASSIVA] Hi ha hagut un error al intentar guardar els documents de l'informe i del error.", e);
            } catch (Exception | Error e) {
                log.error("[NOT-MASSIVA] Hi ha hagut un error al intentar enviar el correu electrònic.", e);
            }

            writeCsvClose(listWriterErrors);
            writeCsvClose(listWriterInforme);

            if (getPrioritatNotificacioMassiva().equals(NotificacioMassivaPrioritatDto.BAIXA)) {
                notificacioMassivaHelper.posposarNotificacions(notificacioMassivaEntity.getId());
            }

            return conversioTipusHelper.convertir(notificacioMassivaEntity, NotificacioMassivaDataDto.class);
        } catch (Throwable t) {
            log.error("[NOT-MASSIVA] Error no controlat en l'enviament massiu", t);
            throw t;
        } finally {
            metricsHelper.fiMetrica(timer);
        }
    }

    private String[] trim(String[] linia) {
        for(int i = 0; i < linia.length; i++) {
            String camp = linia[i] != null ? linia[i].trim() : null;
            if (camp != null && camp.isEmpty()) {
                camp = null;
            }
            linia[i] = camp;
        }
        return linia;
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
            throw new InvalidCSVFileException("S'ha produït un error processant el fitxer CSV indicat: sense contingut");
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
        notificacioHelper.altaEnviamentsWeb(entitat, notificacioEntity, notificacio.getEnviaments());
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
                filtre.getEstatProces() == null,
                filtre.getEstatProces(),
//                filtre.getEstatProces() == null ? "" : filtre.getEstatProces().name(),
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
                filtre.getEstatProces() == null,
                filtre.getEstatProces(),
//                filtre.getEstatProces() == null ? "" : filtre.getEstatProces().name(),
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

    private NotificacioMassivaEntity registrarNotificacioMassiva(EntitatEntity entitat, NotificacioMassivaDto notMassivaDto, int size) {
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
                .estatValidacio(NotificacioMassivaEstatDto.PENDENT)
                .estatProces(NotificacioMassivaEstatDto.PENDENT)
                .totalNotificacions(size)
                .notificacionsValidades(0)
                .notificacionsProcessades(0)
                .notificacionsProcessadesAmbError(0)
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

    private NotificacioDatabaseDto csvToNotificaDatabaseDto(
            String[] linia,
            Date caducitat,
            EntitatEntity entitat,
            String usuariCodi,
            List<String> fileNames,
            byte[] ficheroZipBytes,
            Map<String, Long> documentsProcessatsMassiu,
            Integer fila) {

        log.debug("[NOT-MASSIVA] Construeix notificació de les dades del fitxer CSV");
        NotificacioDatabaseDto notificacio = new NotificacioDatabaseDto();
        NotEnviamentDatabaseDto enviament = new NotEnviamentDatabaseDto();
        List<NotEnviamentDatabaseDto> enviaments = new ArrayList<>();
        DocumentDto document = new DocumentDto();
        String missatge = "";
        String columna = "";

        try {
            notificacio.setCaducitat(caducitat);
            // Organ gestor
            columna = "1 - Codigo Unidad Remisora";
            missatge = "obtenint el codi de l'Òrgan gestor";
            notificacio.setOrganGestorCodi(linia[0]);

            // Entitat
            columna = "";
            missatge = "obtenint el codi Dir3 de l'entitat";
            notificacio.setEmisorDir3Codi(entitat.getDir3Codi());

            // Concepte
            columna = "2 - Concepto";
            missatge = "obtenint el concepte";
            notificacio.setConcepte(linia[1]);

            // Descripció
            notificacio.setDescripcio(null);

            // Tipus enviament
            columna = "3 - Tipo de Envio";
            missatge = "Obtenint el tipus d'enviament. Valors vàlids: [Comunicacio | Notificacio]";
            setTipusEnviament(notificacio, linia[2]);

            // Grup
            notificacio.setGrup(null);

            // Idioma
            notificacio.setIdioma(null);

            // Usuari
            notificacio.setUsuariCodi(usuariCodi);

            // Retard
            columna = "16 - Retardo Postal";
            missatge = "obtenint el retard postal";
            setRetard(notificacio, linia[15]);

            // Procediment
            columna = "17 - Código Procedimiento";
            missatge = "obtenint el procediment";
            ProcSerDto procediment = new ProcSerDto();
            procediment.setCodi(linia[16]);
            notificacio.setProcediment(procediment);

            // Fecha envío programado
            columna = "18 - Fecha Envio Programado";
            missatge = "obtenint la data d'enviament programada";
            setDataProgramada(notificacio, linia[17]);

            // Document
            columna = "5 - Nombre Fichero";
            missatge = "obtenint el document";
            boolean llegirMetadades = setDocument(
                    notificacio,
                    document,
                    linia,
                    fileNames,
                    ficheroZipBytes,
                    documentsProcessatsMassiu);
            if (llegirMetadades) {
                columna = "19 - Origen";
                missatge = "obtenint les metadades del document: Origen";
                setOrigen(notificacio, linia[18]);
                columna = "20 - Estado Elaboración";
                missatge = "obtenint les metadades del document: Estat elaboració";
                setValidesa(notificacio, linia[19]);
                columna = "21 - Tipo documental";
                missatge = "obtenint les metadades del document: Tipus documental";
                setTipusDocumental(notificacio, linia[20]);
                columna = "22 - PDF Firmado";
                missatge = "obtenint les metadades del document: Pdf firmat";
                setModeFirma(notificacio, linia[21]);
            }

            // Enviaments ////////////////

            // Referencia - Núm. expedient
            // TODO: #641 - Els enviaments massius encara que s'ompli el camp de "Referencia Emisor" al csv no es mostra al llistat de remeses al camp "Número expedient"
            columna = "4 - Referencia Emisor (Número expedient)";
            missatge = "obtenint referència";
            String referencia = (linia[3] != null && !linia[3].isEmpty()) ? linia[3] : null;
            notificacio.setNumExpedient(referencia);
            enviament.setNotificaReferencia(referencia); //si no se envía, Notific@ genera una
            enviament.setEntregaDehActiva(false); // De momento dejamos false

            // Entrega postal
            columna = "13, 14 y 15 - Linea 1, Linea 2 y Codigo Postal";
            missatge = "obtenint l'adreça de l'enviament postal";
            setEntregaPostal(linia, entitat, enviament);

            // Servei tipus
            columna = "7 - Prioridad Servicio";
            missatge = "obtenint la prioritat d'enviament. Valors vàlids: [Normal | Urgent]";
            setServeiTipus(notificacio, enviament, linia[6]);

            // Titular /////////////
            PersonaDto titular = new PersonaDto();

            // Nom
            columna = "8 - Nombre";
            missatge = "obtenint el nom del titular";
            titular.setNom(linia[7]);

            // Llinatges
            columna = "9 - Apellidos";
            missatge = "obtenint els llinatges del titular";
            titular.setLlinatge1(linia[8]); // vienen ap1 y ap2 juntos
            titular.setLlinatge2(null);

            // NIF
            columna = "10 - CIF/NIF";
            missatge = "obtenint el CIF/NIF del titular";
            titular.setNif(linia[9]);

            // Interessat tipus
            missatge = "calculant el tipus d'interessat a partir del CIF/NIF.";
            // TODO:  Igual lo hemos planteado mal. Si es un nif, podria ser el Nif de la administración.
            //Entiendo que el "Código destino" = linia[11] solo se informará en caso de ser una administración
            //Si es persona física o jurídica no tiene sentido
            //Entonces podriamos utilizar este campo para saber si es una administración
            setInteressatTipus(notificacio, titular);

            // Email
            columna = "11 - Email";
            missatge = "obtenint el correu electrònic del titular";
            titular.setEmail(linia[10]);

            // Codi Dir3
            columna = "12 - Codigo destino";
            missatge = "obtenint el Codi Dir3 de l'administració destinatària";
            titular.setDir3Codi(linia[11]);

            // Incapacitat
            titular.setIncapacitat(false);

            enviament.setTitular(titular);
            enviaments.add(enviament);
            notificacio.setEnviaments(enviaments);

        } catch (Exception e) {
            throw new NotificacioMassivaException(
                    fila,
                    columna,
                    "Error " + missatge,
                    e);
        }

        return notificacio;
    }

    private void setTipusEnviament(NotificacioDatabaseDto notificacio, String strTipusEnviament) {
        if (strTipusEnviament != null && !strTipusEnviament.isEmpty()) {
            if ("C".equalsIgnoreCase(strTipusEnviament) ||
                    "COMUNICACIO".equalsIgnoreCase(strTipusEnviament) ||
                    "COMUNICACION".equalsIgnoreCase(strTipusEnviament) ||
                    "COMUNICACIÓ".equalsIgnoreCase(strTipusEnviament) ||
                    "COMUNICACIÓN".equalsIgnoreCase(strTipusEnviament)) {
                notificacio.setEnviamentTipus(NotificaEnviamentTipusEnumDto.COMUNICACIO);
            } else if ("N".equalsIgnoreCase(strTipusEnviament) ||
                    "NOTIFICACIO".equalsIgnoreCase(strTipusEnviament) ||
                    "NOTIFICACION".equalsIgnoreCase(strTipusEnviament) ||
                    "NOTIFICACIÓ".equalsIgnoreCase(strTipusEnviament) ||
                    "NOTIFICACIÓN".equalsIgnoreCase(strTipusEnviament)) {
                notificacio.setEnviamentTipus(NotificaEnviamentTipusEnumDto.NOTIFICACIO);
            } else {
                notificacio.setEnviamentTipus(NotificaEnviamentTipusEnumDto.COMUNICACIO);
                notificacio.getErrors().add("[1051] El tipus d'enviament (" + strTipusEnviament + ") no és vàlid. Valors permesos: [Notificacio(n) | Comunicacio(n)].");
            }
        }
    }

    private void setRetard(NotificacioDatabaseDto notificacio, String strRetard) {
        if (isEnter(strRetard)) {
            notificacio.setRetard(Integer.valueOf(strRetard));
        } else if (strRetard != null) {
            notificacio.getErrors().add("[1045] El retard (" + strRetard + ") no és vàlid. Valors permesos: número enter.");
        }
    }

    private void setDataProgramada(NotificacioDatabaseDto notificacio, String strData) {
        try {
            if (strData != null && !strData.isEmpty()) {
                notificacio.setEnviamentDataProgramada(new SimpleDateFormat("dd/MM/yyyy").parse(strData));
            } else {
                notificacio.setEnviamentDataProgramada(null);
            }
        } catch (ParseException e) {
            notificacio.setEnviamentDataProgramada(null);
            notificacio.getErrors().add("[1340] El format de la data d'enviament programada (" + strData + ") no és correcte. Format esperat: dd/MM/yyyy.");
        }
    }

    private boolean setDocument(
            NotificacioDatabaseDto notificacio,
            DocumentDto document,
            String[] linia,
            List<String> fileNames,
            byte[] ficheroZipBytes,
            Map<String, Long> documentsProcessatsMassiu) {

        boolean llegirMetadades = false;

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
                        llegirMetadades = true;
//                        leerMetadadesDelCsv(notificacio, document, linia);
                    }
                }
                notificacio.setDocument(document);
            } else {
                String[] docSplit = linia[4].split("\\.");
                if (docSplit.length > 1 && Arrays.asList("JPG", "JPEG", "ODT", "ODP", "ODS", "ODG", "DOCX", "XLSX", "PPTX",
                        "PDF", "PNG", "RTF", "SVG", "TIFF", "TXT", "XML", "XSIG", "CSIG", "HTML", "CSV", "ZIP")
                        .contains(docSplit[1].toUpperCase())) {
                    notificacio.setDocument(null);
                    notificacio.getErrors().add("[1350] No s'ha trobat el document dins el fitxer ZIP amb els documents adjunts.");
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
                            llegirMetadades = true;
//                            leerMetadadesDelCsv(notificacio, document, linia);
                        }
                    } else {
                        // Csv
                        document.setCsv(linia[4]);
                        document.setNormalitzat("Si".equalsIgnoreCase(linia[5]));
                        document.setGenerarCsv(false);
                        if (registreNotificaHelper.isSendDocumentsActive()) {
                            llegirMetadades = true;
//                            leerMetadadesDelCsv(notificacio, document, linia);
                        }
                    }
                    notificacio.setDocument(document);
                }
            }
        }

        return llegirMetadades;
    }

    private void setOrigen(NotificacioDatabaseDto notificacio, String strOrigen) {
        // Origen
        if (strOrigen != null && !strOrigen.isEmpty()) {
            if ("CIUTADA".equalsIgnoreCase(strOrigen) || "CIUDADANO".equalsIgnoreCase(strOrigen)) {
                notificacio.getDocument().setOrigen(OrigenEnum.CIUTADA);
            } else if ("ADMINISTRACIO".equalsIgnoreCase(strOrigen) || "ADMINISTRACION".equalsIgnoreCase(strOrigen)) {
                notificacio.getDocument().setOrigen(OrigenEnum.ADMINISTRACIO);
            } else {
                notificacio.getDocument().setOrigen(OrigenEnum.CIUTADA);
                notificacio.getErrors().add("[1066] Error en les metadades del document. El valor Origen (" + strOrigen + ") no és vàlid. Valors permesos: [Ciutada | Ciudadano | Administracio(n)].");
            }
        }
    }

    private void setValidesa(NotificacioDatabaseDto notificacio, String strValidesa) {
        // Validesa
        if (strValidesa != null && !strValidesa.isEmpty()) {
            if ("ORIGINAL".equalsIgnoreCase(strValidesa)) {
                notificacio.getDocument().setValidesa(ValidesaEnum.ORIGINAL);
            } else if ("COPIA".equalsIgnoreCase(strValidesa)) {
                notificacio.getDocument().setValidesa(ValidesaEnum.COPIA);
            } else if ("COPIA AUTENTICA".equalsIgnoreCase(strValidesa)) {
                notificacio.getDocument().setValidesa(ValidesaEnum.COPIA_AUTENTICA);
            } else {
                notificacio.getDocument().setValidesa(ValidesaEnum.ORIGINAL);
                notificacio.getErrors().add("[1066] Error en les metadades del document. El valor Validesa (" + strValidesa + ") no és vàlid. Valors permesos: [Original | Copia | Copia autentica].");
            }
        }
    }

    private void setTipusDocumental(NotificacioDatabaseDto notificacio, String strTipus) {
        // Tipo documental
        if (strTipus != null && !strTipus.isEmpty()) {
            TipusDocumentalEnum tipo = TipusDocumentalEnum.ALTRES;
            try {
                tipo = TipusDocumentalEnum.valueOf(strTipus.toUpperCase());
            } catch (IllegalArgumentException e) {
                notificacio.getErrors().add("[1066] Error en les metadades del document. El valor Tipo documental (" + strTipus + ") no és vàlid. Valors permesos: [" +
                        "RESSOLUCIO | ACORD | CONTRACTE | CONVENI | DECLARACIO | COMUNICACIO | NOTIFICACIO | PUBLICACIO | " +
                        "JUSTIFICANT_RECEPCIO | ACTA | CERTIFICAT | DILIGENCIA | INFORME | SOLICITUD | DENUNCIA | ALEGACIO | " +
                        "RECURS | COMUNICACIO_CIUTADA | FACTURA | ALTRES_INCAUTATS | ALTRES | LLEI |  MOCIO | INSTRUCCIO | " +
                        "CONVOCATORIA | ORDRE_DIA | INFORME_PONENCIA | DICTAMEN_COMISSIO | INICIATIVA_LEGISLATIVA | " +
                        "PREGUNTA | INTERPELACIO | RESPOSTA | PROPOSICIO_NO_LLEI | ESQUEMA | PROPOSTA_RESOLUCIO | " +
                        "COMPAREIXENSA | SOLICITUD_INFORMACIO | ESCRIT | INICIATIVA_LEGISLATIVA2 | PETICIO].");
            }
            notificacio.getDocument().setTipoDocumental(tipo);
        }
    }

    private void setModeFirma(NotificacioDatabaseDto notificacio, String strMode) {
        // PDF firmat
        if (strMode != null && !strMode.isEmpty()) {
            if ("SI".equalsIgnoreCase(strMode) || "TRUE".equalsIgnoreCase(strMode)) {
                notificacio.getDocument().setModoFirma(true);
            } else if ("NO".equalsIgnoreCase(strMode) || "FALSE".equalsIgnoreCase(strMode)) {
                notificacio.getDocument().setModoFirma(false);
            } else {
                notificacio.getErrors().add("[1066] Error en les metadades del document. El valor Validesa (" + strMode + ") no és vàlid. Valors permesos: [Original | Copia | Copia autentica].");
            }
//                    Boolean.valueOf(linia[21]) : Boolean.FALSE);
        }
    }

    private void setEntregaPostal(String[] linia, EntitatEntity entitat, NotEnviamentDatabaseDto enviament) {
        if (entitat.getEntregaCie() != null &&
                linia[12] != null && !linia[12].isEmpty() && // Si vienen Línea 1 y Código Postal
                linia[14] != null && !linia[14].isEmpty()) {
            enviament.setEntregaPostalActiva(true);
            EntregaPostalDto entregaPostal = EntregaPostalDto.builder()
                    .domiciliConcretTipus(NotificaDomiciliConcretTipusEnumDto.SENSE_NORMALITZAR)
                    .linea1(linia[12])
                    .linea2(linia[13])
                    .codiPostal(linia[14])
                    .build();
            enviament.setEntregaPostal(entregaPostal);
        } else {
            enviament.setEntregaPostalActiva(false);
        }
    }

    private void setServeiTipus(
            NotificacioDatabaseDto notificacio,
            NotEnviamentDatabaseDto enviament,
            String strServeiTipus) {
        if (strServeiTipus != null && !strServeiTipus.isEmpty()) {
            if ("NORMAL".equalsIgnoreCase(strServeiTipus)) {
                enviament.setServeiTipus(ServeiTipusEnumDto.NORMAL);
            } else if ("URGENT".equalsIgnoreCase(strServeiTipus) || "URGENTE".equalsIgnoreCase(strServeiTipus)) {
                enviament.setServeiTipus(ServeiTipusEnumDto.URGENT);
            } else {
                enviament.setServeiTipus(ServeiTipusEnumDto.NORMAL);
                notificacio.getErrors().add("[1102] El tipus de servei (" + strServeiTipus + ") no és vàlid. Valors permesos: [Normal | Urgent(e)].");
            }
        }
    }

    private void setInteressatTipus(NotificacioDatabaseDto notificacio, PersonaDto titular) {
        if (titular.getNif() != null && !titular.getNif().isEmpty()) {
            if (NifHelper.isValidCif(titular.getNif())) {
                titular.setInteressatTipus(InteressatTipusEnumDto.JURIDICA);
            } else if (NifHelper.isValidNifNie(titular.getNif())) {
                titular.setInteressatTipus(InteressatTipusEnumDto.FISICA);
            } else {
//                try {
                    List<OrganGestorDto> lista = pluginHelper.unitatsPerCodi(titular.getNif());
                    if (lista != null && lista.size() > 0) {
                        titular.setInteressatTipus(InteressatTipusEnumDto.ADMINISTRACIO);
                    } else {
                        notificacio.getErrors().add("[1116] El 'CIF/NIF' del titular (" + titular.getNif() + ") no és vàlid.");
                    }
//                } catch (Exception e) {
//                    notificacio.getErrors().add("");
//                }
            }
        }
    }

    public boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public boolean isEnter(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            int i = Integer.parseInt(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
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
