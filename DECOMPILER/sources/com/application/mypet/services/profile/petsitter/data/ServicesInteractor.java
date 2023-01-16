package com.application.mypet.services.profile.petsitter.data;

import android.os.Handler;
import android.util.Log;
import com.application.mypet.exceptions.ConnectionFailedException;
import com.application.mypet.exceptions.InvalidInputException;
import com.application.mypet.registration.data.PetSitterServicesCredentials;
import com.application.mypet.services.profile.ProfileContract;
import com.application.mypet.utils.db.DBConnection;
import com.mysql.jdbc.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class ServicesInteractor {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final String SQL_EXCEPTION = "SQL Error: ";
    private DBConnection dbConnection;
    private final ProfileContract.ServicesListener servicesListener;

    public ServicesInteractor(ProfileContract.ServicesListener servicesListener2) {
        this.servicesListener = servicesListener2;
    }

    public boolean isValidInput(PetSitterServicesCredentials petSitterServicesCredentials) {
        return !hasInputError(petSitterServicesCredentials);
    }

    public void saveServices(String user, PetSitterServicesCredentials petSitterServicesCredentials) {
        new Handler().postDelayed(new ServicesInteractor$$ExternalSyntheticLambda1(this, user, petSitterServicesCredentials), 10);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$saveServices$0$com-application-mypet-services-profile-petsitter-data-ServicesInteractor  reason: not valid java name */
    public /* synthetic */ void m5lambda$saveServices$0$comapplicationmypetservicesprofilepetsitterdataServicesInteractor(String user, PetSitterServicesCredentials petSitterServicesCredentials) {
        DBConnection dBConnection = new DBConnection();
        this.dbConnection = dBConnection;
        Connection connection = dBConnection.getConnection();
        if (connection != null) {
            boolean out = saveDB(user, petSitterServicesCredentials, connection);
            this.dbConnection.closeConnection(connection);
            if (!out) {
                this.servicesListener.onStoreFailed("Something went wrong...");
            } else {
                this.servicesListener.onStoreSuccess();
            }
        } else {
            try {
                throw new ConnectionFailedException();
            } catch (ConnectionFailedException e) {
                this.servicesListener.onStoreFailed(e.getMessage());
            }
        }
    }

    public void loadServices(String user) {
        new Handler().postDelayed(new ServicesInteractor$$ExternalSyntheticLambda0(this, user), 10);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$loadServices$1$com-application-mypet-services-profile-petsitter-data-ServicesInteractor  reason: not valid java name */
    public /* synthetic */ void m4lambda$loadServices$1$comapplicationmypetservicesprofilepetsitterdataServicesInteractor(String user) {
        DBConnection dBConnection = new DBConnection("100");
        this.dbConnection = dBConnection;
        Connection connection = dBConnection.getConnection();
        if (connection != null) {
            PetSitterServicesCredentials petSitterServicesCredentials = loadDB(user, connection);
            this.dbConnection.closeConnection(connection);
            this.servicesListener.onLoadServicesSuccess(petSitterServicesCredentials);
            return;
        }
        try {
            throw new ConnectionFailedException();
        } catch (ConnectionFailedException e) {
            this.servicesListener.onLoadFailed(e.getMessage());
        }
    }

    private boolean hasInputError(PetSitterServicesCredentials petSitterServicesCredentials) {
        boolean service1 = petSitterServicesCredentials.isServ1();
        boolean service2 = petSitterServicesCredentials.isServ2();
        boolean service3 = petSitterServicesCredentials.isServ3();
        boolean service4 = petSitterServicesCredentials.isServ4();
        boolean service5 = petSitterServicesCredentials.isServ5();
        if (service1 || service2 || service3 || service4 || service5) {
            return false;
        }
        try {
            throw new InvalidInputException("You don't offer any services!");
        } catch (InvalidInputException e) {
            this.servicesListener.onStoreFailed(e.getMessage());
            return true;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:21:0x0079  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0083  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.application.mypet.registration.data.PetSitterServicesCredentials loadDB(java.lang.String r21, java.sql.Connection r22) {
        /*
            r20 = this;
            r1 = r20
            r2 = 0
            r3 = 0
            java.lang.String r4 = "{ call recover_pet_sit_services(?, ?, ?, ?, ?, ?, ?) }"
            r5 = r22
            java.sql.CallableStatement r0 = r5.prepareCall(r4)     // Catch:{ SQLException -> 0x006a, all -> 0x0066 }
            r3 = r0
            r0 = 1
            r6 = r21
            r3.setString(r0, r6)     // Catch:{ SQLException -> 0x0064 }
            r0 = 2
            r7 = 16
            r3.registerOutParameter(r0, r7)     // Catch:{ SQLException -> 0x0064 }
            r8 = 3
            r3.registerOutParameter(r8, r7)     // Catch:{ SQLException -> 0x0064 }
            r9 = 4
            r3.registerOutParameter(r9, r7)     // Catch:{ SQLException -> 0x0064 }
            r10 = 5
            r3.registerOutParameter(r10, r7)     // Catch:{ SQLException -> 0x0064 }
            r11 = 6
            r3.registerOutParameter(r11, r7)     // Catch:{ SQLException -> 0x0064 }
            r7 = 12
            r12 = 7
            r3.registerOutParameter(r12, r7)     // Catch:{ SQLException -> 0x0064 }
            r3.execute()     // Catch:{ SQLException -> 0x0064 }
            boolean r14 = r3.getBoolean(r0)     // Catch:{ SQLException -> 0x0064 }
            boolean r15 = r3.getBoolean(r8)     // Catch:{ SQLException -> 0x0064 }
            boolean r16 = r3.getBoolean(r9)     // Catch:{ SQLException -> 0x0064 }
            boolean r17 = r3.getBoolean(r10)     // Catch:{ SQLException -> 0x0064 }
            boolean r18 = r3.getBoolean(r11)     // Catch:{ SQLException -> 0x0064 }
            java.lang.String r19 = r3.getString(r12)     // Catch:{ SQLException -> 0x0064 }
            com.application.mypet.registration.data.PetSitterServicesCredentials r0 = new com.application.mypet.registration.data.PetSitterServicesCredentials     // Catch:{ SQLException -> 0x0064 }
            r13 = r0
            r13.<init>(r14, r15, r16, r17, r18, r19)     // Catch:{ SQLException -> 0x0064 }
            r2 = r0
            if (r3 == 0) goto L_0x005c
            com.application.mypet.utils.db.DBConnection r0 = r1.dbConnection
            r7 = r3
            com.mysql.jdbc.CallableStatement r7 = (com.mysql.jdbc.CallableStatement) r7
            r0.closeStatement(r7)
            goto L_0x0082
        L_0x005c:
            java.lang.AssertionError r0 = new java.lang.AssertionError
            r0.<init>()
            throw r0
        L_0x0062:
            r0 = move-exception
            goto L_0x0089
        L_0x0064:
            r0 = move-exception
            goto L_0x006d
        L_0x0066:
            r0 = move-exception
            r6 = r21
            goto L_0x0089
        L_0x006a:
            r0 = move-exception
            r6 = r21
        L_0x006d:
            java.lang.String r7 = "SQL Error: "
            java.lang.String r8 = r0.getMessage()     // Catch:{ all -> 0x0062 }
            android.util.Log.e(r7, r8)     // Catch:{ all -> 0x0062 }
            if (r3 == 0) goto L_0x0083
            com.application.mypet.utils.db.DBConnection r0 = r1.dbConnection
            r7 = r3
            com.mysql.jdbc.CallableStatement r7 = (com.mysql.jdbc.CallableStatement) r7
            r0.closeStatement(r7)
        L_0x0082:
            return r2
        L_0x0083:
            java.lang.AssertionError r0 = new java.lang.AssertionError
            r0.<init>()
            throw r0
        L_0x0089:
            if (r3 != 0) goto L_0x0091
            java.lang.AssertionError r0 = new java.lang.AssertionError
            r0.<init>()
            throw r0
        L_0x0091:
            com.application.mypet.utils.db.DBConnection r7 = r1.dbConnection
            r8 = r3
            com.mysql.jdbc.CallableStatement r8 = (com.mysql.jdbc.CallableStatement) r8
            r7.closeStatement(r8)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.application.mypet.services.profile.petsitter.data.ServicesInteractor.loadDB(java.lang.String, java.sql.Connection):com.application.mypet.registration.data.PetSitterServicesCredentials");
    }

    private boolean saveDB(String user, PetSitterServicesCredentials petSitterServicesCredentials, Connection connection) {
        boolean out = false;
        boolean serv1 = petSitterServicesCredentials.isServ1();
        boolean serv2 = petSitterServicesCredentials.isServ2();
        boolean serv3 = petSitterServicesCredentials.isServ3();
        boolean serv4 = petSitterServicesCredentials.isServ4();
        boolean serv5 = petSitterServicesCredentials.isServ5();
        String description = petSitterServicesCredentials.getDescription();
        CallableStatement stmt = null;
        try {
            stmt = connection.prepareCall("{ call save_pet_sit_services(?, ?, ?, ?, ?, ?, ?, ?) }");
            stmt.setString(1, user);
            stmt.setBoolean(2, serv1);
            stmt.setBoolean(3, serv2);
            stmt.setBoolean(4, serv3);
            stmt.setBoolean(5, serv4);
            stmt.setBoolean(6, serv5);
            stmt.setString(7, description);
            stmt.registerOutParameter(8, 16);
            stmt.execute();
            out = stmt.getBoolean(8);
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
