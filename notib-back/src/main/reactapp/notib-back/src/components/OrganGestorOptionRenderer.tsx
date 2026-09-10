import React from 'react';
import { useTranslation } from 'react-i18next';
import Box from '@mui/material/Box';
import Icon from '@mui/material/Icon';
import Tooltip from '@mui/material/Tooltip';

// Codi d'estat "vigent" a es.caib.notib.logic.intf.dto.organisme.OrganGestorEstatEnum; la resta de valors
// (Extingit, Anulat, Transitori) es consideren "no vigents" a efectes de mostrar l'avís.
const ESTAT_VIGENT = 'V';

/**
 * Icona d'avís que es mostra al costat d'un òrgan gestor no vigent, tant als desplegables de selecció
 * d'òrgan com als llistats.
 */
export const OrganGestorNoVigentIcon: React.FC<{ fontSize?: 'small' | 'inherit' | 'medium' | 'large' }> = ({
    fontSize = 'small',
}) => {
    const { t } = useTranslation();
    return (
        <Tooltip title={t('comu.organGestorNoVigent')} arrow>
            <Icon color="warning" fontSize={fontSize} sx={{ verticalAlign: 'middle' }}>
                warning
            </Icon>
        </Tooltip>
    );
};

/**
 * `optionRenderer` per a `FormField`/`GridFormField` de tipus `organGestor` (o qualsevol referència a
 * `organGestorResource`), que afegeix la icona de "no vigent" a les opcions del desplegable que ho
 * siguin. Necessita que la petició de les opcions retorni el camp `estat` de l'òrgan (les peticions per
 * defecte ja el retornen, ja que el backend serveix el recurs sencer).
 */
export const useOrganGestorOptionRenderer = () => {
    return React.useCallback(({ description, data }: { id: any; description: string; data?: any }) => {
        const noVigent = data?.estat != null && data.estat !== ESTAT_VIGENT;
        return (
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5, width: '100%' }}>
                <Box component="span" sx={{ flex: 1, overflow: 'hidden', textOverflow: 'ellipsis' }}>
                    {description}
                </Box>
                {noVigent && <OrganGestorNoVigentIcon />}
            </Box>
        );
    }, []);
};

export default useOrganGestorOptionRenderer;
