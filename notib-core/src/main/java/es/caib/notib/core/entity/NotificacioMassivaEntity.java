package es.caib.notib.core.entity;

import es.caib.notib.core.audit.NotibAuditable;
import lombok.*;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Formula;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(name="NOT_NOTIFICACIO_MASSIVA")
@EntityListeners(AuditingEntityListener.class)
public class NotificacioMassivaEntity extends NotibAuditable<Long> {
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ENTITAT_ID")
    @ForeignKey(name = "NOT_MASSIVA_ENTITAT_FK")
    protected EntitatEntity entitat;

    @Builder.Default
    @Column(name = "PROGRESS", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
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

    @Formula("(case  when progress = 0 then " +
            "           'PENDENT' " +
            "        when progress < 100 then " +
            "           'EN_PROCES' " +
            "        else" +
            "           'FINALITZAT' " +
            " end)")
    private String estat;

//    public NotificacioMassivaEstatDto getEstat(){
//        if (progress == 0) {
//            return NotificacioMassivaEstatDto.PENDENT;
//        } else if(progress < 100) {
//            return NotificacioMassivaEstatDto.EN_PROCES;
//        } else {
//            return NotificacioMassivaEstatDto.FINALITZAT;
//        }
//    }

    public void updateProgress(int progress) {
        this.progress = progress;
    }

    public void joinNotificacio(NotificacioEntity notificacioEntity) {
        if (notificacions == null){
            notificacions = new ArrayList<>();
        }
        notificacions.add(notificacioEntity);
    }
}
