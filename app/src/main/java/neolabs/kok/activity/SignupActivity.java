package neolabs.kok.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;

import neolabs.kok.data.Profile;
import neolabs.kok.retrofit.RetrofitConfig;
import neolabs.kok.sutff.LockClass;
import neolabs.kok.R;
import neolabs.kok.data.Data;
import neolabs.kok.retrofit.RetrofitExService;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignupActivity extends AppCompatActivity {

    private static final int PICK_FROM_ALBUM = 10;

    Uri imageUri = null;
    Button signupcomplete;
    EditText inputemail;
    EditText inputpassword;
    EditText inputnickname;
    EditText inputintroduce;
    RadioButton male;
    RadioButton female;
    String emailstring;
    String passwordstring;
    String nicknamestring;
    String genderstring;
    String introducestring;
    String encryptedstring;
    ImageView profileImage;
    LockClass getsha512 = new LockClass();

    String mediaPath;

    static Activity activity;

    public static void finishThis() {
        if (activity != null) activity.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        setView();
        setPermission();

        signupcomplete = findViewById(R.id.email_signup_button2);
        signupcomplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //???????????? ?????? ?????? ???????????? ??????????????? ??????????????? ?????????.
                emailstring = inputemail.getText().toString();
                passwordstring = inputpassword.getText().toString();
                nicknamestring = inputnickname.getText().toString();
                if(male.isChecked()) {
                    genderstring = "Male";
                } else if(female.isChecked()) {
                    genderstring = "Female";
                }
                introducestring = inputintroduce.getText().toString();
                encryptedstring = getsha512.getSHA512(passwordstring);
                passwordstring = "";

                signintoserver(emailstring, encryptedstring, genderstring, introducestring, nicknamestring);
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, PICK_FROM_ALBUM);
            }
        });
    }

    public void setView() {
        inputemail = findViewById(R.id.email_edittext2);
        inputpassword = findViewById(R.id.password_edittext2);
        inputnickname = findViewById(R.id.getname_edittext3);
        inputintroduce = findViewById(R.id.getname_edittext5);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        profileImage = findViewById(R.id.image_logo);
    }

    public void setPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
            }
        }
    }


    //????????? ???????????? ??????????????????.
    public void signintoserver(final String email, final String password, final String gender, final String introduce, final String nickname) {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(SignupActivity.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                final String newToken = instanceIdResult.getToken();
                //Log.e("newToken", newToken);
                Retrofit client = new Retrofit.Builder().baseUrl(RetrofitExService.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
                RetrofitExService service = client.create(RetrofitExService.class);
                Call<Data> call = service.signupUserInfo(email, password, gender, nickname, introduce, FirebaseInstanceId.getInstance().getToken());
                call.enqueue(new Callback<Data>() {
                    @Override
                    public void onResponse(@NonNull Call<Data> call, @NonNull retrofit2.Response<Data> response) {
                        switch (response.code()) {
                            case 200:
                                Data body = response.body();

                                SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();

                                if(imageUri != null) {
                                    //POST??? ?????? ???????????? ?????? ????????? ?????? Sharedpreferences??? ???????????????.
                                    sendProfileImage(body.getId());
                                } else {
                                    editor.putString("profileImage", "default");
                                }

                                //Retrofit?????? ????????? ???????????? Sharedpreferences??? ???????????????.
                                editor.putString("useremail", body.getEmail());
                                editor.putString("userauthid", body.getId());
                                editor.putString("gender", body.getGender());
                                editor.putString("nickname", body.getNickname());
                                editor.putString("introduce", body.getIntroduce());
                                editor.putString("firebasetoken",  newToken);
                                editor.apply();

                                Toast.makeText(SignupActivity.this, "?????? ??????", Toast.LENGTH_SHORT).show();

                                //????????? ?????? ????????? ?????? ??????????????? ??????.
                                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                LoginActivity.finishThis();
                                startActivity(intent);
                                finish();
                                break;
                            case 409:
                                Toast.makeText(SignupActivity.this, "?????? ???????????? ??????????????????. ?????? ???????????? ???????????? ????????????.", Toast.LENGTH_SHORT).show();
                            default:
                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call<Data> call, Throwable t) {
                        Log.d("checkonthe", "error");
                    }
                });
            }
        });

        //??????: http://falinrush.tistory.com/5 [?????? ????????????]
    }

    public void sendProfileImage(String userauth) {
        File file = new File(mediaPath);

        HashMap<String, Object> input = new HashMap<>();
        input.put("userauthid", userauth);

        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestBody);

        RetrofitExService getResponse = RetrofitConfig.getRetrofit().create(RetrofitExService.class);
        Call<Profile> call = getResponse.uploadProfile(body, input);
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                if(response.isSuccessful()) {
                    SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("profileImage", response.body().getFilename());
                    editor.apply();
                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                Log.d("FailFailFail", "FailFailFail");
            }
        });
    }

    //????????? ?????? ????????? ???????????? ???????????? ??????????????? ????????? ????????????.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PICK_FROM_ALBUM && resultCode == RESULT_OK) {
            profileImage.setImageURI(data.getData()); //????????? ???????????? ??????.
            imageUri = data.getData(); //????????? ?????? ??????

            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(imageUri, filePathColumn, null, null, null);
            assert cursor != null;
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            mediaPath = cursor.getString(columnIndex);
            cursor.close();
        }
    }
}
