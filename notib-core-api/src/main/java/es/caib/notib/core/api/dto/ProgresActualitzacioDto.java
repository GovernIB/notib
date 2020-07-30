package es.caib.notib.core.api.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
public class ProgresActualitzacioDto {

	public enum TipusInfo {
		TITOL,
		SUBTITOL,
		INFO,
		SUBINFO,
		TEMPS,
		SEPARADOR,
		ERROR
	}

	Integer progres = 0;
	Integer numProcediments;
	Integer numProcedimentsActualitzats = 0;
	List<ActualitzacioInfo> info = new ArrayList<ProgresActualitzacioDto.ActualitzacioInfo>();
	
	boolean error = false;
	String errorMsg;
	
	public void addInfo(TipusInfo tipus, String text) {
		info.add(new ActualitzacioInfo(tipus, text));
	}
	
	public void addSeparador() {
		info.add(new ActualitzacioInfo(TipusInfo.SEPARADOR, ""));
	}
	
	public void incrementProcedimentsActualitzats() {
		this.numProcedimentsActualitzats++;
		this.progres = (this.numProcedimentsActualitzats * 100) / this.numProcediments;
	}
	
	@Getter @Setter @AllArgsConstructor @NoArgsConstructor
	public class ActualitzacioInfo {
		TipusInfo tipus;
		String text;
	}
}
