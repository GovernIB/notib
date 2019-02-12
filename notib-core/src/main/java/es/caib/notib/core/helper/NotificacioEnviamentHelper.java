package es.caib.notib.core.helper;

import java.util.ArrayList;
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
	
	
	
	public NotificacioEnviamentDtoV2 toNotificacioEnviamentDtoV2(
			NotificacioEntity notificacio,
			Page<NotificacioEnviamentEntity> enviamentsPage) {
		
		NotificacioEnviamentDtoV2 enviaments = null;
		
		if (!enviamentsPage.getContent().isEmpty()) {
			enviaments = new NotificacioEnviamentDtoV2();
			
			for (NotificacioEnviamentEntity enviament : enviamentsPage.getContent()) {
				enviaments.setId(enviament.getId());
				enviaments.setCreatedDate(enviament.getCreatedDate().toDate());
				enviaments.setUsuari(enviament.getCreatedBy().getCodi());
				enviaments.setNotificacio(
						conversioTipusHelper.convertir(
						notificacio, 
						NotificacioDtoV2.class));
				
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
				enviaments.setEntregaDeh(entregaDehDto);
				
				EntregaPostalDto entregaPostalDto = new EntregaPostalDto();
				entregaPostalDto.setTipus(enviament.getDomiciliNumeracioTipus());
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
				entregaPostalDto.setTipus(enviament.getDomiciliNumeracioTipus());
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
