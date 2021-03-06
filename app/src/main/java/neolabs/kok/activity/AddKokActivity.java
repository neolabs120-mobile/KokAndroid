package neolabs.kok.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RestrictTo;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import neolabs.kok.sutff.GPSInfo;
import neolabs.kok.R;
import neolabs.kok.data.Data;
import neolabs.kok.retrofit.RetrofitExService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

public class AddKokActivity extends RxAppCompatActivity {

    EditText inputmessage;
    Button sendmessage;
    String gomessage;
    String userauthid;
    String usernickname;
    String userprofile;

    private final int PERMISSIONS_ACCESS_FINE_LOCATION = 1000;
    private final int PERMISSIONS_ACCESS_COARSE_LOCATION = 1001;
    private boolean isAccessFineLocation = false;
    private boolean isAccessCoarseLocation = false;
    private boolean isPermission = false;

    private GPSInfo GPS;

    static Activity activity;

    public static void finishThis() {
        if (activity != null) activity.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_kok);

        sendmessage = findViewById(R.id.sendbutton2);
        inputmessage = findViewById(R.id.editText2);

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        userauthid = pref.getString("userauthid", null); //????????? ????????? ?????? ?????? ????????? ??????????????? ??????????????????. ????????????....
        usernickname = pref.getString("nickname", null);
        userprofile = pref.getString("profileImage", null);

        sendmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gomessage = inputmessage.getText().toString();

                if(!isPermission){
                    callPermission();
                    return;
                }

                GPS = new GPSInfo(AddKokActivity.this);

                // GPS ???????????? ????????????
                if (GPS.isGetLocation()) {
                    //GPSInfo??? ?????? ????????? ???????????? ?????????
                    double latitude = GPS.getLatitude();
                    double longitude = GPS.getLongitude();

                    sendreqeust(String.format("%f", latitude), String.format("%f", longitude), userauthid, gomessage, usernickname, userprofile);
                } else {
                    // GPS ??? ???????????? ????????????
                    GPS.showSettingsAlert();
                }
            }
        });

        callPermission();
    }

    //?????? ????????? ???????????? ???????????? ????????????.
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_ACCESS_FINE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            isAccessFineLocation = true;
        } else if (requestCode == PERMISSIONS_ACCESS_COARSE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            isAccessCoarseLocation = true;
        }

        if (isAccessFineLocation && isAccessCoarseLocation) {
            isPermission = true;
        }
    }


    // ?????? ?????? ??????
    private void callPermission() {
        // Check the SDK version and whether the permission is already granted or not.
        // SDK????????? ????????????, ????????? ?????? ????????? ????????? ???????????? ???????????????.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(AddKokActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_ACCESS_FINE_LOCATION);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(AddKokActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            requestPermissions(
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_ACCESS_COARSE_LOCATION);
        } else {
            isPermission = true;
        }
    }

    //?????? ???????????? ?????????.
    public void sendreqeust(String latitude, String longitude, String userauthid, String message, String usernickname, String userprofile) {
        Retrofit client = new Retrofit.Builder().baseUrl(RetrofitExService.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        RetrofitExService service = client.create(RetrofitExService.class);
        Call<Data> call = service.addPick(latitude, longitude, userauthid, message, usernickname, userprofile);
        call.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call, retrofit2.Response<Data> response) {
                switch (response.code()) {
                    case 200:
                        Toast.makeText(AddKokActivity.this, "??? ?????? ??????", Toast.LENGTH_SHORT).show();
                        break;
                    case 409:
                        Toast.makeText(AddKokActivity.this, "????????? ??????????????????.", Toast.LENGTH_SHORT).show();
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
}
