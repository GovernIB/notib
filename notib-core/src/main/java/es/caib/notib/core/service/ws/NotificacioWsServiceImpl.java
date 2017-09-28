/**
 * 
 */
package es.caib.notib.core.service.ws;

import javax.jws.WebService;

import org.springframework.stereotype.Service;


/**
 * Implementació del servei per a la gestió de notificacions des
 * d'altres aplicacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
@WebService(
		name = "NotificacioWs",
		serviceName = "NotificacioWsService",
		portName = "NotificacioWsServicePort",
		targetNamespace = "http://www.caib.es/notib/ws/notificacio",
		endpointInterface = "es.caib.notib.core.api.service.ws.NotificacioWsService")
public class NotificacioWsServiceImpl /*implements NotificacioWsService*/ {

	/*@Autowired
	private EntitatRepository entitatRepository;
	@Autowired
	private NotificacioRepository notificacioRepository;
	@Autowired
	private NotificacioEnviamentRepository notificacioDestinatariRepository;

	@Autowired
	private NotificaHelper notificaHelper;
	@Autowired
	private PluginHelper pluginHelper;



	@Override
	@Transactional
	public List<String> alta(Notificacio notificacio) {
		try {
			EntitatEntity entitat = entitatRepository.findByDir3Codi(notificacio.getCifEntitat());
			String documentGesdocId = pluginHelper.gestioDocumentalCreate(
					PluginHelper.GESDOC_AGRUPACIO_NOTIFICACIONS,
					new ByteArrayInputStream(
							Base64.decode(notificacio.getDocumentContingutBase64())));
			NotificacioEntity notificacioEntity = NotificacioEntity.getBuilder(
					notificacio.getEnviamentTipus(), 
					notificacio.getEnviamentDataProgramada(),
					notificacio.getConcepte(),
					notificacio.getDocumentArxiuNom(),
					documentGesdocId,
					(notificacio.getDocumentSha1() != null && !notificacio.getDocumentSha1().isEmpty()) ? 
							notificacio.getDocumentSha1() : 
								SHAsum(Base64.decode(notificacio.getDocumentContingutBase64())),
					notificacio.getSeuAvisText(),
					notificacio.getSeuAvisTitol(),
					notificacio.getSeuOficiTitol(),
					notificacio.getSeuOficiText(),
					notificacio.getSeuIdioma(),
					notificacio.getSeuRegistreLlibre(),
					notificacio.getSeuRegistreOficina(),
					notificacio.getSeuExpedientTitol(),
					notificacio.getSeuExpedientIdentificadorEni(),
					notificacio.getSeuExpedientUnitatOrganitzativa(),
					notificacio.getSeuExpedientSerieDocumental(),
					notificacio.isDocumentNormalitzat(),
					notificacio.isDocumentGenerarCsv(),
					null,
					entitat).
					pagadorCorreusCodiDir3(notificacio.getPagadorCorreusCodiDir3()).
					pagadorCorreusContracteNum(notificacio.getPagadorCorreusContracteNum()).
					pagadorCorreusCodiClientFacturacio(notificacio.getPagadorCorreusCodiClientFacturacio()).
					pagadorCieDataVigencia(notificacio.getPagadorCorreusDataVigencia()).
					pagadorCieCodiDir3(notificacio.getPagadorCieCodiDir3()).
					pagadorCorreusDataVigencia(notificacio.getPagadorCorreusDataVigencia()).
					procedimentCodiSia(notificacio.getProcedimentCodiSia()).
					procedimentDescripcioSia(notificacio.getProcedimentDescripcioSia()).
					seuAvisTextMobil(notificacio.getSeuAvisTextMobil()).
					build();
			notificacioRepository.saveAndFlush(notificacioEntity);
			List<String> result = new ArrayList<String>();
			List<NotificacioEnviamentEntity> destinataris = new ArrayList<NotificacioEnviamentEntity>();
			for (NotificacioDestinatari d: notificacio.getDestinataris()) {
				NotificacioEnviamentEntity.Builder destinatari = NotificacioEnviamentEntity.getBuilder(
						d.getTitularNom(),
						d.getTitularNif(),
						d.getDestinatariNom(),
						d.getDestinatariNif(),
						d.getServeiTipus().toServeiTipusEnumDto(),
						d.isDehObligat(),
						notificacioEntity);
				destinatari.titularLlinatge1(
						d.getTitularLlinatge1());
				destinatari.titularLlinatge2(
						d.getTitularLlinatge2());
				destinatari.titularTelefon( d.getTitularTelefon() );
				destinatari.titularEmail( d.getTitularEmail() );
				destinatari.destinatariLlinatge1(
						d.getDestinatariLlinatge1());
				destinatari.destinatariLlinatge2(
						d.getDestinatariLlinatge2());
				destinatari.destinatariTelefon( d.getDestinatariTelefon() );
				destinatari.destinatariEmail( d.getDestinatariEmail() );
				if (d.getDomiciliTipus() != null) {
					destinatari.domiciliTipus(d.getDomiciliTipus().toNotificaDomiciliTipusEnumDto());
					destinatari.domiciliConcretTipus(d.getDomiciliConcretTipus().toNotificaDomiciliConcretTipusEnumDto());
					destinatari.domiciliViaTipus( d.getDomiciliViaTipus() );
					destinatari.domiciliViaNom( d.getDomiciliViaNom() );
					destinatari.domiciliNumeracioTipus(d.getDomiciliNumeracioTipus().toNotificaDomiciliNumeracioTipusEnumDto());
					destinatari.domiciliNumeracioNumero( d.getDomiciliNumeracioNumero() );
					destinatari.domiciliNumeracioPuntKm( d.getDomiciliNumeracioPuntKm() );
					destinatari.domiciliApartatCorreus( d.getDomiciliApartatCorreus() );
					destinatari.domiciliBloc( d.getDomiciliBloc() );
					destinatari.domiciliPortal( d.getDomiciliPortal() );
					destinatari.domiciliEscala( d.getDomiciliEscala() );
					destinatari.domiciliPlanta( d.getDomiciliPlanta() );
					destinatari.domiciliPorta( d.getDomiciliPorta() );
					destinatari.domiciliComplement( d.getDomiciliComplement() );
					destinatari.domiciliPoblacio( d.getDomiciliPoblacio() );
					destinatari.domiciliMunicipiCodiIne( d.getDomiciliMunicipiCodiIne() );
					destinatari.domiciliMunicipiNom( d.getDomiciliMunicipiNom() );
					destinatari.domiciliCodiPostal( d.getDomiciliCodiPostal() );
					destinatari.domiciliProvinciaCodi( d.getDomiciliProvinciaCodi() );
					destinatari.domiciliProvinciaNom( d.getDomiciliProvinciaNom() );
					destinatari.domiciliPaisCodiIso( d.getDomiciliPaisCodiIso() );
					destinatari.domiciliPaisNom( d.getDomiciliPaisNom() );
					destinatari.domiciliLinea1( d.getDomiciliLinea1() );
					destinatari.domiciliLinea2( d.getDomiciliLinea2() );
					destinatari.domiciliCie( d.getDomiciliCie() );
				}
				destinatari.dehObligat( d.isDehObligat() );
				destinatari.dehNif( d.getDehNif() != null ? d.getDehNif() : "" );
				destinatari.dehProcedimentCodi( d.getDehProcedimentCodi() );
				destinatari.retardPostal( d.getRetardPostal() );
				destinatari.caducitat( d.getCaducitat() );
				NotificacioEnviamentEntity entity = destinatari.build();
				entity = notificacioDestinatariRepository.saveAndFlush(entity);
				String referencia = notificaHelper.generarReferencia(entity);
				entity.updateReferencia(referencia);
				result.add(referencia);
				destinataris.add(entity);
			}
			notificacioEntity.updateDestinataris(destinataris);
			notificacioRepository.saveAndFlush(notificacioEntity);
			// TODO decidir si es fa l'enviament immediatament o si s'espera
			// a que l'envii la tasca programada.
			// notificaHelper.intentarEnviament(notificacioEntity);
			return result;
			
		} catch (Exception ex) {
			logger.error(
					"Error al donar d'alta la notificació",
					ex);
			if (ex instanceof HibernateJdbcException) {
				throw new InconsistenciaDadesWsServiceException(
						"Inconsistencia de dades al donar d'alta la notificació");
			} else {
				throw new NotificacioWsServiceException(
						"Error al donar d'alta la notificació",
						ex);
			}
		}
	}

	@Override
	@Transactional
	public Notificacio consulta(String referencia) {
		try {
			NotificacioEntity notificacio = notificacioRepository.findByDestinatariAndReferencia(
					referencia);
			if (notificacio == null) {
				throw new NotFoundException(
						"referencia:" + referencia,
						NotificacioEnviamentEntity.class);
			}
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			pluginHelper.gestioDocumentalGet(
					notificacio.getDocumentArxiuId(),
					PluginHelper.GESDOC_AGRUPACIO_NOTIFICACIONS,
					baos);
			Notificacio result = new Notificacio();
			result.setEnviamentTipus(
					notificacio.getEnviamentTipus());
			result.setEnviamentDataProgramada(
					notificacio.getEnviamentDataProgramada());
			result.setConcepte(
					notificacio.getConcepte());
			result.setPagadorCorreusCodiDir3(
					notificacio.getPagadorCorreusCodiDir3());
			result.setPagadorCorreusContracteNum(
					notificacio.getPagadorCorreusContracteNum());
			result.setPagadorCorreusCodiClientFacturacio(
					notificacio.getPagadorCorreusCodiClientFacturacio());
			result.setPagadorCorreusDataVigencia(
					notificacio.getPagadorCorreusDataVigencia());
			result.setPagadorCieCodiDir3(
					notificacio.getPagadorCieCodiDir3());
			result.setPagadorCieDataVigencia(
					notificacio.getPagadorCieDataVigencia());
			result.setProcedimentCodiSia(
					notificacio.getProcedimentCodiSia());
			result.setProcedimentDescripcioSia(
					notificacio.getProcedimentDescripcioSia());
			result.setDocumentArxiuNom(
					notificacio.getDocumentArxiuNom());
			result.setDocumentContingutBase64(
					new String(Base64.encode(baos.toByteArray())));
			result.setDocumentSha1(
					notificacio.getDocumentSha1());
			result.setDocumentNormalitzat(
					notificacio.isDocumentNormalitzat());
			result.setDocumentGenerarCsv(
					notificacio.isDocumentGenerarCsv());
			result.setSeuExpedientSerieDocumental(
					notificacio.getSeuExpedientSerieDocumental());
			result.setSeuExpedientUnitatOrganitzativa(
					notificacio.getSeuExpedientUnitatOrganitzativa());
			result.setSeuExpedientIdentificadorEni(
					notificacio.getSeuExpedientIdentificadorEni());
			result.setSeuExpedientTitol(
					notificacio.getSeuExpedientTitol());
			result.setSeuRegistreOficina(
					notificacio.getSeuRegistreOficina());
			result.setSeuRegistreLlibre(
					notificacio.getSeuRegistreLlibre());
			result.setSeuIdioma(
					notificacio.getSeuIdioma());
			result.setSeuAvisTitol(
					notificacio.getSeuAvisTitol());
			result.setSeuAvisText(
					notificacio.getSeuAvisText());
			result.setSeuAvisTextMobil(
					notificacio.getSeuAvisTextMobil());
			result.setSeuOficiTitol(
					notificacio.getSeuOficiTitol());
			result.setSeuOficiText(
					notificacio.getSeuOficiText());
			result.setEstat(
					notificacio.getEstat());
			List<NotificacioDestinatari> destinataris = new ArrayList<NotificacioDestinatari>();
			NotificacioEnviamentEntity destinatari = notificacioDestinatariRepository.findByNotificacioAndReferencia(
					notificacio,
					referencia);
			destinataris.add(
					new NotificacioDestinatari(
							destinatari.getReferencia(), 
							destinatari.getTitularNom(), 
							destinatari.getTitularLlinatge1(), 
							destinatari.getTitularLlinatge2(), 
							destinatari.getTitularNif(), 
							destinatari.getTitularTelefon(), 
							destinatari.getTitularEmail(), 
							destinatari.getDestinatariNom(), 
							destinatari.getDestinatariLlinatge1(), 
							destinatari.getDestinatariLlinatge2(), 
							destinatari.getDestinatariNif(), 
							destinatari.getDestinatariTelefon(), 
							destinatari.getDestinatariEmail(), 
							DomiciliTipusEnum.toDomiciliTipusEnum(destinatari.getDomiciliTipus()), 
							DomiciliConcretTipusEnum.toDomiciliConcretTipusEnum(destinatari.getDomiciliConcretTipus()), 
							destinatari.getDomiciliViaTipus(), 
							destinatari.getDomiciliViaNom(), 
							DomiciliNumeracioTipusEnum.toDomiciliNumeracioTipusEnum(destinatari.getDomiciliNumeracioTipus()), 
							destinatari.getDomiciliNumeracioNumero(), 
							destinatari.getDomiciliNumeracioPuntKm(), 
							destinatari.getDomiciliApartatCorreus(), 
							destinatari.getDomiciliBloc(), 
							destinatari.getDomiciliPortal(), 
							destinatari.getDomiciliEscala(), 
							destinatari.getDomiciliPlanta(), 
							destinatari.getDomiciliPorta(), 
							destinatari.getDomiciliComplement(), 
							destinatari.getDomiciliPoblacio(), 
							destinatari.getDomiciliMunicipiCodiIne(),
							destinatari.getDomiciliMunicipiNom(),
							destinatari.getDomiciliCodiPostal(),
							destinatari.getDomiciliProvinciaCodi(),
							destinatari.getDomiciliProvinciaNom(),
							destinatari.getDomiciliPaisCodiIso(),
							destinatari.getDomiciliPaisNom(),
							destinatari.getDomiciliLinea1(),
							destinatari.getDomiciliLinea2(),
							destinatari.getDomiciliCie(),
							destinatari.isDehObligat(),
							destinatari.getDehNif(),
							destinatari.getDehProcedimentCodi(),
							ServeiTipusEnum.toServeiTipusEnum(destinatari.getServeiTipus()),
							destinatari.getRetardPostal(),
							destinatari.getCaducitat(),
							destinatari.getNotificaIdentificador(),
							null,
							null,
							calcularEstat(destinatari)));
			result.setDestinataris(destinataris);
			return result;
		} catch (Exception ex) {
			logger.error(
					"Error al consultar la notificació (" +
					"referencia=" + referencia + ")",
					ex);
			throw new NotificacioWsServiceException(
					"Error al consultar la notificació(" +
					"referencia=" + referencia + ")",
					ex);
		}
	}

	@Override
	@Transactional
	public NotificacioEstat consultaEstat(String referencia) {
		try {
			NotificacioEntity notificacio = notificacioRepository.findByDestinatariAndReferencia(
					referencia);
			if (notificacio == null) {
				throw new NotFoundException(
						"referencia:" + referencia,
						NotificacioEnviamentEntity.class);
			}
			NotificacioEnviamentEntity destinatari = notificacioDestinatariRepository.findByNotificacioAndReferencia(
					notificacio,
					referencia);
			if (destinatari.getSeuEstat() != null) {
				switch(destinatari.getSeuEstat()) {
				case ABSENT:
					break;
				case ADRESA_INCORRECTA:
					break;
				case DESCONEGUT:
					break;
				case ENTREGADA_OP:
					break;
				case ENVIADA_CI:
					break;
				case ENVIADA_DEH:
					break;
				case ENVIAMENT_PROGRAMAT:
					break;
				case ERROR_ENTREGA:
					break;
				case EXPIRADA:
					break;
				case EXTRAVIADA:
					break;
				case LLEGIDA:
					break;
				case MORT:
					break;
				case NOTIB_ENVIADA:
					break;
				case NOTIB_PENDENT:
					break;
				case NOTIFICADA:
					break;
				case PENDENT_CIE:
					break;
				case PENDENT_DEH:
					break;
				case PENDENT_ENVIAMENT:
					break;
				case PENDENT_SEU:
					break;
				case REBUTJADA:
					break;
				case SENSE_INFORMACIO:
					break;
				default:
					break;
				}
			}
			NotificacioEstat notificacioEstat = new NotificacioEstat(
					calcularEstat(destinatari),
					destinatari.getNotificaEstatData(),
					destinatari.getNotificaEstatReceptorNom(),
					destinatari.getNotificaEstatReceptorNif(),
					destinatari.getNotificaEstatOrigen(),
					destinatari.getNotificaEstatNumSeguiment());
			return notificacioEstat;
		} catch (Exception ex) {
			logger.error(
					"Error al consultar l'estat de la notificació (" +
					"referencia=" + referencia + ")",
					ex);
			throw new NotificacioWsServiceException(
					"Error al consultar l'estat de la notificació(" +
					"referencia=" + referencia + ")",
					ex);
		}
	}

	@Override
	@Transactional
	public NotificacioCertificacio consultaCertificacio(String referencia) {
		try {
			NotificacioEnviamentEntity destinatari = notificacioDestinatariRepository.findByReferencia(
					referencia);
			if (destinatari == null) {
				throw new NotFoundException(
						"referencia:" + referencia,
						NotificacioEnviamentEntity.class);
			}
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			pluginHelper.gestioDocumentalGet(
					destinatari.getNotificaCertificacioArxiuId(),
					PluginHelper.GESDOC_AGRUPACIO_CERTIFICACIONS,
					baos);
			NotificacioCertificacio certificacio = new NotificacioCertificacio(
					CertificacioTipusEnum.toCertificacioTipusEnum(destinatari.getNotificaCertificacioTipus()), 
					CertificacioArxiuTipusEnum.toCertificacioArxiuTipusEnum(destinatari.getNotificaCertificacioArxiuTipus()), 
					new String(Base64.encode(baos.toByteArray())),
					destinatari.getNotificaCertificacioNumSeguiment(),
					destinatari.getNotificaCertificacioDataActualitzacio());
			return certificacio;
		} catch (Exception ex) {
			logger.error(
					"Error al consultar la certificació de la notificació(" +
					"referencia=" + referencia + ")",
					ex);
			throw new NotificacioWsServiceException(
					"Error al consultar la certificació de la notificació(" +
					"referencia=" + referencia + ")",
					ex);
		}
	}

	private String SHAsum(byte[] convertme) throws NoSuchAlgorithmException{
	    MessageDigest md = MessageDigest.getInstance("SHA-1"); 
	    return byteArray2Hex(md.digest(convertme));
	}
	private String byteArray2Hex(final byte[] hash) {
	    Formatter formatter = new Formatter();
	    for (byte b : hash) {
	        formatter.format("%02x", b);
	    }
	    String hex = formatter.toString();
	    formatter.close();
	    return hex;
	}

	@SuppressWarnings("incomplete-switch")
	private NotificacioDestinatariEstatEnum calcularEstat(
			NotificacioEnviamentEntity destinatari) {
		NotificacioDestinatariEstatEnumDto estatCalculatDto = NotificacioEnviamentEntity.calcularEstatNotificacioDestinatari(destinatari);
		NotificacioDestinatariEstatEnum estatCalculat = null;
		switch (estatCalculatDto) {
		case ABSENT:
			estatCalculat = NotificacioDestinatariEstatEnum.ABSENT;
			break;
		case ADRESA_INCORRECTA:
			estatCalculat = NotificacioDestinatariEstatEnum.ADRESA_INCORRECTA;
			break;
		case DESCONEGUT:
			estatCalculat = NotificacioDestinatariEstatEnum.DESCONEGUT;
			break;
		case ENTREGADA_OP:
			estatCalculat = NotificacioDestinatariEstatEnum.ENTREGADA_OP;
			break;
		case ENVIADA_CI:
			estatCalculat = NotificacioDestinatariEstatEnum.ENVIADA_CI;
			break;
		case ENVIADA_DEH:
			estatCalculat = NotificacioDestinatariEstatEnum.ENVIADA_DEH;
			break;
		case ENVIAMENT_PROGRAMAT:
			estatCalculat = NotificacioDestinatariEstatEnum.ENVIAMENT_PROGRAMAT;
			break;
		case ERROR_ENTREGA:
			estatCalculat = NotificacioDestinatariEstatEnum.ERROR_ENTREGA;
			break;
		case EXPIRADA:
			estatCalculat = NotificacioDestinatariEstatEnum.EXPIRADA;
			break;
		case EXTRAVIADA:
			estatCalculat = NotificacioDestinatariEstatEnum.EXTRAVIADA;
			break;
		case LLEGIDA:
			estatCalculat = NotificacioDestinatariEstatEnum.LLEGIDA;
			break;
		case MORT:
			estatCalculat = NotificacioDestinatariEstatEnum.MORT;
			break;
		case NOTIFICADA:
			estatCalculat = NotificacioDestinatariEstatEnum.NOTIFICADA;
			break;
		case PENDENT_CIE:
			estatCalculat = NotificacioDestinatariEstatEnum.PENDENT_CIE;
			break;
		case PENDENT_DEH:
			estatCalculat = NotificacioDestinatariEstatEnum.PENDENT_DEH;
			break;
		case PENDENT_ENVIAMENT:
			estatCalculat = NotificacioDestinatariEstatEnum.PENDENT_ENVIAMENT;
			break;
		case PENDENT_SEU:
			estatCalculat = NotificacioDestinatariEstatEnum.PENDENT_SEU;
			break;
		case REBUTJADA:
			estatCalculat = NotificacioDestinatariEstatEnum.REBUTJADA;
			break;
		case SENSE_INFORMACIO:
			estatCalculat = NotificacioDestinatariEstatEnum.SENSE_INFORMACIO;
			break;
		}
		return estatCalculat;
	}

	private static final Logger logger = LoggerFactory.getLogger(NotificacioWsServiceImpl.class);
*/
}
