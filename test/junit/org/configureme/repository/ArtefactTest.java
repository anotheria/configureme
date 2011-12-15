package org.configureme.repository;

import static junit.framework.Assert.assertEquals;

import org.junit.Test;

public class ArtefactTest {
	@Test public void testForDefaultEnvironment(){
		Artefact toTest = new Artefact("foo");
		toTest.addAttributeValue("test", new PlainValue("value"), null);
		assertEquals(new PlainValue("value"), toTest.getAttribute("test").getValue());
	}

	@Test (expected=IllegalArgumentException.class) public void testForNonExistingAttribute(){
		Artefact toTest = new Artefact("foo");
		toTest.getAttribute("not-existing");
	}
}
