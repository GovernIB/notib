-- #260
ALTER TABLE NOT_PROCEDIMENT DROP COLUMN LLIBRE;
ALTER TABLE NOT_PROCEDIMENT DROP COLUMN LLIBRE_NOM;
ALTER TABLE NOT_PROCEDIMENT DROP COLUMN OFICINA;
ALTER TABLE NOT_PROCEDIMENT DROP COLUMN OFICINA_NOM;

ALTER TABLE NOT_ORGAN_GESTOR ADD LLIBRE VARCHAR2(255 CHAR);
ALTER TABLE NOT_ORGAN_GESTOR ADD LLIBRE_NOM VARCHAR2(255 CHAR);

ALTER TABLE NOT_ENTITAT ADD OFICINA VARCHAR2(255 CHAR);