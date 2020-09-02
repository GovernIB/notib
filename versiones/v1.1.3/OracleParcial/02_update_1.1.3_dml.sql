-- #189
INSERT INTO NOT_ORGAN_GESTOR (ID, CODI, ENTITAT)
SELECT NOT_HIBERNATE_SEQ.NEXTVAL, CODI, ENTITAT
FROM (  SELECT DISTINCT PRO.ORGAN_GESTOR AS CODI, PRO.ENTITAT AS ENTITAT
        FROM NOT_PROCEDIMENT PRO
        WHERE PRO.ORGAN_GESTOR IS NOT NULL);

-- #215
UPDATE NOT_APLICACIO SET ENTITAT_ID = (SELECT ID FROM NOT_ENTITAT WHERE DIR3_CODI LIKE 'A04003003');

-- #223
UPDATE NOT_NOTIFICACIO SET CALLBACK_ERROR = 1 WHERE ID IN (
	SELECT N.ID
	FROM
	    NOT_NOTIFICACIO_EVENT E
	    LEFT OUTER JOIN NOT_NOTIFICACIO N ON E.NOTIFICACIO_ID = N.ID
	WHERE
	    N.TIPUS_USUARI = 0
	    AND E.ERROR = 1
	    AND ( E.TIPUS = 8 )
	    AND ( E.ID IN (
	        SELECT MAX(E2.ID)
	        FROM NOT_NOTIFICACIO_EVENT E2
	             LEFT OUTER JOIN NOT_NOTIFICACIO N2 ON E2.NOTIFICACIO_ID = N2.ID
            WHERE E2.TIPUS = 8
	        GROUP BY N2.ID ) 
	    )
);