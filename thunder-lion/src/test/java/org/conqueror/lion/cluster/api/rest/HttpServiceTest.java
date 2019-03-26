package org.conqueror.lion.cluster.api.rest;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.server.Route;
import akka.testkit.javadsl.TestKit;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.conqueror.common.exceptions.serialize.SerializableException;
import org.conqueror.common.utils.config.ConfigLoader;
import org.conqueror.common.utils.test.TestClass;
import org.conqueror.lion.cluster.api.rest.router.message.MessageServiceRouter;
import org.conqueror.lion.cluster.communicate.Asker;
import org.conqueror.lion.config.HttpServiceConfig;
import org.conqueror.lion.config.NodeConfig;
import org.conqueror.lion.message.ThunderMessage;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;


public class HttpServiceTest extends TestClass {

    public static class MyRequest1 implements ThunderMessage<MyRequest1> {

        @Override
        public void writeObject(DataOutput output) {

        }

        @Override
        public MyRequest1 readObject(DataInput input) {
            return new MyRequest1();
        }

    }

    public static class MyRequest2 implements ThunderMessage<MyRequest2> {

        private final String name;

        @JsonCreator
        public MyRequest2(@JsonProperty("name") String name) {
            this.name = name;
        }

        @Override
        public void writeObject(DataOutput output) throws SerializableException {
            try {
                output.writeUTF(name);
            } catch (IOException e) {
                throw new SerializableException(e);
            }
        }

        @Override
        public MyRequest2 readObject(DataInput input) throws SerializableException {
            try {
                return new MyRequest2(input.readUTF());
            } catch (IOException e) {
                throw new SerializableException(e);
            }
        }

    }

    public static class MyResponse1 implements ThunderMessage<MyResponse1> {

        private final String name;

        @JsonCreator
        public MyResponse1(@JsonProperty("name") String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public void writeObject(DataOutput output) throws SerializableException {
            try {
                output.writeUTF(name);
            } catch (IOException e) {
                throw new SerializableException(e);
            }
        }

        @Override
        public MyResponse1 readObject(DataInput input) throws SerializableException {
            try {
                return new MyResponse1(input.readUTF());
            } catch (IOException e) {
                throw new SerializableException(e);
            }
        }

    }

    public static class MyResponse2 implements ThunderMessage<MyResponse2> {

        private final String name;

        @JsonCreator
        public MyResponse2(@JsonProperty("name") String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public void writeObject(DataOutput output) throws SerializableException {
            try {
                output.writeUTF(name);
            } catch (IOException e) {
                throw new SerializableException(e);
            }
        }

        @Override
        public MyResponse2 readObject(DataInput input) throws SerializableException {
            try {
                return new MyResponse2(input.readUTF());
            } catch (IOException e) {
                throw new SerializableException(e);
            }
        }

    }

    public static class MyMessageServiceRouter extends MessageServiceRouter {

        public MyMessageServiceRouter(ActorSystem system, ActorRef nodeMaster, NodeConfig config, long messageTimeout) {
            super(system, nodeMaster, config, messageTimeout);
        }

        @Override
        protected Route createGetRoute() {
            return get(
                () ->
                    route(
                        processPathPrefix("path1", new MyRequest1(), MyResponse1.class)
                    )
            );
        }

        @Override
        protected Route createPostRoute() {
            return post(
                () ->
                    route(
                        processPathPrefixEntity("path2", MyRequest2.class, MyResponse2.class)
                    )
            );
        }

        @Override
        protected Route createPutRoute() {
            return put(
                () ->
                    route(
                        processPathPrefixPath("path3", name -> completeOKWithFuture(
                            Asker.ask(getNodeMaster(), new MyRequest2(name), MyResponse2.class, getMessageTimeout())
                            , Jackson.marshaller()
                        ))
                    )
            );
        }

        @Override
        protected Route createDeleteRoute() {
            return delete(
                () ->
                    route(
                        processPathPrefixParam("path4", params -> completeOKWithFuture(
                            Asker.ask(getNodeMaster(), new MyRequest2(params.get("name")), MyResponse2.class, getMessageTimeout())
                            , Jackson.marshaller()
                        ))
                    )
            );
        }

    }

    public static class MasterActor extends AbstractActor {

        @Override
        public Receive createReceive() {
            return receiveBuilder()
                .match(
                    MyRequest1.class,
                    request -> getSender().tell(new MyResponse1("my-name-1"), getSelf()))
                .match(
                    MyRequest2.class,
                    request -> getSender().tell(new MyResponse2(request.name), getSelf()))
                .build();
        }

    }

    static ActorSystem system;

    static HttpServiceConfig config;


    @Before
    public void setUp() throws Exception {
        system = ActorSystem.create();
        config = new HttpServiceConfig(ConfigLoader.parse("{\n" +
            "akka.message.timeout = 6000\n" +
            "api.service.http.host = \"localhost\"\n" +
            "api.service.http.port = 8080\n" +
            "}\n"));
    }

    @Test
    public void test() throws IOException {
        new TestKit(system) {
            {
                final Props props = Props.create(MasterActor.class);
                final ActorRef master = system.actorOf(props);
                HttpService http = new HttpService(system, master, config);
                http.open(new MyMessageServiceRouter(system, master, null, config.getMessageTimeout()));

                Assert.assertEquals("{\"name\":\"my-name-1\"}", get(new URL("http://localhost:8080/path1")));
                Assert.assertEquals("{\"name\":\"foo\"}", post(new URL("http://localhost:8080/path2"), "{\"name\":\"foo\"}"));
                Assert.assertEquals("{\"name\":\"foo\"}", put(new URL("http://localhost:8080/path3/foo"), "{}"));
                Assert.assertEquals("{\"name\":\"foo\"}", delete(new URL("http://localhost:8080/path4?name=foo")));
            }

            String result(HttpURLConnection con) throws IOException {
                Assert.assertEquals(200, con.getResponseCode());
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder result = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    result.append(inputLine);
                }
                in.close();

                return result.toString();
            }

            String get(URL url) throws IOException {
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");

                try {
                    return result(con);
                } finally {
                    con.disconnect();
                }
            }

            String post(URL url, String content) throws IOException {
                byte[] postData = content.getBytes(StandardCharsets.UTF_8);

                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("charset", "utf-8");
                con.setRequestProperty("Content-Length", Integer.toString(postData.length));
                con.setDoOutput(true);

                try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                    wr.write(postData);
                }

                try {
                    return result(con);
                } finally {
                    con.disconnect();
                }
            }

            String put(URL url, String content) throws IOException {
                byte[] postData = content.getBytes(StandardCharsets.UTF_8);

                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("PUT");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("charset", "utf-8");
                con.setRequestProperty("Content-Length", Integer.toString(postData.length));
                con.setDoOutput(true);

                try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                    wr.write(postData);
                }

                try {
                    return result(con);
                } finally {
                    con.disconnect();
                }
            }

            String delete(URL url) throws IOException {
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("DELETE");

                try {
                    return result(con);
                } finally {
                    con.disconnect();
                }
            }
        };
    }

    @After
    public void tearDown() throws Exception {
        TestKit.shutdownActorSystem(system);
        system = null;
    }
}