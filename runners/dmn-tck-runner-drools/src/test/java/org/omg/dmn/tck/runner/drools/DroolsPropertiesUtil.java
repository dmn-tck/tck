package org.omg.dmn.tck.runner.drools;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Properties;

public final class DroolsPropertiesUtil {

    private static final String DROOLS_PROPERTIES_PATH = File.separator + "drools.properties";
    private static final String PROPERTY_DROOLS_VERSION = "drools.version";

    private static String droolsVersion;


    private DroolsPropertiesUtil() {
        // It is not allowed to create instances of util classes.
    }

    public synchronized static String getDroolsVersion() {
        if (droolsVersion == null) {
            try {
                droolsVersion = getDroolsVersionFromProperties();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        return droolsVersion;
    }

    private static String getDroolsVersionFromProperties() throws IOException {
        final Properties ps = new Properties();
        ps.load(DroolsPropertiesUtil.class.getResourceAsStream(DROOLS_PROPERTIES_PATH));
        final String droolsVersionFromProperties = ps.getProperty(PROPERTY_DROOLS_VERSION);
        return droolsVersionFromProperties != null ? droolsVersionFromProperties : "";
    }
}
