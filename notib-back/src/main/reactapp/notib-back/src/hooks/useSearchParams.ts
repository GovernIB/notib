import React from 'react';
import { useSearchParams } from 'react-router-dom';
import { useFormInitialTabContext } from '../components/FormInitialTabProvider';

/**
 * Hook per obtenir el número de pestanya inicial d'un formulari.
 * Primer mira la pestanya demanada per obrir el formulari com a modal (LinkToTab amb popup) i,
 * si no n'hi ha, el paràmetre ?tab=N de la URL.
 * Retorna el número o undefined si no existeix o no es vàlid.
 */
export const useTabParam = () => {
    const [searchParams] = useSearchParams();
    const formInitialTabContext = useFormInitialTabContext();
    const setInitialTab = formInitialTabContext?.setInitialTab;
    // En tancar el formulari s'oblida la pestanya, perquè el següent "Modificar" obri la primera
    React.useEffect(() => () => setInitialTab?.(undefined), [setInitialTab]);

    if (formInitialTabContext?.initialTab != null) {
        return formInitialTabContext.initialTab;
    }
    const tabValue = searchParams.get('tab');
    const initialTab = tabValue ? Number(tabValue) : undefined;

    return initialTab;
};
