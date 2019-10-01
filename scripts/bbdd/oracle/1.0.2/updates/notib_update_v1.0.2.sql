-- Update for version (1.0.1 -> 1.0.2)
-- #109
ALTER TABLE NOT_PERSONA ADD INCAPACITAT NUMBER(1,0);
UPDATE TABLE NOT_PERSONA SET INCAPACITAT = 0;

-- #115
ALTER TABLE NOT_NOTIFICACIO DROP (
    SEU_EXP_SERDOC, 
    SEU_EXP_UNIORG, 
    SEU_EXP_IDENI, 
    SEU_EXP_TITOL, 
    SEU_REG_OFICINA, 
    SEU_REG_LLIBRE,
    SEU_REG_ORGAN,
    SEU_IDIOMA,
    SEU_AVIS_TITOL,
    SEU_AVIS_TEXT,
    SEU_AVIS_MOBIL,
    SEU_OFICI_TITOL,
    SEU_OFICI_TEXT,
    SEU_PROC_CODI);

ALTER TABLE NOT_NOTIFICACIO DROP (
   	REGISTRE_OFICINA,
   	REGISTRE_ORGAN,
   	REGISTRE_LLIBRE,
   	REGISTRE_EXTRACTE,
   	REGISTRE_DOC_FISICA,
   	REGISTRE_IDIOMA,
   	REGISTRE_TIPUS_ASSUMPTE,
   	REGISTRE_REF_EXTERNA,
   	REGISTRE_CODI_ASSUMPTE,
   	REGISTRE_OBSERVACIONS);
   	
ALTER TABLE NOT_NOTIFICACIO DROP (
   	PROC_CODI_SIA);
   	
ALTER TABLE NOT_NOTIFICACIO_ENV DROP (
    SEU_REG_NUMERO, 
    SEU_REG_DATA, 
    SEU_DATA_FI, 
    SEU_ESTAT, 
    SEU_ERROR, 
    SEU_ERROR_EVENT_ID,
    SEU_DATA_ESTAT,
    SEU_DATA_NOTINF,
    SEU_DATA_NOTIDP,
    SEU_INTENT_DATA,
    SEU_FITXER_CODI,
    SEU_FITXER_CLAU);

-- #130
ALTER TABLE NOT_NOTIFICACIO MODIFY (CONCEPTE VARCHAR2(50 CHAR));

-- #131
ALTER TABLE NOT_PERSONA MODIFY (NOM VARCHAR2(255 CHAR));
ALTER TABLE NOT_PERSONA MODIFY (LLINATGE1 VARCHAR2(40 CHAR));
ALTER TABLE NOT_PERSONA MODIFY (LLINATGE2 VARCHAR2(40 CHAR));
ALTER TABLE NOT_PERSONA MODIFY (EMAIL VARCHAR2(255 CHAR));

ALTER TABLE NOT_NOTIFICACIO MODIFY (DESCRIPCIO VARCHAR2(1000 CHAR));

ALTER TABLE NOT_NOTIFICACIO_ENV MODIFY (DOM_VIA_NOM VARCHAR2(50 CHAR));
ALTER TABLE NOT_NOTIFICACIO_ENV MODIFY (DOM_NUM_NUM VARCHAR2(5 CHAR));
ALTER TABLE NOT_NOTIFICACIO_ENV MODIFY (DOM_LINEA1 VARCHAR2(50 CHAR));
ALTER TABLE NOT_NOTIFICACIO_ENV MODIFY (DOM_LINEA2 VARCHAR2(50 CHAR));

ALTER TABLE NOT_GRUP MODIFY (CODI VARCHAR2(255 CHAR));
ALTER TABLE NOT_GRUP MODIFY (NOM VARCHAR2(255 CHAR));

-- #133
ALTER TABLE NOT_NOTIFICACIO ADD REGISTRE_ENV_INTENT NUMBER(10,0);
UPDATE NOT_NOTIFICACIO SET REGISTRE_ENV_INTENT = 0 WHERE REGISTRE_ENV_INTENT IS NULL;
