package es.caib.notib.logic.helper;

import es.caib.notib.logic.intf.dto.EntitatDto;
import es.caib.notib.logic.intf.dto.config.ConfigDto;
import es.caib.notib.persist.entity.config.ConfigEntity;
import es.caib.notib.persist.entity.config.ConfigGroupEntity;
import es.caib.notib.persist.repository.config.ConfigGroupRepository;
import es.caib.notib.persist.repository.config.ConfigRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RunWith(MockitoJUnitRunner.class)
public class ConfigHelperTest {
    @Mock
    private ConfigRepository configRepository;
    @Mock
    private ConfigGroupRepository configGroupRepository;
    @Mock
    private ConfigurableEnvironment environment;

    @InjectMocks
    private ConfigHelper configHelper;

    private final String MAX_INTENTS_CALLBACK = "10";

    private final String entitatCodi = "TEST";
    private final String configKey = ".propietat.de.test";
    private final String valorGlobal = "valor_global";
    private final String valorEntitat = "valor_entitat";
    private static ThreadLocal<EntitatDto> entitat = new ThreadLocal<>();

    @Before
    public void setUp() throws Exception {
        ConfigHelper.setEntitatCodi(entitatCodi);

        Mockito.when(environment.getPropertySources()).thenReturn(new MutablePropertySources());
        Mockito.when(environment.getProperty(Mockito.eq("PROPERTY_KEY"))).thenReturn("PROPERTY_VALUE");
        Mockito.when(environment.getProperty(Mockito.eq(ConfigDto.prefix + configKey))).thenReturn("valor_global");
        Mockito.when(environment.getProperty(Mockito.eq(ConfigDto.prefix + "." + entitatCodi + configKey))).thenReturn("valor_entitat");
        Mockito.when(environment.getProperty(Mockito.eq("CONFIG_1_1_KEY"))).thenReturn("CONFIG_1_1");
        Mockito.when(environment.getProperty(Mockito.eq("CONFIG_1_2_KEY"))).thenReturn("CONFIG_1_2");
        Mockito.when(environment.getProperty(Mockito.eq("CONFIG_2_1_KEY"))).thenReturn("CONFIG_2_1");
        Mockito.when(environment.getProperty(Mockito.eq("CONFIG_2_2_KEY"))).thenReturn("CONFIG_2_2");


        configHelper.reloadDbProperties();
    }

    @Test
    public void getPropertyGlobal() throws Exception {

        ConfigHelper.setEntitatCodi("NO_ENTITAT");
        String valorGlo = configHelper.getConfig(ConfigDto.prefix + configKey);
        Assert.assertEquals(valorGlobal, valorGlo);
    }

    @Test
    public void getPropertyEntitat() throws Exception {

        ConfigEntity config = new ConfigEntity(ConfigDto.prefix + configKey, "valor_global");
        config.setConfigurable(true);
        String valorEnt = configHelper.getConfig(ConfigDto.prefix + configKey);
        Assert.assertEquals(valorEntitat, valorEnt);
    }

    @Test
    public void crearEntitatKeyTest() throws Exception {

        // When
        String propertyValue = configHelper.crearEntitatKey(entitatCodi, ConfigDto.prefix + configKey);
        String propertyValue2 = configHelper.crearEntitatKey(entitatCodi, configKey);

        // Then
        Assert.assertEquals(ConfigDto.prefix + "." +entitatCodi + configKey, propertyValue);
        Assert.assertEquals(configKey, propertyValue2);
    }

    @Test(expected = RuntimeException.class)
    public void crearEntitatKeyTest_throwException() throws Exception {
        // When
        configHelper.crearEntitatKey(entitatCodi, null);
    }


    @Test
    public void whenGetConfig_ThenReturnConfigValue() throws Exception {
        // When
        String propertyValue = configHelper.getConfig("PROPERTY_KEY");

        // Then
        Assert.assertEquals("PROPERTY_VALUE", propertyValue);
    }

    @Test
    public void GivenNonExistingKey_whenGetConfig_ThenException() throws Exception {
        // When
        String propertyValue = configHelper.getConfig("PROPERTY_INEXISTENT");
        Assert.assertNull(propertyValue);
    }

    @Test
    public void whenGetGroupProperties_ThenGetAllGroupProperties() throws Exception {
        // Given
        Set<ConfigEntity> configEntities_g1 = new HashSet<>();
        configEntities_g1.add(new ConfigEntity("CONFIG_1_1_KEY", "CONFIG_1_1"));
        configEntities_g1.add(new ConfigEntity("CONFIG_1_2_KEY", "CONFIG_1_2"));

        Set<ConfigEntity> configEntities_g2 = new HashSet<>();
        configEntities_g2.add(new ConfigEntity("CONFIG_2_1_KEY", "CONFIG_2_1"));
        configEntities_g2.add(new ConfigEntity("CONFIG_2_2_KEY", "CONFIG_2_2"));

        Set<ConfigGroupEntity> innerConfigs = new HashSet<>();
        innerConfigs.add(ConfigGroupEntity.builder()
                .key("GROUP_2")
                .configs(configEntities_g2)
                .innerConfigs(new HashSet<ConfigGroupEntity>())
                .build());
        ConfigGroupEntity configGroup_1 = ConfigGroupEntity.builder()
                .key("GROUP_1")
                .configs(configEntities_g1)
                .innerConfigs(innerConfigs)
                .build();

        Mockito.when(configGroupRepository.findById(Mockito.eq("GROUP_1"))).thenReturn(Optional.of(configGroup_1));

        // When
        Map<String, String> groupProperties = configHelper.getGroupProperties("GROUP_1");

        // Then
        Assert.assertNotNull(groupProperties.get("CONFIG_1_1_KEY"));
        Assert.assertNotNull(groupProperties.get("CONFIG_1_2_KEY"));
        Assert.assertNotNull(groupProperties.get("CONFIG_2_1_KEY"));
        Assert.assertNotNull(groupProperties.get("CONFIG_2_2_KEY"));

        Assert.assertEquals("CONFIG_1_1", groupProperties.get("CONFIG_1_1_KEY"));
        Assert.assertEquals("CONFIG_1_2", groupProperties.get("CONFIG_1_2_KEY"));
        Assert.assertEquals("CONFIG_2_1", groupProperties.get("CONFIG_2_1_KEY"));
        Assert.assertEquals("CONFIG_2_2", groupProperties.get("CONFIG_2_2_KEY"));
    }

}