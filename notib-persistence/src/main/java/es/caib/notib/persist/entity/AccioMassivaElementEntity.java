package es.caib.notib.persist.entity;

import es.caib.notib.logic.intf.dto.accioMassiva.SeleccioTipus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="not_accio_massiva_element")
public class AccioMassivaElementEntity extends AbstractPersistable<Long>  {

    private static int ERROR_DESC_MAX_LENGTH = 1024;
    private static int STACKTRACE_MAX_LENGTH = 2048;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ACCIO_MASSIVA_ID")
    @ForeignKey(name = "FK_ACCIOMASSIVA_ELEMENT")
    private AccioMassivaEntity accioMassiva;

    @Column(name = "seleccio_tipus", nullable = false)
    @Enumerated(EnumType.STRING)
    private SeleccioTipus seleccioTipus;

    @Column(name = "element_id", nullable = false)
    private Long elementId;

    @Column(name = "data_execucio")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataExecucio;

    @Column(name = "error_descripcio", length = 1024)
    private String errorDescripcio;

    @Column(name = "excepcio_stacktrace", length = 2048)
    private String excepcioStackTrace;
//
//    @Lob
//    @Column(name = "SELECCIO")
//    private String seleccio;


//    public void crearSeleccioJson(Collection<Long> ids) {
//
//        seleccio = "{\"elements\": [";
//        var i = 0;
//        var size = ids.size();
//        for (Long id : ids) {
//            seleccio += "{\"id\":\"" + id +"\"," + "\"errorDesc\":\"\"," + "\"errorStackTrace\":\"\"}" ;
//            if (i < size - 1) {
//                seleccio += ",";
//            }
//            i++;
//        }
//        seleccio += "]}";
//    }

//    public AccioMassivaSeleccio getSeleccioJson() {
//
//        try {
//            return new ObjectMapper().readValue(seleccio, AccioMassivaSeleccio.class);
//        } catch (Exception ex) {
//            log.error("[AccioMassivaSeleccio] Error convertint la seleccio JSON", ex);
//            throw new RuntimeException(ex);
//        }
//    }

    public void actualitzar() {

        dataExecucio = new Date();
        actualitzarDataFi();
    }

    public void actualitzar(String errorDesc, String errorStackTrace) {

            dataExecucio = new Date();
            if (!StringUtils.isEmpty(errorDesc)) {
                errorDescripcio = formatErrorDescripcio(errorDesc);
            }
            if (!StringUtils.isEmpty(errorStackTrace)) {
                excepcioStackTrace = formatExcepcioStacktrace(errorStackTrace);
            }
            actualitzarDataFi();
    }

    public void actualitzarDataFi() {

        var elementsNoExecutats = accioMassiva.getElements().stream().filter(x -> dataExecucio == null).collect(Collectors.toList());
        if (elementsNoExecutats.isEmpty()) {
            accioMassiva.setDataFi(new Date());
        }
    }


    public String formatErrorDescripcio(String errorDescripcio) {
        return StringUtils.abbreviate(errorDescripcio, ERROR_DESC_MAX_LENGTH);
    }

    public String formatExcepcioStacktrace(String excepcioStacktrace) {
        return StringUtils.abbreviate(excepcioStacktrace, STACKTRACE_MAX_LENGTH);
    }
}
