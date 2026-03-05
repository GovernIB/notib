import React from 'react';
import { useTranslation } from 'react-i18next';
import { GridPage, MuiDataGrid, useResourceApiService } from 'reactlib';

const columns = [
    {
        field: 'data',
        flex: 2,
    },
    {
        field: 'descripcio',
        flex: 3,
    },
    {
        field: 'aplicacio',
        flex: 2,
    },
    {
        field: 'notificacioId',
        flex: 2,
    },
    {
        field: 'tipus',
        flex: 1,
    },
    {
        field: 'codiEntitat',
        flex: 1,
    },
    {
        field: 'tempsResposta',
        flex: 1,
    },
    {
        field: 'estat',
        flex: 1,
    },
];

export const MonitorIntegracioGrid = () => {
    const { t } = useTranslation();
    const {
        isReady: apiIsReady,
        artifactReport: apiArtifactReport,
        currentFields: apiCurrentFields,
    } = useResourceApiService('monitorIntegracioResource');
    React.useEffect(() => {
        if (apiIsReady) {
            apiArtifactReport(null, { code: 'AGRUPACIONS' }).then((response) => {
                console.log('>>> report', response);
            });
        }
    }, [apiIsReady]);
    React.useEffect(() => {
        const codiField = apiCurrentFields?.find((f) => f.name === 'codi');
        console.log('>>> codiField', codiField);
    }, [apiCurrentFields]);
    return (
        <GridPage disableMargins={false}>
            <MuiDataGrid
                title={t('page.integracio.grid.title')}
                resourceName="monitorIntegracioResource"
                columns={columns}
                paginationActive
                toolbarBulkDelete
                rowLink="detail/{{id}}"
            />
        </GridPage>
    );
};

export default MonitorIntegracioGrid;
