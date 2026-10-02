package es.caib.notib.logic.helper;

import es.caib.notib.persist.repository.NotificacioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.List;

/**
 * Crea el registre de not_notificacio_table de les remeses que no en tenen.
 *
 * Una remesa sense registre a not_notificacio_table no surt al llistat JSP (que consulta aquesta
 * taula), i el llistat de React, que hi fa un INNER JOIN, tampoc no la mostra; però el total del
 * llistat de React es compta sense el JOIN, i per ser exacte cal que totes les remeses en tinguin.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificacioTableReparacioHelper {

	private final NotificacioRepository notificacioRepository;
	private final NotificacioTableHelper notificacioTableHelper;

	/**
	 * @return true si totes les remeses tenen registre (si no, cal tornar-ho a intentar).
	 */
	public boolean crearRegistresQueFalten() {

		var ids = notificacioRepository.findIdsSenseRegistreTaula();
		if (ids.isEmpty()) {
			return true;
		}
		log.info("[NOTIF-TABLE] Creant el registre de not_notificacio_table de {} remeses que no en tenen", ids.size());
		// Com les tasques programades: amb l'usuari SCHEDULLER l'auditoria no intenta donar d'alta l'usuari
		// actual (el registre conserva l'usuari i la data de creació de la remesa)
		var authAnterior = SecurityContextHolder.getContext().getAuthentication();
		Principal principal = () -> "SCHEDULLER";
		SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
				principal, "N/A", List.of(new SimpleGrantedAuthority("NOT_SUPER"), new SimpleGrantedAuthority("NOT_ADMIN"))));
		var creats = 0;
		try {
			for (var id : ids) {
				try {
					// Una transacció per remesa: un error en una remesa no impedeix crear el registre de les altres
					notificacioTableHelper.crearRegistreEnTransaccioNova(id);
					creats++;
				} catch (Exception ex) {
					log.error("[NOTIF-TABLE] No s'ha pogut crear el registre de not_notificacio_table de la remesa " + id, ex);
				}
			}
		} finally {
			SecurityContextHolder.getContext().setAuthentication(authAnterior);
		}
		log.info("[NOTIF-TABLE] Creat el registre de not_notificacio_table de {} de {} remeses", creats, ids.size());
		return creats == ids.size();
	}

}
