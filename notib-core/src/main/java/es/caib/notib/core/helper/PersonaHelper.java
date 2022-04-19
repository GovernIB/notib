package es.caib.notib.core.helper;


import es.caib.notib.client.domini.Persona;
import es.caib.notib.core.entity.PersonaEntity;
import es.caib.notib.core.repository.PersonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Helper per manipular persones
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class PersonaHelper {
    @Autowired
    private PersonaRepository personaRepository;

    public PersonaEntity create(Persona persona, boolean incapacitat) {

        PersonaEntity p = PersonaEntity.getBuilderV2(
                persona.getInteressatTipus(),
                persona.getEmail(),
                persona.getLlinatge1(),
                persona.getLlinatge2(),
                persona.getNif(),
                persona.getNom(),
                persona.getTelefon(),
                persona.getRaoSocial(),
                persona.getDir3Codi()).incapacitat(incapacitat).build();
        p.setDocumentTipus(persona.getDocumentTipus());
        return personaRepository.saveAndFlush(p);
    }

    public PersonaEntity update(Persona persona, boolean incapacitat) {
        PersonaEntity personaEntity = personaRepository.findOne(persona.getId());
        personaEntity.update(
                persona.getInteressatTipus(),
                persona.getEmail(),
                persona.getLlinatge1(),
                persona.getLlinatge2(),
                persona.getNif(),
                persona.getNom(),
                persona.getTelefon(),
                persona.getRaoSocial(),
                persona.getDir3Codi(),
                incapacitat);
        return personaEntity;
    }
}
