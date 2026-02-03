package es.caib.notib.logic.intf.model;

import es.caib.notib.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.notib.logic.intf.base.annotation.ResourceArtifact;
import es.caib.notib.logic.intf.base.annotation.ResourceConfig;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.model.BaseResource;
import es.caib.notib.logic.intf.base.model.ResourceArtifactType;
import es.caib.notib.logic.intf.base.permission.PermissionEnum;
import es.caib.notib.logic.intf.dto.PermisDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
	descriptionField = OrganGestorResource.Fields.codi,
	quickFilterFields = { OrganGestorResource.Fields.codi, OrganGestorResource.Fields.nom },
	artifacts = {
		@ResourceArtifact(
			type = ResourceArtifactType.ACTION,
			code = OrganGestorResource.DIR3_SYNC_ACTION_CODE,
			formClass = OrganGestorResource.OrganGestorDir3SyncForm.class,
			accessConstraints = {
				@ResourceAccessConstraint(
					type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
					roles = { BaseConfig.ROLE_ADMIN })
			})
	},
	accessConstraints = @ResourceAccessConstraint(
		type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
		roles = { BaseConfig.ROLE_ADMIN, BaseConfig.ROLE_SUPER },
		grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE, PermissionEnum.CREATE, PermissionEnum.DELETE }
	)
)
public class OrganGestorResource extends BaseResource<Long> {

	public static final String DIR3_SYNC_ACTION_CODE = "DIR3_SYNC";

	@NotEmpty
	@Size(max = 64)
	private String codi;
	@NotEmpty
	@Size(max = 1000)
	private String nom;
	@Size(max = 64)
	private String codiPare;
	@Size(max = 1000)
	private String nomPare;
	private String nomEs;
	private Long entitatId;
	private String entitatNom;
	private String llibre;
	private String llibreNom;
	private String oficina;
	private String oficinaNom;
	private List<PermisDto> permisos;
	private OrganGestorEstatEnum estat = null;

	private Boolean sir;
	private String cif;
	private boolean actiu;
	private boolean permetreSir;

	private String nomCodi;

	private boolean entregaCieDesactivada;
	private boolean entregaCieActiva;
	private Long operadorPostalId;
	private Long cieId;
	private boolean sobrescriureCieOrganEmisor;

	private String estatTraduccio;

	public void setOrganGestorEstatEnum(OrganGestorEstatEnum estat) {
		this.estat = estat;
		actiu = estat != null && OrganGestorEstatEnum.V.equals(estat);
	}

	public String getNomCodi() {
		return nom + " (" + codi + ")";
	}

	public String getCodiNom() {
		return codi + " - " + nom;
	}

	public String getLlibreCodiNom() {
		if (llibre != null)
			return llibre + " " + (llibreNom != null ? llibreNom : "");
		return "";
	}

//        public String getOficinaCodiNom() {
//                if (oficina != null)
//                        return oficina.getCodi() + " " + (oficina.getNom() != null ? oficina.getNom() : "");
//                return "";
//        }

	public int getPermisosCount() {
		if (permisos == null)
			return 0;
		else
			return permisos.size();
	}

	public String getOrganGestorDesc() {
		if (nom != null && !nom.isEmpty())
			return codi + " - " + nom;
		return codi;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	public static class OrganGestorDir3SyncForm implements Serializable {
		private Boolean real;
	}

	@Getter
	@Setter
	@RequiredArgsConstructor
	public static class OrganGestorDir3SyncResult implements Serializable {
		private final int numSubstitucions;
		private final int numDivisions;
		private final int numFusions;
		private final int numExtincions;
		private final boolean simulat;
	}

}
