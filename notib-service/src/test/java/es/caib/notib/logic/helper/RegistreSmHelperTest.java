package es.caib.notib.logic.helper;

import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.logic.intf.dto.AsientoRegistralBeanDto;
import es.caib.notib.logic.intf.dto.NotificacioRegistreEstatEnumDto;
import es.caib.notib.logic.statemachine.mappers.EnviamentRegistreMapper;
import es.caib.notib.persist.entity.EntitatEntity;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.plugin.registre.RespostaConsultaRegistre;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;

import static org.junit.Assert.*;


@RunWith(MockitoJUnitRunner.class)
public class RegistreSmHelperTest {

    @Mock
    private ConfigHelper configHelper;
    @Mock
    private PluginHelper pluginHelper;
    @Mock
    private NotificacioEventHelper notificacioEventHelper;
    @Mock
    private AuditHelper auditHelper;
    @Mock
    private EnviamentTableHelper enviamentTableHelper;
    @Mock
    private CallbackHelper callbackHelper;
    @Mock
    private IntegracioHelper integracioHelper;
    @Mock
    private EnviamentRegistreMapper enviamentRegistreMapper;
    @InjectMocks
    private RegistreSmHelper registreSmHelper;

    private NotificacioEntity notificacio;
    private NotificacioEnviamentEntity enviament;
    private EntitatEntity entitat;
    private RegistreSmHelper.ReqAssentamentRegistral request;
    private RespostaConsultaRegistre arbResposta;
    private AsientoRegistralBeanDto asientoRegistral;
    private Date dataResposta;

    @Before
    public void setUp() throws Exception {

        entitat = initEntitat();
        request = Mockito.mock(RegistreSmHelper.ReqAssentamentRegistral.class);
        Mockito.when(request.getTipusOperacio()).thenReturn(2L);
        notificacio =  Mockito.mock(NotificacioEntity.class);
        Mockito.when(notificacio.getId()).thenReturn(1L);
        Mockito.when(notificacio.getEntitat()).thenReturn(entitat);
        enviament = Mockito.mock(NotificacioEnviamentEntity.class);
        Mockito.when(enviament.getId()).thenReturn(1L);
        Mockito.when(enviament.getNotificacio()).thenReturn(notificacio);
//        Mockito.when(enviament.getTitular()).thenReturn(titular);
        Mockito.when(pluginHelper.getRegistreReintentsMaxProperty()).thenReturn(3);
        asientoRegistral = Mockito.mock(AsientoRegistralBeanDto.class);
        Mockito.when(enviamentRegistreMapper.toAsientoRegistral(enviament)).thenReturn(asientoRegistral);
        initReposta();
//        Mockito.when(
//                pluginHelper.crearAsientoRegistral(
//                request.getDir3CodiRegistre(),
//                enviamentRegistreMapper.toAsientoRegistral(enviament),
//                request.getTipusOperacio(),
//                enviament.getNotificacio().getId(),
//                enviament.getId().toString(),
//                true)
////               pluginHelper.crearAsientoRegistral(Mockito.anyString(), Mockito.any(AsientoRegistralBeanDto.class), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString(), Mockito.eq(true))
//        ).thenReturn(arbResposta);
//
        Mockito.when(pluginHelper.crearAsientoRegistral(
                        Mockito.anyString(),
                        Mockito.any(AsientoRegistralBeanDto.class),
                        Mockito.nullable(long.class),
                        Mockito.anyLong(), Mockito.anyString(), Mockito.anyBoolean()))
                .thenReturn(arbResposta);

    }

    private void initReposta() {

        dataResposta = new Date();
        arbResposta = Mockito.mock(RespostaConsultaRegistre.class);
        Mockito.when(arbResposta.getErrorCodi()).thenReturn(null);
        Mockito.when(arbResposta.getEstat()).thenReturn(NotificacioRegistreEstatEnumDto.OFICI_ACCEPTAT);
        Mockito.when(arbResposta.getRegistreNumeroFormatat()).thenReturn("170097/2023");
        Mockito.when(arbResposta.getRegistreNumero()).thenReturn("170097");
        Mockito.when(arbResposta.getRegistreData()).thenReturn(dataResposta);
    }

    @Test
    public void check_parametres_entrada_sortida_notificacio() throws Exception {

        // Given
        Mockito.when(notificacio.getEnviamentTipus()).thenReturn(EnviamentTipus.NOTIFICACIO);
        Mockito.when(configHelper.getConfigAsBoolean("es.caib.notib.emprar.sir")).thenReturn(false);

        // When
        var resposta = registreSmHelper.registrarEnviament(enviament, 1);

        // Then
        assertTrue(resposta);
        assertEquals("170097",arbResposta.getRegistreNumero());
        assertEquals("170097/2023",arbResposta.getRegistreNumeroFormatat());
        assertEquals(dataResposta,arbResposta.getRegistreData());

    }

    @Test
    public void check_parametres_entrada_sir() throws Exception {

        // Given
        Mockito.when(notificacio.getEnviamentTipus()).thenReturn(EnviamentTipus.SIR);
        Mockito.when(configHelper.getConfigAsBoolean("es.caib.notib.emprar.sir")).thenReturn(true);

        // When
        var resposta = registreSmHelper.registrarEnviament(enviament, 1);

        // Then
        assertTrue(resposta);
        assertEquals("170097",arbResposta.getRegistreNumero());
        assertEquals("170097/2023",arbResposta.getRegistreNumeroFormatat());
        assertEquals(dataResposta,arbResposta.getRegistreData());
    }

    private EntitatEntity initEntitat() {

        return EntitatEntity.getBuilder("codi",
                        "nom",
                        null,
                        "dir3Codi",
                        "dir3CodiReg",
                        "apiKey",
                        false,
                        //				false,
                        null,
                        null,
                        "colorFons",
                        "colorLletra",
                        null,
                        "oficina",
                        "nomOficinaVirtual",
                        false,
                        "llibre",
                        "llibreNom",
                        false)
                .build();
    }
}
