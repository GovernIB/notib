import React from 'react';
import { EventSource } from 'eventsource';
import { useAuthContext, useResourceApiService } from 'reactlib';

type SseCallback = (event: any) => void;

/** Proveïdor del token d'autenticació (el mateix contracte que `useAuthContext().getToken`). */
type TokenProvider = () => string | undefined;

/**
 * Connexió SSE compartida per tots els subscriptors d'una mateixa cua (queueId).
 *
 * El backend (SseEventServiceImpl) només manté UN listener per nom de cua, de manera global:
 * `consumers.put(queue.name(), listener)`. Si dos components obren cadascun el seu propi
 * EventSource cap a /api/sse/{queueId}, el segon a registrar-se desallotja silenciosament el
 * listener del primer i només un dels dos rep events. Per això aquí es multiplexa: una sola
 * connexió per queueId, i el repartiment per nom d'event es fa al client.
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
    eventSourceHref: string,
    getToken: TokenProvider,
    bearerTokenActive?: boolean
): SharedConnection => {
    const eventSource = new EventSource(eventSourceHref, {
        // Només afegim el Bearer quan l'autenticació és per token (OidcAuthProvider). Amb
        // ContainerAuthProvider (bearerTokenActive=false) l'autenticació és per sessió/cookie i
        // aquest endpoint no valida cap Bearer -Keycloak, en detectar la capçalera Authorization,
        // intenta autenticar la petició amb el token en lloc de la sessió ja establerta, i si
        // aquesta validació "bearer-only" falla (com passa en aquest mode) la connexió SSE queda
        // rebutjada en silenci (onerror no fa res tret que s'hagi demanat closeOnError), deixant
        // l'usuari amb l'spinner indeterminat sense percentatge ni missatges.
        fetch: (input, init) =>
            fetch(input, {
                ...init,
                headers: bearerTokenActive
                    ? { ...init.headers, Authorization: 'Bearer ' + getToken() }
                    : init.headers,
            }),
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
    getToken: TokenProvider,
    eventName: string,
    callback: SseCallback,
    closeOnError?: boolean,
    bearerTokenActive?: boolean
): (() => void) => {
    const connection =
        connections.get(queueId) ?? createConnection(queueId, eventSourceHref, getToken, bearerTokenActive);
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
    const { getToken, bearerTokenActive } = useAuthContext();
    const { isReady: apiIsReady, currentLinks } = useResourceApiService('sse');
    React.useEffect(() => {
        if (!apiIsReady) {
            return;
        }
        const subscribeHref = currentLinks['subscribe'].href;
        const eventSourceHref = subscribeHref.replace('{queueId}', queueId);
        return subscribe(queueId, eventSourceHref, getToken, eventName, onEvent, closeOnError, bearerTokenActive);
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [apiIsReady, queueId, eventName]);
};

export default useSse;
