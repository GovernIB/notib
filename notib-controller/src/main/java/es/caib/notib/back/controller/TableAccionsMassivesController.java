package es.caib.notib.back.controller;

import com.google.common.base.Strings;
import es.caib.notib.back.helper.MissatgesHelper;
import es.caib.notib.back.helper.RequestSessionHelper;
import es.caib.notib.back.helper.RolHelper;
import es.caib.notib.logic.intf.dto.NotificacioEnviamentDtoV2;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioDtoV2;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.exception.NotFoundException;
import es.caib.notib.logic.intf.exception.RegistreNotificaException;
import es.caib.notib.logic.intf.service.EnviamentService;
import es.caib.notib.logic.intf.service.NotificacioService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public abstract class TableAccionsMassivesController extends BaseUserController {

    @Autowired
    private NotificacioService notificacioService;
    @Autowired
    private EnviamentService enviamentService;

    protected String sessionAttributeSeleccio;
    protected Long notMassivaId;
    private static final String REFERER = "Referer";
    private static final String ERROR = "error";
    private static final String REINTENT_TEXT = "enviament.controller.reintent.";
    private static final String NOTIFICACIO = "notificacio";
    private static final String NOTIFICACIONS = "notificacions";
    private static final String REDIRECT = "redirect:";


    @SuppressWarnings("unchecked")
    @GetMapping(value = {"/seleccionar/all", "{notificacioId}/notificacio/seleccionar/all"})
    @ResponseBody
    public int select(HttpServletRequest request,  @PathVariable Map<String, String> pathVarsMap) {

        var id = pathVarsMap.get("notificacioId");
        if (!Strings.isNullOrEmpty(id)) {
            try {
                notMassivaId = Long.valueOf(id);
            } catch (Exception ex) {
                log.error("Error seleccionant tots elements", ex);
            }
        }
        var seleccio = (Set<Long>) RequestSessionHelper.obtenirObjecteSessio(request, sessionAttributeSeleccio);
        if (seleccio == null) {
            seleccio = new HashSet<>();
            RequestSessionHelper.actualitzarObjecteSessio(request, sessionAttributeSeleccio, seleccio);
        }
        try {
            seleccio.addAll(getIdsElementsFiltrats(request));
        } catch (NotFoundException | ParseException ex) {
            log.error("Error seleccionant tots els elements", ex);
        }
        return seleccio.size();
    }

    @GetMapping(value = {"/select", "{notificacioId}/notificacio/select"})
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
        } catch (NotFoundException | ParseException ex) {
            log.error("Error seleccionant elements", ex);
        }
        return seleccio.size();
    }

    @GetMapping(value = {"/deselect", "{notificacioId}/notificacio/deselect"})
    @ResponseBody
    public int deselect(HttpServletRequest request, @RequestParam(value="ids[]", required = false) Long[] ids) {

        @SuppressWarnings("unchecked")
        var seleccio = (Set<Long>) RequestSessionHelper.obtenirObjecteSessio(request, sessionAttributeSeleccio);
        if (seleccio == null) {
            seleccio = new HashSet<>();
            RequestSessionHelper.actualitzarObjecteSessio(request, sessionAttributeSeleccio, seleccio);
        }
        if (ids == null) {
            seleccio.clear();
            return 0;
        }
        for (Long id: ids) {
            seleccio.remove(id);
        }
        return seleccio.size();
    }

    @GetMapping(value = {"/export/{format}", "{notificacioId}/notificacio/export/{format}"})
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
        } catch (NotFoundException | ParseException ex) {
            log.error("Error exportant els elements seleccionats", ex);
        }
        return null;
    }

    @GetMapping(value = {"/reintentar/notificacio", "{notificacioId}/notificacio/reintentar/notificacio"})
    @ResponseBody
    public String reintentarNotificacio(HttpServletRequest request, HttpServletResponse response) throws RegistreNotificaException {

        var seleccio = getIdsEnviamentsSeleccionats(request);
        if (seleccio == null || seleccio.isEmpty()) {
            MissatgesHelper.error(request, getMessage(request, "enviament.controller.notificacio.seleccio.buida"));
            return ERROR;
        }
        MissatgesHelper.info( request, getMessage(request, "enviament.controller.reintent.notificacio.pendents.executant"));
        Set<Long> notificacioIds = new HashSet<>();
        NotificacioEnviamentDtoV2 e;
        for(var id: seleccio) {
            e = enviamentService.getOne(id);
            notificacioIds.add(e.getNotificacioId());
        }
        var notificacionsNoRegistrades = 0;
        var notificacionsError = 0;
        NotificacioDtoV2 notificacio;
        for(var notificacioId: notificacioIds) {
            notificacio = notificacioService.findAmbId(notificacioId, isAdministrador(request));
            if(notificacio.getEstat().equals(NotificacioEstatEnumDto.PENDENT)) {
                try {
                    notificacioService.enviarNotificacioARegistre(notificacioId);
                } catch (Exception ex) {
                    notificacionsError++;
                    mostraErrorReintentarNotificacio(request, notificacioId, notificacio, ex);
                }
                continue;
            }
            if (notificacio.getEstat().equals(NotificacioEstatEnumDto.REGISTRADA)) {
                try {
                    notificacioService.enviarNotificacioANotifica(notificacioId);
                } catch (Exception ex) {
                    notificacionsError++;
                    mostraErrorReintentarNotificacio(request, notificacioId, notificacio, ex);
                }
                continue;
            }
            notificacionsNoRegistrades++;
        }
        String msg;
        if(notificacionsNoRegistrades == notificacioIds.size()) {
            msg = getMessage(request, REINTENT_TEXT + (notificacionsNoRegistrades == 1 ? NOTIFICACIO : NOTIFICACIONS)+ ".pendents.KO");
            MissatgesHelper.error(request, msg);
        } else if(notificacionsError == notificacioIds.size()) {
            msg = getMessage(request, REINTENT_TEXT + (notificacionsError == 1 ? NOTIFICACIO : NOTIFICACIONS)+ ".pendents.error");
            MissatgesHelper.error(request, msg);
        } else if (notificacionsError > 0) {
            msg = getMessage(request, "enviament.controller.reintent.notificacions.pendents.error.alguna");
            MissatgesHelper.warning(request, notificacionsError + " " + msg);
        } else {
            msg = getMessage(request, REINTENT_TEXT + (notificacioIds.size() == 1 ? NOTIFICACIO : NOTIFICACIONS) + ".pendents.OK");
            MissatgesHelper.info(request, msg);
        }
        return "ok";
    }

    @GetMapping(value = "/reactivar/notificacionsError")
    @ResponseBody
    public String reactivarErrors(HttpServletRequest request) throws IOException {

        var seleccio = getIdsEnviamentsSeleccionats(request);
        if (seleccio == null || seleccio.isEmpty()) {
            MissatgesHelper.error(request, getMessage(request, "enviament.controller.notificacio.seleccio.buida"));
            return ERROR;
        }
        NotificacioEnviamentDtoV2 e;
        Set<Long> notificacioIds = new HashSet<>();
        for(var id: seleccio) {
            e = enviamentService.getOne(id);
            notificacioIds.add(e.getNotificacioId());
        }
        log.info("Reactivam enviaments de notificacions amb error: " + StringUtils.join(notificacioIds, ", "));
        var notificacionsNoFinalitzadesAmbError = 0;
        var notificacionsError = 0;
        for(var notificacioId: notificacioIds) {
            var notificacio = notificacioService.findAmbId(notificacioId, isAdministrador(request));
            if(!notificacio.getEstat().equals(NotificacioEstatEnumDto.FINALITZADA_AMB_ERRORS) && !notificacio.isJustificantCreat()) {
                notificacionsNoFinalitzadesAmbError++;
                continue;
            }
            try {
                notificacioService.reactivarNotificacioAmbErrors(notificacioId);
            } catch (Exception ex) {
                notificacionsError++;
                mostraErrorReintentarNotificacio(request, notificacioId, notificacio, ex);
            }
        }
        String msg;
        if (notificacionsNoFinalitzadesAmbError == notificacioIds.size()) {
            msg = getMessage(request, REINTENT_TEXT + (notificacionsNoFinalitzadesAmbError == 1 ? NOTIFICACIO : NOTIFICACIONS)+ ".errors.KO");
            MissatgesHelper.error(request, msg);
        } else if (notificacionsError == notificacioIds.size()) {
            msg = getMessage(request, REINTENT_TEXT + (notificacionsError == 1 ? NOTIFICACIO : NOTIFICACIONS)+ ".errors.error");
            MissatgesHelper.error(request, msg);
        } else if (notificacionsError > 0) {
            msg = getMessage(request, "enviament.controller.reintent.notificacions.errors.error.alguna");
            MissatgesHelper.warning(request, notificacionsError + " " + msg);
        } else {
            msg = getMessage(request, REINTENT_TEXT + (notificacioIds.size() == 1 ? NOTIFICACIO : NOTIFICACIONS) + ".errors.OK");
            MissatgesHelper.info(request, msg);
        }
        return "ok";
    }

    @GetMapping(value = "/reactivar/consulta")
    public String reactivarConsulta(HttpServletRequest request, HttpServletResponse response) throws IOException {

        var seleccio = getIdsEnviamentsSeleccionats(request);
        if (seleccio == null || seleccio.isEmpty()) {
            MissatgesHelper.error(request, getMessage(request, "enviament.controller.reactivar.seleccio.buida"));
            return REDIRECT + request.getHeader(REFERER);
        }
        log.info("Reactivam consulta dels enviaments: " + StringUtils.join(seleccio, ", "));
        try {
            notificacioService.resetNotificacioANotifica(seleccio);
            MissatgesHelper.info(request, getMessage(request, "enviament.controller.reactivar.consultes.OK"));
        } catch (Exception e) {
            MissatgesHelper.error(request, getMessage(request, "enviament.controller.reactivar.consultes.KO"));
        }
        return REDIRECT + request.getHeader(REFERER);
    }

    @GetMapping(value = "/reactivar/sir")
    @ResponseBody
    public String reactivarSir(HttpServletRequest request, HttpServletResponse response) throws IOException {

        var seleccio = getIdsEnviamentsSeleccionats(request);
        if (seleccio == null || seleccio.isEmpty()) {
            MissatgesHelper.error(request, getMessage(request, "enviament.controller.reactivar.seleccio.buida"));
            return REDIRECT + request.getHeader(REFERER);
        }
        log.info("Reactivam SIR dels enviaments: " + StringUtils.join(seleccio, ", "));
        try {
            enviamentService.reactivaSir(seleccio);
            MissatgesHelper.info(request, getMessage(request, "enviament.controller.reactivar.sir.OK"));
        } catch (Exception e) {
            MissatgesHelper.error(request, getMessage(request, "enviament.controller.reactivar.sir.KO"));
        }
        return "Done";
    }

    @GetMapping(value = {"/actualitzarestat", "{notificacioId}/notificacio/actualitzarestat"})
    @ResponseBody
    public String actualitzarEstat(HttpServletRequest request, HttpServletResponse response) throws IOException {

        var seleccio = getIdsEnviamentsSeleccionats(request);
        if (seleccio == null || seleccio.isEmpty()) {
            MissatgesHelper.error(request, getMessage(request, "enviament.controller.actualitzarestat.buida"));
            return ERROR;
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
            return "ok";
        }
        return "";
    }

    @GetMapping(value = {"/enviar/callback", "{notifiacioId}/enviar/callback"})
    public String enviarCallbacks(HttpServletRequest request, HttpServletResponse response) throws IOException {

        var path = request.getServletPath().split("/");
        var seleccio = getIdsSeleccionats(request);
        if ((seleccio == null || seleccio.isEmpty()) && (path.length == 0 || Strings.isNullOrEmpty(path[2]))) {
            MissatgesHelper.error(request, getMessage(request,"enviament.controller.enviar.callback.buida"));
            return REDIRECT + request.getHeader(REFERER);
        }
        var notificacioId =  seleccio != null && seleccio.isEmpty() ? Long.parseLong(path[2]) : null;
        seleccio = notificacioId != null ? new HashSet<>(List.of(notificacioId)) : seleccio;
        log.info("Reactivam callback dels enviaments: " + StringUtils.join(seleccio, ", "));
        var hasErrors = false;
        try {
            var enviamentsAmbError = enviamentService.enviarCallback(seleccio);
            if (!enviamentsAmbError.isEmpty()) {
                hasErrors = true;
                MissatgesHelper.error(request, getMessage(request, "enviament.controller.enviar.callback.KO") + " " + enviamentsAmbError);
            }
        } catch (Exception e) {
            hasErrors = true;
            MissatgesHelper.error(request, getMessage(request, "enviament.controller.enviar.callback.KO"));
        }
        if (!hasErrors) {
            MissatgesHelper.info(request, getMessage(request,"enviament.controller.enviar.callback.OK"));
        }
        return REDIRECT + request.getHeader(REFERER);
    }

    @GetMapping(value = "/reactivar/callback")
    public String reactivarCallbacks(HttpServletRequest request, HttpServletResponse response) throws IOException {

        var seleccio = getIdsEnviamentsSeleccionats(request);
        if (seleccio == null || seleccio.isEmpty()) {
            MissatgesHelper.error(request, getMessage(request,"enviament.controller.reactivar.callback.buida"));
            return REDIRECT + request.getHeader(REFERER);
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
        return REDIRECT + request.getHeader(REFERER);
    }

    private boolean isAdministrador(HttpServletRequest request) {
        return RolHelper.isUsuariActualAdministrador(sessionScopedContext.getRolActual());
    }

    private void mostraErrorReintentarNotificacio(HttpServletRequest request, Long notificacioId, NotificacioDtoV2 notificacio, Exception e) {

        var errorMessage = "";
        if (e.getMessage() != null && !e.getMessage().isEmpty()) {
            errorMessage = e.getMessage();
        } else if (e.getCause() != null && e.getCause().getMessage() != null && !e.getCause().getMessage().isEmpty()) {
            errorMessage = e.getCause().getMessage();
        }
        if (e.getStackTrace() != null && e.getStackTrace().length > 2) {
            var br = "<br/>";
            errorMessage += br;
            errorMessage += e.getStackTrace()[0] + br;
            errorMessage += e.getStackTrace()[1] + br;
            errorMessage += e.getStackTrace()[2] + br + "...";
        }
        var key = "enviament.controller.reintent.notificacio.pendents.error";
        var msg = getMessage(request, key, new String[]{notificacioId.toString(), notificacio.getCreatedDateAmbFormat(), notificacio.getConcepte(), errorMessage});
        MissatgesHelper.error(request, msg);
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
        return seleccio != null ? new HashSet<>(seleccio) : new HashSet<>();
    }

    /**
     * Retorna els ids dels elements que es mostren actualment a la taula.
     * @param request
     * @return
     * @throws ParseException
     */
    protected abstract List<Long> getIdsElementsFiltrats(HttpServletRequest request) throws ParseException;
}
