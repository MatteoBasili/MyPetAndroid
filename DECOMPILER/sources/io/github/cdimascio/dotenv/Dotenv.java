package io.github.cdimascio.dotenv;

import java.util.Set;

public interface Dotenv {

    public enum Filter {
        DECLARED_IN_ENV_FILE
    }

    Set<DotenvEntry> entries();

    Set<DotenvEntry> entries(Filter filter);

    String get(String str);

    String get(String str, String str2);

    static DotenvBuilder configure() {
        return new DotenvBuilder();
    }

    static Dotenv load() {
        return new DotenvBuilder().load();
    }
}
