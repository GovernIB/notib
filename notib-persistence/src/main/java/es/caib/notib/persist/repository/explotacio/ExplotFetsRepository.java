package es.caib.notib.persist.repository.explotacio;

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
            "where (:esNullData = true or n.createdDate < :data) " +
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

    @Query("from ExplotFetsEntity where temps = temps order by dimensio.entitatId, dimensio.organCodi, dimensio.procedimentId, dimensio.tipus, dimensio.origen, dimensio.usuariCodi")
    List<ExplotFetsEntity> findByTemps(ExplotTempsEntity temps);
}
