package es.caib.notib.persist.resourcerepository;

import es.caib.notib.logic.intf.dto.CallbackEstatEnumDto;
import es.caib.notib.persist.base.repository.BaseRepository;
import es.caib.notib.persist.entity.CallbackEntity;
import es.caib.notib.persist.resourceentity.CallbackResourceEntity;
import es.caib.notib.persist.resourceentity.DocumentResourceEntity;

import java.util.List;

/**
 * Repositori per a la gestió dels callbacks d'una remesa.
 * @author Límit Tecnologies
 */
public interface CallbackResourceRepository extends BaseRepository<CallbackResourceEntity, Long> {

	List<CallbackResourceEntity> findByNotificacioIdAndEstatOrderByDataDesc(Long notId, CallbackEstatEnumDto estat);

	CallbackResourceEntity findByEnviamentIdAndEstat(Long envId, CallbackEstatEnumDto estat);
}
