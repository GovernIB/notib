package es.caib.notib.war.controller;

import es.caib.notib.core.api.dto.AdviserResponseDto;
import es.caib.notib.core.api.service.AdviserService;
import es.caib.notib.war.command.adviser.EnviamentAdviser;
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
    public AdviserResponseDto actualitzarReferencies(HttpServletRequest request, @RequestBody EnviamentAdviser adviser, Model model) {

        try {
//            ObjectMapper mapper = new ObjectMapper();
//            EnviamentAdviser adviser = mapper.readValue(env, EnviamentAdviser.class);

            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<EnviamentAdviser>> violations = validator.validate(adviser);

            if (!violations.isEmpty()) {
//                return getMessage(request, "adviser.sincronitzar.enviament.error.validacio");
                StringBuilder errorDescripcio = new StringBuilder(getMessage(request, "adviser.sincronitzar.enviament.error") + ": ");
                for (ConstraintViolation<EnviamentAdviser> violation: violations) {
                    errorDescripcio.append("[" + violation.getPropertyPath() + ": " + violation.getMessage() + "] ");
                }
                return AdviserResponseDto.builder()
                        .identificador(adviser.getHIdentificador())
                        .codigoRespuesta("998")
                        .descripcionRespuesta(errorDescripcio.toString())
                        .build();
            }

            AdviserResponseDto adviserResponse = adviserService.sincronitzarEnviament(adviser.asDto());
            return adviserResponse;
//            return getMessage(request, "adviser.sincronitzar.enviament.ok") + ": " + adviserResponse.toString();
        } catch (Exception ex) {
            log.error("Error al sincronitzar l'enviament", ex);
//            return getMessage(request, "adviser.sincronitzar.enviament.error");
            return AdviserResponseDto.builder()
                    .identificador(adviser.getHIdentificador())
                    .codigoRespuesta("999")
                    .descripcionRespuesta(getMessage(request, "adviser.sincronitzar.enviament.error") + ": " + ex.getMessage())
                    .build();
        }
    }
}
