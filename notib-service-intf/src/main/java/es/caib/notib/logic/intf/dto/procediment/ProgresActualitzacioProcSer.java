package es.caib.notib.logic.intf.dto.procediment;

import es.caib.notib.logic.intf.dto.ProgresActualitzacioDto;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
public class ProgresActualitzacioProcSer extends ProgresActualitzacioDto {

    private Integer totalInicial;
    private Integer totalFinal;
    private Integer actiusInicial;
    private Integer actiusFinal;
    private Integer inactiusInicial;
    private Integer inactiusFinal;

    private List<ProcSerDto> procedimentsObtinguts = new ArrayList<>();
    private List<ProcSerDataDto> senseCodiSia = new ArrayList<>();
    private List<ProcSerDataDto> organNoPertanyEntitat = new ArrayList<>();
    private List<String> noActius = new ArrayList<>();

    public void addSenseCodiSia(ProcSerDataDto procSer) {
        senseCodiSia.add(procSer);
    }

    public void addAmbOrganNoPertanyEntitat(ProcSerDataDto procSer) {
        organNoPertanyEntitat.add(procSer);
    }

}
