package es.caib.notib.core.api.dto.historic;


public enum HistoricMetriquesEnumDto {
	NOTIFICACIONS_TOTAL,
	NOTIFICACIONS_CORRECTES,
	NOTIFICACIONS_AMB_ERROR,
	NOTIFICACIONS_PROCEDIMENT_COMU,
	NOTIFICACIONS_AMB_GRUP,
	NOTIFICACIONS_ORIGEN_API,
	NOTIFICACIONS_ORIGEN_WEB,
	COMUNICACIONS_TOTAL,
	COMUNICACIONS_CORRECTES,
	COMUNICACIONS_AMB_ERROR,
	COMUNICACIONS_PROCEDIMENT_COMU,
	COMUNICACIONS_AMB_GRUP,
	COMUNICACIONS_ORIGEN_API,
	COMUNICACIONS_ORIGEN_WEB,
	PROCEDIMENTS, 
	GRUPS, 
	PERMISOS_CONSULTA, 
	PERMISOS_NOTIFICACIO, 
	PERMISOS_GESTIO, 
	PERMISOS_PROCESSAR, 
	PERMISOS_ADMINISTRAR;
	
	public Long getValue(HistoricAggregationOrganDto historic) {
		Long value = 0L;
		switch (this) {
		case NOTIFICACIONS_TOTAL:
			value = historic.getNumNotTotal();
			break;
		case NOTIFICACIONS_CORRECTES:
			value = historic.getNumNotCorrectes();
			break;
		case NOTIFICACIONS_AMB_ERROR:
			value = historic.getNumNotAmbError();
			break;
		case NOTIFICACIONS_PROCEDIMENT_COMU:
			value = historic.getNumNotProcedimentComu();
			break;
		case NOTIFICACIONS_AMB_GRUP:
			value = historic.getNumNotAmbGrup();
			break;
		case NOTIFICACIONS_ORIGEN_API:
			value = historic.getNumNotOrigenApi();
			break;
		case NOTIFICACIONS_ORIGEN_WEB:
			value = historic.getNumNotOrigenWeb();
			break;	
		case COMUNICACIONS_TOTAL:
			value = historic.getNumComTotal();
			break;
		case COMUNICACIONS_CORRECTES:
			value = historic.getNumComCorrectes();
			break;
		case COMUNICACIONS_AMB_ERROR:
			value = historic.getNumComAmbError();
			break;
		case COMUNICACIONS_PROCEDIMENT_COMU:
			value = historic.getNumComProcedimentComu();
			break;

		case COMUNICACIONS_AMB_GRUP:
			value = historic.getNumComAmbGrup();
			break;
		case COMUNICACIONS_ORIGEN_API:
			value = historic.getNumComOrigenApi();
			break;
		case COMUNICACIONS_ORIGEN_WEB:
			value = historic.getNumComOrigenWeb();
			break;
		default:
			break;
		}
		return value;		
	}
	
	public String toString() {
		String value = "";
//		switch (this) {
//		case EXPEDIENTS_CREATS:
//			value = "Nombre d'expedients creats";
//			break;
//		case EXPEDIENTS_CREATS_ACUM:
//			value = "Nombre d'expedients creats acumulats";
//			break;
//		case EXPEDIENTS_TANCATS:
//			value = "Nombre d'expedients tancats";
//			break;
//		case EXPEDIENTS_TANCATS_ACUM:
//			value = "Nombre d'expedients tancats acumulats";
//			break;
//		case DOCUMENTS_SIGNATS:
//			value = "Nombre de documents signats";
//			break;
//		case DOCUMENTS_NOTIFICATS:
//			value = "Nombre de documents notificats";
//			break;		
//		case TASQUES_TRAMITADES:
//			value = "Nombre de tasques tramitades";
//			break;
//		default:
//			break;
//		}
		return value;		
	}
}
