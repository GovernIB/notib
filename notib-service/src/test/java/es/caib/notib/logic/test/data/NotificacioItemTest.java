package es.caib.notib.logic.test.data;

import es.caib.notib.logic.intf.dto.notificacio.Document;
import es.caib.notib.client.domini.EnviamentEstat;
import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.client.domini.Idioma;
import es.caib.notib.client.domini.InteressatTipus;
import es.caib.notib.client.domini.ServeiTipus;
import es.caib.notib.logic.intf.dto.notificacio.Enviament;
import es.caib.notib.logic.intf.dto.notificacio.Notificacio;
import es.caib.notib.logic.intf.dto.notificacio.Persona;
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
public class NotificacioItemTest extends DatabaseItemTest<Notificacio>{

    @Autowired
    protected NotificacioService notificacioService;
    @Autowired
    protected AuthenticationTest authenticationTest;
    @Autowired
    protected ProcedimentItemTest procedimentItemTest;

    @Getter
    private String[] relatedFields = new String[]{ "procediment" };

    @Override
    public Notificacio create(Object element, Long entitatId) throws Exception{
        return notificacioService.create(entitatId, (Notificacio) element);
    }

    @Override
    public void delete(Long entitatId, Notificacio object) {

        authenticationTest.autenticarUsuari("admin");
        notificacioService.delete(entitatId, object.getId());
    }

//    @Override
//    public NotificacioDatabaseDto getRandomInstance() throws Exception{
//        throw new Exception("Can't build a fully random notificacio");
//    }

    public void relateElement(String key, Object element) throws Exception{

        if (element instanceof ProcSerDto) {
            getObject(key).setProcedimentCodi(((ProcSerDto) element).getCodi());
        }
    }

    public static Notificacio getRandomInstanceWithoutEnviaments() {

        String notificacioId = Long.toString(System.currentTimeMillis());
        var document = new Document();
        try {
            byte[] arxiuBytes = IOUtils.toByteArray(getContingutNotificacioAdjunt());
            document.setContingutBase64(Base64.getEncoder().encodeToString(arxiuBytes));
            document.setHash(Base64.getEncoder().encodeToString(Hex.decodeHex(DigestUtils.sha1Hex(arxiuBytes).toCharArray())));
        } catch (IOException | DecoderException e) {
            e.printStackTrace();
        }

        document.setArxiuNom("documentArxiuNom_" + notificacioId + ".pdf");
        document.setNormalitzat(false);
        document.setGenerarCsv(false);

        Date caducitat = new Date(System.currentTimeMillis() + 10 * 24 * 3600 * 1000);
        Date enviamentDataProgramada = new Date(System.currentTimeMillis() + 10 * 24 * 3600 * 1000);
        Notificacio notCreated = Notificacio.builder()
                .emisorDir3Codi(ConfigTest.ENTITAT_DGTIC_DIR3CODI)
                .enviamentTipus(EnviamentTipus.NOTIFICACIO)
                .enviamentDataProgramada(enviamentDataProgramada)
                .concepte("Test")
                .descripcio("Test descripció")
                .organGestor("A00000000")
                .enviamentDataProgramada(new Date())
                .retard(5)
                .caducitat(caducitat)
//                .procediment(procediment)
//				.procedimentCodiNotib()
//                .grup(grupCreate)
                .enviaments(new ArrayList<Enviament>())
                .usuariCodi("admin")
//				.motiu()
                .numExpedient("EXPEDIENTEX")
                .idioma(Idioma.CA)
                .document(new Document())
                .build();
        notCreated.setDocument(document);
        return notCreated;
    }

    public static Notificacio getRandomInstance() {
        return getRandomInstance(2);
    }

    public static Notificacio getRandomInstance(int numEnviaments) {

        Notificacio notCreated = getRandomInstanceWithoutEnviaments();
        List<Enviament> enviaments = new ArrayList<>();
        for (int i = 0; i < numEnviaments; i++) {
            Enviament enviament = getRandomEnviament(i);
            enviaments.add(enviament);
        }
        notCreated.setEnviaments(enviaments);
        return notCreated;
    }

    public static Enviament getRandomEnviament(int i){

        var enviament = new Enviament();
        var titular = Persona.builder()
                .interessatTipus(InteressatTipus.FISICA)
                .nom("titularNom" + i)
                .llinatge1("titLlinatge1_" + i)
                .llinatge2("titLlinatge2_" + i)
                .nif("00000000T")
                .telefon("666010101")
                .email("titular@gmail.com").build();
        enviament.setTitular(titular);
        List<Persona> destinataris = new ArrayList<>();
        var destinatari = Persona.builder()
                .interessatTipus(InteressatTipus.FISICA)
                .nom("destinatariNom" + i)
                .llinatge1("destLlinatge1_" + i)
                .llinatge2("destLlinatge2_" + i)
                .nif("12345678Z")
                .telefon("666020202")
                .email("destinatari@gmail.com").build();
        destinataris.add(destinatari);
        enviament.setDestinataris(destinataris);
        enviament.setServeiTipus(ServeiTipus.URGENT);
        enviament.setNotificaEstat(EnviamentEstat.NOTIB_PENDENT);
        return enviament;
    }

    private static InputStream getContingutNotificacioAdjunt() {
        return NotificacioItemTest.class.getResourceAsStream(
                "/es/caib/notib/logic/notificacio_adjunt.pdf");
    }
}
