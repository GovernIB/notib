import { Divider, Icon, IconButton } from '@mui/material';
import { useTranslation } from 'react-i18next';
import { MuiDataGridApiRef } from 'reactlib';

type Props = { gridApiRef: MuiDataGridApiRef; hideCreateButton?: boolean; };
const GridToolbarButton: React.FC<Props> = (props) => {

    const { gridApiRef, hideCreateButton } = props;
    const { t } = useTranslation();
    const addButtonClick = () => gridApiRef.current?.triggerCreate();
    const refreshButtonClick = () => gridApiRef.current?.refresh();
    return (
        <>
            <Divider orientation="vertical" flexItem sx={{ mx: 1 }} />
            {(!hideCreateButton && <IconButton onClick={addButtonClick} title={t('component.GridToolbarButton.add')}><Icon>add</Icon></IconButton>)}
            <IconButton onClick={refreshButtonClick} title={t('component.GridToolbarButton.refresh')}><Icon>refresh</Icon></IconButton>
        </>
    );
};

export default GridToolbarButton;
