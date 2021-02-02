/**
 * 
 */
package es.caib.notib.core.api.dto;

import java.io.Serializable;
import java.util.Comparator;

import org.apache.commons.lang.builder.ToStringBuilder;

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
	private boolean notificacio;

	private boolean comuns;
	
	// Booleà per a indicar si en cas de procediment comú, 
	// l'usuari administrador d'òrgan pot editar el permís
	private boolean permetEdicio;
	
	public String getOrganCodiNom() {
		if (organ != null && organNom != null)
			return organ + " - " + organNom;

		return organ;
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
	private static Comparator<PermisDto> readComparator;
	private static Comparator<PermisDto> processarComparator;
	private static Comparator<PermisDto> notificacioComparator;
	private static Comparator<PermisDto> administrationComparator;
	private static Comparator<PermisDto> administradorComparator;
	
	public static Comparator<PermisDto> decending(final Comparator<PermisDto> other) {
        return new Comparator<PermisDto>() {
            public int compare(PermisDto o1, PermisDto o2) {
                return -1 * other.compare(o1, o2);
            }
        };
    }
	
	public static Comparator<PermisDto> sortByRead() {
		if (readComparator == null) {
			readComparator = new ReadComparator();
        }
        return readComparator;
    }
	
	public static Comparator<PermisDto> sortByProcessar() {
		if (processarComparator == null) {
			processarComparator = new ProcessarComparator();
        }
        return processarComparator;
    }
	
	public static Comparator<PermisDto> sortByNotificacio() {
		if (notificacioComparator == null) {
			notificacioComparator = new NotificacioComparator();
        }
        return notificacioComparator;
    }
	
	public static Comparator<PermisDto> sortByAdministration() {
		if (administrationComparator == null) {
			administrationComparator = new AdministrationComparator();
        }
        return administrationComparator;
    }
	
	public static Comparator<PermisDto> sortByAdministrador() {
		if (administradorComparator == null) {
			administradorComparator = new AdministradorComparator();
        }
        return administradorComparator;
    }
	
	private static class ReadComparator implements Comparator<PermisDto> {
        public int compare(PermisDto p1, PermisDto p2) {  
            return p1.isRead() == p2.isRead() ? 0 : (p1.isRead() ? 1 : -1);
        }  
    }
	
	private static class ProcessarComparator implements Comparator<PermisDto> {
        public int compare(PermisDto p1, PermisDto p2) {  
        	return p1.isProcessar() == p2.isProcessar() ? 0 : (p1.isProcessar() ? 1 : -1);
        }  
    }
	
	private static class NotificacioComparator implements Comparator<PermisDto> {
        public int compare(PermisDto p1, PermisDto p2) {  
        	return p1.isNotificacio() == p2.isNotificacio() ? 0 : (p1.isNotificacio() ? 1 : -1);
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
	 
	private static final long serialVersionUID = -139254994389509932L;

}
