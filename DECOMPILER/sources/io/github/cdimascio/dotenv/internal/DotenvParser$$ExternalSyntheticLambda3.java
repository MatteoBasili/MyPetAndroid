package io.github.cdimascio.dotenv.internal;

import java.util.function.Function;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class DotenvParser$$ExternalSyntheticLambda3 implements Function {
    public final Object apply(Object obj) {
        return DotenvParser.matchEntry("^\\s*([\\w.\\-]+)\\s*(=)\\s*(.*)?\\s*$", (String) obj);
    }
}
