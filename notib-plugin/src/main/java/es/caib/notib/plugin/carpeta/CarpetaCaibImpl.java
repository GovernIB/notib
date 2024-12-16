package es.caib.notib.plugin.carpeta;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import es.caib.comanda.salut.model.EstatSalut;
import es.caib.comanda.salut.model.EstatSalutEnum;
import es.caib.comanda.salut.model.IntegracioPeticions;
import es.caib.notib.plugin.utils.NotibLoggerPlugin;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.time.Duration;
import java.time.Instant;
import java.util.Properties;

@Slf4j
public class CarpetaCaibImpl implements CarpetaPlugin {

    private Client client;
    private final Properties properties;

    private NotibLoggerPlugin logger = new NotibLoggerPlugin(log);

//    public CarpetaCaibImpl(Properties properties) {
//
//        this.properties = properties;
//        logger.setMostrarLogs(Boolean.parseBoolean(properties.getProperty("es.caib.notib.log.tipus.plugin.CARPETA")));
//    }

    public CarpetaCaibImpl(Properties properties, boolean configuracioEspecifica) {
        this.properties = properties;
        this.configuracioEspecifica = configuracioEspecifica;
        logger.setMostrarLogs(Boolean.parseBoolean(properties.getProperty("es.caib.notib.log.tipus.plugin.CARPETA")));
    }

    @Override
    public RespostaSendNotificacioMovil enviarNotificacioMobil(MissatgeCarpetaParams params) throws Exception{

        log.info("[CARPETA] Enviant avís a CARPETA");
        RespostaSendNotificacioMovil resposta = null;
        try {
            if (params.getTipus() == null) {
                throw new Exception("No es pot enviar la notificació mòvil. Tipus = null");
            }
            String url = properties.getProperty("es.caib.notib.plugin.carpeta.url");
            String key = "es.caib.notib.plugin.carpeta.missatge.codi." + params.getTipus().name().toLowerCase();
            String notCode = properties.getProperty(key);
            initClient();
            WebResource resource = client.resource(url);
            MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
            queryParams.add("nif", params.getNifDestinatari());
            queryParams.add("notificationCode", notCode);
            var lang = "ca";
            queryParams.add("notificationLang", lang);
            queryParams.add("langError", lang);
            params.setQueryParams(queryParams);
            logger.info("[CARPETA] Enviant notificacio movil nif " + params.getNifDestinatari() + " notificationCode " + notCode + " notificationLang " + lang + " langError " + lang + " url " + url);
            ClientResponse res = resource.queryParams(queryParams).type(MediaType.APPLICATION_JSON_TYPE).accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);
            String jsonResposta = res.getEntity(String.class);
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            resposta = mapper.readValue(jsonResposta, RespostaSendNotificacioMovil.class);
            log.info("[CARPETA] Avís enviat a CARPETA");
            incrementarOperacioOk();
            return resposta;
        } catch (Exception ex) {
            incrementarOperacioError();
            String msg =  "[CARPETA] Error enviant la notificacio mòvil.";
            log.error(msg, ex);
            msg += ex;
            throw new Exception(msg);
        }
    }

    private void initClient() {

        if (client != null) {
            return;
        }
        String username = properties.getProperty("es.caib.notib.plugin.carpeta.usuari");
        String password = properties.getProperty("es.caib.notib.plugin.carpeta.contrasenya");
        client = Client.create();
        client.addFilter(new HTTPBasicAuthFilter(username, password));
    }


    // Mètodes de SALUT
    // /////////////////////////////////////////////////////////////////////////////////////////////

    private boolean configuracioEspecifica = false;
    private int operacionsOk = 0;
    private int operacionsError = 0;

    @Synchronized
    private void incrementarOperacioOk() {
        operacionsOk++;
    }

    @Synchronized
    private void incrementarOperacioError() {
        operacionsError++;
    }

    @Synchronized
    private void resetComptadors() {
        operacionsOk = 0;
        operacionsError = 0;
    }

    @Override
    public boolean teConfiguracioEspecifica() {
        return this.configuracioEspecifica;
    }

    @Override
    public EstatSalut getEstatPlugin() {
        try {
            Instant start = Instant.now();
            existeixNif("99999999R");
            return EstatSalut.builder()
                    .latencia((int) Duration.between(start, Instant.now()).toMillis())
                    .estat(EstatSalutEnum.UP)
                    .build();
        } catch (Exception ex) {
            return EstatSalut.builder().estat(EstatSalutEnum.DOWN).build();
        }
    }

    @Override
    public IntegracioPeticions getPeticionsPlugin() {
        IntegracioPeticions integracioPeticions = IntegracioPeticions.builder()
                .totalOk(operacionsOk)
                .totalError(operacionsError)
                .build();
        resetComptadors();
        return integracioPeticions;
    }

}
