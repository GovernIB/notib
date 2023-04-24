package es.caib.notib.plugin.carpeta;

import com.sun.jersey.api.client.Client;

import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import lombok.extern.slf4j.Slf4j;

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

        try {
            String url = properties.getProperty("es.caib.notib.plugin.carpeta.url");
            url += "?nif=" + params.getNifDestinatari() + "&notificationCode=" + params.getUuIdNotificacio() + "&notificationLang=ca&langError=ca";
            //TODO FALTA AFEGIR ELS PARAMS
//            url += params.getParams();
            initClient();
            RespostaSendNotificacioMovil resposta = client.resource(url).get(RespostaSendNotificacioMovil.class);
            log.info("Avís enviat a CARPETA");
        } catch (Exception ex) {
            String msg =  "[API CARPETA] Error enviant la notificacio mòvil" + ex;
            log.error("[API CARPETA] Error enviant la notificacio mòvil", ex);
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
