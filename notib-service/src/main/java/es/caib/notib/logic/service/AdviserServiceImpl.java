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
import es.caib.notib.logic.intf.dto.IntegracioCodiEnum;
import es.caib.notib.logic.intf.dto.IntegracioInfo;
import es.caib.notib.logic.intf.dto.NotificaCertificacioArxiuTipusEnumDto;
import es.caib.notib.logic.intf.dto.NotificaCertificacioTipusEnumDto;
import es.caib.notib.logic.intf.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.logic.intf.service.AdviserService;
import es.caib.notib.logic.intf.service.AuditService;
import es.caib.notib.logic.intf.ws.adviser.sincronizarenvio.Acuse;
import es.caib.notib.logic.intf.ws.adviser.sincronizarenvio.Receptor;
import es.caib.notib.logic.intf.ws.adviser.sincronizarenvio.ResultadoSincronizarEnvio;
import es.caib.notib.logic.intf.ws.adviser.sincronizarenvio.SincronizarEnvio;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;
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
    private static final int DATAT = 1;
    private static final int DATAT_CERT = 2;
    private static final int CERTIFICACIO = 3;


    private enum ResultatEnviamentEnum {
        OK ("000", "OK"),
        ERROR_ORGANISME("001", "Organismo Desconocido"),
        ERROR_IDENTIFICADOR("002", "Identificador no encontrado"),
        ERROR_DESCONEGUT("666", "Error procesando peticion");

        @Getter private String codi;
        @Getter private String desc;

        ResultatEnviamentEnum(final String codi, final String desc) {
            this.codi = codi;
            this.desc = desc;
        }
    }

    @Override
    @Transactional
    public ResultadoSincronizarEnvio sincronizarEnvio(SincronizarEnvio sincronizarEnvio) {

        var timer = metricsHelper.iniciMetrica();
        try {
            var identificador = sincronizarEnvio.getIdentificador();
            var sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            var dataEstat = toDate(sincronizarEnvio.getFechaEstado());

            generateInfoLog(sincronizarEnvio, identificador, sdf, dataEstat);
            IntegracioInfo info = generateInfoEnvio(sincronizarEnvio, identificador, sdf, dataEstat);

            return updateEnviament(
                    sincronizarEnvio.getIdentificador(),
                    sincronizarEnvio.getTipoEntrega().intValue(),
                    sincronizarEnvio.getModoNotificacion(),
                    sincronizarEnvio.getEstado(),
                    dataEstat,
                    sincronizarEnvio.getReceptor(),
                    sincronizarEnvio.getAcusePDF(),
                    info);
        } finally {
            metricsHelper.fiMetrica(timer);
        }
    }

    private ResultadoSincronizarEnvio updateEnviament(String identificador, int tipoEntrega, BigInteger modoNotificacion, String estado,
                                                      Date dataEstat, Receptor receptor, Acuse acusePDF, IntegracioInfo info) {

        var resultadoSincronizarEnvio = new ResultadoSincronizarEnvio();
        resultadoSincronizarEnvio.setIdentificador(identificador);
        NotificacioEnviamentEntity enviament = null;
        String eventErrorDescripcio = null;
        try {
             enviament = notificacioEnviamentRepository.findByNotificaIdentificador(identificador);
            if (enviament == null) {
                log.error(ERROR_CALLBACK_NOTIFICA + identificador + "): No s'ha trobat cap enviament amb l'identificador especificat (" + identificador + ").");
                var forcarOk = configHelper.getConfigAsBoolean("es.caib.notib.adviser.forcar.resposta.ok");
                setResultadoEnvio(resultadoSincronizarEnvio, forcarOk ? ResultatEnviamentEnum.OK : ResultatEnviamentEnum.ERROR_IDENTIFICADOR);
                integracioHelper.addAccioError(info, "No s'ha trobat cap enviament amb l'identificador especificat");
                return resultadoSincronizarEnvio;
            }
            updateCodiEntitatPerInfoAndConfig(info, enviament);
            if (enviament.isNotificaEstatFinal()) {
                var msg = "L'enviament amb identificador " + enviament.getNotificaIdentificador() + " ha rebut un callback de l'adviser de tipus " + tipoEntrega + " quan ja es troba en estat final." ;
                log.debug(msg);
                // DATAT
                switch (tipoEntrega) {
                    case DATAT:
//                        log.warn("Error al processar petició datadoOrganismo dins el callback de Notifica (L'enviament amb l'identificador especificat (" + identificador + ") ja es troba en un estat final.");
                        info.addParam("Nota", "L'enviament ja es troba en un estat final");
                        if (receptor != null && !isBlank(receptor.getNifReceptor())) {
                            enviament.updateReceptorDatat(receptor.getNifReceptor(), receptor.getNombreReceptor());
                        }
                        setResultadoEnvio(resultadoSincronizarEnvio, ResultatEnviamentEnum.OK);
//                        eventErrorDescripcio = msg;
                        integracioHelper.addAccioOk(info);
                        break;
                    case CERTIFICACIO:
                        log.debug("Guardant certificació de l'enviament [tipoEntrega=" + tipoEntrega + ", id=" + enviament.getId() + "]");
                        certificacionOrganismo(acusePDF, modoNotificacion, identificador, enviament, resultadoSincronizarEnvio);
                        log.debug("Certificació guardada correctament.");
                        integracioHelper.addAccioOk(info);
                        break;
                    default:
                        eventErrorDescripcio = msg;
                        integracioHelper.addAccioError(info, "Tipus d'entrega " + tipoEntrega + " no reconeguda");
                        break;
                }
            } else {
                var receptorNombre = receptor != null ? receptor.getNombreReceptor() : null;
                var receptorNif = receptor != null ? receptor.getNifReceptor() : null;
                var notificaEstat = getNotificaEstat(estado);
                //Update enviament
                notificaHelper.enviamentUpdateDatat(notificaEstat, dataEstat, estado, getModoNotificacion(modoNotificacion), receptorNif, receptorNombre, null, null, enviament);
                log.debug("Registrant event callbackdatat de l'Adviser...");
                setResultadoEnvio(resultadoSincronizarEnvio, ResultatEnviamentEnum.OK);
                if (tipoEntrega == DATAT_CERT || tipoEntrega == CERTIFICACIO) {
                    log.debug("Guardant certificació de l'enviament [tipoEntrega=" + tipoEntrega + ", id=" + enviament.getId() + "]");
                    certificacionOrganismo(acusePDF, modoNotificacion, identificador, enviament, resultadoSincronizarEnvio);
                    log.debug("Certificació guardada correctament.");
                }
                integracioHelper.addAccioOk(info);
            }
        } catch (Exception ex) {
            setResultadoEnvio(resultadoSincronizarEnvio, ResultatEnviamentEnum.ERROR_DESCONEGUT);
            eventErrorDescripcio = ExceptionUtils.getStackTrace(ex);
            log.error(ERROR_CALLBACK_NOTIFICA + identificador + ")", ex);
            integracioHelper.addAccioError(info, "Error processant la petició", ex);
        }
        log.debug("Peticició processada correctament.");
        if (enviament == null || enviament.getNotificacio() == null) {
            log.error("Error greu enviament o notificació son nulls ");
            return resultadoSincronizarEnvio;
        }
        var isError = !Strings.isNullOrEmpty(eventErrorDescripcio);
        if (tipoEntrega == DATAT || tipoEntrega == DATAT_CERT) {
            notificacioEventHelper.addAdviserDatatEvent(enviament, isError, eventErrorDescripcio);
        }
        callbackHelper.updateCallback(enviament, isError, eventErrorDescripcio);
        auditHelper.auditaEnviament(enviament, AuditService.TipusOperacio.UPDATE, "NotificaAdviserWsV2Impl.sincronizarEnvio");
        log.info("[ADV] Fi sincronització enviament Adviser [Id: " + (identificador != null ? identificador : "") + "]");
        return resultadoSincronizarEnvio;
    }

    private void updateCodiEntitatPerInfoAndConfig(IntegracioInfo info, NotificacioEnviamentEntity enviament) {
        if (enviament.getNotificacio() != null && enviament.getNotificacio().getEntitat() != null) {
            info.setCodiEntitat(enviament.getNotificacio().getEntitat().getCodi());
            if (Strings.isNullOrEmpty(configHelper.getEntitatActualCodi())) {
                ConfigHelper.setEntitatCodi(enviament.getNotificacio().getEntitat().getCodi());
            }
        }
    }

    private void certificacionOrganismo(Acuse acusePDF, BigInteger modoNotificacion, String identificador, NotificacioEnviamentEntity enviament, ResultadoSincronizarEnvio resultadoSincronizarEnvio) throws Exception {

        if (enviament == null) {
            throw new Exception("Enviament should not be null");
        }
        var ambAcuse = acusePDF != null && acusePDF.getContenido() != null && acusePDF.getContenido().length > 0;
        var resultat = new ResultatExecucio();
        try {
            if (ambAcuse) {
                log.debug("Nou estat enviament: " + enviament.getNotificaEstatDescripcio());
                log.debug("Nou estat notificació: " + enviament.getNotificacio().getEstat().name());
                var certificacioAntiga = enviament.getNotificaCertificacioArxiuId();
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
                certificacioOk(enviament, resultadoSincronizarEnvio, certificacioAntiga);
            } else {
                certificatAmbError(identificador, enviament, resultadoSincronizarEnvio, resultat);
            }
        } catch (Exception ex) {
            certificacioAmbError(identificador, enviament, resultadoSincronizarEnvio, resultat, ex);
        }
        callbackHelper.updateCallback(enviament, resultat.isError(), resultat.getErrorDescripcio());
        log.debug("Sortint de la certificació...");
    }

    private void certificacioOk(NotificacioEnviamentEntity enviament, ResultadoSincronizarEnvio resultadoSincronizarEnvio, String certificacioAntiga) {
        log.debug("Registrant event callbackcertificacio de l'Adviser...");
        notificacioEventHelper.addAdviserCertificacioEvent(enviament, false, null);
        //si hi havia una certificació antiga
        if (certificacioAntiga != null) {
            log.debug("Esborrant certificació antiga...");
            pluginHelper.gestioDocumentalDelete(certificacioAntiga, PluginHelper.GESDOC_AGRUPACIO_CERTIFICACIONS);
        }
        setResultadoEnvio(resultadoSincronizarEnvio, ResultatEnviamentEnum.OK);
        log.debug("Event callbackcertificacio registrat correctament: " + NotificacioEventTipusEnumDto.ADVISER_CERTIFICACIO.name());
    }

    private void certificatAmbError(String identificador, NotificacioEnviamentEntity enviament, ResultadoSincronizarEnvio resultadoSincronizarEnvio, ResultatExecucio resultat) {
        resultat.setError(ERROR_CALLBACK_NOTIFICA + identificador + "): No s'ha trobat el camp amb l'acús PDF a dins la petició rebuda.", null);
        notificacioEventHelper.addAdviserCertificacioEvent(enviament, resultat.isError(), resultat.getErrorDescripcio());
        setResultadoEnvio(resultadoSincronizarEnvio, ResultatEnviamentEnum.ERROR_ORGANISME);
    }

    private void certificacioAmbError(String identificador, NotificacioEnviamentEntity enviament, ResultadoSincronizarEnvio resultadoSincronizarEnvio, ResultatExecucio resultat, Exception ex) {
        resultat.setError(ERROR_CALLBACK_NOTIFICA + identificador + ")", ex);
        notificacioEventHelper.addAdviserCertificacioEvent(enviament, resultat.isError(), ExceptionUtils.getStackTrace(ex));
        setResultadoEnvio(resultadoSincronizarEnvio, ResultatEnviamentEnum.ERROR_DESCONEGUT);
    }

    private static void generateInfoLog(SincronizarEnvio sincronizarEnvio, String identificador, SimpleDateFormat sdf, Date dataEstat) {
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
    }

    @NotNull
    private static IntegracioInfo generateInfoEnvio(SincronizarEnvio sincronizarEnvio, String identificador, SimpleDateFormat sdf, Date dataEstat) {
        return new IntegracioInfo(IntegracioCodiEnum.NOTIFICA, "Recepció de canvi de notificació via Adviser",
                IntegracioAccioTipusEnumDto.RECEPCIO,
                new AccioParam("Organisme emisor", sincronizarEnvio.getOrganismoEmisor()),
                new AccioParam("Identificador Notifica", (identificador != null ? identificador : "")),
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
    }

    private static void setResultadoEnvio(ResultadoSincronizarEnvio resultadoSincronizarEnvio, ResultatEnviamentEnum resultat) {
        resultadoSincronizarEnvio.setCodigoRespuesta(resultat.getCodi());
        resultadoSincronizarEnvio.setDescripcionRespuesta(resultat.getDesc());
    }

    private String guardarCertificacioAcuseRecibo(byte[] acuse) {
        try {
            log.info("Guardant certificació acusament de rebut...");
            return pluginHelper.gestioDocumentalCreate(PluginHelper.GESDOC_AGRUPACIO_CERTIFICACIONS, acuse);
        } catch (Exception ex) {
            log.error("No s'ha pogut guardar la certificació a la gestió documental", ex);
        }
        return null;
    }

    private EnviamentEstat getNotificaEstat(String estado) {

        switch (estado) {
            case "pendiente_envio":
                return EnviamentEstat.PENDENT_ENVIAMENT;
            case "enviado_ci":
                return EnviamentEstat.ENVIADA_CI;
            case "notificada":
                return EnviamentEstat.NOTIFICADA;
            case "extraviada":
                return EnviamentEstat.EXTRAVIADA;
            case "rehusada":
                return EnviamentEstat.REBUTJADA;
            case "desconocido":
                return EnviamentEstat.DESCONEGUT;
            case "fallecido":
                return EnviamentEstat.MORT;
            case "ausente":
                return EnviamentEstat.ABSENT;
            case "direccion_incorrecta":
                return EnviamentEstat.ADRESA_INCORRECTA;
            case "sin_informacion":
                return EnviamentEstat.SENSE_INFORMACIO;
            case "error":
                return EnviamentEstat.ERROR_ENTREGA;
            case "pendiente_sede":
                return EnviamentEstat.PENDENT_SEU;
            case "enviado_deh":
                return EnviamentEstat.ENVIADA_DEH;
            case "leida":
                return EnviamentEstat.LLEGIDA;
            case "envio_programado":
                return EnviamentEstat.ENVIAMENT_PROGRAMAT;
            case "pendiente_cie":
                return EnviamentEstat.PENDENT_CIE;
            case "pendiente_deh":
                return EnviamentEstat.PENDENT_DEH;
            case "entregado_op":
                return EnviamentEstat.ENTREGADA_OP;
            case "expirada":
                return EnviamentEstat.EXPIRADA;
            case "anulada":
                return EnviamentEstat.ANULADA;
            default:
                return null;
        }
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

    @Getter @Setter
    private static class ResultatExecucio {
        private String codi;
        private String descripcio;
        private boolean error = false;
        private String errorDescripcio = "";

        public void setError(String errorDescripcio, Exception ex) {
            if (!Strings.isNullOrEmpty(errorDescripcio))
                this.error = true;
            this.errorDescripcio = errorDescripcio;
            if (ex == null)
                log.error(errorDescripcio);
            else
                log.error(errorDescripcio, ex);
        }
    }
}
