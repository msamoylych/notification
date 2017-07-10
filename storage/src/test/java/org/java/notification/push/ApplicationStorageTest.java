package org.java.notification.push;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

/**
 * Created by msamoylych on 30.05.2017.
 */
@Test
@ContextConfiguration(locations = "classpath:storage-context-test.xml")
public class ApplicationStorageTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private ApplicationStorage applicationStorage;

    @Test
    public void test() throws Exception {
        applicationStorage.application(1L, null, null);
    }
}
