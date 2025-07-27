package xyz.sadiulhakim.enumeration;

public enum ContentType {
    FORM_DATA(1, "Form Data", "multipart/form-data"),
    X_WWW_FORM_URLENCODED(2, "x-www-url-encoded", "application/x-www-form-urlencoded"),
    RAW_JSON(3, "Raw Json", "application/json");

    private final int id;
    private final String name;
    private final String value;

    ContentType(int id, String name, String value) {
        this.id = id;
        this.name = name;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public static ContentType getById(int id) {
        for (ContentType type : values()) {
            if (type.getId() == id)
                return type;
        }

        return RAW_JSON;
    }

    public static ContentType getByName(String name) {
        for (ContentType type : values()) {
            if (type.getName().equals(name))
                return type;
        }

        return RAW_JSON;
    }
}
