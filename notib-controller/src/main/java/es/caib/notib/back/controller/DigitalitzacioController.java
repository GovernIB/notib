package es.caib.notib.back.controller;

import com.itextpdf.xmp.impl.Utils;
import es.caib.notib.back.helper.ExceptionHelper;
import es.caib.notib.back.helper.RequestSessionHelper;
import es.caib.notib.logic.intf.dto.escaneig.DigitalitzacioPerfil;
import es.caib.notib.logic.intf.dto.escaneig.DigitalitzacioResultat;
import es.caib.notib.logic.intf.dto.escaneig.DigitalitzacioTransaccioResposta;
import es.caib.notib.logic.intf.exception.SistemaExternException;
import es.caib.notib.logic.intf.service.AplicacioService;
import es.caib.notib.logic.intf.service.DigitalitzacioService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/digitalitzacio")
public class DigitalitzacioController extends BaseUserController {

    private static final String SESSION_ATTRIBUTE_RETURN_SCANNED = "DigitalitzacioController.session.scanned";
    private static final String SESSION_ATTRIBUTE_RETURN_SIGNED = "DigitalitzacioController.session.signed";
    private static final String SESSION_ATTRIBUTE_RETURN_IDTRANSACCIO = "DigitalitzacioController.session.idTransaccio";

    @Autowired
    private DigitalitzacioService digitalitzacioService;
    @Autowired
    private AplicacioService aplicacioService;
//    @Autowired
//    private EventService eventService;

    @RequestMapping(value = "/perfils", method = RequestMethod.GET)
    @ResponseBody
    public List<DigitalitzacioPerfil> digitalitzaciGetPerfils(HttpServletRequest request, HttpServletResponse response, Model model) {

        List<DigitalitzacioPerfil> perfils = new ArrayList<DigitalitzacioPerfil>();
        try {
            perfils = digitalitzacioService.getPerfilsDisponibles();
        } catch (Exception e) {
            log.error("Error al initializar digitalitzacio", e);
//            Exception sisExtExc = ExceptionHelper.findExceptionInstance(e, SistemaExternException.class, 3);
//            perfils.add(new DigitalitzacioPerfil("SERVER_ERROR", "SERVER_ERROR", sisExtExc != null ? sisExtExc.getMessage() : e.getMessage(), -1));
            perfils.add(new DigitalitzacioPerfil("SERVER_ERROR", "SERVER_ERROR", e.getMessage(), -1));
        }
        return perfils;

    }
    @RequestMapping(value = "/iniciarDigitalitzacio/{codiPerfil}", method = RequestMethod.GET)
    @ResponseBody
    public DigitalitzacioTransaccioResposta iniciarDigitalitzacio(HttpServletRequest request, @PathVariable String codiPerfil, Model model) {

        RequestSessionHelper.esborrarObjecteSessio(request, SESSION_ATTRIBUTE_RETURN_SCANNED);
        RequestSessionHelper.esborrarObjecteSessio(request, SESSION_ATTRIBUTE_RETURN_SIGNED);
        var urlReturn = aplicacioService.getBaseUrl() + "/digitalitzacio/recuperarResultat/";
        var transaccioResponse = digitalitzacioService.iniciarDigitalitzacio(codiPerfil, urlReturn);
        RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_RETURN_SCANNED, transaccioResponse.isReturnScannedFile());
        RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_RETURN_SIGNED, transaccioResponse.isReturnSignedFile());
        RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_RETURN_IDTRANSACCIO, transaccioResponse.getIdTransaccio());
        return transaccioResponse;
    }

    @RequestMapping(value = "/mock", method = RequestMethod.GET)
    public String mock(HttpServletRequest request, @RequestParam(value = "idExpedient", required = false) String idExpedient, @RequestParam(value = "idTransaccio", required = false) String idTransaccio, Model model) {

        model.addAttribute("idExpedient", idExpedient);
        model.addAttribute("idTransaccio", idTransaccio);
        return "mockDigitalitzacio";
    }

    @RequestMapping(value = "/recuperarResultatMock/{idTransaccio}", method = RequestMethod.GET)
    public String recuperarResultatMock(HttpServletRequest request, @PathVariable String idTransaccio, Model model) {

        var resposta = new DigitalitzacioResultat();
        resposta.setNomDocument("Nom document");
        if (resposta.isError() && resposta.getEstat() != null) {
            model.addAttribute("digitalizacioError", getMessage(request, "document.digitalitzacio.estat.enum."+ resposta.getEstat()));
            return "digitalitzacioIframeTancar";
        }
        model.addAttribute("digitalizacioFinalOk", getMessage(request, "document.digitalitzacio.estat.enum.FINAL_OK"));
        model.addAttribute("nomDocument", resposta.getNomDocument());
        return "digitalitzacioIframeTancar";
    }

    @RequestMapping(value = "/event/resultatScan/{dades}/{idTransaccio}", method = RequestMethod.GET,  produces = "text/plain")
    @ResponseBody
    public ResponseEntity<String> recuperarResultatScanEvent(HttpServletRequest request, @PathVariable String dades, @PathVariable String idTransaccio, Model model) {

        // Autenticar un usuari simulat si és necessari
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        if (auth == null || "anonymousUser".equals(auth.getName())) {
//            User user = new User("$portafib_ripea", "portafib_ripea", Collections.singletonList(new SimpleGrantedAuthority("tothom")));
//            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//        }
//        String data = Utils.desencripta(dades, aplicacioService.propertyFindByNom("es.caib.ripea.encription.key"));
//        String[] dataSplri = data.split("#");
//        Long idExpedient = Long.parseLong(dataSplri[0]);
//        var resposta = recuperaResultatEscaneig(idTransaccio, true, true);
//        resposta.setUsuari(dataSplri[2]);
//        ScanFinalitzatEvent sfe = new ScanFinalitzatEvent(idExpedient, resposta);
//        eventService.notifyScanFinalitzat(sfe);
        return ResponseEntity.ok().header("Content-Type", "text/plain; charset=UTF-8").body("Escaneig finalitzat.");
    }

    @RequestMapping(value = "/recuperarResultat/{idTransaccio}", method = RequestMethod.GET)
    public String recuperarResultat(HttpServletRequest request, @PathVariable String idTransaccio, Model model) {

        var returnScannedFile = (boolean) RequestSessionHelper.obtenirObjecteSessio(request, SESSION_ATTRIBUTE_RETURN_SCANNED);
        var returnSignedFile = (boolean) RequestSessionHelper.obtenirObjecteSessio(request, SESSION_ATTRIBUTE_RETURN_SIGNED);
        var resposta = recuperaResultatEscaneig(idTransaccio, returnScannedFile, returnSignedFile);
        if (resposta.isError() && resposta.getEstat() != null) {
            model.addAttribute("digitalizacioError", getMessage(request, "document.digitalitzacio.estat.enum."+ resposta.getEstat()));
            return "digitalitzacioIframeTancar";
        }
        model.addAttribute("digitalizacioFinalOk", getMessage(request, "document.digitalitzacio.estat.enum.FINAL_OK"));
        model.addAttribute("nomDocument", resposta.getNomDocument());
        return "digitalitzacioIframeTancar";
    }

    private DigitalitzacioResultat recuperaResultatEscaneig(String idTransaccio, boolean returnScannedFile, boolean returnSignedFile) {

        var resposta = digitalitzacioService.recuperarResultat(idTransaccio, returnScannedFile, returnSignedFile);
        return resposta;
    }

    @RequestMapping(value = "/descarregarResultat/{idTransaccio}", method = RequestMethod.GET)
    public void descarregarResultat(HttpServletRequest request, HttpServletResponse response, @PathVariable String idTransaccio, Model model) throws IOException {

        var returnScannedFile = (boolean) RequestSessionHelper.obtenirObjecteSessio(request, SESSION_ATTRIBUTE_RETURN_SCANNED);
        var returnSignedFile = (boolean) RequestSessionHelper.obtenirObjecteSessio(request, SESSION_ATTRIBUTE_RETURN_SIGNED);
        var resposta = digitalitzacioService.recuperarResultat(idTransaccio, returnScannedFile, returnSignedFile);
        writeFileToResponse(resposta.getNomDocument(), resposta.getContingut(), response);
    }

    @RequestMapping(value = "/tancarTransaccio/{idTransaccio}", method = RequestMethod.GET)
    @ResponseBody
    public void tancarTransaccio(HttpServletRequest request, @PathVariable String idTransaccio) throws IOException {

        RequestSessionHelper.esborrarObjecteSessio(request, SESSION_ATTRIBUTE_RETURN_SCANNED);
        RequestSessionHelper.esborrarObjecteSessio(request, SESSION_ATTRIBUTE_RETURN_SIGNED);
        RequestSessionHelper.esborrarObjecteSessio(request, SESSION_ATTRIBUTE_RETURN_IDTRANSACCIO);
        digitalitzacioService.tancarTransaccio(idTransaccio);
    }

}
