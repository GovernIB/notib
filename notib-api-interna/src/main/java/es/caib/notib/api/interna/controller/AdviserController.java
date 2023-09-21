package es.caib.notib.api.interna.controller;

import es.caib.notib.api.interna.model.adviser.EnviamentAdviser;
import es.caib.notib.logic.intf.dto.AdviserResponseDto;
import es.caib.notib.logic.intf.service.AdviserService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;

@Hidden
@Slf4j
@RestController
@RequestMapping("/adviser")
public class AdviserController {

    @Autowired
    private AdviserService adviserService;

    @PostMapping(value = "/sincronitzar", headers="Content-Type=application/json")
    public AdviserResponseDto actualitzarReferencies(HttpServletRequest request, @RequestBody EnviamentAdviser adviser, Model model) {

        try {
            var factory = Validation.buildDefaultValidatorFactory();
            var validator = factory.getValidator();
            var violations = validator.validate(adviser);

            if (!violations.isEmpty()) {
                var errorDescripcio = new StringBuilder("Error validant enviament: ");
                for (ConstraintViolation<EnviamentAdviser> violation: violations) {
                    errorDescripcio.append("[" + violation.getPropertyPath() + ": " + violation.getMessage() + "] ");
                }
                return AdviserResponseDto.builder().identificador(adviser.getHIdentificador()).codigoRespuesta("998").descripcionRespuesta(errorDescripcio.toString()).build();
            }
            var resposta = adviserService.sincronizarEnvio(adviser.asSincronizarEnvio());
            return AdviserResponseDto.builder().identificador(resposta.getIdentificador()).codigoRespuesta(resposta.getCodigoRespuesta()).descripcionRespuesta(resposta.getDescripcionRespuesta()).build();
        } catch (Exception ex) {
            log.error("Error al sincronitzar l'enviament", ex);
            var desc = "Error sincronitzant enviament: " + ex.getMessage();
            return AdviserResponseDto.builder().identificador(adviser.getHIdentificador()).codigoRespuesta("999").descripcionRespuesta(desc).build();
        }
    }
}
