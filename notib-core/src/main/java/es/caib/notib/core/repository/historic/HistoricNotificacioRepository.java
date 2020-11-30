package es.caib.notib.core.repository.historic;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.notib.core.api.dto.NotificacioEstatEnumDto;
import es.caib.notib.core.api.dto.historic.HistoricAggregationEstatDto;
import es.caib.notib.core.api.dto.historic.HistoricAggregationGrupDto;
import es.caib.notib.core.api.dto.historic.HistoricAggregationOrganDto;
import es.caib.notib.core.api.dto.historic.HistoricAggregationProcedimentDto;
import es.caib.notib.core.api.dto.historic.HistoricAggregationUsuariDto;
import es.caib.notib.core.api.dto.historic.HistoricTipusEnumDto;
import es.caib.notib.core.entity.OrganGestorEntity;
import es.caib.notib.core.entity.ProcedimentEntity;
import es.caib.notib.core.entity.historic.HistoricNotificacioEntity;

public interface HistoricNotificacioRepository extends JpaRepository<HistoricNotificacioEntity, Long> {
	HistoricNotificacioEntity findByDataAndProcedimentAndUsuariCodiAndGrupCodiAndEstat(
			Date data,
			ProcedimentEntity procediment,
			String usuariCodi,
			String grupCodi,
			NotificacioEstatEnumDto estat
			);
	
	@Query( "select " +
			"   new es.caib.notib.core.api.dto.historic.HistoricAggregationOrganDto( " +
			"	     h.data, " +
			"	     sum(h.numNotTotal), " +
			"	     sum(h.numComTotal), " +
			"        sum(h.numNotCorrectes), " +
			"	     sum(h.numComCorrectes), " +
			"	     sum(h.numNotAmbError), " +
			"	     sum(h.numComAmbError), " +
			"	     sum(case when h.comu = 1 then h.numNotTotal else 0 end), " +
			"	     sum(case when h.comu = 1 then h.numComTotal else 0 end), " +
			"	     sum(case when h.comu = 1 then h.numNotTotal else 0 end), " +
			"	     sum(case when h.comu = 1 then h.numComTotal else 0 end), " +
			"        sum(h.numNotOrigenApi), " +
			"	     sum(h.numComOrigenApi), " +
			"        sum(h.numNotOrigenWeb), " +
			"        sum(h.numComOrigenWeb), " +
			"        0L, " +
			"        count(distinct h.procediment), " +
			"        count(distinct h.grupCodi), " +
			"		 h.organGestor.codi, " +
			"		 h.organGestor.nom " +
			"   ) " +
			" from " +
			"	HistoricNotificacioEntity h" +
			" where " +
			"       h.data >= :dataInici " +
			"	and h.data <= :dataFi " +
			"   and h.tipus = :tipus " +
			" group by " +
			"	h.data, h.organGestor.codi, h.organGestor.nom ")
	List<HistoricAggregationOrganDto> findByDateRangeGroupedByOrganAndDate(
		@Param("tipus") HistoricTipusEnumDto tipus,
		@Param("dataInici") Date dataInici,
		@Param("dataFi") Date dataFi);

	@Query( " select " +
			"   new es.caib.notib.core.api.dto.historic.HistoricAggregationOrganDto( " +
			"	     h.data, " +
			"	     sum(h.numNotTotal), " +
			"	     sum(h.numComTotal), " +
			"        sum(h.numNotCorrectes), " +
			"	     sum(h.numComCorrectes), " +
			"	     sum(h.numNotAmbError), " +
			"	     sum(h.numComAmbError), " +
			"	     sum(case when h.comu = 1 then h.numNotTotal else 0 end), " +
			"	     sum(case when h.comu = 1 then h.numComTotal else 0 end), " +
			"	     sum(case when h.comu = 1 then h.numNotTotal else 0 end), " +
			"	     sum(case when h.comu = 1 then h.numComTotal else 0 end), " +
			"        sum(h.numNotOrigenApi), " +
			"	     sum(h.numComOrigenApi), " +
			"        sum(h.numNotOrigenWeb), " +
			"        sum(h.numComOrigenWeb), " +
			"        ( 	select " +
			"				sum(hEnv.numTotal) " +
			"			from " +
			"				HistoricEnviamentsEntity hEnv " +
			"			where " +
			"				hEnv.data = h.data and  hEnv.organGestor = :organGestor" +
			"		 )," +
			"        count(distinct h.procediment), " +
			"        count(distinct h.grupCodi), " +
			"		 h.organGestor.codi, " +
			"		 h.organGestor.nom " +
			"   ) " +
			" from " +
			"	HistoricNotificacioEntity h" +
			" where " +
			"		h.organGestor = :organGestor" +
			"   and h.data >= :dataInici " +
			"	and h.data <= :dataFi " +
			"   and h.tipus = :tipus " +
			" group by " +
			"	h.data, h.organGestor.codi, h.organGestor.nom " +
			" order by " +
			"	h.data ")
	List<HistoricAggregationOrganDto> findByOrganGestorAndDateRangeGroupedBydDate(
			@Param("organGestor") OrganGestorEntity organGestor,
			@Param("tipus") HistoricTipusEnumDto tipus,			
			@Param("dataInici") Date dataInici,
			@Param("dataFi") Date dataFi);
	
	@Query( "select " +
			"   new es.caib.notib.core.api.dto.historic.HistoricAggregationProcedimentDto( " +
			"	     h.data, " +
			"	     sum(h.numNotTotal), " +
			"	     sum(h.numComTotal), " +
			"        sum(h.numNotCorrectes), " +
			"	     sum(h.numComCorrectes), " +
			"	     sum(h.numNotAmbError), " +
			"	     sum(h.numComAmbError), " +
			"	     sum(case when h.comu = 1 then h.numNotTotal else 0 end), " +
			"	     sum(case when h.comu = 1 then h.numComTotal else 0 end), " +
			"	     sum(case when h.comu = 1 then h.numNotTotal else 0 end), " +
			"	     sum(case when h.comu = 1 then h.numComTotal else 0 end), " +
			"        sum(h.numNotOrigenApi), " +
			"	     sum(h.numComOrigenApi), " +
			"        sum(h.numNotOrigenWeb), " +
			"        sum(h.numComOrigenWeb), " +
			"        0L, " +
			"        count(distinct h.grupCodi), " +
			"		 h.procediment.codi, " +
			"		 h.procediment.nom " +
//			"        ( 	select " +
//			"				hProc.numGrups " +
//			"			from " +
//			"				HistoricProcedimentEntity " +
//			"			where " +
//			"				hProc.data = h.data and  hProc.procediment = h.procediment" +
//			"		)" +
			"   ) " +
			" from " +
			"	HistoricNotificacioEntity h" +
			" where " +
			"       h.data >= :dataInici " +
			"	and h.data <= :dataFi " +
			"   and h.tipus = :tipus " +
			" group by " +
			"	h.data, h.procediment.codi, h.procediment.nom ")
	List<HistoricAggregationProcedimentDto> findByDateRangeGroupedByProcedimentAndDate(
		@Param("tipus") HistoricTipusEnumDto tipus,
		@Param("dataInici") Date dataInici,
		@Param("dataFi") Date dataFi);
	
	@Query( "select " +
			"   new es.caib.notib.core.api.dto.historic.HistoricAggregationProcedimentDto( " +
			"	    h.data, " +
			"	    sum(h.numNotTotal), " +
			"	    sum(h.numComTotal), " +
			"       sum(h.numNotCorrectes), " +
			"	    sum(h.numComCorrectes), " +
			"	    sum(h.numNotAmbError), " +
			"	    sum(h.numComAmbError), " +
			"	    sum(case when h.comu = 1 then h.numNotTotal else 0 end), " +
			"	    sum(case when h.comu = 1 then h.numComTotal else 0 end), " +
			"	    sum(case when h.comu = 1 then h.numNotTotal else 0 end), " +
			"	    sum(case when h.comu = 1 then h.numComTotal else 0 end), " +
			"       sum(h.numNotOrigenApi), " +
			"	    sum(h.numComOrigenApi), " +
			"       sum(h.numNotOrigenWeb), " +
			"       sum(h.numComOrigenWeb), " +
			"		( 	select " +
			"				sum(hEnv.numTotal) " +
			"			from " +
			"				HistoricEnviamentsEntity hEnv " +
			"			where " +
			"				hEnv.data = h.data and  hEnv.procediment = :procediment" +
			"		)," +
			"       count(distinct h.grupCodi), " +
			"		h.procediment.codi, " +
			"		h.procediment.nom " +
//			"        ( 	select " +
//			"				hProc.numGrups " +
//			"			from " +
//			"				HistoricProcedimentEntity " +
//			"			where " +
//			"				hProc.data = h.data and  hProc.procediment = h.procediment" +
//			"		)" +
			"   ) " +
			" from " +
			"	HistoricNotificacioEntity h" +
			" where " +
			"		h.procediment = :procediment" +
			"   and h.data >= :dataInici " +
			"	and h.data <= :dataFi " +
			"   and h.tipus = :tipus " +
			" group by " +
			"	h.data, h.procediment.codi, h.procediment.nom " +
			" order by " +
			"	h.data ")
	
	List<HistoricAggregationProcedimentDto> findByProcedimentAndDateRangeGroupedBydDate(
		@Param("procediment") ProcedimentEntity procediment,
		@Param("tipus") HistoricTipusEnumDto tipus,
		@Param("dataInici") Date dataInici,
		@Param("dataFi") Date dataFi);
	
	@Query( "select " +
			"   new es.caib.notib.core.api.dto.historic.HistoricAggregationEstatDto( " +
			"	    h.data, " +
			"	    sum(h.numNotTotal), " +
			"	    sum(h.numComTotal), " +
			"       sum(h.numNotCorrectes), " +
			"	    sum(h.numComCorrectes), " +
			"	    sum(h.numNotAmbError), " +
			"	    sum(h.numComAmbError), " +
			"	    sum(case when h.comu = 1 then h.numNotTotal else 0 end), " +
			"	    sum(case when h.comu = 1 then h.numComTotal else 0 end), " +
			"	    sum(case when h.comu = 1 then h.numNotTotal else 0 end), " +
			"	    sum(case when h.comu = 1 then h.numComTotal else 0 end), " +
			"       sum(h.numNotOrigenApi), " +
			"	    sum(h.numComOrigenApi), " +
			"       sum(h.numNotOrigenWeb), " +
			"       sum(h.numComOrigenWeb)," +
			"		h.estat " +
			"   ) " +
			" from " +
			"	HistoricNotificacioEntity h" +
			" where " +
			"       h.data >= :dataInici " +
			"	and h.data <= :dataFi " +
			"   and h.tipus = :tipus " +
			" group by " +
			"	h.data, h.estat ")
	List<HistoricAggregationEstatDto> findByDateRangeGroupedByEstatAndDate(
		@Param("tipus") HistoricTipusEnumDto tipus,
		@Param("dataInici") Date dataInici,
		@Param("dataFi") Date dataFi);
	
	@Query( "select " +
			"   new es.caib.notib.core.api.dto.historic.HistoricAggregationEstatDto( " +
			"	     h.data, " +
			"	     sum(h.numNotTotal), " +
			"	     sum(h.numComTotal), " +
			"        sum(h.numNotCorrectes), " +
			"	     sum(h.numComCorrectes), " +
			"	     sum(h.numNotAmbError), " +
			"	     sum(h.numComAmbError), " +
			"	     sum(case when h.comu = 1 then h.numNotTotal else 0 end), " +
			"	     sum(case when h.comu = 1 then h.numComTotal else 0 end), " +
			"	     sum(case when h.comu = 1 then h.numNotTotal else 0 end), " +
			"	     sum(case when h.comu = 1 then h.numComTotal else 0 end), " +
			"        sum(h.numNotOrigenApi), " +
			"	     sum(h.numComOrigenApi), " +
			"        sum(h.numNotOrigenWeb), " +
			"        sum(h.numComOrigenWeb) " +
			"   ) " +
			" from " +
			"	HistoricNotificacioEntity h" +
			" where " +
			"		h.estat = :estat" +
			"   and h.data >= :dataInici " +
			"	and h.data <= :dataFi " +
			"   and h.tipus = :tipus " +
			" group by " +
			"	h.data ")
	List<HistoricAggregationEstatDto> findByEstatAndDateRangeGroupedByDate(
		@Param("estat") NotificacioEstatEnumDto estat,
		@Param("tipus") HistoricTipusEnumDto tipus,
		@Param("dataInici") Date dataInici,
		@Param("dataFi") Date dataFi);
	
	@Query( "select " +
			"   new es.caib.notib.core.api.dto.historic.HistoricAggregationGrupDto( " +
			"	     h.data, " +
			"	     sum(h.numNotTotal), " +
			"	     sum(h.numComTotal), " +
			"        sum(h.numNotCorrectes), " +
			"	     sum(h.numComCorrectes), " +
			"	     sum(h.numNotAmbError), " +
			"	     sum(h.numComAmbError), " +
			"	     sum(case when h.comu = 1 then h.numNotTotal else 0 end), " +
			"	     sum(case when h.comu = 1 then h.numComTotal else 0 end), " +
			"	     sum(case when h.comu = 1 then h.numNotTotal else 0 end), " +
			"	     sum(case when h.comu = 1 then h.numComTotal else 0 end), " +
			"        sum(h.numNotOrigenApi), " +
			"	     sum(h.numComOrigenApi), " +
			"        sum(h.numNotOrigenWeb), " +
			"        sum(h.numComOrigenWeb) " +
			"   ) " +
			" from " +
			"	HistoricNotificacioEntity h" +
			" where " +
			"       h.data >= :dataInici " +
			"	and h.data <= :dataFi " +
			"   and h.tipus = :tipus " +
			" group by " +
			"	h.data, h.grupCodi ")
	List<HistoricAggregationGrupDto> findByDateRangeGroupedByGrupAndDate(
		@Param("tipus") HistoricTipusEnumDto tipus,
		@Param("dataInici") Date dataInici,
		@Param("dataFi") Date dataFi);
	
	@Query( "select " +
			"   new es.caib.notib.core.api.dto.historic.HistoricAggregationGrupDto( " +
			"	     h.data, " +
			"	     sum(h.numNotTotal), " +
			"	     sum(h.numComTotal), " +
			"        sum(h.numNotCorrectes), " +
			"	     sum(h.numComCorrectes), " +
			"	     sum(h.numNotAmbError), " +
			"	     sum(h.numComAmbError), " +
			"	     sum(case when h.comu = 1 then h.numNotTotal else 0 end), " +
			"	     sum(case when h.comu = 1 then h.numComTotal else 0 end), " +
			"	     sum(case when h.comu = 1 then h.numNotTotal else 0 end), " +
			"	     sum(case when h.comu = 1 then h.numComTotal else 0 end), " +
			"        sum(h.numNotOrigenApi), " +
			"	     sum(h.numComOrigenApi), " +
			"        sum(h.numNotOrigenWeb), " +
			"        sum(h.numComOrigenWeb) " +
			"   ) " +
			" from " +
			"	HistoricNotificacioEntity h" +
			" where " +
			"       h.data >= :dataInici " +
			"	and h.data <= :dataFi " +
			"   and h.tipus = :tipus " +
			"   and h.grupCodi = :grupCodi " +
			" group by " +
			"	h.data ")
	List<HistoricAggregationGrupDto> findByGrupAndDateRangeGroupedByDate(
			@Param("grupCodi") String grupCodi,
			@Param("tipus") HistoricTipusEnumDto tipus,
			@Param("dataInici") Date dataInici,
			@Param("dataFi") Date dataFi);
	
	@Query( "select " +
			"   new es.caib.notib.core.api.dto.historic.HistoricAggregationUsuariDto( " +
			"	     h.data, " +
			"	     sum(h.numNotTotal), " +
			"	     sum(h.numComTotal), " +
			"        sum(h.numNotCorrectes), " +
			"	     sum(h.numComCorrectes), " +
			"	     sum(h.numNotAmbError), " +
			"	     sum(h.numComAmbError), " +
			"	     sum(case when h.comu = 1 then h.numNotTotal else 0 end), " +
			"	     sum(case when h.comu = 1 then h.numComTotal else 0 end), " +
			"	     sum(case when h.comu = 1 then h.numNotTotal else 0 end), " +
			"	     sum(case when h.comu = 1 then h.numComTotal else 0 end), " +
			"        sum(h.numNotOrigenApi), " +
			"	     sum(h.numComOrigenApi), " +
			"        sum(h.numNotOrigenWeb), " +
			"        sum(h.numComOrigenWeb) " +
			"   ) " +
			" from " +
			"	HistoricNotificacioEntity h" +
			" where " +
			"       h.data >= :dataInici " +
			"	and h.data <= :dataFi " +
			"   and h.tipus = :tipus " +
			" group by " +
			"	h.data, h.usuariCodi ")
	List<HistoricAggregationUsuariDto> findByDateRangeGroupedByUserAndDate(
		@Param("tipus") HistoricTipusEnumDto tipus,
		@Param("dataInici") Date dataInici,
		@Param("dataFi") Date dataFi);
	
	@Query( "select " +
			"   new es.caib.notib.core.api.dto.historic.HistoricAggregationUsuariDto( " +
			"	     h.data, " +
			"	     sum(h.numNotTotal), " +
			"	     sum(h.numComTotal), " +
			"        sum(h.numNotCorrectes), " +
			"	     sum(h.numComCorrectes), " +
			"	     sum(h.numNotAmbError), " +
			"	     sum(h.numComAmbError), " +
			"	     sum(case when h.comu = 1 then h.numNotTotal else 0 end), " +
			"	     sum(case when h.comu = 1 then h.numComTotal else 0 end), " +
			"	     sum(case when h.comu = 1 then h.numNotTotal else 0 end), " +
			"	     sum(case when h.comu = 1 then h.numComTotal else 0 end), " +
			"        sum(h.numNotOrigenApi), " +
			"	     sum(h.numComOrigenApi), " +
			"        sum(h.numNotOrigenWeb), " +
			"        sum(h.numComOrigenWeb) " +
			"   ) " +
			" from " +
			"	HistoricNotificacioEntity h" +
			" where " +
			"       h.data >= :dataInici " +
			"	and h.data <= :dataFi " +
			"   and h.tipus = :tipus " +
			"   and h.usuariCodi = :usuariCodi " +
			" group by " +
			"	h.data ")
	List<HistoricAggregationUsuariDto> findByUsuariAndDateRangeGroupedByDate(
			@Param("usuariCodi") String usuariCodi,
			@Param("tipus") HistoricTipusEnumDto tipus,
			@Param("dataInici") Date dataInici,
			@Param("dataFi") Date dataFi);
	
}
