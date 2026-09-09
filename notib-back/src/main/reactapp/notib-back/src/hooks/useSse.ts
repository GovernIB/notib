import React from 'react';
import { EventSource } from 'eventsource';
import { useAuthContext, useResourceApiService } from 'reactlib';

export const useSse = (
    queueId: string,
    eventName: string,
    onEvent: (event: any) => void,
    closeOnError?: boolean
) => {
    const { getToken } = useAuthContext();
    const { isReady: apiIsReady, currentLinks } = useResourceApiService('sse');
    React.useEffect(() => {
        if (!apiIsReady) {
            return;
        }
        const subscribeHref = currentLinks['subscribe'].href;
        const eventSourceHref = subscribeHref.replace('{queueId}', queueId);
        const eventSource = new EventSource(eventSourceHref, {
            fetch: (input, init) =>
                fetch(input, {...init, headers: {...init.headers,
                        Authorization: 'Bearer ' + getToken(),
                    },
                }),
        });
        eventSource.addEventListener(eventName, (event) => {
            const data = JSON.parse(event.data);
            onEvent?.(data);
        });
        eventSource.onerror = () => {
            if (closeOnError) {
                eventSource.close();
            }
        };
        return () => eventSource.close();
    }, [apiIsReady]);
};

export default useSse;
