import React from 'react';
import { UserManager, UserManagerSettings, User } from 'oidc-client-ts';
import useLogConsole from '../util/useLogConsole';
import { toAbsolutePath, isCurrentPathMatching } from '../util/url';
import AuthContext, { AuthConfig } from './AuthContext';

const LOG_PREFIX = '[OAUTH]';

type AuthProviderProps = React.PropsWithChildren & {
    /** La configuració necessària per a crear la instància del UserManager */
    config: AuthConfig;
    /** URL base de l'aplicació (per a poder configurar les uris de redirect, post_logout i silent_redirect) */
    appBaseUrl: string;
    /** Indica que l'autenticació és obligatòria (no es pot veure res si no s'està autenticat) */
    mandatory?: true;
    /** Per a poder funcionar amb servidors Keycloak antics (per a poder posar /auth a davant /realms al construir la URL per authority) */
    urlRealmsPrefix?: string;
    /** Indica si s'han d'imprimir a la consola missatges de depuració */
    debug?: true;
};

const userManagerNewInstance = (config: UserManagerSettings) => {
    const userManager = new UserManager(config);
    return userManager;
};

export const AuthProvider = (props: AuthProviderProps) => {
    const { config, appBaseUrl, mandatory, urlRealmsPrefix, debug, children } = props;
    const oidcAuthConfig = {
        authority: config.url + (urlRealmsPrefix ?? '') + '/realms/' + config.realm,
        client_id: config.clientId,
        redirect_uri: toAbsolutePath('?callback', appBaseUrl),
        post_logout_redirect_uri: toAbsolutePath('', appBaseUrl),
        response_type: 'code',
        scope: 'openid profile email',
        silent_redirect_uri: toAbsolutePath('oidcSilentRenew', appBaseUrl),
        automaticSilentRenew: true,
    };
    const logConsole = useLogConsole(LOG_PREFIX);
    const hasInitialized = React.useRef(false);
    const [isLoading, setIsLoading] = React.useState<boolean>(true);
    const [isAuthenticated, setIsAuthenticated] = React.useState<boolean>(false);
    const tokenRef = React.useRef<string>(undefined);
    const tokenParsedRef = React.useRef<any>(undefined);
    const userManagerRef = React.useRef<UserManager>(undefined);
    const isAuthCallback = isCurrentPathMatching(oidcAuthConfig?.redirect_uri, true);
    const isAuthSilentRedirect = oidcAuthConfig?.silent_redirect_uri
        ? isCurrentPathMatching(oidcAuthConfig?.silent_redirect_uri, false)
        : false;
    const processUser = (user: User | null) => {
        if (user != null) {
            tokenRef.current = user.access_token;
            tokenParsedRef.current = user.profile;
            setIsLoading(false);
            setIsAuthenticated(true);
        } else {
            tokenRef.current = undefined;
            tokenParsedRef.current = undefined;
            setIsLoading(false);
            setIsAuthenticated(false);
        }
    };
    React.useEffect(() => {
        if (hasInitialized.current) {
            return; // evitem executar-ho la segona vegada (en dev amb StrictMode)
        }
        hasInitialized.current = true;
        const userManager = userManagerNewInstance(oidcAuthConfig);
        userManagerRef.current = userManager;
        userManager.startSilentRenew();
        const handleAuthFlow = async () => {
            try {
                if (isAuthSilentRedirect) {
                    debug && logConsole.debug('Callback de la renovació silenciosa');
                    await userManager.signinSilentCallback();
                } else if (isAuthCallback) {
                    debug && logConsole.debug('Callback des del servidor de recursos');
                    await userManager.signinRedirectCallback();
                    window.history.replaceState({}, document.title, '/');
                } else {
                    debug && logConsole.debug("Comprovant si l'usuari ja està autenticat");
                    const loadedUser = await userManager.getUser();
                    if (loadedUser && !loadedUser.expired) {
                        debug && logConsole.debug("S'ha trobat un usuari autenticat");
                        processUser(loadedUser);
                    } else {
                        debug && logConsole.debug('Provant renovació silenciosa');
                        try {
                            const user = await userManager.signinSilent();
                            debug &&
                                logConsole.debug(
                                    'Usuari resultant de la renovació silenciosa',
                                    user
                                );
                            processUser(user);
                        } catch (error: any) {
                            // 'login_required' -> no hi ha sessió SSO activa a Keycloak.
                            // 'invalid_grant' -> el refresh token (o la sessió a Keycloak) ha expirat.
                            // En tots dos casos cal refer el login, no quedar-nos
                            // indefinidament sense resoldre l'estat d'autenticació.
                            const requiresSignin = ['login_required', 'invalid_grant'].includes(
                                error.error
                            );
                            if (mandatory && requiresSignin) {
                                debug &&
                                    logConsole.debug(
                                        'La renovació silenciosa ha fallat amb un codi ' +
                                            error.error +
                                            '. Redirigint a signin.'
                                    );
                                userManager.removeUser();
                                userManager.signinRedirect();
                            } else {
                                debug &&
                                    logConsole.debug('Error en la renovació silenciosa', error);
                                processUser(null);
                            }
                        }
                    }
                }
            } catch (error) {
                logConsole.error("Error durant el flux d'autenticació", error);
                processUser(null);
            }
        };
        handleAuthFlow();
        return () => {
            userManager.stopSilentRenew();
        };
    }, [config, debug, isAuthCallback, isAuthSilentRedirect, mandatory, logConsole]);
    React.useEffect(() => {
        const userManager = userManagerRef.current;
        if (userManager) {
            // NOTA: aquí NO tornam a cridar `signinSilent()` en resposta a `accessTokenExpiring`.
            // Com que `oidcAuthConfig` té `automaticSilentRenew: true` i cridam `userManager.startSilentRenew()`
            // a l'altre efecte, la pròpia llibreria `oidc-client-ts` ja escolta aquest mateix esdeveniment i fa
            // la renovació internament (`SilentRenewService`).
            // Fer-ho també aquí (com es feia abans) provocava DUES crides `signinSilent()` concurrents amb el mateix
            // refresh token cada vegada que el token estava a punt d'expirar; si el realm de Keycloak té activada la
            // rotació de refresh tokens, una de les dues sempre falla amb `invalid_grant` (encara que la sessió sigui
            // vàlida), la qual cosa forçava un `signinRedirect()` -> navegació completa de pàgina -> l'usuari
            // perdia el que tingués a mig fer en aquell moment.
            // Ara només reaccionam al resultat de la renovació (feta una única vegada per la llibreria).
            const onUserLoaded = (user: User) => {
                debug && logConsole.debug('Token renovat correctament', user);
                processUser(user);
            };
            const handleRenewalError = (error: any) => {
                logConsole.error('Error renovant el token', error);
                // Forçam redirecció cap a la pantalla de login si la renovació falla
                if (['login_required', 'invalid_grant'].includes(error.error)) {
                    debug && logConsole.debug('Redirigint cap a la pantalla de login');
                    userManagerRef.current?.removeUser();
                    mandatory && userManagerRef.current?.signinRedirect();
                } else {
                    debug &&
                        logConsole.debug('No redirigim cap a la pantalla de login', error.error);
                }
            };
            userManager.events.addUserLoaded(onUserLoaded);
            userManager.events.addSilentRenewError(handleRenewalError);
            // El temporitzador intern de renovació (`SilentRenewService`, basat en
            // `setTimeout`) pot arribar tard si la pestanya ha estat en segon pla: els
            // navegadors retarden/pausen els temporitzadors de les pestanyes no visibles,
            // de manera que quan l'usuari hi torna el token pot fer estona que ha caducat
            // sense que s'hagi intentat renovar. En tornar a fer-se visible la pestanya,
            // comprovam explícitament l'estat del token i, si cal, en forçam la renovació.
            const onVisibilityChange = () => {
                if (document.visibilityState !== 'visible') {
                    return;
                }
                userManagerRef.current
                    ?.getUser()
                    .then((user) => {
                        if (!user || user.expired) {
                            debug &&
                                logConsole.debug(
                                    'Token caducat en tornar a la pestanya, renovant-lo'
                                );
                            return userManagerRef.current
                                ?.signinSilent()
                                .then((user) => processUser(user ?? null))
                                .catch(handleRenewalError);
                        }
                    })
                    .catch((error) => logConsole.error("Error comprovant l'usuari", error));
            };
            document.addEventListener('visibilitychange', onVisibilityChange);
            return () => {
                userManager.events.removeUserLoaded(onUserLoaded);
                userManager.events.removeSilentRenewError(handleRenewalError);
                document.removeEventListener('visibilitychange', onVisibilityChange);
            };
        }
    }, [debug, logConsole, mandatory]);
    const signIn = isLoading
        ? undefined
        : () => {
              userManagerRef.current?.signinRedirect();
          };
    const signOut = isLoading
        ? undefined
        : () => {
              userManagerRef.current?.signoutRedirect();
          };
    const context = {
        isLoading,
        isReady: !isLoading,
        isAuthenticated,
        bearerTokenActive: true,
        getToken: () => tokenRef.current,
        getTokenParsed: () => tokenParsedRef.current,
        getUserId: () => tokenParsedRef.current?.['preferred_username'],
        getUserName: () => tokenParsedRef.current?.['name'],
        getUserEmail: () => tokenParsedRef.current?.['email'],
        signIn,
        signOut,
        config,
    };
    const showChildren =
        !isLoading && !isAuthSilentRedirect && (!mandatory || (mandatory && isAuthenticated));
    return (
        <AuthContext.Provider value={context}>
            {showChildren ? children : null}
        </AuthContext.Provider>
    );
};
