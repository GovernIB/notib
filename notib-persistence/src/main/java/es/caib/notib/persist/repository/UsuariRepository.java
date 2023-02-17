/**
 * 
 */
package es.caib.notib.persist.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.notib.persist.entity.UsuariEntity;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus usuari.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface UsuariRepository extends JpaRepository<UsuariEntity, String> {
	
	public UsuariEntity findByCodi(String codi);

	@Query("select u from UsuariEntity u "
			+ "where lower(u.nom) like concat('%', lower(:text), '%') "
			+ "or lower(u.codi) like concat('%', lower(:text), '%')"
			+ "order by u.nom desc")
	List<UsuariEntity> findByText(@Param("text") String text);

	@Query("select u.idioma from UsuariEntity u where u.codi = :codi")
	String getIdiomaUsuari(@Param("codi") String codi);
}
