import React from 'react';
import { MuiFilter } from 'reactlib';
import { useNotibContext } from '../components/NotibContext';

export const useDatagridPageSizeOptionsProps = () => {
    const { currentUser, currentUserGridPageSizeOptions } = useNotibContext();
    return {
        defaultPaginationModel: {
            page: 0,
            pageSize: currentUser.numElementsPaginaDefecteAsInt ?? -1,
        },
        pageSizeOptions: currentUserGridPageSizeOptions,
    };
};

export const useDatagridFilterProps = (
    resourceName: string,
    code: string,
    springFilterBuilder: (data: any) => string | undefined,
    content: React.ReactElement,
    minHeight: string = '56px',
) => {
    const [autoFindDisabled, setAutoFindDisabled] = React.useState<boolean>(true);
    const handleSpringFilterChange = (springFilter: string | undefined) => {
        const springFilterEmpty = springFilter == null || springFilter === '';
        setAutoFindDisabled(!springFilterEmpty);
    };
    const filterComponent = (
        <MuiFilter
            resourceName={resourceName}
            code={code}
            persistentStateActive
            springFilterBuilder={springFilterBuilder}
            onSpringFilterChange={handleSpringFilterChange}
            componentProps={{ sx: { mb: 2, mt: 0 } }}
            commonFieldComponentProps={{ size: 'small' }}
        >
            {content}
        </MuiFilter>
    );
    return {
        autoFindDisabled: autoFindDisabled,
        toolbarHideQuickFilter: true as true,
        toolbarAdditionalRow: filterComponent,
        toolbarAdditionalRowMinHeight: minHeight,
    };
};
