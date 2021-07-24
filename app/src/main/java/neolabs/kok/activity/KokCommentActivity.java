package neolabs.kok.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.Settings;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import neolabs.kok.R;
import neolabs.kok.data.Comment;
import neolabs.kok.data.Data;
import neolabs.kok.data.KokData;
import neolabs.kok.item.KokCommentItem;
import neolabs.kok.retrofit.RetrofitExService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class KokCommentActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener  {

    RecyclerView recyclerView;
    TextView newnickname;
    TextView kokcomment;
    EditText commentview;
    Button sendbuttonview;
    String kokid;
    Intent intent;
    String myselfauthid;
    String myselfnickname;

    ImageView profileImage;
    String profileImagelink;

    RecyclerAdapter mAdapter;
    List<String> commentsid = new ArrayList<>();

    List<KokCommentItem> items = new ArrayList<>();

    SwipeRefreshLayout mSwipeRefreshLayout;

    static Activity activity;

    //외부에서 Activity 끝내기
    public static void finishThis() {
        if (activity != null) activity.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kok_comment);

        intent = getIntent();

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        myselfauthid = pref.getString("userauthid",  null);
        myselfnickname = pref.getString("nickname", null);

        //무슨 콕인지 고유 번호를 받아서 불러온다.....
        newnickname = findViewById(R.id.textView2);
        kokcomment = findViewById(R.id.textView4);
        commentview = findViewById(R.id.editcommenttext);
        sendbuttonview = findViewById(R.id.commentsend);
        profileImage = findViewById(R.id.profile_image3);

        mSwipeRefreshLayout = findViewById(R.id.swipe_layout3);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        mSwipeRefreshLayout.setRefreshing(false);

        recyclerView = findViewById(R.id.commentrecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new RecyclerAdapter(items);

        getuserInfo(intent.getStringExtra("userauthid"));

        kokid = intent.getStringExtra("kokidarray");

        loadCommentFromServer();

        sendbuttonview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(commentview.getText().toString().equals("")) return;
                Retrofit client = new Retrofit.Builder().baseUrl(RetrofitExService.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
                RetrofitExService service = client.create(RetrofitExService.class);
                Call<KokData> call = service.addComment(kokid, commentview.getText().toString(), myselfauthid, myselfnickname);
                call.enqueue(new Callback<KokData>() {
                    @Override
                    public void onResponse(@NonNull Call<KokData> call, @NonNull retrofit2.Response<KokData> response) {
                        switch (response.code()) {
                            case 200:
                                items.clear();
                                final List<Comment> comments = response.body().getComments();
                                for(int i = 0; i < comments.size(); i++) {
                                    final int low = i;
                                    if (comments.get(low).getAuthorauthid().equals(myselfauthid)) {
                                        items.add(new KokCommentItem(comments.get(low).getContents(), comments.get(low).getAuthorauthid(), true));
                                    } else {
                                        items.add(new KokCommentItem(comments.get(low).getContents(), comments.get(low).getAuthorauthid(), false));
                                    }
                                    commentsid.add(comments.get(low).getId());
                                }

                                mAdapter.notifyDataSetChanged();
                                commentview.setText("");
                                Toast.makeText(KokCommentActivity.this, "댓글이 정상적으로 등록되었습니다.", Toast.LENGTH_SHORT).show();
                                break;
                            case 409:
                                Toast.makeText(KokCommentActivity.this, "에러가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Log.e("asdf", response.code() + "");
                                break;
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<KokData> call, @NonNull Throwable t) {
                        Log.d("checkonthe", "error");
                    }
                });
            }
        });
    }

    @Override
    public void onRefresh() {
        loadCommentFromServer();
    }

    public void loadCommentFromServer() {
        Retrofit client = new Retrofit.Builder().baseUrl(RetrofitExService.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        RetrofitExService service = client.create(RetrofitExService.class);
        Call<KokData> call = service.getComment(kokid);
        call.enqueue(new Callback<KokData>() {
            @Override
            public void onResponse(@NonNull Call<KokData> call, @NonNull Response<KokData> response) {
                switch (response.code()) {
                    case 200:
                        items.clear();
                        //mAdapter.notifyDataSetChanged();
                        final List<Comment> comments = response.body().getComments();
                        for(int i = 0; i < comments.size(); i++) {
                            final int low = i;
                            if (comments.get(low).getAuthorauthid().equals(myselfauthid)) {
                                items.add(new KokCommentItem(comments.get(low).getContents(), comments.get(low).getAuthorauthid(),  true));
                            } else {
                                items.add(new KokCommentItem(comments.get(low).getContents(), comments.get(low).getAuthorauthid(), false));
                            }
                            commentsid.add(comments.get(low).getId());
                        }
                        recyclerView.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                        mSwipeRefreshLayout.setRefreshing(false);
                        break;
                    case 409:
                        Toast.makeText(KokCommentActivity.this, "에러가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Log.e("asdf", response.code() + "");
                        break;
                }
            }

            @Override
            public void onFailure(@NonNull Call<KokData> call, @NonNull Throwable t) {
                Log.d("checkonthe", "error");
            }
        });
    }

    private interface getCommentUserCallback {
        void setUserInfo(String[] userinfo);
    }

    public void getCommentUserInfo(String userauth, final getCommentUserCallback callback) {
        final String[] userinfo = new String[2];
        Retrofit client = new Retrofit.Builder().baseUrl(RetrofitExService.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        RetrofitExService service = client.create(RetrofitExService.class);
        Call<Data> call = service.getuserInfo(userauth);
        call.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(@NonNull Call<Data> call, @NonNull retrofit2.Response<Data> response) {
                switch (response.code()) {
                    case 200:
                        userinfo[0] = response.body().getNickname();
                        userinfo[1] = response.body().getProfileimage();
                        callback.setUserInfo(userinfo);
                        break;
                    case 409:
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailure(@NonNull Call<Data> call, @NonNull Throwable t) {
                Log.d("checkonthe", "error");
            }
        });
    }

    public void getuserInfo(String userauthid) {
        Retrofit client = new Retrofit.Builder().baseUrl(RetrofitExService.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        RetrofitExService service = client.create(RetrofitExService.class);
        Call<Data> call = service.getuserInfo(userauthid);
        call.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(@NonNull Call<Data> call, @NonNull retrofit2.Response<Data> response) {
                switch (response.code()) {
                    case 200:
                        newnickname.setText(response.body().getNickname());
                        profileImagelink = response.body().getProfileimage();
                        kokcomment.setText(intent.getStringExtra("kokcomment"));
                        if(profileImagelink.equals("default")) {
                            profileImage.setImageResource(R.drawable.icon);
                        } else {
                            Glide.with(KokCommentActivity.this)
                                    .load(RetrofitExService.BASE_URL + "images/" + profileImagelink)
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(profileImage);
                        }
                        break;
                    case 409:
                        Toast.makeText(KokCommentActivity.this, "에러2가 발생하였습니다.", Toast.LENGTH_SHORT).show();
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

    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<KokCommentItem> items;

        public RecyclerAdapter(List<KokCommentItem> items) {
            this.items = items;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.commentlist, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            final MyViewHolder myViewHolder = (MyViewHolder) holder;
            getCommentUserInfo(items.get(position).kokuserauthid, new getCommentUserCallback() {
                @Override
                public void setUserInfo(String[] userinfo) {
                    if(userinfo[1].equals("default")) {
                        myViewHolder.profileImage.setImageResource(R.drawable.icon);
                    } else {
                        Glide.with(KokCommentActivity.this)
                                .load(RetrofitExService.BASE_URL + "images/" + userinfo[1])
                                .apply(RequestOptions.circleCropTransform())
                                .into(myViewHolder.profileImage);
                    }
                    myViewHolder.kokuser.setText(userinfo[0]);
                    myViewHolder.koktext.setText(items.get(position).koktext);

                    myViewHolder.deletebuttona.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder alt_bld = new AlertDialog.Builder(KokCommentActivity.this);
                            alt_bld.setMessage("정말로 댓글을 삭제하시겠습니까?").setCancelable(
                                    false).setPositiveButton("네",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // 네 클릭
                                            Retrofit client = new Retrofit.Builder().baseUrl(RetrofitExService.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
                                            RetrofitExService service = client.create(RetrofitExService.class);
                                            Call<KokData> call = service.deleteComment(kokid, commentsid.get(position));
                                            call.enqueue(new Callback<KokData>() {
                                                @Override
                                                public void onResponse(@NonNull Call<KokData> call, @NonNull Response<KokData> response) {
                                                    switch (response.code()) {
                                                        case 200:
                                                            Toast.makeText(KokCommentActivity.this, "댓글이 정상적으로 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                                            break;
                                                        case 409:
                                                            Toast.makeText(KokCommentActivity.this, "에러가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                                                            break;
                                                        default:
                                                            Log.e("asdf", response.code() + "");
                                                            break;
                                                    }
                                                }

                                                @Override
                                                public void onFailure(@NonNull Call<KokData> call, @NonNull Throwable t) {
                                                    Log.d("checkonthe", "error");
                                                }
                                            });
                                            items.remove(position);
                                            commentsid.remove(position);
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

                            // 대화창 배경 색 설정
                            //alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(255,62,79,92)));
                            alert.show();
                        }
                    });
                    if(!items.get(position).ismycomment) {
                        myViewHolder.deletebuttona.setVisibility(View.GONE);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView koktext;
        TextView kokuser;
        Button deletebuttona;

        MyViewHolder(View view) {
            super(view);
            profileImage = view.findViewById(R.id.feelimage2);
            koktext = view.findViewById(R.id.username3);
            kokuser = view.findViewById(R.id.introduce3);
            deletebuttona = view.findViewById(R.id.deletebutton);
        }
    }
}
