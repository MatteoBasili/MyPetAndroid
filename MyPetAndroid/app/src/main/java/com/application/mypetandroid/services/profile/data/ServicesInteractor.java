package com.application.mypetandroid.services.profile.data;

import android.os.Handler;

import com.application.mypetandroid.registration.data.PetSitServices;
import com.application.mypetandroid.services.profile.ProfileContract;
import com.application.mypetandroid.utils.db.DBProfile;
import com.application.mypetandroid.utils.exceptions.ConnectionFailedException;
import com.application.mypetandroid.utils.exceptions.InvalidInputException;

import org.apache.log4j.Logger;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

public class ServicesInteractor {

    private static final Logger logger = Logger.getLogger(ServicesInteractor.class);
    private static final String SQL_EXCEPTION = "SQL Error: ";
    private static final String ERROR = "Something went wrong...";
    private final DBProfile dbProfile;
    private final ProfileContract.ServicesListener servicesListener;

    public ServicesInteractor(ProfileContract.ServicesListener servicesListener) {
        this.servicesListener = servicesListener;
        this.dbProfile = new DBProfile();
    }

    public void loadServices(String user) {
        new Handler().postDelayed(() -> load(user), 250);
    }

    public void saveServices(String user, PetSitServices petSitServices) {
        if (hasInputError(petSitServices)) {
            return;
        }

        new Handler().postDelayed(() -> save(user, petSitServices), 500);
    }

    private void load(String user) {
        try {
            Connection connection = dbProfile.getConnection();
            if (connection == null) {
                throw new ConnectionFailedException();
            }

            PetSitServices petSitServices = loadServicesFromDB(user, connection);
            this.dbProfile.closeConnection(connection);

            if (petSitServices == null) {
                this.servicesListener.onLoadServicesFailed("Error in loading informations.");
            }

            this.servicesListener.onLoadServicesSuccess(petSitServices);
        }
        catch (ConnectionFailedException e) {
            this.servicesListener.onLoadServicesFailed(e.getMessage());
        }
    }

    private boolean hasInputError(PetSitServices petSitServices) {
        boolean serv1 = petSitServices.isServ1();
        boolean serv2 = petSitServices.isServ2();
        boolean serv3 = petSitServices.isServ3();
        boolean serv4 = petSitServices.isServ4();
        boolean serv5 = petSitServices.isServ5();

        try {
            if (!serv1 && !serv2 && !serv3 && !serv4 && !serv5) {
                throw new InvalidInputException("Select at least one service.");
            }

            return false;
        } catch (InvalidInputException e) {
            this.servicesListener.onStoreServicesFailed(e.getMessage());
            return true;
        }
    }

    private void save(String user, PetSitServices petSitServices) {
        try {
            Connection connection = dbProfile.getConnection();
            if (connection == null) {
                throw new ConnectionFailedException();
            }

            Boolean out = saveServicesToDB(user, petSitServices, connection);
            this.dbProfile.closeConnection(connection);

            if (out == null) {
                this.servicesListener.onStoreServicesFailed(ERROR);
            } else if(!out) {
                this.servicesListener.onStoreServicesFailed("Info saving failed. Try again.");
            } else {
                this.servicesListener.onStoreServicesSuccess();
            }
        }
        catch (ConnectionFailedException e) {
            this.servicesListener.onStoreServicesFailed(e.getMessage());
        }
    }

    private PetSitServices loadServicesFromDB(String user, Connection connection) {
        PetSitServices petSitServices = null;
        CallableStatement stmt = null;
        String query = "{ call recover_pet_sit_services(?, ?, ?, ?, ?, ?, ?) }";
        try {
            // Preparing a CallableStatement to call a procedure
            stmt = connection.prepareCall(query);
            // Setting the value for the IN parameter
            stmt.setString(1, user);
            // Registering the type of the OUT parameters
            stmt.registerOutParameter(2, Types.BOOLEAN);
            stmt.registerOutParameter(3, Types.BOOLEAN);
            stmt.registerOutParameter(4, Types.BOOLEAN);
            stmt.registerOutParameter(5, Types.BOOLEAN);
            stmt.registerOutParameter(6, Types.BOOLEAN);
            stmt.registerOutParameter(7, Types.VARCHAR);
            // Executing the CallableStatement
            stmt.execute();

            // Retrieving the OUT parameters
            petSitServices = new PetSitServices();
            petSitServices.setServ1(stmt.getBoolean(2));
            petSitServices.setServ2(stmt.getBoolean(3));
            petSitServices.setServ3(stmt.getBoolean(4));
            petSitServices.setServ4(stmt.getBoolean(5));
            petSitServices.setServ5(stmt.getBoolean(6));
            petSitServices.setDescription(stmt.getString(7));

        }
        catch (SQLException e) {
            logger.error(SQL_EXCEPTION, e);
        } finally {
            assert stmt != null;
            this.dbProfile.closeStatement(stmt);
        }

        return petSitServices;
    }

    private Boolean saveServicesToDB(String user, PetSitServices petSitServices, Connection connection) {
        boolean serv1 = petSitServices.isServ1();
        boolean serv2 = petSitServices.isServ2();
        boolean serv3 = petSitServices.isServ3();
        boolean serv4 = petSitServices.isServ4();
        boolean serv5 = petSitServices.isServ5();
        String description = petSitServices.getDescription();

        Boolean out = null;
        CallableStatement stmt = null;
        String query = "{ call save_pet_sit_services(?, ?, ?, ?, ?, ?, ?, ?) }";
        try {
            // Preparing a CallableStatement to call a procedure
            stmt = connection.prepareCall(query);
            // Setting the value for the IN parameters
            stmt.setString(1, user);
            stmt.setBoolean(2, serv1);
            stmt.setBoolean(3, serv2);
            stmt.setBoolean(4, serv3);
            stmt.setBoolean(5, serv4);
            stmt.setBoolean(6, serv5);
            stmt.setString(7, description);
            // Registering the type of the OUT parameter
            stmt.registerOutParameter(8, Types.INTEGER);
            // Executing the CallableStatement
            stmt.execute();

            // Retrieving the OUT parameter
            out = stmt.getBoolean(8);
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
