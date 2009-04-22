package org.configureme.repository;

import org.junit.Test;
import static junit.framework.Assert.*;

public class ArtefactTest {
	@Test public void testForDefaultEnvironment(){
		Artefact toTest = new Artefact("foo");
		toTest.addAttributeValue("test", "value", null);
		assertEquals("value", toTest.getAttribute("test").getValue());
	}
	
	@Test (expected=IllegalArgumentException.class) public void testForNonExistingAttribute(){
		Artefact toTest = new Artefact("foo");
		toTest.getAttribute("not-existing");
	}
}
