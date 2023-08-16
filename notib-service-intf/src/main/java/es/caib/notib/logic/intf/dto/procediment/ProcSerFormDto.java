package es.caib.notib.logic.intf.dto.procediment;

import es.caib.notib.logic.intf.dto.AuditoriaDto;
import es.caib.notib.logic.intf.dto.GrupDto;
import es.caib.notib.logic.intf.dto.PermisDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter @Setter
public class ProcSerFormDto extends AuditoriaDto implements Serializable{

	private Long id;
	private String codi;
	private String nom;
	private String entitatNom;
	private String organGestor;
	private String organGestorNom;
	private OrganGestorEstatEnum organGestorEstat;
	private boolean agrupar;
	private Integer retard;
	protected Integer caducitat;
	private List<PermisDto> permisos;
	private List<GrupDto> grups;
	private boolean comu;
	private boolean manual;

	// 0 (Inactiva) / 1 (Activa per procediment) / 2 (Activa per òrgan gestor) / 3 (Activa per entitat)
	protected int entregaCieActiva;
	protected boolean requireDirectPermission;
	private boolean organNoSincronitzat;
	private boolean actiu;

	public int getPermisosCount() {
		if  (permisos == null)
			return 0;
		else
			return permisos.size();
	}
	public int getGrupsCount() {
		if  (grups == null)
			return 0;
		else
			return grups.size();
	}
	
	public String getOrganGestorDesc() {
		if (organGestorNom != null && !organGestorNom.isEmpty())
			return organGestor + " - " + organGestorNom;
		return organGestor;
	}

	private static final long serialVersionUID = 6058789232924135932L;

}
