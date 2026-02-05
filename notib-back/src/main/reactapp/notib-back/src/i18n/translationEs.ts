const translationEs = {
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
        pagadors: {
            cie: "Centros de impresión y ensobrado",
            postal: "Operadores postal"
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
                    tipus: 'Tipo',
                    grantedAuthority: {
                        user: 'Usuario',
                        role: 'Rol',
                    },
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
            },
            form: {
                titleCreate: 'Crear grupo',
                titleUpdate: 'Modificar grupo',
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
                    tipus: 'Tipo',
                    grantedAuthority: {
                        user: 'Usuario',
                        role: 'Rol',
                    },
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
            },
        },
        servei: {
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
            },
        },
        pagador: {
            cie: {
                grid: {
                    title: "Centros de impresión y ensobrado"
                },
                form: {

                }
            },
            postal: {
                grid: {
                    title: "Operadores postal"
                },
                form: {
                    titleCreate: 'Crear operador postal',
                    titleUpdate: 'Modificar operador postal',
                    nom: "Nombre del operador",
                    organGestor: "Organismo pagador",
                    contracteNum: "Número del contrato",
                    contracteDataVig: "Fecha de vigencia del contrato",
                    facturacioClientCodi: "Código cliente facturación",
                    tabs: {
                        dades: 'Datos',
                        permisos: 'Permisos',
                    }
                }
            }
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
            },
        },
    },
};

export default translationEs;
