package es.caib.notib.back.controller;

import com.google.common.base.Strings;
import es.caib.notib.back.command.CallbackFiltreCommand;
import es.caib.notib.back.command.ColumnesRemesesCommand;
import es.caib.notib.back.command.NotificacioFiltreCommand;
import es.caib.notib.back.helper.DatatablesHelper;
import es.caib.notib.back.helper.MissatgesHelper;
import es.caib.notib.back.helper.RequestSessionHelper;
import es.caib.notib.back.helper.RolHelper;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.RolEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioTableItemDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.service.CallbackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/callback")
public class CallbackController extends TableAccionsMassivesController {

    @Autowired
    private CallbackService callbackService;

    private static final  String CALLBACK_FILTRE = "callback_filtre";
    private static final String SESSION_ATTRIBUTE_SELECCIO = "CallbackController.session.seleccio";


    public CallbackController(CallbackService callbackService) {
        super.sessionAttributeSeleccio = SESSION_ATTRIBUTE_SELECCIO;
    }

    protected List<Long> getIdsElementsFiltrats(HttpServletRequest request) throws ParseException {

        return new ArrayList<>();
    }


    @GetMapping
    public String get(HttpServletRequest request, Model model) {

        model.addAttribute(new CallbackFiltreCommand());
        return "callback";
    }

    @PostMapping
    public String post(HttpServletRequest request, NotificacioFiltreCommand command, Model model) {

        var entitatActual = getEntitatActualComprovantPermisos(request);
        RequestSessionHelper.actualitzarObjecteSessio(request, CALLBACK_FILTRE, command);
        model.addAttribute("callbackFiltreCommand", command);
        return "notificacioList";
    }

    @GetMapping(value = "/datatable")
    @ResponseBody
    public DatatablesHelper.DatatablesResponse datatable(HttpServletRequest request) {

        var entitat = getEntitatActualComprovantPermisos(request);
        var callbacks = callbackService.findPendentsByeEntitat(entitat, DatatablesHelper.getPaginacioDtoFromRequest(request));
        return DatatablesHelper.getDatatableResponse(request, callbacks, "id", SESSION_ATTRIBUTE_SELECCIO);
    }

}
