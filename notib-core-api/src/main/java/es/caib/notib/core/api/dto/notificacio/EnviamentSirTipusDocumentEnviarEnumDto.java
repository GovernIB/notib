package es.caib.notib.core.api.dto.notificacio;

/**
 * Enumerat que indica el tipus de document a enviar per a l'enviament SIR.
 * 
 * Els possibles tipus son:
 *  - CSV: Només s'envia el CSV de el document.
 *  - BINARI: Només s'envia el contingut binari de el document.
 *  - TOT: S'envien tots dos, CSV i contingut binari.
 *  
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum EnviamentSirTipusDocumentEnviarEnumDto {
	CSV,
	BINARI,
	TOT
}
