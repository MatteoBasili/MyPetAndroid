package com.application.mypetandroid.services.profile.data;

import android.os.Handler;

import com.application.mypetandroid.registration.data.ProfileUserData;
import com.application.mypetandroid.services.profile.ProfileContract;
import com.application.mypetandroid.utils.db.DBProfile;
import com.application.mypetandroid.utils.exceptions.AlreadyUsedException;
import com.application.mypetandroid.utils.exceptions.ConnectionFailedException;
import com.application.mypetandroid.utils.exceptions.InvalidInputException;
import com.application.mypetandroid.utils.input.InputValidator;

import org.apache.log4j.Logger;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

public class PersonalInfoInteractor {

    private static final Logger logger = Logger.getLogger(PersonalInfoInteractor.class);
    private static final String SQL_EXCEPTION = "SQL Error: ";
    private static final String ERROR = "Something went wrong...";
    private final DBProfile dbProfile;
    private final ProfileContract.PersonalInfoListener personalInfoListener;

    public PersonalInfoInteractor(ProfileContract.PersonalInfoListener personalInfoListener) {
        this.personalInfoListener = personalInfoListener;
        this.dbProfile = new DBProfile();
    }

    public void loadPersonalInfo(String user) {
        new Handler().postDelayed(() -> loadInfo(user), 250);
    }

    public void savePersonalInfo(String user, PersonalInfo personalInfo) {
        if (hasInputError(personalInfo)) {
            return;
        }

        new Handler().postDelayed(() -> saveInfo(user, personalInfo), 500);
    }

    private void loadInfo(String user) {
        try {
            Connection connection = dbProfile.getConnection();
            if (connection == null) {
                throw new ConnectionFailedException();
            }

            PersonalInfo personalInfo = loadInfoFromDB(user, connection);
            this.dbProfile.closeConnection(connection);

            if (personalInfo == null) {
                this.personalInfoListener.onLoadInfoFailed("Error in loading informations.");
            }

            this.personalInfoListener.onLoadInfoSuccess(personalInfo);
        }
        catch (ConnectionFailedException e) {
            this.personalInfoListener.onLoadInfoFailed(e.getMessage());
        }
    }

    private boolean hasInputError(PersonalInfo personalInfo) {
        String name = personalInfo.getProfileUserData().getName();
        String surname = personalInfo.getProfileUserData().getSurname();
        String email = personalInfo.getEmail();
        String region = personalInfo.getProfileUserData().getRegion();
        String province = personalInfo.getProfileUserData().getProvince();
        String phoneNumber = personalInfo.getProfileUserData().getPhoneNumb();
        String firstPetName = personalInfo.getFirstPetName();

        InputValidator validator = new InputValidator();
        try {
            if (validator.isEmpty(name)) {
                throw new InvalidInputException("The name is empty.");
            }
            if (validator.isEmpty(surname)) {
                throw new InvalidInputException("The surname is empty.");
            }
            if (validator.isEmpty(region)) {
                throw new InvalidInputException("Select a region.");
            }
            if (validator.isEmpty(province)) {
                throw new InvalidInputException("Select a province.");
            }
            if (validator.isEmpty(phoneNumber)) {
                throw new InvalidInputException("The phone number is empty.");
            }
            if (validator.isEmpty(email)) {
                throw new InvalidInputException("The email is empty.");
            }
            if (!validator.isValidEmail(email)) {
                throw new InvalidInputException("The email is invalid.");
            }
            if (validator.isEmpty(firstPetName)) {
                throw new InvalidInputException("The first pet name is empty.");
            }
            return false;
        } catch (InvalidInputException e) {
            this.personalInfoListener.onStoreInfoFailed(e.getMessage());
            return true;
        }
    }

    private void saveInfo(String user, PersonalInfo personalInfo) {
        try {
            Connection connection = dbProfile.getConnection();
            if (connection == null) {
                throw new ConnectionFailedException();
            }

            Boolean usedEmail = isEmailUsed(user, personalInfo.getEmail(), connection);
            if (usedEmail == null) {
                this.personalInfoListener.onStoreInfoFailed(ERROR);
            } else if (usedEmail) {
                throw new AlreadyUsedException("email");
            }

            Boolean out = saveInfoToDB(user, personalInfo, connection);
            this.dbProfile.closeConnection(connection);

            if (out == null) {
                this.personalInfoListener.onStoreInfoFailed(ERROR);
            } else if(!out) {
                this.personalInfoListener.onStoreInfoFailed("Info saving failed. Try again.");
            } else {
                this.personalInfoListener.onStoreInfoSuccess();
            }
        }
        catch (ConnectionFailedException | AlreadyUsedException e) {
            this.personalInfoListener.onStoreInfoFailed(e.getMessage());
        }
    }

    private Boolean isEmailUsed(String user, String email, Connection connection) {
        CallableStatement stmt = null;
        Boolean out = null;
        String query = "{ call is_email_used_change(?, ?, ?) }";
        try {
            // Preparing a CallableStatement to call a procedure
            stmt = connection.prepareCall(query);
            // Setting the value for the IN parameters
            stmt.setString(1, user);
            stmt.setString(2, email);
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

    private PersonalInfo loadInfoFromDB(String user, Connection connection) {
        PersonalInfo personalInfo = null;
        CallableStatement stmt = null;
        String query = "{ call recover_user_info(?, ?, ?, ?, ?, ?, ?, ?, ?) }";
        try {
            // Preparing a CallableStatement to call a procedure
            stmt = connection.prepareCall(query);
            // Setting the value for the IN parameter
            stmt.setString(1, user);
            // Registering the type of the OUT parameters
            stmt.registerOutParameter(2, Types.VARCHAR);
            stmt.registerOutParameter(3, Types.VARCHAR);
            stmt.registerOutParameter(4, Types.VARCHAR);
            stmt.registerOutParameter(5, Types.VARCHAR);
            stmt.registerOutParameter(6, Types.VARCHAR);
            stmt.registerOutParameter(7, Types.VARCHAR);
            stmt.registerOutParameter(8, Types.VARCHAR);
            stmt.registerOutParameter(9, Types.VARCHAR);
            // Executing the CallableStatement
            stmt.execute();

            // Retrieving the OUT parameters
            ProfileUserData profileUserData = new ProfileUserData();
            profileUserData.setName(stmt.getString(2));
            profileUserData.setSurname(stmt.getString(3));
            profileUserData.setRegion(stmt.getString(5));
            profileUserData.setProvince(stmt.getString(6));
            profileUserData.setAddress(stmt.getString(7));
            profileUserData.setPhoneNumb(stmt.getString(8));

            personalInfo = new PersonalInfo();

            personalInfo.setProfileUserData(profileUserData);
            personalInfo.setEmail(stmt.getString(4));
            personalInfo.setFirstPetName(stmt.getString(9));

        }
        catch (SQLException e) {
            logger.error(SQL_EXCEPTION, e);
        } finally {
            assert stmt != null;
            this.dbProfile.closeStatement(stmt);
        }

        return personalInfo;
    }

    private Boolean saveInfoToDB(String user, PersonalInfo personalInfo, Connection connection) {
        String name = personalInfo.getProfileUserData().getName();
        String surname = personalInfo.getProfileUserData().getSurname();
        String region = personalInfo.getProfileUserData().getRegion();
        String province = personalInfo.getProfileUserData().getProvince();
        String address = personalInfo.getProfileUserData().getAddress();
        String phoneNumb = personalInfo.getProfileUserData().getPhoneNumb();
        String email = personalInfo.getEmail();
        String firstPetName = personalInfo.getFirstPetName();

        Boolean out = null;
        CallableStatement stmt = null;
        String query = "{ call save_user_info(?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }";
        try {
            // Preparing a CallableStatement to call a procedure
            stmt = connection.prepareCall(query);
            // Setting the value for the IN parameters
            stmt.setString(1, user);
            stmt.setString(2, name);
            stmt.setString(3, surname);
            stmt.setString(4, email);
            stmt.setString(5, region);
            stmt.setString(6, province);
            stmt.setString(7, address);
            stmt.setString(8, phoneNumb);
            stmt.setString(9, firstPetName);
            // Registering the type of the OUT parameter
            stmt.registerOutParameter(10, Types.INTEGER);
            // Executing the CallableStatement
            stmt.execute();

            // Retrieving the OUT parameter
            out = stmt.getBoolean(10);
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
