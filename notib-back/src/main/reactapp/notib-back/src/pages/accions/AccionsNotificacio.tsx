import {FormApi, useBaseAppContext, useMuiActionReportLogic} from 'reactlib';
import React, {useState} from "react";
import Grid from "@mui/material/Grid";
import GridFormField from "../../components/GridFormField.tsx";
import {useTranslation} from "react-i18next";
import {useFormDialog} from "../../../lib/components/mui/form/FormDialog.tsx";
import Snackbar from "@mui/material/Snackbar";
import Alert from "@mui/material/Alert";
import {TemporalMessageSeverity} from "../../../lib/components/BaseAppContext.tsx";

export const useAccionsNotificacio = () => {

    const { t } = useTranslation();
    const { exec: descarregarJustificantEnviament } = useMuiActionReportLogic(
        'notificacioResource',
        undefined,
        'DESCARREGAR_JUSTIFICANT_ENVIAMENT_NOTIFICACIO',
        'CUSTOM'
    );
    const { exec: descarregarDocumentEnviat } = useMuiActionReportLogic(
        'notificacioResource',
        undefined,
        'DESCARREGAR_DOCUMENT_ENVIAT',
        'CUSTOM'
    );

    const { exec: descarregarCertificacio } = useMuiActionReportLogic(
        'notificacioResource',
        undefined,
        'DESCARREGAR_CERTIFICACIO',
        'CUSTOM'
    );

    const botons = [{value: true, text: t('comu.guardar'), icon: 'save', componentProps: { variant: 'contained' }},
        {value: false, text: t('comu.cancelar'), componentProps: { variant: 'outlined' }}];

    const { messageDialogShow, temporalMessageShow } = useBaseAppContext();
    const { exec: anularRemesa, formDialogComponent: anularRemesaDialog } = useMuiActionReportLogic(
        'notificacioResource',
        'ANULAR_REMESA',
        undefined,
        undefined,
        undefined,
        undefined,
        undefined,
        undefined,
        undefined,
        (<Grid container><GridFormField size={12} name="motiu" type="textarea" required /></Grid>),
        undefined,
        undefined,
        botons,
        undefined,
        undefined,
        resposta =>  {
            if (!resposta) {
                temporalMessageShow(null, t('page.notificacio.grid.accions.anular.noReposta'), "error");
                return;
            }
            let severity : TemporalMessageSeverity = "success";
            let msg = "";
            if (resposta.respostes && resposta.respostes.length > 0) {
                msg += t('page.notificacio.grid.accions.anular.respostesError');
                resposta.respostes.forEach(r => {
                    if (r.error) {
                        msg += "Identificador: " + r.identificador + " Error: " + r.codiReposta + " " + r.descripcioResposta + ", ";
                    }
                });
                if (msg.length > 0) {
                    msg = msg.substring(0, msg.length -2);
                    severity = "error";
                }
            }
            if (resposta.noExecutades && resposta.noExecutades.length > 0) {
                severity = severity === "success" ? "warning" : severity;
                msg = msg.length > 0 ? "\n" + msg : msg;
                msg += t('page.notificacio.grid.accions.anular.noExecutades');
                resposta.noExecutades.forEach(r => msg += r + ", ");
                msg = msg.substring(0, msg.length -2);
            }
            temporalMessageShow(null, msg, severity);
        },
        undefined,
        undefined,
        true,
    );

    const { exec: ampliarTermini, formDialogComponent: ampliarTerminiDialog } = useMuiActionReportLogic(
        'notificacioResource',
        'AMPLIAR_TERMINI',
        undefined,
        undefined,
        undefined,
        undefined,
        undefined,
        undefined,
        undefined,
        (<Grid container spacing={2}>
            <GridFormField size={12} name="caducitat" required readOnly disabled/>
            <GridFormField size={12} name="dies" type="date" required /> {/*TODO caducitat que no mostri la hora*/}
            <GridFormField size={12} name="motiu" type="textarea" required />
        </Grid>),
        undefined,
        undefined,
        botons,
        undefined,
        undefined,
        undefined,
        undefined,
        undefined,
        true,
    );

    const { exec: marcarProcessat, formDialogComponent: marcarProcessatDialog } = useMuiActionReportLogic(
        'notificacioResource',
        'MARCAR_PROCESSAT',  undefined,
        undefined,
        undefined,
        undefined,
        undefined,
        undefined,
        undefined,
        (<Grid container spacing={2}>
            <GridFormField size={12} name="motiu" type="textarea" required />
        </Grid>),
        undefined,
        undefined,
        botons,
        undefined,
        undefined,
        undefined,
        undefined,
        undefined,
        true,

    );

    const { exec: esborrarRemesa } = useMuiActionReportLogic(
        'notificacioResource',
        'ESBORRAR_REMESA',
    );

    return { descarregarJustificantEnviament,
             descarregarDocumentEnviat,
             descarregarCertificacio,
             anularRemesa, anularRemesaDialog,
             ampliarTermini, ampliarTerminiDialog,
             marcarProcessat, marcarProcessatDialog,
             esborrarRemesa };
};

export default useAccionsNotificacio;
