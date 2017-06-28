package org.java.netty.client.http2;

import org.java.notification.Message;
import org.java.notification.client.http.HttpClientAdapter;
import org.java.utils.http.Method;
import org.java.utils.http.Status;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by msamoylych on 27.06.2017.
 */
public class NettyHttp2ClientTest {

    @BeforeClass
    public void beforeClass() {
        new AnnotationConfigApplicationContext("org.java");
    }

    @Test
    public void test() throws Exception {
        NettyHttp2Client<Message> client = new NettyHttp2Client<>(new Adapter());
        client.send(new Message() {
        });
        Thread.sleep(10_000L);
    }

    private static class Adapter implements HttpClientAdapter<Message> {
        @Override
        public String host() {
            return "ru.wikipedia.org";
        }

        @Override
        public int port() {
            return 443;
        }

        @Override
        public String path() {
            return "/";
        }

        @Override
        public Method method() {
            return Method.GET;
        }

        @Override
        public void headers(HttpClientAdapter.Headers headers, Message msg) {

        }

        @Override
        public String content(Message msg) {
            return "";
        }

        @Override
        public void handleResponse(Message msg, Status status, HttpClientAdapter.Headers headers, String response) {

        }
    }
}
