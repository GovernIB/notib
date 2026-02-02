import React from 'react';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import { GridPage, MuiDataGrid, MuiActionReportButton, useBaseAppContext } from 'reactlib';
import { Typography } from '@mui/material';

const columns = [
    {
        field: 'codi',
        flex: 2,
    },
    {
        field: 'nom',
        flex: 6,
    },
    {
        field: 'codiPare',
        flex: 2,
    },
    {
        field: 'nomPare',
        flex: 6,
    },
    {
        field: 'llibre',
        flex: 4,
    },
    {
        field: 'estat',
        flex: 2,
    },
    {
        field: 'entregaCieActiva',
        flex: 2,
    },
    {
        field: 'permetreSir',
        flex: 2,
    },
];

const OrganGridDir3SyncActionForm: React.FC<{ setReal: (value: boolean) => void }> = (props) => {
    const { setReal } = props;
    React.useEffect(() => {
        setReal(false);
    }, []);
    return (
        <Grid container>
            <Grid size={12}>
                <Typography>
                    Faci clic al botó de consultar els canvis per a previsualitzar els canvis
                    pendents d'aplicar.
                </Typography>
            </Grid>
        </Grid>
    );
};

const OrganGridDir3SyncActionResults: React.FC<{ result: any }> = (props) => {
    const { result } = props;
    return (
        <Grid container>
            <Grid size={12}>
                <p>Substitucions: {result.numSubstitucions}</p>
                <p>Divisions: {result.numDivisions}</p>
                <p>Fusions: {result.numFusions}</p>
                <p>Extincions: {result.numExtincions}</p>
            </Grid>
            <Grid size={12}>
                <Typography>Faci clic al botó d'aplicar per a fer efectius els canvis.</Typography>
            </Grid>
        </Grid>
    );
};

const OrganGridDir3SyncActionButton: React.FC = () => {
    const { t } = useTranslation();
    const { temporalMessageShow } = useBaseAppContext();
    const [real, setReal] = React.useState<boolean>();
    const resultProcessor = (result: any) => {
        if (result.simulat) {
            setReal(true);
            return <OrganGridDir3SyncActionResults result={result} />;
        } else {
            setReal(false);
        }
    };
    const handleSuccess = (result?: any) => {
        if (!result.simulat) {
            temporalMessageShow(null, t('page.organs.grid.sync.success'), 'success');
        }
    };
    const formDialogButtons = [
        {
            value: false,
            text: t('page.organs.grid.sync.dialogButton.cancel'),
            componentProps: { variant: 'outlined' },
        },
        {
            value: true,
            text: real
                ? t('page.organs.grid.sync.dialogButton.apply')
                : t('page.organs.grid.sync.dialogButton.query'),
            icon: real ? 'check' : 'search',
            componentProps: { variant: 'contained', disabled: false },
        },
    ];
    return (
        <MuiActionReportButton
            resourceName="organGestorResource"
            action="DIR3_SYNC"
            title={t('page.organs.grid.sync.title')}
            icon="sync"
            formAdditionalData={{ real }}
            formDialogTitle={t('page.organs.grid.sync.dialogTitle')}
            formDialogButtons={formDialogButtons}
            formDialogContent={<OrganGridDir3SyncActionForm setReal={setReal} />}
            formDialogResultProcessor={resultProcessor}
            buttonComponentProps={{ variant: 'contained' }}
            onSuccess={handleSuccess}
        />
    );
};

export const OrganGrid = () => {
    const { t } = useTranslation();
    return (
        <GridPage disableMargins={false}>
            <MuiDataGrid
                title={t('page.organs.grid.title')}
                resourceName="organGestorResource"
                columns={columns}
                paginationActive
                toolbarCreateLink="form"
                rowLink="form/{{id}}"
                rowUpdateLink="form/{{id}}"
                toolbarElementsWithPositions={[
                    {
                        position: 2,
                        element: <OrganGridDir3SyncActionButton />,
                    },
                ]}
            />
        </GridPage>
    );
};

export default OrganGrid;
