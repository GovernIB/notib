package es.caib.notib.plugin.carpeta;

import com.sun.jersey.api.client.Client;

import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import es.caib.notib.core.api.dto.NotificaCertificacioTipusEnumDto;
import es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
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
    public void enviarNotificacioMobil(MissatgeCarpetaParams params) throws Exception{

        log.info("Enviant avís a CARPETA");
        RespostaSendNotificacioMovil resposta = null;
        try {
            String url = properties.getProperty("es.caib.notib.plugin.carpeta.url");
            if (params.getTipus() == null) {
                throw new Exception("No es pot enviar la notificació mòvil. Tipus = null");
            }
            String key = "es.caib.notib.plugin.carpeta.missatge.codi." +  params.getTipus().name().toLowerCase();
            String notCode = properties.getProperty(key);
            url += "?nif=" + params.getNifDestinatari() + "&notificationCode=" + notCode + "&notificationLang=ca&langError=ca";
            //TODO FALTA AFEGIR ELS PARAMS
            initClient();
            WebResource resource = client.resource(url);
            resposta = resource.queryParam("notificationParameters", params.getParams().toString()).get(RespostaSendNotificacioMovil.class);

            log.info("Avís enviat a CARPETA");
        } catch (Exception ex) {
            String msg =  "[API CARPETA] Error enviant la notificacio mòvil. Codi: "; //+ resposta.getCode() + " missatge: " + resposta.getMessage();
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
