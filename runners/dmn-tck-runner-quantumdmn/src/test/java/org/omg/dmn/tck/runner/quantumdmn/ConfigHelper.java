/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.omg.dmn.tck.runner.quantumdmn;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Loads configuration from properties file for the QuantumDMN TCK runner.
 */
public final class ConfigHelper {

    private static final String CONFIG_FILE_NAME = "quantumdmn.properties";
    private static Properties properties;

    private ConfigHelper() {
        // utility class
    }

    public static synchronized Properties getProperties() throws IOException {
        if (properties != null) {
            return properties;
        }

        properties = new Properties();

        // try to load from classpath first
        try (var is = ConfigHelper.class.getClassLoader().getResourceAsStream(CONFIG_FILE_NAME)) {
            if (is != null) {
                properties.load(is);
                return properties;
            }
        }

        // try to load from user home
        File userHomeConfig = new File(System.getProperty("user.home"), ".quantumdmn.properties");
        if (userHomeConfig.exists()) {
            try (FileInputStream fis = new FileInputStream(userHomeConfig)) {
                properties.load(fis);
                return properties;
            }
        }

        // try to load from current directory
        File localConfig = new File(CONFIG_FILE_NAME);
        if (localConfig.exists()) {
            try (FileInputStream fis = new FileInputStream(localConfig)) {
                properties.load(fis);
                return properties;
            }
        }

        throw new IOException("Could not find configuration file: " + CONFIG_FILE_NAME
                + ". Please create one in classpath, user home (~/.quantumdmn.properties), or current directory.");
    }

    public static String getBaseUrl() throws IOException {
        return resolveProperty("quantumdmn.base-url", "https://api.quantumdmn.com");
    }

    public static String getAuthIssuer() throws IOException {
        return resolveProperty("quantumdmn.auth.zitadel.issuer", "https://auth.quantumdmn.com");
    }

    public static String getKeyFile() throws IOException {
        return resolveProperty("quantumdmn.auth.zitadel.key-file", null);
    }

    public static String getProjectId() throws IOException {
        return resolveProperty("quantumdmn.auth.zitadel.project-id", null);
    }

    private static String resolveProperty(String key, String defaultValue) throws IOException {
        String value = getProperties().getProperty(key);
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }

        // resolve environment variable placeholders like ${ENV_VAR:default}
        if (value.startsWith("${") && value.contains("}")) {
            int colonIndex = value.indexOf(':');
            int endIndex = value.indexOf('}');

            String envKey;
            String envDefault;
            if (colonIndex > 0 && colonIndex < endIndex) {
                envKey = value.substring(2, colonIndex);
                envDefault = value.substring(colonIndex + 1, endIndex);
            } else {
                envKey = value.substring(2, endIndex);
                envDefault = defaultValue;
            }

            String envValue = System.getenv(envKey);
            return (envValue != null && !envValue.isEmpty()) ? envValue : envDefault;
        }

        return value;
    }
}
