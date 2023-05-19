package es.caib.notib.plugin.carpeta;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import es.caib.notib.core.api.dto.NotificaCertificacioTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Properties;

@Slf4j
public class CarpetaCaibImpl implements CarpetaPlugin {

    private Client client;
    private final Properties properties;

    public CarpetaCaibImpl(Properties properties) {
        this.properties = properties;

    }

    @Override
    public RespostaSendNotificacioMovil enviarNotificacioMobil(MissatgeCarpetaParams params) throws Exception{

        log.info("Enviant avís a CARPETA");
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
            queryParams.add("notificationLang", "ca");
            queryParams.add("langError", "ca");
            params.setQueryParams(queryParams);
            ClientResponse res = resource.queryParams(queryParams).type(MediaType.APPLICATION_JSON_TYPE).accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);
            String jsonResposta = res.getEntity(String.class);
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            resposta = mapper.readValue(jsonResposta, RespostaSendNotificacioMovil.class);
            log.info("Avís enviat a CARPETA");
            return resposta;
        } catch (Exception ex) {
            String msg =  "[API CARPETA] Error enviant la notificacio mòvil.";
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
        String password = properties.getProperty("es.caib.notib.plugin.carpeta.contrasenya");;
        client = Client.create();
        client.addFilter(new HTTPBasicAuthFilter(username, password));
    }
}
