package es.caib.notib.back.controller;

import es.caib.notib.logic.intf.service.AplicacioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
@RequestMapping("/api")
public class ApiController extends BaseUserController {

    @Autowired
    private AplicacioService aplicacioService;


    @GetMapping(value = "/scheduling/restart")
    @ResponseBody
    public String schedulingRestart(HttpServletRequest request) {

        aplicacioService.restartSchedulledTasks();
        return "OK";
    }

    @GetMapping(value = "/sm/broker/restart")
    @ResponseBody
    public String restartSmBroker(HttpServletRequest request) {

        try {
            aplicacioService.restartSmBroker();
        } catch (Exception ex) {
            log.error("Error resetjant el broker de la state machine", ex);
        }
        return "OK";
    }

}