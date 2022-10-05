#!/bin/sh

echo "Substituint variables"

###################
# USUARIS         #
###################
echo "Substituint variables del datasource d'USUARIS"

if [[ -n "$SEYCON_URL" ]]; then
  echo "Substituint SEYCON_URL per $SEYCON_URL"
	sed -i "s|SEYCON_URL|$SEYCON_URL|g" /opt/jboss-eap-5.2-caib/server/default/deploycaib/notib-ds.xml
fi
if [[ -n "$SEYCON_USERNAME" ]]; then
  echo "Substituint SEYCON_USERNAME per $SEYCON_USERNAME"
	sed -i "s/SEYCON_USERNAME/$SEYCON_USERNAME/g" /opt/jboss-eap-5.2-caib/server/default/deploycaib/notib-ds.xml
fi
if [[ -n "$SEYCON_PASSWORD" ]]; then
  echo "Substituint SEYCON_PASSWORD per $SEYCON_PASSWORD"
	sed -i "s/SEYCON_PASSWORD/$SEYCON_PASSWORD/g" /opt/jboss-eap-5.2-caib/server/default/deploycaib/notib-ds.xml
fi

###################
# NOTIB           #
###################
echo "Substituint variables del datasource de NOTIB"

if [[ -n "$DATABASE_URL" ]]; then
  echo "Substituint DATABASE_URL per $DATABASE_URL"
	sed -i "s|DATABASE_URL|$DATABASE_URL|g" /opt/jboss-eap-5.2-caib/server/default/deploycaib/notib-ds.xml
fi
if [[ -n "$DATABASE_USERNAME" ]]; then
  echo "Substituint DATABASE_USERNAME per $DATABASE_USERNAME"
	sed -i "s/DATABASE_USERNAME/$DATABASE_USERNAME/g" /opt/jboss-eap-5.2-caib/server/default/deploycaib/notib-ds.xml
fi
if [[ -n "$DATABASE_PASSWORD" ]]; then
  echo "Substituint DATABASE_PASSWORD per $DATABASE_PASSWORD"
	sed -i "s/DATABASE_PASSWORD/$DATABASE_PASSWORD/g" /opt/jboss-eap-5.2-caib/server/default/deploycaib/notib-ds.xml
fi

###################
# PROPIETATS      #
###################
echo "Substituint variables de les propietats de NOTIB"

if [[ -n "$NOTIFICA_VERSIO" ]]; then
  echo "Substituint NOTIFICA_VERSIO per $NOTIFICA_VERSIO"
	sed -i "s/NOTIFICA_VERSIO/$NOTIFICA_VERSIO/g" /opt/jboss-eap-5.2-caib/server/default/deploycaib/notib-service.xml
fi
if [[ -n "$NOTIFICA_URL" ]]; then
  echo "Substituint NOTIFICA_URL per $NOTIFICA_URL"
	sed -i "s|NOTIFICA_URL|$NOTIFICA_URL|g" /opt/jboss-eap-5.2-caib/server/default/deploycaib/notib-service.xml
fi
if [[ -n "$NOTIFICA_APIKEY" ]]; then
  echo "Substituint NOTIFICA_APIKEY per $NOTIFICA_APIKEY"
	sed -i "s/NOTIFICA_APIKEY/$NOTIFICA_APIKEY/g" /opt/jboss-eap-5.2-caib/server/default/deploycaib/notib-service.xml
fi
if [[ -n "$REGISTRE_URL" ]]; then
  echo "Substituint REGISTRE_URL per $REGISTRE_URL"
	sed -i "s|REGISTRE_URL|$REGISTRE_URL|g" /opt/jboss-eap-5.2-caib/server/default/deploycaib/notib-service.xml
fi
if [[ -n "$REGISTRE_USERNAME" ]]; then
  echo "Substituint REGISTRE_USERNAME per $REGISTRE_USERNAME"
	sed -i "s/REGISTRE_USERNAME/$REGISTRE_USERNAME/g" /opt/jboss-eap-5.2-caib/server/default/deploycaib/notib-service.xml
fi
if [[ -n "$REGISTRE_PASSWORD" ]]; then
  echo "Substituint REGISTRE_PASSWORD per $REGISTRE_PASSWORD"
	sed -i "s/REGISTRE_PASSWORD/$REGISTRE_PASSWORD/g" /opt/jboss-eap-5.2-caib/server/default/deploycaib/notib-service.xml
fi
if [[ -n "$DIR3_URL" ]]; then
  echo "Substituint DIR3_URL per $DIR3_URL"
	sed -i "s|DIR3_URL|$DIR3_URL|g" /opt/jboss-eap-5.2-caib/server/default/deploycaib/notib-service.xml
fi
if [[ -n "$DIR3_USERNAME" ]]; then
  echo "Substituint DIR3_USERNAME per $DIR3_USERNAME"
	sed -i "s/DIR3_USERNAME/$DIR3_USERNAME/g" /opt/jboss-eap-5.2-caib/server/default/deploycaib/notib-service.xml
fi
if [[ -n "$DIR3_PASSWORD" ]]; then
  echo "Substituint DIR3_PASSWORD per $DIR3_PASSWORD"
	sed -i "s/DIR3_PASSWORD/$DIR3_PASSWORD/g" /opt/jboss-eap-5.2-caib/server/default/deploycaib/notib-service.xml
fi
if [[ -n "$ROLSAC_URL" ]]; then
  echo "Substituint ROLSAC_URL per $ROLSAC_URL"
	sed -i "s|ROLSAC_URL|$ROLSAC_URL|g" /opt/jboss-eap-5.2-caib/server/default/deploycaib/notib-service.xml
fi
if [[ -n "$ROLSAC_USERNAME" ]]; then
  echo "Substituint ROLSAC_USERNAME per $ROLSAC_USERNAME"
	sed -i "s/ROLSAC_USERNAME/$ROLSAC_USERNAME/g" /opt/jboss-eap-5.2-caib/server/default/deploycaib/notib-service.xml
fi
if [[ -n "$ROLSAC_PASSWORD" ]]; then
  echo "Substituint ROLSAC_PASSWORD per $ROLSAC_PASSWORD"
	sed -i "s/ROLSAC_PASSWORD/$ROLSAC_PASSWORD/g" /opt/jboss-eap-5.2-caib/server/default/deploycaib/notib-service.xml
fi

# PROPIETATS="/opt/jboss-eap-5.2-caib/server/default/deploycaib/notib-service.xml"
# echo "Fitxer de propietats: "
# while read line; do
#	  echo "$line"
# done < $PROPIETATS