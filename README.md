# odff – Oracle Diag File Fetcher
A simple tool to fetch alert logs and trace files from Oracle databases via JDBC.

## Usage
Show help

    odff -h

```
Usage: odff [-hV] -u=<url> (-t=<tracefile> | -a)
Oracle Diag File Fetcher. Tool to fetch tracefiles and alertlog.
  -a, --alertlog    Whether to fetch the alert log or not
  -h, --help        Show this help message and exit.
  -t, --tracefileName=<tracefile>
                    Name of the trace file
  -u, --url=<url>   JDBC connection string
  -V, --version     Print version information and exit.
```

Fetch alertlog

    odff --url="jdbc:oracle:thin:scott/tiger@localhost:1521/ORCLCDB" --alertlog

Fetch tracefile

    odff --url="jdbc:oracle:thin:scott/tiger@localhost:1521/ORCLCDB" --tracefileName=ORCLCDB_ora_2932.trc

## Required database privileges
- `select on gv$diag_trace_file_contents` for fetching tracefiles
- `select on v$diag_alert_ext` for fetching the alert log

## Status
![Build Status](https://github.com/TAregger/odff/actions/workflows/maven.yml/badge.svg)
