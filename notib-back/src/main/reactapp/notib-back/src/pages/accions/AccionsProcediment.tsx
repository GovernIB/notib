import {useMuiActionReportLogic, useBaseAppContext, MuiDataGridApiRef} from "reactlib";
import {useTranslation} from "react-i18next";

export const useAccionsProcediment = (dataGridApiRef: MuiDataGridApiRef) => {

    const { t } = useTranslation();
    const { temporalMessageShow } = useBaseAppContext();

    const handleSuccess = (i18nKey: string) => (resposta: unknown) => {
        const msg = resposta ? "success" : "error";
        dataGridApiRef.current?.refresh();
        temporalMessageShow(null, t(`page.procediments.grid.accions.${i18nKey}.${msg}`), msg);
    };

    const { exec: activarProcediment } = useMuiActionReportLogic(
        'procedimentResource',
        'PROCEDIMENT_ACTIVAR',
        undefined, undefined, undefined, undefined, undefined, undefined, undefined,
        undefined, undefined, undefined, undefined, undefined, undefined,
        handleSuccess('activar'),
    );

    const { exec: desactivarProcediment } = useMuiActionReportLogic(
        'procedimentResource',
        'PROCEDIMENT_DESACTIVAR',
        undefined, undefined, undefined, undefined, undefined, undefined, undefined,
        undefined, undefined, undefined, undefined, undefined, undefined,
        handleSuccess('desactivar'),
    );

    const { exec: actualitzarProcediment } = useMuiActionReportLogic(
        'procedimentResource',
        'PROCEDIMENT_ACTUALITZAR',
        undefined, undefined, undefined, undefined, undefined, undefined, undefined,
        undefined, undefined, undefined, undefined, undefined, undefined,
        handleSuccess('actualitzar'),
    );

    const { exec: syncManualProcediment } = useMuiActionReportLogic(
        'procedimentResource',
        'PROCEDIMENT_SYNC_MANUAL',
        undefined, undefined, undefined, undefined, undefined, undefined, undefined,
        undefined, undefined, undefined, undefined, undefined, undefined,
        handleSuccess('syncManual'),
    );

    const { exec: syncAutoProcediment } = useMuiActionReportLogic(
        'procedimentResource',
        'PROCEDIMENT_SYNC_AUTO',
        undefined, undefined, undefined, undefined, undefined, undefined, undefined,
        undefined, undefined, undefined, undefined, undefined, undefined,
        handleSuccess('syncAuto'),
    );

    return {
        activarProcediment,
        desactivarProcediment,
        actualitzarProcediment,
        syncManualProcediment,
        syncAutoProcediment,
    };
};

export default useAccionsProcediment;
