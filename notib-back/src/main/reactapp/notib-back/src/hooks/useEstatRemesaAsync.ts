import React from 'react';
import { GridApiPro, GridRowId } from '@mui/x-data-grid-pro';
import { useResourceApiService } from 'reactlib';
import useSse from './useSse';

// Temps màxim d'espera de l'estat calculat via SSE abans de demanar la remesa individualment
const TEMPS_ESPERA_MS = 15000;
const INTERVAL_COMPROVACIO_MS = 5000;

/**
 * Rep els estats de remesa calculats de manera asíncrona pel servidor (event SSE
 * NOTIFICACIO_ESTAT_CALCULAT, veure NotificacioEstatAsyncHelper) i actualitza les files visibles
 * del grid que els tenien pendents (estatPendent).
 *
 * Si l'event no arriba (p.ex. la connexió SSE encara no estava establerta quan s'ha enviat, o
 * s'ha atès per una altra instància del servidor), passat TEMPS_ESPERA_MS es demana cada remesa
 * pendent individualment: la consulta individual calcula l'estat de manera síncrona.
 */
export const useEstatRemesaAsync = (datagridApiRef: React.RefObject<GridApiPro | null>) => {
    const { getOne } = useResourceApiService('notificacioResource');
    // getOne pot canviar a cada render: es guarda en una ref per no reiniciar l'interval
    const getOneRef = React.useRef(getOne);
    getOneRef.current = getOne;
    // Moment en què s'ha detectat cada fila pendent
    const pendentsDesDe = React.useRef(new Map<GridRowId, number>());
    // Files ja demanades individualment, per no repetir la petició si no se'n pot obtenir l'estat
    const demanades = React.useRef(new Set<GridRowId>());

    const updateRows = React.useCallback((rows: any[]) => {
        try {
            datagridApiRef.current?.updateRows(rows);
        } catch {
            // El grid ja no està muntat
        }
    }, [datagridApiRef]);

    useSse('REMESA_ENVIAMENT_ESTAT', 'NOTIFICACIO_ESTAT_CALCULAT', (event: any) => {
        const estats = event?.data;
        if (estats == null) {
            return;
        }
        const rows: any[] = [];
        Object.entries(estats).forEach(([id, estatString]) => {
            const rowId = Number(id);
            pendentsDesDe.current.delete(rowId);
            let row;
            try {
                row = datagridApiRef.current?.getRow(rowId);
            } catch {
                row = null;
            }
            if (row != null) {
                rows.push({ id: rowId, estatString, estatPendent: false });
            }
        });
        if (rows.length > 0) {
            updateRows(rows);
        }
    });

    React.useEffect(() => {
        const interval = setInterval(() => {
            const api = datagridApiRef.current;
            if (api == null) {
                return;
            }
            const ara = Date.now();
            const visibles = new Set<GridRowId>();
            let rowModels;
            try {
                rowModels = api.getRowModels();
            } catch {
                return;
            }
            rowModels.forEach((row: any, id: GridRowId) => {
                if (!row?.estatPendent) {
                    demanades.current.delete(id);
                    return;
                }
                if (demanades.current.has(id)) {
                    return;
                }
                visibles.add(id);
                const desDe = pendentsDesDe.current.get(id);
                if (desDe == null) {
                    pendentsDesDe.current.set(id, ara);
                    return;
                }
                if (ara - desDe < TEMPS_ESPERA_MS) {
                    return;
                }
                pendentsDesDe.current.delete(id);
                demanades.current.add(id);
                getOneRef.current(id, { includeLinks: true })
                    .then((freshRow: any) => updateRows([{ ...freshRow, estatPendent: false }]))
                    .catch(() => updateRows([{ id, estatPendent: false }]));
            });
            // Oblida les files que ja no són visibles (canvi de pàgina, filtre...)
            Array.from(pendentsDesDe.current.keys())
                .filter(id => !visibles.has(id))
                .forEach(id => pendentsDesDe.current.delete(id));
        }, INTERVAL_COMPROVACIO_MS);
        return () => clearInterval(interval);
    }, [datagridApiRef, updateRows]);
};

export default useEstatRemesaAsync;
