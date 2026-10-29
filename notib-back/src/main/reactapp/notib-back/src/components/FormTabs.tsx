import React from 'react';
import Box from '@mui/material/Box';
import { MuiFormTabs, MuiFormTabContent, useFormContext } from 'reactlib';
import { useIsNestedInDialog } from '../../lib/components/BaseAppContext';
import { useFormTabsContext } from '../../lib/components/mui/form/MuiFormTabs';

/** Alçada mínima de les pestanyes amb graella quan el formulari es mostra dins una modal */
const DIALOG_GRID_TAB_HEIGHT = '65vh';

const useTabsInsideDialog = () => {
    const isNestedInDialog = useIsNestedInDialog();
    const { id } = useFormContext();
    return isNestedInDialog && id != null;
};

/**
 * Substitut de MuiFormTabs. Fora d'una modal es comporta igual. Dins una modal, apila totes les
 * pestanyes a la mateixa cel·la d'un CSS grid, de manera que la modal agafa l'alçada de la pestanya
 * més gran i no canvia de mida en moure's entre pestanyes.
 */
export const FormTabs: React.FC<React.ComponentProps<typeof MuiFormTabs>> = (props) => {
    const { children, ...otherProps } = props;
    const tabsInsideDialog = useTabsInsideDialog();
    return (
        <MuiFormTabs {...otherProps}>
            {tabsInsideDialog ? <Box sx={{ display: 'grid' }}>{children}</Box> : children}
        </MuiFormTabs>
    );
};

type FormTabContentProps = React.ComponentProps<typeof MuiFormTabContent> & {
    /** La pestanya conté una graella */
    grid?: boolean;
};

/**
 * Substitut de MuiFormTabContent per fer servir amb FormTabs. Dins una modal:
 * - les pestanyes sense graella sempre estan muntades (amagades amb visibility) perquè comptin
 *   per a l'alçada de la modal;
 * - les pestanyes amb graella només es munten quan estan actives (per no fer peticions de més),
 *   però reserven una alçada mínima i s'estiren fins a l'alçada de la pestanya més gran
 *   (MuiFormTabContent els dona alçada 100%, que dins una modal val 0).
 */
export const FormTabContent: React.FC<FormTabContentProps> = (props) => {
    const { grid, index, showOnCreate, children, style, ...otherProps } = props;
    const tabsInsideDialog = useTabsInsideDialog();
    const { index: currentIndex } = useFormTabsContext();
    if (!tabsInsideDialog) {
        return (
            // `style` només es passa si està definit: un `style={undefined}` explícit esborraria
            // l'`height: 100%` que MuiFormTabContent posa per defecte (les graelles col·lapsarien).
            <MuiFormTabContent
                index={index}
                showOnCreate={showOnCreate}
                {...(style != null ? { style } : {})}
                {...otherProps}>
                {children}
            </MuiFormTabContent>
        );
    }
    const active = currentIndex === index;
    return (
        <div
            role="tabpanel"
            id={`tabpanel-${index}`}
            aria-labelledby={`tab-${index}`}
            aria-hidden={!active}
            style={{
                gridArea: '1 / 1',
                visibility: active ? 'visible' : 'hidden',
                minHeight: grid ? DIALOG_GRID_TAB_HEIGHT : undefined,
                ...style,
            }}
            {...otherProps}>
            {(active || !grid) && (
                <Box sx={{ pt: 3, height: '100%', boxSizing: 'border-box' }}>{children}</Box>
            )}
        </div>
    );
};

export default FormTabs;
