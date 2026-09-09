// L'aplicació notib-back serveix, sota el mateix desplegament, la interfície nova (React, a partir
// d'aquesta mateixa base) i l'antiga (JSP), a l'arrel de l'aplicació. Aquí es reconstrueix aquesta
// arrel a partir de BASE_URL (que als builds reals -dev i build-jboss- val '/notibback/reactapp/')
// traient-ne el sufix 'reactapp/'. Si mai es construeix sense aquest sufix (build local sense
// --base) es fa servir '/notibback/', que és el context path fix d'aquesta aplicació.
const REACTAPP_SUFFIX = /reactapp\/?$/;

const getAppBasePath = (): string => {
    const baseUrl = import.meta.env.BASE_URL;
    return REACTAPP_SUFFIX.test(baseUrl) ? baseUrl.replace(REACTAPP_SUFFIX, '') : '/notibback/';
};

// URL de la interfície clàssica (JSP). "/index" hi redirigeix a la pantalla que correspon segons el
// rol/sessió actual, igual que fa l'arrel de l'aplicació quan la interfície per defecte és la
// clàssica (vegeu NotibController.root a notib-back).
export const getClassicAppUrl = (): string => window.location.origin + getAppBasePath() + 'index';

// URL del recurs que retorna, per a l'usuari autenticat actual, els rols que efectivament té
// concedits (calculats al servidor, no simplement decodificant el JWT: n'hi ha, com l'administrador
// d'òrgan, que es concedeixen des de NOTIB i no formen part del token de Keycloak).
export const getAuthRolesUrl = (): string => window.location.origin + getAppBasePath() + 'authRoles?format=json';

const INTERFICIE_COOKIE = 'notibInterficie';

// Marca, en una cookie, quina interfície ha triat explícitament l'usuari. En canviar d'interfície la
// navegació surt de la SPA (no és una petició fetch) i pot passar per una reautenticació (p.ex. si la
// sessió del navegador encara no té sessió amb la interfície clàssica), que pot acabar redirigint a
// l'arrel de l'aplicació en lloc de la pàgina concreta demanada. NotibController.root() llegeix
// aquesta cookie per no aplicar, en aquest cas, la propietat que decideix la interfície per defecte.
// Es marca abans de navegar-hi perquè, sent una cookie del navegador, sobreviu a qualsevol redirecció
// intermèdia (a diferència d'un paràmetre a la URL, que es podria perdre pel camí).
export const markInterficiePreference = (interficie: 'react' | 'jsp'): void => {
    const path = getAppBasePath().replace(/\/$/, '') || '/';
    const oneYearInSeconds = 60 * 60 * 24 * 365;
    document.cookie = `${INTERFICIE_COOKIE}=${interficie}; path=${path}; max-age=${oneYearInSeconds}`;
};
