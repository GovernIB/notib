import { createPortal } from 'react-dom';
import { useTranslation } from 'react-i18next';
import Icon from '@mui/material/Icon';
import Typography from '@mui/material/Typography';
import { icons } from '../utils/icons';
import { countSpringFilterTerms } from '../utils/springFilterCount';
import { useScreenFrame } from './ScreenFrameContext';

type FilterCountProps = {
    /** Spring Filter aplicat pel filtre de la pantalla. */
    springFilter?: string;
};

/**
 * Recompte de filtres aplicats d'un llistat, com a text breu: «( [icona de filtre] 2 filtres
 * aplicats )». Es pinta a la barra de títol de la pantalla, al costat del títol (veure ScreenTitle);
 * fora d'un ScreenFrame, o sense cap filtre aplicat, no pinta res.
 */
export const FilterCount: React.FC<FilterCountProps> = (props) => {
    const { springFilter } = props;
    const { t } = useTranslation();
    const screenFrame = useScreenFrame();
    const count = countSpringFilterTerms(springFilter);
    if (!count || screenFrame?.infoSlot == null) {
        return null;
    }
    return createPortal(
        <Typography
            variant="body2"
            color="text.secondary"
            noWrap
            sx={{ display: 'inline-flex', alignItems: 'center', gap: 0.5 }}
        >
            {'('}
            <Icon aria-hidden sx={{ fontSize: '1.1rem' }}>
                {icons.filtre}
            </Icon>
            {`${t('comu.filtresAplicats', { count })} )`}
        </Typography>,
        screenFrame.infoSlot
    );
};

export default FilterCount;
