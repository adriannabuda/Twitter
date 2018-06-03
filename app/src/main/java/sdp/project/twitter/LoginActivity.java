package sdp.project.twitter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import sdp.project.tweeter.R;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    // UI references.
    private EditText etName;
    private EditText etPassword;
    private TextView goToRegister;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etName = findViewById(R.id.etName);
        etPassword = findViewById(R.id.etPassword);
        goToRegister = findViewById(R.id.goToRegister);
        goToRegister.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(i);
            }
        });
    }

    @VisibleForTesting
    public ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("loading");
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void buLogin(View view) {
        showProgressDialog();
        String name="";
        try {
            //for space with name
            name = java.net.URLEncoder.encode( etName.getText().toString() , "UTF-8");
        } catch (UnsupportedEncodingException e) {

        }
        if(etName.getText().toString().equals("") || etPassword.getText().toString().equals("")){
            hideProgressDialog();
            Toast.makeText( getApplicationContext(),"One of the fields is empty!" , Toast.LENGTH_SHORT).show();

        }else{
            String url="https://pszczepanski.000webhostapp.com/Login.php?username="+name+"&password="+etPassword.getText().toString() ;
            new MyAsyncTaskGetNews().execute(url);
        }
    }

    public class MyAsyncTaskGetNews extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            //before works
        }
        @Override
        protected String doInBackground(String... params) {
            try {
                String NewsData;
                //define the url we have to connect with
                URL url = new URL(params[0]);
                //make connect with url and send request
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                //waiting for 7000ms for response
                urlConnection.setConnectTimeout(7000);//set timeout to 5 seconds

                try {
                    //getting the response data
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    //convert the stream to string
                    Operations operations = new Operations(getApplicationContext());
                    NewsData = operations.ConvertInputToStringNoChange(in);
                    //send to display data
                    publishProgress(NewsData);
                } finally {
                    //end connection
                    urlConnection.disconnect();
                }

            }catch (Exception ex){}
            return null;
        }

        protected void onProgressUpdate(String... progress) {

            try {
                JSONObject json= new JSONObject(progress[0]);
                //display response data
                if (json.getString("msg")==null)
                    return;
                if (json.getString("msg").equalsIgnoreCase(" Cannot Login")) {
                    hideProgressDialog();
                    Toast.makeText(getApplicationContext(),"Cannot login",Toast.LENGTH_LONG).show();
                }

                if (json.getString("msg").equalsIgnoreCase("Pass Login")) {
                    JSONArray UserInfo = new JSONArray( json.getString("info"));
                    JSONObject UserCredential = UserInfo.getJSONObject(0);
                    //Toast.makeText(getApplicationContext(),UserCredential.getString("user_id"),Toast.LENGTH_LONG).show();
                    hideProgressDialog();
                    SaveSettings saveSettings = new SaveSettings(getApplicationContext());
                    saveSettings.SaveData(UserCredential.getString("user_id"));
                    finish(); //close this activity
                }

            } catch (Exception ex) {
                Log.d("er",  ex.getMessage());
            }
        }

        protected void onPostExecute(String  result2){ }
    }

}

