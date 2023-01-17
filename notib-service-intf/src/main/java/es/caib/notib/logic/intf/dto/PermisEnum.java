package es.caib.notib.logic.intf.dto;

public enum PermisEnum {

	CONSULTA,
	PROCESSAR,
	NOTIFICACIO,
	COMUNICACIO,
	COMUNICACIO_SIR,
	GESTIO,
	COMUNS; // Procediments i serveis comun

	public boolean isPermisNotCom() {
		return NOTIFICACIO.equals(this) || COMUNICACIO.equals(this) || COMUNICACIO_SIR.equals(this);
	}
}
