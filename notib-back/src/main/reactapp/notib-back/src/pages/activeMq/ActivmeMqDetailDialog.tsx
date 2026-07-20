import {useTranslation} from "react-i18next";
import {
    MuiDataGrid,
    MuiDataGridColDef,
    useBaseAppContext,
    useCloseDialogButtons,
    useMuiContentDialog,
    useMuiDataGridApiRef,
    useResourceApiService
} from "reactlib";
import React from "react";
import Box from "@mui/material/Box";
import {TemporalMessageSeverity} from "../../../lib/components/BaseAppContext.tsx";

export const useActiveMqDetailDetailDialog = () => {

    const { t } = useTranslation();
    const [dialogShow, dialogComponentActiveMq] = useMuiContentDialog();
    const defaultDialogButtons = useCloseDialogButtons();
    const handleDetailButtonClick = (id: any) => {
        dialogShow(t('page.activemq.detail.title') + " " + id,
            <ActiveMqDetailDialogContent codiCua={id} />,
            defaultDialogButtons, { maxWidth: 'lg', fullWidth: true,}
        ).catch(() => null);
    };
    return { dialogComponentActiveMq, onDetailClickActiveMq: handleDetailButtonClick };
}

const useActiveMqDetailAction = (refresh?: () => void) => {

    const { t } = useTranslation();
    const { isReady: apiIsReady, artifactAction: apiAction } = useResourceApiService('activeMqDetailResource');
    const { temporalMessageShow } = useBaseAppContext();

    const esborrar = (ids: any[], codiCua: string) => {
        apiAction(undefined, { code: 'ESBORRAR_MISSATGE', data: { ids: ids, codi: codiCua } })
            .then(resposta => {
                refresh?.();
                let error = "esborrarOk";
                let severity : TemporalMessageSeverity = "success";
                if (resposta.error) {
                    severity = "error";
                    error = "esborrarError";
                }
                const msg = t('page.activemq.detail.' + error);
                temporalMessageShow(null, msg , severity);
            })
            .catch(error => temporalMessageShow(null, error?.message, 'error'))
    };

    return { apiIsReady, esborrar };
};

const ActiveMqDetailDialogContent: React.FC<{ codiCua: string }> = (props) => {

    const { t } = useTranslation();
    const { codiCua } = props;
    const gridApiRef = useMuiDataGridApiRef();
    const { esborrar } = useActiveMqDetailAction(gridApiRef?.current?.refresh);

    const columns: MuiDataGridColDef[] = React.useMemo(
        () => [
            {
                field: 'id',
                flex: 1.5
            },
            {
                field: 'data',
                flex: 1.5
            },
            {
                field: 'uuid',
                flex: 1.5
            },
            {
                field: 'notificacioUuId',
                flex: 1.5
            }
        ],
        []
    );

    return (<Box height={500}>
        <MuiDataGrid
            resourceName="activeMqDetailResource"
            titleDisabled
            columns={columns}
            toolbarHideQuickFilter
            readOnly
            checkboxSelection={false}
            fixedFilter={"id: " + codiCua}
            rowAdditionalActions={[
                {
                    label: t('page.activemq.detail.esborrar'),
                    title: t('page.activemq.detail.esborrar'),
                    icon: 'delete',
                    showInMenu: true,
                    onClick: id => esborrar([id], codiCua),
                }
            ]}
        />
    </Box>)
}


export default ActiveMqDetailDialogContent;
