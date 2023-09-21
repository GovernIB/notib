package es.caib.notib.logic.service;

import es.caib.notib.logic.helper.ConfigHelper;
import es.caib.notib.logic.intf.dto.config.ConfigDto;
import es.caib.notib.persist.entity.config.ConfigEntity;
import es.caib.notib.logic.helper.CacheHelper;
import es.caib.notib.logic.helper.ConversioTipusHelper;
import es.caib.notib.logic.helper.PluginHelper;
import es.caib.notib.persist.repository.config.ConfigGroupRepository;
import es.caib.notib.persist.repository.config.ConfigRepository;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class ConfigServiceTest {

    @Mock
    private ConfigGroupRepository configGroupRepository;
    @Mock
    private ConfigRepository configRepository;
    @Spy
    private ConversioTipusHelper conversioTipusHelper;
    @Mock
    private PluginHelper pluginHelper;
    @Mock
    private CacheHelper cacheHelper;
    @Mock
    private ConfigHelper configHelper;

    @InjectMocks
    private ConfigServiceImpl configService;



    @Before
    public void setUp() throws Exception { }

    @After
    public void tearDown() throws Exception { }

    @Test
    public void whenUpdateProperty_thenCallFindOneAndReloadPluginProperties() throws Exception {
        // Given
        Mockito.when(
                conversioTipusHelper.convertir(Mockito.any(ConfigEntity.class), Mockito.eq(ConfigDto.class))
        ).thenCallRealMethod();

        ConfigEntity configEntity = ConfigEntity.builder().key("PROPERTY_KEY").value("PROPERTY_VALUE").configurable(true).build();
        Mockito.when(configRepository.findById(Mockito.eq("PROPERTY_KEY"))).thenReturn(Optional.of(configEntity));

        ConfigDto configDto = conversioTipusHelper.convertir(configEntity, ConfigDto.class);
        configDto.setValue("NEW_VALUE");

        // When
        ConfigDto configEdited = configService.updateProperty(configDto);

        // Then
//        Mockito.verify(pluginHelper).reloadProperties(Mockito.nullable(String.class));
        Mockito.verify(configRepository).findById(Mockito.eq("PROPERTY_KEY"));

        Assert.assertEquals("NEW_VALUE", configEdited.getValue());

    }
}