package com.application.mypetandroid.services.profile.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;

import com.application.mypetandroid.services.profile.ProfileContract;
import com.application.mypetandroid.utils.db.DBProfile;
import com.application.mypetandroid.utils.exceptions.ConnectionFailedException;

import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.*;

public class PetSitProfileInteractor {

    private static final Logger logger = Logger.getLogger(PetSitProfileInteractor.class);
    private static final String SQL_EXCEPTION = "SQL Error: ";
    private static final String ERROR = "Something went wrong...";
    private final DBProfile dbProfile;
    private final ProfileContract.PetSitProfileListener petSitProfileListener;

    public PetSitProfileInteractor(ProfileContract.PetSitProfileListener petSitProfileListener) {
        this.petSitProfileListener = petSitProfileListener;
        dbProfile = new DBProfile();
    }

    public void loadProfile(String user) {
        new Handler().postDelayed(() -> load(user), 500);
    }

    public void savePhoto(String user, Bitmap image) {
        new Handler().postDelayed(() -> save(user, image), 500);
    }

    private void load(String user) {
        try {
            Connection connection = dbProfile.getConnection();
            if (connection == null) {
                throw new ConnectionFailedException();
            }

            PetSitProfileInfo petSitProfileInfo = loadFromDB(user, connection);
            this.dbProfile.closeConnection(connection);

            if (petSitProfileInfo == null) {
                this.petSitProfileListener.onLoadProfileFailed("Error in loading profile.");
            }

            this.petSitProfileListener.onLoadProfileSuccess(petSitProfileInfo);
        }
        catch (ConnectionFailedException e) {
            this.petSitProfileListener.onLoadProfileFailed(e.getMessage());
        }
    }

    private void save(String user, Bitmap image) {
        try {
            Connection connection = dbProfile.getConnection();
            if (connection == null) {
                throw new ConnectionFailedException();
            }

            Boolean out = saveToDB(user, image, connection);
            this.dbProfile.closeConnection(connection);

            if (out == null) {
                this.petSitProfileListener.onStorePhotoFailed(ERROR);
            } else if(!out) {
                this.petSitProfileListener.onStorePhotoFailed("Photo saving failed. Try again.");
            } else {
                this.petSitProfileListener.onStorePhotoSuccess();
            }
        }
        catch (ConnectionFailedException e) {
            this.petSitProfileListener.onStorePhotoFailed(e.getMessage());
        } catch (IOException e) {
            logger.error("IO Error: ", e);
        }
    }

    private PetSitProfileInfo loadFromDB(String user, Connection connection) {
        PetSitProfileInfo petSitProfileInfo = null;
        Bitmap image = null;
        CallableStatement stmt = null;
        String query = "{ call load_profile(?, ?, ?, ?) }";
        try {
            // Preparing a CallableStatement to call a procedure
            stmt = connection.prepareCall(query);
            // Setting the value for the IN parameter
            stmt.setString(1, user);
            // Registering the type of the OUT parameters
            stmt.registerOutParameter(2, Types.BLOB);
            stmt.registerOutParameter(3, Types.INTEGER);
            stmt.registerOutParameter(4, Types.INTEGER);
            // Executing the CallableStatement
            stmt.execute();

            // Retrieving the OUT parameters
            petSitProfileInfo = new PetSitProfileInfo();

            byte[] imageInByte = stmt.getBytes(2);
            if (imageInByte != null) {
                image = BitmapFactory.decodeByteArray(imageInByte, 0, imageInByte.length);
            }
            petSitProfileInfo.setImage(image);
            petSitProfileInfo.setNumLikes(stmt.getInt(3));
            petSitProfileInfo.setNumDislikes(stmt.getInt(4));
        }
        catch (SQLException e) {
            logger.error(SQL_EXCEPTION, e);
        } finally {
            assert stmt != null;
            this.dbProfile.closeStatement(stmt);
        }

        return petSitProfileInfo;
    }

    private Boolean saveToDB(String user, Bitmap image, Connection connection) throws IOException {
        Boolean out = null;
        byte[] imageInByte = null;
        if (image != null) {
            ByteArrayOutputStream objectByteArrayOutputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, objectByteArrayOutputStream);
            imageInByte = objectByteArrayOutputStream.toByteArray();
        }
        CallableStatement stmt = null;
        String query = "{ call save_profile_photo(?, ?, ?) }";
        try {
            // Preparing a CallableStatement to call a procedure
            stmt = connection.prepareCall(query);
            // Setting the value for the IN parameters
            stmt.setString(1, user);
            stmt.setBytes(2, imageInByte);
            // Registering the type of the OUT parameter
            stmt.registerOutParameter(3, Types.INTEGER);
            // Executing the CallableStatement
            stmt.execute();

            // Retrieving the OUT parameter
            out = stmt.getBoolean(3);
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
