## Web Application Threads (Demo)

_A short story of threads trapped in a job they never wanted_

### Dependencies

A virtual machine provides the following services:

Service               | External port
--------------------- | -------------
Graphite              | 2003
Grafana               | 3000
Dependency (Wiremock) | 8001

From the "vagrant" directory:
  - `vagrant up` starts or create the VM
  - `vagrant ssh` opens a shell into the VM

Inside the Vagrant box:
  - `wiremock-server start` Starts the dependency
  - `wiremock-server stop` Stops the dependency
  - `dep-delay DELAY` Delays the dependency by DELAY milliseconds

### Application under test

The application under test is started with `./runApp.sh`

It is configured in `local.yml` to have:
  - server
    - acceptor threads: 1
    - selector thread: 1
    - worker threads: 4
  - client
    - connection request timeout: 100 ms
    - connection timeout: 100 ms
    - read timeout (socket inactivity): 2000 ms
    - no retries
    - connection pool size: 20

It exposes two endpoints on port 8000:
  - GET /sync
  - GET /async

Both endpoints call the downstream dependency but use synchronous and
asynchronous controllers and services respectively. Outgoing connections
are currently not configured, so they use the default configuration.

### Performance tests

Simulations are started with:
  - `./runSyncSimulation.sh`
  - `./runAsyncSimulation.sh`

Settings in `gatling.conf` are:
  - connection timeout: 100 ms
  - request timeout: 3000 ms
  - read timeout: 3000 ms
  - no retries
