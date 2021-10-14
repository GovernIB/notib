package es.caib.notib.war.security;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.Attributes2GrantedAuthoritiesMapper;

import java.util.*;


/**
 * Defineix el mapeig entre els rols J2EE i els rols interns de l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@SuppressWarnings("rawtypes")
public class RolesBasedAttributes2GrantedAuthoritiesMapper implements Attributes2GrantedAuthoritiesMapper {

	private Map baseRoleMapping = new HashMap();

	public void setBaseRoleMapping(Map baseRoleMapping) {
		this.baseRoleMapping = baseRoleMapping;
	}

	public Collection<GrantedAuthority> getGrantedAuthorities(Collection<String> attributes) {
		List<GrantedAuthority> gaList = new ArrayList<GrantedAuthority>();
		for (String attribute: attributes) {
			Object mapping = baseRoleMapping.get(attribute);
			if (mapping != null) {
				if (mapping instanceof Collection) {
					for (Object obj: (Collection)mapping) {
						if (obj != null)
							gaList.add(new SimpleGrantedAuthority(obj.toString()));
					}
				} else if (mapping != null) {
					gaList.add(new SimpleGrantedAuthority(mapping.toString()));
				}
			} else {
				gaList.add(new SimpleGrantedAuthority(attribute));
			}
		}
		log.info("Mapeig dels rols a GrantedAuthority");
		String rolsStr = Arrays.toString(attributes.toArray());
		log.info("Rols de l'aplicació: " + rolsStr);

		rolsStr =  StringUtils.join(gaList, ",");
		log.info(String.format("Rols mapejats: %s", rolsStr));
		return gaList;
	}

}
