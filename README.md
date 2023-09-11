# ![Logo notib](https://github.com/GovernIB/notib/raw/master/assets/logo2.png) Notib

**Versions**
> - Versió Estable: __notib-1.1.23__ (tag [NOTIB 1.1.23](https://github.com/GovernIB/notib/releases/tag/v1.1.23))
> - Versió Desenvolupament: __notib-2.0.0__ (branca [notib-dev](https://github.com/GovernIB/notib/tree/notib-wip))  
>  
> - Versió actual del client REST: __1.1.23.2__
>   - Java7: ([notib-client-1.1.23.2.jar](https://github.com/GovernIB/maven/raw/gh-pages/maven/es/caib/notib/notib-client/1.1.23.2/notib-client-1.1.23.2.jar), [notib-client-intf-1.1.23.jar](https://github.com/GovernIB/maven/raw/gh-pages/maven/es/caib/notib/notib-client-intf/1.1.23/notib-client-intf-1.1.23.jar))  
>     ```
>     <dependency>  
>         <groupId>es.caib.notib</groupId>  
>         <artifactId>notib-client</artifactId>  
>         <version>1.1.23.2</version>  
>     </dependency>
>     ```
>   - Java11: ([notib-client-2-1.1.23.2.jar](https://github.com/GovernIB/maven/raw/gh-pages/maven/es/caib/notib/notib-client-2/1.1.23.2/notib-client-2-1.1.23.2.jar), [notib-client-intf-1.1.23.jar](https://github.com/GovernIB/maven/raw/gh-pages/maven/es/caib/notib/notib-client-intf/1.1.23/notib-client-intf-1.1.23.jar))  
>  
>     ```
>     <dependency>  
>         <groupId>es.caib.notib</groupId>  
>         <artifactId>notib-client-2</artifactId>  
>         <version>1.1.23.2</version>  
>     </dependency>
>     ```
> - Configuració del client REST:
>   - Adreça base del servei: https://SERVER/notibapi (substituïr SERVER per l'adreça que correspongui)
>   - Tipus d'autenticació: BASIC
>    ```
>    private NotificacioRestClientV2 client = NotificacioRestClientFactory.getRestClientV2(
>                https://SERVER/notibapi, // Adreça base
>                USERNAME,
>                PASSWORD,
>                true); // Tipus autenticació BASIC?
>    ```


**Descripció**

NOTIB és una solució tecnològica desenvolupada pel Govern de les Illes Balears que facilita la realització i consulta de notificacions telemàtiques
NOTIB ofereix una plataforma  per a realitzar i gestionar totes les comunicacions i notificacions que es generen en els organismes emisors de les illes balears, i que s'envien a la plataforma Notific@, simplificant al màxim les dades a aportar en la seva creació. Al mateix temps ofereix una interfície web per a poder consultar les notificacions realitzades, de forma simple i òptima, donant la possibilitat d'exportar la informació per el seu posterior tractament.

**Documentació**

>- [Manual d'usuari](https://github.com/GovernIB/notib/raw/notib-1.1/doc/pdf/NOTIB_usuari.pdf)
>- [Manual d'instal·lació](https://github.com/GovernIB/notib/raw/notib-1.1/doc/pdf/NOTIB_instalacio.pdf)
>- [Manual d'administració](https://github.com/GovernIB/notib/raw/notib-1.1/doc/pdf/NOTIB_administracio.pdf)
>- [Manual d'integració](https://github.com/GovernIB/notib/raw/notib-1.1/doc/pdf/NOTIB_integracio.pdf)
>- [Manual de plugins](https://github.com/GovernIB/notib/raw/notib-1.1/doc/pdf/NOTIB_plugins.pdf)
>- [Model de dades](https://github.com/GovernIB/notib/raw/notib-1.1/doc/pdf/NOTIB_model_dades.pdf)
