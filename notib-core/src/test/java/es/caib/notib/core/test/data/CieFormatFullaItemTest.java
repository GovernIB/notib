package es.caib.notib.core.test.data;

import es.caib.notib.core.api.dto.cie.CieFormatFullaDto;
import es.caib.notib.core.api.service.PagadorCieFormatFullaService;
import es.caib.notib.core.test.AuthenticationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CieFormatFullaItemTest extends DatabaseItemTest<CieFormatFullaDto>{
    @Autowired
    protected PagadorCieFormatFullaService cieFormatFullaService;
    @Autowired
    protected AuthenticationTest authenticationTest;
    @Autowired
    protected ProcedimentItemTest procedimentItemTest;

    @Override
    public CieFormatFullaDto create(Object element, Long entitatId) throws Exception{
        return cieFormatFullaService.create(
                ((CieFormatFullaDto) element).getPagadorCieId(),
                (CieFormatFullaDto) element);
    }

    @Override
    public void delete(Long entitatId, CieFormatFullaDto object) {
        authenticationTest.autenticarUsuari("admin");
        cieFormatFullaService.delete(object.getId());
    }

    public static CieFormatFullaDto getRandomInstance() {
        CieFormatFullaDto createPagadorCieFormatFulla = new CieFormatFullaDto();
        createPagadorCieFormatFulla.setCodi("122");
        return createPagadorCieFormatFulla;
    }

}
