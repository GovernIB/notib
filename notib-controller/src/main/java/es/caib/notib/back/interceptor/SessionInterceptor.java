package es.caib.notib.back.interceptor;

import es.caib.notib.back.config.scopedata.SessionScopedContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class SessionInterceptor implements AsyncHandlerInterceptor {

    @Autowired
    private SessionScopedContext sessionScopedContext;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute("sessionScopedContext", sessionScopedContext);
        return true;
    }

}
