# odff – Oracle Diag File Fetcher
A simple tool to fetch alert logs and trace files from Oracle databases via JDBC.

## Usage
Type `odff -h` to show the usage
```
Usage: odff [-hV] (-u=<url> | [-n=<connectionAlias> [-c=<connectionFileName>] [-p[=<password>]]]) (-t=<tracefileName> | -a)
Oracle Diag File Fetcher.
Fetches alert logs and trace files from Oracle databases.
  -u, --url=<url>                          JDBC connection string
  -n, --name=<connectionName>              Name of the connection to use as defined in the connection definitions
  -c, --connections=<connectionFileName>   File with connection definitions. If not specified the default is
                                           connections.json in the the users current working directory
  -p, --password[=<password>]              Password used to connect
  -t, --tracefileName=<tracefileName>      Name of the trace file to fetch
  -a, --alertlog                           Fetches the alert log instead of a trace file
  -h, --help                               Show this help message and exit.
  -V, --version                            Print version information and exit.
```

### Supported diag file types
odff supports fetching alert logs and trace files.

Fetch alertlog example
```bash
odff --url="jdbc:oracle:thin:scott/tiger@localhost:1521/ORCLCDB" --alertlog
```

Fetch tracefile example
```bash
odff --url="jdbc:oracle:thin:scott/tiger@localhost:1521/ORCLCDB" --tracefileName=ORCLCDB_ora_2932.trc
```

The alert log can also be fetched with `--tracefileName` by providing the correct name of the alert log. The difference
is where the contents of the file is fetched from.

The data of `v$diag_alert_ext` comes from `<adr_home>/alert/log.xml` while `gv$diag_trace_file_contents` gets its content
from `<adr_home>/trace/alert_<instance>.log`

### Providing connection data
The target database to fetch from can be either provided directly on the command line with `--url` or by referring to a
name (`--name`) of a connection defined in a separate file.

#### Connection definitions file format
The file with the connection definitions is in JSON format and contains one or more connection definitions. All attributes except `password`
are mandatory. The alias needs to be unique across all connection definitions.

Example file
```json
[
  {
    "alias": "ORCLCDB",
    "tnsString": "localhost:1521/ORCLCDB",
    "username": "scott",
    "password": "tiger"
  },
  {
    "alias": "TODO",
    "tnsString": "TODO",
    "username": "system"
  }
]
```

## Required database privileges
- `select on gv$diag_trace_file_contents` for fetching tracefiles
- `select on v$diag_alert_ext` for fetching the alert log

## Status
![Build Status](https://github.com/TAregger/odff/actions/workflows/maven.yml/badge.svg)
