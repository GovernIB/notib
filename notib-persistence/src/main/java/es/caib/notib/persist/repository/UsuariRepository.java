/**
 * 
 */
package es.caib.notib.persist.repository;

import es.caib.notib.persist.entity.UsuariEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface UsuariRepository extends JpaRepository<UsuariEntity, String> {

	UsuariEntity findByCodi(String codi);

	@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
	@Query("from UsuariEntity u where u.codi = :codi")
	Optional<UsuariEntity> getByCodiReadOnlyNewTransaction(@Param("codi") String codi);


	@Query(   "select u from UsuariEntity u "
			+ "where lower(u.nom) like concat('%', lower(:text), '%') "
			+ "or (u.codi) like concat('%', lower(:text), '%')"
			+ "order by u.nom desc")
	List<UsuariEntity> findByText(@Param("text") String text);

	@Query("select u.idioma from UsuariEntity u where u.codi = :codi")
	String getIdiomaUsuari(@Param("codi") String codi);

	@Query(value = "SELECT DISTINCT rol FROM  (SELECT nas.SID AS rol  FROM NOT_ACL_SID nas WHERE nas.PRINCIPAL = 0 " +
			"UNION SELECT ng.CODI AS rol FROM NOT_GRUP ng)", nativeQuery = true)
	List<String> getNotibRolsDisponibles(@Param("codi") String codi);

}
