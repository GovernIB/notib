package es.caib.notib.persist.repository;

import es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaFiltre;
import es.caib.notib.persist.entity.AccioMassivaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AccioMassivaRepository extends JpaRepository<AccioMassivaEntity, Long>  {


    @Query("from AccioMassivaEntity a where a.entitatId = :#{#filtre.entitatId} " +
            "and (:#{#filtre.usuariCodiNull} = true or a.createdBy.codi = :#{#filtre.usuariCodi}) " +
            "and (:#{#filtre.dataIniciNull} = true or a.createdDate >= :#{#filtre.dataIniciDate}) " +
            "and (:#{#filtre.dataFiNull} = true or a.createdDate <= :#{#filtre.dataFiDate}) "+
            "and (:#{#filtre.tipusNull} = true or a.tipus =  :#{#filtre.tipus}) " +
//            "and (:#{#filtre.estatNull} = true or " +
//            " (CASE WHEN :#{#filtre.estat} = 'FINALITZAT' THEN a.parametres.seleccio like '%' || 'errorDesc\":\"\"' || '%' END))" +
            " ")
    Page<AccioMassivaEntity> findAmbFiltre(AccioMassivaFiltre filtre, Pageable paginacio);
}
