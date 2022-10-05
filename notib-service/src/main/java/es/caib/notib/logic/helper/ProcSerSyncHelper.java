package es.caib.notib.logic.helper;

import es.caib.notib.logic.intf.dto.AccioParam;
import es.caib.notib.logic.intf.dto.AvisNivellEnumDto;
import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.logic.intf.dto.IntegracioInfo;
import es.caib.notib.logic.intf.dto.ProgresActualitzacioDto;
import es.caib.notib.logic.intf.dto.ProgresActualitzacioDto.ActualitzacioInfo;
import es.caib.notib.logic.intf.dto.ProgresActualitzacioDto.TipusInfo;
import es.caib.notib.logic.intf.dto.procediment.ProcSerDto;
import es.caib.notib.logic.cacheable.OrganGestorCachable;
import es.caib.notib.persist.entity.AvisEntity;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.OrganGestorEntity;
import es.caib.notib.persist.repository.AvisRepository;
import es.caib.notib.logic.service.ProcedimentServiceImpl;
import es.caib.notib.logic.service.ServeiServiceImpl;
import es.caib.notib.plugin.unitat.NodeDir3;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper per a convertir entities a dto
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class ProcSerSyncHelper {

	@Autowired
	private AvisRepository avisRepository;

	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private MessageHelper messageHelper;
	@Autowired
	private IntegracioHelper integracioHelper;

	@Autowired
	private OrganGestorCachable organGestorCachable;
	@Autowired
	private ProcSerHelper procSerHelper;
	@Autowired
	private ConfigHelper configHelper;

	// Sincronitzar procediments
	// ///////////////////////////////////////////////////////////////////////////

	public void actualitzaProcediments(EntitatDto entitatDto) {

		IntegracioInfo info = new IntegracioInfo(IntegracioHelper.INTCODI_PROCEDIMENT, "Actualització de procediments", IntegracioAccioTipusEnumDto.PROCESSAR, new AccioParam("Codi Dir3 de l'entitat", entitatDto.getDir3Codi()));
		info.setCodiEntitat(entitatDto.getCodi());
		ConfigHelper.setEntitat(entitatDto);
		log.debug("[PROCEDIMENTS] Inici actualitzar procediments");

		// Comprova si hi ha una altre instància del procés en execució
		ProgresActualitzacioDto progres = ProcedimentServiceImpl.progresActualitzacio.get(entitatDto.getDir3Codi());
		if (progres != null && (progres.getProgres() > 0 && progres.getProgres() < 100) && !progres.isError()) {
			log.debug("[PROCEDIMENTS] Ja existeix un altre procés que està executant l'actualització");
			return;	// Ja existeix un altre procés que està executant l'actualització.
		}

		// inicialitza el seguiment del prgrés d'actualització
		progres = new ProgresActualitzacioDto();
		ProcedimentServiceImpl.progresActualitzacio.put(entitatDto.getDir3Codi(), progres);
		Map<String, String[]> avisosProcedimentsOrgans = new HashMap<>();
		try {
			Long ti = System.currentTimeMillis();
			progres.addInfo(TipusInfo.TITOL, messageHelper.getMessage("procediment.actualitzacio.auto.inici", new Object[] {entitatDto.getNom()}));
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatDto.getId(), false, false, false);
			int totalElementsCons = getTotalProcediments(entitatDto.getDir3Codi());	// Procediments a processar
			progres.setNumOperacions((totalElementsCons * 2) + 1);
			List<ProcSerDto> procedimentsGda = obtenirProcediments(entitatDto, progres, totalElementsCons);
			List<OrganGestorEntity> organsGestorsModificats = processarProcediments(entitat, procedimentsGda, progres, avisosProcedimentsOrgans);
//			eliminarOrgansProcObsoletsNoUtilitzats(organsGestorsModificats, progres);
			Long tf = System.currentTimeMillis();
			progres.addInfo(TipusInfo.TEMPS, messageHelper.getMessage("procediment.actualitzacio.auto.temps", new Object[] {(tf - ti)}));
			progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.fi.resultat", new Object[] {procedimentsGda.size(), totalElementsCons}));
			progres.addInfo(TipusInfo.SUBTITOL, messageHelper.getMessage("procediment.actualitzacio.auto.fi", new Object[] {entitatDto.getNom()}));
			for (ActualitzacioInfo inf: progres.getInfo()) {
				if (inf.getText() != null)
					info.getParams().add(new AccioParam("Msg. procés:", inf.getText()));
			}
			progres.setProgres(100);
			progres.setFinished(true);
			ProcedimentServiceImpl.procedimentsAmbOrganNoSincronitzat.put(entitat.getId(), avisosProcedimentsOrgans.size());
			actualitzaAvisosSyncProcediments(avisosProcedimentsOrgans, entitatDto.getId());
			integracioHelper.addAccioOk(info);
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			ProcedimentServiceImpl.progresActualitzacio.get(entitatDto.getDir3Codi()).setError(true);
			ProcedimentServiceImpl.progresActualitzacio.get(entitatDto.getDir3Codi()).setErrorMsg(sw.toString());
			ProcedimentServiceImpl.progresActualitzacio.get(entitatDto.getDir3Codi()).setProgres(100);
			ProcedimentServiceImpl.progresActualitzacio.get(entitatDto.getDir3Codi()).setFinished(true);
			for (ActualitzacioInfo inf: progres.getInfo()) {
				if (inf.getText() != null)
					info.getParams().add(new AccioParam("Msg. procés:", inf.getText()));
			}
			integracioHelper.addAccioError(info, "Error actualitzant procediments: ", e);
			throw e;
		}
	}

	private List<ProcSerDto> obtenirProcediments(EntitatDto entitatDto, ProgresActualitzacioDto progres, int totalElementsCons) {

		List<ProcSerDto> procedimentsGda = new ArrayList<>();
		progres.addInfo(TipusInfo.SUBTITOL, messageHelper.getMessage("procediment.actualitzacio.auto.obtenir.procediments"));
		long startTime = System.nanoTime();

		// Loes operacions a tenir en compte per al percentatge contarà els procediments a processar, més les pàgines a obtenir
		int elementsUltimaPagina = totalElementsCons % 30 == 0 ? 30 : totalElementsCons % 30;
		int pagines = totalElementsCons / 30 + (totalElementsCons % 30 == 0 ? 0 : 1);
//		progres.setNumOperacions(totalElementsCons + pagines);
		double elapsedTime = (System.nanoTime() - startTime) / 10e6;
		log.info(" [PROCEDIMENTS] Obtenir nombre de procediments de l'entitat: " + elapsedTime + " ms");
		startTime = System.nanoTime();
		Long t1 = System.currentTimeMillis();
		// Recuperam tots els procediments del Rolsac
		int numPagina = 1;
		do {
			int reintents = 0;
			do {
				try {
					procedimentsGda.addAll(getProcedimentsGdaByEntitat(entitatDto.getDir3Codi(), numPagina));
					progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.obtenir.procediments.result", new Object[] {procedimentsGda.size()}));
					progres.addInfo(TipusInfo.TEMPS, messageHelper.getMessage("procediment.actualitzacio.auto.temps", new Object[] {(System.currentTimeMillis() - t1)}));
					// Si obté la pàgina posam reintents a 0, de manera que la condició sigui fals, i finalitzi del do-while
					reintents = 0;
				} catch (Exception e) {
					progres.addInfo(TipusInfo.ERROR, messageHelper.getMessage("procediment.actualitzacio.auto.obtenir.procediments.error"));
					reintents++;
				}
			} while (reintents > 0 && reintents < 3);
			// Actualitzam el percentatge. Si no s'ha pogut obtenir la pàgina, eliminan les operacions d'una pàgina i marcam la obtenció de la pàgina com a feta
			int elementsPagina = numPagina == pagines ? elementsUltimaPagina : 30;
			progres.incrementOperacionsRealitzades(elementsPagina);
//			if (reintents > 0) {
//				progres.setNumOperacions(progres.getNumOperacions() - elementsPagina);
//			}
			numPagina++;
		} while (numPagina * 30 < totalElementsCons);

//				// Actualitzem el número d'operacions
//				progres.setNumOperacions(procedimentsGda.size() + pagines);
		elapsedTime = (System.nanoTime() - startTime) / 10e6;
		log.info(" [TIMER-PRO] Obtenció de procediments: " + elapsedTime + " ms");
		return procedimentsGda;
	}

	private List<OrganGestorEntity> processarProcediments(EntitatEntity entitat, List<ProcSerDto> procedimentsGda, ProgresActualitzacioDto progres,
														  Map<String, String[]> avisosProcedimentsOrgans) {

		long startTime = System.nanoTime();
		List<OrganGestorEntity> organsGestorsModificats = new ArrayList<>();

//		TODO: Organigrama de GDA no de BBDD
//		Map<String, OrganismeDto> organigramaEntitat = organGestorCachable.findOrganigramaByEntitat(entitat.getDir3Codi());
		EntitatDto entitatDto = new EntitatDto();
		entitatDto.setCodi(entitat.getCodi());
		List<NodeDir3> unitatsWs = pluginHelper.unitatsOrganitzativesFindByPare(entitatDto, entitat.getDir3Codi(), null, null);
		List<String> codiOrgansGda = new ArrayList<>();
		for (NodeDir3 unitat: unitatsWs) {
			codiOrgansGda.add(unitat.getCodi());
		}
		double elapsedTime = (System.nanoTime() - startTime) / 10e6;
		log.info(" [PROCEDIMENTS] Obtenir organigrama de l'entitat: " + elapsedTime + " ms");
		startTime = System.nanoTime();
		// Processam els procediments obtinguts
		progres.addInfo(TipusInfo.SUBTITOL, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediments", new Object[] {procedimentsGda.size()}));
		boolean modificar = isActualitzacioProcedimentsModificarProperty();
		int i = 1;
		for (ProcSerDto procedimentGda: procedimentsGda) {
			progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment", new Object[] {i, procedimentGda.getNom()}));
			procSerHelper.actualitzarProcedimentFromGda(progres, procedimentGda, entitat, codiOrgansGda, modificar, organsGestorsModificats, avisosProcedimentsOrgans);
			progres.addSeparador();
			progres.incrementOperacionsRealitzades();
			i++;
		}
		elapsedTime = (System.nanoTime() - startTime) / 10e6;
		log.info(" [TIMER-PRO] Recorregut procediments i actualització: " + elapsedTime + " ms");
		return organsGestorsModificats;
	}

	private void eliminarOrgansProcObsoletsNoUtilitzats(List<OrganGestorEntity> organsGestorsModificats, ProgresActualitzacioDto progres) {

		boolean eliminarOrgans = isActualitzacioProcedimentsEliminarOrgansProperty();
		if (!eliminarOrgans) {
			progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.organs.inactiu"));
			return;
		}
		long startTime = System.nanoTime();
		progres.addInfo(TipusInfo.SUBTITOL, messageHelper.getMessage("procediment.actualitzacio.auto.processar.organs"));
		progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.organs.check"));
		if (organsGestorsModificats.isEmpty()) {
			progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.organs.result.no"));
		} else {
			progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.organs.result.si", new Object[] {organsGestorsModificats.size()}));
		}
		for (OrganGestorEntity organGestorAntic: organsGestorsModificats) {
			procSerHelper.eliminarOrganSiNoEstaEnUs(progres,organGestorAntic);
		}
		double elapsedTime = (System.nanoTime() - startTime) / 10e6;
		log.info(" [TIMER-PRO] Eliminar organs: " + elapsedTime + " ms");
	}

	private void actualitzaAvisosSyncProcediments(Map<String, String[]> avisosProcedimentsOrgans, Long entitatId) {

		List<AvisEntity> avisosSinc = avisRepository.findByEntitatIdAndAssumpte(entitatId, ProcedimentServiceImpl.PROCEDIMENT_ORGAN_NO_SYNC);
		if (avisosSinc != null && !avisosSinc.isEmpty()) {
			avisRepository.deleteAll(avisosSinc);
		}
		addAvisosSyncProcediments(avisosProcedimentsOrgans, entitatId);
	}

	public void addAvisosSyncProcediments(Map<String, String[]> avisosProcedimentsOrgans, Long entitatId) {

		if (avisosProcedimentsOrgans.isEmpty()) {
			return;
		}
		String missatgeAvis = "";
		for(Map.Entry<String, String[]> avisProc: avisosProcedimentsOrgans.entrySet()) {
			missatgeAvis += " - Procediment '" + avisProc.getKey() + "': actualment a l'òrgan " + avisProc.getValue()[0] + ", i hauria de pertànyer a l'òrgan " + avisProc.getValue()[1] + " </br>";
		}
		missatgeAvis += "Realitzi una actualizació d'òrgans per a resoldre aquesta situació, o revisi la configuració dels procediments al repositori de procediments";
		Date ara = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(ara);
		calendar.add(Calendar.YEAR, 1);
		AvisEntity avis = AvisEntity.getBuilder(ProcedimentServiceImpl.PROCEDIMENT_ORGAN_NO_SYNC, missatgeAvis, ara, calendar.getTime(), AvisNivellEnumDto.ERROR, true, entitatId).build();
		avisRepository.save(avis);
	}

	private int getTotalProcediments(String codiDir3) {

		log.debug(">>>> >> Obtenir total procediments Rolsac...");
		Long t1 = System.currentTimeMillis();
		int totalElements = pluginHelper.getTotalProcediments(codiDir3);
		Long t2 = System.currentTimeMillis();
		log.debug(">>>> >> resultat"  + totalElements + " procediments (" + (t2 - t1) + "ms)");
		return totalElements;
	}

	private List<ProcSerDto> getProcedimentsGdaByEntitat(String codiDir3, int numPagina) {

		var progres = ProcedimentServiceImpl.progresActualitzacio.get(codiDir3);
		log.debug(">>>> >> Obtenir tots els procediments de Rolsac...");
		progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.consulta.gesconadm"));
		var t1 = System.currentTimeMillis();
		List<ProcSerDto> procedimentsEntitat = new ArrayList<>();
		for (var i=0;i<3;i++) {
			try {
				procedimentsEntitat = pluginHelper.getProcedimentsGdaByEntitat(codiDir3, numPagina);
				break;
			} catch (Exception ex) {
				if (i == 2) {
					throw ex;
				}
			}
		}
		var t2 = System.currentTimeMillis();
		log.debug(">>>> >> obtinguts" + procedimentsEntitat.size() + " procediments (" + (t2 - t1) + "ms)");
		progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.consulta.gesconadm.result", new Object[] {procedimentsEntitat.size()}));
		progres.addInfo(TipusInfo.TEMPS, messageHelper.getMessage("procediment.actualitzacio.auto.temps", new Object[] {(t2 - t1)}));
		return procedimentsEntitat;
	}

	private boolean isActualitzacioProcedimentsModificarProperty() {

		try {
			return configHelper.getConfigAsBoolean("es.caib.notib.actualitzacio.procediments.modificar");
		} catch (Exception e) {
			return true;
		}
	}

	public boolean isActualitzacioProcedimentsEliminarOrgansProperty() {

		try {
			return configHelper.getConfigAsBoolean("es.caib.notib.actualitzacio.procediments.eliminar.organs");
		} catch (Exception e) {
			return false;
		}
	}


	// Sincronitzar procediments
	// ///////////////////////////////////////////////////////////////////////////
	public void actualitzaServeis(EntitatDto entitatDto) {

		IntegracioInfo info = new IntegracioInfo(IntegracioHelper.INTCODI_PROCEDIMENT, "Actualització de serveis", IntegracioAccioTipusEnumDto.PROCESSAR, new AccioParam("Codi Dir3 de l'entitat", entitatDto.getDir3Codi()));
		info.setCodiEntitat(entitatDto.getCodi());
		ConfigHelper.setEntitat(entitatDto);
		log.debug("[SERVEIS] Inici actualitzar serveis");
		// Comprova si hi ha una altre instància del procés en execució
		ProgresActualitzacioDto progres = ServeiServiceImpl.progresActualitzacioServeis.get(entitatDto.getDir3Codi());
		if (progres != null && (progres.getProgres() > 0 && progres.getProgres() < 100) && !progres.isError()) {
			log.debug("[SERVEIS] Ja existeix un altre procés que està executant l'actualització");
			return;	// Ja existeix un altre procés que està executant l'actualització.
		}
		// inicialitza el seguiment del prgrés d'actualització
		progres = new ProgresActualitzacioDto();
		ServeiServiceImpl.progresActualitzacioServeis.put(entitatDto.getDir3Codi(), progres);
		Map<String, String[]> avisosServeisOrgans = new HashMap<>();
		try {
			Long ti = System.currentTimeMillis();
			progres.addInfo(TipusInfo.TITOL, messageHelper.getMessage("servei.actualitzacio.auto.inici", new Object[] {entitatDto.getNom()}));
			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatDto.getId(), false, false, false);
			int totalElementsCons = getTotalServeis(entitatDto.getDir3Codi());	// Procediments a processar
			List<ProcSerDto> procedimentsGda = obtenirServeis(entitatDto, progres, totalElementsCons);
			List<OrganGestorEntity> organsGestorsModificats = processarServeis(entitat, procedimentsGda, progres, avisosServeisOrgans);
//			eliminarOrgansServObsoletsNoUtilitzats(organsGestorsModificats, progres);
			Long tf = System.currentTimeMillis();
			progres.addInfo(TipusInfo.TEMPS, messageHelper.getMessage("servei.actualitzacio.auto.temps", new Object[] {(tf - ti)}));
			progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("servei.actualitzacio.auto.fi.resultat", new Object[] {progres.getNumOperacionsRealitzades(), totalElementsCons}));
			progres.addInfo(TipusInfo.SUBTITOL, messageHelper.getMessage("servei.actualitzacio.auto.fi", new Object[] {entitatDto.getNom()}));
			for (ActualitzacioInfo inf: progres.getInfo()) {
				if (inf.getText() != null)
					info.getParams().add(new AccioParam("Msg. procés:", inf.getText()));
			}
			progres.setProgres(100);
			progres.setFinished(true);
			ServeiServiceImpl.serveisAmbOrganNoSincronitzat.put(entitat.getId(), avisosServeisOrgans.size());
			actualitzaAvisosSyncServeis(avisosServeisOrgans, entitatDto.getId());
			integracioHelper.addAccioOk(info);
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			ServeiServiceImpl.progresActualitzacioServeis.get(entitatDto.getDir3Codi()).setError(true);
			ServeiServiceImpl.progresActualitzacioServeis.get(entitatDto.getDir3Codi()).setErrorMsg(sw.toString());
			ServeiServiceImpl.progresActualitzacioServeis.get(entitatDto.getDir3Codi()).setProgres(100);
			ServeiServiceImpl.progresActualitzacioServeis.get(entitatDto.getDir3Codi()).setFinished(true);
			for (ActualitzacioInfo inf: progres.getInfo()) {
				if (inf.getText() != null)
					info.getParams().add(new AccioParam("Msg. procés:", inf.getText()));
			}
			integracioHelper.addAccioError(info, "Error actualitzant serveis: ", e);
			throw e;
		}
	}

	private List<ProcSerDto> obtenirServeis(EntitatDto entitatDto, ProgresActualitzacioDto progres, int totalElementsCons) {

		List<ProcSerDto> serveisGda  = new ArrayList<>();
		progres.addInfo(TipusInfo.SUBTITOL, messageHelper.getMessage("servei.actualitzacio.auto.obtenir.serveis"));
		long startTime = System.nanoTime();
		// Loes operacions a tenir en compte per al percentatge contarà els procediments a processar, més les pàgines a obtenir
		int elementsUltimaPagina = totalElementsCons % 30 == 0 ? 30 : totalElementsCons % 30;
		int pagines = totalElementsCons / 30 + (totalElementsCons % 30 == 0 ? 0 : 1);
		progres.setNumOperacions(totalElementsCons + pagines);
		double elapsedTime = (System.nanoTime() - startTime) / 10e6;
		log.info(" [SERVEIS] Obtenir nombre de serveis de l'entitat: " + elapsedTime + " ms");
		startTime = System.nanoTime();
		Long t1 = System.currentTimeMillis();
		// Recuperam tots els procediments del Rolsac
		int numPagina = 1;
		do {
			int reintents = 0;
			do {
				try {
					serveisGda.addAll(getServeisGdaByEntitat(entitatDto.getDir3Codi(), numPagina));
					progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("servei.actualitzacio.auto.obtenir.serveis.result", new Object[] {serveisGda.size()}));
					progres.addInfo(TipusInfo.TEMPS, messageHelper.getMessage("servei.actualitzacio.auto.temps", new Object[] {(System.currentTimeMillis() - t1)}));
					// Si obté la pàgina posam reintents a 0, de manera que la condició sigui fals, i finalitzi del do-while
					reintents = 0;
				} catch (Exception e) {
					progres.addInfo(TipusInfo.ERROR, messageHelper.getMessage("servei.actualitzacio.auto.obtenir.serveis.error"));
					reintents++;
				}
			} while (reintents > 0 && reintents < 3);
			// Actualitzam el percentatge. Si no s'ha pogut obtenir la pàgina, eliminan les operacions d'una pàgina i marcam la obtenció de la pàgina com a feta
			int elementsPagina = numPagina == pagines ? elementsUltimaPagina : 30;
			progres.incrementOperacionsRealitzades(elementsPagina);
//			if (reintents > 0) {
//				progres.setNumOperacions(progres.getNumOperacions() - elementsPagina);
//			}
			numPagina++;
		} while (numPagina * 30 < totalElementsCons);
		elapsedTime = (System.nanoTime() - startTime) / 10e6;
		log.info(" [TIMER-PRO] Recorregut procediments i actualització: " + elapsedTime + " ms");
		return serveisGda;
	}

	private List<OrganGestorEntity> processarServeis(EntitatEntity entitat, List<ProcSerDto> serveisGda, ProgresActualitzacioDto progres, Map<String, String[]> avisosServeisOrgans) {

		long startTime = System.nanoTime();
		List<OrganGestorEntity> organsGestorsModificats = new ArrayList<>();
//		Map<String, OrganismeDto> organigramaEntitat = organGestorCachable.findOrganigramaByEntitat(entitat.getDir3Codi());
		EntitatDto entitatDto = new EntitatDto();
		entitatDto.setCodi(entitat.getCodi());
		List<NodeDir3> unitatsWs = pluginHelper.unitatsOrganitzativesFindByPare(entitatDto, entitat.getDir3Codi(), null, null);
		List<String> codiOrgansGda = new ArrayList<>();
		for (NodeDir3 unitat: unitatsWs) {
			codiOrgansGda.add(unitat.getCodi());
		}
		double elapsedTime = (System.nanoTime() - startTime) / 10e6;
		log.info(" [SERVEIS] Obtenir organigrama de l'entitat: " + elapsedTime + " ms");
		startTime = System.nanoTime();
		// Processam els serveis obtinguts
		progres.addInfo(TipusInfo.SUBTITOL, messageHelper.getMessage("servei.actualitzacio.auto.processar.serveis", new Object[] {serveisGda.size()}));
		boolean modificar = isActualitzacioServeisModificarProperty();
		int i = 1;
		for (ProcSerDto serveiGda: serveisGda) {
			progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("servei.actualitzacio.auto.processar.servei", new Object[] {i, serveiGda.getNom()}));
			procSerHelper.actualitzarServeiFromGda(progres, serveiGda, entitat, codiOrgansGda, modificar, organsGestorsModificats, avisosServeisOrgans);
			progres.addSeparador();
			progres.incrementOperacionsRealitzades();
			i++;
		}
		elapsedTime = (System.nanoTime() - startTime) / 10e6;
		log.info(" [TIMER-SER] Recorregut serveis i actualització: " + elapsedTime + " ms");
		return organsGestorsModificats;
	}

	private void eliminarOrgansServObsoletsNoUtilitzats(List<OrganGestorEntity> organsGestorsModificats, ProgresActualitzacioDto progres) {

		boolean eliminarOrgans = isActualitzacioServeisEliminarOrgansProperty();
		if (!eliminarOrgans) {
			progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("servei.actualitzacio.auto.processar.organs.inactiu"));
			return;
		}
		long startTime = System.nanoTime();
		progres.addInfo(TipusInfo.SUBTITOL, messageHelper.getMessage("servei.actualitzacio.auto.processar.organs"));
		progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("servei.actualitzacio.auto.processar.organs.check"));
		if (organsGestorsModificats.isEmpty()) {
			progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("servei.actualitzacio.auto.processar.organs.result.no"));
		} else {
			progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("servei.actualitzacio.auto.processar.organs.result.si", new Object[] {organsGestorsModificats.size()}));
		}

		for (OrganGestorEntity organGestorAntic: organsGestorsModificats) {
			procSerHelper.eliminarOrganSiNoEstaEnUs(progres, organGestorAntic);
		}
		double elapsedTime = (System.nanoTime() - startTime) / 10e6;
		log.info(" [TIMER-SER] Eliminar organs: " + elapsedTime + " ms");
	}

	private void actualitzaAvisosSyncServeis(Map<String, String[]> avisosProcedimentsOrgans, Long entitatId) {

		List<AvisEntity> avisosSinc = avisRepository.findByEntitatIdAndAssumpte(entitatId, ServeiServiceImpl.SERVEI_ORGAN_NO_SYNC);
		if (avisosSinc != null && !avisosSinc.isEmpty()) {
			avisRepository.deleteAll(avisosSinc);
		}
		addAvisosSyncServeis(avisosProcedimentsOrgans, entitatId);
	}

	public void addAvisosSyncServeis(Map<String, String[]> avisosProcedimentsOrgans, Long entitatId) {

		if (avisosProcedimentsOrgans.isEmpty()) {
			return;
		}
		String missatgeAvis = "";
		for(Map.Entry<String, String[]> avisProc: avisosProcedimentsOrgans.entrySet()) {
			missatgeAvis += " - Servei '" + avisProc.getKey() + "': actualment a l'òrgan " + avisProc.getValue()[0] + ", i hauria de pertànyer a l'òrgan " + avisProc.getValue()[1] + " </br>";
		}
		missatgeAvis += "Realitzi una actualizació d'òrgans per a resoldre aquesta situació, o revisi la configuració dels procediments al repositori de procediments";
		Date ara = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(ara);
		calendar.add(Calendar.YEAR, 1);
		AvisEntity avis = AvisEntity.getBuilder(ServeiServiceImpl.SERVEI_ORGAN_NO_SYNC, missatgeAvis, ara, calendar.getTime(), AvisNivellEnumDto.ERROR, true, entitatId).build();
		avisRepository.save(avis);
	}

	private int getTotalServeis(String codiDir3) {

		log.debug(">>>> >> Obtenir total serveis Rolsac...");
		Long t1 = System.currentTimeMillis();
		int totalElements = pluginHelper.getTotalServeis(codiDir3);
		Long t2 = System.currentTimeMillis();
		log.debug(">>>> >> resultat"  + totalElements + " serveis (" + (t2 - t1) + "ms)");
		return totalElements;
	}

	private List<ProcSerDto> getServeisGdaByEntitat(String codiDir3, int numPagina) {

		var progres = ServeiServiceImpl.progresActualitzacioServeis.get(codiDir3);
		log.debug(">>>> >> Obtenir tots els serveis de Rolsac...");
		progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("servei.actualitzacio.auto.consulta.gesconadm"));
		var t1 = System.currentTimeMillis();
		List<ProcSerDto> serveisEntitat = new ArrayList<>();
		for (int i=0;i<3;i++) {
			try {
				serveisEntitat = pluginHelper.getServeisGdaByEntitat(codiDir3, numPagina);
				break;
			} catch (Exception ex) {
				if (i == 2) {
					throw ex;
				}
			}
		}
		var t2 = System.currentTimeMillis();
		log.debug(">>>> >> obtinguts" + serveisEntitat.size() + " serveis (" + (t2 - t1) + "ms)");
		progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("servei.actualitzacio.auto.consulta.gesconadm.result", new Object[] {serveisEntitat.size()}));
		progres.addInfo(TipusInfo.TEMPS, messageHelper.getMessage("servei.actualitzacio.auto.temps", new Object[] {(t2 - t1)}));
		return serveisEntitat;
	}

	private boolean isActualitzacioServeisModificarProperty() {
		return configHelper.getConfigAsBoolean("es.caib.notib.actualitzacio.procediments.modificar");
	}

	public boolean isActualitzacioServeisEliminarOrgansProperty() {
		return configHelper.getConfigAsBoolean("es.caib.notib.actualitzacio.procediments.eliminar.organs");
	}
}
