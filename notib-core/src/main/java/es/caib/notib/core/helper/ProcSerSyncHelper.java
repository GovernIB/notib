package es.caib.notib.core.helper;

import es.caib.notib.core.api.dto.AccioParam;
import es.caib.notib.core.api.dto.AvisNivellEnumDto;
import es.caib.notib.core.api.dto.EntitatDto;
import es.caib.notib.core.api.dto.IntegracioAccioTipusEnumDto;
import es.caib.notib.core.api.dto.IntegracioInfo;
import es.caib.notib.core.api.dto.ProgresActualitzacioDto;
import es.caib.notib.core.api.dto.ProgresActualitzacioDto.ActualitzacioInfo;
import es.caib.notib.core.api.dto.ProgresActualitzacioDto.TipusInfo;
import es.caib.notib.core.api.dto.procediment.ProcSerDataDto;
import es.caib.notib.core.api.dto.procediment.ProcSerDto;
import es.caib.notib.core.api.dto.procediment.ProgresActualitzacioProcSer;
import es.caib.notib.core.cacheable.OrganGestorCachable;
import es.caib.notib.core.entity.AvisEntity;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.OrganGestorEntity;
import es.caib.notib.core.repository.AvisRepository;
import es.caib.notib.core.repository.ProcedimentRepository;
import es.caib.notib.core.repository.ServeiRepository;
import es.caib.notib.core.service.ProcedimentServiceImpl;
import es.caib.notib.core.service.ServeiServiceImpl;
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
	private ProcedimentRepository procedimentRepository;
	@Autowired
	private ServeiRepository serveiRepository;
	@Autowired
	private PluginHelper pluginHelper;
	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Resource
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
		ProgresActualitzacioProcSer progres = ProcedimentServiceImpl.progresActualitzacio.get(entitatDto.getDir3Codi());
		if (progres != null && (progres.getProgres() > 0 && progres.getProgres() < 100) && !progres.isError()) {
			log.debug("[PROCEDIMENTS] Ja existeix un altre procés que està executant l'actualització");
			return;	// Ja existeix un altre procés que està executant l'actualització.
		}

		// inicialitza el seguiment del prgrés d'actualització
		progres = new ProgresActualitzacioProcSer();
		ProcedimentServiceImpl.progresActualitzacio.put(entitatDto.getDir3Codi(), progres);
		Map<String, String[]> avisosProcedimentsOrgans = new HashMap<>();

		try {

			Long ti = System.currentTimeMillis();
			progres.addInfo(TipusInfo.TITOL, messageHelper.getMessage("procediment.actualitzacio.auto.inici", new Object[] {entitatDto.getNom()}));

			progres.setTotalInicial(procedimentRepository.countByEntitatId(entitatDto.getId()));
			progres.setActiusInicial(procedimentRepository.countByEntitatIdAndActiuTrue(entitatDto.getId()));
			progres.setInactiusInicial(procedimentRepository.countByEntitatIdAndActiuFalse(entitatDto.getId()));

			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatDto.getId(), false, false, false);
			List<ProcSerDto> procedimentsGda = obtenirProcediments(entitatDto, progres);
			if (procedimentsGda.isEmpty()) {
				progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediments.actualitzacio.error.rolsac"));
				progres.setProgres(100);
				progres.setFinished(true);
				return;
			}
//			int totalElementsCons = getTotalProcediments(entitatDto.getDir3Codi());	// Procediments a processar
			int totalElementsCons = procedimentsGda.size();	// Procediments a processar
			progres.setNumOperacions((totalElementsCons * 2) + Math.max(1, totalElementsCons/50));

//			List<ProcSerDto> procedimentsGda = obtenirProcediments(entitatDto, progres, totalElementsCons);
			progres.setProcedimentsObtinguts(procedimentsGda);
			processarProcediments(entitat, procedimentsGda, progres, avisosProcedimentsOrgans);
			procSerHelper.deshabilitarProcedimentsNoActius(procedimentsGda, entitat.getCodi(), progres);
//			eliminarOrgansProcObsoletsNoUtilitzats(organsGestorsModificats, progres);

			progres.setTotalFinal(procedimentRepository.countByEntitatId(entitatDto.getId()));
			progres.setActiusFinal(procedimentRepository.countByEntitatIdAndActiuTrue(entitatDto.getId()));
			progres.setInactiusFinal(procedimentRepository.countByEntitatIdAndActiuFalse(entitatDto.getId()));

			Long tf = System.currentTimeMillis();
			progres.addInfo(TipusInfo.TEMPS, messageHelper.getMessage("procediment.actualitzacio.auto.temps", new Object[] {(tf - ti)}));
			progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.fi.resultat", new Object[] {procedimentsGda.size(), totalElementsCons}));
			crearResum(progres);
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

	private void crearResum(ProgresActualitzacioProcSer progres) {

		progres.addInfo(TipusInfo.TITOL, messageHelper.getMessage("procediment.actualitzacio.resum"));
		progres.addInfo(TipusInfo.SUBTITOL, messageHelper.getMessage("procediment.actualitzacio.procser.sense.codi.sia"));
		List<ProcSerDataDto> procs = progres.getSenseCodiSia();
		for (ProcSerDataDto proc : procs) {
			progres.addInfo(TipusInfo.INFO, proc.getNom());
		}

		progres.addInfo(TipusInfo.SUBTITOL, messageHelper.getMessage("procediment.actualitzacio.procser.organ.no.pertany.entitat"));
		procs = progres.getOrganNoPertanyEntitat();
		for (ProcSerDataDto proc : procs) {
			progres.addInfo(TipusInfo.INFO, proc.getCodi() + " - " + proc.getNom() + " - " + proc.getOrganGestor());
		}

		progres.addInfo(TipusInfo.SUBTITOL, messageHelper.getMessage("procediment.actualitzacio.metriques"));
		progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediments.total.inicial") + " " + progres.getTotalInicial());
		progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediments.actius") + " " + progres.getActiusInicial());
		progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediments.inactius") + " " + progres.getInactiusInicial());
		progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediments.obtinguts") + " " + progres.getProcedimentsObtinguts().size());
		progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.procser.sense.codi.sia") + " " + progres.getSenseCodiSia().size());
		progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.procser.organ.no.pertany.entitat") + " " + progres.getOrganNoPertanyEntitat().size());
		int activats = progres.getProcedimentsObtinguts().size() - progres.getSenseCodiSia().size() - progres.getOrganNoPertanyEntitat().size();
		progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediments.actualitzacio.activats") + " " + activats);
		progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediments.actualitzacio.desactivats.no.provinents.rolsac") +  " " + progres.getNoActius().size());
		progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediments.total.final") + " " + progres.getTotalFinal());
		progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediments.actius") + " " + progres.getActiusFinal());
		progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediments.inactius") + " " + progres.getInactiusFinal());
	}

	private List<ProcSerDto> obtenirProcediments(EntitatDto entitatDto, ProgresActualitzacioDto progres) {
		List<ProcSerDto> procedimentsGda = new ArrayList<>();
		progres.addInfo(TipusInfo.SUBTITOL, messageHelper.getMessage("procediment.actualitzacio.auto.obtenir.procediments"));
		long startTime = System.nanoTime();

		double elapsedTime = (System.nanoTime() - startTime) / 10e6;
		log.info(" [PROCEDIMENTS] Obtenir nombre de procediments de l'entitat: " + elapsedTime + " ms");
		startTime = System.nanoTime();
		Long t1 = System.currentTimeMillis();

		// Recuperam tots els procediments del Rolsac
		int reintents = 0;
		do {
			try {
				procedimentsGda = getProcedimentsGdaByEntitat(entitatDto.getDir3Codi());
				progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.obtenir.procediments.result", new Object[] {procedimentsGda.size()}));
				progres.addInfo(TipusInfo.TEMPS, messageHelper.getMessage("procediment.actualitzacio.auto.temps", new Object[] {(System.currentTimeMillis() - t1)}));
				// Si obté la pàgina posam reintents a 0, de manera que la condició sigui fals, i finalitzi del do-while
				reintents = 0;
			} catch (Exception e) {
				reintents++;
			}
		} while (reintents > 0 && reintents < 3);
		// Actualitzam el percentatge. Si no s'ha pogut obtenir la pàgina, eliminan les operacions d'una pàgina i marcam la obtenció de la pàgina com a feta
		progres.incrementOperacionsRealitzades(procedimentsGda.size());
		if (reintents > 0) {
			progres.addInfo(TipusInfo.ERROR, messageHelper.getMessage("procediment.actualitzacio.auto.obtenir.procediments.error"));
		}

		elapsedTime = (System.nanoTime() - startTime) / 10e6;
		log.info(" [TIMER-PRO] Obtenció de procediments: " + elapsedTime + " ms");

		return procedimentsGda;
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
					reintents++;
				}
			} while (reintents > 0 && reintents < 3);
			// Actualitzam el percentatge. Si no s'ha pogut obtenir la pàgina, eliminan les operacions d'una pàgina i marcam la obtenció de la pàgina com a feta
			int elementsPagina = numPagina == pagines ? elementsUltimaPagina : 30;
			progres.incrementOperacionsRealitzades(elementsPagina);
			if (reintents > 0) {
				progres.addInfo(TipusInfo.ERROR, messageHelper.getMessage("procediment.actualitzacio.auto.obtenir.procediments.error"));
//				progres.setNumOperacions(progres.getNumOperacions() - elementsPagina);
			}
			numPagina++;
		} while ((numPagina - 1) * 30 < totalElementsCons);

//				// Actualitzem el número d'operacions
//				progres.setNumOperacions(procedimentsGda.size() + pagines);

		elapsedTime = (System.nanoTime() - startTime) / 10e6;
		log.info(" [TIMER-PRO] Obtenció de procediments: " + elapsedTime + " ms");

		return procedimentsGda;
	}

	private List<OrganGestorEntity> processarProcediments(EntitatEntity entitat, List<ProcSerDto> procedimentsGda, ProgresActualitzacioProcSer progres, Map<String, String[]> avisosProcedimentsOrgans) {

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
//		System.out.println("SYNCPROC >>> modificar = " + modificar);

		int i = 1;
		for (ProcSerDto procedimentGda: procedimentsGda) {
			progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.procediment", new Object[] {i, procedimentGda.getNom()}));
			procSerHelper.actualitzarProcedimentFromGda(
					progres,
					procedimentGda,
					entitat,
					codiOrgansGda,
					modificar,
					organsGestorsModificats,
					avisosProcedimentsOrgans);
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
		if (eliminarOrgans) {
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
		} else {
			progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.processar.organs.inactiu"));
		}
	}

	private void actualitzaAvisosSyncProcediments(Map<String, String[]> avisosProcedimentsOrgans, Long entitatId) {
		List<AvisEntity> avisosSinc = avisRepository.findByEntitatIdAndAssumpte(entitatId, ProcedimentServiceImpl.PROCEDIMENT_ORGAN_NO_SYNC);
		if (avisosSinc != null && !avisosSinc.isEmpty()) {
			avisRepository.delete(avisosSinc);
		}
		addAvisosSyncProcediments(avisosProcedimentsOrgans, entitatId);
	}

	public void addAvisosSyncProcediments(Map<String, String[]> avisosProcedimentsOrgans, Long entitatId) {
		if (!avisosProcedimentsOrgans.isEmpty()) {
			String missatgeAvis = "";
			for(Map.Entry<String, String[]> avisProc: avisosProcedimentsOrgans.entrySet()) {
				missatgeAvis += " - Procediment '" + avisProc.getKey() + "': actualment a l'òrgan " + avisProc.getValue()[0] + ", i hauria de pertànyer a l'òrgan " + avisProc.getValue()[1] + " </br>";
			}
			missatgeAvis += "Realitzi una actualizació d'òrgans per a resoldre aquesta situació, o revisi la configuració dels procediments al repositori de procediments";
			Date ara = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(ara);
			calendar.add(Calendar.YEAR, 1);
			AvisEntity avis = AvisEntity.getBuilder(
					ProcedimentServiceImpl.PROCEDIMENT_ORGAN_NO_SYNC,
					missatgeAvis,
					ara,
					calendar.getTime(),
					AvisNivellEnumDto.ERROR,
					true,
					entitatId).build();
			avisRepository.save(avis);
		}
	}

	private int getTotalProcediments(String codiDir3) {
		log.debug(">>>> >> Obtenir total procediments Rolsac...");
		Long t1 = System.currentTimeMillis();
		int totalElements = pluginHelper.getTotalProcediments(codiDir3);
		Long t2 = System.currentTimeMillis();
		log.debug(">>>> >> resultat"  + totalElements + " procediments (" + (t2 - t1) + "ms)");
		return totalElements;
	}

	private List<ProcSerDto> getProcedimentsGdaByEntitat(String codiDir3) {

		ProgresActualitzacioDto progres = ProcedimentServiceImpl.progresActualitzacio.get(codiDir3);
		log.debug(">>>> >> Obtenir tots els procediments de Rolsac...");
		progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.consulta.gesconadm"));
		Long t1 = System.currentTimeMillis();
		List<ProcSerDto> procedimentsEntitat = pluginHelper.getProcedimentsGdaByEntitat(codiDir3);
		Long t2 = System.currentTimeMillis();
		log.debug(">>>> >> obtinguts" + procedimentsEntitat.size() + " procediments (" + (t2 - t1) + "ms)");
		progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.consulta.gesconadm.result", new Object[] {procedimentsEntitat.size()}));
		progres.addInfo(TipusInfo.TEMPS, messageHelper.getMessage("procediment.actualitzacio.auto.temps", new Object[] {(t2 - t1)}));
		return procedimentsEntitat;
	}

	private List<ProcSerDto> getProcedimentsGdaByEntitat(String codiDir3, int numPagina) {

		ProgresActualitzacioDto progres = ProcedimentServiceImpl.progresActualitzacio.get(codiDir3);
		log.debug(">>>> >> Obtenir tots els procediments de Rolsac...");
		progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.consulta.gesconadm"));
		Long t1 = System.currentTimeMillis();
		List<ProcSerDto> procedimentsEntitat = pluginHelper.getProcedimentsGdaByEntitat(codiDir3, numPagina);
		Long t2 = System.currentTimeMillis();
		log.debug(">>>> >> obtinguts" + procedimentsEntitat.size() + " procediments (" + (t2 - t1) + "ms)");
		progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("procediment.actualitzacio.auto.consulta.gesconadm.result", new Object[] {procedimentsEntitat.size()}));
		progres.addInfo(TipusInfo.TEMPS, messageHelper.getMessage("procediment.actualitzacio.auto.temps", new Object[] {(t2 - t1)}));
		return procedimentsEntitat;
	}

	private boolean isActualitzacioProcedimentsModificarProperty() {
		try {
			return configHelper.getAsBoolean("es.caib.notib.actualitzacio.procediments.modificar");
		} catch (Exception e) {
			return true;
		}
	}

	public boolean isActualitzacioProcedimentsEliminarOrgansProperty() {
		try {
			return configHelper.getAsBoolean("es.caib.notib.actualitzacio.procediments.eliminar.organs");
		} catch (Exception e) {
			return false;
		}
	}


	// Sincronitzar serveis
	// ///////////////////////////////////////////////////////////////////////////
	public void actualitzaServeis(EntitatDto entitatDto) {

		IntegracioInfo info = new IntegracioInfo(IntegracioHelper.INTCODI_PROCEDIMENT, "Actualització de serveis", IntegracioAccioTipusEnumDto.PROCESSAR, new AccioParam("Codi Dir3 de l'entitat", entitatDto.getDir3Codi()));
		info.setCodiEntitat(entitatDto.getCodi());
		ConfigHelper.setEntitat(entitatDto);
		log.debug("[SERVEIS] Inici actualitzar serveis");

		// Comprova si hi ha una altre instància del procés en execució
		ProgresActualitzacioProcSer progres = ServeiServiceImpl.progresActualitzacioServeis.get(entitatDto.getDir3Codi());
		if (progres != null && (progres.getProgres() > 0 && progres.getProgres() < 100) && !progres.isError()) {
			log.debug("[SERVEIS] Ja existeix un altre procés que està executant l'actualització");
			return;	// Ja existeix un altre procés que està executant l'actualització.
		}

		// inicialitza el seguiment del prgrés d'actualització
		progres = new ProgresActualitzacioProcSer();
		ServeiServiceImpl.progresActualitzacioServeis.put(entitatDto.getDir3Codi(), progres);
		Map<String, String[]> avisosServeisOrgans = new HashMap<>();

		try {

			Long ti = System.currentTimeMillis();
			progres.addInfo(TipusInfo.TITOL, messageHelper.getMessage("servei.actualitzacio.auto.inici", new Object[] {entitatDto.getNom()}));

			progres.setTotalInicial(serveiRepository.countByEntitatId(entitatDto.getId()));
			progres.setActiusInicial(serveiRepository.countByEntitatIdAndActiuTrue(entitatDto.getId()));
			progres.setInactiusInicial(serveiRepository.countByEntitatIdAndActiuFalse(entitatDto.getId()));

			EntitatEntity entitat = entityComprovarHelper.comprovarEntitat(entitatDto.getId(), false, false, false);
			List<ProcSerDto> procedimentsGda = obtenirServeis(entitatDto, progres);
			if (procedimentsGda.isEmpty()) {
				progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("serveis.actualitzacio.error.rolsac"));
				progres.setProgres(100);
				progres.setFinished(true);
				return;
			}
			int totalElementsCons = procedimentsGda.size();	// Procediments a processar
//			int totalElementsCons = getTotalServeis(entitatDto.getDir3Codi());	// Procediments a processar
			progres.setNumOperacions((totalElementsCons * 2) + Math.max(1, totalElementsCons/50));


//			List<ProcSerDto> procedimentsGda = obtenirServeis(entitatDto, progres, totalElementsCons);
			progres.setProcedimentsObtinguts(procedimentsGda);
			List<OrganGestorEntity> organsGestorsModificats = processarServeis(entitat, procedimentsGda, progres, avisosServeisOrgans);
			procSerHelper.deshabilitarServeisNoActius(procedimentsGda, entitat.getCodi(), progres);
//			eliminarOrgansServObsoletsNoUtilitzats(organsGestorsModificats, progres);

			progres.setTotalFinal(serveiRepository.countByEntitatId(entitatDto.getId()));
			progres.setActiusFinal(serveiRepository.countByEntitatIdAndActiuTrue(entitatDto.getId()));
			progres.setInactiusFinal(serveiRepository.countByEntitatIdAndActiuFalse(entitatDto.getId()));

			Long tf = System.currentTimeMillis();
			progres.addInfo(TipusInfo.TEMPS, messageHelper.getMessage("servei.actualitzacio.auto.temps", new Object[] {(tf - ti)}));
			progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("servei.actualitzacio.auto.fi.resultat", new Object[] {procedimentsGda.size(), totalElementsCons}));
			crearResum(progres);
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

	private List<ProcSerDto> obtenirServeis(EntitatDto entitatDto, ProgresActualitzacioDto progres) {
		List<ProcSerDto> serveisGda  = new ArrayList<>();
		progres.addInfo(TipusInfo.SUBTITOL, messageHelper.getMessage("servei.actualitzacio.auto.obtenir.serveis"));
		long startTime = System.nanoTime();

		double elapsedTime = (System.nanoTime() - startTime) / 10e6;
		log.info(" [SERVEIS] Obtenir nombre de serveis de l'entitat: " + elapsedTime + " ms");
		startTime = System.nanoTime();
		Long t1 = System.currentTimeMillis();

		// Recuperam tots els procediments del Rolsac
		int reintents = 0;
		do {
			try {
				serveisGda = getServeisGdaByEntitat(entitatDto.getDir3Codi());
				progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("servei.actualitzacio.auto.obtenir.serveis.result", new Object[] {serveisGda.size()}));
				progres.addInfo(TipusInfo.TEMPS, messageHelper.getMessage("servei.actualitzacio.auto.temps", new Object[] {(System.currentTimeMillis() - t1)}));
				// Si obté la pàgina posam reintents a 0, de manera que la condició sigui fals, i finalitzi del do-while
				reintents = 0;
			} catch (Exception e) {
				reintents++;
			}
		} while (reintents > 0 && reintents < 3);
		// Actualitzam el percentatge. Si no s'ha pogut obtenir la pàgina, eliminan les operacions d'una pàgina i marcam la obtenció de la pàgina com a feta
		progres.incrementOperacionsRealitzades(serveisGda.size());
		if (reintents > 0) {
			progres.addInfo(TipusInfo.ERROR, messageHelper.getMessage("servei.actualitzacio.auto.obtenir.serveis.error"));
		}

		elapsedTime = (System.nanoTime() - startTime) / 10e6;
		log.info(" [TIMER-PRO] Recorregut procediments i actualització: " + elapsedTime + " ms");

		return serveisGda;
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
					reintents++;
				}
			} while (reintents > 0 && reintents < 3);
			// Actualitzam el percentatge. Si no s'ha pogut obtenir la pàgina, eliminan les operacions d'una pàgina i marcam la obtenció de la pàgina com a feta
			int elementsPagina = numPagina == pagines ? elementsUltimaPagina : 30;
			progres.incrementOperacionsRealitzades(elementsPagina);
			if (reintents > 0) {
				progres.addInfo(TipusInfo.ERROR, messageHelper.getMessage("servei.actualitzacio.auto.obtenir.serveis.error"));
//				progres.setNumOperacions(progres.getNumOperacions() - elementsPagina);
			}
			numPagina++;
		} while ((numPagina - 1) * 30 < totalElementsCons);

		elapsedTime = (System.nanoTime() - startTime) / 10e6;
		log.info(" [TIMER-PRO] Recorregut procediments i actualització: " + elapsedTime + " ms");

		return serveisGda;
	}

	private List<OrganGestorEntity> processarServeis(EntitatEntity entitat, List<ProcSerDto> serveisGda, ProgresActualitzacioProcSer progres, Map<String, String[]> avisosServeisOrgans) {

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
			procSerHelper.actualitzarServeiFromGda(
					progres,
					serveiGda,
					entitat,
					codiOrgansGda,
					modificar,
					organsGestorsModificats,
					avisosServeisOrgans);
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
		if (eliminarOrgans) {
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
		} else {
			progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("servei.actualitzacio.auto.processar.organs.inactiu"));
		}
	}

	private void actualitzaAvisosSyncServeis(Map<String, String[]> avisosProcedimentsOrgans, Long entitatId) {
		List<AvisEntity> avisosSinc = avisRepository.findByEntitatIdAndAssumpte(entitatId, ServeiServiceImpl.SERVEI_ORGAN_NO_SYNC);
		if (avisosSinc != null && !avisosSinc.isEmpty()) {
			avisRepository.delete(avisosSinc);
		}
		addAvisosSyncServeis(avisosProcedimentsOrgans, entitatId);
	}

	public void addAvisosSyncServeis(Map<String, String[]> avisosProcedimentsOrgans, Long entitatId) {
		if (!avisosProcedimentsOrgans.isEmpty()) {
			String missatgeAvis = "";
			for(Map.Entry<String, String[]> avisProc: avisosProcedimentsOrgans.entrySet()) {
				missatgeAvis += " - Servei '" + avisProc.getKey() + "': actualment a l'òrgan " + avisProc.getValue()[0] + ", i hauria de pertànyer a l'òrgan " + avisProc.getValue()[1] + " </br>";
			}
			missatgeAvis += "Realitzi una actualizació d'òrgans per a resoldre aquesta situació, o revisi la configuració dels procediments al repositori de procediments";
			Date ara = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(ara);
			calendar.add(Calendar.YEAR, 1);
			AvisEntity avis = AvisEntity.getBuilder(
					ServeiServiceImpl.SERVEI_ORGAN_NO_SYNC,
					missatgeAvis,
					ara,
					calendar.getTime(),
					AvisNivellEnumDto.ERROR,
					true,
					entitatId).build();
			avisRepository.save(avis);
		}
	}

	private int getTotalServeis(String codiDir3) {
		log.debug(">>>> >> Obtenir total serveis Rolsac...");
		Long t1 = System.currentTimeMillis();
		int totalElements = pluginHelper.getTotalServeis(codiDir3);
		Long t2 = System.currentTimeMillis();
		log.debug(">>>> >> resultat"  + totalElements + " serveis (" + (t2 - t1) + "ms)");
		return totalElements;
	}

	private List<ProcSerDto> getServeisGdaByEntitat(String codiDir3) {
		ProgresActualitzacioDto progres = ServeiServiceImpl.progresActualitzacioServeis.get(codiDir3);

		log.debug(">>>> >> Obtenir tots els serveis de Rolsac...");
		progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("servei.actualitzacio.auto.consulta.gesconadm"));
		Long t1 = System.currentTimeMillis();

		List<ProcSerDto> serveisEntitat = pluginHelper.getServeisGdaByEntitat(codiDir3);

		Long t2 = System.currentTimeMillis();
		log.debug(">>>> >> obtinguts" + serveisEntitat.size() + " serveis (" + (t2 - t1) + "ms)");
		progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("servei.actualitzacio.auto.consulta.gesconadm.result", new Object[] {serveisEntitat.size()}));
		progres.addInfo(TipusInfo.TEMPS, messageHelper.getMessage("servei.actualitzacio.auto.temps", new Object[] {(t2 - t1)}));

		return serveisEntitat;
	}

	private List<ProcSerDto> getServeisGdaByEntitat(
			String codiDir3,
			int numPagina) {
		ProgresActualitzacioDto progres = ServeiServiceImpl.progresActualitzacioServeis.get(codiDir3);

		log.debug(">>>> >> Obtenir tots els serveis de Rolsac...");
		progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("servei.actualitzacio.auto.consulta.gesconadm"));
		Long t1 = System.currentTimeMillis();

		List<ProcSerDto> serveisEntitat = pluginHelper.getServeisGdaByEntitat(
				codiDir3,
				numPagina);

		Long t2 = System.currentTimeMillis();
		log.debug(">>>> >> obtinguts" + serveisEntitat.size() + " serveis (" + (t2 - t1) + "ms)");
		progres.addInfo(TipusInfo.INFO, messageHelper.getMessage("servei.actualitzacio.auto.consulta.gesconadm.result", new Object[] {serveisEntitat.size()}));
		progres.addInfo(TipusInfo.TEMPS, messageHelper.getMessage("servei.actualitzacio.auto.temps", new Object[] {(t2 - t1)}));

		return serveisEntitat;
	}

	private boolean isActualitzacioServeisModificarProperty() {
		return configHelper.getAsBoolean("es.caib.notib.actualitzacio.procediments.modificar");
	}

	public boolean isActualitzacioServeisEliminarOrgansProperty() {
		return configHelper.getAsBoolean("es.caib.notib.actualitzacio.procediments.eliminar.organs");
	}


}
