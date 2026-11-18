import { GridPage, MuiDialog, useResourceApiService } from 'reactlib';
import { DetailCard } from '../../components/CardData.tsx';
import { ScreenFrame } from '../../components/ScreenFrame';
import { icons } from '../../utils/icons.ts';
import { useTranslation } from 'react-i18next';
import StyledMuiGrid from '../../components/StyledMuiGrid';
import { formatDate } from '../../utils/dateUtils.ts';
import Load from '../../components/Load.tsx';
import { useMemo, useState } from 'react';
import { FieldData, MuiDetail } from '../../components/MuiDetail';
import { Grid } from '@mui/material';
import { ErrorArea } from '../../components/ErrorPage';
import { useCloseDialogButtons } from '../../utils/buttonsOverride';

const ExcepcioDetail = ({ entity, currentFields }: any) => {
    return (
        <MuiDetail entity={entity} fields={currentFields}>
            <DetailCard>
                <FieldData field={'data'}>{formatDate(entity?.data)}</FieldData>
                <FieldData field={'tipus'} />
                <FieldData field={'uri'} />
                <FieldData field={'message'} />
                <FieldData field={'numOcurrencies'} />
                <FieldData field={'dataDarrera'}>{formatDate(entity?.dataDarrera)}</FieldData>
            </DetailCard>

            <Grid size={12}>
                <ErrorArea sx={{ maxHeight: '290px' }}>{entity?.stacktrace}</ErrorArea>
            </Grid>
        </MuiDetail>
    );
};

const useExcepcioDetail = () => {
    const { t } = useTranslation();

    const { isReady: apiIsReady, currentFields } = useResourceApiService('excepcioLogResource');

    const [open, setOpen] = useState(false);
    const [entity, setEntity] = useState<any>();

    const handleOpen = (_id: any, row: any) => {
        setEntity(row);
        setOpen(true);
    };

    const handleClose = (reason?: string) => {
        if (reason !== 'backdropClick') {
            setEntity(undefined);
            setOpen(false);
        }
    };

    const buttons = useCloseDialogButtons('close');

    const dialog = (
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            title={t('page.excepcio.grid.detallTitol')}
            componentProps={{ fullWidth: true, maxWidth: 'lg' }}
            buttons={buttons}
            buttonCallback={() => {
                handleClose();
            }}
        >
            <Load value={entity && apiIsReady}>
                <ExcepcioDetail entity={entity} currentFields={currentFields} />
            </Load>
        </MuiDialog>
    );

    return {
        apiIsReady,
        handleOpen,
        handleClose,
        dialog,
    };
};

const columns: any[] = [
    {
        field: 'data',
        flex: 0.5,
        minWidth: 100,
        valueFormatter: (value: any) => formatDate(value),
    },
    {
        field: 'tipus',
        flex: 0.75,
    },
    {
        field: 'uri',
        flex: 0.75,
    },
    {
        field: 'message',
        flex: 1,
    },
    {
        field: 'numOcurrencies',
        flex: 0.3,
        minWidth: 100,
    },
    {
        field: 'dataDarrera',
        flex: 0.5,
        minWidth: 160,
        valueFormatter: (value: any) => formatDate(value),
    },
];
const perspectives: any[] = [];
const sortModel: any[] = [{ field: 'data', sort: 'desc' }];

export const ExcepcioGrid = () => {
    const { t } = useTranslation();

    const { apiIsReady, handleOpen, dialog } = useExcepcioDetail();
    const actions = useMemo(
        () => [
            {
                label: t('page.excepcio.grid.detallTitol'),
                icon: 'info',
                showInMenu: false,
                onClick: handleOpen,
            },
        ],
        [apiIsReady]
    );

    return (
        <GridPage>
            <ScreenFrame icon={icons.excepcio} title={t('page.excepcio.grid.title')}>
                <StyledMuiGrid
                    resourceName={'excepcioLogResource'}
                    columns={columns}
                    sortModel={sortModel}
                    perspectives={perspectives}
                    rowAdditionalActions={actions}
                    toolbarShowQuickFilter
                    readOnly
                />
                {dialog}
            </ScreenFrame>
        </GridPage>
    );
};
