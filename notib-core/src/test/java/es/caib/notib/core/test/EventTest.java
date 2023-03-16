package es.caib.notib.core.test;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/es/caib/notib/core/application-context-test.xml"})
@Transactional
public class EventTest {

    @BeforeClass
    public void classSetUp() {

    }

    @Before
    public void setUp() {

    }

    @Test
    public void whenCreateThenNoEventAndNoError() {

    }

    @Test
    public void whenRegistreErrorThenEventRegistreError() {

    }

    @Test
    public void whenRegistreThenEventRegistreOk() {

    }

    @Test
    public void whenSirErrorThenEventSirError() {

    }

    @Test
    public void whenSirThenEventSirOk() {

    }

    @Test
    public void whenConsultaSirErrorThenEventConsultaSirError() {

    }

    @Test
    public void whenConsultaSirThenEventConsultaSirOk() {

    }

    @Test
    public void whenNotificacioErrorThenEventNotificacioError() {

    }

    @Test
    public void whenNotificacioThenEventNotificacioOk() {

    }

    @Test
    public void whenConsultaNotificacioErrorThenEventConsultaNotificacioError() {

    }

    @Test
    public void whenConsultaNotificacioThenEventConsultaNotificacioOk() {

    }

    @Test
    public void whenAdviserCertErrorThenEventAdviserCertError() {

    }

    @Test
    public void whenAdviserCertThenEventAdviserCertOk() {

    }

    @Test
    public void whenAdviserDatatErrorThenEventAdviserDatatError() {

    }

    @Test
    public void whenAdviserDatatThenEventAdviserDatatOk() {

    }

    @Test
    public void whenEmailErrorThenEventEmailError() {

    }

    @Test
    public void whenEmailThenEventEmailOk() {

    }

}
