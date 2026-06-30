package es.caib.notib.persist.resourcerepository;

import es.caib.notib.logic.intf.dto.CallbackEstatEnumDto;
import es.caib.notib.persist.base.repository.BaseRepository;
import es.caib.notib.persist.entity.CallbackEntity;
import es.caib.notib.persist.resourceentity.CallbackResourceEntity;
import es.caib.notib.persist.resourceentity.DocumentResourceEntity;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Repositori per a la gestió dels callbacks d'una remesa.
 * @author Límit Tecnologies
 */
public interface CallbackResourceRepository extends BaseRepository<CallbackResourceEntity, Long> {

	List<CallbackResourceEntity> findByNotificacioIdAndEstatOrderByDataDesc(Long notId, CallbackEstatEnumDto estat);

	CallbackResourceEntity findByEnviamentIdAndEstat(Long envId, CallbackEstatEnumDto estat);

	@Query("SELECT c.id, c.data FROM CallbackResourceEntity c WHERE c.estat = es.caib.notib.logic.intf.dto.CallbackEstatEnumDto.PENDENT")
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
