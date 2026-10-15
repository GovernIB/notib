import React from 'react';
import Box from '@mui/material/Box';
import Card from '@mui/material/Card';
import type { Theme } from '@mui/material/styles';
import { MAIN_PADDING, SCREEN_TITLE_MIN_HEIGHT, SCREEN_TITLE_PADDING_X } from '../utils/layout';
import type { IconName } from '../utils/icons';
import { SCREEN_TITLE_CLASS, ScreenFrameContext } from './ScreenFrameContext';
import { titleBarControlsSx } from './titleBarControlsSx';
import ScreenTitle from './ScreenTitle';

/**
 * Aspecte de la barra de títol d'una pantalla: vora esquerra en el color base i degradat del fons del
 * paper al color base. El fa servir la barra pròpia del ScreenFrame i la barra d'eines de la llibreria
 * quan fa de barra de títol (veure `screenFrameSx`).
 */
const titleBarSx = (theme: Theme) => ({
    minHeight: SCREEN_TITLE_MIN_HEIGHT + 'px',
    boxSizing: 'border-box' as const,
    paddingLeft: SCREEN_TITLE_PADDING_X + 'px',
    paddingRight: SCREEN_TITLE_PADDING_X + 'px',
    borderLeft: `4px solid ${theme.palette.primary.main}`,
    borderBottom: `1px solid ${theme.palette.divider}`,
    background: `linear-gradient(90deg, ${theme.palette.background.paper} 25%, ${theme.palette.primary.main} 75%)`,
    ...titleBarControlsSx,
});

/**
 * Selector de la barra d'eines de la llibreria (lib/components/mui/Toolbar.tsx) que fa de barra de
 * títol: la que conté un ScreenTitle. És la de les graelles (MuiDataGrid amb `toolbarType="upper"`) i
 * la dels formularis (MuiForm).
 */
const LIB_TITLE_TOOLBAR = `.MuiToolbar-root:has(.${SCREEN_TITLE_CLASS})`;

/**
 * Ancoratges sobre el DOM de la llibreria (no es pot tocar). Si la llibreria en canvia l'estructura,
 * aquest és l'únic lloc a revisar:
 *  - La barra d'eines de la llibreria es pinta com a barra de títol. La dels formularis és enganxosa a
 *    64px (l'alçada de la capçalera de la llibreria): aquí el contenidor amb scroll és el marc, i s'hi
 *    enganxa a dalt de tot.
 *  - MuiDataGrid: la barra d'eines va dins un `div`, germà de la fila addicional (el filtre,
 *    `.toolbarAdditionalRow`) i de la graella; la llibreria hi posa 16px de marge (`m: 2`).
 *  - MuiForm: la barra d'eines és germana del contenidor dels camps, amb `m: 2, mt: 3`.
 * Els marges passen a MAIN_PADDING, com la resta de l'aplicació.
 */
const screenFrameSx = (theme: Theme) => ({
    [`& ${LIB_TITLE_TOOLBAR}`]: {
        ...titleBarSx(theme),
        top: 0,
        // Damunt de les capçaleres enganxoses de les graelles dels formularis.
        zIndex: 3,
    },
    // (`:has()` no es pot niar: aquí la barra es reconeix pel títol que conté)
    [`& div:has(> .MuiToolbar-root .${SCREEN_TITLE_CLASS}) ~ .toolbarAdditionalRow`]: {
        margin: `${MAIN_PADDING}px ${MAIN_PADDING}px 0`,
    },
    [`& div:has(> .MuiToolbar-root .${SCREEN_TITLE_CLASS}) ~ .MuiDataGrid-root`]: {
        margin: MAIN_PADDING + 'px',
    },
    [`& ${LIB_TITLE_TOOLBAR} ~ .MuiBox-root`]: {
        margin: MAIN_PADDING + 'px',
    },
});

export type ScreenFrameProps = {
    /**
     * Icona i títol de la barra de títol pròpia del marc, per a les pantalles on la llibreria no en
     * pinta cap. A les graelles i formularis de la llibreria el títol va a la seva barra d'eines
     * (`title={<ScreenTitle icon={...} title={...} />}`) i aquestes propietats no s'hi posen.
     */
    icon?: IconName;
    title?: string;
    /** Accions pròpies de la pantalla, a la dreta de la barra de títol pròpia del marc. */
    actions?: React.ReactNode;
    /** Padding del contingut (per al contingut que no és de la llibreria). */
    padded?: boolean;
    /**
     * El marc només ocupa l'alçada del seu contingut (i no tota la disponible), de manera que
     * la resta de la pantalla deixa veure el fons. Per a pantalles curtes sense graella.
     */
    fitContent?: boolean;
    children: React.ReactNode;
};

/**
 * Marc d'una pantalla del backoffice: targeta amb la barra de títol (icona + títol + informació +
 * accions) a dalt i el contingut a sota.
 *
 * Ocupa tota l'alçada disponible dins `<main>` (veure BaseApp.tsx), de manera que el títol hi queda
 * sempre visible i és el contingut qui fa scroll si no hi cap (les graelles fan scroll de les files per
 * dins).
 *
 * La barra de títol pot ser:
 *  - la barra d'eines de la llibreria (graelles i formularis), si el seu `title` és un ScreenTitle: es
 *    pinta com a barra de títol i hi conserva els seus botons, el filtre ràpid i l'indicador d'error;
 *  - la barra pròpia del marc, si es passen `icon` i `title` (pantalles sense barra de la llibreria).
 */
export const ScreenFrame: React.FC<ScreenFrameProps> = (props) => {
    const { icon, title, actions, padded, fitContent, children } = props;
    const [infoSlot, setInfoSlot] = React.useState<HTMLElement | null>(null);
    const context = React.useMemo(() => ({ infoSlot, setInfoSlot }), [infoSlot]);
    return (
        <ScreenFrameContext.Provider value={context}>
            <Card
                className="screen-frame"
                sx={(theme) => ({
                    height: fitContent ? 'auto' : '100%',
                    flex: fitContent ? '0 1 auto' : undefined,
                    minHeight: 0,
                    display: 'flex',
                    flexDirection: 'column',
                    overflow: 'hidden',
                    ...screenFrameSx(theme),
                })}
            >
                {icon != null && title != null && (
                    <Box sx={(theme) => ({ ...titleBarSx(theme), display: 'flex', alignItems: 'center', flex: '0 0 auto' })}>
                        <ScreenTitle icon={icon} title={title} />
                        {actions != null && (
                            <Box sx={{ ml: 'auto', display: 'flex', alignItems: 'center', gap: 1, flex: '0 0 auto' }}>
                                {actions}
                            </Box>
                        )}
                    </Box>
                )}
                <Box
                    sx={{
                        flex: '1 1 auto',
                        minHeight: 0,
                        display: 'flex',
                        flexDirection: 'column',
                        overflowY: 'auto',
                        ...(padded ? { p: MAIN_PADDING + 'px', gap: MAIN_PADDING + 'px' } : null),
                    }}
                >
                    {children}
                </Box>
            </Card>
        </ScreenFrameContext.Provider>
    );
};

export default ScreenFrame;
