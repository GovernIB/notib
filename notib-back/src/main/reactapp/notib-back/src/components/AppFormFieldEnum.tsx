import React from 'react';
import MenuItem from '@mui/material/MenuItem';
import ListItemText from '@mui/material/ListItemText';
import Checkbox from '@mui/material/Checkbox';
import TextField from '@mui/material/TextField';
import Autocomplete, { AutocompleteChangeReason } from '@mui/material/Autocomplete';
import Icon from '@mui/material/Icon';
import InputAdornment from '@mui/material/InputAdornment';
import CircularProgress from '@mui/material/CircularProgress';
import {
    FormFieldCustomProps,
    useBaseAppContext,
    useResourceApiContext,
} from 'reactlib';

type EnumOption = {
    value: string;
    description?: any;
};

type AppFormFieldEnumProps = FormFieldCustomProps & {
    multiple?: boolean;
    hiddenEnumValues?: string[];
    requestParams?: any;
    autocomplete?: boolean;
    emptyValueDescription?: string;
    options?: EnumOption[];
    optionRenderer?: (args: {
        value: string;
        description: any;
        option: EnumOption;
    }) => React.ReactNode;
    optionOrder?: string[];
};

const useFormFieldCommon = (
    field: any,
    fieldError: any,
    inline: boolean | undefined,
    componentProps: any,
    startAdornmentIcons?: React.ReactElement[]
) => {
    const title = inline ? (fieldError?.message ?? field?.title) : field?.title;
    const inlineErrorIconElement =
        fieldError && inline ? (
            <Icon fontSize="small" color="error" title={title} sx={{ mr: 1 }}>
                warning
            </Icon>
        ) : null;
    const startAdornment =
        inlineErrorIconElement || startAdornmentIcons?.length ? (
            <>
                {inlineErrorIconElement}
                {...(startAdornmentIcons ?? [])}
                {componentProps?.slotProps?.input?.startAdornment}
            </>
        ) : (
            componentProps?.slotProps?.input?.startAdornment
        );
    const helperText = inline ? field?.helperText : (fieldError?.message ?? field?.helperText);
    return {
        helperText,
        title,
        startAdornment,
    };
};

export const AppFormFieldEnum: React.FC<AppFormFieldEnumProps> = (props) => {
    const {
        name,
        label,
        value,
        field,
        fieldError,
        inline,
        required,
        disabled,
        readOnly,
        onChange,
        componentProps,
        multiple: multipleProp,
        hiddenEnumValues,
        requestParams,
        autocomplete,
        emptyValueDescription,
        options,
        optionRenderer,
        optionOrder,
    } = props;
    const { t } = useBaseAppContext();
    const { requestHref } = useResourceApiContext();
    const [loading, setLoading] = React.useState(false);
    const [textFieldOpen, setTextFieldOpen] = React.useState(false);
    const [enumOptions, setEnumOptions] = React.useState<EnumOption[]>();
    const [autocompleteOpen, setAutocompleteOpen] = React.useState<boolean>(false);
    const [autocompleteInputValue, setAutocompleteInputValue] = React.useState<string>('');
    const multiple = (field?.multiple || multipleProp) ?? false;
    const { helperText, title, startAdornment } = useFormFieldCommon(
        field,
        fieldError,
        inline,
        componentProps
    );
    const loadingStartAdornment = loading ? (
        <InputAdornment position="start">
            <CircularProgress size={20} />
        </InputAdornment>
    ) : undefined;
    const inputProps = {
        readOnly,
        ...componentProps?.slotProps?.input,
        startAdornment: startAdornment ?? loadingStartAdornment,
    };
    const valueTextFieldMultipleAdapted = multiple
        ? value != null
            ? Array.isArray(value)
                ? value
                : [value]
            : []
        : (value ?? '');
    const valueAutocompleteMultipleAdapted = React.useMemo(() => {
        return multiple
            ? value != null
                ? Array.isArray(value)
                    ? value
                    : [value]
                : []
            : (value ?? null);
    }, [multiple, value]);
    const autocompleteInputValueMultipleAdapted = React.useMemo(() => {
        return multiple
            ? autocompleteInputValue
            : value != null
              ? (enumOptions?.find((o) => o.value === value)?.description ?? '')
              : autocompleteInputValue;
    }, [multiple, value, autocompleteInputValue, enumOptions]);
    const handleAutocompleteOnChange = (
        _event: Event,
        value: any,
        reason: AutocompleteChangeReason
    ): void => {
        if (reason === 'clear') {
            setAutocompleteOpen(true);
        }
        if (multiple) {
            onChange(value?.map((v: any) => v.id));
            setAutocompleteInputValue('');
        } else {
            onChange(value?.id ?? undefined);
            setAutocompleteInputValue(value?.description ?? '');
        }
    };
    const handleAutocompleteOnInputChange = (_event: any, newValue: string) => {
        setAutocompleteInputValue(newValue);
    };
    React.useEffect(() => {
        const sortOptions = (opts: EnumOption[]) => {
            if (!optionOrder?.length) {
                return opts;
            }
            const idx = (v: string) => {
                const i = optionOrder.indexOf(v);
                return i === -1 ? Number.MAX_SAFE_INTEGER : i;
            };
            return [...opts].sort((a, b) => idx(a.value) - idx(b.value));
        };

        let nextOptions: EnumOption[] = [];
        if (options != null) {
            nextOptions = options.filter((o) =>
                hiddenEnumValues != null ? !hiddenEnumValues.includes(o.value) : true
            );
        } else if (field?.options != null) {
            const optionsObj = { ...field.options };
            hiddenEnumValues?.forEach((v: any) => {
                delete optionsObj[v];
            });
            nextOptions = Object.entries(optionsObj).map(([value, description]) => ({
                value,
                description,
            }));
        } else if (field?.dataSource != null) {
            const dataSource = field.dataSource;
            const valueField = dataSource.valueField;
            const labelField = dataSource.labelField;
            const templateData = requestParams;
            setLoading(true);
            const href =
                dataSource.href +
                (templateData != null ? '{?' + Object.keys(templateData).join(',') + '}' : '');
            requestHref(href, templateData)
                .then((state: any) => {
                    const dsOptions = state
                        .getEmbedded()
                        .map((e: any) => ({
                            value: e.data[valueField],
                            description: e.data[labelField],
                        }))
                        .filter((o: EnumOption) => !hiddenEnumValues?.includes(o.value));
                    setEnumOptions(sortOptions(dsOptions));
                })
                .finally(() => setLoading(false));
            return;
        } else {
            nextOptions = [];
        }
        setEnumOptions(sortOptions(nextOptions));
    }, [field, requestParams, hiddenEnumValues, requestHref, options, optionOrder]);
    const isRequired = required ?? field?.required;
    const renderOptionContent = (o: EnumOption) =>
        optionRenderer
            ? optionRenderer({ value: o.value, description: o.description ?? o.value, option: o })
            : (o.description ?? o.value);
    return !autocomplete ? (
        <TextField
            select
            name={name}
            label={!inline ? label : undefined}
            placeholder={componentProps?.placeholder ?? (inline ? label : undefined)}
            value={valueTextFieldMultipleAdapted}
            required={isRequired}
            disabled={disabled}
            error={fieldError != null}
            title={title}
            onChange={(e) => {
                const value = e.target.value;
                onChange(value !== '' ? value : null);
            }}
            fullWidth
            {...componentProps}
            helperText={helperText ?? componentProps?.helperText}
            slotProps={{
                input: inputProps,
                inputLabel: { shrink: emptyValueDescription != null ? true : undefined },
                select: {
                    multiple,
                    open: textFieldOpen,
                    readOnly,
                    displayEmpty: emptyValueDescription != null,
                    SelectDisplayProps: { 'aria-label': label },
                    onClose: () => setTextFieldOpen(false),
                    onOpen: () => setTextFieldOpen(true),
                    renderValue: (value: any) => {
                        const selectedText = (v: any) => {
                            if (v === '' && emptyValueDescription != null) {
                                return emptyValueDescription;
                            } else {
                                const found = enumOptions?.find((o) =>
                                    v === '' ? o.value == null : o.value === v
                                );
                                return found?.description ?? found?.value;
                            }
                        };
                        return multiple
                            ? value?.map((v: any) => selectedText(v)).join(', ')
                            : selectedText(value);
                    },
                },
            }}>
            {!isRequired && !multiple && enumOptions?.find((o) => o.value == null) == null && (
                <MenuItem key="" value="">
                    {emptyValueDescription ?? <>&nbsp;</>}
                </MenuItem>
            )}
            {enumOptions?.map((o) => {
                const content = renderOptionContent(o);
                return (
                    <MenuItem key={o.value ?? ''} value={o.value ?? ''}>
                        {multiple && <Checkbox checked={!!value?.includes?.(o.value)} />}
                        {multiple ? <ListItemText primary={content} /> : content}
                    </MenuItem>
                );
            })}
        </TextField>
    ) : (
        <Autocomplete
            name={name}
            value={valueAutocompleteMultipleAdapted}
            onChange={handleAutocompleteOnChange}
            inputValue={autocompleteInputValueMultipleAdapted}
            onInputChange={handleAutocompleteOnInputChange}
            options={enumOptions?.map((o) => ({
                id: o.value,
                description: o.description ?? o.value,
                option: o,
            }))}
            multiple={multiple}
            readOnly={readOnly}
            disableCloseOnSelect={multiple}
            open={autocompleteOpen}
            onOpen={() => !disabled && !readOnly && setAutocompleteOpen(true)}
            onClose={(event: Event, reason) => {
                if (reason === 'escape') {
                    handleAutocompleteOnInputChange(event, (value as any)?.description ?? '');
                }
                setAutocompleteOpen(false);
            }}
            getOptionLabel={(option: any) => {
                if (typeof option === 'string') {
                    if (option.length) {
                        const optionFound = enumOptions?.find((o) => o.value === option);
                        return optionFound ? (optionFound.description ?? optionFound.value) : '';
                    } else {
                        return option;
                    }
                } else {
                    return option?.description;
                }
            }}
            isOptionEqualToValue={(option: any, value: any) => option.id === value?.id}
            renderOption={(props, option: any, { selected }) => {
                const content = optionRenderer
                    ? optionRenderer({
                          value: option.id,
                          description: option.description,
                          option: option.option ?? { value: option.id, description: option.description },
                      })
                    : option.description;
                if (multiple) {
                    const { key, ...optionProps } = props;
                    return (
                        <li key={key} {...optionProps}>
                            <Checkbox
                                checked={selected}
                                icon={<Icon>check_box_outline_blank</Icon>}
                                checkedIcon={<Icon>check_box</Icon>}
                                sx={{ mr: 1 }}
                            />
                            {content}
                        </li>
                    );
                } else {
                    return (
                        <li {...props} key={option.id}>
                            {content}
                        </li>
                    );
                }
            }}
            fullWidth
            {...componentProps}
            renderInput={(params) => (
                <TextField
                    {...params}
                    label={!inline ? label : undefined}
                    placeholder={componentProps?.placeholder ?? (inline ? label : undefined)}
                    disabled={disabled}
                    required={isRequired}
                    error={fieldError != null}
                    title={componentProps?.title ?? title}
                    helperText={helperText}
                    sx={{
                        '& .MuiAutocomplete-inputRoot': {
                            flexWrap: inline ? 'nowrap' : undefined,
                        },
                    }}
                    slotProps={{
                        input: {
                            ...params.InputProps,
                            startAdornment: params.InputProps.startAdornment ? (
                                <>
                                    {inputProps.startAdornment}
                                    {params.InputProps.startAdornment}
                                </>
                            ) : (
                                inputProps.startAdornment
                            ),
                            endAdornment: params.InputProps.endAdornment,
                        },
                        htmlInput: params.inputProps,
                    }}
                />
            )}
            slotProps={{
                popper: {
                    sx: {
                        minWidth: '300px',
                    },
                },
                listbox: { tabIndex: '-1' },
            }}
            clearText={t('form.field.enum.clear')}
            noOptionsText={t('form.field.enum.noOptions')}
        />
    );
};

export default AppFormFieldEnum;
