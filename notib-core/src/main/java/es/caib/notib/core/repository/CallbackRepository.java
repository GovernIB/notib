package es.caib.notib.core.repository;

import es.caib.notib.core.entity.CallbackEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CallbackRepository extends JpaRepository<CallbackEntity, Long>  {


    @Query("select c.id from CallbackEntity c order by c.data asc nulls first")
    List<Long> findPendents(Pageable page);

    CallbackEntity findByEnviamentId(Long envId);

//	@Query(" select ce.id from CallbackEntity ce " +
//			" where ce.estat = PENDENT " +
//			"and ce.notificacio.id = :notificacioId " +
//			"order by ce.data asc nulls first")
//	List<Long> findPendentByNotificacioId(@Param("notificacioId") Long notificacioId);
//	List<Long> findPendentByEnviamentId(@Param("enviamentId") Long enviamentId);
}
