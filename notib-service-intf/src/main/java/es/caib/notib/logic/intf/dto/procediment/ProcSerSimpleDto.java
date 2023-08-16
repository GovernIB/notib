package es.caib.notib.logic.intf.dto.procediment;

import es.caib.notib.logic.intf.dto.AuditoriaDto;
import es.caib.notib.logic.intf.dto.ProcSerTipusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class ProcSerSimpleDto extends AuditoriaDto implements Serializable{

	private Long id;
	private String codi;
	private String nom;
	private boolean agrupar;
	private String llibre;
	private String llibreNom;
	private String oficina;
	private String oficinaNom;
	private String organGestor;
	private String organGestorNom;
	private String tipusAssumpte;
	private String tipusAssumpteNom;
	private String codiAssumpte;
	private String codiAssumpteNom;
	private boolean comu;
	private boolean manual;
	private Date ultimaActualitzacio;
	private boolean entregaCieActiva;
	private ProcSerTipusEnum tipus;
	
	public String getOrganGestorDesc() {
		if (organGestorNom != null && !organGestorNom.isEmpty())
			return organGestor + " - " + organGestorNom;
		return organGestor;
	}
	
	public String getDescripcio() {
		return codi + " - " + nom;
	}

}
