package org.configureme.sources.configurationrepository;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ConfigurationsHolderTest {

    @Test
    public void shouldReturnConfigurationWithNewAndDeprecatedAccessor() {
        String name = "configurations-holder-test-" + System.nanoTime();
        try {
            ConfigurationsHolder.INSTANCE.putConfigurationWithName(name, 5);

            assertEquals("5", ConfigurationsHolder.INSTANCE.getConfigurationByName(name));
            assertEquals("5", ConfigurationsHolder.INSTANCE.getConfigurationByname(name));
        } finally {
            ConfigurationsHolder.INSTANCE.deleteConfigurationWithName(name);
        }
    }

    @Test
    public void shouldThrowMeaningfulExceptionForMissingConfigurationContent() {
        String name = "missing-content-" + System.nanoTime();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ConfigurationsHolder.INSTANCE.getConfigurationByName(name)
        );

        assertEquals("No configuration found for name: " + name, exception.getMessage());
    }

    @Test
    public void shouldThrowMeaningfulExceptionForMissingConfigurationTimestamp() {
        String name = "missing-timestamp-" + System.nanoTime();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ConfigurationsHolder.INSTANCE.getConfigurationTimestamp(name)
        );

        assertEquals("No configuration found for name: " + name, exception.getMessage());
    }
}
