package com.application.mypet.services.search.data;

import android.os.Handler;
import android.util.Log;
import com.application.mypet.exceptions.ConnectionFailedException;
import com.application.mypet.services.search.FavoritesContract;
import com.application.mypet.utils.db.DBConnection;
import com.mysql.jdbc.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class FavoritesInteractor {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final String SQL_EXCEPTION = "SQL Error: ";
    private FavoritesContract.AddPetSitToFavListener addPetSitToFavListener;
    private DBConnection dbConnection;
    private FavoritesContract.LoadPetSitFavListener loadPetSitFavListener;

    public FavoritesInteractor(FavoritesContract.AddPetSitToFavListener addPetSitToFavListener2) {
        this.addPetSitToFavListener = addPetSitToFavListener2;
    }

    public FavoritesInteractor(FavoritesContract.LoadPetSitFavListener loadPetSitFavListener2) {
        this.loadPetSitFavListener = loadPetSitFavListener2;
    }

    public void addToFavorites(String user, String petSitter, int pos) {
        new Handler().postDelayed(new FavoritesInteractor$$ExternalSyntheticLambda0(this, user, petSitter, pos), 10);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$addToFavorites$0$com-application-mypet-services-search-data-FavoritesInteractor  reason: not valid java name */
    public /* synthetic */ void m65lambda$addToFavorites$0$comapplicationmypetservicessearchdataFavoritesInteractor(String user, String petSitter, int pos) {
        DBConnection dBConnection = new DBConnection("100");
        this.dbConnection = dBConnection;
        Connection connection = dBConnection.getConnection();
        if (connection != null) {
            Boolean addToFav = setFavDB(user, petSitter, connection);
            this.dbConnection.closeConnection(connection);
            if (addToFav == null) {
                this.addPetSitToFavListener.onAddToFavFailed("Something went wrong...");
            } else if (addToFav.booleanValue()) {
                this.addPetSitToFavListener.onAddToFavSuccess("Added to favorites", pos);
            } else {
                this.addPetSitToFavListener.onAddToFavSuccess("Removed from favorites", pos);
            }
        } else {
            try {
                throw new ConnectionFailedException();
            } catch (ConnectionFailedException e) {
                this.addPetSitToFavListener.onAddToFavFailed(e.getMessage());
            }
        }
    }

    public void loadFavorites(String user) {
        new Handler().postDelayed(new FavoritesInteractor$$ExternalSyntheticLambda1(this, user), 10);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$loadFavorites$1$com-application-mypet-services-search-data-FavoritesInteractor  reason: not valid java name */
    public /* synthetic */ void m66lambda$loadFavorites$1$comapplicationmypetservicessearchdataFavoritesInteractor(String user) {
        DBConnection dBConnection = new DBConnection("100");
        this.dbConnection = dBConnection;
        Connection connection = dBConnection.getConnection();
        if (connection != null) {
            PetSitterResultList petSitterResultList = loadFavDB(user, connection);
            this.dbConnection.closeConnection(connection);
            if (petSitterResultList == null) {
                this.loadPetSitFavListener.onLoadFavFailed("Error loading favorites");
            } else {
                this.loadPetSitFavListener.onLoadFavSuccess(petSitterResultList);
            }
        } else {
            try {
                throw new ConnectionFailedException();
            } catch (ConnectionFailedException e) {
                this.loadPetSitFavListener.onLoadFavFailed(e.getMessage());
            }
        }
    }

    private Boolean setFavDB(String user, String petSitter, Connection connection) {
        Boolean addToFav = null;
        CallableStatement stmt = null;
        try {
            stmt = connection.prepareCall("{ call set_favorite(?, ?, ?) }");
            stmt.setString(1, user);
            stmt.setString(2, petSitter);
            stmt.execute();
            addToFav = Boolean.valueOf(stmt.getBoolean(3));
            if (stmt == null) {
                throw new AssertionError();
            }
        } catch (SQLException e) {
            Log.e(SQL_EXCEPTION, e.getMessage());
            if (0 == 0) {
                throw new AssertionError();
            }
        } catch (Throwable th) {
            if (0 == 0) {
                throw new AssertionError();
            }
            this.dbConnection.closeStatement((CallableStatement) null);
            throw th;
        }
        this.dbConnection.closeStatement(stmt);
        return addToFav;
    }

    /* JADX WARNING: type inference failed for: r0v14, types: [java.sql.CallableStatement] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x00ed  */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x00f7  */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x0100  */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x0106  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.application.mypet.services.search.data.PetSitterResultList loadFavDB(java.lang.String r20, java.sql.Connection r21) {
        /*
            r19 = this;
            r1 = r19
            r2 = 0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r10 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r11 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r12 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r13 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r14 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r15 = r0
            r3 = 0
            java.lang.String r9 = "{ call load_pet_sit_favorites(?) }"
            r8 = r21
            java.sql.CallableStatement r0 = r8.prepareCall(r9)     // Catch:{ SQLException -> 0x00de, all -> 0x00da }
            r7 = r0
            r0 = 1
            r6 = r20
            r7.setString(r0, r6)     // Catch:{ SQLException -> 0x00d2, all -> 0x00ca }
            java.sql.ResultSet r3 = r7.executeQuery()     // Catch:{ SQLException -> 0x00d2, all -> 0x00ca }
            r5 = r3
        L_0x003c:
            boolean r3 = r5.next()     // Catch:{ SQLException -> 0x00d2, all -> 0x00ca }
            if (r3 == 0) goto L_0x009b
            java.lang.String r3 = "Photo"
            byte[] r3 = r5.getBytes(r3)     // Catch:{ SQLException -> 0x0096, all -> 0x0090 }
            if (r3 == 0) goto L_0x0054
            r4 = 0
            int r0 = r3.length     // Catch:{ SQLException -> 0x0096, all -> 0x0090 }
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeByteArray(r3, r4, r0)     // Catch:{ SQLException -> 0x0096, all -> 0x0090 }
            r10.add(r0)     // Catch:{ SQLException -> 0x0096, all -> 0x0090 }
            goto L_0x0058
        L_0x0054:
            r0 = 0
            r10.add(r0)     // Catch:{ SQLException -> 0x0096, all -> 0x0090 }
        L_0x0058:
            java.lang.String r0 = "Username"
            java.lang.String r0 = r5.getString(r0)     // Catch:{ SQLException -> 0x0096, all -> 0x0090 }
            r11.add(r0)     // Catch:{ SQLException -> 0x0096, all -> 0x0090 }
            java.lang.String r4 = "Province"
            java.lang.String r4 = r5.getString(r4)     // Catch:{ SQLException -> 0x0096, all -> 0x0090 }
            r12.add(r4)     // Catch:{ SQLException -> 0x0096, all -> 0x0090 }
            java.lang.String r4 = "Likes"
            int r4 = r5.getInt(r4)     // Catch:{ SQLException -> 0x0096, all -> 0x0090 }
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)     // Catch:{ SQLException -> 0x0096, all -> 0x0090 }
            r13.add(r4)     // Catch:{ SQLException -> 0x0096, all -> 0x0090 }
            java.lang.String r4 = "Dislikes"
            int r4 = r5.getInt(r4)     // Catch:{ SQLException -> 0x0096, all -> 0x0090 }
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)     // Catch:{ SQLException -> 0x0096, all -> 0x0090 }
            r14.add(r4)     // Catch:{ SQLException -> 0x0096, all -> 0x0090 }
            r16 = r0
            r4 = 1
            java.lang.Boolean r0 = java.lang.Boolean.valueOf(r4)     // Catch:{ SQLException -> 0x0096, all -> 0x0090 }
            r15.add(r0)     // Catch:{ SQLException -> 0x0096, all -> 0x0090 }
            r0 = r4
            goto L_0x003c
        L_0x0090:
            r0 = move-exception
            r3 = r7
            r18 = r9
            goto L_0x00fe
        L_0x0096:
            r0 = move-exception
            r3 = r7
            r18 = r9
            goto L_0x00e1
        L_0x009b:
            com.application.mypet.services.search.data.PetSitterResultList r0 = new com.application.mypet.services.search.data.PetSitterResultList     // Catch:{ SQLException -> 0x00d2, all -> 0x00ca }
            r3 = r0
            r4 = r10
            r16 = r5
            r5 = r11
            r6 = r12
            r17 = r7
            r7 = r13
            r8 = r14
            r18 = r9
            r9 = r15
            r3.<init>(r4, r5, r6, r7, r8, r9)     // Catch:{ SQLException -> 0x00c6, all -> 0x00c2 }
            r2 = r0
            if (r17 == 0) goto L_0x00bc
            com.application.mypet.utils.db.DBConnection r0 = r1.dbConnection
            r3 = r17
            com.mysql.jdbc.CallableStatement r3 = (com.mysql.jdbc.CallableStatement) r3
            r0.closeStatement(r3)
            r7 = r17
            goto L_0x00f6
        L_0x00bc:
            java.lang.AssertionError r0 = new java.lang.AssertionError
            r0.<init>()
            throw r0
        L_0x00c2:
            r0 = move-exception
            r3 = r17
            goto L_0x00fe
        L_0x00c6:
            r0 = move-exception
            r3 = r17
            goto L_0x00e1
        L_0x00ca:
            r0 = move-exception
            r17 = r7
            r18 = r9
            r3 = r17
            goto L_0x00fe
        L_0x00d2:
            r0 = move-exception
            r17 = r7
            r18 = r9
            r3 = r17
            goto L_0x00e1
        L_0x00da:
            r0 = move-exception
            r18 = r9
            goto L_0x00fe
        L_0x00de:
            r0 = move-exception
            r18 = r9
        L_0x00e1:
            java.lang.String r4 = "SQL Error: "
            java.lang.String r5 = r0.getMessage()     // Catch:{ all -> 0x00fd }
            android.util.Log.e(r4, r5)     // Catch:{ all -> 0x00fd }
            if (r3 == 0) goto L_0x00f7
            com.application.mypet.utils.db.DBConnection r0 = r1.dbConnection
            r4 = r3
            com.mysql.jdbc.CallableStatement r4 = (com.mysql.jdbc.CallableStatement) r4
            r0.closeStatement(r4)
            r7 = r3
        L_0x00f6:
            return r2
        L_0x00f7:
            java.lang.AssertionError r0 = new java.lang.AssertionError
            r0.<init>()
            throw r0
        L_0x00fd:
            r0 = move-exception
        L_0x00fe:
            if (r3 != 0) goto L_0x0106
            java.lang.AssertionError r0 = new java.lang.AssertionError
            r0.<init>()
            throw r0
        L_0x0106:
            com.application.mypet.utils.db.DBConnection r4 = r1.dbConnection
            r5 = r3
            com.mysql.jdbc.CallableStatement r5 = (com.mysql.jdbc.CallableStatement) r5
            r4.closeStatement(r5)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.application.mypet.services.search.data.FavoritesInteractor.loadFavDB(java.lang.String, java.sql.Connection):com.application.mypet.services.search.data.PetSitterResultList");
    }
}
