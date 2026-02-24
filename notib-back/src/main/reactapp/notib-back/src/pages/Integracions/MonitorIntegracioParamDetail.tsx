import {DetailPage, ResourceApiProvider, useResourceApiService} from 'reactlib';
import React from "react";
// import {useParams} from "react-router-dom";
import {getEnvApiUrl} from "../../App.tsx";



export const MonitorIntegracioParamDetail: React.FC = () => {
    // const { id } = useParams();
    const { isReady: apiIsReady, getOne: apiGetOne } = useResourceApiService('monitorIntegracioParamResource');
    let params;
    React.useEffect(() => {
        if (apiIsReady) {
            apiGetOne(32006415).then(data => {
                console.log('data:', data)
                params = data;
            });
        }
    }, [apiIsReady]);
    console.log(params);
    return (
        <DetailPage>
            {params}
        </DetailPage>
        // <ResourceApiProvider  apiUrl={getEnvApiUrl()}>
        //     {params}
        // </ResourceApiProvider>
    );
};

export default MonitorIntegracioParamDetail;
