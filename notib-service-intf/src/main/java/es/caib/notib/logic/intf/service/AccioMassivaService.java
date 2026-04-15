package es.caib.notib.logic.intf.service;

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

import java.util.List;

public interface AccioMassivaService {

    PaginaDto<AccioMassivaDto> findAmbFiltre(AccioMassivaFiltre filtre, PaginacioParamsDto paginacioParams);

    List<AccioMassivaDetall> findDetall(Long accioId);

    Long altaAccioMassiva(AccioMassivaExecucio accio);

    FitxerDto exportar(AccioMassivaExecucio accio)  throws Exception;

    List<FitxerDto> descarregarJustificant(AccioMassivaExecucio accio);

    List<List<ArxiuDto>> descarregarCertificacio(AccioMassivaExecucio accio);

    RespostaAccio<AccioMassivaElement> reactivarErrors(AccioMassivaExecucio accio);

    List<AccioMassivaDetall> esborrarNotificacions(AccioMassivaExecucio accio);

    List<AccioMassivaDetall> recuperarNotificacionsEsborrades(AccioMassivaExecucio accio);

    void executarAccio(AccioMassivaExecucio accio);

}
