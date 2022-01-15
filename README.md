# Usage

    java -jar oracle-tracefile-fetcher-1.0-SNAPSHOT-shaded.jar

```
Missing required option: '--url=<url>'
Usage: <main class> -u=<url> (-t=<tracefile> | -a)
  -a, --alertlog    Whether to fetch the alert log or not
  -t, --tracefileName=<tracefile>
                    Name of the trace file
  -u, --url=<url>   JDBC connection string
```
Fetch alertlog

    java -jar oracle-tracefile-fetcher-1.0-SNAPSHOT-shaded.jar --url=jdbc:oracle:thin:c##dbzuser/dbz@localhost:1521/ORCLCDB --alertlog


