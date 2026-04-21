import React from 'react';
import { useTranslation } from 'react-i18next';
import {MuiActionReportButton, MuiDataGridColDef} from 'reactlib';
import {DataGridPro} from "@mui/x-data-grid-pro";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import {Alert} from "@mui/material";

const EnviamentDetailTabStateMachine: React.FC<{ id: any }> = () => {
    const { t } = useTranslation();
    const columns: MuiDataGridColDef[] = React.useMemo(
        () => [
            {
                field: 'estatOrigen',
                headerName: t('page.enviament.detail.tab.stateMachine.estatOrigen'),
                flex: 1,
            },
            {
                field: 'event',
                headerName: t('page.enviament.detail.tab.stateMachine.event'),
                flex: 1,
            },
            {
                field: 'accioResultant',
                headerName: t('page.enviament.detail.tab.stateMachine.accioResultant.header'),
                flex: 1,
            },
        ],
        []
    );

    const rows = [
        { "id": 1, "estatOrigen": "NOU", "event": "RG_ENVIAR", "accioResultant": t("page.enviament.detail.tab.stateMachine.accioResultant.registrar") },
        { "id": 2, "estatOrigen": "REGISTRE_ERROR", "event": "RG_RESET", "accioResultant": t("page.enviament.detail.tab.stateMachine.accioResultant.resetIntentsRegistre") },
        { "id": 3, "estatOrigen": "REGISTRE_ERROR", "event": "RG_RETRY", "accioResultant": t("page.enviament.detail.tab.stateMachine.accioResultant.reintentarRegistre") },
        { "id": 4, "estatOrigen": "REGISTRE_PENDENT", "event": "RG_RETRY", "accioResultant": t("page.enviament.detail.tab.stateMachine.accioResultant.reintentarRegistre") },
        { "id": 5, "estatOrigen": "REGISTRE_PENDENT", "event": "RG_ERROR", "accioResultant": t("page.enviament.detail.tab.stateMachine.accioResultant.errorRegistre") },
        { "id": 6, "estatOrigen": "REGISTRE_PENDENT", "event": "RG_SUCCESS", "accioResultant": t("page.enviament.detail.tab.stateMachine.accioResultant.registreOk") },
        { "id": 7, "estatOrigen": "NOTIFICA_PENDENT", "event": "NT_ENVIAR", "accioResultant": t("page.enviament.detail.tab.stateMachine.accioResultant.enviarNotifica") },
        { "id": 8, "estatOrigen": "NOTIFICA_PENDENT", "event": "NT_SUCCESS", "accioResultant": t("page.enviament.detail.tab.stateMachine.accioResultant.notificaOk") },
        { "id": 9, "estatOrigen": "NOTIFICA_PENDENT", "event": "NT_ERROR", "accioResultant": t("page.enviament.detail.tab.stateMachine.accioResultant.notificaError") },
        { "id": 10, "estatOrigen": "NOTIFICA_PENDENT", "event": "NT_RETRY", "accioResultant": t("page.enviament.detail.tab.stateMachine.accioResultant.reintentNotifica") },
        { "id": 11, "estatOrigen": "NOTIFICA_PENDENT", "event": "NT_FI", "accioResultant": t("page.enviament.detail.tab.stateMachine.accioResultant.finalitzaRemesa") },
        { "id": 12, "estatOrigen": "NOTIFICA_ERROR", "event": "NT_RESET", "accioResultant": t("page.enviament.detail.tab.stateMachine.accioResultant.resetIntentsNotifica") },
        { "id": 13, "estatOrigen": "NOTIFICA_ERROR", "event": "NT_RETRY", "accioResultant": t("page.enviament.detail.tab.stateMachine.accioResultant.reintentNotifica") },
        { "id": 14, "estatOrigen": "NOTIFICA_SENT", "event": "CN_CONSULTAR", "accioResultant": t("page.enviament.detail.tab.stateMachine.accioResultant.consultaEstatEnviament") },
        { "id": 15, "estatOrigen": "NOTIFICA_SENT", "event": "CN_SUCCESS", "accioResultant": t("page.enviament.detail.tab.stateMachine.accioResultant.consultaEstatEnviamentOk") },
        { "id": 16, "estatOrigen": "NOTIFICA_SENT", "event": "CN_ERROR", "accioResultant": t("page.enviament.detail.tab.stateMachine.accioResultant.consultaEstatEnviamentError") },
        { "id": 17, "estatOrigen": "CONSULTA_ERROR", "event": "CN_RETRY", "accioResultant": t("page.enviament.detail.tab.stateMachine.accioResultant.reintentarConsultaEstatEnviament") },
        { "id": 18, "estatOrigen": "CONSULTA_ERROR", "event": "CN_FORWARD", "accioResultant": t("page.enviament.detail.tab.stateMachine.accioResultant.finalitzaRemesa") },
        { "id": 19, "estatOrigen": "SIR_PENDENT", "event": "SR_CONSULTAR", "accioResultant": t("page.enviament.detail.tab.stateMachine.accioResultant.consultaEnviamentSir") },
        { "id": 20, "estatOrigen": "SIR_PENDENT", "event": "SR_SUCCESS", "accioResultant": t("page.enviament.detail.tab.stateMachine.accioResultant.consultaEnviamentSirOk") },
        { "id": 21, "estatOrigen": "SIR_PENDENT", "event": "SR_RESET", "accioResultant": t("page.enviament.detail.tab.stateMachine.accioResultant.consultaEnviamentSirError") },
        { "id": 22, "estatOrigen": "SIR_PENDENT", "event": "SR_RETRY", "accioResultant": t("page.enviament.detail.tab.stateMachine.accioResultant.reintentarConsultaEnviamentSir") },
        { "id": 23, "estatOrigen": "SIR_PENDENT", "event": "SR_FORWARD", "accioResultant": t("page.enviament.detail.tab.stateMachine.accioResultant.finalitzarComunicacioSir") },
        { "id": 24, "estatOrigen": "SIR_ERROR", "event": "SR_FORWARD", "accioResultant": t("page.enviament.detail.tab.stateMachine.accioResultant.finalitzarComunicacioSir") }
    ]

    return (
        <Box sx={{ height: '100%', overflowY: 'auto', minHeight: 0 }}>
            <Alert severity="error" sx={{ mb: 1, mt: 2 }}>
                {t('page.enviament.detail.tab.stateMachine.missatge')}
            </Alert>
            <Box display="flex" justifyContent="flex-end">
                <MuiActionReportButton
                    resourceName={"notificacioEnviamentResource"}
                    report="DESCARREGAR_DIAGRAMA_STATE_MACHINE"
                    reportFileType="CUSTOM"
                    title={t('page.enviament.detail.tab.stateMachine.descarregarDiagrama')}
                    buttonComponentProps={{ variant: 'outlined', sx: { mr: 1 } }}
                    buttonIcon="download"/>
            </Box>
            {/*<FormPage>*/}
            {/*    <MuiForm*/}
            {/*        resourceName="notificacioResource"*/}
            {/*        id={id != null ? parseInt(id) : id}*/}
            {/*        title={*/}
            {/*            id != null*/}
            {/*                ? t('page.notificacio.form.title.' + type + '.update')*/}
            {/*                : t('page.notificacio.form.title.' + type + '.create')*/}
            {/*        }*/}
            {/*        initOnChangeRequest*/}
            {/*        additionalData={type ? { enviamentTipus: type, ...initialData } : initialData}*/}
            {/*        onReset={handleReset}*/}
            {/*        apiRef={formApiRef}*/}
            {/*        createLink="../"*/}
            {/*        toolbarElementsWithPositions={[{ position: 2, element: <JSonButton /> }]}*/}
            {/*        componentProps={{ style: { height: '100%' } }}*/}
            {/*        commonFieldComponentProps={{ size: 'small' }}*/}
            {/*    >*/}
            {/*    </MuiForm>*/}
            {/*</FormPage>*/}
            <Box>
                <Typography variant="h6" gutterBottom>{t('page.enviament.detail.tab.stateMachine.taulaEstatsTitle')}</Typography>
                <DataGridPro rows={rows} columns={columns}/>
            </Box>
        </Box>
    );
};

export default EnviamentDetailTabStateMachine;
