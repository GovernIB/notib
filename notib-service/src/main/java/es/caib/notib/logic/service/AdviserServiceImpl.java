package es.caib.notib.logic.service;

import com.codahale.metrics.Timer;
import com.google.common.base.Strings;
import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.logic.helper.AuditHelper;
import es.caib.notib.logic.helper.CallbackHelper;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.IntegracioHelper;
import es.caib.notib.logic.helper.MetricsHelper;
import es.caib.notib.logic.helper.NotificaHelper;
import es.caib.notib.logic.helper.NotificacioEventHelper;
import es.caib.notib.logic.helper.PluginHelper;
import es.caib.notib.logic.intf.dto.AccioParam;
import es.caib.notib.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioInfo;
import es.caib.notib.logic.intf.dto.NotificaCertificacioArxiuTipusEnumDto;
import es.caib.notib.logic.intf.dto.NotificaCertificacioTipusEnumDto;
import es.caib.notib.logic.intf.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.service.AdviserService;
import es.caib.notib.logic.intf.service.AuditService;
import es.caib.notib.logic.intf.ws.adviser.sincronizarenvio.Acuse;
import es.caib.notib.logic.intf.ws.adviser.sincronizarenvio.Receptor;
import es.caib.notib.logic.intf.ws.adviser.sincronizarenvio.ResultadoSincronizarEnvio;
import es.caib.notib.logic.intf.ws.adviser.sincronizarenvio.SincronizarEnvio;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
public class AdviserServiceImpl implements AdviserService {

    @Autowired
    private NotificacioEnviamentRepository notificacioEnviamentRepository;
    @Autowired
    private PluginHelper pluginHelper;
    @Autowired
    private NotificaHelper notificaHelper;
    @Autowired
    private IntegracioHelper integracioHelper;
    @Autowired
    private MetricsHelper metricsHelper;
    @Autowired
    private NotificacioEventHelper notificacioEventHelper;
    @Autowired
    private ConfigHelper configHelper;
    @Autowired
    private CallbackHelper callbackHelper;
    @Autowired
    private AuditHelper auditHelper;

    private final Object lock = new Object();

    @Override
    @Transactional
    public ResultadoSincronizarEnvio sincronizarEnvio(SincronizarEnvio sincronizarEnvio) {

        Timer.Context timer = metricsHelper.iniciMetrica();
        ResultadoSincronizarEnvio resultadoSincronizarEnvio = new ResultadoSincronizarEnvio();
        try {
            String identificador = sincronizarEnvio.getIdentificador();

            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm");
            Date dataEstat = toDate(sincronizarEnvio.getFechaEstado());

            log.info("[ADV] Inici sincronització enviament Adviser [");
            log.info("        Id: " + (identificador != null ? identificador : ""));
            log.info("        OrganismoEmisor: " + sincronizarEnvio.getOrganismoEmisor());
            log.info("        TipoEntrega: " + sincronizarEnvio.getTipoEntrega());
            log.info("        ModoNotificacion: " + sincronizarEnvio.getModoNotificacion());
            log.info("        Estat: " + sincronizarEnvio.getEstado());
            if (dataEstat != null) {
                log.info("        FechaEstado: " + sdf.format(dataEstat));
            }
            log.info("        Receptor: " + (sincronizarEnvio.getReceptor() != null ? sincronizarEnvio.getReceptor().getNifReceptor() : "") + "]");

            log.debug("--------------------------------------------------------------");
            log.debug("Processar petició dins l'Adviser...");

            IntegracioInfo info = new IntegracioInfo(
                    IntegracioHelper.INTCODI_NOTIFICA,
                    "Recepció de canvi de notificació via Adviser",
                    IntegracioAccioTipusEnumDto.RECEPCIO,
                    new AccioParam("Organisme emisor", sincronizarEnvio.getOrganismoEmisor()),
                    new AccioParam("Identificador", (identificador != null ? identificador : "")),
                    new AccioParam("Tipus d'entrega", String.valueOf(sincronizarEnvio.getTipoEntrega())),
                    new AccioParam("Mode de notificació", String.valueOf(sincronizarEnvio.getModoNotificacion())),
                    new AccioParam("Estat", sincronizarEnvio.getEstado()),
                    new AccioParam("Data de l'estat", dataEstat != null ? sdf.format(dataEstat) : ""),
                    new AccioParam("Receptor", sincronizarEnvio.getReceptor() != null ?
                            sincronizarEnvio.getReceptor().getNombreReceptor() + " (" + sincronizarEnvio.getReceptor().getNifReceptor() + ")" +
                                    (sincronizarEnvio.getReceptor().getNifRepresentante() != null ? " - Representant: " +
                                            sincronizarEnvio.getReceptor().getNombreRepresentante() + " (" + sincronizarEnvio.getReceptor().getNifRepresentante() + ")" : "") : ""),
                    new AccioParam("Acús en PDF (Hash)", sincronizarEnvio.getAcusePDF() != null ? sincronizarEnvio.getAcusePDF().getHash() : ""),
                    new AccioParam("Acús en XML (Hash)", sincronizarEnvio.getAcuseXML() != null ? sincronizarEnvio.getAcuseXML().getHash() : ""));

            resultadoSincronizarEnvio = updateEnviament(
                    sincronizarEnvio.getOrganismoEmisor(),
                    sincronizarEnvio.getIdentificador(),
                    sincronizarEnvio.getTipoEntrega(),
                    sincronizarEnvio.getModoNotificacion(),
                    sincronizarEnvio.getEstado(),
                    dataEstat,
                    sincronizarEnvio.getReceptor(),
                    sincronizarEnvio.getAcusePDF(),
                    info);
        } finally {
            metricsHelper.fiMetrica(timer);
        }

        return resultadoSincronizarEnvio;
    }

    private ResultadoSincronizarEnvio updateEnviament(
            String organismoEmisor,
            String identificador,
            BigInteger tipoEntrega,
            BigInteger modoNotificacion,
            String estado,
            Date dataEstat,
            Receptor receptor,
            Acuse acusePDF,
            IntegracioInfo info) {
        ResultadoSincronizarEnvio resultadoSincronizarEnvio = new ResultadoSincronizarEnvio();
        resultadoSincronizarEnvio.setIdentificador(identificador);

        NotificacioEnviamentEntity enviament = null;

        String eventErrorDescripcio = null;
        try {
            enviament = notificacioEnviamentRepository.findByNotificaIdentificador(identificador);
            if (enviament == null) {
                log.error(
                        "Error al processar petició datadoOrganismo dins el callback de Notifica (identificadorDestinatario=" + identificador + "): " +
                                "No s'ha trobat cap enviament amb l'identificador especificat (" + identificador + ").");
                resultadoSincronizarEnvio.setCodigoRespuesta("002");
                resultadoSincronizarEnvio.setDescripcionRespuesta("Identificador no encontrado");
                integracioHelper.addAccioError(info, "No s'ha trobat cap enviament amb l'identificador especificat");
                return resultadoSincronizarEnvio;
            }

            if (Strings.isNullOrEmpty(configHelper.getEntitatActualCodi())) {
                ConfigHelper.setEntitatCodi(enviament.getNotificacio().getEntitat().getCodi());
            }
            if (enviament.getNotificacio() != null && enviament.getNotificacio().getEntitat() != null) {
                info.setCodiEntitat(enviament.getNotificacio().getEntitat().getCodi());
            }

            if (enviament.isNotificaEstatFinal()) {

                String msg = "L'enviament amb identificador " + enviament.getNotificaIdentificador() + " ha rebut un callback de l'adviser de tipus " + tipoEntrega + " quan ja es troba en estat final." ;
                log.debug(msg);

                // DATAT
                if (tipoEntrega.equals(BigInteger.valueOf(1L))) { //if datado (1L)
                    log.warn("Error al processar petició datadoOrganismo dins el callback de Notifica (L'enviament amb l'identificador especificat (" + identificador + ") ja es troba en un estat final.");
                    if (receptor != null && !isBlank(receptor.getNifReceptor())) {
                        enviament.updateReceptorDatat(receptor.getNifReceptor(), receptor.getNombreReceptor());
                    }

                    resultadoSincronizarEnvio.setCodigoRespuesta("000");
                    resultadoSincronizarEnvio.setDescripcionRespuesta("OK");
                    integracioHelper.addAccioError(info, "L'enviament ja es troba en un estat final");
                    eventErrorDescripcio = msg;

                    // CERTIFICACIO
                } else if (tipoEntrega.equals(BigInteger.valueOf(3L))) { //if certificació (3L)
                    log.debug("Guardant certificació de l'enviament [tipoEntrega=" + tipoEntrega + ", id=" + enviament.getId() + "]");
                    certificacionOrganismo(
                            acusePDF,
                            organismoEmisor,
                            modoNotificacion,
                            identificador,
                            enviament,
                            resultadoSincronizarEnvio);
                    log.debug("Certificació guardada correctament.");

                    // DATAT + CERTIFICACIO
                } else {
                    eventErrorDescripcio = msg;
                }
            } else {
                String receptorNombre = null;
                String receptorNif = null;
                if (receptor != null) {
                    receptorNombre = receptor.getNombreReceptor();
                    receptorNif = receptor.getNifReceptor();
                }
                EnviamentEstat notificaEstat = getNotificaEstat(estado);

                //Update enviament
                notificaHelper.enviamentUpdateDatat(
                        notificaEstat,
                        dataEstat,
                        estado,
                        getModoNotificacion(modoNotificacion),
                        receptorNif,
                        receptorNombre,
                        null,
                        null,
                        enviament);
                log.debug("Registrant event callbackdatat de l'Adviser...");

                resultadoSincronizarEnvio.setCodigoRespuesta("000");
                resultadoSincronizarEnvio.setDescripcionRespuesta("OK");


                // CERTIFICACIO o DATAT + CERTIFICACIO
                if (tipoEntrega.equals(BigInteger.valueOf(2L)) || tipoEntrega.equals(BigInteger.valueOf(3L))) {
                    log.debug("Guardant certificació de l'enviament [tipoEntrega=" + tipoEntrega + ", id=" + enviament.getId() + "]");
                    certificacionOrganismo(
                            acusePDF,
                            organismoEmisor,
                            modoNotificacion,
                            identificador,
                            enviament,
                            resultadoSincronizarEnvio);
                    log.debug("Certificació guardada correctament.");
                }
                integracioHelper.addAccioOk(info);

//					if ("expirada".equals(estado) && acusePDF == null && enviament.getNotificaCertificacioData() == null) {
//						log.debug("Consultant la certificació de l'enviament expirat...");
//						notificaHelper.enviamentRefrescarEstat(enviament.getId());
//					}
            }

//		} catch (DatatypeConfigurationException ex) {
//			codigoRespuesta.value = "004";
//			descripcionRespuesta.value = "Fecha incorrecta";
//			integracioHelper.addAccioError(info, "La data de l'estat no té un format vàlid");
        } catch (Exception ex) {
            resultadoSincronizarEnvio.setCodigoRespuesta("666");
            resultadoSincronizarEnvio.setDescripcionRespuesta("Error procesando peticion");

            eventErrorDescripcio = ExceptionUtils.getStackTrace(ex);
            log.error("Error al processar petició datadoOrganismo dins el callback de Notifica (identificadorDestinatario=" + identificador + ")", ex);
            integracioHelper.addAccioError(info, "Error processant la petició", ex);
        }

        log.debug("Peticició processada correctament.");

        NotificacioEstatEnumDto estat = enviament.getNotificacio().getEstat();
        boolean isError = !NotificacioEstatEnumDto.FINALITZADA.equals(estat) && !NotificacioEstatEnumDto.PROCESSADA.equals(estat) && !Strings.isNullOrEmpty(eventErrorDescripcio);
        if (tipoEntrega.equals(BigInteger.valueOf(1L)) || tipoEntrega.equals(BigInteger.valueOf(2L))) {
            notificacioEventHelper.addAdviserDatatEvent(enviament, isError, eventErrorDescripcio);
        }
        callbackHelper.updateCallback(enviament, isError, eventErrorDescripcio);
        auditHelper.auditaEnviament(enviament, AuditService.TipusOperacio.UPDATE, "NotificaAdviserWsV2Impl.sincronizarEnvio");
        log.info("[ADV] Fi sincronització enviament Adviser [Id: " + (identificador != null ? identificador : "") + "]");
        return resultadoSincronizarEnvio;
    }

    private void certificacionOrganismo(
            Acuse acusePDF,
            String organismoEmisor,
            BigInteger modoNotificacion,
            String identificador,
            NotificacioEnviamentEntity enviament,
            ResultadoSincronizarEnvio resultadoSincronizarEnvio) throws Exception {
        if (enviament == null) {
            throw new Exception("Enviament should not be null");
        }
        String gestioDocumentalId = null;
        boolean ambAcuse = acusePDF != null && acusePDF.getContenido() != null && acusePDF.getContenido().length > 0;
        boolean isError = false;
        String errorDesc = "";
        try {
            if (ambAcuse) {
                String certificacioAntiga = enviament.getNotificaCertificacioArxiuId();

                log.debug("Nou estat enviament: " + enviament.getNotificaEstatDescripcio());
                if (enviament.getNotificacio() != null)
                    log.debug("Nou estat notificació: " + enviament.getNotificacio().getEstat().name());
                try {
                    log.info("Guardant certificació acusament de rebut...");
                    gestioDocumentalId = pluginHelper.gestioDocumentalCreate(
                            PluginHelper.GESDOC_AGRUPACIO_CERTIFICACIONS,
                            acusePDF.getContenido());
                } catch (Exception ex) {
                    log.error("No s'ha pogut guardar la certificació a la gestió documental", ex);
                }
                log.debug("Actualitzant enviament amb la certificació. ID gestió documental: " + gestioDocumentalId);
                enviament.updateNotificaCertificacio(
                        new Date(),
                        gestioDocumentalId,
                        acusePDF.getHash(), // hash
                        getModoNotificacion(modoNotificacion), // origen
                        null, // metadades
                        acusePDF.getCsvResguardo(), // csv
                        null, // tipus mime
                        null, // tamany
                        NotificaCertificacioTipusEnumDto.ACUSE,
                        NotificaCertificacioArxiuTipusEnumDto.PDF,
                        null); // núm. seguiment
                log.debug("Registrant event callbackcertificacio de l'Adviser...");
                notificacioEventHelper.addAdviserCertificacioEvent(enviament, false, null);

                //si hi havia una certificació antiga
                if (certificacioAntiga != null) {
                    log.debug("Esborrant certificació antiga...");
                    pluginHelper.gestioDocumentalDelete(certificacioAntiga, PluginHelper.GESDOC_AGRUPACIO_CERTIFICACIONS);
                }

                resultadoSincronizarEnvio.setCodigoRespuesta("000");
                resultadoSincronizarEnvio.setDescripcionRespuesta("OK");
                log.debug("Event callbackcertificacio registrat correctament: " + NotificacioEventTipusEnumDto.ADVISER_CERTIFICACIO.name());
            } else {
                isError = true;
                errorDesc = "Error al processar petició datadoOrganismo dins el callback de Notifica (identificadorDestinatario=" + identificador + "): " +
                        "No s'ha trobat el camp amb l'acús PDF a dins la petició rebuda.";
                log.error(errorDesc);
                notificacioEventHelper.addAdviserCertificacioEvent(enviament, true, errorDesc);
                resultadoSincronizarEnvio.setCodigoRespuesta("001");
                resultadoSincronizarEnvio.setDescripcionRespuesta("Organismo Desconocido");
            }
        } catch (Exception ex) {
            isError = true;
            errorDesc = "Error al processar petició datadoOrganismo dins el callback de Notifica (identificadorDestinatario=" + identificador + ")";
            log.error(errorDesc, ex);
            notificacioEventHelper.addAdviserCertificacioEvent(enviament, true, ExceptionUtils.getStackTrace(ex));
            resultadoSincronizarEnvio.setCodigoRespuesta("666");
            resultadoSincronizarEnvio.setDescripcionRespuesta("Error procesando peticion");
        }

        callbackHelper.updateCallback(enviament, isError, errorDesc);
        log.debug("Sortint de la certificació...");
    }

    private EnviamentEstat getNotificaEstat(String estado) {
        EnviamentEstat notificaEstat = null;
        if ("pendiente_envio".equals(estado)) {
            notificaEstat = EnviamentEstat.PENDENT_ENVIAMENT;
        } else if ("enviado_ci".equals(estado)) {
            notificaEstat = EnviamentEstat.ENVIADA_CI;
        } else if ("notificada".equals(estado)) {
            notificaEstat = EnviamentEstat.NOTIFICADA;
        } else if ("extraviada".equals(estado)) {
            notificaEstat = EnviamentEstat.EXTRAVIADA;
        } else if ("rehusada".equals(estado)) {
            notificaEstat = EnviamentEstat.REBUTJADA;
        } else if ("desconocido".equals(estado)) {
            notificaEstat = EnviamentEstat.DESCONEGUT;
        } else if ("fallecido".equals(estado)) {
            notificaEstat = EnviamentEstat.MORT;
        } else if ("ausente".equals(estado)) {
            notificaEstat = EnviamentEstat.ABSENT;
        } else if ("direccion_incorrecta".equals(estado)) {
            notificaEstat = EnviamentEstat.ADRESA_INCORRECTA;
        } else if ("sin_informacion".equals(estado)) {
            notificaEstat = EnviamentEstat.SENSE_INFORMACIO;
        } else if ("error".equals(estado)) {
            notificaEstat = EnviamentEstat.ERROR_ENTREGA;
        } else if ("pendiente_sede".equals(estado)) {
            notificaEstat = EnviamentEstat.PENDENT_SEU;
        } else if ("enviado_deh".equals(estado)) {
            notificaEstat = EnviamentEstat.ENVIADA_DEH;
        } else if ("leida".equals(estado)) {
            notificaEstat = EnviamentEstat.LLEGIDA;
        } else if ("envio_programado".equals(estado)) {
            notificaEstat = EnviamentEstat.ENVIAMENT_PROGRAMAT;
        } else if ("pendiente_cie".equals(estado)) {
            notificaEstat = EnviamentEstat.PENDENT_CIE;
        } else if ("pendiente_deh".equals(estado)) {
            notificaEstat = EnviamentEstat.PENDENT_DEH;
        } else if ("entregado_op".equals(estado)) {
            notificaEstat = EnviamentEstat.ENTREGADA_OP;
        } else if ("expirada".equals(estado)) {
            notificaEstat = EnviamentEstat.EXPIRADA;
        } else if ("anulada".equals(estado)) {
            notificaEstat = EnviamentEstat.ANULADA;
        }
        return notificaEstat;
    }

    private String getModoNotificacion(BigInteger modo) {
        String modoNotificacion = null;
        switch (modo.intValue()) {
            case 1:
                modoNotificacion = "sede";
                break;
            case 2:
                modoNotificacion = "funcionario_habilitado";
                break;
            case 3:
                modoNotificacion = "postal";
                break;
            case 4:
                modoNotificacion = "electronico";
                break;
            case 5:
                modoNotificacion = "carpeta";
                break;
        }
        return modoNotificacion;
    }

    private Date toDate(XMLGregorianCalendar calendar) {
        if (calendar == null) {
            return null;
        }
        return calendar.toGregorianCalendar().getTime();
    }


//    @Autowired
//    private AdviserWsV2PortType adviser;
//
//    @Override
//    public AdviserResponseDto sincronitzarEnviament(EnviamentAdviser env) {
//
//        Receptor receptor = new Receptor();
//        receptor.setNifReceptor(env.getReceptor().getNifReceptor());
//        receptor.setNombreReceptor(env.getReceptor().getNombreReceptor());
//        receptor.setVinculoReceptor(env.getReceptor().getVinculoReceptor());
//        receptor.setNifRepresentante(env.getReceptor().getNifRepresentante());
//        receptor.setNombreRepresentante(env.getReceptor().getNombreRepresentante());
//        receptor.setCsvRepresetante(env.getReceptor().getCsvRepresetante());
//
//        Acuse acusePdf = new Acuse();
//        acusePdf.setContenido(env.getAcusePDF().getContenido());
//        acusePdf.setHash(env.getAcusePDF().getHash());
//        acusePdf.setCsvResguardo(env.getAcusePDF().getCsvResguardo());
//
//        Acuse acuseXml = new Acuse();
//        acuseXml.setContenido(env.getAcusePDF().getContenido());
//        acuseXml.setHash(env.getAcusePDF().getHash());
//        acuseXml.setCsvResguardo(env.getAcusePDF().getCsvResguardo());
//
//        Holder<String> identificador = new Holder<>(env.getHIdentificador());
//        Holder<String> codigoRespuesta = new Holder<>(env.getCodigoRespuesta());
//        Holder<String> descripcionRespuesta = new Holder<>(env.getDescripcionRespuesta());
//        Holder<Opciones> opciones = new Holder<>(getOpciones(env.getOpcionesResultadoSincronizarEnvio()));
//
//        adviser.sincronizarEnvio(
//                env.getOrganismoEmisor(),
//                identificador,
//                env.getTipoEntrega(),
//                env.getModoNotificacion(),
//                env.getEstado(),
//                env.getFechaEstado(),
//                receptor,
//                acusePdf,
//                acuseXml,
//                getOpciones(env.getOpcionesSincronizarEnvio()),
//                codigoRespuesta,
//                descripcionRespuesta,
//                opciones);
//
//        return AdviserResponseDto.builder()
//                .identificador(env.getHIdentificador())
//                .codigoRespuesta(codigoRespuesta.value)
//                .descripcionRespuesta(descripcionRespuesta.value)
//                .opcionesResultadoSincronizarEnvio(env.getOpcionesResultadoSincronizarEnvio())
//                .build();
//    }
//
//    private Opciones getOpciones(es.caib.notib.logic.intf.dto.adviser.Opciones opciones) {
//
//        Opciones os = new Opciones();
//        Opcion o;
//        for ( es.caib.notib.logic.intf.dto.adviser.Opcion op : opciones.getOpcion()) {
//            o = new Opcion();
//            o.setTipo(op.getTipo());
//            o.setValue(o.getValue());
//            os.getOpcion().add(o);
//        }
//        return os;
//    }
}
