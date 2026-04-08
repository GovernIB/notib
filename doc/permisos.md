# Permisos

## Permisos per a entitats
Els permisos per a entitats son els següents:

| Permís | Funció                      |
|--------|-----------------------------|
| PERM0  | Accés com a usuari          |
| PERM2  | Administració               |
| PERM3  | Accés d'aplicació           |
| PERMX  | Administrador només lectura |

Els permisos d'una entitat els pot gestionar un usuari amb el rol NOT_SUPER o un usuari amb permisos d'administració sobre l'entitat.

## Permisos per a notificacions
Els permisos per a notificacions es poden donar sobre un òrgan gestor, sobre un procediment o sobre una combinació òrgan gestor - procediment.

Els permisos sobre un òrgan gestor son els següents:

| Permís | Funció                                                                  |
|--------|-------------------------------------------------------------------------|
| READ   | Consulta de notificacions                                               |
| ADMIN  | Administració de l'òrgan gestor                                         |
| PERM1  | Processar notificacions (poden marcar una notificació com a processada) |
| PERM2  | Gestionar notificacions (editar)                                        |
| PERM3  | Procediments/serveis comuns                                             |
| PERM4  | Crear notificacions                                                     |
| PERM5  | Crear comunicacions                                                     |
| PERM6  | Crear comunicacions SIR                                                 |
| PERM7  | Crear comunicacions sense procediment                                   |

Els permisos d'un òrgan gestor els pot gestionar l'administrador de l'entitat o l'administrador de l'òrgan gestor.

Els permisos sobre un procediment o sobre una combinació organ gestor - procediment son els següents:

| Permís | Funció                                                                  |
|--------|-------------------------------------------------------------------------|
| READ   | Consulta de notificacions                                               |
| PERM4  | Processar notificacions (poden marcar una notificació com a processada) |
| ADMIN  | Gestionar notificacions (editar)                                        |
| PERM5  | Crear notificacions                                                     |
| PERM8  | Crear comunicacions                                                     |
| PERM7  | Crear comunicacions SIR                                                 |

Els permisos d'un procediment o d'una combinació organ gestor - procediment els pot gestionar l'administrador de l'entitat o l'administrador de l'òrgan gestor.

Per a comprovar els permisos es verifica si es compleix alguna de les següents condicions:
* L'usuari te permís sobre l'òrgan gestor de la notificació.
* La notificació te un procediment no comú i l'usuari te permís sobre aquest procediment.
* La notificació te un procediment comú amb "requereix permisos directes" i l'usuari te permís sobre la combinació organ gestor - procediment de la notificació.
* La notificació te un procediment comú sense "requereix permisos directes", l'usuari te permís sobre la combinació organ gestor - procediment de la notificació i les combinacions òrgan gestor - procediment son únicament dels òrgans gestors amb permís de procediments comuns.
