-- #316
ALTER TABLE NOT_ENTITAT ADD LLIBRE VARCHAR2(255 CHAR);
ALTER TABLE NOT_ENTITAT ADD LLIBRE_NOM VARCHAR2(255 CHAR);
ALTER TABLE NOT_ENTITAT ADD LLIBRE_ENTITAT NUMBER(1,0);

-- #324
ALTER TABLE NOT_NOTIFICACIO ADD ( CONSTRAINT NOT_NOT_ORGAN_FK FOREIGN KEY (ORGAN_GESTOR) REFERENCES NOT_ORGAN_GESTOR (CODI));