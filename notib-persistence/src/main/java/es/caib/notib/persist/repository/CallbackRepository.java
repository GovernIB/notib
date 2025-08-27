package es.caib.notib.persist.repository;

import es.caib.notib.logic.intf.dto.CallbackEstatEnumDto;
import es.caib.notib.logic.intf.dto.callback.CallbackFiltre;
import es.caib.notib.persist.entity.CallbackEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public interface CallbackRepository extends JpaRepository<CallbackEntity, Long>  {


    @Query("select c.enviamentId from CallbackEntity c " +
            "where c.estat = es.caib.notib.logic.intf.dto.CallbackEstatEnumDto.PENDENT and c.pausat = false " +
            "order by c.data asc nulls first")
    List<Long> findEnviamentIdPendents(Pageable page);

    @Query("select c.enviamentId from CallbackEntity c " +
            "where c.estat = es.caib.notib.logic.intf.dto.CallbackEstatEnumDto.PENDENT and c.pausat = false " +
            "and c.ultimIntent = null and c.intents = 0")
    List<Long> findEnviamentIdPendentsNoEnviats();

    CallbackEntity findByEnviamentId(Long envId);

    CallbackEntity findByEnviamentIdAndEstat(Long envId, CallbackEstatEnumDto estat);

    List<CallbackEntity> findByNotificacioIdIn(Set<Long> notId);

    List<CallbackEntity> findByEnviamentIdIn(Set<Long> envId);

    List<CallbackEntity> findByNotificacioIdAndEstatOrderByDataDesc(Long notId, CallbackEstatEnumDto estat);

    @Query("select c from CallbackEntity c " +
            "join NotificacioEntity n on c.notificacioId = n.id " +
            "where n.entitat.id = :#{#filtre.entitatId} " +
            "   and (c.estat = es.caib.notib.logic.intf.dto.CallbackEstatEnumDto.PENDENT or c.error = true) " +
            "   and (:#{#filtre.estat} = true or c.estat = :#{#filtre.estat}) " +
            "   and (:#{#filtre.usuariCodiNull} = true or lower(c.usuariCodi) like concat('%', lower(:#{#filtre.usuariCodi}), '%'))" +
            "   and (:#{#filtre.referenciaRemesaNull} = true or lower(n.referencia) like concat('%', lower(:#{#filtre.referenciaRemesa}), '%'))" +
            "   and (:#{#filtre.dataIniciNull} = true or c.dataCreacio >= :#{#filtre.dataIniciDate}) " +
            "   and (:#{#filtre.dataFiNull} = true or c.dataCreacio <= :#{#filtre.dataFiDate}) "+
            "   and (:#{#filtre.dataIniciUltimIntentNull} = true or c.ultimIntent >= :#{#filtre.dataIniciUltimIntentDate}) " +
            "   and (:#{#filtre.dataFiUltimIntentNull} = true or c.ultimIntent <= :#{#filtre.dataFiUltimIntentDate}) "+
            "   and (:#{#filtre.fiReintentsNull} = true or (:#{#filtre.fiReintentsInt()} = 0 and c.intents < :#{#filtre.maxReintents} or :#{#filtre.fiReintentsInt()} = 1 and c.intents >= :#{#filtre.maxReintents})) ")
    Page<CallbackEntity> findPendentsByEntitat(CallbackFiltre filtre, Pageable page);

    @Query("from CallbackEntity c join NotificacioEntity n on c.notificacioId = n.id " +
            "   where c.estat = es.caib.notib.logic.intf.dto.CallbackEstatEnumDto.PENDENT ")
    Page<CallbackEntity> findPendents(Pageable page);



    @Query("SELECT c.id, c.data FROM CallbackEntity c WHERE c.estat = es.caib.notib.logic.intf.dto.CallbackEstatEnumDto.PENDENT")
    List<Object[]> findIdAndData();

    default Map<Long, Date> findIdAndAdjustedDate() {
        List<Object[]> results = findIdAndData();
        long currentTimeMillis = System.currentTimeMillis();

        return results.stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0], // id
                        row -> {
                            long count = results.stream()
                                    .filter(r -> ((Date) r[1]).before((Date) row[1]))
                                    .count();
                            if (count < 50) {
                                return new Date(currentTimeMillis);
                            } else {
                                long minutesToAdd = (count / 50);
                                return new Date(currentTimeMillis + (minutesToAdd * 60 * 1000));
                            }
                        }
                ));
    }



    }
