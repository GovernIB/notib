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
import java.time.format.DateTimeFormatter;
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
        return EstadistiquesInfo.builder().codi("NOT").dimensions(dimensions).indicadors(indicadors).build();
    }

    @GetMapping("/estadistiques")
    public RegistresEstadistics estadistiques(HttpServletRequest request) throws IOException {

        return estadisticaService.consultaUltimesEstadistiques();
    }

    @GetMapping("/estadistiques/{dies}")
    public List<RegistresEstadistics> estadistiques(HttpServletRequest request, @PathVariable Integer dies) throws IOException {

        List<RegistresEstadistics> result = new ArrayList<>();
        LocalDate data = LocalDate.now().minusDays(1);
        for (int i = 0; i < dies; i++) {
            result.add(estadisticaService.consultaEstadistiques(data));
            data = data.minusDays(1);
        }
        return result;
    }

    @GetMapping("/estadistiques/of/{data}")
    public RegistresEstadistics estadistiques(HttpServletRequest request, @PathVariable String data) throws Exception {

        LocalDate date = LocalDate.parse(data, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        return estadisticaService.consultaEstadistiques(date);
    }

    @GetMapping("/estadistiques/from/{dataInici}/to/{dataFi}")
    public List<RegistresEstadistics> estadistiques(HttpServletRequest request, @PathVariable String dataInici, @PathVariable String dataFi) throws Exception {

        List<RegistresEstadistics> result = new ArrayList<>();
        LocalDate dataFrom = LocalDate.parse(dataInici, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        LocalDate dataTo = LocalDate.parse(dataFi, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        LocalDate startDate = dataFrom.isBefore(dataTo) ? dataFrom : dataTo;
        LocalDate endDate = dataFrom.isBefore(dataTo) ? dataTo : dataFrom;
        LocalDate ahir = LocalDate.now().minusDays(1);
        if (endDate.isAfter(ahir)) {
            endDate = ahir;
        }
        return estadisticaService.consultaEstadistiques(startDate, endDate);
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
    public String generarDadesExplotacio(HttpServletRequest request, @PathVariable Integer dies) throws Exception {

        LocalDate data = LocalDate.now().minusDays(1);
        for (int i = 0; i < dies; i++) {
            estadisticaService.generarDadesExplotacio(data);
            data = data.minusDays(1);
        }
        return "Done";
    }

    @Hidden
    @RequestMapping(value = "/generarDadesBasiquesExplotacio/from/{dataInici}/to/{dataFi}", method = RequestMethod.GET)
    @ResponseBody
    public String generarDadesBasiquesExplotacio(HttpServletRequest request, @PathVariable String dataInici, @PathVariable String dataFi) throws Exception {

        try {
            LocalDate dataFrom = LocalDate.parse(dataInici, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            LocalDate dataTo = LocalDate.parse(dataFi, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            if (dataFrom == null) return "Error: Data inici es null";
            if (dataTo == null) return "Error: Data fi es null";
            LocalDate startDate = dataFrom.isBefore(dataTo) ? dataFrom : dataTo;
            LocalDate endDate = dataFrom.isBefore(dataTo) ? dataTo : dataFrom;
            LocalDate ahir = LocalDate.now().minusDays(1);
            if (endDate.isAfter(ahir)) {
                endDate = ahir;
            }
            estadisticaService.generarDadesExplotacioBasiques(startDate, endDate);
            return "Done";
        } catch (Exception e) {
            String message = e.getMessage() + "<br/>";
            for (StackTraceElement element : e.getStackTrace()) {
                message += element.toString() + "<br/>";
            }
            return message;
        }
    }

}
