package es.caib.notib.api.interna.controller;

import es.caib.comanda.ms.estadistica.model.DimensioDesc;
import es.caib.comanda.ms.estadistica.model.EstadistiquesInfo;
import es.caib.comanda.ms.estadistica.model.IndicadorDesc;
import es.caib.comanda.ms.estadistica.model.RegistresEstadistics;
import es.caib.notib.logic.intf.service.EstadisticaService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class EstadistiquesController {

    private final EstadisticaService estadisticaService;


    @GetMapping("/estadistiquesInfo")
    public EstadistiquesInfo statsInfo() throws IOException {

        List<DimensioDesc> dimensions = estadisticaService.getDimensions();
        List<IndicadorDesc> indicadors = estadisticaService.getIndicadors();

        return EstadistiquesInfo.builder()
                .codi("NOT")
                .dimensions(dimensions)
                .indicadors(indicadors)
                .build();
    }

    @GetMapping("/estadistiques")
    public RegistresEstadistics estadistiques(HttpServletRequest request) throws IOException {

        return estadisticaService.consultaUltimesEstadistiques();
    }

    @GetMapping("/estadistiques/{dies}")
    public List<RegistresEstadistics> estadistiques(
            HttpServletRequest request,
            @PathVariable Integer dies) throws IOException {

        List<RegistresEstadistics> result = new ArrayList<>();
        LocalDate data = LocalDate.now().minusDays(1);
        for (int i = 0; i < dies; i++) {
            result.add(estadisticaService.consultaEstadistiques(data));
            data = data.minusDays(1);
        }
        return result;
    }

    @Hidden
    @RequestMapping(value = "/generarDadesExplotacio", method = RequestMethod.GET)
    @ResponseBody
    public String generarDadesExplotacio(HttpServletRequest request) throws Exception {
        estadisticaService.generarDadesExplotacio();
        return "Done";
    }

    @Hidden
    @RequestMapping(value = "/generarDadesExplotacio/{dies}", method = RequestMethod.GET)
    @ResponseBody
    public String generarDadesExplotacio(
            HttpServletRequest request,
            @PathVariable Integer dies) throws Exception {

        LocalDate data = LocalDate.now().minusDays(1);
        for (int i = 0; i < dies; i++) {
            estadisticaService.generarDadesExplotacio(data);
            data = data.minusDays(1);
        }
        return "Done";
    }
}
