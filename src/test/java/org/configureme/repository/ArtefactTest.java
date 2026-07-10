package org.configureme.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class ArtefactTest {
	@Test public void testForDefaultEnvironment(){
		Artefact toTest = new Artefact("foo");
		toTest.addAttributeValue("test", new PlainValue("value"), null);
		assertEquals(new PlainValue("value"), toTest.getAttribute("test").getValue());
	}

	@Test public void testForNonExistingAttribute(){
		Artefact toTest = new Artefact("foo");
		assertThrows(IllegalArgumentException.class, () -> toTest.getAttribute("not-existing"));
	}
}
