
/**
 *
 */
package es.caib.notib.war.controller;

import es.caib.notib.core.api.dto.*;
import es.caib.notib.core.api.dto.notificacio.NotificacioTableItemDto;
import es.caib.notib.core.api.exception.NoPermisosException;
import es.caib.notib.core.api.exception.RegistreNotificaException;
import es.caib.notib.core.api.exception.ValidationException;
import es.caib.notib.core.api.service.*;
import es.caib.notib.core.api.ws.notificacio.OrigenEnum;
import es.caib.notib.core.api.ws.notificacio.TipusDocumentalEnum;
import es.caib.notib.core.api.ws.notificacio.ValidesaEnum;
import es.caib.notib.war.command.*;
import es.caib.notib.war.helper.*;
import es.caib.notib.war.helper.DatatablesHelper.DatatablesResponse;
import lombok.Data;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Controlador per a la consulta i gestió de notificacions.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/notificacio")
public class NotificacioController extends BaseUserController {

    private final static String NOTIFICACIONS_FILTRE = "notificacions_filtre";

    @Autowired
    private AplicacioService aplicacioService;
    @Autowired
    private NotificacioService notificacioService;
    @Autowired
    private EntitatService entitatService;
    @Autowired
    private ProcedimentService procedimentService;
    @Autowired
    private OrganGestorService organGestorService;
    @Autowired
    private EnviamentService enviamentService;
    @Autowired
    private GrupService grupService;
    @Autowired
    private PagadorCieFormatSobreService pagadorCieFormatSobreService;
    @Autowired
    private PagadorCieFormatFullaService pagadorCieFormatFullaService;

    @RequestMapping(method = RequestMethod.GET)
    public String get(
            HttpServletRequest request,
            Model model) {

        NotificacioFiltreCommand notificacioFiltreCommand = getFiltreCommand(request);

        model.addAttribute(notificacioFiltreCommand);
        ompleProcediments(request, model);
        model.addAttribute("notificacioEstats",
                EnumHelper.getOptionsForEnum(NotificacioEstatEnumDto.class,
                        "es.caib.notib.core.api.dto.NotificacioEstatEnumDto."));
        model.addAttribute("tipusUsuari",
                EnumHelper.getOptionsForEnum(TipusUsuariEnumDto.class,
                        "es.caib.notib.core.api.dto.TipusUsuariEnumDto."));
        model.addAttribute("notificacioEnviamentEstats",
                EnumHelper.getOptionsForEnum(NotificacioEnviamentEstatEnumDto.class,
                        "es.caib.notib.core.api.dto.NotificacioEnviamentEstatEnumDto."));
        model.addAttribute("notificacioComunicacioTipus",
                EnumHelper.getOptionsForEnum(NotificacioComunicacioTipusEnumDto.class,
                        "es.caib.notib.core.api.dto.NotificacioComunicacioTipusEnumDto."));
        model.addAttribute("notificacioEnviamentTipus",
                EnumHelper.getOptionsForEnum(NotificaEnviamentTipusEnumDto.class,
                        "es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto."));
        model.addAttribute("mostrarColumnaEntitat",
                aplicacioService.propertyGet("es.caib.notib.columna.entitat"));
        model.addAttribute("mostrarColumnaNumExpedient",
                aplicacioService.propertyGet("es.caib.notib.columna.num.expedient"));
        return "notificacioList";
    }

    @RequestMapping(method = RequestMethod.POST, params = "netejar")
    public String postNeteja(
            HttpServletRequest request,
            Model model) {
        return post(request, new NotificacioFiltreCommand(), model);
    }

    @RequestMapping(method = RequestMethod.POST)
    public String post(
            HttpServletRequest request,
            NotificacioFiltreCommand command,
            Model model) {
        RequestSessionHelper.actualitzarObjecteSessio(
                request,
                NOTIFICACIONS_FILTRE,
                command);
        ompleProcediments(request, model);
        model.addAttribute("notificacioFiltreCommand", command);
        model.addAttribute("nomesAmbErrors", command.isNomesAmbErrors());
        return "notificacioList";
    }

    private void ompleProcediments(
            HttpServletRequest request,
            Model model) {

        List<CodiValorDto> organsDisponibles = new ArrayList<>();

        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
        Long entitatId = entitatActual.getId();
        String usuari = SecurityContextHolder.getContext().getAuthentication().getName();
        RolEnumDto rol = RolEnumDto.valueOf(RolHelper.getRolActual(request));
        OrganGestorDto organGestorActual = getOrganGestorActual(request);
        String organ = organGestorActual != null ? organGestorActual.getCodi() : null;

        if (RolHelper.isUsuariActualAdministrador(request)) {
            model.addAttribute("entitat", entitatService.findAll());
        }
//		// Eliminam l'òrgan gestor entitat  --> Per ara el mantenim, ja que hi ha notificacions realitzades a l'entitat
//		OrganGestorDto organEntitat = organGestorService.findByCodi(entitatActual.getId(), entitatActual.getDir3Codi());
//		organsGestorsDisponibles.remove(organEntitat);

        try {
            organsDisponibles = organGestorService.getOrgansGestorsDisponiblesConsulta(
                    entitatId,
                    usuari,
                    rol,
                    organ
            );
        }catch (Exception e) {
            if (ExceptionHelper.isExceptionOrCauseInstanceOf(e, NoPermisosException.class))
                MissatgesHelper.warning(request, getMessage(request, "notificacio.controller.sense.permis.lectura"));
        }
        model.addAttribute("organsGestorsPermisLectura", organsDisponibles);

//        model.addAttribute("procedimentsPermisLectura", procedimentsDisponibles);
//        model.addAttribute("organsGestorsPermisLectura", organsGestorsDisponibles);
    }

    @RequestMapping(value = "/new")
    public String altaForm(
            HttpServletRequest request,
            Model model) {
        emplenarModelNotificacio(request, model, null);
        return "notificacioForm";
    }

    @RequestMapping(value = "/procediments", method = RequestMethod.GET)
    public String formProcediments(
            HttpServletRequest request,
            Model model) {
        EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);
        model.addAttribute("entitat", entitatActual);

        UsuariDto usuariActual = aplicacioService.getUsuariActual();
//		List<String> rolsUsuariActual = aplicacioService.findRolsUsuariAmbCodi(usuariActual.getCodi());

        List<ProcedimentDto> procedimentsDisponibles = new ArrayList<ProcedimentDto>();
        List<OrganGestorDto> organsGestorsDisponibles = new ArrayList<OrganGestorDto>();

        if (RolHelper.isUsuariActualUsuari(request)) {
//			procedimentsDisponibles = procedimentService.findProcedimentsWithPermis(entitatActual.getId(), rolsUsuariActual, PermisEnum.NOTIFICACIO);

            procedimentsDisponibles = procedimentService.findProcedimentsWithPermis(entitatActual.getId(), usuariActual.getCodi(), PermisEnum.NOTIFICACIO);
            model.addAttribute("procediments", procedimentsDisponibles);
            List<Long> procedimentsDisponiblesIds = new ArrayList<Long>();
            for (ProcedimentDto pro : procedimentsDisponibles)
                procedimentsDisponiblesIds.add(pro.getId());
            organsGestorsDisponibles = organGestorService.findByProcedimentIds(procedimentsDisponiblesIds);
            model.addAttribute("organsGestors", organsGestorsDisponibles);
        }

        return "notificacioProcedimentsForm";
    }

    @RequestMapping(value = "/procedimentsOrgan", method = RequestMethod.GET)
    @ResponseBody
    public List<CodiValorComuDto> getProcediments(
            HttpServletRequest request,
            Model model) {
        Long entitatId = EntitatHelper.getEntitatActual(request).getId();
        String organCodi = null;
        PermisEnum permis = PermisEnum.CONSULTA;
        OrganGestorDto organGestor = getOrganGestorActual(request);
        if (organGestor != null)
            organCodi = organGestor.getCodi();
        RolEnumDto rol = RolEnumDto.valueOf(RolHelper.getRolActual(request));

        return procedimentService.getProcedimentsOrgan(
                entitatId,
                organCodi,
                null,
                rol,
                permis
        );
	}

    @RequestMapping(value = "/procedimentsOrgan/{organGestor}", method = RequestMethod.GET)
    @ResponseBody
    public List<CodiValorComuDto> getProcedimentByOrganGestor(
            HttpServletRequest request,
            @PathVariable Long organGestor,
            Model model) {

        Long entitatId = EntitatHelper.getEntitatActual(request).getId();
        String organCodi = null;
        String organFiltre = null;
        PermisEnum permis = PermisEnum.CONSULTA;
        OrganGestorDto organActual = getOrganGestorActual(request);
        if (organActual != null)
            organCodi = organActual.getCodi();
        RolEnumDto rol = RolEnumDto.valueOf(RolHelper.getRolActual(request));

        return procedimentService.getProcedimentsOrgan(
                entitatId,
                organCodi,
                organGestor,
                rol,
                permis
        );
    }

    @RequestMapping(value = "/cercaUnitats", method = RequestMethod.GET)
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
        return notificacioService.cercaUnitats(codi, denominacio, nivellAdministracio, comunitatAutonoma, null, null, provincia, municipi);

    }

    @RequestMapping(value = "/administracions/codi/{codi}", method = RequestMethod.GET)
    @ResponseBody
    public List<OrganGestorDto> getAdministracionsPerCodi(
            HttpServletRequest request,
            @PathVariable String codi,
            Model model) {
        return notificacioService.unitatsPerCodi(codi);

    }

    @RequestMapping(value = "/administracions/denominacio/{denominacio}", method = RequestMethod.GET)
    @ResponseBody
    public List<OrganGestorDto> getAdministracionsPerDenominacio(
            HttpServletRequest request,
            @PathVariable String denominacio,
            Model model) {
        return notificacioService.unitatsPerDenominacio(denominacio);

    }

    @RequestMapping(value = "/new/destinatari", method = RequestMethod.GET)
    public PersonaCommand altaDestinatari(
            HttpServletRequest request,
            Model model) {
        PersonaCommand destinatari = new PersonaCommand();
        return destinatari;
    }

    @RequestMapping(value = "/newOrModify", method = RequestMethod.POST)
    public String save(
            HttpServletRequest request,
            @Valid NotificacioCommandV2 notificacioCommand,
            BindingResult bindingResult,
            Model model) throws IOException {
        List<String> tipusDocumentEnumDto = new ArrayList<String>();
        EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);
        ProcedimentDto procedimentActual = null;
        
        if (notificacioCommand.getProcedimentId() != null)
            procedimentActual = procedimentService.findById(
                    entitatActual.getId(),
                    isAdministrador(request),
                    notificacioCommand.getProcedimentId());
        notificacioCommand.setUsuariCodi(aplicacioService.getUsuariActual().getCodi());

        if (bindingResult.hasErrors()) {
            ompliModelFormulari(
                    request,
                    procedimentActual,
                    entitatActual,
                    notificacioCommand,
                    bindingResult,
                    tipusDocumentEnumDto,
                    model);
            for (ObjectError error: bindingResult.getAllErrors()) {
                logger.error("[Error validacio notif] " + error.toString());
            }

            return "notificacioForm";
        }
        if (RolHelper.isUsuariActualAdministrador(request)) {
            model.addAttribute("entitat", entitatService.findAll());
        }
        model.addAttribute(new NotificacioFiltreCommand());
        model.addAttribute(new OrganGestorFiltreCommand());

        try {
            updateDocuments(notificacioCommand);

            if (notificacioCommand.getId() != null) {
                notificacioService.update(
                        entitatActual.getId(),
                        NotificacioCommandV2.asDto(notificacioCommand),
                        RolHelper.isUsuariActualAdministradorEntitat(request));
            } else {
                notificacioService.create(
                        entitatActual.getId(),
                        NotificacioCommandV2.asDto(notificacioCommand));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Error creant una notificació", ex);
            MissatgesHelper.error(request, ex.getMessage());
            ompliModelFormulari(
                    request,
                    procedimentActual,
                    entitatActual,
                    notificacioCommand,
                    bindingResult,
                    tipusDocumentEnumDto,
                    model);
            return "notificacioForm";
        }

        return "redirect:../notificacio";
    }

    private void updateDocuments(NotificacioCommandV2 notificacioCommand) throws IOException {

        for (int i = 0; i < 5; i++) {

            if (notificacioCommand.getTipusDocument()[i] != null) {
                String arxiuGestdocId = notificacioCommand.getDocuments()[i].getArxiuGestdocId();
                switch (notificacioCommand.getTipusDocument()[i]) {
                    case ARXIU:
                        if (notificacioCommand.getArxiu()[i] != null && !notificacioCommand.getArxiu()[i].isEmpty()) {
                            notificacioCommand.getDocuments()[i].setArxiuNom(notificacioCommand.getArxiu()[i].getOriginalFilename());
//                                notificacioCommand.getDocument()[i].setNormalitzat(notificacioCommand.getDocument()[i].isNormalitzat());
                            String contingutBase64 = Base64.encodeBase64String(notificacioCommand.getArxiu()[i].getBytes());
                            notificacioCommand.getDocuments()[i].setContingutBase64(contingutBase64);
                            notificacioCommand.getDocuments()[i].setMediaType(notificacioCommand.getArxiu()[i].getContentType());
                            notificacioCommand.getDocuments()[i].setMida(notificacioCommand.getArxiu()[i].getSize());
//                                notificacioCommand.getDocument()[i].setMetadadesKeys(notificacioCommand.getDocument()[i].getMetadadesKeys());
//                                notificacioCommand.getDocument()[i].setMetadadesValues(notificacioCommand.getDocument()[i].getMetadadesValues());
                        } else if (notificacioCommand.getArxiu()[i].isEmpty() && arxiuGestdocId != null) {
                            byte[] result = notificacioService.obtenirArxiuTemporal(arxiuGestdocId);
                            String contingutBase64 = Base64.encodeBase64String(result);
                            notificacioCommand.getDocuments()[i].setContingutBase64(contingutBase64);
                        }
                        break;
                    case CSV:
                        if (notificacioCommand.getDocumentArxiuCsv()[i] != null
                                && !notificacioCommand.getDocumentArxiuCsv()[i].isEmpty()) {
                            notificacioCommand.getDocuments()[i].setCsv(notificacioCommand.getDocumentArxiuCsv()[i]);
                        }
                        break;
                    case UUID:
                        if (notificacioCommand.getDocumentArxiuUuid()[i] != null
                                && !notificacioCommand.getDocumentArxiuUuid()[i].isEmpty()) {
                            notificacioCommand.getDocuments()[i].setUuid(notificacioCommand.getDocumentArxiuUuid()[i]);
                        }
                        break;
                    case URL:
                        if (notificacioCommand.getDocumentArxiuUrl()[i] != null
                                && !notificacioCommand.getDocumentArxiuUrl()[i].isEmpty()) {
                            notificacioCommand.getDocuments()[i].setUrl(notificacioCommand.getDocumentArxiuUrl()[i]);
                        }
                        break;
                }
            }
        }
    }

    @RequestMapping(value = "/datatable", method = RequestMethod.GET)
    @ResponseBody
    public DatatablesResponse datatable(HttpServletRequest request) {
        getEntitatActualComprovantPermisos(request);
        NotificacioFiltreDto filtre = getFiltreCommand(request).asDto();
        EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);
        PaginaDto<NotificacioTableItemDto> notificacions = new PaginaDto<>();
        UsuariDto usuariActual = aplicacioService.getUsuariActual();
        boolean isUsuari = RolHelper.isUsuariActualUsuari(request);
        boolean isUsuariEntitat = RolHelper.isUsuariActualAdministradorEntitat(request);
        boolean isAdministrador = RolHelper.isUsuariActualAdministrador(request);
        boolean isAdminOrgan = RolHelper.isUsuariActualUsuariAdministradorOrgan(request);
        String organGestorCodi = null;
        List<OrganGestorDto> organsGestorsDisponibles = new ArrayList<OrganGestorDto>();
        List<ProcedimentDto> procedimentsDisponibles = new ArrayList<ProcedimentDto>();
        List<ProcedimentOrganDto> procedimentOrgansDisponibles = new ArrayList<ProcedimentOrganDto>();
        List<String> codisProcedimentsDisponibles = new ArrayList<String>();
        List<Long> codisProcedimentOrgansDisponibles = new ArrayList<Long>();
        List<String> codisOrgansGestorsDisponibles = new ArrayList<String>();
        try {
            if (isUsuariEntitat) {
                if (filtre != null) {
                    filtre.setEntitatId(entitatActual.getId());
                }
            }
            if (isUsuari && entitatActual != null) {
                procedimentsDisponibles = procedimentService.findProcedimentsWithPermis(entitatActual.getId(), usuariActual.getCodi(), PermisEnum.CONSULTA);
                organsGestorsDisponibles = organGestorService.findOrgansGestorsWithPermis(entitatActual.getId(), usuariActual.getCodi(), PermisEnum.CONSULTA);
                procedimentOrgansDisponibles = procedimentService.findProcedimentsOrganWithPermis(entitatActual.getId(), usuariActual.getCodi(), PermisEnum.CONSULTA);
                for (ProcedimentDto procediment : procedimentsDisponibles) {
                    if (!procediment.isComu())
                        codisProcedimentsDisponibles.add(procediment.getCodi());
                }
                for (OrganGestorDto organGestorDto : organsGestorsDisponibles) {
                    codisOrgansGestorsDisponibles.add(organGestorDto.getCodi());
                }
                for (ProcedimentOrganDto procedimentOrganDto : procedimentOrgansDisponibles) {
                    codisProcedimentOrgansDisponibles.add(procedimentOrganDto.getId());
                }
            }
            if (isAdminOrgan) {
                OrganGestorDto organGestorActual = getOrganGestorActual(request);
                organGestorCodi = organGestorActual.getCodi();
                procedimentsDisponibles = procedimentService.findByOrganGestorIDescendents(entitatActual.getId(), organGestorActual);
                for (ProcedimentDto procediment : procedimentsDisponibles) {
                    codisProcedimentsDisponibles.add(procediment.getCodi());
                }
            }
            notificacions = notificacioService.findAmbFiltrePaginat(
                    entitatActual != null ? entitatActual.getId() : null,
                    RolEnumDto.valueOf(RolHelper.getRolActual(request)),
                    codisProcedimentsDisponibles,
                    codisOrgansGestorsDisponibles,
                    codisProcedimentOrgansDisponibles,
                    organGestorCodi,
                    usuariActual.getCodi(),
                    filtre,
                    DatatablesHelper.getPaginacioDtoFromRequest(request));
        } catch (SecurityException e) {
            MissatgesHelper.error(
                    request,
                    getMessage(
                            request,
                            "notificacio.controller.entitat.cap.assignada"));
        }

        return DatatablesHelper.getDatatableResponse(request, notificacions);
    }

    @RequestMapping(value = "/{notificacioId}/edit", method = RequestMethod.GET)
    public String editar(
            HttpServletRequest request,
            Model model,
            @PathVariable Long notificacioId) {
        emplenarModelNotificacio(request, model, notificacioId);
        return "notificacioForm";
    }

    @RequestMapping(value = "/{notificacioId}/info", method = RequestMethod.GET)
    public String info(
            HttpServletRequest request,
            Model model,
            @PathVariable Long notificacioId) {
        EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);

        emplenarModelNotificacioInfo(
                entitatActual,
                notificacioId,
                request,
                "dades",
                model);
        return "notificacioInfo";
    }

    @RequestMapping(value = "/{notificacioId}/delete", method = RequestMethod.GET)
    public String eliminar(
            HttpServletRequest request,
            Model model,
            @PathVariable Long notificacioId) {
        EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);

        try {
            notificacioService.delete(
                    entitatActual.getId(),
                    notificacioId);

        } catch (Exception ex) {
            logger.error("Hi ha hagut un error esborrant la notificació", ex);
            return getModalControllerReturnValueError(
                    request,
                    "redirect:../../notificacio",
                    "notificacio.controller.esborrar.ko",
                    new Object[]{ex.getMessage()});
        }
        return getModalControllerReturnValueSuccess(
                request,
                "redirect:../../notificacio",
                "notificacio.controller.esborrar.ok");
    }

    @RequestMapping(value = "/{notificacioId}/processar", method = RequestMethod.GET)
    public String processarGet(
            HttpServletRequest request,
            Model model,
            @PathVariable
                    Long notificacioId) {
        MarcarProcessatCommand command = new MarcarProcessatCommand();
        model.addAttribute(command);
        return "notificacioMarcarProcessat";
    }

    @RequestMapping(value = "/{notificacioId}/processar", method = RequestMethod.POST)
    public String processarPost(
            HttpServletRequest request,
            @PathVariable Long notificacioId,
            @Valid MarcarProcessatCommand command,
            BindingResult bindingResult,
            Model model) throws MessagingException {
        try {
            if (bindingResult.hasErrors()) {
                return "notificacioMarcarProcessat";
            }
            String resposta = notificacioService.marcarComProcessada(
                    notificacioId,
                    command.getMotiu());

            if (resposta != null) {
                MissatgesHelper.warning(request, resposta);
            }
            return getModalControllerReturnValueSuccess(
                    request,
                    "redirect:../../notificacio",
                    "notificacio.controller.refrescar.estat.ok");
        } catch (Exception ex) {
            logger.error("Hi ha hagut un error processant la notificació", ex);
            return getModalControllerReturnValueError(
                    request,
                    "redirect:../../notificacio",
                    "notificacio.controller.processar.ko",
                    new Object[]{ex.toString()}); //ex.getMessage()});
        }

    }

    @RequestMapping(value = "/{notificacioId}/event", method = RequestMethod.GET)
    public String eventList(
            HttpServletRequest request,
            Model model,
            @PathVariable Long notificacioId) {
        model.addAttribute("notificacioId", notificacioId);
        model.addAttribute("eventTipus",
                EnumHelper.getOptionsForEnum(NotificacioEventTipusEnumDto.class,
                        "es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto."));
        return "notificacioEvents";
    }

    @RequestMapping(value = "/{notificacioId}/event/datatable", method = RequestMethod.GET)
    @ResponseBody
    public DatatablesResponse eventDatatable(
            HttpServletRequest request,
            @PathVariable Long notificacioId) {

        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);

        return DatatablesHelper.getDatatableResponse(
                request,
                notificacioService.eventFindAmbNotificacio(entitatActual.getId(), notificacioId));
    }

    @RequestMapping(value = "/{notificacioId}/enviar", method = RequestMethod.GET)
    public String enviar(
            HttpServletRequest request,
            @PathVariable Long notificacioId,
            Model model) {
        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);

        boolean enviada = notificacioService.enviar(notificacioId);
        emplenarModelNotificacioInfo(
                entitatActual,
                notificacioId,
                request,
                "accions",
                model);
        if (enviada) {
            return getAjaxControllerReturnValueSuccess(request, "notificacioInfo",
                    "notificacio.controller.enviament.ok");
        } else {
            return getAjaxControllerReturnValueError(request, "notificacioInfo",
                    "notificacio.controller.enviament.error");
        }
    }

    @RequestMapping(value = "/{notificacioId}/registrar", method = RequestMethod.GET)
    public String registrar(
            HttpServletRequest request,
            @PathVariable Long notificacioId,
            Model model) throws RegistreNotificaException {
        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);

        List<RegistreIdDto> registresIdDto = notificacioService.registrarNotificar(notificacioId);

        emplenarModelNotificacioInfo(
                entitatActual,
                notificacioId,
                request,
                "accions",
                model);
        if (registresIdDto.size() > 0) {
            for (RegistreIdDto registreIdDto : registresIdDto) {
                if (registreIdDto.getNumero() != null) {
                    MissatgesHelper.success(request, "(" + registreIdDto.getNumeroRegistreFormat() + ")" + getMessage(
                            request,
                            "notificacio.controller.registrar.ok"));
                } else {
                    MissatgesHelper.error(request, getMessage(
                            request,
                            "notificacio.controller.registrar.error"));
                }
            }
        } else {
            MissatgesHelper.error(request, getMessage(
                    request,
                    "notificacio.controller.registrar.error"));
        }

        return "notificacioInfo";
//		if (registreIdDto.getNumeroRegistreFormat() != null) {
//			return getAjaxControllerReturnValueSuccess(request, "notificacioInfo",
//					"notificacio.controller.registrar.ok");
//		} else {
//			return getAjaxControllerReturnValueError(request, "notificacioInfo",
//					"notificacio.controller.registrar.error");
//		}
    }

    @RequestMapping(value = "/{notificacioId}/reactivarconsulta", method = RequestMethod.GET)
    public String reactivarconsulta(
            HttpServletRequest request,
            @PathVariable Long notificacioId,
            Model model) {
        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);

        boolean reactivat = notificacioService.reactivarConsulta(notificacioId);
        emplenarModelNotificacioInfo(
                entitatActual,
                notificacioId,
                request,
                "accions",
                model);
        if (reactivat) {
            return getAjaxControllerReturnValueSuccess(request, "notificacioInfo",
                    "notificacio.controller.reactivar.consulta.ok");
        } else {
            return getAjaxControllerReturnValueError(request, "notificacioInfo",
                    "notificacio.controller.reactivar.consulta.error");
        }
    }

    @RequestMapping(value = "/{notificacioId}/reactivarsir", method = RequestMethod.GET)
    public String reactivarsir(
            HttpServletRequest request,
            @PathVariable Long notificacioId,
            Model model) {
        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);

        boolean reactivat = notificacioService.reactivarSir(notificacioId);
        emplenarModelNotificacioInfo(
                entitatActual,
                notificacioId,
                request,
                "accions",
                model);
        if (reactivat) {
            return getAjaxControllerReturnValueSuccess(request, "notificacioInfo",
                    "notificacio.controller.reactivar.sir.ok");
        } else {
            return getAjaxControllerReturnValueError(request, "notificacioInfo",
                    "notificacio.controller.reactivar.sir.error");
        }
    }

    @RequestMapping(value = "/{notificacioId}/enviament", method = RequestMethod.GET)
    @ResponseBody
    public List<NotificacioEnviamentDatatableDto> enviamentList(
            HttpServletRequest request,
            Model model,
            @PathVariable Long notificacioId) {
        List<NotificacioEnviamentDatatableDto> destinataris = enviamentService.enviamentFindAmbNotificacio(notificacioId);
        return destinataris;
    }

    @RequestMapping(value = "/{notificacioId}/enviament/{enviamentId}", method = RequestMethod.GET)
    public String enviamentInfo(
            HttpServletRequest request,
            @PathVariable Long notificacioId,
            @PathVariable Long enviamentId,
            Model model) {
        emplenarModelEnviamentInfo(
                notificacioId,
                enviamentId,
                "dades",
                model,
                request);
        return "enviamentInfo";
    }

    @RequestMapping(value = "/{notificacioId}/enviament/{enviamentId}/event/datatable", method = RequestMethod.GET)
    @ResponseBody
    public DatatablesResponse enviamentEventsDatatable(
            HttpServletRequest request,
            @PathVariable Long notificacioId,
            @PathVariable Long enviamentId) {
        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);

        return DatatablesHelper.getDatatableResponse(
                request,
                notificacioService.eventFindAmbEnviament(entitatActual.getId(), notificacioId, enviamentId));
    }

    @RequestMapping(value = "/{notificacioId}/enviament/{enviamentId}/refrescarEstatNotifica", method = RequestMethod.GET)
    public String refrescarEstatNotifica(
            HttpServletRequest request,
            @PathVariable Long notificacioId,
            @PathVariable Long enviamentId,
            Model model) {
        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);

        NotificacioEnviamenEstatDto enviamentEstat = notificacioService.enviamentRefrescarEstat(
                entitatActual.getId(),
                enviamentId);
        boolean totbe = !enviamentEstat.isNotificaError();
        if (totbe) {
            MissatgesHelper.success(request, getMessage(request, "notificacio.controller.refrescar.estat.ok"));
        } else {
            MissatgesHelper.error(request, getMessage(request, "notificacio.controller.refrescar.estat.error"));
        }
        emplenarModelEnviamentInfo(notificacioId, enviamentId, "estatNotifica", model, request);
        return "enviamentInfo";
    }

    @RequestMapping(value = "/{notificacioId}/documentDescarregar/{documentId}", method = RequestMethod.GET)
    @ResponseBody
    public void documentDescarregar(
            HttpServletResponse response,
            @PathVariable Long notificacioId,
            @PathVariable Long documentId) throws IOException {
        ArxiuDto arxiu = notificacioService.getDocumentArxiu(notificacioId, documentId);
//        String mimeType = "";
//        if (arxiu.getContentType() == "application_pdf" || arxiu.getContentType() == "application/pdf" || arxiu.getContentType() == "PDF" && !arxiu.getNom().contains(".pdf")) {
//            mimeType = ".pdf";
//        }
        response.setHeader("Set-cookie", "fileDownload=true; path=/");
        writeFileToResponse(arxiu.getNom(), arxiu.getContingut(), response);
    }

    @RequestMapping(value = "/{notificacioId}/enviament/{enviamentId}/certificacioDescarregar", method = RequestMethod.GET)
    @ResponseBody
    public void certificacioDescarregar(
            HttpServletResponse response,
            @PathVariable Long notificacioId,
            @PathVariable Long enviamentId) throws IOException {
        ArxiuDto arxiu = notificacioService.enviamentGetCertificacioArxiu(enviamentId);
        response.setHeader("Set-cookie", "fileDownload=true; path=/");
        writeFileToResponse(
                arxiu.getNom(),
                arxiu.getContingut(),
                response);
    }

	
	@RequestMapping(value = "/nivellsAdministracions", method = RequestMethod.GET)
	@ResponseBody
	private List<CodiValorDto> getNivellsAdministracions(
		HttpServletRequest request,
		Model model) {		
		return notificacioService.llistarNivellsAdministracions();
	}
	
	
	@RequestMapping(value = "/comunitatsAutonomes", method = RequestMethod.GET)
	@ResponseBody
	private List<CodiValorDto> getComunitatsAutonomess(
		HttpServletRequest request,
		Model model) {		
		return notificacioService.llistarComunitatsAutonomes();
	}
	
	

	@RequestMapping(value = "/provincies/{codiCA}", method = RequestMethod.GET)
	@ResponseBody
	private List<ProvinciesDto> getProvinciesPerCA(
		HttpServletRequest request,
		Model model,
		@PathVariable String codiCA) {		
		return notificacioService.llistarProvincies(codiCA);
	}
	
	

    @RequestMapping(value = "/{notificacioId}/enviament/{enviamentId}/justificantDescarregar", method = RequestMethod.GET)
    @ResponseBody
    public void justificantDescarregar(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable Long notificacioId,
            @PathVariable Long enviamentId) throws IOException {
        ArxiuDto arxiu = new ArxiuDto();
        arxiu.setContingut(enviamentService.getDocumentJustificant(enviamentId));
        arxiu.setNom("justificant");
        String mimeType = ".pdf";

        if (arxiu.getContingut() != null) {
            response.setHeader("Set-cookie", "fileDownload=true; path=/");
            writeFileToResponse(arxiu.getNom() + mimeType, arxiu.getContingut(), response);
        } else {
            response.setHeader("Set-cookie", "fileDownload=false; path=/");
            throw new RuntimeException("Hi ha hagut un error generant/descarregant el justificant");
        }
    }

    @RequestMapping(value = "/{notificacioId}/justificant", method = RequestMethod.GET)
    public String justificantDescarregar(
            HttpServletRequest request,
            Model model,
            @PathVariable Long notificacioId) throws IOException {
        model.addAttribute("notificacioId", notificacioId);
        return "justificantDownloadForm";
    }

    @RequestMapping(value = "/{notificacioId}/justificant", method = RequestMethod.POST)
    @ResponseBody
    public void justificantDescarregar(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable Long notificacioId) throws IOException {
        EntitatDto entitatActual = getEntitatActualComprovantPermisos(request);
        String sequence = request.getParameter("sequence");
        FitxerDto justificant = notificacioService.recuperarJustificant(
                notificacioId,
                entitatActual.getId(),
                sequence);
        if (justificant == null) {
            throw new ValidationException("Existeix un altre procés iniciat. Esperau que finalitzi la descàrrega del document.");
        }
        response.setHeader("Set-cookie", "fileDownload=true; path=/");
        writeFileToResponse(justificant.getNom(), justificant.getContingut(), response);
    }

    @RequestMapping(value = "/{notificacioId}/justificant/estat/{sequence}", method = RequestMethod.GET)
    @ResponseBody
    public ProgresDescarregaDto justificantEstat(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable Long notificacioId,
            @PathVariable String sequence) throws IOException {
        return notificacioService.justificantEstat(sequence);
    }

    @RequestMapping(value = "/{notificacioId}/refrescarEstatClient", method = RequestMethod.GET)
    public String refrescarEstatClient(
            HttpServletResponse response,
            HttpServletRequest request,
            Model model,
            @PathVariable Long notificacioId) throws IOException {
        List<NotificacioEventDto> events = enviamentService.eventFindAmbNotificacio(notificacioId);
        boolean notificat = false;

        EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);

        emplenarModelNotificacioInfo(
                entitatActual,
                notificacioId,
                request,
                "dades",
                model);

        if (events != null && events.size() > 0) {
            NotificacioEventDto lastEvent = events.get(events.size() - 1);

            if (lastEvent.isError() &&
                    (lastEvent.getTipus().equals(NotificacioEventTipusEnumDto.CALLBACK_CLIENT) ||
                            lastEvent.getTipus().equals(NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_DATAT) ||
                            lastEvent.getTipus().equals(NotificacioEventTipusEnumDto.NOTIFICA_CALLBACK_CERTIFICACIO) ||
                            lastEvent.getTipus().equals(NotificacioEventTipusEnumDto.REGISTRE_CALLBACK_ESTAT) ||
                            lastEvent.getTipus().equals(NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_ERROR) ||
                            lastEvent.getTipus().equals(NotificacioEventTipusEnumDto.NOTIFICA_CONSULTA_SIR_ERROR) ||
                            lastEvent.getTipus().equals(NotificacioEventTipusEnumDto.NOTIFICA_REGISTRE) ||
                            lastEvent.getTipus().equals(NotificacioEventTipusEnumDto.NOTIFICA_ENVIAMENT))) {
                logger.info("Preparant per notificar canvi del event : " + lastEvent.getId() + " de tipus " + lastEvent.getTipus().name());
                notificat = enviamentService.reintentarCallback(lastEvent.getId());
            }
        }

        if (notificat) {
            MissatgesHelper.success(request,
                    getMessage(
                            request,
                            "notificacio.controller.notificar.client.ok"));
        } else {
            MissatgesHelper.error(request,
                    getMessage(
                            request,
                            "notificacio.controller.notificar.client.error"));
        }
        return "notificacioInfo";
    }

    @RequestMapping(value = "/procediment/{procedimentId}/dades", method = RequestMethod.GET)
    @ResponseBody
    public DadesProcediment getDataCaducitat(
            HttpServletRequest request,
            @PathVariable Long procedimentId) {
        EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);
        UsuariDto usuariActual = aplicacioService.getUsuariActual();
        ProcedimentDto procedimentActual = procedimentService.findById(
                entitatActual.getId(),
                false,
                procedimentId);

        DadesProcediment dadesProcediment = new DadesProcediment();
        dadesProcediment.setOrganCodi(procedimentActual.getOrganGestor());
        dadesProcediment.setCaducitat(CaducitatHelper.sumarDiesLaborals(procedimentActual.getCaducitat()));
        dadesProcediment.setRetard(procedimentActual.getRetard());
        dadesProcediment.setAgrupable(procedimentActual.isAgrupar());
        if (procedimentActual.isAgrupar()) {
            dadesProcediment.setGrups(grupService.findByProcedimentAndUsuariGrups(procedimentId));
        }
        if (procedimentActual.getPagadorcie() != null) {
            dadesProcediment.setFormatsSobre(pagadorCieFormatSobreService.findFormatSobreByPagadorCie(procedimentActual.getPagadorcie().getId()));
            dadesProcediment.setFormatsFulla(pagadorCieFormatFullaService.findFormatFullaByPagadorCie(procedimentActual.getPagadorcie().getId()));
        }
        dadesProcediment.setComu(procedimentActual.isComu());

        if (procedimentActual.isComu()) {
            // Obtenim òrgans seleccionables
            List<ProcedimentOrganDto> procedimentsOrgansAmbPermis = procedimentService.findProcedimentsOrganWithPermis(entitatActual.getId(), usuariActual.getCodi(), PermisEnum.NOTIFICACIO);
            dadesProcediment.setOrgansDisponibles(procedimentService.findProcedimentsOrganCodiWithPermisByProcediment(procedimentActual, entitatActual.getDir3Codi(), procedimentsOrgansAmbPermis));
        }
        return dadesProcediment;
    }

    @RequestMapping(value = "/organ/{organId}/procediments", method = RequestMethod.GET)
    @ResponseBody
    public List<CodiValorOrganGestorComuDto> getProcedimentsOrgan(
            HttpServletRequest request,
            @PathVariable String organId) {

        EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);
        return procedimentService.getProcedimentsOrganNotificables(
                entitatActual.getId(),
                organId.equals("-") ? null : organId,
                RolEnumDto.valueOf(RolHelper.getRolActual(request))
        );
    }

    @RequestMapping(value = "/paisos", method = RequestMethod.GET)
    @ResponseBody
    private List<PaisosDto> getPaisos(
            HttpServletRequest request,
            Model model) {
        return notificacioService.llistarPaisos();
    }

    @RequestMapping(value = "/provincies", method = RequestMethod.GET)
    @ResponseBody
    private List<ProvinciesDto> getProvincies(
            HttpServletRequest request,
            Model model) {
        return notificacioService.llistarProvincies();
    }

    @RequestMapping(value = "/localitats/{provinciaId}", method = RequestMethod.GET)
    @ResponseBody
    private List<LocalitatsDto> getLocalitats(
            HttpServletRequest request,
            Model model,
            @PathVariable String provinciaId) {
        return notificacioService.llistarLocalitats(provinciaId);
    }

    @RequestMapping(value = "/refrescarEstatNotifica", method = RequestMethod.GET)
    public String refrescarEstatNotificaGet(HttpServletRequest request, Model model) {
        return "enviamentsExpiratsActualitzacioForm";
    }

    @RequestMapping(value = "/refrescarEstatNotifica", method = RequestMethod.POST)
    @ResponseBody
    public void refrescarEstatNotifica() {
        try {
            notificacioService.enviamentsRefrescarEstat();
        } catch (Exception ex) {
            logger.error("S'ha produit un error consultant els enviaments", ex);
        }
    }

    @RequestMapping(value = "/refrescarEstatNotifica/estat", method = RequestMethod.GET)
    @ResponseBody
    public ProgresActualitzacioCertificacioDto enviamentsRefrescarEstatProgres() throws IOException {
        return notificacioService.actualitzacioEnviamentsEstat();
    }

    private boolean getLast3months() {
        return PropertiesHelper.getProperties().getAsBoolean("es.caib.notib.filtre.remeses.last.3.month");
    }

    private NotificacioFiltreCommand getFiltreCommand(
            HttpServletRequest request) {
        NotificacioFiltreCommand notificacioFiltreCommand = (NotificacioFiltreCommand) request.getSession()
                .getAttribute(NOTIFICACIONS_FILTRE);

        if (notificacioFiltreCommand == null) {
            notificacioFiltreCommand = new NotificacioFiltreCommand();
            if (getLast3months()) {
                Calendar cal = new GregorianCalendar();
                cal.add(Calendar.MONTH, -3);
                notificacioFiltreCommand.setDataInici(cal.getTime());
                notificacioFiltreCommand.setDataFi(new Date());
            }
            RequestSessionHelper.actualitzarObjecteSessio(
                    request,
                    NOTIFICACIONS_FILTRE,
                    notificacioFiltreCommand);
        }
        return notificacioFiltreCommand;
    }

    private void emplenarModelNotificacioInfo(
            EntitatDto entitatActual,
            Long notificacioId,
            HttpServletRequest request,
            String pipellaActiva,
            Model model) {
        NotificacioDtoV2 notificacio = notificacioService.findAmbId(
                notificacioId,
                isAdministrador(request));

        if (notificacio.getGrupCodi() != null) {
            GrupDto grup = grupService.findByCodi(
                    notificacio.getGrupCodi(),
                    entitatActual.getId());
            notificacio.setGrup(grup);
        }

        model.addAttribute("pipellaActiva", pipellaActiva);
        model.addAttribute("notificacio", notificacio);
        model.addAttribute("eventTipus",
                EnumHelper.getOptionsForEnum(NotificacioEventTipusEnumDto.class,
                        "es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto."));
        if (notificacio.getProcediment() != null && !notificacio.getProcedimentCodiNotib().isEmpty()) {
            model.addAttribute("permisGestio", procedimentService.hasPermisProcediment(
                    notificacio.getProcediment().getId(),
                    PermisEnum.GESTIO));
        } else {
            model.addAttribute("permisGestio", null);
        }
        model.addAttribute("permisAdmin", request.isUserInRole("NOT_ADMIN"));
    }


    private void emplenarModelEnviamentInfo(
            Long notificacioId,
            Long enviamentId,
            String pipellaActiva,
            Model model,
            HttpServletRequest request) {
        model.addAttribute("notificacio", notificacioService.findAmbId(notificacioId, isAdministrador(request)));
        model.addAttribute("pipellaActiva", pipellaActiva);
        NotificacioEnviamentDto enviament = enviamentService.enviamentFindAmbId(enviamentId);
        model.addAttribute("enviament", enviament);
        model.addAttribute("eventTipus", EnumHelper.getOptionsForEnum(NotificacioEventTipusEnumDto.class,
                "es.caib.notib.core.api.dto.NotificacioEventTipusEnumDto."));
    }

    private void emplenarModelNotificacio(
            HttpServletRequest request,
            Model model,
            Long notificacioId) {
        EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);
        UsuariDto usuariActual = aplicacioService.getUsuariActual();

        NotificacioCommandV2 notificacio = null;
        List<String> tipusDocumentEnumDto = new ArrayList<String>();
        if (notificacioId != null) {
            NotificacioDtoV2 notificacioDto = notificacioService.findAmbId(notificacioId, false);
            notificacio = NotificacioCommandV2.asCommand(notificacioDto);

            for(int i = 0; i < 5; i++) {
//                if (notificacio.getDocument()[i] != null) {
                    if (notificacio.getDocuments()[i].getArxiuNom() != null) {
                        model.addAttribute("nomDocument_" + i, notificacio.getDocuments()[i].getArxiuNom());
                        notificacio.setTipusDocumentDefault(i, TipusDocumentEnumDto.ARXIU.name());
                    }
                    if (notificacio.getDocuments()[i].getUuid() != null) {
                        model.addAttribute("nomDocument_" + i, notificacio.getDocuments()[i].getUuid());
                        notificacio.setTipusDocumentDefault(i, TipusDocumentEnumDto.UUID.name());
                    }
                    if (notificacio.getDocuments()[i].getCsv() != null) {
                        model.addAttribute("nomDocument_" + i, notificacio.getDocuments()[i].getCsv());
                        notificacio.setTipusDocumentDefault(i, TipusDocumentEnumDto.CSV.name());
                    }
                    if (notificacio.getDocuments()[i].getUrl() != null) {
                        model.addAttribute("nomDocument_" + i, notificacio.getDocuments()[i].getUrl());
                        notificacio.setTipusDocumentDefault(i, TipusDocumentEnumDto.URL.name());
                    }
//                }
            }

        } else {
            notificacio = new NotificacioCommandV2();
            List<EnviamentCommand> enviaments = new ArrayList<EnviamentCommand>();
            EnviamentCommand enviament = new EnviamentCommand();
            EntregapostalCommand entregaPostal = new EntregapostalCommand();
            entregaPostal.setPaisCodi("ES");
            enviament.setEntregaPostal(entregaPostal);
            enviaments.add(enviament);
            notificacio.setEnviaments(enviaments);
            notificacio.setCaducitat(CaducitatHelper.sumarDiesLaborals(10));

            TipusDocumentEnumDto tipusDocumentDefault = entitatService.findTipusDocumentDefaultByEntitat(entitatActual.getId());
            if (tipusDocumentDefault != null) {
                for(int i = 0; i < 5; i++) {
                    notificacio.setTipusDocumentDefault(i, tipusDocumentDefault.name());
                }
            }
            for(int i = 0; i < 5; i++) {
                notificacio.getDocuments()[i] = new DocumentCommand();
                notificacio.getDocuments()[i].setOrigen(OrigenEnum.ADMINISTRACIO);
                notificacio.getDocuments()[i].setValidesa(ValidesaEnum.ORIGINAL);
                notificacio.getDocuments()[i].setTipoDocumental(TipusDocumentalEnum.ALTRES);
            }
        }
        List<TipusDocumentDto> tipusDocuments = entitatService.findTipusDocumentByEntitat(entitatActual.getId());
        if (tipusDocuments != null) {
            for (TipusDocumentDto tipusDocument : tipusDocuments) {
                tipusDocumentEnumDto.add(tipusDocument.getTipusDocEnum().name());
            }
        }
        model.addAttribute("isTitularAmbIncapacitat", aplicacioService.propertyGet("es.caib.notib.titular.incapacitat", "true"));
        model.addAttribute("isMultiplesDestinataris", aplicacioService.propertyGet("es.caib.notib.destinatari.multiple", "false"));
        model.addAttribute("notificacioCommandV2", notificacio);
        model.addAttribute("ambEntregaDeh", entitatActual.isAmbEntregaDeh());
        model.addAttribute("ambEntregaCie", entitatActual.isAmbEntregaCie());
        model.addAttribute("tipusDocumentEnumDto", tipusDocumentEnumDto);
        model.addAttribute("entitat", entitatActual);

        List<ProcedimentDto> procedimentsDisponibles = new ArrayList<ProcedimentDto>();
        List<OrganGestorDto> organsGestors = new ArrayList<OrganGestorDto>();
        if (RolHelper.isUsuariActualUsuari(request)) {
	        procedimentsDisponibles = procedimentService.findProcedimentsWithPermis(
	                entitatActual.getId(),
	                usuariActual.getCodi(),
	                PermisEnum.NOTIFICACIO);
	
	        List<ProcedimentOrganDto> procedimentsOrgansDisponibles = procedimentService.findProcedimentsOrganWithPermis(
	                entitatActual.getId(),
	                usuariActual.getCodi(),
	                PermisEnum.NOTIFICACIO);
	
	        procedimentsDisponibles = addProcedimentsOrgan(procedimentsDisponibles, procedimentsOrgansDisponibles);
	
	        if (procedimentsDisponibles.isEmpty()) {
	            MissatgesHelper.warning(request, getMessage(request, "notificacio.controller.sense.permis.procediments"));
	        }
	
	        organsGestors = recuperarOrgansPerProcedimentAmbPermis(
	                entitatActual,
	                procedimentsDisponibles,
	                procedimentsOrgansDisponibles,
                    PermisEnum.NOTIFICACIO);
        } else if (RolHelper.isUsuariActualAdministradorEntitat(request)) {
        	procedimentsDisponibles = procedimentService.findByEntitat(entitatActual.getId());
        	organsGestors = organGestorService.findByEntitat(entitatActual.getId());
        } else if (RolHelper.isUsuariActualUsuariAdministradorOrgan(request)) {
        	OrganGestorDto organGestorActual = getOrganGestorActual(request);
            procedimentsDisponibles = procedimentService.findByOrganGestorIDescendents(entitatActual.getId(), organGestorActual);
            organsGestors = organGestorService.findDescencentsByCodi(entitatActual.getId(), organGestorActual.getCodi());
        }
        model.addAttribute("organsGestors", organsGestors);
        if (organsGestors == null || organsGestors.isEmpty()) {
            MissatgesHelper.warning(request, getMessage(request, "notificacio.controller.sense.permis.organs"));
        } else {
            model.addAttribute("procediments", procedimentsDisponibles);
        }

        model.addAttribute("amagat", Boolean.FALSE);

        model.addAttribute("comunicacioTipus",
                EnumHelper.getOptionsForEnum(
                        NotificacioComunicacioTipusEnumDto.class,
                        "es.caib.notib.core.api.dto.NotificacioComunicacioTipusEnumDto."));
        model.addAttribute("enviamentTipus",
                EnumHelper.getOptionsForEnum(
                        NotificaEnviamentTipusEnumDto.class,
                        "notificacio.tipus.enviament.enum."));
        model.addAttribute("serveiTipus",
                EnumHelper.getOptionsForEnum(
                        ServeiTipusEnumDto.class,
                        "es.caib.notib.core.api.dto.NotificaServeiTipusEnumDto."));
        model.addAttribute("interessatTipus",
                EnumHelper.getOrderedOptionsForEnum(
                        InteressatTipusEnumDto.class,
                        "es.caib.notib.core.api.dto.interessatTipusEnumDto.",
                        new Enum<?>[]{InteressatTipusEnumDto.FISICA, InteressatTipusEnumDto.ADMINISTRACIO, InteressatTipusEnumDto.JURIDICA}));
        model.addAttribute("entregaPostalTipus",
                EnumHelper.getOptionsForEnum(
                        NotificaDomiciliConcretTipusEnumDto.class,
                        "es.caib.notib.core.api.dto.NotificaDomiciliConcretTipusEnumDto."));
        model.addAttribute("registreDocumentacioFisica",
                EnumHelper.getOptionsForEnum(
                        RegistreDocumentacioFisicaEnumDto.class,
                        "es.caib.notib.core.api.dto.registreDocumentacioFisicaEnumDto."));
        model.addAttribute("idioma",
                EnumHelper.getOptionsForEnum(
                        IdiomaEnumDto.class,
                        "es.caib.notib.core.api.dto.idiomaEnumDto."));
        model.addAttribute("origens",
                EnumHelper.getOptionsForEnum(
                        OrigenEnum.class,
                        "es.caib.notib.core.api.ws.notificacio.OrigenEnum."));
        model.addAttribute("valideses",
                EnumHelper.getOptionsForEnum(
                        ValidesaEnum.class,
                        "es.caib.notib.core.api.ws.notificacio.ValidesaEnum."));
        model.addAttribute("tipusDocumentals",
                EnumHelper.getOptionsForEnum(
                        TipusDocumentalEnum.class,
                        "es.caib.notib.core.api.ws.notificacio.TipusDocumentalEnum."));

        try {
            model.addAttribute("concepteSize", notificacio.getConcepteDefaultSize());
            model.addAttribute("descripcioSize", notificacio.getDescripcioDefaultSize());
            model.addAttribute("nomSize", notificacio.getNomDefaultSize());
            model.addAttribute("llinatge1Size", notificacio.getLlinatge1DefaultSize());
            model.addAttribute("llinatge2Size", notificacio.getLlinatge2DefaultSize());
            model.addAttribute("emailSize", notificacio.getEmailDefaultSize());
            model.addAttribute("telefonSize", notificacio.getTelefonDefaultSize());
        } catch (Exception ex) {
            logger.error("No s'ha pogut recuperar la longitud del concepte: " + ex.getMessage());
        }

    }

    private List<ProcedimentDto> addProcedimentsOrgan(
            List<ProcedimentDto> procedimentsDisponibles,
            List<ProcedimentOrganDto> procedimentsOrgansDisponibles) {
        if (procedimentsOrgansDisponibles != null && !procedimentsOrgansDisponibles.isEmpty()) {
            Set<ProcedimentDto> setProcediments = new HashSet<ProcedimentDto>(procedimentsDisponibles);
            for (ProcedimentOrganDto procedimentOrgan : procedimentsOrgansDisponibles) {
                setProcediments.add(procedimentOrgan.getProcediment());
            }
            procedimentsDisponibles = new ArrayList<ProcedimentDto>(setProcediments);
            Collections.sort(procedimentsDisponibles, new Comparator<ProcedimentDto>() {
                @Override
                public int compare(ProcedimentDto p1, ProcedimentDto p2) {
                    return p1.getNom().compareTo(p2.getNom());
                }
            });
        }
        return procedimentsDisponibles;
    }

    private List<OrganGestorDto> recuperarOrgansPerProcedimentAmbPermis(
            EntitatDto entitatActual,
            List<ProcedimentDto> procedimentsDisponibles,
            List<ProcedimentOrganDto> procedimentsOrgansDisponibles,
            PermisEnum permis) {
        List<OrganGestorDto> organsGestorsProcediments = new ArrayList<OrganGestorDto>();
        List<Long> procedimentsDisponiblesIds = new ArrayList<Long>();
        for (ProcedimentDto pro : procedimentsDisponibles)
            procedimentsDisponiblesIds.add(pro.getId());

        // 1-recuperam els òrgans dels procediments disponibles (amb permís)
        if (!procedimentsDisponiblesIds.isEmpty())
            organsGestorsProcediments = organGestorService.findByProcedimentIds(procedimentsDisponiblesIds);
        // 2-recuperam els òrgans amb permís de notificació
        List<OrganGestorDto> organsGestorsAmbPermis = organGestorService.findOrgansGestorsWithPermis(
                entitatActual.getId(),
                SecurityContextHolder.getContext().getAuthentication().getName(),
                permis); //PermisEnum.NOTIFICACIO);
        // 3-juntam tots els òrgans i ordenam per nom
        List<OrganGestorDto> organsGestors;
        Set<OrganGestorDto> setOrgansGestors = new HashSet<OrganGestorDto>(organsGestorsProcediments);
        setOrgansGestors.addAll(organsGestorsAmbPermis);
        if (procedimentsOrgansDisponibles != null) {
            for (ProcedimentOrganDto procedimentOrgan : procedimentsOrgansDisponibles) {
                setOrgansGestors.add(procedimentOrgan.getOrganGestor());
            }
        }
        organsGestors = new ArrayList<OrganGestorDto>(setOrgansGestors);
        if (!PropertiesHelper.getProperties().getAsBoolean("es.caib.notib.notifica.dir3.entitat.permes", false)) {
            organsGestors.remove(organGestorService.findByCodi(entitatActual.getId(), entitatActual.getDir3Codi()));
        }

        Collections.sort(organsGestors, new Comparator<OrganGestorDto>() {
            @Override
            public int compare(OrganGestorDto p1, OrganGestorDto p2) {
                return p1.getNom().compareTo(p2.getNom());
            }
        });
        return organsGestors;
    }

    private void ompliModelFormulari(
            HttpServletRequest request,
            ProcedimentDto procedimentActual,
            EntitatDto entitatActual,
            NotificacioCommandV2 notificacioCommand,
            BindingResult bindingResult,
            List<String> tipusDocumentEnumDto,
            Model model) {
        UsuariDto usuariActual = aplicacioService.getUsuariActual();

        // TODO: Afegir formats de sobre i fulla
//		if (procedimentActual != null && procedimentActual.getPagadorcie() != null) {
//			model.addAttribute("formatsFulla", pagadorCieFormatFullaService.findFormatFullaByPagadorCie(procedimentActual.getPagadorcie().getId()));
//			model.addAttribute("formatsSobre", pagadorCieFormatSobreService.findFormatSobreByPagadorCie(procedimentActual.getPagadorcie().getId()));
//		}

        List<TipusDocumentDto> tipusDocuments = entitatService.findTipusDocumentByEntitat(entitatActual.getId());
        TipusDocumentEnumDto tipusDocumentDefault = entitatService.findTipusDocumentDefaultByEntitat(entitatActual.getId());

        if (tipusDocuments != null) {
            for (TipusDocumentDto tipusDocument : tipusDocuments) {
                tipusDocumentEnumDto.add(tipusDocument.getTipusDocEnum().name());
            }
            if (tipusDocumentDefault != null) {
                for (int i = 0; i < 5; i++)
                    notificacioCommand.setTipusDocumentDefault(i, tipusDocumentDefault.name());
            }
        }

        model.addAttribute("isTitularAmbIncapacitat", aplicacioService.propertyGet("es.caib.notib.titular.incapacitat", "true"));
        model.addAttribute("isMultiplesDestinataris", aplicacioService.propertyGet("es.caib.notib.destinatari.multiple", "false"));
        model.addAttribute("ambEntregaDeh", entitatActual.isAmbEntregaDeh());
        model.addAttribute("ambEntregaCie", entitatActual.isAmbEntregaCie());
        model.addAttribute("tipusDocumentEnumDto", tipusDocumentEnumDto);
        model.addAttribute("dir3Codi", entitatActual.getId());
        //model.addAttribute("procediments", procedimentService.findProcedimentsWithPermis(entitatActual.getId(), usuariActual.getCodi(), PermisEnum.NOTIFICACIO));
        //model.addAttribute("organsGestors", organGestorService.findOrganismes(entitatActual));
        List<ProcedimentDto> procedimentsDisponibles = new ArrayList<ProcedimentDto>();
        List<OrganGestorDto> organsGestors = new ArrayList<OrganGestorDto>();
        if (!RolHelper.isUsuariActualAdministradorEntitat(request)) {
	       	procedimentsDisponibles = procedimentService.findProcedimentsWithPermis(
	                entitatActual.getId(),
	                usuariActual.getCodi(),
	                PermisEnum.NOTIFICACIO);
	        List<ProcedimentOrganDto> procedimentsOrgansDisponibles = procedimentService.findProcedimentsOrganWithPermis(
	                entitatActual.getId(),
	                usuariActual.getCodi(),
	                PermisEnum.NOTIFICACIO);
	        addProcedimentsOrgan(procedimentsDisponibles, procedimentsOrgansDisponibles);
	
	        organsGestors = recuperarOrgansPerProcedimentAmbPermis(
	                entitatActual,
	                procedimentsDisponibles,
	                procedimentsOrgansDisponibles,
                    PermisEnum.NOTIFICACIO);
        } else {
        	procedimentsDisponibles = procedimentService.findByEntitat(entitatActual.getId());
        	organsGestors = organGestorService.findByEntitat(entitatActual.getId());
        }
        model.addAttribute("procediments", procedimentsDisponibles);
        if (procedimentsDisponibles.isEmpty()) {
            MissatgesHelper.warning(request, getMessage(request, "notificacio.controller.sense.permis.procediments"));
        }
        model.addAttribute("organsGestors", organsGestors);
        
        if (procedimentActual != null) {
        	 model.addAttribute("grups", grupService.findByProcedimentAndUsuariGrups(procedimentActual.getId()));
        }
           
        model.addAttribute("comunicacioTipus",
                EnumHelper.getOptionsForEnum(
                        NotificacioComunicacioTipusEnumDto.class,
                        "es.caib.notib.core.api.dto.NotificacioComunicacioTipusEnumDto."));
        model.addAttribute("enviamentTipus",
                EnumHelper.getOptionsForEnum(
                        NotificaEnviamentTipusEnumDto.class,
                        "notificacio.tipus.enviament.enum."));
        model.addAttribute("serveiTipus",
                EnumHelper.getOptionsForEnum(
                        ServeiTipusEnumDto.class,
                        "es.caib.notib.core.api.dto.NotificaServeiTipusEnumDto."));
        model.addAttribute("interessatTipus",
                EnumHelper.getOrderedOptionsForEnum(
                        InteressatTipusEnumDto.class,
                        "es.caib.notib.core.api.dto.interessatTipusEnumDto.",
                        new Enum<?>[]{InteressatTipusEnumDto.FISICA, InteressatTipusEnumDto.ADMINISTRACIO, InteressatTipusEnumDto.JURIDICA}));
        model.addAttribute("entregaPostalTipus",
                EnumHelper.getOptionsForEnum(
                        NotificaDomiciliConcretTipusEnumDto.class,
                        "es.caib.notib.core.api.dto.NotificaDomiciliConcretTipusEnumDto."));
        model.addAttribute("registreDocumentacioFisica",
                EnumHelper.getOptionsForEnum(
                        RegistreDocumentacioFisicaEnumDto.class,
                        "es.caib.notib.core.api.dto.registreDocumentacioFisicaEnumDto."));
        model.addAttribute("idioma",
                EnumHelper.getOptionsForEnum(
                        IdiomaEnumDto.class,
                        "es.caib.notib.core.api.dto.idiomaEnumDto."));
        model.addAttribute("origens",
                EnumHelper.getOptionsForEnum(
                        OrigenEnum.class,
                        "es.caib.notib.core.api.ws.notificacio.OrigenEnum."));
        model.addAttribute("valideses",
                EnumHelper.getOptionsForEnum(
                        ValidesaEnum.class,
                        "es.caib.notib.core.api.ws.notificacio.ValidesaEnum."));
        model.addAttribute("tipusDocumentals",
                EnumHelper.getOptionsForEnum(
                        TipusDocumentalEnum.class,
                        "es.caib.notib.core.api.ws.notificacio.TipusDocumentalEnum."));

        model.addAttribute("enviosGuardats", notificacioCommand.getEnviaments());
        model.addAttribute("tipusDocument", notificacioCommand.getTipusDocument());
        if(procedimentActual != null) {
        	model.addAttribute("procedimentId", procedimentActual.getId());
        }

        model.addAttribute("errors", bindingResult.getAllErrors());

        for (int i = 0; i < 5; i++) {
            if (notificacioCommand.getDocuments()[i].getArxiuGestdocId().isEmpty() && notificacioCommand.getTipusDocument()[i] != null && notificacioCommand.getArxiu()[i] != null && notificacioCommand.getTipusDocument()[i] == TipusDocumentEnumDto.ARXIU) {
                String arxiuGestdocId = null;
                String contingutBase64 = null;
                try {
                    contingutBase64 = Base64.encodeBase64String(notificacioCommand.getArxiu()[i].getBytes());
                } catch (Exception ex) {
                    logger.error("No s'ha pogut codificar els bytes de l'arxiu: " + ex.getMessage());
                }
                notificacioCommand.getDocuments()[i].setContingutBase64(contingutBase64);
                notificacioCommand.getDocuments()[i].setArxiuNom(notificacioCommand.getArxiu()[i].getOriginalFilename());
                notificacioCommand.getDocuments()[i].setNormalitzat(notificacioCommand.getDocuments()[i].isNormalitzat());
//                notificacioCommand.getDocument()[i].setMetadadesKeys(notificacioCommand.getDocument()[i].getMetadadesKeys());
//                notificacioCommand.getDocument()[i].setMetadadesValues(notificacioCommand.getDocument()[i].getMetadadesValues());
                notificacioCommand.getDocuments()[i].setMediaType(notificacioCommand.getArxiu()[i].getContentType());
                notificacioCommand.getDocuments()[i].setMida(notificacioCommand.getArxiu()[i].getSize());

                arxiuGestdocId = notificacioService.guardarArxiuTemporal(notificacioCommand.getDocuments()[i].getContingutBase64());

                notificacioCommand.getDocuments()[i].setArxiuGestdocId(arxiuGestdocId);
                model.addAttribute("nomDocument_" + i, notificacioCommand.getArxiu()[i].getOriginalFilename());


            } else {
                model.addAttribute("nomDocument_" + i, !notificacioCommand.getDocuments()[i].getArxiuNom().isEmpty() ? notificacioCommand.getDocuments()[i].getArxiuNom() : notificacioCommand.getArxiu()[i].getOriginalFilename());
            }
        }
        model.addAttribute("document", notificacioCommand.getDocuments());

        try {
//            Method concepte = NotificacioCommandV2.class.getMethod("getConcepte");
//            int concepteSize = concepte.getAnnotation(Size.class).max();
//
//            Method descripcio = NotificacioCommandV2.class.getMethod("getDescripcio");
//            int descripcioSize = descripcio.getAnnotation(Size.class).max();
            model.addAttribute("concepteSize", notificacioCommand.getConcepteDefaultSize() );
            model.addAttribute("descripcioSize", notificacioCommand.getDescripcioDefaultSize());
        } catch (Exception ex) {
            logger.error("No s'ha pogut recuperar la longitud del concepte: " + ex.getMessage());
        }
    }


    private boolean isAdministrador(HttpServletRequest request) {
        return RolHelper.isUsuariActualAdministrador(request);
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(
                Date.class,
                new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
        binder.registerCustomEditor(
                Boolean.class,
                new CustomBooleanEditor("SI", "NO", false));
    }

    @Data
    public class DadesProcediment {
        private String caducitat;
        private Integer retard;
        private String organCodi;
        private List<String> organsDisponibles;
        private boolean agrupable = false;
        private List<GrupDto> grups = new ArrayList<GrupDto>();
        private List<PagadorCieFormatSobreDto> formatsSobre = new ArrayList<PagadorCieFormatSobreDto>();
        private List<PagadorCieFormatFullaDto> formatsFulla = new ArrayList<PagadorCieFormatFullaDto>();
        private boolean comu;

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        public void setCaducitat(Date data) {
            this.caducitat = format.format(data);
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(NotificacioController.class);
}