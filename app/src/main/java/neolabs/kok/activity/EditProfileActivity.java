package neolabs.kok.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
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

import java.io.File;
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

public class EditProfileActivity extends AppCompatActivity {

    private static final int PICK_FROM_ALBUM = 10;

    EditText inputpassword;
    EditText inputnickname;
    EditText inputintroduce;
    RadioButton editmale;
    RadioButton editfemale;
    Button senddata;
    String passwordstring;
    String encryptedstring;
    String nicknamestring;
    String genderstring;
    String introducestring;
    LockClass getsha512 = new LockClass();
    Uri imageUri = null; //초기값을 null로 따로 정의
    ImageView logoView;

    String mediaPath;

    static Activity activity;

    //외부에서 Activity 끝내기
    public static void finishThis() {
        if (activity != null) activity.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        setView();
        setPermission();

        logoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent로 보낸다.
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, PICK_FROM_ALBUM);
            }
        });

        senddata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                String findemail = pref.getString("useremail",  "");

                passwordstring = inputpassword.getText().toString();
                if(!passwordstring.equals("")) encryptedstring = getsha512.getSHA512(passwordstring);
                else encryptedstring = "";
                passwordstring = "";

                nicknamestring = inputnickname.getText().toString();
                if(editmale.isChecked()) {
                    genderstring = "Male";
                } else if(editfemale.isChecked()) {
                    genderstring = "Female";
                } else {
                    genderstring = "";
                }

                introducestring = inputintroduce.getText().toString();

                Retrofit client = new Retrofit.Builder().baseUrl(RetrofitExService.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
                RetrofitExService service = client.create(RetrofitExService.class);
                Call<Data> call = service.EditUserInfo(findemail, encryptedstring, genderstring, nicknamestring, introducestring);
                call.enqueue(new Callback<Data>() {
                    @Override
                    public void onResponse(Call<Data> call, retrofit2.Response<Data> response) {
                        switch (response.code()) {
                            case 200:
                                Data body = response.body();

                                SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit();

                                if(imageUri != null) {
                                    sendProfileImage(body.getId());
                                    editor.putString("profileImage", body.getProfileimage());
                                }

                                editor.putString("gender", body.getGender());
                                editor.putString("nickname", body.getNickname());
                                editor.putString("introduce", body.getIntroduce());
                                editor.apply();

                                Toast.makeText(EditProfileActivity.this, "수정 완료", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                                break;
                            case 409:
                                Toast.makeText(EditProfileActivity.this, "문제가 발생했습니다.", Toast.LENGTH_SHORT).show();
                            default:
                                break;
                        }
                    }

                    @Override
                    public void onFailure(Call<Data> call, Throwable t) {
                    }
                });
            }
        });
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

    //뷰 설정
    public void setView() {
        senddata = findViewById(R.id.email_signup_button3);
        inputpassword = findViewById(R.id.password_edittext3);
        inputnickname = findViewById(R.id.getname_edittext4);
        inputintroduce = findViewById(R.id.getname_edittext6);
        editmale = findViewById(R.id.male2);
        editfemale = findViewById(R.id.female2);
        logoView = findViewById(R.id.image_logo2);
    }

    //이미지 선택 결과를 받아와서 이미지를 변경해주고 경로를 저장한다.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PICK_FROM_ALBUM && resultCode == RESULT_OK) {
            logoView.setImageURI(data.getData()); //가운데 이미지뷰 변경.
            imageUri = data.getData(); //이미지 경로 원본

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
