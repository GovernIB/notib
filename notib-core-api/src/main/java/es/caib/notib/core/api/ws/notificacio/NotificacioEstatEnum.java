/**
 * 
 */
package es.caib.notib.core.api.ws.notificacio;

import java.io.Serializable;

import es.caib.notib.core.api.dto.NotificaEstatEnumDto;

/**
 * Enumerat que indica l'estat d'una notificació a dins Notific@.
 * 
 * Los estados por los que puede pasar un envío son los siguientes:
 *  - Ausente. (sólo notificaciones)
 *  - Desconocido. (sólo notificaciones)
 *  - Dirección incorrecta. (sólo notificaciones)
 *  - Editando: Cuando se encuentra aún pendiente de envío, con lo que se
 *  podrá modificar la información antes de proceder a su posterior envío.
 *  - Enviado al centro de impresión.
 *  - Enviado a la Dirección electrónica habilitada.
 *  - Leída (sólo comunicaciones leídas en Carpeta Ciudadana o Sede
 *  Electrónica).
 *  - Error en el envío. Indica que se ha producido un error en el Agente
 *  Colaborador (por ejemplo, formato A4 del PDF incorrecto). Es un
 *  estado final, por lo que no se reintentará el envío. Debe tratarse como un
 *  error. Se puede investigar qué ha ocurrido observando el detalle del
 *  envío a través de la Aplicación Web.
 *  - Extraviada. (sólo notificaciones)
 *  - Fallecido: cuando el destinatario de la notificación o comunicación ha
 *  fallecido. (sólo notificaciones)
 *  - Notificada. (sólo notificaciones)
 *  - Pendiente de envío.
 *  - Pendiente de comparecencia: Cuando existe un número de días naturales
 *  que estará disponible el envío para su comparecencia desde la sede
 *  electrónica del Punto de Acceso General (Carpeta Ciudadana) antes
 *  de enviar a otro medio alternativo de entrega.
 *  - Rehusada: Cuando la comunicación o notificación es rechazada por el
 *  interesado. También se dará este estado cuando después de los intentos
 *  de entrega estipulados por ley, no comparece el interesado. (sólo
 *  notificaciones)
 *  - Fecha envío programado: Cuando la comunicación o notificación se
 *  encuentra en espera de ser enviada en la fecha indicada por el usuario.
 *  - Sin información.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public enum NotificacioEstatEnum implements Serializable {
	AUSENT,
	DESCONEGUT,//
	ADRESA_INCORRECTA,
	EDITANT,
	ENVIADA_CENTRE_IMPRESSIO,
	ENVIADA_DEH,
	LLEGIDA,
	ERROR_ENVIAMENT,
	EXTRAVIADA,
	MORT,
	NOTIFICADA,//
	PENDENT_ENVIAMENT,
	PENDENT_COMPAREIXENSA,
	REBUTJADA,//
	DATA_ENVIAMENT_PROGRAMAT,
	SENSE_INFORMACIO;
	
	
	public NotificaEstatEnumDto toNotificaEstatEnumDto() {
		
		switch( this ) {
			case AUSENT: return NotificaEstatEnumDto.AUSENTE;
			case DESCONEGUT: return  NotificaEstatEnumDto.DESCONOCIDO;
			case ADRESA_INCORRECTA: return NotificaEstatEnumDto.DIRECCION_INCORRECTA;
			case EDITANT: return NotificaEstatEnumDto.EDITANDO;
			case ENVIADA_CENTRE_IMPRESSIO: return NotificaEstatEnumDto.ENVIADO_CENTRO_IMPRESION;
			case ENVIADA_DEH: return NotificaEstatEnumDto.ENVIADO_DEH;
			case LLEGIDA: return NotificaEstatEnumDto.LEIDA;
			case ERROR_ENVIAMENT: return NotificaEstatEnumDto.ERROR_EN_ENVIO;
			case EXTRAVIADA: return NotificaEstatEnumDto.EXTRAVIADA;
			case MORT: return NotificaEstatEnumDto.FALLECIDO;
			case NOTIFICADA: return NotificaEstatEnumDto.NOTIFICADA;
			case PENDENT_ENVIAMENT: return NotificaEstatEnumDto.PENDIENTE_ENVIO;
			case PENDENT_COMPAREIXENSA: return NotificaEstatEnumDto.PENDIENTE_COMPARECENCIA;
			case REBUTJADA: return NotificaEstatEnumDto.REHUSADA;
			case DATA_ENVIAMENT_PROGRAMAT: return NotificaEstatEnumDto.FECHA_ENVIO_PROGRAMADO;
			case SENSE_INFORMACIO: return NotificaEstatEnumDto.SIN_INFORMACION;
		}
		
		return null;
	}
	
	public static NotificacioEstatEnum toNotificacioEstatEnum(NotificaEstatEnumDto dto) {
		
		switch( dto ) {
			case AUSENTE: return AUSENT;
			case DESCONOCIDO: return DESCONEGUT;
			case DIRECCION_INCORRECTA: return ADRESA_INCORRECTA;
			case EDITANDO: return EDITANT;
			case ENVIADO_CENTRO_IMPRESION: return ENVIADA_CENTRE_IMPRESSIO;
			case ENVIADO_DEH: return ENVIADA_DEH;
			case LEIDA: return LLEGIDA;
			case ERROR_EN_ENVIO: return ERROR_ENVIAMENT;
			case EXTRAVIADA: return EXTRAVIADA;
			case FALLECIDO: return MORT;
			case NOTIFICADA: return NOTIFICADA;
			case PENDIENTE_ENVIO: return PENDENT_ENVIAMENT;
			case PENDIENTE_COMPARECENCIA: return PENDENT_COMPAREIXENSA;
			case REHUSADA: return REBUTJADA;
			case FECHA_ENVIO_PROGRAMADO: return DATA_ENVIAMENT_PROGRAMAT;
			case SIN_INFORMACION: return SENSE_INFORMACIO;
		}
		
		return null;
	}
	
}
