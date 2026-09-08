import React from 'react';
import { FormFieldReference } from '../../lib/components/mui/form/FormFieldReference';
import { FormFieldCustomProps } from '../../lib/components/form/FormField';
import { useNotibContext } from './NotibContext';

const DEFAULT_PAGE_SIZE = 20;

const AppFormFieldReference: React.FC<FormFieldCustomProps> = (props) => {
    const { maxResultSelects } = useNotibContext();
    const optionsPageSize = (props as any).optionsPageSize ?? maxResultSelects ?? DEFAULT_PAGE_SIZE;
    return <FormFieldReference {...(props as any)} optionsPageSize={optionsPageSize} />;
};

export default AppFormFieldReference;
