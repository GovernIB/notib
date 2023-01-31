package es.caib.notib.logic.test.data;

import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.client.domini.Idioma;
import es.caib.notib.client.domini.InteressatTipus;
import es.caib.notib.logic.intf.dto.DocumentDto;
import es.caib.notib.logic.intf.dto.NotificaEnviamentTipusEnumDto;
import es.caib.notib.logic.intf.dto.PersonaDto;
import es.caib.notib.logic.intf.dto.ServeiTipusEnumDto;
import es.caib.notib.logic.intf.dto.notenviament.NotEnviamentDatabaseDto;
import es.caib.notib.logic.intf.dto.notificacio.NotificacioDatabaseDto;
import es.caib.notib.logic.intf.dto.procediment.ProcSerDto;
import es.caib.notib.logic.intf.service.NotificacioService;
import es.caib.notib.logic.test.AuthenticationTest;
import lombok.Getter;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component
public class NotificacioItemTest extends DatabaseItemTest<NotificacioDatabaseDto>{
    @Autowired
    protected NotificacioService notificacioService;
    @Autowired
    protected AuthenticationTest authenticationTest;
    @Autowired
    protected ProcedimentItemTest procedimentItemTest;

    @Getter
    private String[] relatedFields = new String[]{ "procediment" };

    @Override
    public NotificacioDatabaseDto create(Object element, Long entitatId) throws Exception{
        return notificacioService.create(
                entitatId,
                (NotificacioDatabaseDto) element);
    }

    @Override
    public void delete(Long entitatId, NotificacioDatabaseDto object) {
        authenticationTest.autenticarUsuari("admin");
        notificacioService.delete(entitatId, object.getId());
    }

//    @Override
//    public NotificacioDatabaseDto getRandomInstance() throws Exception{
//        throw new Exception("Can't build a fully random notificacio");
//    }

    public void relateElement(String key, Object element) throws Exception{
        if (element instanceof ProcSerDto) {
            getObject(key).setProcediment((ProcSerDto) element);
        }
    }

    public static NotificacioDatabaseDto getRandomInstanceWithoutEnviaments() {
        String notificacioId = new Long(System.currentTimeMillis()).toString();

        DocumentDto document = new DocumentDto();
        try {
            byte[] arxiuBytes = IOUtils.toByteArray(getContingutNotificacioAdjunt());
            document.setContingutBase64(Base64.getEncoder().encodeToString(arxiuBytes));
            document.setHash(
                    Base64.getEncoder().encodeToString(
                            Hex.decodeHex(
                                    DigestUtils.sha1Hex(arxiuBytes).toCharArray())));
        } catch (IOException | DecoderException e) {
            e.printStackTrace();
        }

        document.setArxiuNom("documentArxiuNom_" + notificacioId + ".pdf");
        document.setNormalitzat(false);
        document.setGenerarCsv(false);

        Date caducitat = new Date(System.currentTimeMillis() + 10 * 24 * 3600 * 1000);
        Date enviamentDataProgramada = new Date(System.currentTimeMillis() + 10 * 24 * 3600 * 1000);
        NotificacioDatabaseDto notCreated = NotificacioDatabaseDto.builder()
                .emisorDir3Codi(ConfigTest.ENTITAT_DGTIC_DIR3CODI)
                .enviamentTipus(NotificaEnviamentTipusEnumDto.NOTIFICACIO)
                .enviamentDataProgramada(enviamentDataProgramada)
                .concepte("Test")
                .descripcio("Test descripció")
                .organGestorCodi("A00000000")
                .enviamentDataProgramada(new Date())
                .retard(5)
                .caducitat(caducitat)
//                .procediment(procediment)
//				.procedimentCodiNotib()
//                .grup(grupCreate)
                .enviaments(new ArrayList<NotEnviamentDatabaseDto>())
                .usuariCodi("admin")
//				.motiu()
                .numExpedient("EXPEDIENTEX")
                .idioma(Idioma.CA)
                .document(new DocumentDto())
                .build();
        notCreated.setDocument(document);
        return notCreated;
    }

    public static NotificacioDatabaseDto getRandomInstance() {
        return getRandomInstance(2);
    }

    public static NotificacioDatabaseDto getRandomInstance(int numEnviaments) {
        NotificacioDatabaseDto notCreated = getRandomInstanceWithoutEnviaments();
        List<NotEnviamentDatabaseDto> enviaments = new ArrayList<>();
        for (int i = 0; i < numEnviaments; i++) {
            NotEnviamentDatabaseDto enviament = getRandomEnviament(i);
            enviaments.add(enviament);
        }
        notCreated.setEnviaments(enviaments);

        return notCreated;
    }

    public static NotEnviamentDatabaseDto getRandomEnviament(int i){
        NotEnviamentDatabaseDto enviament = new NotEnviamentDatabaseDto();
        PersonaDto titular = PersonaDto.builder()
                .interessatTipus(InteressatTipus.FISICA)
                .nom("titularNom" + i)
                .llinatge1("titLlinatge1_" + i)
                .llinatge2("titLlinatge2_" + i)
                .nif("00000000T")
                .telefon("666010101")
                .email("titular@gmail.com").build();
        enviament.setTitular(titular);
        List<PersonaDto> destinataris = new ArrayList<PersonaDto>();
        PersonaDto destinatari = PersonaDto.builder()
                .interessatTipus(InteressatTipus.FISICA)
                .nom("destinatariNom" + i)
                .llinatge1("destLlinatge1_" + i)
                .llinatge2("destLlinatge2_" + i)
                .nif("12345678Z")
                .telefon("666020202")
                .email("destinatari@gmail.com").build();
        destinataris.add(destinatari);
        enviament.setDestinataris(destinataris);
        enviament.setServeiTipus(ServeiTipusEnumDto.URGENT);
        enviament.setNotificaEstat(EnviamentEstat.NOTIB_PENDENT);
        return enviament;
    }

    private static InputStream getContingutNotificacioAdjunt() {
        return NotificacioItemTest.class.getResourceAsStream(
                "/es/caib/notib/logic/notificacio_adjunt.pdf");
    }
}
