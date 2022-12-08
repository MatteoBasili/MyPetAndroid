package com.application.mypet.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.application.mypet.BuildConfig;
import com.application.mypet.R;
import com.application.mypet.services.HomeActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {

    // Declaring layout buttons, edit texts, text view
    Button login,signIn;
    EditText username,password;
    TextView recoverPwd;
    ProgressBar progressBar;
    // End Declaring layout button, edit texts

    // Declaring layout icons
    ImageView showPwd;
    ImageView hidePwd;
    // End Declaring layout icons

    // Declaring connection variables
    Connection con;
    String un,pass,db,ip,port;
    // End Declaring connection variables

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login);

        // Getting values from buttons, texts, images and progress bar
        showPwd = findViewById(R.id.show_pwd);
        hidePwd = findViewById(R.id.hide_pwd);
        username = findViewById(R.id.username);
        password = findViewById(R.id.pwd);
        recoverPwd = findViewById(R.id.forgot_pwd);
        login = findViewById(R.id.enter_button);
        signIn = findViewById(R.id.sign_in_button);
        progressBar = findViewById(R.id.progressBar);
        // End Getting values from buttons, texts, images and progress bar

        showPwd.setOnClickListener(view -> {
            password.setTransformationMethod(new HideReturnsTransformationMethod());
            password.setSelection(password.getText().length());
            hidePwd.setVisibility(View.VISIBLE);
            showPwd.setVisibility(View.INVISIBLE);
        });

        hidePwd.setOnClickListener(view -> {
            password.setTransformationMethod(new PasswordTransformationMethod());
            password.setSelection(password.getText().length());
            showPwd.setVisibility(View.VISIBLE);
            hidePwd.setVisibility(View.INVISIBLE);
        });

        progressBar.setVisibility(View.GONE);

        recoverPwd.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, PasswordRecovery.class);
            startActivity(i);
        });

        // Declaring Server ip, username, database name and password
        ip = "192.168.1.153";
        port = "3306";
        db = "mypet";
        un = "Root";
        pass = "Biblioteche.";
        // End Declaring Server ip, username, database name and password

        // Setting up the function when button login is clicked
        login.setOnClickListener(view -> {
            CheckLogin checkLogin = new CheckLogin(); // this is the Asynctask, which is used to process
                                                      // in background to reduce load on app process
            checkLogin.execute("");
        });
        // End Setting up the function when button login is clicked

        signIn.setOnClickListener(view -> {
            /*Intent i = new Intent(MainActivity.this, AccountRegistration1.class);
            startActivity(i);*/
        });
    }

    @SuppressLint("StaticFieldLeak")
    public class CheckLogin extends AsyncTask<String,String,String>
    {
        String z = "";
        Boolean isSuccess = false; // used to check whether the login fails or not

        @Override
        protected void onPreExecute()
        {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params)
        {
            String usernam = username.getText().toString();
            String passwordd = password.getText().toString();
            if(usernam.trim().equals("")|| passwordd.trim().equals(""))
            {
                z = "Please enter Username and Password";
            }
            else {
                PreparedStatement pstmt = null;
                String query = "SELECT * FROM User WHERE Username = ? AND Password = ?";
                try {
                    con = connectionclass(un, pass, db, ip, port);     // Connect to database
                    if (con == null) {
                        z = "Check Your Internet Access!";
                    } else {
                        pstmt = con.prepareStatement(query);
                        pstmt.setString(1, usernam);
                        pstmt.setString(2, passwordd);
                        ResultSet rs = pstmt.executeQuery();
                        if (rs.next()) {
                            z = "Login successful";
                            isSuccess = true;
                            con.close();
                        } else {
                            z = "Invalid Credentials!";
                            isSuccess = false;
                        }
                    }
                } catch (Exception ex) {
                    isSuccess = false;
                    z = ex.getMessage();
                } finally {
                    try {
                        assert pstmt != null;
                        pstmt.close();
                    } catch (SQLException e) {
                        //LOGGER.log("context", e);
                    }
                }
            }
            return z;
        }

        @Override
        protected void onPostExecute(String r)
        {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(MainActivity.this, r, Toast.LENGTH_SHORT).show();
            if(isSuccess)
            {
                Intent i = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(i);
                finish();
                //finish
            }
        }
    }


    @SuppressLint("NewApi")
    public Connection connectionclass(String user, String password, String database, String server, String port)
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String ConnectionURL;
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            ConnectionURL = "jdbc:mysql://" + server + ":" + port + "/" + database /*+ ";user=" + user + ";password=" + password + ";"*/;
            connection = DriverManager.getConnection(ConnectionURL, user, password);
        }
        catch (SQLException se)
        {
            Log.e("error here 1: ", se.getMessage());
        }
        catch (ClassNotFoundException e)
        {
            Log.e("error here 2: ", e.getMessage());
        }
        catch (Exception e)
        {
            Log.e("error here 3: ", e.getMessage());
        }
        return connection;
    }
}