package es.caib.notib.plugin.carpeta;

import com.sun.jersey.api.client.Client;
import javax.ws.rs.client.WebTarget;

import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import lombok.extern.slf4j.Slf4j;

import java.util.Properties;

@Slf4j
public class CarpetaCaibImpl implements CarpetaPlugin {

    private Client client;
    private final Properties properties;


    public CarpetaCaibImpl(String prefix, Properties properties) {
        this.properties = properties;

    }

    @Override
    public void enviarNotificacioMobil(MissatgeCarpetaParams params) {

        log.info("Enviant avís a CARPETA");
        try {
            String url = properties.getProperty("es.caib.notib.plugin.carpeta.url");
            url = "https://se.caib.es/carpetaapi/interna/secure/mobilenotification/existcitizen?nif=12345678Z&lang=ca";
            generarClient(url);
            client.resource(url).get(String.class);
            log.info("Avís enviat a CARPETA");
        } catch (Exception ex) {
            log.error("[API CARPETA] Error enviant la notificacio mòvil" + ex);
        }
    }

    private void generarClient(String url) {

        if (client != null) {
            return;
        }
        String username = "null";
        String password = "null";
        client = Client.create();
        client.addFilter(new HTTPBasicAuthFilter(username, password));

    }
}
