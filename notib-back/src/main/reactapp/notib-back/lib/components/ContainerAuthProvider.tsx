import React from 'react';
import useLogConsole from '../util/useLogConsole';
import AuthContext from './AuthContext';

const LOG_PREFIX = '[CAUTH]';
const CHECK_TOKEN_TIMEOUT_MARGIN_SECS = 1;
// Temps mínim d'espera abans de tornar a verificar el token. Si el servidor retorna un token que ja ha
// (o està a punt de) caducar, sense aquest mínim es tornaria a demanar immediatament, en un bucle que
// repeteix la petició sense parar mentre el servidor no serveixi un token amb més vigència.
const CHECK_TOKEN_MIN_DELAY_MS = 5000;

type AuthProviderProps = React.PropsWithChildren & {
    /** La url a carregar després de fer logout */
    logoutUrl: string;
    /** Indica que l'autenticació és obligatòria (no es pot veure res si no s'està autenticat) */
    mandatory?: true;
    /** Indica si s'han d'imprimir a la consola missatges de depuració */
    debug?: true;
};

const parseJwt = (token?: string) => {

    if (token == null) {
        return token;
    }
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(
        atob(base64)
            .split('')
            .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
            .join('')
    );
    return JSON.parse(jsonPayload);
};

const useTokenWatchTimeout = (callback: () => void, delay: number = 1000) => {

    let timeoutId: any;
    const start = (newDelay?: number) => {
        clearTimeout(timeoutId);
        timeoutId = setTimeout(callback, newDelay ?? delay);
    };
    const refresh = (newDelay?: number) => start(newDelay);
    const stop = () => clearTimeout(timeoutId);
    return {refresh, stop,};
};

export const AuthProvider = (props: AuthProviderProps) => {

    const { logoutUrl, mandatory, debug, children } = props;
    const [loading, setLoading] = React.useState<boolean>(true);
    const tokenRef = React.useRef<string>(undefined);
    const tokenParsedRef = React.useRef<any>(undefined);
    const logConsole = useLogConsole(LOG_PREFIX);
    const isAuthenticated = !loading && tokenRef.current != null;
    const authSrc = Array.from(document.head.getElementsByTagName('script')).find((el) =>
        el.src.includes('/authToken')
    )!.src;
    const signOutUrl = authSrc.replace('/authToken', '/logout');
    const checkToken = () => {
        debug && logConsole.debug('Verificació del token iniciada');
        getToken()
            .then((token) => {
                if (token) {
                    setToken(token, true);
                } else {
                    debug && logConsole.debug('La verificació del token ha retornat <null>');
                    window.location.href = logoutUrl;
                }
            })
            .catch((error) => {
                logConsole.error('Error al verificar el token:', JSON.stringify(error));
                window.location.href = logoutUrl;
            });
    };
    const { refresh: checkTokenRefresh, stop: checkTokenStop } = useTokenWatchTimeout(checkToken);
    const getToken = async () => {
        // redirect: 'manual' -> si la sessió del contenidor ja no és vàlida, /authToken respon amb
        // una redirecció (cap al login de Keycloak). Sense això, fetch la seguiria automàticament;
        // com que acaba creuant a un origen diferent (p.ex. authdev.limit.es) i l'endpoint de login
        // de Keycloak mai serveix capçaleres CORS (està pensat per a navegació completa, no per a
        // fetch/XHR -veure doc/Problema_Logout_CORS_Filter_Chain.md-), el navegador bloquejaria la
        // lectura de la resposta final. Amb 'manual' detectam la redirecció immediatament (sense que
        // el navegador n'intenti llegir el resultat) i ho tractam com "no hi ha token", deixant que
        // qui crida faci el que ja té previst en aquest cas (redirigir a login).
        const response = await fetch(authSrc, { redirect: 'manual' });
        if (response.type === 'opaqueredirect') {
            return null;
        }
        const text = await response.text();
        const match = text.match(/window\.__AUTH_TOKEN__\s*=\s*'([^']+)'/);
        return match ? match[1] : null;
    };
    const setToken = (token: string, verified?: boolean) => {
        tokenRef.current = token;
        const tokenParsed = parseJwt(token);
        tokenParsedRef.current = tokenParsed;
        const checkTokenTimeout = Math.max(
            (tokenParsed.exp - Date.now() / 1000 + CHECK_TOKEN_TIMEOUT_MARGIN_SECS) * 1000,
            CHECK_TOKEN_MIN_DELAY_MS
        );
        tokenParsed && checkTokenRefresh(checkTokenTimeout);
        setLoading(false);
        debug && logConsole.debug('Token', verified ? 'verificat:' : 'obtingut:', token);
    };
    React.useEffect(() => {
        debug &&
            logConsole.debug(
                "Inicialitzant proveidor d'autenticació de JBoss",
                authSrc,
                signOutUrl,
                logoutUrl
            );
        if ((window as any).__AUTH_TOKEN__) {
            setToken((window as any).__AUTH_TOKEN__);
        } else {
            debug && logConsole.debug("No s'ha trobat el token, programant consulta periòdica");
            const checkTokenInterval = setInterval(() => {
                if ((window as any).__AUTH_TOKEN__) {
                    debug && logConsole.debug('Consultant si el token ja està disponible');
                    setToken((window as any).__AUTH_TOKEN__);
                    clearInterval(checkTokenInterval);
                }
            }, 100);
            setTimeout(() => {
                clearInterval(checkTokenInterval);
                if (!(window as any).__AUTH_TOKEN__) {
                    debug &&
                        logConsole.debug(
                            'La consulta periòdica no ha pogut obtenir el token. El consultarem directament.'
                        );
                    getToken()
                        .then((token) => {
                            token && setToken(token);
                        })
                        .catch((error) => {
                            logConsole.error('Error al obtenir el token directament', error);
                        });
                }
            }, 1000);
        }
        return () => {
            checkTokenStop();
        };
    }, []);
    const signIn = loading ? undefined : () => {};
    const signOut = loading
        ? undefined
        : () => {
              fetch(signOutUrl).finally(() => {
                  debug && logConsole.debug('Tancament de sessió');
                  window.location.href = logoutUrl;
              });
          };
    const context = {
        isLoading: loading,
        isReady: !loading,
        isAuthenticated,
        bearerTokenActive: false,
        getToken: () => tokenRef.current,
        getTokenParsed: () => tokenParsedRef.current,
        getUserId: () => tokenParsedRef.current?.['preferred_username'],
        getUserName: () => tokenParsedRef.current?.['name'],
        getUserEmail: () => tokenParsedRef.current?.['email'],
        signIn,
        signOut,
    };
    const showChildren = !loading && (!mandatory || (mandatory && isAuthenticated));
    return (
        <AuthContext.Provider value={context}>
            {showChildren ? children : null}
        </AuthContext.Provider>
    );
};
