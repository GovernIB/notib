import { Alert, Box, Button, Icon } from '@mui/material';
import React from 'react';
import { useTranslation } from 'react-i18next';
import { FieldsDataCard } from '../../components/DataCard';
import {MuiActionReportButton} from "reactlib";

const EnviamentDetailTabEntregaPostal: React.FC<{
    enviament: any;
    apiCurrentFields: any[] | undefined;
    isRolActualAdministradorLectura?: boolean;
}> = (props) => {
    const { enviament, apiCurrentFields, isRolActualAdministradorLectura } = props;
    const { t } = useTranslation();

    const isPendent = !enviament?.entregaPostalInfo?.cieEstat;
    const isErroni =
        enviament?.entregaPostalInfo?.cieEstat === false && !enviament?.entregaPostalInfo?.cieId;

    return (
        <Box sx={{ height: '100%', overflowY: 'auto', minHeight: 0 }}>
            {isPendent ? (
                <Alert severity="warning" sx={{ mb: 1, mt: 2 }}>
                    {t('page.enviament.detail.tab.entregaPostal.cieEstatPendent')}
                </Alert>
            ) : (
                <>
                    {isErroni && (
                        <Alert severity="warning" sx={{ mb: 1, mt: 2 }}>
                            {t('page.enviament.detail.tab.entregaPostal.cieErroni')}
                        </Alert>
                    )}
                    {!isRolActualAdministradorLectura && (
                        <Box sx={{ display: 'flex', justifyContent: 'flex-end', gap: 1 }}>
                            {enviament?.entregaPostalInfo?.cieEstat == 'ENVIADO_CI' && (
                                <Button
                                    id="cancelarEntregaPostal"
                                    startIcon={<Icon>close</Icon>}
                                    variant="outlined"
                                    sx={{ textTransform: 'none' }}
                                >
                                    {t('page.enviament.detail.tab.entregaPostal.cancelarEntregaPostal')}
                                </Button>
                            )}
                            <Button
                                id="consultaEstatEntregaPostal"
                                startIcon={<Icon>refresh</Icon>}
                                variant="outlined"
                                sx={{ textTransform: 'none' }}
                            >
                                {t('page.enviament.detail.tab.entregaPostal.refrescarEstat')}
                            </Button>
                        </Box>
                    )}
                    <FieldsDataCard
                        title={t('page.enviament.detail.tab.entregaPostal.title')}
                        rows={[
                            {
                                field: 'entregaPostalInfo.cieEstat',
                                alwaysVisible: true,
                                label: t('page.enviament.detail.tab.entregaPostal.cieEstat'),
                            },
                            {
                                field: 'entregaPostalInfo.cieEstatData',
                                label: t('page.enviament.detail.tab.entregaPostal.cieEstatData'),
                            },
                            {
                                field: 'entregaPostalInfo.cieId',
                                label: t('page.enviament.detail.tab.entregaPostal.cieId'),
                            },
                            {
                               field: 'entregaPostalInfo.cieDatatErrorDescripcio',
                                label: t('page.enviament.detail.tab.entregaPostal.cieDatatErrorDescripcio'),
                            },
                            {
                                field: 'entregaPostalInfo.cieDatatOrigen',
                                label: t('page.enviament.detail.tab.entregaPostal.cieDatatOrigen'),
                            },
                            {
                                field: 'entregaPostalInfo.cieDatatReceptorNif',
                                label: t(
                                    'page.enviament.detail.tab.entregaPostal.cieDatatReceptorNif'
                                ),
                            },
                            {
                                field: 'entregaPostalInfo.cieDatatReceptorNom',
                                label: t('page.enviament.detail.tab.entregaPostal.cieDatatReceptorNom'),
                            },
                            {
                                field: 'entregaPostalInfo.cieDatatNumSeguiment',
                                label: t('page.enviament.detail.tab.entregaPostal.cieDatatNumSeguiment'),
                            },
                        ]}
                        fields={apiCurrentFields}
                        data={enviament}
                        sx={{ mb: 1 }}
                    />
                    {enviament?.entregaPostalInfo?.cieCertificacioData && (
                        <FieldsDataCard
                            title={t('page.enviament.detail.tab.entregaPostal.certificacio')}
                            rows={[
                                {
                                    field: 'entregaPostalInfo.cieCertificacioData',
                                    alwaysVisible: true,
                                    label: t('page.enviament.detail.tab.entregaPostal.cieCertificacioData'),
                                },
                                {
                                    field: 'entregaPostalInfo.cieCertificacioMime',
                                    label: t('page.enviament.detail.tab.entregaPostal.cieCertificacioMime'),
                                },
                                {
                                    field: 'entregaPostalInfo.cieCertificacioOrigen',
                                    label: t('page.enviament.detail.tab.entregaPostal.cieCertificacioOrigen'),
                                },
                                {
                                    field: 'entregaPostalInfo.cieCertificacioMetadades',
                                    label: t('page.enviament.detail.tab.entregaPostal.cieCertificacioMetadades'),
                                },
                                {
                                    field: 'entregaPostalInfo.cieCertificacioCsv',
                                    label: t('page.enviament.detail.tab.entregaPostal.cieCertificacioCsv'),
                                },
                                {
                                    field: 'entregaPostalInfo.cieCertificacioTipus',
                                    label: t('page.enviament.detail.tab.entregaPostal.cieCertificacioTipus'),
                                },
                                {
                                    field: 'entregaPostalInfo.cieCertificacioArxiuTipus',
                                    label: t('page.enviament.detail.tab.entregaPostal.cieCertificacioArxiuTipus'),
                                },
                                {
                                    field: 'entregaPostalInfo.cieCertificacioNumSeguiment',
                                    label: t('page.enviament.detail.tab.entregaPostal.cieCertificacioNumSeguiment'),
                                },
                                {
                                    field: 'entregaPostalInfo.cieCertificacioArxiuNom',
                                    label: t('page.enviament.detail.tab.entregaPostal.cieCertificacioArxiuNom'),
                                    valueRenderer: (_value: any, formattedValue: string) => {
                                        return (<Box display="flex" justifyContent="space-between">
                                                    {formattedValue}
                                                    <Box display="flex" justifyContent="flex-end">
                                                        <MuiActionReportButton
                                                            id={enviament.id}
                                                            resourceName={"notificacioEnviamentResource"}
                                                            report="DESCARREGAR_CIE_CERTIFICACIO"
                                                            reportFileType="CUSTOM"
                                                            title={t('page.enviament.detail.tab.entregaPostal.descarregarCertificacio')}
                                                            buttonComponentProps={{ variant: 'outlined', sx: { mr: 1 } }}
                                                            buttonIcon="download"/>
                                                    </Box>
                                                </Box>)
                                    }
                                },
                            ]}
                            fields={apiCurrentFields}
                            data={enviament}
                            sx={{ mb: 1 }}
                        />
                    )}
                </>
            )}
        </Box>
    );
};

export default EnviamentDetailTabEntregaPostal;
