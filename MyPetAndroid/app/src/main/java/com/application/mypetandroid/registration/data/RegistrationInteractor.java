package com.application.mypetandroid.registration.data;


import android.content.Context;
import android.os.Handler;

import com.application.mypetandroid.registration.RegistrationContract;
import com.application.mypetandroid.utils.db.DBProfile;
import com.application.mypetandroid.utils.email.SendMail;
import com.application.mypetandroid.utils.exceptions.AlreadyUsedException;
import com.application.mypetandroid.utils.exceptions.ConnectionFailedException;
import com.application.mypetandroid.utils.exceptions.InvalidInputException;
import com.application.mypetandroid.utils.input.InputValidator;

import org.apache.log4j.Logger;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

public class RegistrationInteractor {

    private static final Logger logger = Logger.getLogger(RegistrationInteractor.class);
    private static final String SQL_EXCEPTION = "SQL Error: ";
    private static final String ERROR = "Something went wrong...";
    private final DBProfile dbProfile;
    private final RegistrationContract.RegistrationListener registrationListener;

    public RegistrationInteractor(RegistrationContract.RegistrationListener registrationListener) {
        this.registrationListener = registrationListener;
        dbProfile = new DBProfile();
    }

    public void registerAccount(RegistrationCredentials registrationCredentials) {

        if (hasInputError(registrationCredentials)) {
            return;
        }

        if (registrationCredentials.getSystemUserData().isPetSitter() && hasPetSitterError(registrationCredentials.
                getPetSitterCaredPets(), registrationCredentials.getPetSitterServices()))
        {
            return;
        }

        new Handler().postDelayed(() -> register(registrationCredentials), 2000);
    }

    private boolean hasInputError(RegistrationCredentials registrationCredentials) {
        String name = registrationCredentials.getProfileUserData().getName();
        String surname = registrationCredentials.getProfileUserData().getSurname();
        String username = registrationCredentials.getSystemUserData().getUsername();
        String email = registrationCredentials.getSystemUserData().getEmail();
        String password = registrationCredentials.getSystemUserData().getPassword();
        String passwordConfirm = registrationCredentials.getSystemUserData().getPasswordConfirm();
        String region = registrationCredentials.getProfileUserData().getRegion();
        String province = registrationCredentials.getProfileUserData().getProvince();
        String phoneNumber = registrationCredentials.getProfileUserData().getPhoneNumb();
        String firstPetName = registrationCredentials.getSystemUserData().getFirstPetName();

        InputValidator validator = new InputValidator();
        try {
            if (validator.isEmpty(name)) {
                throw new InvalidInputException("Please enter your name.");
            }
            if (validator.isEmpty(surname)) {
                throw new InvalidInputException("Please enter your surname.");
            }
            if (validator.isEmpty(region)) {
                throw new InvalidInputException("Please select your region.");
            }
            if (validator.isEmpty(province)) {
                throw new InvalidInputException("Please select your province.");
            }
            if (validator.isEmpty(phoneNumber)) {
                throw new InvalidInputException("Please enter your phone number.");
            }
            if (validator.isEmpty(username)) {
                throw new InvalidInputException("Please enter your username.");
            }
            if (validator.isEmpty(email)) {
                throw new InvalidInputException("Please enter your email.");
            }
            if (!validator.isValidEmail(email)) {
                throw new InvalidInputException("The email is invalid.");
            }
            if (validator.isEmpty(password)) {
                throw new InvalidInputException("Please enter your password.");
            }
            if (!validator.isValidPassword(password)) {
                throw new InvalidInputException("The password must be between 8 and 32 characters.");
            }
            if (validator.isEmpty(passwordConfirm)) {
                throw new InvalidInputException("Please confirm your password.");
            }
            if (!passwordConfirm.equals(password)) {
                throw new InvalidInputException("The password and its confirm must be equal.");
            }
            if (validator.isEmpty(firstPetName)) {
                throw new InvalidInputException("Please enter the name of your first pet.");
            }
            return false;
        } catch (InvalidInputException e) {
            this.registrationListener.onFailed(e.getMessage());
            return true;
        }
    }

    private boolean hasPetSitterError(PetSitCaredPets pets, PetSitServices petSitServices) {
        boolean dog = pets.isDog();
        boolean cat = pets.isCat();
        boolean otherPets = pets.isOtherPets();
        boolean service1 = petSitServices.isServ1();
        boolean service2 = petSitServices.isServ2();
        boolean service3 = petSitServices.isServ3();
        boolean service4 = petSitServices.isServ4();
        boolean service5 = petSitServices.isServ5();

        try {
            if (!dog && !cat && !otherPets) {
                throw new InvalidInputException("You don't take care of any puppies!");
            }
            if (!service1 && !service2 && !service3 && !service4 && !service5) {
                throw new InvalidInputException("You don't offer any services!");
            }

            return false;
        } catch (InvalidInputException e) {
            this.registrationListener.onFailed(e.getMessage());
            return true;
        }
    }

    private void register(RegistrationCredentials registrationCredentials) {

        try {
            Connection connection = dbProfile.getConnection();
            if (connection == null) {
                throw new ConnectionFailedException();
            }

            Boolean usedUsername = isUsed(registrationCredentials.getSystemUserData().getUsername(), connection, "{ call is_username_used(?, ?) }");
            if (usedUsername == null) {
                this.registrationListener.onFailed(ERROR);
            } else if (usedUsername) {
                throw new AlreadyUsedException("username");
            }
            Boolean usedEmail = isUsed(registrationCredentials.getSystemUserData().getEmail(), connection, "{ call is_email_used_registration(?, ?) }");
            if (usedEmail == null) {
                this.registrationListener.onFailed(ERROR);
            } else if (usedEmail) {
                throw new AlreadyUsedException("email");
            }

            Boolean out = registerDB(registrationCredentials, connection);
            this.dbProfile.closeConnection(connection);

            if (out == null) {
                this.registrationListener.onFailed(ERROR);
            } else if (!out) {
                this.registrationListener.onFailed("Registration failed. Try again.");
            } else {
                this.registrationListener.onSuccess();
            }
        }
        catch (ConnectionFailedException | AlreadyUsedException e) {
            this.registrationListener.onFailed(e.getMessage());
        }

    }

    private Boolean isUsed(String object, Connection connection, String query) {
        CallableStatement stmt = null;
        Boolean out = null;
        try {
            // Preparing a CallableStatement to call a procedure
            stmt = connection.prepareCall(query);
            // Setting the value for the IN parameter
            stmt.setString(1, object);
            // Registering the type of the OUT parameter
            stmt.registerOutParameter(2, Types.BOOLEAN);
            // Executing the CallableStatement
            stmt.execute();

            // Retrieving the value for role
            out = stmt.getBoolean(2);
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

    private Boolean registerDB(RegistrationCredentials registrationCredentials, Connection connection) {
        String name = registrationCredentials.getProfileUserData().getName();
        String surname = registrationCredentials.getProfileUserData().getSurname();
        String username = registrationCredentials.getSystemUserData().getUsername();
        String email = registrationCredentials.getSystemUserData().getEmail();
        String password = registrationCredentials.getSystemUserData().getPassword();
        String region = registrationCredentials.getProfileUserData().getRegion();
        String province = registrationCredentials.getProfileUserData().getProvince();
        String address = registrationCredentials.getProfileUserData().getAddress();
        String phone = registrationCredentials.getProfileUserData().getPhoneNumb();
        String petName = registrationCredentials.getSystemUserData().getFirstPetName();
        boolean petSitter = registrationCredentials.getSystemUserData().isPetSitter();
        boolean dog = registrationCredentials.getPetSitterCaredPets().isDog();
        boolean cat = registrationCredentials.getPetSitterCaredPets().isCat();
        boolean otherPets = registrationCredentials.getPetSitterCaredPets().isOtherPets();
        boolean serv1 = registrationCredentials.getPetSitterServices().isServ1();
        boolean serv2 = registrationCredentials.getPetSitterServices().isServ2();
        boolean serv3 = registrationCredentials.getPetSitterServices().isServ3();
        boolean serv4 = registrationCredentials.getPetSitterServices().isServ4();
        boolean serv5 = registrationCredentials.getPetSitterServices().isServ5();
        String description = registrationCredentials.getPetSitterServices().getDescription();

        CallableStatement stmt = null;
        Boolean out = null;
        String query = "{ call register_user(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }";
        try {
            // Preparing a CallableStatement to call a procedure
            stmt = connection.prepareCall(query);
            // Setting the values for the IN parameters
            stmt.setString(1, name);
            stmt.setString(2, surname);
            stmt.setString(3, username);
            stmt.setString(4, email);
            stmt.setString(5, password);
            stmt.setString(6, region);
            stmt.setString(7, province);
            stmt.setString(8, address);
            stmt.setString(9, phone);
            stmt.setString(10, petName);
            stmt.setBoolean(11, petSitter);
            stmt.setBoolean(12, dog);
            stmt.setBoolean(13, cat);
            stmt.setBoolean(14, otherPets);
            stmt.setBoolean(15, serv1);
            stmt.setBoolean(16, serv2);
            stmt.setBoolean(17, serv3);
            stmt.setBoolean(18, serv4);
            stmt.setBoolean(19, serv5);
            stmt.setString(20, description);
            // Registering the type of the OUT parameter
            stmt.registerOutParameter(21, Types.BOOLEAN);
            // Executing the CallableStatement
            stmt.execute();

            // Retrieving the value for role
            out = stmt.getBoolean(21);
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

    public void sendEmail(Context context, String username, String email) {
        String emailSubject = "REGISTRATION COMPLETED";
        String emailMessage = "Hi " + username + ", welcome to MyPet!\nWe are very happy that you are part of our community!\n\nEnter now to take advantage of our services, our puppies are waiting for you!";

        SendMail sm = new SendMail(context, email, emailSubject, emailMessage, "Registration completed");
        sm.execute();
    }

}
