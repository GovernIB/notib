import { createContext, useContext } from 'react';

export type NotibContextType = {
    currentUserRealmRoles?: string[];
    currentRole?: string;
    setCurrentRole: (currentRole: string | undefined) => void;
};

export const NotibContext = createContext<NotibContextType | undefined>(undefined);

export const useNotibContext = () => {
    const context = useContext(NotibContext);
    if (context === undefined) {
        throw new Error('useNotibContext must be used within a ResourceApiProvider');
    }
    return context;
};
