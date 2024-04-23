-- #104
ALTER TABLE NOT_PAGADOR_POSTAL ADD ENTITAT BIGSERIAL(19);
ALTER TABLE NOT_PAGADOR_CIE ADD ENTITAT BIGSERIAL(19);

UPDATE NOT_PAGADOR_POSTAL SET ENTITAT = 1;
UPDATE NOT_PAGADOR_CIE SET ENTITAT = 1;

ALTER TABLE ONLY NOT_PAGADOR_POSTAL ADD (
  CONSTRAINT NOT_PAGADOR_POSTAL_ENTITAT_FK FOREIGN KEY (ENTITAT)
    REFERENCES NOT_ENTITAT (ID));

ALTER TABLE ONLY NOT_PAGADOR_CIE ADD (
  CONSTRAINT NOT_PAGADOR_CIE_ENTITAT_FK FOREIGN KEY (ENTITAT)
    REFERENCES NOT_ENTITAT (ID));