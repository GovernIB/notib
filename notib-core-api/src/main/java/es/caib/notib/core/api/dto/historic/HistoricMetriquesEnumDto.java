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
	ENVIAMENTS,
	PROCEDIMENTS, 
	GRUPS, 
	PERMISOS_CONSULTA, 
	PERMISOS_NOTIFICACIO, 
	PERMISOS_GESTIO, 
	PERMISOS_PROCESSAR, 
	PERMISOS_ADMINISTRAR;
	
	public Long getValue(HistoricAggregation historic) {
		switch (this) {
		case NOTIFICACIONS_TOTAL:
			return historic.getNumNotTotal();
		case NOTIFICACIONS_CORRECTES:
			return historic.getNumNotCorrectes();
		case NOTIFICACIONS_AMB_ERROR:
			return historic.getNumNotAmbError();
		case NOTIFICACIONS_PROCEDIMENT_COMU:
			return historic.getNumNotProcedimentComu();
		case NOTIFICACIONS_AMB_GRUP:
			return historic.getNumNotAmbGrup();
		case NOTIFICACIONS_ORIGEN_API:
			return historic.getNumNotOrigenApi();
		case NOTIFICACIONS_ORIGEN_WEB:
			return historic.getNumNotOrigenWeb();
		case COMUNICACIONS_TOTAL:
			return historic.getNumComTotal();
		case COMUNICACIONS_CORRECTES:
			return historic.getNumComCorrectes();
		case COMUNICACIONS_AMB_ERROR:
			return historic.getNumComAmbError();
		case COMUNICACIONS_PROCEDIMENT_COMU:
			return historic.getNumComProcedimentComu();
		case COMUNICACIONS_AMB_GRUP:
			return historic.getNumComAmbGrup();
		case COMUNICACIONS_ORIGEN_API:
			return historic.getNumComOrigenApi();
		case COMUNICACIONS_ORIGEN_WEB:
			return historic.getNumComOrigenWeb();
		}
		return null;		
	}
	
	public Long getValue(HistoricAggregationOrganDto historic) {
		Long value = getValue((HistoricAggregation) historic);
		if (value != null ) {
			return value;
		}
		switch (this) {
		case ENVIAMENTS:
			return historic.getNumEnviaments();
		case PROCEDIMENTS:
			return historic.getNumProcediments();
		case GRUPS:
			return historic.getNumGrups();
		}
		return value;		
	}
	public Long getValue(HistoricAggregationProcedimentDto historic) {
		Long value = getValue((HistoricAggregation) historic);
		if (value != null ) {
			return value;
		}
		switch (this) {
		case ENVIAMENTS:
			return historic.getNumEnviaments();
		case GRUPS:
			return historic.getNumGrups();
		}
		return value;		
	}
	
	public String toString() {
		String value = "";
		switch (this) {
		case NOTIFICACIONS_TOTAL:
			return "Nombre de notificacions total";
		case NOTIFICACIONS_CORRECTES:
			return "Nombre de notificacions correctes";
		case NOTIFICACIONS_AMB_ERROR:
			return "Nombre de notificacions amb error";
		case NOTIFICACIONS_PROCEDIMENT_COMU:
			return "Nombre de notificacions de procediment comú";
		case NOTIFICACIONS_AMB_GRUP:
			return "Nombre de notificacions amb grup";
		case NOTIFICACIONS_ORIGEN_API:
			return "Nombre de notificacions origen api";
		case NOTIFICACIONS_ORIGEN_WEB:
			return "Nombre de notificacions origen web";
		case COMUNICACIONS_TOTAL:
			return "Nombre de comunicacions total";
		case COMUNICACIONS_CORRECTES:
			return "Nombre de comunicacions correctes";
		case COMUNICACIONS_AMB_ERROR:
			return "Nombre de comunicacions amb error";
		case COMUNICACIONS_PROCEDIMENT_COMU:
			return "Nombre de comunicacions procediment comú";
		case COMUNICACIONS_AMB_GRUP:
			return "Nombre de comunicacions amb grup";
		case COMUNICACIONS_ORIGEN_API:
			return "Nombre de comunicacions origen api";
		case COMUNICACIONS_ORIGEN_WEB:
			return "Nombre de comunicacions origen web";
		case ENVIAMENTS:
			return "Nombre d'enviaments";
		case PROCEDIMENTS:
			return "Nombre de procediments";
		case GRUPS:
			return "Nombre de grups";
		}
		return value;		
	}
}
