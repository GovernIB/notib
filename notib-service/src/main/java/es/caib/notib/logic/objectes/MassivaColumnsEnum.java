package es.caib.notib.logic.objectes;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

@Getter
public enum MassivaColumnsEnum {

    UNITAT_EMISORA( "Codigo Unidad Remisora",   "\"Codi DIR3 de la unitat remisora\"",                                                                  "E04975701",                                                        false),
    CONCEPTE(       "Concepto",                 "\"Text amb el concepte de l'enviament\"",                                                              "Concepte",                                                         false),
    TIPUS_ENV(      "Tipo de Envio",            "\"Tipus d'enviament (C|Comunicacio|Comunicacion / N|Notificacio|Notificacion / S|Sir)\"",              "Notificacio",                                                      false),
    REF_EMISOR(     "Referencia Emisor",        "\"Referencia única proveída per l'emisor (número d'expedient)\"",                                      "NOTIF2103021",                                                     false),
    FITXER_NOM(     "Nombre Fichero",           "\"Nom del document a notificar, inclòs en el zip de documents (aquest camp exclou el de UUID i CSV\")","fitxer.pdf",                                                       false),
    FITXER_UUID(    "UUID Fichero",             "\"UUID de l'arxiu del document a notificar (aquest camp exclou el de nom i CSV)\"",                    "f1c9f8c8-d326-4315-8a8f-0ec23b970ae5",                             false),
    FITXER_CSV(     "CSV Fichero",              "\"CSV del document a notificar (aquest camp exclou el de nom i UUID)\"",                               "3968d0f21388c950d4fbd2654f3867771d3ee1d4d017c477872173ec69980359", false),
    FITXER_NORMAL(  "Normalizado",              "\"Indica si el document té un format normalitzat per a la seva impressió (Si|true / No|false)\"",      "No",                                                               false),
    PRIORITAT(      "Prioridad Servicio",       "\"Prioritat del servei d'enviament (Normal / Urgent|Urgente)\"",                                       "Normal",                                                           false),
    DEST_NOM(       "Nombre",                   "\"Nom del destinatari de l'enviament\"",                                                               "Antoni",                                                           false),
    DEST_LLINATGES( "Apellidos",                "\"Llinatges del destinatari de l'enviament\"",                                                         "Riera Sanchez",                                                    false),
    DEST_DOC(       "CIF/NIF",                  "\"Número del document del destinatari de l'enviament\"",                                               "12345678Z",                                                        false),
    DEST_EMAIL(     "Email",                    "\"Email del destinatari de l'enviament\"",                                                             "mail@mail.com",                                                    false),
    UNITAT_DESTI(   "Codigo destino",           "\"Codi Dir3 de l'administració destinatària de l'enviament\"",                                         "L01280796",                                                        false),
    TIPUS_DOC(      "Tipo documento",           "\"Tipus de document de destinatari sense NIF (PASSAPORT / ESTRANGER / ALTRE)\"",                       "PASSAPORT",                                                        false),
    ADDR_LIN1(      "Linea 1",                  "\"Primera línia de l'adreça del destinatari, en cas d'enviament postal\"",                             "Castelló 115",                                                     false),
    ADDR_LIN2(      "Linea 2",                  "\"Segona línia de l'adreça del destinatari, en cas d'enviament postal\"",                              "28016 Madrid",                                                     false),
    CODI_POSTAL(    "Codigo Postal",            "\"Codi postal del destinatari, en cas d'enviament postal\"",                                           "37891",                                                            false),
    RETARD(         "Retardo Postal",           "\"Dies que s'ha d'esperar abans de realitzar l'enviament postal\"",                                    "0",                                                                false),
    PROCEDIMENT(    "Codigo Procedimiento",     "\"Codi SIA del procediment al que pertany l'enviament\"",                                              "101310",                                                           false),
    DATA_PROG(      "Fecha Envio Programado",   "\"Data en que es vol realitzar l'enviament, en cas de no voler-lo realitzar immediatament\"",          "01/01/1970",                                                       false),
    META_ORIGEN(    "Origen",                   "\"Metadata que informa de l'origen del document (Ciutada|Ciudadano / Administracio|Administracion\")", "CIUTADA",                                                          false),
    META_ESTAT_ELAB("Estado Elaboracion",       "\"Metadata que informa de l'estat d'elaboració del document (Original / Copia / Copia autentica)\"",   "ORIGINAL",                                                         false),
    META_TIPUS_DOC( "Tipo documental",          "\"Metadata que informa del tipus documental del document (COMUNICACIO, NOTIFICACIO, ACORD, ...)\"",    "INFORME",                                                          false),
    META_FIRMAT(    "PDF Firmado",              "\"Metadata que informa si el document està firmat o no (Si|true / No|false)\"",                        "Si",                                                               false),
    DESCRIPCIO(     "Descripcion",              "\"Text amb la descripció de l'enviament\"",                                                            "Descripció",                                                       false),
    ENTREGA_POSTAL( "Entrega postal",           "\"Si|true/No|false indicant si hi ha entrega postal activa\"",                                         "Si",                                                               true),
    DOMICILI_CONCRET_TIPUS("Tipo entrega postal","\"NACIONAL|ESTRANGER|APARTAT_CORREUS|SENSE_NORMALITZAR\"",                                            "SENSE_NORMALITZAR",                                                true),
    VIA_TIPUS(      "Via tipo",                 "\"Tipus de via per l'entrega postal\"",                                                                "CALLE",                                                            true),
    VIA_NOM(        "Nombre via",               "\"Nom de la via per l'entrega postal\"",                                                               "Nombre de la via",                                                 true),
    APARTAT_CORREUS("Apartado de correos",      "\"Apartat de correus on enviar l'entrega en cas de tipus de via APARTAT_CORREUS\"",                    "012345",                                                           true),
    NUMERO_CASA(    "Numero de casa",           "\"Número de l'habitatge per l'entrega postal\"",                                                       "1",                                                                true),
    PUNT_KM(        "Punto kilometrico",        "\"Punt kilomètric de l'habitatge per l'entrega postal\"",                                              "1",                                                                true),
    PORTAL(         "Portal",                   "\"Portal de l'habitatge per l'entrega postal\"",                                                       "1",                                                                true),
    ESCALA(         "Escalera",                 "\"Escalera de l'habitatge per l'entrega postal\"",                                                     "A",                                                                true),
    PLANTA(         "Planta",                   "\"Número de planta de l'habitatge per l'entrega postal\"",                                             "1",                                                                true),
    PORTA(          "Puerta",                   "\"Porta de l'habitatge per l'entrega postal\"",                                                        "A",                                                                true),
    BLOC(           "Bloque",                   "\"Bloc de l'habitatge per l'entrega postal\"",                                                         "1",                                                                true),
    PAIS_CODI(      "Pais codigo",              "\"Codi del país per l'entrega postal\"",                                                               "es",                                                               true),
    PROVINCIA(      "Provincia",                "\"Codi de la província per l'entrega postal\"",                                                        "07",                                                               true),
    MUNICIPI_CODI(  "Municipio codigo",         "\"Codi del municipi per l'entrega postal\"",                                                           "070407",                                                           true),
    POBLACIO(       "Poblacion",                "\"Població per l'entrega postal\"",                                                                    "Manacor",                                                          true),
    COMPLEMENT(     "Complemento",              "\"Informació extra de la direcció per l'entrega postal\"",                                             "Casa azul",                                                        true);


    private final String nom;
    private final String descripcio;
    private final String exemple;
    private final boolean opcional;

    MassivaColumnsEnum(String nom, String descripcio, String exemple, boolean opcional) {
        this.nom = nom;
        this.descripcio = descripcio;
        this.exemple = exemple;
        this.opcional = opcional;
    }

    public static MassivaColumnsEnum fromNom(final String nom) {
        return Arrays.stream(MassivaColumnsEnum.values())
                .filter(column -> isNomEqual(column, nom))
                .findFirst()
                .orElse(null);
            }

    private static boolean isNomEqual(MassivaColumnsEnum column, String nom) {
        return nom != null && StringUtils.stripAccents(nom).equalsIgnoreCase(column.getNom());
    }
}
