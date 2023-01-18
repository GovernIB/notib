/**
 * 
 */
package es.caib.notib.logic.intf.dto;

import java.io.Serializable;

/**
 * Enumerat que indica el tipus d'error d'una notificació.
 * 
 * Els possibles tipus son:
 *  - ERROR_XARXA: La comunicació de xarxa ha resultat amb
 *  error (un error de xarxa indica que s'ha de tornar a intentar
 *  la comunicació més envant).
 *  - ERROR_REMOT: La comunicació amb el sistema remot a resultat
 *  amb error (un error remot indica que la comunicacióno s'ha de
 *  tornar a intentar).
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum NotificacioErrorTipusEnumDto implements Serializable {
	ERROR_XARXA,
	ERROR_REMOT,
	ERROR_REGISTRE,
	ERROR_REINTENTS_CONSULTA,
	ERROR_REINTENTS_SIR,
	ERROR_EMAIL
}