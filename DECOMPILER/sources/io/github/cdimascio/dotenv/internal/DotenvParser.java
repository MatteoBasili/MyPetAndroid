package io.github.cdimascio.dotenv.internal;

import io.github.cdimascio.dotenv.DotenvEntry;
import io.github.cdimascio.dotenv.DotenvException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DotenvParser {
    private final Function<String, Boolean> isComment = new DotenvParser$$ExternalSyntheticLambda1();
    private final Function<String, Boolean> isQuoted = new DotenvParser$$ExternalSyntheticLambda2();
    private final Function<String, Boolean> isWhiteSpace = new DotenvParser$$ExternalSyntheticLambda0();
    private final Function<String, DotenvEntry> parseLine = new DotenvParser$$ExternalSyntheticLambda3();
    private final DotenvReader reader;
    private final boolean throwIfMalformed;
    private final boolean throwIfMissing;

    static /* synthetic */ Boolean lambda$new$1(String s) {
        return Boolean.valueOf(s.startsWith("#") || s.startsWith("////"));
    }

    static /* synthetic */ Boolean lambda$new$2(String s) {
        return Boolean.valueOf(s.startsWith("\"") && s.endsWith("\""));
    }

    public DotenvParser(DotenvReader reader2, boolean throwIfMissing2, boolean throwIfMalformed2) {
        this.reader = reader2;
        this.throwIfMissing = throwIfMissing2;
        this.throwIfMalformed = throwIfMalformed2;
    }

    public List<DotenvEntry> parse() throws DotenvException {
        List<DotenvEntry> entries = new ArrayList<>();
        for (String line : lines()) {
            String l = line.trim();
            if (!this.isWhiteSpace.apply(l).booleanValue() && !this.isComment.apply(l).booleanValue() && !isBlank(l)) {
                DotenvEntry entry = this.parseLine.apply(l);
                if (entry != null) {
                    entries.add(new DotenvEntry(entry.getKey(), normalizeValue(entry.getValue())));
                } else if (this.throwIfMalformed) {
                    throw new DotenvException("Malformed entry " + l);
                }
            }
        }
        return entries;
    }

    private List<String> lines() throws DotenvException {
        try {
            return this.reader.read();
        } catch (DotenvException e) {
            if (!this.throwIfMissing) {
                return Collections.emptyList();
            }
            throw e;
        } catch (IOException e2) {
            throw new DotenvException((Throwable) e2);
        }
    }

    private String normalizeValue(String value) {
        String tr = value.trim();
        if (this.isQuoted.apply(tr).booleanValue()) {
            return tr.substring(1, value.length() - 1);
        }
        return tr;
    }

    private static boolean matches(String regex, String text) {
        return Pattern.compile(regex).matcher(text).matches();
    }

    /* access modifiers changed from: private */
    public static DotenvEntry matchEntry(String regex, String text) {
        Matcher matcher = Pattern.compile(regex).matcher(text);
        if (!matcher.matches() || matcher.groupCount() < 3) {
            return null;
        }
        return new DotenvEntry(matcher.group(1), matcher.group(3));
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
