package org.java.notification.push;

import org.java.notification.push.application.Application;
import org.java.notification.push.application.ApplicationAndroid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by msamoylych on 06.06.2017.
 */
@Test
@ContextConfiguration(locations = "classpath:storage-context-test.xml")
public class PushStorageTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private PushStorage pushStorage;

    private Application a;

    @BeforeClass
    public void beforeClass() {
        a = new ApplicationAndroid();
        a.id(1L);
    }

    @Test
    public void testInsert() throws Exception {
        Push<Application> push = new Push<>();
        push.application(a);
        push.token("Token");
        push.title("Title");
        push.body("Body");
        pushStorage.insert(push);

        Assert.assertNotNull(push.id());
    }

    @Test
    public void testInsertList() throws Exception {
        List<Push> pushes = new ArrayList<>();

        Push<Application> push1 = new Push<>();
        push1.application(a);
        push1.token("Token1");
        push1.title("Title1");
        push1.body("Body1");
        pushes.add(push1);

        Push<Application> push2 = new Push<>();
        push2.application(a);
        push2.token("Token2");
        push2.title("Title2");
        push2.body("Body2");
        pushes.add(push2);

        Push<Application> push3 = new Push<>();
        push3.application(a);
        push3.token("Token3");
        push3.title("Title3");
        push3.body("Body3");
        pushes.add(push3);

        pushStorage.insert(pushes);

        Assert.assertNotNull(push1.id());
        Assert.assertNotNull(push2.id());
        Assert.assertNotNull(push3.id());
    }
}
