import {Box, Divider, Icon, Typography} from '@mui/material';
import {FieldsDataCard} from '../../components/DataCard';
import React from 'react';
import {useTranslation} from 'react-i18next';
import {NotificacioEstatDetall} from './NotificacioEstatRender';
import {MuiActionReportButton} from "reactlib";
import {formatDate} from "../../utils/dateUtils.ts";

interface PropsTabDades {
    notificacio: any;
    apiCurrentFields: any[] | undefined;
}

const TableGrup: React.FC<PropsTabDades> = (props) => {
    const { notificacio, apiCurrentFields } = props;
    const { t } = useTranslation();

    if (!notificacio?.grupInfo) return null;

    return (
        <FieldsDataCard
            title={t('page.notificacio.detail.dades.grup.title')}
            rows={[
                {
                    field: 'grupInfo.codi',
                    label: t('page.notificacio.detail.dades.grup.codi'),
                },
                {
                    field: 'grupInfo.nom',
                    label: t('page.notificacio.detail.dades.grup.nom'),
                },
            ]}
            fields={apiCurrentFields}
            data={notificacio}
            sx={{ mb: 1 }}
        />
    );
};

const TableDocuments: React.FC<PropsTabDades> = (props) => {
    const { apiCurrentFields, notificacio } = props;
    const { t } = useTranslation();
    const documentsInfo = notificacio?.documentsInfo;

    if (!documentsInfo) return null;

    return (
        <>
            {documentsInfo?.map((document: any, index: number) => (
                <FieldsDataCard
                    key={`docment-${index + 1}`}
                    id={`docment-${index + 1}`}
                    title={t('page.notificacio.detail.dades.documents.title') + ' ' + (index + 1)}
                    rows={[
                        {
                            field: 'arxiuNom',
                            label: t('page.notificacio.detail.dades.documents.nom'),
                            valueRenderer: () => {
                                return (
                                    <Box display="flex" justifyContent="space-between">
                                        {document.arxiuNom}
                                        <Box sx={{ textAlign: 'right' }}>
                                            <MuiActionReportButton
                                                id={notificacio?.id}
                                                resourceName={"notificacioResource"}
                                                report="DESCARREGAR_DOCUMENT_ENVIAT"
                                                reportFileType="CUSTOM"
                                                title={t('page.notificacio.detail.dades.documents.descarregarDocument')}
                                                buttonComponentProps={{ variant: 'outlined', sx: { mr: 1 } }}
                                                buttonIcon="file_download"
                                                formAdditionalData={{docId: document.id}}/>
                                        </Box>
                                    </Box>
                                )
                            }
                        },
                        {
                            field: 'normalitzat',
                            label: t('page.notificacio.detail.dades.documents.normalitzat'),
                            valueRenderer: (_value: any, formattedValue: string) => {
                                return formattedValue ? 'Si' : 'No';
                            },
                        },
                        {
                            field: 'csv',
                            label: t('page.notificacio.detail.dades.documents.csv'),
                            valueRenderer: (_value: any, formattedValue: string) => {
                                return formattedValue ? 'Si' : 'No';
                            },
                        },
                    ]}
                    fields={apiCurrentFields}
                    data={document}
                    sx={{ mb: 1 }}
                />
            ))}
        </>
    );
};

const TableOperadorPostal: React.FC<PropsTabDades> = (props) => {
    const { apiCurrentFields, notificacio } = props;
    const { t } = useTranslation();

    // TODO: Revisar es nom des camp, ha de ser es mateix que abaix
    if (!notificacio?.operadorPostal?.organismePagadorCodi) return null;

    return (
        <FieldsDataCard
            title={t('page.notificacio.detail.dades.pagadorPostal.title')}
            rows={[
                {
                    field: 'operadorPostalInfo.organismePagadorNom', // TODO
                    label: t('page.notificacio.detail.dades.pagadorPostal.organismePagadorNom'),
                },
                {
                    field: 'operadorPostalInfo.contracteNum',
                    label: t('page.notificacio.detail.dades.pagadorPostal.contracteNum'),
                },
                {
                    field: 'operadorPostalInfo.facturacioClientCodi',
                    label: t('page.notificacio.detail.dades.pagadorPostal.facturacioClientCodi'),
                },
                {
                    field: 'operadorPostalInfo.contracteDataVig',
                    label: t('page.notificacio.detail.dades.pagadorPostal.contracteDataVig'),
                },
            ]}
            fields={apiCurrentFields}
            data={notificacio}
            sx={{ mb: 1 }}
        />
    );
};

const TableCie: React.FC<PropsTabDades> = (props) => {
    const { apiCurrentFields, notificacio } = props;
    const { t } = useTranslation();

    // TODO revisar es valor d'aqui, ha de ser igual que abaix
    if (!notificacio?.operadorCieInfo.organismePagadorCodi) return null;

    return (
        <FieldsDataCard
            title={t('page.notificacio.detail.dades.pagadorCie.title')}
            rows={[
                {
                    field: '${notificacio.cie.organismePagadorCodi} - ${notificacio.cie.organismePagadorNom', // TODO
                    label: t('page.notificacio.detail.dades.pagadorCie.organismeEmissor'),
                },
                {
                    field: 'operadorCieInfo.contracteDataVig',
                    label: t('page.notificacio.detail.dades.pagadorCie.vigencia'),
                },
            ]}
            fields={apiCurrentFields}
            data={notificacio}
            sx={{ mb: 1 }}
        />
    );
};

const TableEnviaments: React.FC<PropsTabDades> = (props) => {
    const { notificacio, apiCurrentFields } = props;
    const { t } = useTranslation();
    const enviamentsInfo = notificacio?.enviamentsInfo;

    if (!enviamentsInfo) return null;

    return (
        <>
            {enviamentsInfo?.map((enviament: any, index: number) => (
                <FieldsDataCard
                    key={`enviament-${index + 1}`}
                    title={`${t('page.notificacio.detail.dades.enviaments.title')} ${index + 1}`}
                    rows={[
                        {
                            field: 'titularInfo.nom',
                            label: t('page.notificacio.detail.dades.enviaments.interessat'),
                            valueRenderer: () => {
                                const { nom, llinatge1, llinatge2, nif, email } =
                                    enviament.titularInfo;
                                const nomComplet = [nom, llinatge1, llinatge2]
                                    .filter(Boolean) // Elimina null, undefined o strings buits
                                    .join(' ');

                                const identitatAmbNif = nif
                                    ? `${nomComplet} (${nif})`.trim()
                                    : nomComplet;

                                return (
                                    <Box sx={{display: 'flex', alignItems: 'center', flexWrap: 'wrap', gap: 0.5,}}>
                                        <Typography>{identitatAmbNif}</Typography>
                                        {identitatAmbNif && email && (
                                            <Typography sx={{ mx: 0.5 }}>-</Typography>
                                        )}
                                        {email && (
                                            <Box sx={{display: 'flex', alignItems: 'center', gap: 0.5,}}>
                                                <Icon sx={{fontSize: '1.2rem', color: 'action.active',}}>email</Icon>
                                                <Typography>{email}</Typography>
                                            </Box>
                                        )}
                                    </Box>
                                );
                            },
                        },
                        {
                            field: 'enviament.destinataris',
                            label: t('page.notificacio.detail.dades.enviaments.destinataris'),
                            valueRenderer: () => {
                                if (!enviament?.representantsInfo || !Array.isArray(enviament?.representantsInfo) || enviament?.representantsInfo?.length === 0) {
                                    return (<Typography sx={{ fontStyle: 'italic' }}>{t('page.notificacio.detail.dades.enviaments.senseDestinataris')}</Typography>);
                                }

                                return (
                                    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 0.5 }}>
                                        {enviament?.representantsInfo.map(
                                            (destinatari: any, index: number) => {
                                                const { nom, llinatge1, llinatge2, nif } =
                                                    destinatari;

                                                const nomComplet = [nom, llinatge1, llinatge2]
                                                    .filter(Boolean)
                                                    .join(' ');
                                                const identitat = nif
                                                    ? `${nomComplet} (${nif})`.trim()
                                                    : nomComplet;

                                                return (<Typography key={index} variant="body2" sx={{ display: 'block' }}>{identitat}</Typography>);
                                            }
                                        )}
                                        <Divider sx={{ my: 0.5 }} />
                                        <Typography variant="caption" sx={{fontWeight: 'bold', color: 'primary.main', textAlign: 'right',}}>
                                            {`Total: ${enviament?.representantsInfo?.length}`}
                                        </Typography>
                                    </Box>
                                );
                            },
                        },
                        {
                            field: 'notificaEstat', // TODO: Revisar back
                            // Revisar condicions JSP Linia 775 a 807 notificacioInfo.jsp
                        },
                        {
                            field: 'registre',
                            label: t('page.notificacio.detail.dades.enviaments.registre.title'),
                            valueRenderer: () => {
                                return enviament?.registreNumeroFormatat ? (
                                    <FieldsDataCard
                                        rows={[
                                            {
                                                field: 'registreNumeroFormatat',
                                                label: t('page.notificacio.detail.dades.enviaments.registre.registreNumeroFormatat')
                                            },
                                            {
                                                field: 'registreData',
                                                label: t('page.notificacio.detail.dades.enviaments.registre.registreData')
                                            },
                                            {
                                                field: 'registreEstat',
                                                label: t('page.notificacio.detail.dades.enviaments.registre.registreEstat')
                                            },
                                            {
                                                field: 'sirRecepcioData',
                                                label: t('page.notificacio.detail.dades.enviaments.registre.sirRecepcioData')
                                            },
                                            {
                                                field: 'registreMotiu',
                                                label: t('page.notificacio.detail.dades.enviaments.registre.registreMotiu')
                                            },
                                            {
                                                field: 'sirRegDestiData',
                                                label: t('page.notificacio.detail.dades.enviaments.registre.sirRegDestiData')
                                            },
                                            //${(isRolActualAdministradorEntitat || isRolActualAdministradorOrgan)
                                            // && (not empty notificacio.registreOficinaNom || not empty notificacio.registreLlibreNom)}
                                            {
                                                field: 'registreOficinaNom', // TODO
                                                label: t('page.notificacio.detail.dades.enviaments.registre.registreOficinaNom')
                                            },
                                            {
                                                field: 'registreLlibreNom', // TODO
                                                label: t('page.notificacio.detail.dades.enviaments.registre.registreLlibreNom')
                                            },
                                        ]}
                                        fields={apiCurrentFields}
                                        data={enviament}
                                        sx={{ mb: 1 }}
                                    /> // TODO: s'ha de posar un boto de justificant despres de sa taula Linia 868 de notificacioInfo.jsp
                                ) : (<Typography>{t('page.notificacio.detail.dades.enviaments.registre.registreLlibreNom')}</Typography>);
                            },
                        },
                        {
                            field: 'certificacio',
                            label: t(
                                'page.notificacio.detail.dades.enviaments.registre.certificacio'
                            ),
                            valueRenderer: () => {
                                return enviament?.notificaCertificacioData ? (
                                    <FieldsDataCard
                                        rows={[
                                            {
                                                field: 'notificaCertificacioData',
                                                label: t('page.notificacio.detail.dades.enviaments.registre.notificaCertificacioData'),
                                                valueRenderer: (_value: any) => formatDate(_value)
                                            },
                                            {
                                                field: 'notificaCertificacioMime',
                                                label: t('page.notificacio.detail.dades.enviaments.registre.notificaCertificacioMime'),
                                            },
                                            {
                                                field: 'notificaCertificacioOrigen',
                                                label: t('page.notificacio.detail.dades.enviaments.registre.notificaCertificacioOrigen'),
                                            },
                                            {
                                                field: 'notificaCertificacioMetadades',
                                                label: t('page.notificacio.detail.dades.enviaments.registre.notificaCertificacioMetadades'),
                                            },
                                            {
                                                field: 'notificaCertificacioCsv',
                                                label: t('page.notificacio.detail.dades.enviaments.registre.notificaCertificacioCsv'),
                                            },
                                            {
                                                field: 'notificaCertificacioTipus',
                                                label: t('page.notificacio.detail.dades.enviaments.registre.notificaCertificacioTipus'),
                                            },
                                            {
                                                field: 'notificaCertificacioArxiuTipus',
                                                label: t('page.notificacio.detail.dades.enviaments.registre.notificaCertificacioArxiuTipus'),
                                            },
                                            {
                                                field: 'notificaCertificacioNumSeguiment',
                                                label: t('page.notificacio.detail.dades.enviaments.registre.notificaCertificacioNumSeguiment'),
                                            },
                                            {
                                                label: t('page.notificacio.detail.dades.enviaments.registre.certificacioNom'),
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
                                            }
                                        ]}
                                        fields={apiCurrentFields}
                                        data={enviament}
                                        sx={{ mb: 1 }}
                                    />

                                ) : (<Typography>{t('page.notificacio.detail.dades.enviaments.registre.noCertificacio')}</Typography>);
                            },
                        },
                    ]}
                    fields={apiCurrentFields}
                    data={enviament}
                    sx={{ mb: 1 }}
                />
            ))}
        </>
    );
};

const NotificacioDetailDialogTabDades: React.FC<PropsTabDades> = (props) => {
    const { notificacio, apiCurrentFields } = props;
    const { t } = useTranslation();
    const procedimentTipusField = apiCurrentFields?.find((f) => f?.name === 'procedimentTipus');

    return (
        <Box sx={{ height: '100%', overflowY: 'auto', minHeight: 0 }}>
            <FieldsDataCard
                title={t('page.notificacio.detail.dades.title')}
                rows={[
                    {
                        field: 'organGestor',
                    },
                    {
                        field: 'procediment',
                        labelRenderer: () =>
                            procedimentTipusField.options[notificacio.procedimentTipus],
                    },
                    {
                        field: 'numExpedient',
                    },
                    {
                        field: 'concepte',
                    },
                    {
                        field: 'descripcio',
                    },
                    {
                        field: 'idioma',
                    },
                    {
                        field: 'createdDate',
                    },
                    {
                        field: 'createdBy', // TODO
                        valueRenderer: () => {
                            return (
                                <>
                                    {notificacio.tipusUsuari === 'INTERFICIE_WEB' ? (
                                        <Typography>{`${notificacio?.createdBy?.nom} (${notificacio?.createdBy?.codi}`}</Typography>
                                    ) : (
                                        <>
                                            <Typography>
                                                {` Aplicacio: ${notificacio?.createdBy?.nom} (${notificacio?.createdBy?.codi}`}
                                            </Typography>
                                            <Typography>
                                                {` Usuari: ${notificacio?.usuariNom} (${notificacio?.usuariCodi}`}
                                            </Typography>
                                        </>
                                    )}
                                </>
                            );
                        },
                    },
                    {
                        field: 'enviadaDate',
                    },
                    {
                        field: 'enviamentDataProgramada',
                    },
                    {
                        field: 'estatDate',
                    },
                    {
                        field: 'estatProcessatDate',
                    },
                    {
                        field: 'caducitat',
                        formatOptions: { noTime: true },
                    },
                    {
                        field: 'caducitatOriginal',
                    },
                    {
                        field: 'retard',
                    },
                    {
                        field: 'estat',
                        valueRenderer: () => {
                            return <NotificacioEstatDetall notificacio={notificacio} />;
                        },
                    },
                ]}
                fields={apiCurrentFields}
                data={notificacio}
                sx={{ mb: 1 }}
            />

            {(notificacio?.justificantCreat && (!notificacio?.hasEnviamentsPendents || notificacio?.estat == 'FINALITZADA_AMB_ERRORS')) &&
                <Box sx={{ textAlign: 'right' }}>
                    <MuiActionReportButton
                        id={notificacio?.id}
                        resourceName={"notificacioResource"}
                        report="DESCARREGAR_JUSTIFICANT_ENVIAMENT_NOTIFICACIO"
                        reportFileType="CUSTOM"
                        title={t('page.notificacio.detail.dades.justificant')}
                        buttonComponentProps={{ variant: 'outlined', sx: { mr: 1 } }}
                        buttonIcon="file_download"/>
                </Box>
            }
            <TableGrup notificacio={notificacio} apiCurrentFields={apiCurrentFields} />
            <TableDocuments apiCurrentFields={apiCurrentFields} notificacio={notificacio} />
            {notificacio?.operadorPostalInfo && (
                <>
                    <TableOperadorPostal apiCurrentFields={apiCurrentFields} notificacio={notificacio}/>
                    <TableCie apiCurrentFields={apiCurrentFields} notificacio={notificacio} />
                </>
            )}
            <TableEnviaments apiCurrentFields={apiCurrentFields} notificacio={notificacio}/>
        </Box>
    );
};

export default NotificacioDetailDialogTabDades;
