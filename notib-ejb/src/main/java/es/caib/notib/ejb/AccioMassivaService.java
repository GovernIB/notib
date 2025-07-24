package es.caib.notib.ejb;

import es.caib.notib.logic.intf.dto.AmpliacionPlazoDto;
import es.caib.notib.logic.intf.dto.ArxiuDto;
import es.caib.notib.logic.intf.dto.FitxerDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.dto.RespostaAccio;
import es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaDto;
import es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaDetall;
import es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaElement;
import es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaExecucio;
import es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaFiltre;
import org.springframework.context.annotation.Primary;

import javax.ejb.Stateless;
import java.util.List;

@Primary
@Stateless
public class AccioMassivaService extends AbstractService<es.caib.notib.logic.intf.service.AccioMassivaService> implements es.caib.notib.logic.intf.service.AccioMassivaService {

    @Override
    public PaginaDto<AccioMassivaDto> findAmbFiltre(AccioMassivaFiltre filtre, PaginacioParamsDto paginacioParams) {
        return getDelegateService().findAmbFiltre(filtre, paginacioParams);
    }

    @Override
    public List<AccioMassivaDetall> findDetall(Long accioId) {
        return getDelegateService().findDetall(accioId);
    }

    @Override
    public Long altaAccioMassiva(AccioMassivaExecucio accio) {
        return getDelegateService().altaAccioMassiva(accio);
    }

    @Override
    public FitxerDto exportar(AccioMassivaExecucio accio)  throws Exception {
        return getDelegateService().exportar(accio);
    }

    @Override
    public List<FitxerDto> descarregarJustificant(AccioMassivaExecucio accio) {
        return getDelegateService().descarregarJustificant(accio);
    }

    @Override
    public List<List<ArxiuDto>> descarregarCertificacio(AccioMassivaExecucio accio) {
        return getDelegateService().descarregarCertificacio(accio);
    }

    @Override
    public RespostaAccio<AccioMassivaElement> reactivarErrors(AccioMassivaExecucio accio) {
        return getDelegateService().reactivarErrors(accio);
    }

    @Override
    public List<AccioMassivaDetall> esborrarNotificacions(AccioMassivaExecucio accio) {
        return getDelegateService().esborrarNotificacions(accio);
    }

    @Override
    public List<AccioMassivaDetall> recuperarNotificacionsEsborrades(AccioMassivaExecucio accio) {
        return getDelegateService().recuperarNotificacionsEsborrades(accio);
    }

    @Override
    public void executarAccio(AccioMassivaExecucio accio) {
        getDelegateService().executarAccio(accio);
    }

}
