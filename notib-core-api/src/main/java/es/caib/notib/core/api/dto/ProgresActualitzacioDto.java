package es.caib.notib.core.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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
	Integer numOperacions;
	Integer numOperacionsRealitzades = 0;
	List<ActualitzacioInfo> info = new ArrayList<ProgresActualitzacioDto.ActualitzacioInfo>();
	boolean finished = false;
	
	boolean error = false;
	String errorMsg;
	
	public void addInfo(TipusInfo tipus, String text) {
		info.add(new ActualitzacioInfo(tipus, text));
	}
	
	public void addSeparador() {
		info.add(new ActualitzacioInfo(TipusInfo.SEPARADOR, ""));
	}
	
	public void incrementOperacionsRealitzades() {
		if (numOperacions == null) {
			return;
		}
		this.numOperacionsRealitzades++;
		double auxprogres = (this.numOperacionsRealitzades.doubleValue()  / this.numOperacions.doubleValue()) * 100;
		this.progres = (int) auxprogres;
	}
	
	@Getter @Setter @AllArgsConstructor @NoArgsConstructor
	public class ActualitzacioInfo {
		TipusInfo tipus;
		String text;
	}
}
