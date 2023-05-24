package es.caib.notib.back.controller;

import es.caib.notib.back.command.adviser.EnviamentAdviser;
import es.caib.notib.logic.intf.dto.AdviserResponseDto;
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
            var factory = Validation.buildDefaultValidatorFactory();
            var validator = factory.getValidator();
            var violations = validator.validate(adviser);

            if (!violations.isEmpty()) {
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

            return adviserService.sincronitzarEnviament(adviser.asDto());
        } catch (Exception ex) {
            log.error("Error al sincronitzar l'enviament", ex);
            return AdviserResponseDto.builder()
                    .identificador(adviser.getHIdentificador())
                    .codigoRespuesta("999")
                    .descripcionRespuesta(getMessage(request, "adviser.sincronitzar.enviament.error") + ": " + ex.getMessage())
                    .build();
        }
    }
}
