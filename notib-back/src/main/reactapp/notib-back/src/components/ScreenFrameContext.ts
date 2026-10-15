import React from 'react';

/**
 * Classe de l'element arrel d'un ScreenTitle. ScreenFrame la fa servir per reconèixer la barra d'eines
 * de la llibreria que conté el títol de la pantalla i pintar-la com a barra de títol.
 */
export const SCREEN_TITLE_CLASS = 'screen-title';

/**
 * Què ofereix un `ScreenFrame` als components de dins (veure ScreenFrame.tsx). Va en un fitxer a part
 * perquè el fitxer del component només exporti components (react-refresh).
 */
export type ScreenFrameContextValue = {
    /**
     * Node de la barra de títol, al costat del títol, on pintar-hi informació breu de la pantalla
     * (p. ex. el nombre de filtres aplicats) amb `createPortal`. És `null` fins que es munta.
     */
    infoSlot: HTMLElement | null;
    /** El registra el títol de la pantalla (ScreenTitle) quan es munta. */
    setInfoSlot: (element: HTMLElement | null) => void;
};

export const ScreenFrameContext = React.createContext<ScreenFrameContextValue | undefined>(undefined);

/** Marc de pantalla en què es troba el component, o `undefined` si no n'hi ha cap. */
export const useScreenFrame = () => React.useContext(ScreenFrameContext);
