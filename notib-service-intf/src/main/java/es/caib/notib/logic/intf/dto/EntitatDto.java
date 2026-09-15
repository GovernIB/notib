package es.caib.notib.logic.intf.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

/**
 * Informació d'una entitat.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class EntitatDto extends EntitatDataDto {

	private boolean activa;
	private List<PermisDto> permisos;
	private boolean usuariActualAdministradorEntitat;
	private boolean usuariActualAdministradorLectura;
	private boolean usuariActualAdministradorOrgan;
	private Long numAplicacions;

	public String getLlibreCodiNom() {
		if (llibre != null)
			return llibre + " - " + (llibreNom != null ? llibreNom : "");
		return "";
	}

	public int getPermisosCount() {
		if  (permisos == null)
			return 0;
		else
			return permisos.size();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
