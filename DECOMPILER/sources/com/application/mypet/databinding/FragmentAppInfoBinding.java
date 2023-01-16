package com.application.mypet.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import com.application.mypet.R;

public final class FragmentAppInfoBinding implements ViewBinding {
    public final ConstraintLayout appInfoFragment;
    public final ConstraintLayout intConstLayout;
    private final ConstraintLayout rootView;
    public final TextView splashScreenTitle;
    public final TextView textView;
    public final TextView textView1;
    public final TextView textView2;
    public final TextView textView3;
    public final TextView textView4;
    public final TextView textView6;
    public final DefaultToolbarBinding toolbar;

    private FragmentAppInfoBinding(ConstraintLayout rootView2, ConstraintLayout appInfoFragment2, ConstraintLayout intConstLayout2, TextView splashScreenTitle2, TextView textView5, TextView textView12, TextView textView22, TextView textView32, TextView textView42, TextView textView62, DefaultToolbarBinding toolbar2) {
        this.rootView = rootView2;
        this.appInfoFragment = appInfoFragment2;
        this.intConstLayout = intConstLayout2;
        this.splashScreenTitle = splashScreenTitle2;
        this.textView = textView5;
        this.textView1 = textView12;
        this.textView2 = textView22;
        this.textView3 = textView32;
        this.textView4 = textView42;
        this.textView6 = textView62;
        this.toolbar = toolbar2;
    }

    public ConstraintLayout getRoot() {
        return this.rootView;
    }

    public static FragmentAppInfoBinding inflate(LayoutInflater inflater) {
        return inflate(inflater, (ViewGroup) null, false);
    }

    public static FragmentAppInfoBinding inflate(LayoutInflater inflater, ViewGroup parent, boolean attachToParent) {
        View root = inflater.inflate(R.layout.fragment_app_info, parent, false);
        if (attachToParent) {
            parent.addView(root);
        }
        return bind(root);
    }

    /*  JADX ERROR: NullPointerException in pass: CodeShrinkVisitor
        java.lang.NullPointerException
        	at jadx.core.dex.instructions.args.InsnArg.wrapInstruction(InsnArg.java:118)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.inline(CodeShrinkVisitor.java:146)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.shrinkBlock(CodeShrinkVisitor.java:71)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.shrinkMethod(CodeShrinkVisitor.java:43)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.visit(CodeShrinkVisitor.java:35)
        */
    public static com.application.mypet.databinding.FragmentAppInfoBinding bind(android.view.View r26) {
        /*
            r0 = r26
            r13 = r0
            androidx.constraintlayout.widget.ConstraintLayout r13 = (androidx.constraintlayout.widget.ConstraintLayout) r13
            r1 = 2131362080(0x7f0a0120, float:1.834393E38)
            android.view.View r2 = androidx.viewbinding.ViewBindings.findChildViewById(r0, r1)
            r14 = r2
            androidx.constraintlayout.widget.ConstraintLayout r14 = (androidx.constraintlayout.widget.ConstraintLayout) r14
            if (r14 == 0) goto L_0x00a2
            r1 = 2131362312(0x7f0a0208, float:1.8344401E38)
            android.view.View r2 = androidx.viewbinding.ViewBindings.findChildViewById(r0, r1)
            r15 = r2
            android.widget.TextView r15 = (android.widget.TextView) r15
            if (r15 == 0) goto L_0x00a1
            r1 = 2131362362(0x7f0a023a, float:1.8344502E38)
            android.view.View r2 = androidx.viewbinding.ViewBindings.findChildViewById(r0, r1)
            r16 = r2
            android.widget.TextView r16 = (android.widget.TextView) r16
            if (r16 == 0) goto L_0x00a0
            r1 = 2131362363(0x7f0a023b, float:1.8344504E38)
            android.view.View r2 = androidx.viewbinding.ViewBindings.findChildViewById(r0, r1)
            r17 = r2
            android.widget.TextView r17 = (android.widget.TextView) r17
            if (r17 == 0) goto L_0x009f
            r1 = 2131362366(0x7f0a023e, float:1.834451E38)
            android.view.View r2 = androidx.viewbinding.ViewBindings.findChildViewById(r0, r1)
            r18 = r2
            android.widget.TextView r18 = (android.widget.TextView) r18
            if (r18 == 0) goto L_0x009e
            r1 = 2131362367(0x7f0a023f, float:1.8344513E38)
            android.view.View r2 = androidx.viewbinding.ViewBindings.findChildViewById(r0, r1)
            r19 = r2
            android.widget.TextView r19 = (android.widget.TextView) r19
            if (r19 == 0) goto L_0x009d
            r1 = 2131362368(0x7f0a0240, float:1.8344515E38)
            android.view.View r2 = androidx.viewbinding.ViewBindings.findChildViewById(r0, r1)
            r20 = r2
            android.widget.TextView r20 = (android.widget.TextView) r20
            if (r20 == 0) goto L_0x009c
            r1 = 2131362369(0x7f0a0241, float:1.8344517E38)
            android.view.View r2 = androidx.viewbinding.ViewBindings.findChildViewById(r0, r1)
            r21 = r2
            android.widget.TextView r21 = (android.widget.TextView) r21
            if (r21 == 0) goto L_0x009b
            r12 = 2131362386(0x7f0a0252, float:1.8344551E38)
            android.view.View r22 = androidx.viewbinding.ViewBindings.findChildViewById(r0, r12)
            if (r22 == 0) goto L_0x0096
            com.application.mypet.databinding.DefaultToolbarBinding r23 = com.application.mypet.databinding.DefaultToolbarBinding.bind(r22)
            com.application.mypet.databinding.FragmentAppInfoBinding r24 = new com.application.mypet.databinding.FragmentAppInfoBinding
            r2 = r0
            androidx.constraintlayout.widget.ConstraintLayout r2 = (androidx.constraintlayout.widget.ConstraintLayout) r2
            r1 = r24
            r3 = r13
            r4 = r14
            r5 = r15
            r6 = r16
            r7 = r17
            r8 = r18
            r9 = r19
            r10 = r20
            r11 = r21
            r25 = r12
            r12 = r23
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12)
            return r24
        L_0x0096:
            r25 = r12
            r1 = r25
            goto L_0x00a3
        L_0x009b:
            goto L_0x00a3
        L_0x009c:
            goto L_0x00a3
        L_0x009d:
            goto L_0x00a3
        L_0x009e:
            goto L_0x00a3
        L_0x009f:
            goto L_0x00a3
        L_0x00a0:
            goto L_0x00a3
        L_0x00a1:
            goto L_0x00a3
        L_0x00a2:
        L_0x00a3:
            android.content.res.Resources r2 = r26.getResources()
            java.lang.String r2 = r2.getResourceName(r1)
            java.lang.NullPointerException r3 = new java.lang.NullPointerException
            java.lang.String r4 = "Missing required view with ID: "
            java.lang.String r4 = r4.concat(r2)
            r3.<init>(r4)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.application.mypet.databinding.FragmentAppInfoBinding.bind(android.view.View):com.application.mypet.databinding.FragmentAppInfoBinding");
    }
}
