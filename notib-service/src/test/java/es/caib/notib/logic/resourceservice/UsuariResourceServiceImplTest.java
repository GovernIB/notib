package es.caib.notib.logic.resourceservice;

import es.caib.notib.logic.base.helper.AuthenticationHelper;
import es.caib.notib.logic.intf.model.auth.NotibAuthenticationDetails;
import es.caib.notib.persist.resourceentity.UsuariResourceEntity;
import es.caib.notib.persist.resourcerepository.UsuariResourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test unitari per a UsuariResourceServiceImpl.
 * <p>
 * Cobreix:
 *  - refresh(): creació, actualització i no-acció
 *  - additionalSpringFilter()
 */
@ExtendWith(MockitoExtension.class)
class UsuariResourceServiceImplTest {

	@Mock
	private AuthenticationHelper authenticationHelper;
	@Mock
	private UsuariResourceRepository usuariResourceRepository;
	@InjectMocks
	private UsuariResourceServiceImpl service;

	@BeforeEach
	void setUp() {
		SecurityContext context = mock(SecurityContext.class);
		SecurityContextHolder.setContext(context);
	}

	@Test
	void additionalSpringFilterShouldReturnFilterWithUsername() {
		when(authenticationHelper.getCurrentUserName()).thenReturn("user1");
		String result = service.additionalSpringFilter("", new String[]{});
		assertEquals("id:'user1'", result);
	}

	@Test
	void refreshShouldCreateUserIfNotExists() {
		// given
		mockJwtAuthentication("user1", "Name", "123", "test@mail.com");
		when(authenticationHelper.getCurrentUserName()).thenReturn("user1");
		when(usuariResourceRepository.findById("user1")).thenReturn(Optional.empty());
		// when
		service.refresh();
		// then
		verify(usuariResourceRepository).save(any(UsuariResourceEntity.class));
	}

	@Test
	void refreshShouldUpdateUserIfDataChanged() {
		// given
		mockJwtAuthentication("user1", "NewName", "123", "new@mail.com");
		when(authenticationHelper.getCurrentUserName()).thenReturn("user1");
		UsuariResourceEntity existing = new UsuariResourceEntity();
		existing.setNomSencer("OldName");
		existing.setNif("123");
		existing.setEmail("old@mail.com");
		when(usuariResourceRepository.findById("user1")).thenReturn(Optional.of(existing));
		// when
		service.refresh();
		// then
		verify(usuariResourceRepository).save(existing);
		assertEquals("NewName", existing.getNomSencer());
		assertEquals("new@mail.com", existing.getEmail());
	}

	@Test
	void refreshShouldNotUpdateIfDataIsSame() {
		// given
		mockJwtAuthentication("user1", "Name", "123", "mail@test.com");
		when(authenticationHelper.getCurrentUserName()).thenReturn("user1");
		UsuariResourceEntity existing = new UsuariResourceEntity();
		existing.setNomSencer("Name");
		existing.setNif("123");
		existing.setEmail("mail@test.com");
		when(usuariResourceRepository.findById("user1")).thenReturn(Optional.of(existing));
		// when
		service.refresh();
		// then
		verify(usuariResourceRepository, never()).save(any());
	}

	@Test
	void refreshShouldCreateUserFromUserPrincipal() {
		// given
		mockUserAuthentication("user1", "Name", "12345678A", "user@mail.com");
		when(authenticationHelper.getCurrentUserName()).thenReturn("user1");
		when(usuariResourceRepository.findById("user1"))
			.thenReturn(Optional.empty());
		// when
		service.refresh();
		// then
		verify(usuariResourceRepository).save(argThat(entity ->
			entity.getNomSencer().equals("Name") &&
				entity.getNif().equals("12345678A") &&
				entity.getEmail().equals("user@mail.com")
		));
	}

	@Test
	void refreshShouldDoNothingIfNoAuthUser() {
		SecurityContextHolder.clearContext();
		service.refresh();
		verifyNoInteractions(usuariResourceRepository);
	}

	private void mockJwtAuthentication(String username, String name, String nif, String email) {
		SecurityContext context = SecurityContextHolder.getContext();
		Jwt jwt = mock(Jwt.class);
		when(jwt.getClaimAsString("name")).thenReturn(name);
		when(jwt.getClaimAsString("nif")).thenReturn(nif);
		when(jwt.getClaimAsString("email")).thenReturn(email);
		Authentication authentication = mock(Authentication.class);
		when(authentication.getPrincipal()).thenReturn(jwt);
		when(authentication.getName()).thenReturn(username);
		when(context.getAuthentication()).thenReturn(authentication);
	}

	private void mockUserAuthentication(String username, String name, String nif, String email) {
		SecurityContext context = SecurityContextHolder.getContext();
		User user = mock(User.class);
		NotibAuthenticationDetails details = mock(NotibAuthenticationDetails.class);
		when(details.getName()).thenReturn(name);
		when(details.getNif()).thenReturn(nif);
		when(details.getEmail()).thenReturn(email);
		Authentication authentication = mock(Authentication.class);
		when(authentication.getPrincipal()).thenReturn(user);
		when(authentication.getDetails()).thenReturn(details);
		when(authentication.getName()).thenReturn(username);
		when(context.getAuthentication()).thenReturn(authentication);
	}

}
