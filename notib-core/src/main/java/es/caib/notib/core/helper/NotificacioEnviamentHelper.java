package es.caib.notib.core.helper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import es.caib.notib.core.api.dto.EntregaDehDto;
import es.caib.notib.core.api.dto.EntregaPostalDto;
import es.caib.notib.core.api.dto.NotificacioDto;
import es.caib.notib.core.api.dto.NotificacioDtoV2;
import es.caib.notib.core.api.dto.NotificacioEnviamentDto;
import es.caib.notib.core.api.dto.NotificacioEnviamentDtoV2;
import es.caib.notib.core.api.dto.PersonaDto;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.entity.PersonaEntity;


/**
 * Helper per a convertir notificació entity i enviament entity a notificacioEnviamentDto.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class NotificacioEnviamentHelper {

	@Resource
	private ConversioTipusHelper conversioTipusHelper;
	
	
	
	public List<NotificacioEnviamentDtoV2> toNotificacioEnviamentDtoV2(
			NotificacioEntity notificacio,
			Page<NotificacioEnviamentEntity> enviamentsPage) {
		
		List<NotificacioEnviamentDtoV2> enviaments = null;
		
		if (!enviamentsPage.getContent().isEmpty()) {
			enviaments = new ArrayList<NotificacioEnviamentDtoV2>();
			
			for (NotificacioEnviamentEntity enviament : enviamentsPage.getContent()) {
				NotificacioEnviamentDtoV2 env = new NotificacioEnviamentDtoV2();
				env.setId(enviament.getId());
				env.setCreatedDate(enviament.getCreatedDate().toDate());
				env.setUsuari(enviament.getCreatedBy().getCodi());
				env.setNotificacio(
						conversioTipusHelper.convertir(
						notificacio, 
						NotificacioDtoV2.class));
				env.setNotificaIdentificador(enviament.getNotificaIdentificador());
				
				env.setTitular(
						conversioTipusHelper.convertir(
						enviament.getTitular(), 
						PersonaDto.class));
				
				List<PersonaDto> destinataris = new ArrayList<PersonaDto>();
				for(PersonaEntity persona : enviament.getDestinataris()) {
					destinataris.add(conversioTipusHelper.convertir(persona, PersonaDto.class));
				}
				env.setDestinataris(destinataris);
				EntregaDehDto entregaDehDto = new EntregaDehDto();
				entregaDehDto.setObligat(enviament.getDehObligat());
				env.setEntregaDeh(entregaDehDto);
				
				EntregaPostalDto entregaPostalDto = new EntregaPostalDto();
				entregaPostalDto.setTipus(enviament.getDomiciliConcretTipus());
				entregaPostalDto.setTipusVia(enviament.getDomiciliViaTipus());
				entregaPostalDto.setViaNom(enviament.getDomiciliViaNom());
				entregaPostalDto.setNumeroCasa(enviament.getDomiciliNumeracioNumero());
				entregaPostalDto.setNumeroQualificador(enviament.getDomiciliNumeracioQualificador());
				entregaPostalDto.setPuntKm(enviament.getDomiciliNumeracioPuntKm());
				entregaPostalDto.setApartatCorreus(enviament.getDomiciliApartatCorreus());
				entregaPostalDto.setPortal(enviament.getDomiciliPortal());
				entregaPostalDto.setEscala(enviament.getDomiciliEscala());
				entregaPostalDto.setPlanta(enviament.getDomiciliPlanta());
				entregaPostalDto.setPorta(enviament.getDomiciliPorta());
				entregaPostalDto.setBloc(enviament.getDomiciliBloc());
				entregaPostalDto.setComplement(enviament.getDomiciliComplement());
				entregaPostalDto.setCodiPostal(enviament.getDomiciliCodiPostal());
				entregaPostalDto.setPoblacio(enviament.getDomiciliPoblacio());
				entregaPostalDto.setMunicipiCodi(enviament.getDomiciliMunicipiCodiIne());
				entregaPostalDto.setProvinciaCodi(enviament.getDomiciliProvinciaCodi());
				entregaPostalDto.setPaisCodi(enviament.getDomiciliPaisCodiIso());
				entregaPostalDto.setLinea1(enviament.getDomiciliLinea1());
				entregaPostalDto.setLinea2(enviament.getDomiciliLinea2());
				entregaPostalDto.setCie(enviament.getDomiciliCie());
				entregaPostalDto.setFormatSobre(enviament.getFormatSobre());
				entregaPostalDto.setFormatFulla(enviament.getFormatFulla());
				env.setEntregaPostal(entregaPostalDto);
				/*Revisar*/
				env.setNumeroCertCorreus(enviament.getNotificaCertificacioNumSeguiment());
				env.getNotificacio().setCsv_uuid(notificacio.getDocument().getArxiuGestdocId());
				
				for(PersonaEntity destinatari : enviament.getDestinataris()) {
					env.setDestinatarisNomLlinatges("[" + destinatari.getLlinatge1() + " " + destinatari.getLlinatge1() + ", " + destinatari.getNom() + "] \n");
				}
				
				env.setServeiTipus(enviament.getServeiTipus());
				env.setTitularEmail(enviament.getTitular().getEmail());
				env.setReferencia(enviament.getNotificaReferencia());
				env.setTitularNif(enviament.getTitular().getNif());
				enviaments.add(env);
			}
		}
		return enviaments;
	}
	
	public NotificacioEnviamentDto toNotificacioEnviamentDto(
			NotificacioEntity notificacio,
			Page<NotificacioEnviamentEntity> enviamentsPage) {
		
		NotificacioEnviamentDto enviaments = null;
		
		if (!enviamentsPage.getContent().isEmpty()) {
			enviaments = new NotificacioEnviamentDto();
			
			for (NotificacioEnviamentEntity enviament : enviamentsPage.getContent()) {
				enviaments.setId(enviament.getId());
				enviaments.setCreatedDate(enviament.getCreatedDate().toDate());
				enviaments.setUsuari(enviament.getCreatedBy().getCodi());
				enviaments.setNotificacio(
						conversioTipusHelper.convertir(
								notificacio, 
								NotificacioDto.class));
				
				enviaments.setTitular(
						conversioTipusHelper.convertir(
						enviament.getTitular(), 
						PersonaDto.class));
				
				List<PersonaDto> destinataris = new ArrayList<PersonaDto>();
				for(PersonaEntity persona : enviament.getDestinataris()) {
					destinataris.add(conversioTipusHelper.convertir(persona, PersonaDto.class));
				}
				enviaments.setDestinataris(destinataris);
				EntregaDehDto entregaDehDto = new EntregaDehDto();
				entregaDehDto.setObligat(enviament.getDehObligat());
				entregaDehDto.setProcedimentCodi(enviament.getDehProcedimentCodi());
				enviaments.setEntregaDeh(entregaDehDto);
				
				EntregaPostalDto entregaPostalDto = new EntregaPostalDto();
				entregaPostalDto.setTipus(enviament.getDomiciliConcretTipus());
				entregaPostalDto.setTipusVia(enviament.getDomiciliViaTipus());
				entregaPostalDto.setViaNom(enviament.getDomiciliViaNom());
				entregaPostalDto.setNumeroCasa(enviament.getDomiciliNumeracioNumero());
				entregaPostalDto.setNumeroQualificador(enviament.getDomiciliNumeracioQualificador());
				entregaPostalDto.setPuntKm(enviament.getDomiciliNumeracioPuntKm());
				entregaPostalDto.setApartatCorreus(enviament.getDomiciliApartatCorreus());
				entregaPostalDto.setPortal(enviament.getDomiciliPortal());
				entregaPostalDto.setEscala(enviament.getDomiciliEscala());
				entregaPostalDto.setPlanta(enviament.getDomiciliPlanta());
				entregaPostalDto.setPorta(enviament.getDomiciliPorta());
				entregaPostalDto.setBloc(enviament.getDomiciliBloc());
				entregaPostalDto.setComplement(enviament.getDomiciliComplement());
				entregaPostalDto.setCodiPostal(enviament.getDomiciliCodiPostal());
				entregaPostalDto.setPoblacio(enviament.getDomiciliPoblacio());
				entregaPostalDto.setMunicipiCodi(enviament.getDomiciliMunicipiCodiIne());
				entregaPostalDto.setProvinciaCodi(enviament.getDomiciliProvinciaCodi());
				entregaPostalDto.setPaisCodi(enviament.getDomiciliPaisCodiIso());
				entregaPostalDto.setLinea1(enviament.getDomiciliLinea1());
				entregaPostalDto.setLinea2(enviament.getDomiciliLinea2());
				entregaPostalDto.setCie(enviament.getDomiciliCie());
				entregaPostalDto.setFormatSobre(enviament.getFormatSobre());
				entregaPostalDto.setFormatFulla(enviament.getFormatFulla());
				enviaments.setEntregaPostal(entregaPostalDto);
				
				enviaments.setServeiTipus(enviament.getServeiTipus());
				
				enviaments.setReferencia(enviament.getNotificaReferencia());
			}
		}
		return enviaments;
	}
}
