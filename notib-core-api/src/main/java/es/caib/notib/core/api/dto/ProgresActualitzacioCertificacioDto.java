package es.caib.notib.core.api.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
public class ProgresActualitzacioCertificacioDto {

	public enum TipusActInfo {
		TITOL,
		INFO,
		SUB_INFO,
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
		this.progres = (this.numEnviamentsActualitzats * 100) / this.numEnviamentsExpirats;
	}
	
	@Getter @Setter @AllArgsConstructor @NoArgsConstructor
	public class ActualitzacioInfo {
		TipusActInfo tipus;
		String text;
	}
}
