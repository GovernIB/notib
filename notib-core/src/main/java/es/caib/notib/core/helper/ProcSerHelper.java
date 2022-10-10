package es.caib.notib.core.helper;

import com.google.common.base.Strings;
import es.caib.notib.core.api.dto.PermisDto;
import es.caib.notib.core.api.dto.ProgresActualitzacioDto;
import es.caib.notib.core.api.dto.ProgresActualitzacioDto.TipusInfo;
import es.caib.notib.core.api.dto.procediment.ProcSerDataDto;
import es.caib.notib.core.api.dto.procediment.ProcSerDto;
import es.caib.notib.core.api.service.OrganGestorService;
import es.caib.notib.core.cacheable.ProcSerCacheable;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.GrupEntity;
import es.caib.notib.core.entity.GrupProcSerEntity;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.OrganGestorEntity;
import es.caib.notib.core.entity.ProcSerEntity;
import es.caib.notib.core.entity.ProcSerOrganEntity;
import es.caib.notib.core.entity.ProcedimentEntity;
import es.caib.notib.core.entity.ServeiEntity;
import es.caib.notib.core.repository.GrupProcSerRepository;
import es.caib.notib.core.repository.OrganGestorRepository;
import es.caib.notib.core.repository.ProcedimentRepository;
import es.caib.notib.core.repository.ServeiRepository;
import es.caib.notib.core.security.ExtendedPermission;
import es.caib.notib.plugin.usuari.DadesUsuari;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Helper per a convertir entities a dto
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class ProcSerHelper {
	
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private PermisosHelper permisosHelper;
	@Autowired
	private ProcSerUpdateHelper procSerUpdateHelper;
	@Autowired
	private GrupProcSerRepository grupProcSerRepository;
	@Autowired
	private ProcedimentRepository procedimentRepository;
	@Autowired
	private ServeiRepository serveiRepository;
	@Autowired
	private OrganGestorRepository organGestorRepository;
	@Resource
	private OrganGestorService organGestorService;
	@Resource
	private MessageHelper messageHelper;
	@Resource
	private ProcSerCacheable procedimentsCacheable;
	@Resource
	private OrganigramaHelper organigramaHelper;

	public static final String PROCEDIMENT_ORGAN_NO_SYNC = "Hi ha procediments que pertanyen a òrgans no existents en l'organigrama actual";

	/**
	 * Retorna el codi de tots els procediments que tenen un determinat permís per a totes les notificacions.
	 *
	 * @param auth
	 * @param entitat
	 * @param permisos
	 * @return
	 */
	public List<String> findCodiProcedimentsWithPermis(Authentication auth, EntitatEntity entitat, Permission[] permisos) {

		// Procediments comuns amb permís a un òrgan gestor
		List<ProcSerEntity> procediments = procedimentsCacheable.getProcedimentsWithPermis(auth.getName(), entitat, permisos);
		Set<String> codis = new HashSet<>();
		for (ProcSerEntity procediment : procediments) {
			codis.add(procediment.getCodi());
		}

		return new ArrayList<>(codis);
	}

	/**
	 * Retorna un codi únic per a totes les tuples organ-procediment que tenen el permís indicat per paràmetre.
	 *
	 * @param auth
	 * @param entitat
	 * @param permisos
	 * @return
	 */
	public List<String> findCodiProcedimentsOrganWithPermis(Authentication auth, EntitatEntity entitat, Permission[] permisos) {

		List<ProcSerOrganEntity> procedimentOrgansAmbPermis = procedimentsCacheable.getProcedimentOrganWithPermis(auth, entitat, permisos);
		List<String> codisProcedimentsOrgans = new ArrayList<>();
		for (ProcSerOrganEntity procedimentOrganEntity : procedimentOrgansAmbPermis) {
			codisProcedimentsOrgans.add(procedimentOrganEntity.getProcSer().getCodi() + "-" + procedimentOrganEntity.getOrganGestor().getCodi());
		}
		return codisProcedimentsOrgans;
	}

	public void omplirPermisos(ProcSerDto procediment, boolean ambLlistaPermisos) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		procediment.setUsuariActualRead(permisosHelper.isGrantedAll(procediment.getId(), ProcedimentEntity.class, new Permission[] {ExtendedPermission.READ}, auth));
		procediment.setUsuariActualProcessar(permisosHelper.isGrantedAll(procediment.getId(), ProcedimentEntity.class, new Permission[] {ExtendedPermission.PROCESSAR}, auth));
		procediment.setUsuariActualNotificacio(permisosHelper.isGrantedAll(procediment.getId(), ProcedimentEntity.class, new Permission[] {ExtendedPermission.NOTIFICACIO}, auth));
		procediment.setUsuariActualAdministration(permisosHelper.isGrantedAll(procediment.getId(), ProcedimentEntity.class, new Permission[] {ExtendedPermission.ADMINISTRATION}, auth));
		if (!ambLlistaPermisos) {
			return;
		}
		List<PermisDto> permisos = permisosHelper.findPermisos(procediment.getId(), ProcedimentEntity.class);
		procediment.setPermisos(permisos);
	}

	@Cacheable(value = "findUsuarisAmbPermis", key = "#notificacio.getProcediment().getId().toString().concat('-').concat(#notificacio.getOrganGestor().getCodi())")
	public Set<String> findUsuaris(NotificacioEntity notificacio) {

		if (notificacio.getProcediment() == null) {
			return new HashSet<>();
		}
		Set<String> usuarisAmbPermis = findUsuarisAmbPermisReadPerProcediment(notificacio);
		if(usuarisAmbPermis.isEmpty() || !notificacio.getProcediment().isAgrupar() || Strings.isNullOrEmpty(notificacio.getGrupCodi())) {
			return usuarisAmbPermis;
		}
		List<DadesUsuari> usuarisGrup = pluginHelper.dadesUsuariConsultarAmbGrup(notificacio.getGrupCodi());
		if (usuarisGrup == null) {
			return usuarisAmbPermis;
		}
		List<String> usuarisDelGrup = new ArrayList<>();
		for (DadesUsuari u : usuarisGrup) {
			usuarisDelGrup.add(u.getCodi());
		}
		usuarisAmbPermis.retainAll(usuarisDelGrup);
		return usuarisAmbPermis;
	}

	public Set<String> findUsuarisAmbPermisReadPerProcediment(NotificacioEntity not) {

		ProcSerEntity procediment = not.getProcediment();
		StringBuilder sb = new StringBuilder("Preparant la llista d'usuaris per enviar l'email: ");
		List<PermisDto> permisos;
		permisos = permisosHelper.findPermisos(procediment.getId(), ProcedimentEntity.class);

		List<OrganGestorEntity> organs = new ArrayList<>();
		if (!procediment.isComu() && procediment.getOrganGestor() != null ) {
			organs = organigramaHelper.getOrgansGestorsParesExistentsByOrgan(not.getEntitat().getDir3Codi(), procediment.getOrganGestor().getCodi());
		}

		if (procediment.isComu() && not.getOrganGestor() != null) {
			organs = organigramaHelper.getOrgansGestorsParesExistentsByOrgan(not.getEntitat().getDir3Codi(), not.getOrganGestor().getCodi());

			List<ProcSerOrganEntity> procSerOrgansGestorsParesExistentsByOrgan = organigramaHelper.getProcSerOrgansGestorsParesExistentsByOrgan(procediment.getId(), not.getEntitat().getDir3Codi(), not.getOrganGestor().getCodi());
			for (ProcSerOrganEntity po: procSerOrgansGestorsParesExistentsByOrgan) {
				permisos.addAll(permisosHelper.findPermisos(po.getId(), ProcSerOrganEntity.class));
			}
		}
		for (OrganGestorEntity po: organs) {
			permisos.addAll(permisosHelper.findPermisos(po.getId(), OrganGestorEntity.class));
		}



		Set<String> usuaris = new HashSet<String>();
		for (PermisDto permis: permisos) {
			if (!permis.isRead()) {
				continue;
			}
			switch (permis.getTipus()) {
				case USUARI:
				usuaris.add(permis.getPrincipal());
				sb.append(" usuari ").append(permis.getPrincipal());
				break;
				case ROL:
				List<DadesUsuari> usuarisGrup = pluginHelper.dadesUsuariConsultarAmbGrup(permis.getPrincipal());
				sb.append(" rol ").append(permis.getPrincipal()).append(" (");
				if (usuarisGrup != null) {
					for (DadesUsuari usuariGrup : usuarisGrup) {
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
	
	public Set<String> findUsuarisAmbPermisReadPerGrup(ProcSerEntity procediment) {

		StringBuilder sb = new StringBuilder("Preparant la llista d'usuaris per enviar l'email: ");
		List<GrupProcSerEntity> grupsProcediment = grupProcSerRepository.findByProcSer(procediment);
		List<PermisDto> permisos = new ArrayList<>();
		permisos = permisosHelper.findPermisos(procediment.getId(), ProcedimentEntity.class);
		Set<String> usuaris = new HashSet<String>();
		for (GrupProcSerEntity permisGrup: grupsProcediment) {
			List<DadesUsuari> usuarisGrup = pluginHelper.dadesUsuariConsultarAmbGrup(permisGrup.getGrup().getCodi());
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

	public Set<String> findUsuarisAmbPermisReadPerGrupNotificacio(GrupEntity grup, ProcSerEntity procediment) {

		StringBuilder sb = new StringBuilder("Preparant la llista d'usuaris per enviar l'email: ");
		GrupProcSerEntity grupProcediment = grupProcSerRepository.findByGrupAndProcSer(grup, procediment);
		List<PermisDto> permisos = new ArrayList<PermisDto>();
		permisos = permisosHelper.findPermisos(procediment.getId(), ProcedimentEntity.class);
		Set<String> usuaris = new HashSet<String>();
		if (grupProcediment != null) {
			List<DadesUsuari> usuarisGrup = pluginHelper.dadesUsuariConsultarAmbGrup(grupProcediment.getGrup().getCodi());
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

	/**
	 * El procediment s'ha d'actualitzar si compleix les seguents condicions:
	 *  - El procediement de GDA té el codi Sia definit
	 *  - No Existeix a la base de dades o s'ha modificat desde la última actualització
	 *  - L'ògan gestor del procediement de GDA apareix a l'rganigrama.
	 *
	 * @param procedimentGda Procediment obtingut desde GDA
	 * @param procedimentEntity Procediment de la base de dades
	 * @param codiOrgansGda Organigrama dels òrgans gestors de l'entitat
	 * @param progres Seguiment del progrés d'actualització
	 *
	 * @return Si el procediment s'ha d'actualitzar
	 */
	private boolean procedimentHasToBeUpdated(
			ProcSerDataDto procedimentGda,
			ProcedimentEntity procedimentEntity,
//			Map<String, OrganismeDto> organigramaEntitat,
			List<String> codiOrgansGda,
			ProgresActualitzacioDto progres) {

		if (procedimentGda.getCodi() == null || procedimentGda.getCodi().isEmpty()) {
			progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.descartat"));
			progres.addSeparador();
//			progres.incrementOperacionsRealitzades();
			return false;
		}

		if (procedimentEntity != null) {
			// Si el darrer pic que el varem actualitzar es posterior a la darrera actualització a GDA no fa falta actualitzar
			if (procedimentEntity.getUltimaActualitzacio() != null && procedimentGda.getUltimaActualitzacio() != null &&
					procedimentEntity.getUltimaActualitzacio().after(procedimentGda.getUltimaActualitzacio())) {
				progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.descartat.data"));
				progres.addSeparador();
//				progres.incrementOperacionsRealitzades();
				return false;
			}
		}

		if (!codiOrgansGda.contains(procedimentGda.getOrganGestor())) {
			//if (organigramaEntitat.get(procedimentGda.getOrganGestor())==null) {
			// Si l'Organ gestor del procediment no existeix dins el nostre organigrama, no es guarda el procediment
			progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.descartat.noOrganDinsOrganigrama", new Object[] {procedimentGda.getOrganGestor()}));
			progres.addSeparador();
//			progres.incrementOperacionsRealitzades();
			return false;
		}
		return true;
	}

	private boolean serveiHasToBeUpdated(
			ProcSerDataDto serveiGda,
			ServeiEntity serveiEntity,
//			Map<String, OrganismeDto> organigramaEntitat,
			List<String> codiOrgansGda,
			ProgresActualitzacioDto progres) {

		if (serveiGda.getCodi() == null || serveiGda.getCodi().isEmpty()) {
			progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("servei.actualitzacio.auto.processar.servei.descartat"));
			progres.addSeparador();
//			progres.incrementOperacionsRealitzades();
			return false;
		}

		if (serveiEntity != null) {
			// Si el darrer pic que el varem actualitzar es posterior a la darrera actualització a GDA no fa falta actualitzar
			if (serveiEntity.getUltimaActualitzacio() != null && serveiGda.getUltimaActualitzacio() != null &&
					serveiEntity.getUltimaActualitzacio().after(serveiGda.getUltimaActualitzacio())) {
				progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("servei.actualitzacio.auto.processar.servei.descartat.data"));
				progres.addSeparador();
//				progres.incrementOperacionsRealitzades();
				return false;
			}
		}

		if (!codiOrgansGda.contains(serveiGda.getOrganGestor())) {
			//if (organigramaEntitat.get(procedimentGda.getOrganGestor())==null) {
			// Si l'Organ gestor del procediment no existeix dins el nostre organigrama, no es guarda el procediment
			progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("servei.actualitzacio.auto.processar.servei.descartat.noOrganDinsOrganigrama", new Object[] {serveiGda.getOrganGestor()}));
			progres.addSeparador();
//			progres.incrementOperacionsRealitzades();
			return false;
		}
		return true;
	}


	@Transactional(timeout = 300, propagation = Propagation.REQUIRES_NEW)
	public void actualitzarProcedimentFromGda(
			ProgresActualitzacioDto progres,
			ProcSerDataDto procedimentGda,
			EntitatEntity entitat,
//			Map<String, OrganismeDto> organigramaEntitat,
			List<String> codiOrgansGda,
			boolean modificar,
			List<OrganGestorEntity> organsGestorsModificats,
			Map<String, String[]> avisosProcedimentsOrgans) {
		
		Long t1 = System.currentTimeMillis();
		OrganGestorEntity organGestorGda = null;

		try {
			ProcedimentEntity procediment = procedimentRepository.findByCodiAndEntitat(procedimentGda.getCodi(), entitat);

			if (!procedimentHasToBeUpdated(procedimentGda, procediment, codiOrgansGda, progres)) {
				return;
			}

			// Organ gestor
			progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.organ", new Object[]{procedimentGda.getOrganGestor()}));

			if (!procedimentGda.isComu() && procedimentGda.getOrganGestor() != null && !procedimentGda.getOrganGestor().isEmpty()) {
				organGestorGda = organGestorRepository.findByCodi(procedimentGda.getOrganGestor());
				progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.organ.result." + (organGestorGda == null ? "no" : "si")));
			}
			logger.trace(">>>> >> organ gestor " + (organGestorGda == null ? "NOU" : "EXISTENT"));

			progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment", new Object[]{procedimentGda.getCodi()}));
			if (procediment == null) {
				progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment.result.no"));
				progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment.crear", new Object[]{procedimentGda.getCodi()}));

				// CREATE
				if (!procedimentGda.isComu() && organGestorGda == null) {
					progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment.organ.result.no", new Object[]{procedimentGda.getOrganGestor()}));
					progres.addInfo(TipusInfo.ERROR, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment.organ.sync", new Object[]{procedimentGda.getCodi(), procediment.getEntitat().getNom()}));
					// Organ no sincronitzat
					procediment.setOrganNoSincronitzat(true);
					avisosProcedimentsOrgans.put(procedimentGda.getNom(), new String[] { "sense òrgan", procedimentGda.getOrganGestor() });
					return;
				}
				procediment = procSerUpdateHelper.nouProcediment(procedimentGda, entitat, organGestorGda);
				progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment.creat"));
			} else {
				progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment.result.si"));

				if (modificar) {
					progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment.entitat"));

					if (!entitat.equals(procediment.getEntitat())) {
						progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment.entitat.result.no", new Object[]{procediment.getEntitat().getNom()}));
						progres.addInfo(TipusInfo.ERROR, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment.entitat.revisar", new Object[]{procedimentGda.getCodi(), procediment.getEntitat().getNom()}));
						return;
					}

					if (!procedimentGda.isComu() && organGestorGda == null) {
						progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment.organ.result.no", new Object[]{procedimentGda.getOrganGestor()}));
						progres.addInfo(TipusInfo.ERROR, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment.organ.sync", new Object[]{procedimentGda.getCodi(), procediment.getEntitat().getNom()}));
						// Organ no sincronitzat
						String organOriginal = procediment.getOrganGestor() != null ? procediment.getOrganGestor().getCodi() + " - " + procediment.getOrganGestor().getNom() : "sense òrgan";
						avisosProcedimentsOrgans.put(procedimentGda.getNom(), new String[] { organOriginal, procedimentGda.getOrganGestor() });
						return;
					}

					progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment.entitat.result.si"));
					progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment.update"));

					// UPDATE
					OrganGestorEntity organProcediment = procediment.getOrganGestor();
					boolean haCanviatOrgan = (organProcediment == null && procedimentGda.getOrganGestor() != null) ||
							(organProcediment != null && !organProcediment.getCodi().equals(procedimentGda.getOrganGestor()));
					if (haCanviatOrgan ||
							!procediment.getNom().equals(procedimentGda.getNom()) ||
							procediment.isComu() != procedimentGda.isComu()) {

						// Si canviam l'organ gestor, i aquest no s'utilitza en cap altre procediment, l'eliminarem (1)
						if (haCanviatOrgan) {
							progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment.update.organ"));
							organsGestorsModificats.add(organProcediment);
						}
						if (!procediment.getNom().equals(procedimentGda.getNom())) {
							progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment.update.nom"));
						}
						progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment.update.result.si"));
						progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment.updating", new Object[]{procedimentGda.getCodi()}));

						procSerUpdateHelper.updateProcediment(procedimentGda, procediment, organGestorGda);
						progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment.updated"));

					} else {
						progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment.update.result.no"));
					}
				} else {
					progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.modificar.inactiu"));
					return;
				}
			}
			Long t2 = System.currentTimeMillis();
			progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.result", new Object[]{procedimentGda.getNom()}));
			progres.addInfo(TipusInfo.TEMPS, messageHelper.getMessage("procediment.actualitzacio.auto.temps", new Object[]{(t2 - t1)}));
		} catch (Exception ex) {
			log.error("Error no esperat al actualitzar el procediment", ex);
		}
	}

	@Transactional
	public void deshabilitarProcedimentsNoActius(List<ProcSerDto> procedimentsGda, ProgresActualitzacioDto progres) {
		List<String> procedimentsActiusNotib = procedimentRepository.findCodiActius();
		for (ProcSerDto procedimentGda: procedimentsGda) {
			procedimentsActiusNotib.remove(procedimentGda.getCodi());
		}
		for (String codi: procedimentsActiusNotib) {
			procedimentRepository.updateActiu(codi, false);
			progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.deshabilitar.procediment", new Object[] {codi}));
		}
	}

	@Transactional(timeout = 300, propagation = Propagation.REQUIRES_NEW)
	public void actualitzarServeiFromGda(
			ProgresActualitzacioDto progres,
			ProcSerDataDto serveiGda,
			EntitatEntity entitat,
//			Map<String, OrganismeDto> organigramaEntitat,
			List<String> codiOrgansGda,
			boolean modificar,
			List<OrganGestorEntity> organsGestorsModificats,
			Map<String, String[]> avisosProcedimentsOrgans) {

		Long t1 = System.currentTimeMillis();
		OrganGestorEntity organGestorGda = null;

		try {
			ServeiEntity servei = serveiRepository.findByCodiAndEntitat(serveiGda.getCodi(), entitat);

			if (!serveiHasToBeUpdated(serveiGda, servei, codiOrgansGda, progres)) {
				return;
			}

			// Organ gestor
			progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("servei.actualitzacio.auto.processar.servei.organ", new Object[]{serveiGda.getOrganGestor()}));

			if (!serveiGda.isComu() && serveiGda.getOrganGestor() != null && !serveiGda.getOrganGestor().isEmpty()) {
				organGestorGda = organGestorRepository.findByCodi(serveiGda.getOrganGestor());
				progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.servei.organ.result." + (organGestorGda == null ? "no" : "si")));
			}
			logger.trace(">>>> >> organ gestor " + (organGestorGda == null ? "NOU" : "EXISTENT"));

			progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("servei.actualitzacio.auto.processar.servei.servei", new Object[]{serveiGda.getCodi()}));
			if (servei == null) {
				progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("servei.actualitzacio.auto.processar.servei.servei.result.no"));
				progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("servei.actualitzacio.auto.processar.servei.servei.crear", new Object[]{serveiGda.getCodi()}));

				// CREATE
				if (!serveiGda.isComu() && organGestorGda == null) {
					progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("servei.actualitzacio.auto.processar.servei.servei.organ.result.no", new Object[]{serveiGda.getOrganGestor()}));
					progres.addInfo(TipusInfo.ERROR, messageHelper.getMessage("servei.actualitzacio.auto.processar.servei.servei.organ.sync", new Object[]{serveiGda.getCodi(), servei.getEntitat().getNom()}));
					// Organ no sincronitzat
					servei.setOrganNoSincronitzat(true);
					avisosProcedimentsOrgans.put(serveiGda.getNom(), new String[] { "sense òrgan", serveiGda.getOrganGestor() });
					return;
				}
				procSerUpdateHelper.nouServei(serveiGda, entitat, organGestorGda);
				progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("servei.actualitzacio.auto.processar.servei.servei.creat"));
			} else {
				progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("servei.actualitzacio.auto.processar.servei.servei.result.si"));

				if (modificar) {
					progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("servei.actualitzacio.auto.processar.servei.servei.entitat"));

					if (!entitat.equals(servei.getEntitat())) {
						progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("servei.actualitzacio.auto.processar.servei.servei.entitat.result.no", new Object[]{servei.getEntitat().getNom()}));
						progres.addInfo(TipusInfo.ERROR, messageHelper.getMessage("servei.actualitzacio.auto.processar.servei.servei.entitat.revisar", new Object[]{serveiGda.getCodi(), servei.getEntitat().getNom()}));
						return;
					}

					if (!serveiGda.isComu() && organGestorGda == null) {
						progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("servei.actualitzacio.auto.processar.servei.servei.organ.result.no", new Object[]{serveiGda.getOrganGestor()}));
						progres.addInfo(TipusInfo.ERROR, messageHelper.getMessage("servei.actualitzacio.auto.processar.servei.servei.organ.sync", new Object[]{serveiGda.getCodi(), servei.getEntitat().getNom()}));
						// Organ no sincronitzat
						String organOriginal = servei.getOrganGestor() != null ? servei.getOrganGestor().getCodi() + " - " + servei.getOrganGestor().getNom() : "sense òrgan";
						avisosProcedimentsOrgans.put(serveiGda.getNom(), new String[] { organOriginal, serveiGda.getOrganGestor() });
						return;
					}

					progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("servei.actualitzacio.auto.processar.servei.servei.entitat.result.si"));
					progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("servei.actualitzacio.auto.processar.servei.servei.update"));

					// UPDATE
					OrganGestorEntity organProcediment = servei.getOrganGestor();
					boolean haCanviatOrgan = (organProcediment == null && serveiGda.getOrganGestor() != null) ||
							(organProcediment != null && !organProcediment.getCodi().equals(serveiGda.getOrganGestor()));
					if (haCanviatOrgan ||
							!servei.getNom().equals(serveiGda.getNom()) ||
							servei.isComu() != serveiGda.isComu()) {

						// Si canviam l'organ gestor, i aquest no s'utilitza en cap altre procediment, l'eliminarem (1)
						if (haCanviatOrgan) {
							progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("servei.actualitzacio.auto.processar.servei.servei.update.organ"));
							organsGestorsModificats.add(organProcediment);
						}
						if (!servei.getNom().equals(serveiGda.getNom())) {
							progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("servei.actualitzacio.auto.processar.servei.servei.update.nom"));
						}
						progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("servei.actualitzacio.auto.processar.servei.servei.update.result.si"));
						progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("servei.actualitzacio.auto.processar.servei.servei.updating", new Object[]{serveiGda.getCodi()}));

						procSerUpdateHelper.updateServei(serveiGda, servei, organGestorGda);
						progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("servei.actualitzacio.auto.processar.servei.servei.updated"));

					} else {
						progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("servei.actualitzacio.auto.processar.servei.servei.update.result.no"));
					}
				} else {
					progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("servei.actualitzacio.auto.processar.servei.modificar.inactiu"));
					return;
				}
			}
			Long t2 = System.currentTimeMillis();
			progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("servei.actualitzacio.auto.processar.servei.result", new Object[] {serveiGda.getNom()}));
			progres.addInfo(TipusInfo.TEMPS, messageHelper.getMessage("servei.actualitzacio.auto.temps", new Object[] {(t2 - t1)}));
		} catch (Exception ex) {
			log.error("Error no esperat al actualitzar el servei", ex);
		}
	}

	@Transactional
	public void deshabilitarServeisNoActius(List<ProcSerDto> serveisGda, ProgresActualitzacioDto progres) {
		List<String> serveisActiusNotib = serveiRepository.findCodiActius();
		for (ProcSerDto serveiGda: serveisGda) {
			serveisActiusNotib.remove(serveiGda.getCodi());
		}
		for (String codi: serveisActiusNotib) {
			serveiRepository.updateActiu(codi, false);
			progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("servei.actualitzacio.auto.deshabilitar.procediment", new Object[] {codi}));
		}
	}

	@Transactional(timeout = 300, propagation = Propagation.REQUIRES_NEW)
	public void eliminarOrganSiNoEstaEnUs(ProgresActualitzacioDto progres, OrganGestorEntity organGestorAntic) {
		logger.debug(">>>> Processant organ gestor " + organGestorAntic.getCodi() + "...   ");
		progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.organ", new Object[] {organGestorAntic.getCodi()}));
		progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.organ.us"));

		// Si canviam l'organ gestor, i aquest no s'utilitza en cap altre procediment, l'eliminarem (2)
		if (!organGestorService.organGestorEnUs(organGestorAntic.getId())) {
			progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.organ.us.result.no"));
			progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.organ.permis.result.no"));
			progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.organ.result.borrar"));
			progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.organ.borrar", new Object[] {organGestorAntic.getCodi()}));
			organGestorRepository.delete(organGestorAntic);
			progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.organ.borrat"));
//			logger.debug(">>>> ELIMINAT: No té cap procediment ni permís assignat.");
		}else{
			progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.organ.permis.result.si"));
			progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.organ.result.permis"));
//			logger.debug(">>>> NO ELIMINAT: Té permisos configurats.");
			progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.organ.us.result.si"));
			progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.organ.result.procediments"));
		}
//		logger.debug(">>>> ..........................................................................");
		progres.addSeparador();
	}

	private static final Logger logger = LoggerFactory.getLogger(ProcSerHelper.class);

}
