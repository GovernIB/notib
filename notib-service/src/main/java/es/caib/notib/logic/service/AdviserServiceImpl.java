package es.caib.notib.logic.service;

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

    private static final String ERROR_CALLBACK_NOTIFICA = "Error al processar petició datadoOrganismo dins el callback de Notifica (identificadorDestinatario=";


    @Override
    @Transactional
    public ResultadoSincronizarEnvio sincronizarEnvio(SincronizarEnvio sincronizarEnvio) {

        var timer = metricsHelper.iniciMetrica();
        try {
            var identificador = sincronizarEnvio.getIdentificador();
            var sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
            var dataEstat = toDate(sincronizarEnvio.getFechaEstado());

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

            IntegracioInfo info = new IntegracioInfo(IntegracioHelper.INTCODI_NOTIFICA, "Recepció de canvi de notificació via Adviser",
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

            return updateEnviament(sincronizarEnvio.getIdentificador(), sincronizarEnvio.getTipoEntrega(), sincronizarEnvio.getModoNotificacion(),
                                        sincronizarEnvio.getEstado(), dataEstat, sincronizarEnvio.getReceptor(), sincronizarEnvio.getAcusePDF(), info);
        } finally {
            metricsHelper.fiMetrica(timer);
        }
    }

    private ResultadoSincronizarEnvio updateEnviament(String identificador, BigInteger tipoEntrega, BigInteger modoNotificacion, String estado,
                                                      Date dataEstat, Receptor receptor, Acuse acusePDF, IntegracioInfo info) {

        var resultadoSincronizarEnvio = new ResultadoSincronizarEnvio();
        resultadoSincronizarEnvio.setIdentificador(identificador);
        NotificacioEnviamentEntity enviament = null;
        String eventErrorDescripcio = null;
        try {
            enviament = notificacioEnviamentRepository.findByNotificaIdentificador(identificador);
            if (enviament == null) {
                log.error(ERROR_CALLBACK_NOTIFICA + identificador + "): No s'ha trobat cap enviament amb l'identificador especificat (" + identificador + ").");
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
                var msg = "L'enviament amb identificador " + enviament.getNotificaIdentificador() + " ha rebut un callback de l'adviser de tipus " + tipoEntrega + " quan ja es troba en estat final." ;
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
                    certificacionOrganismo(acusePDF, modoNotificacion, identificador, enviament, resultadoSincronizarEnvio);
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
                var notificaEstat = getNotificaEstat(estado);
                //Update enviament
                notificaHelper.enviamentUpdateDatat(notificaEstat, dataEstat, estado, getModoNotificacion(modoNotificacion), receptorNif, receptorNombre, null, null, enviament);
                log.debug("Registrant event callbackdatat de l'Adviser...");
                resultadoSincronizarEnvio.setCodigoRespuesta("000");
                resultadoSincronizarEnvio.setDescripcionRespuesta("OK");
                // CERTIFICACIO o DATAT + CERTIFICACIO
                if (tipoEntrega.equals(BigInteger.valueOf(2L)) || tipoEntrega.equals(BigInteger.valueOf(3L))) {
                    log.debug("Guardant certificació de l'enviament [tipoEntrega=" + tipoEntrega + ", id=" + enviament.getId() + "]");
                    certificacionOrganismo(acusePDF, modoNotificacion, identificador, enviament, resultadoSincronizarEnvio);
                    log.debug("Certificació guardada correctament.");
                }
                integracioHelper.addAccioOk(info);
            }
        } catch (Exception ex) {
            resultadoSincronizarEnvio.setCodigoRespuesta("666");
            resultadoSincronizarEnvio.setDescripcionRespuesta("Error procesando peticion");
            eventErrorDescripcio = ExceptionUtils.getStackTrace(ex);
            log.error(ERROR_CALLBACK_NOTIFICA + identificador + ")", ex);
            integracioHelper.addAccioError(info, "Error processant la petició", ex);
        }
        log.debug("Peticició processada correctament.");
        if (enviament == null || enviament.getNotificacio() == null) {
            log.error("Error greu enviament o notificació son nulls ");
            return resultadoSincronizarEnvio;
        }
        var estat = enviament.getNotificacio().getEstat();
        var isError = !NotificacioEstatEnumDto.FINALITZADA.equals(estat) && !NotificacioEstatEnumDto.PROCESSADA.equals(estat) && !Strings.isNullOrEmpty(eventErrorDescripcio);
        if (tipoEntrega.equals(BigInteger.valueOf(1L)) || tipoEntrega.equals(BigInteger.valueOf(2L))) {
            notificacioEventHelper.addAdviserDatatEvent(enviament, isError, eventErrorDescripcio);
        }
        callbackHelper.updateCallback(enviament, isError, eventErrorDescripcio);
        auditHelper.auditaEnviament(enviament, AuditService.TipusOperacio.UPDATE, "NotificaAdviserWsV2Impl.sincronizarEnvio");
        log.info("[ADV] Fi sincronització enviament Adviser [Id: " + (identificador != null ? identificador : "") + "]");
        return resultadoSincronizarEnvio;
    }

    private void certificacionOrganismo(Acuse acusePDF, BigInteger modoNotificacion, String identificador, NotificacioEnviamentEntity enviament, ResultadoSincronizarEnvio resultadoSincronizarEnvio) throws Exception {

        if (enviament == null) {
            throw new Exception("Enviament should not be null");
        }
        var ambAcuse = acusePDF != null && acusePDF.getContenido() != null && acusePDF.getContenido().length > 0;
        var isError = false;
        var errorDesc = "";
        try {
            if (ambAcuse) {
                var certificacioAntiga = enviament.getNotificaCertificacioArxiuId();
                log.debug("Nou estat enviament: " + enviament.getNotificaEstatDescripcio());
                if (enviament.getNotificacio() != null) {
                    log.debug("Nou estat notificació: " + enviament.getNotificacio().getEstat().name());
                }
                var gestioDocumentalId = guardarCertificacioAcuseRecibo(acusePDF.getContenido());
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
                errorDesc = ERROR_CALLBACK_NOTIFICA + identificador + "): No s'ha trobat el camp amb l'acús PDF a dins la petició rebuda.";
                log.error(errorDesc);
                notificacioEventHelper.addAdviserCertificacioEvent(enviament, true, errorDesc);
                resultadoSincronizarEnvio.setCodigoRespuesta("001");
                resultadoSincronizarEnvio.setDescripcionRespuesta("Organismo Desconocido");
            }
        } catch (Exception ex) {
            isError = true;
            errorDesc = ERROR_CALLBACK_NOTIFICA + identificador + ")";
            log.error(errorDesc, ex);
            notificacioEventHelper.addAdviserCertificacioEvent(enviament, true, ExceptionUtils.getStackTrace(ex));
            resultadoSincronizarEnvio.setCodigoRespuesta("666");
            resultadoSincronizarEnvio.setDescripcionRespuesta("Error procesando peticion");
        }
        callbackHelper.updateCallback(enviament, isError, errorDesc);
        log.debug("Sortint de la certificació...");
    }

    private String guardarCertificacioAcuseRecibo(byte[] acuse) {

        try {
            log.info("Guardant certificació acusament de rebut...");
            return pluginHelper.gestioDocumentalCreate(PluginHelper.GESDOC_AGRUPACIO_CERTIFICACIONS,acuse);
        } catch (Exception ex) {
            log.error("No s'ha pogut guardar la certificació a la gestió documental", ex);
        }
        return null;
    }

    private EnviamentEstat getNotificaEstat(String estado) {

        if ("pendiente_envio".equals(estado)) {
            return EnviamentEstat.PENDENT_ENVIAMENT;
        }
        if ("enviado_ci".equals(estado)) {
           return EnviamentEstat.ENVIADA_CI;
        }
        if ("notificada".equals(estado)) {
         return EnviamentEstat.NOTIFICADA;
        }
        if ("extraviada".equals(estado)) {
            return EnviamentEstat.EXTRAVIADA;
        }
        if ("rehusada".equals(estado)) {
            return EnviamentEstat.REBUTJADA;
        }
        if ("desconocido".equals(estado)) {
            return EnviamentEstat.DESCONEGUT;
        }
        if ("fallecido".equals(estado)) {
            return EnviamentEstat.MORT;
        }
        if ("ausente".equals(estado)) {
            return EnviamentEstat.ABSENT;
        }
        if ("direccion_incorrecta".equals(estado)) {
            return EnviamentEstat.ADRESA_INCORRECTA;
        }
        if ("sin_informacion".equals(estado)) {
            return EnviamentEstat.SENSE_INFORMACIO;
        }
        if ("error".equals(estado)) {
            return EnviamentEstat.ERROR_ENTREGA;
        }
        if ("pendiente_sede".equals(estado)) {
            return EnviamentEstat.PENDENT_SEU;
        }
        if ("enviado_deh".equals(estado)) {
            return EnviamentEstat.ENVIADA_DEH;
        }
        if ("leida".equals(estado)) {
            return EnviamentEstat.LLEGIDA;
        }
        if ("envio_programado".equals(estado)) {
            return EnviamentEstat.ENVIAMENT_PROGRAMAT;
        }
        if ("pendiente_cie".equals(estado)) {
            return EnviamentEstat.PENDENT_CIE;
        }
        if ("pendiente_deh".equals(estado)) {
            return EnviamentEstat.PENDENT_DEH;
        }
        if ("entregado_op".equals(estado)) {
            return EnviamentEstat.ENTREGADA_OP;
        }
        if ("expirada".equals(estado)) {
            return EnviamentEstat.EXPIRADA;
        }
        if ("anulada".equals(estado)) {
            return EnviamentEstat.ANULADA;
        }
        return null;
    }

    private String getModoNotificacion(BigInteger modo) {

        switch (modo.intValue()) {
            case 1:
                return "sede";
            case 2:
                return "funcionario_habilitado";
            case 3:
                return "postal";
            case 4:
                return "electronico";
            case 5:
                return "carpeta";
            default:
                return null;
        }
    }

    private Date toDate(XMLGregorianCalendar calendar) {
        return calendar != null ? calendar.toGregorianCalendar().getTime() : null;
    }
}
