package es.caib.notib.core.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.LoggingFilter;
import es.caib.notib.core.api.ws.callback.NotificacioCanviClient;
import org.springframework.stereotype.Component;

@Component
public class RequestsHelper {

    private static Integer CONNECT_TIMEOUT = 10000;
    private static Integer READ_TIMEOUT = 30000;

    public ClientResponse callbackAplicacioNotificaCanvi(String urlCallback, NotificacioCanviClient contingut)
            throws JsonProcessingException {
        // Passa l'objecte a JSON
        ObjectMapper mapper  = new ObjectMapper();
        String body = mapper.writeValueAsString(contingut);

        // Prepara el client JSON per a la crida POST
        Client jerseyClient = this.getClient();

        // Fa la crida POST passant les dades JSON
        return jerseyClient.
                resource(urlCallback).
                type("application/json").
                post(ClientResponse.class, body);
    }

    private Client getClient() {
        Client jerseyClient =  new Client();
        jerseyClient.setConnectTimeout(CONNECT_TIMEOUT);
        jerseyClient.setReadTimeout(READ_TIMEOUT);
        // Només per depurar la sortida, esborrar o comentar-ho:
        jerseyClient.addFilter(new LoggingFilter(System.out));
        return jerseyClient;
    }
}
