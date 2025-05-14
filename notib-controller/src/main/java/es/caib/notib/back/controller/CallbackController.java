package es.caib.notib.back.controller;

import es.caib.notib.back.command.CallbackFiltreCommand;
import es.caib.notib.back.helper.DatatablesHelper;
import es.caib.notib.back.helper.RequestSessionHelper;
import es.caib.notib.logic.intf.dto.callback.CallbackFiltre;
import es.caib.notib.logic.intf.service.CallbackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    public String post(HttpServletRequest request, CallbackFiltreCommand command, Model model) {

        var entitatActual = getEntitatActualComprovantPermisos(request);
        RequestSessionHelper.actualitzarObjecteSessio(request, CALLBACK_FILTRE, command);
        model.addAttribute("callbackFiltreCommand", command);
        return "callback";
    }

    @PostMapping(params = "/netejar")
    public String postNeteja(HttpServletRequest request, Model model) {
        return post(request, new CallbackFiltreCommand(), model);
    }

    @GetMapping(value = "/datatable")
    @ResponseBody
    public DatatablesHelper.DatatablesResponse datatable(HttpServletRequest request) {

        var entitat = getEntitatActualComprovantPermisos(request);
        var filtre = (CallbackFiltre) RequestSessionHelper.obtenirObjecteSessio(request, CALLBACK_FILTRE);
        var callbacks = callbackService.findPendentsByeEntitat(entitat, filtre, DatatablesHelper.getPaginacioDtoFromRequest(request));
        return DatatablesHelper.getDatatableResponse(request, callbacks, "id", SESSION_ATTRIBUTE_SELECCIO);
    }

    @GetMapping(value = "/{callbackId}/enviar")
    public String enviar(HttpServletRequest request, Model model, @PathVariable Long callbackId) {

        try {
            var resposta = callbackService.enviarCallback(callbackId);
            return resposta.isOk() ? getAjaxControllerReturnValueSuccess(request, "redirect:/callback", "callback.controller.enviat.ok")
                    : getAjaxControllerReturnValueError(request, "redirect:/callback", "callback.controller.enviat.error", new Object[] {resposta.getErrorMsg()});
        } catch (Exception ex) {
            return getAjaxControllerReturnValueError(request, "redirect:/callback", "callback.controller.enviat.error");
        }
    }

    @GetMapping(value = "/{callbackId}/pausar")
    public String pausar(HttpServletRequest request, Model model, @PathVariable Long callbackId) {

        try {

            return getAjaxControllerReturnValueSuccess(request, "redirect:/callback", "callback.controller.pausat.ok");
        } catch (Exception ex) {
            return getAjaxControllerReturnValueSuccess(request, "redirect:/callback", "callback.controller.pausat.error");
        }
    }

}
