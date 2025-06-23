package es.caib.notib.logic.service;

import com.google.common.base.Strings;
import es.caib.notib.logic.cacheable.CacheBridge;
import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.logic.cacheable.OrganGestorCachable;
import es.caib.notib.logic.email.EmailConstants;
import es.caib.notib.logic.exception.DocumentNotFoundException;
import es.caib.notib.logic.helper.AuditHelper;
import es.caib.notib.logic.helper.CacheHelper;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.ConversioTipusHelper;
import es.caib.notib.logic.helper.DocumentHelper;
import es.caib.notib.logic.helper.EmailNotificacioMassivaHelper;
import es.caib.notib.logic.helper.EntityComprovarHelper;
import es.caib.notib.logic.helper.MessageHelper;
import es.caib.notib.logic.helper.MetricsHelper;
import es.caib.notib.logic.helper.NotificacioHelper;
import es.caib.notib.logic.helper.NotificacioListHelper;
import es.caib.notib.logic.helper.NotificacioMassivaHelper;
import es.caib.notib.logic.helper.PaginacioHelper;
import es.caib.notib.logic.helper.PluginHelper;
import es.caib.notib.logic.helper.RegistreNotificaHelper;
import es.caib.notib.logic.helper.SubsistemesHelper;
import es.caib.notib.logic.intf.dto.DocumentValidDto;
import es.caib.notib.logic.intf.dto.FitxerDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.dto.RolEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.Notificacio;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioFiltreDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioMassivaDataDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioMassivaDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioMassivaEstatDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioMassivaFiltreDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioMassivaInfoDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioMassivaPrioritatDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioMassivaTableItemDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioTableItemDto;
import es.caib.notib.logic.intf.exception.AccessDeniedException;
import es.caib.notib.logic.intf.exception.NoDocumentException;
import es.caib.notib.logic.intf.exception.NoMetadadesException;
import es.caib.notib.logic.intf.exception.RegistreNotificaException;
import es.caib.notib.logic.intf.exception.WriteCsvException;
import es.caib.notib.logic.intf.service.AuditService;
import es.caib.notib.logic.intf.service.EnviamentSmService;
import es.caib.notib.logic.intf.service.NotificacioMassivaService;
import es.caib.notib.logic.intf.service.OrganGestorService;
import es.caib.notib.logic.mapper.NotificacioTableMapper;
import es.caib.notib.logic.objectes.MassivaColumnsEnum;
import es.caib.notib.logic.objectes.MassivaFile;
import es.caib.notib.logic.service.ws.NotificacioValidator;
import es.caib.notib.logic.statemachine.SmConstants;
import es.caib.notib.logic.utils.CSVReader;
import es.caib.notib.logic.utils.DatesUtils;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.entity.NotificacioEventEntity;
import es.caib.notib.persist.entity.NotificacioMassivaEntity;
import es.caib.notib.persist.entity.cie.PagadorPostalEntity;
import es.caib.notib.persist.repository.AplicacioRepository;
import es.caib.notib.persist.repository.DocumentRepository;
import es.caib.notib.persist.repository.GrupProcSerRepository;
import es.caib.notib.persist.repository.GrupRepository;
import es.caib.notib.persist.repository.NotificacioEventRepository;
import es.caib.notib.persist.repository.NotificacioMassivaRepository;
import es.caib.notib.persist.repository.NotificacioRepository;
import es.caib.notib.persist.repository.NotificacioTableViewRepository;
import es.caib.notib.persist.repository.OrganGestorRepository;
import es.caib.notib.persist.repository.PagadorPostalRepository;
import es.caib.notib.persist.repository.ProcSerRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ScheduledMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static es.caib.notib.logic.helper.SubsistemesHelper.SubsistemesEnum.MAS;

/**
 * Implementació del servei de gestió de notificacions.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Service
public class NotificacioMassivaServiceImpl implements NotificacioMassivaService {

    @Autowired
    private GrupProcSerRepository grupProcSerRepository;
    @Autowired
    private GrupRepository grupRepository;
    @Autowired
    private AplicacioRepository aplicacioRepository;
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
    private DocumentHelper documentHelper;
    @Autowired
    private NotificacioMassivaHelper notificacioMassivaHelper;
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private ProcSerRepository procSerRepository;
    @Autowired
    private OrganGestorRepository organGestorRepository;
    @Autowired
    private PagadorPostalRepository pagadorPostalRepository;
    @Autowired
    private NotificacioMassivaRepository notificacioMassivaRepository;
    @Autowired
    private NotificacioTableViewRepository notificacioTableViewRepository;
    @Autowired
    private AuditHelper auditHelper;
    @Autowired
    private NotificacioRepository notificacioRepository;
    @Autowired
    private NotificacioListHelper notificacioListHelper;
    @Autowired
    private ConfigHelper configHelper;
    @Autowired
    private MessageHelper messageHelper;
    @Autowired
    private NotificacioEventRepository notificacioEventRepository;
//    @Autowired
//    private NotificacioValidator notificacioValidator;
    @Autowired
    private CacheHelper cacheHelper;
    @Autowired
    private OrganGestorCachable organGestorCachable;
    @Autowired
    private NotificacioTableMapper notificacioTableMapper;
    @Autowired
    private EnviamentSmService enviamentSmService;
    @Autowired
    protected JmsTemplate jmsTemplate;
    @Autowired
    private OrganGestorService organGestorService;
    @Autowired
    private CacheBridge cacheBridge;

    @Override
    public NotificacioMassivaDataDto findById(Long entitatId, Long id) {

        entityComprovarHelper.comprovarEntitat(entitatId);
        var notificacioMassiva = notificacioMassivaRepository.findById(id).orElseThrow();
        return conversioTipusHelper.convertir(notificacioMassiva, NotificacioMassivaDataDto.class);
    }

    @Override
    @Transactional
    public NotificacioMassivaInfoDto getNotificacioMassivaInfo(Long entitatId, Long notificacioMassivaId) {

        entityComprovarHelper.comprovarEntitat(entitatId);
        var notificacioMassiva = notificacioMassivaRepository.findById(notificacioMassivaId).orElseThrow();
        var baos = new ByteArrayOutputStream();
        pluginHelper.gestioDocumentalGet(notificacioMassiva.getResumGesdocId(), PluginHelper.GESDOC_AGRUPACIO_MASSIUS_INFORMES, baos, false);
        var massivaFile = new MassivaFile(configHelper, messageHelper, pluginHelper);
        massivaFile.initInfo(baos.toByteArray(), notificacioMassiva.getNotificacions(), notificacioEventRepository);
        List<NotificacioMassivaInfoDto.NotificacioInfo> info = massivaFile.getEnviamentsInfo();

//        var linies = CSVReader.readBody(baos.toByteArray());
//        if (linies == null) {
//            return new NotificacioMassivaInfoDto();
//        }
//        List<NotificacioMassivaInfoDto.NotificacioInfo> info = new ArrayList<>();
//        var numNotificacio = 0;
//        NotificacioMassivaInfoDto.NotificacioInfo.NotificacioInfoBuilder builder;
//        for (var linea : linies) {
//            builder = NotificacioMassivaInfoDto.NotificacioInfo.builder()
//                        .codiDir3UnidadRemisora(linea[0])
//                        .concepto(linea[1])
//                        .concepto(linea[1])
//                        .enviamentTipus(linea[2])
//                        .referenciaEmisor(linea[3])
//                        .nombreFichero(linea[4])
//                        .normalizado(linea[5])
//                        .prioridadServicio(linea[6])
//                        .nombre(linea[7])
//                        .apellidos(linea[8])
//                        .cifNif(linea[9])
//                        .email(linea[10])
//                        .codigoDestino(linea[11])
//                        .linea1(linea[12])
//                        .linea2(linea[13])
//                        .codigoPostal(linea[14])
//                        .retardoPostal(linea[15])
//                        .codigoProcedimiento(linea[16])
//                        .fechaEnvioProgramado(linea[17]);
//
//            builder.descripcio(linea.length > 20 ? linea[22] : linea[18]);
//            if (linea.length >=24) { // si hi ha les metadades
//                builder.origen(linea[18]).estadoElaboracion(linea[19]).tipoDocumental(linea[20]).pdfFirmado(linea[21]);
//            }
//            var errores = linea[linea.length - 1];
//            builder.errores(errores);
//            if (messageHelper.getMessage("notificacio.massiva.cancelada").equals(linea[linea.length - 1])) {
//                builder.cancelada(true);
//            }
//             if (notificacioMassiva.getNotificacions() != null && !notificacioMassiva.getNotificacions().isEmpty()
//                && "OK".equalsIgnoreCase(errores)) {
//                NotificacioEntity not = notificacioMassiva.getNotificacions().get(numNotificacio);
//                StringBuilder error = new StringBuilder();
//                List<NotificacioEventEntity> events = notificacioEventRepository.findByNotificacioIdAndErrorIsTrue(not.getId());
//                for (NotificacioEventEntity event : events) {
//                    error.append(!Strings.isNullOrEmpty(event.getErrorDescripcio()) ? "\n" + event.getErrorDescripcio() : "");
//                }
//                builder.errorsExecucio(error.toString());
//                numNotificacio++;
//            }
//            info.add(builder.build());
//        }
        var dto = conversioTipusHelper.convertir(notificacioMassiva, NotificacioMassivaInfoDto.class);
        dto.setSummary(info);
        return dto;
    }

    @Transactional(rollbackFor=Exception.class, timeout = 3600)
    @Override
    public NotificacioMassivaDataDto create(Long entitatId, @NonNull String usuariCodi, @NonNull NotificacioMassivaDto notificacioMassiva) throws RegistreNotificaException {

        var timer = metricsHelper.iniciMetrica();
        long start = System.currentTimeMillis();
        try (var writerListErrors = new StringWriter();var writerListInforme = new StringWriter()){
            log.info("[NOT-MASSIVA] Alta de nova notificacio massiva (usuari: {}). Fitxer csv: {}", usuariCodi, notificacioMassiva.getFicheroCsvNom());
            var listWriterErrors = new CsvListWriter(writerListErrors, CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE);
            var listWriterInforme = new CsvListWriter(writerListInforme, CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE);

            var entitat = entityComprovarHelper.comprovarEntitat(entitatId);
            var massivaFile = new MassivaFile(configHelper, messageHelper, pluginHelper);
            massivaFile.initCreate(notificacioMassiva, entitat, usuariCodi);

            var header = massivaFile.getHeader();
            header.add("Errors");
            writeCsvHeader(listWriterErrors, header);
            writeCsvHeader(listWriterInforme, header);

            var notificacioMassivaEntity = registrarNotificacioMassiva(entitat, notificacioMassiva, massivaFile.getNombreEnviamentsCsv());

            var numAltes = 0;
            var fila = 0;
            Map<String, Long> documentsProcessatsMassiu = new HashMap<>();

            for (Notificacio notificacio: massivaFile.getNotificacions()) {
                var notificacioValidator = new NotificacioValidator(
                        aplicacioRepository,
                        grupRepository,
                        procSerRepository,
                        grupProcSerRepository,
                        messageHelper,
                        cacheHelper,
                        organGestorCachable,
                        configHelper,
                        organGestorService,
                        conversioTipusHelper,
                        cacheBridge);
                notificacioValidator.setMassiva(true);

                var procediment = !Strings.isNullOrEmpty(notificacio.getProcedimentCodi()) ? procSerRepository.findByCodiAndEntitat(notificacio.getProcedimentCodi(), entitat) : null;
                var organGestor = !Strings.isNullOrEmpty(notificacio.getOrganGestor()) ? organGestorRepository.findByEntitatAndCodi(entitat, notificacio.getOrganGestor()) : null;
                var document = documentHelper.getDocument(notificacio.getDocument());
                // En massives només podem tenir un document
                DocumentValidDto document2 = null;
                DocumentValidDto document3 = null;
                DocumentValidDto document4 = null;
                DocumentValidDto document5 = null;
                if (EnviamentTipus.SIR.equals(notificacio.getEnviamentTipus())) {
                    document2 = documentHelper.getDocument(notificacio.getDocument2());
                    document3 = documentHelper.getDocument(notificacio.getDocument3());
                    document4 = documentHelper.getDocument(notificacio.getDocument4());
                    document5 = documentHelper.getDocument(notificacio.getDocument5());
                }

                //TODO AQUEST IF S'HA DE COMPROVAR SI REALMENT ES NECESITA, EN MASIVES ES POT CREAR SENSE DOCUMENT I VALIDA
                if (notificacio.getDocument() == null || Strings.isNullOrEmpty(notificacio.getDocument().getContingutBase64())) {
                    notificacioValidator.setValidarDocuments(false);
                }
                var docs = new DocumentValidDto[] { document, document2, document3, document4, document5 };
                var errors = new BindException(notificacio, "notificacio");
                notificacioValidator.setNotificacio(notificacio);
                notificacioValidator.setEntitat(entitat);
                notificacioValidator.setProcediment(procediment);
                notificacioValidator.setOrganGestor(organGestor);
                notificacioValidator.setDocuments(docs);
                notificacioValidator.setErrors(errors);
                notificacioValidator.setLocale(new Locale("rest"));
                notificacioValidator.validate();

                if (!errors.hasErrors()) {
                    try {
                        if (procediment != null) {
                            notificacio.setProcedimentId(procediment.getId());
                        }
                        crearNotificacio(entitat, notificacio, notificacioMassivaEntity, documentsProcessatsMassiu);
                    } catch (DocumentNotFoundException | NoDocumentException ex) {
                    } catch (NoMetadadesException ex) {
                    }
                }

                var linia = massivaFile.getEnviamentCsv(fila++);
                if (errors.hasErrors()) {
                    List<String> errs = errors.getAllErrors().stream().map(x -> x.getCode()).collect(Collectors.toList());
                    log.debug("[NOT-MASSIVA] Alta errònea de la notificació de la nova notificacio massiva");
                    writeCsvLinia(listWriterErrors, linia, errs);
                    writeCsvLinia(listWriterInforme, linia, errs);
                } else {
                    log.debug("[NOT-MASSIVA] Alta satisfactoria de la notificació de la nova notificacio massiva");
                    List<String> ok = Collections.singletonList(messageHelper.getMessage("notificacio.massiva.ok.validacio"));
                    writeCsvLinia(listWriterInforme, linia, ok);
                    numAltes++;
                }
            }
            notificacioMassivaEntity.updateEstatValidacio(numAltes);

            try {
                listWriterInforme.flush();
                listWriterErrors.flush();
                var fileResumContent = writerListInforme.toString().getBytes();
                var fileErrorsContent = writerListErrors.toString().getBytes();
                pluginHelper.gestioDocumentalUpdate(notificacioMassivaEntity.getErrorsGesdocId(), PluginHelper.GESDOC_AGRUPACIO_MASSIUS_ERRORS, fileErrorsContent);
                pluginHelper.gestioDocumentalUpdate(notificacioMassivaEntity.getResumGesdocId(), PluginHelper.GESDOC_AGRUPACIO_MASSIUS_INFORMES, fileResumContent);
            } catch (Exception e) {
                log.error("[NOT-MASSIVA] Hi ha hagut un error al intentar guardar els documents de l'informe i del error.", e);
            }
            writeCsvClose(listWriterErrors);
            writeCsvClose(listWriterInforme);
            if (getPrioritatNotificacioMassiva().equals(NotificacioMassivaPrioritatDto.BAIXA)) {
                notificacioMassivaHelper.posposarNotificacions(notificacioMassivaEntity.getId());
            }
            enviarCorreuElectronic(notificacioMassivaEntity);
            SubsistemesHelper.addSuccessOperation(MAS, System.currentTimeMillis() - start);
            return conversioTipusHelper.convertir(notificacioMassivaEntity, NotificacioMassivaDataDto.class);
        } catch (Throwable t) {
            log.error("[NOT-MASSIVA] Error no controlat en l'enviament massiu", t);
            SubsistemesHelper.addErrorOperation(MAS, System.currentTimeMillis() - start);
            throw new RegistreNotificaException(t.getMessage());
        } finally {
            metricsHelper.fiMetrica(timer);
        }
    }

    @Transactional
    @Override
    public PaginaDto<NotificacioTableItemDto> findNotificacions(Long entitatId, Long notificacioMassivaId, NotificacioFiltreDto filtre, PaginacioParamsDto paginacioParams) {

        var entitatActual = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false, false);
        var pageable = notificacioListHelper.getMappeigPropietats(paginacioParams);
        var f = notificacioListHelper.getFiltre(filtre, entitatId, null, null, null);
        f.setNotificacioMassiva(notificacioMassivaRepository.findById(notificacioMassivaId).orElse(null));
        var notificacions = notificacioTableViewRepository.findAmbFiltreByNotificacioMassiva(f, pageable);
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var dtos = notificacioTableMapper.toNotificacionsTableItemDto(
                notificacions.getContent(),
                notificacioListHelper.getCodisProcedimentsAndOrgansAmpPermisProcessar(entitatId, auth.getName()),
                cacheHelper.findOrganigramaNodeByEntitat(f.getEntitat().getDir3Codi()));
        return paginacioHelper.toPaginaDto(dtos, notificacions);
    }

//    private String[] trim(String[] linia) {
//
//        String camp;
//        for(var i = 0; i < linia.length; i++) {
//            camp = linia[i] != null ? linia[i].trim() : null;
//            if (camp != null && camp.isEmpty()) {
//                camp = null;
//            }
//            linia[i] = camp;
//        }
//        return linia;
//    }

    @Override
    public void delete(Long entitatId, Long notificacioMassivaId) {

        entityComprovarHelper.comprovarEntitat(entitatId);
        var notificacioMassiva = notificacioMassivaRepository.findById(notificacioMassivaId).orElseThrow();
        if (notificacioMassiva.getNotificacions().isEmpty()) {
            notificacioMassivaRepository.deleteById(notificacioMassivaId);
        }
    }

    @Override
    public PaginaDto<NotificacioMassivaTableItemDto> findAmbFiltrePaginat(Long entitatId, NotificacioMassivaFiltreDto filtre, RolEnumDto rol, PaginacioParamsDto paginacioParams) {

        var entitat = entityComprovarHelper.comprovarEntitat(entitatId);
        Page<NotificacioMassivaEntity> pageNotificacionsMassives;
        if (RolEnumDto.tothom.equals(rol)){
            pageNotificacionsMassives = findAmbFiltrePaginatByUser(entitat, filtre, paginacioParams);
        } else if (RolEnumDto.NOT_ADMIN.equals(rol)){
            pageNotificacionsMassives = findAmbFiltrePaginatByAdminEntitat(entitat, filtre, paginacioParams);
        } else {
            throw new AccessDeniedException("Només es poden consultar les notificacions massives amb els rols d'usuari o d'administrador d'entitat");
        }
        return paginacioHelper.toPaginaDto(pageNotificacionsMassives, NotificacioMassivaTableItemDto.class);
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

    @Transactional
    @Override
    public void cancelar(Long entitatId, Long notificacioMassivaId) throws Exception {

        entityComprovarHelper.comprovarEntitat(entitatId);
        notificacioMassivaHelper.posposarNotificacions(notificacioMassivaId);
        var massiva = notificacioMassivaRepository.findById(notificacioMassivaId).orElseThrow();
        var baos = new ByteArrayOutputStream();
        var baosCsv = new ByteArrayOutputStream();
        pluginHelper.gestioDocumentalGet(massiva.getResumGesdocId(), PluginHelper.GESDOC_AGRUPACIO_MASSIUS_INFORMES, baos, false);
        pluginHelper.gestioDocumentalGet(massiva.getCsvGesdocId(), PluginHelper.GESDOC_AGRUPACIO_MASSIUS_CSV, baosCsv, false);
        var linies = CSVReader.readBody(baos.toByteArray());
        var liniesCsv = CSVReader.readBody(baos.toByteArray());
        if (linies == null || liniesCsv == null) {
            return;
        }
        var notificacions = massiva.getNotificacions();
        try {
            var cancelada = false;
            var writerListErrors = new StringWriter();
            var writerListInforme = new StringWriter();
            var listWriterInforme = initCsvWritter(writerListInforme);
            var listWriterErrors = initCsvWritter(writerListErrors);
            var header = CSVReader.readHeader(baos.toByteArray());
            writeCsvHeader(listWriterInforme, header);
            writeCsvHeader(listWriterErrors, header);
            var ok = messageHelper.getMessage("notificacio.massiva.ok.validacio");
            NotificacioEntity not;
            List<String> linia;
            List<String> liniaCsv;
            String txt;
            List<String> msg;
            for (var foo = 0; foo < notificacions.size(); foo++) {
                not = notificacions.get(foo);
                linia = linies.get(foo);
                liniaCsv = liniesCsv.get(foo);
                txt = NotificacioEstatEnumDto.PENDENT.equals(not.getEstat()) ? messageHelper.getMessage("notificacio.massiva.cancelada") : "";
                msg = Collections.singletonList(txt);
                if (!ok.equals(linia.get(linia.size()-1)) || !Strings.isNullOrEmpty(txt)) {
                    msg = !ok.equals(linia.get(linia.size()-1)) ? Collections.singletonList("") : msg;
                    writeCsvLinia(listWriterErrors, liniaCsv, msg);
                }
                writeCsvLinia(listWriterInforme, linia, msg);
                if (!NotificacioEstatEnumDto.PENDENT.equals(not.getEstat())) {
                    continue;
                }
                notificacioTableViewRepository.deleteById(not.getId());
                notificacioRepository.deleteById(not.getId());
                massiva.updateCancelades();
                cancelada = true;
            }
            if (!cancelada) {
                return;
            }
            notificacioMassivaRepository.save(massiva);
            listWriterErrors.flush();
            byte[] content = writerListErrors.toString().getBytes();
            pluginHelper.gestioDocumentalUpdate(massiva.getErrorsGesdocId(), PluginHelper.GESDOC_AGRUPACIO_MASSIUS_ERRORS, content);
            listWriterInforme.flush();
            content = writerListInforme.toString().getBytes();
            pluginHelper.gestioDocumentalUpdate(massiva.getResumGesdocId(), PluginHelper.GESDOC_AGRUPACIO_MASSIUS_INFORMES, content);
        } catch (Exception ex) {
            log.error("Error cancelant la notificacio massiva " + notificacioMassivaId);
            throw ex;
        }
    }

    @Override
    @Transactional
    public void iniciar(Long id) {

        try {
            AtomicInteger enviamentCounter = new AtomicInteger(1);
            var globalDelay = configHelper.getConfigAsLong("es.caib.notib.massives.state.machine.inici.delay", SmConstants.MASSIU_DELAY);
            var massiva = notificacioMassivaRepository.findById(id).orElseThrow();
            massiva.getNotificacions().forEach(n -> n.getEnviaments().forEach(e -> {
                var delay = enviamentCounter.getAndIncrement() * globalDelay;
                Thread t = new Thread(() -> enviamentSmService.altaEnviament(e.getNotificaReferencia(), delay));
                t.start();
            }));
        } catch (Exception ex) {
            log.error("Error inicialitant la màquina d'estats per la notificacio massiva " + id);
        }
    }

    @Transactional
    public byte [] afegirErrorsProcessat(NotificacioMassivaEntity massiva, byte[] contingut, boolean fitxerErrors) {

        var files = CSVReader.readBody(contingut);
        var notificacions = massiva.getNotificacions();
        if (notificacions == null) {
            return "".getBytes();
        }
        try {
            var writer = new StringWriter();
            var listWriter = initCsvWritter(writer);
            List<String> errors;
            StringBuilder error;
            var csvHeader = CSVReader.readHeader(contingut);
            if (fitxerErrors) {
                csvHeader = new ArrayList<>();
                csvHeader.add("Referencia Notib");
                csvHeader.add("Desc");
            }
            if (csvHeader == null) {
                csvHeader = new ArrayList<>();
            }
            csvHeader.add("Errores exec");
            writeCsvHeader(listWriter, csvHeader);
            NotificacioEntity not;
            List<NotificacioEventEntity> events;
            for (var foo = 0; foo < notificacions.size(); foo++) {
                errors = new ArrayList<>();
                error = new StringBuilder();
                not = notificacions.get(foo);
                events = notificacioEventRepository.findByNotificacioIdAndErrorIsTrue(not.getId());
                for (var event : events) {
                    error.append(!Strings.isNullOrEmpty(event.getErrorDescripcio()) ? "\n" + event.getErrorDescripcio() : "");
                }
                errors.add(error.toString());
                writeCsvLinia(listWriter, fitxerErrors ? List.of(not.getReferencia(), not.getConcepte()) : files.get(foo), errors);
            }
            listWriter.flush();
            var content = writer.toString().getBytes();
            writeCsvClose(listWriter);
            return content;
        } catch(Throwable ex) {
            log.error("Error creant el document d'errors d'execució", ex);
            return "".getBytes();
        }
    }

    @Override
    public FitxerDto getCSVFile(Long entitatId, Long notificacioMassivaId) {

        entityComprovarHelper.comprovarEntitat(entitatId);
        var notificacioMassiva = notificacioMassivaRepository.findById(notificacioMassivaId).orElseThrow();
        var baos = new ByteArrayOutputStream();
        pluginHelper.gestioDocumentalGet(notificacioMassiva.getCsvGesdocId(), PluginHelper.GESDOC_AGRUPACIO_MASSIUS_CSV, baos, false);
        return FitxerDto.builder().nom(notificacioMassiva.getCsvFilename()).contentType("text").contingut(baos.toByteArray()).tamany(baos.size()).build();
    }

    @Override
    public FitxerDto getZipFile(Long entitatId, Long notificacioMassivaId) {

        entityComprovarHelper.comprovarEntitat(entitatId);
        var notificacioMassiva = notificacioMassivaRepository.findById(notificacioMassivaId).orElseThrow();
        var baos = new ByteArrayOutputStream();
        pluginHelper.gestioDocumentalGet(notificacioMassiva.getZipGesdocId(), PluginHelper.GESDOC_AGRUPACIO_MASSIUS_ZIP, baos, true);
        return FitxerDto.builder().nom(notificacioMassiva.getZipFilename()).contentType("text").contingut(baos.toByteArray()).tamany(baos.size()).build();
    }

    @Override
    @Transactional
    public FitxerDto getResumFile(Long entitatId, Long notificacioMassivaId) {

        entityComprovarHelper.comprovarEntitat(entitatId);
        var notificacioMassiva = notificacioMassivaRepository.findById(notificacioMassivaId).orElseThrow();
        var baos = new ByteArrayOutputStream();
        pluginHelper.gestioDocumentalGet(notificacioMassiva.getResumGesdocId(), PluginHelper.GESDOC_AGRUPACIO_MASSIUS_INFORMES, baos, false);
        var fitxer = FitxerDto.builder().nom("resum.csv").contentType("text").contingut(baos.toByteArray()).tamany(baos.size()).build();
        var errors = afegirErrorsProcessat(notificacioMassiva, fitxer.getContingut(), false);
        fitxer.setContingut(errors);
        return fitxer;
    }

    @Override
    public FitxerDto getErrorsValidacioFile(Long entitatId, Long notificacioMassivaId) throws IOException {

        try (var baos = new ByteArrayOutputStream()) {
            entityComprovarHelper.comprovarEntitat(entitatId);
            var notificacioMassiva = notificacioMassivaRepository.findById(notificacioMassivaId).orElseThrow();
            pluginHelper.gestioDocumentalGet(notificacioMassiva.getErrorsGesdocId(), PluginHelper.GESDOC_AGRUPACIO_MASSIUS_ERRORS, baos, false);
            return FitxerDto.builder().nom("errors_validacio.csv").contentType("text").contingut(baos.toByteArray()).tamany(baos.size()).build();
        }
    }

    @Override
    @Transactional
    public FitxerDto getErrorsExecucioFile(Long entitatId, Long notificacioMassivaId) throws IOException {

        try (var baos = new ByteArrayOutputStream()) {
            entityComprovarHelper.comprovarEntitat(entitatId);
            var notificacioMassiva = notificacioMassivaRepository.findById(notificacioMassivaId).orElseThrow();
            var fitxer = FitxerDto.builder().nom("errors_execucio.csv").contentType("text").contingut(baos.toByteArray()).tamany(baos.size()).build();
            var errors = afegirErrorsProcessat(notificacioMassiva, fitxer.getContingut(), true);
            fitxer.setContingut(errors);
            return fitxer;
        }
    }

    @Transactional
    @Override
    public byte[] getModelDadesCarregaMassiuCSV() throws IOException {

        var timer = metricsHelper.iniciMetrica();
        try {
//            var path = registreNotificaHelper.isSendDocumentsActive() ?  "modelo_datos_carga_masiva_metadades.csv" : "modelo_datos_carga_masiva.csv";
//            var input = this.getClass().getClassLoader().getResourceAsStream("es/caib/notib/logic/plantillas/" + path);
//            assert input != null;
//            return IOUtils.toByteArray(input);
            return generateModelCsv().getBytes();
        } finally {
            metricsHelper.fiMetrica(timer);
        }
    }

    private String generateModelCsv() {
        Boolean sendDocumentsActive = registreNotificaHelper.isSendDocumentsActive();
        String headerName = Stream.of(MassivaColumnsEnum.values())
                .filter(e -> sendDocumentsActive || !e.name().startsWith("META_"))
                .map(MassivaColumnsEnum::getNom)
                .collect(Collectors.joining(";"));
        String headerDesc = Stream.of(MassivaColumnsEnum.values())
                .filter(e -> sendDocumentsActive || !e.name().startsWith("META_"))
                .map(MassivaColumnsEnum::getDescripcio)
                .collect(Collectors.joining(";"));

        String exampleRow = Stream.of(MassivaColumnsEnum.values())
                .filter(e -> sendDocumentsActive || !e.name().startsWith("META_"))
                .map(MassivaColumnsEnum::getExemple)
                .collect(Collectors.joining(";"));

        return headerName + "\n" + headerDesc + "\n" + exampleRow;
    }

//    private void checkCSVContent(List<String[]> linies, List<String> csvHeader) {
//
//        if (linies == null || csvHeader == null) {
//            throw new InvalidCSVFileException("S'ha produït un error processant el fitxer CSV indicat: sense contingut");
//        }
//        if (linies.isEmpty()) {
//            throw new InvalidCSVFileNotificacioMassivaException("El fitxer CSV està buid.");
//        }
//        var maxFilles = configHelper.getConfig("es.caib.notib.massives.maxim.files");
//        var maxEnviaments = !Strings.isNullOrEmpty(maxFilles) ? Long.parseLong(maxFilles) : MAX_ENVIAMENTS;
//        if (linies.size() > maxEnviaments) {
//            log.debug(String.format("[NOT-MASSIVA] El fitxer CSV conté més de les %d línies permeses.", maxEnviaments));
//            throw new MaxLinesExceededException(String.format("S'ha superat el màxim nombre de línies permès (%d) per al CSV de càrrega massiva.", maxEnviaments));
//        }
//        if (csvHeader.size() < numberRequiredColumns()) {
//            var msg = String.format("El fitxer CSV no conté totes les columnes necessaries. Nombre de columnes requerides: %d. Nombre de columnes trobades %d", numberRequiredColumns(), csvHeader.size());
//            throw new InvalidCSVFileNotificacioMassivaException(msg);
//        }
//    }
//
//    public int numberRequiredColumns() {
//        return registreNotificaHelper.isSendDocumentsActive() ? 23 : 19;
//    }

    private void crearNotificacio(EntitatEntity entitat, Notificacio notificacio, NotificacioMassivaEntity notMassiva, Map<String, Long> documentsProcessatsMassiu) throws RegistreNotificaException {

        log.debug("[NOT-MASSIVA] Creació de notificació de nova notificacio massiva");
        var notificacioEntity = notificacioHelper.saveNotificacio(entitat, notificacio,false, notMassiva, documentsProcessatsMassiu);
        auditHelper.auditaNotificacio(notificacioEntity, AuditService.TipusOperacio.CREATE, "NotificacioMassivaServiceImpl.crearNotificacio");
        log.debug("[NOT-MASSIVA] Alta notificació de nova notificacio massiva");
        notificacioHelper.altaEnviamentsWeb(entitat, notificacioEntity, notificacio.getEnviaments());
        // SM
//        notificacioEntity.getEnviaments().forEach(e -> enviamentSmService.altaEnviament(e.getNotificaReferencia()));
        notMassiva.joinNotificacio(notificacioEntity);
    }

    private Page<NotificacioMassivaEntity> findAmbFiltrePaginatByUser(EntitatEntity entitat, NotificacioMassivaFiltreDto filtre, PaginacioParamsDto paginacioParams){

        var auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth.getName() == null) {
            throw new AccessDeniedException("No s'ha pogut consultar el llistat de notificacions massives");
        }
        var dataIniciNull = filtre.getDataInici() == null;
        var dataFiNull = filtre.getDataFi() == null;
        var estatProcessNull = filtre.getEstatProces() == null;
        filtre.setDataFi(DatesUtils.incrementarDataFi(filtre.getDataFi()));
        return notificacioMassivaRepository.findUserRolePage(entitat, auth.getName(), dataIniciNull, filtre.getDataInici(), dataFiNull, filtre.getDataFi(),
                estatProcessNull, filtre.getEstatProces(), paginacioHelper.toSpringDataPageable(paginacioParams));
    }

    private Page<NotificacioMassivaEntity> findAmbFiltrePaginatByAdminEntitat(EntitatEntity entitat, NotificacioMassivaFiltreDto filtre, PaginacioParamsDto paginacioParams){

        var dataIniciNull = filtre.getDataInici() == null;
        var dataFiNull = filtre.getDataFi() == null;
        var estatProcessNull = filtre.getEstatProces() == null;
        var createdByNull = Strings.isNullOrEmpty(filtre.getCreatedByCodi());
        filtre.setDataFi(DatesUtils.incrementarDataFi(filtre.getDataFi()));
        return notificacioMassivaRepository.findEntitatAdminRolePage(entitat, dataIniciNull, filtre.getDataInici(), dataFiNull, filtre.getDataFi(), estatProcessNull,
                filtre.getEstatProces(), createdByNull, filtre.getCreatedByCodi(), paginacioHelper.toSpringDataPageable(paginacioParams));
    }

    private void enviarCorreuElectronic(NotificacioMassivaEntity notificacioMassiva) {

        if (Strings.isNullOrEmpty(notificacioMassiva.getEmail())) {
            return;
        }
        try {

            jmsTemplate.convertAndSend(EmailConstants.CUA_EMAIL_MASSIVA, notificacioMassiva.getId(), m -> {
                // Esperam 5 segons a enviar el correu per asseguar que ja s'hagi desat la notificació massiva
                m.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, 5000L);
                return m;
            });
        } catch (JmsException ex) {
            log.error("[NOT-MASSIVA] Hi ha hagut un error al intentar enviar el correu electrònic.", ex);
        }
    }

    private NotificacioMassivaEntity registrarNotificacioMassiva(EntitatEntity entitat, NotificacioMassivaDto notMassivaDto, int size) {

        var csvGesdocId = pluginHelper.gestioDocumentalCreate(PluginHelper.GESDOC_AGRUPACIO_MASSIUS_CSV, notMassivaDto.getFicheroCsvBytes());
        var zipGesdocId = pluginHelper.gestioDocumentalCreate(PluginHelper.GESDOC_AGRUPACIO_MASSIUS_ZIP, notMassivaDto.getFicheroZipBytes());
        var informeGesdocId = pluginHelper.gestioDocumentalCreate(PluginHelper.GESDOC_AGRUPACIO_MASSIUS_INFORMES, new byte[0]);
        var errorsGesdocId = pluginHelper.gestioDocumentalCreate(PluginHelper.GESDOC_AGRUPACIO_MASSIUS_ERRORS, new byte[0]);
        PagadorPostalEntity pagadorPostal = null;
        if (notMassivaDto.getPagadorPostalId() != null) {
            pagadorPostal = pagadorPostalRepository.findById(notMassivaDto.getPagadorPostalId()).orElse(null);
        }
        NotificacioMassivaEntity notMassiva =  NotificacioMassivaEntity.builder()
                .entitat(entitat)
                .csvGesdocId(csvGesdocId)
                .zipGesdocId(zipGesdocId)
                .zipFilename(notMassivaDto.getFicheroZipNom())
                .csvFilename(notMassivaDto.getFicheroCsvNom())
                .caducitat(notMassivaDto.getCaducitat())
                .email(notMassivaDto.getEmail())
                .pagadorPostal(pagadorPostal)
                .resumGesdocId(informeGesdocId).errorsGesdocId(errorsGesdocId)
                .estatValidacio(NotificacioMassivaEstatDto.PENDENT)
                .estatProces(NotificacioMassivaEstatDto.PENDENT)
                .totalNotificacions(size)
                .notificacionsValidades(0)
                .notificacionsProcessades(0)
                .notificacionsProcessadesAmbError(0).build();
        notificacioMassivaRepository.saveAndFlush(notMassiva);
        return notMassiva;
    }

//    private String getKeyDocument(Notificacio notificacio) {
//
//        if (notificacio.getDocument() == null) {
//            return null;
//        }
//        if (notificacio.getDocument().getContingutBase64() != null && !notificacio.getDocument().getContingutBase64().isEmpty()) {
//            return notificacio.getDocument().getArxiuNom();
//        }
//        if (notificacio.getDocument().getUuid() != null) {
//            return notificacio.getDocument().getUuid();
//        }
//        if (notificacio.getDocument().getCsv() != null) {
//            return notificacio.getDocument().getCsv();
//        }
//        return null;
//    }
//
//    private Notificacio csvLiniaToNotificacioV2(String[] linia, Date caducitat, EntitatEntity entitat, String usuariCodi, List<String> fileNames,
//                                                byte[] ficheroZipBytes, Map<String, Long> documentsProcessatsMassiu, Map<String, Document> documents, Integer fila) {
//
//        log.debug("[NOT-MASSIVA] Construeix notificació de les dades del fitxer CSV");
//        var notV2 = new Notificacio();
//        var envV2 = new Enviament();
//        List<Enviament> envsV2 = new ArrayList<>();
//        var document = new Document();
//        var missatge = "";
//        var columna = "";
//        try {
//            notV2.setCaducitat(caducitat);
//            // Organ gestor
//            notV2.setOrganGestor(linia[0]);
//            // Entitat
//            notV2.setEmisorDir3Codi(entitat.getDir3Codi());
//            // Concepte
//            notV2.setConcepte(linia[1]);
//            // Tipus enviament
//            setTipusEnviament(notV2, linia[2]);
//            // Grup
//            notV2.setGrupCodi(null);
//            // Idioma
//            notV2.setIdioma(null);
//            // Usuari
//            notV2.setUsuariCodi(usuariCodi);
//            // Retard
//            setRetard(notV2, linia[15]);
//            // Procediment
//            notV2.setProcedimentCodi(linia[16]);
//            // Fecha envío programado
//            setDataProgramada(notV2, linia[17]);
//            // Document
//            var llegirMetadades = setDocument(notV2, document, linia, fileNames, ficheroZipBytes, documentsProcessatsMassiu, documents);
//            if (llegirMetadades) {
//                setOrigen(notV2, linia[18]);
//                setValidesa(notV2, linia[19]);
//                setTipusDocumental(notV2, linia[20]);
//                setModeFirma(notV2, linia[21]);
//            } else if (notV2.getDocument() != null){ //Metadades per defecte
//                setOrigen(notV2, OrigenEnum.ADMINISTRACIO.name());
//                setValidesa(notV2, ValidesaEnum.ORIGINAL.name());
//                setTipusDocumental(notV2, TipusDocumentalEnum.ALTRES.name());
//                setModeFirma(notV2, String.valueOf(false));
//            }
//            // Descripció
//            notV2.setDescripcio(linia.length > 19 ? linia[22] : linia[18]);
//            // Enviaments ////////////////
//
//            // Referencia - Núm. expedient
//            // TODO: #641 - Els enviaments massius encara que s'ompli el camp de "Referencia Emisor" al csv no es mostra al llistat de remeses al camp "Número expedient"
//            var referencia = (linia[3] != null && !linia[3].isEmpty()) ? linia[3] : null;
//            notV2.setNumExpedient(referencia);
//            // TODO assignar valor correcte a NotificaReferencai
//            envV2.setEntregaDehActiva(false); // De momento dejamos false
//            // Servei tipus
//            setServeiTipus(notV2, envV2, linia[6]);
//            // Titular /////////////
//            var titular = new Persona();
//            // Nom
//            titular.setNom(linia[7]);
//            // Llinatges
//            titular.setLlinatge1(linia[8]); // vienen ap1 y ap2 juntos
//            titular.setLlinatge2(null);
//            // NIF
//            titular.setNif(linia[9]);
//            // Email
//            titular.setEmail(linia[10]);
//            // Interessat tipus
//            missatge = messageHelper.getMessage("error.csv.to.notificacio.enviaments.interessat.tipus.missatge");
//            // TODO:  Igual lo hemos planteado mal. Si es un nif, podria ser el Nif de la administración.
//            //Entiendo que el "Código destino" = linia[11] solo se informará en caso de ser una administración
//            //Si es persona física o jurídica no tiene sentido
//            //Entonces podriamos utilizar este campo para saber si es una administración
//            setInteressatTipus(notV2, titular);
//            var senseNif = InteressatTipus.FISICA_SENSE_NIF.equals(titular.getInteressatTipus());
//            envV2.setPerEmail(senseNif);
//            if (senseNif) {
//                var tipus = Strings.isNullOrEmpty(linia[12]) ? DocumentTipus.ALTRE :
//                        DocumentTipus.PASSAPORT.name().equals(linia[12].toUpperCase()) ? DocumentTipus.PASSAPORT :
//                                DocumentTipus.ESTRANGER.name().equals(linia[12].toUpperCase()) ? DocumentTipus.ESTRANGER : DocumentTipus.ALTRE;
//                titular.setDocumentTipus(tipus);
//            } else {
//                // Entrega postal
//                setEntregaPostal(linia, entitat, envV2);
//            }
//            // Codi Dir3
//            titular.setDir3Codi(linia[11]);
//            // Incapacitat
//            titular.setIncapacitat(false);
//            envV2.setTitular(titular);
//            envsV2.add(envV2);
//            notV2.setEnviaments(envsV2);
//        } catch (Exception e) {
//            throw new NotificacioMassivaException(fila, columna, "Error " + missatge, e);
//        }
//
//        return notV2;
//    }
//
//    private void setTipusEnviament(Notificacio notificacio, String strTipusEnviament) {
//
//        if (Strings.isNullOrEmpty(strTipusEnviament)) {
//            return;
//        }
//        if ("C".equalsIgnoreCase(strTipusEnviament) || "COMUNICACIO".equalsIgnoreCase(strTipusEnviament) || "COMUNICACION".equalsIgnoreCase(strTipusEnviament) ||
//            "COMUNICACIÓ".equalsIgnoreCase(strTipusEnviament) || "COMUNICACIÓN".equalsIgnoreCase(strTipusEnviament)) {
//
//            notificacio.setEnviamentTipus(EnviamentTipus.COMUNICACIO);
//            return;
//        }
//        if ("N".equalsIgnoreCase(strTipusEnviament) || "NOTIFICACIO".equalsIgnoreCase(strTipusEnviament) || "NOTIFICACION".equalsIgnoreCase(strTipusEnviament) ||
//            "NOTIFICACIÓ".equalsIgnoreCase(strTipusEnviament) || "NOTIFICACIÓN".equalsIgnoreCase(strTipusEnviament)) {
//
//            notificacio.setEnviamentTipus(EnviamentTipus.NOTIFICACIO);
//            return;
//        }
//        if ("S".equalsIgnoreCase(strTipusEnviament) || "SIR".equalsIgnoreCase(strTipusEnviament)) {
//            notificacio.setEnviamentTipus(EnviamentTipus.SIR);
//        }
//    }
//
//    private void setRetard(Notificacio notificacio, String strRetard) {
//
//        if (isEnter(strRetard)) {
//            notificacio.setRetard(Integer.valueOf(strRetard));
//
//        }
//    }
//
//    private void setDataProgramada(Notificacio notificacio, String strData) {
//
//        try {
//            notificacio.setEnviamentDataProgramada(!Strings.isNullOrEmpty(strData) ? new SimpleDateFormat("dd/MM/yyyy").parse(strData) : null);
//        } catch (ParseException e) {
//            notificacio.setEnviamentDataProgramada(null);
//        }
//    }
//
//    private boolean setDocument(Notificacio notificacio, Document document, String[] linia, List<String> fileNames, byte[] ficheroZipBytes,
//                                Map<String, Long> documentsProcessatsMassiu, Map<String, Document> documents) {
//
//        var llegirMetadades = false;
//        if (linia[4] == null || linia[4].isEmpty()) {
//            notificacio.setDocument(null);
//            return llegirMetadades;
//        }
//        var arxiuNom = linia[4];
//        var count = 0;
//        var nom = "";
//        for (var name : fileNames) {
//            if (!name.contains(arxiuNom)) {
//                continue;
//            }
//            nom = name;
//            count++;
//        }
//        if (count != 1) {
//            // TODO COMPROVAR SI AQUESTA VALIDACIO ES POT PASSAR AL VALIDATOR (I NOMÉS FER-LA EN LES MASSIVES)
//            var msg = count == 0 ? messageHelper.getMessage("error.document.no.trobat.dins.zip") : messageHelper.getMessage("error.document.indeterminat.dins.zip");
////            notificacio.getErrors().add(msg);
//        } else {
//            arxiuNom = nom;
//        }
//        byte[] arxiuBytes;
//        if (fileNames.contains(arxiuNom)) { // Archivo físico
//            document.setArxiuNom(arxiuNom);
//            if (documentsProcessatsMassiu.isEmpty() || !documentsProcessatsMassiu.containsKey(document.getArxiuNom()) ||
//                    (documentsProcessatsMassiu.containsKey(document.getArxiuNom()) && documentsProcessatsMassiu.get(document.getArxiuNom()) == null)) {
//
//                arxiuBytes = ZipFileUtils.readZipFile(ficheroZipBytes, arxiuNom);
//                document.setContingutBase64(Base64.encodeBase64String(arxiuBytes));
//                document.setNormalitzat("Si".equalsIgnoreCase(linia[5]));
//                // TODO COMPROVAR QUE AL GUARDAR S'ESTA FENT BEN FET
//                document.setGenerarCsv(false);
//                document.setMediaType(URLConnection.guessContentTypeFromName(arxiuNom));
//                document.setMida(Long.valueOf(arxiuBytes.length));
//                if (registreNotificaHelper.isSendDocumentsActive()) {
//                    llegirMetadades = true;
//                }
//                documents.put(arxiuNom, document);
//            }
//            notificacio.setDocument(document);
//            return llegirMetadades;
//        }
//
//        // TODO AQUESTA VALIDACIO S'HAURIA DE FER AL VALIDATOR. SINO S'ESTA FENT PASSAR-HO ALLÀ
//        var docSplit = arxiuNom.split("\\.");
//        if (docSplit.length > 1 && Arrays.asList("JPG", "JPEG", "ODT", "ODP", "ODS", "ODG", "DOCX", "XLSX", "PPTX",
//                "PDF", "PNG", "RTF", "SVG", "TIFF", "TXT", "XML", "XSIG", "CSIG", "HTML", "CSV", "ZIP")
//                .contains(docSplit[1].toUpperCase())) {
//
//            notificacio.setDocument(null);
//            // TODO COMPROVAR SI AQUESTA VALIDACIO ES POT PASSAR AL VALIDATOR (I NOMÉS FER-LA EN LES MASSIVES)
//            return llegirMetadades;
//        }
//        var uuidPattern = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-5][0-9a-f]{3}-[089ab][0-9a-f]{3}-[0-9a-f]{12}$";
//        var pUuid = Pattern.compile(uuidPattern);
//        var mUuid = pUuid.matcher(linia[4]);
//        if (mUuid.matches()) {
//            // Uuid
//            document.setUuid(linia[4]);
//            document.setNormalitzat("Si".equalsIgnoreCase(linia[5]));
//            document.setGenerarCsv(false);
//            if (registreNotificaHelper.isSendDocumentsActive()) {
//                llegirMetadades = true;
//            }
//            notificacio.setDocument(document);
//            return llegirMetadades;
//        }
//        // Csv
//        document.setCsv(linia[4]);
//        document.setNormalitzat("Si".equalsIgnoreCase(linia[5]));
//        document.setGenerarCsv(false);
//        if (registreNotificaHelper.isSendDocumentsActive()) {
//            llegirMetadades = true;
//        }
//        notificacio.setDocument(document);
//        return llegirMetadades;
//    }
//
//    private void setOrigen(Notificacio notificacio, String strOrigen) {
//
//        // Origen
//        if (Strings.isNullOrEmpty(strOrigen)) {
//            return;
//        }
//        if ("CIUTADA".equalsIgnoreCase(strOrigen) || "CIUDADANO".equalsIgnoreCase(strOrigen)) {
//            notificacio.getDocument().setOrigen(OrigenEnum.CIUTADA);
//            return;
//        }
//        if ("ADMINISTRACIO".equalsIgnoreCase(strOrigen) || "ADMINISTRACION".equalsIgnoreCase(strOrigen)) {
//            notificacio.getDocument().setOrigen(OrigenEnum.ADMINISTRACIO);
//            return;
//        }
//        notificacio.getDocument().setOrigen(OrigenEnum.CIUTADA);
//        var msg = messageHelper.getMessage("error.valor.origen.no.valid.a") + strOrigen + messageHelper.getMessage("error.valor.origen.no.valid.b");
//    }
//
//    private void setValidesa(Notificacio notificacio, String strValidesa) {
//
//        // Validesa
//        if (Strings.isNullOrEmpty(strValidesa)) {
//            return;
//        }
//        if ("ORIGINAL".equalsIgnoreCase(strValidesa)) {
//            notificacio.getDocument().setValidesa(ValidesaEnum.ORIGINAL);
//            return;
//        }
//        if ("COPIA".equalsIgnoreCase(strValidesa)) {
//            notificacio.getDocument().setValidesa(ValidesaEnum.COPIA);
//            return;
//        }
//        if ("COPIA AUTENTICA".equalsIgnoreCase(strValidesa)) {
//            notificacio.getDocument().setValidesa(ValidesaEnum.COPIA_AUTENTICA);
//            return;
//        }
//        notificacio.getDocument().setValidesa(ValidesaEnum.ORIGINAL);
//    }
//
//    private void setTipusDocumental(Notificacio notificacio, String strTipus) {
//
//        // Tipo documental
//        if (Strings.isNullOrEmpty(strTipus)) {
//            return;
//        }
//        var tipo = TipusDocumentalEnum.ALTRES;
//        try {
//            tipo = TipusDocumentalEnum.valueOf(strTipus.toUpperCase());
//        } catch (IllegalArgumentException e) {
////            notificacio.getErrors().add(
////                    messageHelper.getMessage("error.valor.tipus.documental.no.valid.a")
////                    + strTipus +
////                    messageHelper.getMessage("error.valor.tipus.documental.no.valid.b") +
////                    messageHelper.getMessage("error.valor.tipus.documental.no.valid.valors.a") +
////                    messageHelper.getMessage("error.valor.tipus.documental.no.valid.valors.b") +
////                    messageHelper.getMessage("error.valor.tipus.documental.no.valid.valors.c") +
////                    messageHelper.getMessage("error.valor.tipus.documental.no.valid.valors.d") +
////                    messageHelper.getMessage("error.valor.tipus.documental.no.valid.valors.e") +
////                    messageHelper.getMessage("error.valor.tipus.documental.no.valid.valors.f"));
//        }
//        notificacio.getDocument().setTipoDocumental(tipo);
//    }
//
//    private void setModeFirma(Notificacio notificacio, String strMode) {
//
//        // PDF firmat
//        if (Strings.isNullOrEmpty(strMode)) {
//            return;
//        }
//        if ("SI".equalsIgnoreCase(strMode) || "TRUE".equalsIgnoreCase(strMode)) {
//            notificacio.getDocument().setModoFirma(true);
//            return;
//        }
//        if ("NO".equalsIgnoreCase(strMode) || "FALSE".equalsIgnoreCase(strMode)) {
//            notificacio.getDocument().setModoFirma(false);
//        }
//    }
//
//    private void setEntregaPostal(String[] linia, EntitatEntity entitat, Enviament enviament) {
//
//        // Si no vienen Línea 1 y Código Postal
//        if (entitat.getEntregaCie() == null || !Strings.isNullOrEmpty(linia[12]) || !Strings.isNullOrEmpty(linia[14])) {
//            enviament.setEntregaPostalActiva(false);
//            return;
//        }
//        enviament.setEntregaPostalActiva(true);
//        var entregaPostal = EntregaPostal.builder()
//                .tipus(NotificaDomiciliConcretTipus.SENSE_NORMALITZAR)
//                .linea1(linia[12]).linea2(linia[13]).codiPostal(linia[14]).build();
//        enviament.setEntregaPostal(entregaPostal);
//    }
//
//    private void setServeiTipus(Notificacio notificacio, Enviament enviament, String strServeiTipus) {
//
//        if (Strings.isNullOrEmpty(strServeiTipus)) {
//            return;
//        }
//        if ("NORMAL".equalsIgnoreCase(strServeiTipus)) {
//            enviament.setServeiTipus(ServeiTipus.NORMAL);
//            return;
//        }
//        if ("URGENT".equalsIgnoreCase(strServeiTipus) || "URGENTE".equalsIgnoreCase(strServeiTipus)) {
//            enviament.setServeiTipus(ServeiTipus.URGENT);
//            return;
//        }
//        enviament.setServeiTipus(ServeiTipus.NORMAL);
//    }
//
//    private void setInteressatTipus(Notificacio notificacio, Persona titular) {
//
//        if (Strings.isNullOrEmpty(titular.getNif()) && !Strings.isNullOrEmpty(titular.getEmail())) {
//            titular.setInteressatTipus(InteressatTipus.FISICA_SENSE_NIF);
//            return;
//        }
//        if (Strings.isNullOrEmpty(titular.getNif()) && Strings.isNullOrEmpty(titular.getEmail())) {
//            return;
//        }
//        if (NifHelper.isValidCif(titular.getNif())) {
//            titular.setInteressatTipus(InteressatTipus.JURIDICA);
//            return;
//        }
//        if (NifHelper.isValidNifNie(titular.getNif())) {
//            titular.setInteressatTipus(InteressatTipus.FISICA);
//            return;
//        }
//        var lista = pluginHelper.unitatsPerCodi(titular.getNif());
//        if (lista != null && !lista.isEmpty()) {
//            titular.setInteressatTipus(InteressatTipus.ADMINISTRACIO);
//        }
//    }
//
//    public boolean isNumeric(String strNum) {
//
//        if (strNum == null) {
//            return false;
//        }
//        try {
//            Double.parseDouble(strNum);
//            return true;
//        } catch (NumberFormatException nfe) {
//            return false;
//        }
//    }
//
//    public boolean isEnter(String strNum) {
//
//        if (strNum == null) {
//            return false;
//        }
//        try {
//            Integer.parseInt(strNum);
//        } catch (NumberFormatException nfe) {
//            return false;
//        }
//        return true;
//    }

    private ICsvListWriter initCsvWritter(Writer writer) {
        return new CsvListWriter(writer, CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE);
    }

    private ICsvListWriter writeCsvHeader(ICsvListWriter listWriter, List<String> csvHeader) {

        try {
            listWriter.writeHeader(csvHeader.toArray(new String[0]));
            return listWriter;
        } catch (IOException e) {
            log.error("S'ha produït un error a l'escriure la capçalera de l'fitxer CSV.", e);
            throw new WriteCsvException(messageHelper.getMessage("error.escriure.capcalera.fitxer.csv"));
        }
    }

    private void writeCsvLinia(ICsvListWriter listWriter, List<String> linia, List<String> errors) {

        var liniaAmbErrors = new ArrayList<>(linia);
        if (errors != null && !errors.isEmpty()) {
            liniaAmbErrors.add(errors.stream().collect(Collectors.joining(", ")));
        }

        try {
            listWriter.write(liniaAmbErrors);
        } catch (IOException e) {
            log.error("S'ha produït un error a l'escriure la línia en el fitxer CSV.", e);
            throw new WriteCsvException(messageHelper.getMessage("error.escriure.linia.fitxer.csv"));
        }
    }

    private void writeCsvClose(ICsvListWriter listWriter) {

        try {
            if( listWriter != null ) {
                listWriter.close();
            }
        } catch (IOException e) {
            log.error("S'ha produït un error a l'tancar el fitxer CSV.", e);
            throw new WriteCsvException(messageHelper.getMessage("error.tancar.fitxer.csv"));
        }
    }

    private NotificacioMassivaPrioritatDto getPrioritatNotificacioMassiva(){

        var tipus = NotificacioMassivaPrioritatDto.BAIXA;
        try {
            var tipusStr = configHelper.getConfig("es.caib.notib.enviament.massiu.prioritat");
            if (!Strings.isNullOrEmpty(tipusStr)) {
                tipus = NotificacioMassivaPrioritatDto.valueOf(tipusStr);
            }
        } catch (Exception ex) {
            log.error("No s'ha pogut obtenir la prioritat de la notificació massiva per defecte. S'utilitzarà la BAIXA.");
        }
        return tipus;
    }
}
