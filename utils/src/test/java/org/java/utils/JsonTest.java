package org.java.utils;

import org.java.utils.io.IOUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.InputStream;

/**
 * Created by msamoylych on 26.05.2017.
 */
@Test
public class JsonTest {

    @Test
    public void testBuild() throws Exception {
        InputStream is = this.getClass().getResourceAsStream("test.json");
        String test = IOUtils.toString(is).replaceAll("\\s", "");

        String json = Json.start()
                .add("name1", "value")
                .add("name2", 2)
                .addArray("array", "value1", "value2", "value3")
                .startObject("object")
                .add("o1", "v1")
                .add("o2", "v2")
                .endObject()
                .startArray("objects")
                .startObject()
                .add("ao11", "av11")
                .add("ao12", "av12")
                .endObject()
                .startObject()
                .add("ao21", "av21")
                .add("ao22", "av22")
                .endObject()
                .endArray()
                .end();

        Assert.assertEquals(json, test);
    }
}
