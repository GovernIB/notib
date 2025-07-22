package es.caib.notib.logic.intf.dto.accioMassiva;

import com.google.common.base.Strings;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccioMassivaDto implements Serializable {

    private Long id;
    private String createdByCodi;
    private Date createdDate;
    private AccioMassivaTipus tipus;
    private Date dataInici;
    private Date dataFi;
    private Boolean error;
    private int numOk;
    private int numErrors;
    private int numPendent;
    private String errorDescripcio;
    private String excepcioStacktrace;
    private List<AccioMassivaElementDto> elements;
    private int progresBar;

    public int getProgresBar() {

        if (elements == null || elements.isEmpty()) {
            return 0;
        }
        var nPendents = elements.stream().filter(e -> e.isPendent()).collect(Collectors.toList()).size();
        if (nPendents == 0) {
            return 100;
        }
        var total = elements.size();
        return 100 - (int) ((double) nPendents / total * 100);

    }
}
