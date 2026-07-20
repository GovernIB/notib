import {useBaseAppContext, useMuiActionReportLogic} from 'reactlib';
import Grid from "@mui/material/Grid";
import GridFormField from "../../components/GridFormField.tsx";
import {useTranslation} from "react-i18next";
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

    const { temporalMessageShow } = useBaseAppContext();
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
                msg += t('page.notificacio.grid.accions.anular.respostesError') + "\n";
                resposta.respostes.forEach((r: any) => {
                    if (r.error) {
                        msg += r.identificador + " - Error: " + r.codiReposta + " - " + r.descripcioResposta + "\n";
                    }
                });
                if (msg.length > 0) {
                    severity = "error";
                }
            }
            if (resposta.noExecutades && resposta.noExecutades.length > 0) {
                severity = severity === "success" ? "warning" : severity;
                msg = msg.length > 0 ? "\n" + msg : msg;
                msg += t('page.notificacio.grid.accions.anular.noExecutades');
                resposta.noExecutades.forEach((r: any) => msg += r + ", ");
                msg = msg.substring(0, msg.length -2);
            }
            msg = !msg ? t('page.notificacio.grid.accions.anular.ok') : msg;
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
            <GridFormField size={12} name="caducitat" required readOnly disabled/> {/*TODO caducitat que no mostri la hora*/}
            <GridFormField size={12} name="dies"  required />
            <GridFormField size={12} name="motiu" type="textarea" required />
        </Grid>),
        undefined,
        undefined,
        botons,
        undefined,
        undefined,
        resposta => {
            if (!resposta) {
                temporalMessageShow(null, t('page.notificacio.grid.accions.ampliarTermini.noReposta'), "error");
                return;
            }
            let severity : TemporalMessageSeverity = "success";
            let msg = "";
            if (!resposta.ok) {
                msg += t('page.notificacio.grid.accions.ampliarTermini.respostaError');
                severity = "error";
                msg += resposta.descripcionRespuesta ? resposta.descripcionRespuesta : resposta;
                if (resposta.descripcions && resposta.descripcions.lengths > 0) {
                    resposta.descripcions.forEach((r: any) => msg += r + ", ");
                    msg = msg.substring(0, msg.length -2);
                }
            }
            if (resposta.noExecutades && resposta.noExecutades.length > 0) {
                severity = severity === "success" ? "warning" : severity;
                msg = msg.length > 0 ? "\n" + msg : msg;
                msg += t('page.notificacio.grid.accions.ampliarTermini.noExecutades');
                resposta.noExecutades.forEach((r: any) => msg += r + ", ");
                msg = msg.substring(0, msg.length -2);
            }
            msg = !msg ? t('page.notificacio.grid.accions.ampliarTermini.ok') : msg;
            temporalMessageShow(null, msg, severity);
        },
        undefined,
        undefined,
        true,
    );

    const { exec: marcarProcessat, formDialogComponent: marcarProcessatDialog } = useMuiActionReportLogic(
        'notificacioResource',
        'MARCAR_PROCESSAT',
        undefined,
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
    const { exec: recuperarRemesa } = useMuiActionReportLogic(
        'notificacioResource',
        'RECUPERAR_REMESA',
    );

    return { descarregarJustificantEnviament,
             descarregarDocumentEnviat,
             descarregarCertificacio,
             anularRemesa, anularRemesaDialog,
             ampliarTermini, ampliarTerminiDialog,
             marcarProcessat, marcarProcessatDialog,
             esborrarRemesa,
             recuperarRemesa
    };
};

export default useAccionsNotificacio;
