package es.caib.notib.logic.test.data;

import es.caib.notib.logic.intf.dto.cie.CieFormatSobreDto;
import es.caib.notib.logic.intf.service.PagadorCieFormatSobreService;
import es.caib.notib.logic.test.AuthenticationTest;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CieFormatSobreItemTest extends DatabaseItemTest<CieFormatSobreDto> {

    @Autowired
    protected PagadorCieFormatSobreService cieFormatSobreService;
    @Autowired
    protected AuthenticationTest authenticationTest;
    @Autowired
    protected ProcedimentItemTest procedimentItemTest;

    @Getter
    private String[] relatedFields = new String[]{ "procediment" };

    @Override
    public CieFormatSobreDto create(Object element, Long entitatId) throws Exception{
        return cieFormatSobreService.create(((CieFormatSobreDto) element).getPagadorCieId(), (CieFormatSobreDto) element);
    }

    @Override
    public void delete(Long entitatId, CieFormatSobreDto object) {
        authenticationTest.autenticarUsuari("admin");
        cieFormatSobreService.delete(object.getId());
    }

    public static CieFormatSobreDto getRandomInstance() {
        CieFormatSobreDto createPagadorCieFormatFulla = new CieFormatSobreDto();
        createPagadorCieFormatFulla.setCodi("122");
        return createPagadorCieFormatFulla;
    }

}
