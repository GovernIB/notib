package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.helper.MetricsHelper;
import es.caib.notib.logic.helper.UserSessionHelper;
import es.caib.notib.logic.intf.dto.permis.PermisosUsuarisFiltre;
import es.caib.notib.logic.intf.model.UsuariPermisResource;
import es.caib.notib.logic.intf.resourceservice.UsuariPermisResourceService;
import es.caib.notib.logic.intf.service.UsuariService;
import es.caib.notib.logic.usuaris.PermisosUsuariActionExecutor;
import es.caib.notib.persist.resourceentity.UsuariPermisResourceEntity;
import es.caib.notib.persist.resourcerepository.UsuariResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;


/**
 * Implementació del servei de consulta de l'ActiveMq
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UsuariPermisResourceServiceImpl extends BaseMutableResourceService<UsuariPermisResource, String, UsuariPermisResourceEntity> implements UsuariPermisResourceService {

	private final MetricsHelper metricsHelper;
	private final UsuariResourceRepository usuariRepository;
	private final UsuariService usuariService;
	private final UserSessionHelper userSessionHelper;

	@PostConstruct
	public void init() {

		register(UsuariPermisResource.ACTION_GET_PERMISOS_USUARI, new PermisosUsuariActionExecutor(usuariService, userSessionHelper));
		register(UsuariPermisResource.REPORT_EXPORTAR_PERMISOS_USUARI, new PermisosUsuariActionExecutor(usuariService, userSessionHelper));
	}

	@Override
	public Page<UsuariPermisResource> findPage(String quickFilter, String filter, String[] namedQueries, String[] perspectives, Pageable pageable) {

		var timer = metricsHelper.iniciMetrica();
		try {
			var usuariCodi = !StringUtils.isEmpty(filter) ?  StringUtils.substringBetween(filter, "%", "%") : null;
			var filtre = PermisosUsuarisFiltre.builder().usuariCodi(usuariCodi).build();
			var usuaris = usuariRepository.findByFiltre(filtre);
			UsuariPermisResource usuariPermis;
			List<UsuariPermisResource> usuarisPermis = new ArrayList<>();
			for (var usuari : usuaris) {
				usuariPermis = new UsuariPermisResource();
				usuariPermis.setCodi(usuari.getCodi());
				usuariPermis.setNom(usuari.getNom());
				usuariPermis.setLlinatges(usuari.getLlinatges());
				usuariPermis.setNif(usuari.getNif());
				usuariPermis.setEmail(usuari.getEmail());
				usuariPermis.setEmailAlt(usuari.getEmailAlt());
				usuarisPermis.add(usuariPermis);
			}
			return new PageImpl<>(usuarisPermis, pageable, usuarisPermis.size());
		} catch (Exception ex) {
			log.error("Error obtinguent els usuaris");
			return Page.empty();
		} finally {
			metricsHelper.fiMetrica(timer);
		}
	}

	@Override
	public boolean isEntityRepositoryOptional() {
		return true;
	}

}
