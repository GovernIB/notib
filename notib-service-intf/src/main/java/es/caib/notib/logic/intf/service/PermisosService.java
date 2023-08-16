package es.caib.notib.logic.intf.service;

import es.caib.notib.logic.intf.dto.CodiValorDto;
import es.caib.notib.logic.intf.dto.CodiValorOrganGestorComuDto;
import es.caib.notib.logic.intf.dto.PermisEnum;
import es.caib.notib.logic.intf.dto.ProcSerTipusEnum;
import es.caib.notib.logic.intf.dto.procediment.ProcSerDto;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Declaració dels mètodes per a la gestió dels paràmetres de configuració de l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface PermisosService {

    /**
     * Comprova si l'usuari té permís per a realitzar una notificació en l'entitat
     *
     * @param entitatId Identificador de l'entitat
     * @param usuariCodi Codi de l'usuari
     * @return {@code true} si l'usuari té permís per a realitzar una notificació en l'entitat. {@code false} en cas contrari
     */
    @PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
    Boolean hasPermisNotificacio(Long entitatId, String usuariCodi);

    /**
     * Comprova si l'usuari té permís per a realitzar una comunicació en l'entitat
     *
     * @param entitatId Identificador de l'entitat
     * @param usuariCodi Codi de l'usuari
     * @return {@code true} si l'usuari té permís de comunicació en l'entitat. {@code false} en cas contrari
     */
    @PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
    Boolean hasPermisComunicacio(Long entitatId, String usuariCodi);

    /**
     * Comprova si l'usuari té permís per a realitzar una comunicació  sense procediment en l'entitat
     *
     * @param entitatId Identificador de l'entitat
     * @param usuariCodi Codi de l'usuari
     * @return {@code true} si l'usuari té permís de comunicació sense procediment en l'entitat. {@code false} en cas contrari
     */
//    @PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
//    Boolean hasPermisComunicacioSenseProcediment(Long entitatId, String usuariCodi);

    /**
     * Comprova si l'usuari té permís per a realitzar una comunicació SIR en l'entitat
     *
     * @param entitatId Identificador de l'entitat
     * @param usuariCodi Codi de l'usuari
     * @return {@code true} si l'usuari té permís per a realitzar una comunicació SIR en l'entitat. {@code false} en cas contrari
     */
    @PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
    Boolean hasPermisComunicacioSir(Long entitatId, String usuariCodi);

    @PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
    List<CodiValorDto> getOrgansAmbPermis(Long entitatId, String usuariCodi, PermisEnum permis);

    @PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
    List<CodiValorDto> getOrgansAmbPermisPerConsulta(Long entitatId, String usuariCodi, PermisEnum permis);

    @PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
    boolean hasUsrPermisOrgan(Long entitatId, String usr, String organCodi, PermisEnum permis);

//    @PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
//    List<CodiValorOrganGestorComuDto> getProcedimentsOrganNotificables(Long entitatId, String usuariCodi, TipusEnviamentEnumDto enviamentTipus);
//
//    @PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
//    List<CodiValorOrganGestorComuDto> getServeisOrganNotificables(Long entitatId, String usuariCodi, TipusEnviamentEnumDto enviamentTipus);

    // Obté òrgans amb permís per notificar per un procediment comú
    @PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
    List<String> getOrgansCodisAmbPermisPerProcedimentComu(Long entitatId, String usuariCodi, PermisEnum permis, ProcSerDto procSetDto);

    @PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
    List<String> getProcedimentsOrgansAmbPermis(Long entitatId, String usuariCodi, PermisEnum permis);

    @PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
    List<CodiValorOrganGestorComuDto> getProcSersAmbPermis(Long entitatId, String usuariCodi, PermisEnum permis);

    @PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
    List<CodiValorOrganGestorComuDto> getProcSerComuns(Long entitatId, List<String> grups, boolean removeInactius, ProcSerTipusEnum tipus);

    @PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
    List<CodiValorOrganGestorComuDto> getProcedimentsAmbPermis(Long entitatId, String usuariCodi, PermisEnum permis);

    @PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
    List<CodiValorOrganGestorComuDto> getServeisAmbPermis(Long entitatId, String usuariCodi, PermisEnum permis);

    @PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
    boolean hasNotificacioPermis(Long notId, Long entitat, String usuari, PermisEnum permis);

    @PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
    void evictGetOrgansAmbPermis();
}

