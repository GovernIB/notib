import React from 'react';
import { useBaseAppContext, useIsNestedInDialog } from './BaseAppContext';

/**
 * Propietats del component GridPage.
 */
type GridPageProps = React.PropsWithChildren & {
    /** Indica que s'han de desactivar els marges */
    disableMargins?: boolean;
    /** Indica que la taula de la pàgina tendrà autoHeight activat */
    autoHeight?: boolean;
    /** Estils addicionals per l'element contenidor */
    style?: React.CSSProperties;
};

/**
 * Pàgina que conté un element de graella de dades.
 *
 * @param props - Propietats del component.
 * @returns Element JSX de la pàgina.
 */
export const GridPage: React.FC<GridPageProps> = (props) => {
    const { disableMargins = true, autoHeight, style, children } = props;
    // Quan GridPage es troba dins un Dialog (p. ex. una pestanya d'un detall en una finestra
    // emergent), l'estat global de disposició de la pàgina (contentExpandsToAvailableHeight,
    // marginsDisabled) no li és rellevant: el Dialog ja imposa la seva pròpia alçada. Alterar
    // aquest estat global des d'aquí faria que, en muntar/desmuntar-se en canviar de pestanya,
    // es sobreescrigués l'estat que necessita la pàgina de fons (p. ex. un llistat visible
    // darrere la finestra emergent), deixant-la sense alçada i, per tant, sense contingut visible
    // fins que es refresqui la pàgina. Per això, quan està niat dins un Dialog, s'ignora
    // completament aquest mecanisme.
    const isNestedInDialog = useIsNestedInDialog();
    const {
        setMarginsDisabled,
        contentExpandsToAvailableHeight,
        setContentExpandsToAvailableHeight,
    } = useBaseAppContext();
    const [proceed, setProceed] = React.useState<boolean>(
        isNestedInDialog || contentExpandsToAvailableHeight
    );
    React.useEffect(() => {
        if (isNestedInDialog) {
            return;
        }
        if (!proceed && contentExpandsToAvailableHeight === !autoHeight) {
            setProceed(true);
        }
    }, [contentExpandsToAvailableHeight, isNestedInDialog]);
    React.useEffect(() => {
        if (isNestedInDialog) {
            return;
        }
        setMarginsDisabled(disableMargins);
        return () => setMarginsDisabled(false);
    }, [disableMargins, isNestedInDialog]);
    React.useEffect(() => {
        if (isNestedInDialog) {
            return;
        }
        if (!autoHeight) {
            setContentExpandsToAvailableHeight(true);
            return () => setContentExpandsToAvailableHeight(false);
        } else {
            setContentExpandsToAvailableHeight(false);
        }
    }, [autoHeight, isNestedInDialog]);
    return (
        <div
            style={{
                ...(!autoHeight
                    ? { display: 'flex', flexDirection: 'column', height: '100%' }
                    : {}),
                ...style,
            }}>
            {(isNestedInDialog || proceed) && children}
        </div>
    );
};

export default GridPage;
