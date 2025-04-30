package es.caib.notib.persist.entity.explotacio;

import es.caib.notib.client.domini.explotacio.DiaSetmanaEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.WeekFields;

@Entity
@Table(name = "not_explot_temps")
@Getter @Setter
@Builder @AllArgsConstructor
public class ExplotTempsEntity extends AbstractPersistable<Long> implements Serializable {

	private static final long serialVersionUID = -2144138256112639860L;

	@Column(name = "data")
	private LocalDate data;
	
	@Column(name = "anualitat")
	private Integer anualitat;
	
	@Column(name = "mes")
	private Integer mes;
	
	@Column(name = "trimestre")
	private Integer trimestre;
	
	@Column(name = "setmana")
	private Integer setmana;

	@Column(name = "dia")
	private Integer dia;

	@Column(name = "dia_setmana")
	@Enumerated(EnumType.STRING)
	private DiaSetmanaEnum diaSetmana;

	public ExplotTempsEntity() {
		super();

		emplenarCamps(LocalDate.now());
	}

	public ExplotTempsEntity(LocalDate data) {
		super();

		emplenarCamps(data);
	}

	private void emplenarCamps(LocalDate data) {
		this.data = data;
		this.anualitat = data.getYear();
		this.trimestre = data.getMonthValue() / 3;
		this.mes = data.getMonthValue();
		this.setmana = data.get(WeekFields.ISO.weekOfWeekBasedYear());
		this.dia = data.getDayOfMonth();
		this.diaSetmana = DiaSetmanaEnum.valueOfData(data.getDayOfWeek().name());
	}

}
