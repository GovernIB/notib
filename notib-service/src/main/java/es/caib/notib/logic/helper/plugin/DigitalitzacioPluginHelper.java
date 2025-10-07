package es.caib.notib.logic.helper.plugin;

import com.google.common.base.Strings;
import es.caib.comanda.ms.salut.model.IntegracioApp;
import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.helper.IntegracioHelper;
import es.caib.notib.logic.intf.dto.AccioParam;
import es.caib.notib.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioCodi;
import es.caib.notib.logic.intf.dto.IntegracioInfo;
import es.caib.notib.logic.intf.dto.UsuariDto;
import es.caib.notib.logic.intf.dto.escaneig.DigitalitzacioEstat;
import es.caib.notib.logic.intf.dto.escaneig.DigitalitzacioPerfil;
import es.caib.notib.logic.intf.dto.escaneig.DigitalitzacioResultat;
import es.caib.notib.logic.intf.dto.escaneig.DigitalitzacioTransaccioResposta;
import es.caib.notib.logic.intf.exception.SistemaExternException;
import es.caib.notib.logic.intf.service.AplicacioService;
import es.caib.notib.persist.repository.EntitatRepository;
import es.caib.notib.plugin.digitalitzacio.DigitalitzacioPlugin;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Slf4j
@Component
public class DigitalitzacioPluginHelper extends AbstractPluginHelper<DigitalitzacioPlugin> {

    public static final String GRUP = "DigitalIB";
    private final AplicacioService aplicacioService;

    public DigitalitzacioPluginHelper(IntegracioHelper integracioHelper,
                                      ConfigHelper configHelper,
                                      EntitatRepository entitatRepository,
                                      MeterRegistry meterRegistry,
                                      @Lazy AplicacioService aplicacioService) {

        super(integracioHelper, configHelper, entitatRepository, meterRegistry);
        this.aplicacioService = aplicacioService;
    }

    public List<DigitalitzacioPerfil> digitalitzacioPerfilsDisponibles() {

//        Timer.Sample sample = Timer.start(aplicacioService.getMeterRegistry());
        long t0 = System.currentTimeMillis();
        var idioma = aplicacioService.getUsuariActual().getIdioma();
        if (idioma != null) {
            idioma = idioma.toLowerCase();
        }
        var info = new IntegracioInfo(IntegracioCodi.DIGITALITZACIO, "Recuperant perfils disponibles", IntegracioAccioTipusEnumDto.ENVIAMENT,
                new AccioParam("Idioma", idioma),
                new AccioParam("Endpoint", getEndpointURL()),
                new AccioParam("Durada", String.valueOf(System.currentTimeMillis() - t0)));
        List<DigitalitzacioPerfil> perfilsDto = new ArrayList<>();
        try {
            List<DigitalitzacioPerfil> perfils = getPlugin().recuperarPerfilsDisponibles(idioma);
            if (perfils == null || perfils.isEmpty()) {
                integracioHelper.addAccioError(info, "No s'ha recuperat cap perfil de la digitalització");
                return perfilsDto;
            }
            DigitalitzacioPerfil perfilDto;
            for (var perfil : perfils) {
                perfilDto = new DigitalitzacioPerfil();
                perfilDto.setCodi(perfil.getCodi());
                perfilDto.setNom(perfil.getNom());
                perfilDto.setDescripcio(perfil.getDescripcio());
                perfilDto.setTipus(perfil.getTipus());
                perfilsDto.add(perfilDto);
            }
            integracioHelper.addAccioOk(info);
//            applicationHelper.stopTimer(sample, "METRICS@Integracions.digitalitzacio", "resultado", "exito", "endpoint", Utils.hasValue(endpoint)?endpoint:"N/D");
            return perfilsDto;
        } catch (Exception ex) {
            integracioHelper.addAccioError(info, "Error al obtenir els perfils disponibles", ex);
//            applicationHelper.stopTimer(sample, "METRICS@Integracions.digitalitzacio", "resultado", "error", "endpoint", Utils.hasValue(endpoint)?endpoint:"N/D");
            throw new SistemaExternException(IntegracioCodi.DIGITALITZACIO.name(), "Error al accedir al plugin de digitalitzacio", ex);
        }
    }

    public DigitalitzacioTransaccioResposta digitalitzacioIniciarProces(String idioma, String codiPerfil, UsuariDto funcionari, String urlReturn) {

//        Timer.Sample sample = Timer.start(aplicacioService.getMeterRegistry());
        long t0 = System.currentTimeMillis();
        var respostaDto = new DigitalitzacioTransaccioResposta();
        var info = new IntegracioInfo(IntegracioCodi.DIGITALITZACIO, "Iniciant procés digitalització", IntegracioAccioTipusEnumDto.ENVIAMENT,
                new AccioParam("Idioma", idioma),
                new AccioParam("Endpoint", getEndpointURL()),
                new AccioParam("Codi perfil", codiPerfil),
                new AccioParam("Url retorn", urlReturn),
                new AccioParam("Durada", String.valueOf(System.currentTimeMillis() - t0)));
        try {
            var resposta = getPlugin().iniciarProces(codiPerfil, idioma, funcionari, urlReturn);
            if (resposta == null) {
                integracioHelper.addAccioError(info, "No s'ha rebut resposta al iniciar el procés de digitalització");
                return respostaDto;
            }
            respostaDto.setIdTransaccio(resposta.getIdTransaccio());
            respostaDto.setUrlRedireccio(resposta.getUrlRedireccio());
            respostaDto.setReturnScannedFile(resposta.isReturnScannedFile());
            respostaDto.setReturnSignedFile(resposta.isReturnSignedFile());
            integracioHelper.addAccioOk(info);
//            applicationHelper.stopTimer(sample, "METRICS@Integracions.digitalitzacio", "resultado", "exito", "endpoint", Utils.hasValue(endpoint)?endpoint:"N/D");
            return respostaDto;
        } catch (Exception ex) {
            integracioHelper.addAccioError(info, "Error al iniciar el procés de digitalitzacio", ex);
//            applicationHelper.stopTimer(sample, "METRICS@Integracions.digitalitzacio", "resultado", "error", "endpoint", Utils.hasValue(endpoint)?endpoint:"N/D");
            throw new SistemaExternException(IntegracioCodi.DIGITALITZACIO.name(), "Error al accedir al plugin de digitalitzacio", ex);
        }
    }

    public DigitalitzacioResultat digitalitzacioRecuperarResultat(String idTransaccio, boolean returnScannedFile, boolean returnSignedFile) {

//        Timer.Sample sample = Timer.start(aplicacioService.getMeterRegistry());
        long t0 = System.currentTimeMillis();
        var info = new IntegracioInfo(IntegracioCodi.DIGITALITZACIO, "Recuperant resultat de la digitalització", IntegracioAccioTipusEnumDto.ENVIAMENT,
                new AccioParam("idTransaccio", idTransaccio),
                new AccioParam("Endpoint", getEndpointURL()),
                new AccioParam("returnScannedFile", String.valueOf(returnScannedFile)),
                new AccioParam("returnSignedFile", String.valueOf(returnSignedFile)),
                new AccioParam("Durada", String.valueOf(System.currentTimeMillis() - t0)));
        var resultatDto = new DigitalitzacioResultat();
        try {
            var resultat = getPlugin().recuperarResultat(idTransaccio, returnScannedFile, returnSignedFile);
            if (resultat == null) {
                integracioHelper.addAccioError(info, "No sa recuperat cap resultat de la digitalització");
                return resultatDto;
            }
            resultatDto.setError(resultat.isError());
            resultatDto.setErrorDescripcio(resultat.getErrorDescripcio());
            resultatDto.setEstat(resultat.getEstat() != null ? DigitalitzacioEstat.valueOf(resultat.getEstat().toString()) : null);
            resultatDto.setContingut(resultat.getContingut());
            resultatDto.setNomDocument(resultat.getNomDocument());
            resultatDto.setMimeType(resultat.getMimeType());
            resultatDto.setEniTipoFirma(resultat.getEniTipoFirma());
            resultatDto.setIdioma(resultat.getIdioma());
            resultatDto.setResolucion(resultat.getResolucion());
            integracioHelper.addAccioOk(info);
//                applicationHelper.stopTimer(sample, "METRICS@Integracions.digitalitzacio", "resultado", "exito", "endpoint", Utils.hasValue(endpoint)?endpoint:"N/D");
            return resultatDto;
        } catch (Exception ex) {
            integracioHelper.addAccioError(info, "Error al recuperar el resultat de digitalització", ex);
//            applicationHelper.stopTimer(sample, "METRICS@Integracions.digitalitzacio", "resultado", "error", "endpoint", Utils.hasValue(endpoint)?endpoint:"N/D");
            throw new SistemaExternException(IntegracioCodi.DIGITALITZACIO.name(), "Error al accedir al plugin de digitalitzacio", ex);

        }
    }

    public void digitalitzacioTancarTransaccio(String idTransaccio) {

//        Timer.Sample sample = Timer.start(aplicacioService.getMeterRegistry());
        long t0 = System.currentTimeMillis();
        var info = new IntegracioInfo(IntegracioCodi.DIGITALITZACIO, "Tancant transacció digitalització", IntegracioAccioTipusEnumDto.ENVIAMENT,
                new AccioParam("idTransaccio", idTransaccio),
                new AccioParam("Endpoint", getEndpointURL()),
                new AccioParam("Durada", String.valueOf(System.currentTimeMillis() - t0)));
        try {
            getPlugin().tancarTransaccio(idTransaccio);
            integracioHelper.addAccioOk(info);
//            applicationHelper.stopTimer(sample, "METRICS@Integracions.digitalitzacio", "resultado", "exito", "endpoint", Utils.hasValue(endpoint)?endpoint:"N/D");
        } catch (Exception ex) {
            integracioHelper.addAccioError(info, "Error tancant transacció digitalització", ex);
//            applicationHelper.stopTimer(sample, "METRICS@Integracions.digitalitzacio", "resultado", "error", "endpoint", Utils.hasValue(endpoint)?endpoint:"N/D");
            throw new SistemaExternException(IntegracioCodi.DIGITALITZACIO.name(), "Error al accedir al plugin de digitalitzacio", ex);
        }
    }

    @Override
    protected DigitalitzacioPlugin getPlugin() {

        var codiEntitat = getCodiEntitatActual();
        if (Strings.isNullOrEmpty(codiEntitat)) {
            throw new RuntimeException("El codi d'entitat no pot ser nul");
        }

        var plugin = pluginMap.get(codiEntitat);
        if (plugin != null) {
            return plugin;
        }
        var pluginClass = getPluginClassProperty();
        if (Strings.isNullOrEmpty(pluginClass)) {
            var msg = "\"La classe del plugin de digitalitzacio no està definida\"";
            log.error(msg);
            throw new SistemaExternException(IntegracioCodi.REGISTRE.name(), msg);
        }
        try {
            var configuracioEspecifica = configHelper.hasEntityGroupPropertiesModified(codiEntitat, getConfigGrup());
            var propietats = configHelper.getAllEntityProperties(codiEntitat);
            Class<?> clazz = Class.forName(pluginClass);
            plugin = (DigitalitzacioPlugin) clazz.getDeclaredConstructor(Properties.class, boolean.class).newInstance(propietats, configuracioEspecifica);
            plugin.init(meterRegistry, getCodiApp().name(), codiEntitat);
            pluginMap.put(codiEntitat, plugin);
            return plugin;
        } catch (Exception ex) {
            var msg = "\"Error al crear la instància del plugin de digitalitzacio (\" + pluginClass + \") \"";
            log.error(msg, ex);
            throw new SistemaExternException(IntegracioCodi.DIGITALITZACIO.name(), msg, ex);
        }
    }

    private String getEndpointURL() {
        return configHelper.getConfig("es.caib.notib.plugin.digitalitzacio.endpointName");
    }

    @Override
    protected String getPluginClassProperty() {
        return configHelper.getConfig("es.caib.notib.plugin.digitalitzacio.class");
    }

    @Override
    protected IntegracioApp getCodiApp() {
        return IntegracioApp.DIB;
    }

    @Override
    protected String getConfigGrup() {
        return GRUP;
    }

    @Override
    public boolean diagnosticar(Map diagnostics) throws Exception {
        return false;
    }



}
