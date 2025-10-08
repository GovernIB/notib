package es.caib.notib.back.controller;

import es.caib.notib.logic.intf.service.NotificacioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @Lazy
    @Autowired
    private NotificacioService notificacioService;

    @ResponseBody
    @GetMapping(value = "/referencies")
    public void actualitzarReferencies(HttpServletRequest request, Model model) {
        notificacioService.actualitzarReferencies();
    }
}