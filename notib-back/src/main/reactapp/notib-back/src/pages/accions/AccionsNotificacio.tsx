import {useMuiActionReportLogic} from 'reactlib';
import {useState} from "react";

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

    const { exec: descarregarCertificacio } = useMuiActionReportLogic(
        'notificacioResource',
        undefined,
        'DESCARREGAR_CERTIFICACIO',
        'CUSTOM'
    );

    const [motiu, setMotiu] = useState('');
    const { exec: anularRemesa } = useMuiActionReportLogic(
        'notificacioResource',
        'ANULAR_REMESA',
        undefined,
        undefined,
        undefined,
        undefined,
        undefined,
        undefined,
        undefined,
        undefined,
        undefined,
        undefined,
        undefined,
        {motiu}

    );
    const { exec: ampliarTermini } = useMuiActionReportLogic(
        'notificacioResource',
        'AMPLIAR_TERMINI',
    );

    const { exec: marcarProcessat } = useMuiActionReportLogic(
        'notificacioResource',
        'MARCAR_PROCESSAT',
    );

    const { exec: esborrarRemesa } = useMuiActionReportLogic(
        'notificacioResource',
        'ESBORRAR_REMESA',
    );

    return { descarregarJustificantEnviament, descarregarDocumentEnviat, descarregarCertificacio, anularRemesa, ampliarTermini, marcarProcessat, esborrarRemesa };
};

export default useAccionsNotificacio;
