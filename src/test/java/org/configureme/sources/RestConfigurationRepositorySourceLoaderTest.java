package org.configureme.sources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.configureme.sources.configurationrepository.ConfigurationRepositorySourceLoader;
import org.configureme.sources.configurationrepository.ReplyObject;
import org.eclipse.jetty.util.log.Slf4jLog;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@Ignore
public class RestConfigurationRepositorySourceLoaderTest {
    private static final Logger log = LoggerFactory.getLogger(Slf4jLog.class);
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(10088);
    private ConfigurationRepositorySourceLoader loader = new ConfigurationRepositorySourceLoader();

    @Before
    public void init() {
        stubFor(get(urlEqualTo("/configurations/Test"))
                .withHeader("Accept", equalTo("application/json"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(getConfigBody())));

        stubFor(get(urlEqualTo("/lastChangeTimestamp/Test"))
                .withHeader("Accept", equalTo("application/json"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(getChangeTimestampBody())));
    }

    @Test
    public void getContentTest() {
        String result = loader.getContent(getTestKey());
        log.info(result);
        assertThat(result, is("{\"a\":\"A\",\"b\":\"B\",\"c\":\"C\"}"));
    }

    @Test
    public void getLastChangeTimestamp() {
        long result = loader.getLastChangeTimestamp(getTestKey());
        assertThat(result, is(345667076465L));
    }

    @Test
    public void isAvailableTest() {
        boolean result = loader.isAvailable(getTestKey());
        assertThat(result, is(true));
    }

    public static ConfigurationSourceKey getTestKey() {
        ConfigurationSourceKey key = new ConfigurationSourceKey();
        key.setName("Test");
        //key.setType(ConfigurationSourceKey.Type.REST);
        key.setFormat(ConfigurationSourceKey.Format.JSON);
        //key.setRemoteConfigurationRepositoryUrl("http://localhost:10080");
        return key;
    }

    private static String getConfigBody() {
        ObjectMapper mapper = new ObjectMapper();
        TestConfiguration testConfiguration = new TestConfiguration();
        testConfiguration.setA("A");
        testConfiguration.setB("B");
        testConfiguration.setC("C");
        ReplyObject replyObject = ReplyObject.success("Test", testConfiguration);
        String result = null;
        try {
            result = mapper.writeValueAsString(replyObject);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static String getChangeTimestampBody() {
        ObjectMapper mapper = new ObjectMapper();
        ReplyObject replyObject = ReplyObject.success("Test", 345667076465L);
        String result = null;
        try {
            result = mapper.writeValueAsString(replyObject);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return result;
    }

    static class TestConfiguration {
        private String a;
        private String b;
        private String c;

        public String getA() {
            return a;
        }

        public void setA(String a) {
            this.a = a;
        }

        public String getB() {
            return b;
        }

        public void setB(String b) {
            this.b = b;
        }

        public String getC() {
            return c;
        }

        public void setC(String c) {
            this.c = c;
        }

        @Override
        public String toString() {
            return "{" +
                    "\"a\":" + (a == null ? "null" : "\"" + a + "\"") + ", " +
                    "\"b\":" + (b == null ? "null" : "\"" + b + "\"") + ", " +
                    "\"c\":" + (c == null ? "null" : "\"" + c + "\"") +
                    "}";
        }
    }
}
