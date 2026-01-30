import React from 'react';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import Box from '@mui/material/Box';
import Paper from '@mui/material/Paper';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemButton from '@mui/material/ListItemButton';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import Typography from '@mui/material/Typography';
import {
    MuiForm,
    FormField,
    TextHighlight,
    useBaseAppContext,
    useResourceApiService,
    ResourceApiRequestArgs,
} from 'reactlib';

type PropsContextType = {
    apiCreate: (args: ResourceApiRequestArgs) => Promise<any>;
    apiPatch: (id: any, args: ResourceApiRequestArgs) => Promise<any>;
};
const PropsContext = React.createContext<PropsContextType | undefined>(undefined);
const usePropsContext = () => {
    const context = React.useContext(PropsContext);
    if (context === undefined) {
        throw new Error('usePropsContext must be used within a PropsContext provider');
    }
    return context;
};

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
    const { t } = useTranslation();
    const { apiPatch } = usePropsContext();
    const { temporalMessageShow } = useBaseAppContext();
    const [changedValue, setChangedValue] = React.useState<any | undefined>(undefined);
    const disabled = item.jbossProperty;
    const password = item.configTypeCode === 'PASSWORD' ? true : undefined;
    const decimalScale = item.configTypeCode === 'INT' ? 0 : undefined;
    const handleFieldOnChange = (value: any) => {
        setChangedValue(value);
    };
    const handleSaveClick = () => {
        apiPatch(item.id, { data: { value: changedValue } })
            .then(() => {
                setChangedValue(undefined);
                temporalMessageShow(null, t('page.propietats.save.success'), 'success');
            })
            .catch((error) =>
                temporalMessageShow(t('page.propietats.save.error'), error.message, 'error')
            );
    };
    const handleExpandClick = () => {
        console.log('>>> expand (TODO)');
    };
    return (
        <Grid container spacing={2} sx={{ width: '100%' }}>
            <Grid size={6}>
                <TextHighlight text={item.description} match={highlight} ignoreCase />
            </Grid>
            <Grid size={5}>
                <FormField
                    name={item.key}
                    inline
                    password={password}
                    decimalScale={decimalScale}
                    disabled={disabled}
                    onChange={handleFieldOnChange}
                    componentProps={{ helperText: item.key }}
                />
            </Grid>
            <Grid size={1}>
                <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                    <IconButton
                        size="small"
                        onClick={handleSaveClick}
                        disabled={changedValue === undefined}>
                        <Icon fontSize="small">save</Icon>
                    </IconButton>
                    {item.configurable && (
                        <IconButton size="small" onClick={handleExpandClick} sx={{ ml: 1 }}>
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
    const {
        isReady: apiIsReady,
        find: apiFind,
        create: apiCreate,
        patch: apiPatch,
    } = useResourceApiService('configResource');
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
    const propsContextValue = {
        apiCreate,
        apiPatch,
    };
    return (
        group != null &&
        customFields != null && (
            <PropsContext.Provider value={propsContextValue}>
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
            </PropsContext.Provider>
        )
    );
};
