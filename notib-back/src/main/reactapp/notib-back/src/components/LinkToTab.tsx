import { Link } from 'react-router-dom';
import { useMuiDataGridContext } from 'reactlib';
import { useFormInitialTabContext } from './FormInitialTabProvider';

export type LinkToTabProps = {
    id: string;
    tab: number;
    clickEnabled?: boolean;
    /** El formulari del llistat s'obre com a modal (popupEditActive) en lloc d'anar a la ruta form/{id} */
    popup?: boolean;
    /** Fila del llistat (necessària amb popup per saber si l'usuari pot modificar el registre) */
    row?: any;
    children: React.ReactNode;
};

const LinkToTab = (props: LinkToTabProps) => {
    const { id, tab, popup, row, children } = props;
    const { apiRef: dataGridApiRef } = useMuiDataGridContext();
    const formInitialTabContext = useFormInitialTabContext();
    const targetUrl = `form/${id}?tab=${tab}`;

    return (
        <Link
            to={targetUrl}
            onClick={(event) => {
                event.stopPropagation();
                if (!props.clickEnabled) {
                    event.preventDefault();
                } else if (popup) {
                    event.preventDefault();
                    formInitialTabContext?.setInitialTab(tab);
                    dataGridApiRef.current?.triggerUpdate(id, row);
                }
            }}
            // onClick={(event) => event.stopPropagation()}
            style={{
                width: '100%',
                height: '100%',
                display: 'flex',
                justifyContent: 'flex-end',
                alignItems: 'center',
                textDecoration: 'none',
            }}
        >
            {children}
        </Link>
    );
};

export default LinkToTab;
