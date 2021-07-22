package es.caib.notib.core.api.dto.procediment;

import es.caib.notib.core.api.dto.AuditoriaDto;
import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.GrupDto;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter @Setter
public class ProcedimentDataDto extends AuditoriaDto implements Serializable {

	protected Long id;
	protected String codi;
	protected String nom;
	protected EntitatDto entitat;
	protected boolean agrupar;
	protected List<GrupDto> grups;
	protected String llibre;
	protected String llibreNom;
	protected String oficina;
	protected String oficinaNom;
	protected String organGestor;
	protected String organGestorNom;
	protected String tipusAssumpte;
	protected String tipusAssumpteNom;
	protected String codiAssumpte;
	protected String codiAssumpteNom;
	protected boolean comu;
	protected boolean requireDirectPermission;
	protected Date ultimaActualitzacio;

	protected int retard;
	protected int caducitat;

	protected boolean entregaCieActiva;
	protected Long operadorPostalId;
	protected Long cieId;

}
