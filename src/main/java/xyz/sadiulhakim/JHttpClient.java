package xyz.sadiulhakim;

import com.fasterxml.jackson.databind.ObjectMapper;
import xyz.sadiulhakim.domain.Request;
import xyz.sadiulhakim.enumeration.ContentType;
import xyz.sadiulhakim.enumeration.HttpMethod;
import xyz.sadiulhakim.exception.InvalidInputException;
import xyz.sadiulhakim.validator.ValidatorUtil;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.util.stream.Collectors;

public class JHttpClient {

    private static final Scanner INPUT = new Scanner(System.in);
    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    private JHttpClient() {
    }

    public static void start() {
        System.out.println("*Welcome To JHttpClient*");
        Request request = takeRequest();
        if (request == null) {
            System.out.println("Something went wrong, could not make the request!");
            return;
        }
        System.out.println("Making call to : " + request.getUrl());
        HttpRequest.BodyPublisher bodyPublisher = getBodyPublisher(request);
        if (bodyPublisher == null) {
            return;
        }
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(request.getUrl())
                .method(request.getMethod().name(), bodyPublisher);

        if (request.getContentType() != null) {
            requestBuilder.header("Content-Type", request.getContentType().getValue());
        }

        if (request.getTimeout() > 0) {
            requestBuilder.timeout(Duration.ofSeconds(request.getTimeout()));
        }

        request.getHeaders().forEach(requestBuilder::header);
        HttpRequest httpRequest = requestBuilder.build();
        try {

            HttpResponse<String> response = CLIENT.send(httpRequest, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            System.out.println("Status : " + response.statusCode());
            System.out.println("Body : ");
            System.out.println(response.body());
        } catch (Exception ex) {
            System.out.println("Could not send request to : " + request.getUrl());
        }
    }

    private static Request takeRequest() {

        try {

            Request.Builder builder = new Request.Builder();

            // Take url
            System.out.print("Enter Url: ");
            String url = INPUT.nextLine();
            URI uri = ValidatorUtil.validateUrl(url);
            builder.url(uri);

            // Take Method
            System.out.println("Choose Method : ");
            System.out.println("1. Get");
            System.out.println("2. Post");
            System.out.println("3. Put");
            System.out.println("4. Patch");
            System.out.println("5. Delete");
            System.out.print(": ");
            int methodId = INPUT.nextInt();
            HttpMethod method = ValidatorUtil.validateMethod(methodId);
            builder.method(method);

            System.out.println("Add Request Parameters : (separate key and value with = and enter q to quite)");
            Map<String, Object> parameters = new HashMap<>();
            while (true) {
                System.out.print(": ");
                String parameter = INPUT.next();
                if (parameter.equalsIgnoreCase("q")) {
                    break;
                }

                if (!parameter.contains("=")) {
                    System.out.println("Invalid pair!");
                }

                String[] pair = parameter.split("=");
                parameters.put(pair[0].trim(), pair[1]);
            }
            builder.params(parameters);

            System.out.println("Add Request Headers : (separate key and value with = and enter q to quite)");
            Map<String, String> headers = new HashMap<>();
            while (true) {
                System.out.print(": ");
                String header = INPUT.next();
                if (header.equalsIgnoreCase("q")) {
                    break;
                }

                if (!header.contains("=")) {
                    System.out.println("Invalid pair!");
                }

                String[] pair = header.split("=");
                headers.put(pair[0].trim(), pair[1]);
            }
            builder.headers(headers);

            if (!method.equals(HttpMethod.GET) && !method.equals(HttpMethod.DELETE)) {
                System.out.println("Choose Content Type : ");
                System.out.println("1. Form Data");
                System.out.println("2. x-www-url-encoded");
                System.out.println("3. Raw Json");
                System.out.print(": ");
                int contentTypeId = INPUT.nextInt();
                ContentType contentType = ValidatorUtil.validateContentType(contentTypeId);
                builder.contentType(contentType);

                if (contentType.equals(ContentType.RAW_JSON)) {
                    System.out.println("Enter Json Body : (finish with :q)");
                    StringBuilder jsonBuilder = new StringBuilder();
                    String line;
                    while (!(line = INPUT.nextLine()).equals(":q")) {
                        jsonBuilder.append(line).append("\n");
                    }
                    ValidatorUtil.validateJsonBody(jsonBuilder.toString());
                    builder.body(jsonBuilder.toString());
                } else if (contentType.equals(ContentType.FORM_DATA) || contentType.equals(ContentType.X_WWW_FORM_URLENCODED)) {
                    System.out.println("Enter Body : (separate key and value with = and enter q to quite)");
                    Map<String, Object> body = new HashMap<>();
                    while (true) {
                        System.out.print(": ");
                        String record = INPUT.next();
                        if (record.equalsIgnoreCase("q")) {
                            break;
                        }

                        if (!record.contains("=")) {
                            System.out.println("Invalid pair!");
                        }

                        String[] pair = record.split("=");
                        body.put(pair[0].trim(), pair[1]);
                    }
                    builder.body(body);
                }
            }

            System.out.println("Enter timeout (in seconds, by default no timeout)");
            System.out.print(": ");
            int timeout = INPUT.nextInt();
            builder.timeout(timeout);
            return builder.build();
        } catch (InvalidInputException inputException) {
            System.out.println(inputException.getMessage());
            return null;
        }
    }

    private static HttpRequest.BodyPublisher getBodyPublisher(Request request) {

        try {
            Object body = request.getBody();

            if (body == null) return HttpRequest.BodyPublishers.noBody();

            return switch (request.getContentType()) {
                case RAW_JSON -> HttpRequest.BodyPublishers.ofString((String) body);

                case X_WWW_FORM_URLENCODED -> {
                    if (!(body instanceof Map)) throw new IllegalArgumentException("Expected Map for form data");
                    @SuppressWarnings("unchecked")
                    Map<String, ?> form = (Map<String, ?>) body;
                    String encoded = form.entrySet().stream()
                            .map(e -> URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8) + "=" +
                                    URLEncoder.encode(String.valueOf(e.getValue()), StandardCharsets.UTF_8))
                            .collect(Collectors.joining("&"));
                    yield HttpRequest.BodyPublishers.ofString(encoded);
                }

                case FORM_DATA -> {

                    // Basic multi-part handling (simplified, no file uploads)
                    if (!(body instanceof Map)) throw new IllegalArgumentException("Expected Map for multipart data");
                    @SuppressWarnings("unchecked")
                    Map<String, ?> form = (Map<String, ?>) body;

                    String boundary = "----WebKitFormBoundary" + UUID.randomUUID();
                    String multipartBody = form.entrySet().stream()
                            .map(e -> "--" + boundary + "\r\n" +
                                    "Content-Disposition: form-data; name=\"" + e.getKey() + "\"\r\n\r\n" +
                                    e.getValue() + "\r\n")
                            .collect(Collectors.joining("")) + "--" + boundary + "--";

                    // Update header
                    request.getHeaders().put("Content-Type", "multipart/form-data; boundary=" + boundary);
                    yield HttpRequest.BodyPublishers.ofString(multipartBody);
                }
            };
        } catch (Exception ex) {
            System.out.println("Internal Exception occurred, Could not build body Publisher.");
            return null;
        }
    }
}
