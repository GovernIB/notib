import React from 'react';
import { useTranslation } from 'react-i18next';
import TextField from '@mui/material/TextField';
import InputAdornment from '@mui/material/InputAdornment';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import { FormApiRef, GridPage, useDebounce, useFormApiRef } from 'reactlib';
import { PropietatsGroups } from './PropietatsGroups';
import { PropietatsProps } from './PropietatsProps';
import { Box, Tooltip } from '@mui/material';

const PropietatsQuickFilter: React.FC<{
    onChange: (quickFilter: string | undefined) => void;
    formApiRef: FormApiRef;
}> = (props) => {
    const { onChange, formApiRef } = props;
    const { t } = useTranslation();
    const [quickFilter, setQuickFilter] = React.useState<string>('');
    const quickFilterDebounced = useDebounce(quickFilter);

    React.useEffect(() => {
        onChange?.(quickFilterDebounced);
    }, [quickFilterDebounced]);

    return (
        <Box sx={{ display: 'flex', gap: 1 }}>
            <TextField
                value={quickFilter}
                onChange={(event) => setQuickFilter(event.target.value)}
                label={t('page.propietats.find')}
                variant="outlined"
                fullWidth
                size="small"
                slotProps={{
                    input: {
                        startAdornment: (
                            <InputAdornment position="start">
                                <Icon fontSize="small">search</Icon>
                            </InputAdornment>
                        ),
                        endAdornment: quickFilter && (
                            <InputAdornment position="end">
                                <IconButton size="small" onClick={() => setQuickFilter('')}>
                                    <Icon fontSize="inherit">clear</Icon>
                                </IconButton>
                            </InputAdornment>
                        ),
                    },
                }}
            />
            <Tooltip title={t('page.propietats.revert')}>
                <IconButton
                    size="small"
                    onClick={() => {
                        formApiRef.current?.revert();
                    }}
                >
                    <Icon>undo</Icon>
                </IconButton>
            </Tooltip>
        </Box>
    );
};

const Propietats: React.FC = () => {
    const [quickFilter, setQuickFilter] = React.useState<string>();
    const [selectedGroup, setSelectedGroup] = React.useState<any>();
    const formApiRef = useFormApiRef();

    return (
        <GridPage disableMargins>
            <Box
                id="contingutGeneral"
                sx={{
                    display: 'flex',
                    flexDirection: 'column',
                    height: '100%',
                    overflow: 'hidden',
                }}
            >
                <Box
                    id="filtre"
                    sx={{
                        p: 2,
                        px: 10,
                        mt: 1,
                        flexShrink: 0,
                        bgcolor: 'background.paper',
                        borderBottom: '1px solid',
                        borderColor: 'divider',
                    }}
                >
                    <PropietatsQuickFilter onChange={setQuickFilter} formApiRef={formApiRef} />
                </Box>

                <Box
                    id="contingutPropietats"
                    sx={{
                        display: 'flex',
                        flexGrow: 1,
                        overflow: 'hidden',
                    }}
                >
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
                            formApiRef={formApiRef}
                        />
                    </Box>
                </Box>
            </Box>
        </GridPage>
    );
};

export default Propietats;
