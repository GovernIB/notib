package es.caib.notib.core.api.dto;

public enum PermisEnum {
	CONSULTA,
	PROCESSAR,
	NOTIFICACIO, // Notificacions i comunicacions NO SIR
	COMUNICACIO,
	COMUNICACIO_SIR, // Comunicacions SIR
	GESTIO,
	COMUNS, // Procediments i serveis comuns
	ADMIN;

	public boolean isPermisNotCom() {
		return NOTIFICACIO.equals(this) || COMUNICACIO.equals(this) || COMUNICACIO_SIR.equals(this);
	}
}
