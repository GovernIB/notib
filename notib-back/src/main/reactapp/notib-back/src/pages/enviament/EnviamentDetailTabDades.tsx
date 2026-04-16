import { Box, Icon, Typography } from '@mui/material';
import React from 'react';
import { FieldsDataCard } from '../../components/DataCard';
import { useTranslation } from 'react-i18next';

const EnviamentDetailTabDades: React.FC<{
    enviament: any;
    apiCurrentFields: any[] | undefined;
}> = (props) => {
    const { enviament, apiCurrentFields } = props;
    const { t } = useTranslation();

    // const serveiTipusField = apiCurrentFields?.find((f) => f?.name === 'serveiTipus');
    //const estatField = apiCurrentFields?.find((f) => f.name === 'estat');

    return (
        <Box sx={{ height: '100%', overflowY: 'auto', minHeight: 0 }}>
            <FieldsDataCard
                title={t('page.enviament.detail.tab.dades.enviaments')}
                rows={[
                    {
                        field: 'referenciaNotificacio',
                        alwaysVisible: true,
                    },
                    {
                        field: 'referenciaEnviament',
                    },
                    {
                        field: 'dehNif',
                        alwaysVisible: true,
                    },
                    {
                        field: 'dehProcedimentCodi',
                        alwaysVisible: true,
                    },
                    {
                        field: 'dehObligat',
                        valueRenderer: (_value: any, formattedValue: string) => {
                            return formattedValue;
                        },
                        alwaysVisible: true,
                    },
                    {
                        field: 'serveiTipus',
                        alwaysVisible: true,
                    },
                    {
                        field: 'notificaEstat',
                        alwaysVisible: true,
                        valueRenderer: (_value: any, formattedValue: string) => {
                            // const getIcon = () => {
                            //     switch (_value) {
                            //         case 'NOTIB_PENDENT':
                            //             return '123';
                            //         case 'NOTIB_ENVIADA':
                            //             return '123';
                            //         case 'ABSENT':
                            //             return '123';
                            //         case 'ADRESA_INCORRECTA':
                            //             return '123';
                            //         case 'DESCONEGUT':
                            //             return '123';
                            //         case 'ENVIADA_CI':
                            //             return '123';
                            //         case 'ENVIADA_DEH':
                            //             return '123';
                            //         case 'ENVIAMENT_PROGRAMAT':
                            //             return '123';
                            //         case 'ENTREGADA_OP':
                            //             return '123';
                            //         case 'ERROR_ENTREGA':
                            //             return '123';
                            //         case 'EXPIRADA':
                            //             return '123';
                            //         case 'EXTRAVIADA':
                            //             return '123';
                            //         case 'MORT':
                            //             return '123';
                            //         case 'LLEGIDA':
                            //             return '123';
                            //         case 'NOTIFICADA':
                            //             return '123';
                            //         case 'PENDENT':
                            //             return '123';
                            //         case 'PENDENT_ENVIAMENT':
                            //             return '123';
                            //         case 'PENDENT_SEU':
                            //             return '123';
                            //         case 'PENDENT_CIE':
                            //             return '123';
                            //         case 'PENDENT_DEH':
                            //             return '123';
                            //         case 'REBUTJADA':
                            //             return '123';
                            //         case 'SENSE_INFORMACIO':
                            //             return '123';
                            //         case 'FINALITZADA':
                            //             return '123';
                            //         case 'ENVIADA':
                            //             return '123';
                            //         case 'REGISTRADA':
                            //             return '123';
                            //         case 'PROCESSADA':
                            //             return '123';
                            //         case 'ANULADA':
                            //             return '123';
                            //         case 'ENVIAT_SIR':
                            //             return '123';
                            //         case 'ENVIADA_AMB_ERRORS':
                            //             return '123';
                            //         case 'FINALITZADA_AMB_ERRORS':
                            //             return '123';
                            //     }
                            // };

                            return (
                                <>
                                    <Typography>
                                        <Icon
                                            sx={{
                                                position: 'relative',
                                                top: 2,
                                                mr: 0.6,
                                                fontSize: 16,
                                            }}
                                        >
                                            {/* TODO: S'ha de fer una funció per obtenir l'icon amb getIcon o obtenir-ho del back*/}
                                            rocket_launch
                                        </Icon>
                                        {formattedValue}
                                    </Typography>
                                </>
                            );
                        },
                    },
                ]}
                fields={apiCurrentFields}
                data={enviament}
                sx={{ mb: 1 }}
            />
            <FieldsDataCard
                title={t('page.enviament.detail.tab.dades.interessat')}
                rows={[
                    {
                        field: 'titularInfo.nif',
                        label: t('page.enviament.detail.tab.dades.titularInfo.nif'),
                        alwaysVisible: true,
                    },
                    {
                        field: 'titularInfo.nom',
                        label: t('page.enviament.detail.tab.dades.titularInfo.nom'),
                        alwaysVisible: true,
                    },
                    {
                        field: 'titularInfo.llinatges',
                        label: t('page.enviament.detail.tab.dades.titularInfo.llinatges'),
                    },
                    {
                        field: 'titularInfo.telefon',
                        label: t('page.enviament.detail.tab.dades.titularInfo.telefon'),
                    },
                    {
                        field: 'titularInfo.email',
                        label: t('page.enviament.detail.tab.dades.titularInfo.email'),
                    },
                ]}
                fields={apiCurrentFields}
                data={enviament}
                sx={{ mb: 1 }}
            />
        </Box>
    );
};

export default EnviamentDetailTabDades;
