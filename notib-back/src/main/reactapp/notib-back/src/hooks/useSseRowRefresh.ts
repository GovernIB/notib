import React from 'react';
import { GridApiPro } from '@mui/x-data-grid-pro';
import { useResourceApiService } from 'reactlib';
import useSse from './useSse';

/**
 * Es subscriu a events SSE d'avís de canvi d'estat d'una entitat (remesa/enviament) i, quan la
 * fila afectada ja és visible al grid indicat, la torna a demanar al servidor i l'actualitza
 * in-place (datagridApiRef.updateRows), sense recarregar tota la pàgina ni perdre la selecció o el
 * scroll. Si la fila no és visible (altra pàgina, filtre, etc.) l'event s'ignora sense cap petició.
 *
 * L'event SSE només duu l'identificador de l'entitat afectada; el valor actualitzat es calcula al
 * servidor sota demanda (mateix mecanisme que ja fa servir la càrrega normal del llistat), evitant
 * haver de dur-hi duplicada la lògica de generació de l'estat.
 */
export const useSseRowRefresh = (
    resourceName: string,
    datagridApiRef: React.RefObject<GridApiPro | null>,
    queueId: string,
    eventName: string
) => {
    const { getOne } = useResourceApiService(resourceName);

    useSse(queueId, eventName, (event: any) => {
        const entityId = event?.entityId;
        if (entityId == null) {
            return;
        }
        let row;
        try {
            row = datagridApiRef.current?.getRow(entityId);
        } catch {
            row = null;
        }
        if (row == null) {
            // La fila no és visible a la pàgina/filtre actual del grid: no cal fer res.
            return;
        }
        getOne(entityId, { includeLinks: true })
            .then((freshRow: any) => {
                datagridApiRef.current?.updateRows([freshRow]);
            })
            .catch(() => {
                // Si la fila ha desaparegut (esborrada, filtre canviat, etc.) no fem res: es
                // refrescarà igualment al proper refresc normal del grid.
            });
    });
};

export default useSseRowRefresh;
