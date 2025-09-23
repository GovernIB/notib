package es.caib.notib.plugin.carpeta;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import es.caib.notib.plugin.AbstractSalutPlugin;
import es.caib.notib.plugin.utils.NotibLoggerPlugin;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.util.Properties;

@Slf4j
public class CarpetaCaibImpl extends AbstractSalutPlugin implements CarpetaPlugin {

    private Client client;
    private final Properties properties;

    private NotibLoggerPlugin logger = new NotibLoggerPlugin(log);

    public CarpetaCaibImpl(Properties properties) {

        this.properties = properties;
        this.configuracioEspecifica = false;
        logger.setMostrarLogs(Boolean.parseBoolean(properties.getProperty("es.caib.notib.log.tipus.plugin.CARPETA")));
    }

    public CarpetaCaibImpl(Properties properties, boolean configuracioEspecifica, String codiEntitat) {

        this.properties = properties;
        this.configuracioEspecifica = configuracioEspecifica;
        this.codiEntitat = codiEntitat;
        this.urlPlugin = properties.getProperty("es.caib.notib.plugin.carpeta.url");
        logger.setMostrarLogs(Boolean.parseBoolean(properties.getProperty("es.caib.notib.log.tipus.plugin.CARPETA")));
    }

    @Override
    public RespostaSendNotificacioMovil enviarNotificacioMobil(MissatgeCarpetaParams params) throws Exception{

        log.info("[CARPETA] Enviant avís a CARPETA");
        RespostaSendNotificacioMovil resposta = null;
        try {
            long startTime = System.currentTimeMillis();
            if (params.getTipus() == null) {
                throw new Exception("No es pot enviar la notificació mòvil. Tipus = null");
            }
            String url = properties.getProperty("es.caib.notib.plugin.carpeta.url");
            url += (url.endsWith("/") ? "" : "/") + "secure/mobilenotification/sendnotificationtomobile";
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
            incrementarOperacioOk(System.currentTimeMillis() - startTime);
            return resposta;
        } catch (Exception ex) {
            incrementarOperacioError();
            String msg =  "[CARPETA] Error enviant la notificacio mòvil.";
            log.error(msg, ex);
            msg += ex;
            throw new Exception(msg);
        }
    }

    @Override
    public boolean existeixNif(String nif) {

        try {
            initClient();
            var url = properties.getProperty("es.caib.notib.plugin.carpeta.url");
            url += (url.endsWith("/") ? "" : "/") + "secure/mobilenotification/existcitizen";
            var resource = client.resource(url);
            MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
            queryParams.add("nif", nif);
            queryParams.add("lang", "ca");
            var res = resource.queryParams(queryParams).type(MediaType.APPLICATION_JSON_TYPE).accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);
            if (res == null || res.getStatus() != 200) {
                return false;
            }
            var jsonResposta = res.getEntity(String.class);
            return "true".equalsIgnoreCase(jsonResposta);
        } catch (Exception ex) {
            log.error("[CARPETA] Error consultant el nif ", ex);
            throw ex;
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

}
