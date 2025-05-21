package es.caib.notib.persist.repository.explotacio;

import es.caib.notib.persist.entity.explotacio.ExplotEnvStats;
import es.caib.notib.persist.entity.explotacio.ExplotFets;
import es.caib.notib.persist.entity.explotacio.ExplotFetsEntity;
import es.caib.notib.persist.entity.explotacio.ExplotTempsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface ExplotFetsRepository extends JpaRepository<ExplotFetsEntity, Long> {

    public void deleteAllByTemps(ExplotTempsEntity ete);

    @Query(	"select new es.caib.notib.persist.entity.explotacio.ExplotFets( " +
            "    n.entitat.id, " +                                                                                                                              // entitatId
            "    n.procediment.id, " +                                                                                                                          // procedimentId
            "    n.organGestor.codi, " +                                                                                                                        // organCodi
            "    n.usuariCodi, " +                                                                                                                              // usuariCodi
            "    n.enviamentTipus, " +                                                                                                                          // tipus
            "    n.origen, " +                                                                                                                                  // origen
            "    sum(case when e.notificaEstat = es.caib.notib.client.domini.EnviamentEstat.NOTIB_PENDENT and e.notificaError = false then 1 else 0 end), " +   // pendent
            "    sum(case when e.notificaEstat = es.caib.notib.client.domini.EnviamentEstat.NOTIB_PENDENT and e.notificaError = true then 1 else 0 end), " +    // regEnviamentError
            "    sum(case when (e.notificaEstat = es.caib.notib.client.domini.EnviamentEstat.REGISTRADA and e.notificaError = false) " +
            "               or (n.enviamentTipus = es.caib.notib.client.domini.EnviamentTipus.SIR " +
            "                   and (e.notificaEstat = es.caib.notib.logic.intf.dto.NotificacioRegistreEstatEnumDto.OFICI_SIR " +
            "                     or e.notificaEstat = es.caib.notib.logic.intf.dto.NotificacioRegistreEstatEnumDto.REENVIAT)) then 1 else 0 end), " +          // registrada
            "    sum(case when n.enviamentTipus = es.caib.notib.client.domini.EnviamentTipus.SIR " +
            "               and e.registreEstat = es.caib.notib.logic.intf.dto.NotificacioRegistreEstatEnumDto.OFICI_ACCEPTAT then 1 else 0 end), " +           // regAcceptada
            "    sum(case when n.enviamentTipus = es.caib.notib.client.domini.EnviamentTipus.SIR " +
            "               and e.registreEstat = es.caib.notib.logic.intf.dto.NotificacioRegistreEstatEnumDto.REBUTJAT then 1 else 0 end), " +                 // regRebutjada
            "    sum(case when e.notificaEstat = es.caib.notib.client.domini.EnviamentEstat.REGISTRADA and e.notificaError = true then 1 else 0 end), " +       // notEnviamentError
            "    sum(case when (e.notificaEstat = es.caib.notib.client.domini.EnviamentEstat.NOTIB_ENVIADA " +
            "               or e.notificaEstat = es.caib.notib.client.domini.EnviamentEstat.ENVIADA_CI " +
            "               or e.notificaEstat = es.caib.notib.client.domini.EnviamentEstat.ENVIADA_DEH " +
            "               or e.notificaEstat = es.caib.notib.client.domini.EnviamentEstat.ENTREGADA_OP " +
            "               or e.notificaEstat = es.caib.notib.client.domini.EnviamentEstat.PENDENT_ENVIAMENT " +
            "               or e.notificaEstat = es.caib.notib.client.domini.EnviamentEstat.PENDENT_SEU " +
            "               or e.notificaEstat = es.caib.notib.client.domini.EnviamentEstat.PENDENT_CIE " +
            "               or e.notificaEstat = es.caib.notib.client.domini.EnviamentEstat.PENDENT_DEH ) and e.notificaError = false then 1 else 0 end), " +   // notEnviada
            "    sum(case when e.notificaEstat = es.caib.notib.client.domini.EnviamentEstat.NOTIFICADA " +
            "               or e.notificaEstat = es.caib.notib.client.domini.EnviamentEstat.LLEGIDA then 1 else 0 end), " +                                     // notNotificada
            "    sum(case when e.notificaEstat = es.caib.notib.client.domini.EnviamentEstat.REBUTJADA then 1 else 0 end), " +                                   // notRebutjada
            "    sum(case when e.notificaEstat = es.caib.notib.client.domini.EnviamentEstat.EXPIRADA then 1 else 0 end), " +                                    // notExpirada
            "    sum(case when ep is not null and ep.cieEstat = es.caib.notib.client.domini.CieEstat.ERROR then 1 else 0 end), " +                              // cieEnviamentError
            "    sum(case when ep is not null and (ep.cieEstat = es.caib.notib.client.domini.CieEstat.ENVIADO_CI " +
            "                                   or ep.cieEstat = es.caib.notib.client.domini.CieEstat.ENTREGADO_OP) then 1 else 0 end), " +                     // cieEnviada
            "    sum(case when ep is not null and ep.cieEstat = es.caib.notib.client.domini.CieEstat.NOTIFICADA then 1 else 0 end), " +                         // cieNotificada
            "    sum(case when ep is not null and ep.cieEstat = es.caib.notib.client.domini.CieEstat.REHUSADA then 1 else 0 end), " +                           // cieRebutjada
            "    sum(case when ep is not null and (ep.cieEstat = es.caib.notib.client.domini.CieEstat.EXTRAVIADA " +
            "                                   or ep.cieEstat = es.caib.notib.client.domini.CieEstat.SIN_INFORMACION " +
            "                                   or ep.cieEstat = es.caib.notib.client.domini.CieEstat.AUSENTE " +
            "                                   or ep.cieEstat = es.caib.notib.client.domini.CieEstat.DESCONOCIDO " +
            "                                   or ep.cieEstat = es.caib.notib.client.domini.CieEstat.DIRECCION_INCORRECTA " +
            "                                   or ep.cieEstat = es.caib.notib.client.domini.CieEstat.FALLECIDO " +
            "                                   or ep.cieEstat = es.caib.notib.client.domini.CieEstat.DEVUELTO) then 1 else 0 end), " +                         // cieError
            "    sum(case when n.estat = es.caib.notib.client.domini.NotificacioEstatEnum.PROCESSADA then 1 else 0 end)) " +                                    // processada
            "from NotificacioEnviamentEntity e " +
            "     left outer join e.notificacio n " +
            "     left outer join e.entregaPostal ep " +
            "where (:esNullData = true or n.createdDate <= :data) " +
            "  and n.procediment.id is not null " +
            "  and n.organGestor is not null " +
            "group by " +
            "    n.entitat.id, " +
            "    n.procediment.id, " +
            "    n.organGestor.codi, " +
            "    n.usuariCodi, " +
            "    n.enviamentTipus, " +
            "    n.origen " +
            "order by " +
            "	 n.entitat.id, " +
            "	 n.procediment.id, " +
            "    n.organGestor.codi, " +
            "    n.usuariCodi, " +
            "	 n.enviamentTipus, " +
            "    n.origen")
    List<ExplotFets> getFetsPerEstadistiques(
            @Param("esNullData") boolean esNullData,
            @Param("data") Date data);

    @Query("from ExplotFetsEntity where temps = :temps order by dimensio.entitatId, dimensio.organCodi, dimensio.procedimentId, dimensio.tipus, dimensio.origen, dimensio.usuariCodi")
    List<ExplotFetsEntity> findByTemps(@Param("temps") ExplotTempsEntity temps);




    @Query("SELECT new es.caib.notib.persist.entity.explotacio.ExplotEnvStats( " +
            "    e.entitatId, " +
            "    e.procedimentId, " +
            "    e.organGestorCodi, " +
            "    e.usuariCodi, " +
            "    e.enviamentTipus, " +
            "    e.origen, " +
            "    COUNT(e), " +
            "    cast(null AS java.lang.Double), " +
            "    cast(null AS java.lang.Double), " +
            "    cast(null AS java.lang.Double) " +
            ") " +
            "FROM ExplotEnvInfoEntity e " +
            "WHERE e.dataCreacio IS NOT NULL AND e.dataCreacio >= :iniciDelDia AND e.dataCreacio < :finalDelDia " +
            "GROUP BY e.entitatId, e.procedimentId, e.organGestorCodi, e.usuariCodi, e.enviamentTipus, e.origen " +
            "ORDER BY e.entitatId, e.procedimentId, e.organGestorCodi, e.usuariCodi, e.enviamentTipus, e.origen ")
    List<ExplotEnvStats> getStatsCreacioPerDay(@Param("iniciDelDia") Date iniciDelDia, @Param("finalDelDia") Date finalDelDia
);

    @Query("SELECT new es.caib.notib.persist.entity.explotacio.ExplotEnvStats( " +
            "    e.entitatId, " +
            "    e.procedimentId, " +
            "    e.organGestorCodi, " +
            "    e.usuariCodi, " +
            "    e.enviamentTipus, " +
            "    e.origen, " +
            "    COUNT(e), " +
            "    AVG(e.tempsPendent), " +
            "    AVG(CASE WHEN e.intentsRegEnviament > 1 THEN e.intentsRegEnviament ELSE null END), " +
            "    cast(null AS java.lang.Double) " +
            ") " +
            "FROM ExplotEnvInfoEntity e " +
            "WHERE e.dataRegistrada IS NOT NULL AND e.dataRegistrada >= :iniciDelDia AND e.dataRegistrada < :finalDelDia " +
            "GROUP BY e.entitatId, e.procedimentId, e.organGestorCodi, e.usuariCodi, e.enviamentTipus, e.origen " +
            "ORDER BY e.entitatId, e.procedimentId, e.organGestorCodi, e.usuariCodi, e.enviamentTipus, e.origen ")
    List<ExplotEnvStats> getStatsRegistradaPerDay(@Param("iniciDelDia") Date iniciDelDia, @Param("finalDelDia") Date finalDelDia
);

    @Query("SELECT new es.caib.notib.persist.entity.explotacio.ExplotEnvStats( " +
            "    e.entitatId, " +
            "    e.procedimentId, " +
            "    e.organGestorCodi, " +
            "    e.usuariCodi, " +
            "    e.enviamentTipus, " +
            "    e.origen, " +
            "    COUNT(e), " +
            "    cast(null AS java.lang.Double), " +
            "    AVG(e.intentsRegEnviament), " +
            "    cast(null AS java.lang.Double) " +
            ") " +
            "FROM ExplotEnvInfoEntity e " +
            "WHERE e.dataRegEnviamentError IS NOT NULL AND e.dataRegEnviamentError >= :iniciDelDia AND e.dataRegEnviamentError < :finalDelDia " +
            "GROUP BY e.entitatId, e.procedimentId, e.organGestorCodi, e.usuariCodi, e.enviamentTipus, e.origen " +
            "ORDER BY e.entitatId, e.procedimentId, e.organGestorCodi, e.usuariCodi, e.enviamentTipus, e.origen ")
    List<ExplotEnvStats> getStatsRegEnviamentErrorPerDay(@Param("iniciDelDia") Date iniciDelDia, @Param("finalDelDia") Date finalDelDia
);

    @Query("SELECT new es.caib.notib.persist.entity.explotacio.ExplotEnvStats( " +
            "    e.entitatId, " +
            "    e.procedimentId, " +
            "    e.organGestorCodi, " +
            "    e.usuariCodi, " +
            "    e.enviamentTipus, " +
            "    e.origen, " +
            "    COUNT(e), " +
            "    AVG(e.tempsRegistrada), " +
            "    AVG(CASE WHEN e.intentsSirConsulta > 1 THEN e.intentsSirConsulta ELSE null END), " +
            "    cast(null AS java.lang.Double) " +
            ") " +
            "FROM ExplotEnvInfoEntity e " +
            "WHERE e.dataRegAcceptada IS NOT NULL AND e.dataRegAcceptada >= :iniciDelDia AND e.dataRegAcceptada < :finalDelDia " +
            "GROUP BY e.entitatId, e.procedimentId, e.organGestorCodi, e.usuariCodi, e.enviamentTipus, e.origen " +
            "ORDER BY e.entitatId, e.procedimentId, e.organGestorCodi, e.usuariCodi, e.enviamentTipus, e.origen ")
    List<ExplotEnvStats> getStatsRegAcceptadaPerDay(@Param("iniciDelDia") Date iniciDelDia, @Param("finalDelDia") Date finalDelDia
);

    @Query("SELECT new es.caib.notib.persist.entity.explotacio.ExplotEnvStats( " +
            "    e.entitatId, " +
            "    e.procedimentId, " +
            "    e.organGestorCodi, " +
            "    e.usuariCodi, " +
            "    e.enviamentTipus, " +
            "    e.origen, " +
            "    COUNT(e), " +
            "    AVG(e.tempsRegistrada), " +
            "    AVG(CASE WHEN e.intentsSirConsulta > 1 THEN e.intentsSirConsulta ELSE null END), " +
            "    cast(null AS java.lang.Double) " +
            ") " +
            "FROM ExplotEnvInfoEntity e " +
            "WHERE e.dataRegRebutjada IS NOT NULL AND e.dataRegRebutjada >= :iniciDelDia AND e.dataRegRebutjada < :finalDelDia " +
            "GROUP BY e.entitatId, e.procedimentId, e.organGestorCodi, e.usuariCodi, e.enviamentTipus, e.origen " +
            "ORDER BY e.entitatId, e.procedimentId, e.organGestorCodi, e.usuariCodi, e.enviamentTipus, e.origen ")
    List<ExplotEnvStats> getStatsRegRebutjadaPerDay(@Param("iniciDelDia") Date iniciDelDia, @Param("finalDelDia") Date finalDelDia
);

    @Query("SELECT new es.caib.notib.persist.entity.explotacio.ExplotEnvStats( " +
            "    e.entitatId, " +
            "    e.procedimentId, " +
            "    e.organGestorCodi, " +
            "    e.usuariCodi, " +
            "    e.enviamentTipus, " +
            "    e.origen, " +
            "    COUNT(e), " +
            "    cast(null AS java.lang.Double), " +
            "    AVG(e.intentsNotEnviament), " +
            "    cast(null AS java.lang.Double) " +
            ") " +
            "FROM ExplotEnvInfoEntity e " +
            "WHERE e.dataNotEnviamentError IS NOT NULL AND e.dataNotEnviamentError >= :iniciDelDia AND e.dataNotEnviamentError < :finalDelDia " +
            "GROUP BY e.entitatId, e.procedimentId, e.organGestorCodi, e.usuariCodi, e.enviamentTipus, e.origen " +
            "ORDER BY e.entitatId, e.procedimentId, e.organGestorCodi, e.usuariCodi, e.enviamentTipus, e.origen ")
    List<ExplotEnvStats> getStatsNotEnviamentErrorPerDay(@Param("iniciDelDia") Date iniciDelDia, @Param("finalDelDia") Date finalDelDia
);

    @Query("SELECT new es.caib.notib.persist.entity.explotacio.ExplotEnvStats( " +
            "    e.entitatId, " +
            "    e.procedimentId, " +
            "    e.organGestorCodi, " +
            "    e.usuariCodi, " +
            "    e.enviamentTipus, " +
            "    e.origen, " +
            "    COUNT(e), " +
            "    AVG(e.tempsRegistrada), " +
            "    AVG(CASE WHEN e.intentsNotEnviament > 1 THEN e.intentsNotEnviament ELSE null END)," +
            "    cast(null AS java.lang.Double) " +
            ") " +
            "FROM ExplotEnvInfoEntity e " +
            "WHERE e.dataNotEnviada IS NOT NULL AND e.dataNotEnviada >= :iniciDelDia AND e.dataNotEnviada < :finalDelDia " +
            "GROUP BY e.entitatId, e.procedimentId, e.organGestorCodi, e.usuariCodi, e.enviamentTipus, e.origen " +
            "ORDER BY e.entitatId, e.procedimentId, e.organGestorCodi, e.usuariCodi, e.enviamentTipus, e.origen ")
    List<ExplotEnvStats> getStatsNotEnviadaPerDay(@Param("iniciDelDia") Date iniciDelDia, @Param("finalDelDia") Date finalDelDia
);

    @Query("SELECT new es.caib.notib.persist.entity.explotacio.ExplotEnvStats( " +
            "    e.entitatId, " +
            "    e.procedimentId, " +
            "    e.organGestorCodi, " +
            "    e.usuariCodi, " +
            "    e.enviamentTipus, " +
            "    e.origen, " +
            "    COUNT(e), " +
            "    AVG(e.tempsNotEnviada), " +
            "    cast(null AS java.lang.Double), " +
            "    AVG(e.tempsTotal) " +
            ") " +
            "FROM ExplotEnvInfoEntity e " +
            "WHERE e.dataNotNotificada IS NOT NULL AND e.dataNotNotificada >= :iniciDelDia AND e.dataNotNotificada < :finalDelDia " +
            "GROUP BY e.entitatId, e.procedimentId, e.organGestorCodi, e.usuariCodi, e.enviamentTipus, e.origen " +
            "ORDER BY e.entitatId, e.procedimentId, e.organGestorCodi, e.usuariCodi, e.enviamentTipus, e.origen ")
    List<ExplotEnvStats> getStatsNotNotificadaPerDay(@Param("iniciDelDia") Date iniciDelDia, @Param("finalDelDia") Date finalDelDia
);

    @Query("SELECT new es.caib.notib.persist.entity.explotacio.ExplotEnvStats( " +
            "    e.entitatId, " +
            "    e.procedimentId, " +
            "    e.organGestorCodi, " +
            "    e.usuariCodi, " +
            "    e.enviamentTipus, " +
            "    e.origen, " +
            "    COUNT(e), " +
            "    AVG(e.tempsNotEnviada), " +
            "    cast(null AS java.lang.Double), " +
            "    AVG(e.tempsTotal) " +
            ") " +
            "FROM ExplotEnvInfoEntity e " +
            "WHERE e.dataNotRebutjada IS NOT NULL AND e.dataNotRebutjada >= :iniciDelDia AND e.dataNotRebutjada < :finalDelDia " +
            "GROUP BY e.entitatId, e.procedimentId, e.organGestorCodi, e.usuariCodi, e.enviamentTipus, e.origen " +
            "ORDER BY e.entitatId, e.procedimentId, e.organGestorCodi, e.usuariCodi, e.enviamentTipus, e.origen ")
    List<ExplotEnvStats> getStatsNotRebutjadaPerDay(@Param("iniciDelDia") Date iniciDelDia, @Param("finalDelDia") Date finalDelDia
);

    @Query("SELECT new es.caib.notib.persist.entity.explotacio.ExplotEnvStats( " +
            "    e.entitatId, " +
            "    e.procedimentId, " +
            "    e.organGestorCodi, " +
            "    e.usuariCodi, " +
            "    e.enviamentTipus, " +
            "    e.origen, " +
            "    COUNT(e), " +
            "    AVG(e.tempsNotEnviada), " +
            "    cast(null AS java.lang.Double), " +
            "    AVG(e.tempsTotal) " +
            ") " +
            "FROM ExplotEnvInfoEntity e " +
            "WHERE e.dataNotExpirada IS NOT NULL AND e.dataNotExpirada >= :iniciDelDia AND e.dataNotExpirada < :finalDelDia " +
            "GROUP BY e.entitatId, e.procedimentId, e.organGestorCodi, e.usuariCodi, e.enviamentTipus, e.origen " +
            "ORDER BY e.entitatId, e.procedimentId, e.organGestorCodi, e.usuariCodi, e.enviamentTipus, e.origen ")
    List<ExplotEnvStats> getStatsNotExpiradaPerDay(@Param("iniciDelDia") Date iniciDelDia, @Param("finalDelDia") Date finalDelDia
);

    @Query("SELECT new es.caib.notib.persist.entity.explotacio.ExplotEnvStats( " +
            "    e.entitatId, " +
            "    e.procedimentId, " +
            "    e.organGestorCodi, " +
            "    e.usuariCodi, " +
            "    e.enviamentTipus, " +
            "    e.origen, " +
            "    COUNT(e), " +
            "    AVG(e.tempsNotEnviada), " +
            "    cast(null AS java.lang.Double), " +
            "    AVG(e.tempsTotal) " +
            ") " +
            "FROM ExplotEnvInfoEntity e " +
            "WHERE e.dataNotError IS NOT NULL AND e.dataNotError >= :iniciDelDia AND e.dataNotError < :finalDelDia " +
            "GROUP BY e.entitatId, e.procedimentId, e.organGestorCodi, e.usuariCodi, e.enviamentTipus, e.origen " +
            "ORDER BY e.entitatId, e.procedimentId, e.organGestorCodi, e.usuariCodi, e.enviamentTipus, e.origen ")
    List<ExplotEnvStats> getStatsNotErrorPerDay(@Param("iniciDelDia") Date iniciDelDia, @Param("finalDelDia") Date finalDelDia
);

    @Query("SELECT new es.caib.notib.persist.entity.explotacio.ExplotEnvStats( " +
            "    e.entitatId, " +
            "    e.procedimentId, " +
            "    e.organGestorCodi, " +
            "    e.usuariCodi, " +
            "    e.enviamentTipus, " +
            "    e.origen, " +
            "    COUNT(e), " +
            "    cast(null AS java.lang.Double), " +
            "    AVG(e.intentsCieEnviament), " +
            "    cast(null AS java.lang.Double) " +
            ") " +
            "FROM ExplotEnvInfoEntity e " +
            "WHERE e.dataCieEnviamentError IS NOT NULL AND e.dataCieEnviamentError >= :iniciDelDia AND e.dataCieEnviamentError < :finalDelDia " +
            "GROUP BY e.entitatId, e.procedimentId, e.organGestorCodi, e.usuariCodi, e.enviamentTipus, e.origen " +
            "ORDER BY e.entitatId, e.procedimentId, e.organGestorCodi, e.usuariCodi, e.enviamentTipus, e.origen ")
    List<ExplotEnvStats> getStatsCieEnviamentErrorPerDay(@Param("iniciDelDia") Date iniciDelDia, @Param("finalDelDia") Date finalDelDia
);

    @Query("SELECT new es.caib.notib.persist.entity.explotacio.ExplotEnvStats( " +
            "    e.entitatId, " +
            "    e.procedimentId, " +
            "    e.organGestorCodi, " +
            "    e.usuariCodi, " +
            "    e.enviamentTipus, " +
            "    e.origen, " +
            "    COUNT(e), " +
            "    cast(null AS java.lang.Double), " +
            "    AVG(CASE WHEN e.intentsCieEnviament > 1 THEN e.intentsCieEnviament ELSE null END), " +
            "    cast(null AS java.lang.Double) " +
            ") " +
            "FROM ExplotEnvInfoEntity e " +
            "WHERE e.dataCieEnviada IS NOT NULL AND e.dataCieEnviada >= :iniciDelDia AND e.dataCieEnviada < :finalDelDia " +
            "GROUP BY e.entitatId, e.procedimentId, e.organGestorCodi, e.usuariCodi, e.enviamentTipus, e.origen " +
            "ORDER BY e.entitatId, e.procedimentId, e.organGestorCodi, e.usuariCodi, e.enviamentTipus, e.origen ")
    List<ExplotEnvStats> getStatsCieEnviadaPerDay(@Param("iniciDelDia") Date iniciDelDia, @Param("finalDelDia") Date finalDelDia
);

    @Query("SELECT new es.caib.notib.persist.entity.explotacio.ExplotEnvStats( " +
            "    e.entitatId, " +
            "    e.procedimentId, " +
            "    e.organGestorCodi, " +
            "    e.usuariCodi, " +
            "    e.enviamentTipus, " +
            "    e.origen, " +
            "    COUNT(e), " +
            "    AVG(e.tempsCieEnviada), " +
            "    cast(null AS java.lang.Double), " +
            "    AVG(e.tempsTotal) " +
            ") " +
            "FROM ExplotEnvInfoEntity e " +
            "WHERE e.dataCieNotificada IS NOT NULL AND e.dataCieNotificada >= :iniciDelDia AND e.dataCieNotificada < :finalDelDia " +
            "GROUP BY e.entitatId, e.procedimentId, e.organGestorCodi, e.usuariCodi, e.enviamentTipus, e.origen " +
            "ORDER BY e.entitatId, e.procedimentId, e.organGestorCodi, e.usuariCodi, e.enviamentTipus, e.origen ")
    List<ExplotEnvStats> getStatsCieNotificadaPerDay(@Param("iniciDelDia") Date iniciDelDia, @Param("finalDelDia") Date finalDelDia
);

    @Query("SELECT new es.caib.notib.persist.entity.explotacio.ExplotEnvStats( " +
            "    e.entitatId, " +
            "    e.procedimentId, " +
            "    e.organGestorCodi, " +
            "    e.usuariCodi, " +
            "    e.enviamentTipus, " +
            "    e.origen, " +
            "    COUNT(e), " +
            "    AVG(e.tempsCieEnviada), " +
            "    cast(null AS java.lang.Double), " +
            "    cast(null AS java.lang.Double) " +
            ") " +
            "FROM ExplotEnvInfoEntity e " +
            "WHERE e.dataCieRebutjada IS NOT NULL AND e.dataCieRebutjada >= :iniciDelDia AND e.dataCieRebutjada < :finalDelDia " +
            "GROUP BY e.entitatId, e.procedimentId, e.organGestorCodi, e.usuariCodi, e.enviamentTipus, e.origen " +
            "ORDER BY e.entitatId, e.procedimentId, e.organGestorCodi, e.usuariCodi, e.enviamentTipus, e.origen ")
    List<ExplotEnvStats> getStatsCieRebutjadaPerDay(@Param("iniciDelDia") Date iniciDelDia, @Param("finalDelDia") Date finalDelDia
);

    @Query("SELECT new es.caib.notib.persist.entity.explotacio.ExplotEnvStats( " +
            "    e.entitatId, " +
            "    e.procedimentId, " +
            "    e.organGestorCodi, " +
            "    e.usuariCodi, " +
            "    e.enviamentTipus, " +
            "    e.origen, " +
            "    COUNT(e), " +
            "    AVG(e.tempsCieEnviada), " +
            "    cast(null AS java.lang.Double), " +
            "    cast(null AS java.lang.Double) " +
            ") " +
            "FROM ExplotEnvInfoEntity e " +
            "WHERE e.dataCieCancelada IS NOT NULL AND e.dataCieCancelada >= :iniciDelDia AND e.dataCieCancelada < :finalDelDia " +
            "GROUP BY e.entitatId, e.procedimentId, e.organGestorCodi, e.usuariCodi, e.enviamentTipus, e.origen " +
            "ORDER BY e.entitatId, e.procedimentId, e.organGestorCodi, e.usuariCodi, e.enviamentTipus, e.origen ")
    List<ExplotEnvStats> getStatsCieCanceladaPerDay(@Param("iniciDelDia") Date iniciDelDia, @Param("finalDelDia") Date finalDelDia
);

    @Query("SELECT new es.caib.notib.persist.entity.explotacio.ExplotEnvStats( " +
            "    e.entitatId, " +
            "    e.procedimentId, " +
            "    e.organGestorCodi, " +
            "    e.usuariCodi, " +
            "    e.enviamentTipus, " +
            "    e.origen, " +
            "    COUNT(e), " +
            "    AVG(e.tempsCieEnviada), " +
            "    cast(null AS java.lang.Double), " +
            "    cast(null AS java.lang.Double) " +
            ") " +
            "FROM ExplotEnvInfoEntity e " +
            "WHERE e.dataCieError IS NOT NULL AND e.dataCieError >= :iniciDelDia AND e.dataCieError < :finalDelDia " +
            "GROUP BY e.entitatId, e.procedimentId, e.organGestorCodi, e.usuariCodi, e.enviamentTipus, e.origen " +
            "ORDER BY e.entitatId, e.procedimentId, e.organGestorCodi, e.usuariCodi, e.enviamentTipus, e.origen ")
    List<ExplotEnvStats> getStatsCieErrorPerDay(@Param("iniciDelDia") Date iniciDelDia, @Param("finalDelDia") Date finalDelDia
);

    @Query("SELECT new es.caib.notib.persist.entity.explotacio.ExplotEnvStats( " +
            "    e.entitatId, " +
            "    e.procedimentId, " +
            "    e.organGestorCodi, " +
            "    e.usuariCodi, " +
            "    e.enviamentTipus, " +
            "    e.origen, " +
            "    COUNT(e), " +
            "    cast(null AS java.lang.Double), " +
            "    AVG(e.intentsEmailEnviament), " +
            "    cast(null AS java.lang.Double) " +
            ") " +
            "FROM ExplotEnvInfoEntity e " +
            "WHERE e.dataEmailEnviamentError IS NOT NULL AND e.dataEmailEnviamentError >= :iniciDelDia AND e.dataEmailEnviamentError < :finalDelDia " +
            "GROUP BY e.entitatId, e.procedimentId, e.organGestorCodi, e.usuariCodi, e.enviamentTipus, e.origen " +
            "ORDER BY e.entitatId, e.procedimentId, e.organGestorCodi, e.usuariCodi, e.enviamentTipus, e.origen ")
    List<ExplotEnvStats> getStatsEmailEnviamentErrorPerDay(@Param("iniciDelDia") Date iniciDelDia, @Param("finalDelDia") Date finalDelDia
);

    @Query("SELECT new es.caib.notib.persist.entity.explotacio.ExplotEnvStats( " +
            "    e.entitatId, " +
            "    e.procedimentId, " +
            "    e.organGestorCodi, " +
            "    e.usuariCodi, " +
            "    e.enviamentTipus, " +
            "    e.origen, " +
            "    COUNT(e), " +
            "    cast(null AS java.lang.Double), " +
            "    AVG(CASE WHEN e.intentsEmailEnviament > 1 THEN e.intentsEmailEnviament ELSE null END), " +
            "    cast(null AS java.lang.Double) " +
            ") " +
            "FROM ExplotEnvInfoEntity e " +
            "WHERE e.dataEmailEnviada IS NOT NULL AND e.dataEmailEnviada >= :iniciDelDia AND e.dataEmailEnviada < :finalDelDia " +
            "GROUP BY e.entitatId, e.procedimentId, e.organGestorCodi, e.usuariCodi, e.enviamentTipus, e.origen " +
            "ORDER BY e.entitatId, e.procedimentId, e.organGestorCodi, e.usuariCodi, e.enviamentTipus, e.origen ")
    List<ExplotEnvStats> getStatsEmailEnviadaPerDay(@Param("iniciDelDia") Date iniciDelDia, @Param("finalDelDia") Date finalDelDia
);

}
