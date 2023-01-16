package androidx.emoji2.text.flatbuffer;

import com.android.tools.r8.annotations.SynthesizedClassV2;
import java.util.function.Supplier;

@SynthesizedClassV2(kind = 19, versionHash = "5e5398f0546d1d7afd62641edb14d82894f11ddc41bce363a0c8d0dac82c9c5a")
/* compiled from: D8$$SyntheticClass */
public final /* synthetic */ class Utf8Old$$ExternalSyntheticThreadLocal1 extends ThreadLocal {
    public final /* synthetic */ Supplier initialValueSupplier;

    public /* synthetic */ Utf8Old$$ExternalSyntheticThreadLocal1(Supplier supplier) {
        this.initialValueSupplier = supplier;
    }

    /* access modifiers changed from: protected */
    public /* synthetic */ Object initialValue() {
        return this.initialValueSupplier.get();
    }
}
