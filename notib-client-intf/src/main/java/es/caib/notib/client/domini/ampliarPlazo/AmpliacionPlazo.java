
package es.caib.notib.client.domini.ampliarPlazo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.datatype.XMLGregorianCalendar;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmpliacionPlazo {

    protected String estado;
    protected String identificador;
    protected String fechaCaducidad;
    protected String mensajeError;
    protected String codigo;

}
