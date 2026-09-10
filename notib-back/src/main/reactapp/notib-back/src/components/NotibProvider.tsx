import React from 'react';
import { useTranslation } from 'react-i18next';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import CircularProgress from '@mui/material/CircularProgress';
import { useAuthContext, useResourceApiContext, useResourceApiService } from 'reactlib';
import {
    NotibContext,
    ROLE_PREFIX,
    ROLE_SUPER,
    ROLE_ADMIN,
    ROLE_ADMIN_LECTURA,
    ROLE_ORGAN,
    ROLE_APLICACIO,
    ROLE_USER,
} from './NotibContext';
import { getAuthRolesUrl } from '../appUrls';

const ALLOWED_ROLES = [ROLE_APLICACIO, ROLE_SUPER, ROLE_ADMIN, ROLE_ADMIN_LECTURA, ROLE_ORGAN, ROLE_USER].reverse();

export const notibChannel = new BroadcastChannel('notib');
type CurrentSession = Readonly<{
    role?: string;
    entitatId?: number;
    organId?: number;
}>;

const useBroadcastSession = () => {

    const [session, setSessionState] = React.useState<CurrentSession>({});

    const setSession = React.useCallback(
        (
            update:
                | Partial<CurrentSession>
                | ((previous: CurrentSession) => Partial<CurrentSession>),
            broadcast = true
        ) => {

            setSessionState(previous => {

                const changes =
                    typeof update === "function"
                        ? update(previous)
                        : update;

                const next = {
                    ...previous,
                    ...changes,
                };

                if (broadcast) {
                    notibChannel.postMessage(next);
                }

                return next;
            });

        },
        []
    );

    React.useEffect(() => {

        const listener = ({ data }: MessageEvent<CurrentSession>) => {
            if (!data) {
                return;
            }
            setSession(data, false);
        };

        notibChannel.addEventListener("message", listener);

        return () =>
            notibChannel.removeEventListener("message", listener);

    }, [setSession]);

    return {
        session,
        setSession,
    };

};

type BroadcastSession = ReturnType<typeof useBroadcastSession>;

const decodeJwt = (token: string) => {

    const payload = token.split('.')[1];
    const base64 = payload.replace(/-/g, '+').replace(/_/g, '/');
    return JSON.parse(atob(base64));
};

// Es fa servir per tornar a consultar els rols disponibles quan l'usuari torna a la pestanya/finestra
// (p.ex. després que un administrador li hagi concedit un permís nou), sense haver de tancar sessió.
const useWindowFocusTrigger = () => {

    const [trigger, setTrigger] = React.useState(0);
    React.useEffect(() => {
        const handleFocus = () => setTrigger((t) => t + 1);
        window.addEventListener('focus', handleFocus);
        return () => window.removeEventListener('focus', handleFocus);
    }, []);
    return trigger;
};

// Es fa servir localStorage (en lloc de sessionStorage) perquè el darrer rol/entitat/òrgan
// utilitzats es recordin també entre sessions del navegador (p.ex. en tornar a obrir-lo), no
// només mentre es manté la mateixa pestanya oberta.
const useLocalStorage = (...keyParts: any[]) => {

    const key = keyParts.map((p) => (typeof p === 'object' && p !== null ? JSON.stringify(p) : String(p))).join('|');
    const getValue = () => localStorage.getItem(key);
    const setValue = (value: string | null) => {
        if (value == null) {
            localStorage.removeItem(key);
            return;
        }
        localStorage.setItem(key, value);
    };
    return {getValue, setValue,};
};

const useCurrentUser = () => {

    const {isReady: apiIsReady, find: apiFind, currentFields: apiFields,} = useResourceApiService('usuariResource');
    const [currentUser, setCurrentUser] = React.useState<string>();
    const [currentUserGridPageSizeOptions, setCurrentUserGridPageSizeOptions] = React.useState<number[]>();
    React.useEffect(() => {
        if (!apiIsReady) {
            return;
        }
        void apiFind({ unpaged: true }).then((response) => {
            if (response.rows.length) {
                setCurrentUser(response.rows[0]);
            }
        });
        const gridPageSizeOptionsField = apiFields?.find((f) => f.name === 'numElementsPaginaDefecte');
        const gridPageSizeOptions = gridPageSizeOptionsField != null ? Object.values(gridPageSizeOptionsField?.options).map((v: any) => Number.parseInt(v)) : [10, 20, 50, 100];
        if (!gridPageSizeOptions.includes(-1)) {
            gridPageSizeOptions.unshift(-1);
        }
        setCurrentUserGridPageSizeOptions(gridPageSizeOptions);
    }, [apiIsReady]);
    return { currentUser, setCurrentUser, currentUserGridPageSizeOptions };
};

const useMaxResultSelects = (currentRole: string | undefined) => {

    // El rol aplicació no té accés al recurs configResource.
    const { isReady: apiIsReady, find: apiFind } = useResourceApiService('configResource', { enabled: currentRole !== ROLE_APLICACIO });
    const [maxResultSelects, setMaxResultSelects] = React.useState<number>();
    React.useEffect(() => {
        if (!apiIsReady) {
            return;
        }
        const args = { filter: "key: 'es.caib.notib.app.maxresults.selects'", unpaged: true };
        apiFind(args).then((response) => {
            const value = response.rows?.[0]?.value;
            setMaxResultSelects(value != null ? Number.parseInt(value) : undefined);
        }).catch((error) => console.error('Error obtenint el nombre màxim de resultats als desplegables:', error));
    }, [apiIsReady]);
    return maxResultSelects;
};

const useCurrentRole = (broadcast: BroadcastSession) => {

    const {
        isReady: authIsReady,
        getUserId: authGetUserId,
        getToken: authGetToken,
        bearerTokenActive,
    } = useAuthContext();
    const { httpHeaders: apiHttpHeaders, setHttpHeaders: apiSetHttpHeaders } = useResourceApiContext();
    const [currentUserId, setCurrentUserId] = React.useState<string>();
    const [rolesAvailable, setRolesAvailable] = React.useState<string[]>();
    const {
        session,
        setSession
    } = broadcast;

    const currentRole = session.role;

    const setCurrentRole = (role?: string) => setSession({role, entitatId: undefined, organId: undefined});
    const { getValue: roleStorageGetValue, setValue: roleStorageSetValue } = useLocalStorage(currentUserId, 'currentRole');
    const focusTrigger = useWindowFocusTrigger();
    React.useEffect(() => {
        // Obté els rols disponibles del token JWT o de __AUTH_ROLES__. Es torna a consultar quan la
        // finestra recupera el focus (focusTrigger) perquè, si un administrador ha concedit o revocat
        // un permís mentre l'usuari tenia l'aplicació oberta, el canvi es reflecteixi sense haver de
        // tancar sessió; com que currentRole ja està establert, això no en força el canvi, només
        // n'actualitza la llista de disponibles.
        if (!authIsReady) {
            return;
        }
        const userId = authGetUserId();
        setCurrentUserId(userId);
        const token = authGetToken();
        if (token == null) {
            return;
        }
        const tokenDecoded = decodeJwt(token);
        if (tokenDecoded.realm_access == null) {
            const windowAuthRoles = (window as any).__AUTH_ROLES__ ?? [];
            const rolesAvailable = ALLOWED_ROLES.filter((a) => windowAuthRoles.includes(a));
            setRolesAvailable(rolesAvailable);
            return;
        }
        // Els rols del token només inclouen els gestionats des de Keycloak: n'hi ha (com l'administrador
        // d'òrgan) que es concedeixen des de NOTIB i no hi apareixen mai. Es consulten sempre al
        // servidor, que és qui coneix els permisos reals; si la consulta falla es cau als rols del
        // propi token, per no deixar l'aplicació sense cap rol disponible.
        // Només enviam el Bearer quan l'autenticació és per token (OidcAuthProvider): amb
        // ContainerAuthProvider (bearerTokenActive=false) l'autenticació és per sessió/cookie i aquest
        // endpoint no valida cap Bearer -Keycloak, quan detecta la capçalera Authorization, intenta
        // autenticar la petició amb el token en lloc d'amb la sessió ja establerta, i si aquesta
        // validació "bearer-only" falla (com passa en aquest mode, no pensat per anar per aquí) es rep
        // un 401 encara que la sessió sigui perfectament vàlida.
        fetch(getAuthRolesUrl(), bearerTokenActive ? { headers: { Authorization: 'Bearer ' + token } } : undefined).
            then((response) => response.ok ? response.json() : Promise.reject(response.status)).
            then((serverRoles: string[]) => {
                setRolesAvailable(ALLOWED_ROLES.filter((a) => serverRoles.includes(a)));
            }).
            catch((error) => {
                console.error('Error obtenint els rols disponibles des del servidor:', error);
                const realmRoles = tokenDecoded.realm_access?.roles?.filter((r: string) => r === ROLE_USER || r.startsWith(ROLE_PREFIX)) ?? [];
                setRolesAvailable(ALLOWED_ROLES.filter((a) => realmRoles.includes(a)));
            });
    }, [authIsReady, focusTrigger]);

    React.useEffect(() => {
        // Configura l'estat amb el rol actual si aquest encara no s'ha inicialitzat i els rols disponibles ja s'han obtingut
        if (rolesAvailable == null || currentRole != null) {
            return;
        }
        const storedValue = roleStorageGetValue();
        const isStoredValueInRolesAvailable = storedValue != null && rolesAvailable?.includes(storedValue);
        if (storedValue != null && isStoredValueInRolesAvailable) {
            setCurrentRole(storedValue);
        } else if (rolesAvailable?.length && currentRole == null) {
            setCurrentRole(rolesAvailable[0]);
        }
    }, [rolesAvailable, currentRole]);

    React.useEffect(() => {
        // Desa al local storage i a la capçalera HTTP el rol actual quan aquest canvia
        if (currentRole === undefined) {
            return;
        }
        roleStorageSetValue(currentRole);
        if (currentRole) {
            apiSetHttpHeaders([{ 'X-App-Role': currentRole }]);
        }
    }, [currentRole]);
    const currentRoleFromHttpHeader = apiHttpHeaders?.find((h) => 'X-App-Role' in h)?.['X-App-Role'];
    const roleHttpHeaderInitialized = currentRole != null && currentRole === currentRoleFromHttpHeader;
    return {
        currentUserId,
        currentRole,
        currentRoleReady: roleHttpHeaderInitialized,
        rolesAvailable,
        setCurrentRole,
    };
};

const useCurrentEntitat = (
    broadcast: BroadcastSession,
    currentUserId: string | undefined,
    currentRole: string | undefined,
    currentRoleReady: boolean
) => {

    const { httpHeaders: apiHttpHeaders, setHttpHeaders: apiSetHttpHeaders } = useResourceApiContext();
    // El rol aplicació no té accés al recurs entitatResource (no apareix ni al document arrel de l'API), per tant
    // cal no consultar-lo per a aquest rol: quedaria bloquejat esperant per sempre un recurs que mai estarà llest.
    const roleWithoutEntitat = currentRole === ROLE_APLICACIO;
    const {isReady: apiIsReady, find: apiFind, getOne: apiGetOne} = useResourceApiService('entitatResource', { enabled: !roleWithoutEntitat });
    const [entitatsAvailable, setEntitatsAvailable] = React.useState<any[]>();
    const [currentEntitatLoading, setCurrentEntitatLoading] = React.useState<boolean>();
    const [currentEntitat, setCurrentEntitat] = React.useState<any>();
    const { getValue: entitatOrganStorageGetValue, setValue: entitatOrganStorageSetValue } = useLocalStorage(currentUserId, 'currentSession');
    const { isReady: apiIsReadyOrgan, artifactAction: apiAction } = useResourceApiService('organGestorResource', { enabled: currentRole === ROLE_ORGAN });
    const [organsAvailable, setOrgansAvailable] = React.useState<any[]>();
    const {
        session,
        setSession
    } = broadcast;

    const currentEntitatId = session.entitatId;
    const currentOrganId = session.organId;

    const setCurrentEntitatId = (id?: number) => setSession({ entitatId: id });

    const setCurrentOrganId = (id?: number) => setSession({ organId: id });
    React.useEffect(() => {
        if (!currentRoleReady || currentRole == null) {
            return;
        }
        if (roleWithoutEntitat) {
            setEntitatsAvailable([]);
            setCurrentEntitat(undefined);
            setCurrentEntitatId(undefined);
            setOrgansAvailable(undefined);
            return;
        }
        if (!apiIsReady) {
            return;
        }
        setEntitatsAvailable(undefined);
        setCurrentEntitat(undefined);
        setCurrentEntitatId(undefined);
        if (currentRole !== ROLE_ORGAN) {
            setOrgansAvailable(undefined);
        }

        if (currentRole === ROLE_SUPER) {
            setEntitatsAvailable([]);
            return;
        }
        apiFind({ unpaged: true }).then((response) => {
            const entitatsAvailable = response.rows;
            setEntitatsAvailable(entitatsAvailable);

            const storedSession = entitatOrganStorageGetValue();

            const parsedSession = storedSession ? JSON.parse(storedSession) : {};
            const sessionValue = parsedSession.e;
            const isSessionValueInEntitatsAvailable = entitatsAvailable.map((e) => e.id).includes(sessionValue);
            // Si l'entitat actual (la darrera seleccionada, es mantengui o no amb el nou rol) té permís
            // amb el rol actual es manté; en cas contrari se selecciona la primera entitat disponible.
            if (isSessionValueInEntitatsAvailable) {
                setCurrentEntitatId(sessionValue);
            } else if (entitatsAvailable?.length) {
                setCurrentEntitatId(entitatsAvailable[0].id);
            }
            if (!apiIsReadyOrgan || currentRole !== ROLE_ORGAN) {
                return;
            }
            apiAction(undefined, { code: 'ADMIN_ORGANS_AMB_PERMIS' }).then(resposta => {

                const organs = resposta.organs ?? [];
                setOrgansAvailable(organs)
                const organActual = parsedSession.o;
                if (organActual != null && organs.some((o: { id: any; }) => o.id === organActual)) {
                    setCurrentOrganId(organActual);
                } else if (organs.length) {
                    setCurrentOrganId(organs[0].id);
                }
            }).catch((error) => {
                console.error(error);
                setOrgansAvailable([]);
            })
        }).catch((error) => {
            // Si la consulta d'entitats falla (p.ex. l'usuari no té cap permís concedit) es considera que no
            // té accés a cap entitat, en lloc de deixar l'aplicació carregant indefinidament.
            console.error('Error obtenint les entitats disponibles:', error);
            setEntitatsAvailable([]);
            setOrgansAvailable([]);
        });
    }, [apiIsReady, apiIsReadyOrgan, currentRoleReady, currentRole]);

    const entitatsFocusTrigger = useWindowFocusTrigger();
    React.useEffect(() => {
        // Quan la finestra recupera el focus es torna a consultar la llista d'entitats disponibles en
        // segon pla (sense el reset "carregant" complet de l'efecte anterior, per no fer parpellejar
        // l'aplicació), perquè un permís concedit o revocat mentre l'usuari tenia l'aplicació oberta es
        // reflecteixi sense haver de tancar sessió. Si l'entitat actual ha deixat de tenir permís es
        // canvia a la primera disponible.
        if (entitatsFocusTrigger === 0 || roleWithoutEntitat || !apiIsReady || !currentRoleReady || currentRole == null || currentRole === ROLE_SUPER) {
            return;
        }
        apiFind({ unpaged: true }).then((response) => {
            const freshEntitats = response.rows;
            setEntitatsAvailable(freshEntitats);
            if (currentEntitatId != null && !freshEntitats.some((e: any) => e.id === currentEntitatId)) {
                setCurrentEntitatId(freshEntitats.length ? freshEntitats[0].id : undefined);
            }
        }).catch((error) => console.error('Error refrescant les entitats disponibles:', error));
    }, [entitatsFocusTrigger]);

    React.useEffect(() => {
        if (currentRole == null || currentRole === ROLE_SUPER || currentEntitatId == null) {
            return;
        }

        const sessionJson = JSON.stringify({
            e: currentEntitatId,
            ...(currentOrganId != null && { o: currentOrganId }),
        });
        entitatOrganStorageSetValue(sessionJson);

        apiSetHttpHeaders([
            { "X-App-Role": currentRole },
            { "X-App-Session": sessionJson },
        ]);
    }, [currentRole, currentEntitatId, currentOrganId]);

    React.useEffect(() => {
        if (!apiIsReady || currentEntitatId == null || entitatsAvailable == null) {
            return;
        }
        const entitatExisteix = entitatsAvailable.some(e => e.id === currentEntitatId);
        if (!entitatExisteix) {
            return;
        }
        setCurrentEntitatLoading(true);
        apiGetOne(currentEntitatId, {perspectives: ['PERMISSIONS']})
        .then(setCurrentEntitat)
        .finally(() => setCurrentEntitatLoading(false));

    }, [apiIsReady, currentEntitatId, entitatsAvailable,]);

    const currentSessionFromHttpHeader = apiHttpHeaders?.find((h) => 'X-App-Session' in h)?.['X-App-Session'];
    const currentEntitatIdFromHttpHeader = currentSessionFromHttpHeader != null ? JSON.parse(currentSessionFromHttpHeader).e : undefined;
    const entitatIdHttpHeaderInitialized = currentRole === ROLE_SUPER
        || (currentEntitatId == null && currentEntitatIdFromHttpHeader == null)
        || currentEntitatId === currentEntitatIdFromHttpHeader;
    // Per a l'administrador d'òrgan cal esperar que el selector d'òrgans de la capçalera hagi rebut els valors
    // abans de donar l'aplicació per carregada, per no mostrar-lo momentàniament buit.
    const organsReady = currentRole !== ROLE_ORGAN || organsAvailable != null;
    const currentEntitatReady = roleWithoutEntitat || (apiIsReady && entitatsAvailable != null && entitatIdHttpHeaderInitialized && organsReady);
    return {
        currentEntitatId,
        currentEntitatReady,
        currentEntitat,
        currentEntitatLoading,
        entitatsAvailable,
        setCurrentEntitatId,
        organsAvailable,
        currentOrganId,
        setCurrentOrganId
    };
};

const NotibProviderLoading: React.FC = () => {

    const { t } = useTranslation();
    return (
        <Box sx={{display: 'flex', flexDirection: 'column', justifyContent: 'center', alignItems: 'center', height: '100vh',}}>
            <CircularProgress size={70} />
            <Typography sx={{ mt: 1 }}>{t('app.loading')}</Typography>
        </Box>
    );
};

const NotibProviderNoAccess: React.FC = () => {

    const { t } = useTranslation();
    return (
        <Box sx={{display: 'flex', flexDirection: 'column', justifyContent: 'center', alignItems: 'center', height: '100vh',}}>
            <Typography variant="h6">{t('app.sensePermisos')}</Typography>
        </Box>
    );
};

export const NotibProvider: React.FC<React.PropsWithChildren> = ({ children }) => {

    const { offline: apiOffline } = useResourceApiContext();
    const broadcast = useBroadcastSession();
    const { currentUserId, currentRole, currentRoleReady, rolesAvailable, setCurrentRole } = useCurrentRole(broadcast);
    const { currentUser, setCurrentUser, currentUserGridPageSizeOptions } = useCurrentUser();
    const maxResultSelects = useMaxResultSelects(currentRole);
    const {
        currentEntitatId,
        currentEntitatReady,
        currentEntitat,
        currentEntitatLoading,
        entitatsAvailable,
        setCurrentEntitatId,
        organsAvailable,
        currentOrganId,
        setCurrentOrganId
    } = useCurrentEntitat(broadcast, currentUserId, currentRole, currentRoleReady);
    const isReady = apiOffline || (currentRoleReady && currentEntitatReady && currentUser != null);
    // Un usuari autenticat sense cap dels rols de NOTIB no arribarà mai a tenir un rol actual, per tant cal
    // distingir aquest cas del de "encara carregant" per no deixar l'aplicació carregant indefinidament.
    const noRolesAvailable = rolesAvailable != null && rolesAvailable.length === 0;
    // Un cop l'aplicació ha arribat a estar llesta una vegada, canviar de rol o d'entitat torna a posar
    // isReady a false momentàniament (es tornen a demanar entitats/òrgans). No es vol tornar a mostrar
    // l'spinner de pàgina completa en aquest cas (faria l'efecte de refrescar tota l'aplicació): un cop
    // superada la càrrega inicial es continuen mostrant els fills, encara que isReady torni a ser false.
    const hasBeenReadyRef = React.useRef(false);
    if (isReady) {
        hasBeenReadyRef.current = true;
    }
    const contextValue = {
        isReady,
        currentUser,
        setCurrentUser,
        currentUserGridPageSizeOptions,
        maxResultSelects,
        rolesAvailable,
        currentRole,
        setCurrentRole,
        entitatsAvailable,
        currentEntitatId,
        setCurrentEntitatId,
        currentEntitat,
        currentEntitatLoading,
        organsAvailable,
        currentOrganId,
        setCurrentOrganId
    };
    return (
        <NotibContext.Provider value={contextValue}>
            {noRolesAvailable ? <NotibProviderNoAccess /> : (isReady || hasBeenReadyRef.current) ? children : <NotibProviderLoading />}
        </NotibContext.Provider>
    );
};

export default NotibProvider;
