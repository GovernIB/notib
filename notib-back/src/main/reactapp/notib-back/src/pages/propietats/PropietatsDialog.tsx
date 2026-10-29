import React from 'react';
import { useTranslation } from 'react-i18next';
import { Grid } from '@mui/material';
import { FormApi, MuiDialog, useBaseAppContext, useFormContext, useResourceApiService } from 'reactlib';
import GridFormField from '../../components/GridFormField';
import Load from '../../components/Load';
import StyledMuiGrid from '../../components/StyledMuiGrid';
import { icons } from '../../utils/icons';
import * as builder from '../../utils/springFilterUtils';
import type { PropietatItem } from './PropietatsTypes';
import { PREFIX_CLAU, configTypeValueLabel, getFieldFromItem, sufixClau } from './PropietatsUtils';

/** Formulari de modificació del valor d'una propietat per entitat (la clau i l'entitat no es poden canviar). */
const PropietatsEntitatForm: React.FC<{ itemField: any }> = ({ itemField }) => {
    const { data, fields, apiRef } = useFormContext();

    const valueField = React.useMemo(
        () => ({
            ...itemField,
            label: fields?.find((f) => f.name === 'value')?.label,
            key: data?.key,
        }),
        [itemField, fields, data?.key]
    );

    // Les caselles necessiten un booleà i el valor arriba com a text
    React.useEffect(() => {
        if (data?.id && valueField.type === 'checkbox' && typeof data.value === 'string') {
            apiRef.current?.setFieldValue('value', data.value === 'true');
        }
    }, [data?.id, data?.value, valueField.type, apiRef]);

    return (
        <Grid container direction={'row'} columnSpacing={1} rowSpacing={1}>
            <GridFormField size={12} name={'entitat'} disabled />
            <GridFormField size={12} name={'key'} disabled />
            <GridFormField size={12} name={'value'} field={valueField} />
        </Grid>
    );
};

// Les claus per entitat duen el codi d'entitat enmig, per tant l'ordre per clau és l'ordre per entitat
const sortModel: any[] = [{ field: 'key', sort: 'asc' }];

/**
 * Diàleg amb els valors d'una propietat configurable per a cada entitat. Les files ja existeixen:
 * des d'aquí es modifica el valor o s'esborra (i s'aplica el de la propietat general).
 */
export const usePropietatsDialog = () => {
    const { t } = useTranslation();
    const { temporalMessageShow, t: tLib } = useBaseAppContext();
    const { isReady: apiIsReady, patch: apiPatch } = useResourceApiService('configResource');
    const formApiRef = React.useRef<FormApi | any>({});

    const [open, setOpen] = React.useState(false);
    const [item, setItem] = React.useState<PropietatItem>();
    const [itemField, setItemField] = React.useState<any>();

    // Les propietats per entitat són PREFIX + '.' + codi d'entitat + la resta de la clau de la general
    const filter = React.useMemo(
        () =>
            item
                ? builder.and(
                      builder.neq('entitat', null),
                      `key~'${PREFIX_CLAU}.%${sufixClau(item.key)}'`
                  )
                : undefined,
        [item]
    );

    // El valor es mostra amb l'etiqueta traduïda del tipus de propietat, igual que al formulari
    // d'edició. El valor desat no canvia: si no hi ha traducció es mostra tal qual.
    const columns: any[] = React.useMemo(
        () => [
            { field: 'entitat', flex: 0.5, sortable: false },
            { field: 'key', flex: 1 },
            {
                field: 'value',
                flex: 0.5,
                valueFormatter: (value: any) =>
                    value != null && item?.configTypeCode != null
                        ? configTypeValueLabel(t, item.configTypeCode, value)
                        : value,
            },
        ],
        [item, t]
    );

    const handleOpen = (row: PropietatItem) => {
        setItem(row);
        setItemField(getFieldFromItem(row, t));
        setOpen(true);
    };

    const handleClose = (reason?: string) => {
        if (reason !== 'backdropClick') {
            setItem(undefined);
            setItemField(undefined);
            setOpen(false);
        }
    };

    const buttons: any[] = [
        {
            value: 'close',
            text: t('common.close'),
            icon: icons.cancela,
            componentProps: { variant: 'outlined' },
        },
    ];

    const actions = [
        {
            label: t('common.update'),
            icon: icons.modifica,
            showInMenu: false,
            clickShowUpdateDialog: true,
        },
        {
            label: t('common.delete'),
            icon: icons.esborra,
            showInMenu: false,
            onClick: (id: any) => {
                if (apiIsReady) {
                    apiPatch(id, { data: { value: null } })
                        .then(() => {
                            formApiRef.current?.refresh();
                            temporalMessageShow(null, tLib('form.update.success'), 'success');
                        })
                        .catch((error) => temporalMessageShow(null, error.message, 'error'));
                }
            },
            hidden: (row: any) => row.value == null,
        },
    ];

    const dialog = (
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            title={item?.description}
            componentProps={{ fullWidth: true, maxWidth: 'xl' }}
            buttons={buttons}
            buttonCallback={(): void => {
                handleClose();
            }}
        >
            <Load value={item}>
                <StyledMuiGrid
                    apiRef={formApiRef}
                    resourceName={'configResource'}
                    columns={columns}
                    filter={filter}
                    popupEditActive
                    popupEditFormDialogResourceTitle={t('page.propietats.title')}
                    popupEditFormContent={<PropietatsEntitatForm itemField={itemField} />}
                    sortModel={sortModel}
                    autoHeight
                    paginationActive
                    rowAdditionalActions={actions}
                    toolbarHideCreate
                    rowHideUpdateButton
                    rowHideDeleteButton
                />
            </Load>
        </MuiDialog>
    );

    return { handleOpen, handleClose, dialog };
};
