import { Alert, Divider, List, ListItem, ListItemText } from '@mui/material';
import React from 'react';
import { useTranslation } from 'react-i18next';
import { MuiActionReportButton } from 'reactlib';

interface AccioConfig extends Record<string, any> {
    visible?: boolean;
    title: string;
    titleButton: string;
    resourceName: string;
}

const NotificacioDetailDialogTabAccions: React.FC<{ notificacio: any }> = (props) => {
    const { notificacio } = props;
    const { t } = useTranslation();

    const accionsVisibles = React.useMemo(() => {
        const llistaAccions: AccioConfig[] = [
            {
                // visible: notificacio.tipusUsuari == 'APLICACIO' && (notificacio.errorLastCallback || notificacio.eventsCallbackPendent),
                id: notificacio?.id,
                title: t('Envia canvi estat al client'),
                titleButton: t(''),
                resourceName: 'notificacioResource',
                buttonIcon: 'send',
                action: 'ENVIAR_CALLBACK'
            },
            // {
            //     // visible: notificacio.errorEntregaPostal == true and notificacio.notificacioAntiga == false,
            //     // TODO: errorEntregaPostal i notificacioAntiga no existeix
            //     title: t('Reenviar la entrega postal'),
            //     titleButton: t('Reenvia'),
            //     resourceName: 'notificacioResource',
            //     buttonIcon: 'send',
            // },
            // {
            //     visible: notificacio?.estat == 'PENDENT',
            //     title: t('Registra notificació pendent'),
            //     titleButton: t('Registra'),
            //     resourceName: 'notificacioResource',
            //     buttonIcon: 'send',
            // },
            // {
            //     visible: notificacio?.estat == 'REGISTRADA',
            //     title: t('Envia notificació registrada'),
            //     titleButton: t('Envia'),
            //     resourceName: 'notificacioResource',
            //     buttonIcon: 'send',
            // },
            // {
            //     visible: true,
            //     title: t('page.enviament.detail.tab.notifica.refrescar'),
            //     titleButton: t('Refrescar estat'),
            //     resourceName: 'notificacioEnviamentResource',
            //     buttonIcon: 'refresh',
            //     action: 'REFRESCAR_ESTAT_NOTIFICA',
            //     id: notificacio?.id,
            // },
            // {
            //     // visible: notificacio.estat == 'ENVIADA' && notificacio.notificaErrorTipus == 'ERROR_REINTENTS_CONSULTA',
            //     // TODO: notificaErrorTipus no existeix
            //     title: t("Reactiva consulta d'estat"),
            //     titleButton: t('Reactiva'),
            //     resourceName: 'notificacioResource',
            //     buttonIcon: 'play_arrow',
            // },
            // {
            //     // visible: notificacio.estat == 'ENVIAT_SIR' && notificacio.fiReintents,
            //     // TODO: fiReintents no existeix
            //     title: t('Reactiva consulta estat SIR'),
            //     titleButton: t('Reactiva'),
            //     resourceName: 'notificacioResource',
            //     buttonIcon: 'play_arrow',
            // },
            // {
            //     visible:
            //         (notificacio?.estat == 'ENVIADA_AMB_ERRORS' ||
            //             notificacio?.estat == 'FINALITZADA_AMB_ERRORS') &&
            //         !notificacio?.justificantCreat,
            //     title: t('Reactiva enviaments amb error'),
            //     titleButton: t('Reactiva'),
            //     resourceName: 'notificacioResource',
            //     buttonIcon: 'refresh',
            // },
            // {
            //     visible:
            //         (notificacio?.estat == 'ENVIADA_AMB_ERRORS' ||
            //             notificacio?.estat == 'FINALITZADA_AMB_ERRORS') &&
            //         !notificacio?.justificantCreat,
            //     title: t('Reenvia enviaments amb error'),
            //     titleButton: t('Reenvia'),
            //     resourceName: 'notificacioResource',
            //     buttonIcon: 'refresh',
            // },
        ];
        return llistaAccions.filter((accio) => accio.visible !== false);
    }, [notificacio, t]);

    return (
        <List>
            {accionsVisibles.length > 0 ? (
                accionsVisibles.map((accio, index) => {
                    const { title, titleButton, resourceName, ...buttonProps } = accio;
                    return (
                        <React.Fragment key={buttonProps.action || buttonProps.report || index}>
                            <ListItem
                                secondaryAction={
                                    <MuiActionReportButton
                                        title={titleButton || title}
                                        resourceName={resourceName}
                                        buttonComponentProps={{
                                            variant: 'outlined',
                                            sx: { mr: 1, textTransform: 'none' },
                                        }}
                                        {...buttonProps}
                                    />
                                }
                            >
                                <ListItemText primary={title} />
                            </ListItem>
                            <Divider />
                        </React.Fragment>
                    );
                })
            ) : (
                <Alert severity="info">{t('page.notificacio.detail.accions.noAccions')}</Alert>
            )}
        </List>
    );
};

export default NotificacioDetailDialogTabAccions;
