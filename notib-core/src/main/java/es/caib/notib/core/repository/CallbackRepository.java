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

}
