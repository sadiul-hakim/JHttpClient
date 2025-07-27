package xyz.sadiulhakim.validator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import xyz.sadiulhakim.enumeration.ContentType;
import xyz.sadiulhakim.enumeration.HttpMethod;
import xyz.sadiulhakim.exception.InvalidInputException;

import java.net.URI;
import java.net.URISyntaxException;

public class ValidatorUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    private ValidatorUtil() {
    }

    public static URI validateUrl(String url) throws InvalidInputException {
        if (url == null || url.isBlank()) {
            throw new InvalidInputException("URL cannot be null or blank");
        }

        if (!(url.startsWith("http://") || url.startsWith("https://"))) {
            throw new InvalidInputException("URL must start with http:// or https://");
        }

        try {
            URI uri = new URI(url);
            uri.toURL(); // confirms it's also a valid URL
            return uri;
        } catch (URISyntaxException | IllegalArgumentException | java.net.MalformedURLException e) {
            throw new InvalidInputException("Malformed URL: " + e.getMessage());
        }
    }

    public static HttpMethod validateMethod(int methodId) throws InvalidInputException {
        var method = HttpMethod.getById(methodId);
        if (method == null) {
            throw new InvalidInputException("Invalid method " + methodId);
        }

        return method;
    }

    public static ContentType validateContentType(int contentTypeId) {
        var contentType = ContentType.getById(contentTypeId);
        if (contentType == null) {
            throw new InvalidInputException("Invalid contentType " + contentTypeId);
        }

        return contentType;
    }

    public static void validateJsonBody(String jsonString) {
        try {
            mapper.readTree(jsonString);
        } catch (JsonProcessingException e) {
            throw new InvalidInputException("Invalid json format!");
        }
    }
}
