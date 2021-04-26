package es.caib.notib.core.service;

import es.caib.notib.core.api.dto.notificacio.NotificacioDtoV2;
import es.caib.notib.core.api.exception.RegistreNotificaException;
import es.caib.notib.core.api.service.AplicacioService;
import es.caib.notib.core.api.service.NotificacioService;
import es.caib.notib.core.api.service.ProcedimentService;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.helper.*;
import es.caib.notib.core.repository.DocumentRepository;
import es.caib.notib.core.repository.EntitatRepository;
import es.caib.notib.core.repository.NotificacioEnviamentRepository;
import es.caib.notib.core.repository.NotificacioEventRepository;
import es.caib.notib.core.repository.NotificacioRepository;
import es.caib.notib.core.repository.NotificacioTableViewRepository;
import es.caib.notib.core.repository.OrganGestorRepository;
import es.caib.notib.core.repository.PersonaRepository;
import es.caib.notib.core.repository.ProcedimentRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NotificacioServiceTest {

    @Mock
    private EntityComprovarHelper entityComprovarHelper;
    @Mock
    private ConversioTipusHelper conversioTipusHelper;
    @Mock
    private PaginacioHelper paginacioHelper;
    @Mock
    private NotificaHelper notificaHelper;
    @Mock
    private PluginHelper pluginHelper;
    @Mock
    private NotificacioRepository notificacioRepository;
    @Mock
    private NotificacioEnviamentRepository notificacioEnviamentRepository;
    @Mock
    private NotificacioEventRepository notificacioEventRepository;
    @Mock
    private NotificacioTableViewRepository notificacioTableViewRepository;
    @Mock
    private EntitatRepository entitatRepository;
    @Mock
    private DocumentRepository documentRepository;
    @Mock
    private PersonaRepository personaRepository;
    @Mock
    private ProcedimentRepository procedimentRepository;
    @Mock
    private OrganGestorRepository organGestorRepository;
    @Mock
    private EmailHelper emailHelper;
    @Mock
    private UsuariHelper usuariHelper;
    @Mock
    private RegistreNotificaHelper registreNotificaHelper;
    @Mock
    private OrganigramaHelper organigramaHelper;
    @Mock
    private RegistreHelper registreHelper;
    @Mock
    private AuditNotificacioHelper auditNotificacioHelper;
    @Mock
    private AuditEnviamentHelper auditEnviamentHelper;
    @Mock
    private AplicacioService aplicacioService;
    @Mock
    private CacheHelper cacheHelper;
    @Mock
    private MetricsHelper metricsHelper;
    @Mock
    private JustificantHelper justificantHelper;
    @Mock
    private MessageHelper messageHelper;
    @Mock
    private NotificacioHelper notificacioHelper;
    @Mock
    private IntegracioHelper integracioHelper;
    @Mock
    private ProcedimentService procedimentService;
    @Mock
    private NotificacioTableHelper notificacioTableHelper;

    @InjectMocks
    private NotificacioService notificacioService = new NotificacioServiceImpl();


    @Test
    public void whenFindById_thenReturn() throws RegistreNotificaException {

        // Given
        NotificacioEntity notificaioEntity = new NotificacioEntity();

        when(metricsHelper.iniciMetrica()).thenReturn(null);
        when(notificacioRepository.findById(anyLong())).thenReturn(notificaioEntity);

        // When
        NotificacioDtoV2 notificacioDtoV2 = notificacioService.findAmbId(
                1L,
                true
        );

        // Then
        Mockito.verify(notificacioRepository).findById(
                anyLong());
    }
}
