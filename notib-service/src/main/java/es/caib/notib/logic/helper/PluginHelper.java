package es.caib.notib.logic.helper;

import es.caib.notib.logic.exception.DocumentNotFoundException;
import es.caib.notib.logic.helper.plugin.AbstractPluginHelper;
import es.caib.notib.logic.helper.plugin.ArxiuPluginHelper;
import es.caib.notib.logic.helper.plugin.CarpetaPluginHelper;
import es.caib.notib.logic.helper.plugin.DadesUsuarisPluginHelper;
import es.caib.notib.logic.helper.plugin.FirmaPluginHelper;
import es.caib.notib.logic.helper.plugin.GestioDocumentalPluginHelper;
import es.caib.notib.logic.helper.plugin.GestorDocumentalAdministratiuPluginHelper;
import es.caib.notib.logic.helper.plugin.RegistrePluginHelper;
import es.caib.notib.logic.helper.plugin.UnitatsOrganitzativesPluginHelper;
import es.caib.notib.logic.helper.plugin.ValidaSignaturaPluginHelper;
import es.caib.notib.logic.intf.dto.AsientoRegistralBeanDto;
import es.caib.notib.logic.intf.dto.FitxerDto;
import es.caib.notib.logic.intf.dto.InteresadoWsDto;
import es.caib.notib.logic.intf.dto.LlibreDto;
import es.caib.notib.logic.intf.dto.OficinaDto;
import es.caib.notib.logic.intf.dto.RegistreAnnexDto;
import es.caib.notib.logic.intf.dto.RegistreModeFirmaDtoEnum;
import es.caib.notib.logic.intf.dto.RegistreOrigenDtoEnum;
import es.caib.notib.logic.intf.dto.RegistreTipusDocumentDtoEnum;
import es.caib.notib.logic.intf.dto.RegistreTipusDocumentalDtoEnum;
import es.caib.notib.logic.intf.dto.SignatureInfoDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioComunicacioTipusEnumDto;
import es.caib.notib.logic.intf.dto.organisme.OrganGestorDto;
import es.caib.notib.logic.intf.dto.organisme.OrganismeDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerDto;
import es.caib.notib.logic.intf.exception.SistemaExternException;
import es.caib.notib.persist.entity.DocumentEntity;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.persist.entity.OrganGestorEntity;
import es.caib.notib.persist.entity.PersonaEntity;
import es.caib.notib.persist.repository.OrganGestorRepository;
import es.caib.notib.plugin.arxiu.ArxiuPlugin;
import es.caib.notib.plugin.firmaservidor.FirmaServidorPlugin.TipusFirma;
import es.caib.notib.plugin.gesconadm.GestorContingutsAdministratiuPlugin;
import es.caib.notib.plugin.gesdoc.GestioDocumentalPlugin;
import es.caib.notib.plugin.registre.AutoritzacioRegiWeb3Enum;
import es.caib.notib.plugin.registre.CodiAssumpte;
import es.caib.notib.plugin.registre.LlibreOficina;
import es.caib.notib.plugin.registre.Organisme;
import es.caib.notib.plugin.registre.RegistrePlugin;
import es.caib.notib.plugin.registre.RegistrePluginException;
import es.caib.notib.plugin.registre.RespostaConsultaRegistre;
import es.caib.notib.plugin.registre.RespostaJustificantRecepcio;
import es.caib.notib.plugin.registre.TipusAssumpte;
import es.caib.notib.plugin.registre.TipusRegistreRegweb3Enum;
import es.caib.notib.plugin.unitat.CodiValor;
import es.caib.notib.plugin.unitat.CodiValorPais;
import es.caib.notib.plugin.unitat.NodeDir3;
import es.caib.notib.plugin.unitat.ObjetoDirectorio;
import es.caib.notib.plugin.unitat.UnitatsOrganitzativesPlugin;
import es.caib.notib.plugin.usuari.DadesUsuari;
import es.caib.notib.plugin.usuari.DadesUsuariPlugin;
import es.caib.plugins.arxiu.api.ArxiuException;
import es.caib.plugins.arxiu.api.Document;
import es.caib.plugins.arxiu.api.DocumentContingut;
import es.caib.plugins.arxiu.api.DocumentEstatElaboracio;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Helper per a interactuar amb els plugins.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PluginHelper {

	public static final String GESDOC_AGRUPACIO_CERTIFICACIONS = "certificacions";
	public static final String GESDOC_AGRUPACIO_NOTIFICACIONS = "notificacions";
	public static final String GESDOC_AGRUPACIO_TEMPORALS = "tmp";
	public static final String GESDOC_AGRUPACIO_MASSIUS_CSV = "massius_csv";
	public static final String GESDOC_AGRUPACIO_MASSIUS_ZIP = "massius_zip";
	public static final String GESDOC_AGRUPACIO_MASSIUS_ERRORS = "massius_errors";
	public static final String GESDOC_AGRUPACIO_MASSIUS_INFORMES = "massius_informes";

	private final DadesUsuarisPluginHelper dadesUsuarisPluginHelper;
	private final GestioDocumentalPluginHelper gestioDocumentalPluginHelper;
	private final RegistrePluginHelper registrePluginHelper;
	private final ArxiuPluginHelper arxiuPluginHelper;
	private final UnitatsOrganitzativesPluginHelper unitatsOrganitzativesPluginHelper;
	private final GestorDocumentalAdministratiuPluginHelper gestorDocumentalAdministratiuPluginHelper;
	private final FirmaPluginHelper firmaPluginHelper;
	private final ValidaSignaturaPluginHelper validaSignaturaPluginHelper;
	private final CarpetaPluginHelper carpetaPluginHelper;

	private final ConfigHelper configHelper;
	private final OrganGestorRepository organGestorRepository;


	// REGISTRE
	// /////////////////////////////////////////////////////////////////////////////////////

	public RespostaConsultaRegistre crearAsientoRegistral(String codiDir3Entitat, AsientoRegistralBeanDto arb, Long tipusOperacio, Long notificacioId, String enviamentIds, boolean generarJustificant) {
		return registrePluginHelper.crearAsientoRegistral(codiDir3Entitat, arb, tipusOperacio, notificacioId, enviamentIds, generarJustificant);
	}

	public RespostaConsultaRegistre obtenerAsientoRegistral(String codiDir3Entitat, String numeroRegistreFormatat, Long tipusRegistre, boolean ambAnnexos) {
		return registrePluginHelper.obtenerAsientoRegistral(codiDir3Entitat, numeroRegistreFormatat, tipusRegistre, ambAnnexos);
	}

	public RespostaJustificantRecepcio obtenirJustificant(String codiDir3Entitat, String numeroRegistreFormatat) {
		return registrePluginHelper.obtenirJustificant(codiDir3Entitat, numeroRegistreFormatat);
	}
	
	public RespostaJustificantRecepcio obtenirOficiExtern(String codiDir3Entitat, String numeroRegistreFormatat) {
		return registrePluginHelper.obtenirOficiExtern(codiDir3Entitat, numeroRegistreFormatat);
	}
	
	public List<TipusAssumpte> llistarTipusAssumpte(String codiDir3Entitat) throws SistemaExternException {
		return registrePluginHelper.llistarTipusAssumpte(codiDir3Entitat);
	}

	public List<CodiAssumpte> llistarCodisAssumpte(String codiDir3Entitat, String tipusAssumpte) throws SistemaExternException {
		return registrePluginHelper.llistarCodisAssumpte(codiDir3Entitat, tipusAssumpte);
	}
	
	public OficinaDto llistarOficinaVirtual(String codiDir3Entitat, String nomOficinaVirtual, TipusRegistreRegweb3Enum autoritzacio) throws SistemaExternException {
		return registrePluginHelper.llistarOficinaVirtual(codiDir3Entitat, nomOficinaVirtual, autoritzacio);
	}
	
	public List<OficinaDto> llistarOficines(String codiDir3Entitat, AutoritzacioRegiWeb3Enum autoritzacio) throws SistemaExternException {
		return registrePluginHelper.llistarOficines(codiDir3Entitat, autoritzacio);
	}
	
	public List<LlibreOficina> llistarLlibresOficines(String codiDir3Entitat, String usuariCodi, TipusRegistreRegweb3Enum tipusRegistre) throws SistemaExternException{
		return registrePluginHelper.llistarLlibresOficines(codiDir3Entitat, usuariCodi, tipusRegistre);
	}
	
	public LlibreDto llistarLlibreOrganisme(String codiDir3Entitat, String organismeCodi) throws SistemaExternException{
		return registrePluginHelper.llistarLlibreOrganisme(codiDir3Entitat, organismeCodi);
	}
	
	public List<LlibreDto> llistarLlibres(String codiDir3Entitat, String oficina, AutoritzacioRegiWeb3Enum autoritzacio) throws SistemaExternException {
		return registrePluginHelper.llistarLlibres(codiDir3Entitat, oficina, autoritzacio);
	}
	
	public List<Organisme> llistarOrganismes(String codiDir3Entitat) throws SistemaExternException {
		return registrePluginHelper.llistarOrganismes(codiDir3Entitat);
	}

	public String estatElaboracioToValidesa(DocumentEstatElaboracio estatElaboracio) {
		return registrePluginHelper.estatElaboracioToValidesa(estatElaboracio);
	}

	public Integer getModeFirma(Document document, String nom) {
		return registrePluginHelper.getModeFirma(document, nom);
	}

	public AsientoRegistralBeanDto notificacioEnviamentsToAsientoRegistralBean(NotificacioEntity notificacio, Set<NotificacioEnviamentEntity> enviaments, boolean inclou_documents) throws RegistrePluginException {
		return registrePluginHelper.notificacioEnviamentsToAsientoRegistralBean(notificacio, enviaments, inclou_documents);
	}

	public AsientoRegistralBeanDto notificacioToAsientoRegistralBean(NotificacioEntity notificacio, NotificacioEnviamentEntity enviament, boolean inclou_documents, boolean isComunicacioSir) throws RegistrePluginException {
		return registrePluginHelper.notificacioToAsientoRegistralBean(notificacio, enviament, inclou_documents, isComunicacioSir);
	}

	public InteresadoWsDto personaToRepresentanteEInteresadoWs (PersonaEntity titular, PersonaEntity destinatari) {
		return registrePluginHelper.personaToRepresentanteEInteresadoWs(titular, destinatari);
	}

	public void addOficinaAndLlibreRegistre(NotificacioEntity notificacio){
		registrePluginHelper.addOficinaAndLlibreRegistre(notificacio);
	}

	// USUARIS
	// /////////////////////////////////////////////////////////////////////////////////////
	
	public List<String> consultarRolsAmbCodi(String usuariCodi) {
		return dadesUsuarisPluginHelper.consultarRolsAmbCodi(usuariCodi);
	}
	
	public DadesUsuari dadesUsuariConsultarAmbCodi(String usuariCodi) {
		return dadesUsuarisPluginHelper.dadesUsuariConsultarAmbCodi(usuariCodi);
	}
	
	public List<DadesUsuari> dadesUsuariConsultarAmbGrup(String grupCodi) {
		return dadesUsuarisPluginHelper.dadesUsuariConsultarAmbGrup(grupCodi);
	}

	// ARXIU 
	// /////////////////////////////////////////////////////////////////////////////////////
	
	public Document arxiuDocumentConsultar(String arxiuUuid, String versio, boolean isUuid) {
		return arxiuDocumentConsultar(arxiuUuid, versio, false, isUuid);
	}

	public Document arxiuDocumentConsultar(String identificador, String versio, boolean ambContingut, boolean isUuid) throws DocumentNotFoundException {
		return arxiuPluginHelper.arxiuDocumentConsultar(identificador, versio, ambContingut, isUuid);
	}
	
	public DocumentContingut arxiuGetImprimible(String id, boolean isUuid) {
		return arxiuPluginHelper.arxiuGetImprimible(id, isUuid);
	}
	
	
	// GESTOR DOCUMENTAL
	// /////////////////////////////////////////////////////////////////////////////////////

	public String gestioDocumentalCreate(String agrupacio, byte[] contingut) {
		return gestioDocumentalPluginHelper.gestioDocumentalCreate(agrupacio, contingut);
	}

	public void gestioDocumentalUpdate(String id, String agrupacio, byte[] contingut) {
		gestioDocumentalPluginHelper.gestioDocumentalUpdate(id, agrupacio, contingut);
	}
	
	public void gestioDocumentalDelete(String id, String agrupacio) {
		gestioDocumentalPluginHelper.gestioDocumentalDelete(id, agrupacio);
	}

	public void gestioDocumentalGet(String id, String agrupacio, OutputStream contingutOut) {
		gestioDocumentalGet(id, agrupacio, contingutOut, null);
	}

	public void gestioDocumentalGet(String id, String agrupacio, OutputStream contingutOut, Boolean isZip) {
		gestioDocumentalPluginHelper.gestioDocumentalGet(id, agrupacio, contingutOut, isZip);
	}

	// GESTOR CONTINGUTS ADMINISTRATIU (ROLSAC)
	// /////////////////////////////////////////////////////////////////////////////////////
	
	public List<ProcSerDto> getProcedimentsGda() {
		return gestorDocumentalAdministratiuPluginHelper.getProcedimentsGda();
	}
	
	public int getTotalProcediments(String codiDir3Entitat) {
		return gestorDocumentalAdministratiuPluginHelper.getTotalProcediments(codiDir3Entitat);
	}

	public List<ProcSerDto> getProcedimentsGdaByEntitat(String codiDir3) {
		return gestorDocumentalAdministratiuPluginHelper.getProcedimentsGdaByEntitat(codiDir3);
	}

	public ProcSerDto getProcSerByCodiSia(String codiSia, boolean isServei) {
		return gestorDocumentalAdministratiuPluginHelper.getProcSerByCodiSia(codiSia, isServei);
	}
	
	public List<ProcSerDto> getProcedimentsGdaByEntitat(String codiDir3, int numPagina) {
		return gestorDocumentalAdministratiuPluginHelper.getProcedimentsGdaByEntitat(codiDir3, numPagina);
	}

	public int getTotalServeis(String codiDir3) {
		return gestorDocumentalAdministratiuPluginHelper.getTotalServeis(codiDir3);
	}

	public List<ProcSerDto> getServeisGdaByEntitat(String codiDir3) {
		return gestorDocumentalAdministratiuPluginHelper.getServeisGdaByEntitat(codiDir3);
	}

	public List<ProcSerDto> getServeisGdaByEntitat(String codiDir3, int numPagina) {
		return gestorDocumentalAdministratiuPluginHelper.getServeisGdaByEntitat(codiDir3, numPagina);
	}
	
	// UNITATS ORGANITZATIVES
	// /////////////////////////////////////////////////////////////////////////////////////

	public Map<String, NodeDir3> getOrganigramaPerEntitat(String codiDir3Entitat) throws SistemaExternException {
		return unitatsOrganitzativesPluginHelper.getOrganigramaPerEntitat(codiDir3Entitat);
	}

	public List<NodeDir3> getOrganNomMultidioma(EntitatEntity entitat) {
		return unitatsOrganitzativesFindByPare(entitat.getCodi(), entitat.getDir3Codi(), null, null);
	}

	public byte[] unitatsOrganitzativesFindByPareJSON(String entitatCodi, String pareCodi, Date dataActualitzacio, Date dataSincronitzacio) {
		return unitatsOrganitzativesPluginHelper.unitatsOrganitzativesFindByPareJSON(entitatCodi, pareCodi, dataActualitzacio, dataSincronitzacio);
	}

	public List<NodeDir3> unitatsOrganitzativesFindByPare(String entitatCodi, String pareCodi, Date dataActualitzacio, Date dataSincronitzacio) {
		return unitatsOrganitzativesPluginHelper.unitatsOrganitzativesFindByPare(entitatCodi, pareCodi, dataActualitzacio, dataSincronitzacio);
	}

	public NodeDir3 unitatOrganitzativaFindByCodi(String entitatCodi, String codi, Date dataActualitzacio, Date dataSincronitzacio) {
		return unitatsOrganitzativesPluginHelper.unitatOrganitzativaFindByCodi(entitatCodi, codi, dataActualitzacio, dataSincronitzacio);
	}

	public List<OficinaDto> oficinesSIRUnitat(String unitatCodi, Map<String, OrganismeDto> arbreUnitats) throws SistemaExternException {
		return unitatsOrganitzativesPluginHelper.oficinesSIRUnitat(unitatCodi, arbreUnitats);
	}

	public List<OficinaDto> oficinesEntitat(String codiDir3Entitat) throws SistemaExternException {
		return unitatsOrganitzativesPluginHelper.oficinesEntitat(codiDir3Entitat);
	}

	public List<ObjetoDirectorio> llistarOrganismesPerEntitat(String entitatcodi) throws SistemaExternException {
		return unitatsOrganitzativesPluginHelper.llistarOrganismesPerEntitat(entitatcodi);
	}
	
	public String getDenominacio(String codiDir3) {
		return unitatsOrganitzativesPluginHelper.getDenominacio(codiDir3);
	}
	
	public List<OrganGestorDto> cercaUnitats(String codi, String denominacio, Long nivellAdministracio, Long comunitatAutonoma, Boolean ambOficines, Boolean esUnitatArrel, Long provincia, String municipi) throws SistemaExternException {
		var organs = unitatsOrganitzativesPluginHelper.cercaUnitats(codi, denominacio, nivellAdministracio, comunitatAutonoma, ambOficines, esUnitatArrel, provincia, municipi);
		OrganGestorEntity organ;
		for (var o : organs) {
			organ = organGestorRepository.findByCodi(o.getCodi());
			if (organ == null) {
				continue;
			}
			o.setPermetreSir(organ.isPermetreSir());
		}
		return organs;
	}

	public List<OrganGestorDto> unitatsPerCodi(String codi) throws SistemaExternException {
		return cercaUnitats(codi,null,null,null,null,null,null,null);
	}

	public List<OrganGestorDto> unitatsPerDenominacio(String denominacio) throws SistemaExternException {
		return unitatsOrganitzativesPluginHelper.unitatsPerDenominacio(denominacio);
	}

	public List<CodiValor> llistarNivellsAdministracions() throws SistemaExternException {
		return unitatsOrganitzativesPluginHelper.llistarNivellsAdministracions();
	}
	
	public List<CodiValor> llistarComunitatsAutonomes() throws SistemaExternException {
		return unitatsOrganitzativesPluginHelper.llistarComunitatsAutonomes();
	}

	public List<CodiValorPais> llistarPaisos() throws SistemaExternException {
		return unitatsOrganitzativesPluginHelper.llistarPaisos();
	}

	public List<CodiValor> llistarProvincies() throws SistemaExternException {
		return unitatsOrganitzativesPluginHelper.llistarProvincies();
	}
	
	public List<CodiValor> llistarProvincies(String codiCA) throws SistemaExternException {
		return unitatsOrganitzativesPluginHelper.llistarProvincies(codiCA);
	}
	
	public List<CodiValor> llistarLocalitats(String codiProvincia) throws SistemaExternException {
		return unitatsOrganitzativesPluginHelper.llistarLocalitats(codiProvincia);
	}


	// FIRMA EN SERVIDOR
	// /////////////////////////////////////////////////////////////////////////////////////

	public byte[] firmaServidorFirmar(NotificacioEntity notificacio, FitxerDto fitxer, TipusFirma tipusFirma, String motiu, String idioma) {
		return firmaPluginHelper.firmaServidorFirmar(notificacio, fitxer, tipusFirma, motiu, idioma);
	}


	// VALIDACIÓ DE FIRMES
	// /////////////////////////////////////////////////////////////////////////////////////

	public SignatureInfoDto detectSignedAttachedUsingValidateSignaturePlugin(byte[] documentContingut, String nom, String firmaContentType) {
		return validaSignaturaPluginHelper.detectSignedAttachedUsingValidateSignaturePlugin(documentContingut, nom, firmaContentType);
	}

	// CARPETA
	// /////////////////////////////////////////////////////////////////////////////////////

	public void enviarNotificacioMobil(NotificacioEnviamentEntity e) {
		carpetaPluginHelper.enviarNotificacioMobil(e);
	}

	public boolean enviarCarpeta() {
		return carpetaPluginHelper.enviarCarpeta();
	}



	// UTILS PLUGINS
//	public boolean isDadesUsuariPluginDisponible() {
//		return dadesUsuarisPluginHelper.isDadesUsuariPluginDisponible();
//	}

//	public boolean isGestioDocumentalPluginDisponible() {
//		return gestioDocumentalPluginHelper.isGestioDocumentalPluginDisponible();
//	}

	public boolean isArxiuPluginDisponible() {
		return arxiuPluginHelper.isArxiuPluginDisponible();
	}

	public void resetPlugins(String grup) {

		switch (grup) {
			case "ARXIU":
				arxiuPluginHelper.resetPlugin();
				break;
			case "USUARIS":
				dadesUsuarisPluginHelper.resetPlugin();
				break;
			case "FIRMA":
				firmaPluginHelper.resetPlugin();
				break;
			case "VALIDATE_SIGNATURE":
				validaSignaturaPluginHelper.resetPlugin();
				break;
			case "GESCONADM":
				gestorDocumentalAdministratiuPluginHelper.resetPlugin();
				break;
			case "GES_DOC":
				gestioDocumentalPluginHelper.resetPlugin();
				break;
			case "REGISTRE":
				registrePluginHelper.resetPlugin();
				break;
			case "DIR3":
				unitatsOrganitzativesPluginHelper.resetPlugin();
				break;
			case "CARPETA":
				carpetaPluginHelper.resetPlugin();
				break;
			default:
				break;
		}
	}

	public void resetAllPlugins() {

		arxiuPluginHelper.resetPlugin();
		dadesUsuarisPluginHelper.resetPlugin();
		firmaPluginHelper.resetPlugin();
		validaSignaturaPluginHelper.resetPlugin();
		gestorDocumentalAdministratiuPluginHelper.resetPlugin();
		gestioDocumentalPluginHelper.resetPlugin();
		registrePluginHelper.resetPlugin();
		unitatsOrganitzativesPluginHelper.resetPlugin();
		carpetaPluginHelper.resetPlugin();
	}

//	public List<IntegracioSalut> getPeticionsPluginsAndReset() {
//
//		var estatPluginArx = arxiuPluginHelper.getIntegracionsSalut();
//		var estatPluginUsr = dadesUsuarisPluginHelper.getIntegracionsSalut();
//		var estatPluginPfi = firmaPluginHelper.getIntegracionsSalut();
//		var estatPluginAfi = validaSignaturaPluginHelper.getIntegracionsSalut();
//		var estatPluginRsc = gestorDocumentalAdministratiuPluginHelper.getIntegracionsSalut();
////		var estatPluginGdo = gestioDocumentalPluginHelper.getIntegracionsSalut()();
//		var estatPluginReg = registrePluginHelper.getIntegracionsSalut();
//		var estatPluginDir = unitatsOrganitzativesPluginHelper.getIntegracionsSalut();
//		var estatPluginCar = carpetaPluginHelper.getIntegracionsSalut();
//
//		var peticionsPluginArx = arxiuPluginHelper.getPeticionsPluginAndReset();
//		var peticionsPluginUsr = dadesUsuarisPluginHelper.getPeticionsPluginAndReset();
//		var peticionsPluginPfi = firmaPluginHelper.getPeticionsPluginAndReset();
//		var peticionsPluginAfi = validaSignaturaPluginHelper.getPeticionsPluginAndReset();
//		var peticionsPluginRsc = gestorDocumentalAdministratiuPluginHelper.getPeticionsPluginAndReset();
////		var peticionsPluginGdo = gestioDocumentalPluginHelper.getPeticionsPluginAndReset();
//		var peticionsPluginReg = registrePluginHelper.getPeticionsPluginAndReset();
//		var peticionsPluginDir = unitatsOrganitzativesPluginHelper.getPeticionsPluginAndReset();
//		var peticionsPluginCar = carpetaPluginHelper.getPeticionsPluginAndReset();
//
//
//		var salutPluginArx = IntegracioSalut.builder().codi(IntegracioApp.ARX.name()).estat(estatPluginArx.getEstat()).latencia(estatPluginArx.getLatencia()).peticions(peticionsPluginArx).build();
//		var salutPluginUsr = IntegracioSalut.builder().codi(IntegracioApp.USR.name()).estat(estatPluginUsr.getEstat()).latencia(estatPluginUsr.getLatencia()).peticions(peticionsPluginUsr).build();
//		var salutPluginPfi = IntegracioSalut.builder().codi(IntegracioApp.PFI.name()).estat(estatPluginPfi.getEstat()).latencia(estatPluginPfi.getLatencia()).peticions(peticionsPluginPfi).build();
//		var salutPluginAfi = IntegracioSalut.builder().codi(IntegracioApp.AFI.name()).estat(estatPluginAfi.getEstat()).latencia(estatPluginAfi.getLatencia()).peticions(peticionsPluginAfi).build();
//		var salutPluginRsc = IntegracioSalut.builder().codi(IntegracioApp.RSC.name()).estat(estatPluginRsc.getEstat()).latencia(estatPluginRsc.getLatencia()).peticions(peticionsPluginRsc).build();
////		var salutPluginGdo = IntegracioSalut.builder().codi("GDO").estat(estatPluginGdo.getEstat()).latencia(estatPluginGdo.getLatencia()).peticions(peticionsPluginGdo).build();
//		var salutPluginReg = IntegracioSalut.builder().codi(IntegracioApp.REG.name()).estat(estatPluginReg.getEstat()).latencia(estatPluginReg.getLatencia()).peticions(peticionsPluginReg).build();
//		var salutPluginDir = IntegracioSalut.builder().codi(IntegracioApp.DIR.name()).estat(estatPluginDir.getEstat()).latencia(estatPluginDir.getLatencia()).peticions(peticionsPluginDir).build();
//		var salutPluginCar = IntegracioSalut.builder().codi(IntegracioApp.CAR.name()).estat(estatPluginCar.getEstat()).latencia(estatPluginCar.getLatencia()).peticions(peticionsPluginCar).build();
//		// TODO: Afegir Notifica i Email
////		var salutPluginNtf = IntegracioSalut.builder().codi(IntegracioApp.NTF.name()).estat(estatPluginNtf.getEstat()).latencia(estatPluginNtf.getLatencia()).peticions(peticionsPluginNtf).build();
////		var salutPluginEml = IntegracioSalut.builder().codi(IntegracioApp.EML.name()).estat(estatPluginEml.getEstat()).latencia(estatPluginEml.getLatencia()).peticions(peticionsPluginEml).build();
//
//
//		return List.of(
//				salutPluginUsr,
//				salutPluginReg,
//				salutPluginArx,
//				salutPluginDir,
//				salutPluginRsc,
//				salutPluginPfi,
//				salutPluginAfi,
//				salutPluginCar
////				salutPluginNtf,
////				salutPluginEml
//		);
//	}


	// PROPIETATS TASQUES EN SEGON PLA
	public int getRegistreReintentsPeriodeProperty() {
		return configHelper.getConfigAsInteger("es.caib.notib.tasca.registre.enviaments.periode");
	}
	public int getNotificaReintentsPeriodeProperty() {
		return configHelper.getConfigAsInteger("es.caib.notib.tasca.notifica.enviaments.periode");
	}
	public int getConsultaReintentsPeriodeProperty() {
		return configHelper.getConfigAsInteger("es.caib.notib.tasca.enviament.actualitzacio.estat.periode");
	}
	public int getConsultaSirReintentsPeriodeProperty() {
		return configHelper.getConfigAsInteger("es.caib.notib.tasca.enviament.actualitzacio.estat.registre.periode");
	}
	public int getRegistreReintentsMaxProperty() {
		return configHelper.getConfigAsInteger("es.caib.notib.tasca.registre.enviaments.reintents.maxim");
	}
	public int getNotificaReintentsMaxProperty() {
		return configHelper.getConfigAsInteger("es.caib.notib.tasca.notifica.enviaments.reintents.maxim");
	}
	public int getConsultaReintentsMaxProperty() {
		return configHelper.getConfigAsInteger("es.caib.notib.tasca.enviament.actualitzacio.estat.reintents.maxim");
	}
	public int getConsultaReintentsDEHMaxProperty() {
		return configHelper.getConfigAsInteger("es.caib.notib.tasca.enviament.actualitzacio.estat.deh.reintents.maxim");
	}
	public int getConsultaReintentsCIEMaxProperty() {
		return configHelper.getConfigAsInteger("es.caib.notib.tasca.enviament.actualitzacio.estat.cie.reintents.maxim");
	}
	public int getConsultaSirReintentsMaxProperty() {
		return configHelper.getConfigAsInteger("es.caib.notib.tasca.enviament.actualitzacio.estat.registre.reintents.maxim");
	}

	public NotificacioComunicacioTipusEnumDto getNotibTipusComunicacioDefecte() {

		var tipus = NotificacioComunicacioTipusEnumDto.SINCRON;
		try {
			var tipusStr = configHelper.getConfig("es.caib.notib.comunicacio.tipus.defecte");
			if (tipusStr != null && !tipusStr.isEmpty()) {
				tipus = NotificacioComunicacioTipusEnumDto.valueOf(tipusStr);
			}
		} catch (Exception ex) {
			log.error("No s'ha pogut obtenir el tipus de comunicació per defecte. S'utilitzarà el tipus SINCRON.");
		}
		return tipus;
	}


	// Mètodes pels tests
	public void setDadesUsuariPlugin(DadesUsuariPlugin dadesUsuariPlugin) {
		dadesUsuarisPluginHelper.setDadesUsuariPlugin(dadesUsuariPlugin);
	}

	public void setGestioDocumentalPlugin(GestioDocumentalPlugin gestioDocumentalPlugin) {
		gestioDocumentalPluginHelper.setGestioDocumentalPlugin(gestioDocumentalPlugin);
	}
	public void setGestioDocumentalPlugin(Map<String, GestioDocumentalPlugin> gestioDocumentalPlugin) {
		gestioDocumentalPluginHelper.setGestioDocumentalPlugin(gestioDocumentalPlugin);
	}
	
	public void setRegistrePlugin(RegistrePlugin registrePlugin) {
		registrePluginHelper.setRegistrePlugin(registrePlugin);
	}
	public void setRegistrePlugin(Map<String, RegistrePlugin> registrePlugin) {
		registrePluginHelper.setRegistrePlugin(registrePlugin);
	}

	public void setArxiuPlugin(ArxiuPlugin arxiuPlugin) {
		arxiuPluginHelper.setArxiuPlugin(arxiuPlugin);
	}
	public void setArxiuPlugin(Map<String, ArxiuPlugin> arxiuPlugin) {
		arxiuPluginHelper.setArxiuPlugin(arxiuPlugin);
	}
	
	public void setUnitatsOrganitzativesPlugin(UnitatsOrganitzativesPlugin unitatsOrganitzativesPlugin) {
		unitatsOrganitzativesPluginHelper.setUnitatsOrganitzativesPlugin(unitatsOrganitzativesPlugin);
	}
	public void setUnitatsOrganitzativesPlugin(Map<String, UnitatsOrganitzativesPlugin> unitatsOrganitzativesPlugin) {
		unitatsOrganitzativesPluginHelper.setUnitatsOrganitzativesPlugin(unitatsOrganitzativesPlugin);
	}

	public void setGestorDocumentalAdministratiuPlugin(Map<String, GestorContingutsAdministratiuPlugin> gestorDocumentalAdministratiuPlugin) {
		gestorDocumentalAdministratiuPluginHelper.setGestorDocumentalAdministratiuPlugin(gestorDocumentalAdministratiuPlugin);
	}

	public boolean isReadDocsMetadataFromArxiu() {
		return registrePluginHelper.isReadDocsMetadataFromArxiu();
	}

	public RegistreAnnexDto documentToRegistreAnnexDto (DocumentEntity document) {

		var annex = new RegistreAnnexDto();
		annex.setTipusDocument(RegistreTipusDocumentDtoEnum.DOCUMENT_ADJUNT_FORMULARI);
		annex.setTipusDocumental(RegistreTipusDocumentalDtoEnum.NOTIFICACIO);
		annex.setOrigen(RegistreOrigenDtoEnum.ADMINISTRACIO);
		annex.setData(new Date());
		annex.setIdiomaCodi("ca");

		if((document.getUuid() != null || document.getCsv() != null) && document.getContingutBase64() == null) {
			var loadFromArxiu = isReadDocsMetadataFromArxiu() && document.getUuid() != null || document.getCsv() == null;
			DocumentContingut doc;
			if(loadFromArxiu) {
				try {
					annex.setModeFirma(RegistreModeFirmaDtoEnum.SENSE_FIRMA);

					doc = arxiuGetImprimible(document.getUuid(), true);
					annex.setArxiuContingut(doc.getContingut());
					annex.setArxiuNom(doc.getArxiuNom());
				} catch (ArxiuException ae) {
					log.error("Error Obtenint el document per l'uuid");
				}
				return annex;
			}
			try {
				annex.setModeFirma(RegistreModeFirmaDtoEnum.AUTOFIRMA_SI);
				doc = arxiuGetImprimible(document.getCsv(), false);
				annex.setArxiuContingut(doc.getContingut());
				annex.setArxiuNom(doc.getArxiuNom());
			} catch (ArxiuException ae) {
				log.error("Error Obtenint el document per csv");
			}
			return annex;
		}
		if(document.getContingutBase64() != null && (document.getUuid() == null && document.getCsv() == null)) {
			annex.setArxiuContingut(document.getContingutBase64().getBytes());
			annex.setArxiuNom(document.getArxiuNom());
			annex.setModeFirma(RegistreModeFirmaDtoEnum.SENSE_FIRMA);
		}
		return annex;
	}

	public List<AbstractPluginHelper<?>> getPluginHelpers() {
		return List.of(
				arxiuPluginHelper,
				dadesUsuarisPluginHelper,
				firmaPluginHelper,
				validaSignaturaPluginHelper,
				gestorDocumentalAdministratiuPluginHelper,
				registrePluginHelper,
				unitatsOrganitzativesPluginHelper,
				carpetaPluginHelper
		);
	}

//	private boolean isFitxerSigned(byte[] contingut, String contentType) {
//
//		if (!contentType.equals("application/pdf")) {
//			return false;
//		}
//		try {
//			var reader = new PdfReader(contingut);
//			var acroFields = reader.getAcroFields();
//			var signatureNames = acroFields.getSignatureNames();
//			return signatureNames != null && !signatureNames.isEmpty();
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//
//	}
//
//	public byte[] getUrlDocumentContent(String urlPath) throws SistemaExternException {
//
//		var baos = new ByteArrayOutputStream();
//		try {
//			try (var is = new URL(urlPath).openStream()) {
//				var byteChunk = new byte[4096]; // Or whatever size you want to read in at a time.
//				int n;
//				while ((n = is.read(byteChunk)) > 0) {
//					baos.write(byteChunk, 0, n);
//				}
//				return baos.toByteArray();
//			}
//		} catch (Exception e) {
//			log.error("Error al obtenir document de la URL: " + urlPath, e);
//			throw new SistemaExternException(IntegracioCodiEnum.GESDOC.name(), "Error al obtenir document de la URL: " + urlPath);
//		}
//	}

}
