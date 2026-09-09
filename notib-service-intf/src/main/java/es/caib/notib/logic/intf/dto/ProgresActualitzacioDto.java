package es.caib.notib.logic.intf.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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

	transient Consumer<ActualitzacioInfo> onInfo;
	transient Consumer<Integer> onProgressChanged;

	public void addInfo(TipusInfo tipus, String text) {

		log.info("[Progres Actualitzacio] " + text);
		var entry = new ActualitzacioInfo(tipus, text);
		info.add(entry);
		if (onInfo != null) {
			onInfo.accept(entry);
		}
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
		if (onProgressChanged != null) {
			onProgressChanged.accept(this.progres);
		}
	}
	
	@Getter @Setter @AllArgsConstructor @NoArgsConstructor
	public class ActualitzacioInfo {
		TipusInfo tipus;
		String text;
	}


}
