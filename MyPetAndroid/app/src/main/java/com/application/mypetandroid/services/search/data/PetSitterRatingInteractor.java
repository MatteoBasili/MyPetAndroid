package com.application.mypetandroid.services.search.data;

import android.os.Handler;

import com.application.mypetandroid.services.search.PetSitterSearchContract;
import com.application.mypetandroid.utils.db.DBProfile;
import com.application.mypetandroid.utils.exceptions.ConnectionFailedException;

import org.apache.log4j.Logger;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

public class PetSitterRatingInteractor {

    private static final Logger logger = Logger.getLogger(PetSitterRatingInteractor.class);
    private static final String SQL_EXCEPTION = "SQL Error: ";
    private static final String ERROR = "Something went wrong...";
    private final DBProfile dbProfile;
    private final PetSitterSearchContract.PetSitterRatingListener petSitterRatingListener;

    public PetSitterRatingInteractor(PetSitterSearchContract.PetSitterRatingListener petSitterRatingListener) {
        this.petSitterRatingListener = petSitterRatingListener;
        this.dbProfile = new DBProfile();
    }

    public void ratePetSitter(String user, String petSitter, int vote) {
        new Handler().postDelayed(() -> rate(user, petSitter, vote), 0);
    }

    private void rate(String user, String petSitter, int vote) {
        try {
            Connection connection = dbProfile.getConnection();
            if (connection == null) {
                throw new ConnectionFailedException();
            }

            Boolean out = setRatingToDB(user, petSitter, vote, connection);
            this.dbProfile.closeConnection(connection);

            if (out == null) {
                this.petSitterRatingListener.onRateFailed(ERROR);
            }

            this.petSitterRatingListener.onRateSuccess();
        }
        catch (ConnectionFailedException e) {
            this.petSitterRatingListener.onRateFailed(e.getMessage());
        }
    }

    private Boolean setRatingToDB(String user, String petSitter, int vote, Connection connection) {
        Boolean out = null;
        CallableStatement stmt = null;
        String query = "{ call rate_pet_sitter(?, ?, ?, ?) }";
        try {
            // Preparing a CallableStatement to call a procedure
            stmt = connection.prepareCall(query);
            // Setting the value for the IN parameters
            stmt.setString(1, user);
            stmt.setString(2, petSitter);
            stmt.setInt(3, vote);
            // Registering the type of the OUT parameter
            stmt.registerOutParameter(4, Types.BOOLEAN);
            // Executing the CallableStatement
            stmt.execute();

            // Retrieving the OUT parameter
            out = stmt.getBoolean(4);

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
