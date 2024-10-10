package es.caib.notib.api.externa.controller;

import com.google.common.base.Strings;
import es.caib.notib.api.externa.adviser.EnviamentAdviser;
import es.caib.notib.logic.intf.dto.AdviserResponseDto;
import es.caib.notib.logic.intf.service.AdviserService;
import es.caib.notib.logic.intf.service.NexeaAdviserService;
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
@RequestMapping("/adviser/entrega/postal")
public class NexeaAdviserController {

    @Autowired
    private NexeaAdviserService nexeaAdviserService;

    @PostMapping(value = "/sincronitzar", headers="Content-Type=application/json")
    public AdviserResponseDto actualitzarReferencies(HttpServletRequest request, @RequestBody EnviamentAdviser adviser, Model model) {
        return null;
//        try {
//            var factory = Validation.buildDefaultValidatorFactory();
//            var validator = factory.getValidator();
//            var violations = validator.validate(adviser);
//
//            if (!violations.isEmpty()) {
//                var errorDescripcio = new StringBuilder("Error validant l'entrega postal: ");
//                for (ConstraintViolation<EnviamentAdviser> violation: violations) {
//                    errorDescripcio.append("[" + violation.getPropertyPath() + ": " + violation.getMessage() + "] ");
//                }
//                return AdviserResponseDto.builder().identificador(adviser.getHIdentificador()).codigoRespuesta("998").descripcionRespuesta(errorDescripcio.toString()).build();
//            }
//            var sincronizarEnvio = adviser.asSincronizarEnvio();
//            var resposta = nexeaAdviserService.sincronizarEnvio(sincronizarEnvio);
//            if (Strings.isNullOrEmpty(resposta.getCodigoRespuesta())) {
//                return AdviserResponseDto.builder().identificador(adviser.getHIdentificador()).codigoRespuesta(resposta.getCodigoRespuesta()).descripcionRespuesta(resposta.getDescripcionRespuesta()).build();
//            }
//            sincronizarEnvio.setIdentificador(resposta.getIdentificador());
//            var resultadoSincronizarEnvio = adviserService.sincronizarEnvio(sincronizarEnvio);
//            return AdviserResponseDto.builder().identificador(resposta.getIdentificador()).codigoRespuesta(resultadoSincronizarEnvio.getCodigoRespuesta()).descripcionRespuesta(resultadoSincronizarEnvio.getDescripcionRespuesta()).build();
//        } catch (Exception ex) {
//            log.error("Error al sincronitzar l'entrega postal", ex);
//            var desc = "Error sincronitzant l'entrega postal: " + ex.getMessage();
//            return AdviserResponseDto.builder().identificador(adviser.getHIdentificador()).codigoRespuesta("999").descripcionRespuesta(desc).build();
//        }
    }
}

