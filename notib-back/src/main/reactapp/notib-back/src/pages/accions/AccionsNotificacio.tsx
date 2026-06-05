import { useMuiActionReportLogic } from 'reactlib';

export const useAccionsNotificacio = () => {
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
    return { descarregarJustificantEnviament, descarregarDocumentEnviat };
};

export default useAccionsNotificacio;
