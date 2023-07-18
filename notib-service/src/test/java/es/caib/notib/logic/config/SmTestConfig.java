package es.caib.notib.logic.config;

import es.caib.notib.logic.helper.RegistreSmHelper;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("test")
public class SmTestConfig {

//    @Bean
//    @Primary
//    public EnviamentRegistreListener enviamentRegistreListener() {
//        return Mockito.mock(EnviamentRegistreListener.class);
//    }
//
//    @Bean
//    @Primary
//    public EnviamentNotificaListener enviamentNotificaListener() {
//        return Mockito.mock(EnviamentNotificaListener.class);
//    }
//
//    @Bean
//    @Primary
//    public EnviamentEmailListener enviamentEmailListener() {
//        return Mockito.mock(EnviamentEmailListener.class);
//    }
//
//    @Bean
//    @Primary
//    public ConsultaSirListener consultaSirListener() {
//        return Mockito.mock(ConsultaSirListener.class);
//    }
//
//    @Bean
//    @Primary
//    public ConsultaNotificaListener consultaNotificaListener() {
//        return Mockito.mock(ConsultaNotificaListener.class);
//    }

    @Bean
    @Primary
    public RegistreSmHelper registreSmHelper() {
        return Mockito.mock(RegistreSmHelper.class);
    }

}
