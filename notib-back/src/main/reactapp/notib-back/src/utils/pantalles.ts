import type { MenuEntry } from 'reactlib';
import {
    ROLE_ADMIN,
    ROLE_ADMIN_LECTURA, ROLE_ORGAN,
    ROLE_SUPER,
    ROLE_USER,
} from '../components/NotibContext';

/**
 * Rols amb els quals es pot operar a la interfície REACT: els mateixos que ofereix el selector de
 * rol (veure ALLOWED_ROLES a NotibProvider).
 */
export const ROLS_APLICACIO = [ROLE_SUPER, ROLE_ADMIN, ROLE_ADMIN_LECTURA, ROLE_USER];

/** Identificador de cada pantalla amb control d'accés. Una pantalla nova s'ha d'afegir aquí. */
export type Pantalla = 'home'
    | 'entitats'
    | 'integracions'
    | 'metriques'
    | 'monitorSistema'
    | 'activemq'
    | 'notificacionsCallbackError'
    | 'propietats'
    | 'caches'
    | 'avisos'
    | 'notificacions'
    | 'enviaments'
    | 'notificacionsErrorRegistre'
    | 'notificacioMassiva'
    | 'notificacionsEsborrades'
    | 'callbacks'
    | 'accionsMassives'
    | 'permisosUsuari'
    | 'entitatActual'
    | 'organs'
    | 'procediments'
    | 'serveis'
    | 'grups'
    | 'pagadorspostal'
    | 'pagadorscie'
;
/**
 * Rols autoritzats per pantalla. És l'única font de veritat del control d'accés de la interfície:
 * la fan servir tant el filtre del menú lateral (App.tsx) com les guardes de ruta (router.tsx), de
 * manera que amagar una entrada de menú i barrar-ne la ruta no poden divergir. El tipus Pantalla
 * obliga a declarar-hi tota pantalla nova.
 *
 * El mapatge reprodueix el de la interfície JSP, que aplica interceptors per prefix d'URL (veure
 * WebMvcConfig): SUPER_PATHS -- entre els quals "/entitat**" -- només per al rol actual DIS_SUPER
 * (AccesSuperInterceptor), ADMIN_PATHS per a DIS_ADMIN i DIS_ADMIN_LECTURA (AccesAdminInterceptor)
 * i USER_PATHS per a "tothom".
 *
 * Compte: això és usabilitat, no seguretat. Qui protegeix de veritat és el backend amb els
 * @ResourceAccessConstraint de cada classe *Resource (a EntitatResource, per exemple, l'escriptura
 * és només de DIS_SUPER i la lectura de tots els rols, perquè el selector d'entitat la necessita).
 * Tota pantalla nova ha de declarar les seves restriccions al recurs encara que aquí ja s'amagui.
 */
export const PANTALLA_ROLS: Record<Pantalla, string[]> = {
    home: ROLS_APLICACIO,
    entitats: [ROLE_SUPER],
    integracions: [ROLE_SUPER],
    metriques: [ROLE_SUPER],
    monitorSistema: [ROLE_SUPER],
    activemq: [ROLE_SUPER],
    notificacionsCallbackError: [ROLE_SUPER],
    propietats: [ROLE_SUPER],
    caches: [ROLE_SUPER],
    avisos: [ROLE_SUPER],
    notificacions: [ROLE_ADMIN, ROLE_ADMIN_LECTURA, ROLE_ORGAN, ROLE_USER],
    enviaments: [ROLE_ADMIN, ROLE_ADMIN_LECTURA, ROLE_ORGAN, ROLE_USER],
    notificacionsErrorRegistre: [ROLE_ADMIN, ROLE_ADMIN_LECTURA],
    notificacioMassiva: [ROLE_ADMIN, ROLE_ADMIN_LECTURA, ROLE_ORGAN, ROLE_USER],
    notificacionsEsborrades: [ROLE_ADMIN, ROLE_ADMIN_LECTURA],
    callbacks: [ROLE_ADMIN, ROLE_ADMIN_LECTURA],
    accionsMassives: [ROLE_ADMIN, ROLE_ADMIN_LECTURA],
    permisosUsuari: [ROLE_ADMIN, ROLE_ADMIN_LECTURA, ROLE_ORGAN],
    entitatActual: [ROLE_ADMIN, ROLE_ADMIN_LECTURA],
    organs: [ROLE_ADMIN, ROLE_ADMIN_LECTURA, ROLE_ORGAN],
    procediments: [ROLE_ADMIN, ROLE_ADMIN_LECTURA, ROLE_ORGAN],
    serveis: [ROLE_ADMIN, ROLE_ADMIN_LECTURA, ROLE_ORGAN],
    grups: [ROLE_ADMIN, ROLE_ADMIN_LECTURA, ROLE_ORGAN],
    pagadorspostal: [ROLE_ADMIN, ROLE_ADMIN_LECTURA, ROLE_ORGAN],
    pagadorscie: [ROLE_ADMIN, ROLE_ADMIN_LECTURA, ROLE_ORGAN],

};

export const isPantallaPermesa = (pantalla: Pantalla, rol?: string): boolean => rol != null && PANTALLA_ROLS[pantalla].includes(rol)

/**
 * Pantalla d'inici per rol -- l'equivalent del HomeRedirect de RIPEA. Reprodueix la redirecció de
 * l'arrel de la interfície JSP (DistribucioController.get): DIS_SUPER va a "integracio",
 * DIS_ADMIN i DIS_ADMIN_LECTURA a "registreAdmin" i "tothom" a "registreUser", que a REACT són
 * la mateixa pantalla "registre". La ruta que s'hi indiqui ha d'estar autoritzada per al rol a
 * PANTALLA_ROLS; un rol sense entrada va a RUTA_INICIAL_DEFECTE.
 */
const RUTA_INICIAL_PER_ROL: Partial<Record<string, string>> = {
    [ROLE_SUPER]: '/integracio',
    [ROLE_ADMIN]: '/notificacions',
    [ROLE_ADMIN_LECTURA]: '/notificacions',
    [ROLE_ORGAN]: '/notificacions',
    [ROLE_USER]: '/notificacions',
};

export const RUTA_INICIAL_DEFECTE = '/home';

export const rutaInicialPerRol = (rol?: string): string =>
    (rol != null ? RUTA_INICIAL_PER_ROL[rol] : undefined) ?? RUTA_INICIAL_DEFECTE;

/**
 * Entrada de menú amb la pantalla a la qual dona accés. Les entrades sense pantalla (separadors,
 * agrupadors o enllaços externs) es mostren a tots els rols.
 */
export type MenuEntryAmbPantalla = MenuEntry & {
    pantalla?: Pantalla;
    children?: MenuEntryAmbPantalla[];
};

/**
 * Deixa al menú només les entrades que el rol pot obrir. Els submenús es filtren en profunditat i
 * els que es queden sense fills visibles i no són cap enllaç desapareixen.
 */
export const filtrarEntradesMenu = (
    entrades: MenuEntryAmbPantalla[],
    rol?: string
): MenuEntry[] =>
    entrades.reduce<MenuEntry[]>((visibles, entrada) => {
        const { pantalla, children, ...entradaMenu } = entrada;
        if (pantalla != null && !isPantallaPermesa(pantalla, rol)) {
            return visibles;
        }
        if (children != null) {
            const fills = filtrarEntradesMenu(children, rol);
            if (fills.length === 0 && entradaMenu.to == null) {
                return visibles;
            }
            visibles.push({ ...entradaMenu, children: fills });
        } else {
            visibles.push(entradaMenu);
        }
        return visibles;
    }, []);
