package com.application.mypet.services.profile.data;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.core.util.PatternsCompat;
import com.application.mypet.exceptions.AlreadyUsedException;
import com.application.mypet.exceptions.ConnectionFailedException;
import com.application.mypet.exceptions.InvalidInputException;
import com.application.mypet.services.profile.ProfileContract;
import com.application.mypet.utils.db.DBConnection;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class PersonalInfoInteractor {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final String SQL_EXCEPTION = "SQL Error: ";
    private DBConnection dbConnection;
    private final ProfileContract.PersonalInfoListener personalInfoListener;

    public PersonalInfoInteractor(ProfileContract.PersonalInfoListener personalInfoListener2) {
        this.personalInfoListener = personalInfoListener2;
    }

    public boolean isValidInput(PersonalInformations personalInformations) {
        return !hasInputError(personalInformations);
    }

    public void saveInfo(String user, PersonalInformations personalInformations) {
        new Handler().postDelayed(new PersonalInfoInteractor$$ExternalSyntheticLambda0(this, user, personalInformations), 10);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$saveInfo$0$com-application-mypet-services-profile-data-PersonalInfoInteractor  reason: not valid java name */
    public /* synthetic */ void m46lambda$saveInfo$0$comapplicationmypetservicesprofiledataPersonalInfoInteractor(String user, PersonalInformations personalInformations) {
        DBConnection dBConnection = new DBConnection();
        this.dbConnection = dBConnection;
        Connection connection = dBConnection.getConnection();
        if (connection != null) {
            try {
                if (!isEmailUsed(user, personalInformations.getEmail(), connection)) {
                    boolean out = saveDB(user, personalInformations, connection);
                    this.dbConnection.closeConnection(connection);
                    if (!out) {
                        this.personalInfoListener.onStoreFailed("Something went wrong...");
                    } else {
                        this.personalInfoListener.onStoreSuccess();
                    }
                } else {
                    throw new AlreadyUsedException(NotificationCompat.CATEGORY_EMAIL);
                }
            } catch (ConnectionFailedException e) {
                this.personalInfoListener.onStoreFailed(e.getMessage());
            } catch (AlreadyUsedException e2) {
                this.personalInfoListener.onStoreFailed(e2.getMessage());
                this.dbConnection.closeConnection(connection);
            }
        } else {
            throw new ConnectionFailedException();
        }
    }

    public void loadInfo(String user) {
        new Handler().postDelayed(new PersonalInfoInteractor$$ExternalSyntheticLambda1(this, user), 10);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$loadInfo$1$com-application-mypet-services-profile-data-PersonalInfoInteractor  reason: not valid java name */
    public /* synthetic */ void m45lambda$loadInfo$1$comapplicationmypetservicesprofiledataPersonalInfoInteractor(String user) {
        DBConnection dBConnection = new DBConnection("100");
        this.dbConnection = dBConnection;
        Connection connection = dBConnection.getConnection();
        if (connection != null) {
            PersonalInformations personalInformations = loadDB(user, connection);
            this.dbConnection.closeConnection(connection);
            this.personalInfoListener.onLoadPersonalInfoSuccess(personalInformations);
            return;
        }
        try {
            throw new ConnectionFailedException();
        } catch (ConnectionFailedException e) {
            this.personalInfoListener.onLoadFailed(e.getMessage());
        }
    }

    private boolean hasInputError(PersonalInformations personalInformations) {
        String name = personalInformations.getProfileCredentials().getName();
        String surname = personalInformations.getProfileCredentials().getSurname();
        String region = personalInformations.getProfileCredentials().getRegion();
        String province = personalInformations.getProfileCredentials().getProvince();
        String email = personalInformations.getEmail();
        String phoneNumber = personalInformations.getProfileCredentials().getPhoneNumb();
        String firstPetName = personalInformations.getFirstPetName();
        try {
            if (TextUtils.isEmpty(name)) {
                throw new InvalidInputException("The name is empty");
            } else if (TextUtils.isEmpty(surname)) {
                throw new InvalidInputException("The surname is empty");
            } else if (region.equals("Select your region")) {
                throw new InvalidInputException("Select a region");
            } else if (province.equals("Select your province")) {
                throw new InvalidInputException("Select a province");
            } else if (TextUtils.isEmpty(email)) {
                throw new InvalidInputException("The email is empty");
            } else if (!PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()) {
                throw new InvalidInputException("The email is invalid");
            } else if (TextUtils.isEmpty(phoneNumber)) {
                throw new InvalidInputException("The phone number is empty");
            } else if (!TextUtils.isEmpty(firstPetName)) {
                return false;
            } else {
                throw new InvalidInputException("The first pet name is empty");
            }
        } catch (InvalidInputException e) {
            this.personalInfoListener.onStoreFailed(e.getMessage());
            return true;
        }
    }

    private boolean isEmailUsed(String user, String email, Connection connection) {
        try {
            CallableStatement stmt = connection.prepareCall("{ call is_email_used_change(?, ?, ?) }");
            stmt.setString(1, user);
            stmt.setString(2, email);
            stmt.registerOutParameter(3, 16);
            stmt.execute();
            if (stmt.getBoolean(3)) {
                this.dbConnection.closeConnection(connection);
                if (stmt != null) {
                    this.dbConnection.closeStatement((com.mysql.jdbc.CallableStatement) stmt);
                    return true;
                }
                throw new AssertionError();
            } else if (stmt != null) {
                this.dbConnection.closeStatement((com.mysql.jdbc.CallableStatement) stmt);
                return false;
            } else {
                throw new AssertionError();
            }
        } catch (SQLException e) {
            Log.e(SQL_EXCEPTION, e.getMessage());
            if (0 != 0) {
                this.dbConnection.closeStatement((com.mysql.jdbc.CallableStatement) null);
                return false;
            }
            throw new AssertionError();
        } catch (Throwable th) {
            if (0 == 0) {
                throw new AssertionError();
            }
            this.dbConnection.closeStatement((com.mysql.jdbc.CallableStatement) null);
            throw th;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:21:0x008e  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0098  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.application.mypet.services.profile.data.PersonalInformations loadDB(java.lang.String r23, java.sql.Connection r24) {
        /*
            r22 = this;
            r1 = r22
            r2 = 0
            r3 = 0
            java.lang.String r4 = "{ call recover_user_info(?, ?, ?, ?, ?, ?, ?, ?, ?) }"
            r5 = r24
            java.sql.CallableStatement r0 = r5.prepareCall(r4)     // Catch:{ SQLException -> 0x007f, all -> 0x007b }
            r3 = r0
            r0 = 1
            r6 = r23
            r3.setString(r0, r6)     // Catch:{ SQLException -> 0x0079 }
            r0 = 2
            r7 = 12
            r3.registerOutParameter(r0, r7)     // Catch:{ SQLException -> 0x0079 }
            r8 = 3
            r3.registerOutParameter(r8, r7)     // Catch:{ SQLException -> 0x0079 }
            r9 = 4
            r3.registerOutParameter(r9, r7)     // Catch:{ SQLException -> 0x0079 }
            r10 = 5
            r3.registerOutParameter(r10, r7)     // Catch:{ SQLException -> 0x0079 }
            r11 = 6
            r3.registerOutParameter(r11, r7)     // Catch:{ SQLException -> 0x0079 }
            r12 = 7
            r3.registerOutParameter(r12, r7)     // Catch:{ SQLException -> 0x0079 }
            r13 = 8
            r3.registerOutParameter(r13, r7)     // Catch:{ SQLException -> 0x0079 }
            r14 = 9
            r3.registerOutParameter(r14, r7)     // Catch:{ SQLException -> 0x0079 }
            r3.execute()     // Catch:{ SQLException -> 0x0079 }
            java.lang.String r16 = r3.getString(r0)     // Catch:{ SQLException -> 0x0079 }
            java.lang.String r17 = r3.getString(r8)     // Catch:{ SQLException -> 0x0079 }
            java.lang.String r0 = r3.getString(r9)     // Catch:{ SQLException -> 0x0079 }
            java.lang.String r18 = r3.getString(r10)     // Catch:{ SQLException -> 0x0079 }
            java.lang.String r19 = r3.getString(r11)     // Catch:{ SQLException -> 0x0079 }
            java.lang.String r20 = r3.getString(r12)     // Catch:{ SQLException -> 0x0079 }
            java.lang.String r21 = r3.getString(r13)     // Catch:{ SQLException -> 0x0079 }
            java.lang.String r7 = r3.getString(r14)     // Catch:{ SQLException -> 0x0079 }
            com.application.mypet.registration.data.ProfileCredentials r8 = new com.application.mypet.registration.data.ProfileCredentials     // Catch:{ SQLException -> 0x0079 }
            r15 = r8
            r15.<init>(r16, r17, r18, r19, r20, r21)     // Catch:{ SQLException -> 0x0079 }
            com.application.mypet.services.profile.data.PersonalInformations r9 = new com.application.mypet.services.profile.data.PersonalInformations     // Catch:{ SQLException -> 0x0079 }
            r9.<init>(r8, r0, r7)     // Catch:{ SQLException -> 0x0079 }
            r2 = r9
            if (r3 == 0) goto L_0x0071
            com.application.mypet.utils.db.DBConnection r9 = r1.dbConnection
            r10 = r3
            com.mysql.jdbc.CallableStatement r10 = (com.mysql.jdbc.CallableStatement) r10
            r9.closeStatement(r10)
            goto L_0x0097
        L_0x0071:
            java.lang.AssertionError r9 = new java.lang.AssertionError
            r9.<init>()
            throw r9
        L_0x0077:
            r0 = move-exception
            goto L_0x009e
        L_0x0079:
            r0 = move-exception
            goto L_0x0082
        L_0x007b:
            r0 = move-exception
            r6 = r23
            goto L_0x009e
        L_0x007f:
            r0 = move-exception
            r6 = r23
        L_0x0082:
            java.lang.String r7 = "SQL Error: "
            java.lang.String r8 = r0.getMessage()     // Catch:{ all -> 0x0077 }
            android.util.Log.e(r7, r8)     // Catch:{ all -> 0x0077 }
            if (r3 == 0) goto L_0x0098
            com.application.mypet.utils.db.DBConnection r0 = r1.dbConnection
            r7 = r3
            com.mysql.jdbc.CallableStatement r7 = (com.mysql.jdbc.CallableStatement) r7
            r0.closeStatement(r7)
        L_0x0097:
            return r2
        L_0x0098:
            java.lang.AssertionError r0 = new java.lang.AssertionError
            r0.<init>()
            throw r0
        L_0x009e:
            if (r3 != 0) goto L_0x00a6
            java.lang.AssertionError r0 = new java.lang.AssertionError
            r0.<init>()
            throw r0
        L_0x00a6:
            com.application.mypet.utils.db.DBConnection r7 = r1.dbConnection
            r8 = r3
            com.mysql.jdbc.CallableStatement r8 = (com.mysql.jdbc.CallableStatement) r8
            r7.closeStatement(r8)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.application.mypet.services.profile.data.PersonalInfoInteractor.loadDB(java.lang.String, java.sql.Connection):com.application.mypet.services.profile.data.PersonalInformations");
    }

    /* JADX WARNING: Removed duplicated region for block: B:22:0x00a8  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x00b3  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x00c0  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x00c6  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean saveDB(java.lang.String r18, com.application.mypet.services.profile.data.PersonalInformations r19, java.sql.Connection r20) {
        /*
            r17 = this;
            r1 = r17
            r2 = 0
            com.application.mypet.registration.data.ProfileCredentials r0 = r19.getProfileCredentials()
            java.lang.String r3 = r0.getName()
            com.application.mypet.registration.data.ProfileCredentials r0 = r19.getProfileCredentials()
            java.lang.String r4 = r0.getSurname()
            com.application.mypet.registration.data.ProfileCredentials r0 = r19.getProfileCredentials()
            java.lang.String r5 = r0.getRegion()
            com.application.mypet.registration.data.ProfileCredentials r0 = r19.getProfileCredentials()
            java.lang.String r6 = r0.getProvince()
            com.application.mypet.registration.data.ProfileCredentials r0 = r19.getProfileCredentials()
            java.lang.String r7 = r0.getAddress()
            java.lang.String r8 = r19.getEmail()
            com.application.mypet.registration.data.ProfileCredentials r0 = r19.getProfileCredentials()
            java.lang.String r9 = r0.getPhoneNumb()
            java.lang.String r10 = r19.getFirstPetName()
            r11 = 0
            java.lang.String r12 = "{ call save_user_info(?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }"
            r13 = r20
            java.sql.CallableStatement r0 = r13.prepareCall(r12)     // Catch:{ SQLException -> 0x0097, all -> 0x0091 }
            r11 = r0
            r0 = 1
            r14 = r18
            r11.setString(r0, r14)     // Catch:{ SQLException -> 0x008f, all -> 0x008d }
            r0 = 2
            r11.setString(r0, r3)     // Catch:{ SQLException -> 0x008f, all -> 0x008d }
            r0 = 3
            r11.setString(r0, r4)     // Catch:{ SQLException -> 0x008f, all -> 0x008d }
            r0 = 4
            r11.setString(r0, r8)     // Catch:{ SQLException -> 0x008f, all -> 0x008d }
            r0 = 5
            r11.setString(r0, r5)     // Catch:{ SQLException -> 0x008f, all -> 0x008d }
            r0 = 6
            r11.setString(r0, r6)     // Catch:{ SQLException -> 0x008f, all -> 0x008d }
            r0 = 7
            r11.setString(r0, r7)     // Catch:{ SQLException -> 0x008f, all -> 0x008d }
            r0 = 8
            r11.setString(r0, r9)     // Catch:{ SQLException -> 0x008f, all -> 0x008d }
            r0 = 9
            r11.setString(r0, r10)     // Catch:{ SQLException -> 0x008f, all -> 0x008d }
            r0 = 16
            r15 = 10
            r11.registerOutParameter(r15, r0)     // Catch:{ SQLException -> 0x008f, all -> 0x008d }
            r11.execute()     // Catch:{ SQLException -> 0x008f, all -> 0x008d }
            boolean r0 = r11.getBoolean(r15)     // Catch:{ SQLException -> 0x008f, all -> 0x008d }
            r2 = r0
            if (r11 == 0) goto L_0x0087
            com.application.mypet.utils.db.DBConnection r0 = r1.dbConnection
            r15 = r11
            com.mysql.jdbc.CallableStatement r15 = (com.mysql.jdbc.CallableStatement) r15
            r0.closeStatement(r15)
            goto L_0x00b2
        L_0x0087:
            java.lang.AssertionError r0 = new java.lang.AssertionError
            r0.<init>()
            throw r0
        L_0x008d:
            r0 = move-exception
            goto L_0x0094
        L_0x008f:
            r0 = move-exception
            goto L_0x009a
        L_0x0091:
            r0 = move-exception
            r14 = r18
        L_0x0094:
            r16 = r2
            goto L_0x00be
        L_0x0097:
            r0 = move-exception
            r14 = r18
        L_0x009a:
            java.lang.String r15 = "SQL Error: "
            r16 = r2
            java.lang.String r2 = r0.getMessage()     // Catch:{ all -> 0x00b9 }
            android.util.Log.e(r15, r2)     // Catch:{ all -> 0x00b9 }
            if (r11 == 0) goto L_0x00b3
            com.application.mypet.utils.db.DBConnection r0 = r1.dbConnection
            r2 = r11
            com.mysql.jdbc.CallableStatement r2 = (com.mysql.jdbc.CallableStatement) r2
            r0.closeStatement(r2)
            r2 = r16
        L_0x00b2:
            return r2
        L_0x00b3:
            java.lang.AssertionError r0 = new java.lang.AssertionError
            r0.<init>()
            throw r0
        L_0x00b9:
            r0 = move-exception
            goto L_0x00be
        L_0x00bb:
            r0 = move-exception
            r16 = r2
        L_0x00be:
            if (r11 != 0) goto L_0x00c6
            java.lang.AssertionError r0 = new java.lang.AssertionError
            r0.<init>()
            throw r0
        L_0x00c6:
            com.application.mypet.utils.db.DBConnection r2 = r1.dbConnection
            r15 = r11
            com.mysql.jdbc.CallableStatement r15 = (com.mysql.jdbc.CallableStatement) r15
            r2.closeStatement(r15)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.application.mypet.services.profile.data.PersonalInfoInteractor.saveDB(java.lang.String, com.application.mypet.services.profile.data.PersonalInformations, java.sql.Connection):boolean");
    }
}
