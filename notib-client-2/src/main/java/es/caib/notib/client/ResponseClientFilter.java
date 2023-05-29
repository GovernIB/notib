package es.caib.notib.client;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import java.io.IOException;
import java.util.ArrayList;

public class ResponseClientFilter implements ClientResponseFilter {

    private ArrayList<Object> cookies;
    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {

        if (cookies != null) {
            requestContext.getHeaders().put("Cookie", cookies);
        }
        if (responseContext.getCookies() == null) {
            return;
        }
        if (cookies == null) {
            cookies = new ArrayList<>();
        }
        cookies.addAll(responseContext.getCookies().entrySet());
    }
}
