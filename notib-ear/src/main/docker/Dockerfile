FROM 280421/jb5caib

ENV DATABASE_URL jdbc:oracle:thin:@10.35.3.242:1521:orcl
ENV DATABASE_USERNAME notib
ENV DATABASE_PASSWORD notib

ENV SEYCON_URL jdbc:oracle:thin:@10.35.3.242:1521:orcl
ENV SEYCON_USERNAME seycon
ENV SEYCON_PASSWORD seycon

ENV NOTIFICA_VERSIO 0
ENV NOTIFICA_URL https://se-notificaws.redsara.es:443
ENV NOTIFICA_APIKEY MjU1MzQ2MDIxMDQyMjIwMDk0NA==

ENV REGISTRE_URL https://dev.caib.es/regweb3/ws/v3
ENV REGISTRE_USERNAME \$notib_regweb
ENV REGISTRE_PASSWORD notib_regweb

ENV DIR3_URL https://dev.caib.es/dir3caib
ENV DIR3_USERNAME \$notib_dir3caib
ENV DIR3_PASSWORD notib_dir3caib

ENV ROLSAC_URL https://dev.caib.es/rolsac
ENV ROLSAC_USERNAME \$distribucio_rolsac
ENV ROLSAC_PASSWORD distribucio_rolsac

#Falta configurar l'arxiu/concsv

EXPOSE 8080
EXPOSE 8787

WORKDIR /opt/
ADD --chown=default:default deploycaib/ /opt/jboss-eap-5.2-caib/server/default/deploycaib/
ADD --chown=default:default lib/ /opt/jboss-eap-5.2-caib/server/default/lib/
ADD --chown=default:default config/ /opt/webapps/
RUN chmod +x /opt/webapps/env_init.sh
USER default
COPY --chown=default:default maven/${project.build.finalName}.ear /opt/jboss-eap-5.2-caib/server/default/deploycaib/${project.build.finalName}.ear
WORKDIR /opt/jboss
ENTRYPOINT [ "/opt/jboss-eap-5.2-caib/tools/docker-entrypoint.sh" ]
#CMD ["-b 0.0.0.0"]
#CMD ["/opt/jboss/bin/run.sh", "-b 0.0.0.0"]