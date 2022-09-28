package es.caib.notib.logic.intf.dto;

public class InteresadoWsDto {
	protected DatosInteresadoWsDto interesado;
    protected DatosInteresadoWsDto representante;
    
	public DatosInteresadoWsDto getInteresado() {
		return interesado;
	}
	public void setInteresado(DatosInteresadoWsDto interesado) {
		this.interesado = interesado;
	}
	public DatosInteresadoWsDto getRepresentante() {
		return representante;
	}
	public void setRepresentante(DatosInteresadoWsDto representante) {
		this.representante = representante;
	}
    
    
}
