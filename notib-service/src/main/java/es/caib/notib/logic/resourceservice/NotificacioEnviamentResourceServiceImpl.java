package es.caib.notib.logic.resourceservice;

import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.base.service.BaseMutableResourceService;
import es.caib.notib.logic.helper.NotibPermissionHelper;
import es.caib.notib.logic.helper.UserSessionHelper;
import es.caib.notib.logic.intf.EntregaPostalResource;
import es.caib.notib.logic.intf.base.config.BaseConfig;
import es.caib.notib.logic.intf.base.exception.AnswerRequiredException;
import es.caib.notib.logic.intf.base.exception.PerspectiveApplicationException;
import es.caib.notib.logic.intf.base.exception.ResourceNotCreatedException;
import es.caib.notib.logic.intf.base.model.ResourceReference;
import es.caib.notib.logic.intf.model.EntitatResource;
import es.caib.notib.logic.intf.model.NotificacioEnviamentResource;
import es.caib.notib.logic.intf.model.PersonaResource;
import es.caib.notib.logic.intf.resourceservice.NotificacioEnviamentResourceService;
import es.caib.notib.persist.resourceentity.EntitatResourceEntity;
import es.caib.notib.persist.resourceentity.NotificacioEnviamentResourceEntity;
import es.caib.notib.persist.resourceentity.OrganGestorResourceEntity;
import es.caib.notib.persist.resourceentity.ProcedimentResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * Implementació del servei de gestió d'enviaments d'una notificació.
 * Aquest servei només s'ha implementat perquè feia falta per a poder consultar els fields d'aquest recurs al formulari
 * del front.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
public class NotificacioEnviamentResourceServiceImpl
	extends BaseMutableResourceService<NotificacioEnviamentResource, Long, NotificacioEnviamentResourceEntity>
	implements NotificacioEnviamentResourceService {


	@PostConstruct
	public void init() {
		register(NotificacioEnviamentResource.PERSPECTIVE_TITULAR, new NotificacioEnviamentResourceTitularPerspectiveApplicator());
		register(NotificacioEnviamentResource.PERSPECTIVE_ENTREGA_POSTAL, new NotificacioEnviamentResourceEntregaPostalPerspectiveApplicator());
	}

	/*
	 * Com que aquest servei no s'ha d'utilitzar més que per a consultar els fields feim que no es retorni mai cap
	 * resultat.
	 */
	@Override
	protected String additionalSpringFilter(
		String currentSpringFilter,
		String[] namedQueries) {
		//return "id is null";
		return null;
	}

	/*
	 * Com que aquest servei no s'ha d'utilitzar més que per a consultar els fields feim que si s'intenta crear un
	 * recurs llençam una excepció.
	 */
	@Override
	protected void beforeCreateSave(
		NotificacioEnviamentResourceEntity entity,
		NotificacioEnviamentResource resource,
		Map<String, AnswerRequiredException.AnswerValue> answers) {
		throw new ResourceNotCreatedException(getResourceClass(), "Create is not allowed");
	}

	@Override
	protected void afterConversion(NotificacioEnviamentResourceEntity entity, NotificacioEnviamentResource resource) {

		var organGestor = entity.getNotificacio().getOrganGestor();
		var procediment = entity.getNotificacio().getProcediment();
		resource.setNotificacioOrganGestor(ResourceReference.toResourceReference(organGestor.getId(), organGestor.getCodiNom()));
		resource.setNotificacioProcediment(ResourceReference.toResourceReference(procediment.getId(), procediment.getNom()));
		resource.setReferenciaNotificacio(entity.getNotificacio().getReferencia());
		var titular = entity.getTitular();
		resource.setTitular(ResourceReference.toResourceReference(titular.getId(), titular.getNomSencerNif()));
	}

	/**
	 * Perspectiva per a emplenar els camps del titular d'un enviament.
	 */
	@RequiredArgsConstructor
	public static class NotificacioEnviamentResourceTitularPerspectiveApplicator implements PerspectiveApplicator<NotificacioEnviamentResourceEntity, NotificacioEnviamentResource> {

		@Override
		public void applySingle(String code, NotificacioEnviamentResourceEntity entity, NotificacioEnviamentResource resource) throws PerspectiveApplicationException {

			var titularEntity = entity.getTitular();
			var titularInfo = resource.getTitularInfo();
			if (titularInfo == null) {
				titularInfo = new PersonaResource();
			}
			titularInfo.setNif(titularEntity.getNif());
			titularInfo.setNom(titularEntity.getNom());
			titularInfo.setEmail(titularEntity.getEmail());
			titularInfo.setTelefon(titularEntity.getTelefon());
			titularInfo.setLlinatge1(titularEntity.getLlinatge1());
			titularInfo.setLlinatge2(titularEntity.getLlinatge2());
			resource.setTitularInfo(titularInfo);
		}
	}

	/**
	 * Perspectiva per a emplenar els camps de la entrega postal d'un enviament.
	 */
	@RequiredArgsConstructor
	public static class NotificacioEnviamentResourceEntregaPostalPerspectiveApplicator implements PerspectiveApplicator<NotificacioEnviamentResourceEntity, NotificacioEnviamentResource> {

		@Override
		public void applySingle(String code, NotificacioEnviamentResourceEntity entity, NotificacioEnviamentResource resource) throws PerspectiveApplicationException {

			var entregaPostalEntity = entity.getEntregaPostal();
			if (entregaPostalEntity == null) {
				return;
			}
			var entregaPostalInfo = resource.getEntregaPostalInfo();
			if (entregaPostalInfo == null) {
				entregaPostalInfo = new EntregaPostalResource();
			}
			entregaPostalInfo.setCieId(entregaPostalEntity.getCieId());
			entregaPostalInfo.setCieEstat(entregaPostalEntity.getCieEstat());
			entregaPostalInfo.setCieEstatData(entregaPostalEntity.getCieEstatData());
			entregaPostalInfo.setCieDatatOrigen(entregaPostalEntity.getCieDatatOrigen());
			entregaPostalInfo.setCieDatatReceptorNif(entregaPostalEntity.getCieDatatReceptorNif());
			entregaPostalInfo.setCieDatatReceptorNom(entregaPostalEntity.getCieDatatReceptorNom());
			entregaPostalInfo.setCieDatatNumSeguiment(entregaPostalEntity.getCieDatatNumSeguiment());
			entregaPostalInfo.setCieDatatErrorDescripcio(entregaPostalEntity.getCieDatatErrorDescripcio());
			entregaPostalInfo.setCieCertificacioData(entregaPostalEntity.getCieCertificacioData());
			entregaPostalInfo.setCieCertificacioMime(entregaPostalEntity.getCieCertificacioMime());
			entregaPostalInfo.setCieCertificacioOrigen(entregaPostalEntity.getCieCertificacioOrigen());
			entregaPostalInfo.setCieCertificacioMetadades(entregaPostalEntity.getCieCertificacioMetadades());
			entregaPostalInfo.setCieCertificacioCsv(entregaPostalEntity.getCieCertificacioCsv());
			entregaPostalInfo.setCieCertificacioTipus(entregaPostalEntity.getCieCertificacioTipus());
			entregaPostalInfo.setCieCertificacioArxiuTipus(entregaPostalEntity.getCieCertificacioArxiuTipus());
			entregaPostalInfo.setCieCertificacioNumSeguiment(entregaPostalEntity.getCieCertificacioNumSeguiment());
			entregaPostalInfo.setCieCertificacioArxiuNom(entregaPostalEntity.getCieCertificacioArxiuNom());
			resource.setEntregaPostalInfo(entregaPostalInfo);
		}
	}
}
