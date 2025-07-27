package xyz.sadiulhakim.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import xyz.sadiulhakim.enumeration.ContentType;
import xyz.sadiulhakim.enumeration.HttpMethod;

import java.net.URI;
import java.util.Map;

public class RequestTest {

    @Test
    void testDomain() {
        Request build = new Request.Builder()
                .url(URI.create("https://example.com"))
                .method(HttpMethod.POST)
                .params(Map.of("user", 1))
                .headers(Map.of("Accept", "Application/Json"))
                .contentType(ContentType.RAW_JSON)
                .body("{\"name\":\"Hakim\"}")
                .build();

        System.out.println(build.toString());

        Assertions.assertEquals("""
                POST : https://example.com?user=1&
                --
                Accept : Application/Json
                --
                RAW_JSON :\s
                {"name":"Hakim"}
                """, build +"\n");
    }
}
