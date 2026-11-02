import { alpha, darken, decomposeColor, lighten, recomposeColor, type Theme } from '@mui/material/styles';

// Color "tonal" per a xips i etiquetes d'estat: fons translúcid del color semàntic i text del mateix
// to, enfosquit (temes clars) o aclarit (temes foscos). És més suau que el farciment ple de MUI
// (`<Chip color="success">`) i compleix WCAG 2.1 AA (≥ 4,5:1) a tots els temes de theme.ts.
//
// Els coeficients estan calculats amb les paletes d'aquest projecte, no són els de RIPEA: les
// paletes fosques de NOTIB (success #1b5e20, info #01579b...) són molt fosques i el `primary` del
// tema clar (#83cb41) és molt clar. El cas pitjor és la fila de la graella amb el cursor a sobre als
// temes foscos (capa verde sobre la fila parella): els valors mínims hi són 4,59:1 (primary, Obscur)
// i 4,55:1 (secondary, Dracula). Si es canvia una paleta o un coeficient, cal tornar a calcular el
// contrast del text sobre el fons resultant (el fons amb alpha mesclat amb el de la fila).
const FONS_CLAR = 0.14;
const FONS_FOSC = 0.2;
const TEXT_CLAR = 0.5; // darken
const TEXT_FOSC = 0.75; // lighten

export type TonalColor = 'success' | 'error' | 'warning' | 'info' | 'primary' | 'secondary';

/** Les icones del xip (o una `Icon` dins l'etiqueta) agafen el color del text. */
const ICONA_HEREDA_COLOR = { '& .MuiChip-icon, & .MuiIcon-root, & .MuiSvgIcon-root': { color: 'inherit' } };

/**
 * Estils tonals per a un xip o etiqueta d'estat. Es fa servir com a `sx={tonal('success')}`; si el
 * component no accepta una funció com a `sx` (el combina amb un spread), passar-li l'objecte ja
 * resolt: `tonal('success')(theme)`.
 */
export const tonal = (color: TonalColor) => (theme: Theme) => {
    const main = theme.palette[color].main;
    const fosc = theme.palette.mode === 'dark';
    return {
        bgcolor: alpha(main, fosc ? FONS_FOSC : FONS_CLAR),
        color: fosc ? lighten(main, TEXT_FOSC) : darken(main, TEXT_CLAR),
        ...ICONA_HEREDA_COLOR,
    };
};

/**
 * Estils tonals neutres, per a estats inactius o sense color. Als temes foscos el text és
 * `text.primary`: amb `text.secondary` la fila amb el cursor a sobre del tema Obscur queda a 3,85:1.
 */
export const tonalNeutre = (theme: Theme) => ({
    bgcolor: alpha(theme.palette.text.primary, 0.08),
    color: theme.palette.mode === 'dark' ? theme.palette.text.primary : theme.palette.text.secondary,
    ...ICONA_HEREDA_COLOR,
});

/** Mescla un color amb alpha sobre un fons opac i retorna el color opac resultant. */
const mesclaOpaca = (color: string, fons: string): string => {
    const c = decomposeColor(color);
    const f = decomposeColor(fons);
    const a = c.values[3] ?? 1;
    const valors = [0, 1, 2].map((i) => Math.round(c.values[i] * a + f.values[i] * (1 - a)));
    return recomposeColor({ type: 'rgb', values: valors as [number, number, number] });
};

/**
 * Com `tonal`, però amb el fons opac (el to translúcid ja mesclat amb `background.paper`). Per als
 * elements que es superposen a un altre contingut, com el comptador (`Badge`) d'una pestanya, que
 * tapa part de l'etiqueta: amb el fons translúcid les lletres de sota es veurien a través del número.
 * El contrast és el de `tonal` sobre el paper.
 */
export const tonalOpac = (color: TonalColor) => (theme: Theme) => {
    const estils = tonal(color)(theme);
    return { ...estils, bgcolor: mesclaOpaca(estils.bgcolor, theme.palette.background.paper) };
};

export const isTonalColor =(c: unknown): c is TonalColor =>
    typeof c === 'string' && ['success', 'error', 'warning', 'info', 'primary', 'secondary'].includes(c);

/**
 * Estils tonals per a una `LinearProgress`. La barra translúcida no arriba a 3:1 amb la pista
 * (WCAG 1.4.11) i el nivell no es pot transmetre només amb el color: s'ha d'escriure el valor a
 * sobre. Fer servir `TonalLinearProgress`, que ja ho fa.
 */
export const tonalProgress = (color: TonalColor) => (theme: Theme) => {
    const main = theme.palette[color].main;
    const fosc = theme.palette.mode === 'dark';
    return {
        bgcolor: alpha(main, fosc ? 0.12 : 0.1),
        '& .MuiLinearProgress-bar': { bgcolor: alpha(main, fosc ? 0.3 : 0.4) },
    };
};
