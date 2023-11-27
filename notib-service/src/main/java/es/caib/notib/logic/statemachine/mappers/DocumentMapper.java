package es.caib.notib.logic.statemachine.mappers;

import es.caib.notib.client.domini.OrigenEnum;
import es.caib.notib.client.domini.ValidesaEnum;
import es.caib.notib.logic.intf.dto.AnexoWsDto;
import es.caib.notib.logic.intf.statemachine.dto.DocumentRegistreDto;
import es.caib.plugins.arxiu.api.DocumentEstatElaboracio;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;
import java.util.GregorianCalendar;

@Mapper(componentModel = "spring")
public interface DocumentMapper {

    @Mapping(target = "tipoDocumento", constant = "02")
    @Mapping(source = "titol", target = "titulo")
    @Mapping(source = "nom", target = "nombreFicheroAnexado")
    @Mapping(source = "contingut", target = "ficheroAnexado")
    @Mapping(source = "mimeType", target = "tipoMIMEFicheroAnexado")
    @Mapping(source = "tipusDocumental", target = "tipoDocumental")
    @Mapping(source = "origen", target = "origenCiudadanoAdmin", qualifiedByName = "origen")
    @Mapping(source = "validesa", target = "validezDocumento", qualifiedByName = "validesa")
    @Mapping(source = "modeFirma", target = "modoFirma", qualifiedByName = "modeFirma")
    @Mapping(source = "dataCaptura", target = "fechaCaptura", qualifiedByName = "dataCaptura")
    AnexoWsDto toAnexoWs(DocumentRegistreDto document);

    @Named("origen")
    default Integer getOrigen(OrigenEnum origen) {
        if (origen == null) {
            return DocumentEstatElaboracio.ORIGINAL.ordinal();
        }
        return origen.getValor();
    }

    @Named("validesa")
    default String getValidesa(ValidesaEnum validesa) {
        if (validesa == null) {
            return null;
        }
        return validesa.getValor();
    }

    @Named("modeFirma")
    default Integer getModeFirma(Boolean modeFirma) {
        if (modeFirma == null) {
            return 0;
        }
        return modeFirma ? 1 : 0;
    }

    @Named("dataCaptura")
    default XMLGregorianCalendar getDataCaptura(Date dataCaptura) throws DatatypeConfigurationException {
        if (dataCaptura == null) {
            return null;
        }
        var gc = new GregorianCalendar();
        gc.setTime(dataCaptura);
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
    }

}
