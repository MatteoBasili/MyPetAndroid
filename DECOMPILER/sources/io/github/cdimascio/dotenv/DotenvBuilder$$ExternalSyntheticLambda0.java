package io.github.cdimascio.dotenv;

import java.util.function.Consumer;

/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class DotenvBuilder$$ExternalSyntheticLambda0 implements Consumer {
    public final void accept(Object obj) {
        System.setProperty(((DotenvEntry) obj).getKey(), ((DotenvEntry) obj).getValue());
    }
}
