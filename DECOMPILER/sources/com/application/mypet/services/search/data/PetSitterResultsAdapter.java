package com.application.mypet.services.search.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.application.mypet.R;
import com.application.mypet.services.search.FavoritesContract;
import com.application.mypet.services.search.FavoritesPresenter;
import com.application.mypet.services.search.view.PetSitterDetailsFragment;
import java.util.List;

public class PetSitterResultsAdapter extends ArrayAdapter<String> implements FavoritesContract.AddPetSitToFavView {
    private static final String PET_SITTER_RESULTS_FRAGMENT_TAG = "petSitResFragmentTag";
    AppCompatActivity activity;
    Context context;
    List<Boolean> favorites;
    List<Bitmap> images;
    List<Integer> numDislikes;
    List<Integer> numLikes;
    List<String> places;
    String user;
    List<String> usernames;

    public PetSitterResultsAdapter(AppCompatActivity context2, String user2, List<Bitmap> images2, List<String> usernames2, List<String> places2, List<Integer> numLikes2, List<Integer> numDislikes2, List<Boolean> favorites2) {
        super(context2, R.layout.pet_sitter_single_item, R.id.username, usernames2);
        this.context = context2;
        this.user = user2;
        this.images = images2;
        this.usernames = usernames2;
        this.places = places2;
        this.numLikes = numLikes2;
        this.numDislikes = numDislikes2;
        this.favorites = favorites2;
        this.activity = context2;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v16, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v1, resolved type: com.application.mypet.services.search.data.PetSitterResultsListViewHolder} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.View getView(int r8, android.view.View r9, android.view.ViewGroup r10) {
        /*
            r7 = this;
            r0 = r9
            com.application.mypet.services.search.FavoritesPresenter r1 = new com.application.mypet.services.search.FavoritesPresenter
            r1.<init>((com.application.mypet.services.search.FavoritesContract.AddPetSitToFavView) r7)
            r2 = 0
            if (r0 != 0) goto L_0x0023
            android.content.Context r3 = r7.context
            java.lang.String r4 = "layout_inflater"
            java.lang.Object r3 = r3.getSystemService(r4)
            android.view.LayoutInflater r3 = (android.view.LayoutInflater) r3
            r4 = 2131558526(0x7f0d007e, float:1.874237E38)
            android.view.View r0 = r3.inflate(r4, r10, r2)
            com.application.mypet.services.search.data.PetSitterResultsListViewHolder r4 = new com.application.mypet.services.search.data.PetSitterResultsListViewHolder
            r4.<init>(r0)
            r0.setTag(r4)
            goto L_0x002a
        L_0x0023:
            java.lang.Object r3 = r0.getTag()
            r4 = r3
            com.application.mypet.services.search.data.PetSitterResultsListViewHolder r4 = (com.application.mypet.services.search.data.PetSitterResultsListViewHolder) r4
        L_0x002a:
            java.util.List<android.graphics.Bitmap> r3 = r7.images
            java.lang.Object r3 = r3.get(r8)
            if (r3 == 0) goto L_0x0040
            android.widget.ImageView r3 = r4.itemImage
            java.util.List<android.graphics.Bitmap> r5 = r7.images
            java.lang.Object r5 = r5.get(r8)
            android.graphics.Bitmap r5 = (android.graphics.Bitmap) r5
            r3.setImageBitmap(r5)
            goto L_0x0052
        L_0x0040:
            android.widget.ImageView r3 = r4.itemImage
            android.content.Context r5 = r7.context
            android.content.res.Resources r5 = r5.getResources()
            r6 = 2131230974(0x7f0800fe, float:1.8078016E38)
            android.graphics.drawable.Drawable r5 = r5.getDrawable(r6)
            r3.setImageDrawable(r5)
        L_0x0052:
            android.widget.TextView r3 = r4.itemUsername
            java.util.List<java.lang.String> r5 = r7.usernames
            java.lang.Object r5 = r5.get(r8)
            java.lang.CharSequence r5 = (java.lang.CharSequence) r5
            r3.setText(r5)
            android.widget.TextView r3 = r4.itemPlace
            java.util.List<java.lang.String> r5 = r7.places
            java.lang.Object r5 = r5.get(r8)
            java.lang.CharSequence r5 = (java.lang.CharSequence) r5
            r3.setText(r5)
            android.widget.TextView r3 = r4.itemNumLikes
            java.util.List<java.lang.Integer> r5 = r7.numLikes
            java.lang.Object r5 = r5.get(r8)
            java.lang.String r5 = java.lang.String.valueOf(r5)
            r3.setText(r5)
            android.widget.TextView r3 = r4.itemNumDislikes
            java.util.List<java.lang.Integer> r5 = r7.numDislikes
            java.lang.Object r5 = r5.get(r8)
            java.lang.String r5 = java.lang.String.valueOf(r5)
            r3.setText(r5)
            java.lang.Boolean r3 = java.lang.Boolean.TRUE
            java.util.List<java.lang.Boolean> r5 = r7.favorites
            java.lang.Object r5 = r5.get(r8)
            boolean r3 = r3.equals(r5)
            r5 = 8
            if (r3 == 0) goto L_0x00a5
            android.widget.ImageView r3 = r4.addFav
            r3.setVisibility(r2)
            android.widget.ImageView r2 = r4.noFav
            r2.setVisibility(r5)
            goto L_0x00af
        L_0x00a5:
            android.widget.ImageView r3 = r4.noFav
            r3.setVisibility(r2)
            android.widget.ImageView r2 = r4.addFav
            r2.setVisibility(r5)
        L_0x00af:
            android.widget.ImageView r2 = r4.itemImage
            com.application.mypet.services.search.data.PetSitterResultsAdapter$$ExternalSyntheticLambda1 r3 = new com.application.mypet.services.search.data.PetSitterResultsAdapter$$ExternalSyntheticLambda1
            r3.<init>(r7, r4)
            r2.setOnClickListener(r3)
            android.widget.ImageView r2 = r4.noFav
            com.application.mypet.services.search.data.PetSitterResultsAdapter$$ExternalSyntheticLambda2 r3 = new com.application.mypet.services.search.data.PetSitterResultsAdapter$$ExternalSyntheticLambda2
            r3.<init>(r7, r8, r4, r1)
            r2.setOnClickListener(r3)
            android.widget.ImageView r2 = r4.addFav
            com.application.mypet.services.search.data.PetSitterResultsAdapter$$ExternalSyntheticLambda3 r3 = new com.application.mypet.services.search.data.PetSitterResultsAdapter$$ExternalSyntheticLambda3
            r3.<init>(r7, r8, r4, r1)
            r2.setOnClickListener(r3)
            android.widget.ImageView r2 = r4.info
            com.application.mypet.services.search.data.PetSitterResultsAdapter$$ExternalSyntheticLambda4 r3 = new com.application.mypet.services.search.data.PetSitterResultsAdapter$$ExternalSyntheticLambda4
            r3.<init>(r7, r8)
            r2.setOnClickListener(r3)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.application.mypet.services.search.data.PetSitterResultsAdapter.getView(int, android.view.View, android.view.ViewGroup):android.view.View");
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$getView$1$com-application-mypet-services-search-data-PetSitterResultsAdapter  reason: not valid java name */
    public /* synthetic */ void m71lambda$getView$1$comapplicationmypetservicessearchdataPetSitterResultsAdapter(PetSitterResultsListViewHolder holder, View view) {
        if (holder.itemImage.getDrawable().getConstantState() != this.context.getResources().getDrawable(R.drawable.user).getConstantState()) {
            View zoomImage = LayoutInflater.from(getContext()).inflate(R.layout.zoom_image, (ViewGroup) null);
            ((ImageView) zoomImage.findViewById(R.id.image)).setImageDrawable(holder.itemImage.getDrawable());
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this.context);
            dialogBuilder.setView(zoomImage);
            ((ImageView) zoomImage.findViewById(R.id.close)).setOnClickListener(new PetSitterResultsAdapter$$ExternalSyntheticLambda0(dialogBuilder.show()));
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$getView$2$com-application-mypet-services-search-data-PetSitterResultsAdapter  reason: not valid java name */
    public /* synthetic */ void m72lambda$getView$2$comapplicationmypetservicessearchdataPetSitterResultsAdapter(int position, PetSitterResultsListViewHolder holder, FavoritesPresenter presenter, View v) {
        if (!this.favorites.get(position).booleanValue()) {
            holder.addFav.setVisibility(0);
            holder.noFav.setVisibility(8);
        }
        presenter.addToFav(this.user, this.usernames.get(position), position);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$getView$3$com-application-mypet-services-search-data-PetSitterResultsAdapter  reason: not valid java name */
    public /* synthetic */ void m73lambda$getView$3$comapplicationmypetservicessearchdataPetSitterResultsAdapter(int position, PetSitterResultsListViewHolder holder, FavoritesPresenter presenter, View v) {
        if (this.favorites.get(position).booleanValue()) {
            holder.noFav.setVisibility(0);
            holder.addFav.setVisibility(8);
        }
        presenter.addToFav(this.user, this.usernames.get(position), position);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$getView$4$com-application-mypet-services-search-data-PetSitterResultsAdapter  reason: not valid java name */
    public /* synthetic */ void m74lambda$getView$4$comapplicationmypetservicessearchdataPetSitterResultsAdapter(int position, View v) {
        this.activity.getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, PetSitterDetailsFragment.newInstance(this.user, this.usernames.get(position), position)).addToBackStack(PET_SITTER_RESULTS_FRAGMENT_TAG).commit();
    }

    public void onAddToFavSuccess(String message, int pos) {
        Toast.makeText(this.context, message, 0).show();
        this.favorites.set(pos, Boolean.valueOf(!this.favorites.get(pos).booleanValue()));
    }

    public void onAddToFavFailed(String message) {
        Toast.makeText(this.context, message, 0).show();
    }
}
