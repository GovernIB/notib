package es.caib.notib.logic.test.data;

import es.caib.notib.logic.intf.dto.cie.CieDataDto;
import es.caib.notib.logic.intf.dto.cie.CieDto;
import es.caib.notib.logic.intf.service.PagadorCieService;
import es.caib.notib.logic.test.AuthenticationTest;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class CieItemTest extends DatabaseItemTest<CieDataDto>{
    @Autowired
    protected PagadorCieService pagadorCieService;
    @Autowired
    protected AuthenticationTest authenticationTest;
    @Autowired
    protected ProcedimentItemTest procedimentItemTest;

    @Getter
    private String[] relatedFields = new String[]{ "procediment" };

    @Override
    public CieDataDto create(Object element, Long entitatId) throws Exception{
        return pagadorCieService.upsert(entitatId, (CieDataDto) element);
    }

    @Override
    public void delete(Long entitatId, CieDataDto object) {
        authenticationTest.autenticarUsuari("admin");
        pagadorCieService.delete(object.getId());
    }

//    @Override
//    public NotificacioDatabaseDto getRandomInstance() throws Exception{
//        throw new Exception("Can't build a fully random notificacio");
//    }

    public void relateElement(String key, Object element) throws Exception{
//        if (element instanceof ProcedimentDto) {
//            getObject(key).setProcediment((ProcedimentDto) element);
//        }
    }

    public static CieDataDto getRandomInstance() {
        CieDto cie = new CieDto();
        cie.setOrganismePagadorCodi(ConfigTest.DEFAULT_ORGAN_DIR3);
        cie.setContracteDataVig(new Date());
        return cie;
    }

}
