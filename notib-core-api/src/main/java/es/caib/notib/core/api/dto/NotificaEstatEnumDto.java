/**
 * 
 */
package es.caib.notib.core.api.dto;

import java.io.Serializable;

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
public enum NotificaEstatEnumDto implements Serializable {

	AUSENTE(""),
	DESCONOCIDO(""),
	DIRECCION_INCORRECTA(""),
	EDITANDO(""),
	ENVIADO_CENTRO_IMPRESION(""),
	ENVIADO_DEH(""),
	LEIDA(""),
	ERROR_EN_ENVIO(""),
	EXTRAVIADA(""),
	FALLECIDO(""),
	NOTIFICADA(""),
	PENDIENTE_ENVIO(""),
	PENDIENTE_COMPARECENCIA(""),
	REHUSADA(""),
	FECHA_ENVIO_PROGRAMADO(""),
	SIN_INFORMACION("");

	private final String text;

	NotificaEstatEnumDto(String text) {
		this.text = text;
		
	}
	public String getText() {
		return text;
	}
	public static NotificaEstatEnumDto toEnum(String text) {
        if (text == null)
            return null;
         for (NotificaEstatEnumDto valor : NotificaEstatEnumDto.values()) {
            if (text.equals(valor.getText())) {
                return valor;
            }
        }
        throw new IllegalArgumentException("No s'ha trobat cap correspondència a dins l'enumeració " + NotificaEnviamentTipusEnumDto.class.getName() + " per al text " + text);
    }

}
