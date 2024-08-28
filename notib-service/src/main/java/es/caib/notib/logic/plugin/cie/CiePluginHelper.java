package es.caib.notib.logic.plugin.cie;

import com.google.common.base.Strings;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.ConversioTipusHelper;
import es.caib.notib.logic.helper.IntegracioHelper;
import es.caib.notib.logic.helper.PluginHelper;
import es.caib.notib.logic.intf.dto.AccioParam;
import es.caib.notib.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioCodiEnum;
import es.caib.notib.logic.intf.dto.IntegracioInfo;
import es.caib.notib.logic.intf.dto.cie.CieDto;
import es.caib.notib.logic.intf.dto.cie.OperadorPostalDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioDtoV2;
import es.caib.notib.logic.intf.exception.SistemaExternException;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.repository.EntitatRepository;
import es.caib.notib.plugin.cie.CiePlugin;
import es.caib.notib.plugin.cie.EnviamentCie;
import es.caib.notib.plugin.cie.RespostaCie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Slf4j
@Component
public class CiePluginHelper {

    private Map<String, CiePlugin> ciePlugin = new HashMap<>();
    private final ConversioTipusHelper conversioTipusHelper;
    private final ConfigHelper configHelper;
    private final EntitatRepository entitatRepository;
    private final IntegracioHelper integracioHelper;
    private final PluginHelper pluginHelper;

    public CiePluginHelper(ConfigHelper configHelper, EntitatRepository entitatRepository, IntegracioHelper integracioHelper, ConversioTipusHelper conversioTipusHelper, PluginHelper pluginHelper) {

        this.configHelper = configHelper;
        this.entitatRepository = entitatRepository;
        this.integracioHelper = integracioHelper;
        this.conversioTipusHelper = conversioTipusHelper;
        this.pluginHelper = pluginHelper;
    }

    public void reset() {
        ciePlugin = new HashMap<>();
    }

    public RespostaCie enviar(NotificacioEntity notificacio) {

        var codiDir3Entitat = notificacio.getEntitat().getDir3Codi();
        var info = new IntegracioInfo(IntegracioCodiEnum.CIE, "Enviar entrega postal", IntegracioAccioTipusEnumDto.ENVIAMENT,
                new AccioParam("Codi Dir3 de l'entitat", codiDir3Entitat),
                new AccioParam("Notificacio", notificacio.getId() + ""));

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
            enviamentCie.setEntregaCie(conversioTipusHelper.convertir(entregaCieEfectiva, CieDto.class));
            enviamentCie.setOperadorPostal(conversioTipusHelper.convertir(entregaCieEfectiva, OperadorPostalDto.class));
            resposta = getCiePlugin(entitat.getCodi()).enviar(enviamentCie);
            if (Strings.isNullOrEmpty(resposta.getCodiError())) {
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
            resposta.setDescripcioError(errorDescripcio);
            throw new SistemaExternException(IntegracioCodiEnum.CIE.name(), errorDescripcio, ex);
        }
        return resposta;
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
            throw new SistemaExternException(IntegracioCodiEnum.REGISTRE.name(), msg);
        }
        try {
            Class<?> clazz = Class.forName(pluginClass);
            plugin = (CiePlugin) clazz.getDeclaredConstructor(Properties.class).newInstance(configHelper.getAllEntityProperties(codiEntitat));
            ciePlugin.put(codiEntitat, plugin);
            return plugin;
        } catch (Exception ex) {
            var msg = "\"Error al crear la instància del plugin CIE (\" + pluginClass + \") \"";
            log.error(msg, ex);
            throw new SistemaExternException(IntegracioCodiEnum.REGISTRE.name(), msg, ex);
        }
    }
}
