package es.caib.notib.war.controller;

import es.caib.notib.core.api.dto.EnviamentAdviser;
import es.caib.notib.core.api.service.AdviserService;
import es.caib.notib.core.wsdl.adviser.AdviserWsV2PortType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
@RequestMapping("/adviser")
public class AdviserController  extends BaseController {

    @Autowired
    private AdviserService adviserService;

    @RequestMapping(value = "/sincronitzar", method = RequestMethod.POST)
    public void actualitzarReferencies(HttpServletRequest request, EnviamentAdviser env, Model model) {

        try {
//            EnviamentAdviser obj = new Gson().fromJson(request.getReader(), EnviamentAdviser.class)
            adviserService.sincronitzarEnviament(env);
        } catch (Exception ex) {
            log.error("Error al sincronitzar l'enviament");
        }
    }
}
