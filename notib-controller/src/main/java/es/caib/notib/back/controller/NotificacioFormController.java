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
import es.caib.notib.back.helper.EntitatHelper;
import es.caib.notib.back.helper.EnumHelper;
import es.caib.notib.back.helper.MissatgesHelper;
import es.caib.notib.back.helper.RequestSessionHelper;
import es.caib.notib.back.helper.RolHelper;
import es.caib.notib.client.domini.DocumentTipus;
import es.caib.notib.client.domini.Idioma;
import es.caib.notib.client.domini.InteressatTipus;
import es.caib.notib.client.domini.NotificaDomiciliConcretTipus;
import es.caib.notib.client.domini.OrigenEnum;
import es.caib.notib.client.domini.TipusDocumentalEnum;
import es.caib.notib.client.domini.ValidesaEnum;
import es.caib.notib.logic.intf.dto.CodiValorDto;
import es.caib.notib.logic.intf.dto.CodiValorOrganGestorComuDto;
import es.caib.notib.logic.intf.dto.DocumentDto;
import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.FirmaValidDto;
import es.caib.notib.logic.intf.dto.GrupDto;
import es.caib.notib.logic.intf.dto.LocalitatsDto;
import es.caib.notib.logic.intf.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.logic.intf.dto.OficinaDto;
import es.caib.notib.logic.intf.dto.PaisosDto;
import es.caib.notib.logic.intf.dto.PermisEnum;
import es.caib.notib.logic.intf.dto.ProvinciesDto;
import es.caib.notib.logic.intf.dto.RegistreDocumentacioFisicaEnumDto;
import es.caib.notib.logic.intf.dto.RespostaConsultaArxiuDto;
import es.caib.notib.logic.intf.dto.RolEnumDto;
import es.caib.notib.logic.intf.dto.ServeiTipusEnumDto;
import es.caib.notib.logic.intf.dto.TipusDocumentEnumDto;
import es.caib.notib.logic.intf.dto.UsuariDto;
import es.caib.notib.logic.intf.dto.cie.CieFormatFullaDto;
import es.caib.notib.logic.intf.dto.cie.CieFormatSobreDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioComunicacioTipusEnumDto;
import es.caib.notib.logic.intf.dto.notificacio.TipusEnviamentEnumDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerDto;
import es.caib.notib.logic.intf.service.AplicacioService;
import es.caib.notib.logic.intf.service.EntitatService;
import es.caib.notib.logic.intf.service.GestioDocumentalService;
import es.caib.notib.logic.intf.service.GrupService;
import es.caib.notib.logic.intf.service.NotificacioService;
import es.caib.notib.logic.intf.service.OrganGestorService;
import es.caib.notib.logic.intf.service.PagadorCieFormatFullaService;
import es.caib.notib.logic.intf.service.PagadorCieFormatSobreService;
import es.caib.notib.logic.intf.service.PermisosService;
import es.caib.notib.logic.intf.service.ProcedimentService;
import es.caib.notib.logic.intf.service.ServeiService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Controlador per a la consulta i gestió de notificacions.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Controller
@RequestMapping("/notificacio")
public class NotificacioFormController extends BaseUserController {

    private final static String METADADES_ORIGEN = "metadades_origen";
    private final static String METADADES_VALIDESA = "metadades_validesa";
    private final static String METADADES_TIPO_DOCUMENTAL = "metadades_tipo_documental";
    private final static String METADADES_MODO_FIRMA = "metadades_modo_firma";
    private final static String EDIT_REFERER = "edit_referer";
    private final static String ENVIAMENT_TIPUS = "enviament_tipus";
    
    @Autowired
    private AplicacioService aplicacioService;
    @Autowired
    private NotificacioService notificacioService;
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

    DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

    private TipusEnviamentEnumDto tipusEnviament;

    @RequestMapping(value = "/new/notificacio")
    public String altaNotificacio(HttpServletRequest request, Model model) {

        initForm(request, model, TipusEnviamentEnumDto.NOTIFICACIO);
        RequestSessionHelper.esborrarObjecteSessio(request, ENVIAMENT_TIPUS);
        return "notificacioForm";
    }

    @RequestMapping(value = "/new/comunicacio")
    public String altaComunicacio(HttpServletRequest request, Model model) {

        initForm(request, model, TipusEnviamentEnumDto.COMUNICACIO);
        RequestSessionHelper.esborrarObjecteSessio(request, ENVIAMENT_TIPUS);
        return "notificacioForm";
    }

    @RequestMapping(value = "/new/comunicacioSIR")
    public String altaComunicacioSIR(HttpServletRequest request, Model model) {

        initForm(request, model, TipusEnviamentEnumDto.COMUNICACIO_SIR);
        RequestSessionHelper.actualitzarObjecteSessio(request, ENVIAMENT_TIPUS, TipusEnviamentEnumDto.COMUNICACIO_SIR);
        return "notificacioForm";
    }

    private void initForm(HttpServletRequest request, Model model, TipusEnviamentEnumDto tipusEnviament) {

        var referer = request.getHeader("Referer");
        RequestSessionHelper.actualitzarObjecteSessio(request, EDIT_REFERER, referer);
        var entitatActual = EntitatHelper.getEntitatActual(request);
        var notificacioCommand = new NotificacioCommand();
        List<EnviamentCommand> enviaments = new ArrayList<>();
        var enviament = new EnviamentCommand();
        var entregaPostal = new EntregapostalCommand();
        entregaPostal.setPaisCodi("ES");
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

//    @RequestMapping(value = "/procediments", method = RequestMethod.GET)
//    public String formProcediments(HttpServletRequest request, Model model) {
//
//        EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);
//        model.addAttribute("entitat", entitatActual);
//        UsuariDto usuariActual = aplicacioService.getUsuariActual();
//
//        Set<ProcSerCacheDto> procedimentsDisponibles = new HashSet<>();
//        List<OrganGestorDto> organsGestorsDisponibles = new ArrayList<OrganGestorDto>();
//
//        if (RolHelper.isUsuariActualUsuari(request)) {
//        	List<ProcSerCacheDto> procedimentsDisponiblesPerNotificacio = procedimentService.findProcedimentsWithPermis(entitatActual.getId(), usuariActual.getCodi(), PermisEnum.NOTIFICACIO);
//        	List<ProcSerCacheDto> procedimentsDisponiblesPerComunicacioSir = procedimentService.findProcedimentsWithPermis(entitatActual.getId(), usuariActual.getCodi(), PermisEnum.COMUNIACIO_SIR);
//        	procedimentsDisponibles.addAll(procedimentsDisponiblesPerNotificacio);
//        	procedimentsDisponibles.addAll(procedimentsDisponiblesPerComunicacioSir);
//            model.addAttribute("procediments", procedimentsDisponibles);
//            List<Long> procedimentsDisponiblesIds = new ArrayList<>();
//            for (ProcSerCacheDto pro : procedimentsDisponibles) {
//                procedimentsDisponiblesIds.add(pro.getId());
//            }
//            organsGestorsDisponibles = organGestorService.findByProcedimentIds(procedimentsDisponiblesIds);
//            model.addAttribute("organsGestors", organsGestorsDisponibles);
//        }
//        return "notificacioProcedimentsForm";
//    }

    @RequestMapping(value = "/organ/{organId}/procediments", method = RequestMethod.GET)
    @ResponseBody
    public List<CodiValorOrganGestorComuDto> getProcedimentsOrgan(HttpServletRequest request, @PathVariable String organId) {

        var entitatActual = EntitatHelper.getEntitatActual(request);
        var rol = RolEnumDto.valueOf(RolHelper.getRolActual(request));
        return procedimentService.getProcedimentsOrganNotificables(entitatActual.getId(), organId.equals("-") ? null : organId, rol, tipusEnviament);
    }

    @RequestMapping(value = "/organ/{organId}/serveis", method = RequestMethod.GET)
    @ResponseBody
    public List<CodiValorOrganGestorComuDto> getServeisOrgan(HttpServletRequest request, @PathVariable String organId) {

//    	TipusEnviamentEnumDto enviamentTipus = (TipusEnviamentEnumDto) RequestSessionHelper.obtenirObjecteSessio(request, ENVIAMENT_TIPUS);
        var entitatActual = EntitatHelper.getEntitatActual(request);
        var rol = RolEnumDto.valueOf(RolHelper.getRolActual(request));
        return serveiService.getServeisOrganNotificables(entitatActual.getId(), organId.equals("-") ? null : organId, rol, tipusEnviament);
    }

    @RequestMapping(value = "/cercaUnitats", method = RequestMethod.GET)
    @ResponseBody
    public List<OrganGestorDto> getAdministracions(HttpServletRequest request,
            @RequestParam(value = "codi", required = false) String codi,
            @RequestParam(value = "denominacio", required = false) String denominacio,
            @RequestParam(value = "nivellAdministracio", required = false) Long nivellAdministracio,
            @RequestParam(value = "comunitatAutonoma", required = false) Long comunitatAutonoma,
            @RequestParam(value = "provincia", required = false) Long provincia,
            @RequestParam(value = "municipi", required = false) String municipi,
            Model model) {

        var codiNull = Strings.isNullOrEmpty(codi);
        var denominacioNull = Strings.isNullOrEmpty(denominacio);
        var comunitatNull = comunitatAutonoma == null;
//        if (comunitatNull && (!codiNull || !denominacioNull) ||  !comunitatNull && codiNull && denominacioNull || codiNull && denominacioNull && comunitatNull) {
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

    @RequestMapping(value = "/administracions/codi/{codi}", method = RequestMethod.GET)
    @ResponseBody
    public List<OrganGestorDto> getAdministracionsPerCodi(HttpServletRequest request, @PathVariable String codi, Model model) {
        return notificacioService.unitatsPerCodi(codi);

    }

    @RequestMapping(value = "/administracions/denominacio/{denominacio}", method = RequestMethod.GET)
    @ResponseBody
    public List<OrganGestorDto> getAdministracionsPerDenominacio(HttpServletRequest request, @PathVariable String denominacio, Model model) {
        return notificacioService.unitatsPerDenominacio(denominacio);
    }

    @RequestMapping(value = "/new/destinatari", method = RequestMethod.GET)
    public PersonaCommand altaDestinatari(HttpServletRequest request, Model model) {

        var destinatari = new PersonaCommand();
        return destinatari;
    }

    @ResponseBody
    @RequestMapping(value = "/organ/oficina/{organCodi}")
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

    @RequestMapping(value = "/newOrModify", method = RequestMethod.POST)
    public String save(HttpServletRequest request, @Valid NotificacioCommand notificacioCommand, BindingResult bindingResult, Model model) throws IOException {

        log.debug("[NOT-CONTROLLER] POST notificació desde interfície web. ");
        List<String> tipusDocumentEnumDto = new ArrayList<>();
        var entitatActual = EntitatHelper.getEntitatActual(request);
        ProcSerDto procedimentActual = null;
        model.addAttribute("isPermesComunicacionsSirPropiaEntitat", aplicacioService.propertyGetByEntitat("es.caib.notib.comunicacions.sir.internes", "false"));
        if (notificacioCommand.getProcedimentId() != null) {
            procedimentActual = procedimentService.findById(entitatActual.getId(), isAdministrador(request), notificacioCommand.getProcedimentId());
        }
        notificacioCommand.setUsuariCodi(getCodiUsuariActual());
        if (bindingResult.hasErrors()) {
            relooadForm(request, notificacioCommand, bindingResult, model, tipusDocumentEnumDto, entitatActual, procedimentActual);
            return "notificacioForm";
        }
        if (RolHelper.isUsuariActualAdministrador(request)) {
            model.addAttribute("entitat", entitatService.findAll());
        }
        model.addAttribute(new NotificacioFiltreCommand());
        model.addAttribute(new OrganGestorFiltreCommand());
        try {
            log.debug("[NOT-CONTROLLER] POST notificació desde interfície web. Processant dades del formulari. ");
            updateDocuments(notificacioCommand, bindingResult);
            if (bindingResult.hasErrors()) {
                var msg = TipusEnviamentEnumDto.NOTIFICACIO.equals(notificacioCommand.getEnviamentTipus())
                        ? "notificacio.form.errors.validacio.notificacio" : "notificacio.form.errors.validacio.comunicacio";
                MissatgesHelper.error(request, getMessage(request, msg));
                relooadForm(request, notificacioCommand, bindingResult, model, tipusDocumentEnumDto, entitatActual, procedimentActual);
                return "notificacioForm";
            }
            if (notificacioCommand.getId() != null) {
                notificacioService.update(entitatActual.getId(), notificacioCommand.asDatabaseDto(), RolHelper.isUsuariActualAdministradorEntitat(request));
            } else {
                notificacioService.create(entitatActual.getId(), notificacioCommand.asDatabaseDto());
            }
            log.debug("[NOT-CONTROLLER] POST notificació desde interfície web. Formulari processat satisfactoriament. ");
            return "redirect:../notificacio";
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("[NOT-CONTROLLER] POST notificació desde interfície web. Excepció al processar les dades del formulari", ex);
            log.error(ExceptionUtils.getStackTrace(ex));
            MissatgesHelper.error(request, ex.getMessage());
            relooadForm(request, notificacioCommand, bindingResult, model, tipusDocumentEnumDto, entitatActual, procedimentActual);
//            ompliModelFormulari(request, procedimentActual, entitatActual, notificacioCommand, bindingResult, tipusDocumentEnumDto, model);
            model.addAttribute("notificacioCommandV2", notificacioCommand);
            return "notificacioForm";
        }
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
                        var contingutBase64 = Base64.getEncoder().encodeToString(contingut);
                        notificacioCommand.getDocuments()[i].setContingutBase64(contingutBase64);
                        notificacioCommand.getDocuments()[i].setMediaType(contentType);
                        notificacioCommand.getDocuments()[i].setMida(notificacioCommand.getArxiu()[i].getSize());
                        validaFirma(nom, contentType, bindingResult, i, contingut);
                    } else if (notificacioCommand.getArxiu()[i].isEmpty() && arxiuGestdocId != null) {
                        var result = notificacioCommand.getId() != null ? gestioDocumentalService.obtenirArxiuNotificacio(arxiuGestdocId)
                                        : gestioDocumentalService.obtenirArxiuTemporal(arxiuGestdocId);
                        var contingutBase64 = Base64.getEncoder().encodeToString(result);
                        notificacioCommand.getDocuments()[i].setContingutBase64(contingutBase64);
                        validaFirma(notificacioCommand.getDocuments()[i].getArxiuNom(), notificacioCommand.getDocuments()[i].getMediaType(), bindingResult, i, result);
                    } else {
                        notificacioCommand.getDocuments()[i] = null;
                    }
                    break;
                case CSV:
                    if (notificacioCommand.getDocumentArxiuCsv()[i] != null && !notificacioCommand.getDocumentArxiuCsv()[i].isEmpty()) {
                        notificacioCommand.getDocuments()[i].setCsv(notificacioCommand.getDocumentArxiuCsv()[i]);
                    } else {
                        notificacioCommand.getDocuments()[i] = null;
                    }
                    break;
                case UUID:
                    if (notificacioCommand.getDocumentArxiuUuid()[i] != null && !notificacioCommand.getDocumentArxiuUuid()[i].isEmpty()) {
                        notificacioCommand.getDocuments()[i].setUuid(notificacioCommand.getDocumentArxiuUuid()[i]);
                    } else {
                        notificacioCommand.getDocuments()[i] = null;
                    }
                    break;
                case URL:
                    if (notificacioCommand.getDocumentArxiuUrl()[i] != null && !notificacioCommand.getDocumentArxiuUrl()[i].isEmpty()) {
                        notificacioCommand.getDocuments()[i].setUrl(notificacioCommand.getDocumentArxiuUrl()[i]);
                    } else {
                        notificacioCommand.getDocuments()[i] = null;
                    }
                    break;
            }
        }
    }

    @RequestMapping(value = "/valida/firma", method = RequestMethod.POST)
    @ResponseBody
    public FirmaValidDto validaFirmaDocument(HttpServletRequest request, @RequestParam(value = "fitxer") MultipartFile fitxer) throws IOException {

        var nom = fitxer.getOriginalFilename();
        var content = fitxer.getBytes();
        var contentType = fitxer.getContentType();
//        String contingutBase64 = Base64.encodeBase64String(content);
//        String arxiuGestdocId = gestioDocumentalService.guardarArxiuTemporal(contingutBase64);
        var signatureInfo = notificacioService.checkIfSignedAttached(content, nom, contentType);
        return FirmaValidDto.builder().nom(nom).mida(fitxer.getSize()).mediaType(fitxer.getContentType()).signed(signatureInfo.isSigned())
                .error(signatureInfo.isError()).errorMsg(signatureInfo.getErrorMsg()).build();
    }

    private void validaFirma(String nom, String mediaType, BindingResult bindingResult, int position, byte[] content) {

        if (!isValidaFirmaWebEnabled()) {
            return;
        }
        var signatureInfoDto = notificacioService.checkIfSignedAttached(content, nom, mediaType);
        if (signatureInfoDto.isError()) {
            var codes = bindingResult.resolveMessageCodes("notificacio.form.valid.document.firma", "arxiu[" + position + "]");
            bindingResult.addError(new FieldError(bindingResult.getObjectName(), "arxiu[" + position + "]", "", true, codes, null, "La firma del document no és vàlida"));
        }
    }

    @RequestMapping(value = "/{notificacioId}/edit", method = RequestMethod.GET)
    public String editar(HttpServletRequest request, Model model, @PathVariable Long notificacioId) {

        var referer = request.getHeader("Referer");
        RequestSessionHelper.actualitzarObjecteSessio(request, EDIT_REFERER, referer);
        var notificacioDto = notificacioService.findAmbId(notificacioId, false);
        var notificacioCommand = NotificacioCommand.asCommand(notificacioDto);
        for(var i = 0; i < 5; i++) {
//                if (notificacio.getDocument()[i] != null) {
            if (notificacioCommand.getDocuments()[i].getArxiuNom() != null) {
                model.addAttribute("nomDocument_" + i, notificacioCommand.getDocuments()[i].getArxiuNom());
                notificacioCommand.setTipusDocumentDefault(i, TipusDocumentEnumDto.ARXIU.name());
            }
            if (notificacioCommand.getDocuments()[i].getUuid() != null) {
                model.addAttribute("nomDocument_" + i, notificacioCommand.getDocuments()[i].getUuid());
                notificacioCommand.setTipusDocumentDefault(i, TipusDocumentEnumDto.UUID.name());
            }
            if (notificacioCommand.getDocuments()[i].getCsv() != null) {
                model.addAttribute("nomDocument_" + i, notificacioCommand.getDocuments()[i].getCsv());
                notificacioCommand.setTipusDocumentDefault(i, TipusDocumentEnumDto.CSV.name());
            }
            if (notificacioCommand.getDocuments()[i].getUrl() != null) {
                model.addAttribute("nomDocument_" + i, notificacioCommand.getDocuments()[i].getUrl());
                notificacioCommand.setTipusDocumentDefault(i, TipusDocumentEnumDto.URL.name());
            }
        }
        emplenarModelNotificacio(request, model, notificacioCommand);
        return "notificacioForm";
    }

	@RequestMapping(value = "/nivellsAdministracions", method = RequestMethod.GET)
	@ResponseBody
	private List<CodiValorDto> getNivellsAdministracions(HttpServletRequest request, Model model) {
		return notificacioService.llistarNivellsAdministracions();
	}

	@RequestMapping(value = "/comunitatsAutonomes", method = RequestMethod.GET)
	@ResponseBody
	private List<CodiValorDto> getComunitatsAutonomess(HttpServletRequest request, Model model) {
		return notificacioService.llistarComunitatsAutonomes();
	}

	@RequestMapping(value = "/provincies/{codiCA}", method = RequestMethod.GET)
	@ResponseBody
	private List<ProvinciesDto> getProvinciesPerCA(HttpServletRequest request, Model model, @PathVariable String codiCA) {
		return notificacioService.llistarProvincies(codiCA);
	}

    @RequestMapping(value = "/procediment/{procedimentId}/dades", method = RequestMethod.GET)
    @ResponseBody
    public DadesProcediment getDadesProcSer(HttpServletRequest request, @PathVariable Long procedimentId) {

        var entitatActual = EntitatHelper.getEntitatActual(request);
        var usuariActual = aplicacioService.getUsuariActual();
        var procedimentActual = procedimentService.findById(entitatActual.getId(),false, procedimentId);
        var enviamentTipus = (TipusEnviamentEnumDto) RequestSessionHelper.obtenirObjecteSessio(request, ENVIAMENT_TIPUS);
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
        if (procedimentActual.isComu()) {
            // Obtenim òrgans seleccionables
            var permis = TipusEnviamentEnumDto.COMUNICACIO_SIR.equals(enviamentTipus) ? PermisEnum.COMUNICACIO_SIR :
                    TipusEnviamentEnumDto.COMUNICACIO.equals(enviamentTipus) ? PermisEnum.COMUNICACIO : PermisEnum.NOTIFICACIO;

            dadesProcediment.setOrgansDisponibles(permisosService.getOrgansCodisAmbPermisPerProcedimentComu(entitatActual.getId(), getCodiUsuariActual(), permis, procedimentActual));
//
//        	List<ProcSerOrganCacheDto> procedimentsOrgansAmbPermis = TipusEnviamentEnumDto.COMUNICACIO_SIR.equals(enviamentTipus) ?
//                    procedimentService.findProcedimentsOrganWithPermis(entitatActual.getId(), usuariActual.getCodi(), PermisEnum.COMUNIACIO_SIR)
//                    :  procedimentService.findProcedimentsOrganWithPermis(entitatActual.getId(), usuariActual.getCodi(), PermisEnum.NOTIFICACIO);
//            dadesProcediment.setOrgansDisponibles(procedimentService.findProcedimentsOrganCodiWithPermisByProcediment(procedimentActual, entitatActual.getDir3Codi(), procedimentsOrgansAmbPermis));
        }
        return dadesProcediment;
    }

    @RequestMapping(value = "/paisos", method = RequestMethod.GET)
    @ResponseBody
    private List<PaisosDto> getPaisos(HttpServletRequest request, Model model) {
        return notificacioService.llistarPaisos();
    }

    @RequestMapping(value = "/provincies", method = RequestMethod.GET)
    @ResponseBody
    private List<ProvinciesDto> getProvincies(HttpServletRequest request, Model model) {
        return notificacioService.llistarProvincies();
    }

    @RequestMapping(value = "/localitats/{provinciaId}", method = RequestMethod.GET)
    @ResponseBody
    private List<LocalitatsDto> getLocalitats(HttpServletRequest request, Model model, @PathVariable String provinciaId) {
        return notificacioService.llistarLocalitats(provinciaId);
    }

    @RequestMapping(value = "/consultaDocumentIMetadadesCsv/consulta", method = RequestMethod.POST, headers="Content-Type=application/json")
    @ResponseBody
    public RespostaConsultaArxiuDto consultaDocumentIMetadadesCsv(HttpServletRequest request, @RequestBody String csv) {

    	DocumentDto doc = null;
    	var validacioIdCsv = notificacioService.validarIdCsv(csv);
//        Boolean formatCsvValid = notificacioService.validarFormatCsv(csv); //TODO AQUEST VALIDACIO NOSE SI FUNCIONA
    	if (validacioIdCsv) {
            doc = notificacioService.consultaDocumentIMetadades(csv, false);
        }
    	return prepararResposta(validacioIdCsv, doc, request);
    }

    @RequestMapping(value = "/consultaDocumentIMetadadesUuid/consulta", method = RequestMethod.POST, headers="Content-Type=application/json" )
    @ResponseBody
    public RespostaConsultaArxiuDto consultaDocumentIMetadadesUuid(HttpServletRequest request, @RequestBody String uuid) {
        DocumentDto doc = notificacioService.consultaDocumentIMetadades(uuid, true);
        return prepararResposta(true, doc, request);
    }

    private RespostaConsultaArxiuDto prepararResposta(Boolean validacio, DocumentDto doc, HttpServletRequest request) {

        var teMetadades = Boolean.FALSE;
        if (!validacio && doc == null) {
            return RespostaConsultaArxiuDto.builder().validacioIdCsv(false).documentExistent(false).metadadesExistents(teMetadades).origen(null).validesa(null)
                    .tipoDocumental(null).modoFirma(null).build();
        }
        if (doc == null) {
            return RespostaConsultaArxiuDto.builder().validacioIdCsv(true).documentExistent(false).metadadesExistents(teMetadades).origen(null).validesa(null)
                    .tipoDocumental(null).modoFirma(null).build();
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

    @RequestMapping(value = "/caducitatDiesNaturals/{dia}/{mes}/{any}", method = RequestMethod.GET)
    @ResponseBody
    private long getDiesCaducitat(@PathVariable String dia, @PathVariable String mes, @PathVariable String any) throws ParseException {

        var data = df.parse(dia + "/" + mes + "/" + any);
        return CaducitatHelper.getDiesEntreDates(data);
    }

    @RequestMapping(value = "/caducitatData/{dies}", method = RequestMethod.GET)
    @ResponseBody
    private String getDataCaducitat(@PathVariable int dies) {
        return df.format(CaducitatHelper.sumarDiesNaturals(dies));
    }

    private void emplenarModelNotificacio(HttpServletRequest request, Model model, NotificacioCommand notificacioCommand) {

        var entitatActual = EntitatHelper.getEntitatActual(request);
        var usuariActual = aplicacioService.getUsuariActual();
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
        model.addAttribute("isPermesComunicacionsSirPropiaEntitat", aplicacioService.propertyGetByEntitat("es.caib.notib.comunicacions.sir.internes", "false"));
    }

//
//    private List<OrganGestorDto> recuperarOrgansPerProcedimentAmbPermis(EntitatDto entitatActual, List<CodiValorOrganGestorComuDto> procedimentsDisponibles, PermisEnum permis) {
//
//        // 1-recuperam els òrgans dels procediments disponibles (amb permís)
//        List<String> codisOrgansGestorsProcediments = new ArrayList<>();
//        for (CodiValorOrganGestorComuDto pro : procedimentsDisponibles)
//            codisOrgansGestorsProcediments.add(pro.getOrganGestor());
//
//        // 2-Descartam els organs vigents i els transformam a Dto
//        List<OrganGestorDto> organsGestorsProcediments = new ArrayList<>();
//        if (!codisOrgansGestorsProcediments.isEmpty())
//            organsGestorsProcediments = organGestorService.findByCodisAndEstat(codisOrgansGestorsProcediments, OrganGestorEstatEnum.V);
//
//        // 3-Obtenim els òrgans amb permís de notificació
//        List<OrganGestorDto> organsGestorsAmbPermis = organGestorService.findOrgansGestorsWithPermis(
//                entitatActual.getId(),
//                SecurityContextHolder.getContext().getAuthentication().getName(),
//                permis); //PermisEnum.NOTIFICACIO || PermisEnum.COMUNICACIO_SIR);
//
//        // 4-juntam tots els òrgans i ordenam per nom
//        Set<OrganGestorDto> setOrgansGestors = new HashSet<OrganGestorDto>(organsGestorsProcediments);
//        setOrgansGestors.addAll(organsGestorsAmbPermis);
//
//        List<OrganGestorDto> organsGestors = new ArrayList<OrganGestorDto>(setOrgansGestors);
//        if (!PropertiesHelper.getProperties().getAsBoolean("es.caib.notib.notifica.dir3.entitat.permes", false)) {
//            organsGestors.remove(organGestorService.findByCodi(entitatActual.getId(), entitatActual.getDir3Codi()));
//        }
//
//        Collections.sort(organsGestors, new Comparator<OrganGestorDto>() {
//            @Override
//            public int compare(OrganGestorDto p1, OrganGestorDto p2) {
//                return p1.getNom().compareTo(p2.getNom());
//            }
//        });
//        return organsGestors;
//    }

    private void ompliModelFormulari(HttpServletRequest request, ProcSerDto procedimentActual, EntitatDto entitatActual, NotificacioCommand notificacioCommand,
                                    BindingResult bindingResult, List<String> tipusDocumentEnumDto, Model model) {

        var tipusDocuments = entitatService.findTipusDocumentByEntitat(entitatActual.getId());
        var tipusDocumentDefault = entitatService.findTipusDocumentDefaultByEntitat(entitatActual.getId());
        if (tipusDocuments != null) {
            for (var tipusDocument : tipusDocuments) {
                tipusDocumentEnumDto.add(tipusDocument.getTipusDocEnum().name());
            }
            if (tipusDocumentDefault != null) {
                for (var i = 0; i < 5; i++)
                    notificacioCommand.setTipusDocumentDefault(i, tipusDocumentDefault.name());
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
        for (var i = 0; i < 5; i++) {
            var documentCommand = notificacioCommand.getDocuments()[i];
            if (documentCommand == null) {
                continue;
            }
            if (documentCommand.getArxiuGestdocId().isEmpty() && notificacioCommand.getTipusDocument()[i] != null &&
                (notificacioCommand.getArxiu()[i] != null && !notificacioCommand.getArxiu()[i].isEmpty()) &&  notificacioCommand.getTipusDocument()[i] == TipusDocumentEnumDto.ARXIU) {

                String contingutBase64 = null;
                try {
                    contingutBase64 = Base64.getEncoder().encodeToString(notificacioCommand.getArxiu()[i].getBytes());
                } catch (Exception ex) {
                    log.error("No s'ha pogut codificar els bytes de l'arxiu: " + ex.getMessage());
                }
                documentCommand.setContingutBase64(contingutBase64);
                documentCommand.setArxiuNom(notificacioCommand.getArxiu()[i].getOriginalFilename());
                documentCommand.setNormalitzat(notificacioCommand.getDocuments()[i].isNormalitzat());
                documentCommand.setMediaType(notificacioCommand.getArxiu()[i].getContentType());
                documentCommand.setMida(notificacioCommand.getArxiu()[i].getSize());
                var arxiuGestdocId = gestioDocumentalService.guardarArxiuTemporal(documentCommand.getContingutBase64());
                documentCommand.setArxiuGestdocId(arxiuGestdocId);
                model.addAttribute("nomDocument_" + i, notificacioCommand.getArxiu()[i].getOriginalFilename());
                continue;
            }
            if (documentCommand.getArxiuNom() != null && !documentCommand.getArxiuNom().isEmpty()) {
                model.addAttribute("nomDocument_" + i, documentCommand.getArxiuNom());
                continue;
            }
            if (notificacioCommand.getArxiu()[i] != null) {
                model.addAttribute("nomDocument_" + i, notificacioCommand.getArxiu()[i].getOriginalFilename());
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

    private void fillNotificacioModel(HttpServletRequest request, EntitatDto entitatActual, Model model, TipusEnviamentEnumDto tipusEnviament) {

        var rol = RolEnumDto.valueOf(RolHelper.getRolActual(request));
        String organFiltreProcediments = null;
        if (RolEnumDto.NOT_ADMIN_ORGAN.equals(rol)) {
            organFiltreProcediments = getOrganGestorActual(request).getCodi();
        }
        var procedimentsDisponibles = procedimentService.getProcedimentsOrganNotificables(entitatActual.getId(), organFiltreProcediments, RolEnumDto.valueOf(RolHelper.getRolActual(request)), tipusEnviament);
        var serveisDisponibles = serveiService.getServeisOrganNotificables(entitatActual.getId(), organFiltreProcediments, RolEnumDto.valueOf(RolHelper.getRolActual(request)), tipusEnviament);

        List<CodiValorOrganGestorComuDto> procSerDisponibles = new ArrayList<>();
        procSerDisponibles.addAll(procedimentsDisponibles);
        procSerDisponibles.addAll(serveisDisponibles);
        List<CodiValorDto> codisValor = new ArrayList<>();
        List<OrganGestorDto> organsGestors  = null;
        if (RolEnumDto.NOT_ADMIN.equals(rol)) {
            organsGestors = organGestorService.findByEntitat(entitatActual.getId());
        } else if (RolEnumDto.NOT_ADMIN_ORGAN.equals(rol)) {
            OrganGestorDto organGestorActual = getOrganGestorActual(request);
            organsGestors = organGestorService.findDescencentsByCodi(entitatActual.getId(), organGestorActual.getCodi());

        } else { // Rol usuari o altres
            var permis = tipusEnviament.equals(TipusEnviamentEnumDto.COMUNICACIO_SIR) ? PermisEnum.COMUNICACIO_SIR :
                    tipusEnviament.equals(TipusEnviamentEnumDto.COMUNICACIO) ? PermisEnum.COMUNICACIO : PermisEnum.NOTIFICACIO;
//            organsGestors = recuperarOrgansPerProcedimentAmbPermis(entitatActual, procSerDisponibles, tipus);
//            codisValor = organGestorService.getOrgansAmbPermis(entitatActual.getId(), permis);
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
        model.addAttribute("isMultiplesDestinataris", aplicacioService.propertyGetByEntitat("es.caib.notib.destinatari.multiple", "false"));
        model.addAttribute("ambEntregaDeh", entitatActual.isAmbEntregaDeh());
        model.addAttribute("comunicacioTipus", EnumHelper.getOptionsForEnum(NotificacioComunicacioTipusEnumDto.class," es.caib.notib.logic.intf.dto.notificacio.NotificacioComunicacioTipusEnumDto."));
        model.addAttribute("enviamentTipus",EnumHelper.getOptionsForEnum(NotificaEnviamentTipusEnumDto.class, "notificacio.tipus.enviament.enum."));
        model.addAttribute("serveiTipus", EnumHelper.getOptionsForEnum(ServeiTipusEnumDto.class,"es.caib.notib.logic.intf.dto.NotificaServeiTipusEnumDto."));
        Enum<?>[] interessatsTipus;
        Enum<?>[] interessatsTipusDest;
        if (TipusEnviamentEnumDto.COMUNICACIO_SIR.equals(tipusEnviament)) {
            interessatsTipus = new Enum<?>[]{ InteressatTipus.ADMINISTRACIO };
            interessatsTipusDest = new Enum<?>[]{ InteressatTipus.ADMINISTRACIO };
        } else if (TipusEnviamentEnumDto.COMUNICACIO.equals(tipusEnviament)) {
            interessatsTipus = new Enum<?>[]{ InteressatTipus.FISICA, InteressatTipus.FISICA_SENSE_NIF, InteressatTipus.ADMINISTRACIO, InteressatTipus.JURIDICA, };
            interessatsTipusDest = new Enum<?>[]{ InteressatTipus.FISICA, InteressatTipus.JURIDICA };
        } else {
            interessatsTipus = new Enum<?>[]{ InteressatTipus.FISICA, InteressatTipus.FISICA_SENSE_NIF, InteressatTipus.ADMINISTRACIO, InteressatTipus.JURIDICA };
            interessatsTipusDest = new Enum<?>[]{ InteressatTipus.FISICA, InteressatTipus.JURIDICA };
        }
        model.addAttribute("interessatTipus", EnumHelper.getOrderedOptionsForEnum(InteressatTipus.class,"es.caib.notib.client.domini.interessatTipusEnumDto.", interessatsTipus));
        model.addAttribute("interessatTipusDest", EnumHelper.getOrderedOptionsForEnum(InteressatTipus.class,"es.caib.notib.client.domini.interessatTipusEnumDto.", interessatsTipusDest));
        model.addAttribute("entregaPostalTipus", EnumHelper.getOptionsForEnum(NotificaDomiciliConcretTipus.class,"es.caib.notib.client.domini.NotificaDomiciliConcretTipusEnumDto."));
        model.addAttribute("registreDocumentacioFisica", EnumHelper.getOptionsForEnum(RegistreDocumentacioFisicaEnumDto.class,"es.caib.notib.logic.intf.dto\n.registreDocumentacioFisicaEnumDto."));
        model.addAttribute("idioma", EnumHelper.getOptionsForEnum(Idioma.class, "es.caib.notib.client.domini.idiomaEnumDto."));
        model.addAttribute("origens", EnumHelper.getOptionsForEnum(OrigenEnum.class, "es.caib.notib.client.domini.OrigenEnum."));
        Enum<?>[] valideses = TipusEnviamentEnumDto.NOTIFICACIO.equals(tipusEnviament) ? new Enum<?>[]{ValidesaEnum.COPIA_AUTENTICA, ValidesaEnum.ORIGINAL} :
                new Enum<?>[]{ValidesaEnum.COPIA, ValidesaEnum.COPIA_AUTENTICA, ValidesaEnum.ORIGINAL};
        model.addAttribute("valideses", EnumHelper.getOrderedOptionsForEnum(ValidesaEnum.class,"es.caib.notib.client.domini.ValidesaEnum.", valideses));
        var tipusDocumentals = EnumHelper.getOptionsForEnum(TipusDocumentalEnum.class,"es.caib.notib.client.domini.notificacio.TipusDocumentalEnum.");
        Collections.sort(tipusDocumentals);
        model.addAttribute("tipusDocumentals", tipusDocumentals);
        model.addAttribute("documentTipus", EnumHelper.getOptionsForEnum(DocumentTipus.class, "es.caib.notib.client.domini\n.DocumentTipusEnum."));
    }

    private boolean isAdministrador(HttpServletRequest request) {
        return RolHelper.isUsuariActualAdministrador(request);
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
        private List<GrupDto> grups = new ArrayList<GrupDto>();
        private List<CieFormatSobreDto> formatsSobre = new ArrayList<CieFormatSobreDto>();
        private List<CieFormatFullaDto> formatsFulla = new ArrayList<CieFormatFullaDto>();
        private boolean comu;
        private boolean entregaCieActiva;
        private boolean entregaCieVigent;
        private SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        public void setCaducitat(Date data) {
            this.caducitat = format.format(data);
        }
    }
}