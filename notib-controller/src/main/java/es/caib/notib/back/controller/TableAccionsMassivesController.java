package es.caib.notib.back.controller;

import com.google.common.base.Strings;
import es.caib.notib.back.helper.MissatgesHelper;
import es.caib.notib.back.helper.RequestSessionHelper;
import es.caib.notib.back.helper.RolHelper;
import es.caib.notib.logic.intf.dto.ArxiuDto;
import es.caib.notib.logic.intf.dto.FitxerDto;
import es.caib.notib.logic.intf.dto.NotificacioEnviamentDtoV2;
import es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaExecucio;
import es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaTipus;
import es.caib.notib.logic.intf.dto.accioMassiva.SeleccioTipus;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioDtoV2;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioEstatEnumDto;
import es.caib.notib.logic.intf.exception.NotFoundException;
import es.caib.notib.logic.intf.exception.RegistreNotificaException;
import es.caib.notib.logic.intf.service.AccioMassivaService;
import es.caib.notib.logic.intf.service.EnviamentService;
import es.caib.notib.logic.intf.service.JustificantService;
import es.caib.notib.logic.intf.service.NotificacioService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaTipus.EXPORTAR_FULL_CALCUL;

@Slf4j
public abstract class TableAccionsMassivesController extends BaseUserController {

    @Autowired
    private NotificacioService notificacioService;
    @Autowired
    private EnviamentService enviamentService;
    @Autowired
    protected JustificantService justificantService;
    @Autowired
    private AccioMassivaService accioMassivaService;

    protected String sessionAttributeSeleccio;
    protected Long notMassivaId;
    private static final String REFERER = "Referer";
    private static final String ERROR = "error";
    private static final String REINTENT_TEXT = "enviament.controller.reintent.";
    private static final String NOTIFICACIO = "notificacio";
    private static final String NOTIFICACIONS = "notificacions";
    private static final String REDIRECT = "redirect:";
    protected static final String SET_COOKIE = "Set-cookie";
    protected static final String FILE_DOWNLOAD = "fileDownload=true; path=/";


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
        if (seleccio.size() == 1 && seleccio.contains(-1L)) {
            return "redirect:../.." + (requestIsRemesesEnviamentMassiu(request) ? "/remeses" : "");
        }
        var entitatActual = getEntitatActualComprovantPermisos(request);
        try {
            var seleccioTipus = SeleccioTipus.ENVIAMENT;
            var isAdminEntitat = RolHelper.isUsuariActualAdministradorEntitat(sessionScopedContext.getRolActual());
            var accio = AccioMassivaExecucio.builder().isAdminEntitat(isAdminEntitat).tipus(AccioMassivaTipus.EXPORTAR_FULL_CALCUL).seleccioTipus(seleccioTipus).entitatId(entitatActual.getId()).seleccio(seleccio).format(format).build();
            var accioId = accioMassivaService.altaAccioMassiva(accio);
            accio.setAccioId(accioId);
            var fitxer = accioMassivaService.exportar(accio);
            if (fitxer == null) {
                return null;
            }
            writeFileToResponse(fitxer.getNom(), fitxer.getContingut(), response);
        } catch (Exception ex) {
            log.error("Error exportant els elements seleccionats", ex);
        }
        return null;
    }

    @GetMapping(value = {"/descarregar/justificant/massiu", "{notificacioId}/notificacio/descarregar/justificant/massiu"})
    @ResponseBody
    public void justificantDescarregarMassiu(HttpServletRequest request, HttpServletResponse response, Model model) throws IOException {

        var entitatActual = sessionScopedContext.getEntitatActual();
        var referer = request.getHeader("Referer");
        var seleccio = getIdsSeleccionats(request);
        if (seleccio == null || seleccio.isEmpty() || (seleccio.size() == 1 && seleccio.contains(-1L))) {
            return;
        }

        var seleccioTipus = requestIsRemesesEnviamentMassiu(request) ? SeleccioTipus.NOTIFICACIO : SeleccioTipus.ENVIAMENT;
        var isAdminEntitat = RolHelper.isUsuariActualAdministradorEntitat(sessionScopedContext.getRolActual());
        var accio = AccioMassivaExecucio.builder().isAdminEntitat(isAdminEntitat).tipus(AccioMassivaTipus.DESCARREGA_JUSTIFICANT_ENVIAMENT).seleccio(seleccio).seleccioTipus(seleccioTipus).entitatId(entitatActual.getId()).build();
        var accioId = accioMassivaService.altaAccioMassiva(accio);
        accio.setAccioId(accioId);
        var justificants = accioMassivaService.descarregarJustificant(accio);
        response.setHeader(SET_COOKIE, FILE_DOWNLOAD);
        try (var baos = new ByteArrayOutputStream(); var zos = new ZipOutputStream(baos)) {
            for (var just : justificants) {
                var entry = new ZipEntry(StringUtils.stripAccents(just.getNom()));
                entry.setSize(just.getContingut().length);
                zos.putNextEntry(entry);
                zos.write(just.getContingut());
                zos.closeEntry();
            }
            zos.close();
            var sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            var date = sdf.format(new Date()).replace(":", "_");
            writeFileToResponse("justificantsMassiu_" + date + ".zip", baos.toByteArray(), response);
            //        return getModalControllerReturnValueSuccess(request,REDIRECT + referer,"notificacio.controller.descarregar.justificant.massiu.ok");
        }
    }

    @GetMapping(value = {"/descarregar/certificacio/massiu", "{notificacioId}/notificacio/descarregar/certificacio/massiu"})
    @ResponseBody
    public void certificacioDescarregarMassiu(HttpServletRequest request, HttpServletResponse response, Model model) throws IOException {

        var seleccio = getIdsSeleccionats(request);
        if (seleccio == null || seleccio.isEmpty() || (seleccio.size() == 1 && seleccio.contains(-1L))) {
            return;
        }
        var entitatActual = sessionScopedContext.getEntitatActual();
        var seleccioTipus = requestIsRemesesEnviamentMassiu(request) ? SeleccioTipus.NOTIFICACIO : SeleccioTipus.ENVIAMENT;
        var isAdminEntitat = RolHelper.isUsuariActualAdministradorEntitat(sessionScopedContext.getRolActual());
        var accio = AccioMassivaExecucio.builder().isAdminEntitat(isAdminEntitat).tipus(AccioMassivaTipus.DESCARREGA_CERTIFICAT_RECEPCIO).seleccioTipus(seleccioTipus).entitatId(entitatActual.getId()).seleccio(seleccio).build();
        var accioId = accioMassivaService.altaAccioMassiva(accio);
        accio.setAccioId(accioId);
        response.setHeader(SET_COOKIE, FILE_DOWNLOAD);
        var certificacions = accioMassivaService.descarregarCertificacio(accio);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        for (var notCerts : certificacions) {
            for (var certificacio : notCerts) {
                ZipEntry entry = new ZipEntry(StringUtils.stripAccents(certificacio.getNom()));
                entry.setSize(certificacio.getContingut().length);
                zos.putNextEntry(entry);
                zos.write(certificacio.getContingut());
                zos.closeEntry();
            }
        }
        zos.close();
        baos.close();
        var sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        var date = sdf.format(new Date()).replace(":", "_");
        writeFileToResponse("certificacionsMassives_" + date + ".zip", baos.toByteArray(), response);
    }

    @GetMapping(value = {"/reactivar/notificacionsError", "{notificacioId}/notificacio/reactivar/notificacionsError"})
    @ResponseBody
    public String reactivarErrors(HttpServletRequest request) throws IOException {

        var seleccio = getIdsEnviamentsSeleccionats(request);
        if (seleccio == null || seleccio.isEmpty()) {
            MissatgesHelper.error(request, getMessage(request, "enviament.controller.reactivar.seleccio.buida"));
            return REDIRECT + request.getHeader(REFERER);
        }
        if (seleccio.size() == 1 && seleccio.contains(-1L)) {
            return REDIRECT + request.getHeader(REFERER);
        }
        log.info("Reactivam els enviaments amb error: " + StringUtils.join(seleccio, ", "));
        var entitatActual = sessionScopedContext.getEntitatActual();
        var seleccioTipus = SeleccioTipus.ENVIAMENT;
        var isAdminEntitat = RolHelper.isUsuariActualAdministradorEntitat(sessionScopedContext.getRolActual());
        var accio = AccioMassivaExecucio.builder().isAdminEntitat(isAdminEntitat).tipus(AccioMassivaTipus.TORNA_ENVIAR_AMB_ERROR).seleccioTipus(seleccioTipus).entitatId(entitatActual.getId()).seleccio(seleccio).build();
        var accioId = accioMassivaService.altaAccioMassiva(accio);
        accio.setAccioId(accioId);
        try {
            var resposta = accioMassivaService.reactivarErrors(accio);
            var msg = "enviament.controller.reactivar.enviament.error.fi.reintents.";
            msg += !resposta.getNoExecutables().isEmpty() ? "notificacions.antigues" : !resposta.getErrors().isEmpty() ? "KO" : "OK";
            MissatgesHelper.info(request, getMessage(request, msg));
        } catch (Exception e) {
            MissatgesHelper.error(request, getMessage(request, "enviament.controller.reactivar.enviament.error.fi.reintents.KO"));
        }
        return REDIRECT + request.getHeader(REFERER);
    }

    @GetMapping(value = {"/reactivar/consulta", "{notificacioId}/notificacio/reactivar/consulta"})
    public String reactivarConsulta(HttpServletRequest request, HttpServletResponse response) throws IOException {

        var seleccio = getIdsEnviamentsSeleccionats(request);
        if (seleccio == null || seleccio.isEmpty()) {
            MissatgesHelper.error(request, getMessage(request, "enviament.controller.reactivar.seleccio.buida"));
            return REDIRECT + request.getHeader(REFERER);
        }
        if (seleccio.size() == 1 && seleccio.contains(-1L)) {
            return REDIRECT + request.getHeader(REFERER);
        }
        log.info("Reactivam consulta dels canvis d'estat: " + StringUtils.join(seleccio, ", "));
        var entitatActual = sessionScopedContext.getEntitatActual();
        var seleccioTipus = SeleccioTipus.ENVIAMENT;
        var isAdminEntitat = RolHelper.isUsuariActualAdministradorEntitat(sessionScopedContext.getRolActual());
        var accio = AccioMassivaExecucio.builder().isAdminEntitat(isAdminEntitat).tipus(AccioMassivaTipus.TORNA_ACTIVAR_CONSULTES_CANVI_ESTAT).seleccioTipus(seleccioTipus).entitatId(entitatActual.getId()).seleccio(seleccio).build();
        var accioId = accioMassivaService.altaAccioMassiva(accio);
        accio.setAccioId(accioId);
        try {
            accioMassivaService.executarAccio(accio);
            MissatgesHelper.info(request, getMessage(request, "enviament.controller.reactivar.consultes.OK"));
        } catch (Exception e) {
            MissatgesHelper.error(request, getMessage(request, "enviament.controller.reactivar.consultes.KO"));
        }
        return REDIRECT + request.getHeader(REFERER);
    }

    @GetMapping(value = {"/reactivar/sir", "{notificacioId}/notificacio/reactivar/sir"})
    @ResponseBody
    public String reactivarSir(HttpServletRequest request, HttpServletResponse response) throws IOException {

        var seleccio = getIdsEnviamentsSeleccionats(request);
        if (seleccio == null || seleccio.isEmpty()) {
            MissatgesHelper.error(request, getMessage(request, "enviament.controller.reactivar.seleccio.buida"));
            return REDIRECT + request.getHeader(REFERER);
        }
        if (seleccio.size() == 1 && seleccio.contains(-1L)) {
            return REDIRECT + request.getHeader(REFERER);
        }
        log.info("Reactivam SIR dels enviaments: " + StringUtils.join(seleccio, ", "));
        var entitatActual = sessionScopedContext.getEntitatActual();
        var seleccioTipus = requestIsRemesesEnviamentMassiu(request) ? SeleccioTipus.NOTIFICACIO : SeleccioTipus.ENVIAMENT;
        var isAdminEntitat = RolHelper.isUsuariActualAdministradorEntitat(sessionScopedContext.getRolActual());
        var accio = AccioMassivaExecucio.builder().isAdminEntitat(isAdminEntitat).tipus(AccioMassivaTipus.REACTIVAR_SIR).seleccioTipus(seleccioTipus).entitatId(entitatActual.getId()).seleccio(seleccio).build();
        var accioId = accioMassivaService.altaAccioMassiva(accio);
        accio.setAccioId(accioId);
        try {
            accioMassivaService.executarAccio(accio);
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
        if (seleccio.size() == 1 && seleccio.contains(-1L)) {
            return ERROR;
        }
        MissatgesHelper.info( request, getMessage(request, "enviament.controller.actualitzarestat.executant"));
        log.info("Acualitzam estat dels enviaments: " + StringUtils.join(seleccio, ", "));
        var entitatActual = sessionScopedContext.getEntitatActual();
        var isAdminEntitat = RolHelper.isUsuariActualAdministradorEntitat(sessionScopedContext.getRolActual());
        var accio = AccioMassivaExecucio.builder().isAdminEntitat(isAdminEntitat).tipus(AccioMassivaTipus.ACTUALITZAR_ESTAT).seleccioTipus(SeleccioTipus.ENVIAMENT).entitatId(entitatActual.getId()).seleccio(seleccio).build();
        var accioId = accioMassivaService.altaAccioMassiva(accio);
        accio.setAccioId(accioId);
        accioMassivaService.executarAccio(accio);
//        var hasErrors = false;
//        for(var enviamentId : seleccio) {
//            try {
//                enviamentService.actualitzarEstat(enviamentId);
//            } catch (Exception e) {
//                hasErrors = true;
//                MissatgesHelper.error(request,getMessage(request, "enviament.controller.actualitzarestat.KO") + " [" + enviamentId + "]");
//            }
//        }
        RequestSessionHelper.actualitzarObjecteSessio(request, sessionAttributeSeleccio, new HashSet<Long>());
//        if (!hasErrors) {
            MissatgesHelper.info(request, getMessage(request,"enviament.controller.actualitzarestat.OK"));
            return "ok";
//        }
//        return "";
    }

    @GetMapping(value = {"{notificacioId}/enviar/callback", "{notifiacioId}/notificacio/enviar/callback"})
    public String enviarCallbacks(HttpServletRequest request, HttpServletResponse response) throws IOException {

        var path = request.getServletPath().split("/");
        var seleccio = getIdsSeleccionats(request);
        if ((seleccio == null || seleccio.isEmpty()) && (path.length == 0 || Strings.isNullOrEmpty(path[2]))) {
            MissatgesHelper.error(request, getMessage(request,"enviament.controller.enviar.callback.buida"));
            return REDIRECT + request.getHeader(REFERER);
        }
        if (seleccio.size() == 1 && seleccio.contains(-1L)) {
            return REDIRECT + request.getHeader(REFERER);
        }
        var notificacioId =  seleccio != null && seleccio.isEmpty() ? Long.parseLong(path[2]) : null;
        seleccio = notificacioId != null ? new HashSet<>(List.of(notificacioId)) : seleccio;
        log.info("Reactivam callback dels enviaments: " + StringUtils.join(seleccio, ", "));
        var entitatActual = sessionScopedContext.getEntitatActual();
        var seleccioTipus = requestIsRemesesEnviamentMassiu(request) ? SeleccioTipus.NOTIFICACIO : SeleccioTipus.ENVIAMENT;
        var isAdminEntitat = RolHelper.isUsuariActualAdministradorEntitat(sessionScopedContext.getRolActual());
        var accio = AccioMassivaExecucio.builder().isAdminEntitat(isAdminEntitat).tipus(AccioMassivaTipus.ENVIAR_CALLBACK).seleccioTipus(seleccioTipus).entitatId(entitatActual.getId()).seleccio(seleccio).build();
        var accioId = accioMassivaService.altaAccioMassiva(accio);
        accio.setAccioId(accioId);
        var hasErrors = false;
        try {
            accioMassivaService.executarAccio(accio);
//            var enviamentsAmbError = enviamentService.enviarCallback(seleccio);
//            if (!enviamentsAmbError.isEmpty()) {
//                hasErrors = true;
//                MissatgesHelper.error(request, getMessage(request, "enviament.controller.enviar.callback.KO") + " " + enviamentsAmbError);
//            }
        } catch (Exception e) {
            hasErrors = true;
            MissatgesHelper.error(request, getMessage(request, "enviament.controller.enviar.callback.KO"));
        }
        if (!hasErrors) {
            MissatgesHelper.info(request, getMessage(request,"enviament.controller.enviar.callback.OK"));
        }
        return REDIRECT + request.getHeader(REFERER);
    }

    @GetMapping(value = {"/reactivar/callback" , "{notificacioId}/notificacio/reactivar/callback"})
    public String reactivarCallbacks(HttpServletRequest request, HttpServletResponse response) throws IOException {

        var seleccio = getIdsEnviamentsSeleccionats(request);
        if (seleccio == null || seleccio.isEmpty()) {
            MissatgesHelper.error(request, getMessage(request,"enviament.controller.reactivar.callback.buida"));
            return REDIRECT + request.getHeader(REFERER);
        }
        if (seleccio.size() == 1 && seleccio.contains(-1L)) {
            return REDIRECT + request.getHeader(REFERER);
        }
        log.info("Reactivam callback dels enviaments: " + StringUtils.join(seleccio, ", "));
        var entitatActual = sessionScopedContext.getEntitatActual();
        var seleccioTipus = SeleccioTipus.ENVIAMENT;
        var isAdminEntitat = RolHelper.isUsuariActualAdministradorEntitat(sessionScopedContext.getRolActual());
        var accio = AccioMassivaExecucio.builder().isAdminEntitat(isAdminEntitat).tipus(AccioMassivaTipus.TORNA_ACTIVAR_CALLBACK).seleccioTipus(seleccioTipus).entitatId(entitatActual.getId()).seleccio(seleccio).build();
        var accioId = accioMassivaService.altaAccioMassiva(accio);
        accio.setAccioId(accioId);
        accioMassivaService.executarAccio(accio);
//        var hasErrors = false;
//        for(var enviamentId : seleccio) {
//            try {
//                enviamentService.activarCallback(enviamentId);
//            } catch (Exception e) {
//                hasErrors = true;
//                MissatgesHelper.error(request, getMessage(request, "enviament.controller.reactivar.callback.KO"));
//            }
//        }
//        if (!hasErrors) {
            MissatgesHelper.info(request, getMessage(request,"enviament.controller.reactivar.callback.OK"));
//        }
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
        return request.getRequestURI().contains(request.getContextPath() + "/notificacio/");
    }

    protected Set<Long> getIdsEnviamentsSeleccionats(HttpServletRequest request) {

        var ids = getIdsSeleccionats(request);
        if (ids.contains(-1L)) {
            return ids;
        }
        return requestIsRemesesEnviamentMassiu(request) ? enviamentService.findIdsByNotificacioIds(ids) : ids;
    }

    protected Set<Long> getIdsSeleccionats(HttpServletRequest request) {

        @SuppressWarnings("unchecked")
        var seleccio = (Set<Long>) RequestSessionHelper.obtenirObjecteSessio(request, sessionAttributeSeleccio);
        var max = notificacioService.getMaxAccionesMassives();
        if (seleccio != null && seleccio.size() > max) {
            MissatgesHelper.error(request, getMessage(request,"enviament.list.user.accio.massiva.max.elements", new String[] {max+""}));
            Set<Long> maxError = new HashSet<>();
            maxError.add(-1L);
            return maxError;
        }
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
