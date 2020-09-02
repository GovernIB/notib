-- #190
ALTER TABLE NOT_PROCEDIMENT ADD ULTIMA_ACT timestamp without time zone;

-- #263
ALTER TABLE NOT_GRUP ADD ORGAN_GESTOR BIGSERIAL(19);
ALTER TABLE NOT_PAGADOR_CIE ADD ORGAN_GESTOR BIGSERIAL(19);
ALTER TABLE NOT_PAGADOR_POSTAL ADD ORGAN_GESTOR BIGSERIAL(19);

ALTER TABLE NOT_GRUP ADD CONSTRAINT NOT_GRUP_ORGAN_FK FOREIGN KEY (ORGAN_GESTOR) REFERENCES NOT_ORGAN_GESTOR (ID);
ALTER TABLE NOT_PAGADOR_CIE ADD CONSTRAINT NOT_PAGCIE_ORGAN_FK FOREIGN KEY (ORGAN_GESTOR) REFERENCES NOT_ORGAN_GESTOR (ID);
ALTER TABLE NOT_PAGADOR_POSTAL ADD CONSTRAINT NOT_PAGPOSTAL_ORGAN_FK FOREIGN KEY (ORGAN_GESTOR) REFERENCES NOT_ORGAN_GESTOR (ID);
