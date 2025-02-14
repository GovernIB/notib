package es.caib.notib.logic.plugin.cie;

import com.google.common.base.Strings;
import es.caib.notib.client.domini.CieEstat;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.ConversioTipusHelper;
import es.caib.notib.logic.helper.IntegracioHelper;
import es.caib.notib.logic.helper.MessageHelper;
import es.caib.notib.logic.helper.NotificacioEventHelper;
import es.caib.notib.logic.helper.NotificacioTableHelper;
import es.caib.notib.logic.helper.PluginHelper;
import es.caib.notib.logic.intf.dto.AccioParam;
import es.caib.notib.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioCodi;
import es.caib.notib.logic.intf.dto.IntegracioInfo;
import es.caib.notib.logic.intf.dto.cie.CieDto;
import es.caib.notib.logic.intf.dto.cie.OperadorPostalDto;
import es.caib.notib.logic.intf.exception.SistemaExternException;
import es.caib.notib.logic.utils.EncryptionUtil;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.entity.cie.EntregaPostalEntity;
import es.caib.notib.persist.entity.cie.PagadorCieEntity;
import es.caib.notib.persist.repository.EntitatRepository;
import es.caib.notib.persist.repository.EntregaPostalRepository;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import es.caib.notib.persist.repository.NotificacioEventRepository;
import es.caib.notib.persist.repository.NotificacioRepository;
import es.caib.notib.persist.repository.NotificacioTableViewRepository;
import es.caib.notib.plugin.cie.CiePlugin;
import es.caib.notib.plugin.cie.EnviamentCie;
import es.caib.notib.plugin.cie.InfoCie;
import es.caib.notib.plugin.cie.RespostaCie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Slf4j
@Component
public class CiePluginHelper {

    private final NotificacioTableViewRepository notificacioTableViewRepository;
    private final MessageHelper messageHelper;
    private final NotificacioTableHelper notificacioTableHelper;
    private Map<String, CiePlugin> ciePlugin = new HashMap<>();
    private final ConversioTipusHelper conversioTipusHelper;
    private final NotificacioEventHelper notificacioEventHelper;
    private final ConfigHelper configHelper;
    private final EntitatRepository entitatRepository;
    private final NotificacioRepository notificacioRepository;
    private final NotificacioEventRepository notificacioEventRepository;
    private final IntegracioHelper integracioHelper;
    private final PluginHelper pluginHelper;
    private final EntregaPostalRepository entregaPostalRepository;
    private final NotificacioEnviamentRepository enviamentRepository;

    private static final String ERROR_INESPERAT = "Error inesperat";

    public CiePluginHelper(ConfigHelper configHelper, EntitatRepository entitatRepository, IntegracioHelper integracioHelper, ConversioTipusHelper conversioTipusHelper, NotificacioEventHelper notificacioEventHelper, NotificacioRepository notificacioRepository, NotificacioEventRepository notificacioEventRepository, PluginHelper pluginHelper, EntregaPostalRepository entregaPostalRepository, NotificacioEnviamentRepository enviamentRepository, NotificacioTableViewRepository notificacioTableViewRepository, MessageHelper messageHelper, NotificacioTableHelper notificacioTableHelper) {

        this.configHelper = configHelper;
        this.entitatRepository = entitatRepository;
        this.integracioHelper = integracioHelper;
        this.conversioTipusHelper = conversioTipusHelper;
        this.notificacioEventHelper = notificacioEventHelper;
        this.notificacioRepository = notificacioRepository;
        this.notificacioEventRepository = notificacioEventRepository;
        this.pluginHelper = pluginHelper;
        this.entregaPostalRepository = entregaPostalRepository;
        this.enviamentRepository = enviamentRepository;
        this.notificacioTableViewRepository = notificacioTableViewRepository;
        this.messageHelper = messageHelper;
        this.notificacioTableHelper = notificacioTableHelper;
    }

    public void reset() {
        ciePlugin = new HashMap<>();
    }

    @Transactional
    public RespostaCie enviar(String notificacioReferencia) {

        var notificacio = notificacioRepository.findByReferencia(notificacioReferencia);
        var codiDir3Entitat = notificacio.getEntitat().getDir3Codi();
        var info = new IntegracioInfo(IntegracioCodi.CIE, "Enviar entrega postal", IntegracioAccioTipusEnumDto.ENVIAMENT,
                new AccioParam("Codi Dir3 de l'entitat", codiDir3Entitat),
                new AccioParam("Notificacio", notificacio.getId() + ""));
        info.setAplicacio(notificacio.getTipusUsuari(), notificacio.getCreatedBy().get().getCodi());
        var resposta = new RespostaCie();
        try {
            EntitatEntity entitat = entitatRepository.findByDir3Codi(codiDir3Entitat);
            if (entitat == null) {
                throw new Exception("Entitat amb codiDir3 " + codiDir3Entitat+ "no trobada");
            }
            info.setCodiEntitat(entitat.getCodi());
            byte[] contingut = null;
            if(notificacio.getDocument() != null && notificacio.getDocument().getArxiuGestdocId() != null) {
                var baos = new ByteArrayOutputStream();
                ConfigHelper.setEntitatCodi(entitat.getCodi());
                pluginHelper.gestioDocumentalGet(notificacio.getDocument().getArxiuGestdocId(), PluginHelper.GESDOC_AGRUPACIO_NOTIFICACIONS, baos);
                contingut = baos.size() > 0 ? baos.toByteArray() : null;
            } else {
                contingut = pluginHelper.documentToRegistreAnnexDto(notificacio.getDocument()).getArxiuContingut();
            }
            if (contingut == null) {
                throw new Exception("El document no te contingut " + notificacio.getDocument().getId());
            }
            var enviamentCie = conversioTipusHelper.convertir(notificacio, EnviamentCie.class);
            enviamentCie.setContingutDocument(contingut);
            enviamentCie.setCodiDir3Entitat(configHelper.getConfigAsBoolean("es.caib.notib.plugin.codi.dir3.entitat"));
            var entregaCieEfectiva = notificacio.getProcediment().getEntregaCieEfectiva();
            entregaCieEfectiva = entregaCieEfectiva == null ? notificacio.getOrganGestor().getEntregaCie() : entregaCieEfectiva;
            var pagadorCieEntity = entregaCieEfectiva.getCie();
            var apiKey = getApiKey(pagadorCieEntity);
            var cie = conversioTipusHelper.convertir(pagadorCieEntity, CieDto.class);
            cie.setApiKey(apiKey);
            enviamentCie.setEntregaCie(cie);
            enviamentCie.setOperadorPostal(conversioTipusHelper.convertir(entregaCieEfectiva.getOperadorPostal(), OperadorPostalDto.class));
            resposta = getCiePlugin(entitat.getCodi()).enviar(enviamentCie);
            if ("000".equals(resposta.getCodiResposta())) {
                integracioHelper.addAccioOk(info);
            } else {
                integracioHelper.addAccioError(info, resposta.getDescripcioError());
            }
        } catch (Exception ex) {
            var errorDescripcio = "Error al accedir al plugin CIE";
            integracioHelper.addAccioError(info, errorDescripcio, ex);
            if (ex.getCause() != null) {
                errorDescripcio += " :" + ex.getCause().getMessage();
            }
            resposta.setCodiResposta(ERROR_INESPERAT);
            resposta.setDescripcioError(errorDescripcio);
//            throw new SistemaExternException(IntegracioCodiEnum.CIE.name(), errorDescripcio, ex);
        }
        guardarRespostaCie(notificacioReferencia, resposta);
        notificacioTableHelper.actualitzarRegistre(notificacio);
        return resposta;
    }

    private void guardarRespostaCie(String notificacioReferencia, RespostaCie resposta) {

        try {
            var notificacio = notificacioRepository.findByReferencia(notificacioReferencia);
            var enviaments = notificacio.getEnviaments();
            Map<String, NotificacioEnviamentEntity> envs = new HashMap<>();
            for (var env : enviaments) {
                envs.put(env.getTitular().getNif(), env);
            }
            NotificacioEnviamentEntity env;
            EntregaPostalEntity entregaPostal;
            if (resposta.getIdentificadors() == null) {
                afegirEventsEnviarCie(notificacioReferencia, resposta);
                return;
            }
            for (var id : resposta.getIdentificadors()) {
                if (Strings.isNullOrEmpty(id.getNifTitular())) {
                    log.error("[ENTREGA_POSTAL] Error al guardar l'enviament CIE. Resposta amb id " + id.getIdentificador() + " sense NIF");
                    continue;
                }
                if (Strings.isNullOrEmpty(id.getIdentificador())) {
                    log.error("[ENTREGA_POSTAL] Error al guardar l'enviament CIE. Resposta amb NIF " + id.getNifTitular() + " no te identificador");
                    continue;
                }
                env = envs.get(id.getNifTitular());
                entregaPostal = env.getEntregaPostal();
                if (entregaPostal == null) {
                    log.error("[ENTREGA_POSTAL] Error al guardar l'enviament CIE. El NIF " + id.getNifTitular() + " no pertany a cap dels enviamentsç ");
                    continue;
                }
                entregaPostal.setCieId(id.getIdentificador());
                entregaPostal.setCieEstat(CieEstat.ENVIADO_CI);
                entregaPostalRepository.save(entregaPostal);
            }
            afegirEventsEnviarCie(notificacioReferencia, resposta);
        } catch (Exception ex) {
            log.error("Error guardant l'enviament CIE a la BDD", ex);
        }
    }

    private void afegirEventsEnviarCie(String notificacioReferencia, RespostaCie resposta) {

        var notificacio = notificacioRepository.findByReferencia(notificacioReferencia);
        var events = notificacioEventRepository.findEventsCieByNotificacioId(notificacio.getId());
        var maxReintents = configHelper.getConfigAsInteger("es.caib.notib.plugin.cie.max.reintents");
        var fiReintents = events != null && !events.isEmpty() && events.get(0).getIntents() >= maxReintents;
        for (var env : notificacio.getEnviaments()) {
            if (resposta == null) {
                notificacioEventHelper.addCieEventEnviar(env, true, ERROR_INESPERAT, fiReintents);
                continue;
            }
            var error = !"000".equals(resposta.getCodiResposta());
            notificacioEventHelper.addCieEventEnviar(env, error, error ? resposta.getDescripcioError() : "", fiReintents);
            if (error) {
                var entregaPostal = env.getEntregaPostal();
                entregaPostal.setCieEstat(CieEstat.ERROR);
                entregaPostal.setCieErrorDesc(resposta.getDescripcioError());
                entregaPostalRepository.save(entregaPostal);
//                var notTable = notificacioTableViewRepository.findById(notificacio.getId()).get();
//                notTable.setErrorLastEvent(true);
            }
        }
    }

    @Transactional
    public boolean cancelar(String uuidEnviament) {

        var enviament = enviamentRepository.findByUuid(uuidEnviament).orElse(null);
        if (enviament == null) {
            log.error("[ENTREGA_POSTAL] Error al cancelar l'enviament CIE. UUID " + uuidEnviament + " no trobat");
            return false;
        }
        var codiDir3Entitat = enviament.getNotificacio().getEntitat().getDir3Codi();
        var info = new IntegracioInfo(IntegracioCodi.CIE, "Cancelar entrega postal", IntegracioAccioTipusEnumDto.ENVIAMENT,
                new AccioParam("Codi Dir3 de l'entitat", codiDir3Entitat),
                new AccioParam("Enviament", enviament.getId() + ""));
        info.setAplicacio(enviament.getNotificacio().getTipusUsuari(), enviament.getNotificacio().getCreatedBy().get().getCodi());
        var resposta = new RespostaCie();
        try {
            EntitatEntity entitat = entitatRepository.findByDir3Codi(codiDir3Entitat);
            if (entitat == null) {
                throw new Exception("Entitat amb codiDir3 " + codiDir3Entitat+ "no trobada");
            }
            info.setCodiEntitat(entitat.getCodi());
            var cieEntity = enviament.getNotificacio().getProcediment().getEntregaCieEfectiva();
            var apiKey = getApiKey(cieEntity.getCie());
            var cie = conversioTipusHelper.convertir(cieEntity, CieDto.class);
            cie.setApiKey(apiKey);
            var enviamentCie = new EnviamentCie();
            enviamentCie.setIdentificador(enviament.getEntregaPostal().getCieId());
            enviamentCie.setEntregaCie(cie);
            resposta = getCiePlugin(entitat.getCodi()).cancelar(enviamentCie);
            if ("000".equals(resposta.getCodiResposta())) {
                integracioHelper.addAccioOk(info);
                enviament.getEntregaPostal().setCieCancelat(true);
                enviament.getEntregaPostal().setCieEstat(CieEstat.CANCELADO);
                cancelarUpdateEntregaPostal(uuidEnviament, true);
            } else {
                integracioHelper.addAccioError(info, resposta.getDescripcioError());
                cancelarUpdateEntregaPostal(uuidEnviament, false);
            }
            return true;
        } catch (Exception ex) {
            var errorDescripcio = "Error al accedir al plugin CIE";
            integracioHelper.addAccioError(info, errorDescripcio, ex);
            cancelarUpdateEntregaPostal(uuidEnviament, false);
            if (ex.getCause() != null) {
                errorDescripcio += " :" + ex.getCause().getMessage();
            }
            resposta.setDescripcioError(errorDescripcio);
            throw new SistemaExternException(IntegracioCodi.CIE.name(), errorDescripcio, ex);
        }
    }

    private void cancelarUpdateEntregaPostal(String uuidEnviament, boolean ok) {

        var enviament = enviamentRepository.findByUuid(uuidEnviament).orElse(null);
        if (enviament == null) {
            log.error("[ENTREGA_POSTAL]  Error al guardar el resultat de cancelar l'enviament CIE. UUID " + uuidEnviament + " no trobat");
            return;
        }
        enviament.getEntregaPostal().setCieCancelat(ok);
        var event = notificacioEventRepository.findEventCieByEnviamentId(enviament.getId());
        var maxReintents = configHelper.getConfigAsInteger("es.caib.notib.plugin.cie.max.reintents");
        var fiReintents = event != null && event.getIntents() > maxReintents;
        notificacioEventHelper.addCieEventCancelar(enviament, !ok, !ok ? "Error al cancelar l'enviament" : "", fiReintents);
    }

    @Transactional
    public InfoCie consultarEstatEntregaPostal(Long enviamentId) {

        var enviament = enviamentRepository.findById(enviamentId).orElse(null);
        if (enviament == null) {
            log.error("[ENTREGA_POSTAL] Error al consultar l'estat de l'entrega postal l'enviament CIE. enviamentId " + enviamentId + " no trobat");
            return null;
        }
        var codiDir3Entitat = enviament.getNotificacio().getEntitat().getDir3Codi();
        var info = new IntegracioInfo(IntegracioCodi.CIE, "Consulta estat entrega postal", IntegracioAccioTipusEnumDto.ENVIAMENT,
                new AccioParam("Codi Dir3 de l'entitat", codiDir3Entitat),
                new AccioParam("Enviament", enviament.getId() + ""));
        info.setAplicacio(enviament.getNotificacio().getTipusUsuari(), enviament.getNotificacio().getCreatedBy().get().getCodi());
        var infoCie = new InfoCie();
        try {
            EntitatEntity entitat = entitatRepository.findByDir3Codi(codiDir3Entitat);
            if (entitat == null) {
                throw new Exception("Entitat amb codiDir3 " + codiDir3Entitat+ "no trobada");
            }
            info.setCodiEntitat(entitat.getCodi());
            var cieEntity = enviament.getNotificacio().getProcediment().getEntregaCieEfectiva();
            cieEntity = cieEntity == null ? enviament.getNotificacio().getOrganGestor().getEntregaCie() : cieEntity;
            var apiKey = getApiKey(cieEntity.getCie());
            var cie = conversioTipusHelper.convertir(cieEntity, CieDto.class);
            cie.setApiKey(apiKey);
            var enviamentCie = new EnviamentCie();
            enviamentCie.setIdentificador(enviament.getEntregaPostal().getCieId());
            enviamentCie.setEntregaCie(cie);
            infoCie = getCiePlugin(entitat.getCodi()).consultarEstat(enviamentCie);
            if ("000".equals(infoCie.getCodiResposta())) {
                integracioHelper.addAccioOk(info);
                enviament.getEntregaPostal().setCieEstat(infoCie.getCodiEstat());
                if (CieEstat.ERROR.equals(infoCie.getCodiEstat()) && Strings.isNullOrEmpty(enviament.getEntregaPostal().getCieErrorDesc())) {
                    enviament.getEntregaPostal().setCieErrorDesc(messageHelper.getMessage("entrega.postal.error.pendent.adviser"));
                }
                notificacioEventHelper.addCieEventConsultaEstat(enviament, false, "", false);
            } else {
                integracioHelper.addAccioError(info, infoCie.getDescripcioResposta());
                notificacioEventHelper.addCieEventConsultaEstat(enviament, true, infoCie.getDescripcioResposta(), false);
            }
            return infoCie;
        } catch (Exception ex) {
            var errorDescripcio = "Error al accedir al plugin CIE";
            integracioHelper.addAccioError(info, errorDescripcio, ex);
            if (ex.getCause() != null) {
                errorDescripcio += " :" + ex.getCause().getMessage();
            }
            infoCie.setDescripcioResposta(errorDescripcio);
            notificacioEventHelper.addCieEventConsultaEstat(enviament, true, infoCie.getDescripcioResposta(), false);
            throw new SistemaExternException(IntegracioCodi.CIE.name(), errorDescripcio, ex);
        }
    }

    private String getApiKey(PagadorCieEntity pagadorCieEntity) {

        var encripter = new EncryptionUtil(configHelper.getConfig("es.caib.notib.plugin.cie.encriptor.key"), pagadorCieEntity.getSalt());
        return encripter.decrypt(pagadorCieEntity.getApiKey());
    }

    private CiePlugin getCiePlugin(String codiEntitat) {

        if (Strings.isNullOrEmpty(codiEntitat)) {
            throw new RuntimeException("El codi d'entitat no pot ser nul");
        }

        var plugin = ciePlugin.get(codiEntitat);
        if (plugin != null) {
            return plugin;
        }
        var pluginClass = configHelper.getConfig("es.caib.notib.plugin.cie.class");
        if (Strings.isNullOrEmpty(pluginClass)) {
            var msg = "\"La classe del plugin CIE no està definida\"";
            log.error(msg);
            throw new SistemaExternException(IntegracioCodi.REGISTRE.name(), msg);
        }
        try {
            Class<?> clazz = Class.forName(pluginClass);
            plugin = (CiePlugin) clazz.getDeclaredConstructor(Properties.class).newInstance(configHelper.getAllEntityProperties(codiEntitat));
            ciePlugin.put(codiEntitat, plugin);
            return plugin;
        } catch (Exception ex) {
            var msg = "\"Error al crear la instància del plugin CIE (\" + pluginClass + \") \"";
            log.error(msg, ex);
            throw new SistemaExternException(IntegracioCodi.REGISTRE.name(), msg, ex);
        }
    }


}
