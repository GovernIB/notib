import {DetailPage, useResourceApiService} from 'reactlib';
import React from "react";
import {useParams} from "react-router-dom";


export const MonitorIntegracioParamDetail: React.FC = () => {

    const { id } = useParams();
    const { isReady: apiIsReady, find: apiFind } = useResourceApiService('monitorIntegracioParamResource');
    const {  getOne: apiGetOne } = useResourceApiService('monitorIntegracioResource');
    const [params, setParams] = React.useState<any[]>([]);
    const [integracio, setIntegracio] = React.useState<any>(null);

    React.useEffect(() => {
        const fetchParams = async () => {

            if (!apiIsReady || !id) {
                return;
            }
            try {
                const args = { filter: 'monitorIntegracio.id:' + id, unpaged: true };
                const paramResponse = await apiFind(args);
                setParams(paramResponse?.rows || []);
                if (paramResponse?.rows.length > 0) {
                    const integracio = await apiGetOne(id);
                    setIntegracio(integracio);
                }
            } catch (error) {
                console.error('Error fetching params:', error);
            }
        };
        fetchParams();
    }, [apiIsReady, id, apiFind, apiGetOne]);

    return (
        <DetailPage>
            <p>Descripcio:   {integracio?.descripcio}</p>
            <p>Data:   {integracio?.data}</p>
            <p>Tipus:   {integracio?.tipus}</p>
            <p>Estat:   {integracio?.estat}</p>
            <p>----------- Params  --------------</p>
            {params.map((param) => (
                <div key={param.id}>
                    <p>valor:   {param.codi}</p>
                    <p>codi: {param.valor}</p>
                </div>
            ))}
            <p>---------------------------------</p>
            <p>Error descripcio:   {integracio?.errorDescripcio}</p>
            <p>Excepcio missatge:   {integracio?.excepcioMessage}</p>
            <p>Excepcio stacktrace:   {integracio?.excepcioStacktrace}</p>
        </DetailPage>
    );
};

export default MonitorIntegracioParamDetail;
