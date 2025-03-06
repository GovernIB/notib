package es.caib.notib.api.interna.controller;

import es.caib.notib.api.interna.openapi.interficies.SirAdviserApiRestIntf;
import es.caib.notib.client.domini.Registre;
import es.caib.notib.logic.intf.dto.adviser.sir.RespostaSirAdviser;
import es.caib.notib.logic.intf.dto.adviser.sir.SirAdviser;
import es.caib.notib.logic.intf.service.RegistreService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Hidden
@Slf4j
@RestController
@RequestMapping("/sir/adviser")
public class SirAdviserController implements SirAdviserApiRestIntf {

    @Autowired
    private RegistreService registreService;

    @PostMapping(value = "/sincronitzar", headers="Content-Type=application/json")
    public RespostaSirAdviser sincronitzarEnviamentSir(HttpServletRequest request, @RequestBody SirAdviser adviser, Model model) {

        try {
//            var factory = Validation.buildDefaultValidatorFactory();
//            var validator = factory.getValidator();
//            var violations = validator.validate(adviser);
//
//            if (!violations.isEmpty()) {
//                var errorDescripcio = new StringBuilder("Error validant enviament SIR: ");
//                for (ConstraintViolation<SirAdviser> violation: violations) {
//                    errorDescripcio.append("[" + violation.getPropertyPath() + ": " + violation.getMessage() + "] ");
//                }
//                return RespostaSirAdviser.builder().ok(false).errorDescripcio(errorDescripcio.toString()).build();
//            }
            return registreService.sincronitzarEnviamentSir(adviser);
        } catch (Exception ex) {
            log.error("[SIR ADVISER CONTROLLER] Error al sincronitzar enviament SIR", ex);
            var desc = "[SIR ADVISER CONTROLLER] Error sincronitzant enviament SIR: " + ex.getMessage();
            return RespostaSirAdviser.builder().ok(false).errorDescripcio(desc).build();
        }
    }

}
