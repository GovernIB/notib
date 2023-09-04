package es.caib.notib.persist.repository;

import es.caib.notib.logic.intf.dto.CallbackEstatEnumDto;
import es.caib.notib.persist.entity.CallbackEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface CallbackRepository extends JpaRepository<CallbackEntity, Long>  {


    @Query("select c.enviamentId from CallbackEntity c where c.estat = es.caib.notib.logic.intf.dto.CallbackEstatEnumDto.PENDENT order by c.data asc nulls first")
    List<Long> findEnviamentIdPendents(Pageable page);

    CallbackEntity findByEnviamentId(Long envId);

    CallbackEntity findByEnviamentIdAndEstat(Long envId, CallbackEstatEnumDto estat);

    List<CallbackEntity> findByNotificacioIdIn(Set<Long> notId);

    List<CallbackEntity> findByEnviamentIdIn(Set<Long> envId);

    List<CallbackEntity> findByNotificacioIdAndEstatOrderByDataDesc(Long notId, CallbackEstatEnumDto estat);

}