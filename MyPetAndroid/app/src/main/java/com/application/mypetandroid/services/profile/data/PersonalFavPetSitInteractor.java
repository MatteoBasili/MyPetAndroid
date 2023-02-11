package com.application.mypetandroid.services.profile.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;

import com.application.mypetandroid.services.profile.ProfileContract;
import com.application.mypetandroid.utils.db.DBProfile;
import com.application.mypetandroid.utils.exceptions.ConnectionFailedException;
import com.application.mypetandroid.utils.singleton_examples.PetSitterResultsSingletonClass;

import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonalFavPetSitInteractor {

    private static final Logger logger = Logger.getLogger(PersonalFavPetSitInteractor.class);
    private static final String SQL_EXCEPTION = "SQL Error: ";
    private static final String ERROR = "Something went wrong...";
    private final DBProfile dbProfile;
    private final ProfileContract.LoadFavPetSitListener loadFavPetSitListener;

    public PersonalFavPetSitInteractor(ProfileContract.LoadFavPetSitListener loadFavPetSitListener) {
        this.loadFavPetSitListener = loadFavPetSitListener;
        dbProfile = new DBProfile();
    }

    public void loadFavourites(String user) {
        new Handler().postDelayed(() -> load(user), 250);
    }

    private void load (String user) {

        try {
            Connection connection = dbProfile.getConnection();
            if (connection == null) {
                throw new ConnectionFailedException();
            }

            PetSitterResultsSingletonClass petSitterResultsSingletonClass = loadFavouritesFromDB(user, connection);
            this.dbProfile.closeConnection(connection);

            if (petSitterResultsSingletonClass == null) {
                this.loadFavPetSitListener.onLoadFavoritesFailed(ERROR);
            } else {
                this.loadFavPetSitListener.onLoadFavoritesSuccess(petSitterResultsSingletonClass);
            }
        }
        catch (ConnectionFailedException e) {
            this.loadFavPetSitListener.onLoadFavoritesFailed(e.getMessage());
        }

    }

    private PetSitterResultsSingletonClass loadFavouritesFromDB(String user, Connection connection) {
        Bitmap photo;
        int likes;
        int dislikes;
        List<PetSitProfileInfo> profiles = new ArrayList<>();
        List<String> petSitters = new ArrayList<>();
        List<String> regions = new ArrayList<>();
        List<String> provinces = new ArrayList<>();
        List<Boolean> favorites = new ArrayList<>();

        CallableStatement stmt = null;
        PetSitterResultsSingletonClass petSitterResultsSingletonClass = null;
        String query = "{ call load_pet_sit_favorites(?) }";
        try {
            // Preparing a CallableStatement to call a procedure
            stmt = connection.prepareCall(query);
            // Setting the values for the IN parameter
            stmt.setString(1, user);
            // Executing the CallableStatement
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                petSitters.add(rs.getString("Username"));
                regions.add(rs.getString("Region"));
                provinces.add(rs.getString("Province"));
                likes = rs.getInt("Likes");
                dislikes = rs.getInt("Dislikes");
                byte[] imageInByte = rs.getBytes("Photo");
                if (imageInByte != null) {
                    photo = BitmapFactory.decodeByteArray(imageInByte, 0, imageInByte.length);
                } else {
                    photo = null;
                }

                PetSitProfileInfo petSitProfileInfo = new PetSitProfileInfo();
                petSitProfileInfo.setImage(photo);
                petSitProfileInfo.setNumLikes(likes);
                petSitProfileInfo.setNumDislikes(dislikes);
                profiles.add(petSitProfileInfo);

                favorites.add(true);
            }

            petSitterResultsSingletonClass = PetSitterResultsSingletonClass.getSingletonInstance();
            petSitterResultsSingletonClass.setPetSitProfileInfo(profiles);
            petSitterResultsSingletonClass.setFavorites(favorites);
            petSitterResultsSingletonClass.setUsernames(petSitters);
            petSitterResultsSingletonClass.setProvinces(provinces);
            petSitterResultsSingletonClass.setRegions(regions);

        }
        catch (SQLException e) {
            logger.error(SQL_EXCEPTION, e);
        } finally {
            assert stmt != null;
            this.dbProfile.closeStatement(stmt);
        }

        return petSitterResultsSingletonClass;
    }

}
