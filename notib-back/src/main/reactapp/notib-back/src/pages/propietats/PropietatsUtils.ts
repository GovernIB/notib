import type { PropietatItem } from './PropietatsTypes';

export type TranslateFn = (key: string, options?: Record<string, unknown>) => string;

/** Prefix comú a totes les claus de propietat. */
export const PREFIX_CLAU = 'es.caib.notib';

/** Codi del tipus de les propietats que contenen una contrasenya. */
export const TIPUS_CONTRASENYA = 'PASSWORD';

/** Valors d'una propietat BOOL que es mostren com a marcats (sense distingir majúscules). */
export const VALORS_BOOLEANS_CERTS = ['true', 's', '1', 'yes', 'y', 'on'];

/** Indica si el valor d'una propietat BOOL es considera cert. */
export const esBooleaCert = (value: unknown): boolean =>
    typeof value === 'string' && VALORS_BOOLEANS_CERTS.includes(value.trim().toLowerCase());

/** Text de l'error d'una crida a l'API (o del que s'hagi llançat). */
export const missatgeError = (error: unknown): string => {
    const detall = error as { description?: string; message?: string } | null;
    return detall?.description ?? detall?.message ?? String(error);
};

/**
 * Etiqueta traduïda d'un dels valors vàlids d'un tipus de propietat. El valor que es desa a la base
 * de dades no canvia: si no hi ha traducció definida es mostra tal qual.
 */
export const configTypeValueLabel = (t: TranslateFn, typeCode: string, value: string): string =>
    t(`enum.configType.${typeCode}.${value}`, { defaultValue: value, nsSeparator: false });

const fieldPropType = (typeCode: string, typeValue?: string | null): string => {
    if (typeValue != null) {
        return 'search';
    }
    switch (typeCode) {
        case 'INT':
        case 'FLOAT':
            return 'number';
        case 'BOOL':
            return 'checkbox';
        default:
            return 'text';
    }
};

/**
 * Definició del camp de formulari d'una propietat a partir del seu tipus: `configTypeValue` conté,
 * quan és una enumeració, la llista de valors vàlids separats per coma.
 */
export const getFieldFromItem = (item: PropietatItem, t: TranslateFn) => {
    const type = fieldPropType(item.configTypeCode, item.configTypeValue);
    const options = item.configTypeValue
        ? Object.fromEntries(
              item.configTypeValue
                  .split(',')
                  .map((v: string) => [v, configTypeValueLabel(t, item.configTypeCode, v)])
          )
        : undefined;
    // Les propietats BOOL arriben com a text ('true'/'false'): la casella necessita un booleà
    const value = item.configTypeCode === 'BOOL' ? esBooleaCert(item.value) : item.value;
    return {
        label: item.description,
        name: item.key,
        type,
        value,
        options,
    };
};

/** Part de la clau que va darrere del prefix comú (inclòs el punt inicial). */
export const sufixClau = (key: string) => key.substring(PREFIX_CLAU.length);

/**
 * Comprova si `key` és la propietat per entitat corresponent a la propietat base `baseKey`. Les
 * propietats per entitat tenen la forma PREFIX + '.' + codi d'entitat + resta de la propietat base.
 */
export const esClauPerEntitatDe = (key: string, baseKey: string) => {
    if (!key.startsWith(PREFIX_CLAU + '.')) {
        return false;
    }
    const darrerePrefix = key.substring(PREFIX_CLAU.length + 1);
    const punt = darrerePrefix.indexOf('.');
    if (punt < 0) {
        return false;
    }
    return PREFIX_CLAU + darrerePrefix.substring(punt) === baseKey;
};
