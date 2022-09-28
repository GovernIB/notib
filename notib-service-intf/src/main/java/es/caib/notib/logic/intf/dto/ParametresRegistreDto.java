package es.caib.notib.logic.intf.dto;

public class ParametresRegistreDto extends AuditoriaDto {

	private String organ;
	private String oficina;
	private String llibre;
	
	public String getOrgan() {
		return organ;
	}

	public void setOrgan(String organ) {
		this.organ = organ;
	}

	public String getOficina() {
		return oficina;
	}

	public void setOficina(String oficina) {
		this.oficina = oficina;
	}

	public String getLlibre() {
		return llibre;
	}

	public void setLlibre(String llibre) {
		this.llibre = llibre;
	}
	
	private static final long serialVersionUID = -3496228309587107013L;

}
