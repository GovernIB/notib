package es.caib.notib.persist.entity;

import es.caib.notib.logic.intf.dto.notificacio.NotificacioMassivaEstatDto;
import es.caib.notib.persist.audit.NotibAuditable;
import es.caib.notib.persist.entity.cie.PagadorPostalEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.ForeignKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Slf4j
@Entity
@Table(name="NOT_NOTIFICACIO_MASSIVA")
@EntityListeners(AuditingEntityListener.class)
public class NotificacioMassivaEntity extends NotibAuditable<Long> {

    @Transient
    private final Object procesLock = new Object();

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ENTITAT_ID")
    @ForeignKey(name = "NOT_MASSIVA_ENTITAT_FK")
    protected EntitatEntity entitat;

    @Builder.Default
    @Column(name = "PROGRESS", length = 20, nullable = false)
//    @Enumerated(EnumType.STRING)
    private Integer progress = 0;

    @Column(name = "CSV_FILENAME", length = 200, nullable = false)
    private String csvFilename;
    @Column(name = "ZIP_FILENAME", length = 200, nullable = false)
    private String zipFilename;
    @Column(name = "CSV_GESDOC_ID", length = 64, nullable = false)
    private String csvGesdocId;
    @Column(name = "ZIP_GESDOC_ID", length = 64, nullable = false)
    private String zipGesdocId;
    @Setter
    @Column(name = "RESUM_GESDOC_ID", length = 64)
    private String resumGesdocId;
    @Setter
    @Column(name = "ERRORS_GESDOC_ID", length = 64)
    private String errorsGesdocId;

    @Column(name = "CADUCITAT", nullable = false)
    @Temporal(TemporalType.DATE)
    protected Date caducitat;

    @Column(name = "EMAIL", nullable = true)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PAGADOR_POSTAL_ID")
    @ForeignKey(name = "NOT_MASSIVA_PAGADOR_POSTAL_FK")
    private PagadorPostalEntity pagadorPostal;

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true)
    @ForeignKey(name = "NOT_NOTIF_NOTIF_MASSIVA_FK")
    @JoinColumn(name = "NOTIFICACIO_MASSIVA_ID") // we need to duplicate the physical information
    protected List<NotificacioEntity> notificacions;


    @Column(name = "estat_validacio", length = 32)
    @Enumerated(EnumType.STRING)
    private NotificacioMassivaEstatDto estatValidacio;

    @Column(name = "estat_proces", length = 32)
    @Enumerated(EnumType.STRING)
    private NotificacioMassivaEstatDto estatProces;

    @Column(name = "num_notificacions")
    private Integer totalNotificacions;
    @Column(name = "num_validades")
    private Integer notificacionsValidades;
    @Column(name = "num_processades")
    private Integer notificacionsProcessades;
    @Column(name = "num_error")
    private Integer notificacionsProcessadesAmbError;
    @Column(name = "num_cancelades")
    private Integer notificacionsCancelades;

//    @Formula("(case  when progress < 0 then " +
//            "           'ERRONIA' " +
//            "        when progress = 0 then " +
//            "           'PENDENT' " +
//            "        when progress < 100 then " +
//            "           'EN_PROCES' " +
//            "        else" +
//            "           'FINALITZAT' " +
//            " end)")
//    private String estat;

//    public void updateProgress(int progress) {
//        this.progress = progress;
//    }

    public void updateEstatValidacio(Integer notificacionsValidades) {
        this.notificacionsValidades = notificacionsValidades;
        if (notificacionsValidades == 0) {
            this.estatValidacio = NotificacioMassivaEstatDto.ERRONIA;
        } else if (notificacionsValidades < totalNotificacions) {
            this.estatValidacio = NotificacioMassivaEstatDto.FINALITZAT_AMB_ERRORS;
        } else {
            this.estatValidacio = NotificacioMassivaEstatDto.FINALITZAT;
        }
    }

    public void updateProcessadaToError() {
        log.info("[PROCES MASSIU] updateProcessadaToError");
        this.notificacionsProcessadesAmbError++;
        this.notificacionsProcessades--;
        updateProgres();
    }

    public void updateErrorToProcessada() {
        log.info("[PROCES MASSIU] updateErrorToProcessada");
        this.notificacionsProcessades++;
        this.notificacionsProcessadesAmbError--;
        updateProgres();
    }

    public void updateToProcessada() {
        log.info("[PROCES MASSIU] updateToProcessada");
        this.notificacionsProcessades++;
        updateProgres();
    }

    public void updateCancelades() {
        log.info("[PROCES MASSIU] updateCancelades");
        if (notificacionsCancelades == null) {
            notificacionsCancelades = 0;
        }
        notificacionsCancelades++;
        updateProgres();
    }

    public void updateToError() {
        log.info("[PROCES MASSIU] updateToError");
        this.notificacionsProcessadesAmbError++;
        updateProgres();
    }

    private void updateProgres() {
        this.progress = ((notificacionsProcessades + notificacionsProcessadesAmbError) * 100) / notificacionsValidades;
        log.info("[PROCES MASSIU] updateProgres (" + this.progress + ") - validades: " + notificacionsValidades + ", processades: " + notificacionsProcessades + ", error: " + notificacionsProcessadesAmbError);

        if (notificacionsCancelades != null && notificacionsCancelades == notificacions.size()) {
            this.estatProces = NotificacioMassivaEstatDto.CANCELADA;
            return;
        }

        if (notificacionsCancelades != null && notificacionsCancelades > 0) {
            this.estatProces = NotificacioMassivaEstatDto.FINALITZAT_PARCIAL;
            return;
        }

        if ((notificacionsProcessades + notificacionsProcessadesAmbError) == 0) {
            this.estatProces = NotificacioMassivaEstatDto.PENDENT;
        } else if ((notificacionsProcessades + notificacionsProcessadesAmbError) == notificacionsValidades) {
            if (notificacionsProcessadesAmbError > 0) {
                this.estatProces = NotificacioMassivaEstatDto.FINALITZAT_AMB_ERRORS;
            } else {
                this.estatProces = NotificacioMassivaEstatDto.FINALITZAT;
            }
        } else {
            if (notificacionsProcessadesAmbError > 0) {
                this.estatProces = NotificacioMassivaEstatDto.EN_PROCES_AMB_ERRORS;
            } else {
                this.estatProces = NotificacioMassivaEstatDto.EN_PROCES;
            }
        }
    }

    public void joinNotificacio(NotificacioEntity notificacioEntity) {
        if (notificacions == null){
            notificacions = new ArrayList<>();
        }
        notificacions.add(notificacioEntity);
    }
}
