import type { TFunction } from 'i18next';
import { ROLE_SUPER, ROLE_ADMIN, ROLE_ADMIN_LECTURA, ROLE_ORGAN, ROLE_USER, ROLE_APLICACIO } from './components/NotibContext';
import type { MenuEntryWithResource } from './components/BaseApp';

// Pàgina d'informació de l'API REST de l'api interna, servida per un altre desplegament (fora de la SPA de React).
export const APLICACIO_HOME_URL = '/notibapi/interna/swagger-ui/index.html';

// Estructura del menú de l'aplicació, parametritzada pel rol per poder-la reutilitzar tant per renderitzar
// el menú del rol actual (App.tsx) com per comprovar l'accessibilitat d'una pàgina en canviar de rol (RoleSelector).
export const getMenuEntries = (role: string | undefined, t: TFunction): MenuEntryWithResource[] => {
    const menuEnviamentMassiu: MenuEntryWithResource[] = [
        {
            id: 'nouEnviamentMassiu',
            title: t('app.menu.nouEnviamentmassiu'),
            to: '/notificacio/massiva/form',
            icon: 'add',
            resourceName: 'notificacioMassivaResource',
            hidden: role !== ROLE_USER
        },
        {
            id: 'enviamentMassiu',
            title: t('app.menu.consultaEnviamentmassiu'),
            to: '/notificacio/massiva',
            icon: 'forward_to_inbox',
            resourceName: 'notificacioMassivaResource',
            hidden: role !== ROLE_USER
        },
    ];
    const menuGestio: MenuEntryWithResource[] = [
        {
            id: 'notificacionsErrorRegistre',
            title: t('app.menu.errorRegistre'),
            to: '/notificacionsErrorRegistre',
            icon: 'error',
            resourceName: 'notificacioResource',
            hidden: role !== ROLE_ADMIN && role !== ROLE_ADMIN_LECTURA
        },
        {
            id: 'enviamentMassiu',
            title: t('app.menu.consultaEnviamentmassiu'),
            to: '/notificacio/massiva',
            icon: 'forward_to_inbox',
            resourceName: 'notificacioMassivaResource',
            hidden: role !== ROLE_ADMIN && role !== ROLE_ADMIN_LECTURA
        },
        {
            id: 'notificacionsEsborrades',
            title: t('app.menu.notificacioEsborrades'),
            to: '/notificacionsEsborrades',
            icon: 'delete_outline',
            resourceName: 'notificacioResource',
            hidden: role !== ROLE_ADMIN && role !== ROLE_ADMIN_LECTURA
        },
        {
            id: 'callbackPendent',
            title: t('app.menu.callbackPendent'),
            to: '/callbacks',
            icon: 'pending_actions',
            resourceName: 'callbackResource',
            hidden: role !== ROLE_ADMIN && role !== ROLE_ADMIN_LECTURA
        },
        {
            id: 'accionsMassives',
            title: t('app.menu.accionsMassives'),
            to: '/accions/massives',
            icon: 'format_list_bulleted',
            resourceName: 'accioMassivaResource',
            hidden: role !== ROLE_ADMIN && role !== ROLE_ADMIN_LECTURA
        },
        {
            id: 'permisosUsuari',
            title: t('app.menu.permisosUsuari'),
            to: '/permisosUsuari',
            icon: 'group',
            resourceName: 'usuariPermisResource',
            hidden: role !== ROLE_ADMIN && role !== ROLE_ADMIN_LECTURA && role !== ROLE_ORGAN
        }
    ];
    const menuConfig: MenuEntryWithResource[] = [
        {
            id: 'entitats',
            title: t('app.menu.entitats'),
            to: '/entitats',
            icon: 'layers',
            resourceName: 'entitatResource',
            hidden: role !== ROLE_SUPER
        },
        {
            id: 'propietats',
            title: t('app.menu.propietats'),
            to: '/propietats',
            icon: 'settings',
            resourceName: 'configGroupResource',
            hidden: role !== ROLE_SUPER
        },
        {
            id: 'currentEntitat',
            title: t('app.menu.currentEntitat'),
            to: '/entitats/current',
            icon: 'my_location',
            resourceName: 'entitatResource',
            hidden: role !== ROLE_ADMIN && role !== ROLE_ADMIN_LECTURA
        },
        {
            id: 'organs',
            title: t('app.menu.organsGestors'),
            to: '/organs',
            icon: 'account_tree',
            resourceName: 'organGestorResource',
            hidden: role !== ROLE_ADMIN && role !== ROLE_ADMIN_LECTURA && role !== ROLE_ORGAN
        },
        {
            id: 'procediment',
            title: t('app.menu.procediments'),
            to: '/procediments',
            icon: 'view_timeline',
            resourceName: 'procedimentResource',
            hidden: role !== ROLE_ADMIN && role !== ROLE_ADMIN_LECTURA && role !== ROLE_ORGAN
        },
        {
            id: 'servei',
            title: t('app.menu.serveis'),
            to: '/serveis',
            icon: 'miscellaneous_services',
            resourceName: 'procedimentResource',
            hidden: role !== ROLE_ADMIN && role !== ROLE_ADMIN_LECTURA && role !== ROLE_ORGAN
        },
        {
            id: 'grups',
            title: t('app.menu.grups'),
            to: '/grups',
            icon: 'group',
            resourceName: 'grupResource',
            hidden: role !== ROLE_ADMIN && role !== ROLE_ADMIN_LECTURA && role !== ROLE_ORGAN
        },
        {
            id: 'pagadorspostal',
            title: t('app.menu.pagadorsPostals'),
            to: '/pagadorspostal',
            icon: 'markunread_mailbox',
            resourceName: 'pagadorPostalResource',
            hidden: role !== ROLE_ADMIN && role !== ROLE_ADMIN_LECTURA
        },
        {
            id: 'pagadorscie',
            title: t('app.menu.pagadorsCie'),
            to: '/pagadorscie',
            icon: 'mark_as_unread',
            resourceName: 'pagadorCieResource',
            hidden: role !== ROLE_ADMIN && role !== ROLE_ADMIN_LECTURA
        },
        {
            id: 'caches',
            title: t('app.menu.cache'),
            to: '/caches',
            icon: 'sd_storage',
            resourceName: 'cacheResource',
            hidden: role !== ROLE_SUPER,
        }
    ];
    const menuMonitoritza: MenuEntryWithResource[] = [
        {
            id: 'notificacionsCallbackError',
            title: t('app.menu.notificacionsCallbacksError'),
            to: '/notificacionsCallbackError',
            icon: 'running_with_errors',
            resourceName: 'notificacioResource',
            hidden: role !== ROLE_SUPER,
        },
        {
            id: 'integracions',
            title: t('app.menu.integracions'),
            to: '/integracions',
            icon: 'build',
            resourceName: 'monitorIntegracioResource',
            hidden: role !== ROLE_SUPER,
        },
        {
            id: 'metriques',
            title: t('app.menu.metriques'),
            to: '/metriques',
            icon: 'bar_chart',
            resourceName: 'metriquesResource',
            hidden: role !== ROLE_SUPER,
        },
        {
            id: 'monitorSistema',
            title: t('app.menu.monitorSistema'),
            to: '/monitorSistema',
            icon: 'monitor_heart',
            hidden: role !== ROLE_SUPER,
        },
        {
            id: 'activemq',
            title: t('app.menu.activemq'),
            to: '/activemq',
            icon: 'subscriptions',
            resourceName: 'activeMqResource',
            hidden: role !== ROLE_SUPER,
        },

    ];
    return [
        {
            id: 'notificacions',
            title: t('app.menu.notificacions'),
            to: '/notificacions',
            icon: 'mail',
            resourceName: 'notificacioResource',
            hidden: role === ROLE_SUPER
        },
        {
            id: 'enviaments',
            title: t('app.menu.enviaments'),
            to: '/enviaments',
            icon: 'send',
            resourceName: 'notificacioEnviamentResource',
            hidden: role === ROLE_SUPER
        },
        {
            id: 'enviamentsMassius',
            title: t('app.menu.enviamentMassiu'),
            icon: 'dashboard',
            children: menuEnviamentMassiu,
            hidden: role !== ROLE_USER
        },
        {
            id: 'gestio',
            title: t('app.menu.gestio'),
            icon: 'dashboard',
            children: menuGestio,
            hidden: role !== ROLE_ADMIN && role !== ROLE_ADMIN_LECTURA
        },
        {
            id: 'monitoritza',
            title: t('app.menu.monitoritza'),
            icon: 'monitor',
            children: menuMonitoritza,
            hidden: role !== ROLE_SUPER
        },
        {
            id: 'config',
            title: t('app.menu.config'),
            icon: 'settings',
            children: menuConfig,
            hidden: role === ROLE_USER
        },
        {
            id: 'avisos',
            title: t('app.menu.avisos'),
            to: '/avisos',
            icon: 'notifications',
            resourceName: 'avisResource',
            hidden: role !== ROLE_SUPER
        },
    ];
};

// Pàgina inicial en accedir a l'aplicació (o en canviar de rol), segons el rol actual.
export const getRoleHomePath = (role: string | undefined): string => {
    if (role === ROLE_SUPER) {
        return '/integracions';
    }
    if (role === ROLE_APLICACIO) {
        return APLICACIO_HOME_URL;
    }
    return '/notificacions';
};

// La pàgina d'informació de l'API REST no forma part de la SPA de React, per tant cal navegar-hi amb una
// recàrrega completa de la pàgina en lloc d'una navegació de react-router.
export const isExternalPath = (path: string): boolean => path.startsWith('/notibapi');

const flattenMenuEntries = (entries: MenuEntryWithResource[]): MenuEntryWithResource[] =>
    entries.flatMap((entry) => (entry.children ? flattenMenuEntries(entry.children) : [entry]));

const stripLeadingSlashes = (path: string) => path.replace(/^\/+/, '');

// Comprova si la pàgina identificada per pathname és accessible per al rol indicat, segons el menú de l'aplicació.
// Una pàgina sense entrada corresponent al menú (p.ex. la pàgina d'inici) es considera sempre accessible.
export const isPathAccessibleForRole = (pathname: string, role: string | undefined, t: TFunction): boolean => {
    const normalizedPath = stripLeadingSlashes(pathname);
    const matches = flattenMenuEntries(getMenuEntries(role, t))
        .filter((entry): entry is MenuEntryWithResource & { to: string } => entry.to != null)
        .map((entry) => ({ entry, to: stripLeadingSlashes(entry.to) }))
        .filter(({ to }) => normalizedPath === to || normalizedPath.startsWith(to + '/'));
    if (matches.length === 0) {
        return true;
    }
    const bestMatch = matches.reduce((longest, current) => (current.to.length > longest.to.length ? current : longest));
    return !bestMatch.entry.hidden;
};
