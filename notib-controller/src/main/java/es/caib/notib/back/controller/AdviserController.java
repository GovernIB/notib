package es.caib.notib.back.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.caib.notib.back.command.adviser.EnviamentAdviser;
import es.caib.notib.logic.intf.service.AdviserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

@Slf4j
@Controller
@RequestMapping("/adviser")
public class AdviserController  extends BaseController {

    @Autowired
    private AdviserService adviserService;

    @ResponseBody
    @RequestMapping(value = "/sincronitzar", method = RequestMethod.POST, headers="Content-Type=application/json")
    public String actualitzarReferencies(HttpServletRequest request, @RequestBody String env, Model model) {


        try {

            ObjectMapper mapper = new ObjectMapper();
            EnviamentAdviser adviser = mapper.readValue(env, EnviamentAdviser.class);

            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<EnviamentAdviser>> violations = validator.validate(adviser);

            if (!violations.isEmpty()) {
                return getMessage(request, "adviser.sincronitzar.enviament.error.validacio");
            }

            adviserService.sincronitzarEnviament(adviser.asDto());
            return getMessage(request, "adviser.sincronitzar.enviament.ok");
        } catch (Exception ex) {
            log.error("Error al sincronitzar l'enviament", ex);
            return getMessage(request, "adviser.sincronitzar.enviament.error");
        }
    }
}
