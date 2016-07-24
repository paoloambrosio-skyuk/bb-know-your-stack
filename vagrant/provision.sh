#!/bin/sh

set -e

# 2003 Graphite Carbon
# 3000 Grafana
# 8001 Fake dependency with Wiremock

export DEBIAN_FRONTEND=noninteractive

apt-get update

dpkg -s graphite-web >/dev/null 2>&1 || {
    echo Installing Graphite
    apt-get install -y graphite-carbon graphite-web apache2 libapache2-mod-wsgi
    cp /vagrant/graphite/storage-aggregation.conf /etc/carbon/storage-aggregation.conf
    cat /vagrant/graphite/storage-schemas-add.conf >>/etc/carbon/storage-schemas.conf
    echo 'CARBON_CACHE_ENABLED=true' >/etc/default/graphite-carbon
    graphite-build-search-index
    service carbon-cache start

    echo no | sudo -u _graphite graphite-manage syncdb
    ln -s /usr/share/graphite-web/apache2-graphite.conf /etc/apache2/sites-available/graphite-web.conf
    a2dissite *
    a2ensite graphite-web
    service apache2 reload
}

dpkg -s grafana >/dev/null 2>&1 || {
    echo Installing Grafana
    wget -nv -P /tmp https://grafanarel.s3.amazonaws.com/builds/grafana_3.1.0-1468321182_amd64.deb
    apt-get install -y adduser libfontconfig
    dpkg -i /tmp/grafana_3.1.0-1468321182_amd64.deb
    update-rc.d grafana-server defaults 95 10

    cp /vagrant/grafana/dashboard.json /usr/share/grafana/public/dashboards/home.json
    cp /vagrant/grafana/grafana.ini /etc/grafana/grafana.ini

    service grafana-server start
    until $(curl -o /dev/null -s 'http://admin:admin@localhost:3000/api/datasources/')
    do
      echo $?
      echo 'Waiting for Grafana'
      sleep 3
    done
    curl -s -H 'Content-Type: application/json' -X POST 'http://admin:admin@localhost:3000/api/datasources' --data-binary '{"name":"test","type":"graphite","url":"http://localhost","access":"proxy","basicAuth":false,"isDefault":true}'
}

test -d /opt/wiremock || {
    echo Installing Wiremock Server
    apt-get install -y openjdk-7-jre-headless
    mkdir -p /opt/wiremock/mappings
    wget -nv -P /opt/wiremock http://repo1.maven.org/maven2/com/github/tomakehurst/wiremock-standalone/2.1.7/wiremock-standalone-2.1.7.jar
    cp /vagrant/wiremock/wiremock-server /usr/local/bin/
    cp /vagrant/wiremock/dep-delay /usr/local/bin/
    cp /vagrant/wiremock/call.json /opt/wiremock/mappings
}
