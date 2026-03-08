# Installetion Guide

This document provides example installation flows for the Snowflake SCIM2 connector.

## Prerequisites

- Java 17 available on the machine that runs midPoint.
- Access to the connector source repository.
- midPoint installed (examples below use `/opt/midpoint`).
- Linux user with permissions to restart midPoint service.

## 1. Build the Connector

From the repository root:

```bash
./gradlew clean fatJar -x test
```

Expected artifact (example):

```bash
build/libs/snowflake-midpoint-connector-scim2-1.2.10-<build>-fat.jar
```

## 2. Install on midPoint Server (Linux)

Stop midPoint, replace connector jar, start midPoint:

```bash
sudo systemctl stop midpoint
sudo rm -f /opt/midpoint/var/icf-connectors/snowflake-midpoint-connector-scim2-*.jar
sudo cp build/libs/*-fat.jar /opt/midpoint/var/icf-connectors/
sudo systemctl start midpoint
sudo systemctl status midpoint --no-pager
```

## 3. Verify Connector Loaded

```bash
tail -n 250 /opt/midpoint/var/log/midpoint.log | grep -Ei "Discovered ICF bundle|snowflake-midpoint-connector-scim2"
```

You should see:

- `Discovered ICF bundle in JAR: snowflake-midpoint-connector-scim2 ...`

## 4. Configure Resource in midPoint (Snowflake)

Recommended connector configuration:

- `Enable Dynamic Schema = false`
- `Enable Standard Schema = true`
- `Users Endpoint URL = /Users`
- `Groups Endpoint URL = /Groups`

Notes:

- Test flow uses Users endpoint reachability.
- Connector tries `/Users?count=1` and falls back to `/Users`.

## 5. Schema Handling Example

Create object types:

- Account object type:
  - Kind: `Account`
  - Intent: `default`
  - Object class: `Scim2 User`
- Entitlement object type:
  - Kind: `Entitlement`
  - Intent: `default`
  - Object class: `Scim2 Group`

Minimal outbound mappings for account provisioning:

- `userName` <- user `emailAddress` (or `name`, based on your policy)
- `emails` <- user `emailAddress`

## 6. Test and Troubleshooting

Run Full test in UI after save:

- `Test resource (FULL)`
- `Refresh schema`

If test fails, collect logs:

```bash
tail -n 300 /opt/midpoint/var/log/midpoint.log | grep -Ei "ConnectorException|SCIM2|Snowflake_GUI_Config|Error received from Scim Backend"
```

Common issues:

- Multiple old connector jars in `var/icf-connectors`.
- Using thin jar instead of `*-fat.jar`.
- Missing outbound mapping for `userName` causing empty shadow create.

## 7. Example: Build on Dev, Install on EC2

On development machine:

```bash
./gradlew clean fatJar -x test
scp build/libs/*-fat.jar ubuntu@<EC2_IP>:/tmp/
```

On EC2:

```bash
sudo systemctl stop midpoint
sudo rm -f /opt/midpoint/var/icf-connectors/snowflake-midpoint-connector-scim2-*.jar
sudo cp /tmp/*-fat.jar /opt/midpoint/var/icf-connectors/
sudo systemctl start midpoint
```

## 8. Rollback Example

If needed, restore previous jar:

```bash
sudo systemctl stop midpoint
sudo cp /path/to/backup/snowflake-midpoint-connector-scim2-<old>-fat.jar /opt/midpoint/var/icf-connectors/
sudo systemctl start midpoint
```
