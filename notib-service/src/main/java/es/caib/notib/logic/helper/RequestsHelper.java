package es.caib.notib.logic.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.LoggingFilter;
import es.caib.notib.logic.intf.dto.callback.NotificacioCanviClient;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@Component
public class RequestsHelper {

    private static Integer CONNECT_TIMEOUT = 5000;
    private static Integer READ_TIMEOUT = 20000;

    public ClientResponse callbackAplicacioNotificaCanvi(String urlCallback, NotificacioCanviClient contingut, boolean headerCsrf) throws JsonProcessingException {

        // Passa l'objecte a JSON
        var mapper  = new ObjectMapper();
        var body = mapper.writeValueAsString(contingut);
        // Prepara el client JSON per a la crida POST
        var jerseyClient = this.getClient();
        // Fa la crida POST passant les dades JSON
        var jersey = jerseyClient.resource(urlCallback).type("application/json");
        if (headerCsrf) {
            jersey.header("X-CSRF-Token", generateToken());
        }
        return jersey.post(ClientResponse.class, body);
    }

    private Client getClient() {

        var jerseyClient =  new Client();
        jerseyClient.setConnectTimeout(CONNECT_TIMEOUT);
        jerseyClient.setReadTimeout(READ_TIMEOUT);
        // Només per depurar la sortida, esborrar o comentar-ho:
        jerseyClient.addFilter(new LoggingFilter(System.out));
        return jerseyClient;
    }

    private String generateToken() {

        var secureRandom = new SecureRandom();
        var base64Encoder = Base64.getUrlEncoder();
        var randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }
}
