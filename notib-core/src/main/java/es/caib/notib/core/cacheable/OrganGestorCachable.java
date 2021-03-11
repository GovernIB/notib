package es.caib.notib.core.cacheable;

import es.caib.notib.core.api.dto.OrganismeDto;
import es.caib.notib.core.helper.CacheHelper;
import es.caib.notib.plugin.unitat.NodeDir3;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * Utilitat per a accedir a les caches dels organs gestors. Els mètodes cacheables es
 * defineixen aquí per evitar la impossibilitat de fer funcionar
 * l'anotació @Cacheable als mètodes privats degut a limitacions
 * AOP.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class OrganGestorCachable {
    @Resource
    private CacheHelper cacheHelper;

    @Cacheable(value = "organigrama", key="#entitatcodi")
    public Map<String, OrganismeDto> findOrganigramaByEntitat(String entitatcodi) {
        Map<String, OrganismeDto> organigrama = new HashMap<String, OrganismeDto>();
        Map<String, NodeDir3> organigramaDir3 = cacheHelper.findOrganigramaNodeByEntitat(entitatcodi);
        if (organigramaDir3 != null) {
            for (String organ : organigramaDir3.keySet()) {
                organigrama.put(organ, nodeDir3ToOrganisme(organigramaDir3.get(organ)));
            }
        }
        return organigrama;
    }

    @Cacheable(value = "codisOrgansFills", key="#codiDir3Entitat.concat('-').concat(#codiDir3Organ)")
    public List<String> getCodisOrgansGestorsFillsByOrgan(String codiDir3Entitat, String codiDir3Organ) {
        Map<String, OrganismeDto> organigramaEntitat = findOrganigramaByEntitat(codiDir3Entitat);

        String codiDir3 = codiDir3Organ != null ? codiDir3Organ : codiDir3Entitat;

        List<String> unitatsEntitat = new ArrayList<String>();
        unitatsEntitat.addAll(getCodisOrgansGestorsFills(organigramaEntitat, codiDir3));

        return unitatsEntitat;
    }

    @Cacheable(value = "organismes", key="#entitatcodi")
    public List<OrganismeDto> findOrganismesByEntitat(String entitatcodi) {
        List<OrganismeDto> organismes = new ArrayList<OrganismeDto>();
        Map<String, NodeDir3> organigramaDir3 = cacheHelper.findOrganigramaNodeByEntitat(entitatcodi);
        for (String codi: organigramaDir3.keySet()) {
            OrganismeDto organisme = new OrganismeDto();
            NodeDir3 node = organigramaDir3.get(codi);
            organisme.setCodi(node.getCodi());
            organisme.setNom(node.getDenominacio());
            organismes.add(organisme);
        }
        Collections.sort(organismes, new Comparator<OrganismeDto>() {
            @Override
            public int compare(OrganismeDto o1, OrganismeDto o2) {
                return o1.getCodi().compareTo(o2.getCodi());
            }
        });
        return organismes;
    }

    private List<String> getCodisOrgansGestorsFills(
            Map<String, OrganismeDto> organigrama,
            String codiDir3) {
        List<String> unitats = new ArrayList<String>();
        unitats.add(codiDir3);
        OrganismeDto organisme = organigrama.get(codiDir3);
        if (organisme != null && organisme.getFills() != null && !organisme.getFills().isEmpty()) {
            for (String fill: organisme.getFills()) {
                unitats.addAll(getCodisOrgansGestorsFills(organigrama, fill));
            }
        }
        return unitats;
    }

    private OrganismeDto nodeDir3ToOrganisme(NodeDir3 node) {
        OrganismeDto organisme = new OrganismeDto();
        organisme.setCodi(node.getCodi());
        organisme.setNom(node.getDenominacio());
        String pare = node.getSuperior();
        if (pare != null && !pare.isEmpty()) {
            int size = pare.indexOf(" - ");
            if (size > 0)
                pare = pare.substring(0, pare.indexOf(" - "));
        }
        organisme.setPare(pare);
        List<String> fills = null;
        if (node.getFills() != null && !node.getFills().isEmpty()) {
            fills = new ArrayList<String>();
            for (NodeDir3 fill: node.getFills()) {
                fills.add(fill.getCodi());
            }
        }
        organisme.setFills(fills);

        return organisme;
    }

    @CacheEvict(value = "organigrama", key="#entitatcodi")
    public void evictFindOrganigramaByEntitat(String entitatcodi) {
    }
    @CacheEvict(value = "organismes", key="#entitatcodi")
    public void evictFindOrganismesByEntitat(String entitatcodi) {
    }

}