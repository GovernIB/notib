package es.caib.notib.logic.helper;

import com.google.common.base.Strings;
import es.caib.notib.logic.cacheable.ProcSerCacheable;
import es.caib.notib.logic.intf.acl.ExtendedPermission;
import es.caib.notib.logic.intf.dto.CodiValorOrganGestorComuDto;
import es.caib.notib.logic.intf.dto.PermisDto;
import es.caib.notib.logic.intf.dto.PermisEnum;
import es.caib.notib.logic.intf.dto.ProgresActualitzacioDto;
import es.caib.notib.logic.intf.dto.ProgresActualitzacioDto.TipusInfo;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum;
import es.caib.notib.logic.intf.dto.procediment.ProcSerDataDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerDto;
import es.caib.notib.logic.intf.dto.procediment.ProgresActualitzacioProcSer;
import es.caib.notib.logic.intf.service.PermisosService;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.GrupEntity;
import es.caib.notib.persist.entity.GrupProcSerEntity;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.entity.OrganGestorEntity;
import es.caib.notib.persist.entity.ProcSerEntity;
import es.caib.notib.persist.entity.ProcSerOrganEntity;
import es.caib.notib.persist.entity.ProcedimentEntity;
import es.caib.notib.persist.entity.ServeiEntity;
import es.caib.notib.persist.repository.GrupProcSerRepository;
import es.caib.notib.persist.repository.GrupRepository;
import es.caib.notib.persist.repository.NotificacioRepository;
import es.caib.notib.persist.repository.OrganGestorRepository;
import es.caib.notib.persist.repository.PagadorCieRepository;
import es.caib.notib.persist.repository.PagadorPostalRepository;
import es.caib.notib.persist.repository.ProcSerRepository;
import es.caib.notib.persist.repository.ProcedimentRepository;
import es.caib.notib.persist.repository.ServeiRepository;
import es.caib.notib.plugin.usuari.DadesUsuari;
import lombok.extern.slf4j.Slf4j;
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
	private PermisosService permisosService;
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
	@Autowired
	private NotificacioRepository notificacioRepository;
	@Autowired
	private ProcSerRepository procSerRepository;
	@Autowired
	private GrupRepository grupReposity;
	@Autowired
	private PagadorCieRepository pagadorCieReposity;
	@Autowired
	private PagadorPostalRepository pagadorPostalReposity;
	@Resource
	private MessageHelper messageHelper;
	@Resource
	private OrganigramaHelper organigramaHelper;

	private static final String PROC_ACTIVAT = "procediment.activat";
	private static final String SENSE_ORGAN = "sense òrgan";
	private static final String LLISTA_MAIL_TEXT = "Preparant la llista d'usuaris per enviar l'email: ";
	private static final String ROL = " rol ";

	/**
	 * Retorna el codi de tots els procediments que tenen un determinat permís per a totes les notificacions.
	 *
	 * @param auth
	 * @param entitat
	 * @param permis
	 * @return
	 */
	public List<String> findCodiProcedimentsWithPermis(Authentication auth, EntitatEntity entitat, PermisEnum permis) {

		// Procediments comuns amb permís a un òrgan gestor
		List<CodiValorOrganGestorComuDto> procediments = permisosService.getProcSersAmbPermis(entitat.getId(), auth.getName(), permis);
		Set<String> codis = new HashSet<>();
		for (CodiValorOrganGestorComuDto procediment : procediments) {
			codis.add(procediment.getCodi());
		}
		return new ArrayList<>(codis);
	}

	public void omplirPermisos(ProcSerDto procediment, boolean ambLlistaPermisos) {

		var auth = SecurityContextHolder.getContext().getAuthentication();
		procediment.setUsuariActualRead(permisosHelper.isGrantedAll(procediment.getId(), ProcedimentEntity.class, new Permission[] {ExtendedPermission.READ}, auth));
		procediment.setUsuariActualProcessar(permisosHelper.isGrantedAll(procediment.getId(), ProcedimentEntity.class, new Permission[] {ExtendedPermission.PROCESSAR}, auth));
		procediment.setUsuariActualNotificacio(permisosHelper.isGrantedAll(procediment.getId(), ProcedimentEntity.class, new Permission[] {ExtendedPermission.NOTIFICACIO}, auth));
		procediment.setUsuariActualAdministration(permisosHelper.isGrantedAll(procediment.getId(), ProcedimentEntity.class, new Permission[] {ExtendedPermission.ADMINISTRATION}, auth));
		if (!ambLlistaPermisos) {
			return;
		}
		var permisos = permisosHelper.findPermisos(procediment.getId(), ProcedimentEntity.class);
		procediment.setPermisos(permisos);
	}

	@Cacheable(value = "findUsuarisAmbPermis", key = "#notificacio.getProcediment().getId().toString().concat('-').concat(#notificacio.getOrganGestor().getCodi())")
	public Set<String> findUsuaris(NotificacioEntity notificacio) {

		if (notificacio.getProcediment() == null) {
			return new HashSet<>();
		}
		var usuarisAmbPermis = findUsuarisAmbPermisReadPerProcediment(notificacio);
		if(usuarisAmbPermis.isEmpty() || !notificacio.getProcediment().isAgrupar() || Strings.isNullOrEmpty(notificacio.getGrupCodi())) {
			return usuarisAmbPermis;
		}
		var usuarisGrup = pluginHelper.dadesUsuariConsultarAmbGrup(notificacio.getGrupCodi());
		if (usuarisGrup == null) {
			return usuarisAmbPermis;
		}
		List<String> usuarisDelGrup = new ArrayList<>();
		for (var u : usuarisGrup) {
			usuarisDelGrup.add(u.getCodi());
		}
		usuarisAmbPermis.retainAll(usuarisDelGrup);
		return usuarisAmbPermis;
	}

	public Set<String> findUsuarisAmbPermisReadPerProcediment(NotificacioEntity not) {

		var procediment = not.getProcediment();
		var sb = new StringBuilder(LLISTA_MAIL_TEXT);
		var permisos = permisosHelper.findPermisos(procediment.getId(), ProcedimentEntity.class);
		List<OrganGestorEntity> organs = new ArrayList<>();
		if (!procediment.isComu() && procediment.getOrganGestor() != null ) {
			organs = organigramaHelper.getOrgansGestorsParesExistentsByOrgan(not.getEntitat().getDir3Codi(), procediment.getOrganGestor().getCodi());
		}
		if (procediment.isComu() && not.getOrganGestor() != null) {
			organs = organigramaHelper.getOrgansGestorsParesExistentsByOrgan(not.getEntitat().getDir3Codi(), not.getOrganGestor().getCodi());
			var procSerOrgansGestorsParesExistentsByOrgan = organigramaHelper.getProcSerOrgansGestorsParesExistentsByOrgan(procediment.getId(), not.getEntitat().getDir3Codi(), not.getOrganGestor().getCodi());
			for (var po: procSerOrgansGestorsParesExistentsByOrgan) {
				permisos.addAll(permisosHelper.findPermisos(po.getId(), ProcSerOrganEntity.class));
			}
		}
		for (var po: organs) {
			permisos.addAll(permisosHelper.findPermisos(po.getId(), OrganGestorEntity.class));
		}
		Set<String> usuaris = new HashSet<>();
		for (var permis: permisos) {
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
					sb.append(ROL).append(permis.getPrincipal()).append(" (");
					if (usuarisGrup != null) {
						for (var usuariGrup : usuarisGrup) {
							usuaris.add(usuariGrup.getCodi());
							sb.append(" ").append(usuariGrup.getCodi());
						}
					}
					sb.append(")");
					break;
			}
		}
		log.debug(sb.toString());
		return usuaris;
	}
	
	public Set<String> findUsuarisAmbPermisReadPerGrup(ProcSerEntity procediment) {

		var sb = new StringBuilder(LLISTA_MAIL_TEXT);
		var grupsProcediment = grupProcSerRepository.findByProcSer(procediment);
		var permisos = permisosHelper.findPermisos(procediment.getId(), ProcedimentEntity.class);
		Set<String> usuaris = new HashSet<>();
		for (var permisGrup: grupsProcediment) {
			List<DadesUsuari> usuarisGrup = pluginHelper.dadesUsuariConsultarAmbGrup(permisGrup.getGrup().getCodi());
			sb.append(ROL).append(permisGrup.getGrup().getCodi()).append(" (");
			if (usuarisGrup != null) {
				for (var usuariGrup: usuarisGrup) {
					for (var permis : permisos) {
						if (permis.getPrincipal().equals(usuariGrup.getCodi())) {
							usuaris.add(usuariGrup.getCodi());
							sb.append(" ").append(usuariGrup.getCodi());
						}
					}
				}
			}
			sb.append(")");
		}
		log.debug(sb.toString());
		return usuaris;
	}

	public Set<String> findUsuarisAmbPermisReadPerGrupNotificacio(GrupEntity grup, ProcSerEntity procediment) {

		var sb = new StringBuilder(LLISTA_MAIL_TEXT);
		var grupProcediment = grupProcSerRepository.findByGrupAndProcSer(grup, procediment);
		var permisos = permisosHelper.findPermisos(procediment.getId(), ProcedimentEntity.class);
		Set<String> usuaris = new HashSet<>();
		if (grupProcediment != null) {
			var usuarisGrup = pluginHelper.dadesUsuariConsultarAmbGrup(grupProcediment.getGrup().getCodi());
			sb.append(ROL).append(grupProcediment.getGrup().getCodi()).append(" (");
			if (usuarisGrup != null) {
				for (var usuariGrup: usuarisGrup) {
					for (var permis : permisos) {
						if (permis.getPrincipal().equals(usuariGrup.getCodi())) {
							usuaris.add(usuariGrup.getCodi());
							sb.append(" ").append(usuariGrup.getCodi());
						}
					}
				}
			}
			sb.append(")");
		}
		log.debug(sb.toString());
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
	private boolean procedimentHasToBeUpdated(ProcSerDataDto procedimentGda, ProcedimentEntity procedimentEntity, List<String> codiOrgansGda, ProgresActualitzacioProcSer progres) {

		if (procedimentGda.getCodi() == null || procedimentGda.getCodi().isEmpty()) {
			progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.descartat"));
			progres.addSeparador();
			progres.addSenseCodiSia(procedimentGda);
			//id i nom de procediment gda.
			if (procedimentEntity != null) {
				procedimentEntity.setActiu(false);
				progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.desactivat"));
			}
			return false;
		}
			// Si el darrer pic que el varem actualitzar es posterior a la darrera actualització a GDA no fa falta actualitzar
		if (procedimentEntity != null && (procedimentEntity.getUltimaActualitzacio() != null && procedimentGda.getUltimaActualitzacio() != null &&
					procedimentEntity.getUltimaActualitzacio().after(procedimentGda.getUltimaActualitzacio()))) {

				progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.descartat.data"));
				progres.addSeparador();
				procedimentEntity.setActiu(true);
				progres.addInfo(TipusInfo.INFO, messageHelper.getMessage(PROC_ACTIVAT));
				return false;

		}

		if (!codiOrgansGda.contains(procedimentGda.getOrganGestor())) {
			// Si l'Organ gestor del procediment no existeix dins el nostre organigrama, no es guarda el procediment

			//id i nom de procediment gda. sense organ gestor pertanyent a l'entiatt
			progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.descartat.noOrganDinsOrganigrama", new Object[] {procedimentGda.getOrganGestor()}));
			progres.addSeparador();
			progres.addAmbOrganNoPertanyEntitat(procedimentGda);
			if (procedimentEntity != null) {
				procedimentEntity.setActiu(false);
				progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.desactivat"));
			}
			return false;
		}
		return true;
	}

	private boolean serveiHasToBeUpdated(ProcSerDataDto serveiGda, ServeiEntity serveiEntity, List<String> codiOrgansGda, ProgresActualitzacioProcSer progres) {

		if (serveiGda.getCodi() == null || serveiGda.getCodi().isEmpty()) {
			progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("servei.actualitzacio.auto.processar.servei.descartat"));
			progres.addSeparador();
			if (serveiEntity != null) {
				serveiEntity.setActiu(false);
				progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("servei.desactivat"));
			}
			progres.addSenseCodiSia(serveiGda);
			return false;
		}

		if (serveiEntity != null && (serveiEntity.getUltimaActualitzacio() != null && serveiGda.getUltimaActualitzacio() != null &&
				serveiEntity.getUltimaActualitzacio().after(serveiGda.getUltimaActualitzacio()))) {

			progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("servei.actualitzacio.auto.processar.servei.descartat.data"));
			progres.addSeparador();
			serveiEntity.setActiu(true);
			progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("servei.activat"));
			return false;
		}

		if (!codiOrgansGda.contains(serveiGda.getOrganGestor())) {
			// Si l'Organ gestor del procediment no existeix dins el nostre organigrama, no es guarda el procediment
			var msg = messageHelper.getMessage("servei.actualitzacio.auto.processar.servei.descartat.noOrganDinsOrganigrama", new Object[] {serveiGda.getOrganGestor()});
			progres.addInfo(TipusInfo.INFO, msg);
			progres.addSeparador();
			progres.addAmbOrganNoPertanyEntitat(serveiGda);
			if (serveiEntity != null) {
				serveiEntity.setActiu(false);
				progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("servei.desactivat"));
			}
			return false;
		}
		return true;
	}


	@Transactional(timeout = 300, propagation = Propagation.REQUIRES_NEW)
	public void actualitzarProcedimentFromGda(ProgresActualitzacioProcSer progres, ProcSerDataDto procedimentGda, EntitatEntity entitat, List<String> codiOrgansGda,
											  boolean modificar, List<OrganGestorEntity> organsGestorsModificats, Map<String, String[]> avisosProcedimentsOrgans) {
		
		Long t1 = System.currentTimeMillis();
		OrganGestorEntity organGestorGda = null;
		try {
			var procediment = procedimentRepository.findByCodiAndEntitat(procedimentGda.getCodi(), entitat);
			if (!procedimentHasToBeUpdated(procedimentGda, procediment, codiOrgansGda, progres)) {
				return;
			}
			// Organ gestor
			progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.organ", new Object[]{procedimentGda.getOrganGestor()}));
			if (!procedimentGda.isComu() && procedimentGda.getOrganGestor() != null && !procedimentGda.getOrganGestor().isEmpty()) {
				organGestorGda = organGestorRepository.findByCodi(procedimentGda.getOrganGestor());
				progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.organ.result." + (organGestorGda == null ? "no" : "si")));
			}
			log.trace(">>>> >> organ gestor " + (organGestorGda == null ? "NOU" : "EXISTENT"));
			progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment", new Object[]{procedimentGda.getCodi()}));
			if (procediment == null) {
				progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment.result.no"));
				progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment.crear", new Object[]{procedimentGda.getCodi()}));
				// CREATE
				if (!procedimentGda.isComu() && organGestorGda == null) {
					progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment.organ.result.no", new Object[]{procedimentGda.getOrganGestor()}));
					progres.addInfo(TipusInfo.ERROR, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment.organ.sync", new Object[]{procedimentGda.getCodi(), entitat.getNom()}));
					avisosProcedimentsOrgans.put(procedimentGda.getNom(), new String[] { SENSE_ORGAN, procedimentGda.getOrganGestor() });
					return;
				}
				procSerUpdateHelper.nouProcediment(procedimentGda, entitat, organGestorGda);
				progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage(PROC_ACTIVAT));
				progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment.creat"));
			} else {
				progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment.result.si"));
				procediment.updateActiu(true);
				progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage(PROC_ACTIVAT));
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
						procediment.setOrganNoSincronitzat(true);
						String organOriginal = procediment.getOrganGestor() != null ? procediment.getOrganGestor().getCodi() + " - " + procediment.getOrganGestor().getNom() : SENSE_ORGAN;
						avisosProcedimentsOrgans.put(procedimentGda.getNom(), new String[] { organOriginal, procedimentGda.getOrganGestor() });
						return;
					}
					progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment.entitat.result.si"));
					progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment.procediment.update"));
					// UPDATE
					var organProcediment = procediment.getOrganGestor();
					var haCanviatOrgan = (organProcediment == null && procedimentGda.getOrganGestor() != null) ||
							(organProcediment != null && !organProcediment.getCodi().equals(procedimentGda.getOrganGestor()));

					if (haCanviatOrgan || !procediment.getNom().equals(procedimentGda.getNom()) || procediment.isComu() != procedimentGda.isComu()) {
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
	public void deshabilitarProcedimentsNoActius(List<ProcSerDto> procedimentsGda, String entitatCodi, ProgresActualitzacioProcSer progres) {

		var procedimentsActiusNotib = procedimentRepository.findCodiActiusByEntitat(entitatCodi);
		for (var procedimentGda: procedimentsGda) {
			procedimentsActiusNotib.remove(procedimentGda.getCodi());
		}
		progres.setNoActius(procedimentsActiusNotib);
		for (var codi: procedimentsActiusNotib) {
			procedimentRepository.updateActiu(codi, false);
			progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.deshabilitar.procediment", new Object[] {codi}));
		}
	}

	@Transactional(timeout = 300, propagation = Propagation.REQUIRES_NEW)
	public void actualitzarServeiFromGda(ProgresActualitzacioProcSer progres, ProcSerDataDto serveiGda, EntitatEntity entitat, List<String> codiOrgansGda,
										 boolean modificar, List<OrganGestorEntity> organsGestorsModificats, Map<String, String[]> avisosProcedimentsOrgans) {

		Long t1 = System.currentTimeMillis();
		OrganGestorEntity organGestorGda = null;
		try {
			var servei = serveiRepository.findByCodiAndEntitat(serveiGda.getCodi(), entitat);
			if (!serveiHasToBeUpdated(serveiGda, servei, codiOrgansGda, progres)) {
				return;
			}
			// Organ gestor
			progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("servei.actualitzacio.auto.processar.servei.organ", new Object[]{serveiGda.getOrganGestor()}));
			if (!serveiGda.isComu() && serveiGda.getOrganGestor() != null && !serveiGda.getOrganGestor().isEmpty()) {
				organGestorGda = organGestorRepository.findByCodi(serveiGda.getOrganGestor());
				progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.servei.organ.result." + (organGestorGda == null ? "no" : "si")));
			}
			log.trace(">>>> >> organ gestor " + (organGestorGda == null ? "NOU" : "EXISTENT"));
			progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("servei.actualitzacio.auto.processar.servei.servei", new Object[]{serveiGda.getCodi()}));
			if (servei == null) {
				progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("servei.actualitzacio.auto.processar.servei.servei.result.no"));
				progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("servei.actualitzacio.auto.processar.servei.servei.crear", new Object[]{serveiGda.getCodi()}));
				// CREATE
				if (!serveiGda.isComu() && organGestorGda == null) {
					progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("servei.actualitzacio.auto.processar.servei.servei.organ.result.no", new Object[]{serveiGda.getOrganGestor()}));
					progres.addInfo(TipusInfo.ERROR, messageHelper.getMessage("servei.actualitzacio.auto.processar.servei.servei.organ.sync", new Object[]{serveiGda.getCodi(), entitat.getNom()}));
					// Organ no sincronitzat
					avisosProcedimentsOrgans.put(serveiGda.getNom(), new String[] { SENSE_ORGAN, serveiGda.getOrganGestor() });
					return;
				}
				procSerUpdateHelper.nouServei(serveiGda, entitat, organGestorGda);
				progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage(PROC_ACTIVAT));
				progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("servei.actualitzacio.auto.processar.servei.servei.creat"));
			} else {
				progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("servei.actualitzacio.auto.processar.servei.servei.result.si"));
				servei.updateActiu(true);
				progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage(PROC_ACTIVAT));
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
						servei.setOrganNoSincronitzat(true);
						var organOriginal = servei.getOrganGestor() != null ? servei.getOrganGestor().getCodi() + " - " + servei.getOrganGestor().getNom() : SENSE_ORGAN;
						avisosProcedimentsOrgans.put(serveiGda.getNom(), new String[] { organOriginal, serveiGda.getOrganGestor() });
						return;
					}
					progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("servei.actualitzacio.auto.processar.servei.servei.entitat.result.si"));
					progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("servei.actualitzacio.auto.processar.servei.servei.update"));

					// UPDATE
					var organProcediment = servei.getOrganGestor();
					var haCanviatOrgan = (organProcediment == null && serveiGda.getOrganGestor() != null) ||
							(organProcediment != null && !organProcediment.getCodi().equals(serveiGda.getOrganGestor()));
					if (haCanviatOrgan || !servei.getNom().equals(serveiGda.getNom()) || servei.isComu() != serveiGda.isComu()) {
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
	public void deshabilitarServeisNoActius(List<ProcSerDto> serveisGda, String entitatCodi, ProgresActualitzacioProcSer progres) {

		var serveisActiusNotib = serveiRepository.findCodiActiusByEntitat(entitatCodi);
		for (var serveiGda: serveisGda) {
			serveisActiusNotib.remove(serveiGda.getCodi());
		}
		progres.setNoActius(serveisActiusNotib);
		for (var codi: serveisActiusNotib) {
			serveiRepository.updateActiu(codi, false);
			progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("servei.actualitzacio.auto.deshabilitar.procediment", new Object[] {codi}));
		}
	}

	@Transactional(timeout = 300, propagation = Propagation.REQUIRES_NEW)
	public void eliminarOrganSiNoEstaEnUs(ProgresActualitzacioDto progres, OrganGestorEntity organGestorAntic) {

		log.debug(">>>> Processant organ gestor " + organGestorAntic.getCodi() + "...   ");
		progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.organ", new Object[] {organGestorAntic.getCodi()}));
		progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.organ.us"));
		// Si canviam l'organ gestor, i aquest no s'utilitza en cap altre procediment, l'eliminarem (2)
		if (!organGestorEnUs(organGestorAntic.getId())) {
			progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.organ.us.result.no"));
			progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.organ.permis.result.no"));
			progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.organ.result.borrar"));
			progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.organ.borrar", new Object[] {organGestorAntic.getCodi()}));
			organGestorRepository.delete(organGestorAntic);
			progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.organ.borrat"));
		} else {
			progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.organ.permis.result.si"));
			progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.organ.result.permis"));
			progres.addInfo(TipusInfo.SUBINFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.organ.us.result.si"));
			progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.organ.result.procediments"));
		}
		progres.addSeparador();
	}

	@Transactional(readOnly = true)
	public boolean organGestorEnUs(Long organId) {

		//Compravacions en ús
		var organGestor = organGestorRepository.findById(organId).orElse(null);
		if (organGestor == null) {
			return true; // No en ús pq no existeix!!
		}
		if (OrganGestorEstatEnum.V.equals(organGestor.getEstat())) {
			return true;
		}
		if (notificacioRepository.countByOrganGestor(organGestor) > 0) {
			return true;
		}
		var procedimentsOrganGestor = procSerRepository.findByOrganGestorId(organId);
		if (procedimentsOrganGestor != null && !procedimentsOrganGestor.isEmpty()) {
			return true;
		}
		var grupsByOrganGestor = grupReposity.findByOrganGestorId(organId);
		if (grupsByOrganGestor != null && !grupsByOrganGestor.isEmpty()) {
			return true;
		}
		var pagCiesByOrganGestor = pagadorCieReposity.findByOrganGestor(organGestor);
		if (pagCiesByOrganGestor != null && !pagCiesByOrganGestor.isEmpty()) {
			return true;
		}
		var pagPostalByOrganGestor = pagadorPostalReposity.findByOrganGestorId(organId);
		return pagPostalByOrganGestor != null && !pagPostalByOrganGestor.isEmpty();
	}
}