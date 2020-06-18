-- #215
ALTER TABLE NOT_APLICACIO ADD ENTITAT_ID BIGSERIAL(19);
ALTER TABLE NOT_APLICACIO ADD CONSTRAINT NOT_APLICACIO_ENTITAT_FK FOREIGN KEY (ENTITAT_ID) REFERENCES NOT_ENTITAT (ID);
UPDATE NOT_APLICACIO SET ENTITAT_ID = (SELECT ID FROM NOT_ENTITAT WHERE DIR3_CODI LIKE 'A04003003');
ALTER TABLE NOT_APLICACIO ALTER COLUMN ENTITAT_ID SET NOT NULL;