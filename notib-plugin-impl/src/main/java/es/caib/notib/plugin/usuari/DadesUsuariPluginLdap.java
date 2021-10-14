/**
 * 
 */
package es.caib.notib.plugin.usuari;

import es.caib.notib.plugin.SistemaExternException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Implementació del plugin de consulta de dades d'usuaris emprant JDBC.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DadesUsuariPluginLdap implements DadesUsuariPlugin {


	@Override
	public List<String> consultarRolsAmbCodi(
			String usuariCodi) throws SistemaExternException {
		LOGGER.debug("Consulta dels rols de l'usuari (usuariCodi=" + usuariCodi + ")");
		try {
			return consultaRolsUsuari(
					getLdapFiltreCodi(),
					usuariCodi);
			
		} catch (Exception ex) {
			throw new SistemaExternException(
					"Error al consultar els rols de l'usuari (usuariCodi=" + usuariCodi + ")",
					ex);
		}
		
	}
	
	@Override
	public DadesUsuari consultarAmbCodi(
			String usuariCodi) throws SistemaExternException {
		LOGGER.debug("Consulta de les dades de l'usuari (usuariCodi=" + usuariCodi + ")");
		try {
			return consultaUsuariUnic(
					getLdapFiltreCodi(),
					usuariCodi);
		} catch (SistemaExternException ex) {
			throw ex;
		} catch (NamingException ex) {
			throw new SistemaExternException(
					"Error al consultar l'usuari (usuariCodi=" + usuariCodi + ")",
					ex);
		}
	}

	@Override
	public List<DadesUsuari> consultarAmbGrup(
			String grupCodi) throws SistemaExternException {
		LOGGER.debug("Consulta dels usuaris del grup (grupCodi=" + grupCodi + ")");
		try {
			return consultaUsuaris(
					getLdapFiltreGrup(),
					grupCodi);
		} catch (NamingException ex) {
			throw new SistemaExternException(
					"Error al consultar els usuaris del grup (grupCodi=" + grupCodi + ")",
					ex);
		}
	}



	private DadesUsuari consultaUsuariUnic(
			String filtre,
			String valor) throws SistemaExternException, NamingException {
		List<DadesUsuari> usuaris = consultaUsuaris(filtre, valor);
		if (usuaris.size() == 1) {
			return usuaris.get(0);
		} else {
			throw new SistemaExternException(
					"La consulta d'usuari únic ha retornat més d'un resultat (" +
					"filtre=" + filtre + ", " +
					"valor=" + valor + ")");
		}
	}
	private List<String> consultaRolsUsuari(
			String filtre,
			String valor) throws NamingException {
		
		List<String> rolsUsuari = new ArrayList<String>();
		Hashtable<String, String> entornLdap = new Hashtable<String, String>();
		entornLdap.put(
				Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.ldap.LdapCtxFactory");
		entornLdap.put(Context.PROVIDER_URL, getLdapServerUrl());
		entornLdap.put(Context.SECURITY_PRINCIPAL, getLdapPrincipal());
		entornLdap.put(Context.SECURITY_CREDENTIALS, getLdapCredentials());
		LdapContext ctx = new InitialLdapContext(entornLdap, null);
		try {
			String[] atributs = getLdapAtributs().split(",");
			SearchControls searchCtls = new SearchControls();
			searchCtls.setReturningAttributes(atributs);
			searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			NamingEnumeration<SearchResult> answer = ctx.search(
					getLdapSearchBase(),
					filtre.replace("XXX", valor),
					searchCtls);
			while (answer.hasMoreElements()) {
				SearchResult result = answer.next();
					rolsUsuari = obtenirAtributComListString(
							result.getAttributes(),
							atributs[5]);
				
			}
		} finally {
			ctx.close();
		}
		return rolsUsuari;
	}
	private List<DadesUsuari> consultaUsuaris(
			String filtre,
			String valor) throws NamingException {
		List<DadesUsuari> usuaris = new ArrayList<DadesUsuari>();
		Hashtable<String, String> entornLdap = new Hashtable<String, String>();
		entornLdap.put(
				Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.ldap.LdapCtxFactory");
		entornLdap.put(Context.PROVIDER_URL, getLdapServerUrl());
		entornLdap.put(Context.SECURITY_PRINCIPAL, getLdapPrincipal());
		entornLdap.put(Context.SECURITY_CREDENTIALS, getLdapCredentials());
		LdapContext ctx = new InitialLdapContext(entornLdap, null);
		try {
			String[] atributs = getLdapAtributs().split(",");
			SearchControls searchCtls = new SearchControls();
			searchCtls.setReturningAttributes(atributs);
			searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			NamingEnumeration<SearchResult> answer = ctx.search(
					getLdapSearchBase(),
					filtre.replace("XXX", valor),
					searchCtls);
			while (answer.hasMoreElements()) {
				SearchResult result = answer.next();
				String grup = obtenirAtributComString(
						result.getAttributes(),
						atributs[4]);
				String memberOf = obtenirAtributComString(
						result.getAttributes(),
						atributs[5]);
				boolean excloure = false;
				if (getLdapExcloureGrup() != null) {
					excloure = grup.equals(getLdapExcloureGrup());
					if (excloure && getLdapExcloureMembre() != null) {
						excloure = memberOf.contains(getLdapExcloureMembre());
					}
				}
				if (!excloure) {
					String codi = obtenirAtributComString(
							result.getAttributes(),
							atributs[0]);
					String nom = obtenirAtributComString(
							result.getAttributes(),
							atributs[1]);
					String llinatges = obtenirAtributComString(
							result.getAttributes(),
							atributs[2]);
					String email = obtenirAtributComString(
							result.getAttributes(),
							atributs[3]);
					String nif = obtenirAtributComString(
							result.getAttributes(),
							atributs[4]);
					DadesUsuari dadesUsuari = new DadesUsuari();
					dadesUsuari.setCodi(codi);
					dadesUsuari.setNom(nom);
					dadesUsuari.setLlinatges(llinatges);
					dadesUsuari.setEmail(email);
					dadesUsuari.setNif(nif);
					usuaris.add(dadesUsuari);
				}
			}
		} finally {
			ctx.close();
		}
		return usuaris;
	}

	private String obtenirAtributComString(
			Attributes atributs,
			String atributNom) throws NamingException {
		Attribute atribut = atributs.get(atributNom);
		return (atribut != null) ? (String)atribut.get() : null;
	}
	
	@SuppressWarnings("rawtypes")
	private List<String> obtenirAtributComListString(
			Attributes atributs,
			String atributNom) throws NamingException {
		Attribute atribut = atributs.get(atributNom);
		List<String> listRols = new ArrayList<String>();
		
		NamingEnumeration rols = atribut.getAll();
		
		while (rols.hasMoreElements()) {
			String rol = rols.next().toString();
			int iniciIndexRol = rol.indexOf("CN=") + 3;
			int fiIndexRol = rol.indexOf(",");
			listRols.add(rol.substring(iniciIndexRol, fiIndexRol));
		}
		
		return listRols;
	}

	private String getLdapServerUrl() {
		return System.getProperties().getProperty("es.caib.notib.plugin.dades.usuari.ldap.server.url");
	}
	private String getLdapPrincipal() {
		return System.getProperties().getProperty("es.caib.notib.plugin.dades.usuari.ldap.principal");
	}
	private String getLdapCredentials() {
		return System.getProperties().getProperty("es.caib.notib.plugin.dades.usuari.ldap.credentials");
	}
	private String getLdapSearchBase() {
		return System.getProperties().getProperty("es.caib.notib.plugin.dades.usuari.ldap.search.base");
	}
	private String getLdapAtributs() {
		// Exemple: cn,givenName,sn,mail,departmentNumber,memberOf
		return System.getProperties().getProperty("es.caib.notib.plugin.dades.usuari.ldap.atributs");
	}
	private String getLdapFiltreCodi() {
		// Exemple: (&(objectClass=inetOrgPersonCAIB)(cn=XXX))
		return System.getProperties().getProperty("es.caib.notib.plugin.dades.usuari.ldap.filtre.codi");
	}
	private String getLdapFiltreGrup() {
		// Exemple: (&(objectClass=inetOrgPersonCAIB)(memberOf=cn=XXX,dc=caib,dc=es))
		return System.getProperties().getProperty("es.caib.notib.plugin.dades.usuari.ldap.filtre.grup");
	}
	private String getLdapExcloureGrup() {
		return System.getProperties().getProperty("es.caib.notib.plugin.dades.usuari.ldap.excloure.grup");
	}
	private String getLdapExcloureMembre() {
		return System.getProperties().getProperty("es.caib.notib.plugin.dades.usuari.ldap.excloure.membre");
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(DadesUsuariPluginLdap.class);


}
