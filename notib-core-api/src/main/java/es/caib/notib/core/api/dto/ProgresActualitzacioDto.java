package es.caib.notib.core.api.dto;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProgresActualitzacioDto {

	Integer progres;
	Integer numProcediments;
	Integer numProcedimentsActualitzats;
	Map<ProcedimentDto, String> missatges = new HashMap<ProcedimentDto, String>();
	
	boolean error = false;
	String errorMsg;
}
