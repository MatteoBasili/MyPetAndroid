package io.github.cdimascio.dotenv;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.internal.DotenvParser;
import io.github.cdimascio.dotenv.internal.DotenvReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class DotenvBuilder {
    private String directoryPath = "./";
    private String filename = ".env";
    private boolean systemProperties = false;
    private boolean throwIfMalformed = true;
    private boolean throwIfMissing = true;

    public DotenvBuilder directory(String path) {
        this.directoryPath = path;
        return this;
    }

    public DotenvBuilder filename(String name) {
        this.filename = name;
        return this;
    }

    public DotenvBuilder ignoreIfMissing() {
        this.throwIfMissing = false;
        return this;
    }

    public DotenvBuilder ignoreIfMalformed() {
        this.throwIfMalformed = false;
        return this;
    }

    public DotenvBuilder systemProperties() {
        this.systemProperties = true;
        return this;
    }

    public Dotenv load() throws DotenvException {
        List<DotenvEntry> env = new DotenvParser(new DotenvReader(this.directoryPath, this.filename), this.throwIfMissing, this.throwIfMalformed).parse();
        if (this.systemProperties) {
            env.forEach(new DotenvBuilder$$ExternalSyntheticLambda0());
        }
        return new DotenvImpl(env);
    }

    static class DotenvImpl implements Dotenv {
        private final Map<String, String> envVars;
        private final Map<String, String> envVarsInFile;
        private final Set<DotenvEntry> set;
        private final Set<DotenvEntry> setInFile;

        public DotenvImpl(List<DotenvEntry> envVars2) {
            Map<String, String> map = (Map) envVars2.stream().collect(Collectors.toMap(new DotenvBuilder$DotenvImpl$$ExternalSyntheticLambda0(), new DotenvBuilder$DotenvImpl$$ExternalSyntheticLambda1()));
            this.envVarsInFile = map;
            HashMap hashMap = new HashMap(map);
            this.envVars = hashMap;
            Map<String, String> map2 = System.getenv();
            Objects.requireNonNull(hashMap);
            map2.forEach(new DotenvBuilder$DotenvImpl$$ExternalSyntheticLambda2(hashMap));
            this.set = (Set) hashMap.entrySet().stream().map(new DotenvBuilder$DotenvImpl$$ExternalSyntheticLambda3()).collect(Collectors.collectingAndThen(Collectors.toSet(), new DotenvBuilder$DotenvImpl$$ExternalSyntheticLambda4()));
            this.setInFile = (Set) map.entrySet().stream().map(new DotenvBuilder$DotenvImpl$$ExternalSyntheticLambda5()).collect(Collectors.collectingAndThen(Collectors.toSet(), new DotenvBuilder$DotenvImpl$$ExternalSyntheticLambda4()));
        }

        static /* synthetic */ DotenvEntry lambda$new$0(Map.Entry it) {
            return new DotenvEntry((String) it.getKey(), (String) it.getValue());
        }

        static /* synthetic */ DotenvEntry lambda$new$1(Map.Entry it) {
            return new DotenvEntry((String) it.getKey(), (String) it.getValue());
        }

        public Set<DotenvEntry> entries() {
            return this.set;
        }

        public Set<DotenvEntry> entries(Dotenv.Filter filter) {
            if (filter != null) {
                return this.setInFile;
            }
            return entries();
        }

        public String get(String key) {
            String value = System.getenv(key);
            return value != null ? value : this.envVars.get(key);
        }

        public String get(String key, String defaultValue) {
            String value = get(key);
            return value != null ? value : defaultValue;
        }
    }
}
