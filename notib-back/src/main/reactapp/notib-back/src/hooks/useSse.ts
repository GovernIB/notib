import React from 'react';
import { EventSource } from 'eventsource';
import { useResourceApiService } from 'reactlib';

type SseCallback = (event: any) => void;

/**
 * Connexió SSE compartida per tots els subscriptors d'una mateixa cua (queueId) EN AQUESTA
 * PESTANYA/sessió del navegador.
 *
 * El backend (SseEventServiceImpl) admet múltiples listeners simultanis per cua (un per cada
 * connexió SSE, p.ex. un per usuari o pestanya oberta), de manera que dues pestanyes/usuaris
 * diferents reben totes dues els mateixos events sense desallotjar-se. Tot i així, dins una
 * mateixa pestanya es continua multiplexant en una sola connexió EventSource per queueId (encara
 * que hi hagi diversos components subscrits a noms d'event diferents de la mateixa cua), evitant
 * obrir-ne una per cada `useSse`; el repartiment per nom d'event es fa al client.
 */
type SharedConnection = {
    eventSource: EventSource;
    /** Callbacks registrats, agrupats per nom d'event SSE. */
    callbacks: Map<string, Set<SseCallback>>;
    /** Listener DOM únic registrat a l'EventSource per cada nom d'event. */
    domListeners: Map<string, (event: MessageEvent) => void>;
    /** Nombre de subscriptors vius (hooks muntats) d'aquesta connexió. */
    refCount: number;
    /** Cert si algun subscriptor ha demanat tancar la connexió en cas d'error. */
    closeOnError: boolean;
};

const connections = new Map<string, SharedConnection>();

const createConnection = (
    queueId: string,
    eventSourceHref: string
): SharedConnection => {
    const eventSource = new EventSource(eventSourceHref, {
        // La petició s'autentica amb la galeta de la sessió de servidor (veure NotibAuthProvider);
        // "include" fa que s'enviï també quan el front no és al mateix origen que el backend
        // (`npm run dev`).
        fetch: (input, init) => fetch(input, { ...init, credentials: 'include' }),
    });
    const connection: SharedConnection = {
        eventSource,
        callbacks: new Map(),
        domListeners: new Map(),
        refCount: 0,
        closeOnError: false,
    };
    // El tractament d'error és a nivell de connexió compartida (no per subscriptor): si algun
    // dels subscriptors ha demanat closeOnError, la connexió es tanca per a tots.
    eventSource.onerror = () => {
        if (connection.closeOnError) {
            eventSource.close();
            connections.delete(queueId);
        }
    };
    connections.set(queueId, connection);
    return connection;
};

const subscribe = (
    queueId: string,
    eventSourceHref: string,
    eventName: string,
    callback: SseCallback,
    closeOnError?: boolean
): (() => void) => {
    const connection = connections.get(queueId) ?? createConnection(queueId, eventSourceHref);
    if (closeOnError) {
        connection.closeOnError = true;
    }
    let callbacks = connection.callbacks.get(eventName);
    if (!callbacks) {
        callbacks = new Set<SseCallback>();
        connection.callbacks.set(eventName, callbacks);
        // Un únic listener DOM per nom d'event, que reparteix a tots els callbacks registrats.
        const domListener = (event: MessageEvent) => {
            const data = JSON.parse(event.data);
            connection.callbacks.get(eventName)?.forEach((cb) => cb?.(data));
        };
        connection.domListeners.set(eventName, domListener);
        connection.eventSource.addEventListener(eventName, domListener);
    }
    callbacks.add(callback);
    connection.refCount++;
    let unsubscribed = false;
    return () => {
        if (unsubscribed) {
            return;
        }
        unsubscribed = true;
        const remaining = connection.callbacks.get(eventName);
        remaining?.delete(callback);
        if (remaining && remaining.size === 0) {
            connection.callbacks.delete(eventName);
            const domListener = connection.domListeners.get(eventName);
            if (domListener) {
                connection.eventSource.removeEventListener(eventName, domListener);
                connection.domListeners.delete(eventName);
            }
        }
        connection.refCount--;
        if (connection.refCount <= 0) {
            connection.eventSource.close();
            // Només s'esborra si el registre encara apunta a AQUESTA connexió: onerror amb
            // closeOnError o una re-subscripció posterior poden haver-la substituïda.
            if (connections.get(queueId) === connection) {
                connections.delete(queueId);
            }
        }
    };
};

/**
 * Es subscriu a un nom d'event d'una cua SSE del backend.
 *
 * Diverses crides amb el mateix `queueId` comparteixen una única connexió EventSource (veure
 * SharedConnection); la connexió es crea amb el primer subscriptor i es tanca quan es desmunta
 * el darrer.
 */
export const useSse = (
    queueId: string,
    eventName: string,
    onEvent: (event: any) => void,
    closeOnError?: boolean
) => {
    const { isReady: apiIsReady, currentLinks } = useResourceApiService('sse');
    React.useEffect(() => {
        if (!apiIsReady) {
            return;
        }
        const subscribeHref = currentLinks['subscribe'].href;
        const eventSourceHref = subscribeHref.replace('{queueId}', queueId);
        return subscribe(queueId, eventSourceHref, eventName, onEvent, closeOnError);
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [apiIsReady, queueId, eventName]);
};

export default useSse;
