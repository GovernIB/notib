-- #132
ALTER TABLE NOT_USUARI ADD IDIOMA character varying(2);
UPDATE NOT_USUARI SET IDIOMA = 'CA' WHERE IDIOMA IS NULL;