package com.application.mypet.services.profile.petsitter.data;

import android.os.Handler;
import android.util.Log;
import com.application.mypet.exceptions.ConnectionFailedException;
import com.application.mypet.exceptions.InvalidInputException;
import com.application.mypet.registration.data.PetSitterCaredPetsCredentials;
import com.application.mypet.services.profile.ProfileContract;
import com.application.mypet.utils.db.DBConnection;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class CaredPetsInteractor {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final String SQL_EXCEPTION = "SQL Error: ";
    private final ProfileContract.CaredPetsListener caredPetsListener;
    private DBConnection dbConnection;

    public CaredPetsInteractor(ProfileContract.CaredPetsListener caredPetsListener2) {
        this.caredPetsListener = caredPetsListener2;
    }

    public boolean isValidInput(PetSitterCaredPetsCredentials petSitterCaredPetsCredentials) {
        return !hasInputError(petSitterCaredPetsCredentials);
    }

    public void saveCaredPets(String user, PetSitterCaredPetsCredentials petSitterCaredPetsCredentials) {
        new Handler().postDelayed(new CaredPetsInteractor$$ExternalSyntheticLambda1(this, user, petSitterCaredPetsCredentials), 10);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$saveCaredPets$0$com-application-mypet-services-profile-petsitter-data-CaredPetsInteractor  reason: not valid java name */
    public /* synthetic */ void m1lambda$saveCaredPets$0$comapplicationmypetservicesprofilepetsitterdataCaredPetsInteractor(String user, PetSitterCaredPetsCredentials petSitterCaredPetsCredentials) {
        DBConnection dBConnection = new DBConnection();
        this.dbConnection = dBConnection;
        Connection connection = dBConnection.getConnection();
        if (connection != null) {
            boolean out = saveDB(user, petSitterCaredPetsCredentials, connection);
            this.dbConnection.closeConnection(connection);
            if (!out) {
                this.caredPetsListener.onStoreFailed("Something went wrong...");
            } else {
                this.caredPetsListener.onStoreSuccess();
            }
        } else {
            try {
                throw new ConnectionFailedException();
            } catch (ConnectionFailedException e) {
                this.caredPetsListener.onStoreFailed(e.getMessage());
            }
        }
    }

    public void loadCaredPets(String user) {
        new Handler().postDelayed(new CaredPetsInteractor$$ExternalSyntheticLambda0(this, user), 10);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$loadCaredPets$1$com-application-mypet-services-profile-petsitter-data-CaredPetsInteractor  reason: not valid java name */
    public /* synthetic */ void m0lambda$loadCaredPets$1$comapplicationmypetservicesprofilepetsitterdataCaredPetsInteractor(String user) {
        DBConnection dBConnection = new DBConnection("100");
        this.dbConnection = dBConnection;
        Connection connection = dBConnection.getConnection();
        if (connection != null) {
            PetSitterCaredPetsCredentials petSitterCaredPetsCredentials = loadDB(user, connection);
            this.dbConnection.closeConnection(connection);
            this.caredPetsListener.onLoadCaredPetsSuccess(petSitterCaredPetsCredentials);
            return;
        }
        try {
            throw new ConnectionFailedException();
        } catch (ConnectionFailedException e) {
            this.caredPetsListener.onLoadFailed(e.getMessage());
        }
    }

    private boolean hasInputError(PetSitterCaredPetsCredentials petSitterCaredPetsCredentials) {
        boolean cared1 = petSitterCaredPetsCredentials.isDog();
        boolean cared2 = petSitterCaredPetsCredentials.isCat();
        boolean cared3 = petSitterCaredPetsCredentials.isOtherPets();
        if (cared1 || cared2 || cared3) {
            return false;
        }
        try {
            throw new InvalidInputException("You don't take care of any puppies!");
        } catch (InvalidInputException e) {
            this.caredPetsListener.onStoreFailed(e.getMessage());
            return true;
        }
    }

    private PetSitterCaredPetsCredentials loadDB(String user, Connection connection) {
        PetSitterCaredPetsCredentials petSitterCaredPetsCredentials = null;
        CallableStatement stmt = null;
        try {
            stmt = connection.prepareCall("{ call recover_pet_sit_cared_pets(?, ?, ?, ?) }");
            stmt.setString(1, user);
            stmt.registerOutParameter(2, 16);
            stmt.registerOutParameter(3, 16);
            stmt.registerOutParameter(4, 16);
            stmt.execute();
            petSitterCaredPetsCredentials = new PetSitterCaredPetsCredentials(stmt.getBoolean(2), stmt.getBoolean(3), stmt.getBoolean(4));
            if (stmt != null) {
                this.dbConnection.closeStatement((com.mysql.jdbc.CallableStatement) stmt);
                return petSitterCaredPetsCredentials;
            }
            throw new AssertionError();
        } catch (SQLException e) {
            Log.e(SQL_EXCEPTION, e.getMessage());
            if (stmt != null) {
                this.dbConnection.closeStatement((com.mysql.jdbc.CallableStatement) stmt);
            } else {
                throw new AssertionError();
            }
        } catch (Throwable th) {
            if (stmt == null) {
                throw new AssertionError();
            }
            this.dbConnection.closeStatement((com.mysql.jdbc.CallableStatement) stmt);
            throw th;
        }
    }

    private boolean saveDB(String user, PetSitterCaredPetsCredentials petSitterCaredPetsCredentials, Connection connection) {
        boolean out = false;
        boolean cared1 = petSitterCaredPetsCredentials.isDog();
        boolean cared2 = petSitterCaredPetsCredentials.isCat();
        boolean cared3 = petSitterCaredPetsCredentials.isOtherPets();
        com.mysql.jdbc.CallableStatement stmt = null;
        try {
            stmt = connection.prepareCall("{ call save_pet_sit_cared_pets(?, ?, ?, ?, ?) }");
            stmt.setString(1, user);
            stmt.setBoolean(2, cared1);
            stmt.setBoolean(3, cared2);
            stmt.setBoolean(4, cared3);
            stmt.registerOutParameter(5, 16);
            stmt.execute();
            out = stmt.getBoolean(5);
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
            this.dbConnection.closeStatement((com.mysql.jdbc.CallableStatement) null);
            throw th;
        }
        this.dbConnection.closeStatement(stmt);
        return out;
    }
}
