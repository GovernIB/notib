CREATE TABLE not_explot_env_info (
    id                         NUMBER(38, 0) NOT NULL,
    entitat_id                 NUMBER(38, 0) NOT NULL,
    procediment_id             NUMBER(38, 0),
    organ_gestor_codi          VARCHAR2(64 CHAR) NOT NULL,
    usuari_codi                VARCHAR2(64 CHAR),
    enviament_tipus            VARCHAR2(16 CHAR) NOT NULL,
    origen                     VARCHAR2(16 CHAR) NOT NULL,
    data_creacio               TIMESTAMP NOT NULL,
    temps_pendent              NUMBER(38, 0),
    data_reg_env_error         TIMESTAMP,
    intents_reg_enviament      INTEGER,
    data_registrada            TIMESTAMP,
    temps_registrada           NUMBER(38, 0),
    intents_sir_consulta       INTEGER,
    data_reg_acceptada         TIMESTAMP,
    data_reg_rebutjada         TIMESTAMP,
    data_not_enviament_error   TIMESTAMP,
    intents_not_enviament      INTEGER,
    data_not_enviada           TIMESTAMP,
    temps_not_enviada          NUMBER(38, 0),
    data_not_notificada        TIMESTAMP,
    data_not_rebutjada         TIMESTAMP,
    data_not_expirada          TIMESTAMP,
    data_not_error             TIMESTAMP,
    data_cie_enviament_error   TIMESTAMP,
    intents_cie_enviament      INTEGER,
    data_cie_enviada           TIMESTAMP,
    temps_cie_enviada          NUMBER(38, 0),
    data_cie_notificada        TIMESTAMP,
    data_cie_rebutjada         TIMESTAMP,
    data_cie_cancelada         TIMESTAMP,
    data_cie_error             TIMESTAMP,
    data_email_enviament_error TIMESTAMP,
    intents_email_enviament    INTEGER,
    data_email_enviada         TIMESTAMP,
    temps_total                NUMBER(38, 0),
    enviament_id               NUMBER(38, 0) NOT NULL,
    CONSTRAINT PK_NOT_EXPLOT_ENV_INFO PRIMARY KEY (id));
ALTER TABLE not_explot_env_info ADD CONSTRAINT not_explot_env_uk UNIQUE (enviament_id);

CREATE INDEX not_expinfo_dcreacio_i ON not_explot_env_info(TRUNC(data_creacio));
CREATE INDEX not_expinfo_dreg_env_error_i ON not_explot_env_info(TRUNC(data_reg_env_error));
CREATE INDEX not_expinfo_dregistrada_i ON not_explot_env_info(TRUNC(data_registrada));
CREATE INDEX not_expinfo_dreg_acceptada_i ON not_explot_env_info(TRUNC(data_reg_acceptada));
CREATE INDEX not_expinfo_dreg_rebutjada_i ON not_explot_env_info(TRUNC(data_reg_rebutjada));
CREATE INDEX not_expinfo_dnot_env_error_i ON not_explot_env_info(TRUNC(data_not_enviament_error));
CREATE INDEX not_expinfo_dnot_enviada_i ON not_explot_env_info(TRUNC(data_not_enviada));
CREATE INDEX not_expinfo_dnot_notificada_i ON not_explot_env_info(TRUNC(data_not_notificada));
CREATE INDEX not_expinfo_dnot_rebutjada_i ON not_explot_env_info(TRUNC(data_not_rebutjada));
CREATE INDEX not_expinfo_dnot_expirada_i ON not_explot_env_info(TRUNC(data_not_expirada));
CREATE INDEX not_expinfo_dnot_error_i ON not_explot_env_info(TRUNC(data_not_error));
CREATE INDEX not_expinfo_dcie_env_error_i ON not_explot_env_info(TRUNC(data_cie_enviament_error));
CREATE INDEX not_expinfo_dcie_enviada_i ON not_explot_env_info(TRUNC(data_cie_enviada));
CREATE INDEX not_expinfo_dcie_notificada_i ON not_explot_env_info(TRUNC(data_cie_notificada));
CREATE INDEX not_expinfo_dcie_rebutjada_i ON not_explot_env_info(TRUNC(data_cie_rebutjada));
CREATE INDEX not_expinfo_dcie_cancelada_i ON not_explot_env_info(TRUNC(data_cie_cancelada));
CREATE INDEX not_expinfo_dcie_error_i ON not_explot_env_info(TRUNC(data_cie_error));
CREATE INDEX not_expinfo_demail_env_error_i ON not_explot_env_info(TRUNC(data_email_enviament_error));
CREATE INDEX not_expinfo_demail_enviada_i ON not_explot_env_info(TRUNC(data_email_enviada));

GRANT SELECT, UPDATE, INSERT, DELETE ON not_explot_env_info TO www_notib;

-- Changeset db/changelog/changes/2.0.9/explotacio2.yml::17458513344727::limit
ALTER TABLE not_explot_fet RENAME COLUMN pendent TO tot_pendent;
ALTER TABLE not_explot_fet RENAME COLUMN reg_env_error TO tot_reg_err;
ALTER TABLE not_explot_fet RENAME COLUMN registrada TO tot_registr;
ALTER TABLE not_explot_fet RENAME COLUMN reg_acceptada TO tot_sir_acc;
ALTER TABLE not_explot_fet RENAME COLUMN reg_rebutjada TO tot_sir_reb;
ALTER TABLE not_explot_fet RENAME COLUMN not_env_error TO tot_not_err;
ALTER TABLE not_explot_fet RENAME COLUMN not_enviada TO tot_not_env;
ALTER TABLE not_explot_fet RENAME COLUMN not_notificada TO tot_not_not;
ALTER TABLE not_explot_fet RENAME COLUMN not_rebutjada TO tot_not_reb;
ALTER TABLE not_explot_fet RENAME COLUMN not_expirada TO tot_not_exp;
ALTER TABLE not_explot_fet RENAME COLUMN cie_env_error TO tot_cie_err;
ALTER TABLE not_explot_fet RENAME COLUMN cie_enviada TO tot_cie_env;
ALTER TABLE not_explot_fet RENAME COLUMN cie_notificada TO tot_cie_not;
ALTER TABLE not_explot_fet RENAME COLUMN cie_rebutjada TO tot_cie_reb;
ALTER TABLE not_explot_fet RENAME COLUMN cie_error TO tot_cie_fal;
ALTER TABLE not_explot_fet RENAME COLUMN processada TO tot_process;

ALTER TABLE not_explot_fet ADD tr_creades NUMBER(38, 0);
ALTER TABLE not_explot_fet ADD tr_reg_err NUMBER(38, 0);
ALTER TABLE not_explot_fet ADD tr_registr NUMBER(38, 0);
ALTER TABLE not_explot_fet ADD tr_sir_acc NUMBER(38, 0);
ALTER TABLE not_explot_fet ADD tr_sir_reb NUMBER(38, 0);
ALTER TABLE not_explot_fet ADD tr_not_err NUMBER(38, 0);
ALTER TABLE not_explot_fet ADD tr_not_env NUMBER(38, 0);
ALTER TABLE not_explot_fet ADD tr_not_not NUMBER(38, 0);
ALTER TABLE not_explot_fet ADD tr_not_reb NUMBER(38, 0);
ALTER TABLE not_explot_fet ADD tr_not_exp NUMBER(38, 0);
ALTER TABLE not_explot_fet ADD tr_not_fal NUMBER(38, 0);
ALTER TABLE not_explot_fet ADD tr_cie_err NUMBER(38, 0);
ALTER TABLE not_explot_fet ADD tr_cie_env NUMBER(38, 0);
ALTER TABLE not_explot_fet ADD tr_cie_not NUMBER(38, 0);
ALTER TABLE not_explot_fet ADD tr_cie_reb NUMBER(38, 0);
ALTER TABLE not_explot_fet ADD tr_cie_can NUMBER(38, 0);
ALTER TABLE not_explot_fet ADD tr_cie_fal NUMBER(38, 0);
ALTER TABLE not_explot_fet ADD tr_eml_err NUMBER(38, 0);
ALTER TABLE not_explot_fet ADD tr_eml_env NUMBER(38, 0);
ALTER TABLE not_explot_fet ADD tmp_pnd NUMBER(38, 0);
ALTER TABLE not_explot_fet ADD tmp_reg NUMBER(38, 0);
ALTER TABLE not_explot_fet ADD tmp_not NUMBER(38, 0);
ALTER TABLE not_explot_fet ADD tmp_cie NUMBER(38, 0);
ALTER TABLE not_explot_fet ADD tmp_tot NUMBER(38, 0);
ALTER TABLE not_explot_fet ADD tmp_reg_sac NUMBER(38, 0);
ALTER TABLE not_explot_fet ADD tmp_reg_srb NUMBER(38, 0);
ALTER TABLE not_explot_fet ADD tmp_reg_not NUMBER(38, 0);
ALTER TABLE not_explot_fet ADD tmp_not_not NUMBER(38, 0);
ALTER TABLE not_explot_fet ADD tmp_not_reb NUMBER(38, 0);
ALTER TABLE not_explot_fet ADD tmp_not_exp NUMBER(38, 0);
ALTER TABLE not_explot_fet ADD tmp_not_fal NUMBER(38, 0);
ALTER TABLE not_explot_fet ADD tmp_cie_not NUMBER(38, 0);
ALTER TABLE not_explot_fet ADD tmp_cie_reb NUMBER(38, 0);
ALTER TABLE not_explot_fet ADD tmp_cie_can NUMBER(38, 0);
ALTER TABLE not_explot_fet ADD tmp_cie_fal NUMBER(38, 0);
ALTER TABLE not_explot_fet ADD tmp_tot_nac NUMBER(38, 0);
ALTER TABLE not_explot_fet ADD tmp_tot_nrb NUMBER(38, 0);
ALTER TABLE not_explot_fet ADD tmp_tot_nex NUMBER(38, 0);
ALTER TABLE not_explot_fet ADD tmp_tot_nfl NUMBER(38, 0);
ALTER TABLE not_explot_fet ADD tmp_tot_cac NUMBER(38, 0);
ALTER TABLE not_explot_fet ADD int_reg NUMBER(38, 0);
ALTER TABLE not_explot_fet ADD int_sir NUMBER(38, 0);
ALTER TABLE not_explot_fet ADD int_not NUMBER(38, 0);
ALTER TABLE not_explot_fet ADD int_cie NUMBER(38, 0);
ALTER TABLE not_explot_fet ADD int_eml NUMBER(38, 0);
