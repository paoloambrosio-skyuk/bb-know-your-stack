## Web Application Threads (Demo)

_A short story of threads trapped in a job they never wanted_

### Dependencies

A virtual machine provides the following services:

Service | External port
------- | -------------
Graphite | 2003
Grafana | 3000
Fake dependency (optionally slowed down with Saboteur) | 8001
Saboteur |

From the "vagrant" directory:
  - `vagrant up` starts or create the VM
  - `vagrant ssh` opens a shell into the VM (e.g. to run Saboteur)

### Application under test

The application under test is started with `./runApp.sh`

It is configured in `local.yml` to have:
  - acceptor threads: 1
  - selector thread: 1
  - worker threads: 4

It exposes two endpoints on port 8000:
  - GET /sync
  - GET /async

Both endpoints call the downstream dependency but use synchronous and
asynchronous clients and controllers respectively. Outgoing connections
are currently not configured, so they use the default configuration.

### Performance tests

Tests are started with:
  - `./runSyncSimulation.sh`
  - `./runAsyncSimulation.sh`

Settings are:
  - connection timeout: 1000 ms
  - request timeout: 1000 ms
  - read timeout: 5000 ms
  - no retries
