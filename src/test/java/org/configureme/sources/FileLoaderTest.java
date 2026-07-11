package org.configureme.sources;

import org.configureme.sources.ConfigurationSourceKey.Format;
import org.configureme.sources.ConfigurationSourceKey.Type;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FileLoaderTest {
	@Test public void acceptOnlyFileKeys(){
		ConfigurationSourceKey key = new ConfigurationSourceKey(Type.FIXTURE, Format.JSON, "foo");
		assertThrows(AssertionError.class, () -> new FileLoader().isAvailable(key));
	}

	@Test public void checkNonExistingFile(){
		ConfigurationSourceKey key = new ConfigurationSourceKey(Type.FILE, Format.JSON, "foo");
		FileLoader loader = new FileLoader();
		assertFalse(loader.isAvailable(key));
		assertThrows(IllegalArgumentException.class, () -> loader.getLastChangeTimestamp(key));
	}

	@Test public void loadNonExistingFile(){
		ConfigurationSourceKey key = new ConfigurationSourceKey(Type.FILE, Format.JSON, "foo");
		FileLoader loader = new FileLoader();
		assertFalse(loader.isAvailable(key));
		assertThrows(IllegalArgumentException.class, () -> loader.getContent(key));
	}

	@Test public void loadDirectory(){
		ConfigurationSourceKey key = new ConfigurationSourceKey(Type.FILE, Format.JSON, "empty");
		FileLoader loader = new FileLoader();
		assertThrows(RuntimeException.class, () -> loader.getContent(key));
	}
}
