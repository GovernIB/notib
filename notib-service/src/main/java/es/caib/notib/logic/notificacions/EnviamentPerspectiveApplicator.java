package es.caib.notib.logic.notificacions;

import es.caib.notib.client.domini.CieEstat;
import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.logic.base.service.BaseReadonlyResourceService;
import es.caib.notib.logic.intf.base.exception.PerspectiveApplicationException;
import es.caib.notib.logic.intf.model.NotificacioEnviamentResource;
import es.caib.notib.logic.intf.model.NotificacioResource;
import es.caib.notib.logic.intf.model.PersonaResource;
import es.caib.notib.persist.resourceentity.EntregaPostalResourceEntity;
import es.caib.notib.persist.resourceentity.NotificacioResourceEntity;
import es.caib.notib.persist.resourceentity.PersonaResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;


/**
 * Perspectiva per a emplenar els camps dels enviaments d'una remesa.
 */
@Slf4j
@RequiredArgsConstructor
public class EnviamentPerspectiveApplicator implements BaseReadonlyResourceService.PerspectiveApplicator<NotificacioResourceEntity, NotificacioResource> {

	@Override
	public void applySingle(String code, NotificacioResourceEntity entity, NotificacioResource resource) throws PerspectiveApplicationException {

		List<NotificacioEnviamentResource> enviaments = new ArrayList<>();
		resource.setEnviamentsInfo(enviaments);
		var enviamentsEntity = entity.getEnviaments();
		if (enviamentsEntity == null || enviamentsEntity.isEmpty()) {
			log.error("No es pot emplenar la prespectiva d'enviaments per la remesa amb id " + entity.getId());
			return;
		}
		NotificacioEnviamentResource enviament;
		PersonaResource persona;
		PersonaResourceEntity personaEntity;
		var notificat = false;
		EnviamentEstat notificaEstat;
		EntregaPostalResourceEntity entregaPostal;
		for (var env : enviamentsEntity) {
			notificaEstat = env.getNotificaEstat();
			entregaPostal = env.getEntregaPostal();
			notificat = EnviamentEstat.NOTIFICADA.equals(notificaEstat) || entregaPostal != null && CieEstat.NOTIFICADA.name().equals(entregaPostal.getCieEstat());;
			persona = crearPersona(env.getTitular());
			enviament = NotificacioEnviamentResource.builder()
//				.estat() //TODO MIRAR QUIN VALOR HA D'ANAR
				.titularInfo(persona)
				.registreNumeroFormatat(env.getRegistreNumeroFormatat())
				.registreData(env.getRegistreData())
				.registreEstat(env.getRegistreEstat())
				.registreNumeroFormatat(env.getRegistreNumeroFormatat())
				.registreData(env.getRegistreData())
				.registreEstat(env.getRegistreEstat())
				.registreMotiu(env.getRegistreMotiu())
				.sirRecepcioData(env.getSirRecepcioData())
				.sirRegDestiData(env.getSirRegDestiData())
				.notificaEstat(env.getNotificaEstat())
				.notificat(notificat)
//				.registreOficinaNom		TODO
//				.registreLlibreNom		TODO
				.notificaReferencia(env.getNotificaReferencia())
				.notificaCertificacioData(env.getNotificaCertificacioData())
				.notificaCertificacioMime(env.getNotificaCertificacioMime())
				.notificaCertificacioOrigen(env.getNotificaCertificacioOrigen())
				.notificaCertificacioMetadades(env.getNotificaCertificacioMetadades())
				.notificaCertificacioCsv(env.getNotificaCertificacioCsv())
				.notificaCertificacioTipus(env.getNotificaCertificacioTipus())
				.notificaCertificacioArxiuTipus(env.getNotificaCertificacioArxiuTipus())
				.notificaCertificacioNumSeguiment(env.getNotificaCertificacioNumSeguiment())
				.build();
			enviament.setId(env.getId());
			enviaments.add(enviament);

			var representantsEntity = env.getDestinataris();
			if (representantsEntity == null || representantsEntity.isEmpty()) {
				continue;
			}
			List<PersonaResource> representants = new ArrayList<>();
			for (var representant : representantsEntity) {
				representants.add(crearPersona(representant));
			}
			enviament.setRepresentantsInfo(representants);
		}
	}

	private PersonaResource crearPersona(PersonaResourceEntity entity) {
		return PersonaResource.builder()
			.nom(entity.getNom())
			.llinatge1(entity.getLlinatge1())
			.llinatge2(entity.getLlinatge2())
			.nif(entity.getNif())
			.email(entity.getEmail())
			.build();
	}
}
