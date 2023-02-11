package com.application.mypetandroid.services.profile.data;

import android.os.Handler;

import com.application.mypetandroid.registration.data.PetSitCaredPets;
import com.application.mypetandroid.services.profile.ProfileContract;
import com.application.mypetandroid.utils.db.DBProfile;
import com.application.mypetandroid.utils.exceptions.ConnectionFailedException;
import com.application.mypetandroid.utils.exceptions.InvalidInputException;

import org.apache.log4j.Logger;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

public class CaredPetsInteractor {

    private static final Logger logger = Logger.getLogger(CaredPetsInteractor.class);
    private static final String SQL_EXCEPTION = "SQL Error: ";
    private static final String ERROR = "Something went wrong...";
    private final DBProfile dbProfile;
    private final ProfileContract.CaredPetsListener caredPetsListener;

    public CaredPetsInteractor(ProfileContract.CaredPetsListener caredPetsListener) {
        this.caredPetsListener = caredPetsListener;
        this.dbProfile = new DBProfile();
    }

    public void loadCaredPets(String user) {
        new Handler().postDelayed(() -> load(user), 250);
    }

    public void saveCaredPets(String user, PetSitCaredPets petSitCaredPets) {
        if (hasInputError(petSitCaredPets)) {
            return;
        }

        new Handler().postDelayed(() -> save(user, petSitCaredPets), 500);
    }

    private void load(String user) {
        try {
            Connection connection = dbProfile.getConnection();
            if (connection == null) {
                throw new ConnectionFailedException();
            }

            PetSitCaredPets petSitCaredPets = loadPetsFromDB(user, connection);
            this.dbProfile.closeConnection(connection);

            if (petSitCaredPets == null) {
                this.caredPetsListener.onLoadPetsFailed("Error in loading informations.");
            }

            this.caredPetsListener.onLoadPetsSuccess(petSitCaredPets);
        }
        catch (ConnectionFailedException e) {
            this.caredPetsListener.onLoadPetsFailed(e.getMessage());
        }
    }

    private boolean hasInputError(PetSitCaredPets petSitCaredPets) {
        boolean dog = petSitCaredPets.isDog();
        boolean cat = petSitCaredPets.isCat();
        boolean otherPets = petSitCaredPets.isOtherPets();

        try {
            if (!dog && !cat && !otherPets) {
                throw new InvalidInputException("You must select at least one pet.");
            }

            return false;
        } catch (InvalidInputException e) {
            this.caredPetsListener.onStorePetsFailed(e.getMessage());
            return true;
        }
    }

    private void save(String user, PetSitCaredPets petSitCaredPets) {
        try {
            Connection connection = dbProfile.getConnection();
            if (connection == null) {
                throw new ConnectionFailedException();
            }

            Boolean out = savePetsToDB(user, petSitCaredPets, connection);
            this.dbProfile.closeConnection(connection);

            if (out == null) {
                this.caredPetsListener.onStorePetsFailed(ERROR);
            } else if(!out) {
                this.caredPetsListener.onStorePetsFailed("Info saving failed. Try again.");
            } else {
                this.caredPetsListener.onStorePetsSuccess();
            }
        }
        catch (ConnectionFailedException e) {
            this.caredPetsListener.onStorePetsFailed(e.getMessage());
        }
    }

    private PetSitCaredPets loadPetsFromDB(String user, Connection connection) {
        PetSitCaredPets petSitCaredPets = null;
        CallableStatement stmt = null;
        String query = "{ call recover_pet_sit_cared_pets(?, ?, ?, ?) }";
        try {
            // Preparing a CallableStatement to call a procedure
            stmt = connection.prepareCall(query);
            // Setting the value for the IN parameter
            stmt.setString(1, user);
            // Registering the type of the OUT parameters
            stmt.registerOutParameter(2, Types.BOOLEAN);
            stmt.registerOutParameter(3, Types.BOOLEAN);
            stmt.registerOutParameter(4, Types.BOOLEAN);
            // Executing the CallableStatement
            stmt.execute();

            // Retrieving the OUT parameters
            petSitCaredPets = new PetSitCaredPets();
            petSitCaredPets.setDog(stmt.getBoolean(2));
            petSitCaredPets.setCat(stmt.getBoolean(3));
            petSitCaredPets.setOtherPets(stmt.getBoolean(4));

        }
        catch (SQLException e) {
            logger.error(SQL_EXCEPTION, e);
        } finally {
            assert stmt != null;
            this.dbProfile.closeStatement(stmt);
        }

        return petSitCaredPets;
    }

    private Boolean savePetsToDB(String user, PetSitCaredPets petSitCaredPets, Connection connection) {
        boolean dog = petSitCaredPets.isDog();
        boolean cat = petSitCaredPets.isCat();
        boolean other = petSitCaredPets.isOtherPets();

        Boolean out = null;
        CallableStatement stmt = null;
        String query = "{ call save_pet_sit_cared_pets(?, ?, ?, ?, ?) }";
        try {
            // Preparing a CallableStatement to call a procedure
            stmt = connection.prepareCall(query);
            // Setting the value for the IN parameters
            stmt.setString(1, user);
            stmt.setBoolean(2, dog);
            stmt.setBoolean(3, cat);
            stmt.setBoolean(4, other);
            // Registering the type of the OUT parameter
            stmt.registerOutParameter(5, Types.INTEGER);
            // Executing the CallableStatement
            stmt.execute();

            // Retrieving the OUT parameter
            out = stmt.getBoolean(5);
        }
        catch (SQLException e) {
            logger.error(SQL_EXCEPTION, e);
        } finally {
            assert stmt != null;
            this.dbProfile.closeStatement(stmt);
        }

        return out;
    }

}
