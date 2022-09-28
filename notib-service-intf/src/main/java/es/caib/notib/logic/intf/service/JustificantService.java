package es.caib.notib.logic.intf.service;

import es.caib.notib.logic.intf.dto.FitxerDto;
import es.caib.notib.logic.intf.dto.ProgresDescarregaDto;
import es.caib.notib.logic.intf.exception.JustificantException;
import org.springframework.security.access.prepost.PreAuthorize;
/**
 * Declaració dels mètodes per a la generació de justificants de les notificacions
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface JustificantService {

    /**
     * Executa el procés per a generar el justificant d'enviament d'una notificació
     * Generació del justificant desde API rest
     *
     * @param notificacioId
     *          Identificador de la notificació.
     * @param sequence
     *         String per a identificar el procés de generació del justificant.
     *         El seu valor serà utilitzat per a posteriorment consultar el progrés de la generació.
     * @return
     *      - Si tot va bé retorn el justificant firmat
     *      - Si no es pot firmar retorn el justificant sense firmar
     *      - Si hi ha un altre procés per generar el justificant en execució retorna null
     *
     * @throws JustificantException
     *      S'aixeca aquesta excepció quan hi ha un error durant la generació del document del justificant
     */
    @PreAuthorize("hasRole('tothom')")
    FitxerDto generarJustificantEnviament(Long notificacioId, String sequence) throws JustificantException;

    /**
     * Executa el procés per a generar el justificant d'enviament d'una notificació
     *
     * @param notificacioId
     *          Identificador de la notificació.
     * @param entitatId
     *          Identificador de l'entitat actual.
     * @param sequence
     *         String per a identificar el procés de generació del justificant.
     *         El seu valor serà utilitzat per a posteriorment consultar el progrés de la generació.
     * @return
     *      - Si tot va bé retorn el justificant firmat
     *      - Si no es pot firmar retorn el justificant sense firmar
     *      - Si hi ha un altre procés per generar el justificant en execució retorna null
     *
     * @throws JustificantException
     *      S'aixeca aquesta excepció quan hi ha un error durant la generació del document del justificant
     */
    @PreAuthorize("hasRole('tothom')")
    FitxerDto generarJustificantEnviament(Long notificacioId, Long entitatId, String sequence) throws JustificantException;

    /**
     * Executa el procés per a generar el justificant de recepció de comunicació SIR.
     *
     * @param notificacioId
     *            Atribut id de la notificació.
     * @param entitatId
     *          Identificador de l'entitat actual.
     * @param sequence
     *         String per a identificar el procés de generació del justificant.
     *         El seu valor serà utilitzat per a posteriorment consultar el progrés de la generació.
     * @return
     *      - Si tot va bé retorn el justificant firmat
     *      - Si no es pot firmar retorn el justificant sense firmar
     *      - Si hi ha un altre procés per generar el justificant en execució retorna null
     *
     * @throws JustificantException
     *      S'aixeca aquesta excepció quan hi ha un error durant la generació del document del justificant
     */
    @PreAuthorize("hasRole('tothom')")
    FitxerDto generarJustificantComunicacioSIR(Long notificacioId, Long entitatId, String sequence) throws JustificantException;

    /**
     * Consulta l'estat del progrés de la generació del justificant indicat
     * pel paràmetre sequence.
     *
     * @param sequence
     *          String per a identificar el progrés de generació del justificant que
     *          volem consultar
     * @return
     *          Objecte amb la informació del progrés.
     */
    @PreAuthorize("hasRole('tothom')")
    ProgresDescarregaDto consultaProgresGeneracioJustificant(String sequence);

}
