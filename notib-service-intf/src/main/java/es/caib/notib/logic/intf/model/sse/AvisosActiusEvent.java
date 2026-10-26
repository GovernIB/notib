package es.caib.notib.logic.intf.model.sse;

import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.model.AvisResource;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Esdeveniment amb els avisos actius que ha de veure un usuari.
 * <p/>
 * Quan els avisos canvien, {@code SseEventService.notifyAvisosActius} publica a la cua
 * {@code AVISOS} tots els avisos actius. Qui el rep ({@code SseController}) els filtra per a
 * cada connexió amb {@link #filtrar}, perquè el resultat depèn del rol i de l'entitat de cada
 * connexió, i envia per SSE un d'aquests esdeveniments amb la llista ja filtrada.
 *
 * @author Límit Tecnologies
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AvisosActiusEvent implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<AvisResource> avisos;

	/**
	 * Filtra els avisos actius que ha de veure un usuari que opera amb el rol i l'entitat
	 * indicats: amb el rol de superusuari tots; amb qualsevol altre rol només els globals (sense
	 * entitat) i els de l'entitat de treball, i els marcats com a d'administrador només si el rol
	 * és el d'administrador d'entitat (igual que la interfície JSP).
	 *
	 * @param avisos tots els avisos actius.
	 * @param rol rol amb què opera l'usuari.
	 * @param entitatId identificador de l'entitat de treball, o null si no n'hi ha cap.
	 * @return l'esdeveniment amb els avisos filtrats.
	 */
	public static AvisosActiusEvent filtrar(List<AvisResource> avisos, String rol, Long entitatId) {

		if (BaseConfig.ROLE_SUPER.equals(rol)) {
			return new AvisosActiusEvent(avisos);
		}
		boolean admin = BaseConfig.ROLE_ADMIN.equals(rol);
		return new AvisosActiusEvent(avisos.stream()
			.filter(avis -> avis.getEntitat() == null || avis.getEntitat().getId().equals(entitatId))
			.filter(avis -> admin || !avis.isAvisAdministrador())
			.collect(Collectors.toList()));
	}

}
