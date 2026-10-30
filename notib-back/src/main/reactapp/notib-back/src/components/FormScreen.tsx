import React from 'react';
import Box from '@mui/material/Box';
import { useScreenFrame } from './ScreenFrameContext';
import { SCREEN_TITLE_MIN_HEIGHT, SCREEN_TITLE_PADDING_X } from '../utils/layout';
import { titleBarControlsSx } from './titleBarControlsSx';

/**
 * Envolta el MuiForm d'una pantalla i en superposa la barra d'eines a la barra de títol del
 * ScreenFrame, com fa la graella (veure StyledMuiGrid.tsx): així la pantalla té una sola capçalera.
 *
 * No toca la llibreria: només reposiciona el .MuiToolbar-root que el MuiForm ja dibuixa i n'avisa
 * el ScreenFrame de l'amplada, perquè el títol no hi passi per sota.
 */
export const FormScreen: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const frame = useScreenFrame();
    const setToolbarWidth = frame?.setToolbarWidth;
    const wrapperRef = React.useRef<HTMLDivElement>(null);

    React.useEffect(() => {
        const wrapper = wrapperRef.current;
        if (!setToolbarWidth || !wrapper) {
            return;
        }
        const GAP = 12;                       // separació entre el títol i la primera eina
        let toolbar: HTMLElement | null = null;
        let sizeObserver: ResizeObserver | undefined;
        const attach = () => {
            // La barra del MuiForm és un .MuiToolbar-root directament dins de la caixa exterior
            // del MuiForm (a la graella hi ha un Box intermedi i s'usa LIB_TOOLBAR).
            const found = wrapper.querySelector<HTMLElement>(':scope > div > div > .MuiToolbar-root');            if (found === toolbar) {
                return;
            }
            sizeObserver?.disconnect();
            toolbar = found;
            if (toolbar) {
                const observed = toolbar;
                sizeObserver = new ResizeObserver(() =>
                    setToolbarWidth(Math.ceil(observed.offsetWidth) + GAP)
                );
                sizeObserver.observe(observed);
            } else {
                setToolbarWidth(0);
            }
        };
        // La barra no existeix fins que el formulari és a punt: la llibreria només renderitza
        // els fills quan isReady, així que es busca a cada canvi del DOM.
        const domObserver = new MutationObserver(attach);
        domObserver.observe(wrapper, { childList: true, subtree: true });
        attach();
        return () => {
            domObserver.disconnect();
            sizeObserver?.disconnect();
            setToolbarWidth(0);
        };
    }, [setToolbarWidth]);

    return (
        <Box
            ref={wrapperRef}
            sx={{
                // Perquè el `height: 100%` del MuiForm i de les pestanyes amb graella tinguin una
                // referència (si no, la graella col·lapsa a la seva alçada mínima).
                height: '100%',
                // Mateix tractament que la barra de la graella (StyledMuiGrid.tsx).
                '& > div > div > .MuiToolbar-root': {
                    position: 'absolute',
                    // `--screen-toolbar-top` el posa el ScreenFrame: 0 si hi cab al costat del títol,
                    // o l'alçada d'una fila si ha de passar a sota.
                    top: 'var(--screen-toolbar-top, 0px)',
                    right: SCREEN_TITLE_PADDING_X + 'px',
                    height: SCREEN_TITLE_MIN_HEIGHT + 'px',
                    width: 'auto',              // la llibreria posa width:'100%': taparia tota la barra
                    minHeight: 0,
                    mt: 0,
                    display: 'flex',
                    alignItems: 'center',
                    backgroundColor: 'transparent', // la llibreria la pinta de gris (upperToolbar)
                    zIndex: 1,
                    ...titleBarControlsSx,      // mateix estil que els controls de la barra de títol
                },
                // La llibreria deixa un marge (16px als costats, 24px a dalt) a la caixa del contingut
                // pensat per a un formulari a pàgina sencera; aquí el ScreenFrame ja hi posa el seu
                // padding (MAIN_PADDING) i els dos marges se sumaven.
                '& > div > div > div:last-child': { m: 0 },
                // Separació entre les pestanyes i el seu contingut: la llibreria hi deixa 24px.
                '& [role="tabpanel"] > .MuiBox-root': { pt: 2 },
                // Les graelles de les pestanyes (p. ex. permisos) pinten una barra d'eines de 64px
                // (alçada per defecte de MUI) només per als botons de crear i refrescar.
                '& [role="tabpanel"] .MuiToolbar-root': { minHeight: '48px' },
            }}>
            {children}
        </Box>
    );
};

export default FormScreen;
