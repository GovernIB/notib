import {useMuiActionReportLogic} from "reactlib";

export const useAccionsCallbacks = () => {

    const { exec: enviarCallbackPendent } = useMuiActionReportLogic(
        'callbackResource',
        'ENVIAR_CALLBACK_PENDENT',
    );

    const { exec: pausarCallbackPendent } = useMuiActionReportLogic(
        'callbackResource',
        'PAUSAR_CALLBACK_PENDENT',
    );

    const { exec: activarCallbackPendent } = useMuiActionReportLogic(
        'callbackResource',
        'ACTIVAR_CALLBACK_PENDENT',
    );

    return {
        enviarCallbackPendent,
        pausarCallbackPendent,
        activarCallbackPendent
    };
}

export default useAccionsCallbacks;
