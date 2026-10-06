import { Helmet } from '@dr.pogodin/react-helmet';

const PageTitle = ({ title }: { title: string }) => {
    return <Helmet><title>Notib - {title}</title></Helmet>
};

export default PageTitle;
