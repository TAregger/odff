# odff – Oracle Diag File Fetcher
A simple tool to fetch alert logs and trace files from Oracle databases via JDBC.

## Usage
Type `odff -h` to show the usage
```
Usage: odff [-hV] (-u=<url> | [-n=<name> [-c=<filepath>] [-p[=<password>]]]) (-t=<tracefile> | -a)
Oracle Diag File Fetcher.
Fetches alert logs and trace files from Oracle databases.
  -u, --url=<url>                JDBC connection string
  -n, --name=<name>              Name of the connection to use as defined in the connection definitions
  -c, --connections=<filepath>   File with connection definitions. If not specified the default is
                                 connections.json in the the users current working directory
  -p, --password[=<password>]    Password used to connect
  -t, --tracefile=<tracefile>    Name of the trace file to fetch
  -a, --alertlog                 Fetches the alert log instead of a trace file
  -h, --help                     Show this help message and exit.
  -V, --version                  Print version information and exit.
```

### Supported diag file types
odff supports fetching alert logs and trace files.

Fetch alertlog example
```bash
odff --url="jdbc:oracle:thin:scott/tiger@localhost:1521/ORCLCDB" --alertlog
```

Fetch tracefile example
```bash
odff --url="jdbc:oracle:thin:scott/tiger@localhost:1521/ORCLCDB" --tracefile=ORCLCDB_ora_2932.trc
```

The alert log can also be fetched with `--tracefile` by providing the correct name of the alert log. The difference
is where the contents of the file is fetched from.

The data of `v$diag_alert_ext` comes from `<adr_home>/alert/log.xml` while `gv$diag_trace_file_contents` gets its content
from `<adr_home>/trace/alert_<instance>.log`

### Providing connection data
The target database to fetch from can be either provided directly on the command line with `--url` or by referring to a
name (`--name`) of a connection defined in a separate file.

#### Connection definitions file format
The file with the connection definitions is in JSON format and contains one or more connection definitions. All attributes except `password`
are mandatory. The name needs to be unique across all connection definitions.

Example file
```json
[
  {
    "name": "ORCLCDB",
    "tnsString": "localhost:1521/ORCLCDB",
    "username": "scott",
    "password": "tiger"
  },
  {
    "name": "PDB001",
    "tnsString": "(DESCRIPTION=(FAILOVER=on)(ADDRESS_LIST=(LOAD_BALANCE=on)(CONNECT_TIMEOUT=3)(RETRY_COUNT=3)(ADDRESS=(PROTOCOL=TCP)(HOST=arrakis)(PORT=1521))(ADDRESS=(PROTOCOL=TCP)(HOST=caladan)(PORT=1521)))(CONNECT_DATA=(SERVICE_NAME=PDB001)))",
    "username": "system"
  }
]
```

## Required database privileges
- `select on gv$diag_trace_file_contents` for fetching tracefiles
- `select on v$diag_alert_ext` for fetching the alert log

## Status
![Build Status](https://github.com/TAregger/odff/actions/workflows/maven.yml/badge.svg)
