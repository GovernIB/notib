package es.caib.notib.logic.helper;

import es.caib.notib.logic.helper.plugin.GestioDocumentalPluginHelper;
import es.caib.notib.logic.helper.plugin.UnitatsOrganitzativesPluginHelper;
import es.caib.notib.logic.intf.base.model.FileReference;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.model.Dir3Resource;
import es.caib.notib.logic.intf.service.AuditService;
import es.caib.notib.logic.intf.service.EnviamentSmService;
import es.caib.notib.logic.mapper.NotificacioTableMapper;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import es.caib.notib.persist.repository.NotificacioRepository;
import es.caib.notib.persist.repository.NotificacioTableViewRepository;
import es.caib.notib.persist.resourceentity.EntitatResourceEntity;
import es.caib.notib.persist.resourceentity.NotificacioResourceEntity;
import es.caib.notib.plugin.unitat.CodiValor;
import es.caib.notib.plugin.unitat.NodeDir3;
import joptsimple.internal.Strings;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Helper per a cridar lògica preexistent des dels *ResourceServiceImpls.
 *
 * @author Límit Tecnologies
 */
@Component
@RequiredArgsConstructor
public class LegacyHelper {

	private final UserSessionHelper userSessionHelper;
	private final GestioDocumentalPluginHelper gestioDocumentalPluginHelper;
	private final UnitatsOrganitzativesPluginHelper unitatsOrganitzativesPluginHelper;
	private final NotificacioRepository notificacioRepository;
	private final NotificacioEnviamentRepository notificacioEnviamentRepository;
	private final NotificacioTableHelper notificacioTableHelper;
	private final EnviamentTableHelper enviamentTableHelper;
	private final AuditHelper auditHelper;
	private final EnviamentSmService enviamentSmService;

	private final PluginHelper pluginHelper;

	/**
	 * Crea un document adjunt per una notificació.
	 *
	 * @param fileReference
	 *            referència a l'arxiu del document adjunt.
	 * @return l'id del document creat a la gestió documental.
	 */
	public String notificacioAdjuntCreate(FileReference fileReference) {
		EntitatResourceEntity currentEntitat = userSessionHelper.getCurrentEntitat();
		ConfigHelper.setEntitatCodi(currentEntitat.getCodi());
		return gestioDocumentalPluginHelper.gestioDocumentalCreate(
			PluginHelper.GESDOC_AGRUPACIO_NOTIFICACIONS,
			fileReference.getContent());
	}

	/**
	 * Consulta una unitat organitzativa al servei DIR3 extern.
	 *
	 * @param codi
	 *            el codi DIR3.
	 * @return la informació de la unitat organitzativa (si l'ha trobada).
	 */
	public Optional<Dir3Resource> dir3FindOne(String codi) {
		EntitatResourceEntity currentEntitat = userSessionHelper.getCurrentEntitat();
		ConfigHelper.setEntitatCodi(currentEntitat.getCodi());
		NodeDir3 nodeDir3 = unitatsOrganitzativesPluginHelper.unitatOrganitzativaFindByCodi(
			currentEntitat.getCodi(),
			codi,
			null,
			null);
		if (nodeDir3 != null) {
			return Optional.of(toDir3Resource(nodeDir3));
		} else {
			return Optional.empty();
		}
	}

	/**
	 * Fa una consulta d'unitats organitzatives al servei DIR3 extern en base a una sèrie de paràmetres.
	 *
	 * @param codi
	 *            el codi DIR3.
	 * @param denominacio
	 *            la denominació.
	 * @param nivellAdministracio
	 *            el nivell de l'administració.
	 * @param comunitatAutonoma
	 *            el codi de la comunitat autònoma.
	 * @param provincia
	 *            el codi de la província.
	 * @param municipi
	 *            el codi del municipi.
	 * @param ambOficines
	 *            indica si només s'han de retornar les unitats que tenen oficines.
	 * @param esUnitatArrel
	 *            indica si només s'han de retornar les unitats arrel.
	 * @param pageable
	 *            la informació de paginació.
	 * @return la pàgina amb els resultats de la consulta.
	 */
	public Page<Dir3Resource> dir3FindMultiple(
		String codi,
		String denominacio,
		Long nivellAdministracio,
		Long comunitatAutonoma,
		Long provincia,
		String municipi,
		Boolean ambOficines,
		Boolean esUnitatArrel,
		Pageable pageable) {
		EntitatResourceEntity currentEntitat = userSessionHelper.getCurrentEntitat();
		ConfigHelper.setEntitatCodi(currentEntitat.getCodi());
		List<OrganGestorDto> nodesDir3 = pluginHelper.cercaUnitats(
			currentEntitat.getId(),
			codi,
			denominacio,
			nivellAdministracio,
			comunitatAutonoma,
			ambOficines,
			esUnitatArrel,
			provincia,
			municipi);
		if (!nodesDir3.isEmpty()) {
			return new PageImpl<>(
				nodesDir3.stream().map(this::toDir3Resource).collect(Collectors.toList()),
				pageable, nodesDir3.size());
		} else {
			return Page.empty();
		}
	}

	/**
	 * Consulta la llista de comunitats autònomes a DIR3.
	 *
	 * @return la llista de comunitats autònomes.
	 */
	public List<CodiValor> dir3ConsultaComunitatsAutonomes() {
		EntitatResourceEntity currentEntitat = userSessionHelper.getCurrentEntitat();
		ConfigHelper.setEntitatCodi(currentEntitat.getCodi());
		return unitatsOrganitzativesPluginHelper.llistarComunitatsAutonomes();
	}

	/**
	 * Consulta la llista de províncies a DIR3.
	 *
	 * @param comunitatAutonomaCodi
	 *            el codi de la comunitat autònoma.
	 * @return la llista de províncies.
	 */
	public List<CodiValor> dir3ConsultaProvincies(String comunitatAutonomaCodi) {
		EntitatResourceEntity currentEntitat = userSessionHelper.getCurrentEntitat();
		ConfigHelper.setEntitatCodi(currentEntitat.getCodi());
		return !Strings.isNullOrEmpty(comunitatAutonomaCodi) ? unitatsOrganitzativesPluginHelper.llistarProvincies(comunitatAutonomaCodi) : unitatsOrganitzativesPluginHelper.llistarProvincies();
	}

	/**
	 * Consulta la llista de localitats a DIR3.
	 *
	 * @param provinciaCodi
	 *            el codi de la província.
	 * @return la llista de localitats.
	 */
	public List<CodiValor> dir3ConsultaLocalitats(String provinciaCodi) {
		EntitatResourceEntity currentEntitat = userSessionHelper.getCurrentEntitat();
		ConfigHelper.setEntitatCodi(currentEntitat.getCodi());
		return unitatsOrganitzativesPluginHelper.llistarLocalitats(provinciaCodi);
	}
	/**
	 * Lògica antiga per a les notificacions:
	 * <p>
	 *   - Creació del registre a notificacio_env_table per a cada enviament.
	 *   - Creació de l'auditoria per a cada enviament.
	 *   - Creació del registre a notificacio_table.
	 *   - Creació de l'auditoria.
	 *   - Alta de cada enviament a la màquina d'estats (SM) al finalitzar la transacció.
	 * @param notificacioId l'identificador de la notificació
	 * @param enviamentsIds la llista d'ids dels enviaments associats a la notificació
	 */
	@Transactional
	public void altaNotificacio(Long notificacioId, List<Long> enviamentsIds) {

		// Lògica antiga per a les notificacions
		Optional<NotificacioEntity> notificacioEntity = notificacioRepository.findById(notificacioId);
		if (!notificacioEntity.isPresent()) {
			return;
		}
		// Lògica antiga per a cada enviament
		for (Long enviamentId: enviamentsIds) {
			var notificacioEnviamentEntity = notificacioEnviamentRepository.findById(enviamentId);
			if (!notificacioEnviamentEntity.isPresent()) {
				continue;
			}
			// Crea el registre a notificacio_env_table
			enviamentTableHelper.crearRegistre(notificacioEnviamentEntity.get());
			// Crea la informació d'auditoria
			auditHelper.auditaEnviament(notificacioEnviamentEntity.get(), AuditService.TipusOperacio.CREATE, "NotificacioResourceServiceImpl.saveEnviaments");
		}
		// Crea el registre a notificacio_table
		 notificacioTableHelper.crearRegistre(notificacioEntity.get());
		actualitzarColumnaEstat(notificacioEntity.get());
		// Crea la informació d'auditoria
		auditHelper.auditaNotificacio(notificacioEntity.get(), AuditService.TipusOperacio.CREATE, "NotificacioResourceServiceImpl.afterCreateSave");
		// Dona d'alta els enviaments a la màqina d'estats al finalitzar la transacció
		notificacioEntity.get().getEnviaments().forEach(e -> enviamentSmService.altaEnviament(e.getNotificaReferencia()));
	}

	private Dir3Resource toDir3Resource(NodeDir3 nodeDir3) {
		Dir3Resource dir3Resource = new Dir3Resource();
		dir3Resource.setCodi(nodeDir3.getCodi());
		dir3Resource.setDenominacio(nodeDir3.getDenominacio());
		dir3Resource.setDenominacionCooficial(nodeDir3.getDenominacionCooficial());
		dir3Resource.setVersio(nodeDir3.getVersio());
		dir3Resource.setEstat(nodeDir3.getEstat());
		return dir3Resource;
	}

	private Dir3Resource toDir3Resource(OrganGestorDto organGestorDto) {
		Dir3Resource dir3Resource = new Dir3Resource();
		dir3Resource.setCodi(organGestorDto.getCodi());
		dir3Resource.setDenominacio(organGestorDto.getNom());
		dir3Resource.setDenominacionCooficial(organGestorDto.getNomEs());
		dir3Resource.setEstat(organGestorDto.getEstat().toString());
		dir3Resource.setCif(organGestorDto.getCif());
		dir3Resource.setSir(organGestorDto.getSir() != null && organGestorDto.getSir());
		dir3Resource.setPermetreSir(organGestorDto.isPermetreSir());
		return dir3Resource;
	}


	private final NotificacioTableViewRepository notificacioTableViewRepository;
	private final NotificacioTableMapper notificacioTableMapper;
	private final NotificacioListHelper notificacioListHelper;
	private final CacheHelper cacheHelper;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public String actualitzarColumnaEstat(NotificacioResourceEntity entity) {

		var notificacio = notificacioRepository.findById(entity.getId()).orElseThrow();
		return actualitzarColumnaEstat(notificacio);

	}

	public String actualitzarColumnaEstat(NotificacioEntity entity) {

		var entitat = entity.getEntitat();
		var tableEntity = notificacioTableViewRepository.findById(entity.getId()).get();
		if (!tableEntity.isPerActualitzar()) {
			return null;
		}
		if (tableEntity.getEnviaments() == null) {
			tableEntity.setEnviaments(new java.util.LinkedHashSet<>(entity.getEnviaments()));
		}
		var permisos = notificacioListHelper.getCodisProcedimentsAndOrgansAmpPermisProcessar(entitat.getId(), tableEntity.getUsuariCodi());
		var organigrama = cacheHelper.findOrganigramaNodeByEntitat(entitat.getDir3Codi());
		notificacioTableMapper.toNotificacionsTableItemDto(List.of(tableEntity), permisos, organigrama);
		var reloaded =  notificacioTableViewRepository.findById(tableEntity.getId()).orElseThrow();
		return reloaded.getEstatString();
	}

}
