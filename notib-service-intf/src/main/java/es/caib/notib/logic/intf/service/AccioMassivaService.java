package es.caib.notib.logic.intf.service;

import es.caib.notib.logic.intf.dto.ArxiuDto;
import es.caib.notib.logic.intf.dto.FitxerDto;
import es.caib.notib.logic.intf.dto.PaginaDto;
import es.caib.notib.logic.intf.dto.PaginacioParamsDto;
import es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaDto;
import es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaExecucio;
import es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaFiltre;
import es.caib.notib.logic.intf.dto.accioMassiva.AccioMassivaTipus;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface AccioMassivaService {

    PaginaDto<AccioMassivaDto> findAmbFiltre(AccioMassivaFiltre filtre, PaginacioParamsDto paginacioParams);

    Long altaAccioMassiva(AccioMassivaTipus accioMassivaTipus, Long entitatId);

    FitxerDto exportar(AccioMassivaExecucio accio)  throws Exception;

    List<FitxerDto> descarregarJustificant(AccioMassivaExecucio accio);

//    List<List<ArxiuDto>> descarregarCertificacio();
}
