import React from 'react';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import Box from '@mui/material/Box';
import Paper from '@mui/material/Paper';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemButton from '@mui/material/ListItemButton';
import Collapse from '@mui/material/Collapse';
import CircularProgress from '@mui/material/CircularProgress';
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
    FormApiRef,
    springFilterBuilder,
} from 'reactlib';

// Prefix comú a totes les claus de propietat. Les propietats configurables per entitat
// tenen la forma PREFIX + '.' + Codi Entitat + resta de la propietat (que ja inclou el punt inicial).
const PREFIX = 'es.caib.notib';

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

const buildCustomField = (c: any) => {
    const type = fieldPropType(c.configTypeCode, c.configTypeValue);
    const options = c.configTypeValue
        ? Object.fromEntries(c.configTypeValue.split(',').map((v: string) => [v, v]))
        : undefined;
    return {
        name: c.key,
        type,
        label: '',
        value: c.value,
        options,
    };
};

// Normalitza un valor de propietat per poder comparar el valor actual amb l'original
// independentment del tipus concret que retorni cada component de camp (string, number, boolean...).
const normalizeValue = (value: any) => (value == null || value === '' ? '' : String(value));

// Donada la clau d'una propietat base (sense entitat), retorna la clau que hauria de tenir
// la mateixa propietat configurada per una entitat concreta.
const entitatKeySuffix = (key: string) => key.substring(PREFIX.length);

// Comprova si `key` és la propietat per entitat corresponent a la propietat base `baseKey`.
const isEntitatConfigOfBaseKey = (key: string, baseKey: string) => {
    if (!key.startsWith(PREFIX + '.')) {
        return false;
    }
    const afterPrefix = key.substring(PREFIX.length + 1);
    const dotIndex = afterPrefix.indexOf('.');
    if (dotIndex < 0) {
        return false;
    }
    return PREFIX + afterPrefix.substring(dotIndex) === baseKey;
};

const PropsListItem: React.FC<{
    item: any;
    highlight?: string;
    title?: string;
    expanded?: boolean;
    onToggleExpand?: () => void;
    onSaved?: (key: string, value: any) => void;
}> = (props) => {
    const { item, highlight, title, expanded, onToggleExpand, onSaved } = props;
    const { t } = useTranslation();
    const { apiPatch } = usePropsContext();
    const { temporalMessageShow } = useBaseAppContext();
    const [changedValue, setChangedValue] = React.useState<any | undefined>(undefined);
    const disabled = item.jbossProperty;
    const password = item.configTypeCode === 'PASSWORD' ? true : undefined;
    const decimalScale = item.configTypeCode === 'INT' ? 0 : undefined;
    // Només es considera modificat si el valor actual del camp difereix realment del
    // valor original de la propietat (i no simplement perquè el camp hagi disparat
    // un onChange, cosa que alguns components fan també en normalitzar el seu valor inicial).
    const isDirty =
        changedValue !== undefined && normalizeValue(changedValue) !== normalizeValue(item.value);
    const handleFieldOnChange = (value: any) => {
        setChangedValue(value);
    };
    const handleSaveClick = () => {
        apiPatch(item.id, { data: { value: changedValue } })
            .then(() => {
                onSaved?.(item.key, changedValue);
                setChangedValue(undefined);
                temporalMessageShow(null, t('page.propietats.save.success'), 'success');
            })
            .catch((error) =>
                temporalMessageShow(t('page.propietats.save.error'), error.message, 'error')
            );
    };
    return (
        <Grid container spacing={2} sx={{ width: '100%' }}>
            <Grid
                size={6}
                sx={{
                    '& p.MuiTypography-root': {
                        fontSize: '14px',
                    },
                }}
            >
                <TextHighlight text={title ?? item.description} match={highlight} ignoreCase />
            </Grid>
            <Grid size={6}>
                <Box
                    sx={{
                        display: 'flex',
                        flexDirection: 'row',
                        justifyContent: 'space-between',
                        gap: 1,
                    }}
                >
                    <FormField
                        name={item.key}
                        inline
                        password={password}
                        decimalScale={decimalScale}
                        disabled={disabled}
                        onChange={handleFieldOnChange}
                        componentProps={{ helperText: item.key }}
                    />
                    <Box
                        sx={{
                            display: 'flex',
                            justifyContent: 'flex-end',
                            alignItems: 'flex-start',
                        }}
                    >
                        {isDirty && (
                            <IconButton
                                color="primary"
                                onClick={handleSaveClick}
                                // disabled={changedValue === undefined}
                            >
                                <Icon fontSize="small">save</Icon>
                            </IconButton>
                        )}
                        {onToggleExpand && (
                            <IconButton
                                size="small"
                                onClick={onToggleExpand}
                                sx={{
                                    ml: 1,
                                    transform: expanded ? 'rotate(180deg)' : 'rotate(0deg)',
                                    transition: 'transform 0.2s',
                                }}
                            >
                                <Icon fontSize="small">expand_more</Icon>
                            </IconButton>
                        )}
                    </Box>
                </Box>
            </Grid>
        </Grid>
    );
};

// Bloc que es desplega sota una propietat configurable per entitat, amb el valor
// d'aquesta propietat per a cada entitat que el te configurat.
const PropsEntitatConfigs: React.FC<{ item: any; groupId: any; highlight?: string }> = (props) => {
    const { item, groupId, highlight } = props;
    const { t } = useTranslation();
    const { isReady: apiIsReady, find: apiFind } = useResourceApiService('configResource');
    const [entitatConfigs, setEntitatConfigs] = React.useState<any[]>();
    const [customFields, setCustomFields] = React.useState<any[]>();

    React.useEffect(() => {
        if (!apiIsReady) {
            return;
        }
        const args = {
            filter: springFilterBuilder.and(
                springFilterBuilder.eq('configGroup.id', groupId),
                springFilterBuilder.neq('entitat', null),
                `key~'${PREFIX}.%${entitatKeySuffix(item.key)}'`
            ),
            unpaged: true,
        };
        apiFind(args).then((response) => {
            const rows = response.rows
                .filter((r: any) => isEntitatConfigOfBaseKey(r.key, item.key))
                .sort((a: any, b: any) =>
                    (a.entitat?.description ?? '').localeCompare(b.entitat?.description ?? '')
                );
            setEntitatConfigs(rows);
            setCustomFields(rows.map(buildCustomField));
        });
    }, [apiIsReady, item.key, groupId]);

    const handleItemSaved = (key: string, value: any) => {
        setEntitatConfigs((prev) => prev?.map((c) => (c.key === key ? { ...c, value } : c)));
    };

    if (entitatConfigs == null || customFields == null) {
        return (
            <Box sx={{ display: 'flex', justifyContent: 'center', py: 2 }}>
                <CircularProgress size={20} />
            </Box>
        );
    }
    return (
        <Box sx={{ pl: 4, pr: 2, pb: 1 }}>
            {entitatConfigs.length ? (
                <MuiForm
                    resourceName="configResource"
                    customFields={customFields}
                    hiddenToolbar
                    commonFieldComponentProps={{ size: 'small' }}
                >
                    <List dense disablePadding>
                        {entitatConfigs.map((ec) => (
                            <ListItem key={ec.key} disablePadding>
                                <PropsListItem
                                    item={ec}
                                    highlight={highlight}
                                    title={ec.entitat?.description}
                                    onSaved={handleItemSaved}
                                />
                            </ListItem>
                        ))}
                    </List>
                </MuiForm>
            ) : (
                <Typography variant="body2" color="text.secondary">
                    {t('page.propietats.entitats.empty')}
                </Typography>
            )}
        </Box>
    );
};

export const PropietatsProps: React.FC<{
    quickFilter?: string;
    group?: any;
    formApiRef?: FormApiRef;
}> = (props) => {
    const { quickFilter, group, formApiRef } = props;
    const { t } = useTranslation();
    const {
        isReady: apiIsReady,
        find: apiFind,
        create: apiCreate,
        patch: apiPatch,
    } = useResourceApiService('configResource');
    const [configs, setConfigs] = React.useState<any[]>();
    const [customFields, setCustomFields] = React.useState<any[]>();
    const [expandedKeys, setExpandedKeys] = React.useState<Set<string>>(new Set());
    React.useEffect(() => {
        if (apiIsReady && group != null) {
            const args = {
                quickFilter,
                filter: springFilterBuilder.and(
                    springFilterBuilder.eq('configGroup.id', group.id),
                    springFilterBuilder.eq('entitat', null)
                ),
                sorts: ['position,asc'],
                unpaged: true,
            };
            apiFind(args).then((response) => {
                const configs = response.rows.filter(() => true);
                setConfigs(configs);
                setExpandedKeys(new Set());
                setCustomFields(configs.map(buildCustomField));
            });
        }
    }, [apiIsReady, quickFilter, group]);
    const propsContextValue = {
        apiCreate,
        apiPatch,
    };
    const handleItemSaved = (key: string, value: any) => {
        setConfigs((prev) => prev?.map((c) => (c.key === key ? { ...c, value } : c)));
    };
    const handleToggleExpand = (key: string) => {
        setExpandedKeys((prev) => {
            const next = new Set(prev);
            if (next.has(key)) {
                next.delete(key);
            } else {
                next.add(key);
            }
            return next;
        });
    };
    return (
        group != null &&
        customFields != null && (
            <PropsContext.Provider value={propsContextValue}>
                <Box sx={{ px: 3 }}>
                    <Typography variant="h6" sx={{ mb: 1 }}>
                        {group.description}
                    </Typography>
                    <List component={Paper}>
                        {customFields.length ? (
                            <MuiForm
                                apiRef={formApiRef}
                                resourceName="configResource"
                                customFields={customFields}
                                hiddenToolbar
                                commonFieldComponentProps={{ size: 'small' }}
                            >
                                {configs?.map((c) => {
                                    const expanded = expandedKeys.has(c.key);
                                    return (
                                        <ListItem key={c.key} disablePadding sx={{ display: 'block' }}>
                                            <ListItemButton disableRipple>
                                                <PropsListItem
                                                    item={c}
                                                    highlight={quickFilter}
                                                    expanded={expanded}
                                                    onToggleExpand={
                                                        c.configurable
                                                            ? () => handleToggleExpand(c.key)
                                                            : undefined
                                                    }
                                                    onSaved={handleItemSaved}
                                                />
                                            </ListItemButton>
                                            {c.configurable && (
                                                <Collapse in={expanded} unmountOnExit>
                                                    <PropsEntitatConfigs
                                                        item={c}
                                                        groupId={group.id}
                                                        highlight={quickFilter}
                                                    />
                                                </Collapse>
                                            )}
                                        </ListItem>
                                    );
                                })}
                            </MuiForm>
                        ) : (
                            <Box
                                sx={{
                                    width: '100%',
                                    textAlign: 'center',
                                    px: 2,
                                    py: 4,
                                }}
                            >
                                <Icon fontSize="large" color="disabled">
                                    block
                                </Icon>
                                <Typography variant="h5" color="text.secondary">
                                    {t('page.propietats.empty')}
                                </Typography>
                            </Box>
                        )}
                    </List>
                </Box>
            </PropsContext.Provider>
        )
    );
};
