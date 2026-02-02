import React from 'react';
import { useAuthContext, useResourceApiContext, useResourceApiService } from 'reactlib';
import {
    NotibContext,
    ROLE_PREFIX,
    ROLE_SUPER,
    ROLE_ADMIN,
    ROLE_ADMIN_CONSULTA,
    ROLE_ORGAN,
    ROLE_USER,
} from './NotibContext';

const ALLOWED_ROLES = [
    ROLE_SUPER,
    ROLE_ADMIN,
    ROLE_ADMIN_CONSULTA,
    ROLE_ORGAN,
    ROLE_USER,
].reverse();

const decodeJwt = (token: string) => {
    const payload = token.split('.')[1];
    const base64 = payload.replace(/-/g, '+').replace(/_/g, '/');
    return JSON.parse(atob(base64));
};

const useSessionStorage = (...keyParts: any[]) => {
    const key = keyParts
        .map((p) => (typeof p === 'object' && p !== null ? JSON.stringify(p) : String(p)))
        .join('|');
    const getValue = () => {
        return sessionStorage.getItem(key);
    };
    const setValue = (value: string | null) => {
        if (value != null) {
            sessionStorage.setItem(key, value);
        } else {
            sessionStorage.removeItem(key);
        }
    };
    return {
        getValue,
        setValue,
    };
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

export const NotibProvider: React.FC<React.PropsWithChildren> = ({ children }) => {
    const {
        isReady: authIsReady,
        getUserId: authGetUserId,
        getToken: authGetToken,
    } = useAuthContext();
    const { setHttpHeaders: apiSetHttpHeaders } = useResourceApiContext();
    const { isReady: apiIsReady, find: apiFind } = useResourceApiService('entitatResource');
    const [currentUserId, setCurrentUserId] = React.useState<string>();
    const [rolesAvailable, setRolesAvailable] = React.useState<string[]>();
    const [entitatsAvailable, setEntitatsAvailable] = React.useState<any[]>();
    const [currentRole, setCurrentRole] = React.useState<string>();
    const [currentEntitatId, setCurrentEntitatId] = React.useState<number>();
    const { getValue: roleSessionGetValue, setValue: roleSessionSetValue } = useSessionStorage(
        currentUserId,
        'currentRole'
    );
    const { getValue: sessionSessionGetValue, setValue: sessionSessionSetValue } =
        useSessionStorage(currentUserId, 'currentSession');
    const isReady = rolesAvailable != null && entitatsAvailable != null;
    React.useEffect(() => {
        if (authIsReady) {
            const userId = authGetUserId();
            setCurrentUserId(userId);
            const token = authGetToken();
            if (token != null) {
                const tokenDecoded = decodeJwt(token);
                const realmRoles = tokenDecoded.realm_access?.roles?.filter((r: string) =>
                    r.startsWith(ROLE_PREFIX)
                );
                const rolesAvailable = ALLOWED_ROLES.filter((a) => realmRoles.includes(a));
                setRolesAvailable(rolesAvailable);
            }
        }
    }, [authIsReady]);
    React.useEffect(() => {
        if (rolesAvailable != null && currentRole == null) {
            const sessionValue = roleSessionGetValue();
            const isSessionValueInRolesAvailable =
                sessionValue != null && rolesAvailable?.includes(sessionValue);
            if (sessionValue != null && isSessionValueInRolesAvailable) {
                setCurrentRole(sessionValue);
            } else if (rolesAvailable?.length && currentRole == null) {
                setCurrentRole(rolesAvailable[0]);
            }
        }
    }, [rolesAvailable, currentRole]);
    React.useEffect(() => {
        if (apiIsReady && currentRole != null) {
            if (currentRole !== ROLE_SUPER) {
                apiFind({ unpaged: true }).then((response) => {
                    const entitatsAvailable = response.rows;
                    setEntitatsAvailable(entitatsAvailable);
                    const sessionValue = getSessionValue(
                        sessionSessionGetValue() ?? undefined,
                        'e'
                    );
                    const isSessionValueInEntitatsAvailable = entitatsAvailable
                        .map((e) => e.id)
                        .includes(sessionValue);
                    if (isSessionValueInEntitatsAvailable) {
                        setCurrentEntitatId(sessionValue);
                    } else if (entitatsAvailable?.length && currentEntitatId == null) {
                        setCurrentEntitatId(entitatsAvailable[0]);
                    }
                });
            } else {
                setEntitatsAvailable([]);
            }
        }
    }, [apiIsReady, currentRole]);
    React.useEffect(() => {
        if (currentRole !== undefined) {
            roleSessionSetValue(currentRole);
            if (currentRole) {
                apiSetHttpHeaders([{ 'X-App-Role': currentRole }]);
            }
        }
    }, [currentRole]);
    React.useEffect(() => {
        if (currentRole != null && currentRole !== ROLE_SUPER && currentEntitatId != null) {
            const session = createSession(currentEntitatId);
            sessionSessionSetValue(session);
            if (currentRole) {
                apiSetHttpHeaders([
                    {
                        'X-App-Role': currentRole,
                    },
                    {
                        'X-App-Session': session,
                    },
                ]);
            }
        }
    }, [currentRole, currentEntitatId]);
    const contextValue = {
        isReady,
        rolesAvailable,
        entitatsAvailable,
        currentRole,
        setCurrentRole,
        currentEntitatId,
        setCurrentEntitatId,
    };
    return (
        <NotibContext.Provider value={contextValue}>{isReady && children}</NotibContext.Provider>
    );
};

export default NotibProvider;
