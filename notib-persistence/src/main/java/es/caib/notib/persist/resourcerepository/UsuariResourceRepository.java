package es.caib.notib.persist.resourcerepository;

import es.caib.notib.logic.intf.dto.permis.PermisosUsuarisFiltre;
import es.caib.notib.persist.base.repository.BaseRepository;
import es.caib.notib.persist.resourceentity.UsuariResourceEntity;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Repositori per a la gestió d'entitats de tipus usuari de l'aplicació.
 *
 * @author Límit Tecnologies
 */
public interface UsuariResourceRepository extends BaseRepository<UsuariResourceEntity, String> {

	@Query(value = "from UsuariResourceEntity u where (:#{#filtre.usuariCodiNull} = true or u.id like '%' || :#{#filtre.usuariCodi} || '%')")
	List<UsuariResourceEntity> findByFiltre(PermisosUsuarisFiltre filtre);

	Optional<UsuariResourceEntity> findById(String codi);

}
