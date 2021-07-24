package neolabs.kok.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import neolabs.kok.R;
import neolabs.kok.sutff.RecyclerItemClickListener;
import neolabs.kok.data.KokData;
import neolabs.kok.item.KokItem;
import neolabs.kok.retrofit.RetrofitExService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfileActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    Button logout;
    Button editprofile;
    String usernickname;
    String userintroduce;
    String userauthid;
    TextView putusername;
    TextView putintroduce;
    RecyclerView recyclerView;

    RecyclerAdapter2 mAdapter;
    List<KokItem> items = new ArrayList<>();

    SwipeRefreshLayout mSwipeRefreshLayout;
    String[] kokauthidarray = new String[99999];

    ImageView profileImage;
    String profileImageUri;

    static Activity activity;

    public static void finishThis() {
        if (activity != null) activity.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        usernickname = pref.getString("nickname", "");
        userintroduce = pref.getString("introduce", "");
        userauthid = pref.getString("userauthid", "");

        setView();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.clear();
                editor.apply();

                MainActivity.finishThis();
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Toast.makeText(getApplicationContext(),position+"번 째 아이템 클릭",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onLongItemClick(View view, final int position) {
                        Toast.makeText(getApplicationContext(),position+"번 째 아이템 롱 클릭",Toast.LENGTH_SHORT).show();
                        //삭제 여부를 물어본후 삭제한다.
                        AlertDialog.Builder alt_bld = new AlertDialog.Builder(ProfileActivity.this);
                        alt_bld.setMessage("정말로 삭제하시겠습니까?").setCancelable(
                                false).setPositiveButton("네",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // 네 클릭
                                        deletekokfromserver(kokauthidarray[position]);
                                        items.remove(position);
                                        mAdapter.notifyDataSetChanged();
                                    }
                                }).setNegativeButton("아니오",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // 아니오 클릭. dialog 닫기.
                                        dialog.cancel();
                                    }
                                });

                        AlertDialog alert = alt_bld.create();

                        // 대화창 아이콘 설정
                        alert.setIcon(R.drawable.icon);

                        alert.show();
                    }
                }));
        getkokfromserver();
    }

    //초기 뷰 설정
    public void setView() {
        mSwipeRefreshLayout = findViewById(R.id.swipe_layout2);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        mSwipeRefreshLayout.setRefreshing(false);

        putusername = findViewById(R.id.textView);
        putintroduce = findViewById(R.id.textView3);

        logout = findViewById(R.id.logoutbutton);
        editprofile = findViewById(R.id.editbutton);

        recyclerView = findViewById(R.id.myrecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //여기에 아이템 추가.... 위치 얻어서....?

        //items.add(new KokItem("test"));

        mAdapter = new RecyclerAdapter2(items);
        recyclerView.setAdapter(mAdapter);

        putusername.setText(usernickname);
        putintroduce.setText(userintroduce);

        profileImage = findViewById(R.id.profile_image);

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        String profilelink = pref.getString("profileImage",  null);

        //나중에 로드중인 GIF로 바꿔주자.


        if(profilelink.equals("default")) {
            profileImage.setImageResource(R.drawable.icon);
        } else {
            Glide.with(ProfileActivity.this)
                    .load(RetrofitExService.BASE_URL + "images/" + profilelink)
                    .apply(RequestOptions.circleCropTransform())
                    .into(profileImage);
        }
    }

    //수정후 다시 화면을 띄울때 반영을 해준다.
    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        String nickname = pref.getString("nickname", "");
        String introduce = pref.getString("introduce", "");
        String profilelink = pref.getString("profileImage", "");
        Log.d("seeprofilelink", profilelink);

        putusername.setText(nickname);
        putintroduce.setText(introduce);

        //나중에 로드중인 gif로 바꿔주자.
        /*profileImage.setImageResource(R.mipmap.ic_launcher_round);

        Glide.with(ProfileActivity.this)
                .load(RetrofitExService.BASE_URL + "images/" + profilelink)
                .apply(RequestOptions.circleCropTransform())
                .into(profileImage);*/
        if(profilelink.equals("default")) {
            profileImage.setImageResource(R.drawable.icon);
        } else {
            Glide.with(ProfileActivity.this)
                    .load(RetrofitExService.BASE_URL + "images/" + profilelink)
                    .apply(RequestOptions.circleCropTransform())
                    .into(profileImage);
        }
    }

    //서버에서 가까이에 있는 콕을 받아온다.
    public void getkokfromserver () {
        Retrofit client = new Retrofit.Builder().baseUrl(RetrofitExService.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        RetrofitExService service = client.create(RetrofitExService.class);
        Call<List<KokData>> call = service.getmyPick(userauthid);
        call.enqueue(new Callback<List<KokData>>() {
            @Override
            public void onResponse(@NonNull Call<List<KokData>> call, @NonNull retrofit2.Response<List<KokData>> response) {
                Log.d("softtag", "isitworkingcheck");
                switch (response.code()) {
                    case 200:
                        for(int i = 0; i < response.body().size(); i++) {
                            items.add(new KokItem(response.body().get(i).getMessage()));
                            kokauthidarray[i] = response.body().get(i).getId();
                        }
                        mAdapter.notifyDataSetChanged();
                        mSwipeRefreshLayout.setRefreshing(false);
                        //출처: http://jekalmin.tistory.com/entry/Gson을-이용한-json을-객체에-담기 [jekalmin의 블로그]
                        break;
                    case 409:
                        Toast.makeText(ProfileActivity.this, "에러가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                    default:
                        Log.e("asdf", response.code() + "");
                        break;
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<KokData>> call, @NonNull Throwable t) {
                Log.d("checkonthe", "error");
            }
        });
    }

    //콕을 서버에서 부터 지우는 리스폰스를 보낸다.
    public void deletekokfromserver (String kokid) {
        Retrofit client = new Retrofit.Builder().baseUrl(RetrofitExService.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        RetrofitExService service = client.create(RetrofitExService.class);
        Call<List<KokData>> call = service.deletemyPick(kokid);
        call.enqueue(new Callback<List<KokData>>() {
            @Override
            public void onResponse(@NonNull Call<List<KokData>> call, @NonNull retrofit2.Response<List<KokData>> response) {
                switch (response.code()) {
                    case 200:
                        break;
                    case 409:
                        Toast.makeText(ProfileActivity.this, "에러가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                    default:
                        Log.e("asdf", response.code() + "");
                        break;
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<KokData>> call, @NonNull Throwable t) {
                Log.d("checkonthe", "error");
            }
        });
    }

    //당겨서 리프레쉬
    @Override
    public void onRefresh() {
        items.clear();
        mAdapter.notifyDataSetChanged();
        getkokfromserver();
    }

    public class RecyclerAdapter2 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<KokItem> items;

        public RecyclerAdapter2(List<KokItem> items) {
            this.items = items;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.mylist, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            MyViewHolder myViewHolder = (MyViewHolder) holder;
            myViewHolder.koktext.setText(items.get(position).koktext);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView koktext;

        MyViewHolder(View view) {
            super(view);
            koktext = view.findViewById(R.id.title2);
        }
    }
}
