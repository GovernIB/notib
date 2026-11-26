package es.caib.notib.api.interna.config;

import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class SwaggerTCCLFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        String uri = ((HttpServletRequest) req).getRequestURI();
        if (uri.contains("/swagger-ui/") || uri.contains("/webjars/")) {
            ClassLoader original = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
            try {
                chain.doFilter(req, res);
            } finally {
                Thread.currentThread().setContextClassLoader(original);
            }
        } else {
            chain.doFilter(req, res);
        }
    }
}