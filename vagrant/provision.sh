#!/bin/sh

# 2003 graphite carbon
# 3000 grafana
# 8000 dropwizard-app
# 8888 graphite-api
# 9000 gropwizard-app admin

export DEBIAN_FRONTEND=noninteractive

wget -nv -P /tmp https://github.com/tomakehurst/saboteur/releases/download/v0.7/saboteur_0.7_all.deb
dpkg -i /tmp/saboteur_0.7_all.deb
update-rc.d saboteur-agent defaults 95 10
service saboteur-agent start

apt-get install -y graphite-carbon graphite-web apache2 libapache2-mod-wsgi
cp /usr/share/doc/graphite-carbon/examples/storage-aggregation.conf.example /etc/carbon/storage-aggregation.conf
echo 'CARBON_CACHE_ENABLED=true' >/etc/default/graphite-carbon
graphite-build-search-index
service carbon-cache start

echo no | sudo -u _graphite graphite-manage syncdb
ln -s /usr/share/graphite-web/apache2-graphite.conf /etc/apache2/sites-available/graphite-web.conf
a2dissite *
a2ensite graphite-web

echo 'Listen 8001
<VirtualHost *:8001>
DocumentRoot /var/www/dependency
</VirtualHost>' >/etc/apache2/sites-available/dependency.conf
mkdir /var/www/dependency
echo -n OK >/var/www/dependency/call
a2ensite dependency

service apache2 reload

wget -nv -P /tmp https://grafanarel.s3.amazonaws.com/builds/grafana_2.6.0_amd64.deb
apt-get install -y adduser libfontconfig
dpkg -i /tmp/grafana_2.6.0_amd64.deb
update-rc.d grafana-server defaults 95 10
service grafana-server start
until $(curl -o /dev/null -s 'http://admin:admin@localhost:3000/api/datasources/')
do
  echo $?
  echo 'Waiting for Grafana'
  sleep 3
done
curl -s -H 'Content-Type: application/json' -X POST 'http://admin:admin@localhost:3000/api/datasources' --data-binary '{"name":"test","type":"graphite","url":"http://localhost","access":"proxy","basicAuth":false,"isDefault":true}'
