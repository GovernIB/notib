package es.caib.notib.back.controller;

import com.google.common.base.Strings;
import es.caib.notib.logic.intf.dto.NotificacioEnviamentDtoV2;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioDtoV2;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.exception.NotFoundException;
import es.caib.notib.logic.intf.exception.RegistreNotificaException;
import es.caib.notib.logic.intf.service.EnviamentService;
import es.caib.notib.logic.intf.service.NotificacioService;
import es.caib.notib.back.helper.MissatgesHelper;
import es.caib.notib.back.helper.RequestSessionHelper;
import es.caib.notib.back.helper.RolHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public abstract class TableAccionsMassivesController extends BaseUserController {

    protected String sessionAttributeSeleccio;

    @Autowired
    private NotificacioService notificacioService;
    @Autowired
    private EnviamentService enviamentService;

    @RequestMapping(value = {"/select", "{notificacioId}/notificacio/select"}, method = RequestMethod.GET)
    @ResponseBody
    public int select(HttpServletRequest request, @RequestParam(value="ids[]", required = false) Long[] ids) {

        @SuppressWarnings("unchecked")
        var seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(request, sessionAttributeSeleccio);
        if (seleccio == null) {
            seleccio = new HashSet<>();
            RequestSessionHelper.actualitzarObjecteSessio(request, sessionAttributeSeleccio, seleccio);
        }
        if (ids != null) {
            seleccio.addAll(Arrays.asList(ids));
            return seleccio.size();
        }
        try {
            seleccio.addAll(getIdsElementsFiltrats(request));
        } catch (NotFoundException | ParseException e) {
            e.printStackTrace();
        }
        return seleccio.size();
    }

    @RequestMapping(value = {"/deselect", "{notificacioId}/notificacio/deselect"}, method = RequestMethod.GET)
    @ResponseBody
    public int deselect(HttpServletRequest request, @RequestParam(value="ids[]", required = false) Long[] ids) {

        @SuppressWarnings("unchecked")
        var seleccio = (Set<Long>) RequestSessionHelper.obtenirObjecteSessio(request, sessionAttributeSeleccio);
        if (seleccio == null) {
            seleccio = new HashSet<>();
            RequestSessionHelper.actualitzarObjecteSessio(request, sessionAttributeSeleccio, seleccio);
        }
        if (ids != null) {
            for (var id: ids) {
                seleccio.remove(id);
            }
        } else {
            seleccio.clear();
        }
        return seleccio.size();
    }
    // TODO SEGUIR AQUÍ 
    @RequestMapping(value = {"/export/{format}", "{notificacioId}/notificacio/export/{format}"}, method = RequestMethod.GET)
    public String export(HttpServletRequest request, HttpServletResponse response, @PathVariable String format) throws IOException {

        var seleccio = getIdsEnviamentsSeleccionats(request);
        if (seleccio == null || seleccio.isEmpty()) {
            MissatgesHelper.error(request, getMessage(request, "enviament.controller.exportacio.seleccio.buida"));
            return "redirect:../.." + (requestIsRemesesEnviamentMassiu(request) ? "/remeses" : "");
        }
        var entitatActual = getEntitatActualComprovantPermisos(request);
        try {
            var fitxer = enviamentService.exportacio(entitatActual.getId(), seleccio, format);
            writeFileToResponse(fitxer.getNom(), fitxer.getContingut(), response);
        } catch (NotFoundException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping(value = {"/reintentar/notificacio", "{notificacioId}/notificacio/reintentar/notificacio"}, method = RequestMethod.GET)
    @ResponseBody
    public String reintentarNotificacio(HttpServletRequest request, HttpServletResponse response) throws IOException, RegistreNotificaException {

        var seleccio = getIdsEnviamentsSeleccionats(request);
        var resposta = "";
        if (seleccio == null || seleccio.isEmpty()) {
            MissatgesHelper.error(request, getMessage(request, "enviament.controller.notificacio.seleccio.buida"));
            resposta = "error";
            return resposta;
        }
        MissatgesHelper.info( request, getMessage(request, "enviament.controller.reintent.notificacio.pendents.executant"));
        Set<Long> notificacioIds = new HashSet<>();
        NotificacioEnviamentDtoV2 env;
        for(var id: seleccio) {
            env = enviamentService.getOne(id);
            notificacioIds.add(env.getNotificacioId());
        }
        Integer notificacionsNoRegistrades = 0;
        Integer notificacionsError = 0;
        NotificacioDtoV2 notificacio;
        for(var notificacioId: notificacioIds) {
            notificacio = notificacioService.findAmbId(notificacioId, isAdministrador(request));
            if(notificacio.getEstat().equals(NotificacioEstatEnumDto.PENDENT)) {
                try {
                    notificacioService.registrarNotificar(notificacioId);
                } catch (Exception e) {
                    notificacionsError++;
                    mostraErrorReintentarNotificacio(request, notificacioId, notificacio, e);
                }
                continue;
            }
            if (notificacio.getEstat().equals(NotificacioEstatEnumDto.REGISTRADA)) {
                try {
                    notificacioService.notificacioEnviar(notificacioId);
                } catch (Exception e) {
                    notificacionsError++;
                    mostraErrorReintentarNotificacio(request, notificacioId, notificacio, e);
                }
                continue;
            }
            notificacionsNoRegistrades++;
        }

        String key;
        if(notificacionsNoRegistrades.equals((Integer)notificacioIds.size())) {
            key = "enviament.controller.reintent." + (notificacionsNoRegistrades == 1 ? "notificacio" : "notificacions" )+ ".pendents.KO";
            MissatgesHelper.error(request, getMessage(request, key));
        } else if (notificacionsError.equals((Integer)notificacioIds.size())) {
            key = "enviament.controller.reintent." + (notificacionsError == 1 ? "notificacio" : "notificacions" )+ ".pendents.error";
            MissatgesHelper.error(request, getMessage(request, key));
        } else if (notificacionsError > 0) {
            key =  notificacionsError + " " + getMessage(request, "enviament.controller.reintent.notificacions.pendents.error.alguna");
            MissatgesHelper.warning(request, key);
        } else {
            key =  "enviament.controller.reintent." + (notificacioIds.size() == 1 ? "notificacio" : "notificacions") + ".pendents.OK";
            MissatgesHelper.info(request, getMessage(request, key));
        }
        resposta = "ok";
        return resposta;
    }

    @RequestMapping(value = "/reactivar/notificacionsError", method = RequestMethod.GET)
    @ResponseBody
    public String reactivarErrors(HttpServletRequest request) throws IOException {

        var seleccio = getIdsEnviamentsSeleccionats(request);
        if (seleccio == null || seleccio.isEmpty()) {
            MissatgesHelper.error(request, getMessage(request, "enviament.controller.notificacio.seleccio.buida"));
            return "error";
        }
        Set<Long> notificacioIds = new HashSet<Long>();
        for(Long id: seleccio) {
            NotificacioEnviamentDtoV2 e = enviamentService.getOne(id);
            notificacioIds.add(e.getNotificacioId());
        }
        log.info("Reactivam enviaments de notificacions amb error: " + StringUtils.join(notificacioIds, ", "));
        Integer notificacionsNoFinalitzadesAmbError = 0;
        Integer notificacionsError = 0;
        for(var notificacioId: notificacioIds) {
            NotificacioDtoV2 notificacio = notificacioService.findAmbId(notificacioId, isAdministrador(request));
            if(notificacio.getEstat().equals(NotificacioEstatEnumDto.FINALITZADA_AMB_ERRORS) || notificacio.isJustificantCreat()) {
                try {
                    notificacioService.reactivarNotificacioAmbErrors(notificacioId);
                } catch (Exception e) {
                    notificacionsError++;
                    mostraErrorReintentarNotificacio(request, notificacioId, notificacio, e);
                }
                continue;
            }
            notificacionsNoFinalitzadesAmbError++;
        }

        String key;
        if(notificacionsNoFinalitzadesAmbError.equals((Integer)notificacioIds.size())) {
            key = "enviament.controller.reintent." + (notificacionsNoFinalitzadesAmbError == 1 ? "notificacio" : "notificacions" )+ ".errors.KO";
            MissatgesHelper.error(request, getMessage(request, key));
        } else if(notificacionsError.equals((Integer)notificacioIds.size())) {
            key = "enviament.controller.reintent." + (notificacionsError == 1 ? "notificacio" : "notificacions" )+ ".errors.error";
            MissatgesHelper.error(request, getMessage(request, key));
        } else if (notificacionsError > 0) {
            key = notificacionsError + " " + getMessage(request, "enviament.controller.reintent.notificacions.errors.error.alguna");
            MissatgesHelper.warning(request, key);
        } else {
            key = "enviament.controller.reintent." + (notificacioIds.size() == 1 ? "notificacio" : "notificacions") + ".errors.OK";
            MissatgesHelper.info(request, getMessage(request, key));
        }
        return "ok";
    }

    @RequestMapping(value = "/reactivar/consulta", method = RequestMethod.GET)
    public String reactivarConsulta(HttpServletRequest request, HttpServletResponse response) throws IOException {

        var seleccio = getIdsEnviamentsSeleccionats(request);
        if (seleccio == null || seleccio.isEmpty()) {
            MissatgesHelper.error(request, getMessage(request, "enviament.controller.reactivar.seleccio.buida"));
            return "redirect:" + request.getHeader("Referer");
        }
        log.info("Reactivam consulta dels enviaments: " + StringUtils.join(seleccio, ", "));
        try {
            enviamentService.reactivaConsultes(seleccio);
            MissatgesHelper.info(request, getMessage(request, "enviament.controller.reactivar.consultes.OK"));
        } catch (Exception e) {
            MissatgesHelper.error(request, getMessage(request, "enviament.controller.reactivar.consultes.KO"));
        }
        return "redirect:" + request.getHeader("Referer");
    }


    @RequestMapping(value = "/reactivar/sir", method = RequestMethod.GET)
    public String reactivarSir(HttpServletRequest request, HttpServletResponse response) throws IOException {

        var seleccio = getIdsEnviamentsSeleccionats(request);
        if (seleccio == null || seleccio.isEmpty()) {
            MissatgesHelper.error(request, getMessage(request, "enviament.controller.reactivar.seleccio.buida"));
            return "redirect:" + request.getHeader("Referer");
        }

        log.info("Reactivam SIR dels enviaments: " + StringUtils.join(seleccio, ", "));
        try {
            enviamentService.reactivaSir(seleccio);
            MissatgesHelper.info(request, getMessage(request, "enviament.controller.reactivar.sir.OK"));
        } catch (Exception e) {
            MissatgesHelper.error(request, getMessage(request, "enviament.controller.reactivar.sir.KO"));
        }

        return "redirect:" + request.getHeader("Referer");
    }

    @RequestMapping(value = {"/actualitzarestat", "{notificacioId}/notificacio/actualitzarestat"}, method = RequestMethod.GET)
    @ResponseBody
    public String actualitzarEstat(HttpServletRequest request, HttpServletResponse response) throws IOException {

        var seleccio = getIdsEnviamentsSeleccionats(request);
        var resposta = "";
        if (seleccio == null || seleccio.isEmpty()) {
            MissatgesHelper.error(request, getMessage(request, "enviament.controller.actualitzarestat.buida"));
            resposta = "error";
            return resposta;
        }

        MissatgesHelper.info( request, getMessage(request, "enviament.controller.actualitzarestat.executant"));
        log.info("Acualitzam estat dels enviaments: " + StringUtils.join(seleccio, ", "));
        var hasErrors = false;
        for(var enviamentId : seleccio) {
            try {
                enviamentService.actualitzarEstat(enviamentId);
            } catch (Exception e) {
                hasErrors = true;
                MissatgesHelper.error(request,getMessage(request, "enviament.controller.actualitzarestat.KO") + " [" + enviamentId + "]");
            }
        }
        RequestSessionHelper.actualitzarObjecteSessio(request, sessionAttributeSeleccio, new HashSet<Long>());
        if (!hasErrors) {
            MissatgesHelper.info(request, getMessage(request,"enviament.controller.actualitzarestat.OK"));
            resposta = "ok";
        }
        return resposta;
    }

    @RequestMapping(value = {"/enviar/callback", "{notifiacioId}/enviar/callback"}, method = RequestMethod.GET)
    public String enviarCallbacks(HttpServletRequest request, HttpServletResponse response) throws IOException {


        var path = request.getServletPath().split("/");
        var seleccio = getIdsEnviamentsSeleccionats(request);
        if ((seleccio == null || seleccio.isEmpty()) && (path.length == 0 || Strings.isNullOrEmpty(path[2]))) {
            MissatgesHelper.error(request, getMessage(request,"enviament.controller.enviar.callback.buida"));
            return "redirect:" + request.getHeader("Referer");
        }
        var notificacioId = seleccio.isEmpty() ? Long.parseLong(path[2]) : null;
        seleccio = notificacioId != null ? new HashSet<>(Arrays.asList(notificacioId)) : seleccio;
        log.info("Reactivam callback dels enviaments: " + StringUtils.join(seleccio, ", "));
        var hasErrors = false;
        for(var enviamentId : seleccio) {
            try {
                enviamentService.enviarCallback(enviamentId);
            } catch (Exception e) {
                hasErrors = true;
                MissatgesHelper.error(request, getMessage(request, "enviament.controller.enviar.callback.KO"));
            }
        }
        if (!hasErrors) {
            MissatgesHelper.info(request, getMessage(request,"enviament.controller.enviar.callback.OK"));
        }
        return "redirect:" + request.getHeader("Referer");
    }

    @RequestMapping(value = "/reactivar/callback", method = RequestMethod.GET)
    public String reactivarCallbacks(HttpServletRequest request, HttpServletResponse response) throws IOException {

        var seleccio = getIdsEnviamentsSeleccionats(request);
        if (seleccio == null || seleccio.isEmpty()) {
            MissatgesHelper.error(request, getMessage(request,"enviament.controller.reactivar.callback.buida"));
            return "redirect:" + request.getHeader("Referer");
        }
        log.info("Reactivam callback dels enviaments: " + StringUtils.join(seleccio, ", "));
        var hasErrors = false;
        for(var enviamentId : seleccio) {
            try {
                enviamentService.activarCallback(enviamentId);
            } catch (Exception e) {
                hasErrors = true;
                MissatgesHelper.error(request, getMessage(request, "enviament.controller.reactivar.callback.KO"));
            }
        }
        if (!hasErrors) {
            MissatgesHelper.info(request, getMessage(request,"enviament.controller.reactivar.callback.OK"));
        }
        return "redirect:" + request.getHeader("Referer");
    }

    private boolean isAdministrador(HttpServletRequest request) {
        return RolHelper.isUsuariActualAdministrador(request);
    }

    private void mostraErrorReintentarNotificacio(HttpServletRequest request, Long notificacioId, NotificacioDtoV2 notificacio, Exception e) {

        var errorMessage = "";
        if (e.getMessage() != null && !e.getMessage().isEmpty()) {
            errorMessage = e.getMessage();
        } else if (e.getCause() != null && e.getCause().getMessage() != null && !e.getCause().getMessage().isEmpty()) {
            errorMessage = e.getCause().getMessage();
        }
        if (e.getStackTrace() != null && e.getStackTrace().length > 2) {
            errorMessage += "<br/>";
            errorMessage += e.getStackTrace()[0] + "<br/>";
            errorMessage += e.getStackTrace()[1] + "<br/>";
            errorMessage += e.getStackTrace()[2] + "<br/>...";
        }
        MissatgesHelper.error(request, getMessage(request,"enviament.controller.reintent.notificacio.pendents.error",
                        new String[]{notificacioId.toString(), notificacio.getCreatedDateAmbFormat(), notificacio.getConcepte(), errorMessage}));
    }

    protected boolean requestIsRemesesEnviamentMassiu(HttpServletRequest request) {
        return request.getRequestURI().contains("/notibback/notificacio/");
    }

    protected Set<Long> getIdsEnviamentsSeleccionats(HttpServletRequest request) {

        var ids = getIdsSeleccionats(request);
        return requestIsRemesesEnviamentMassiu(request) ? enviamentService.findIdsByNotificacioIds(ids) : ids;
    }

    protected Set<Long> getIdsSeleccionats(HttpServletRequest request) {

        @SuppressWarnings("unchecked")
        var seleccio = (Set<Long>) RequestSessionHelper.obtenirObjecteSessio(request, sessionAttributeSeleccio);
        return seleccio != null ? new HashSet<>(seleccio) : new HashSet<Long>();
    }

    /**
     * Retorna els ids dels elements que es mostren actualment a la taula.
     * @param request
     * @return
     * @throws ParseException
     */
    protected abstract List<Long> getIdsElementsFiltrats(HttpServletRequest request) throws ParseException;
}
