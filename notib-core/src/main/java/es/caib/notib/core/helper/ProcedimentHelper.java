package es.caib.notib.core.helper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.OrganismeDto;
import es.caib.notib.core.api.dto.PermisDto;
import es.caib.notib.core.api.dto.ProcedimentDto;
import es.caib.notib.core.api.dto.ProgresActualitzacioDto;
import es.caib.notib.core.api.dto.ProgresActualitzacioDto.TipusInfo;
import es.caib.notib.core.api.exception.ValidationException;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.GrupEntity;
import es.caib.notib.core.entity.GrupProcedimentEntity;
import es.caib.notib.core.entity.OrganGestorEntity;
import es.caib.notib.core.entity.ProcedimentEntity;
import es.caib.notib.core.repository.GrupProcedimentRepository;
import es.caib.notib.core.repository.OrganGestorRepository;
import es.caib.notib.core.repository.ProcedimentRepository;
import es.caib.notib.core.security.ExtendedPermission;
import es.caib.notib.plugin.registre.Llibre;
import es.caib.notib.plugin.registre.Oficina;
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
	@Autowired
	private ProcedimentRepository procedimentRepository;
	@Autowired
	private OrganGestorRepository organGestorRepository;
	@Resource
	MessageHelper messageHelper;
	
	public void omplirPermisos(
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
		List<PermisDto> permisos = new ArrayList<PermisDto>();
		permisos = permisosHelper.findPermisos(
				procediment.getId(),
				ProcedimentEntity.class);
		
		Set<String> usuaris = new HashSet<String>();
		for (GrupProcedimentEntity permisGrup: grupsProcediment) {
			List<DadesUsuari> usuarisGrup = pluginHelper.dadesUsuariConsultarAmbGrup(
					permisGrup.getGrup().getCodi());
				sb.append(" rol ").append(permisGrup.getGrup().getCodi()).append(" (");
				
				if (usuarisGrup != null) {
					for (DadesUsuari usuariGrup: usuarisGrup) {
						for (PermisDto permis : permisos) {
							if (permis.getPrincipal().equals(usuariGrup.getCodi())) {
								usuaris.add(usuariGrup.getCodi());
								sb.append(" ").append(usuariGrup.getCodi());
							}
						}
					}
				}
				sb.append(")");
			
		}
		logger.debug(sb.toString());
		return usuaris;
	}
	
	public Set<String> findUsuarisAmbPermisReadPerGrupNotificacio(
			GrupEntity grup,
			ProcedimentEntity procediment) {
		StringBuilder sb = new StringBuilder("Preparant la llista d'usuaris per enviar l'email: ");
		GrupProcedimentEntity grupProcediment = grupProcedimentRepository.findByGrupAndProcediment(grup, procediment);
		List<PermisDto> permisos = new ArrayList<PermisDto>();
		permisos = permisosHelper.findPermisos(
				procediment.getId(),
				ProcedimentEntity.class);
		Set<String> usuaris = new HashSet<String>();
		if (grupProcediment != null) {
			List<DadesUsuari> usuarisGrup = pluginHelper.dadesUsuariConsultarAmbGrup(
					grupProcediment.getGrup().getCodi());
				sb.append(" rol ").append(grupProcediment.getGrup().getCodi()).append(" (");
				if (usuarisGrup != null) {
					for (DadesUsuari usuariGrup: usuarisGrup) {
						for (PermisDto permis : permisos) {
							if (permis.getPrincipal().equals(usuariGrup.getCodi())) {
								usuaris.add(usuariGrup.getCodi());
								sb.append(" ").append(usuariGrup.getCodi());
							}
						}
					}
				}
				sb.append(")");
			
		}
		logger.debug(sb.toString());
		return usuaris;
	}
	
	@Transactional(timeout = 300, propagation = Propagation.REQUIRES_NEW)
	public void actualitzarProcedimentFromGda(ProgresActualitzacioDto progres, Long t1, Long t2, 
			ProcedimentDto procedimentGda, EntitatDto entitatDto, EntitatEntity entitat,
			Oficina oficinaVirtual, Map<String, OrganismeDto> organigramaEntitat, boolean modificar,
			List<OrganGestorEntity> organsGestorsModificats, int i) {
		
		t1 = System.currentTimeMillis();
//		logger.debug(">>>> " + i + ". Processant procediment: " + procedimentGda.getNom());
//		logger.debug(">>>> ..........................................................................");
		progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment", new Object[] {i, procedimentGda.getNom()}));
		
		if (procedimentGda.getCodi() == null || procedimentGda.getCodi().isEmpty()) {
//			logger.debug(">>>> Procediment DESCARTAT: No disposa de Codi SIA");
//			logger.debug(">>>> ..........................................................................");
//			logger.debug(">>>> ..........................................................................");
			progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.descartat"));
			progres.addSeparador();
			progres.incrementProcedimentsActualitzats();
			return;
		}
		
		ProcedimentEntity procediment = procedimentRepository.findByCodi(procedimentGda.getCodi());
		if (procediment != null) {
			// Si no s'ha modificat des de la última actualització, no es fa res
			if (procediment.getUltimaActualitzacio() != null && procedimentGda.getUltimaActualitzacio() != null && 
					!procediment.getUltimaActualitzacio().before(procedimentGda.getUltimaActualitzacio())) {
				progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.descartat.data"));
//				logger.debug(">>>> Procediment DESCARTAT: No s'ha modificat des de la última actualització.");
				progres.addSeparador();
				progres.incrementProcedimentsActualitzats();
				return;
			}
		}
		
		if (!organigramaEntitat.containsKey(procedimentGda.getOrganGestor())) {
		//if (organigramaEntitat.get(procedimentGda.getOrganGestor())==null) {
			// Si l'Organ gestor del procediment no existeix dins el nostre organigrama, no es guarda el procediment
			progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.descartat.noOrganDinsOrganigrama", new Object[] {procedimentGda.getOrganGestor()}));
//			logger.debug(">>>> Procediment DESCARTAT: No s'ha trobat l'organ del procediment dins l'organigrama de l'entitat. Organ: "+ procedimentGda.getOrganGestor());
			progres.addSeparador();
			progres.incrementProcedimentsActualitzats();
			return;
		}
			

		// Organ gestor
//		logger.debug(">>>> >> Comprovant Organ gestor. Codi: " + procedimentGda.getOrganGestor() +  "...");
		progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.organ", new Object[] {procedimentGda.getOrganGestor()}));
		
		OrganGestorEntity organGestor = organGestorRepository.findByCodi(procedimentGda.getOrganGestor());
						logger.debug(">>>> >> organ gestor " + (organGestor == null ? "NOU" : "EXISTENT"));
		
		if (organGestor == null) {
			progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.organ.result.no"));
			progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.organ.crear", new Object[] {procedimentGda.getOrganGestor()}));

			organGestor = OrganGestorEntity.getBuilder(
					procedimentGda.getOrganGestor(),
					organigramaEntitat.get(procedimentGda.getOrganGestor()).getNom(),
					entitat).build();
			organGestorRepository.save(organGestor);
			
			progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.organ.creat"));
		} else {
			progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.organ.result.si"));
		}

//		logger.debug(">>>> >> Comprovant Procediment. Codi SIA: " + procedimentGda.getCodi() +  "...");
		progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment", new Object[] {procedimentGda.getCodi()}));
		
		
		if (procediment == null) {
//			logger.debug(">>>> >> procediment NOU ...");
			progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment.result.no"));
			progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment.crear", new Object[] {procedimentGda.getCodi()}));

			//#260 Capturar Llibre i oficina del regweb3
			String llibre = null;
			String llibreNom = null;
			String oficina = null; 
			String oficinaNom = null;
			
			Llibre llibreEntitatiOrgan = pluginHelper.llistarLlibreOrganisme(entitatDto.getDir3Codi(), organGestor.getCodi());
			
			if (llibreEntitatiOrgan!=null) {
				llibre = llibreEntitatiOrgan.getCodi();
				llibreNom = llibreEntitatiOrgan.getNomCurt();
				oficina = oficinaVirtual.getCodi(); 
				oficinaNom = oficinaVirtual.getNom();
			}
			
			// CREATE
			procediment = ProcedimentEntity.getBuilder(
					procedimentGda.getCodi(),
					procedimentGda.getNom(),
					Integer.parseInt(PropertiesHelper.getProperties().getProperty("es.caib.notib.procediment.alta.auto.retard", "0")),
					Integer.parseInt(PropertiesHelper.getProperties().getProperty("es.caib.notib.procediment.alta.auto.caducitat", "10")),
					entitat,
					null,
					null,
					false,
					llibre,
					llibreNom,
					oficina,
					oficinaNom,
					organGestor,
					null,
					null,
					null,
					null,
					procedimentGda.isComu()).build();
			
			procediment.updateDataActualitzacio(new Date());
			procedimentRepository.save(procediment);
			
//			logger.debug(">>>> >> Creat.");
			progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment.creat"));
			
		} else { 
//			logger.debug(">>>> >> Procediment EXISTENT ...");
			progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment.result.si"));
			
			if (modificar) {
				progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment.entitat"));

				if (!entitat.equals(procediment.getEntitat())) {
					progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment.entitat.result.no", new Object[] {procediment.getEntitat().getNom()}));
					progres.addInfo(TipusInfo.ERROR, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment.entitat.revisar", new Object[] {procedimentGda.getCodi(), procediment.getEntitat().getNom()}));
					throw new ValidationException(
							procedimentGda.getId(),
							ProcedimentEntity.class,
							"El procediment '" + procediment.getNom() + "'  no pertany a la entitat actual (id=" + entitat.getId() + ") ");
				}
				
				progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment.entitat.result.si"));
				progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment.update"));
				
				// UPDATE
				if (!procediment.getOrganGestor().getCodi().equals(procedimentGda.getOrganGestor()) ||
					!procediment.getNom().equals(procedimentGda.getNom()) || 
					procediment.isComu() != procedimentGda.isComu()) {
					// Si canviam l'organ gestor, i aquest no s'utilitza en cap altre procediment, l'eliminarem (1)
					if (!procediment.getOrganGestor().getCodi().equals(procedimentGda.getOrganGestor())) {
						progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment.update.organ"));
						organsGestorsModificats.add(procediment.getOrganGestor());
					}
					if (!procediment.getNom().equals(procedimentGda.getNom())) {
						progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment.update.nom"));
					}
					progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment.update.result.si"));
					progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment.updating", new Object[] {procedimentGda.getCodi()}));
					
					procediment.update(
							procedimentGda.getNom(),
							organGestor,
							procedimentGda.isComu());
					procediment.updateDataActualitzacio(new Date());
					procedimentRepository.save(procediment);
					
					t2 = System.currentTimeMillis();
//					logger.debug(">>>> >> Modificat (" + (t2 - t1) + "ms)");
					progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment.updated"));
					
				} else {
//					logger.debug(">>>> >> NO es necessari realitzar cap modificació.");
					progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment.update.result.no"));
				}
			} else {
				progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.modificar.inactiu"));
			}
		}
		t2 = System.currentTimeMillis();
//		logger.debug(">>>> ..........................................................................");
//		logger.debug(">>>> ..........................................................................");
		progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.result", new Object[] {procedimentGda.getNom()}));
		progres.addInfo(TipusInfo.TEMPS, messageHelper.getMessage("procediment.actualitzacio.auto.temps", new Object[] {(t2 - t1)}));
		progres.addSeparador();
		progres.incrementProcedimentsActualitzats();
	}
	
	private static final Logger logger = LoggerFactory.getLogger(ProcedimentHelper.class);

}
