import React from "react";
import {useResourceApiService} from "reactlib";

export const useDadesProcediment = (parentFormData: any) => {

    const [forceEntregaPostalActiva, setForceEntregaPostalActiva] = React.useState(false);
    const [agrupable, setAgrupable] = React.useState(false);
    const {isReady: apiIsReady, artifactAction: apiAction,} = useResourceApiService('procedimentResource');
    const procedimentId = parentFormData?.procediment?.id;
    const organ = parentFormData?.organGestor;
    const organId = organ?.id;

    React.useEffect(() => {
        if (!apiIsReady || !procedimentId || !organId) {
            setForceEntregaPostalActiva(false);
            setAgrupable(false);
            return;
        }
        apiAction(procedimentId, {code: 'DADES_PROCEDIMENT', data: {organGestor: organ,}}).then((resposta: any) => {
            setForceEntregaPostalActiva(Boolean(resposta?.entregaCieActiva));
            setAgrupable(Boolean(resposta?.agrupable));
        })
        .catch((error: any) => {
            console.error(error);
            setForceEntregaPostalActiva(false);
            setAgrupable(false);
        });
    }, [apiIsReady, apiAction, procedimentId, organId, organ]);

    return {
        forceEntregaPostalActiva,
        agrupable,
    };
};
