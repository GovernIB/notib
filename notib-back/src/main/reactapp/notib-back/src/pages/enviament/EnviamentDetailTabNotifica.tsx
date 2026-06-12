import {Alert, Box, Button, Icon, Typography} from '@mui/material';
import React from 'react';
import { FieldsDataCard } from '../../components/DataCard';
import { useTranslation } from 'react-i18next';
import {MuiActionReportButton, useBaseAppContext} from 'reactlib';

const EnviamentDetailTabNotifica: React.FC<{
    enviament: any;
    apiCurrentFields: any[] | undefined;
    isRolActualAdministradorLectura?: boolean;
}> = (props) => {
    const { enviament, apiCurrentFields, isRolActualAdministradorLectura } = props;
    const { t } = useTranslation();
    const { temporalMessageShow } = useBaseAppContext();

    const renderAlertEstat = () => {
        if (enviament?.notificaEstat === 'PENDENT') {
            return (
                <Alert severity="warning" sx={{ mb: 1, mt: 2 }}>
                    {enviament?.perEmail
                        ? t('page.enviament.detail.tab.notifica.noEnviat')
                        : t('page.enviament.detail.tab.notifica.notificacioNoEnviat')}
                </Alert>
            );
        }
    };
    const isAlertVisible = Boolean(renderAlertEstat);
    const renderContingutRefrescar = () => {

        // Cas NO PENDENT
        const isCasEspecialSir =
            (enviament?.tipusEnviament === 'COMUNICACIO' || enviament?.tipusEnviament === 'SIR') &&
            enviament?.titularInfo?.interessatTipus === 'ADMINISTRACIO';
        const potRefrescar = !isRolActualAdministradorLectura;
        // Si és el cas de SIR i pot refrescar
        if (isCasEspecialSir && potRefrescar) {
            return (
                <Box display="flex" justifyContent="flex-end">
                    <MuiActionReportButton
                        resourceName="notificacioEnviamentResource"
                        action="REFRESCAR_ESTAT_SIR"
                        id={enviament?.id}
                        title={t('page.enviament.detail.tab.notifica.refrescarEstat.titleButton')}
                        buttonComponentProps={{ variant: 'outlined', sx: { mr: 1 } }}
                        buttonIcon="refresh"
                        onSuccess={resposta => {
                            const msg = resposta?.ok ? "success" : "error";
                            temporalMessageShow(null, t('page.enviament.detail.tab.notifica.refrescarEstat.' + msg), msg);
                        }}
                        onError={error => temporalMessageShow(null, error?.message, "error")}
                    />
                </Box>
            );
        }

        // Cas general: Botó de refrescar estàndard

        if (potRefrescar && !isAlertVisible) {
            return (
                <Box display="flex" justifyContent="flex-end">
                    <MuiActionReportButton
                        resourceName="notificacioEnviamentResource"
                        action="REFRESCAR_ESTAT_NOTIFICA"
                        id={enviament?.id}
                        title={t('page.enviament.detail.tab.notifica.refrescarEstat.titleButton')}
                        buttonComponentProps={{ variant: 'outlined', sx: { mr: 1 } }}
                        buttonIcon="refresh"
                        onSuccess={resposta => {
                            const msg = resposta?.ok ? "success" : "error";
                            temporalMessageShow(null, t('page.enviament.detail.tab.notifica.refrescarEstat.' + msg), msg);
                        }}
                        onError={error => temporalMessageShow(null, error?.message, "error")}
                    />
                </Box>
            );
        }
    };

    return (
        <Box sx={{ height: '100%', overflowY: 'auto', minHeight: 0 }}>
            {renderContingutRefrescar()}
            {renderAlertEstat()}
            {!isAlertVisible && <FieldsDataCard
                title={t('page.enviament.detail.tab.notifica.datat')}
                rows={[
                    {
                        field: 'notificaEstat',
                        alwaysVisible: true,
                    },
                    {
                        field: 'notificaDataCaducitat',
                        alwaysVisible: true,
                    },
                    {
                        field: 'plazoAmpliado',
                        valueRenderer: (_value: any, formattedValue: string) => {
                            return _value === true ? formattedValue : undefined;
                        },
                    },
                    {
                        field: 'sirRecepcioData',
                    },
                    {
                        field: 'sirRegDestiData',
                    },
                    {
                        field: 'notificaEstatData',
                    },
                    {
                        field: 'notificaReferencia',
                    },
                    // {
                    //     field: '',
                    //     //enviament.notificaDatatErrorDescripcio
                    // },
                    {
                        field: 'notificaDatatOrigen',
                    },
                    {
                        field: 'notificaDatatReceptorNif',
                    },
                    {
                        field: 'notificaDatatReceptorNom',
                    },
                    {
                        field: 'notificaDatatNumSeguiment',
                    },
                    {
                        field: 'notificaDatatErrorDescripcio',
                    },
                ]}
                fields={apiCurrentFields}
                data={enviament}
                sx={{ mb: 1 }}
            />}
            {enviament?.notificaCertificacioData && (
                <FieldsDataCard
                    title={t('page.enviament.detail.tab.notifica.certificacio')}
                    rows={[
                        {
                            field: 'notificaCertificacioData',
                            alwaysVisible: true,
                        },
                        {
                            field: 'notificaCertificacioMime',
                        },

                        {
                            field: 'notificaCertificacioOrigen',
                        },

                        {
                            field: 'notificaCertificacioMetadades',
                        },

                        {
                            field: 'notificaCertificacioCsv',
                        },

                        {
                            field: 'notificaCertificacioTipus',
                        },

                        {
                            field: 'notificaCertificacioArxiuTipus',
                        },

                        {
                            field: 'notificaCertificacioNumSeguiment',
                        },

                        {
                            field: 'notificaCertificacioArxiuNom',
                            valueRenderer: (_value: any) => {
                                return (
                                    <Box sx={{display: 'flex', alignItems: 'center', flexDirection: 'row', justifyContent: 'space-between', width: '100%'}}>
                                        <Typography>{'certifciacio_' + enviament?.notificaReferencia + ".pdf"}</Typography>
                                        <MuiActionReportButton
                                            id={enviament?.id}
                                            resourceName={"notificacioEnviamentResource"}
                                            report="DESCARREGAR_CERTIFICACIO_ENVIAMENT"
                                            reportFileType="CUSTOM"
                                            title={t('page.notificacio.detail.dades.enviaments.registre.certificacio')}
                                            buttonComponentProps={{ variant: 'outlined', sx: { mr: 1 } }}
                                            buttonIcon="file_download"/>
                                    </Box>
                                );
                            }
                        },
                    ]}
                    fields={apiCurrentFields}
                    data={enviament}
                    sx={{ mb: 1 }}
                />
            )}
        </Box>
    );
};

export default EnviamentDetailTabNotifica;
