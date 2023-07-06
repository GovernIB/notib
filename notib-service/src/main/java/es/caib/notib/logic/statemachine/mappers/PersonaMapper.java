package es.caib.notib.logic.statemachine.mappers;

import com.google.common.base.Strings;
import es.caib.notib.client.domini.DocumentTipus;
import es.caib.notib.client.domini.InteressatTipus;
import es.caib.notib.client.domini.Persona;
import es.caib.notib.logic.intf.dto.DatosInteresadoWsDto;
import es.caib.notib.persist.entity.PersonaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface PersonaMapper {

    Persona toDto(PersonaEntity persona);

    @Mapping(source = "nom", target = "nombre", qualifiedByName = "nom")
    @Mapping(source = "llinatge1", target = "apellido1")
    @Mapping(source = "llinatge2", target = "apellido2")
    @Mapping(source = "raoSocial", target = "razonSocial", qualifiedByName = "raoSocial")
    @Mapping(source = "interessatTipus", target = "tipoInteresado", qualifiedByName = "tipusInteressat")
    @Mapping(source = ".", target = "tipoDocumentoIdentificacion", qualifiedByName = "tipoDocumento")
    @Mapping(source = ".", target = "documento", qualifiedByName = "documento")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "email", target = "direccionElectronica")
    @Mapping(source = "telefon", target = "telefono")
    @Mapping(target = "codigoDire", constant = "")
    @Mapping(target = "direccion", constant = "")
    @Mapping(target = "cp", constant = "")
    @Mapping(target = "observaciones", constant = "")
    DatosInteresadoWsDto toInteresadoWs(Persona persona);

    @Named("tipusInteressat")
    default Long getTipusInteressat(InteressatTipus interessatTipus) {
        if (interessatTipus == null) {
            return null;
        }
        return InteressatTipus.FISICA_SENSE_NIF.equals(interessatTipus) ? 2l: interessatTipus.getLongVal();
    }

    @Named("nom")
    default String getNom(String nom) {
        if (nom == null) {
            return null;
        }
        return nom.length() < 30 ? nom : nom.substring(0, 30);
    }

    @Named("raoSocial")
    default String getRaoSocial(String raoSocial) {
        if (raoSocial == null) {
            return null;
        }
        return raoSocial.length() < 80 ? raoSocial : raoSocial.substring(0, 80);
    }

    @Named("tipoDocumento")
    default String getTipusDocument(Persona persona) {
        if (persona == null) {
            return null;
        }
        if (persona.getInteressatTipus() == null) {
            return null;
        }
        switch (persona.getInteressatTipus()) {
            case ADMINISTRACIO:
                return "O";
            case FISICA:
                return isDocumentEstranger(persona.getNif()) ? "E" : "N";
            case FISICA_SENSE_NIF:
                return !Strings.isNullOrEmpty(persona.getNif()) && persona.getDocumentTipus() != null ?
                        (persona.getDocumentTipus() == DocumentTipus.PASSAPORT ? "P" : "X") : null;
            case JURIDICA:
                return "C";
            default:
                return null;
        }
    }

    @Named("documento")
    default String getDocument(Persona persona) {
        if (persona == null) {
            return null;
        }
        if (persona.getInteressatTipus() == null) {
            return null;
        }
        switch (persona.getInteressatTipus()) {
            case ADMINISTRACIO:
                return persona.getDir3Codi() != null ? persona.getDir3Codi().trim() : null;
            case FISICA:
            case JURIDICA:
                return persona.getNif() != null ? persona.getNif().trim() : null;
            case FISICA_SENSE_NIF:
                return !Strings.isNullOrEmpty(persona.getNif()) ? persona.getNif() : null;
            default:
                return null;
        }
    }

    default boolean isDocumentEstranger(String nie) {
        if (nie == null) {
            return false;
        }
        var aux = nie.toUpperCase();
        return aux.startsWith("X") || aux.startsWith("Y") || aux.startsWith("Z");
    }

}
