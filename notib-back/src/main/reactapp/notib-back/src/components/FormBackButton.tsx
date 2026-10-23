import React from 'react';
import { useBaseAppContext } from 'reactlib';
// El mateix component que fa servir la llibreria per al botó de tornar enrere de la seva// barra d'eines (lib/components/mui/form/MuiForm.tsx, línies 59-68): així el botó es veu i
// es comporta exactament igual (hover, tooltip, mida, color i estat desactivat).
import { toToolbarIcon } from '../../lib/components/mui/ToolbarIcon';

/**
 * Botó de "tornar enrere" de la barra de títol d'un formulari a pàgina.
 *
 * La llibreria el posa a la seva pròpia barra d'eines (que en aquestes pantalles es superposa a
 * la dreta de la barra de títol, veure FormScreen.tsx). Per tenir-lo entre la icona i el títol,
 * com la resta de pantalles de l'aplicació, el dibuixa l'aplicació i s'amaga el de la llibreria
 * amb `hiddenBackButton` del MuiForm.
 */
export const FormBackButton: React.FC<{ goBackLink?: string }> = ({ goBackLink }) => {
    const { t, goBack, anyHistoryEntryExist } = useBaseAppContext();
    // Mateixa condició que la llibreria (lib/components/mui/form/MuiForm.tsx, línia 57).
    const disabled = !anyHistoryEntryExist() && goBackLink == null;
    return toToolbarIcon('arrow_back', {
        title: t('form.goBack.title'),            // "Torna enrere" / "Vuelve atrás"
        onClick: () => goBack(goBackLink),
        disabled,
        sx: { mr: 1 },
    });
};

export default FormBackButton;
