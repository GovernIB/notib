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
    config: 'display_settings',
    servei: 'miscellaneous_services',
    grup: 'group',
    pagadorPostal: 'markunread_mailbox',
    pagadorCie: 'mark_as_unread',
    cache: 'sd_storage',
    integracio: 'sync_alt',
    metrica: 'bar_chart',
    monitorSistema: 'monitor_heart',
    activeMq: 'subscriptions',
    avis: 'notifications',
    mapaWeb: 'map',
    accessibilitat: 'accessibility_new',
    noTrobat: 'search_off',

    // --- Accions -----------------------------------------------------------------------------
    crea: 'add',
    modifica: 'edit',
    esborra: 'delete',
    esborraTot: 'delete_sweep',
    desa: 'save',
    desfes: 'undo',
    cancela: 'close',
    accepta: 'check',
    activa: 'check',
    desactiva: 'close',
    executa: 'bolt',
    refresca: 'refresh',
    sincronitza: 'cached',
    copia: 'content_copy',
    descarrega: 'file_download',
    carrega: 'file_upload',
    detall: 'info',
    informe: 'description',
    llista: 'list',
    diagnostic: 'monitor_heart',
    envia: 'send',
    imprimeix: 'print',
    obreNovaFinestra: 'open_in_new',
    mou: 'turn_right',
    avanca: 'fast_forward',
    reprenExecucio: 'play_circle',
    pausaExecucio: 'pause',
    desaDefinitiu: 'edit_note',
    afegeixUsuari: 'person_add',
    bloqueja: 'lock',
    desbloqueja: 'lock_open_right',
    mostra: 'visibility',
    amunt: 'arrow_upward',
    avall: 'arrow_downward',
    anterior: 'keyboard_double_arrow_left',
    seguent: 'keyboard_double_arrow_right',
    plega: 'expand_less',
    desplega: 'expand_more',
    mesAccions: 'more_vert',
    buida: 'clear',
    interficieClassica: 'fast_rewind',

    // --- Rols (insígnia del botó d'usuari de la capçalera) ----------------------------------
    rolSuper: 'shield',
    rolAdmin: 'admin_panel_settings',
    rolAdminLectura: 'visibility',
    rolOrgan: 'account_tree',
    rolAplicacio: 'apps',
    rolUsuari: 'person',

    // --- Agrupacions del menú ----------------------------------------------------------------
    grupEnviamentMassiu: 'dashboard',
    gestio: 'dashboard',
    monitoritzacio: 'monitor',
    configuracio: 'settings',

    // --- Filtre i cerca ----------------------------------------------------------------------
    cerca: 'search',
    filtre: 'filter_alt',
    neteja: 'filter_alt_off',
    cercaAvancadaObre: 'expand_more',
    cercaAvancadaTanca: 'expand_less',

    // --- Estats i avisos ---------------------------------------------------------------------
    senseDades: 'block',


} as const;

export type IconName = (typeof icons)[keyof typeof icons];
