package es.caib.notib.api.interna.controller;

import es.caib.notib.logic.intf.dto.logs.FitxerContingut;
import es.caib.notib.logic.intf.dto.logs.FitxerInfo;
import es.caib.notib.logic.intf.service.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/logs")
public class LogController {

    @Autowired
    private LogService logService;

    @GetMapping("/llistar/fitxers")
    public List<FitxerInfo> llistarFitxers() {
        return logService.llistarFitxers();
    }

    @GetMapping("/fitxer/{nom}")
    public FitxerContingut getFitxerByNom(@PathVariable("nom") String nom) {
        return logService.getFitxerByNom(nom);
    }


}
