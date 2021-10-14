package es.caib.notib.core.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class ProgresActualitzacioCertificacioDto {

	public enum TipusActInfo {
		TITOL,
		INFO,
		SUB_INFO,
		WARNING,
		ERROR
	}

	Integer progres = 0;
	Integer numEnviamentsExpirats;
	Integer numEnviamentsActualitzats = 0;
	List<ActualitzacioInfo> info = new ArrayList<ProgresActualitzacioCertificacioDto.ActualitzacioInfo>();
	
	boolean error = false;
	String errorMsg;
	
	public void addInfo(TipusActInfo tipus, String text) {
		info.add(new ActualitzacioInfo(tipus, text));
	}
	
	public void incrementProcedimentsActualitzats() {
		this.numEnviamentsActualitzats++;
		this.progres = (int) Math.round((this.numEnviamentsActualitzats.doubleValue() / this.numEnviamentsExpirats.doubleValue()) * 100);
	}
	
	@Getter @Setter @AllArgsConstructor @NoArgsConstructor
	public class ActualitzacioInfo {
		TipusActInfo tipus;
		String text;
	}
}
