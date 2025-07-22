package es.caib.notib.logic.service;

import com.codahale.metrics.Timer;
import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.client.domini.consulta.RespostaConsultaV2;
import es.caib.notib.logic.intf.dto.ApiConsulta;
import es.caib.notib.logic.intf.dto.ArxiuDto;
import es.caib.notib.logic.intf.dto.NotificacioRegistreEstatEnumDto;
import es.caib.notib.persist.entity.DocumentEntity;
import es.caib.notib.persist.entity.NotificacioEntity;
import es.caib.notib.persist.entity.NotificacioEnviamentEntity;
import es.caib.notib.logic.helper.DocumentHelper;
import es.caib.notib.logic.helper.MetricsHelper;
import es.caib.notib.logic.helper.PluginHelper;
import es.caib.notib.persist.filtres.FiltreConsultaEviament;
import es.caib.notib.persist.repository.NotificacioEnviamentRepository;
import es.caib.notib.persist.repository.NotificacioRepository;
import es.caib.notib.plugin.registre.RespostaJustificantRecepcio;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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

        var consulta = ApiConsulta.builder().build();
        Integer total = 100;
        Integer pagina = 0;
        Integer mida = 10;
        String nif = "12345678z";
        var filtre = FiltreConsultaEviament.builder()
                .dniTitular(Mockito.anyString())
                .esDataInicialNull(anyBoolean())
                .dataInicial(Mockito.any(Date.class))
                .esDataFinalNull(anyBoolean())
                .dataFinal(Mockito.any(Date.class))
                .tipusNull(anyBoolean())
                .tipus(Mockito.any(EnviamentTipus.class))
                .esEstatFinalNull(anyBoolean())
                .estatFinal(anyBoolean())
                .esVisibleCarpetaNull(anyBoolean())
                .visibleCarpeta(anyBoolean()).build();
        Mockito.when(notificacioEnviamentRepository.countEnviaments(filtre)).thenReturn(total);
        var pageable = PageRequest.of(pagina, mida);
        filtre.setDniTitular(nif);
        Mockito.when(notificacioEnviamentRepository.findEnviaments(filtre, pageable)).thenReturn(page);
        var resposta = enviamentService.findEnviamentsV2(consulta);
        assertNotNull(resposta);
    }

    @Test
    public void getDocumentArxiu() {

        var doc = new DocumentEntity();
        var not = NotificacioEntity.builder().document(doc).build();
        var arxiu = ArxiuDto.builder().build();
        Mockito.when(notificacioRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(not));
        Mockito.when(documentHelper.documentToArxiuDto(Mockito.anyString(), Mockito.any(DocumentEntity.class))).thenReturn(arxiu);
        var d = notificacioService.getDocumentArxiu(1L);
        assertNotNull(d);
        assertEquals(arxiu, d);
    }

    @Test
    public void enviamentGetCertificacioArxiu() throws Exception {

        byte [] c = new byte[0];
        var arxiu = ArxiuDto.builder().nom("certificacio_" + foo + ".pdf").contingut(c).build();
        var env = NotificacioEnviamentEntity.builder().notificaIdentificador(foo).notificaCertificacioArxiuId(foo).build();
        Mockito.when(notificacioEnviamentRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(env));
        var a = notificacioService.enviamentGetCertificacioArxiu(1L);
        assertNotNull(a);
        assertEquals(a, arxiu);
    }

    @Test
    public void getJustificant() {

        var resposta = crearRespostaJustificant(foo, false);
        Mockito.when(pluginHelper.obtenirJustificant(Mockito.anyString(), Mockito.anyString())).thenReturn(resposta);
        var justificant = enviamentService.getDocumentJustificant(Mockito.anyLong());
        assertNotNull(justificant);
        assertEquals(foo, new String(justificant));
    }

    @Test
    public void getJustificant_ofici_extern() {

        var resposta = crearRespostaJustificant(foo, true);
        Mockito.when(pluginHelper.obtenirOficiExtern(Mockito.anyString(), Mockito.anyString())).thenReturn(resposta);
        var justificant = enviamentService.getDocumentJustificant(Mockito.anyLong());
        assertNotNull(justificant);
        assertEquals(foo, new String(justificant));
    }

    private RespostaJustificantRecepcio crearRespostaJustificant(String justificant, boolean oficiExtern) {

        var resposta = new RespostaJustificantRecepcio();
        resposta.setJustificant(justificant.getBytes());
        var not = NotificacioEntity.builder().emisorDir3Codi(justificant).build();
        var env = NotificacioEnviamentEntity.builder().notificacio(not).registreNumeroFormatat(justificant).build();
        if (oficiExtern) {
            env.setRegistreEstat(NotificacioRegistreEstatEnumDto.OFICI_EXTERN);
        }
        Mockito.when(notificacioEnviamentRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(env));
        return resposta;
    }
}
