import { createTheme, alpha, darken, type Theme } from '@mui/material/styles';
import type {} from '@mui/x-tree-view/themeAugmentation';
import type {} from '@mui/x-data-grid-pro/themeAugmentation';
// `palette.DataGrid` (headerBg) només és a l'augmentació del paquet base de MUI X
import type {} from '@mui/x-data-grid/themeAugmentation';
import {
    HEADER_HEIGHT,
    ICON_SIZE,
    MENU_BORDER_WIDTH,
    MENU_COLLAPSED_BELOW,
    MENU_ICON_COLUMN,
    MENU_ITEM_PADDING_LEFT,
    MENU_WIDTH_COLLAPSED,
} from './utils/layout';

// Estenem les interfícies de MUI per admetre el nou color
declare module '@mui/material/styles' {
  interface Palette {
    customBackground: string;
    greyBackground: string;
    /** Atenuat del color base de l'aplicació: fons de la capçalera de les graelles. */
    primarySoft: string;
  }
  interface PaletteOptions {
    customBackground?: string;
    greyBackground?: string;
    primarySoft?: string;
  }
}

// Identitat visual de NOTIB (colors de Drassana). Per portar el disseny a una altra aplicació només
// cal canviar aquests quatre valors:
//  - MAIN: color base (el verd del logo). Barra de títol de les pantalles, vora de l'entrada
//    seleccionada del menú, capçalera de les graelles i botons d'acció.
//  - DARK: "torrat", és el `primary.dark` de MUI: hover dels botons i, als temes clars, badges,
//    paginació, barres de desplaçament i icona seleccionada del menú (veure accentColor). És el
//    primari de la interfície clàssica de NOTIB.
//  - LIGHT: to clar de primer pla, és el `primary.light` de MUI als temes foscos, on el torrat no té
//    contrast i fa de color "torrat". És `lighten(MAIN, 0.3)`.
//  - SOFT: atenuat, és `palette.primarySoft`. Fons de la capçalera de les graelles al tema clar.
//    Opac (no `alpha`): la capçalera de la graella és enganxosa i les files hi passen per sota.
const NOTIB_PRIMARY_MAIN = '#83cb41';
const NOTIB_PRIMARY_DARK = '#497e3a';
const NOTIB_PRIMARY_LIGHT = '#a8db7a';
const NOTIB_PRIMARY_SOFT = '#F2F9EB';

/**
 * Color "torrat" de la identitat: barres de desplaçament, icona de la barra de títol, icona
 * seleccionada del menú... És `primary.dark` als temes clars; als foscos, un torrat sobre fons fosc no
 * té contrast i es fa servir `primary.light`. Tot el que ha de sortir "torrat" passa per aquí.
 */
export const accentColor = (theme: Theme): string =>
    theme.palette.mode === 'dark' ? theme.palette.primary.light : theme.palette.primary.dark;

/**
 * Color de farciment (fons) dels elements primaris petits amb text a sobre: pàgina seleccionada de la
 * paginació, badges i xips. Als temes clars és el mateix «torrat» (amb text blanc); als foscos el
 * color base de l'aplicació, amb el text que li correspongui per contrast.
 */
export const accentFillColor = (theme: Theme): string =>
    theme.palette.mode === 'dark' ? theme.palette.primary.main : theme.palette.primary.dark;

// Files de les graelles als temes foscos (Obscur i Dracula comparteixen el verd base): el hover és un
// verd suau; la fila seleccionada, un verd fosc, i amb el cursor a sobre una mica més viu.
const GRID_ROW_HOVER_DARK = alpha(NOTIB_PRIMARY_MAIN, 0.2);
const GRID_ROW_SELECTED_DARK = alpha(darken(NOTIB_PRIMARY_MAIN, 0.55), 0.8);
const GRID_ROW_SELECTED_HOVER_DARK = alpha(darken(NOTIB_PRIMARY_MAIN, 0.4), 0.9);

// Patró de línies en diagonal per al fons
const hatchPattern = (lineColor: string) =>
    `repeating-linear-gradient(45deg, transparent 0, transparent 0.75px, ${lineColor} 1.25px, transparent 1.75px, transparent 3px)`;

// Vora inferior de la capçalera de les graelles: degradat del color base al torrat. El fons de la
// capçalera no es fixa aquí sinó a `palette.DataGrid.headerBg` de cada tema (és la via de MUI X, que
// també acoloreix el filler i el buit de la barra de desplaçament).
//
// Es declara a part, i no dins `baseComponentStyles`, perquè cada tema redefineix
// `MuiDataGrid.styleOverrides` sencer i l'ha de referenciar explícitament.
const gridColumnHeadersOverride = ({ theme }: { theme: Theme }) => ({
    position: 'relative' as const,
    '&::after': {
        content: '""',
        position: 'absolute' as const,
        left: 0,
        right: 0,
        bottom: 0,
        height: '3px',
        background: `linear-gradient(90deg, ${theme.palette.primary.main} 25%, ${accentColor(theme)} 75%)`,
        pointerEvents: 'none' as const,
    },
});

// Barres de desplaçament de tota l'aplicació (i de les graelles, que hereten la propietat) al color
// "torrat" del tema. `scrollbar-color` és herència CSS, per això n'hi ha prou amb posar-lo a `html`.
const scrollbarOverrides = (thumbColor: string) => ({
    html: { scrollbarColor: `${thumbColor} transparent` },
});

// Compartit per tots els temes: els overrides reben el `theme` actiu per paràmetre, per tant no cal
// repetir-los a cada tema.
const baseComponentStyles = {
    MuiTreeItem: {
        styleOverrides: {
            content: ({ theme }: { theme: Theme }) => ({
                '&:hover': {
                    backgroundColor: alpha(theme.palette.primary.main, 0.10),
                },
                '&.Mui-selected': {
                    backgroundColor: alpha(theme.palette.primary.main, 0.15),
                    '&:hover': {
                        backgroundColor: alpha(theme.palette.primary.main, 0.20),
                    },
                    '&.Mui-focused': {
                        backgroundColor: alpha(theme.palette.primary.main, 0.25),
                    },
                },
            }),
        },
    },
    // Menú lateral. La llibreria en té la geometria cablejada (espaiador superior de 64px, plegat a
    // 57px, icones de 20px, panell flotant dels submenús a 64px/57px) i no es pot tocar; s'ajusta aquí,
    // al tema, i no des de BaseApp.tsx, perquè en pantalles estretes la llibreria pinta el menú en un
    // portal, fora del <nav> i de qualsevol embolcall de l'aplicació: només els overrides de tema hi
    // arriben. Les mides surten de utils/layout.ts. El color de les entrades segueix a BaseApp.tsx
    // (depèn del perfil de l'usuari).
    MuiDrawer: {
        styleOverrides: {
            // Menú permanent: la llibreria el plega a 57px i en reserva aquest espai al layout
            docked: { minWidth: `${MENU_WIDTH_COLLAPSED}px` },
            paper: {
                minWidth: `${MENU_WIDTH_COLLAPSED}px`,
                // Cada entrada amaga el text només quan el menú és estret, sense haver de saber-ne
                // l'estat.
                containerType: 'inline-size' as const,
                // El menú ha de començar sota la capçalera, que és més alta que els 64px de la llibreria
                '& > .MuiBox-root:first-of-type': { marginTop: `${HEADER_HEIGHT}px` },
                '& .menu-item-icon .MuiIcon-root': { fontSize: `${ICON_SIZE}px` },
                // Panell flotant amb les entrades d'un submenú quan el menú està plegat: la llibreria
                // el posa (position: fixed) a 64px de dalt i a 57px de l'esquerra; ha de començar sota
                // la capçalera i a la vora del menú plegat.
                '& > div > .MuiBox-root': {
                    top: `${HEADER_HEIGHT}px`,
                    left: `${MENU_WIDTH_COLLAPSED}px`,
                },
                // Plegat: la llibreria hi centra les icones a `espai útil / 2`, sense dependre de
                // la mida de la icona, que és la columna menys mig píxel per la vora del menú
                // (MENU_BORDER_WIDTH). La icona du un marge esquerre de -8px que aquí se li treu
                // un píxel perquè el centre caigui just a MENU_ICON_COLUMN.
                [`@container (max-width: ${MENU_COLLAPSED_BELOW}px)`]: {
                    '& .MuiListItemText-root': { display: 'none' },
                    '& > div > .MuiList-root > .MuiListItemButton-root > .menu-item-icon': {
                        marginLeft: `${-8 + MENU_BORDER_WIDTH}px`,
                    },
                },
                // Obert: la llibreria deixa la icona a `padding + marge` (24 + 8 = 32px de vora
                // esquerra), i amb la mida nova el centre cauria a 44px. Es retoca el marge de les
                // entrades de nivell 0 perquè el centre sigui a MENU_ICON_COLUMN, com en plegat.
                [`@container (min-width: ${MENU_COLLAPSED_BELOW + 1}px)`]: {
                    '& > .MuiList-root > .MuiListItemButton-root > .menu-item-icon': {
                        marginLeft: `${MENU_ICON_COLUMN - ICON_SIZE / 2 - MENU_ITEM_PADDING_LEFT}px`,
                    },
                },
            },
        },
    },
    // Identitat: paginació, badges, xips primaris i icona seleccionada del menú en el color "torrat"
    // (veure accentColor). El botó, l'enllaç i la resta de components primaris es queden al base.
    MuiPaginationItem: {
        styleOverrides: {
            root: ({ theme }: { theme: Theme }) => ({
                '&.Mui-selected': {
                    backgroundColor: accentFillColor(theme),
                    color: theme.palette.getContrastText(accentFillColor(theme)),
                    '&:hover': { backgroundColor: accentFillColor(theme), filter: 'brightness(1.15)' },
                },
            }),
        },
    },
    MuiBadge: {
        styleOverrides: {
            badge: ({ theme }: { theme: Theme }) => ({
                '&.MuiBadge-colorPrimary': {
                    backgroundColor: accentFillColor(theme),
                    color: theme.palette.getContrastText(accentFillColor(theme)),
                },
            }),
        },
    },
    MuiChip: {
        styleOverrides: {
            root: ({ theme }: { theme: Theme }) => ({
                '&.MuiChip-colorPrimary:not(.MuiChip-outlined)': {
                    backgroundColor: accentFillColor(theme),
                    color: theme.palette.getContrastText(accentFillColor(theme)),
                },
            }),
        },
    },
    // Pestanya seleccionada als temes foscos en el to clar (accentColor). Al clar es manté el del tema.
    MuiTab: {
        styleOverrides: {
            root: ({ theme }: { theme: Theme }) => ({
                ...(theme.palette.mode === 'dark' && { '&.Mui-selected': { color: accentColor(theme) } }),
            }),
        },
    },
    MuiListItemButton: {
        styleOverrides: {
            root: ({ theme }: { theme: Theme }) => ({
                // `menu-item-icon` és la classe que la llibreria posa al ListItemIcon del menú
                '&.Mui-selected .menu-item-icon': { color: accentColor(theme) },
                // Vora esquerra de l'entrada seleccionada, del color de la barra de títol de les
                // pantalles (als estils de menú amb colors propis la pren de l'accent, veure BaseApp.tsx).
                // Un grup desplegat (seguit del bloc amb les seves entrades) no la porta: ja la porta
                // l'entrada seleccionada de dins.
                '&.Mui-selected': { boxShadow: `inset 4px 0 0 ${theme.palette.primary.main}` },
                '&.Mui-selected:has(+ .MuiBox-root)': { boxShadow: 'none' },
                // Línia vertical de les entrades dels submenús: la llibreria pinta la de l'entrada
                // seleccionada d'un blau fix.
                '&.Mui-selected > ::before': { background: theme.palette.primary.main },
            }),
        },
    },
};

const LIGHT_CUSTOM_BACKGROUND = '#ffffff';

export const lightTheme = createTheme({
    palette: {
        mode: 'light',
        primary: { main: NOTIB_PRIMARY_MAIN, dark: NOTIB_PRIMARY_DARK },
        customBackground: LIGHT_CUSTOM_BACKGROUND,
        greyBackground: NOTIB_PRIMARY_MAIN,
        primarySoft: NOTIB_PRIMARY_SOFT,
        DataGrid: { headerBg: NOTIB_PRIMARY_SOFT },
    },
    components: {
        ...baseComponentStyles,
        MuiCssBaseline: {
            styleOverrides: {
                ...scrollbarOverrides(NOTIB_PRIMARY_DARK),
                body: {
                    backgroundColor: LIGHT_CUSTOM_BACKGROUND,
                    backgroundImage: hatchPattern('#e1e1e1'),
                },
            },
        },
        MuiDataGrid: {
            styleOverrides: {
                columnHeaders: gridColumnHeadersOverride,
            },
        },
    },
});

const DARK_CUSTOM_BACKGROUND = '#121212';
// Opac a propòsit: la capçalera de la graella és enganxosa i les files hi passen per sota.
const DARK_PRIMARY_SOFT = darken(NOTIB_PRIMARY_MAIN, 0.7);

export const darkTheme = createTheme({
    palette: {
        mode: 'dark',
        primary: { light: NOTIB_PRIMARY_LIGHT, main: NOTIB_PRIMARY_MAIN, dark: NOTIB_PRIMARY_DARK },
        customBackground: DARK_CUSTOM_BACKGROUND,
        greyBackground: NOTIB_PRIMARY_MAIN,
        primarySoft: DARK_PRIMARY_SOFT,
        DataGrid: { headerBg: DARK_PRIMARY_SOFT },
    },
    components: {
        ...baseComponentStyles,
        MuiCssBaseline: {
            styleOverrides: {
                ...scrollbarOverrides(NOTIB_PRIMARY_LIGHT),
                body: {
                    backgroundColor: DARK_CUSTOM_BACKGROUND,
                    backgroundImage: hatchPattern('#2c2c2c'),
                },
            },
        },
        MuiDataGrid: {
            styleOverrides: {
                root: {
                    '& .MuiDataGrid-row:not(.Mui-selected):hover': {
                        backgroundColor: `${GRID_ROW_HOVER_DARK} !important`,
                    },
                },
                row: {
                    '&.Mui-selected': { backgroundColor: `${GRID_ROW_SELECTED_DARK} !important` },
                    '&.Mui-selected:hover': { backgroundColor: `${GRID_ROW_SELECTED_HOVER_DARK} !important` },
                },
                columnHeaders: gridColumnHeadersOverride,
            },
        },
    },
});

const DRACULA_BACKGROUND = '#282A36';
// Opac a propòsit: la capçalera de la graella és enganxosa i les files hi passen per sota.
const DRACULA_PRIMARY_SOFT = darken(NOTIB_PRIMARY_MAIN, 0.6);

// Paleta oficial del tema Dràcula (https://draculatheme.com), amb el color de fons
// secundari ("paper") i el gris ("greyBackground") ajustats per separar-se de la resta
// de superfícies i mantenir el contrast WCAG amb el text. El color primari és el de la
// identitat de NOTIB, com als altres temes.
export const draculaTheme = createTheme({
    palette: {
        mode: 'dark',
        primary: { light: NOTIB_PRIMARY_LIGHT, main: NOTIB_PRIMARY_MAIN, dark: NOTIB_PRIMARY_DARK },
        secondary: { main: '#F8F8F2' },
        background: { default: DRACULA_BACKGROUND, paper: '#303341' },
        text: { primary: '#F8F8F2', secondary: '#D6D6C2' },
        divider: '#7a7d8b',
        error: { main: '#FF5555' },
        warning: { main: '#FFB86C' },
        success: { main: '#50FA7B' },
        info: { main: '#8BE9FD' },
        customBackground: DRACULA_BACKGROUND,
        greyBackground: '#343746',
        primarySoft: DRACULA_PRIMARY_SOFT,
        DataGrid: { headerBg: DRACULA_PRIMARY_SOFT },
    },
    components: {
        ...baseComponentStyles,
        MuiCssBaseline: {
            styleOverrides: {
                ...scrollbarOverrides(NOTIB_PRIMARY_LIGHT),
                body: {
                    backgroundColor: DRACULA_BACKGROUND,
                    backgroundImage: hatchPattern('#44475a'),
                },
            },
        },
        MuiButton: {
            styleOverrides: {
                root: {
                    variants: [
                        {
                            props: ({ color, variant }: any) =>
                                variant === 'outlined' && color === 'primary',
                            style: {
                                color: '#F8F8F2',
                                borderColor: NOTIB_PRIMARY_LIGHT,
                            },
                        },
                    ],
                },
            },
        },
        MuiDataGrid: {
            styleOverrides: {
                root: {
                    '& .MuiDataGrid-row:not(.Mui-selected):hover': {
                        backgroundColor: `${GRID_ROW_HOVER_DARK} !important`,
                    },
                },
                row: {
                    '&.Mui-selected': { backgroundColor: `${GRID_ROW_SELECTED_DARK} !important` },
                    '&.Mui-selected:hover': { backgroundColor: `${GRID_ROW_SELECTED_HOVER_DARK} !important` },
                },
                columnHeaders: gridColumnHeadersOverride,
            },
        },
    },
});
