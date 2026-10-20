import React from 'react';

type FormInitialTabContextType = {
    initialTab?: number;
    setInitialTab: (tab?: number) => void;
};

/**
 * Pestanya inicial dels formularis que s'obren com a modal des d'un llistat (p. ex. en clicar un
 * comptador de LinkToTab). Fa la mateixa funció que el paràmetre ?tab=N dels formularis en pàgina.
 */
const FormInitialTabContext = React.createContext<FormInitialTabContextType | undefined>(undefined);

export const useFormInitialTabContext = () => React.useContext(FormInitialTabContext);

export const FormInitialTabProvider: React.FC<React.PropsWithChildren> = ({ children }) => {
    const [initialTab, setInitialTab] = React.useState<number>();
    const context = React.useMemo(() => ({ initialTab, setInitialTab }), [initialTab]);
    return <FormInitialTabContext.Provider value={context}>{children}</FormInitialTabContext.Provider>;
};

export default FormInitialTabProvider;
