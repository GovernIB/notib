package es.caib.notib.persist.entity.explotacio;

import es.caib.notib.client.domini.EnviamentTipus;
import es.caib.notib.client.domini.explotacio.EnviamentOrigen;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.io.Serializable;

@Entity
@Table(name = "not_explot_dim",
		uniqueConstraints = {@UniqueConstraint(name = "not_explot_dim_uk", columnNames = {"entitat_id", "procediment_id", "organ_codi", "usuari_codi", "tipus", "origen"})}
)
@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode
public class ExplotDimensioEntity extends AbstractPersistable<Long> implements Serializable {

	private static final long serialVersionUID = 2900135379128738307L;

	@Column(name = "entitat_id")
	private Long entitatId;

	@Column(name = "procediment_id")
	private Long procedimentId;

	@Column(name = "organ_codi")
	private String organCodi;

	@Column(name = "usuari_codi")
	private String usuariCodi;

	@Column(name = "tipus")
	@Enumerated(EnumType.STRING)
	private EnviamentTipus tipus;

	@Column(name = "origen")
	@Enumerated(EnumType.STRING)
	private EnviamentOrigen origen;

}
