# Usage
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

    odff --url=jdbc:oracle:thin:c##dbzuser/dbz@localhost:1521/ORCLCDB --alertlog


