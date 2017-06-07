/**
 * 
 */
package es.caib.notib.core.service.ws;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sun.jersey.core.util.Base64;

import es.caib.notib.core.api.ws.notificacio.CertificacioArxiuTipusEnum;
import es.caib.notib.core.api.ws.notificacio.CertificacioTipusEnum;
import es.caib.notib.core.api.ws.notificacio.DomiciliConcretTipusEnum;
import es.caib.notib.core.api.ws.notificacio.DomiciliNumeracioTipusEnum;
import es.caib.notib.core.api.ws.notificacio.DomiciliTipusEnum;
import es.caib.notib.core.api.ws.notificacio.Notificacio;
import es.caib.notib.core.api.ws.notificacio.NotificacioCertificacio;
import es.caib.notib.core.api.ws.notificacio.NotificacioDestinatari;
import es.caib.notib.core.api.ws.notificacio.NotificacioEstat;
import es.caib.notib.core.api.ws.notificacio.NotificacioWsService;
import es.caib.notib.core.api.ws.notificacio.NotificacioWsServiceException;
import es.caib.notib.core.api.ws.notificacio.ServeiTipusEnum;
import es.caib.notib.core.entity.EntitatEntity;
import es.caib.notib.core.entity.NotificacioDestinatariEntity;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.helper.EntityComprovarHelper;
import es.caib.notib.core.helper.NotificaHelper;
import es.caib.notib.core.helper.PluginHelper;
import es.caib.notib.core.repository.EntitatRepository;
import es.caib.notib.core.repository.NotificacioDestinatariRepository;
import es.caib.notib.core.repository.NotificacioRepository;


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
public class NotificacioWsServiceImpl implements NotificacioWsService {

	@Autowired
	private EntitatRepository entitatRepository;
	@Autowired
	private NotificacioRepository notificacioRepository;
	@Autowired
	private NotificacioDestinatariRepository notificacioDestinatariRepository;

	@Autowired
	private EntityComprovarHelper entityComprovarHelper;
	@Autowired
	private NotificaHelper notificaHelper;
	@Autowired
	private PluginHelper pluginHelper;



	@Override
	@Transactional
	public List<String> alta(Notificacio notificacio) {
		try {
			EntitatEntity entitat = entitatRepository.findByCif(notificacio.getCifEntitat());
			entityComprovarHelper.comprovarPermisosAplicacio(entitat.getId());
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
					notificacio.getDocumentSha1(),
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
			notificacioRepository.save(notificacioEntity);
			// TODO decidir si es fa l'enviament immediatament o si s'espera
			// a que l'envii la tasca programada.
			//notificaHelper.intentarEnviament(notificacioEntity);
			List<NotificacioDestinatariEntity> destinatarislist = new ArrayList();
			List<String> result = new ArrayList<>();
			for (NotificacioDestinatari d: notificacio.getDestinataris()) {
				NotificacioDestinatariEntity.Builder destinatari = NotificacioDestinatariEntity.getBuilder(
						d.getTitularNom(),
						d.getTitularNif(),
						d.getDestinatariNom(),
						d.getDestinatariNif(),
						d.getServeiTipus().toServeiTipusEnumDto(),
						d.getSeuEstat(),
						d.isDehObligat(),
						notificacioEntity);
				destinatari.titularLlinatges( d.getTitularLlinatges() );
				destinatari.titularTelefon( d.getTitularTelefon() );
				destinatari.titularEmail( d.getTitularEmail() );
				destinatari.destinatariLlinatges( d.getDestinatariLlinatges() );
				destinatari.destinatariTelefon( d.getDestinatariTelefon() );
				destinatari.destinatariEmail( d.getDestinatariEmail() );
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
				destinatari.dehObligat( d.isDehObligat() );
				destinatari.dehNif( d.getDehNif() );
				destinatari.dehProcedimentCodi( d.getDehProcedimentCodi() );
				destinatari.retardPostal( d.getRetardPostal() );
				destinatari.caducitat( d.getCaducitat() );
				NotificacioDestinatariEntity entity = destinatari.build();
				entity = notificacioDestinatariRepository.save(entity);
				String referencia = notificaHelper.generarReferencia(entity);
				entity.updateReferencia(referencia);
				result.add(referencia);
				destinatarislist.add(entity);
			}
			notificacioEntity.updateDestinataris(destinatarislist);
			return result;
		} catch (Exception ex) {
			throw new NotificacioWsServiceException(
					"Error al donar d'alta la notificació",
					ex);
		}
	}

	@Override
	@Transactional
	public Notificacio consulta(String referencia) {
		try {
			NotificacioEntity notificacio = notificacioRepository.findByDestinatariReferencia(
					referencia);
			entityComprovarHelper.comprovarPermisosAplicacio(notificacio.getEntitat().getId());
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			pluginHelper.gestioDocumentalGet(
					notificacio.getDocumentArxiuId(),
					PluginHelper.GESDOC_AGRUPACIO_NOTIFICACIONS,
					baos);
			List<NotificacioDestinatari> destinataris = new ArrayList<>();
			for (NotificacioDestinatariEntity d: notificacio.getDestinataris()) {
				destinataris.add(new NotificacioDestinatari(
						d.getReferencia(), 
						d.getTitularNom(), 
						d.getTitularLlinatges(), 
						d.getTitularNif(), 
						d.getTitularTelefon(), 
						d.getTitularEmail(), 
						d.getDestinatariNom(), 
						d.getDestinatariLlinatges(), 
						d.getDestinatariNif(), 
						d.getDestinatariTelefon(), 
						d.getDestinatariEmail(), 
						DomiciliTipusEnum.toDomiciliTipusEnum(d.getDomiciliTipus()), 
						DomiciliConcretTipusEnum.toDomiciliConcretTipusEnum(d.getDomiciliConcretTipus()), 
						d.getDomiciliViaTipus(), 
						d.getDomiciliViaNom(), 
						DomiciliNumeracioTipusEnum.toDomiciliNumeracioTipusEnum(d.getDomiciliNumeracioTipus()), 
						d.getDomiciliNumeracioNumero(), 
						d.getDomiciliNumeracioPuntKm(), 
						d.getDomiciliApartatCorreus(), 
						d.getDomiciliBloc(), 
						d.getDomiciliPortal(), 
						d.getDomiciliEscala(), 
						d.getDomiciliPlanta(), 
						d.getDomiciliPorta(), 
						d.getDomiciliComplement(), 
						d.getDomiciliPoblacio(), 
						d.getDomiciliMunicipiCodiIne(),
						d.getDomiciliMunicipiNom(),
						d.getDomiciliCodiPostal(),
						d.getDomiciliProvinciaCodi(),
						d.getDomiciliProvinciaNom(),
						d.getDomiciliPaisCodiIso(),
						d.getDomiciliPaisNom(),
						d.getDomiciliLinea1(),
						d.getDomiciliLinea2(),
						d.getDomiciliCie(),
						d.isDehObligat(),
						d.getDehNif(),
						d.getDehProcedimentCodi(),
						ServeiTipusEnum.toServeiTipusEnum(d.getServeiTipus()),
						d.getRetardPostal(),
						d.getCaducitat(),
						d.getNotificaIdentificador(),
						d.getSeuRegistreNumero(),
						d.getSeuRegistreData(),
						d.getSeuEstat() )
						);
			}
			Notificacio result = new Notificacio(
					notificacio.getEntitat().getCif(),
					notificacio.getEnviamentTipus(),
					notificacio.getEnviamentDataProgramada(),
					notificacio.getConcepte(),
					notificacio.getPagadorCorreusCodiDir3(),
					notificacio.getPagadorCorreusContracteNum(),
					notificacio.getPagadorCorreusCodiClientFacturacio(),
					notificacio.getPagadorCorreusDataVigencia(),
					notificacio.getPagadorCieCodiDir3(),
					notificacio.getPagadorCieDataVigencia(),
					notificacio.getProcedimentCodiSia(),
					notificacio.getProcedimentDescripcioSia(),
					notificacio.getDocumentArxiuNom(),
					new String(Base64.encode(baos.toByteArray())),
					notificacio.getDocumentSha1(),
					notificacio.isDocumentNormalitzat(),
					notificacio.isDocumentGenerarCsv(),
					notificacio.getSeuExpedientSerieDocumental(),
					notificacio.getSeuExpedientUnitatOrganitzativa(),
					notificacio.getSeuExpedientIdentificadorEni(),
					notificacio.getSeuExpedientTitol(),
					notificacio.getSeuRegistreOficina(),
					notificacio.getSeuRegistreLlibre(),
					notificacio.getSeuIdioma(),
					notificacio.getSeuAvisTitol(),
					notificacio.getSeuAvisText(),
					notificacio.getSeuAvisTextMobil(),
					notificacio.getSeuOficiTitol(),
					notificacio.getSeuOficiText(),
					notificacio.getEstat(),
					destinataris);
			return result;
		} catch (Exception ex) {
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
			NotificacioEntity notificacio = notificacioRepository.findByDestinatariReferencia( referencia );
			entityComprovarHelper.comprovarPermisosAplicacio(notificacio.getEntitat().getId());
			NotificacioDestinatariEntity destinatari = notificacioDestinatariRepository.findByReferencia(referencia);
			NotificacioEstat notificacioEstat = new NotificacioEstat(
					destinatari.getEstatUnificat(),
					destinatari.getNotificaEstatData(),
					destinatari.getDestinatariNom(),//receptorNom,
					destinatari.getDestinatariNif(),//receptorNif,
					destinatari.getNotificaEstatOrigen(),//origen,
					destinatari.getNotificaCertificacioNumSeguiment());//numSeguiment
			return notificacioEstat;
		} catch (Exception ex) {
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
			NotificacioDestinatariEntity destinatari = notificacioDestinatariRepository.findByReferencia(referencia);
			entityComprovarHelper.comprovarPermisosAplicacio(destinatari.getNotificacio().getEntitat().getId());
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
			throw new NotificacioWsServiceException(
					"Error al consultar la certificació de la notificació(" +
					"referencia=" + referencia + ")",
					ex);
		}
	}

}
