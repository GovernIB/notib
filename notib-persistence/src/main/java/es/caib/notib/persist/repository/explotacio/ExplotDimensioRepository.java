package es.caib.notib.persist.repository.explotacio;

import es.caib.notib.persist.entity.explotacio.ExplotDimensio;
import es.caib.notib.persist.entity.explotacio.ExplotDimensioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ExplotDimensioRepository extends JpaRepository<ExplotDimensioEntity, Long> {

    @Query("from ExplotDimensioEntity order by entitatId, tipus, procedimentId, organCodi, usuariCodi, origen")
    List<ExplotDimensioEntity> findAllOrdered();

    @Query(   "SELECT new es.caib.notib.persist.entity.explotacio.ExplotDimensio("
            + "       n.entitat.id, "
            + "       n.entitat.codi, "
            + "       p.id, "
            + "       p.codi, "
            + "       n.organGestor.codi, "
            + "       CASE WHEN n.usuariCodi is null THEN 'DESCONEGUT' ELSE n.usuariCodi END, "
            + "       n.enviamentTipus, "
            + "       n.origen) "
            + "  FROM NotificacioEntity n "
            + "  LEFT OUTER JOIN n.procediment p "
            + " WHERE n.organGestor IS NOT NULL "
            + " GROUP BY n.entitat.id, n.entitat.codi, p.id, p.codi, n.organGestor.codi, n.usuariCodi, n.enviamentTipus, n.origen "
            + " ORDER BY n.entitat.id, p.id, n.organGestor.codi, n.usuariCodi, n.enviamentTipus, n.origen")
    public List<ExplotDimensio> getDimensionsPerEstadistiques();
}
