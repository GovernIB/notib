package es.caib.notib.war.controller;

import es.caib.notib.core.api.service.SchedulledService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/api")
public class ApiController extends BaseUserController {

    @Autowired
    private SchedulledService schedulledService;

//	@RequestMapping(value = "/rest")
//	public String altaForm(
//			HttpServletRequest request) {
//		return "redirect:apidoc";
//	}

    @RequestMapping(value = "/scheduling/restart", method = RequestMethod.GET)
    @ResponseBody
    public String schedulingRestart(HttpServletRequest request) {

        schedulledService.restartSchedulledTasks();
        return "OK";
    }

}