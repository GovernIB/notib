import {
    ROLE_ADMIN,
    ROLE_ADMIN_LECTURA,
    ROLE_APLICACIO,
    ROLE_ORGAN,
    ROLE_SUPER,
    ROLE_USER,
} from '../components/NotibContext';
import { icons } from './icons';

/** Icona de cada rol (selector de rol i insígnia del botó d'usuari de la capçalera). */
export const ROLE_ICONS: Record<string, string> = {
    [ROLE_SUPER]: icons.rolSuper,
    [ROLE_ADMIN]: icons.rolAdmin,
    [ROLE_ADMIN_LECTURA]: icons.rolAdminLectura,
    [ROLE_ORGAN]: icons.rolOrgan,
    [ROLE_APLICACIO]: icons.rolAplicacio,
    [ROLE_USER]: icons.rolUsuari,
};

/** Icona de la insígnia del botó d'usuari: l'usuari normal (tothom) no en du cap. */
export const roleBadgeIcon = (role?: string): string | undefined =>
    role != null && role !== ROLE_USER ? ROLE_ICONS[role] : undefined;
