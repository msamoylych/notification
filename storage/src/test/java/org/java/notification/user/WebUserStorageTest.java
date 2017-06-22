package org.java.notification.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

/**
 * Created by msamoylych on 04.05.2017.
 */
@Test
@ContextConfiguration(locations = "classpath:storage-context-test.xml")
public class WebUserStorageTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private WebUserStorage webUserStorage;

    @Test
    public void test() throws Exception {
        for (int i = 0; i < 100; i++)
            webUserStorage.findUser("user" + i);
    }
}
