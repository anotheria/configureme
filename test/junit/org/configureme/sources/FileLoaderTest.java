package org.configureme.sources;

import org.configureme.sources.ConfigurationSourceKey.Format;
import org.configureme.sources.ConfigurationSourceKey.Type;
import org.junit.Test;

import static junit.framework.Assert.*;

public class FileLoaderTest {
	@Test (expected=AssertionError.class) public void acceptOnlyFileKeys(){
		ConfigurationSourceKey key = new ConfigurationSourceKey(Type.FIXTURE, Format.JSON, "foo");
		new FileLoader().isAvailable(key);
		fail("An error should have been thrown.");
	}

	@Test (expected=IllegalArgumentException.class) public void checkNonExistingFile(){
		ConfigurationSourceKey key = new ConfigurationSourceKey(Type.FILE, Format.JSON, "foo");
		FileLoader loader = new FileLoader();
		assertFalse(loader.isAvailable(key));
		loader.getLastChangeTimestamp(key);
		fail("An exception should have been thrown.");
	}

	@Test (expected=IllegalArgumentException.class) public void loadNonExistingFile(){
		ConfigurationSourceKey key = new ConfigurationSourceKey(Type.FILE, Format.JSON, "foo");
		FileLoader loader = new FileLoader();
		assertFalse(loader.isAvailable(key));
		loader.getContent(key);
		fail("An exception should have been thrown.");
	}
}
