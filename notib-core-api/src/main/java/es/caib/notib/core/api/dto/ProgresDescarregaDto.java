package es.caib.notib.core.api.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
public class ProgresDescarregaDto {

	public enum TipusInfo {
		INFO,
		ERROR
	}

	Integer progres = 0;
	List<ActualitzacioInfo> info = new ArrayList<ProgresDescarregaDto.ActualitzacioInfo>();
	
	boolean error = false;
	String errorMsg;
	
	public void addInfo(TipusInfo tipus, String text) {
		info.add(new ActualitzacioInfo(tipus, text));
	}
	
	@Getter @Setter @AllArgsConstructor @NoArgsConstructor
	public class ActualitzacioInfo {
		TipusInfo tipus;
		String text;
	}
}
