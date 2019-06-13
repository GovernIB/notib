package es.caib.notib.core.helper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import es.caib.notib.core.api.dto.PermisDto;
import es.caib.notib.core.api.dto.ProcedimentDto;
import es.caib.notib.core.entity.GrupProcedimentEntity;
import es.caib.notib.core.entity.ProcedimentEntity;
import es.caib.notib.core.repository.GrupProcedimentRepository;
import es.caib.notib.core.security.ExtendedPermission;
import es.caib.notib.plugin.usuari.DadesUsuari;

/**
 * Helper per a convertir entities a dto
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ProcedimentHelper {
	
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private PermisosHelper permisosHelper;
	@Autowired
	private GrupProcedimentRepository grupProcedimentRepository;
	
	public void omplirPermisosPerMetaNode(
			ProcedimentDto procediment,
			boolean ambLlistaPermisos) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		procediment.setUsuariActualRead(
				permisosHelper.isGrantedAll(
						procediment.getId(),
						ProcedimentEntity.class,
						new Permission[] {ExtendedPermission.READ},
						auth));
		procediment.setUsuariActualProcessar(
				permisosHelper.isGrantedAll(
						procediment.getId(),
						ProcedimentEntity.class,
						new Permission[] {ExtendedPermission.PROCESSAR},
						auth));
		procediment.setUsuariActualNotificacio(
				permisosHelper.isGrantedAll(
						procediment.getId(),
						ProcedimentEntity.class,
						new Permission[] {ExtendedPermission.NOTIFICACIO},
						auth));
		procediment.setUsuariActualAdministration(
				permisosHelper.isGrantedAll(
						procediment.getId(),
						ProcedimentEntity.class,
						new Permission[] {ExtendedPermission.ADMINISTRATION},
						auth));
		if (ambLlistaPermisos) {
			List<PermisDto> permisos = permisosHelper.findPermisos(
					procediment.getId(),
					ProcedimentEntity.class);
			procediment.setPermisos(permisos);
		}
	}
	
	public Set<String> findUsuarisAmbPermisReadPerProcediment(
			ProcedimentEntity procediment) {
		StringBuilder sb = new StringBuilder("Preparant la llista d'usuaris per enviar l'email: ");
		List<PermisDto> permisos = new ArrayList<PermisDto>();
		permisos = permisosHelper.findPermisos(
				procediment.getId(),
				ProcedimentEntity.class);
		
		Set<String> usuaris = new HashSet<String>();
		for (PermisDto permis: permisos) {
			switch (permis.getTipus()) {
			case USUARI:
				usuaris.add(permis.getPrincipal());
				sb.append(" usuari ").append(permis.getPrincipal());
				break;
			case ROL:
				List<DadesUsuari> usuarisGrup = pluginHelper.dadesUsuariConsultarAmbGrup(
						permis.getPrincipal());
				sb.append(" rol ").append(permis.getPrincipal()).append(" (");
				if (usuarisGrup != null) {
					for (DadesUsuari usuariGrup: usuarisGrup) {
						usuaris.add(usuariGrup.getCodi());
						sb.append(" ").append(usuariGrup.getCodi());
					}
				}
				sb.append(")");
				break;
			}
		}
		logger.debug(sb.toString());
		return usuaris;
	}
	
	public Set<String> findUsuarisAmbPermisReadPerGrup(
			ProcedimentEntity procediment) {
		StringBuilder sb = new StringBuilder("Preparant la llista d'usuaris per enviar l'email: ");
		List<GrupProcedimentEntity> grupsProcediment = grupProcedimentRepository.findByProcediment(procediment);
		
		Set<String> usuaris = new HashSet<String>();
		for (GrupProcedimentEntity permisGrup: grupsProcediment) {
			List<DadesUsuari> usuarisGrup = pluginHelper.dadesUsuariConsultarAmbGrup(
					permisGrup.getGrup().getCodi());
				sb.append(" rol ").append(permisGrup.getGrup().getCodi()).append(" (");
				if (usuarisGrup != null) {
					for (DadesUsuari usuariGrup: usuarisGrup) {
						usuaris.add(usuariGrup.getCodi());
						sb.append(" ").append(usuariGrup.getCodi());
					}
				}
				sb.append(")");
			
		}
		logger.debug(sb.toString());
		return usuaris;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(ProcedimentHelper.class);

}
