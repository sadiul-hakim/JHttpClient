package xyz.sadiulhakim.domain;

import xyz.sadiulhakim.enumeration.ContentType;
import xyz.sadiulhakim.enumeration.HttpMethod;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class Request {
    private HttpMethod method;
    private URI url;
    private ContentType contentType;
    private Map<String, Object> params = new HashMap<>();
    private Map<String, String> headers = new HashMap<>();
    private Object body;
    private long timeout;

    public Request() {
    }

    public Request(HttpMethod method, URI url, ContentType contentType, Map<String, Object> params,
                   Map<String, String> headers, Object body, long timeout) {
        this.method = method;
        this.url = url;
        this.contentType = contentType;
        this.params = params;
        this.headers = headers;
        this.body = body;
        this.timeout = timeout;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public URI getUrl() {
        return url;
    }

    public void setUrl(URI url) {
        this.url = url;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(method.toString()).append(" : ").append(url);
        if (!params.isEmpty()) {
            sb.append("?");
        }
        params.forEach((k, v) -> sb.append(k).append("=").append(v).append("&"));
        sb.append("\n");
        sb.append("--").append("\n");
        headers.forEach((k, v) -> {
            sb.append(k).append(" : ").append(v).append("\n");
        });
        sb.append("--\n");
        sb.append(contentType).append(" : ").append("\n").append(body);
        return sb.toString();
    }

    public static class Builder {
        private HttpMethod method = HttpMethod.GET;
        private URI url;
        private ContentType contentType;
        private Map<String, Object> params = new HashMap<>();
        private Map<String, String> headers = new HashMap<>();
        private Object body;
        private long timeout = -1;

        public Builder method(HttpMethod method) {
            this.method = method;
            return this;
        }

        public Builder url(URI url) {
            this.url = url;
            return this;
        }

        public Builder contentType(ContentType contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder param(String key, Object value) {
            this.params.put(key, value);
            return this;
        }

        public Builder params(Map<String, Object> params) {
            this.params.putAll(params);
            return this;
        }

        public Builder header(String key, String value) {
            this.headers.put(key, value);
            return this;
        }

        public Builder headers(Map<String, String> headers) {
            this.headers.putAll(headers);
            return this;
        }

        public Builder body(Object body) {
            this.body = body;
            return this;
        }

        public Builder timeout(long timeout) {
            this.timeout = timeout;
            return this;
        }

        public Request build() {
            return new Request(method, url, contentType, params, headers, body, timeout);
        }
    }
}
