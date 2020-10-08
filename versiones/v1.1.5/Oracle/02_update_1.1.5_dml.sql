-- #324
UPDATE  NOT_NOTIFICACIO n
SET     n.organ_gestor = (
        SELECT  p.organ_gestor
        FROM    NOT_PROCEDIMENT p
        WHERE   p.id = n.procediment_id)
WHERE n.organ_gestor IS NULL;