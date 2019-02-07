/**
 * 
 */
package es.caib.notib.core.entity;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.notib.core.audit.NotibAuditable;


/**
 * Classe del model de dades que representa una notificació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name="not_destinatari")
@EntityListeners(AuditingEntityListener.class)
public class DestinatariEntity extends NotibAuditable<Long> {

	private static final long serialVersionUID = -405444246114162657L;


}
