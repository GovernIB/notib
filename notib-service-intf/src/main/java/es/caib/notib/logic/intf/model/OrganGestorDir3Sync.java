package es.caib.notib.logic.intf.model;

import es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

/**
 * Resposta de l'acció de sincronització d'òrgans gestors.
 *
 * @author Límit Tecnologies
 */
@Getter
@RequiredArgsConstructor
public class OrganGestorDir3Sync implements Serializable {

	private final OrganGestorDir3SyncArbreItem arbreAmbCanvis;
	private final OrganGestorDir3SyncCanviCreacio[] creacions;
	private final OrganGestorDir3SyncCanviModificacio[] modificacions;
	private final OrganGestorDir3SyncCanviSubstitucio[] substitucions;
	private final OrganGestorDir3SyncCanviExtincio[] extincions;
	private final OrganGestorDir3SyncCanviFusio[] fusions;
	private final OrganGestorDir3SyncCanviDivisio[] divisions;
	private final boolean simulat;

	@Getter
	@RequiredArgsConstructor
	public static class OrganGestorDir3SyncArbreItem implements Serializable {
		private final String codi;
		private final String nom;
		private final String nomCooficial;
		private final OrganGestorEstatEnum estat;
		private final OrganGestorDir3SyncCanviTipus canviTipus;
		private final OrganGestorDir3SyncArbreItem[] fills;
		private final boolean canviEnFills;
		public OrganGestorDir3SyncArbreItem(
			String codi,
			String nom,
			String nomCooficial,
			OrganGestorEstatEnum estat) {
			this.codi = codi;
			this.nom = nom;
			this.nomCooficial = nomCooficial;
			this.estat = estat;
			this.canviTipus = null;
			this.fills = null;
			this.canviEnFills = false;
		}
	}

	@Getter
	@RequiredArgsConstructor
	public static class OrganGestorDir3SyncCanviCreacio implements Serializable {
		private final OrganGestorDir3SyncArbreItem nou;
	}

	@Getter
	@RequiredArgsConstructor
	public static class OrganGestorDir3SyncCanviModificacio implements Serializable {
		private final OrganGestorDir3SyncArbreItem vell;
		private final OrganGestorDir3SyncArbreItem nou;
	}

	@Getter
	@RequiredArgsConstructor
	public static class OrganGestorDir3SyncCanviSubstitucio implements Serializable {
		private final OrganGestorDir3SyncArbreItem vell;
		private final OrganGestorDir3SyncArbreItem nou;
	}

	@Getter
	@RequiredArgsConstructor
	public static class OrganGestorDir3SyncCanviExtincio implements Serializable {
		private final OrganGestorDir3SyncArbreItem vell;
	}

	@Getter
	@RequiredArgsConstructor
	public static class OrganGestorDir3SyncCanviFusio implements Serializable {
		private final OrganGestorDir3SyncArbreItem[] vells;
		private final OrganGestorDir3SyncArbreItem nou;
	}

	@Getter
	@RequiredArgsConstructor
	public static class OrganGestorDir3SyncCanviDivisio implements Serializable {
		private final OrganGestorDir3SyncArbreItem vell;
		private final OrganGestorDir3SyncArbreItem[] nous;
	}

	public enum OrganGestorDir3SyncCanviTipus {
		CREACIO,
		MODIFICACIO,
		SUBSTITUCIO,
		EXTINCIO,
		FUSIO,
		DIVISO
	}

}
