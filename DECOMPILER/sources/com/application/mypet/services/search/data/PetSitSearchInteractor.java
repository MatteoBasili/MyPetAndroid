package com.application.mypet.services.search.data;

import android.os.Handler;
import android.util.Log;
import com.application.mypet.exceptions.ConnectionFailedException;
import com.application.mypet.exceptions.InvalidInputException;
import com.application.mypet.services.search.RatingContract;
import com.application.mypet.services.search.SearchContract;
import com.application.mypet.utils.db.DBConnection;
import com.mysql.jdbc.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class PetSitSearchInteractor {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final String ERROR = "Something went wrong...";
    private static final String SQL_EXCEPTION = "SQL Error: ";
    private DBConnection dbConnection;
    private SearchContract.PetSitSearchInputListener petSitSearchInputListener;
    private SearchContract.PetSitterDetailsListener petSitterDetailsListener;
    private SearchContract.PetSitterSearchListener petSitterSearchListener;
    private RatingContract.RatePetSitListener ratePetSitListener;

    public enum Rating {
        NO_RATE,
        LIKE,
        DISLIKE,
        FROM_LIKE_TO_DISLIKE,
        FROM_DISLIKE_TO_LIKE,
        FROM_LIKE_TO_NONE,
        FROM_DISLIKE_TO_NONE
    }

    public PetSitSearchInteractor(SearchContract.PetSitSearchInputListener petSitSearchInputListener2) {
        this.petSitSearchInputListener = petSitSearchInputListener2;
    }

    public PetSitSearchInteractor(SearchContract.PetSitterSearchListener petSitterSearchListener2) {
        this.petSitterSearchListener = petSitterSearchListener2;
    }

    public PetSitSearchInteractor(SearchContract.PetSitterDetailsListener petSitterDetailsListener2) {
        this.petSitterDetailsListener = petSitterDetailsListener2;
    }

    public PetSitSearchInteractor(RatingContract.RatePetSitListener ratePetSitListener2) {
        this.ratePetSitListener = ratePetSitListener2;
    }

    public void isValidInput(PetSitSearchFilters petSitSearchFilters) {
        new Handler().postDelayed(new PetSitSearchInteractor$$ExternalSyntheticLambda2(this, petSitSearchFilters), 500);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$isValidInput$0$com-application-mypet-services-search-data-PetSitSearchInteractor  reason: not valid java name */
    public /* synthetic */ void m67lambda$isValidInput$0$comapplicationmypetservicessearchdataPetSitSearchInteractor(PetSitSearchFilters petSitSearchFilters) {
        try {
            if (!petSitSearchFilters.getRegion().equals("Select your region")) {
                this.petSitSearchInputListener.onInputSearchSuccess();
                return;
            }
            throw new InvalidInputException("Select the region!");
        } catch (InvalidInputException e) {
            this.petSitSearchInputListener.onInputSearchFailed(e.getMessage());
        }
    }

    public void loadResults(String user, PetSitSearchFilters petSitSearchFilters) {
        new Handler().postDelayed(new PetSitSearchInteractor$$ExternalSyntheticLambda3(this, user, petSitSearchFilters), 10);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$loadResults$1$com-application-mypet-services-search-data-PetSitSearchInteractor  reason: not valid java name */
    public /* synthetic */ void m69lambda$loadResults$1$comapplicationmypetservicessearchdataPetSitSearchInteractor(String user, PetSitSearchFilters petSitSearchFilters) {
        DBConnection dBConnection = new DBConnection();
        this.dbConnection = dBConnection;
        Connection connection = dBConnection.getConnection();
        if (connection != null) {
            PetSitterResultList petSitterResultList = getPetSitListFromDB(user, petSitSearchFilters, connection);
            this.dbConnection.closeConnection(connection);
            if (petSitterResultList == null) {
                this.petSitterSearchListener.onLoadResultsFailed(ERROR);
            } else {
                this.petSitterSearchListener.onLoadResultsSuccess(petSitterResultList);
            }
        } else {
            try {
                throw new ConnectionFailedException();
            } catch (ConnectionFailedException e) {
                this.petSitterSearchListener.onLoadResultsFailed(e.getMessage());
            }
        }
    }

    public void loadDetails(String user, String petSitter) {
        new Handler().postDelayed(new PetSitSearchInteractor$$ExternalSyntheticLambda0(this, user, petSitter), 10);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$loadDetails$2$com-application-mypet-services-search-data-PetSitSearchInteractor  reason: not valid java name */
    public /* synthetic */ void m68lambda$loadDetails$2$comapplicationmypetservicessearchdataPetSitSearchInteractor(String user, String petSitter) {
        DBConnection dBConnection = new DBConnection();
        this.dbConnection = dBConnection;
        Connection connection = dBConnection.getConnection();
        if (connection != null) {
            PetSitDetailsInfo petSitDetailsInfo = loadPetSitDetailsDB(user, petSitter, connection);
            this.dbConnection.closeConnection(connection);
            if (petSitDetailsInfo == null) {
                this.petSitterDetailsListener.onLoadDetailsFailed(ERROR);
            } else {
                this.petSitterDetailsListener.onLoadDetailsSuccess(petSitDetailsInfo);
            }
        } else {
            try {
                throw new ConnectionFailedException();
            } catch (ConnectionFailedException e) {
                this.petSitterDetailsListener.onLoadDetailsFailed(e.getMessage());
            }
        }
    }

    public void ratePetSitter(String user, String petSitter, Enum<Rating> rating) {
        new Handler().postDelayed(new PetSitSearchInteractor$$ExternalSyntheticLambda1(this, user, petSitter, rating), 10);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$ratePetSitter$3$com-application-mypet-services-search-data-PetSitSearchInteractor  reason: not valid java name */
    public /* synthetic */ void m70lambda$ratePetSitter$3$comapplicationmypetservicessearchdataPetSitSearchInteractor(String user, String petSitter, Enum rating) {
        DBConnection dBConnection = new DBConnection("100");
        this.dbConnection = dBConnection;
        Connection connection = dBConnection.getConnection();
        if (connection != null) {
            boolean out = rateDB(user, petSitter, rating, connection);
            this.dbConnection.closeConnection(connection);
            if (!out) {
                this.ratePetSitListener.onRateFailed(ERROR);
            } else {
                this.ratePetSitListener.onRateSuccess();
            }
        } else {
            try {
                throw new ConnectionFailedException();
            } catch (ConnectionFailedException e) {
                this.ratePetSitListener.onRateFailed(e.getMessage());
            }
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v0, resolved type: com.mysql.jdbc.CallableStatement} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v1, resolved type: com.mysql.jdbc.CallableStatement} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v2, resolved type: com.mysql.jdbc.CallableStatement} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x0295  */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x02b1  */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x02ba  */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x02d4  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.application.mypet.services.search.data.PetSitterResultList getPetSitListFromDB(java.lang.String r27, com.application.mypet.services.search.data.PetSitSearchFilters r28, java.sql.Connection r29) {
        /*
            r26 = this;
            r1 = r26
            r2 = r29
            com.application.mypet.registration.data.PetSitterCaredPetsCredentials r0 = r28.getPetSitterCaredPetsCredentials()
            boolean r3 = r0.isDog()
            com.application.mypet.registration.data.PetSitterCaredPetsCredentials r0 = r28.getPetSitterCaredPetsCredentials()
            boolean r4 = r0.isCat()
            com.application.mypet.registration.data.PetSitterCaredPetsCredentials r0 = r28.getPetSitterCaredPetsCredentials()
            boolean r5 = r0.isOtherPets()
            java.lang.String r6 = r28.getRegion()
            java.lang.String r0 = r28.getProvince()
            java.lang.String r7 = "Select your province"
            boolean r7 = r0.equals(r7)
            if (r7 == 0) goto L_0x0030
            java.lang.String r0 = ""
            r7 = r0
            goto L_0x0031
        L_0x0030:
            r7 = r0
        L_0x0031:
            r8 = 0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r15 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r14 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r13 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r12 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r11 = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r10 = r0
            r9 = 0
            r16 = 0
            r17 = r8
            java.lang.String r8 = "{ call search_pet_sitter(?, ?, ?, ?, ?) }"
            java.lang.String r1 = "{ call recover_pet_sitter_favorite(?, ?, ?) }"
            java.sql.CallableStatement r0 = r2.prepareCall(r8)     // Catch:{ SQLException -> 0x0276, all -> 0x0262 }
            r9 = r0
            r0 = 1
            r9.setBoolean(r0, r3)     // Catch:{ SQLException -> 0x024d, all -> 0x0237 }
            r0 = 2
            r9.setBoolean(r0, r4)     // Catch:{ SQLException -> 0x024d, all -> 0x0237 }
            r0 = 3
            r9.setBoolean(r0, r5)     // Catch:{ SQLException -> 0x024d, all -> 0x0237 }
            r0 = 4
            r9.setString(r0, r6)     // Catch:{ SQLException -> 0x024d, all -> 0x0237 }
            r0 = 5
            r9.setString(r0, r7)     // Catch:{ SQLException -> 0x024d, all -> 0x0237 }
            java.sql.CallableStatement r0 = r2.prepareCall(r1)     // Catch:{ SQLException -> 0x024d, all -> 0x0237 }
            r16 = r0
            r2 = r27
            r21 = r1
            r1 = r16
            r0 = 1
            r1.setString(r0, r2)     // Catch:{ SQLException -> 0x021e, all -> 0x0205 }
            r0 = 16
            r2 = 3
            r1.registerOutParameter(r2, r0)     // Catch:{ SQLException -> 0x021e, all -> 0x0205 }
            java.sql.ResultSet r0 = r9.executeQuery()     // Catch:{ SQLException -> 0x021e, all -> 0x0205 }
        L_0x0092:
            boolean r2 = r0.next()     // Catch:{ SQLException -> 0x021e, all -> 0x0205 }
            if (r2 == 0) goto L_0x017f
            java.lang.String r2 = "Photo"
            byte[] r2 = r0.getBytes(r2)     // Catch:{ SQLException -> 0x0167, all -> 0x014f }
            if (r2 == 0) goto L_0x00da
            r18 = r3
            int r3 = r2.length     // Catch:{ SQLException -> 0x00c4, all -> 0x00ae }
            r22 = r4
            r4 = 0
            android.graphics.Bitmap r3 = android.graphics.BitmapFactory.decodeByteArray(r2, r4, r3)     // Catch:{ SQLException -> 0x013b, all -> 0x0127 }
            r15.add(r3)     // Catch:{ SQLException -> 0x013b, all -> 0x0127 }
            goto L_0x00e2
        L_0x00ae:
            r0 = move-exception
            r22 = r4
            r2 = r26
            r16 = r1
            r4 = r10
            r19 = r11
            r20 = r12
            r23 = r13
            r24 = r14
            r25 = r15
            r10 = r21
            goto L_0x02b8
        L_0x00c4:
            r0 = move-exception
            r22 = r4
            r2 = r26
            r16 = r1
            r4 = r10
            r19 = r11
            r20 = r12
            r23 = r13
            r24 = r14
            r25 = r15
            r10 = r21
            goto L_0x0289
        L_0x00da:
            r18 = r3
            r22 = r4
            r3 = 0
            r15.add(r3)     // Catch:{ SQLException -> 0x013b, all -> 0x0127 }
        L_0x00e2:
            java.lang.String r3 = "Username"
            java.lang.String r3 = r0.getString(r3)     // Catch:{ SQLException -> 0x013b, all -> 0x0127 }
            r14.add(r3)     // Catch:{ SQLException -> 0x013b, all -> 0x0127 }
            java.lang.String r4 = "Province"
            java.lang.String r4 = r0.getString(r4)     // Catch:{ SQLException -> 0x013b, all -> 0x0127 }
            r13.add(r4)     // Catch:{ SQLException -> 0x013b, all -> 0x0127 }
            java.lang.String r4 = "Likes"
            int r4 = r0.getInt(r4)     // Catch:{ SQLException -> 0x013b, all -> 0x0127 }
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)     // Catch:{ SQLException -> 0x013b, all -> 0x0127 }
            r12.add(r4)     // Catch:{ SQLException -> 0x013b, all -> 0x0127 }
            java.lang.String r4 = "Dislikes"
            int r4 = r0.getInt(r4)     // Catch:{ SQLException -> 0x013b, all -> 0x0127 }
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)     // Catch:{ SQLException -> 0x013b, all -> 0x0127 }
            r11.add(r4)     // Catch:{ SQLException -> 0x013b, all -> 0x0127 }
            r4 = 2
            r1.setString(r4, r3)     // Catch:{ SQLException -> 0x013b, all -> 0x0127 }
            r1.execute()     // Catch:{ SQLException -> 0x013b, all -> 0x0127 }
            r4 = 3
            boolean r16 = r1.getBoolean(r4)     // Catch:{ SQLException -> 0x013b, all -> 0x0127 }
            java.lang.Boolean r4 = java.lang.Boolean.valueOf(r16)     // Catch:{ SQLException -> 0x013b, all -> 0x0127 }
            r10.add(r4)     // Catch:{ SQLException -> 0x013b, all -> 0x0127 }
            r3 = r18
            r4 = r22
            goto L_0x0092
        L_0x0127:
            r0 = move-exception
            r2 = r26
            r16 = r1
            r4 = r10
            r19 = r11
            r20 = r12
            r23 = r13
            r24 = r14
            r25 = r15
            r10 = r21
            goto L_0x02b8
        L_0x013b:
            r0 = move-exception
            r2 = r26
            r16 = r1
            r4 = r10
            r19 = r11
            r20 = r12
            r23 = r13
            r24 = r14
            r25 = r15
            r10 = r21
            goto L_0x0289
        L_0x014f:
            r0 = move-exception
            r18 = r3
            r22 = r4
            r2 = r26
            r16 = r1
            r4 = r10
            r19 = r11
            r20 = r12
            r23 = r13
            r24 = r14
            r25 = r15
            r10 = r21
            goto L_0x02b8
        L_0x0167:
            r0 = move-exception
            r18 = r3
            r22 = r4
            r2 = r26
            r16 = r1
            r4 = r10
            r19 = r11
            r20 = r12
            r23 = r13
            r24 = r14
            r25 = r15
            r10 = r21
            goto L_0x0289
        L_0x017f:
            r18 = r3
            r22 = r4
            com.application.mypet.services.search.data.PetSitterResultList r2 = new com.application.mypet.services.search.data.PetSitterResultList     // Catch:{ SQLException -> 0x01f0, all -> 0x01db }
            r3 = r9
            r9 = r2
            r4 = r10
            r10 = r15
            r19 = r11
            r11 = r14
            r20 = r12
            r12 = r13
            r23 = r13
            r13 = r20
            r24 = r14
            r14 = r19
            r25 = r15
            r15 = r4
            r9.<init>(r10, r11, r12, r13, r14, r15)     // Catch:{ SQLException -> 0x01d1, all -> 0x01c7 }
            r0 = r2
            if (r3 == 0) goto L_0x01c1
            r2 = r26
            r10 = r21
            com.application.mypet.utils.db.DBConnection r9 = r2.dbConnection
            r11 = r3
            com.mysql.jdbc.CallableStatement r11 = (com.mysql.jdbc.CallableStatement) r11
            r9.closeStatement(r11)
            if (r1 == 0) goto L_0x01bb
            com.application.mypet.utils.db.DBConnection r9 = r2.dbConnection
            r11 = r1
            com.mysql.jdbc.CallableStatement r11 = (com.mysql.jdbc.CallableStatement) r11
            r9.closeStatement(r11)
            r16 = r1
            r9 = r3
            goto L_0x02aa
        L_0x01bb:
            java.lang.AssertionError r9 = new java.lang.AssertionError
            r9.<init>()
            throw r9
        L_0x01c1:
            java.lang.AssertionError r9 = new java.lang.AssertionError
            r9.<init>()
            throw r9
        L_0x01c7:
            r0 = move-exception
            r2 = r26
            r10 = r21
            r16 = r1
            r9 = r3
            goto L_0x02b8
        L_0x01d1:
            r0 = move-exception
            r2 = r26
            r10 = r21
            r16 = r1
            r9 = r3
            goto L_0x0289
        L_0x01db:
            r0 = move-exception
            r2 = r26
            r3 = r9
            r4 = r10
            r19 = r11
            r20 = r12
            r23 = r13
            r24 = r14
            r25 = r15
            r10 = r21
            r16 = r1
            goto L_0x02b8
        L_0x01f0:
            r0 = move-exception
            r2 = r26
            r3 = r9
            r4 = r10
            r19 = r11
            r20 = r12
            r23 = r13
            r24 = r14
            r25 = r15
            r10 = r21
            r16 = r1
            goto L_0x0289
        L_0x0205:
            r0 = move-exception
            r2 = r26
            r18 = r3
            r22 = r4
            r3 = r9
            r4 = r10
            r19 = r11
            r20 = r12
            r23 = r13
            r24 = r14
            r25 = r15
            r10 = r21
            r16 = r1
            goto L_0x02b8
        L_0x021e:
            r0 = move-exception
            r2 = r26
            r18 = r3
            r22 = r4
            r3 = r9
            r4 = r10
            r19 = r11
            r20 = r12
            r23 = r13
            r24 = r14
            r25 = r15
            r10 = r21
            r16 = r1
            goto L_0x0289
        L_0x0237:
            r0 = move-exception
            r2 = r26
            r18 = r3
            r22 = r4
            r3 = r9
            r4 = r10
            r19 = r11
            r20 = r12
            r23 = r13
            r24 = r14
            r25 = r15
            r10 = r1
            goto L_0x02b8
        L_0x024d:
            r0 = move-exception
            r2 = r26
            r18 = r3
            r22 = r4
            r3 = r9
            r4 = r10
            r19 = r11
            r20 = r12
            r23 = r13
            r24 = r14
            r25 = r15
            r10 = r1
            goto L_0x0289
        L_0x0262:
            r0 = move-exception
            r2 = r26
            r18 = r3
            r22 = r4
            r4 = r10
            r19 = r11
            r20 = r12
            r23 = r13
            r24 = r14
            r25 = r15
            r10 = r1
            goto L_0x02b8
        L_0x0276:
            r0 = move-exception
            r2 = r26
            r18 = r3
            r22 = r4
            r4 = r10
            r19 = r11
            r20 = r12
            r23 = r13
            r24 = r14
            r25 = r15
            r10 = r1
        L_0x0289:
            java.lang.String r1 = "SQL Error: "
            java.lang.String r3 = r0.getMessage()     // Catch:{ all -> 0x02b7 }
            android.util.Log.e(r1, r3)     // Catch:{ all -> 0x02b7 }
            if (r9 == 0) goto L_0x02b1
            com.application.mypet.utils.db.DBConnection r0 = r2.dbConnection
            r1 = r9
            com.mysql.jdbc.CallableStatement r1 = (com.mysql.jdbc.CallableStatement) r1
            r0.closeStatement(r1)
            if (r16 == 0) goto L_0x02ab
            com.application.mypet.utils.db.DBConnection r0 = r2.dbConnection
            r1 = r16
            com.mysql.jdbc.CallableStatement r1 = (com.mysql.jdbc.CallableStatement) r1
            r0.closeStatement(r1)
            r0 = r17
        L_0x02aa:
            return r0
        L_0x02ab:
            java.lang.AssertionError r0 = new java.lang.AssertionError
            r0.<init>()
            throw r0
        L_0x02b1:
            java.lang.AssertionError r0 = new java.lang.AssertionError
            r0.<init>()
            throw r0
        L_0x02b7:
            r0 = move-exception
        L_0x02b8:
            if (r9 == 0) goto L_0x02d4
            com.application.mypet.utils.db.DBConnection r1 = r2.dbConnection
            r3 = r9
            com.mysql.jdbc.CallableStatement r3 = (com.mysql.jdbc.CallableStatement) r3
            r1.closeStatement(r3)
            if (r16 != 0) goto L_0x02ca
            java.lang.AssertionError r0 = new java.lang.AssertionError
            r0.<init>()
            throw r0
        L_0x02ca:
            com.application.mypet.utils.db.DBConnection r1 = r2.dbConnection
            r3 = r16
            com.mysql.jdbc.CallableStatement r3 = (com.mysql.jdbc.CallableStatement) r3
            r1.closeStatement(r3)
            throw r0
        L_0x02d4:
            java.lang.AssertionError r0 = new java.lang.AssertionError
            r0.<init>()
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.application.mypet.services.search.data.PetSitSearchInteractor.getPetSitListFromDB(java.lang.String, com.application.mypet.services.search.data.PetSitSearchFilters, java.sql.Connection):com.application.mypet.services.search.data.PetSitterResultList");
    }

    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x0311  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x033e  */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x034d  */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x0353  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.application.mypet.services.search.data.PetSitDetailsInfo loadPetSitDetailsDB(java.lang.String r45, java.lang.String r46, java.sql.Connection r47) {
        /*
            r44 = this;
            r1 = r44
            r2 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            r13 = 0
            r14 = 0
            r15 = 0
            r16 = 0
            r17 = 0
            r18 = 0
            r19 = 0
            r20 = 0
            r21 = 0
            r22 = 0
            r23 = r2
            java.lang.String r2 = "{ call load_pet_sit_details(?, ?, ?, ?) }"
            r24 = r3
            r3 = r47
            java.sql.CallableStatement r0 = r3.prepareCall(r2)     // Catch:{ SQLException -> 0x02fe, all -> 0x02f8 }
            r22 = r0
            r0 = 1
            r3 = r45
            r25 = r2
            r2 = r22
            r2.setString(r0, r3)     // Catch:{ SQLException -> 0x02f2, all -> 0x02eb }
            r0 = 2
            r3 = r46
            r2.setString(r0, r3)     // Catch:{ SQLException -> 0x02f2, all -> 0x02eb }
            r0 = 16
            r3 = 3
            r2.registerOutParameter(r3, r0)     // Catch:{ SQLException -> 0x02f2, all -> 0x02eb }
            r3 = 4
            r2.registerOutParameter(r3, r0)     // Catch:{ SQLException -> 0x02f2, all -> 0x02eb }
            java.sql.ResultSet r0 = r2.executeQuery()     // Catch:{ SQLException -> 0x02f2, all -> 0x02eb }
            r38 = r18
            r39 = r19
            r40 = r20
            r18 = r10
            r19 = r11
            r20 = r15
            r11 = r21
            r15 = r7
            r21 = r16
            r7 = r6
            r16 = r8
            r6 = r5
            r5 = r4
            r4 = r24
            r24 = r17
            r17 = r9
            r43 = r14
            r14 = r12
            r12 = r43
        L_0x006b:
            boolean r8 = r0.next()     // Catch:{ SQLException -> 0x02c5, all -> 0x029e }
            if (r8 == 0) goto L_0x0153
            java.lang.String r8 = "Photo"
            byte[] r8 = r0.getBytes(r8)     // Catch:{ SQLException -> 0x012e, all -> 0x0109 }
            if (r8 == 0) goto L_0x0081
            r9 = 0
            int r10 = r8.length     // Catch:{ SQLException -> 0x012e, all -> 0x0109 }
            android.graphics.Bitmap r9 = android.graphics.BitmapFactory.decodeByteArray(r8, r9, r10)     // Catch:{ SQLException -> 0x012e, all -> 0x0109 }
            r4 = r9
            goto L_0x0082
        L_0x0081:
            r4 = 0
        L_0x0082:
            java.lang.String r9 = "Likes"
            int r9 = r0.getInt(r9)     // Catch:{ SQLException -> 0x012e, all -> 0x0109 }
            r5 = r9
            java.lang.String r9 = "Dislikes"
            int r9 = r0.getInt(r9)     // Catch:{ SQLException -> 0x012e, all -> 0x0109 }
            r6 = r9
            java.lang.String r9 = "Name"
            java.lang.String r9 = r0.getString(r9)     // Catch:{ SQLException -> 0x012e, all -> 0x0109 }
            r15 = r9
            java.lang.String r9 = "Surname"
            java.lang.String r9 = r0.getString(r9)     // Catch:{ SQLException -> 0x012e, all -> 0x0109 }
            r16 = r9
            java.lang.String r9 = "Province"
            java.lang.String r9 = r0.getString(r9)     // Catch:{ SQLException -> 0x012e, all -> 0x0109 }
            r17 = r9
            java.lang.String r9 = "PhoneNumber"
            java.lang.String r9 = r0.getString(r9)     // Catch:{ SQLException -> 0x012e, all -> 0x0109 }
            r18 = r9
            java.lang.String r9 = "Email"
            java.lang.String r9 = r0.getString(r9)     // Catch:{ SQLException -> 0x012e, all -> 0x0109 }
            r19 = r9
            java.lang.String r9 = "Description"
            java.lang.String r9 = r0.getString(r9)     // Catch:{ SQLException -> 0x012e, all -> 0x0109 }
            r40 = r9
            java.lang.String r9 = "Dog"
            boolean r9 = r0.getBoolean(r9)     // Catch:{ SQLException -> 0x012e, all -> 0x0109 }
            r14 = r9
            java.lang.String r9 = "Cat"
            boolean r9 = r0.getBoolean(r9)     // Catch:{ SQLException -> 0x012e, all -> 0x0109 }
            r13 = r9
            java.lang.String r9 = "OtherPets"
            boolean r9 = r0.getBoolean(r9)     // Catch:{ SQLException -> 0x012e, all -> 0x0109 }
            r12 = r9
            java.lang.String r9 = "AtHome"
            boolean r9 = r0.getBoolean(r9)     // Catch:{ SQLException -> 0x012e, all -> 0x0109 }
            r20 = r9
            java.lang.String r9 = "AtPetSitHome"
            boolean r9 = r0.getBoolean(r9)     // Catch:{ SQLException -> 0x012e, all -> 0x0109 }
            r21 = r9
            java.lang.String r9 = "HomeVisit"
            boolean r9 = r0.getBoolean(r9)     // Catch:{ SQLException -> 0x012e, all -> 0x0109 }
            r24 = r9
            java.lang.String r9 = "Walk"
            boolean r9 = r0.getBoolean(r9)     // Catch:{ SQLException -> 0x012e, all -> 0x0109 }
            r38 = r9
            java.lang.String r9 = "ChangeOfLitter"
            boolean r9 = r0.getBoolean(r9)     // Catch:{ SQLException -> 0x012e, all -> 0x0109 }
            r39 = r9
            r9 = 3
            boolean r10 = r2.getBoolean(r9)     // Catch:{ SQLException -> 0x012e, all -> 0x0109 }
            r7 = r10
            int r10 = r2.getInt(r3)     // Catch:{ SQLException -> 0x012e, all -> 0x0109 }
            r11 = r10
            goto L_0x006b
        L_0x0109:
            r0 = move-exception
            r22 = r2
            r3 = r4
            r4 = r5
            r5 = r6
            r6 = r7
            r7 = r15
            r8 = r16
            r9 = r17
            r10 = r18
            r15 = r20
            r16 = r21
            r17 = r24
            r18 = r38
            r20 = r40
            r21 = r11
            r11 = r19
            r19 = r39
            r43 = r14
            r14 = r12
            r12 = r43
            goto L_0x034b
        L_0x012e:
            r0 = move-exception
            r22 = r2
            r3 = r4
            r4 = r5
            r5 = r6
            r6 = r7
            r7 = r15
            r8 = r16
            r9 = r17
            r10 = r18
            r15 = r20
            r16 = r21
            r17 = r24
            r18 = r38
            r20 = r40
            r21 = r11
            r11 = r19
            r19 = r39
            r43 = r14
            r14 = r12
            r12 = r43
            goto L_0x0303
        L_0x0153:
            com.application.mypet.services.profile.petsitter.data.LoadProfileInfo r3 = new com.application.mypet.services.profile.petsitter.data.LoadProfileInfo     // Catch:{ SQLException -> 0x02c5, all -> 0x029e }
            r3.<init>(r4, r5, r6)     // Catch:{ SQLException -> 0x02c5, all -> 0x029e }
            r27 = r3
            com.application.mypet.registration.data.ProfileCredentials r29 = new com.application.mypet.registration.data.ProfileCredentials     // Catch:{ SQLException -> 0x02c5, all -> 0x029e }
            r3 = 0
            r22 = 0
            r8 = r29
            r9 = r15
            r10 = r16
            r41 = r11
            r11 = r3
            r3 = r12
            r12 = r17
            r42 = r13
            r13 = r22
            r22 = r4
            r4 = r14
            r14 = r18
            r8.<init>(r9, r10, r11, r12, r13, r14)     // Catch:{ SQLException -> 0x0277, all -> 0x0250 }
            com.application.mypet.registration.data.PetSitterCaredPetsCredentials r8 = new com.application.mypet.registration.data.PetSitterCaredPetsCredentials     // Catch:{ SQLException -> 0x0277, all -> 0x0250 }
            r13 = r42
            r8.<init>(r4, r13, r3)     // Catch:{ SQLException -> 0x022b, all -> 0x0206 }
            r30 = r8
            com.application.mypet.registration.data.PetSitterServicesCredentials r8 = new com.application.mypet.registration.data.PetSitterServicesCredentials     // Catch:{ SQLException -> 0x022b, all -> 0x0206 }
            r31 = r8
            r32 = r20
            r33 = r21
            r34 = r24
            r35 = r38
            r36 = r39
            r37 = r40
            r31.<init>(r32, r33, r34, r35, r36, r37)     // Catch:{ SQLException -> 0x022b, all -> 0x0206 }
            r31 = r8
            com.application.mypet.services.search.data.PetSitRating r8 = new com.application.mypet.services.search.data.PetSitRating     // Catch:{ SQLException -> 0x022b, all -> 0x0206 }
            r11 = r41
            r8.<init>(r7, r11)     // Catch:{ SQLException -> 0x01e3, all -> 0x01c0 }
            r28 = r8
            com.application.mypet.services.search.data.PetSitDetailsInfo r8 = new com.application.mypet.services.search.data.PetSitDetailsInfo     // Catch:{ SQLException -> 0x01e3, all -> 0x01c0 }
            r26 = r8
            r32 = r19
            r26.<init>(r27, r28, r29, r30, r31, r32)     // Catch:{ SQLException -> 0x01e3, all -> 0x01c0 }
            r0 = r8
            if (r2 == 0) goto L_0x01ba
            com.application.mypet.utils.db.DBConnection r8 = r1.dbConnection
            r9 = r2
            com.mysql.jdbc.CallableStatement r9 = (com.mysql.jdbc.CallableStatement) r9
            r8.closeStatement(r9)
            r12 = r3
            r14 = r4
            r4 = r22
            r22 = r2
            r2 = r0
            goto L_0x033d
        L_0x01ba:
            java.lang.AssertionError r8 = new java.lang.AssertionError
            r8.<init>()
            throw r8
        L_0x01c0:
            r0 = move-exception
            r14 = r3
            r12 = r4
            r4 = r5
            r5 = r6
            r6 = r7
            r7 = r15
            r8 = r16
            r9 = r17
            r10 = r18
            r15 = r20
            r16 = r21
            r3 = r22
            r17 = r24
            r18 = r38
            r20 = r40
            r22 = r2
            r21 = r11
            r11 = r19
            r19 = r39
            goto L_0x034b
        L_0x01e3:
            r0 = move-exception
            r14 = r3
            r12 = r4
            r4 = r5
            r5 = r6
            r6 = r7
            r7 = r15
            r8 = r16
            r9 = r17
            r10 = r18
            r15 = r20
            r16 = r21
            r3 = r22
            r17 = r24
            r18 = r38
            r20 = r40
            r22 = r2
            r21 = r11
            r11 = r19
            r19 = r39
            goto L_0x0303
        L_0x0206:
            r0 = move-exception
            r11 = r41
            r14 = r3
            r12 = r4
            r4 = r5
            r5 = r6
            r6 = r7
            r7 = r15
            r8 = r16
            r9 = r17
            r10 = r18
            r15 = r20
            r16 = r21
            r3 = r22
            r17 = r24
            r18 = r38
            r20 = r40
            r22 = r2
            r21 = r11
            r11 = r19
            r19 = r39
            goto L_0x034b
        L_0x022b:
            r0 = move-exception
            r11 = r41
            r14 = r3
            r12 = r4
            r4 = r5
            r5 = r6
            r6 = r7
            r7 = r15
            r8 = r16
            r9 = r17
            r10 = r18
            r15 = r20
            r16 = r21
            r3 = r22
            r17 = r24
            r18 = r38
            r20 = r40
            r22 = r2
            r21 = r11
            r11 = r19
            r19 = r39
            goto L_0x0303
        L_0x0250:
            r0 = move-exception
            r11 = r41
            r13 = r42
            r14 = r3
            r12 = r4
            r4 = r5
            r5 = r6
            r6 = r7
            r7 = r15
            r8 = r16
            r9 = r17
            r10 = r18
            r15 = r20
            r16 = r21
            r3 = r22
            r17 = r24
            r18 = r38
            r20 = r40
            r22 = r2
            r21 = r11
            r11 = r19
            r19 = r39
            goto L_0x034b
        L_0x0277:
            r0 = move-exception
            r11 = r41
            r13 = r42
            r14 = r3
            r12 = r4
            r4 = r5
            r5 = r6
            r6 = r7
            r7 = r15
            r8 = r16
            r9 = r17
            r10 = r18
            r15 = r20
            r16 = r21
            r3 = r22
            r17 = r24
            r18 = r38
            r20 = r40
            r22 = r2
            r21 = r11
            r11 = r19
            r19 = r39
            goto L_0x0303
        L_0x029e:
            r0 = move-exception
            r22 = r4
            r3 = r12
            r4 = r14
            r14 = r3
            r12 = r4
            r4 = r5
            r5 = r6
            r6 = r7
            r7 = r15
            r8 = r16
            r9 = r17
            r10 = r18
            r15 = r20
            r16 = r21
            r3 = r22
            r17 = r24
            r18 = r38
            r20 = r40
            r22 = r2
            r21 = r11
            r11 = r19
            r19 = r39
            goto L_0x034b
        L_0x02c5:
            r0 = move-exception
            r22 = r4
            r3 = r12
            r4 = r14
            r14 = r3
            r12 = r4
            r4 = r5
            r5 = r6
            r6 = r7
            r7 = r15
            r8 = r16
            r9 = r17
            r10 = r18
            r15 = r20
            r16 = r21
            r3 = r22
            r17 = r24
            r18 = r38
            r20 = r40
            r22 = r2
            r21 = r11
            r11 = r19
            r19 = r39
            goto L_0x0303
        L_0x02eb:
            r0 = move-exception
            r22 = r2
            r3 = r24
            goto L_0x034b
        L_0x02f2:
            r0 = move-exception
            r22 = r2
            r3 = r24
            goto L_0x0303
        L_0x02f8:
            r0 = move-exception
            r25 = r2
            r3 = r24
            goto L_0x034b
        L_0x02fe:
            r0 = move-exception
            r25 = r2
            r3 = r24
        L_0x0303:
            java.lang.String r2 = "SQL Error: "
            r24 = r3
            java.lang.String r3 = r0.getMessage()     // Catch:{ all -> 0x0344 }
            android.util.Log.e(r2, r3)     // Catch:{ all -> 0x0344 }
            if (r22 == 0) goto L_0x033e
            com.application.mypet.utils.db.DBConnection r0 = r1.dbConnection
            r2 = r22
            com.mysql.jdbc.CallableStatement r2 = (com.mysql.jdbc.CallableStatement) r2
            r0.closeStatement(r2)
            r38 = r18
            r39 = r19
            r40 = r20
            r2 = r23
            r18 = r10
            r19 = r11
            r20 = r15
            r11 = r21
            r15 = r7
            r21 = r16
            r7 = r6
            r16 = r8
            r6 = r5
            r5 = r4
            r4 = r24
            r24 = r17
            r17 = r9
            r43 = r14
            r14 = r12
            r12 = r43
        L_0x033d:
            return r2
        L_0x033e:
            java.lang.AssertionError r0 = new java.lang.AssertionError
            r0.<init>()
            throw r0
        L_0x0344:
            r0 = move-exception
            r3 = r24
            goto L_0x034b
        L_0x0348:
            r0 = move-exception
            r24 = r3
        L_0x034b:
            if (r22 != 0) goto L_0x0353
            java.lang.AssertionError r0 = new java.lang.AssertionError
            r0.<init>()
            throw r0
        L_0x0353:
            com.application.mypet.utils.db.DBConnection r2 = r1.dbConnection
            r1 = r22
            com.mysql.jdbc.CallableStatement r1 = (com.mysql.jdbc.CallableStatement) r1
            r2.closeStatement(r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.application.mypet.services.search.data.PetSitSearchInteractor.loadPetSitDetailsDB(java.lang.String, java.lang.String, java.sql.Connection):com.application.mypet.services.search.data.PetSitDetailsInfo");
    }

    private boolean rateDB(String user, String petSitter, Enum<Rating> rating, Connection connection) {
        int rate;
        boolean out = false;
        if (Rating.LIKE.equals(rating)) {
            rate = 1;
        } else if (Rating.DISLIKE.equals(rating)) {
            rate = 2;
        } else if (Rating.FROM_LIKE_TO_DISLIKE.equals(rating)) {
            rate = 3;
        } else if (Rating.FROM_DISLIKE_TO_LIKE.equals(rating)) {
            rate = 4;
        } else if (Rating.FROM_LIKE_TO_NONE.equals(rating)) {
            rate = 5;
        } else if (Rating.FROM_DISLIKE_TO_NONE.equals(rating)) {
            rate = 6;
        } else {
            rate = 0;
        }
        CallableStatement stmt = null;
        try {
            stmt = connection.prepareCall("{ call rate_pet_sitter(?, ?, ?, ?) }");
            stmt.setString(1, user);
            stmt.setString(2, petSitter);
            stmt.setInt(3, rate);
            stmt.registerOutParameter(4, 16);
            stmt.execute();
            out = stmt.getBoolean(4);
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
        return out;
    }
}
