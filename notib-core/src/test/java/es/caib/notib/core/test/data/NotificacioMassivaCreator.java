package es.caib.notib.core.test.data;

import es.caib.notib.core.api.dto.notificacio.NotificacioMassivaDataDto;
import es.caib.notib.core.api.dto.notificacio.NotificacioMassivaDto;
import es.caib.notib.core.api.service.NotificacioMassivaService;
import es.caib.notib.core.test.AuthenticationTest;
import es.caib.notib.core.test.NotificacioMassivaTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class NotificacioMassivaCreator extends DatabaseItemTest<NotificacioMassivaDataDto>{
    @Autowired
    protected NotificacioMassivaService notificacioMassivaService;
    @Autowired
    protected AuthenticationTest authenticationTest;
    @Autowired
    protected ProcedimentItemTest procedimentItemTest;

    @Override
    public NotificacioMassivaDataDto create(Object element, Long entitatId) throws Exception{
        return notificacioMassivaService.create(
                entitatId,
                ConfigTest.ADMIN_USER_CODE,
                (NotificacioMassivaDto) element);
    }

    @Override
    public void delete(Long entitatId, NotificacioMassivaDataDto object) {
        authenticationTest.autenticarUsuari("admin");
        notificacioMassivaService.delete(entitatId, object.getId());
    }

    public static NotificacioMassivaDto getTest1Instance() {
        NotificacioMassivaTests.TestMassiusFiles testData = NotificacioMassivaTests.getTest1Files();
        return NotificacioMassivaDto.builder()
                .ficheroCsvNom("csv_test1.csv")
                .ficheroZipNom("zip_test1.zip")
                .ficheroCsvBytes(testData.getCsvContent())
                .ficheroZipBytes(testData.getZipContent())
                .caducitat(new Date())
                .email("test@email.com")
                .build();
    }
    public static NotificacioMassivaDto getTest2Instance() {
        NotificacioMassivaTests.TestMassiusFiles testData = NotificacioMassivaTests.getTest2Files();
        return NotificacioMassivaDto.builder()
                .ficheroCsvNom("csv_test2.csv")
                .ficheroZipNom("zip_test2.zip")
                .ficheroCsvBytes(testData.getCsvContent())
                .ficheroZipBytes(testData.getZipContent())
                .caducitat(new Date())
                .email("test@email.com")
                .build();
    }
    public static NotificacioMassivaDto getTest3Instance() {
        NotificacioMassivaTests.TestMassiusFiles testData = NotificacioMassivaTests.getTest3Files();
        return NotificacioMassivaDto.builder()
                .ficheroCsvNom("csv_test3.csv")
                .ficheroZipNom("zip_test3.zip")
                .ficheroCsvBytes(testData.getCsvContent())
                .ficheroZipBytes(testData.getZipContent())
                .caducitat(new Date())
                .email("test@email.com")
                .build();
    }

}
