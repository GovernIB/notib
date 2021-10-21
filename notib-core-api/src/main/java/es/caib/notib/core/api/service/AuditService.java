package es.caib.notib.core.api.service;


public interface AuditService {
	
	public static enum TipusOperacio { CREATE, UPDATE, DELETE }
	public static enum TipusEntitat { NOTIFICACIO, ENVIAMENT, ENTITAT, PROCEDIMENT, SERVEI, GRUP, PROCEDIMENT_GRUP, APLICACIO }
	public static enum TipusObjecte { ENTITAT, DTO }
	
	public void audita(
			Object objecteAuditar,
			TipusOperacio tipusOperacio,
			TipusEntitat tipusEntitat,
			TipusObjecte tipusObjecte,
			String joinPoint);

}
