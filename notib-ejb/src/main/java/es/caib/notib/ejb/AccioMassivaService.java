package es.caib.notib.ejb;

import es.caib.notib.logic.intf.dto.FitxerDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaDto;
import es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaExecucio;
import es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaFiltre;
import es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaTipus;
import org.springframework.context.annotation.Primary;

import javax.ejb.Stateless;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Primary
@Stateless
public class AccioMassivaService extends AbstractService<es.caib.notib.logic.intf.service.AccioMassivaService> implements es.caib.notib.logic.intf.service.AccioMassivaService {

    @Override
    public PaginaDto<AccioMassivaDto> findAmbFiltre(AccioMassivaFiltre filtre, PaginacioParamsDto paginacioParams) {
        return getDelegateService().findAmbFiltre(filtre, paginacioParams);
    }

    @Override
    public Long altaAccioMassiva(AccioMassivaTipus accioMassivaTipus, Long entitatId) {
        return getDelegateService().altaAccioMassiva(accioMassivaTipus, entitatId);
    }

    @Override
    public FitxerDto exportar(AccioMassivaExecucio accio)  throws Exception {
        return getDelegateService().exportar(accio);
    }

    @Override
    public List<FitxerDto> descarregarJustificant(AccioMassivaExecucio accio) {
        return getDelegateService().descarregarJustificant(accio);
    }
}
