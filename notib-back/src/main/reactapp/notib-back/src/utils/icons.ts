/**
 * Catàleg d'icones (Material Icons) per **concepte**. L'entrada de menú, el títol de la pantalla i els
 * botons que representen el mateix concepte han de fer servir sempre la mateixa icona (veure
 * routeAccess.ts i components/ScreenFrame.tsx).
 *
 * Si una pantalla o acció nova necessita una icona, s'hi afegeix aquí per concepte; no es posa el nom
 * de la icona en línia al component.
 */
export const icons = {
    // --- Pantalles (entrades de menú i títol de la pantalla) ---------------------------------
    inici: 'home',
    notificacio: 'mail',
    enviament: 'send',
    enviamentMassiu: 'mark_email_read',
    nouEnviamentMassiu: 'forward_to_inbox',
    notificacioErrorRegistre: 'error',
    notificacioEsborrada: 'delete_outline',
    notificacioErrorCallback: 'running_with_errors',
    callbackPendent: 'pending_actions',
    accioMassiva: 'format_list_bulleted',
    permisUsuari: 'group',
    entitat: 'layers',
    entitatActual: 'my_location',
    propietat: 'settings',
    organ: 'account_tree',
    procediment: 'view_timeline',
    servei: 'miscellaneous_services',
    grup: 'group',
    pagadorPostal: 'markunread_mailbox',
    pagadorCie: 'mark_as_unread',
    cache: 'sd_storage',
    integracio: 'build',
    metrica: 'bar_chart',
    monitorSistema: 'monitor_heart',
    activeMq: 'subscriptions',
    avis: 'notifications',
    mapaWeb: 'map',
    accessibilitat: 'accessibility_new',
    noTrobat: 'search_off',

    // --- Agrupacions del menú ----------------------------------------------------------------
    grupEnviamentMassiu: 'dashboard',
    gestio: 'dashboard',
    monitoritzacio: 'monitor',
    configuracio: 'settings',

    // --- Filtre i cerca ----------------------------------------------------------------------
    filtre: 'filter_alt',
    neteja: 'auto_fix_normal',
    cercaAvancadaObre: 'expand_more',
    cercaAvancadaTanca: 'expand_less',
} as const;

export type IconName = (typeof icons)[keyof typeof icons];
