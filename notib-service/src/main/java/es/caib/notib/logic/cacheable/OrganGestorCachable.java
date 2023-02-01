package es.caib.notib.logic.cacheable;

import es.caib.notib.logic.intf.dto.organisme.OrganismeDto;
import es.caib.notib.logic.helper.CacheHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

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
        return cacheHelper.findOrganigramaNodeByEntitat(entitatcodi);
    }

    @Cacheable(value = "codisOrgansFills", key="#codiDir3Entitat.concat('-').concat(#codiDir3Organ)")
    public List<String> getCodisOrgansGestorsFillsByOrgan(String codiDir3Entitat, String codiDir3Organ) {

       var organigramaEntitat = findOrganigramaByEntitat(codiDir3Entitat);
        var codiDir3 = codiDir3Organ != null ? codiDir3Organ : codiDir3Entitat;
        List<String> unitatsEntitat = new ArrayList<>();
        unitatsEntitat.addAll(getCodisOrgansGestorsFills(organigramaEntitat, codiDir3));
        return unitatsEntitat;
    }

    /**
     * Obté una llista dels codis dir3 dels organismes pares del organisme indicat,
     * inclou el codi dir3 de l'òrgan indicat.
     *
     * @param codiDir3Entitat Codi dir3 de l'entitat de l'organigrama a consultar.
     * @param codiDir3Organ Codi dir3 de l'òrgan del que es volen coneixer els seus pares.
     *
     * @return Una llista de String amb els codis DIR3.
     */
    @Cacheable(value = "organCodisAncestors", key="#codiDir3Entitat.concat('-').concat(#codiDir3Organ)")
    public List<String> getCodisAncestors(String codiDir3Entitat, String codiDir3Organ) {

        var organigramaEntitat = findOrganigramaByEntitat(codiDir3Entitat);
        var currentNode = organigramaEntitat.get(codiDir3Organ);
        if (currentNode == null) { // organ obsolet
            return new ArrayList<>();
        }
        List<String> pares = new ArrayList<>();
        while(currentNode != null && !currentNode.getCodi().equals(currentNode.getPare())) {
            pares.add(currentNode.getCodi());
            currentNode = organigramaEntitat.get(currentNode.getPare());
        }
        return pares;
    }

    @Cacheable(value = "organismes", key="#entitatcodi")
    public List<OrganismeDto> findOrganismesByEntitat(String entitatcodi) {

        List<OrganismeDto> organismes = new ArrayList<>();
        var organigramaDir3 = cacheHelper.findOrganigramaNodeByEntitat(entitatcodi);
        if (organigramaDir3 == null || organigramaDir3.isEmpty()) {
            return organismes;
        }
        organismes = new ArrayList<>(organigramaDir3.values());
        Collections.sort(organismes, new Comparator<OrganismeDto>() {
            @Override
            public int compare(OrganismeDto o1, OrganismeDto o2) {
                return o1.getCodi().compareTo(o2.getCodi());
            }
        });
        return organismes;
    }

    private List<String> getCodisOrgansGestorsFills(Map<String, OrganismeDto> organigrama, String codiDir3) {

        List<String> unitats = new ArrayList<String>();
        unitats.add(codiDir3);
        var organisme = organigrama.get(codiDir3);
        if (organisme != null && organisme.getFills() != null && !organisme.getFills().isEmpty()) {
            for (var fill: organisme.getFills()) {
                unitats.addAll(getCodisOrgansGestorsFills(organigrama, fill));
            }
        }
        return unitats;
    }

    @CacheEvict(value = "organigrama", key="#entitatcodi")
    public void evictFindOrganigramaByEntitat(String entitatcodi) {
    }
    @CacheEvict(value = "organismes", key="#entitatcodi")
    public void evictFindOrganismesByEntitat(String entitatcodi) {
    }
    @CacheEvict(value = "organCodisAncestors", key="#codiDir3Entitat.concat('-').concat(#codiDir3Organ)")
    public void evictCodisAncestors(String codiDir3Entitat, String codiDir3Organ) {
    }
}
