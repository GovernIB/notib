package es.caib.notib.back.controller;

import com.google.common.base.Strings;
import es.caib.notib.back.command.DocumentCommand;
import es.caib.notib.back.command.EntregapostalCommand;
import es.caib.notib.back.command.EnviamentCommand;
import es.caib.notib.back.command.NotificacioCommand;
import es.caib.notib.back.command.NotificacioFiltreCommand;
import es.caib.notib.back.command.OrganGestorFiltreCommand;
import es.caib.notib.back.command.PersonaCommand;
import es.caib.notib.back.helper.CaducitatHelper;
import es.caib.notib.back.helper.EnumHelper;
import es.caib.notib.back.helper.FileHelper;
import es.caib.notib.back.helper.MissatgesHelper;
import es.caib.notib.back.helper.RequestSessionHelper;
import es.caib.notib.back.helper.RolHelper;
import es.caib.notib.client.domini.DocumentTipus;
import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.client.domini.Idioma;
import es.caib.notib.client.domini.InteressatTipus;
import es.caib.notib.client.domini.NotificaDomiciliConcretTipus;
import es.caib.notib.client.domini.OrigenEnum;
import es.caib.notib.client.domini.ServeiTipus;
import es.caib.notib.client.domini.TipusDocumentalEnum;
import es.caib.notib.client.domini.ValidesaEnum;
import es.caib.notib.logic.intf.dto.CodiValorDto;
import es.caib.notib.logic.intf.dto.CodiValorOrganGestorComuDto;
import es.caib.notib.logic.intf.dto.DocCieValid;
import es.caib.notib.logic.intf.dto.DocumentDto;
import es.caib.notib.logic.intf.dto.DocumentValidacio;
import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.FirmaValid;
import es.caib.notib.logic.intf.dto.GrupDto;
import es.caib.notib.logic.intf.dto.LocalitatsDto;
import es.caib.notib.logic.intf.dto.OficinaDto;
import es.caib.notib.logic.intf.dto.PaisosDto;
import es.caib.notib.logic.intf.dto.PermisEnum;
import es.caib.notib.logic.intf.dto.ProvinciesDto;
import es.caib.notib.logic.intf.dto.RegistreDocumentacioFisicaEnumDto;
import es.caib.notib.logic.intf.dto.RespostaConsultaArxiuDto;
import es.caib.notib.logic.intf.dto.RolEnumDto;
import es.caib.notib.logic.intf.dto.TipusDocumentEnumDto;
import es.caib.notib.logic.intf.dto.cie.CieFormatFullaDto;
import es.caib.notib.logic.intf.dto.cie.CieFormatSobreDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioComunicacioTipusEnumDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerDto;
import es.caib.notib.logic.intf.service.AplicacioService;
import es.caib.notib.logic.intf.service.EntitatService;
import es.caib.notib.logic.intf.service.EnviamentSmService;
import es.caib.notib.logic.intf.service.GestioDocumentalService;
import es.caib.notib.logic.intf.service.GrupService;
import es.caib.notib.logic.intf.service.NotificacioService;
import es.caib.notib.logic.intf.service.OperadorPostalService;
import es.caib.notib.logic.intf.service.OrganGestorService;
import es.caib.notib.logic.intf.service.PagadorCieFormatFullaService;
import es.caib.notib.logic.intf.service.PagadorCieFormatSobreService;
import es.caib.notib.logic.intf.service.PagadorCieService;
import es.caib.notib.logic.intf.service.PermisosService;
import es.caib.notib.logic.intf.service.ProcedimentService;
import es.caib.notib.logic.intf.service.ServeiService;
import es.caib.notib.logic.intf.util.MimeUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Controlador per a la consulta i gestió de notificacions.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Controller
@RequestMapping("/notificacio")
public class NotificacioFormController extends BaseUserController {
    
    @Autowired
    private AplicacioService aplicacioService;
    @Autowired
    private NotificacioService notificacioService;
    @Autowired
    private EnviamentSmService enviamentSmService;
    @Autowired
    private EntitatService entitatService;
    @Autowired
    private ProcedimentService procedimentService;
    @Autowired
    private ServeiService serveiService;
    @Autowired
    private OrganGestorService organGestorService;
    @Autowired
    private GrupService grupService;
    @Autowired
    private PagadorCieFormatSobreService pagadorCieFormatSobreService;
    @Autowired
    private PagadorCieFormatFullaService pagadorCieFormatFullaService;
    @Autowired
    private GestioDocumentalService gestioDocumentalService;
    @Autowired
    private PermisosService permisosService;
    @Autowired
    private PagadorCieService pagadorCieService;
    @Autowired
    private OperadorPostalService operadorPostalService;


    DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
    private EnviamentTipus tipusEnviament;
    private static final String NOTIFICACIO_FORM = "notificacioForm";
    private static final String METADADES_ORIGEN = "metadades_origen";
    private static final String METADADES_VALIDESA = "metadades_validesa";
    private static final String METADADES_TIPO_DOCUMENTAL = "metadades_tipo_documental";
    private static final String METADADES_MODO_FIRMA = "metadades_modo_firma";
    private static final String EDIT_REFERER = "edit_referer";
    private static final String ENVIAMENT_TIPUS = "enviament_tipus";
    private static final String NOM_DOCUMENT = "nomDocument_";
    private static final String FALSE = "false";



    @GetMapping(value = "/new/notificacio")
    public String altaNotificacio(HttpServletRequest request, Model model) {

        initForm(request, model, EnviamentTipus.NOTIFICACIO);
        RequestSessionHelper.esborrarObjecteSessio(request, ENVIAMENT_TIPUS);
        return NOTIFICACIO_FORM;
    }

    @GetMapping(value = "/new/comunicacio")
    public String altaComunicacio(HttpServletRequest request, Model model) {

        initForm(request, model, EnviamentTipus.COMUNICACIO);
        RequestSessionHelper.esborrarObjecteSessio(request, ENVIAMENT_TIPUS);
        return NOTIFICACIO_FORM;
    }

    @GetMapping(value = "/new/comunicacioSIR")
    public String altaComunicacioSIR(HttpServletRequest request, Model model) {

        initForm(request, model, EnviamentTipus.SIR);
        RequestSessionHelper.actualitzarObjecteSessio(request, ENVIAMENT_TIPUS, EnviamentTipus.SIR);
        return NOTIFICACIO_FORM;
    }

    private void initForm(HttpServletRequest request, Model model, EnviamentTipus tipusEnviament) {

        var referer = request.getHeader("Referer");
        RequestSessionHelper.actualitzarObjecteSessio(request, EDIT_REFERER, referer);
        var entitatActual = sessionScopedContext.getEntitatActual();
        var notificacioCommand = new NotificacioCommand();
        List<EnviamentCommand> enviaments = new ArrayList<>();
        var enviament = new EnviamentCommand();
        var entregaPostal = new EntregapostalCommand();
        entregaPostal.setPaisCodi("ES");
        entregaPostal.setProvincia("07");
        enviament.setEntregaPostal(entregaPostal);
        enviaments.add(enviament);
        notificacioCommand.setEnviaments(enviaments);
        notificacioCommand.setCaducitat(CaducitatHelper.sumarDiesNaturals(10));
        notificacioCommand.setCaducitatDiesNaturals(10);
        var tipusDocumentDefault = entitatService.findTipusDocumentDefaultByEntitat(entitatActual.getId());
        if (tipusDocumentDefault != null) {
            for(var i = 0; i < 5; i++) {
                notificacioCommand.setTipusDocumentDefault(i, tipusDocumentDefault.name());
            }
        }
        for(var i = 0; i < 5; i++) {
            notificacioCommand.getDocuments()[i] = new DocumentCommand();
            notificacioCommand.getDocuments()[i].setOrigen(OrigenEnum.ADMINISTRACIO);
            notificacioCommand.getDocuments()[i].setValidesa(ValidesaEnum.ORIGINAL);
            notificacioCommand.getDocuments()[i].setTipoDocumental(TipusDocumentalEnum.ALTRES);
        }
        this.tipusEnviament = tipusEnviament;
        notificacioCommand.setEnviamentTipus(tipusEnviament);
        emplenarModelNotificacio(request, model, notificacioCommand);
    }

    @GetMapping(value = "/organ/{organId}/procediments")
    @ResponseBody
    public List<CodiValorOrganGestorComuDto> getProcedimentsOrgan(HttpServletRequest request, @PathVariable String organId) {

        var entitatActual = sessionScopedContext.getEntitatActual();
        var rol = RolEnumDto.valueOf(sessionScopedContext.getRolActual());
        return procedimentService.getProcedimentsOrganNotificables(entitatActual.getId(), organId.equals("-") ? null : organId, rol, tipusEnviament);
    }

    @GetMapping(value = "/organ/{organId}/serveis")
    @ResponseBody
    public List<CodiValorOrganGestorComuDto> getServeisOrgan(HttpServletRequest request, @PathVariable String organId) {

        var enviamentTipus = (EnviamentTipus) RequestSessionHelper.obtenirObjecteSessio(request, ENVIAMENT_TIPUS);
        var entitatActual = sessionScopedContext.getEntitatActual();
        var rol = RolEnumDto.valueOf(sessionScopedContext.getRolActual());
        return serveiService.getServeisOrganNotificables(entitatActual.getId(), organId.equals("-") ? null : organId, rol, enviamentTipus);
    }

    @GetMapping(value = "/cercaUnitats")
    @ResponseBody
    public List<OrganGestorDto> getAdministracions(
            HttpServletRequest request,
            @RequestParam(value = "codi", required = false) String codi,
            @RequestParam(value = "denominacio", required = false) String denominacio,
            @RequestParam(value = "nivellAdministracio", required = false) Long nivellAdministracio,
            @RequestParam(value = "comunitatAutonoma", required = false) Long comunitatAutonoma,
            @RequestParam(value = "provincia", required = false) Long provincia,
            @RequestParam(value = "municipi", required = false) String municipi,
            Model model) {

        var codiNull = Strings.isNullOrEmpty(codi);
        var denominacioNull = Strings.isNullOrEmpty(denominacio);
        if (codiNull && denominacioNull) {
            return new ArrayList<>();
        }
        try {
            return notificacioService.cercaUnitats(codi, denominacio, nivellAdministracio, comunitatAutonoma, null, null, provincia, municipi);
        } catch (Exception ex) {
            log.error("Error obtinguent les unitats codi " + codi + " denominacio: " + denominacio + ", nivellAdministracio: " + nivellAdministracio +
                    ", comunitatAutonoma: " + comunitatAutonoma + ", provincia: " + provincia + ", municipi: " + municipi, ex);
            throw new RuntimeException(ex.getMessage());
        }
    }

    @GetMapping(value = "/administracions/codi/{codi}")
    @ResponseBody
    public List<OrganGestorDto> getAdministracionsPerCodi(HttpServletRequest request, @PathVariable String codi, Model model) {
        return notificacioService.unitatsPerCodi(codi);

    }

    @GetMapping(value = "/administracions/denominacio/{denominacio}")
    @ResponseBody
    public List<OrganGestorDto> getAdministracionsPerDenominacio(HttpServletRequest request, @PathVariable String denominacio, Model model) {
        return notificacioService.unitatsPerDenominacio(denominacio);
    }

    @GetMapping(value = "/new/destinatari")
    public PersonaCommand altaDestinatari(HttpServletRequest request, Model model) {
        return new PersonaCommand();
    }

    @ResponseBody
    @GetMapping(value = "/organ/oficina/{organCodi}")
    public OficinaDto getOficina(HttpServletRequest request, Model model, @PathVariable String organCodi) {

        var entitat = getEntitatActualComprovantPermisos(request);
        try {
            var o = organGestorService.findByCodi(entitat.getId(), organCodi);
            return o == null ? null : o.getOficina();
        } catch (Exception ex) {
            log.error("Error obtinguent la oficina de l'órgan " + organCodi, ex);
            return null;
        }
    }

    @PostMapping(value = "/newOrModify")
    public String save(HttpServletRequest request, @Valid NotificacioCommand notificacioCommand, BindingResult bindingResult, Model model) throws IOException {

        log.debug("[NOT-CONTROLLER] POST notificació desde interfície web. ");
        List<String> tipusDocumentEnumDto = new ArrayList<>();
        var entitatActual = sessionScopedContext.getEntitatActual();
        ProcSerDto procedimentActual = null;
        var property = aplicacioService.propertyGetByEntitat("es.caib.notib.comunicacions.sir.internes", FALSE);
        model.addAttribute("isPermesComunicacionsSirPropiaEntitat", property);
        if (notificacioCommand.getProcedimentId() != null) {
            procedimentActual = procedimentService.findById(entitatActual.getId(), isAdministrador(), notificacioCommand.getProcedimentId());
        }
        notificacioCommand.setUsuariCodi(getCodiUsuariActual());
        if (bindingResult.hasErrors()) {
            var msg = EnviamentTipus.NOTIFICACIO.equals(notificacioCommand.getEnviamentTipus()) ?
                    "notificacio.form.errors.validacio.notificacio" : "notificacio.form.errors.validacio.comunicacio";
            MissatgesHelper.error(request, getMessage(request, msg));
            relooadForm(request, notificacioCommand, bindingResult, model, tipusDocumentEnumDto, entitatActual, procedimentActual);
            return NOTIFICACIO_FORM;
        }

        if (RolHelper.isUsuariActualAdministrador(sessionScopedContext.getRolActual())) {
            model.addAttribute("entitat", entitatService.findAll());
        }
        model.addAttribute(new NotificacioFiltreCommand());
        model.addAttribute(new OrganGestorFiltreCommand());

        try {
            log.debug("[NOT-CONTROLLER] POST notificació desde interfície web. Processant dades del formulari. ");
            updateDocuments(notificacioCommand, bindingResult);
            if (bindingResult.hasErrors()) {
                relooadForm(request, notificacioCommand, bindingResult, model, tipusDocumentEnumDto, entitatActual, procedimentActual);
                return NOTIFICACIO_FORM;
            }
            if (notificacioCommand.getId() != null) {
                notificacioService.update(entitatActual.getId(), notificacioCommand.asNotificacioV2(), RolHelper.isUsuariActualAdministradorEntitat(sessionScopedContext.getRolActual()));
            } else {
                var not = notificacioService.create(entitatActual.getId(), notificacioCommand.asNotificacioV2());
                // SM
                not.getEnviaments().forEach(e -> enviamentSmService.altaEnviament(e.getNotificaReferencia()));

            }
        } catch (Exception ex) {
            log.error("[NOT-CONTROLLER] POST notificació desde interfície web. Excepció al processar les dades del formulari", ex);
            log.error(ExceptionUtils.getStackTrace(ex));
            MissatgesHelper.error(request, ex.getMessage());
            relooadForm(request, notificacioCommand, bindingResult, model, tipusDocumentEnumDto, entitatActual, procedimentActual);
            model.addAttribute("notificacioCommandV2", notificacioCommand);
            return NOTIFICACIO_FORM;
        }
        log.debug("[NOT-CONTROLLER] POST notificació desde interfície web. Formulari processat satisfactoriament. ");
        return "redirect:../notificacio";
    }

    private void relooadForm(HttpServletRequest request, NotificacioCommand notificacioCommand, BindingResult bindingResult, Model model, List<String> tipusDocumentEnumDto, EntitatDto entitatActual, ProcSerDto procedimentActual) {

        log.debug("[NOT-CONTROLLER] POST notificació desde interfície web. Errors de validació formulari. ");
        ompliModelFormulari(request, procedimentActual, entitatActual, notificacioCommand, bindingResult, tipusDocumentEnumDto, model);
        for (var error: bindingResult.getAllErrors()) {
            log.debug("[NOT-CONTROLLER] POST notificació desde interfície web. Error formulari: " + error.toString());
        }
        model.addAttribute(notificacioCommand);
        emplenarModelNotificacio(request, model, notificacioCommand);
    }

    private void updateDocuments(NotificacioCommand notificacioCommand, BindingResult bindingResult) throws IOException {

        for (var i = 0; i < 5; i++) {

            if (notificacioCommand.getTipusDocument()[i] == null) {
                notificacioCommand.getDocuments()[i] = null;
                continue;
            }
            var arxiuGestdocId = notificacioCommand.getDocuments()[i].getArxiuGestdocId();
            switch (notificacioCommand.getTipusDocument()[i]) {
                case ARXIU:
                    if (notificacioCommand.getArxiu()[i] != null && !notificacioCommand.getArxiu()[i].isEmpty()) {
                        var contingut = notificacioCommand.getArxiu()[i].getBytes();
                        var contentType = notificacioCommand.getArxiu()[i].getContentType();
                        var nom = notificacioCommand.getArxiu()[i].getOriginalFilename();
                        notificacioCommand.getDocuments()[i].setArxiuNom(nom);
                        var contingutBase64 = Base64.encodeBase64String(contingut);
                        notificacioCommand.getDocuments()[i].setContingutBase64(contingutBase64);
                        notificacioCommand.getDocuments()[i].setMediaType(contentType);
                        notificacioCommand.getDocuments()[i].setMida(notificacioCommand.getArxiu()[i].getSize());
                        validaFirma(nom, contentType, bindingResult, i, contingut);
                        break;
                    }
                    if (notificacioCommand.getArxiu()[i].isEmpty() && arxiuGestdocId != null) {
                        var result = notificacioCommand.getId() != null ?
                                gestioDocumentalService.obtenirArxiuNotificacio(arxiuGestdocId) :
                                gestioDocumentalService.obtenirArxiuTemporal(arxiuGestdocId, MimeUtils.isZipFileByMimeType(notificacioCommand.getDocuments()[i].getMediaType()));

                        var contingutBase64 = Base64.encodeBase64String(result);
                        notificacioCommand.getDocuments()[i].setContingutBase64(contingutBase64);
                        validaFirma(notificacioCommand.getDocuments()[i].getArxiuNom(), notificacioCommand.getDocuments()[i].getMediaType(), bindingResult, i, result);
                        break;
                    }
                    notificacioCommand.getDocuments()[i] = null;
                    break;
                case CSV:
                    if (notificacioCommand.getDocumentArxiuCsv()[i] != null && !notificacioCommand.getDocumentArxiuCsv()[i].isEmpty()) {
                        notificacioCommand.getDocuments()[i].setCsv(notificacioCommand.getDocumentArxiuCsv()[i]);
                        break;
                    }
                    notificacioCommand.getDocuments()[i] = null;
                    break;
                case UUID:
                    if (notificacioCommand.getDocumentArxiuUuid()[i] != null && !notificacioCommand.getDocumentArxiuUuid()[i].isEmpty()) {
                        notificacioCommand.getDocuments()[i].setUuid(notificacioCommand.getDocumentArxiuUuid()[i]);
                        break;
                    }
                    notificacioCommand.getDocuments()[i] = null;
                    break;
            }
        }
    }

    @PostMapping(value = "/valida/document/{entregaPostal}")
    @ResponseBody
    public DocumentValidacio validaDocument(@RequestParam(value = "fitxer") MultipartFile fitxer, @PathVariable String entregaPostal,
                                            @RequestParam(required = false) Long procedimentId, @RequestParam(required = false) String organCodi) throws IOException {

        var nom = fitxer.getOriginalFilename();
        var content = fitxer.getBytes();
        var contentType = fitxer.getContentType();
        var contingutBase64 = Base64.encodeBase64String(content);
        var firma = FirmaValid.builder().nom(nom).mida(fitxer.getSize()).mediaType(fitxer.getContentType()).build();
        if (!FileHelper.isPdf(contingutBase64)) {
            return DocumentValidacio.builder().validacioFirma(firma).build();
        }
        var signatureInfo = notificacioService.checkIfSignedAttached(content, nom, contentType);
        firma.setSigned(signatureInfo.isSigned());
        firma.setError(signatureInfo.isError());
        firma.setErrorMsg(signatureInfo.getErrorMsg());

        if (!Boolean.valueOf(entregaPostal)) {
            return DocumentValidacio.builder().validacioFirma(firma).build();
        }
        // si es entrega postal
        if (procedimentId != null && !procedimentService.procedimentAmbCieExtern(procedimentId, organCodi)) {
            return DocumentValidacio.builder().validacioFirma(firma).build();
        }
        var cieValid = notificacioService.validateDocCIE(content);
        return DocumentValidacio.builder().validacioFirma(firma).validacioCie(cieValid).build();
    }

    @PostMapping(value = "/valida/entrega/postal/{procedimentId}/{organCodi}")
    @ResponseBody
    public DocCieValid validaFirmaDocument(@RequestParam(value = "fitxer") MultipartFile fitxer, @PathVariable Long procedimentId, @PathVariable String organCodi) throws IOException {

        return procedimentService.procedimentAmbCieExtern(procedimentId, organCodi) ? notificacioService.validateDocCIE(fitxer.getBytes())
                : DocCieValid.builder().errorsCie(new ArrayList<>()).build();
    }

    private void validaFirma(String nom, String mediaType, BindingResult bindingResult, int position, byte[] content) {

        var contingutBase64 = Base64.encodeBase64String(content);
        if (!FileHelper.isPdf(contingutBase64) || !isValidaFirmaWebEnabled()) {
            return;
        }
        var signatureInfoDto = notificacioService.checkIfSignedAttached(content, nom, mediaType);
        if (!signatureInfoDto.isError()) {
            return;
        }
        var codes = bindingResult.resolveMessageCodes("notificacio.form.valid.document.firma", "arxiu[" + position + "]");
        bindingResult.addError(new FieldError(bindingResult.getObjectName(), "arxiu[" + position + "]", "", true, codes, null, "La firma del document no és vàlida"));
    }

    @GetMapping(value = "/{notificacioId}/edit")
    public String editar(HttpServletRequest request, Model model, @PathVariable Long notificacioId) {

        var referer = request.getHeader("Referer");
        RequestSessionHelper.actualitzarObjecteSessio(request, EDIT_REFERER, referer);
        var notificacioDto = notificacioService.findAmbId(notificacioId, false);
        var notificacioCommand = NotificacioCommand.asCommand(notificacioDto);
        for(var i = 0; i < 5; i++) {
            if (notificacioCommand.getDocuments()[i].getArxiuNom() != null) {
                model.addAttribute(NOM_DOCUMENT + i, notificacioCommand.getDocuments()[i].getArxiuNom());
                notificacioCommand.setTipusDocumentDefault(i, TipusDocumentEnumDto.ARXIU.name());
            }
            if (notificacioCommand.getDocuments()[i].getUuid() != null) {
                model.addAttribute(NOM_DOCUMENT + i, notificacioCommand.getDocuments()[i].getUuid());
                notificacioCommand.setTipusDocumentDefault(i, TipusDocumentEnumDto.UUID.name());
            }
            if (notificacioCommand.getDocuments()[i].getCsv() != null) {
                model.addAttribute(NOM_DOCUMENT + i, notificacioCommand.getDocuments()[i].getCsv());
                notificacioCommand.setTipusDocumentDefault(i, TipusDocumentEnumDto.CSV.name());
            }
        }
        emplenarModelNotificacio(request, model, notificacioCommand);
        return NOTIFICACIO_FORM;
    }

    @GetMapping(value = "/nivellsAdministracions")
    @ResponseBody
    public List<CodiValorDto> getNivellsAdministracions(HttpServletRequest request, Model model) {
        return notificacioService.llistarNivellsAdministracions();
    }


    @GetMapping(value = "/comunitatsAutonomes")
    @ResponseBody
    public List<CodiValorDto> getComunitatsAutonomess(HttpServletRequest request, Model model) {
        return notificacioService.llistarComunitatsAutonomes();
    }

    @GetMapping(value = "/provincies/{codiCA}")
    @ResponseBody
    public List<ProvinciesDto> getProvinciesPerCA(HttpServletRequest request, Model model, @PathVariable String codiCA) {
        return notificacioService.llistarProvincies(codiCA);
    }

    @GetMapping(value = "/procediment/{procedimentId}/dades")
    @ResponseBody
    public DadesProcediment getDadesProcSer(HttpServletRequest request, @PathVariable Long procedimentId, @RequestParam(required = false) String organCodi) {

        var entitatActual = sessionScopedContext.getEntitatActual();
        var procedimentActual = procedimentService.findById(entitatActual.getId(),false, procedimentId);
        var enviamentTipus = (EnviamentTipus) RequestSessionHelper.obtenirObjecteSessio(request, ENVIAMENT_TIPUS);
        var dadesProcediment = new DadesProcediment();
        dadesProcediment.setOrganCodi(procedimentActual.getOrganGestor());
        dadesProcediment.setCaducitat(CaducitatHelper.sumarDiesNaturals(procedimentActual.getCaducitat()));
        dadesProcediment.setCaducitatDiesNaturals(procedimentActual.getCaducitat());
        dadesProcediment.setRetard(procedimentActual.getRetard());
        dadesProcediment.setAgrupable(procedimentActual.isAgrupar());
        if (procedimentActual.isAgrupar()) {
            dadesProcediment.setGrups(grupService.findByProcedimentAndUsuariGrups(procedimentId));
        }
        if (procedimentActual.isEntregaCieActiva()) {
            dadesProcediment.setFormatsSobre(pagadorCieFormatSobreService.findFormatSobreByPagadorCie(procedimentActual.getCieId()));
            dadesProcediment.setFormatsFulla(pagadorCieFormatFullaService.findFormatFullaByPagadorCie(procedimentActual.getCieId()));
        }
        dadesProcediment.setComu(procedimentActual.isComu());
        dadesProcediment.setEntregaCieActiva(procedimentActual.isEntregaCieActivaAlgunNivell());
        dadesProcediment.setEntregaCieVigent(procedimentActual.isEntregaCieVigent());
        if (!procedimentActual.isComu()) {
            return dadesProcediment;
        }
            // Obtenim òrgans seleccionables
        var permis = EnviamentTipus.SIR.equals(enviamentTipus) ? PermisEnum.COMUNICACIO_SIR :
                EnviamentTipus.COMUNICACIO.equals(enviamentTipus) ? PermisEnum.COMUNICACIO : PermisEnum.NOTIFICACIO;
        var organsDisponibles = permisosService.getOrgansCodisAmbPermisPerProcedimentComu(entitatActual.getId(), getCodiUsuariActual(), permis, procedimentActual);
        dadesProcediment.setOrgansDisponibles(organsDisponibles);
        if (!organsDisponibles.contains(organCodi)) {
            return dadesProcediment;
        }
        // Mirar si organ seleccionat te entrega postal actvia
        var organ = organGestorService.findByCodi(entitatActual.getId(), organCodi);
//        var pagadorsCie = pagadorCieService.findNoCaducatsByEntitatAndOrgan(entitatActual, organCodi, false);
//        var pagadorsPostal = operadorPostalService.findNoCaducatsByEntitatAndOrgan(entitatActual, organCodi, false);
//        var cieActiuPerPare = (pagadorsCie == null || !pagadorsCie.isEmpty()) && (pagadorsPostal == null || !pagadorsPostal.isEmpty());
        var cieActiuPerPare = organGestorService.entregaCieActiva(entitatActual, organCodi);
//        dadesProcediment.setEntregaCieActiva(organ.isEntregaCieActiva() || cieActiuPerPare);
        dadesProcediment.setEntregaCieActiva(cieActiuPerPare);
        return dadesProcediment;
    }

    @GetMapping(value = "/paisos")
    @ResponseBody
    public List<PaisosDto> getPaisos(HttpServletRequest request, Model model) {
        return notificacioService.llistarPaisos();
    }

    @GetMapping(value = "/provincies")
    @ResponseBody
    public List<ProvinciesDto> getProvincies(HttpServletRequest request, Model model) {
        return notificacioService.llistarProvincies();
    }

    @GetMapping(value = "/localitats/{provinciaId}")
    @ResponseBody
    public List<LocalitatsDto> getLocalitats(HttpServletRequest request, Model model, @PathVariable String provinciaId) {
        return notificacioService.llistarLocalitats(provinciaId);
    }

    @PostMapping(value = "/consultaDocumentIMetadadesCsv/consulta", headers="Content-Type=application/json")
    @ResponseBody
    public RespostaConsultaArxiuDto consultaDocumentIMetadadesCsv(HttpServletRequest request, @RequestBody String csv) {

        DocumentDto doc = null;
        var validacioIdCsv = notificacioService.validarIdCsv(csv);
        if (validacioIdCsv) {
            doc = notificacioService.consultaDocumentIMetadades(csv, false);
        }
        return prepararResposta(validacioIdCsv, doc, request);
    }

    @PostMapping(value = "/consultaDocumentIMetadadesUuid/consulta", headers="Content-Type=application/json" )
    @ResponseBody
    public RespostaConsultaArxiuDto consultaDocumentIMetadadesUuid(HttpServletRequest request, @RequestBody String uuid) {

        var doc = notificacioService.consultaDocumentIMetadades(uuid, true);
        return prepararResposta(true, doc, request);
    }

    private RespostaConsultaArxiuDto prepararResposta(boolean validacio, DocumentDto doc, HttpServletRequest request) {

        var teMetadades = false;
        if (!validacio && doc == null) {
            return RespostaConsultaArxiuDto.builder().validacioIdCsv(false).documentExistent(false)
                    .metadadesExistents(teMetadades).origen(null).validesa(null).tipoDocumental(null).modoFirma(null).build();
        }
        if (doc == null) {
            return RespostaConsultaArxiuDto.builder().validacioIdCsv(true).documentExistent(false)
                    .metadadesExistents(teMetadades).origen(null).validesa(null).tipoDocumental(null).modoFirma(null).build();
        }
        teMetadades = doc.getOrigen() != null || doc.getValidesa() != null || doc.getTipoDocumental() != null || doc.getModoFirma() != null;
        if (teMetadades) {
            //Es guarden en sessió
            RequestSessionHelper.actualitzarObjecteSessio(request, METADADES_ORIGEN, doc.getOrigen());
            RequestSessionHelper.actualitzarObjecteSessio(request, METADADES_VALIDESA, doc.getValidesa());
            RequestSessionHelper.actualitzarObjecteSessio(request, METADADES_TIPO_DOCUMENTAL, doc.getTipoDocumental());
            RequestSessionHelper.actualitzarObjecteSessio(request, METADADES_MODO_FIRMA, doc.getModoFirma());
        }
        return RespostaConsultaArxiuDto.builder().validacioIdCsv(validacio).documentExistent(true)
                .metadadesExistents(teMetadades).origen(doc.getOrigen()).validesa(doc.getValidesa())
                .tipoDocumental(doc.getTipoDocumental()).modoFirma(doc.getModoFirma()).build();
    }

    @GetMapping(value = "/caducitatDiesNaturals/{dia}/{mes}/{any}")
    @ResponseBody
    public long getDiesCaducitat(@PathVariable String dia, @PathVariable String mes, @PathVariable String any) throws ParseException {
        return CaducitatHelper.getDiesEntreDates(df.parse(dia + "/" + mes + "/" + any));
    }

    @GetMapping(value = "/caducitatData/{dies}")
    @ResponseBody
    public String getDataCaducitat(@PathVariable int dies) {
        return df.format(CaducitatHelper.sumarDiesNaturals(dies));
    }

    private void emplenarModelNotificacio(HttpServletRequest request, Model model, NotificacioCommand notificacioCommand) {

        var entitatActual = sessionScopedContext.getEntitatActual();
        List<String> tipusDocumentEnumDto = new ArrayList<>();
        var tipusDocuments = entitatService.findTipusDocumentByEntitat(entitatActual.getId());
        if (tipusDocuments != null) {
            for (var tipusDocument : tipusDocuments) {
                tipusDocumentEnumDto.add(tipusDocument.getTipusDocEnum().name());
            }
        }
        model.addAttribute("tipusDocumentEnumDto", tipusDocumentEnumDto);
        model.addAttribute("entitat", entitatActual);
        model.addAttribute(notificacioCommand);
        fillNotificacioModel(request, entitatActual, model, notificacioCommand.getEnviamentTipus());
        model.addAttribute("amagat", Boolean.FALSE);
        try {
            model.addAttribute("concepteSize", notificacioCommand.getConcepteDefaultSize());
            model.addAttribute("descripcioSize", notificacioCommand.getDescripcioDefaultSize());
            model.addAttribute("nomSize", notificacioCommand.getNomDefaultSize());
            model.addAttribute( "raoSocialSize", notificacioCommand.getRaoSocialDefaultsize());
            model.addAttribute("llinatge1Size", notificacioCommand.getLlinatge1DefaultSize());
            model.addAttribute("llinatge2Size", notificacioCommand.getLlinatge2DefaultSize());
            model.addAttribute("emailSize", notificacioCommand.getEmailDefaultSize());
            model.addAttribute("telefonSize", notificacioCommand.getTelefonDefaultSize());
        } catch (Exception ex) {
            log.error("No s'ha pogut recuperar la longitud del concepte: " + ex.getMessage());
        }
        var referer = (String) RequestSessionHelper.obtenirObjecteSessio(request, EDIT_REFERER);
        model.addAttribute("referer", referer);
        model.addAttribute("validaFirmaWebEnabled", isValidaFirmaWebEnabled());
        model.addAttribute("isPermesComunicacionsSirPropiaEntitat", aplicacioService.propertyGetByEntitat("es.caib.notib.comunicacions.sir.internes", FALSE));
        model.addAttribute("docMaxSize", Long.parseLong(aplicacioService.propertyGet("es.caib.notib.notificacio.document.size", "10485760")));

        long maxFileSize = Long.parseLong(aplicacioService.propertyGet("es.caib.notib.notificacio.document.size"));
        long maxFilesSize = Long.parseLong(aplicacioService.propertyGet("es.caib.notib.notificacio.document.total.size"));

        model.addAttribute("maxFileSize", maxFileSize/(1024*1024));
        model.addAttribute("maxFilesSize", maxFilesSize/(1024*1024));
    }

    private void ompliModelFormulari(HttpServletRequest request, ProcSerDto procedimentActual, EntitatDto entitatActual, NotificacioCommand notificacioCommand,
                                    BindingResult bindingResult, List<String> tipusDocumentEnumDto, Model model) {

        var tipusDocuments = entitatService.findTipusDocumentByEntitat(entitatActual.getId());
        var tipusDocumentDefault = entitatService.findTipusDocumentDefaultByEntitat(entitatActual.getId());
        if (tipusDocuments != null) {
            for (var tipusDocument : tipusDocuments) {
                tipusDocumentEnumDto.add(tipusDocument.getTipusDocEnum().name());
            }
            if (tipusDocumentDefault != null) {
                for (var i = 0; i < 5; i++) {
                    notificacioCommand.setTipusDocumentDefault(i, tipusDocumentDefault.name());
                }
            }
        }
        model.addAttribute("tipusDocumentEnumDto", tipusDocumentEnumDto);
        model.addAttribute("dir3Codi", entitatActual.getId());
        fillNotificacioModel(request, entitatActual, model, notificacioCommand.getEnviamentTipus());
        if (procedimentActual != null) {
            model.addAttribute("grups", grupService.findByProcedimentAndUsuariGrups(procedimentActual.getId()));
        }
        model.addAttribute("enviosGuardats", notificacioCommand.getEnviaments());
        model.addAttribute("tipusDocument", notificacioCommand.getTipusDocument());
        if(procedimentActual != null) {
            model.addAttribute("procedimentId", procedimentActual.getId());
        }
        model.addAttribute("errors", bindingResult.getAllErrors());
        DocumentCommand documentCommand;
        for (var i = 0; i < 5; i++) {
            documentCommand = notificacioCommand.getDocuments()[i];
            if (documentCommand == null) {
                continue;
            }
            if (documentCommand.getArxiuGestdocId().isEmpty() && notificacioCommand.getTipusDocument()[i] != null &&
                (notificacioCommand.getArxiu()[i] != null && !notificacioCommand.getArxiu()[i].isEmpty()) &&  notificacioCommand.getTipusDocument()[i] == TipusDocumentEnumDto.ARXIU) {

                String arxiuGestdocId = null;
                String contingutBase64 = null;
                try {
                    contingutBase64 = Base64.encodeBase64String(notificacioCommand.getArxiu()[i].getBytes());
                } catch (Exception ex) {
                    log.error("No s'ha pogut codificar els bytes de l'arxiu: " + ex.getMessage());
                }
                documentCommand.setContingutBase64(contingutBase64);
                documentCommand.setArxiuNom(notificacioCommand.getArxiu()[i].getOriginalFilename());
                documentCommand.setNormalitzat(notificacioCommand.getDocuments()[i].isNormalitzat());
                documentCommand.setMediaType(notificacioCommand.getArxiu()[i].getContentType());
                documentCommand.setMida(notificacioCommand.getArxiu()[i].getSize());
                arxiuGestdocId = gestioDocumentalService.guardarArxiuTemporal(documentCommand.getContingutBase64());
                documentCommand.setArxiuGestdocId(arxiuGestdocId);
                model.addAttribute(NOM_DOCUMENT + i, notificacioCommand.getArxiu()[i].getOriginalFilename());
            } else if (documentCommand.getArxiuNom() != null && !documentCommand.getArxiuNom().isEmpty()) {
                model.addAttribute(NOM_DOCUMENT + i, documentCommand.getArxiuNom());
            } else if (notificacioCommand.getArxiu()[i] != null) {
                model.addAttribute(NOM_DOCUMENT + i, notificacioCommand.getArxiu()[i].getOriginalFilename());
            }
        }
        model.addAttribute("document", notificacioCommand.getDocuments());
        try {
            model.addAttribute("concepteSize", notificacioCommand.getConcepteDefaultSize() );
            model.addAttribute("descripcioSize", notificacioCommand.getDescripcioDefaultSize());
        } catch (Exception ex) {
            log.error("No s'ha pogut recuperar la longitud del concepte: " + ex.getMessage());
        }
        var referer = (String) RequestSessionHelper.obtenirObjecteSessio(request, EDIT_REFERER);
        model.addAttribute("referer", referer);
    }

    private void fillNotificacioModel(HttpServletRequest request, EntitatDto entitatActual, Model model, EnviamentTipus tipusEnviament) {

        var rol = RolEnumDto.valueOf(sessionScopedContext.getRolActual());
        String organFiltreProcs = null;
        if (RolEnumDto.NOT_ADMIN_ORGAN.equals(rol)) {
            organFiltreProcs = getOrganGestorActual(request).getCodi();
        }
        List<CodiValorOrganGestorComuDto> procedimentsDisponibles = procedimentService.getProcedimentsOrganNotificables(entitatActual.getId(), organFiltreProcs, rol, tipusEnviament);
        List<CodiValorOrganGestorComuDto> serveisDisponibles = serveiService.getServeisOrganNotificables(entitatActual.getId(), organFiltreProcs, rol, tipusEnviament);
        List<CodiValorOrganGestorComuDto> procSerDisponibles = new ArrayList<>();
        procSerDisponibles.addAll(procedimentsDisponibles);
        procSerDisponibles.addAll(serveisDisponibles);
        List<OrganGestorDto> organsGestors  = null;
        List<CodiValorDto> codisValor = new ArrayList<>();
        if (RolEnumDto.NOT_ADMIN.equals(rol)) {
            organsGestors = organGestorService.findByEntitat(entitatActual.getId());
        } else if (RolEnumDto.NOT_ADMIN_ORGAN.equals(rol)) {
            var organGestorActual = getOrganGestorActual(request);
            organsGestors = organGestorService.findDescencentsByCodi(entitatActual.getId(), organGestorActual.getCodi());
        } else { // Rol usuari o altres
            var permis = tipusEnviament.equals(EnviamentTipus.SIR) ? PermisEnum.COMUNICACIO_SIR :
                                tipusEnviament.equals(EnviamentTipus.COMUNICACIO) ? PermisEnum.COMUNICACIO : PermisEnum.NOTIFICACIO;
            codisValor = permisosService.getOrgansAmbPermis(entitatActual.getId(), SecurityContextHolder.getContext().getAuthentication().getName(), permis);
        }
        if (procSerDisponibles.isEmpty() && !procedimentService.hasProcedimentsComunsAndNotificacioPermission(entitatActual.getId(), tipusEnviament)) {
            MissatgesHelper.warning(request, getMessage(request, "notificacio.controller.sense.permis.procediments"));
        }
        if (organsGestors != null) {
            for (var o : organsGestors) {
                codisValor.add(CodiValorDto.builder().codi(o.getCodi()).valor(o.getCodi() + " " + o.getCodiNom()).build());
            }
        }
        if (codisValor == null || codisValor.isEmpty()) {
            MissatgesHelper.warning(request, getMessage(request, "notificacio.controller.sense.permis.organs"));
        }
        model.addAttribute("organsGestors", codisValor);
        model.addAttribute("procediments", procedimentsDisponibles);
        model.addAttribute("serveis", serveisDisponibles);
        model.addAttribute("isTitularAmbIncapacitat", aplicacioService.propertyGetByEntitat("es.caib.notib.titular.incapacitat", "true"));
        model.addAttribute("isMultiplesDestinataris", aplicacioService.propertyGetByEntitat("es.caib.notib.destinatari.multiple", FALSE));
        model.addAttribute("ambEntregaDeh", entitatActual.isAmbEntregaDeh());
        model.addAttribute("comunicacioTipus", EnumHelper.getOptionsForEnum(NotificacioComunicacioTipusEnumDto.class,"es.caib.notib.logic.intf.dto.notificacio.NotificacioComunicacioTipusEnumDto."));
        model.addAttribute("enviamentTipus",EnumHelper.getOptionsForEnum(EnviamentTipus.class, "notificacio.tipus.enviament.enum."));
        model.addAttribute("serveiTipus", EnumHelper.getOptionsForEnum(ServeiTipus.class,"es.caib.notib.logic.intf.dto.NotificaServeiTipusEnumDto."));
        Enum<?>[] interessatsTipus;
        Enum<?>[] interessatsTipusDest;
        if (EnviamentTipus.SIR.equals(tipusEnviament)) {
            interessatsTipus = new Enum<?>[]{ InteressatTipus.ADMINISTRACIO };
            interessatsTipusDest = new Enum<?>[]{ InteressatTipus.ADMINISTRACIO };
        } else if (EnviamentTipus.COMUNICACIO.equals(tipusEnviament)) {
            interessatsTipus = new Enum<?>[]{ InteressatTipus.FISICA, InteressatTipus.FISICA_SENSE_NIF, InteressatTipus.JURIDICA, };
            interessatsTipusDest = new Enum<?>[]{ InteressatTipus.FISICA, InteressatTipus.JURIDICA };
        } else {
            interessatsTipus = new Enum<?>[]{ InteressatTipus.FISICA, InteressatTipus.FISICA_SENSE_NIF, InteressatTipus.ADMINISTRACIO, InteressatTipus.JURIDICA };
            interessatsTipusDest = new Enum<?>[]{ InteressatTipus.FISICA, InteressatTipus.JURIDICA };
        }

        model.addAttribute("interessatTipus", EnumHelper.getOrderedOptionsForEnum(InteressatTipus.class,"es.caib.notib.logic.intf.dto.InteressatTipus.", interessatsTipus));
        model.addAttribute("interessatTipusDest", EnumHelper.getOrderedOptionsForEnum(InteressatTipus.class,"es.caib.notib.logic.intf.dto.InteressatTipus.", interessatsTipusDest));
        model.addAttribute("entregaPostalTipus", EnumHelper.getOptionsForEnum(NotificaDomiciliConcretTipus.class,"es.caib.notib.logic.intf.dto.NotificaDomiciliConcretTipus."));
        model.addAttribute("registreDocumentacioFisica", EnumHelper.getOptionsForEnum(RegistreDocumentacioFisicaEnumDto.class,"es.caib.notib.logic.intf.dto.registreDocumentacioFisicaEnumDto."));
        model.addAttribute("idioma", EnumHelper.getOptionsForEnum(Idioma.class, "es.caib.notib.logic.intf.dto.Idioma."));
        model.addAttribute("origens", EnumHelper.getOptionsForEnum(OrigenEnum.class, "es.caib.notib.logic.intf.ws.notificacio.OrigenEnum."));
        var valideses = EnviamentTipus.NOTIFICACIO.equals(tipusEnviament) ? new Enum<?>[]{ValidesaEnum.COPIA_AUTENTICA, ValidesaEnum.ORIGINAL} :
                                new Enum<?>[]{ValidesaEnum.COPIA, ValidesaEnum.COPIA_AUTENTICA, ValidesaEnum.ORIGINAL};
        model.addAttribute("valideses", EnumHelper.getOrderedOptionsForEnum(ValidesaEnum.class,"es.caib.notib.logic.intf.ws.notificacio.ValidesaEnum.", valideses));
        var tipusDocumentals = EnumHelper.getOptionsForEnum(TipusDocumentalEnum.class,"es.caib.notib.logic.intf.ws.notificacio.TipusDocumentalEnum.");
        Collections.sort(tipusDocumentals);
        model.addAttribute("tipusDocumentals", tipusDocumentals);
        model.addAttribute("documentTipus", EnumHelper.getOptionsForEnum(DocumentTipus.class, "es.caib.notib.logic.intf.dto.DocumentTipusEnum."));
    }

    private boolean isAdministrador() {
        return RolHelper.isUsuariActualAdministrador(sessionScopedContext.getRolActual());
    }

    private boolean isValidaFirmaWebEnabled() {
        return Boolean.parseBoolean(aplicacioService.propertyGetByEntitat("es.caib.notib.plugins.validatesignature.enable.web", "true"));
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {

        binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
        binder.registerCustomEditor(Boolean.class, new CustomBooleanEditor("SI", "NO", false));
    }

    @Data
    public class DadesProcediment {

        private String caducitat;
        private Integer caducitatDiesNaturals;
        private Integer retard;
        private String organCodi;
        private List<String> organsDisponibles;
        private boolean agrupable = false;
        private List<GrupDto> grups = new ArrayList<>();
        private List<CieFormatSobreDto> formatsSobre = new ArrayList<>();
        private List<CieFormatFullaDto> formatsFulla = new ArrayList<>();
        private boolean comu;
        private boolean entregaCieVigent;
        private boolean entregaCieActiva;

        public void setCaducitat(Date data) {
            this.caducitat = df.format(data);
        }
    }
}