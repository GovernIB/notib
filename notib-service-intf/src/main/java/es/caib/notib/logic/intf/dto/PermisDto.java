/**
 * 
 */
package es.caib.notib.logic.intf.dto;

import java.io.Serializable;
import java.util.Comparator;

import org.apache.commons.lang3.builder.ToStringBuilder;

import lombok.Getter;
import lombok.Setter;

/**
 * Informació d'un permís.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class PermisDto implements Serializable {

	private Long id;
	private String principal;
	private String organ;
	private String organNom;
	private String nomSencerAmbCodi;
	private TipusEnumDto tipus;
	private boolean read;
	private boolean write;
	private boolean create;
	private boolean delete;
	private boolean administration;
	
	private boolean usuari;
	private boolean administrador;
	private boolean administradorEntitat;
	private boolean aplicacio;
	
	private boolean processar;
	private boolean comuns;
	
	private boolean notificacio;
	private boolean comunicacio;
	private boolean comunicacioSir;
	private boolean comunicacioSenseProcediment;
	
	// Booleà per a indicar si en cas de procediment comú, 
	// l'usuari administrador d'òrgan pot editar el permís
	private boolean permetEdicio;
	
	public String getOrganCodiNom() {
		return organ != null && organNom != null ? organ + " - " + organNom : organ;
	}

	public void revocaPermisos() {

		this.read = false;
		this.write= false;
		this.create= false;
		this.delete= false;
		this.administration= false;
		this.usuari= false;
		this.administrador= false;
		this.administradorEntitat= false;
		this.aplicacio= false;
		this.processar= false;
		this.notificacio= false;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	// Comparadors
	private static Comparator<PermisDto> tipusComparator;
	private static Comparator<PermisDto> nomSencerAmbCodiComparator;
	private static Comparator<PermisDto> organCodiNomComparator;
	private static Comparator<PermisDto> readComparator;
	private static Comparator<PermisDto> processarComparator;
	private static Comparator<PermisDto> notificacioComparator;
	private static Comparator<PermisDto> comunsComparator;
	private static Comparator<PermisDto> administrationComparator;
	private static Comparator<PermisDto> administradorComparator;
	private static Comparator<PermisDto> comunicacioSirComparator;
	
	public static Comparator<PermisDto> decending(final Comparator<PermisDto> other) {
        return new Comparator<PermisDto>() {
            public int compare(PermisDto o1, PermisDto o2) {
                return -1 * other.compare(o1, o2);
            }
        };
    }

	public static Comparator<PermisDto> sortByTipus() {
		return tipusComparator != null ? tipusComparator : new TipusComparator();
	}
	public static Comparator<PermisDto> sortByNomSencerAmbCodiComparator() {
		return nomSencerAmbCodiComparator != null ? nomSencerAmbCodiComparator : new NomSencerAmbCodiComparator();
	}
	public static Comparator<PermisDto> sortByOrganCodiNomComparator() {
		return organCodiNomComparator != null ? organCodiNomComparator : new OrganCodiNomComparator();
	}
	
	public static Comparator<PermisDto> sortByRead() {
		return readComparator != null ?	readComparator : new ReadComparator();
    }
	
	public static Comparator<PermisDto> sortByProcessar() {
		return processarComparator != null ? processarComparator : new ProcessarComparator();
    }
	
	public static Comparator<PermisDto> sortByNotificacio() {
		return notificacioComparator != null ? notificacioComparator : new NotificacioComparator();
    }

    public static Comparator<PermisDto> sortByComuns() {
		return comunsComparator != null ? comunsComparator : new ComunsComparator();
	}
	
	public static Comparator<PermisDto> sortByAdministration() {
		return administrationComparator != null ? administrationComparator : new AdministrationComparator();
    }
	
	public static Comparator<PermisDto> sortByAdministrador() {
		return administradorComparator != null ? administradorComparator : new AdministradorComparator();
    }
	public static Comparator<PermisDto> sortByComunicacioSir() {
		return comunicacioSirComparator != null ? comunicacioSirComparator : new ComunicacioSirComparator();
    }

	private static class TipusComparator implements java.util.Comparator<PermisDto> {
		public int compare(PermisDto p1, PermisDto p2) {
			return p1.getTipus().compareTo(p2.getTipus());
		}
	}

	private static class NomSencerAmbCodiComparator implements java.util.Comparator<PermisDto> {
		public int compare(PermisDto p1, PermisDto p2) {
			if (p1.getNomSencerAmbCodi() == null) {
				return  p2.getNomSencerAmbCodi() != null ? 0 : -1;
			}
			if (p2.getNomSencerAmbCodi() == null) {
				return 1;
			}
			return p1.getNomSencerAmbCodi().toLowerCase().compareTo(p2.getNomSencerAmbCodi().toLowerCase());
		}
	}

	private static class OrganCodiNomComparator implements java.util.Comparator<PermisDto> {
		public int compare(PermisDto p1, PermisDto p2) {
			if (p1.getOrganCodiNom() == null) {
				return p2.getOrganCodiNom() != null ? 0 : -1;
			}
			if (p2.getOrganCodiNom() == null) {
				return 1;
			}
			return p1.getOrganCodiNom().toLowerCase().compareTo(p2.getOrganCodiNom().toLowerCase());
		}
	}

	private static class ReadComparator implements java.util.Comparator<PermisDto> {
        public int compare(PermisDto p1, PermisDto p2) {  
            return p1.isRead() == p2.isRead() ? 0 : (p1.isRead() ? 1 : -1);
        }  
    }
	
	private static class ProcessarComparator implements java.util.Comparator<PermisDto> {
        public int compare(PermisDto p1, PermisDto p2) {  
        	return p1.isProcessar() == p2.isProcessar() ? 0 : (p1.isProcessar() ? 1 : -1);
        }  
    }
	
	private static class NotificacioComparator implements java.util.Comparator<PermisDto> {
        public int compare(PermisDto p1, PermisDto p2) {  
        	return p1.isNotificacio() == p2.isNotificacio() ? 0 : (p1.isNotificacio() ? 1 : -1);
        }  
    }
    private static class ComunsComparator implements Comparator<PermisDto> {
        public int compare(PermisDto p1, PermisDto p2) {
        	return p1.isComuns() == p2.isComuns() ? 0 : (p1.isComuns() ? 1 : -1);
        }
    }
	
	private static class AdministrationComparator implements Comparator<PermisDto> {
        public int compare(PermisDto p1, PermisDto p2) {  
        	return p1.isAdministration() == p2.isAdministration() ? 0 : (p1.isAdministration() ? 1 : -1);
        }  
    }
	
	private static class AdministradorComparator implements Comparator<PermisDto> {
        public int compare(PermisDto p1, PermisDto p2) {  
        	return p1.isAdministrador() == p2.isAdministrador() ? 0 : (p1.isAdministrador() ? 1 : -1);
        }  
    }
	private static class ComunicacioSirComparator implements java.util.Comparator<PermisDto> {
        public int compare(PermisDto p1, PermisDto p2) {  
        	return p1.isComunicacioSir() == p2.isComunicacioSir() ? 0 : (p1.isComunicacioSir() ? 1 : -1);
        }  
    }
	private static final long serialVersionUID = -139254994389509932L;

}