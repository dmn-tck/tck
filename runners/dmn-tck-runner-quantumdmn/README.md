# QuantumDMN TCK Runner

This module runs the DMN TCK test suite against the QuantumDMN API using the `/evaluate/design` endpoint.

## Configuration

Create a `quantumdmn.properties` file with your API credentials. You can place it in:
- Classpath (`src/test/resources/`)
- User home (`~/.quantumdmn.properties`)
- Current directory

See `quantumdmn.properties.example` for the format.

### Environment Variables

You can also use environment variables:
- `DMN_API_URL` - API base URL (default: `https://api.quantumdmn.com`)
- `DMN_AUTH_URL` - Zitadel issuer URL
- `DMN_KEY_FILE` - Path to service account JWT key file
- `ZITADEL_PROJECT_ID` - Zitadel project ID
- `QUANTUMDMN_TOKEN` - Static bearer token (alternative to Zitadel)

## Running Tests

1. Create the enable flag file:
   ```bash
   touch enable-quantumdmn.flag
   ```

2. Build and run tests:
   ```bash
   mvn test
   ```

3. Results will be written to `target/tck_results.csv`

## Requirements

- Java 17+
- QuantumDMN API access
- `dmn-java-client` https://central.sonatype.com/artifact/com.quantumdmn/dmn-java-client
