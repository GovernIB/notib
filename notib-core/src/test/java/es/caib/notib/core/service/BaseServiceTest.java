/**
 * 
 */
package es.caib.notib.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Tests per al servei d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BaseServiceTest {

	@Autowired
	private UserDetailsService userDetailsService;



	protected void autenticarUsuari(String usuariCodi) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(usuariCodi);
		Authentication authToken = new UsernamePasswordAuthenticationToken(
				userDetails.getUsername(),
				userDetails.getPassword(),
				userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
	}

}
