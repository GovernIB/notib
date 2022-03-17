package es.caib.notib.war.controller;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.FitxerDto;
import es.caib.notib.core.api.dto.NotificacioEnviamentDtoV2;
import es.caib.notib.core.api.dto.notificacio.NotificacioDtoV2;
import es.caib.notib.core.api.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.core.api.exception.NotFoundException;
import es.caib.notib.core.api.exception.RegistreNotificaException;
import es.caib.notib.core.api.service.EnviamentService;
import es.caib.notib.core.api.service.NotificacioService;
import es.caib.notib.war.helper.MissatgesHelper;
import es.caib.notib.war.helper.RequestSessionHelper;
import es.caib.notib.war.helper.RolHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public int select(
            HttpServletRequest request,
            @RequestParam(value="ids[]", required = false) Long[] ids) {
        @SuppressWarnings("unchecked")
        Set<Long> seleccio = (Set<Long>)RequestSessionHelper.obtenirObjecteSessio(request, sessionAttributeSeleccio);
        if (seleccio == null) {
            seleccio = new HashSet<>();
            RequestSessionHelper.actualitzarObjecteSessio(request, sessionAttributeSeleccio, seleccio);
        }
        if (ids != null) {
            seleccio.addAll(Arrays.asList(ids));
        } else {
            try {
                seleccio.addAll(getIdsElementsFiltrats(request));
            } catch (NotFoundException | ParseException e) {
                e.printStackTrace();
            }
        }
        return seleccio.size();
    }

    @RequestMapping(value = {"/deselect", "{notificacioId}/notificacio/deselect"}, method = RequestMethod.GET)
    @ResponseBody
    public int deselect(
            HttpServletRequest request,
            @RequestParam(value="ids[]", required = false) Long[] ids) {
        @SuppressWarnings("unchecked")
        Set<Long> seleccio = (Set<Long>) RequestSessionHelper.obtenirObjecteSessio(
                request,
                sessionAttributeSeleccio);
        if (seleccio == null) {
            seleccio = new HashSet<Long>();
            RequestSessionHelper.actualitzarObjecteSessio(
                    request,
                    sessionAttributeSeleccio,
                    seleccio);
        }
        if (ids != null) {
            for (Long id: ids) {
                seleccio.remove(id);
            }
        } else {
            seleccio.clear();
        }
        return seleccio.size();
    }
    // TODO SEGUIR AQUÍ 
    @RequestMapping(value = {"/export/{format}", "{notificacioId}/notificacio/export/{format}"}, method = RequestMethod.GET)
    public String export(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable String format) throws IOException {
        Set<Long> seleccio = getIdsEnviamentsSeleccionats(request);
        if (seleccio == null || seleccio.isEmpty()) {
            MissatgesHelper.error(
                    request,
                    getMessage(
                            request,
                            "enviament.controller.exportacio.seleccio.buida"));
            return "redirect:../.." + (requestIsRemesesEnviamentMassiu(request) ? "/remeses" : "");
        } else {
            EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
            FitxerDto fitxer;
            try {
                fitxer = enviamentService.exportacio(
                        entitatActual.getId(),
                        seleccio,
                        format);
                writeFileToResponse(
                        fitxer.getNom(),
                        fitxer.getContingut(),
                        response);
            } catch (NotFoundException | ParseException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    @RequestMapping(value = {"/reintentar/notificacio", "{notificacioId}/notificacio/reintentar/notificacio"}, method = RequestMethod.GET)
    @ResponseBody
    public String reintentarNotificacio(
            HttpServletRequest request,
            HttpServletResponse response) throws IOException, RegistreNotificaException {
        Set<Long> seleccio = getIdsEnviamentsSeleccionats(request);
        String resposta = "";
        if (seleccio == null || seleccio.isEmpty()) {
            MissatgesHelper.error(
                    request,
                    getMessage(
                            request,
                            "enviament.controller.notificacio.seleccio.buida"));
            resposta = "error";
        } else {
            MissatgesHelper.info( request,
                    getMessage(
                            request,
                            "enviament.controller.reintent.notificacio.pendents.executant"));
            Set<Long> notificacioIds = new HashSet<Long>();
            for(Long id: seleccio) {
                NotificacioEnviamentDtoV2 e = enviamentService.getOne(id);
                notificacioIds.add(e.getNotificacioId());
            }
            Integer notificacionsNoRegistrades = 0;
            Integer notificacionsError = 0;

            for(Long notificacioId: notificacioIds) {
                NotificacioDtoV2 notificacio = notificacioService.findAmbId(
                        notificacioId,
                        isAdministrador(request));
                if(notificacio.getEstat().equals(NotificacioEstatEnumDto.PENDENT)) {
                    try {
                        notificacioService.registrarNotificar(notificacioId);
                    } catch (Exception e) {
                        notificacionsError++;
                        mostraErrorReintentarNotificacio(request, notificacioId, notificacio, e);
                    }
                }else if (notificacio.getEstat().equals(NotificacioEstatEnumDto.REGISTRADA)) {
                    try {
                        notificacioService.notificacioEnviar(notificacioId);
                    } catch (Exception e) {
                        notificacionsError++;
                        mostraErrorReintentarNotificacio(request, notificacioId, notificacio, e);
                    }
                }else{
                    notificacionsNoRegistrades++;
                }
            }

            if(notificacionsNoRegistrades.equals((Integer)notificacioIds.size())) {
                MissatgesHelper.error(
                        request,
                        getMessage(
                                request,
                                "enviament.controller.reintent." + (notificacionsNoRegistrades == 1 ? "notificacio" : "notificacions" )+ ".pendents.KO"));
            } else if(notificacionsError.equals((Integer)notificacioIds.size())) {
                MissatgesHelper.error(
                        request,
                        getMessage(
                                request,
                                "enviament.controller.reintent." + (notificacionsError == 1 ? "notificacio" : "notificacions" )+ ".pendents.error"));
            } else if (notificacionsError > 0) {
                MissatgesHelper.warning(
                        request,
                        notificacionsError + " " + getMessage(request, "enviament.controller.reintent.notificacions.pendents.error.alguna"));
            } else {
                MissatgesHelper.info(
                        request,
                        getMessage(
                                request,
                                "enviament.controller.reintent." + (notificacioIds.size() == 1 ? "notificacio" : "notificacions") + ".pendents.OK"));
            }
            resposta = "ok";
        }
        return resposta;
    }

    @RequestMapping(value = "/reactivar/notificacionsError", method = RequestMethod.GET)
    @ResponseBody
    public String reactivarErrors(
            HttpServletRequest request) throws IOException {
        String resposta = "";
        Set<Long> seleccio = getIdsEnviamentsSeleccionats(request);
        if (seleccio == null || seleccio.isEmpty()) {
            MissatgesHelper.error(
                    request,
                    getMessage(
                            request,
                            "enviament.controller.notificacio.seleccio.buida"));
            resposta = "error";
        } else {

            Set<Long> notificacioIds = new HashSet<Long>();
            for(Long id: seleccio) {
                NotificacioEnviamentDtoV2 e = enviamentService.getOne(id);
                notificacioIds.add(e.getNotificacioId());
            }

            log.info("Reactivam enviaments de notificacions amb error: " + StringUtils.join(notificacioIds, ", "));

            Integer notificacionsNoFinalitzadesAmbError = 0;
            Integer notificacionsError = 0;
            for(Long notificacioId: notificacioIds) {
                NotificacioDtoV2 notificacio = notificacioService.findAmbId(
                        notificacioId,
                        isAdministrador(request));
                if(notificacio.getEstat().equals(NotificacioEstatEnumDto.FINALITZADA_AMB_ERRORS) || notificacio.isJustificantCreat()) {
                    try {
                        notificacioService.reactivarNotificacioAmbErrors(notificacioId);
                    } catch (Exception e) {
                        notificacionsError++;
                        mostraErrorReintentarNotificacio(request, notificacioId, notificacio, e);
                    }
                } else {
                    notificacionsNoFinalitzadesAmbError++;
                }
            }

            if(notificacionsNoFinalitzadesAmbError.equals((Integer)notificacioIds.size())) {
                MissatgesHelper.error(
                        request,
                        getMessage(
                                request,
                                "enviament.controller.reintent." + (notificacionsNoFinalitzadesAmbError == 1 ? "notificacio" : "notificacions" )+ ".errors.KO"));
            } else if(notificacionsError.equals((Integer)notificacioIds.size())) {
                MissatgesHelper.error(
                        request,
                        getMessage(
                                request,
                                "enviament.controller.reintent." + (notificacionsError == 1 ? "notificacio" : "notificacions" )+ ".errors.error"));
            } else if (notificacionsError > 0) {
                MissatgesHelper.warning(
                        request,
                        notificacionsError + " " + getMessage(request, "enviament.controller.reintent.notificacions.errors.error.alguna"));
            } else {
                MissatgesHelper.info(
                        request,
                        getMessage(
                                request,
                                "enviament.controller.reintent." + (notificacioIds.size() == 1 ? "notificacio" : "notificacions") + ".errors.OK"));
            }
            resposta = "ok";
        }

        return resposta;
    }

    @RequestMapping(value = "/reactivar/consulta", method = RequestMethod.GET)
    public String reactivarConsulta(
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        Set<Long> seleccio = getIdsEnviamentsSeleccionats(request);
        if (seleccio == null || seleccio.isEmpty()) {
            MissatgesHelper.error(
                    request,
                    getMessage(
                            request,
                            "enviament.controller.reactivar.seleccio.buida"));
            return "redirect:" + request.getHeader("Referer");
        }

        log.info("Reactivam consulta dels enviaments: " + StringUtils.join(seleccio, ", "));
        try {
            enviamentService.reactivaConsultes(seleccio);
            MissatgesHelper.info(
                    request,
                    getMessage(
                            request,
                            "enviament.controller.reactivar.consultes.OK"));
        } catch (Exception e) {
            MissatgesHelper.error(
                    request,
                    getMessage(
                            request,
                            "enviament.controller.reactivar.consultes.KO"));
        }

        return "redirect:" + request.getHeader("Referer");
    }


    @RequestMapping(value = "/reactivar/sir", method = RequestMethod.GET)
    public String reactivarSir(
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        Set<Long> seleccio = getIdsEnviamentsSeleccionats(request);
        if (seleccio == null || seleccio.isEmpty()) {
            MissatgesHelper.error(
                    request,
                    getMessage(
                            request,
                            "enviament.controller.reactivar.seleccio.buida"));
            return "redirect:" + request.getHeader("Referer");
        }


        log.info("Reactivam SIR dels enviaments: " + StringUtils.join(seleccio, ", "));
        try {
            enviamentService.reactivaSir(seleccio);
            MissatgesHelper.info(
                    request,
                    getMessage(
                            request,
                            "enviament.controller.reactivar.sir.OK"));
        } catch (Exception e) {
            MissatgesHelper.error(
                    request,
                    getMessage(
                            request,
                            "enviament.controller.reactivar.sir.KO"));
        }

        return "redirect:" + request.getHeader("Referer");
    }

    @RequestMapping(value = {"/actualitzarestat", "{notificacioId}/notificacio/actualitzarestat"}, method = RequestMethod.GET)
    @ResponseBody
    public String actualitzarEstat(
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        Set<Long> seleccio = getIdsEnviamentsSeleccionats(request);
        String resposta = "";
        if (seleccio == null || seleccio.isEmpty()) {
            MissatgesHelper.error(
                    request,
                    getMessage(
                            request,
                            "enviament.controller.actualitzarestat.buida"));
            resposta = "error";
        } else {
            MissatgesHelper.info( request,
                    getMessage(
                            request,
                            "enviament.controller.actualitzarestat.executant"));

            log.info("Acualitzam estat dels enviaments: " + StringUtils.join(seleccio, ", "));
            boolean hasErrors = false;
            for(Long enviamentId : seleccio) {
                try {
                    enviamentService.actualitzarEstat(enviamentId);
                } catch (Exception e) {
                    hasErrors = true;
                    MissatgesHelper.error(
                            request,
                            getMessage(
                                    request,
                                    "enviament.controller.actualitzarestat.KO") + " [" + enviamentId + "]");
                }
            }
            RequestSessionHelper.actualitzarObjecteSessio(request, sessionAttributeSeleccio, new HashSet<Long>());
            if (!hasErrors) {
                MissatgesHelper.info(
                        request,
                        getMessage(
                                request,
                                "enviament.controller.actualitzarestat.OK"));
                resposta = "ok";
            }
        }
        return resposta;
    }

    @RequestMapping(value = "/reactivar/callback", method = RequestMethod.GET)
    public String reactivarCallbacks(
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        Set<Long> seleccio = getIdsEnviamentsSeleccionats(request);
        if (seleccio == null || seleccio.isEmpty()) {
            MissatgesHelper.error(
                    request,
                    getMessage(
                            request,
                            "enviament.controller.reactivar.callback.buida"));
            return "redirect:" + request.getHeader("Referer");
        }

        log.info("Reactivam callback dels enviaments: " + StringUtils.join(seleccio, ", "));
        boolean hasErrors = false;
        for(Long enviamentId : seleccio) {
            try {
                enviamentService.activarCallback(enviamentId);
            } catch (Exception e) {
                hasErrors = true;
                MissatgesHelper.error(
                        request,
                        getMessage(
                                request,
                                "enviament.controller.reactivar.callback.KO"));
            }
        }

        if (!hasErrors) {
            MissatgesHelper.info(
                    request,
                    getMessage(
                            request,
                            "enviament.controller.reactivar.callback.OK"));
        }

        return "redirect:" + request.getHeader("Referer");
    }

    private boolean isAdministrador(HttpServletRequest request) {
        return RolHelper.isUsuariActualAdministrador(request);
    }
    private void mostraErrorReintentarNotificacio(HttpServletRequest request, Long notificacioId, NotificacioDtoV2 notificacio, Exception e) {
        String errorMessage = "";
        if (e.getMessage() != null && !e.getMessage().isEmpty())
            errorMessage = e.getMessage();
        else if (e.getCause() != null && e.getCause().getMessage() != null && !e.getCause().getMessage().isEmpty())
            errorMessage = e.getCause().getMessage();
        if (e.getStackTrace() != null && e.getStackTrace().length > 2) {
            errorMessage += "<br/>";
            errorMessage += e.getStackTrace()[0] + "<br/>";
            errorMessage += e.getStackTrace()[1] + "<br/>";
            errorMessage += e.getStackTrace()[2] + "<br/>...";
        }
        MissatgesHelper.error(
                request,
                getMessage(
                        request,
                        "enviament.controller.reintent.notificacio.pendents.error",
                        new String[]{
                                notificacioId.toString(),
                                notificacio.getCreatedDateAmbFormat(),
                                notificacio.getConcepte(),
                                errorMessage})
        );
    }

    protected boolean requestIsRemesesEnviamentMassiu(HttpServletRequest request) {
        return request.getRequestURI().contains("/notib/notificacio/");
    }

    protected Set<Long> getIdsEnviamentsSeleccionats(HttpServletRequest request) {
        Set<Long> ids = getIdsSeleccionats(request);
        return requestIsRemesesEnviamentMassiu(request) ? enviamentService.findIdsByNotificacioIds(ids) : ids;
    }

    protected Set<Long> getIdsSeleccionats(HttpServletRequest request) {

        Set<Long> seleccio = (Set<Long>) RequestSessionHelper.obtenirObjecteSessio(request, sessionAttributeSeleccio);
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
