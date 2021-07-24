package neolabs.kok.retrofit;

import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import neolabs.kok.data.Data;
import neolabs.kok.data.KokData;
import neolabs.kok.data.Profile;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface RetrofitExService {

    String BASE_URL = "https://kok1.herokuapp.com/";

    @GET("user/signup")
    Call<Data> signupUserInfo(@Query("email") String email, @Query("password") String password, @Query("gender") String gender, @Query("nickname") String nickname, @Query("introduce") String introduce, @Query("firebasetoken") String firebasetoken);

    @GET("edituserinfo")
    Call<Data> EditUserInfo(@Query("useremail") String useremail, @Query("password") String password, @Query("gender") String gender, @Query("nickname") String nickname, @Query("introduce") String introduce);

    @GET("user/signin")
    Call<Data> signinUserInfo(@Query("email") String email, @Query("password") String password);

    @GET("addpick")
    Call<Data> addPick(@Query("latitude") String latitude, @Query("longitude") String longitude, @Query("userauthid") String userauthid, @Query("message") String message, @Query("usernickname") String usernickname, @Query("profileimage") String profileimage);

    @GET("getpicknearby")
    Call<List<KokData>> getPick(@Query("latitude") String latitude, @Query("longitude") String longitude);

    @GET("modifyfirebasetoken")
    Call<Data> editToken(@Query("useremail") String useremail, @Query("firebasetoken") String firebasetoken);

    @GET("getpickmy")
    Call<List<KokData>> getmyPick(@Query("userauthid") String userauthid);

    @GET("getuserinfo")
    Call<Data> getuserInfo(@Query("useruid") String useruid);

    @GET("deletepickmy")
    Call<List<KokData>> deletemyPick(@Query("deleteuseruid") String deleteuseruid);

    @GET("addcomment")
    Call<KokData> addComment(@Query("userauthid") String userauthid, @Query("contents") String contents, @Query("authorauthid") String authorauthid, @Query("authorusernickname") String authorusernickname);

    @GET("deletecomment")
    Call<KokData> deleteComment(@Query("userauthid") String userauthid, @Query("idofcomment") String commentcontents);

    @GET("getcomments")
    Call<KokData> getComment(@Query("userauthid") String userauthid);

    @Multipart
    @POST("uploadprofileimage")
    Call<Profile> uploadProfile(@Part MultipartBody.Part file, @QueryMap HashMap<String, Object> param);

    //출처: http://falinrush.tistory.com/5 [형필 개발일지]
}