package es.caib.notib.logic.service;

import com.google.common.base.Strings;
import es.caib.notib.client.domini.CieEstat;
import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.logic.helper.AbstractNotificaHelper;
import es.caib.notib.logic.helper.AuditHelper;
import es.caib.notib.logic.helper.CallbackHelper;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.ConversioTipusHelper;
import es.caib.notib.logic.helper.IntegracioHelper;
import es.caib.notib.logic.helper.MetricsHelper;
import es.caib.notib.logic.helper.NotificaHelper;
import es.caib.notib.logic.helper.NotificacioEventHelper;
import es.caib.notib.logic.helper.PluginHelper;
import es.caib.notib.logic.intf.dto.AccioParam;
import es.caib.notib.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioCodi;
import es.caib.notib.logic.intf.dto.IntegracioInfo;
import es.caib.notib.logic.intf.dto.NotificaCertificacioArxiuTipusEnumDto;
import es.caib.notib.logic.intf.dto.NotificaCertificacioTipusEnumDto;
import es.caib.notib.logic.intf.dto.NotificacioEventTipusEnumDto;
import es.caib.notib.logic.intf.dto.adviser.ResultatEnviamentEnum;
import es.caib.notib.logic.intf.dto.adviser.ResultatExecucio;
import es.caib.notib.logic.intf.service.AuditService;
import es.caib.notib.logic.intf.service.CieAdviserService;
import es.caib.notib.logic.intf.util.NifHelper;
import es.caib.notib.logic.intf.ws.adviser.nexea.NexeaAdviserWs;
import es.caib.notib.logic.intf.ws.adviser.nexea.common.Opciones;
import es.caib.notib.logic.intf.ws.adviser.nexea.sincronizarenvio.Acuse;
import es.caib.notib.logic.intf.ws.adviser.nexea.sincronizarenvio.Receptor;
import es.caib.notib.logic.intf.ws.adviser.nexea.sincronizarenvio.ResultadoSincronizarEnvio;
import es.caib.notib.logic.intf.ws.adviser.nexea.sincronizarenvio.SincronizarEnvio;
import es.caib.notib.logic.objectes.LoggingTipus;
import es.caib.notib.logic.utils.DatesUtils;
import es.caib.notib.logic.utils.NotibLogger;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import es.caib.notib.persist.repository.NotificacioTableViewRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ScheduledMessage;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.Holder;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
public class CieAdviserServiceImpl implements CieAdviserService {

    @Autowired
    private NotificacioEnviamentRepository enviamentRepository;
    @Autowired
    private NotificacioTableViewRepository notificacioTableRepository;
    @Autowired
    private IntegracioHelper integracioHelper;
    @Autowired
    private ConversioTipusHelper conversioTipusHelper;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private NotificacioEventHelper notificacioEventHelper;
    @Autowired
    private MetricsHelper metricsHelper;
    @Autowired
    private ConfigHelper configHelper;
    @Autowired
    private PluginHelper pluginHelper;
    @Autowired
    private CallbackHelper callbackHelper;
    @Autowired
    private AuditHelper auditHelper;
    @Autowired
    private NotificaHelper notificaHelper;

    private static final String ERROR_CALLBACK_NOTIFICA = "[CIE ADVISER] Error al processar la informació de l'adviser ";
    private static final int DATAT = 1;
    private static final int DATAT_CERT = 2;
    private static final int CERTIFICACIO = 3;


    @Override
    public ResultadoSincronizarEnvio sincronizarEnvio(SincronizarEnvio sincronizarEnvio) {

        var info = new IntegracioInfo(IntegracioCodi.CIE, "Sincronitzar enviament", IntegracioAccioTipusEnumDto.RECEPCIO,
                new AccioParam("Identificador Nexea", sincronizarEnvio.getIdentificador()),
                new AccioParam("Estat", sincronizarEnvio.getEstado()));

        var sinc = conversioTipusHelper.convertir(sincronizarEnvio, SincronizarEnvio.class);
        var resposta = sincronitzarEntregaPostal(sinc, info);
        if (!NexeaAdviserWs.CODI_OK.equals(resposta.getCodigoRespuesta())) {
            integracioHelper.addAccioError(info, resposta.getDescripcionRespuesta());
            return resposta;
        }
        sincronizarEnvio.setIdentificador(resposta.getIdentificador());
        var resultadoSincronizarEnvio = sincronizarEnvioDatado(sincronizarEnvio);
        if (NexeaAdviserWs.CODI_OK_DEC.equalsIgnoreCase(resposta.getDescripcionRespuesta())) {
            integracioHelper.addAccioOk(info);
        } else {
            integracioHelper.addAccioError(info, resposta.getDescripcionRespuesta());
        }
        return resultadoSincronizarEnvio;
    }

    @Transactional
    @Override
    public void sincronizarEnvio(String organismoEmisor,
                                 Holder<String> identificador,
                                 BigInteger tipoEntrega,
                                 BigInteger modoNotificacion,
                                 String estado,
                                 XMLGregorianCalendar fechaEstado,
                                 Receptor receptor,
                                 Acuse acusePDF,
                                 Acuse acuseXML,
                                 Opciones opcionesSincronizarEnvio,
                                 Holder<String> codigoRespuesta,
                                 Holder<String> descripcionRespuesta,
                                 Holder<Opciones> opcionesResultadoSincronizarEnvio) {

        var info = new IntegracioInfo(IntegracioCodi.CIE, "Sincronitzar enviament", IntegracioAccioTipusEnumDto.RECEPCIO,
                new AccioParam("Identificador Nexea", identificador.value),
                new AccioParam("Estat", estado));

        var sincronizarEnvio = new SincronizarEnvio();
        sincronizarEnvio.setOrganismoEmisor(organismoEmisor);
        sincronizarEnvio.setIdentificador(identificador.value);
        sincronizarEnvio.setTipoEntrega(tipoEntrega);
        sincronizarEnvio.setModoNotificacion(modoNotificacion);
        sincronizarEnvio.setEstado(estado);
        sincronizarEnvio.setFechaEstado(fechaEstado);
        sincronizarEnvio.setReceptor(receptor);
        sincronizarEnvio.setAcusePDF(acusePDF);
        sincronizarEnvio.setAcuseXML(acuseXML);
        sincronizarEnvio.setOpcionesSincronizarEnvio(opcionesSincronizarEnvio);

        var resposta = sincronitzarEntregaPostal(sincronizarEnvio, info);
        codigoRespuesta.value = resposta.getCodigoRespuesta();
        descripcionRespuesta.value = resposta.getDescripcionRespuesta();
        if (!NexeaAdviserWs.CODI_OK.equals(resposta.getCodigoRespuesta())) {
            integracioHelper.addAccioError(info, resposta.getDescripcionRespuesta());
            return;
        }

        sincronizarEnvio.setIdentificador(resposta.getIdentificador());
        var resultadoSincronizarEnvio = sincronizarEnvioDatado(sincronizarEnvio);
        var codi = ResultatEnviamentEnum.getByCodi(resultadoSincronizarEnvio.getCodigoRespuesta());
        if (NexeaAdviserWs.CODI_OK_DEC.equalsIgnoreCase(resposta.getDescripcionRespuesta())) {
            codigoRespuesta.value = NexeaAdviserWs.CODI_OK;
            descripcionRespuesta.value = NexeaAdviserWs.CODI_OK_DEC;
            integracioHelper.addAccioOk(info);
        } else {
            codigoRespuesta.value = codi.getCodiNexea();
            descripcionRespuesta.value = codi.getDesc();
            integracioHelper.addAccioError(info, resposta.getDescripcionRespuesta());
        }
    }

    private ResultadoSincronizarEnvio sincronitzarEntregaPostal(SincronizarEnvio sincronizarEnvio, IntegracioInfo info) {

        var identificador = sincronizarEnvio.getIdentificador();
        var resultado = new ResultadoSincronizarEnvio();
        try {
            if (Strings.isNullOrEmpty(identificador)) {
                var error = "[CIE Adviser] Error l'identificador no pot ser null";
                log.error(error);
                resultado.setCodigoRespuesta(NexeaAdviserWs.CODI_ERROR_IDENTIFICADOR_INCORRECTE);
                resultado.setDescripcionRespuesta("Identificador incorrecto");
                return resultado;
            }
            var enviament = enviamentRepository.findByCieId(identificador);
            if (enviament == null) {
                resultado.setCodigoRespuesta(NexeaAdviserWs.CODI_ERROR_IDENTIFICADOR_INEXISTENT);
                resultado.setDescripcionRespuesta("Identificador no se corresponde con el CIE");
                return resultado;
            }
            info.setAplicacio(enviament.getNotificacio().getTipusUsuari(), enviament.getNotificacio().getCreatedBy().get().getCodi());
//            if (!receptorValid(sincronizarEnvio.getReceptor())) {
//                resultado.setCodigoRespuesta(NexeaAdviserWs.CODI_ERROR_VALIDACIO);
//                resultado.setDescripcionRespuesta("Receptor no tiene el formato correcto");
//                return resultado;
//            }

            var estat = updateEntregaPostal(enviament, sincronizarEnvio);
            info.setCodiEntitat(enviament.getNotificacio().getEntitat().getCodi());
            resultado.setIdentificador(enviament.getEntregaPostal().getCieId());
            resultado.setCodigoRespuesta(NexeaAdviserWs.CODI_OK);
            resultado.setDescripcionRespuesta(NexeaAdviserWs.CODI_OK_DEC);
            if (CieEstat.NOTIFICADA.equals(estat)) {
                jmsTemplate.convertAndSend(NotificaHelper.CUA_SINCRONIZAR_ENVIO_OE, sincronizarEnvio,
                        m -> {
                            m.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY,  0L);
                            return m;
                        });
            }
            return resultado;
        } catch (Exception e) {
            var error = "[CIE Adviser] Error al sincronitzar l'enviament amb id " + identificador;
            log.error(error, e);
            resultado.setCodigoRespuesta(NexeaAdviserWs.CODI_ERROR);
            resultado.setDescripcionRespuesta(error);
            return resultado;
        }
    }


    private CieEstat updateEntregaPostal(NotificacioEnviamentEntity enviament, SincronizarEnvio sincronizarEnvio) {

        var entregaPostal = enviament.getEntregaPostal();
        if (entregaPostal == null) {
            return null;
        }
        var notTable = notificacioTableRepository.findById(enviament.getNotificacio().getId()).get();
        notTable.setPerActualitzar(true);
        notificacioTableRepository.save(notTable);
        var estat = CieEstat.valueOf(sincronizarEnvio.getEstado().toUpperCase());
        enviament.getEntregaPostal().setCieEstat(estat);
        var error = CieEstat.ERROR.equals(estat);
        var opciones = sincronizarEnvio.getOpcionesSincronizarEnvio();
        if (opciones == null) {
            notificacioEventHelper.addCieAdviserEvent(enviament, error, "Error ", false);
            return estat;
        }
        for(var opcion : opciones.getOpcion()) {
            if (!"motivoError".equals(opcion.getTipo())) {
                continue;
            }
            var errorMsg = !Strings.isNullOrEmpty(opcion.getValue()) ? opcion.getValue().length() > 250 ? opcion.getValue().substring(0, 250) : opcion.getValue() : null;
            entregaPostal.setCieErrorDesc(errorMsg);
        }
        notificacioEventHelper.addCieAdviserEvent(enviament, error,  entregaPostal.getCieErrorDesc(), false);
        return estat;
    }

    private boolean receptorValid(Receptor receptor) {
        return receptor != null && !Strings.isNullOrEmpty(receptor.getNifReceptor())
                && NifHelper.isvalid(receptor.getNifReceptor());
    }

    private ResultadoSincronizarEnvio sincronizarEnvioDatado(SincronizarEnvio sincronizarEnvio) {

        var timer = metricsHelper.iniciMetrica();
        try {
            var identificador = sincronizarEnvio.getIdentificador();
            var sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            var dataEstat = DatesUtils.toDate(sincronizarEnvio.getFechaEstado());

            generateInfoLog(sincronizarEnvio, identificador, sdf, dataEstat);
            var info = generateInfoEnvio(sincronizarEnvio, identificador, sdf, dataEstat);

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
        ResultatEnviamentEnum resultatEnum = null;
        try {
            enviament = enviamentRepository.findByCieId(identificador);
            if (enviament == null) {
                log.error(ERROR_CALLBACK_NOTIFICA + "No s'ha trobat cap enviament amb l'identificador especificat (" + identificador + ").");
                var forcarOk = configHelper.getConfigAsBoolean("es.caib.notib.adviser.forcar.resposta.ok");
                setResultadoEnvio(resultadoSincronizarEnvio, forcarOk ? ResultatEnviamentEnum.OK : ResultatEnviamentEnum.ERROR_IDENTIFICADOR);
                integracioHelper.addAccioWarn(info, "No s'ha trobat cap enviament amb l'identificador especificat");
                return resultadoSincronizarEnvio;
            }
            var entregaPostal = enviament.getEntregaPostal();
            updateCodiEntitatPerInfoAndConfig(info, enviament);
            if (entregaPostal.isCieEstatFinal()) {
                var msg = "L'enviament amb identificador " + identificador + " ha rebut un callback de l'adviser de tipus " + tipoEntrega + " quan ja es troba en estat final." ;
                log.debug(msg);
                setResultadoEnvio(resultadoSincronizarEnvio, ResultatEnviamentEnum.OK);
                // DATAT
                switch (tipoEntrega) {
                    case DATAT:
//                        log.warn("Error al processar petició datadoOrganismo dins el callback de Notifica (L'enviament amb l'identificador especificat (" + identificador + ") ja es troba en un estat final.");
                        info.addParam("Nota", "L'enviament ja es troba en un estat final");
                        if (receptor != null && !isBlank(receptor.getNifReceptor())) {
                            enviament.updateReceptorDatat(receptor.getNifReceptor(), receptor.getNombreReceptor());
                        }
//                        eventErrorDescripcio = msg;
                        integracioHelper.addAccioOk(info);
                        break;
                    case CERTIFICACIO:
                    case DATAT_CERT:
                        if (enviament.getNotificaCertificacioData() != null && !Strings.isNullOrEmpty(enviament.getNotificaCertificacioArxiuId())) {
                            break;
                        }
                        log.debug("Guardant certificació de l'enviament [tipoEntrega=" + tipoEntrega + ", id=" + enviament.getId() + "]");
                        certificacionOrganismo(acusePDF, modoNotificacion, identificador, enviament, resultadoSincronizarEnvio);
                        log.debug("Certificació guardada correctament.");
                        integracioHelper.addAccioOk(info);
                        break;
                    default:
                        eventErrorDescripcio = msg;
                        setResultadoEnvio(resultadoSincronizarEnvio, ResultatEnviamentEnum.ERROR_DESCONEGUT);
                        integracioHelper.addAccioError(info, "Tipus d'entrega " + tipoEntrega + " no reconeguda");
                        break;
                }
            } else {
                var receptorNombre = receptor != null ? receptor.getNombreReceptor() : null;
                var receptorNif = receptor != null ? receptor.getNifReceptor() : null;
                var notificaEstat = getCieEstat(estado);
                if (notificaEstat == null) {
                    resultatEnum = ResultatEnviamentEnum.ESTAT_DESCONEGUT;
                    throw new Exception("Estat no trobat");
                }
                //Update enviament
                notificaHelper.enviamentUpdateDatat(notificaEstat, dataEstat, estado, getModoNotificacion(modoNotificacion), receptorNif, receptorNombre, null, null, enviament);
                log.debug("Registrant event callback datat de l'Adviser...");
                setResultadoEnvio(resultadoSincronizarEnvio, ResultatEnviamentEnum.OK);
                if (tipoEntrega == DATAT_CERT || tipoEntrega == CERTIFICACIO) {
                    log.debug("Guardant certificació de l'enviament [tipoEntrega=" + tipoEntrega + ", id=" + enviament.getId() + "]");
                    certificacionOrganismo(acusePDF, modoNotificacion, identificador, enviament, resultadoSincronizarEnvio);
                    log.debug("Certificació guardada correctament.");
                }
                integracioHelper.addAccioOk(info);
            }
        } catch (Exception ex) {
            setResultadoEnvio(resultadoSincronizarEnvio, resultatEnum != null ? resultatEnum : ResultatEnviamentEnum.ERROR_DESCONEGUT);
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
            notificacioEventHelper.addCieAdviserDatatEvent(enviament, isError, eventErrorDescripcio);
        }
        callbackHelper.updateCallback(enviament, isError, eventErrorDescripcio);
        auditHelper.auditaEnviament(enviament, AuditService.TipusOperacio.UPDATE, "NotificaAdviserWsV2Impl.sincronizarEnvio");
        log.info("[ADV] Fi sincronització enviament Adviser [Id: " + (identificador != null ? identificador : "") + "]");
        return resultadoSincronizarEnvio;
    }

    private void generateInfoLog(SincronizarEnvio sincronizarEnvio, String identificador, SimpleDateFormat sdf, Date dataEstat) {

        log.info("[CIE ADVISER] Inici sincronització enviament Adviser [");
        log.info("        Id: " + (identificador != null ? identificador : ""));
        log.info("        OrganismoEmisor: " + sincronizarEnvio.getOrganismoEmisor());
        log.info("        TipoEntrega: " + sincronizarEnvio.getTipoEntrega());
        log.info("        ModoNotificacion: " + sincronizarEnvio.getModoNotificacion());
        log.info("        Estat: " + sincronizarEnvio.getEstado());
        if (dataEstat != null) {
            log.info("        FechaEstado: " + sdf.format(dataEstat));
        }
        log.info("        Receptor: " + (sincronizarEnvio.getReceptor() != null ? sincronizarEnvio.getReceptor().getNifReceptor() : "") + "]");
        log.info("--------------------------------------------------------------");
    }

    private static IntegracioInfo generateInfoEnvio(SincronizarEnvio sincronizarEnvio, String identificador, SimpleDateFormat sdf, Date dataEstat) {
        return new IntegracioInfo(IntegracioCodi.CIE, "Recepció de canvi d'estat al CIE via Adviser",
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

    private static void setResultadoEnvio(ResultadoSincronizarEnvio resultadoSincronizarEnvio, ResultatEnviamentEnum resultat, String error) {

        resultadoSincronizarEnvio.setCodigoRespuesta(resultat.getCodi());
        resultadoSincronizarEnvio.setDescripcionRespuesta(error);
    }

    private void updateCodiEntitatPerInfoAndConfig(IntegracioInfo info, NotificacioEnviamentEntity enviament) {

        if (enviament.getNotificacio() == null || enviament.getNotificacio().getEntitat() == null) {
            return;
        }
        info.setCodiEntitat(enviament.getNotificacio().getEntitat().getCodi());
        if (Strings.isNullOrEmpty(configHelper.getEntitatActualCodi())) {
            ConfigHelper.setEntitatCodi(enviament.getNotificacio().getEntitat().getCodi());
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
        notificacioEventHelper.addCieAdviserCertificacioEvent(enviament, false, null);
        //si hi havia una certificació antiga
        if (certificacioAntiga != null) {
            log.debug("Esborrant certificació antiga...");
            pluginHelper.gestioDocumentalDelete(certificacioAntiga, PluginHelper.GESDOC_AGRUPACIO_CERTIFICACIONS);
        }
        setResultadoEnvio(resultadoSincronizarEnvio, ResultatEnviamentEnum.OK);
        log.debug("Event callbackcertificacio registrat correctament: " + NotificacioEventTipusEnumDto.ADVISER_CERTIFICACIO.name());
    }

    private void certificatAmbError(String identificador, NotificacioEnviamentEntity enviament, ResultadoSincronizarEnvio resultadoSincronizarEnvio, ResultatExecucio resultat) {

        resultat.setError(ERROR_CALLBACK_NOTIFICA + "(" + identificador + "): No s'ha trobat el camp amb l'acús PDF a dins la petició rebuda.", null);
        notificacioEventHelper.addCieAdviserCertificacioEvent(enviament, resultat.isError(), resultat.getErrorDescripcio());
        setResultadoEnvio(resultadoSincronizarEnvio, ResultatEnviamentEnum.ERROR_ACUSE, resultat.getErrorDescripcio());
    }

    private void certificacioAmbError(String identificador, NotificacioEnviamentEntity enviament, ResultadoSincronizarEnvio resultadoSincronizarEnvio, ResultatExecucio resultat, Exception ex) {

        resultat.setError(ERROR_CALLBACK_NOTIFICA + "(" + identificador + ")", ex);
        notificacioEventHelper.addCieAdviserCertificacioEvent(enviament, resultat.isError(), ExceptionUtils.getStackTrace(ex));
        setResultadoEnvio(resultadoSincronizarEnvio, ResultatEnviamentEnum.ERROR_DESCONEGUT);
    }

    private String guardarCertificacioAcuseRecibo(byte[] acuse) {

        try {
            log.info("Guardant certificacio acusament de rebut...");
            NotibLogger.getInstance().printInfoSistema(log, LoggingTipus.METRIQUES_SISTEMA);
            return pluginHelper.gestioDocumentalCreate(PluginHelper.GESDOC_AGRUPACIO_CERTIFICACIONS, acuse);
        } catch (Exception ex) {
            log.error("No s'ha pogut guardar la certificació a la gestió documental", ex);
            throw ex;
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

    private EnviamentEstat getCieEstat(String estado) {

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

}
