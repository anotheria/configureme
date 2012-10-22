package org.configureme.embeddedobjects;

import org.configureme.ConfigurationManager;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test for weird combinations of embeding, see embeddedobjects for details.
 *
 * @author lrosenberg
 * @since 22.10.12 11:33
 */
public class EmbeddedObjectsTest {
	@Test public void testEmbeddedObjects(){
		OuterObject outer = new OuterObject();
		ConfigurationManager.INSTANCE.configure(outer);

		assertEquals("first", outer.getList()[0].getName());
		assertEquals("first child", outer.getList()[0].getItem().getName());
		assertEquals(12, outer.getList()[0].getItem().getAmount());


		assertEquals("second", outer.getList()[1].getName());
		assertEquals("second child", outer.getList()[1].getItem().getName());
		assertEquals(15, outer.getList()[1].getItem().getAmount());
	}
}
