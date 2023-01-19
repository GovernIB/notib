/**
 * 
 */
package es.caib.notib.plugin.unitat;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Informació bàsica d'una oficina de registre
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OficinaSir implements Serializable, Comparable<OficinaSir> {

	private String codi;
	private String nom;
	private String organCodi;
	private boolean sir;
	
	@Override
	public int compareTo(OficinaSir o) {
		return codi.compareToIgnoreCase(o.getCodi());
	}
	
	private static final long serialVersionUID = -5789696564220350986L;

}
