import React from 'react';
import { useTranslation } from 'react-i18next';
import { Box, Grid, Icon, IconButton, ListItemButton } from '@mui/material';
import ListItem from '@mui/material/ListItem';
import { FormField, MuiForm, useFormContext } from 'reactlib';
import { icons } from '../../utils/icons';
import type { PropietatItem } from './PropietatsTypes';
import { TIPUS_CONTRASENYA, getFieldFromItem } from './PropietatsUtils';
import { TextHighlight } from './TextHighlight';
import { usePropietatsDialog } from './PropietatsDialog';

type PropsListItemProps = {
    highlight?: string;
};

/**
 * Fila d'una propietat: descripció, camp de valor segons el tipus i les accions disponibles.
 *
 * - Les propietats del fitxer de propietats del servidor (`jbossProperty`) es mostren en només
 *   lectura, amb el valor efectiu.
 * - Els botons de desfer i desar només estan habilitats si el valor s'ha modificat.
 * - Les propietats configurables per entitat tenen un botó que obre el diàleg amb els seus valors.
 */
const PropsListItem: React.FC<PropsListItemProps> = ({ highlight }) => {
    const { t } = useTranslation();
    const { data, apiRef, modified } = useFormContext();
    const item = data as PropietatItem;
    const { handleOpen, dialog } = usePropietatsDialog();

    const disabled = item.jbossProperty;
    const password = item.configTypeCode === TIPUS_CONTRASENYA;
    const decimalScale = item.configTypeCode === 'INT' ? 0 : undefined;
    const field = getFieldFromItem(item, t);
    // La descripció és l'etiqueta (<label for>) del camp, que es pinta `inline` (sense etiqueta
    // pròpia); sense, l'OAW (1.9) el dona per camp sense etiqueta. Les propietats amb valors possibles
    // es pinten amb un Select de MUI, que posa l'id a un <div>: un <label for> que hi apuntés seria una
    // associació incorrecta, i per això en aquest cas la descripció queda com a text.
    const inputId = React.useId();
    const ambEtiqueta = field.type !== 'search';

    const save = () => {
        apiRef.current?.save().catch(() => undefined);
    };

    // Torna al valor carregat o desat per últim cop, sense demanar confirmació
    const undo = () => {
        apiRef.current?.revert(true);
    };

    return (
        <Grid container spacing={2} sx={{ width: '100%' }}>
            <Grid size={4.5}>
                {ambEtiqueta ? (
                    <label htmlFor={inputId}>
                        <TextHighlight
                            text={item.description}
                            component="span"
                            match={highlight}
                            ignoreCase
                        />
                    </label>
                ) : (
                    <TextHighlight text={item.description} match={highlight} ignoreCase />
                )}
            </Grid>
            <Grid size={6.5}>
                <FormField
                    field={field}
                    name={'value'}
                    value={field.value}
                    inline
                    decimalScale={decimalScale}
                    disabled={disabled}
                    componentProps={{
                        ...(ambEtiqueta && { id: inputId }),
                        type: password ? 'password' : field.type,
                        autoComplete: password ? 'new-password' : undefined,
                        helperText: (
                            <TextHighlight
                                text={item.key}
                                component="span"
                                match={highlight}
                                ignoreCase
                            />
                        ),
                    }}
                />
            </Grid>
            <Grid size={1}>
                <Box sx={{ display: 'flex', justifyContent: 'end' }}>
                    {!disabled && (
                        <>
                            <IconButton
                                title={t('common.undo')}
                                size="small"
                                onClick={undo}
                                disabled={!modified}
                            >
                                <Icon fontSize="small">{icons.desfes}</Icon>
                            </IconButton>
                            <IconButton
                                title={t('common.save')}
                                size="small"
                                onClick={save}
                                color={'success'}
                                disabled={!modified}
                            >
                                <Icon fontSize="small">{icons.desa}</Icon>
                            </IconButton>
                            {item.configurable && (
                                <IconButton
                                    title={t('page.propietats.entitats.label')}
                                    size="small"
                                    onClick={() => handleOpen(item)}
                                >
                                    <Icon sx={{ m: 0 }} fontSize="small">
                                        {icons.configuracio}
                                    </Icon>
                                </IconButton>
                            )}
                        </>
                    )}
                </Box>
                {dialog}
            </Grid>
        </Grid>
    );
};

type PropietatsFormProps = PropsListItemProps & {
    id: string;
    onSaved?: (item: PropietatItem) => void;
};

/** Formulari (independent) d'una propietat, que carrega i desa el recurs per la seva clau. */
export const PropietatsForm: React.FC<PropietatsFormProps> = ({ id, highlight, onSaved }) => (
    <MuiForm
        key={id}
        id={id}
        resourceName="configResource"
        hiddenToolbar
        formBlockerDisabled
        onUpdateSuccess={(saved: PropietatItem) => onSaved?.(saved)}
        commonFieldComponentProps={{ size: 'small' }}
    >
        {/* <div> i no <li>: el MuiForm s'interposa entre la llista i l'element, i un <li> fora
            d'un <ul> és una llista mal construïda per a l'OAW (1.3) */}
        <ListItem component="div" disablePadding>
            <ListItemButton disableRipple>
                <PropsListItem highlight={highlight} />
            </ListItemButton>
        </ListItem>
    </MuiForm>
);

export default PropietatsForm;
