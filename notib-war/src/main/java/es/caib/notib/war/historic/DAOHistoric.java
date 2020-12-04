package es.caib.notib.war.historic;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import es.caib.notib.core.api.dto.GrupDto;
import es.caib.notib.core.api.dto.NotificacioEstatEnumDto;
import es.caib.notib.core.api.dto.OrganGestorDto;
import es.caib.notib.core.api.dto.ProcedimentDto;
import es.caib.notib.core.api.dto.UsuariDto;
import es.caib.notib.core.api.dto.historic.HistoricAggregationEstatDto;
import es.caib.notib.core.api.dto.historic.HistoricAggregationGrupDto;
import es.caib.notib.core.api.dto.historic.HistoricAggregationOrganDto;
import es.caib.notib.core.api.dto.historic.HistoricAggregationProcedimentDto;
import es.caib.notib.core.api.dto.historic.HistoricAggregationUsuariDto;
import lombok.Setter;

public class DAOHistoric {

	public static RootOrganGestors mapRegistreOrganGestor(
			Map<OrganGestorDto, List<HistoricAggregationOrganDto>> dades) {
		List<RegistresOrganGestor> registres = new ArrayList<>();

		List<RegistreOrganGestor> regOrgans = new ArrayList<>();
		for (OrganGestorDto organGestor : dades.keySet()) {
			List<HistoricAggregationOrganDto> listHistorics = dades.get(organGestor);
			for (HistoricAggregationOrganDto historic : listHistorics) {
				RegistreOrganGestor registre = new RegistreOrganGestor();
				BeanUtils.copyProperties(historic, registre);
				regOrgans.add(registre);
			}
			registres.add(new RegistresOrganGestor(organGestor.getNom(), organGestor.getCodi(), regOrgans));
		}

		return new RootOrganGestors(registres);
	}

	public static RootProcediments mapRegistresProcediments(Map<ProcedimentDto, List<HistoricAggregationProcedimentDto>> dades) {
		List<RegistresProcediment> registres = new ArrayList<>();

		List<RegistreProcediment> regOrgans = new ArrayList<>();
		for (ProcedimentDto procediment : dades.keySet()) {
			List<HistoricAggregationProcedimentDto> listHistorics = dades.get(procediment);
			for (HistoricAggregationProcedimentDto historic : listHistorics) {
				RegistreProcediment registre = new RegistreProcediment();
				BeanUtils.copyProperties(historic, registre);
				regOrgans.add(registre);
			}
			registres.add(new RegistresProcediment(procediment.getNom(), procediment.getCodi(), regOrgans));
		}

		return new RootProcediments(registres);
	}

	public static RootEstat mapRegistresEstat(Map<NotificacioEstatEnumDto, List<HistoricAggregationEstatDto>> dades) {
		List<RegistresEstat> registres = new ArrayList<>();

		List<RegistreEstat> regOrgans = new ArrayList<>();
		for (NotificacioEstatEnumDto estat : dades.keySet()) {
			List<HistoricAggregationEstatDto> listHistorics = dades.get(estat);
			for (HistoricAggregationEstatDto historic : listHistorics) {
				RegistreEstat registre = new RegistreEstat();
				BeanUtils.copyProperties(historic, registre);
				regOrgans.add(registre);
			}
			registres.add(new RegistresEstat(estat.toString(), regOrgans));
		}

		return new RootEstat(registres);
	}

	public static RootGrup mapRegistresGrup(Map<GrupDto, List<HistoricAggregationGrupDto>> dades) {
		List<RegistresGrup> registres = new ArrayList<>();

		List<RegistreGrup> regOrgans = new ArrayList<>();
		for (GrupDto grup : dades.keySet()) {
			List<HistoricAggregationGrupDto> listHistorics = dades.get(grup);
			for (HistoricAggregationGrupDto historic : listHistorics) {
				RegistreGrup registre = new RegistreGrup();
				BeanUtils.copyProperties(historic, registre);
				regOrgans.add(registre);
			}
			registres.add(new RegistresGrup(grup.getCodi(), regOrgans));
		}

		return new RootGrup(registres);
	}
	public static RootUsuari mapRegistresUsuari(Map<UsuariDto, List<HistoricAggregationUsuariDto>> dades) {
		List<RegistresUsuari> registres = new ArrayList<>();

		List<RegistreUsuari> regOrgans = new ArrayList<>();
		for (UsuariDto usuari : dades.keySet()) {
			List<HistoricAggregationUsuariDto> listHistorics = dades.get(usuari);
			for (HistoricAggregationUsuariDto historic : listHistorics) {
				RegistreUsuari registre = new RegistreUsuari();
				BeanUtils.copyProperties(historic, registre);
				regOrgans.add(registre);
			}
			registres.add(new RegistresUsuari(usuari.getCodi(), regOrgans));
		}

		return new RootUsuari(registres);
	}

	/**********
	 * 
	 * ORGANS GESTORS
	 *
	 **********/

	@XmlRootElement(name = "registres-organGestor")
	@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
	public static class RootOrganGestors extends RootData {
		@XmlElement(name = "organ_gestor")
		@JsonProperty("organs_gestors")
		public List<RegistresOrganGestor> registres;

		public RootOrganGestors(List<RegistresOrganGestor> registres) {
			super();
			this.registres = registres;
		}

		public RootOrganGestors() {
		}

	}

	public static class RegistresOrganGestor {

		@XmlAttribute(name = "nom")
		public String nom;
		@XmlAttribute(name = "codi")
		public String codi;

		@XmlElement(name = "registre")
		public List<RegistreOrganGestor> registres;

		public RegistresOrganGestor(String nom, String codi, List<RegistreOrganGestor> registres) {
			super();
			this.nom = nom;
			this.codi = codi;
			this.registres = registres;
		}

		public RegistresOrganGestor() {
		}

	}

	@Setter
	public static class RegistreOrganGestor extends Registre {
		public Long numEnviaments;
		public Long numProcediments;
		public Long numGrups;
	}

	/**********
	 * 
	 * PROCEDIMENTS
	 *
	 **********/

	@XmlRootElement(name = "registres-procediments")
	@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
	public static class RootProcediments extends RootData {

		@XmlElement(name = "procediment")
		@JsonProperty("procediments")
		public List<RegistresProcediment> registres;

		public RootProcediments(List<RegistresProcediment> registres) {
			super();
			this.registres = registres;
		}

		public RootProcediments() {	}

	}

	public static class RegistresProcediment {

		@XmlAttribute(name = "nom")
		public String nom;
		@XmlAttribute(name = "codi")
		public String codiSia;

		@XmlElement(name = "registre")
		public List<RegistreProcediment> registres;

		public RegistresProcediment(String nom, String codi, List<RegistreProcediment> registres) {
			super();
			this.nom = nom;
			this.codiSia = codi;
			this.registres = registres;
		}

		public RegistresProcediment() { }

	}

	@Setter
	public static class RegistreProcediment extends Registre {
		public Long numEnviaments;
		public Long numGrups;
	}	

	/**********
	 * 
	 * ESTAT
	 *
	 **********/

	@XmlRootElement(name = "registres-procediments")
	@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
	public static class RootEstat extends RootData {

		@XmlElement(name = "estat")
		@JsonProperty("estats")
		public List<RegistresEstat> registres;

		public RootEstat(List<RegistresEstat> registres) {
			super();
			this.registres = registres;
		}

		public RootEstat() {	}

	}

	public static class RegistresEstat {

		@XmlAttribute(name = "estat")
		public String estat;

		@XmlElement(name = "registre")
		public List<RegistreEstat> registres;

		public RegistresEstat(String estat, List<RegistreEstat> registres) {
			super();
			this.estat = estat;
			this.registres = registres;
		}

		public RegistresEstat() { }

	}

	@Setter
	public static class RegistreEstat extends Registre { }

	
	/**********
	 * 
	 * GRUP
	 *
	 **********/

	@XmlRootElement(name = "registres-grups")
	@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
	public static class RootGrup extends RootData {

		@XmlElement(name = "grup")
		@JsonProperty("grups")
		public List<RegistresGrup> registres;

		public RootGrup(List<RegistresGrup> registres) {
			super();
			this.registres = registres;
		}

		public RootGrup() {	}

	}

	public static class RegistresGrup {

		@XmlAttribute(name = "grup")
		public String grup;

		@XmlElement(name = "registre")
		public List<RegistreGrup> registres;

		public RegistresGrup(String grup, List<RegistreGrup> registres) {
			super();
			this.grup = grup;
			this.registres = registres;
		}

		public RegistresGrup() { }

	}

	@Setter
	public static class RegistreGrup extends Registre { }
	
	
	/**********
	 * 
	 * USUARI
	 *
	 **********/

	@XmlRootElement(name = "registres-usuaris")
	@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
	public static class RootUsuari extends RootData {

		@XmlElement(name = "usuari")
		@JsonProperty("usuaris")
		public List<RegistresUsuari> registres;

		public RootUsuari(List<RegistresUsuari> registres) {
			super();
			this.registres = registres;
		}

		public RootUsuari() {	}

	}

	public static class RegistresUsuari {

		@XmlAttribute(name = "usuari")
		public String usuari;

		@XmlElement(name = "registre")
		public List<RegistreUsuari> registres;

		public RegistresUsuari(String usuari, List<RegistreUsuari> registres) {
			super();
			this.usuari = usuari;
			this.registres = registres;
		}

		public RegistresUsuari() { }

	}

	@Setter
	public static class RegistreUsuari extends Registre { }
	
	/**********
	 * 
	 * GENERALS
	 *
	 **********/
	public static class RootData {

		@XmlAttribute
		@XmlJavaTypeAdapter(DateAdapter.class)
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
		public Date generationDate;
		
		public RootData() {
			this.generationDate = new Date();
		}

	}

	@Setter
	@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
	public static class Registre {
		@XmlAttribute
		@XmlJavaTypeAdapter(DateAdapter.class)
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
		public Date data;
		
		public Long numNotTotal;
		public Long numComTotal;

		public Long numNotCorrectes;
		public Long numComCorrectes;

		public Long numNotAmbError;
		public Long numComAmbError;

		public Long numNotProcedimentComu;
		public Long numComProcedimentComu;

		public Long numNotAmbGrup;
		public Long numComAmbGrup;

		public Long numNotOrigenApi;
		public Long numComOrigenApi;

		public Long numNotOrigenWeb;
		public Long numComOrigenWeb;

	}

	public static class DateAdapter extends XmlAdapter<String, Date> {

		private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

		@Override
		public String marshal(Date v) throws Exception {
			synchronized (dateFormat) {
				return dateFormat.format(v);
			}
		}

		@Override
		public Date unmarshal(String v) throws Exception {
			synchronized (dateFormat) {
				return dateFormat.parse(v);
			}
		}

	}

}
