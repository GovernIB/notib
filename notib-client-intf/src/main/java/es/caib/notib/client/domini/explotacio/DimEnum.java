package es.caib.notib.client.domini.explotacio;

public enum DimEnum {
    ENT ("Entitat", "Codi de l'entitat a la que pertany la comunicació/notificació"),
    ORG ("Organ Gestor", "Organ gestor al que pertany la comunicació/notificació"),
    PRC ("Procediment", "Procediment al que pertany la comunicació/notificació"),
    USU ("Usuari", "Codi de l'usuari que ha creat la comunicació/notificació"),
    TIP ("Tipus", "Tipus de comunicació oficial: notificació, comunicació o comunicació SIR"),
    ORI ("Origen", "Lloc des d'on s'ha creat la comunicació/notificació: interfície web, API Rest o enviament massiu");

    private String nom;
    private String descripcio;

    DimEnum(String nom, String descripcio) {
        this.nom = nom;
        this.descripcio = descripcio;
    }

    public String getNom() {
        return nom;
    }
    public String getDescripcio() {
        return descripcio;
    }
}
