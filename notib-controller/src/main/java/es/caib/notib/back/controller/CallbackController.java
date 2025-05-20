package es.caib.notib.back.controller;

import es.caib.notib.back.command.CallbackFiltreCommand;
import es.caib.notib.back.helper.DatatablesHelper;
import es.caib.notib.back.helper.EnumHelper;
import es.caib.notib.back.helper.RequestSessionHelper;
import es.caib.notib.back.helper.RolHelper;
import es.caib.notib.logic.intf.dto.SiNo;
import es.caib.notib.logic.intf.dto.organisme.NumeroPermisos;
import es.caib.notib.logic.intf.service.CallbackService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/callback")
public class CallbackController extends TableAccionsMassivesController {

    @Autowired
    private CallbackService callbackService;

    private static final  String CALLBACK_FILTRE = "callback_filtre";
    private static final String SESSION_ATTRIBUTE_SELECCIO = "CallbackController.session.seleccio";
    private static final String CALLBACK_ID = "CallbackController.session.callback.id";

    public CallbackController() {
        super.sessionAttributeSeleccio = SESSION_ATTRIBUTE_SELECCIO;
    }

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


    @GetMapping
    public String get(HttpServletRequest request, Model model) {


        Boolean mantenirPaginacio = Boolean.parseBoolean(request.getParameter("mantenirPaginacio"));
        model.addAttribute("mantenirPaginacio", mantenirPaginacio != null && mantenirPaginacio);
        model.addAttribute("seleccio", RequestSessionHelper.obtenirObjecteSessio(request, SESSION_ATTRIBUTE_SELECCIO));
        model.addAttribute("fiReintetsList", EnumHelper.getOptionsForEnum(SiNo.class, "es.caib.notib.logic.intf.dto.SiNo."));
        model.addAttribute(new CallbackFiltreCommand());
        return "callback";
    }

    @PostMapping
    public String post(HttpServletRequest request, CallbackFiltreCommand command, Model model) {

        var entitatActual = getEntitatActualComprovantPermisos(request);

        var callbackId = (Long)RequestSessionHelper.obtenirObjecteSessio(request, CALLBACK_ID);
        if (callbackId == null || !callbackId.equals(command.getId())) {
            RequestSessionHelper.esborrarObjecteSessio(request, SESSION_ATTRIBUTE_SELECCIO);
            RequestSessionHelper.actualitzarObjecteSessio(request, CALLBACK_ID, command.getId());
        }
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
        var filtre = getFiltreCommand(request);
        var filtreDto = filtre.asDto();
        filtreDto.setEntitatId(entitat.getId());
        var callbacks = callbackService.findPendentsByEntitat(filtreDto, DatatablesHelper.getPaginacioDtoFromRequest(request));
        return DatatablesHelper.getDatatableResponse(request, callbacks, "id", SESSION_ATTRIBUTE_SELECCIO);
    }

    @GetMapping(value = {"/{callbackId}/enviar"})
    public String enviar(HttpServletRequest request, Model model, @PathVariable Long callbackId) {

        try {
            var resposta = callbackService.enviarCallback(callbackId);
            return resposta.isOk() ? getAjaxControllerReturnValueSuccess(request, "redirect:/callback", "callback.controller.enviat.ok")
                    : getAjaxControllerReturnValueError(request, "redirect:/callback", "callback.controller.enviat.error", new Object[] {resposta.getErrorMsg()});
        } catch (Exception ex) {
            return getAjaxControllerReturnValueError(request, "redirect:/callback", "callback.controller.enviat.error");
        }
    }

    @GetMapping(value = {"/enviar"})
    public String enviarMassiu(HttpServletRequest request, Model model) {

        try {
            var seleccio = getIdsSeleccionats(request);
            if (seleccio == null || seleccio.isEmpty()) {
                var msg = getMessage(request,"callback.controller.massiva.buida");
                getAjaxControllerReturnValueError(request, "redirect:/callback", "callback.controller.enviat.error", new Object[] {msg});
            }
            log.info("Enviant callbacks amb id: " + StringUtils.join(seleccio, ", "));
            var resposta = callbackService.enviarCallback(seleccio);
            return resposta.isOk() ? getAjaxControllerReturnValueSuccess(request, "redirect:/callback", "callback.controller.enviat.ok")
                    : getAjaxControllerReturnValueError(request, "redirect:/callback", "callback.controller.enviat.error", new Object[] {resposta.getErrorMsg()});
        } catch (Exception ex) {
            return getAjaxControllerReturnValueError(request, "redirect:/callback", "callback.controller.enviat.error");
        }
    }

    @GetMapping(value = "/{callbackId}/pausar")
    public String pausar(HttpServletRequest request, Model model, @PathVariable Long callbackId) {

        try {
            var anulat = callbackService.pausarCallback(callbackId, true);
            return anulat ? getAjaxControllerReturnValueSuccess(request, "redirect:/callback", "callback.controller.pausat.ok")
                : getAjaxControllerReturnValueSuccess(request, "redirect:/callback", "callback.controller.pausat.error");
        } catch (Exception ex) {
            return getAjaxControllerReturnValueSuccess(request, "redirect:/callback", "callback.controller.pausat.error");
        }
    }

    @GetMapping(value = {"/pausar"})
    public String pausarMassiu(HttpServletRequest request, Model model) {

        try {
            var seleccio = getIdsSeleccionats(request);
            if (seleccio == null || seleccio.isEmpty()) {
                var msg = getMessage(request,"callback.controller.massiva.buida");
                getAjaxControllerReturnValueError(request, "redirect:/callback", "callback.controller.pausat.error", new Object[] {msg});
            }
            log.info("Pausant callbacks amb id: " + StringUtils.join(seleccio, ", "));
            var resposta = callbackService.pausarCallback(seleccio, true);
            return resposta.isOk() ? getAjaxControllerReturnValueSuccess(request, "redirect:/callback", "callback.controller.pausat.ok")
                    : getAjaxControllerReturnValueError(request, "redirect:/callback", "callback.controller.pausat.error", new Object[] {resposta.getErrorMsg()});
        } catch (Exception ex) {
            return getAjaxControllerReturnValueError(request, "redirect:/callback", "callback.controller.pausat.error");
        }
    }

    @GetMapping(value = "/{callbackId}/activar")
    public String activar(HttpServletRequest request, Model model, @PathVariable Long callbackId) {

        try {
            var anulat = callbackService.pausarCallback(callbackId, false);
            return anulat ? getAjaxControllerReturnValueSuccess(request, "redirect:/callback", "callback.controller.activar.ok")
                    : getAjaxControllerReturnValueSuccess(request, "redirect:/callback", "callback.controller.activar.error");
        } catch (Exception ex) {
            return getAjaxControllerReturnValueSuccess(request, "redirect:/callback", "callback.controller.activar.error");
        }
    }

    @GetMapping(value = {"/activar"})
    public String activarMassiu(HttpServletRequest request, Model model) {

        try {
            var seleccio = getIdsSeleccionats(request);
            if (seleccio == null || seleccio.isEmpty()) {
                var msg = getMessage(request,"callback.controller.massiva.buida");
                getAjaxControllerReturnValueError(request, "redirect:/callback", "callback.controller.activar.error", new Object[] {msg});
            }
            log.info("Activant callbacks amb id: " + StringUtils.join(seleccio, ", "));
            var resposta = callbackService.pausarCallback(seleccio, false);
            return resposta.isOk() ? getAjaxControllerReturnValueSuccess(request, "redirect:/callback", "callback.controller.activar.ok")
                    : getAjaxControllerReturnValueError(request, "redirect:/callback", "callback.controller.activar.error", new Object[] {resposta.getErrorMsg()});
        } catch (Exception ex) {
            return getAjaxControllerReturnValueError(request, "redirect:/callback", "callback.controller.activar.error");
        }
    }


    private CallbackFiltreCommand getFiltreCommand(HttpServletRequest request) {

        var filtreCommand = (CallbackFiltreCommand) RequestSessionHelper.obtenirObjecteSessio(request, CALLBACK_FILTRE);
        if (filtreCommand != null) {
//            setDefaultFiltreData(filtreCommand);
            return filtreCommand;
        }
        filtreCommand = new CallbackFiltreCommand();
//        setDefaultFiltreData(filtreCommand);
        RequestSessionHelper.actualitzarObjecteSessio(request, CALLBACK_FILTRE, filtreCommand);
        return filtreCommand;
    }

    private  void setDefaultFiltreData(CallbackFiltreCommand command) {

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

}
