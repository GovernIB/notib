package es.caib.notib.back.controller;

import es.caib.notib.back.command.AccioMassivaFiltreCommand;
import es.caib.notib.back.command.CallbackFiltreCommand;
import es.caib.notib.back.helper.DatatablesHelper;
import es.caib.notib.back.helper.DatatablesHelper.DatatablesResponse;
import es.caib.notib.back.helper.EnumHelper;
import es.caib.notib.back.helper.RequestSessionHelper;
import es.caib.notib.back.helper.RolHelper;
import es.caib.notib.logic.intf.dto.SiNo;
import es.caib.notib.logic.intf.service.AccioMassivaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/accions/massives")
public class AccionsMassivesController extends TableAccionsMassivesController {

    private static final  String ACCIO_MASSIVA_FILTRE = "accio_massiva_filtre";
    private static final String SESSION_ATTRIBUTE_SELECCIO = "CallbackController.session.seleccio";
    private static final String ACCIO_MASSIVA_ID = "AccionsMassivesController.session.accio.massiva.id";

    @Autowired
    private AccioMassivaService accioMassivaService;

    @GetMapping
    public String get(HttpServletRequest request, Model model) {

        Boolean mantenirPaginacio = Boolean.parseBoolean(request.getParameter("mantenirPaginacio"));
        model.addAttribute("mantenirPaginacio", mantenirPaginacio != null && mantenirPaginacio);
        model.addAttribute("seleccio", RequestSessionHelper.obtenirObjecteSessio(request, SESSION_ATTRIBUTE_SELECCIO));
        model.addAttribute(getFiltreCommand(request));
        return "consultaAccionsMassives";
    }

    @GetMapping(value = "/datatable")
    @ResponseBody
    public DatatablesResponse datatable(HttpServletRequest request ) {

        var entitat = getEntitatActualComprovantPermisos(request);
        var filtre = getFiltreCommand(request);
        var filtreDto = filtre.asDto();
        filtreDto.setEntitatId(entitat.getId());
        var accions = accioMassivaService.findAmbFiltre(filtreDto, DatatablesHelper.getPaginacioDtoFromRequest(request));
        return DatatablesHelper.getDatatableResponse(request, accions, "id", SESSION_ATTRIBUTE_SELECCIO);
    }

    private AccioMassivaFiltreCommand getFiltreCommand(HttpServletRequest request) {

        var filtreCommand = (AccioMassivaFiltreCommand) RequestSessionHelper.obtenirObjecteSessio(request, ACCIO_MASSIVA_FILTRE);
        if (filtreCommand != null) {
            setDefaultFiltreData(filtreCommand);
            return filtreCommand;
        }
        filtreCommand = new AccioMassivaFiltreCommand();
        setDefaultFiltreData(filtreCommand);
        RequestSessionHelper.actualitzarObjecteSessio(request, ACCIO_MASSIVA_FILTRE, filtreCommand);
        return filtreCommand;
    }

    private  void setDefaultFiltreData(AccioMassivaFiltreCommand command) {

        var df = new SimpleDateFormat("dd/MM/yyyy");
        var avui = new Date();
        if (command.getDataFi() == null) {
            command.setDataFi(df.format(avui));
        }
        if (command.getDataInici() != null) {
            return;
        }
        var c = Calendar.getInstance();
        c.setTime(avui);
        c.add(Calendar.MONTH, -3);
        var inici = c.getTime();
        command.setDataInici(df.format(inici));
    }

    @Override
    protected List<Long> getIdsElementsFiltrats(HttpServletRequest request) throws ParseException {

        var entitatActual = getEntitatActualComprovantPermisos(request);
        if (entitatActual == null) {
            return  new ArrayList<>();
        }
        var filtreCommand = getFiltreCommand(request);
        String organGestorCodi = null;
        if (RolHelper.isUsuariActualUsuariAdministradorOrgan(sessionScopedContext.getRolActual())) {
            var organGestorActual = getOrganGestorActual(request);
            organGestorCodi = organGestorActual.getCodi();
        }
        return new ArrayList<>();
    }
}
