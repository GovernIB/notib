package es.caib.notib.logic.service;

import com.google.common.base.Strings;
import es.caib.notib.client.domini.DocumentTipus;
import es.caib.notib.client.domini.InteressatTipus;
import es.caib.notib.client.domini.NotificaDomiciliConcretTipus;
import es.caib.notib.client.domini.OrigenEnum;
import es.caib.notib.client.domini.TipusDocumentalEnum;
import es.caib.notib.client.domini.ValidesaEnum;
import es.caib.notib.logic.intf.dto.*;
import es.caib.notib.logic.intf.dto.cie.EntregaPostalDto;
import es.caib.notib.logic.intf.dto.notenviament.NotEnviamentDatabaseDto;
import es.caib.notib.logic.intf.dto.notificacio.*;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerDto;
import es.caib.notib.logic.intf.exception.AccessDeniedException;
import es.caib.notib.logic.intf.exception.InvalidCSVFileException;
import es.caib.notib.logic.intf.exception.InvalidCSVFileNotificacioMassivaException;
import es.caib.notib.logic.intf.exception.MaxLinesExceededException;
import es.caib.notib.logic.intf.exception.NoDocumentException;
import es.caib.notib.logic.intf.exception.NoMetadadesException;
import es.caib.notib.logic.intf.exception.NotificacioMassivaException;
import es.caib.notib.logic.intf.exception.RegistreNotificaException;
import es.caib.notib.logic.intf.exception.WriteCsvException;
import es.caib.notib.logic.intf.service.NotificacioMassivaService;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.entity.NotificacioEventEntity;
import es.caib.notib.persist.entity.NotificacioMassivaEntity;
import es.caib.notib.persist.entity.cie.PagadorPostalEntity;
import es.caib.notib.logic.exception.DocumentNotFoundException;
import es.caib.notib.logic.helper.*;
import es.caib.notib.persist.repository.EnviamentTableRepository;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import es.caib.notib.persist.repository.NotificacioEventRepository;
import es.caib.notib.persist.repository.NotificacioMassivaRepository;
import es.caib.notib.persist.repository.NotificacioRepository;
import es.caib.notib.persist.repository.NotificacioTableViewRepository;
import es.caib.notib.persist.repository.PagadorPostalRepository;
import es.caib.notib.persist.repository.ProcSerRepository;
import es.caib.notib.logic.utils.CSVReader;
import es.caib.notib.logic.utils.ZipFileUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URLConnection;
import java.nio.file.NoSuchFileException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private EnviamentTableRepository enviamentTableRepository;
    @Autowired
    private NotificacioEnviamentRepository enviamentRepository;
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

    private static final int MAX_ENVIAMENTS = 999;

    @Override
    public NotificacioMassivaDataDto findById(Long entitatId, Long id) {

        entityComprovarHelper.comprovarEntitat(entitatId);
        var notificacioMassiva = notificacioMassivaRepository.findById(id).orElse(null);
        return conversioTipusHelper.convertir(notificacioMassiva, NotificacioMassivaDataDto.class);
    }

    @Override
    @Transactional
    public NotificacioMassivaInfoDto getNotificacioMassivaInfo(Long entitatId, Long notificacioMassivaId) {

        entityComprovarHelper.comprovarEntitat(entitatId);
        var notificacioMassiva = notificacioMassivaRepository.findById(notificacioMassivaId).orElse(null);
        var baos = new ByteArrayOutputStream();
        pluginHelper.gestioDocumentalGet(notificacioMassiva.getResumGesdocId(), PluginHelper.GESDOC_AGRUPACIO_MASSIUS_INFORMES, baos);

        var linies = CSVReader.readFile(baos.toByteArray());
        List<NotificacioMassivaInfoDto.NotificacioInfo> info = new ArrayList<>();
        int foo = 0;
        NotificacioMassivaInfoDto.NotificacioInfo.NotificacioInfoBuilder builder;
        for (var linea : linies) {
            builder = NotificacioMassivaInfoDto.NotificacioInfo.builder()
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
                builder.origen(linea[18]).estadoElaboracion(linea[19]).tipoDocumental(linea[20]).pdfFirmado(linea[21]).errores(linea[22]);
            } else {
                builder.errores(linea.length == 21 ? linea[20] : linea[18]);
            }
            if (messageHelper.getMessage("notificacio.massiva.cancelada").equals(linea[linea.length - 1])) {
                builder.cancelada(true);
            }
            if (notificacioMassiva.getNotificacions() != null && !notificacioMassiva.getNotificacions().isEmpty()) {
                var not = notificacioMassiva.getNotificacions().get(foo);
                String error = "";
                var events = notificacioEventRepository.findByNotificacioIdAndErrorIsTrue(not.getId());
                for (var event : events) {
                    error += !Strings.isNullOrEmpty(event.getErrorDescripcio()) ? "\n" + event.getErrorDescripcio() : "";
                }
                builder.errorsExecucio(error);
            }
            info.add(builder.build());
        }
        var dto = conversioTipusHelper.convertir(notificacioMassiva, NotificacioMassivaInfoDto.class);
        dto.setSummary(info);
        return dto;
    }

    @Transactional(rollbackFor=Exception.class, timeout = 900)
    @Override
    public NotificacioMassivaDataDto create(Long entitatId, @NonNull String usuariCodi, @NonNull NotificacioMassivaDto notificacioMassiva) throws RegistreNotificaException {

        var timer = metricsHelper.iniciMetrica();
        try {
            log.info("[NOT-MASSIVA] Alta de nova notificacio massiva (usuari: {}). Fitxer csv: {}", usuariCodi, notificacioMassiva.getFicheroCsvNom());
            var entitat = entityComprovarHelper.comprovarEntitat(entitatId);
            var csvHeader = CSVReader.readHeader(notificacioMassiva.getFicheroCsvBytes());
            var linies = CSVReader.readFile(notificacioMassiva.getFicheroCsvBytes());
            checkCSVContent(linies, csvHeader);
            var fileNames = ZipFileUtils.readZipFileNames(notificacioMassiva.getFicheroZipBytes());
            Map<String, Long> documentsProcessatsMassiu = new HashMap<>(); // key: csv/uuid/arxiuFisicoNom - value: documentEntity.getId()
            var writerListErrors = new StringWriter();
            var writerListInforme = new StringWriter();
            csvHeader.add("Errores");
            var listWriterErrors = initCsvWritter(writerListErrors);
            var listWriterInforme = initCsvWritter(writerListInforme);
            writeCsvHeader(listWriterErrors, csvHeader.toArray(new String[]{}));
            writeCsvHeader(listWriterInforme, csvHeader.toArray(new String[]{}));

            var numAltes = 0;
            var fila = 1;
            var notificacioMassivaEntity = registrarNotificacioMassiva(entitat, notificacioMassiva, linies.size());
            for (var linia : linies) {
                if (linia.length < numberRequiredColumns()) {
                    break;
                }
                linia = trim(linia);
                var notificacio = csvToNotificaDatabaseDto(linia, notificacioMassiva.getCaducitat(), entitat, usuariCodi, fileNames,
                                                                            notificacioMassiva.getFicheroZipBytes(),documentsProcessatsMassiu, fila++);
                var keyDocument = getKeyDocument(notificacio);
                if (keyDocument != null && !documentsProcessatsMassiu.containsKey(keyDocument)) {
                    documentsProcessatsMassiu.put(keyDocument, null);
                }
                var errors = notificacioValidatorHelper.validarNotificacioMassiu(notificacio, entitat, documentsProcessatsMassiu);
                try {
                    var procediment = !Strings.isNullOrEmpty(notificacio.getProcediment().getCodi()) ?
                            procSerRepository.findByCodiAndEntitat(notificacio.getProcediment().getCodi(), entitat) : null;
                    if (procediment == null && !NotificaEnviamentTipusEnumDto.COMUNICACIO.equals(notificacio.getEnviamentTipus())) {
                        errors.add(messageHelper.getMessage("error.validacio.procser.amb.codi.no.trobat"));
                    } else if (procediment != null && ProcSerTipusEnum.SERVEI.equals(procediment.getTipus()) && NotificaEnviamentTipusEnumDto.NOTIFICACIO.equals(notificacio.getEnviamentTipus())) {
                        errors.add(messageHelper.getMessage("error.validacio.alta.notificacio.amb.servei.nomes.comunicacions"));
                    } else if (procediment != null) {
                        notificacio.setProcediment(conversioTipusHelper.convertir(procediment, ProcSerDto.class));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    errors.add(messageHelper.getMessage("error.obtenint.procediment.amb.codi") + notificacio.getProcediment().getCodi());
                }

                if (errors.size() == 0) {
                    try {
                        crearNotificacio(entitat, notificacio, notificacioMassivaEntity, documentsProcessatsMassiu);
                    } catch (DocumentNotFoundException | NoDocumentException ex) {
                        errors.add(messageHelper.getMessage("error.obtenint.document.arxiu"));
                    } catch (NoMetadadesException ex) {
                        errors.add(messageHelper.getMessage("error.metadades.document"));
                    }
                }
                if (errors.size() > 0) {
                    log.debug("[NOT-MASSIVA] Alta errònea de la notificació de la nova notificacio massiva");
                    writeCsvLinia(listWriterErrors, linia, errors);
                    writeCsvLinia(listWriterInforme, linia, errors);
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

    @Transactional
    @Override
    public PaginaDto<NotificacioTableItemDto> findNotificacions(Long entitatId, Long notificacioMassivaId, NotificacioFiltreDto filtre, PaginacioParamsDto paginacioParams) {

        var entitatActual = entityComprovarHelper.comprovarEntitat(entitatId, false, false, false);
        var pageable = notificacioListHelper.getMappeigPropietats(paginacioParams);
        var filtreNetejat = notificacioListHelper.getFiltre(filtre);
        var notificacions = notificacioTableViewRepository.findAmbFiltreByNotificacioMassiva(
                filtreNetejat.getEntitatId().isNull(),
                filtreNetejat.getEntitatId().getField(),
                notificacioMassivaRepository.findById(notificacioMassivaId).orElse(null),
                filtreNetejat.getEnviamentTipus().isNull(),
                filtreNetejat.getEnviamentTipus().getField(),
                filtreNetejat.getConcepte().isNull(),
                filtreNetejat.getConcepte().getField(),
                filtreNetejat.getEstat().isNull(),
                filtreNetejat.getEstat().isNull() ? 0 : filtreNetejat.getEstat().getField().getMask(),
//                !filtreNetejat.getEstat().isNull() ?
//                        EnviamentEstat.valueOf(filtreNetejat.getEstat().getField().toString()) : null,
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
//                filtreNetejat.getHasZeronotificaEnviamentIntent().isNull(),
//                filtreNetejat.getHasZeronotificaEnviamentIntent().getField(),
                pageable);

        var auth = SecurityContextHolder.getContext().getAuthentication();
        return notificacioListHelper.complementaNotificacions(entitatActual, auth.getName(), notificacions);
    }

    private String[] trim(String[] linia) {

        String camp;
        for(var i = 0; i < linia.length; i++) {
            camp = linia[i] != null ? linia[i].trim() : null;
            if (camp != null && camp.isEmpty()) {
                camp = null;
            }
            linia[i] = camp;
        }
        return linia;
    }

    @Override
    public void delete(Long entitatId, Long notificacioMassivaId) {

        entityComprovarHelper.comprovarEntitat(entitatId);
        var notificacioMassiva = notificacioMassivaRepository.findById(notificacioMassivaId).orElseThrow();
        if (notificacioMassiva.getNotificacions().size() == 0) {
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
        pluginHelper.gestioDocumentalGet(massiva.getResumGesdocId(), PluginHelper.GESDOC_AGRUPACIO_MASSIUS_INFORMES, baos);
        pluginHelper.gestioDocumentalGet(massiva.getCsvGesdocId(), PluginHelper.GESDOC_AGRUPACIO_MASSIUS_CSV, baosCsv);
        var linies = CSVReader.readFile(baos.toByteArray());
        var liniesCsv = CSVReader.readFile(baos.toByteArray());
        var notificacions = massiva.getNotificacions();

        try {
            var cancelada = false;
            var writerListErrors = new StringWriter();
            var writerListInforme = new StringWriter();
            var listWriterInforme = initCsvWritter(writerListInforme);
            var listWriterErrors = initCsvWritter(writerListErrors);
            var header = CSVReader.readHeader(baos.toByteArray());
            writeCsvHeader(listWriterInforme, header.toArray(new String[]{}));
            writeCsvHeader(listWriterErrors, header.toArray(new String[]{}));
            var ok = messageHelper.getMessage("notificacio.massiva.ok.validacio");
            NotificacioEntity not;
            String[] linia;
            String[] liniaCsv;
            String txt;
            List<String> msg;
            for (var foo = 0; foo < notificacions.size(); foo++) {
                not = notificacions.get(foo);
                linia = linies.get(foo);
                liniaCsv = liniesCsv.get(foo);
                txt = NotificacioEstatEnumDto.PENDENT.equals(not.getEstat()) ? messageHelper.getMessage("notificacio.massiva.cancelada") : "";
                msg = Collections.singletonList(txt);
                if (!ok.equals(linia[linia.length-1]) || !Strings.isNullOrEmpty(txt)) {
                    msg = !ok.equals(linia[linia.length-1]) ? Collections.singletonList("") : msg;
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
            var content = writerListErrors.toString().getBytes();
            pluginHelper.gestioDocumentalUpdate(massiva.getErrorsGesdocId(), PluginHelper.GESDOC_AGRUPACIO_MASSIUS_ERRORS, content);
            listWriterInforme.flush();
            content = writerListInforme.toString().getBytes();
            pluginHelper.gestioDocumentalUpdate(massiva.getResumGesdocId(), PluginHelper.GESDOC_AGRUPACIO_MASSIUS_INFORMES, content);
        } catch (Exception ex) {
            log.error("Error cancelant la notificacio massiva " + notificacioMassivaId);
            throw ex;
        }
    }

    @Transactional
    public byte [] afegirErrorsProcessat(NotificacioMassivaEntity massiva, byte[] contingut, boolean fitxerErrors) {

        var files = CSVReader.readFile(contingut);
        var notificacions = massiva.getNotificacions();
        if (notificacions == null) {
            return "".getBytes();
        }
        try {
            var writer = new StringWriter();
            var listWriter = initCsvWritter(writer);
            List<String> errors;
            String error = "";
            var csvHeader = CSVReader.readHeader(contingut);
            if (fitxerErrors) {
                csvHeader = new ArrayList<>();
                csvHeader.add("Referencia Notib");
                csvHeader.add("Desc");
            }
            csvHeader.add("Errores exec");
            writeCsvHeader(listWriter, csvHeader.toArray(new String[]{}));
            NotificacioEntity not;
            List<NotificacioEventEntity> events;
            for (var foo = 0; foo < notificacions.size(); foo++) {
                errors = new ArrayList<>();
                error = "";
                not = notificacions.get(foo);
                events = notificacioEventRepository.findByNotificacioIdAndErrorIsTrue(not.getId());
                for (var event : events) {
                    error += !Strings.isNullOrEmpty(event.getErrorDescripcio()) ? "\n" +  event.getErrorDescripcio() : "";
                }
                errors.add(error);
                writeCsvLinia(listWriter, fitxerErrors ? new String[] {not.getReferencia(), not.getConcepte()} : files.get(foo), errors);
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
        pluginHelper.gestioDocumentalGet(notificacioMassiva.getCsvGesdocId(), PluginHelper.GESDOC_AGRUPACIO_MASSIUS_CSV, baos);
        var fitxer = FitxerDto.builder().nom(notificacioMassiva.getCsvFilename()).contentType("text").contingut(baos.toByteArray()).tamany(baos.size()).build();
        return fitxer;
    }

    @Override
    public FitxerDto getZipFile(Long entitatId, Long notificacioMassivaId) {

        entityComprovarHelper.comprovarEntitat(entitatId);
        var notificacioMassiva = notificacioMassivaRepository.findById(notificacioMassivaId).orElseThrow();
        var baos = new ByteArrayOutputStream();
        pluginHelper.gestioDocumentalGet(notificacioMassiva.getZipGesdocId(), PluginHelper.GESDOC_AGRUPACIO_MASSIUS_ZIP, baos);
        return FitxerDto.builder().nom(notificacioMassiva.getZipFilename()).contentType("text").contingut(baos.toByteArray()).tamany(baos.size()).build();
    }

    @Override
    @Transactional
    public FitxerDto getResumFile(Long entitatId, Long notificacioMassivaId) {

        entityComprovarHelper.comprovarEntitat(entitatId);
        var notificacioMassiva = notificacioMassivaRepository.findById(notificacioMassivaId).orElseThrow();
        var baos = new ByteArrayOutputStream();
        pluginHelper.gestioDocumentalGet(notificacioMassiva.getResumGesdocId(), PluginHelper.GESDOC_AGRUPACIO_MASSIUS_INFORMES, baos);
        var fitxer = FitxerDto.builder().nom("resum.csv").contentType("text").contingut(baos.toByteArray()).tamany(baos.size()).build();
        var errors = afegirErrorsProcessat(notificacioMassiva, fitxer.getContingut(), false);
        fitxer.setContingut(errors);
        return fitxer;
    }

    @Override
    public FitxerDto getErrorsValidacioFile(Long entitatId, Long notificacioMassivaId) {

        entityComprovarHelper.comprovarEntitat(entitatId);
        var notificacioMassiva = notificacioMassivaRepository.findById(notificacioMassivaId).orElseThrow();
        var baos = new ByteArrayOutputStream();
        pluginHelper.gestioDocumentalGet(notificacioMassiva.getErrorsGesdocId(), PluginHelper.GESDOC_AGRUPACIO_MASSIUS_ERRORS, baos);
        return FitxerDto.builder().nom("errors_validacio.csv").contentType("text").contingut(baos.toByteArray()).tamany(baos.size()).build();
    }

    @Override
    @Transactional
    public FitxerDto getErrorsExecucioFile(Long entitatId, Long notificacioMassivaId) {

        entityComprovarHelper.comprovarEntitat(entitatId);
        var notificacioMassiva = notificacioMassivaRepository.findById(notificacioMassivaId).orElseThrow();
        var baos = new ByteArrayOutputStream();
        var fitxer = FitxerDto.builder().nom("errors_execucio.csv").contentType("text").contingut(baos.toByteArray()).tamany(baos.size()).build();
        var errors = afegirErrorsProcessat(notificacioMassiva, fitxer.getContingut(), true);
        fitxer.setContingut(errors);
        return fitxer;
    }

    @Transactional
    @Override
    public byte[] getModelDadesCarregaMassiuCSV() throws NoSuchFileException, IOException {

        var timer = metricsHelper.iniciMetrica();
        try {
            var input = registreNotificaHelper.isSendDocumentsActive() ?
                        this.getClass().getClassLoader().getResourceAsStream("es/caib/notib/logic/plantillas/modelo_datos_carga_masiva_metadades.csv")
                        : this.getClass().getClassLoader().getResourceAsStream("es/caib/notib/logic/plantillas/modelo_datos_carga_masiva.csv");
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
            throw new MaxLinesExceededException(String.format("S'ha superat el màxim nombre de línies permès (%d) per al CSV de càrrega massiva.", MAX_ENVIAMENTS));
        }
        if (csvHeader.size() < numberRequiredColumns()) {
            throw new InvalidCSVFileNotificacioMassivaException(String.format("El fitxer CSV no conté totes les columnes necessaries. " +
                            "Nombre de columnes requerides: %d. Nombre de columnes trobades %d", numberRequiredColumns(), csvHeader.size()));
        }
    }

    public int numberRequiredColumns() {
        return registreNotificaHelper.isSendDocumentsActive() ? 22 : 18;
    }

    private void crearNotificacio(EntitatEntity entitat, NotificacioDatabaseDto notificacio, NotificacioMassivaEntity notMassiva, Map<String, Long> documentsProcessatsMassiu) throws RegistreNotificaException {

        log.debug("[NOT-MASSIVA] Creació de notificació de nova notificacio massiva");
        var notificacioEntity = notificacioHelper.saveNotificacio(entitat, notificacio,false, notMassiva, documentsProcessatsMassiu);
        log.debug("[NOT-MASSIVA] Alta notificació de nova notificacio massiva");
        notificacioHelper.altaEnviamentsWeb(entitat, notificacioEntity, notificacio.getEnviaments());
        notMassiva.joinNotificacio(notificacioEntity);
    }

    private Page<NotificacioMassivaEntity> findAmbFiltrePaginatByUser(EntitatEntity entitat, NotificacioMassivaFiltreDto filtre, PaginacioParamsDto paginacioParams){

        var auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth.getName() == null) {
            throw new AccessDeniedException("No s'ha pogut consultar el llistat de notificacions massives");
        }
        return notificacioMassivaRepository.findUserRolePage(
                entitat,
                auth.getName(),
                filtre.getDataInici() == null,
                filtre.getDataInici(),
                filtre.getDataFi() == null,
                filtre.getDataFi(),
                filtre.getEstatProces() == null,
                filtre.getEstatProces(),
                paginacioHelper.toSpringDataPageable(paginacioParams));
    }

    private Page<NotificacioMassivaEntity> findAmbFiltrePaginatByAdminEntitat(EntitatEntity entitat, NotificacioMassivaFiltreDto filtre, PaginacioParamsDto paginacioParams){

        return notificacioMassivaRepository.findEntitatAdminRolePage(
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
                paginacioHelper.toSpringDataPageable(paginacioParams));
    }

    private boolean enviarCorreuElectronic(NotificacioMassivaEntity notMassiva, @NonNull byte[] fileResumContent, @NonNull byte[] fileErrorsContent) throws Exception {

        if (notMassiva.getEmail() == null || notMassiva.getEmail().isEmpty()) {
            return false;
        }
        emailNotificacioMassivaHelper.sendMail(notMassiva, notMassiva.getEmail(), fileResumContent, fileErrorsContent);
        return true;
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
        var notMassiva =  NotificacioMassivaEntity.builder().entitat(entitat).csvGesdocId(csvGesdocId).zipGesdocId(zipGesdocId)
                .zipFilename(notMassivaDto.getFicheroZipNom()).csvFilename(notMassivaDto.getFicheroCsvNom()).caducitat(notMassivaDto.getCaducitat())
                .email(notMassivaDto.getEmail()).pagadorPostal(pagadorPostal).resumGesdocId(informeGesdocId).errorsGesdocId(errorsGesdocId)
                .estatValidacio(NotificacioMassivaEstatDto.PENDENT).estatProces(NotificacioMassivaEstatDto.PENDENT).totalNotificacions(size)
                .notificacionsValidades(0).notificacionsProcessades(0).notificacionsProcessadesAmbError(0).build();
        notificacioMassivaRepository.saveAndFlush(notMassiva);
        return notMassiva;
    }

    private String getKeyDocument(NotificacioDatabaseDto notificacio) {

        if (notificacio.getDocument() == null) {
            return null;
        }
        if (notificacio.getDocument().getContingutBase64() != null && !notificacio.getDocument().getContingutBase64().isEmpty()) {//arxiu
            return notificacio.getDocument().getArxiuNom();
        }
        if (notificacio.getDocument().getUuid() != null) {
            return notificacio.getDocument().getUuid();
        }
        if (notificacio.getDocument().getCsv() != null) {
            return notificacio.getDocument().getCsv();
        }
        return null;
    }

    private NotificacioDatabaseDto csvToNotificaDatabaseDto(String[] linia, Date caducitat, EntitatEntity entitat, String usuariCodi, List<String> fileNames,
                                                            byte[] ficheroZipBytes, Map<String, Long> documentsProcessatsMassiu, Integer fila) {

        log.debug("[NOT-MASSIVA] Construeix notificació de les dades del fitxer CSV");
        var notificacio = new NotificacioDatabaseDto();
        var enviament = new NotEnviamentDatabaseDto();
        List<NotEnviamentDatabaseDto> enviaments = new ArrayList<>();
        var document = new DocumentDto();
        var missatge = "";
        var columna = "";
        try {
            notificacio.setCaducitat(caducitat);
            // Organ gestor
            columna = messageHelper.getMessage("error.csv.to.notificacio.codi.organ.gestor.columna");
            missatge = messageHelper.getMessage("error.csv.to.notificacio.codi.organ.gestor.missatge");
            notificacio.setOrganGestorCodi(linia[0]);

            // Entitat
            columna = "";
            missatge = messageHelper.getMessage("error.csv.to.notificacio.codi.entitat.missatge");
            notificacio.setEmisorDir3Codi(entitat.getDir3Codi());

            // Concepte
            columna = messageHelper.getMessage("error.csv.to.notificacio.codi.concepte.columna");
            missatge = messageHelper.getMessage("error.csv.to.notificacio.codi.concepte.missatge");
            notificacio.setConcepte(linia[1]);

            // Descripció
            notificacio.setDescripcio(null);

            // Tipus enviament
            columna = messageHelper.getMessage("error.csv.to.notificacio.tipus.enviament.columna");
            missatge = messageHelper.getMessage("error.csv.to.notificacio.tipus.enviament.missatge");
            setTipusEnviament(notificacio, linia[2]);

            // Grup
            notificacio.setGrup(null);

            // Idioma
            notificacio.setIdioma(null);

            // Usuari
            notificacio.setUsuariCodi(usuariCodi);

            // Retard
            columna = messageHelper.getMessage("error.csv.to.notificacio.codi.retard.postal.columna");
            missatge = messageHelper.getMessage("error.csv.to.notificacio.codi.retard.postal.missatge");
            setRetard(notificacio, linia[15]);

            // Procediment
            columna = messageHelper.getMessage("error.csv.to.notificacio.codi.procediment.columna");
            missatge = messageHelper.getMessage("error.csv.to.notificacio.codi.procediment.missatge");
            var procediment = new ProcSerDto();
            procediment.setCodi(linia[16]);
            notificacio.setProcediment(procediment);

            // Fecha envío programado
            columna = messageHelper.getMessage("error.csv.to.notificacio.codi.data.enviament.programada.columna");
            missatge = messageHelper.getMessage("error.csv.to.notificacio.codi.data.enviament.programada.missatge");
            setDataProgramada(notificacio, linia[17]);

            // Document
            columna = messageHelper.getMessage("error.csv.to.notificacio.codi.document.columna");
            missatge = messageHelper.getMessage("error.csv.to.notificacio.codi.document.missatge");
            var llegirMetadades = setDocument(notificacio, document, linia, fileNames, ficheroZipBytes, documentsProcessatsMassiu);
            if (llegirMetadades) {
                columna = messageHelper.getMessage("error.csv.to.notificacio.codi.origen.columna");
                missatge = messageHelper.getMessage("error.csv.to.notificacio.codi.origen.missatge");
                setOrigen(notificacio, linia[18]);
                columna = messageHelper.getMessage("error.csv.to.notificacio.codi.estat.elaboracio.columna");
                missatge = messageHelper.getMessage("error.csv.to.notificacio.codi.estat.elaboracio.missatge");;
                setValidesa(notificacio, linia[19]);
                columna = messageHelper.getMessage("error.csv.to.notificacio.codi.tipus.documental.columna");
                missatge = messageHelper.getMessage("error.csv.to.notificacio.codi.tipus.documental.missatge");
                setTipusDocumental(notificacio, linia[20]);
                columna = messageHelper.getMessage("error.csv.to.notificacio.codi.pdf.firmat.columna");
                missatge = messageHelper.getMessage("error.csv.to.notificacio.codi.pdf.firmat.missatge");
                setModeFirma(notificacio, linia[21]);
            }

            // Enviaments ////////////////

            // Referencia - Núm. expedient
            // TODO: #641 - Els enviaments massius encara que s'ompli el camp de "Referencia Emisor" al csv no es mostra al llistat de remeses al camp "Número expedient"
            columna = messageHelper.getMessage("error.csv.to.notificacio.enviaments.referencia.columna");
            missatge = messageHelper.getMessage("error.csv.to.notificacio.enviaments.referencia.missatge");
            var referencia = (linia[3] != null && !linia[3].isEmpty()) ? linia[3] : null;
            notificacio.setNumExpedient(referencia);
            enviament.setNotificaReferencia(referencia); //si no se envía, Notific@ genera una
            enviament.setEntregaDehActiva(false); // De momento dejamos false

            // Servei tipus
            columna = messageHelper.getMessage("error.csv.to.notificacio.enviaments.prioritat.servei.columna");
            missatge = messageHelper.getMessage("error.csv.to.notificacio.enviaments.prioritat.servei.missatge");
            setServeiTipus(notificacio, enviament, linia[6]);

            // Titular /////////////
            var titular = new PersonaDto();

            // Nom
            columna = messageHelper.getMessage("error.csv.to.notificacio.enviaments.nom.columna");
            missatge = messageHelper.getMessage("error.csv.to.notificacio.enviaments.nom.missatge");
            titular.setNom(linia[7]);

            // Llinatges
            columna = messageHelper.getMessage("error.csv.to.notificacio.enviaments.llinatges.columna");
            missatge = messageHelper.getMessage("error.csv.to.notificacio.enviaments.llinatges.missatge");
            titular.setLlinatge1(linia[8]); // vienen ap1 y ap2 juntos
            titular.setLlinatge2(null);

            // NIF
            columna = messageHelper.getMessage("error.csv.to.notificacio.enviaments.cifnif.columna");
            missatge = messageHelper.getMessage("error.csv.to.notificacio.enviaments.cifnif.missatge");
            titular.setNif(linia[9]);

            // Email
            columna = messageHelper.getMessage("error.csv.to.notificacio.enviaments.email.columna");
            missatge = messageHelper.getMessage("error.csv.to.notificacio.enviaments.email.missatge");
            titular.setEmail(linia[10]);

            // Interessat tipus
            missatge = messageHelper.getMessage("error.csv.to.notificacio.enviaments.interessat.tipus.missatge");
            // TODO:  Igual lo hemos planteado mal. Si es un nif, podria ser el Nif de la administración.
            //Entiendo que el "Código destino" = linia[11] solo se informará en caso de ser una administración
            //Si es persona física o jurídica no tiene sentido
            //Entonces podriamos utilizar este campo para saber si es una administración
            setInteressatTipus(notificacio, titular);
            var senseNif = InteressatTipus.FISICA_SENSE_NIF.equals(titular.getInteressatTipus());
            enviament.setPerEmail(senseNif);

            if (senseNif) {
                log.error("FISICA SENSE NIF!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1111 +****************");
                var tipus = Strings.isNullOrEmpty(linia[12]) ? DocumentTipus.ALTRE :
                        DocumentTipus.PASSAPORT.name().equals(linia[12].toUpperCase()) ? DocumentTipus.PASSAPORT :
                        DocumentTipus.ESTRANGER.name().equals(linia[12].toUpperCase()) ? DocumentTipus.ESTRANGER : DocumentTipus.ALTRE;
                titular.setDocumentTipus(tipus);
            } else {
                // Entrega postal
                columna = messageHelper.getMessage("error.csv.to.notificacio.enviaments.entrega.postal.columna");
                missatge = messageHelper.getMessage("error.csv.to.notificacio.enviaments.entrega.postal.missatge");
                setEntregaPostal(linia, entitat, enviament);
            }
            // Codi Dir3
            columna = messageHelper.getMessage("error.csv.to.notificacio.enviaments.dir3.columna");
            missatge = messageHelper.getMessage("error.csv.to.notificacio.enviaments.dir3.missatge");
            titular.setDir3Codi(linia[11]);

            // Incapacitat
            titular.setIncapacitat(false);

            enviament.setTitular(titular);
            enviaments.add(enviament);
            notificacio.setEnviaments(enviaments);
            return notificacio;
        } catch (Exception e) {
            throw new NotificacioMassivaException(fila, columna, "Error " + missatge, e);
        }
    }

    private void setTipusEnviament(NotificacioDatabaseDto notificacio, String strTipusEnviament) {

        if (strTipusEnviament == null || strTipusEnviament.isEmpty()) {
            return;
        }
        if ("C".equalsIgnoreCase(strTipusEnviament) || "COMUNICACIO".equalsIgnoreCase(strTipusEnviament) || "COMUNICACION".equalsIgnoreCase(strTipusEnviament) ||
                "COMUNICACIÓ".equalsIgnoreCase(strTipusEnviament) || "COMUNICACIÓN".equalsIgnoreCase(strTipusEnviament)) {
            notificacio.setEnviamentTipus(NotificaEnviamentTipusEnumDto.COMUNICACIO);
            return;
        }
        if ("N".equalsIgnoreCase(strTipusEnviament) || "NOTIFICACIO".equalsIgnoreCase(strTipusEnviament) || "NOTIFICACION".equalsIgnoreCase(strTipusEnviament) ||
                "NOTIFICACIÓ".equalsIgnoreCase(strTipusEnviament) || "NOTIFICACIÓN".equalsIgnoreCase(strTipusEnviament)) {
            notificacio.setEnviamentTipus(NotificaEnviamentTipusEnumDto.NOTIFICACIO);
            return;
        }
        notificacio.setEnviamentTipus(NotificaEnviamentTipusEnumDto.COMUNICACIO);
        notificacio.getErrors().add(messageHelper.getMessage("error.tipus.enviament.no.valid.a") + strTipusEnviament + messageHelper.getMessage("error.tipus.enviament.no.valid.b"));
    }

    private void setRetard(NotificacioDatabaseDto notificacio, String strRetard) {

        if (isEnter(strRetard)) {
            notificacio.setRetard(Integer.valueOf(strRetard));
            return;
        }
        if (strRetard != null) {
            notificacio.getErrors().add(messageHelper.getMessage("error.retard.no.valid.a") + strRetard + messageHelper.getMessage("error.retard.no.valid.b"));
        }
    }

    private void setDataProgramada(NotificacioDatabaseDto notificacio, String strData) {

        if (strData == null || strData.isEmpty()) {
            return;
        }
        try {
            notificacio.setEnviamentDataProgramada(strData != null && !strData.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy").parse(strData) : null);
        } catch (ParseException e) {
            notificacio.setEnviamentDataProgramada(null);
            notificacio.getErrors().add(messageHelper.getMessage("error.format.data.programada.a") + strData + messageHelper.getMessage("error.format.data.programada.b"));
        }
    }

    private boolean setDocument(NotificacioDatabaseDto notificacio, DocumentDto document, String[] linia, List<String> fileNames, byte[] ficheroZipBytes, Map<String, Long> documentsProcessatsMassiu) {

        var llegirMetadades = false;
        if (linia[4] == null || linia[4].isEmpty()) {
            notificacio.setDocument(null);
            return llegirMetadades;
        }
        var arxiuNom = linia[4];
        var count = 0;
        var nom = "";
        for (var name : fileNames) {
            if (name.contains(arxiuNom)) {
                nom = name;
                count++;
            }
        }
        if (count != 1) {
            var msg = count == 0 ? messageHelper.getMessage("error.document.no.trobat.dins.zip") : messageHelper.getMessage("error.document.indeterminat.dins.zip");
            notificacio.getErrors().add(msg);
        } else {
            arxiuNom = nom;
        }
        if (fileNames != null && fileNames.contains(arxiuNom)) { // Archivo físico
            document.setArxiuNom(arxiuNom);
            byte[] arxiuBytes;
            if (documentsProcessatsMassiu.isEmpty() || !documentsProcessatsMassiu.containsKey(document.getArxiuNom()) ||
                    (documentsProcessatsMassiu.containsKey(document.getArxiuNom()) && documentsProcessatsMassiu.get(document.getArxiuNom()) == null)) {

                arxiuBytes = ZipFileUtils.readZipFile(ficheroZipBytes, arxiuNom);
                document.setContingutBase64(Base64.getEncoder().encodeToString(arxiuBytes));
                document.setNormalitzat("Si".equalsIgnoreCase(linia[5]));
                document.setGenerarCsv(false);
                document.setMediaType(URLConnection.guessContentTypeFromName(arxiuNom));
                document.setMida(Long.valueOf(arxiuBytes.length));
                if (registreNotificaHelper.isSendDocumentsActive()) {
                    llegirMetadades = true;
//                        leerMetadadesDelCsv(notificacio, document, linia);
                }
            }
            notificacio.setDocument(document);
            return llegirMetadades;
        }
        var docSplit = arxiuNom.split("\\.");
        if (docSplit.length > 1 && Arrays.asList("JPG", "JPEG", "ODT", "ODP", "ODS", "ODG", "DOCX", "XLSX", "PPTX",
                "PDF", "PNG", "RTF", "SVG", "TIFF", "TXT", "XML", "XSIG", "CSIG", "HTML", "CSV", "ZIP").contains(docSplit[1].toUpperCase())) {

            notificacio.setDocument(null);
            notificacio.getErrors().add(messageHelper.getMessage("error.document.no.trobat.dins.zip"));
            return llegirMetadades;
        }
        var uuidPattern = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-5][0-9a-f]{3}-[089ab][0-9a-f]{3}-[0-9a-f]{12}$";
        var pUuid = Pattern.compile(uuidPattern);
        var mUuid = pUuid.matcher(linia[4]);
        if (mUuid.matches()) {
            // Uuid
            document.setUuid(linia[4]);
            document.setNormalitzat("Si".equalsIgnoreCase(linia[5]));
            document.setGenerarCsv(false);
            if (registreNotificaHelper.isSendDocumentsActive()) {
                llegirMetadades = true;
//                            leerMetadadesDelCsv(notificacio, document, linia);
            }
            notificacio.setDocument(document);
            return llegirMetadades;
        }
        // Csv
        document.setCsv(linia[4]);
        document.setNormalitzat("Si".equalsIgnoreCase(linia[5]));
        document.setGenerarCsv(false);
        if (registreNotificaHelper.isSendDocumentsActive()) {
            llegirMetadades = true;
//                            leerMetadadesDelCsv(notificacio, document, linia);
        }
        notificacio.setDocument(document);
        return llegirMetadades;
    }

    private void setOrigen(NotificacioDatabaseDto notificacio, String strOrigen) {

        // Origen
        if (strOrigen == null || strOrigen.isEmpty()) {
            return;
        }
        if ("CIUTADA".equalsIgnoreCase(strOrigen) || "CIUDADANO".equalsIgnoreCase(strOrigen)) {
            notificacio.getDocument().setOrigen(OrigenEnum.CIUTADA);
            return;
        }
        if ("ADMINISTRACIO".equalsIgnoreCase(strOrigen) || "ADMINISTRACION".equalsIgnoreCase(strOrigen)) {
            notificacio.getDocument().setOrigen(OrigenEnum.ADMINISTRACIO);
            return;
        }
        notificacio.getDocument().setOrigen(OrigenEnum.CIUTADA);
        var msg = messageHelper.getMessage("error.valor.origen.no.valid.a") + strOrigen + messageHelper.getMessage("error.valor.origen.no.valid.b");
        notificacio.getErrors().add(msg);
    }

    private void setValidesa(NotificacioDatabaseDto notificacio, String strValidesa) {

        // Validesa
        if (strValidesa == null || strValidesa.isEmpty()) {
            return;
        }
        if ("ORIGINAL".equalsIgnoreCase(strValidesa)) {
            notificacio.getDocument().setValidesa(ValidesaEnum.ORIGINAL);
            return;
        }
        if ("COPIA".equalsIgnoreCase(strValidesa)) {
            notificacio.getDocument().setValidesa(ValidesaEnum.COPIA);
            return;
        }
        if ("COPIA AUTENTICA".equalsIgnoreCase(strValidesa)) {
            notificacio.getDocument().setValidesa(ValidesaEnum.COPIA_AUTENTICA);
            return;
        }
        notificacio.getDocument().setValidesa(ValidesaEnum.ORIGINAL);
        var msg = messageHelper.getMessage("error.valor.validesa.no.valid.a") + strValidesa + messageHelper.getMessage("error.valor.validesa.no.valid.b");
        notificacio.getErrors().add(msg);
    }

    private void setTipusDocumental(NotificacioDatabaseDto notificacio, String strTipus) {

        // Tipo documental
        if (strTipus == null || strTipus.isEmpty()) {
            return;
        }
        var tipo = TipusDocumentalEnum.ALTRES;
        try {
            tipo = TipusDocumentalEnum.valueOf(strTipus.toUpperCase());
        } catch (IllegalArgumentException e) {
            notificacio.getErrors().add(messageHelper.getMessage("error.valor.tipus.documental.no.valid.a")
                    + strTipus + messageHelper.getMessage("error.valor.tipus.documental.no.valid.b") +
                    messageHelper.getMessage("error.valor.tipus.documental.no.valid.valors.a") +
                    messageHelper.getMessage("error.valor.tipus.documental.no.valid.valors.b") +
                    messageHelper.getMessage("error.valor.tipus.documental.no.valid.valors.c") +
                    messageHelper.getMessage("error.valor.tipus.documental.no.valid.valors.d") +
                    messageHelper.getMessage("error.valor.tipus.documental.no.valid.valors.e") +
                    messageHelper.getMessage("error.valor.tipus.documental.no.valid.valors.f"));
        }
        notificacio.getDocument().setTipoDocumental(tipo);
    }

    private void setModeFirma(NotificacioDatabaseDto notificacio, String strMode) {

        // PDF firmat
        if (strMode == null || strMode.isEmpty()) {
            return;
        }
        if ("SI".equalsIgnoreCase(strMode) || "TRUE".equalsIgnoreCase(strMode)) {
            notificacio.getDocument().setModoFirma(true);
            return;
        }
        if ("NO".equalsIgnoreCase(strMode) || "FALSE".equalsIgnoreCase(strMode)) {
            notificacio.getDocument().setModoFirma(false);
            return;
        }
        var msg = messageHelper.getMessage("error.valor.validesa.no.valid.a") + strMode + messageHelper.getMessage("error.valor.validesa.no.valid.b");
        notificacio.getErrors().add(msg);
    }

    private void setEntregaPostal(String[] linia, EntitatEntity entitat, NotEnviamentDatabaseDto enviament) {

        if (entitat.getEntregaCie() != null &&
                linia[12] != null && !linia[12].isEmpty() && // Si vienen Línea 1 y Código Postal
                linia[14] != null && !linia[14].isEmpty()) {

            enviament.setEntregaPostalActiva(true);
            var entregaPostal = EntregaPostalDto.builder().domiciliConcretTipus(NotificaDomiciliConcretTipus.SENSE_NORMALITZAR)
                    .linea1(linia[12]).linea2(linia[13]).codiPostal(linia[14]).build();
            enviament.setEntregaPostal(entregaPostal);
        } else {
            enviament.setEntregaPostalActiva(false);
        }
    }

    private void setServeiTipus(NotificacioDatabaseDto notificacio, NotEnviamentDatabaseDto enviament, String strServeiTipus) {

        if (strServeiTipus == null || strServeiTipus.isEmpty()) {
            return;
        }
        if ("NORMAL".equalsIgnoreCase(strServeiTipus)) {
            enviament.setServeiTipus(ServeiTipusEnumDto.NORMAL);
            return;
        }
        if ("URGENT".equalsIgnoreCase(strServeiTipus) || "URGENTE".equalsIgnoreCase(strServeiTipus)) {
            enviament.setServeiTipus(ServeiTipusEnumDto.URGENT);
            return;
        }
        enviament.setServeiTipus(ServeiTipusEnumDto.NORMAL);
        notificacio.getErrors().add(messageHelper.getMessage("error.tipus.servei.no.valid.a") + strServeiTipus + messageHelper.getMessage("error.tipus.servei.no.valid.b"));

    }

    private void setInteressatTipus(NotificacioDatabaseDto notificacio, PersonaDto titular) {

        if (Strings.isNullOrEmpty(titular.getNif()) && !Strings.isNullOrEmpty(titular.getEmail())) {
            titular.setInteressatTipus(InteressatTipus.FISICA_SENSE_NIF);
            return;
        }
        if (Strings.isNullOrEmpty(titular.getNif()) && Strings.isNullOrEmpty(titular.getEmail())) {
            notificacio.getErrors().add(messageHelper.getMessage("error.persona.sense.nif.no.email"));
            return;
        }
        if (NifHelper.isValidCif(titular.getNif())) {
            titular.setInteressatTipus(InteressatTipus.JURIDICA);
            return;
        }
        if (NifHelper.isValidNifNie(titular.getNif())) {
            titular.setInteressatTipus(InteressatTipus.FISICA);
            return;
        }
        List<OrganGestorDto> lista = pluginHelper.unitatsPerCodi(titular.getNif());
        if (lista != null && lista.size() > 0) {
            titular.setInteressatTipus(InteressatTipus.ADMINISTRACIO);
            return;
        }
        notificacio.getErrors().add(messageHelper.getMessage("error.nifcif.no.valid.a") + titular.getNif() + messageHelper.getMessage("error.nifcif.no.valid.b"));
    }

    public boolean isNumeric(String strNum) {

        if (strNum == null) {
            return false;
        }
        try {
            Double.parseDouble(strNum);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    public boolean isEnter(String strNum) {

        if (strNum == null) {
            return false;
        }
        try {
            Integer.parseInt(strNum);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    private ICsvListWriter initCsvWritter(Writer writer) {
        return new CsvListWriter(writer, CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE);
    }

    private ICsvListWriter writeCsvHeader(ICsvListWriter listWriter, String[] csvHeader) {

        try {
            listWriter.writeHeader(csvHeader);
            return listWriter;
        } catch (IOException e) {
            log.error("S'ha produït un error a l'escriure la capçalera de l'fitxer CSV.", e);
            throw new WriteCsvException(messageHelper.getMessage("error.escriure.capcalera.fitxer.csv"));
        }
    }

    private void writeCsvLinia(ICsvListWriter listWriter, String[] linia, List<String> errors) {

        List<String> liniaAmbErrors = new ArrayList<>(Arrays.asList(linia));
        var sbErrors = new StringBuffer();
        for (var error : errors) {
            sbErrors.append(error);
        }
        liniaAmbErrors.add(sbErrors.toString());
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
            if (tipusStr != null && !tipusStr.isEmpty()) {
                tipus = NotificacioMassivaPrioritatDto.valueOf(tipusStr);
            }
        } catch (Exception ex) {
            log.error("No s'ha pogut obtenir la prioritat de la notificació massiva per defecte. S'utilitzarà la BAIXA.");
        }
        return tipus;
    }
}
