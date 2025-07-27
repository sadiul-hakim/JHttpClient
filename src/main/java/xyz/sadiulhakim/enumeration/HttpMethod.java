package xyz.sadiulhakim.enumeration;

public enum HttpMethod {
    GET(1, "Get"),
    POST(2, "Post"),
    PUT(3, "Put"),
    PATCH(4, "Patch"),
    DELETE(5, "Delete");

    private final int id;
    private final String name;

    HttpMethod(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static HttpMethod getById(int id) {
        for (HttpMethod method : values()) {
            if (method.getId() == id)
                return method;
        }

        return null;
    }

    public static HttpMethod getByName(String name) {
        for (HttpMethod method : values()) {
            if (method.getName().equals(name))
                return method;
        }

        return null;
    }
}
