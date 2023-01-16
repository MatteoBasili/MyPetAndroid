package io.github.cdimascio.dotenv;

public class DotenvEntry {
    private final String key;
    private final String value;

    public DotenvEntry(String key2, String value2) {
        this.key = key2;
        this.value = value2;
    }

    public String getKey() {
        return this.key;
    }

    public String getValue() {
        return this.value;
    }

    public String toString() {
        return this.key + "=" + this.value;
    }
}
