import { Alert, Divider, List, ListItem, ListItemText } from '@mui/material';
import React from 'react';
import { useTranslation } from 'react-i18next';
import {MuiActionReportButton, useBaseAppContext} from 'reactlib';
import {TemporalMessageSeverity} from "../../../lib/components/BaseAppContext.tsx";

interface AccioConfig extends Record<string, any> {
    visible?: boolean;
    title: string;
    titleButton: string;
    resourceName: string;
    onSucces?: (resposta) => void;
    onError?: (error) => void;
}

const NotificacioDetailDialogTabAccions: React.FC<{ notificacio: any }> = (props) => {
    const { notificacio } = props;
    const { t } = useTranslation();
    const { temporalMessageShow } = useBaseAppContext();
    const mostrarMissatgeRespostaAction = reposta => {
        let msg = t('page.notificacio.detail.accions.success');
        let severity : TemporalMessageSeverity = "success";
        if (!reposta.ok) {
            msg = t('page.notificacio.detail.accions.error');
            severity = 'error';
        }
        temporalMessageShow(null, msg, severity);
    }
    const accionsVisibles = React.useMemo(() => {
        const llistaAccions: AccioConfig[] = [
            {
                visible: notificacio?.tipusUsuari === 'APLICACIO' && (notificacio?.errorLastCallback || notificacio?.eventsCallbackPendent),
                id: notificacio?.id,
                title: t('page.notificacio.detail.accions.enviarCallback'),
                titleButton: t('page.notificacio.detail.accions.enviarCallback'),
                resourceName: 'notificacioResource',
                buttonIcon: 'send',
                action: 'ENVIAR_CALLBACK',
                onSuccess: mostrarMissatgeRespostaAction,
                onError: error => temporalMessageShow(null, error?.message, "error")
            },
            {
                visible: notificacio?.errorEntregaPostal && !notificacio?.notificacioAntiga,
                id: notificacio?.id,
                title: t('page.notificacio.detail.accions.enviarEntregaPostal'),
                titleButton: t('page.notificacio.detail.accions.enviarEntregaPostalButton'),
                resourceName: 'notificacioResource',
                buttonIcon: 'send',
                action: 'ENVIAR_ENTREGA_POSTAL',
                onSuccess: mostrarMissatgeRespostaAction,
                onError: error => temporalMessageShow(null, error?.message, "error")
            },
            {
                visible: notificacio?.estat === 'PENDENT',
                id: notificacio?.id,
                title: t('page.notificacio.detail.accions.registrar.title'),
                titleButton: t('page.notificacio.detail.accions.registrar.button'),
                resourceName: 'notificacioResource',
                buttonIcon: 'send',
                action: 'REGISTRAR_REMESA',
                onSuccess: resposta => {
                    if (!resposta) {
                        temporalMessageShow(null, t('page.notificacio.detail.accions.registrar.noReposta'), "error");
                        return;
                    }
                    let severity : TemporalMessageSeverity = "success";
                    let msg = "";
                    if (resposta.errors && resposta.errors.length > 0) {
                        msg += t('page.notificacio.detail.accions.registrar.respostesError');
                        resposta.errors.forEach(r => {
                            if (r.error) {
                                msg += r.descripcioResposta + ", ";
                            }
                        });
                        if (msg.length > 0) {
                            msg = msg.substring(0, msg.length -2);
                            severity = "error";
                        }
                    }
                    if (resposta.noExecutables && resposta.noExecutables.length > 0) {
                        severity = severity === "success" ? "warning" : severity;
                        msg = msg.length > 0 ? "\n" + msg : msg;
                        msg += t('page.notificacio.detail.accions.registrar.noExecutades');
                        resposta.noExecutables.forEach(r => msg += r + ", ");
                        msg = msg.substring(0, msg.length -2);
                    }
                    msg = !msg ? t('page.notificacio.detail.accions.registrar.ok') : msg;
                    temporalMessageShow(null, msg, severity);
                },
                onError: error => temporalMessageShow(null, error?.message, "error")
            },
            {
                visible: notificacio?.estat === 'REGISTRADA',
                id: notificacio?.id,
                title: t('page.notificacio.detail.accions.enviarNotifica.title'),
                titleButton: t('page.notificacio.detail.accions.enviarNotifica.button'),
                resourceName: 'notificacioResource',
                buttonIcon: 'send',
                action: 'ENVIAR_NOTIFICA',
                onSuccess: mostrarMissatgeRespostaAction,
                onError: error => temporalMessageShow(null, error.message, "error")
            },
            {
                visible: notificacio?.estat === 'ENVIADA' && notificacio?.notificaErrorTipus === 'ERROR_REINTENTS_CONSULTA',
                id: notificacio?.id,
                title: t('page.notificacio.detail.accions.reactivarEstatNotifica.title'),
                titleButton: t('page.notificacio.detail.accions.reactivarEstatNotifica.button'),
                resourceName: 'notificacioResource',
                buttonIcon: 'refresh',
                action: 'REACTIVAR_ESTAT_NOTIFICA',
                onSuccess: mostrarMissatgeRespostaAction,
                onError: error => temporalMessageShow(null, error.message, "error")
            },
            {
                visible: notificacio?.estat === 'ENVIAT_SIR' && notificacio?.fiReintents,
                id: notificacio?.id,
                title: t('page.notificacio.detail.accions.reactivarSir.title'),
                titleButton: t('page.notificacio.detail.accions.reactivarSir.button'),
                resourceName: 'notificacioResource',
                buttonIcon: 'play_arrow',
                action: 'REACTIVAR_CONSULTA_SIR',
                onSuccess: mostrarMissatgeRespostaAction,
                onError: error => temporalMessageShow(null, error.message, "error")
            },
            {
                visible: (notificacio?.estat === 'ENVIADA_AMB_ERRORS' || notificacio?.estat === 'FINALITZADA_AMB_ERRORS') && !notificacio.justificantCreat,
                id: notificacio?.id,
                title: t('page.notificacio.detail.accions.reactivarErrors.title'),
                titleButton: t('page.notificacio.detail.accions.reactivarErrors.button'),
                resourceName: 'notificacioResource',
                buttonIcon: 'play_arrow',
                action: 'REACTIVAR_AMB_ERRORS',
                onSuccess: mostrarMissatgeRespostaAction,
                onError: error => temporalMessageShow(null, error.message, "error")
            },
            {
                visible: (notificacio?.estat === 'ENVIADA_AMB_ERRORS' || notificacio?.estat === 'FINALITZADA_AMB_ERRORS') && !notificacio.justificantCreat,
                id: notificacio?.id,
                title: t('page.notificacio.detail.accions.reenviarErrors.title'),
                titleButton: t('page.notificacio.detail.accions.reenviarErrors.button'),
                resourceName: 'notificacioResource',
                buttonIcon: 'play_arrow',
                action: 'REENVIAR_AMB_ERRORS',
                onSuccess: mostrarMissatgeRespostaAction,
                onError: error => temporalMessageShow(null, error.message, 'error')
            }
        ];
        return llistaAccions.filter((accio) => accio.visible !== false);
    }, [notificacio, t]);

    return (
        <List>
            {accionsVisibles.length > 0 ? (
                accionsVisibles.map((accio, index) => {
                    const { title, titleButton, resourceName, onSuccess, onError, ...buttonProps } = accio;
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
                                        onSuccess={onSuccess}
                                        onError={onError}
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
