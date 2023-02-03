/**
 * 
 */
package es.caib.notib.plugin.usuari;

import es.caib.notib.plugin.SistemaExternException;
import lombok.extern.slf4j.Slf4j;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

/**
 * Implementació del plugin de consulta de dades d'usuaris emprant JDBC.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
public class DadesUsuariPluginLdap implements DadesUsuariPlugin {

	private final Properties properties;

	public DadesUsuariPluginLdap(Properties properties) {
		this.properties = properties;
	}

	@Override
	public List<String> consultarRolsAmbCodi(String usuariCodi) throws SistemaExternException {

		log.debug("Consulta dels rols de l'usuari (usuariCodi=" + usuariCodi + ")");
		try {
			return consultaRolsUsuari(getLdapFiltreCodi(), usuariCodi);
		} catch (Exception ex) {
			throw new SistemaExternException("Error al consultar els rols de l'usuari (usuariCodi=" + usuariCodi + ")", ex);
		}
	}
	
	@Override
	public DadesUsuari consultarAmbCodi(String usuariCodi) throws SistemaExternException {

		log.debug("Consulta de les dades de l'usuari (usuariCodi=" + usuariCodi + ")");
		try {
			return consultaUsuariUnic(getLdapFiltreCodi(), usuariCodi);
		} catch (SistemaExternException ex) {
			throw ex;
		} catch (NamingException ex) {
			throw new SistemaExternException("Error al consultar l'usuari (usuariCodi=" + usuariCodi + ")", ex);
		}
	}

	@Override
	public List<DadesUsuari> consultarAmbGrup(String grupCodi) throws SistemaExternException {

		log.debug("Consulta dels usuaris del grup (grupCodi=" + grupCodi + ")");
		try {
			return consultaUsuaris(getLdapFiltreGrup(), grupCodi);
		} catch (NamingException ex) {
			throw new SistemaExternException("Error al consultar els usuaris del grup (grupCodi=" + grupCodi + ")", ex);
		}
	}

	private DadesUsuari consultaUsuariUnic(String filtre, String valor) throws SistemaExternException, NamingException {

		var usuaris = consultaUsuaris(filtre, valor);
		if (usuaris.size() == 1) {
			return usuaris.get(0);
		}
		throw new SistemaExternException("La consulta d'usuari únic ha retornat més d'un resultat (filtre=" + filtre + ", valor=" + valor + ")");
	}

	private List<String> consultaRolsUsuari(String filtre, String valor) throws NamingException {
		
		List<String> rolsUsuari = new ArrayList<>();
		Hashtable<String, String> entornLdap = new Hashtable<String, String>();
		entornLdap.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		entornLdap.put(Context.PROVIDER_URL, getLdapServerUrl());
		entornLdap.put(Context.SECURITY_PRINCIPAL, getLdapPrincipal());
		entornLdap.put(Context.SECURITY_CREDENTIALS, getLdapCredentials());
		var ctx = new InitialLdapContext(entornLdap, null);
		try {
			var atributs = getLdapAtributs().split(",");
			var searchCtls = new SearchControls();
			searchCtls.setReturningAttributes(atributs);
			searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			var answer = ctx.search(getLdapSearchBase(), filtre.replace("XXX", valor), searchCtls);
			SearchResult result;
			while (answer.hasMoreElements()) {
				result = answer.next();
				rolsUsuari = obtenirAtributComListString(result.getAttributes(), atributs[5]);
			}
		} finally {
			ctx.close();
		}
		return rolsUsuari;
	}

	private List<DadesUsuari> consultaUsuaris(String filtre, String valor) throws NamingException {

		List<DadesUsuari> usuaris = new ArrayList<>();
		Hashtable<String, String> entornLdap = new Hashtable<>();
		entornLdap.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		entornLdap.put(Context.PROVIDER_URL, getLdapServerUrl());
		entornLdap.put(Context.SECURITY_PRINCIPAL, getLdapPrincipal());
		entornLdap.put(Context.SECURITY_CREDENTIALS, getLdapCredentials());
		LdapContext ctx = new InitialLdapContext(entornLdap, null);
		try {
			var atributs = getLdapAtributs().split(",");
			var searchCtls = new SearchControls();
			searchCtls.setReturningAttributes(atributs);
			searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			var answer = ctx.search(getLdapSearchBase(), filtre.replace("XXX", valor), searchCtls);
			SearchResult result;
			String grup, memberOf;
			boolean excloure;
			while (answer.hasMoreElements()) {
				result = answer.next();
				grup = obtenirAtributComString(result.getAttributes(), atributs[4]);
				memberOf = obtenirAtributComString(result.getAttributes(), atributs[5]);
				excloure = false;
				if (getLdapExcloureGrup() != null) {
					excloure = grup.equals(getLdapExcloureGrup());
					if (excloure && getLdapExcloureMembre() != null) {
						excloure = memberOf.contains(getLdapExcloureMembre());
					}
				}
				if (!excloure) {
					var codi = obtenirAtributComString(result.getAttributes(), atributs[0]);
					var nom = obtenirAtributComString(result.getAttributes(), atributs[1]);
					var llinatges = obtenirAtributComString(result.getAttributes(), atributs[2]);
					var email = obtenirAtributComString(result.getAttributes(), atributs[3]);
					var nif = obtenirAtributComString(result.getAttributes(), atributs[4]);
					var dadesUsuari = new DadesUsuari();
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

	private String obtenirAtributComString(Attributes atributs, String atributNom) throws NamingException {
		
		var atribut = atributs.get(atributNom);
		return (atribut != null) ? (String)atribut.get() : null;
	}
	
	@SuppressWarnings("rawtypes")
	private List<String> obtenirAtributComListString(Attributes atributs, String atributNom) throws NamingException {
		
		var atribut = atributs.get(atributNom);
		List<String> listRols = new ArrayList<>();
		var rols = atribut.getAll();
		String rol;
		int iniciIndexRol, fiIndexRol;
		while (rols.hasMoreElements()) {
			rol = rols.next().toString();
			iniciIndexRol = rol.indexOf("CN=") + 3;
			fiIndexRol = rol.indexOf(",");
			listRols.add(rol.substring(iniciIndexRol, fiIndexRol));
		}
		return listRols;
	}

	private String getLdapServerUrl() {
		return properties.getProperty("es.caib.notib.plugin.dades.usuari.ldap.server.url");
	}
	
	private String getLdapPrincipal() {
		return properties.getProperty("es.caib.notib.plugin.dades.usuari.ldap.principal");
	}
	
	private String getLdapCredentials() {
		return properties.getProperty("es.caib.notib.plugin.dades.usuari.ldap.credentials");
	}
	
	private String getLdapSearchBase() {
		return properties.getProperty("es.caib.notib.plugin.dades.usuari.ldap.search.base");
	}
	
	private String getLdapAtributs() {
		// Exemple: cn,givenName,sn,mail,departmentNumber,memberOf
		return properties.getProperty("es.caib.notib.plugin.dades.usuari.ldap.atributs");
	}
	
	private String getLdapFiltreCodi() {
		// Exemple: (&(objectClass=inetOrgPersonCAIB)(cn=XXX))
		return properties.getProperty("es.caib.notib.plugin.dades.usuari.ldap.filtre.codi");
	}
	
	private String getLdapFiltreGrup() {
		// Exemple: (&(objectClass=inetOrgPersonCAIB)(memberOf=cn=XXX,dc=caib,dc=es))
		return properties.getProperty("es.caib.notib.plugin.dades.usuari.ldap.filtre.grup");
	}
	
	private String getLdapExcloureGrup() {
		return properties.getProperty("es.caib.notib.plugin.dades.usuari.ldap.excloure.grup");
	}
	
	private String getLdapExcloureMembre() {
		return properties.getProperty("es.caib.notib.plugin.dades.usuari.ldap.excloure.membre");
	}

}
