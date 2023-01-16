package com.application.mypet.registration.data;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.core.util.PatternsCompat;
import com.application.mypet.exceptions.AlreadyUsedException;
import com.application.mypet.exceptions.ConnectionFailedException;
import com.application.mypet.exceptions.InvalidInputException;
import com.application.mypet.registration.RegistrationContract;
import com.application.mypet.utils.db.DBConnection;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class RegistrationInteractor {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final String SQL_EXCEPTION = "SQL Error: ";
    private DBConnection dbConnection;
    private final RegistrationContract.RegistrationListener registrationListener;

    public RegistrationInteractor(RegistrationContract.RegistrationListener registrationListener2) {
        this.registrationListener = registrationListener2;
    }

    public boolean isValidInput(RegistrationCredentials registrationCredentials) {
        return !hasInputError(registrationCredentials);
    }

    public void registerAccount(RegistrationCredentials registrationCredentials) {
        new Handler().postDelayed(new RegistrationInteractor$$ExternalSyntheticLambda0(this, registrationCredentials), 2500);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$registerAccount$0$com-application-mypet-registration-data-RegistrationInteractor  reason: not valid java name */
    public /* synthetic */ void m64lambda$registerAccount$0$comapplicationmypetregistrationdataRegistrationInteractor(RegistrationCredentials registrationCredentials) {
        DBConnection dBConnection = new DBConnection();
        this.dbConnection = dBConnection;
        Connection connection = dBConnection.getConnection();
        if (connection != null) {
            try {
                if (isUsernameUsed(registrationCredentials.getSystemCredentials().getUsername(), connection)) {
                    throw new AlreadyUsedException("username");
                } else if (!isEmailUsed(registrationCredentials.getSystemCredentials().getEmail(), connection)) {
                    boolean out = register(registrationCredentials, connection);
                    this.dbConnection.closeConnection(connection);
                    if (!out) {
                        this.registrationListener.onFailed("Something went wrong...");
                    } else {
                        this.registrationListener.onSuccess();
                    }
                } else {
                    throw new AlreadyUsedException(NotificationCompat.CATEGORY_EMAIL);
                }
            } catch (ConnectionFailedException e) {
                this.registrationListener.onFailed(e.getMessage());
            } catch (AlreadyUsedException e2) {
                this.registrationListener.onFailed(e2.getMessage());
                this.dbConnection.closeConnection(connection);
            }
        } else {
            throw new ConnectionFailedException();
        }
    }

    private boolean hasInputError(RegistrationCredentials registrationCredentials) {
        String name = registrationCredentials.getProfileCredentials().getName();
        String surname = registrationCredentials.getProfileCredentials().getSurname();
        String username = registrationCredentials.getSystemCredentials().getUsername();
        String email = registrationCredentials.getSystemCredentials().getEmail();
        String password = registrationCredentials.getSystemCredentials().getPassword();
        String passwordConfirm = registrationCredentials.getSystemCredentials().getPasswordConfirm();
        String region = registrationCredentials.getProfileCredentials().getRegion();
        String province = registrationCredentials.getProfileCredentials().getProvince();
        String phoneNumber = registrationCredentials.getProfileCredentials().getPhoneNumb();
        String firstPetName = registrationCredentials.getSystemCredentials().getFirstPetName();
        boolean petSitter = registrationCredentials.getSystemCredentials().isPetSitter();
        try {
            if (TextUtils.isEmpty(name)) {
                throw new InvalidInputException("The name is empty");
            } else if (TextUtils.isEmpty(surname)) {
                throw new InvalidInputException("The surname is empty");
            } else if (TextUtils.isEmpty(username)) {
                throw new InvalidInputException("The username is empty");
            } else if (TextUtils.isEmpty(email)) {
                throw new InvalidInputException("The email is empty");
            } else if (!PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()) {
                throw new InvalidInputException("The email is invalid");
            } else if (TextUtils.isEmpty(password)) {
                throw new InvalidInputException("The password is empty");
            } else if (password.length() < 8 || password.length() > 32) {
                throw new InvalidInputException("The password must be between 8 and 32 characters");
            } else if (TextUtils.isEmpty(passwordConfirm)) {
                throw new InvalidInputException("The password confirm is empty");
            } else if (!passwordConfirm.equals(password)) {
                throw new InvalidInputException("The password and its confirm must be equal");
            } else if (region.equals("Select your region")) {
                throw new InvalidInputException("Select a region");
            } else if (province.equals("Select your province")) {
                throw new InvalidInputException("Select a province");
            } else if (TextUtils.isEmpty(phoneNumber)) {
                throw new InvalidInputException("The phone number is empty");
            } else if (TextUtils.isEmpty(firstPetName)) {
                throw new InvalidInputException("The first pet name is empty");
            } else if (petSitter) {
                return hasPetSitterInputError(registrationCredentials);
            } else {
                return false;
            }
        } catch (InvalidInputException e) {
            this.registrationListener.onFailed(e.getMessage());
            return true;
        }
    }

    private boolean hasPetSitterInputError(RegistrationCredentials registrationCredentials) {
        boolean dog = registrationCredentials.getPetSitterCaredPets().isDog();
        boolean cat = registrationCredentials.getPetSitterCaredPets().isCat();
        boolean otherPets = registrationCredentials.getPetSitterCaredPets().isOtherPets();
        boolean service1 = registrationCredentials.getPetSitterServices().isServ1();
        boolean service2 = registrationCredentials.getPetSitterServices().isServ2();
        boolean service3 = registrationCredentials.getPetSitterServices().isServ3();
        boolean service4 = registrationCredentials.getPetSitterServices().isServ4();
        boolean service5 = registrationCredentials.getPetSitterServices().isServ5();
        if (!dog && !cat && !otherPets) {
            try {
                throw new InvalidInputException("You don't take care of any puppies!");
            } catch (InvalidInputException e) {
                this.registrationListener.onFailed(e.getMessage());
                return true;
            }
        } else if (service1 || service2 || service3 || service4) {
            return false;
        } else {
            if (service5) {
                return false;
            }
            throw new InvalidInputException("You don't offer any services!");
        }
    }

    private boolean isUsernameUsed(String username, Connection connection) {
        try {
            CallableStatement stmt = connection.prepareCall("{ call is_username_used(?, ?) }");
            stmt.setString(1, username);
            stmt.registerOutParameter(2, 16);
            stmt.execute();
            if (stmt.getBoolean(2)) {
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

    private boolean isEmailUsed(String email, Connection connection) {
        try {
            CallableStatement stmt = connection.prepareCall("{ call is_email_used_registration(?, ?) }");
            stmt.setString(1, email);
            stmt.registerOutParameter(2, 16);
            stmt.execute();
            if (stmt.getBoolean(2)) {
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

    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:87:0x025d  */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x0268  */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x0271  */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x0277  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean register(com.application.mypet.registration.data.RegistrationCredentials r28, java.sql.Connection r29) {
        /*
            r27 = this;
            r1 = r27
            r2 = 0
            com.application.mypet.registration.data.ProfileCredentials r0 = r28.getProfileCredentials()
            java.lang.String r3 = r0.getName()
            com.application.mypet.registration.data.ProfileCredentials r0 = r28.getProfileCredentials()
            java.lang.String r4 = r0.getSurname()
            com.application.mypet.registration.data.SystemCredentials r0 = r28.getSystemCredentials()
            java.lang.String r5 = r0.getUsername()
            com.application.mypet.registration.data.SystemCredentials r0 = r28.getSystemCredentials()
            java.lang.String r6 = r0.getEmail()
            com.application.mypet.registration.data.SystemCredentials r0 = r28.getSystemCredentials()
            java.lang.String r7 = r0.getPassword()
            com.application.mypet.registration.data.ProfileCredentials r0 = r28.getProfileCredentials()
            java.lang.String r8 = r0.getRegion()
            com.application.mypet.registration.data.ProfileCredentials r0 = r28.getProfileCredentials()
            java.lang.String r9 = r0.getProvince()
            com.application.mypet.registration.data.ProfileCredentials r0 = r28.getProfileCredentials()
            java.lang.String r0 = r0.getAddress()
            int r10 = r0.length()
            if (r10 != 0) goto L_0x004c
            r0 = 0
            r10 = r0
            goto L_0x004d
        L_0x004c:
            r10 = r0
        L_0x004d:
            com.application.mypet.registration.data.ProfileCredentials r0 = r28.getProfileCredentials()
            java.lang.String r11 = r0.getPhoneNumb()
            com.application.mypet.registration.data.SystemCredentials r0 = r28.getSystemCredentials()
            java.lang.String r12 = r0.getFirstPetName()
            com.application.mypet.registration.data.SystemCredentials r0 = r28.getSystemCredentials()
            boolean r13 = r0.isPetSitter()
            com.application.mypet.registration.data.PetSitterCaredPetsCredentials r0 = r28.getPetSitterCaredPets()
            boolean r14 = r0.isDog()
            com.application.mypet.registration.data.PetSitterCaredPetsCredentials r0 = r28.getPetSitterCaredPets()
            boolean r15 = r0.isCat()
            com.application.mypet.registration.data.PetSitterCaredPetsCredentials r0 = r28.getPetSitterCaredPets()
            r16 = r2
            boolean r2 = r0.isOtherPets()
            com.application.mypet.registration.data.PetSitterServicesCredentials r0 = r28.getPetSitterServices()
            boolean r1 = r0.isServ1()
            com.application.mypet.registration.data.PetSitterServicesCredentials r0 = r28.getPetSitterServices()
            r17 = r1
            boolean r1 = r0.isServ2()
            com.application.mypet.registration.data.PetSitterServicesCredentials r0 = r28.getPetSitterServices()
            r18 = r1
            boolean r1 = r0.isServ3()
            com.application.mypet.registration.data.PetSitterServicesCredentials r0 = r28.getPetSitterServices()
            r19 = r1
            boolean r1 = r0.isServ4()
            com.application.mypet.registration.data.PetSitterServicesCredentials r0 = r28.getPetSitterServices()
            r20 = r1
            boolean r1 = r0.isServ5()
            com.application.mypet.registration.data.PetSitterServicesCredentials r0 = r28.getPetSitterServices()
            java.lang.String r0 = r0.getDescription()
            int r21 = r0.length()
            if (r21 != 0) goto L_0x00c1
            r0 = 0
            r22 = r0
            goto L_0x00c3
        L_0x00c1:
            r22 = r0
        L_0x00c3:
            r21 = 0
            r23 = r1
            java.lang.String r1 = "{ call register_user(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }"
            r24 = r2
            r2 = r29
            java.sql.CallableStatement r0 = r2.prepareCall(r1)     // Catch:{ SQLException -> 0x0248, all -> 0x023e }
            r21 = r0
            r0 = 1
            r25 = r1
            r1 = r21
            r1.setString(r0, r3)     // Catch:{ SQLException -> 0x0234, all -> 0x022a }
            r0 = 2
            r1.setString(r0, r4)     // Catch:{ SQLException -> 0x0234, all -> 0x022a }
            r0 = 3
            r1.setString(r0, r5)     // Catch:{ SQLException -> 0x0234, all -> 0x022a }
            r0 = 4
            r1.setString(r0, r6)     // Catch:{ SQLException -> 0x0234, all -> 0x022a }
            r0 = 5
            r1.setString(r0, r7)     // Catch:{ SQLException -> 0x0234, all -> 0x022a }
            r0 = 6
            r1.setString(r0, r8)     // Catch:{ SQLException -> 0x0234, all -> 0x022a }
            r0 = 7
            r1.setString(r0, r9)     // Catch:{ SQLException -> 0x0234, all -> 0x022a }
            r0 = 8
            r1.setString(r0, r10)     // Catch:{ SQLException -> 0x0234, all -> 0x022a }
            r0 = 9
            r1.setString(r0, r11)     // Catch:{ SQLException -> 0x0234, all -> 0x022a }
            r0 = 10
            r1.setString(r0, r12)     // Catch:{ SQLException -> 0x0234, all -> 0x022a }
            r0 = 11
            r1.setBoolean(r0, r13)     // Catch:{ SQLException -> 0x0234, all -> 0x022a }
            r0 = 12
            r1.setBoolean(r0, r14)     // Catch:{ SQLException -> 0x0234, all -> 0x022a }
            r0 = 13
            r1.setBoolean(r0, r15)     // Catch:{ SQLException -> 0x0234, all -> 0x022a }
            r0 = 14
            r2 = r24
            r1.setBoolean(r0, r2)     // Catch:{ SQLException -> 0x021e, all -> 0x0212 }
            r0 = 15
            r24 = r2
            r2 = r17
            r1.setBoolean(r0, r2)     // Catch:{ SQLException -> 0x0206, all -> 0x01f9 }
            r0 = 16
            r17 = r2
            r2 = r18
            r1.setBoolean(r0, r2)     // Catch:{ SQLException -> 0x01ee, all -> 0x01e3 }
            r0 = 17
            r26 = r2
            r2 = r19
            r1.setBoolean(r0, r2)     // Catch:{ SQLException -> 0x01d8, all -> 0x01cd }
            r0 = 18
            r19 = r2
            r2 = r20
            r1.setBoolean(r0, r2)     // Catch:{ SQLException -> 0x01c2, all -> 0x01b7 }
            r0 = 19
            r20 = r2
            r2 = r23
            r1.setBoolean(r0, r2)     // Catch:{ SQLException -> 0x01ac, all -> 0x01a1 }
            r0 = 20
            r23 = r2
            r2 = r22
            r1.setString(r0, r2)     // Catch:{ SQLException -> 0x0196, all -> 0x018b }
            r0 = 21
            r22 = r2
            r2 = 16
            r1.registerOutParameter(r0, r2)     // Catch:{ SQLException -> 0x0182, all -> 0x0179 }
            r1.execute()     // Catch:{ SQLException -> 0x0182, all -> 0x0179 }
            boolean r0 = r1.getBoolean(r0)     // Catch:{ SQLException -> 0x0182, all -> 0x0179 }
            r2 = r0
            if (r1 == 0) goto L_0x0173
            r16 = r2
            r2 = r27
            com.application.mypet.utils.db.DBConnection r0 = r2.dbConnection
            r18 = r3
            r3 = r1
            com.mysql.jdbc.CallableStatement r3 = (com.mysql.jdbc.CallableStatement) r3
            r0.closeStatement(r3)
            r21 = r1
            goto L_0x0267
        L_0x0173:
            java.lang.AssertionError r0 = new java.lang.AssertionError
            r0.<init>()
            throw r0
        L_0x0179:
            r0 = move-exception
            r2 = r27
            r18 = r3
            r21 = r1
            goto L_0x026f
        L_0x0182:
            r0 = move-exception
            r2 = r27
            r18 = r3
            r21 = r1
            goto L_0x0251
        L_0x018b:
            r0 = move-exception
            r22 = r2
            r18 = r3
            r2 = r27
            r21 = r1
            goto L_0x026f
        L_0x0196:
            r0 = move-exception
            r22 = r2
            r18 = r3
            r2 = r27
            r21 = r1
            goto L_0x0251
        L_0x01a1:
            r0 = move-exception
            r23 = r2
            r18 = r3
            r2 = r27
            r21 = r1
            goto L_0x026f
        L_0x01ac:
            r0 = move-exception
            r23 = r2
            r18 = r3
            r2 = r27
            r21 = r1
            goto L_0x0251
        L_0x01b7:
            r0 = move-exception
            r20 = r2
            r18 = r3
            r2 = r27
            r21 = r1
            goto L_0x026f
        L_0x01c2:
            r0 = move-exception
            r20 = r2
            r18 = r3
            r2 = r27
            r21 = r1
            goto L_0x0251
        L_0x01cd:
            r0 = move-exception
            r19 = r2
            r18 = r3
            r2 = r27
            r21 = r1
            goto L_0x026f
        L_0x01d8:
            r0 = move-exception
            r19 = r2
            r18 = r3
            r2 = r27
            r21 = r1
            goto L_0x0251
        L_0x01e3:
            r0 = move-exception
            r26 = r2
            r18 = r3
            r2 = r27
            r21 = r1
            goto L_0x026f
        L_0x01ee:
            r0 = move-exception
            r26 = r2
            r18 = r3
            r2 = r27
            r21 = r1
            goto L_0x0251
        L_0x01f9:
            r0 = move-exception
            r17 = r2
            r26 = r18
            r2 = r27
            r18 = r3
            r21 = r1
            goto L_0x026f
        L_0x0206:
            r0 = move-exception
            r17 = r2
            r26 = r18
            r2 = r27
            r18 = r3
            r21 = r1
            goto L_0x0251
        L_0x0212:
            r0 = move-exception
            r24 = r2
            r26 = r18
            r2 = r27
            r18 = r3
            r21 = r1
            goto L_0x026f
        L_0x021e:
            r0 = move-exception
            r24 = r2
            r26 = r18
            r2 = r27
            r18 = r3
            r21 = r1
            goto L_0x0251
        L_0x022a:
            r0 = move-exception
            r2 = r27
            r26 = r18
            r18 = r3
            r21 = r1
            goto L_0x026f
        L_0x0234:
            r0 = move-exception
            r2 = r27
            r26 = r18
            r18 = r3
            r21 = r1
            goto L_0x0251
        L_0x023e:
            r0 = move-exception
            r2 = r27
            r25 = r1
            r26 = r18
            r18 = r3
            goto L_0x026f
        L_0x0248:
            r0 = move-exception
            r2 = r27
            r25 = r1
            r26 = r18
            r18 = r3
        L_0x0251:
            java.lang.String r1 = "SQL Error: "
            java.lang.String r3 = r0.getMessage()     // Catch:{ all -> 0x026e }
            android.util.Log.e(r1, r3)     // Catch:{ all -> 0x026e }
            if (r21 == 0) goto L_0x0268
            com.application.mypet.utils.db.DBConnection r0 = r2.dbConnection
            r1 = r21
            com.mysql.jdbc.CallableStatement r1 = (com.mysql.jdbc.CallableStatement) r1
            r0.closeStatement(r1)
        L_0x0267:
            return r16
        L_0x0268:
            java.lang.AssertionError r0 = new java.lang.AssertionError
            r0.<init>()
            throw r0
        L_0x026e:
            r0 = move-exception
        L_0x026f:
            if (r21 != 0) goto L_0x0277
            java.lang.AssertionError r0 = new java.lang.AssertionError
            r0.<init>()
            throw r0
        L_0x0277:
            com.application.mypet.utils.db.DBConnection r1 = r2.dbConnection
            r3 = r21
            com.mysql.jdbc.CallableStatement r3 = (com.mysql.jdbc.CallableStatement) r3
            r1.closeStatement(r3)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.application.mypet.registration.data.RegistrationInteractor.register(com.application.mypet.registration.data.RegistrationCredentials, java.sql.Connection):boolean");
    }
}
