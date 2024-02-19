package es.caib.notib.logic.objectes;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

public enum MassivaColumnsEnum {

    UNITAT_EMISORA( "Codigo Unidad Remisora",   "Codi DIR3 de la unitat remisora",                                                                  "E04975701"),
    CONCEPTE(       "Concepto",                 "Text amb el concepte de l'enviament",                                                              "Concepte"),
    TIPUS_ENV(      "Tipo de Envio",            "Tipus d'enviament (C|Comunicacio|Comunicacion / N|Notificacio|Notificacion / S|Sir)",                "Notificacio"),
    REF_EMISOR(     "Referencia Emisor",        "Referencia única proveída per l'emisor (número d'expedient)",                                                           "NOTIF2103021"),
    FITXER_NOM(     "Nombre Fichero",           "Nom del document a notificar, inclòs en el zip de documents (aquest camp exclou el de UUID i CSV)","fitxer.pdf"),
    FITXER_UUID(    "UUID Fichero",             "UUID de l'arxiu del document a notificar (aquest camp exclou el de nom i CSV)",                    "f1c9f8c8-d326-4315-8a8f-0ec23b970ae5"),
    FITXER_CSV(     "CSV Fichero",              "CSV del document a notificar (aquest camp exclou el de nom i UUID)",                               "3968d0f21388c950d4fbd2654f3867771d3ee1d4d017c477872173ec69980359"),
    FITXER_NORMAL(  "Normalizado",              "Indica si el document té un format normalitzat per a la seva impressió (Si|true / No|false)",      "No"),
    PRIORITAT(      "Prioridad Servicio",       "Prioritat del servei d'enviament (Normal / Urgent|Urgente)",                                       "Normal"),
    DEST_NOM(       "Nombre",                   "Nom del destinatari de l'enviament",                                                               "Antoni"),
    DEST_LLINATGES( "Apellidos",                "Llinatges del destinatari de l'enviament",                                                         "Riera Sanchez"),
    DEST_DOC(       "CIF/NIF",                  "Número del document del destinatari de l'enviament",                                               "12345678Z"),
    DEST_EMAIL(     "Email",                    "Email del destinatari de l'enviament",                                                             "mail@mail.com"),
    UNITAT_DESTI(   "Codigo destino",           "Codi Dir3 de l'administració destinatària de l'enviament",                                         "L01280796"),
    TIPUS_DOC(      "Tipo documento",           "Tipus de document de destinatari sense NIF (PASSAPORT / ESTRANGER / ALTRE)",                       "PASSAPORT"),
    ADDR_LIN1(      "Linea 1",                  "Primera línia de l'adreça del destinatari, en cas d'enviament postal",                             "Castelló 115"),
    ADDR_LIN2(      "Linea 2",                  "Segona línia de l'adreça del destinatari, en cas d'enviament postal",                              "28016 Madrid"),
    ADDR_CP(        "Codigo Postal",            "Codi postal del destinatari, en cas d'enviament postal",                                           "37891"),
    RETARD(         "Retardo Postal",           "Dies que s'ha d'esperar abans de realitzar l'enviament postal",                                    "0"),
    PROCEDIMENT(    "Codigo Procedimiento",     "Codi SIA del procediment al que pertany l'enviament",                                              "101310"),
    DATA_PROG(      "Fecha Envio Programado",   "Data en que es vol realitzar l'enviament, en cas de no voler-lo realitzar immediatament",          "01/01/1970"),
    META_ORIGEN(    "Origen",                   "Metadata que informa de l'origen del document (Ciutada|Ciudadano / Administracio|Administracion)", "CIUTADA"),
    META_ESTAT_ELAB("Estado Elaboracion",       "Metadata que informa de l'estat d'elaboració del document (Original / Copia / Copia autentica)",   "ORIGINAL"),
    META_TIPUS_DOC( "Tipo documental",          "Metadata que informa del tipus documental del document (COMUNICACIO, NOTIFICACIO, ACORD, ...)",    "INFORME"),
    META_FIRMAT(    "PDF Firmado",              "Metadata que informa si el document està firmat o no (Si|true / No|false)",                        "Si"),
    DESCRIPCIO(     "Descripcion",              "Text amb la descripció de l'enviament",                                                            "Descripció");

    private final String nom;
    private final String descripcio;
    private final String exemple;

    MassivaColumnsEnum(String nom, String descripcio, String exemple) {
        this.nom = nom;
        this.descripcio = descripcio;
        this.exemple = exemple;
    }

    public String getNom() {
        return nom;
    }

    public String getDescripcio() {
        return descripcio;
    }

    public String getExemple() {
        return exemple;
    }

    public static MassivaColumnsEnum fromNom(final String nom) {
        return Arrays.stream(MassivaColumnsEnum.values())
                .filter(column -> isNomEqual(column, nom))
                .findFirst()
                .orElse(null);
            }

    private static boolean isNomEqual(MassivaColumnsEnum column, String nom) {
        return StringUtils.stripAccents(nom).equalsIgnoreCase(column.getNom());
    }
}
