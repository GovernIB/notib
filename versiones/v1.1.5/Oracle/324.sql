-- descomentar en cas de no existir la columna organ_gestor
-- ALTER TABLE NOT_NOTIFICACIO ADD organ_gestor VARCHAR2(64 CHAR);

UPDATE  NOT_NOTIFICACIO n
SET     n.organ_gestor = (
        SELECT  p.organ_gestor
        FROM    NOT_PROCEDIMENT p
        WHERE   p.id = n.procediment_id)
WHERE n.organ_gestor IS NULL;

ALTER TABLE NOT_NOTIFICACIO ADD ( CONSTRAINT NOT_NOT_ORGAN_FK FOREIGN KEY (ORGAN_GESTOR) REFERENCES NOT_ORGAN_GESTOR (CODI));