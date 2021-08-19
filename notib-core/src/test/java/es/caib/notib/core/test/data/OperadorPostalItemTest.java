package es.caib.notib.core.test.data;

import es.caib.notib.core.api.dto.cie.OperadorPostalDto;
import es.caib.notib.core.api.service.OperadorPostalService;
import es.caib.notib.core.test.AuthenticationTest;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OperadorPostalItemTest extends DatabaseItemTest<OperadorPostalDto>{
    @Autowired
    protected OperadorPostalService operadorPostalService;
    @Autowired
    protected AuthenticationTest authenticationTest;
    @Autowired
    protected ProcedimentItemTest procedimentItemTest;

    @Getter
    private String[] relatedFields = new String[]{ "procediment" };

    @Override
    public OperadorPostalDto create(Object element, Long entitatId) throws Exception{
        return operadorPostalService.create(
                entitatId,
                (OperadorPostalDto) element);
    }

    @Override
    public void delete(Long entitatId, OperadorPostalDto object) {
        authenticationTest.autenticarUsuari("admin");
        operadorPostalService.delete(object.getId());
    }

//    public static OperadorPostalDto getRandomInstance() {
//        CieFormatSobreDto createPagadorCieFormatFulla = new CieFormatSobreDto();
//        createPagadorCieFormatFulla.setCodi("122");
//        return createPagadorCieFormatFulla;
//    }

}
