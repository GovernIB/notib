/**
 * 
 */
package es.caib.notib.plugin.unitat;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * Informació bàsica d'una oficina de registre
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class OficinaSIR implements Serializable, Comparable<OficinaSIR> {

	private String codi;
	private String nom;
	
	@Override
	public int compareTo(OficinaSIR o) {
		return codi.compareToIgnoreCase(o.getCodi());
	}
	
	private static final long serialVersionUID = -5789696564220350986L;

}
