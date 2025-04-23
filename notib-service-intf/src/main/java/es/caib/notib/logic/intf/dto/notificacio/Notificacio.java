
package es.caib.notib.logic.intf.dto.notificacio;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.client.domini.Idioma;
import es.caib.notib.client.domini.InteressatTipus;
import es.caib.notib.client.util.TrimStringDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Informació d'una notificació per al seu enviament.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Notificacio implements Serializable {

    private Long id;
    private Long procedimentId;
    private Long grupId;

    /**
     * Codi Dir3 de l’organisme emisor
     * Camp obligatori
     */
    @JsonDeserialize(using = TrimStringDeserializer.class)
    private String emisorDir3Codi;


    /**
     * Codi DIR3 de l’òrgan gestor que realitza la notificació/comunicació.
     * Obligatori en el cas de procediments comuns. En cas contrari s’utilitzarà l’òrgan gestor al que pertany el procediment.
     */
    @JsonDeserialize(using = TrimStringDeserializer.class)
    private String organGestor;

    /**
     * Enumerat que indica si l’enviament és una comunicació o una notificació.
     * Possibles valors: [NOTIFICACIO, COMUNICACIO, SIR]
     * Valor per defecte: COMUNICACIO
     * Camp obligatori
     */
    private EnviamentTipus enviamentTipus;

    /**
     * Concepte de l’enviament. Si ha d’anar a CIE només s’agafaran els 50 primers caràcters.
     * Camp obligatori
     */
    @JsonDeserialize(using = TrimStringDeserializer.class)
    private String concepte;

    /**
     * Descripció detallada de l’enviament
     * Camp opcional
     */
    @JsonDeserialize(using = TrimStringDeserializer.class)
    private String descripcio; //Observacions

    /**
     * Data en la que l’enviament es posarà a disposició per a la compareixença
     * Camp opcional. En cas de no informar-se es posarà en disposiciño per a la compareixença de forma inmediata
     */
    private Date enviamentDataProgramada;

    /**
     * Dies que l’enviament estarà a disposició de compareixença en carpeta abans d’entregar-lo per altres mitjans
     * Camp opcional
     */
    private Integer retard;

    /**
     * Data d’expiració de l’enviament
     * Aquest camp és excuient amb el de caducitatDiesNaturals. Només s'ha d'indicar un dels dos. En cas d'informar els dos, s'ignorarà el camp caducitatDiesNaturals.
     * Camp opcional per a comunicacions
     */
    private Date caducitat;

    /**
     * Dies naturals abans que expiri l'enviament.
     * Aquest camp és excuient amb el de caducitat. Només s'ha d'indicar un dels dos. En cas d'informar els dos, aquest s'ignorarà.
     * Camp opcional per a comunicacions
     */
    private Integer caducitatDiesNaturals;

    /**
     * Codi de l’usuari que està realitzant la notificació.
     * Requisit per fer el registre de sortida o assentament registral.
     * Camp obligatori
     */
    @JsonDeserialize(using = TrimStringDeserializer.class)
    private String usuariCodi;

    /**
     * Identificador del procediment SIA al que pertany la notificació
     * Camp obligatori
     */
    @JsonDeserialize(using = TrimStringDeserializer.class)
    private String procedimentCodi;

    /**
     * Codi del grup al que s’assigna la notificació.
     * Aquest camp només té sentit quan es vol donar d’alta una notificació que s’ha configurat amb grups a Notifica (veure manual d’usuari)
     * Camp opcional
     */
    @JsonDeserialize(using = TrimStringDeserializer.class)
    private String grupCodi;

    /**
     * Referència al nombre d'expedient de l'assentament
     * Camp opcional
     */
    @JsonDeserialize(using = TrimStringDeserializer.class)
    private String numExpedient;

    /**
     * Llista d'enviaments continguts en la Notificació/Comunicació
     * Camp obligatori
     */
    @XmlElement(nillable = true)
    private List<Enviament> enviaments;

    /**
     * Enumerat que indica l’idioma de la notificació.
     * Valors possibles: [CA, ES]
     * Valor per defecte: CA
     * Camp opcional
     */
    private Idioma idioma;

    /**
     * Document que s’envia en la notificació/comunicació
     * Camp obligatori
     */
    private Document document;

    /**
     * Document que s’envia en la comunicació. Únicament es pot adjuntar més d’un document en cas de comunicacions a la administració (SIR)
     * Camp opcional
     */
    private Document document2;

    /**
     * Document que s’envia en la comunicació. Únicament es pot adjuntar més d’un document en cas de comunicacions a la administració (SIR)
     * Camp opcional
     */
    private Document document3;

    /**
     * Document que s’envia en la comunicació. Únicament es pot adjuntar més d’un document en cas de comunicacions a la administració (SIR)
     * Camp opcional
     */
    private Document document4;

    /**
     * Document que s’envia en la comunicació. Únicament es pot adjuntar més d’un document en cas de comunicacions a la administració (SIR)
     * Camp opcional
     */
    private Document document5;

    public Notificacio(Notificacio notificacio) {

        this.id = notificacio.getId();
        this.procedimentId = notificacio.getProcedimentId();
        this.grupId = notificacio.getGrupId();
        this.emisorDir3Codi = notificacio.getEmisorDir3Codi();
        this.organGestor = notificacio.getOrganGestor();
        this.enviamentTipus = notificacio.getEnviamentTipus();
        this.concepte = notificacio.getConcepte();
        this.descripcio = notificacio.getDescripcio();
        this.enviamentDataProgramada = notificacio.getEnviamentDataProgramada();
        this.retard = notificacio.getRetard();
        this.caducitat = notificacio.getCaducitat();
        this.caducitatDiesNaturals = notificacio.getCaducitatDiesNaturals();
        this.usuariCodi = notificacio.getUsuariCodi();
        this.procedimentCodi = notificacio.getProcedimentCodi();
        this.grupCodi = notificacio.getGrupCodi();
        this.numExpedient = notificacio.getNumExpedient();
        this.enviaments = notificacio.getEnviaments();
        this.idioma = notificacio.getIdioma();
        this.document = notificacio.getDocument();
        this.document2 = notificacio.getDocument2();
        this.document3 = notificacio.getDocument3();
        this.document4 = notificacio.getDocument4();
        this.document5 = notificacio.getDocument5();
    }

    public Notificacio(NotificacioV3 notificacio) {

        this.id = notificacio.getId();
        this.procedimentId = notificacio.getProcedimentId();
        this.grupId = notificacio.getGrupId();
        this.emisorDir3Codi = notificacio.getEntitatDir3Codi();
        this.organGestor = notificacio.getOrganEmissorDir3Codi();
        this.enviamentTipus = notificacio.getEnviamentTipus();
        this.concepte = notificacio.getConcepte();
        this.descripcio = notificacio.getDescripcio();
        this.enviamentDataProgramada = notificacio.getEnviamentDataProgramada();
        this.retard = notificacio.getRetard();
        this.caducitat = notificacio.getCaducitat();
        this.caducitatDiesNaturals = notificacio.getCaducitatDiesNaturals();
        this.usuariCodi = notificacio.getUsuariCodi();
        this.procedimentCodi = notificacio.getProcedimentCodi();
        this.grupCodi = notificacio.getGrupCodi();
        this.numExpedient = notificacio.getNumExpedient();
        this.enviaments = notificacio.getEnviaments();
        this.idioma = notificacio.getIdioma();
        this.document = notificacio.getDocument();
        this.document2 = notificacio.getDocument2();
        this.document3 = notificacio.getDocument3();
        this.document4 = notificacio.getDocument4();
        this.document5 = notificacio.getDocument5();
    }

    public List<Enviament> getEnviaments() {
        if (enviaments == null) {
            enviaments = new ArrayList<>();
        }
        return this.enviaments;
    }

    public EnviamentTipus getEnviamentTipus() {
        if (!EnviamentTipus.COMUNICACIO.equals(enviamentTipus) || enviaments == null) {
            return enviamentTipus;
        }
        boolean sir = true;
        for (var enviament : enviaments) {
            if (!InteressatTipus.ADMINISTRACIO.equals(enviament.getTitular().getInteressatTipus())) {
                sir = false;
                break;
            }
        }
        return  sir ? EnviamentTipus.SIR : enviamentTipus;
    }

    // Mètodes per a validacions
    public List<String> getNifsEnviaments() {

        List<String> nifs = new ArrayList<>();
        for(var enviament: getEnviaments()) {
            nifs.addAll(enviament.getNifsEnviament());
        }
        return nifs;
    }

    public boolean isSir() {
        return EnviamentTipus.SIR.equals(getEnviamentTipus());
    }


    public final Long FILE_TOTAL_MAX_SIZE = 15728640L; // 15MB

    public boolean docMidaMaximaSuperada() {

        var docsMida = document != null ? document.getMida() : 0;
        docsMida += document2 != null ? document2.getMida() : 0;
        docsMida += document3 != null ? document3.getMida() : 0;
        docsMida += document4 != null ? document4.getMida() : 0;
        docsMida += document5 != null ? document5.getMida() : 0;
        return docsMida > FILE_TOTAL_MAX_SIZE;
    }

    public int getNumDocuments() {

        var num = 0;
        num += document != null ? 1 : 0;
        num += document2 != null ? 1 : 0;
        num += document3 != null ? 1 : 0;
        num += document4 != null ? 1 : 0;
        num += document5 != null ? 1 : 0;
        return num;
    }

}
