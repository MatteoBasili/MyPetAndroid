package com.application.mypetandroid.services.search.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;

import com.application.mypetandroid.services.profile.data.PetSitProfileInfo;
import com.application.mypetandroid.services.search.PetSitterSearchContract;
import com.application.mypetandroid.utils.db.DBProfile;
import com.application.mypetandroid.utils.exceptions.ConnectionFailedException;
import com.application.mypetandroid.utils.exceptions.InvalidInputException;
import com.application.mypetandroid.utils.input.InputValidator;
import com.application.mypetandroid.utils.singleton_examples.PetSitterResultsSingletonClass;

import org.apache.log4j.Logger;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class PetSitterSearchInteractor {

    private static final Logger logger = Logger.getLogger(PetSitterSearchInteractor.class);
    private static final String SQL_EXCEPTION = "SQL Error: ";
    private static final String ERROR = "Something went wrong...";
    private final DBProfile dbProfile;
    private final PetSitterSearchContract.PetSitterSearchListener petSitterSearchListener;

    public PetSitterSearchInteractor(PetSitterSearchContract.PetSitterSearchListener petSitterSearchListener) {
        this.petSitterSearchListener = petSitterSearchListener;
        dbProfile = new DBProfile();
    }

    public void findPetSitters(String user, PetSitSearchFilters petSitSearchFilters) {

        if (hasInputError(petSitSearchFilters)) {
            return;
        }

        new Handler().postDelayed(() -> findResults(user, petSitSearchFilters), 1500);
    }

    private boolean hasInputError(PetSitSearchFilters petSitSearchFilters) {
        String region = petSitSearchFilters.getRegion();

        InputValidator validator = new InputValidator();
        try {
            if (validator.isEmpty(region)) {
                throw new InvalidInputException("Select a region.");
            }
            return false;
        } catch (InvalidInputException e) {
            this.petSitterSearchListener.onFindResultsFailed(e.getMessage());
            return true;
        }
    }

    private void findResults(String user, PetSitSearchFilters petSitSearchFilters) {

        try {
            Connection connection = dbProfile.getConnection();
            if (connection == null) {
                throw new ConnectionFailedException();
            }

            PetSitterResultsSingletonClass petSitterResultsSingletonClass = loadResultsFromDB(user, petSitSearchFilters, connection);
            this.dbProfile.closeConnection(connection);

            if (petSitterResultsSingletonClass == null) {
                this.petSitterSearchListener.onFindResultsFailed(ERROR);
            } else {
                this.petSitterSearchListener.onFindResultsSuccess();
            }
        }
        catch (ConnectionFailedException e) {
            this.petSitterSearchListener.onFindResultsFailed(e.getMessage());
        }

    }

    private PetSitterResultsSingletonClass loadResultsFromDB(String user, PetSitSearchFilters petSitSearchFilters, Connection connection) {
        String region = petSitSearchFilters.getRegion();
        String province = petSitSearchFilters.getProvince();
        boolean dog = petSitSearchFilters.isDog();
        boolean cat = petSitSearchFilters.isCat();
        boolean otherPets = petSitSearchFilters.isOtherPets();

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
        String query = "{ call search_pet_sitter(?, ?, ?, ?, ?) }";
        try {
            // Preparing a CallableStatement to call a procedure
            stmt = connection.prepareCall(query);
            // Setting the values for the IN parameters
            stmt.setBoolean(1, dog);
            stmt.setBoolean(2, cat);
            stmt.setBoolean(3, otherPets);
            stmt.setString(4, region);
            stmt.setString(5, province);
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

                PetSitProfileInfo loadPetSitProfileInfo = new PetSitProfileInfo();
                loadPetSitProfileInfo.setImage(photo);
                loadPetSitProfileInfo.setNumLikes(likes);
                loadPetSitProfileInfo.setNumDislikes(dislikes);
                profiles.add(loadPetSitProfileInfo);

                favorites.add(recoverFavorite(user, rs.getString("Username"), connection));
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

    private boolean recoverFavorite(String user, String petSitter, Connection connection) {
        CallableStatement stmt = null;
        boolean out = false;
        String query = "{ call recover_pet_sitter_favorite(?, ?, ?) }";
        try {
            // Preparing a CallableStatement to call a procedure
            stmt = connection.prepareCall(query);
            // Setting the values for the IN parameters
            stmt.setString(1, user);
            stmt.setString(2, petSitter);
            // Registering the type of the OUT parameter
            stmt.registerOutParameter(3, Types.BOOLEAN);
            // Executing the CallableStatement
            stmt.execute();

            // Retrieving the value for role
            out = stmt.getBoolean(3);
        }
        catch (SQLException e) {
            logger.error(SQL_EXCEPTION, e);
        }
        finally {
            assert stmt != null;
            this.dbProfile.closeStatement(stmt);
        }

        return out;
    }

}
