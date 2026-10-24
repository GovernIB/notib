import React from 'react';
import { useTranslation } from 'react-i18next';
import TextField from '@mui/material/TextField';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import {GridPage, useBaseAppContext, useDebounce, useResourceApiService} from 'reactlib';
import { PropietatsGroups } from './PropietatsGroups';
import { PropietatsProps } from './PropietatsProps';
import { Box } from '@mui/material';
import PageTitle from "../../components/PageTitle.tsx";
import ScreenFrame from '../../components/ScreenFrame';
import { icons } from '../../utils/icons';
import {ToolbarButton} from "../../components/StyledMuiGrid.tsx";

const PropietatsQuickFilter: React.FC<{
    onChange: (quickFilter: string | undefined) => void;
}> = (props) => {
    const {onChange} = props;
    const {t} = useTranslation();
    const [quickFilter, setQuickFilter] = React.useState<string>('');
    const quickFilterDebounced = useDebounce(quickFilter);
    React.useEffect(() => {
        onChange?.(quickFilterDebounced);
    }, [quickFilterDebounced]);
    return (
        <Box sx={{display: 'flex', alignItems: 'center', gap: 1}}>
            <TextField
                // Mateix aspecte que el filtre ràpid de la llibreria
                // (lib/components/mui/datacommon/DataQuickFilter.tsx): sense etiqueta
                // flotant, la lupa al final i el botó d'esborrar al seu costat.
                title={t('page.propietats.find')}
                value={quickFilter}
                onChange={(event) => setQuickFilter(event.target.value)}
                size="small"
                slotProps={{
                    htmlInput: {
                        'aria-label': t('page.propietats.find'),
                    },
                    input: {
                        endAdornment: (
                            <>
                                {quickFilter && (
                                    <IconButton size="small" onClick={() => setQuickFilter('')}>
                                        <Icon fontSize="inherit">clear</Icon>
                                    </IconButton>
                                )}
                                <Icon>search</Icon>
                            </>
                        ),
                    },
                }}
                // Mateixa alçada que el filtre ràpid de la barra d'eines quan cau
                // sobre la barra de títol (32px; veure StyledMuiGrid.tsx:310).
                sx={{
                    '& .MuiOutlinedInput-root': {height: '32px'},
                    '& .MuiOutlinedInput-input': {pt: 0, pb: 0},
                }}
            />
        </Box>
    );
};

const Propietats: React.FC = () => {

    const { t } = useTranslation();
    const [quickFilter, setQuickFilter] = React.useState<string>();
    const [selectedGroup, setSelectedGroup] = React.useState<any>();
    const { artifactAction: apiAction } = useResourceApiService('configResource');
    const { temporalMessageShow } = useBaseAppContext();
    const sync = () => {
        apiAction(undefined, { code: 'SYNC_JBOSS' })
            .then(() => {
                temporalMessageShow(null, t('page.propietats.syncJboss.ok'), 'success');
            }).catch((error) => temporalMessageShow(null, error.message, 'error'));
    };
    return (
        <>
        <PageTitle title={t('app.menu.propietats')}></PageTitle>
        <GridPage disableMargins>
            <ScreenFrame
                icon={icons.config}
                title={t('app.menu.propietats')}

                // El cercador i el botó de desfer van a la barra de títol
                actions={<>
                    <PropietatsQuickFilter onChange={setQuickFilter} />
                    <ToolbarButton
                        title={t('page.propietats.syncJboss.label')}
                        icon={icons.sincronitza}
                        color="primary"
                        onClick={sync}
                    >
                        {t('page.propietats.syncJboss.label')}
                    </ToolbarButton>
                </>
                }
            >
                <Box id="contingutPropietats" sx={{display: 'flex', flexGrow: 1, overflow: 'hidden'}}>
                    <Box
                        id="grup"
                        sx={{
                            width: { md: '250px', lg: '400px' },
                            flexShrink: 0,
                            height: '100%',
                            minHeight: 0,
                            borderRight: '1px solid',
                            borderColor: 'divider',
                            overflowY: 'auto',
                            p: 2,
                        }}
                    >
                        <PropietatsGroups quickFilter={quickFilter} onChange={setSelectedGroup} />
                    </Box>

                    <Box
                        id="props"
                        sx={{
                            flexGrow: 1,
                            height: 'auto',
                            overflowY: 'auto',
                            py: 2,
                            bgcolor: 'customBackground',
                        }}
                    >
                        <PropietatsProps
                            group={selectedGroup}
                            quickFilter={quickFilter}
                        />
                    </Box>
                </Box>
            </ScreenFrame>
        </GridPage>
    </>
    );
};

export default Propietats;
