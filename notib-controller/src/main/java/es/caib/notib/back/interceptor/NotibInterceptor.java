package es.caib.notib.back.interceptor;

import com.google.common.base.Strings;
import es.caib.notib.back.config.scopedata.SessionScopedContext;
import es.caib.notib.back.helper.AjaxHelper;
import es.caib.notib.back.helper.MissatgesHelper;
import es.caib.notib.back.helper.ModalHelper;
import es.caib.notib.back.helper.NodecoHelper;
import es.caib.notib.back.helper.RequestSessionHelper;
import es.caib.notib.back.helper.RolHelper;
import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.RolEnumDto;
import es.caib.notib.logic.intf.service.AplicacioService;
import es.caib.notib.logic.intf.service.AvisService;
import es.caib.notib.logic.intf.service.EntitatService;
import es.caib.notib.logic.intf.service.OrganGestorService;
import es.caib.notib.logic.intf.service.PermisosService;
import es.caib.notib.logic.intf.service.ProcedimentService;
import es.caib.notib.logic.intf.service.ServeiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static es.caib.notib.back.helper.RolHelper.*;

@Slf4j
@Component
public class NotibInterceptor implements AsyncHandlerInterceptor {

    public static final String REQUEST_ATTRIBUTE_MANIFEST_ATRIBUTES = "manifestAtributes";
    public static final String REQUEST_ATTRIBUTE_LOCALE = "requestLocale";

    @Autowired
    @Lazy
    AplicacioService aplicacioService;
    @Autowired @Lazy
    private PermisosService permisosService;
    @Autowired @Lazy
    EntitatService entitatService;
    @Autowired @Lazy
    private OrganGestorService organGestorService;
    @Autowired @Lazy
    private ProcedimentService procedimentService;
    @Autowired @Lazy
    private ServeiService serveiService;
    @Autowired @Lazy
    private AvisService avisService;

    @Autowired //@Lazy
    private SessionScopedContext sessionScopedContext;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        request.setAttribute(REQUEST_ATTRIBUTE_MANIFEST_ATRIBUTES, MissatgesHelper.getManifestAtributsMap());
        request.setAttribute(REQUEST_ATTRIBUTE_LOCALE, RequestContextUtils.getLocale(request).getLanguage());

        // Tipus accés: Es comprova si s0estpa fent una petició noDeco, modal o ajax.
        // En aquest cas es canvia la url, es redirigeix a la nova url, i per tant NO ha de continuar executat l'interceptor
        var continuarExecucio = ModalHelper.comprovarModalInterceptor(request, response) &&
                NodecoHelper.comprovarNodecoInterceptor(request, response) &&
                AjaxHelper.comprovarAjaxInterceptor(request, response);

        if (!continuarExecucio)
            return false;

        // Es comprova que l'usuari s'hagi desat a la BBDD, i l'assigna com a usuari actual
        // També es calculen els permisos que té l'usuari en el conjunt d'entitats
        processarUsuariActual(request, response);
        // Comprova si s'està canviant el rol.
        // En cas afirmatiu s'assignarà el nou rol, i es recalcularan les entitats disponibles per rol, i actual
        processarCanviRol(request);
        // Es carreguen les entitats disponibles i actual, així com els rols disponibles i actuals (si bo s'ha fet un canvi de rol)
        processarEntitatsIRols(request);
        // Comprova is s'està canviant l'entitat. En cas afirmatiu:
        // En cas afirmatiu s'assigna la nova entitat
        processarCanviEntitat(request);
        // Assignam el codi d'entitat per al servei de propietats
        entitatService.setConfigEntitat(sessionScopedContext.getEntitatActualCodi());
        // Obtenim els permisos per a mostrar o ocultar els menus d'alta de notificacions, comunicacions i comunicacions SIR
        obtenirPermisosMenu();
        // Obtenim els òrgans gestors accessibles com a administrador per l'usuari, i comprovam si es fa un canvi d'òrgan
        processarOrgansGestors(request);
        // Carregar avisos
        processarAvisos(request);

        request.setAttribute("sessionScopedContext", sessionScopedContext);
        return true;
    }



    // Mètodes a executar per l'interceptor
    // ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Es comprova que l'usuari s'hagi desat a la BBDD, i l'assigna com a usuari actual
    // També es calculen els permisos que té l'usuari en el conjunt d'entitats
    private void processarUsuariActual(HttpServletRequest request, HttpServletResponse response) {
        if (request.getUserPrincipal() != null) {
            // Comprovam si ja s'ha processat l'usuari en una request anterior
            Boolean autenticacioProcessada = sessionScopedContext.getAutenticacioProcessada();
            // Si encara no s'ha processat
            if (autenticacioProcessada == null) {
                // Cream o actualitzam l'usuari a la BBDD
                aplicacioService.processarAutenticacioUsuari();
                sessionScopedContext.setAutenticacioProcessada(true);
            }
        }
        // Assignam l'usuari autenticat com a usuari actual
        sessionScopedContext.setUsuariActual(aplicacioService.getUsuariActual());

        // Comprovam de quins permisos (Usuari, Administrador entitat, Administrador òrgan i superusuari) disposa l'usuari sobre el conjunt d'entitats
        var permisos = entitatService.getPermisosEntitatsUsuariActual();
        sessionScopedContext.setUsuariEntitat(permisos.get(RolEnumDto.tothom));
        sessionScopedContext.setAdminEntitat(permisos.get(RolEnumDto.NOT_ADMIN));
        sessionScopedContext.setAplicacioEntitat(permisos.get(RolEnumDto.NOT_APL));
        sessionScopedContext.setAdminOrgan(permisos.get(RolEnumDto.NOT_ADMIN_ORGAN));

        // Assignam l'idioma de l'usuari com a Locale
        RequestContextUtils.getLocaleResolver(request).setLocale(request, response, StringUtils.parseLocaleString(sessionScopedContext.getIdiomaUsuari()));

        // Obtenim les propietats globals de capçalera i peu
        sessionScopedContext.setCapLogo(aplicacioService.propertyGet("es.caib.notib.capsalera.logo"));
        sessionScopedContext.setPeuLogo(aplicacioService.propertyGet("es.caib.notib.peu.logo"));
        sessionScopedContext.setCapBackColor(aplicacioService.propertyGet("es.caib.notib.capsalera.color.fons"));
        sessionScopedContext.setCapColor(aplicacioService.propertyGet("es.caib.notib.capsalera.color.lletra"));
    }


    // Comprova si s'està canviant el rol.
    // En cas afirmatiu s'assignarà el nou rol, i es recalcularan les entitats disponibles per rol, i actual
    private void processarCanviRol(HttpServletRequest request) {

        // Comprovam si s'està canviant el rol
        var nouRol = request.getParameter(RolHelper.REQUEST_PARAMETER_CANVI_ROL);
        if (Strings.isNullOrEmpty(nouRol)) {
            return;
        }
        // Si es canvia el rol, i es tracta d'un rol vàlid
        // (rol disponible per usuari o administrador d'òrgan i rol tothom amb permís d'administració sobre algun òrgan)
        log.debug("Processant canvi rol (rol=" + nouRol + ")");
        if (request.isUserInRole(nouRol) ||
                (RolEnumDto.NOT_ADMIN_ORGAN.name().equals(nouRol) && request.isUserInRole(RolEnumDto.tothom.name()) && sessionScopedContext.getAdminOrgan())) {
            // Assignam el rol com a actual i ho desem com a darrer rol utilitzap per l'usuari
            sessionScopedContext.setRolActual(nouRol);
            aplicacioService.updateRolUsuariActual(nouRol);

            // Al canviar el rol poden canviar les entitats accessibles, i l'entitat actual
            processarCanviEntitatPerRol();
        }
    }

    // Al realitzar un canvi de rol poden canviar les entitats accessibles, i l'entitat actual
    private void processarCanviEntitatPerRol() {
        // Reassignam les entitats accessibles, depenent del rol actual
        var entitatsAccessibles = getEntitatsAccessibles();
        sessionScopedContext.setEntitatsAccessibles(entitatsAccessibles);

        // Obtenim l'entitat actual
        var entitatActual = sessionScopedContext.getEntitatActual();
        if (entitatActual == null) {
            entitatActual = getEntitatActual();
            sessionScopedContext.setEntitatActual(entitatActual);
        }
        // Si l'entitat actual no és una de les entitas disponibles, llavors s'assigna la primera entitat accessible com a entitat actual
        if (!entitatsAccessibles.isEmpty() && (entitatActual == null || !entitatsAccessibles.contains(entitatActual))) {
            sessionScopedContext.setEntitatActual(entitatsAccessibles.get(0));
        }
        // Desam l'entitat actual com a darrera entitat utilitzada per l'usuari
        if (sessionScopedContext.getEntitatActualId() != null)
            aplicacioService.updateEntitatUsuariActual(sessionScopedContext.getEntitatActualId());
    }

    // Es carreguen les entitats disponibles i actual, així com els rols disponibles i actuals (si bo s'ha fet un canvi de rol)
    private void processarEntitatsIRols(HttpServletRequest request) {

        // Comprovam si les entitats o els seus permisos s'han modificat des de l'últim com que s'han obtingut el llistat d'entitats disponibles,
        // i per tant si s'han d'actualitzar les entitats disponibles i l'entitat actual
        var entitatsToBeUpdated = hasToBeUpdated(entitatService.getLastPermisosModificatsInstant(), sessionScopedContext.getInstantEntitatsCarregades());
        // Si encara no s'han obtingut les entitats accessibles en aquesta sessió, o s'han d'actualitzar, obtenim les entitats accessibles
        if (sessionScopedContext.getEntitatsAccessibles() == null || entitatsToBeUpdated) {
            sessionScopedContext.setEntitatsAccessibles(getEntitatsAccessibles());
        }
        // Si encara no s'ha obtingut l'entitat actual en aquesta sessió, o s'han d'actualitzar les entitats, obtenim l'entitat actual
        if (sessionScopedContext.getEntitatActual() == null || entitatsToBeUpdated) {
            sessionScopedContext.setEntitatActual(getEntitatActual());
        }
        // Si encara no s'han obtingut els rols disponibles en aquesta sessió, els obtenim
        if (sessionScopedContext.getRolsDisponibles() == null) {
            sessionScopedContext.setRolsDisponibles(getRolsDisponibles(request));
        }
        // Si encara no s'ha obtingut el rol actual en aquesta sessió, l'obtenim
        if (sessionScopedContext.getRolActual() == null) {
            sessionScopedContext.setRolActual(getRolActual(request));
        }
    }

    // Comprova is s'està canviant l'entitat. En cas afirmatiu:
    // En cas afirmatiu s'assigna la nova entitat
    private void processarCanviEntitat(HttpServletRequest request) {

        // Comprovam si s'està canviant l'entitat
        var novaEntitat = request.getParameter(RolHelper.REQUEST_PARAMETER_CANVI_ENTITAT);
        if (Strings.isNullOrEmpty(novaEntitat)) {
            return;
        }
        // Si es canvia l'entitat, i es tracta d'una entitat accessible per l'usuari, s'assigna com a entitat actual
        log.debug("Processant canvi entitat (id=" + novaEntitat + ")");
        try {
            Long novaEntitatId = Long.parseLong(novaEntitat);
            var entitats = sessionScopedContext.getEntitatsAccessibles();
            for (var entitat: entitats) {
                if (!novaEntitatId.equals(entitat.getId())) {
                    continue;
                }
                sessionScopedContext.setEntitatActual(entitat);
                // Desam l'entitat actual com a darrera entitat utilitzada per l'usuari
                aplicacioService.updateEntitatUsuariActual(novaEntitatId);
            }
        } catch (NumberFormatException ignoredEx) {
            log.error("Error al canviar a la entitat amb id: " + novaEntitat, ignoredEx);
        }
    }

    // Obtenim els permisos per a mostrar o ocultar els menus d'alta de notificacions, comunicacions i comunicacions SIR
    private void obtenirPermisosMenu() {

        // Només cal obtenir els permisis si el rol actual és usuari
        if (sessionScopedContext.getEntitatActual() == null || sessionScopedContext.getUsuariActual() == null || !RolHelper.isUsuariActualUsuari(sessionScopedContext.getRolActual())) {
            return;
        }
        sessionScopedContext.setMenuNotificacions(permisosService.hasPermisNotificacio(sessionScopedContext.getEntitatActualId(), sessionScopedContext.getUsuariActualCodi()));
        sessionScopedContext.setMenuComunicacions(permisosService.hasPermisComunicacio(sessionScopedContext.getEntitatActualId(), sessionScopedContext.getUsuariActualCodi()));
        sessionScopedContext.setMenuSir(permisosService.hasPermisComunicacioSir(sessionScopedContext.getEntitatActualId(), sessionScopedContext.getUsuariActualCodi()));
    }

    // Obtenim els òrgans gestors accessibles com a administrador per l'usuari, i comprovam si es fa un canvi d'òrgan
    private void processarOrgansGestors(HttpServletRequest request) {

        // Carregam la informació dels òrgans que tenen procediments o servies pendents de sincronitzar
        if (RolHelper.isUsuariActualAdministradorEntitat(sessionScopedContext.getRolActual()) || RolHelper.isUsuariActualUsuariAdministradorOrgan(sessionScopedContext.getRolActual())) {
            var entitatActualId = sessionScopedContext.getEntitatActualId();
            sessionScopedContext.setProcedimentsAmbOrganNoSinc(procedimentService.getProcedimentsAmbOrganNoSincronitzat(entitatActualId));
            sessionScopedContext.setServeisAmbOrganNoSinc(serveiService.getServeisAmbOrganNoSincronitzat(entitatActualId));
        }
        // Carregam el òrgans gestors únicament si el rol actual és el d'administrador d'òrgan
        var rolActual = sessionScopedContext.getRolActual();
        if (!RolEnumDto.NOT_ADMIN_ORGAN.name().equals(rolActual)) {
            return;
        }
        // Comprovam si els òrgans han estat sincronizats o els seus permisos s'han modificat des de l'últim com que s'han obtingut el llistat d'òrgans disponibles,
        // i per tant si s'han d'actualitzar els òrgans disponibles i l'òrgan actual
        var organsToBeUpdated = hasToBeUpdated(organGestorService.getLastPermisosModificatsInstant(), sessionScopedContext.getInstantOrgansCarregades());
        // Carregam el òrgans gestors únicament si encara no s'han carregat o s'han d'actualitzar
        if (sessionScopedContext.getOrgansAccessibles() == null || organsToBeUpdated) {
            sessionScopedContext.setOrgansAccessibles(organGestorService.findAccessiblesByUsuariAndEntitatActual(sessionScopedContext.getEntitatActualId()));
            sessionScopedContext.setInstantOrgansCarregades(System.currentTimeMillis());
        }
        // Si tenim òrgans disponibles, assignamer l'òrgan actual, i comprovarem si hi ha canvi d'òrgan
        var organsAccessibles = sessionScopedContext.getOrgansAccessibles();
        if (organsAccessibles == null || organsAccessibles.isEmpty()) {
            return;
        }
        // Si encara no s'ha assignat cap òrgan, assignam el primer dels disponibles
        if (sessionScopedContext.getOrganActual() == null || !organsAccessibles.contains(sessionScopedContext.getOrganActual())) {
            sessionScopedContext.setOrganActual(sessionScopedContext.getOrgansAccessibles().get(0));
        }
        // Comprovam si es produeix un canvi d'òrgan gestor
        var nouOrgan = request.getParameter(RolHelper.REQUEST_PARAMETER_CANVI_ORGAN);
        if (Strings.isNullOrEmpty(nouOrgan)) {
            return;
        }
        try {
            Long organId = Long.parseLong(nouOrgan);
            for (var organGestor: organsAccessibles) {
                if (organGestor.getId().equals(organId)) {
                    sessionScopedContext.setOrganActual(organGestor);
                }
            }
        } catch (NumberFormatException ignoredEx) {
            log.error("Error al canviar a l'òrgan amb id: " + nouOrgan, ignoredEx);
        }
    }

    // Carregar avisos
    private void processarAvisos(HttpServletRequest request) {

        // Només es carregarà si l'usuari té rols per accedir a l'aplicació
        var rolsDisponibles = sessionScopedContext.getRolsDisponibles();
        var rolsAvisos = List.of(ROLE_USUARI, ROLE_ADMIN_ENTITAT, ROLE_ADMIN_ORGAN, ROLE_SUPER);
        if (Collections.disjoint(rolsDisponibles, rolsAvisos)) {
            return;
        }
        // Es carregaran només si encara no s'han carregat, o si hi ha un canvi de rol
        var canviRol = !Strings.isNullOrEmpty(request.getParameter(RolHelper.REQUEST_PARAMETER_CANVI_ROL));
        var isNotSuper = RolHelper.isUsuariActualAdministrador(sessionScopedContext.getRolActual());
        if ((sessionScopedContext.getAvisos() != null || RequestSessionHelper.isError(request)) && !canviRol && !isNotSuper) {
            return;
        }
        sessionScopedContext.setAvisos(RolHelper.isUsuariActualAdministradorEntitat(sessionScopedContext.getRolActual()) ?
                avisService.findActiveAdmin(sessionScopedContext.getEntitatActualId()) : avisService.findActive());
    }


    // Mètodes auxiliars
    // /////////////////

    // Comprovam si s'han modificat les entitats o els seus permisos des de que es varen actualitzar per darrer cop en la sessió
    private boolean hasToBeUpdated(Long instantUltimaActualitzacio, Long instantCarregades) {
        if (instantCarregades == null)
            return true;
        if (instantUltimaActualitzacio == null)
            return false;
        return instantUltimaActualitzacio > instantCarregades;
    }

    // Obtenim les entitats accessibles per l'usuari actual, donat el rol actual
    private List<EntitatDto> getEntitatsAccessibles() {
        var rolActual = sessionScopedContext.getRolActual();
        if (rolActual == null) {
            rolActual = "tothom";
        }
        var entitatsAccessibles = entitatService.findAccessiblesUsuariActual(rolActual);
        sessionScopedContext.setInstantEntitatsCarregades(System.currentTimeMillis());
        return entitatsAccessibles;
    }

    // Obtenim l'entitat actual
    private EntitatDto getEntitatActual() {
        // L'entitat actual ha de seu una de les entitats accessibles de l'usuari
        var entitatsAccessibles = sessionScopedContext.getEntitatsAccessibles();
        if (entitatsAccessibles == null || entitatsAccessibles.isEmpty())
            return null;

        EntitatDto entitatActual = null;
        // Si només es disposa d'una entitat accessible, s'assigna aquesta
        if (entitatsAccessibles != null && entitatsAccessibles.size() == 1) {
            entitatActual = entitatsAccessibles.get(0);
        }
        // Si ja es té una entitat assignada, i és una de les accessibles, s'assigna aquesta
        if (entitatActual == null && sessionScopedContext.getEntitatActual() != null && entitatsAccessibles.contains(sessionScopedContext.getEntitatActual()))
            entitatActual = sessionScopedContext.getEntitatActual();

        // Si es disposa d'una entitat desada com a darrera entitat utilitzada, i és una de les accessibles, s'assigna aquesta
        if (entitatActual == null) {
            var ultimaEntitat = aplicacioService.getUsuariActual().getUltimaEntitat();
            for (var entitat : entitatsAccessibles) {
                if (entitat.getId().equals(ultimaEntitat)) {
                    entitatActual = entitat;
                    break;
                }
            }
        }

        // Si no, s'utilitza la primera de les accessibles
        if (entitatActual == null) {
            entitatActual = entitatsAccessibles.get(0);
        }

        // Es desa a la BBDD l'entitat actual com a darrerar entitat utilitzada
        if (!entitatActual.equals(sessionScopedContext.getEntitatActual())) {
            aplicacioService.updateEntitatUsuariActual(entitatActual.getId());
        }
        return entitatActual;
    }

    // Obtenim els rols disponibles de l'usuari actual
    private List<String> getRolsDisponibles(HttpServletRequest request) {
        log.debug("Obtenint rols disponibles per a l'usuari actual");
        List<String> rols = new ArrayList<>();
        // Si l'usuari disposa del rol de superusuari, l'afegim
        if (request.isUserInRole(ROLE_SUPER)) {
            rols.add(ROLE_SUPER);
        }
        // Si l'usuari disposa del rol d'usuari, i té permís com a usuari en alguna entitat, l'afegim
        if (request.isUserInRole(ROLE_USUARI) && sessionScopedContext.getUsuariEntitat()) {
            rols.add(ROLE_USUARI);
        }
        // Si l'usuari disposa del rol d'aplicació, i té permís com a aplicació en alguna entitat, l'afegim
        if (request.isUserInRole(ROLE_APLICACIO) && sessionScopedContext.getAplicacioEntitat()) {
            rols.add(ROLE_APLICACIO);
        }
        // Si l'usuari disposa del rol d'usuari, i té permís com a administrador sobre algun órgan d'alguna entitat, afegim el rol administrador d'òrgan
        if (request.isUserInRole(ROLE_USUARI) && sessionScopedContext.getAdminOrgan()) {
            rols.add(ROLE_ADMIN_ORGAN);
        }
        // Si l'usuari disposa del rol d'administrador d'entitat, i té permís com a administrador d'entitat en alguna entitat, l'afegim
        if (sessionScopedContext.getEntitatActual() != null) {
            if (sessionScopedContext.getEntitatActual().isUsuariActualAdministradorEntitat() && request.isUserInRole(ROLE_ADMIN_ENTITAT) && sessionScopedContext.getAdminEntitat()) {
                rols.add(ROLE_ADMIN_ENTITAT);
            }
        } else {
            if (request.isUserInRole(ROLE_ADMIN_ENTITAT) && sessionScopedContext.getAdminEntitat()) {
                rols.add(ROLE_ADMIN_ENTITAT);
            }
        }
        return rols;
    }

    // Obtenim el rol actual de l'usuari
    private String getRolActual(HttpServletRequest request) {
        // Comprovam si l'usuari tenia desat l'últim rol utilitzar en la base de dades
        var rolActual = aplicacioService.getUsuariActual().getUltimRol();
        // Obtenim els rols disponibles de l'usuari
        var rolsDisponibles = sessionScopedContext.getRolsDisponibles();
        // Si es disposa de últim rol utilitzat, i és un dels rols disponibles, s'assigna com a rol actual.
        // En cas contrari s'agafarà el primer rol disponible segons el següent ordre: usuari, administrador d'entitat, superusuari i aplicació
        if (rolActual == null || !rolsDisponibles.contains(rolActual)) {
            if (request.isUserInRole(ROLE_USUARI) && rolsDisponibles.contains(ROLE_USUARI)) {
                rolActual = ROLE_USUARI;
            } else if (request.isUserInRole(ROLE_ADMIN_ENTITAT) && rolsDisponibles.contains(ROLE_ADMIN_ENTITAT)) {
                rolActual = ROLE_ADMIN_ENTITAT;
            } else if (request.isUserInRole(ROLE_SUPER) && rolsDisponibles.contains(ROLE_SUPER)) {
                rolActual = ROLE_SUPER;
            } else if (request.isUserInRole(ROLE_APLICACIO) && rolsDisponibles.contains(ROLE_APLICACIO)) {
                rolActual = ROLE_APLICACIO;
            }
            if (rolActual != null)
                aplicacioService.updateRolUsuariActual(rolActual);
        }
        log.debug("Obtenint rol actual (rol=" + rolActual + ")");
        return rolActual;
    }

}
