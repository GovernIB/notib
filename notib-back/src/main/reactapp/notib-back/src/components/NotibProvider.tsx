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
    ROLE_USER,
} from './NotibContext';

const ALLOWED_ROLES = [ROLE_SUPER, ROLE_ADMIN, ROLE_ADMIN_LECTURA, ROLE_ORGAN, ROLE_USER].reverse();

const decodeJwt = (token: string) => {

    const payload = token.split('.')[1];
    const base64 = payload.replace(/-/g, '+').replace(/_/g, '/');
    return JSON.parse(atob(base64));
};

const useSessionStorage = (...keyParts: any[]) => {

    const key = keyParts.map((p) => (typeof p === 'object' && p !== null ? JSON.stringify(p) : String(p))).join('|');
    const getValue = () => sessionStorage.getItem(key);
    const setValue = (value: string | null) => {
        if (value == null) {
            sessionStorage.removeItem(key);
            return;
        }
        sessionStorage.setItem(key, value);
    };
    return {getValue, setValue,};
};

const createSession = (entitatId?: number, organId?: number) => {

    const sessionObject = {
        ...(entitatId != null && { e: entitatId }),
        ...(organId != null && { o: organId }),
    };
    return JSON.stringify(sessionObject);
};
const getSessionValue = (json: string | undefined, field: string) => {
    if (json != null) {
        const parsed = JSON.parse(json);
        return parsed[field];
    }
};

const useCurrentUser = () => {

    const {isReady: apiIsReady, find: apiFind, currentFields: apiFields,} = useResourceApiService('usuariResource');
    const [currentUser, setCurrentUser] = React.useState<string>();
    const [currentUserGridPageSizeOptions, setCurrentUserGridPageSizeOptions] = React.useState<number[]>();
    React.useEffect(() => {
        if (!apiIsReady) {
            return;
        }
        apiFind({ unpaged: true }).then((response) => {
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

const useCurrentRole = () => {

    const {isReady: authIsReady, getUserId: authGetUserId, getToken: authGetToken,} = useAuthContext();
    const { httpHeaders: apiHttpHeaders, setHttpHeaders: apiSetHttpHeaders } = useResourceApiContext();
    const [currentUserId, setCurrentUserId] = React.useState<string>();
    const [rolesAvailable, setRolesAvailable] = React.useState<string[]>();
    const [currentRole, setCurrentRole] = React.useState<string>();
    const { getValue: roleSessionGetValue, setValue: roleSessionSetValue } = useSessionStorage(currentUserId, 'currentRole');
    React.useEffect(() => {
        // Obté els rols disponibles del token JWT o de __AUTH_ROLES__
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
        const realmRoles = tokenDecoded.realm_access?.roles?.filter((r: string) => r === ROLE_USER || r.startsWith(ROLE_PREFIX)) ?? [];
        const rolesAvailable = ALLOWED_ROLES.filter((a) => realmRoles.includes(a));
        setRolesAvailable(rolesAvailable);
    }, [authIsReady]);

    React.useEffect(() => {
        // Configura l'estat amb el rol actual si aquest encara no s'ha inicialitzat i els rols disponibles ja s'han obtingut
        if (rolesAvailable == null || currentRole != null) {
            return;
        }
        const sessionValue = roleSessionGetValue();
        const isSessionValueInRolesAvailable = sessionValue != null && rolesAvailable?.includes(sessionValue);
        if (sessionValue != null && isSessionValueInRolesAvailable) {
            setCurrentRole(sessionValue);
        } else if (rolesAvailable?.length && currentRole == null) {
            setCurrentRole(rolesAvailable[0]);
        }
    }, [rolesAvailable, currentRole]);

    React.useEffect(() => {
        // Configura el session storage i la capçalera HTTP amb el rol actual quan aquest canvia
        if (currentRole === undefined) {
            return;
        }
        roleSessionSetValue(currentRole);
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

const useCurrentEntitat = (currentUserId: string | undefined, currentRole: string | undefined, currentRoleReady: boolean) => {

    const { httpHeaders: apiHttpHeaders, setHttpHeaders: apiSetHttpHeaders } = useResourceApiContext();
    const {isReady: apiIsReady, find: apiFind, getOne: apiGetOne} = useResourceApiService('entitatResource');
    const [entitatsAvailable, setEntitatsAvailable] = React.useState<any[]>();
    const [currentEntitatId, setCurrentEntitatId] = React.useState<number>();
    const [currentEntitatLoading, setCurrentEntitatLoading] = React.useState<boolean>();
    const [currentEntitat, setCurrentEntitat] = React.useState<any>();
    const { getValue: sessionSessionGetValue, setValue: sessionSessionSetValue } = useSessionStorage(currentUserId, 'currentSession');
    const { isReady: apiIsReadyOrgan, artifactAction: apiAction } = useResourceApiService('organGestorResource', { enabled: currentRole === ROLE_ORGAN });
    const [organsAvailable, setOrgansAvailable] = React.useState<any[]>([]);
    const [currentOrganId, setCurrentOrganId] = React.useState<number>();

    React.useEffect(() => {
        if (!apiIsReady || !currentRoleReady || currentRole == null) {
            return;
        }
        setCurrentEntitatId(undefined);
        if (currentRole === ROLE_SUPER) {
            setEntitatsAvailable([]);
            return;
        }
        apiFind({ unpaged: true }).then((response) => {
            const entitatsAvailable = response.rows;
            setEntitatsAvailable(entitatsAvailable);
            const sessionValue = getSessionValue(sessionSessionGetValue() ?? undefined, 'e');
            const isSessionValueInEntitatsAvailable = entitatsAvailable.map((e) => e.id).includes(sessionValue);
            if (isSessionValueInEntitatsAvailable) {
                setCurrentEntitatId(sessionValue);
            } else if (entitatsAvailable?.length && currentEntitatId == null) {
                setCurrentEntitatId(entitatsAvailable[0].id);
            }
            if (!apiIsReadyOrgan || currentRole !== ROLE_ORGAN) {
                return;
            }
            apiAction(undefined, { code: 'ADMIN_ORGANS_AMB_PERMIS' }).then(resposta => {

                const organs = resposta.organs ?? [];
                setOrgansAvailable(organs)
                const organActual = getSessionValue(sessionSessionGetValue() ?? undefined, 'o');
                if (organActual != null && organs.some((o: { id: any; }) => o.id === organActual)) {
                    setCurrentOrganId(organActual);
                } else if (organs.length) {
                    setCurrentOrganId(organs[0].id);
                }
            }).catch(error => console.error(error))
        });
    }, [apiIsReady, apiIsReadyOrgan, currentRoleReady, currentRole]);

    React.useEffect(() => {
        if (currentRole == null || currentRole === ROLE_SUPER || currentEntitatId == null) {
            return;
        }
        const session = createSession(currentEntitatId, currentRole === ROLE_ORGAN ? currentOrganId : undefined);
        sessionSessionSetValue(session);
        if (currentRole) {
            apiSetHttpHeaders([{'X-App-Role': currentRole,}, {'X-App-Session': session,},]);
        }
    }, [currentRole, currentEntitatId, currentOrganId]);

    React.useEffect(() => {
        if (currentEntitatId == null || !apiIsReady) {
            return;
        }
        setCurrentEntitatLoading(true);
        apiGetOne(currentEntitatId, { perspectives: ['PERMISSIONS'] }).then(setCurrentEntitat).finally(() => setCurrentEntitatLoading(false));
    }, [currentEntitatId]);

    const currentSessionFromHttpHeader = apiHttpHeaders?.find((h) => 'X-App-Session' in h)?.['X-App-Session'];
    const currentEntitatIdFromHttpHeader = currentSessionFromHttpHeader != null ? JSON.parse(currentSessionFromHttpHeader).e : undefined;
    const entitatIdHttpHeaderInitialized = currentRole === ROLE_SUPER
        || (currentEntitatId == null && currentEntitatIdFromHttpHeader == null)
        || currentEntitatId === currentEntitatIdFromHttpHeader;
    const currentEntitatReady = apiIsReady && entitatsAvailable != null && entitatIdHttpHeaderInitialized;
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

export const NotibProvider: React.FC<React.PropsWithChildren> = ({ children }) => {

    const { offline: apiOffline } = useResourceApiContext();
    const { currentUserId, currentRole, currentRoleReady, rolesAvailable, setCurrentRole } = useCurrentRole();
    const { currentUser, setCurrentUser, currentUserGridPageSizeOptions } = useCurrentUser();
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
    } = useCurrentEntitat(currentUserId, currentRole, currentRoleReady);
    const isReady = apiOffline || (currentRoleReady && currentEntitatReady && currentUser != null);
    const contextValue = {
        isReady,
        currentUser,
        setCurrentUser,
        currentUserGridPageSizeOptions,
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
            {isReady ? children : <NotibProviderLoading />}
        </NotibContext.Provider>
    );
};

export default NotibProvider;
