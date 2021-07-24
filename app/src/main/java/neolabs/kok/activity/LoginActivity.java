package neolabs.kok.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import neolabs.kok.firebase.MyFirebaseMessagingService;
import neolabs.kok.sutff.LockClass;
import neolabs.kok.R;
import neolabs.kok.data.Data;
import neolabs.kok.retrofit.RetrofitExService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    Button login;
    Button signup;
    EditText inputemail;
    EditText inputpassword;
    String emailstring;
    String passwordstring;
    String encryptedstring;
    LockClass getsha512 = new LockClass();

    static Activity activity;

    public static void finishThis() {
        if (activity != null) activity.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = findViewById(R.id.email_login_button);
        signup = findViewById(R.id.email_signup_button);
        inputemail = findViewById(R.id.email_edittext);
        inputpassword = findViewById(R.id.password_edittext);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailstring = inputemail.getText().toString();
                passwordstring = inputpassword.getText().toString();

                //SHA-512암호화를 해준다.
                encryptedstring = getsha512.getSHA512(passwordstring);
                passwordstring = ""; //암호화가 끝난 이후에는 원문을 메모리에서도 제거한다.

                //결과를 받아온다.
                logintoserver(emailstring, encryptedstring);
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    private void logintoserver(String email, String password) {
        Retrofit client = new Retrofit.Builder().baseUrl(RetrofitExService.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        RetrofitExService service = client.create(RetrofitExService.class);
        Call<Data> call = service.signinUserInfo(email, password);
        call.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call, retrofit2.Response<Data> response) {
                switch (response.code()) {
                    case 200:
                        Data body = response.body();

                        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("useremail", body.getEmail());
                        editor.putString("userauthid", body.getId());
                        editor.putString("gender", body.getGender());
                        editor.putString("nickname", body.getNickname());
                        editor.putString("introduce", body.getIntroduce());
                        editor.putString("profileImage", body.getProfileimage());
                        editor.apply();

                        String token = pref.getString("firebasetoken", "");
                        savetokenToServer(body.getEmail(), token);

                        Toast.makeText(LoginActivity.this, "로그인 완료", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case 409:
                        Toast.makeText(LoginActivity.this, "존재하지 않은 계정입니다.", Toast.LENGTH_SHORT).show();
                    default:
                        Log.e("asdf", response.code() + "");
                        break;
                }
            }

            @Override
            public void onFailure(Call<Data> call, Throwable t) {
                Log.d("checkonthe", "error");
            }
        });
    }

    public void savetokenToServer(String useremail, String token) {
        Retrofit client = new Retrofit.Builder().baseUrl(RetrofitExService.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        RetrofitExService service = client.create(RetrofitExService.class);
        Call<Data> call = service.editToken(useremail, token);
        call.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(@NonNull Call<Data> call, @NonNull retrofit2.Response<Data> response) {
                switch (response.code()) {
                    case 200:
                        break;
                    case 409:
                        Toast.makeText(getApplicationContext(), "에러가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Log.e("asdf", response.code() + "");
                        break;
                }
            }

            @Override
            public void onFailure(@NonNull Call<Data> call, @NonNull Throwable t) {
                Log.d("checkonthe", "error");
            }
        });

    }
}
