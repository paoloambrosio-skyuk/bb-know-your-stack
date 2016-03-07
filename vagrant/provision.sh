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

apt-get install -y graphite-carbon
cp /usr/share/doc/graphite-carbon/examples/storage-aggregation.conf.example /etc/carbon/storage-aggregation.conf
echo 'CARBON_CACHE_ENABLED=true' >/etc/default/graphite-carbon
service carbon-cache start

wget -nv -P /tmp https://github.com/brutasse/graphite-api/releases/download/1.1.2/graphite-api_1.1.2-1447943657-ubuntu14.04_amd64.deb
dpkg -i /tmp/graphite-api_1.1.2-1447943657-ubuntu14.04_amd64.deb

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
curl -s -H 'Content-Type: application/json' -X POST 'http://admin:admin@localhost:3000/api/datasources' --data-binary '{"name":"test","type":"graphite","url":"http://localhost:8888","access":"proxy","basicAuth":false,"isDefault":true}'