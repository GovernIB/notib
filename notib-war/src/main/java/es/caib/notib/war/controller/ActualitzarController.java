package es.caib.notib.war.controller;

import es.caib.notib.core.api.service.NotificacioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Controlador per actualitzacions
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Controller
@RequestMapping("/actualitzar")
public class ActualitzarController extends BaseController {

    @Autowired
    private NotificacioService notificacioService;

    @ResponseBody
    @RequestMapping(value = "/referencies", method = RequestMethod.GET)
    public String actualitzarReferencies(HttpServletRequest request, Model model) {
        return notificacioService.actualitzarReferencies() ? "Ok" : "Error: consultar el log";
    }
}
