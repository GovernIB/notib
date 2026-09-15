package es.caib.notib.logic.procediments;

import es.caib.notib.logic.intf.dto.GrupDto;
import es.caib.notib.logic.intf.dto.cie.CieFormatFullaDto;
import es.caib.notib.logic.intf.dto.cie.CieFormatSobreDto;
import lombok.Data;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class DadesProcediment implements Serializable {

	private String caducitat;
	private Integer caducitatDiesNaturals;
	private Integer retard;
	private Long organCodi;
	private List<String> organsDisponibles;
	private boolean agrupable = false;
	private List<GrupDto> grups = new ArrayList<>();
	private List<CieFormatSobreDto> formatsSobre = new ArrayList<>();
	private List<CieFormatFullaDto> formatsFulla = new ArrayList<>();
	private boolean comu;
	private boolean entregaCieVigent;
	private boolean entregaCieActiva;

	DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
	public void setCaducitat(Date data) {
		this.caducitat = df.format(data);
	}
}
