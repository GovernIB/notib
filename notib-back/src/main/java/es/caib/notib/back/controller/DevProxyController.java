package es.caib.notib.back.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

@Controller
@Profile("devProxy")
@RequestMapping("/reactapp")
public class DevProxyController {

    @Value("${es.caib.notib.development.proxyUrl:http://localhost:5173}")
    private String proxyUrl;

    @RequestMapping("/**")
    public ResponseEntity<byte[]> proxy(HttpServletRequest request) {
        String path = request.getRequestURI();//.replaceFirst("/proxy", "");
        String query = request.getQueryString();
        String targetUrl = proxyUrl + path + (query != null ? "?" + query : "");

        HttpHeaders headers = new HttpHeaders();
        Collections.list(request.getHeaderNames()).forEach(name -> {
            headers.add(name, request.getHeader(name));
        });

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<?> entity = new HttpEntity<>(headers);
        ResponseEntity<byte[]> response = restTemplate.exchange(targetUrl, HttpMethod.GET, entity, byte[].class);

        HttpHeaders responseHeaders = new HttpHeaders();
        response.getHeaders().forEach((key, value) -> responseHeaders.put(key, value));

        return new ResponseEntity<>(response.getBody(), responseHeaders, response.getStatusCode());
    }
}