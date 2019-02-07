/**
 * 
 */
package es.caib.notib.war.command;
import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.notib.core.api.dto.ColumnesDto;
import es.caib.notib.war.helper.ConversioTipusHelper;
/**
 * Command per definir la visibilitat de columnes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ColumnesCommand {

	private Long id;
	private boolean dataEnviament;
	private boolean dataProgramada;
	private boolean notIdentificador;
	private boolean proCodi; 
	private boolean grupCodi; 
	private boolean dir3Codi; 
	private boolean usuari; 
	private boolean enviamentTipus; 
	private boolean concepte; 
	private boolean descripcio; 
	private boolean titularNif; 
	private boolean titularNomLlinatge; 
	private boolean titularEmail;
	private boolean destinataris; 
	private boolean llibreRegistre; 
	private boolean numeroRegistre; 
	private boolean dataRegistre; 
	private boolean dataCaducitat; 
	private boolean codiNotibEnviament; 
	private boolean numCertificacio; 
	private boolean csvUuid; 
	private boolean estat;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public boolean isDataEnviament() {
		return dataEnviament;
	}
	public void setDataEnviament(boolean dataEnviament) {
		this.dataEnviament = dataEnviament;
	}
	public boolean isDataProgramada() {
		return dataProgramada;
	}
	public void setDataProgramada(boolean dataProgramada) {
		this.dataProgramada = dataProgramada;
	}
	public boolean isNotIdentificador() {
		return notIdentificador;
	}
	public void setNotIdentificador(boolean notIdentificador) {
		this.notIdentificador = notIdentificador;
	}
	public boolean isProCodi() {
		return proCodi;
	}
	public void setProCodi(boolean proCodi) {
		this.proCodi = proCodi;
	}
	public boolean isGrupCodi() {
		return grupCodi;
	}
	public void setGrupCodi(boolean grupCodi) {
		this.grupCodi = grupCodi;
	}
	public boolean isDir3Codi() {
		return dir3Codi;
	}
	public void setDir3Codi(boolean dir3Codi) {
		this.dir3Codi = dir3Codi;
	}
	public boolean isUsuari() {
		return usuari;
	}
	public void setUsuari(boolean usuari) {
		this.usuari = usuari;
	}
	public boolean isEnviamentTipus() {
		return enviamentTipus;
	}
	public void setEnviamentTipus(boolean enviamentTipus) {
		this.enviamentTipus = enviamentTipus;
	}
	public boolean isConcepte() {
		return concepte;
	}
	public void setConcepte(boolean concepte) {
		this.concepte = concepte;
	}
	public boolean isDescripcio() {
		return descripcio;
	}
	public void setDescripcio(boolean descripcio) {
		this.descripcio = descripcio;
	}
	public boolean isTitularNif() {
		return titularNif;
	}
	public void setTitularNif(boolean titularNif) {
		this.titularNif = titularNif;
	}
	public boolean isTitularNomLlinatge() {
		return titularNomLlinatge;
	}
	public void setTitularNomLlinatge(boolean titularNomLlinatge) {
		this.titularNomLlinatge = titularNomLlinatge;
	}
	public boolean isTitularEmail() {
		return titularEmail;
	}
	public void setTitularEmail(boolean titularEmail) {
		this.titularEmail = titularEmail;
	}
	public boolean isDestinataris() {
		return destinataris;
	}
	public void setDestinataris(boolean destinataris) {
		this.destinataris = destinataris;
	}
	public boolean isLlibreRegistre() {
		return llibreRegistre;
	}
	public void setLlibreRegistre(boolean llibreRegistre) {
		this.llibreRegistre = llibreRegistre;
	}
	public boolean isNumeroRegistre() {
		return numeroRegistre;
	}
	public void setNumeroRegistre(boolean numeroRegistre) {
		this.numeroRegistre = numeroRegistre;
	}
	public boolean isDataRegistre() {
		return dataRegistre;
	}
	public void setDataRegistre(boolean dataRegistre) {
		this.dataRegistre = dataRegistre;
	}
	public boolean isDataCaducitat() {
		return dataCaducitat;
	}
	public void setDataCaducitat(boolean dataCaducitat) {
		this.dataCaducitat = dataCaducitat;
	}
	public boolean isCodiNotibEnviament() {
		return codiNotibEnviament;
	}
	public void setCodiNotibEnviament(boolean codiNotibEnviament) {
		this.codiNotibEnviament = codiNotibEnviament;
	}
	public boolean isNumCertificacio() {
		return numCertificacio;
	}
	public void setNumCertificacio(boolean numCertificacio) {
		this.numCertificacio = numCertificacio;
	}
	public boolean isCsvUuid() {
		return csvUuid;
	}
	public void setCsvUuid(boolean csvUuid) {
		this.csvUuid = csvUuid;
	}
	public boolean isEstat() {
		return estat;
	}
	public void setEstat(boolean estat) {
		this.estat = estat;
	}
	
	public static ColumnesCommand asCommand(ColumnesDto dto) {
		return ConversioTipusHelper.convertir(
				dto,
				ColumnesCommand.class);
	}
	public static ColumnesDto asDto(ColumnesCommand command) {
		return ConversioTipusHelper.convertir(
				command,
				ColumnesDto.class);
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
