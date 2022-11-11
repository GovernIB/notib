package es.caib.notib.core.api.service;

import org.springframework.security.access.prepost.PreAuthorize;

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
     * Comprova si l'usuari té permís per a realitzar una comunicació SIR en l'entitat
     *
     * @param entitatId Identificador de l'entitat
     * @param usuariCodi Codi de l'usuari
     * @return {@code true} si l'usuari té permís per a realitzar una comunicació SIR en l'entitat. {@code false} en cas contrari
     */
    @PreAuthorize("hasRole('NOT_ADMIN') or hasRole('tothom')")
    Boolean hasPermisComunicacioSir(Long entitatId, String usuariCodi);

}

