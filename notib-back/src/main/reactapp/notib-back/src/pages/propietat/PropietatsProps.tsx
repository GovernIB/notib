import React from 'react';
import Grid from '@mui/material/Grid';
import Box from '@mui/material/Box';
import Paper from '@mui/material/Paper';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemButton from '@mui/material/ListItemButton';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import Typography from '@mui/material/Typography';
import { MuiForm, FormField, TextHighlight, useResourceApiService } from 'reactlib';

const fieldPropType = (typeCode: string, typeValue: string) => {
    if (typeValue != null) {
        return 'search';
    } else {
        switch (typeCode) {
            case 'INT':
                return 'number';
            case 'FLOAT':
                return 'number';
            case 'BOOL':
                return 'checkbox';
            default:
                return 'text';
        }
    }
};

const PropsListItem: React.FC<{ item: any; highlight?: string }> = (props) => {
    const { item, highlight } = props;
    const disabled = item.jbossProperty;
    const password = item.configTypeCode === 'PASSWORD' ? true : undefined;
    const decimalScale = item.configTypeCode === 'INT' ? 0 : undefined;
    return (
        <Grid container spacing={2} sx={{ width: '100%' }}>
            <Grid size={5}>
                <TextHighlight text={item.description} match={highlight} ignoreCase />
            </Grid>
            <Grid size={6}>
                <FormField
                    name={item.key}
                    inline
                    password={password}
                    decimalScale={decimalScale}
                    disabled={disabled}
                    helperText={item.key}
                />
            </Grid>
            <Grid size={1}>
                <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                    <IconButton size="small">
                        <Icon fontSize="small">save</Icon>
                    </IconButton>
                    {item.configurable && (
                        <IconButton size="small" sx={{ ml: 1 }}>
                            <Icon fontSize="small">expand_more</Icon>
                        </IconButton>
                    )}
                </Box>
            </Grid>
        </Grid>
    );
};

export const PropietatsProps: React.FC<{ quickFilter?: string; group?: any }> = (props) => {
    const { quickFilter, group } = props;
    const { isReady: apiIsReady, find: apiFind } = useResourceApiService('configResource');
    const [configs, setConfigs] = React.useState<any[]>();
    const [customFields, setCustomFields] = React.useState<any[]>();
    React.useEffect(() => {
        if (apiIsReady && group != null) {
            const args = {
                quickFilter,
                filter: 'configGroup.id:' + group.id,
                sorts: ['position,asc'],
                unpaged: true,
            };
            apiFind(args).then((response) => {
                const configs = response.rows.filter(() => true);
                console.log('>>> configs', configs);
                setConfigs(configs);
                const customFields = configs.map((c) => {
                    const type = fieldPropType(c.configTypeCode, c.configTypeValue);
                    const options = c.configTypeValue
                        ? Object.fromEntries(
                              c.configTypeValue.split(',').map((v: string) => [v, v])
                          )
                        : undefined;
                    return {
                        name: c.key,
                        type,
                        label: '', // c.description,
                        value: c.value,
                        options,
                    };
                });
                setCustomFields(customFields);
            });
        }
    }, [apiIsReady, quickFilter, group]);
    return (
        group != null &&
        customFields != null && (
            <Box sx={{ px: 3 }}>
                <Typography variant="h6" sx={{ mb: 1 }}>
                    {group.description}
                </Typography>
                <MuiForm
                    resourceName="configResource"
                    customFields={customFields}
                    hiddenToolbar
                    commonFieldComponentProps={{ size: 'small' }}>
                    <List component={Paper}>
                        {configs?.map((c) => (
                            <ListItem key={c.key} disablePadding>
                                <ListItemButton disableRipple>
                                    <PropsListItem item={c} highlight={quickFilter} />
                                </ListItemButton>
                            </ListItem>
                        ))}
                    </List>
                </MuiForm>
            </Box>
        )
    );
};
