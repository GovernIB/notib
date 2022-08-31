package es.caib.notib.core.service;

import com.codahale.metrics.Timer;
import es.caib.notib.client.domini.consulta.RespostaConsultaV2;
import es.caib.notib.core.api.dto.ApiConsulta;
import es.caib.notib.core.api.dto.ArxiuDto;
import es.caib.notib.core.api.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.core.api.dto.NotificacioRegistreEstatEnumDto;
import es.caib.notib.core.entity.DocumentEntity;
import es.caib.notib.core.entity.NotificacioEntity;
import es.caib.notib.core.entity.NotificacioEnviamentEntity;
import es.caib.notib.core.helper.DocumentHelper;
import es.caib.notib.core.helper.MetricsHelper;
import es.caib.notib.core.helper.PluginHelper;
import es.caib.notib.core.repository.NotificacioEnviamentRepository;
import es.caib.notib.core.repository.NotificacioRepository;
import es.caib.notib.plugin.registre.RespostaJustificantRecepcio;
import junitparams.internal.Utils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyBoolean;

@RunWith(MockitoJUnitRunner.class)
public class ConsusltaApiRestV2Test {

    @Mock
    private MetricsHelper metricsHelper;
    @Mock
    private PluginHelper pluginHelper;
    @Mock
    private DocumentHelper documentHelper;
    @Mock
    private NotificacioEnviamentRepository notificacioEnviamentRepository;
    @Mock
    private NotificacioRepository notificacioRepository;
    @Mock
    private Timer.Context timer;
    @Mock
    Page<NotificacioEnviamentEntity> page;
    @Mock
    private Pageable pageableMock;

    private final String foo = "foo";

    @InjectMocks
    EnviamentServiceImpl enviamentService = new EnviamentServiceImpl();
    @InjectMocks
    NotificacioServiceImpl notificacioService = new NotificacioServiceImpl();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(metricsHelper.iniciMetrica()).thenReturn(timer);
    }

    @Ignore
    @Test
    public void findEnviamentsV2() {

        ApiConsulta consulta = ApiConsulta.builder().build();
        Integer total = 100;
        Integer pagina = 0;
        Integer mida = 10;
        String nif = "12345678z";
        List<NotificacioEnviamentEntity> dades = new ArrayList<>();
        Mockito.when(notificacioEnviamentRepository.countEnviaments(Mockito.anyString(), anyBoolean(), Mockito.any(Date.class), anyBoolean(), Mockito.any(Date.class),
                Mockito.any(NotificaEnviamentTipusEnumDto.class), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean())).thenReturn(total);
        Pageable pageable = new PageRequest(pagina, mida);
        Mockito.when(notificacioEnviamentRepository.findEnviaments(nif, true, null, true, null, null,
                true, null, true, null, Mockito.any(Pageable.class))).thenReturn(page);

        RespostaConsultaV2 resposta = enviamentService.findEnviamentsV2(consulta);

        assertNotNull(resposta);
//        assertNotNull(resposta.getResultat());
    }

    @Test
    public void getDocumentArxiu() {

        DocumentEntity doc = new DocumentEntity();
        NotificacioEntity not = NotificacioEntity.builder().document(doc).build();
        ArxiuDto arxiu = ArxiuDto.builder().build();

        Mockito.when(notificacioRepository.findById(Mockito.anyLong())).thenReturn(not);
        Mockito.when(documentHelper.documentToArxiuDto(Mockito.anyString(), Mockito.any(DocumentEntity.class))).thenReturn(arxiu);
        ArxiuDto d = notificacioService.getDocumentArxiu(1l);
        assertNotNull(d);
        assertEquals(arxiu, d);
    }

    @Test
    public void enviamentGetCertificacioArxiu() {

        byte [] c = new byte[0];
        ArxiuDto arxiu = ArxiuDto.builder().nom("certificacio_" + foo + ".pdf").contingut(c).build();
        NotificacioEnviamentEntity env = NotificacioEnviamentEntity.builder().notificaIdentificador(foo).notificaCertificacioArxiuId(foo).build();
        Mockito.when(notificacioEnviamentRepository.findOne(Mockito.anyLong())).thenReturn(env);
        ArxiuDto a = notificacioService.enviamentGetCertificacioArxiu(1l);
        assertNotNull(a);
        assertEquals(a, arxiu);
    }

    @Test
    public void getJustificant() {


        RespostaJustificantRecepcio resposta = crearRespostaJustificant(foo, false);
        Mockito.when(pluginHelper.obtenirJustificant(Mockito.anyString(), Mockito.anyString())).thenReturn(resposta);
        byte [] justificant = enviamentService.getDocumentJustificant(Mockito.anyLong());
        assertNotNull(justificant);
        assertEquals(foo, new String(justificant));
    }

    @Test
    public void getJustificant_ofici_extern() {

        RespostaJustificantRecepcio resposta = crearRespostaJustificant(foo, true);
        Mockito.when(pluginHelper.obtenirOficiExtern(Mockito.anyString(), Mockito.anyString())).thenReturn(resposta);
        byte [] justificant = enviamentService.getDocumentJustificant(Mockito.anyLong());
        assertNotNull(justificant);
        assertEquals(foo, new String(justificant));
    }

    private RespostaJustificantRecepcio crearRespostaJustificant(String justificant, boolean oficiExtern) {

        RespostaJustificantRecepcio resposta = new RespostaJustificantRecepcio();
        resposta.setJustificant(justificant.getBytes());
        NotificacioEntity not = NotificacioEntity.builder().emisorDir3Codi(justificant).build();
        NotificacioEnviamentEntity env = NotificacioEnviamentEntity.builder().notificacio(not).registreNumeroFormatat(justificant).build();
        if (oficiExtern) {
            env.setRegistreEstat(NotificacioRegistreEstatEnumDto.OFICI_EXTERN);
        }
        Mockito.when(notificacioEnviamentRepository.findById(Mockito.anyLong())).thenReturn(env);
        return resposta;
    }
}
