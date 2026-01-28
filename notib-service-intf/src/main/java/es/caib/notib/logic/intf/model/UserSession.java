package es.caib.notib.logic.intf.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class UserSession implements Serializable {

	private Long entitatId;
	private Long organGestorId;

}
