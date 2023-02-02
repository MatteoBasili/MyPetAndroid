package com.application.mypetandroid.services.search.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;

import com.application.mypetandroid.registration.data.PetSitCaredPets;
import com.application.mypetandroid.registration.data.PetSitServices;
import com.application.mypetandroid.registration.data.ProfileUserData;
import com.application.mypetandroid.services.profile.data.PetSitProfileInfo;
import com.application.mypetandroid.services.search.PetSitterSearchContract;
import com.application.mypetandroid.utils.db.DBProfile;
import com.application.mypetandroid.utils.exceptions.ConnectionFailedException;

import org.apache.log4j.Logger;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class PetSitterDetailsInteractor {

    private static final Logger logger = Logger.getLogger(PetSitterDetailsInteractor.class);
    private static final String SQL_EXCEPTION = "SQL Error: ";
    private final DBProfile dbProfile;
    private final PetSitterSearchContract.PetSitterDetailsListener petSitterDetailsListener;

    public PetSitterDetailsInteractor(PetSitterSearchContract.PetSitterDetailsListener petSitterDetailsListener) {
        this.petSitterDetailsListener = petSitterDetailsListener;
        dbProfile = new DBProfile();
    }

    public void loadDetails(String user, String petSitter) {
        new Handler().postDelayed(() -> load(user, petSitter), 250);
    }

    private void load(String user, String petSitter) {
        try {
            Connection connection = dbProfile.getConnection();
            if (connection == null) {
                throw new ConnectionFailedException();
            }

            PetSitterDetails petSitterDetails = loadFromDB(user, petSitter, connection);
            this.dbProfile.closeConnection(connection);

            if (petSitterDetails == null) {
                this.petSitterDetailsListener.onLoadDetailsFailed("Error in loading profile.");
            }

            this.petSitterDetailsListener.onLoadDetailsSuccess(petSitterDetails);
        }
        catch (ConnectionFailedException e) {
            this.petSitterDetailsListener.onLoadDetailsFailed(e.getMessage());
        }
    }

    private PetSitterDetails loadFromDB(String user, String petSitter, Connection connection) {
        PetSitterDetails petSitterDetails = null;

        PetSitCaredPets petSitCaredPets = new PetSitCaredPets();
        PetSitServices petSitServices = new PetSitServices();
        PetSitterRating petSitterRating = new PetSitterRating();
        ProfileUserData profileUserData = new ProfileUserData();
        PetSitProfileInfo loadPetSitProfileInfo = new PetSitProfileInfo();
        String email = null;
        Bitmap image = null;

        CallableStatement stmt = null;
        String query = "{ call load_pet_sit_details(?, ?, ?, ?) }";
        try {
            // Preparing a CallableStatement to call a procedure
            stmt = connection.prepareCall(query);
            // Setting the value for the IN parameters
            stmt.setString(1, user);
            stmt.setString(2, petSitter);
            // Registering the type of the OUT parameters
            stmt.registerOutParameter(3, Types.BOOLEAN);
            stmt.registerOutParameter(4, Types.INTEGER);
            // Executing the CallableStatement
            ResultSet rs = stmt.executeQuery();

            // Retrieving the OUT parameters
            while (rs.next()) {

                // Take cared pets
                petSitCaredPets.setDog(rs.getBoolean("Dog"));
                petSitCaredPets.setCat(rs.getBoolean("Cat"));
                petSitCaredPets.setOtherPets(rs.getBoolean("OtherPets"));

                // Take services
                petSitServices.setServ1(rs.getBoolean("AtHome"));
                petSitServices.setServ2(rs.getBoolean("AtPetSitHome"));
                petSitServices.setServ3(rs.getBoolean("HomeVisit"));
                petSitServices.setServ4(rs.getBoolean("Walk"));
                petSitServices.setServ5(rs.getBoolean("ChangeOfLitter"));
                petSitServices.setDescription(rs.getString("Description"));

                // Take photo, likes and dislikes
                loadPetSitProfileInfo.setNumLikes(rs.getInt("Likes"));
                loadPetSitProfileInfo.setNumDislikes(rs.getInt("Dislikes"));
                byte[] imageInByte = rs.getBytes("Photo");
                if (imageInByte != null) {
                    image = BitmapFactory.decodeByteArray(imageInByte, 0, imageInByte.length);
                }
                loadPetSitProfileInfo.setImage(image);

                // Take profile data
                profileUserData.setName(rs.getString("Name"));
                profileUserData.setSurname(rs.getString("Surname"));
                profileUserData.setProvince(rs.getString("Province"));
                profileUserData.setPhoneNumb(rs.getString("PhoneNumber"));

                // Take email
                email = rs.getString("Email");
            }

            // Take rating
            petSitterRating.setFavorite(stmt.getBoolean(3));
            petSitterRating.setRating(stmt.getInt(4));

            petSitterDetails = new PetSitterDetails();
            petSitterDetails.setPetSitterRating(petSitterRating);
            petSitterDetails.setPetSitCaredPets(petSitCaredPets);
            petSitterDetails.setPetSitServices(petSitServices);
            petSitterDetails.setPetSitProfileInfo(loadPetSitProfileInfo);
            petSitterDetails.setProfileUserData(profileUserData);
            petSitterDetails.setEmail(email);

        }
        catch (SQLException e) {
            logger.error(SQL_EXCEPTION, e);
        } finally {
            assert stmt != null;
            this.dbProfile.closeStatement(stmt);
        }

        return petSitterDetails;
    }

}
