
package es.caib.notib.api.interna.openapi.model.notificacio;

import es.caib.notib.client.domini.EntregaPostalVia;
import es.caib.notib.client.domini.NotificaDomiciliConcretTipus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import javax.validation.constraints.Size;

/**
 * Informació de l'entrega postal.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Schema(name = "EntregaPostal")
public class EntregaPostalApi {

    @Schema(name = "tipus", implementation = NotificaDomiciliConcretTipus.class, example = "NACIONAL", required = true,
            description = "Enumerat que indica el tipus de entrega postal.")
    protected NotificaDomiciliConcretTipus tipus;
    @Schema(name = "viaTipus", implementation = EntregaPostalVia.class, example = "CARRER", required = false,
            description = "Enumerat que indica el tipus de via per a entregues postals\n" +
                    " * Obligatori quan tipoDomicili = NACIONAL")
    protected EntregaPostalVia viaTipus;
    @Size(max = 50)
    @Schema(name = "viaNom", implementation = String.class, example = "Aragó", required = false,
            description = "Nom de la via per entregues postals\n" +
                    " * Obligatori quan tipoDomicili = NACIONAL o ESTRANGER")
    protected String viaNom;
    @Size(max = 5)
    @Schema(name = "numeroCasa", implementation = String.class, example = "26", required = false,
            description = "Número de la casa per entregues postals\n" +
                    " * Obligatori quan tipoDomicili = NACIONAL excepte si s’indica el punt quilomètric")
    protected String numeroCasa;
    @Size(max = 3)
    @Schema(name = "numeroQualificador", implementation = String.class, example = "bis", required = false,
            description = "Informació addicional sobre la numeració de l’adreça")
    protected String numeroQualificador;
    @Size(max = 30)
    @Schema(name = "puntKm", implementation = String.class, example = "12.5", required = false,
            description = "Punt quilomètric per entregues postals\n" +
                    " * Obligatori quan tipoDomicili = NACIONAL excepte si s’indica numeroCasa")
    protected String puntKm;
    @Size(max = 10)
    @Schema(name = "apartatCorreus", implementation = String.class, example = "123", required = false,
            description = "Apartat de correus per entregues postals\n" +
                    " * Obligatori quan tipoDomicili = APARTAT_CORREUS")
    protected String apartatCorreus;
    @Size(max = 3)
    @Schema(name = "portal", implementation = String.class, example = "2", required = false,
            description = "Portal de la casa per entregues postals")
    protected String portal;
    @Size(max = 3)
    @Schema(name = "escala", implementation = String.class, example = "B", required = false,
            description = "Escala de la casa per entregues postals")
    protected String escala;
    @Size(max = 3)
    @Schema(name = "planta", implementation = String.class, example = "4", required = false,
            description = "Planta de la casa per entregues postals")
    protected String planta;
    @Size(max = 3)
    @Schema(name = "porta", implementation = String.class, example = "2", required = false,
            description = "Porta de la casa per entregues postals")
    protected String porta;
    @Size(max = 3)
    @Schema(name = "bloc", implementation = String.class, example = "C", required = false,
            description = "Bloc de la casa per entregues postals")
    protected String bloc;
    @Size(max = 40)
    @Schema(name = "complement", implementation = String.class, example = "Edificio Walden", required = false,
            description = "Informació extra sobre la casa per entregues postals")
    protected String complement;
    @Size(max = 10)
    @Schema(name = "codiPostal", implementation = String.class, example = "07003", required = true,
            description = "Codi postal de la casa per entregues postals.\n" +
                    " * Per enviaments internacionals a països sense codi posatal es pot posar 00000.")
    protected String codiPostal;
    @Size(max = 255)
    @Schema(name = "poblacio", implementation = String.class, example = "Palma", required = false,
            description = "Població on s’entrega l’enviament\n" +
                    " * Obligatori quan tipoDomicili = NACIONAL, ESTRANGER o APARTAT_POSTAL")
    protected String poblacio;
    @Size(max = 6)
    @Schema(name = "municipiCodi", implementation = String.class, example = "070407", required = false,
            description = "Codi INE de 6 caràcters del municipi on s’entrega l’enviament (inclou codi de control)\n" +
                    " * Obligatori quan tipoDomicili = NACIONAL o APARTAT_POSTAL")
    protected String municipiCodi;
    @Size(max = 2)
    @Schema(name = "provincia", implementation = String.class, example = "07", required = false,
            description = "Codi INE de 2 dígits de la província on s’entrega l’enviament\n" +
                    " * Obligatori quan tipoDomicili = NACIONAL o APARTAT_POSTAL")
    protected String provincia;
    @Size(max = 2)
    @Schema(name = "paisCodi", implementation = String.class, example = "DE", required = false,
            description = "Codi ISO 3166 de 2 caràcters del país on s’entrega l’enviament\n" +
                    "* Obligatori quan tipoDomicili = ESTRANGER")
    protected String paisCodi;
    @Size(max = 50)
    @Schema(name = "linea1", implementation = String.class, example = "Carrer Aragó 26bis, 4t - 3", required = false,
            description = "Línia 1 de l’adreça d’entrega de l’enviament sense normalitzar\n" +
                    " * Obligatori quan tipoDomicili = SENSE_NORMALITZAR")
    protected String linea1;
    @Size(max = 50)
    @Schema(name = "linea2", implementation = String.class, example = "Palma 07003 - Illes Balears", required = false,
            description = "Línia 2 de l’adreça d’entrega de l’enviament sense normalitzar\n" +
                    " * Obligatori quan tipoDomicili = SENSE_NORMALITZAR")
    protected String linea2;
    @Schema(name = "cie", implementation = String.class, example = "Servera", required = false,
            description = "Línia 2 de l’adreça d’entrega de l’enviament sense normalitzar")
    protected Integer cie;
    @Size(max = 10)
    @Schema(name = "formatSobre", implementation = String.class, example = "C5", required = false,
            description = "Cadena indicant el format del sobre ")
    protected String formatSobre;
    @Size(max = 10)
    @Schema(name = "formatFulla", implementation = String.class, example = "A4", required = false,
            description = "Cadena indicant el format de la fulla")
    protected String formatFulla;

}
