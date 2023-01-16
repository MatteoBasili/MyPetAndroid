package com.application.mypet.services.profile.petsitter.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import com.application.mypet.exceptions.ConnectionFailedException;
import com.application.mypet.services.profile.ProfileContract;
import com.application.mypet.utils.db.DBConnection;
import com.mysql.jdbc.CallableStatement;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.SQLException;

public class PetSitProfileInteractor {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final String SQL_EXCEPTION = "SQL Error: ";
    private DBConnection dbConnection;
    private final ProfileContract.ProfileListener profileListener;

    public PetSitProfileInteractor(ProfileContract.ProfileListener profileListener2) {
        this.profileListener = profileListener2;
    }

    public void loadProfile(String user) {
        new Handler().postDelayed(new PetSitProfileInteractor$$ExternalSyntheticLambda0(this, user), 10);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$loadProfile$0$com-application-mypet-services-profile-petsitter-data-PetSitProfileInteractor  reason: not valid java name */
    public /* synthetic */ void m2lambda$loadProfile$0$comapplicationmypetservicesprofilepetsitterdataPetSitProfileInteractor(String user) {
        DBConnection dBConnection = new DBConnection("100");
        this.dbConnection = dBConnection;
        Connection connection = dBConnection.getConnection();
        if (connection != null) {
            LoadProfileInfo loadProfileInfo = loadDB(user, connection);
            this.dbConnection.closeConnection(connection);
            if (loadProfileInfo == null) {
                this.profileListener.onLoadFailed("Error loading profile");
            } else {
                this.profileListener.onLoadProfileSuccess(loadProfileInfo);
            }
        } else {
            try {
                throw new ConnectionFailedException();
            } catch (ConnectionFailedException e) {
                this.profileListener.onLoadFailed(e.getMessage());
            }
        }
    }

    public void savePhoto(SavePhotoInfo savePhotoInfo) {
        new Handler().postDelayed(new PetSitProfileInteractor$$ExternalSyntheticLambda1(this, savePhotoInfo), 10);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$savePhoto$1$com-application-mypet-services-profile-petsitter-data-PetSitProfileInteractor  reason: not valid java name */
    public /* synthetic */ void m3lambda$savePhoto$1$comapplicationmypetservicesprofilepetsitterdataPetSitProfileInteractor(SavePhotoInfo savePhotoInfo) {
        DBConnection dBConnection = new DBConnection();
        this.dbConnection = dBConnection;
        Connection connection = dBConnection.getConnection();
        if (connection != null) {
            boolean out = storeDB(savePhotoInfo, connection);
            this.dbConnection.closeConnection(connection);
            if (!out) {
                this.profileListener.onStoreFailed("Something went wrong...");
            } else {
                this.profileListener.onStoreSuccess();
            }
        } else {
            try {
                throw new ConnectionFailedException();
            } catch (ConnectionFailedException e) {
                this.profileListener.onStoreFailed(e.getMessage());
            }
        }
    }

    private boolean storeDB(SavePhotoInfo savePhotoInfo, Connection connection) {
        boolean out = false;
        String user = savePhotoInfo.getUser();
        Bitmap photo = savePhotoInfo.getPhoto();
        byte[] imageInByte = null;
        if (photo != null) {
            ByteArrayOutputStream objectByteArrayOutputStream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 100, objectByteArrayOutputStream);
            imageInByte = objectByteArrayOutputStream.toByteArray();
        }
        CallableStatement stmt = null;
        try {
            stmt = connection.prepareCall("{ call save_profile_photo(?, ?, ?) }");
            stmt.setString(1, user);
            stmt.setBytes(2, imageInByte);
            stmt.registerOutParameter(3, 16);
            stmt.execute();
            out = stmt.getBoolean(3);
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
            this.dbConnection.closeStatement((CallableStatement) null);
            throw th;
        }
        this.dbConnection.closeStatement(stmt);
        return out;
    }

    private LoadProfileInfo loadDB(String user, Connection connection) {
        Bitmap image = null;
        LoadProfileInfo loadProfileInfo = null;
        try {
            java.sql.CallableStatement stmt = connection.prepareCall("{ call load_profile(?, ?, ?, ?) }");
            stmt.setString(1, user);
            stmt.registerOutParameter(2, 2004);
            stmt.registerOutParameter(3, 4);
            stmt.registerOutParameter(4, 4);
            stmt.execute();
            byte[] imageInByte = stmt.getBytes(2);
            if (imageInByte != null) {
                image = BitmapFactory.decodeByteArray(imageInByte, 0, imageInByte.length);
            }
            loadProfileInfo = new LoadProfileInfo(image, stmt.getInt(3), stmt.getInt(4));
            if (stmt != null) {
                this.dbConnection.closeStatement((CallableStatement) stmt);
                return loadProfileInfo;
            }
            throw new AssertionError();
        } catch (SQLException e) {
            Log.e(SQL_EXCEPTION, e.getMessage());
            if (0 != 0) {
                this.dbConnection.closeStatement((CallableStatement) null);
            } else {
                throw new AssertionError();
            }
        } catch (Throwable th) {
            if (0 == 0) {
                throw new AssertionError();
            }
            this.dbConnection.closeStatement((CallableStatement) null);
            throw th;
        }
    }
}
