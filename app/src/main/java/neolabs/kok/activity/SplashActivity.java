package neolabs.kok.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import neolabs.kok.R;

public class SplashActivity extends AppCompatActivity {

    Boolean isLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        String ischeck = pref.getString("useremail",  null);

        //로그인이 되어 있는 상태인지 아닌지 체크를 해준다.
        if(ischeck != null) {
            isLogin = true;
        } else {
            isLogin = false;
        }

        Handler hd = new Handler();
        hd.postDelayed(new splashhandler(), 2000); // 1초 후에 핸들러 실행 2000ms = 2초

        //출처: http://yoo-hyeok.tistory.com/31 [유혁의 엉터리 개발]
    }

    //마찬가지로 로그인이 되어 있는지 체크를 해주고 적절한 화면으로 이동시킨다.
    private class splashhandler implements Runnable{
        public void run(){
            if(isLogin) {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        //초반 플래시 화면에서 넘어갈때 뒤로가기 버튼 못누르게 한다.
    }
}
