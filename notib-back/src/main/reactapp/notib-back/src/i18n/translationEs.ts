const translationEs = {
    app: {
        noEntitat: 'Este usuario no tiene acceso a ningún entorno',
        menu: {
            home: 'Inicio',
            config: 'Configuración',
            entitats: 'Entidades',
            avisos: 'Avisos',
            propietats: 'Propiedades',
            currentEntitat: 'Entidad actual',
            organsGestors: 'Órganos gestores',
            procediments: 'Procediments',
            serveis: 'Serveis',
            grups: 'Grupos',
            enviaments: 'Envios',
            pagadorsPostals: 'Operadores postales',
            pagadorsCie: 'Centros de impresión y ensobrado',
            notificacions: 'Remesas',
            integracions: 'Integraciones',
            cache: "Cache de aplicaciones",
        },
    },
    page: {
        home: {
            toolbar: {
                title: 'Bienvenidos a NOTIB',
                subtitle: 'Aplicación para gestionar y enviar las notificaciones de la CAIB',
            },
        },
        entitats: {
            grid: {
                title: 'Entidades',
            },
            form: {
                titleCreate: 'Crear entidad',
                titleUpdate: 'Modificar entidad',
                tabs: {
                    dades: 'Datos',
                    personalitzar: 'Personalizar',
                    tipusDocs: 'Tipos doc.',
                    aplicacions: 'Aplicaciones',
                    permisos: 'Permisos',
                },
                resourceNames: {
                    aplicacio: 'aplicación',
                    permis: 'permiso',
                },
                personalitzar: {
                    capsalera: 'Cabecera',
                },
                tipusDocuments: {
                    tableColumn: {
                        tipusDoc: 'Tipo de documento',
                        actiu: 'Activo',
                    },
                    enable: {
                        success: 'Tipo de documento activado',
                        error: 'Error al activar el tipo de documento',
                    },
                    disable: {
                        success: 'Tipo de documento desactivado',
                        error: 'Error al desactivar el tipo de documento',
                    },
                },
                permisos: {
                    usuariAllowed: 'Usuario',
                    admEntitatAllowed: 'Adm. entidad',
                    admLecturaAllowed: 'Adm. lectura',
                    aplicacioAllowed: 'Aplicación',
                },
            },
        },
        avisos: {
            grid: {
                title: 'Avisos',
            },
            form: {
                titleCreate: 'Crear aviso',
                titleUpdate: 'Modificar aviso',
            },
        },
        grups: {
            grid: {
                title: 'Grupos',
                popupResourceTitle: 'grupo',
            },
        },
        organs: {
            grid: {
                title: 'Órganos gestores',
                sync: {
                    title: 'Sincronización DIR3',
                    dialogTitle: 'Sincronización DIR3',
                    dialogButton: {
                        cancel: 'Cancelar',
                        query: 'Consultar cambios',
                        apply: 'Aplicar cambios',
                    },
                    success: 'Cambios aplicados con éxito',
                },
            },
            form: {
                titleCreate: 'Crear órgano gestor',
                titleUpdate: 'Modificar órgano gestor',
                tabs: {
                    dades: 'Datos',
                    permisos: 'Permisos',
                },
                resourceNames: {
                    permis: 'permiso',
                },
                permisos: {
                    administrador: 'Administrador',
                    consulta: 'Consulta',
                    processar: 'Procesar',
                    gestio: 'Gestión',
                    comuns: 'ProcSer comunes',
                    notificacions: 'Notificaciones',
                    comunicacions: 'Comunicaciones',
                    sir: 'Comunicaciones SIR',
                    comSenseProc: 'Comunicaciones sin procedimiento',
                },
            },
        },
        propietats: {
            find: 'Buscar en las propiedades',
            empty: 'Sin propiedades',
            revert: 'Deshacer cambios',
            save: {
                success: 'Valor modificado correctamente',
                error: 'Error modificando la propiedad',
            },
        },
        propietatsConfiguracio: {
            grid: {
                title: 'Propiedades configurables',
            },
            form: {
                titleCreate: 'Crear propiedad configurable',
                titleUpdate: 'Modificar propiedad configurable',
            },
        },
        procediment: {
            grid: {
                title: 'Procedimientos',
            },
            form: {
                titleCreate: 'Crear procedimiento',
                titleUpdate: 'Modificar procedimiento',
                tabs: {
                    dades: 'Datos',
                    grups: 'Grupos',
                    permisos: 'Permisos',
                },
                grups: {
                    tableColumn: {
                        grup: 'Grupo',
                        actiu: 'Activo',
                    },
                    enable: {
                        success: 'Grupo activado',
                        error: 'Error activando el grupo',
                    },
                    disable: {
                        success: 'Grupo desactivado',
                        error: 'Error desactivando el grupo',
                    },
                },
                permisos: {
                    consultaAllowed: 'Consulta',
                    procesAllowed: 'Proceso',
                    gestioAllowed: 'Gestión',
                    notificacioAllowed: 'Notificación',
                    comunicacioAllowed: 'Comunicación',
                    comunicacioSirAllowed: 'Comunicación SIR',
                },
            },
        },
        serveis: {
            grid: {
                title: 'Servicios',
            },
            form: {
                titleCreate: 'Crear servicio',
                titleUpdate: 'Modificar servicio',
                tabs: {
                    dades: 'Datos',
                    grups: 'Grupos',
                    permisos: 'Permisos',
                },
                resourceNames: {
                    permis: 'permiso',
                },
                grups: {
                    tableColumn: {
                        grup: 'Grupo',
                        actiu: 'Activo',
                    },
                    enable: {
                        success: 'Grupo activado',
                        error: 'Error activando el grupo',
                    },
                    disable: {
                        success: 'Grupo desactivado',
                        error: 'Error desactivando el grupo',
                    },
                },
                permisos: {
                    consultaAllowed: 'Consulta',
                    procesAllowed: 'Proceso',
                    gestioAllowed: 'Gestión',
                    notificacioAllowed: 'Notificación',
                    comunicacioAllowed: 'Comunicación',
                    comunicacioSirAllowed: 'Comunicación SIR',
                },
            },
        },
        pagadorPostal: {
            grid: {
                title: 'Operadores postales',
                popupResourceTitle: 'operador postal',
            },
        },
        pagadorCie: {
            grid: {
                title: 'Centros de impresión y ensobrado',
            },
            form: {
                titleCreate: 'Crear centro de impresión y ensobrado',
                titleUpdate: 'Modificar centro de impresión y ensobrado',
                tabs: {
                    dades: 'Datos',
                    fulles: 'Formatos de hoja',
                    sobres: 'Formatos de sobre',
                },
            },
        },
        notificacio: {
            grid: {
                title: 'Remesas',
                new: {
                    title: 'Nuevo envio',
                    NOTIFICACIO: 'Notificación',
                    COMUNICACIO: 'Comunicación',
                    SIR: 'Comunicación SIR',
                },
                enviament: {
                    column: {
                        interessat: 'Interesado',
                        representant: 'Representante',
                        estatPostal: 'Estado de entrega postal',
                        estatTelematica: 'Estado de entrega telemática',
                    },
                    detalls: 'Detalles',
                },
                column: {
                    detalls: 'Detalles',
                    desplegar: 'Desplegar envios',
                },
                procediment: 'Procedimiento',
                servei: 'Servicio',
            },
            form: {
                title: {
                    NOTIFICACIO: {
                        create: 'Crear notificación',
                        update: 'Modificar notificación',
                    },
                    COMUNICACIO: {
                        create: 'Crear comunicación',
                        update: 'Modificar comunicación',
                    },
                    SIR: {
                        create: 'Crear comunicación SIR',
                        update: 'Modificar comunicación SIR',
                    },
                },
                tabs: {
                    remesa: 'Información de la remesa',
                    enviaments: 'Envios',
                    documents: 'Documentos adjuntos',
                },
                enviaments: {
                    title: 'Envio',
                    add: 'Añadir envio',
                    remove: 'Eliminar envio',
                },
                interessats: {
                    interessat: 'Interesado (titular en Notifica)',
                    representant: 'Representante (destinatario en Notifica)',
                    add: 'Añadir representante',
                    remove: 'Eliminar representante',
                    nifLabel: {
                        FISICA: 'NIF/NIE/identificador EIDAS',
                        JURIDICA: 'CIF/identificador EIDAS',
                        ADMINISTRACIO: 'NIF',
                        FISICA_SENSE_NIF: 'Número de documento',
                    },
                },
                documents: {
                    title: 'Adjunto',
                    add: 'Añadir adjunto',
                    remove: 'Eliminar adjunto',
                    helperText: {
                        attachment: {
                            noSir: 'El tamaño máximo del documento es de 10 MB. Los formatos admitidos son PDF y ZIP.',
                            sir: 'El tamaño máximo del documento es de 10 MB. Máximo de 15 MB entre todos los documentos. Los formatos admitidos son JPG, JPEG, ODT, ODP, ODS, ODG, DOCX, XLSX, PPTX, PDF, PNG, RTF, SVG, TIFF, TXT, XML y XSIG.',
                        },
                        normalitzat:
                            'Está la primera página del documento preparada para el ensobrado?',
                    },
                },
            },
            detail: {
                title: {
                    notificacio: 'Detalles de la notificación',
                    comunicacio: 'Detalles de la comunicación',
                    sir: 'Detalles de la comunicación SIR',
                },
                tab: {
                    dades: 'Datos',
                    enviaments: 'Envios',
                    documents: 'Documentos',
                    registreEsdev: 'Registro de acontecimientos',
                    accions: 'Acciones',
                    historic: 'Histórico',
                },
            },
        },
        enviament: {
            grid: {
                title: 'Envios',
                detalls: 'Detalles',
            },
            detail: {
                title: 'Detalles del envio',
                tab: {
                    dades: 'Datos',
                    notifica: 'Notific@',
                    registre: 'Registro',
                    registreEsdev: 'Registro de acontecimientos',
                },
            },
        },
        integracio: {
            grid: {
                title: 'Monitor de integraciones',
            },
            detall: {
                title: 'Detalle del monitor de integración',
                descripcio: 'Descripción:',
                data: 'Fecha:',
                tipus: 'Tipo:',
                tipusEnum: {
                    enviament: 'Envío',
                    recepcio: 'Recepción',
                    processar: 'Procesar',
                },
                estat: 'Estado:',
                estatEnum: {
                    ok: 'Correcto',
                    warn: 'Alerta',
                    error: 'Error',
                },
                parametres: 'Parámetros',
                tootlipCopiarParametres: 'Copiar parámetros',
                tootlipCopiarError: 'Copiar error',
                tooltipCopiat: 'Copiado',
                error: 'Error',
                errorDescripcio: 'Descripción del error:',
                excepcioMessage: 'Mensaje de excepción:',
                excepcioStacktrace: 'Stacktrace',
            },
        },
        cache: {
            grid: {
                title: "Cache de aplicaciones"
            },
        },
        notFound: {
            title: 'Página no encontrada',
            toHome: 'Ir al inicio',
        },
    },
    component: {
        HeaderThemeSelector: {
            light: 'Claro',
            system: 'Sistema',
            dark: 'Oscuro',
        },
        HeaderLanguageSelector: {
            languages: {
                ca: 'Catalán',
                es: 'Castellano',
            },
        },
        Offline: {
            message: 'Sin conexión con el servidor',
            retry: 'Volver a intentar',
        },
        AclPermissionManager: {
            title: 'Permisos',
            resourceTitle: 'Permiso',
        },
        RoleSelector: {
            role: {
                NOT_SUPER: 'Superadministrador',
                NOT_ADMIN: 'Administrador',
                tothom: 'Usuario',
            },
        },
        PermissionGrid: {
            tipus: 'Tipo',
            grantedAuthority: {
                user: 'Usuario',
                role: 'Rol',
            },
        },
        GridToolbarButton: {
            add: 'Añadir',
            refresh: 'Refrescar',
        },
        UserProfileFormDialog: {
            perfil: 'Perfil del usuario',
        },
    },
    comu: {
        netejarFiltre: 'Limpiar filtro',
    },
};

export default translationEs;
