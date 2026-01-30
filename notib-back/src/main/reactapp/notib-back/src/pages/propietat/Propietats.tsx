import React from 'react';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import TextField from '@mui/material/TextField';
import InputAdornment from '@mui/material/InputAdornment';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import { useDebounce } from 'reactlib';
import { PropietatsGroups } from './PropietatsGroups';
import { PropietatsProps } from './PropietatsProps';

const PropietatsQuickFilter: React.FC<{ onChange: (quickFilter: string | undefined) => void }> = (
    props
) => {
    const { onChange } = props;
    const { t } = useTranslation();
    const [quickFilter, setQuickFilter] = React.useState<string>('');
    const quickFilterDebounced = useDebounce(quickFilter);
    React.useEffect(() => {
        onChange?.(quickFilterDebounced);
    }, [quickFilterDebounced]);
    return (
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
    );
};

const Propietats: React.FC = () => {
    const [quickFilter, setQuickFilter] = React.useState<string>();
    const [selectedGroup, setSelectedGroup] = React.useState<any>();
    return (
        <Grid container spacing={2}>
            <Grid size={12} sx={{ px: 10 }}>
                <PropietatsQuickFilter onChange={setQuickFilter} />
            </Grid>
            <Grid size={3}>
                <PropietatsGroups quickFilter={quickFilter} onChange={setSelectedGroup} />
            </Grid>
            <Grid size={9}>
                <PropietatsProps group={selectedGroup} quickFilter={quickFilter} />
            </Grid>
        </Grid>
    );
};

export default Propietats;
