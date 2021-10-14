package es.caib.notib.core.api.dto.notificacio;

/**
 * Enumerat que indica el tipus de comunicació de la notificació.
 * 
 * Els possibles tipus son:
 *  - SINCRON: La petició a Notifica es fa quan es processa la
 *  notificació rebuda per NOTIB.
 *  - ASINCRON: La notificació es dona d'alta a NOTIB amb l'estat
 *  PENDENT i la petició a Notifica es realitza amb posterioritat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum NotificacioComunicacioTipusEnumDto {
	SINCRON,
	ASINCRON
}
