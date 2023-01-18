package es.caib.notib.logic.intf.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
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

	int fase = 0;
	Integer progres = 0;
	Integer numOperacions;
	Integer numOperacionsRealitzades = 0;
	List<ActualitzacioInfo> info = new ArrayList<ProgresActualitzacioDto.ActualitzacioInfo>();
	boolean finished = false;

	boolean error = false;
	String errorMsg;

	public void addInfo(TipusInfo tipus, String text) {

		log.info("[Progres Actualitzacio] " + text);
		info.add(new ActualitzacioInfo(tipus, text));
	}
	
	public void addSeparador() {
		info.add(new ActualitzacioInfo(TipusInfo.SEPARADOR, ""));
	}

	public void incrementOperacionsRealitzades() {
		incrementOperacionsRealitzades(1);
	}
	public void incrementOperacionsRealitzades(int numOperacions) {
		if (this.numOperacions == null) {
			return;
		}
		this.numOperacionsRealitzades += numOperacions;
		double auxprogres = (this.numOperacionsRealitzades.doubleValue()  / this.numOperacions.doubleValue()) * 100;
		this.progres = (int) auxprogres;
	}
	
	@Getter @Setter @AllArgsConstructor @NoArgsConstructor
	public class ActualitzacioInfo {
		TipusInfo tipus;
		String text;
	}
}