package neolabs.kok.data;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class KokData {

    @SerializedName("location")
    @Expose
    private Location location;

    @SerializedName("_id")
    @Expose
    private String id;

    @SerializedName("comments")
    @Expose
    private List<Comment> comments = null;

    @SerializedName("userauthid")
    @Expose
    private String userauthid;

    @SerializedName("usernickname")
    @Expose
    private String usernickname;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("profileimage")
    @Expose
    private String profileimage;

    @SerializedName("__v")
    @Expose
    private Integer v;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public String getUserauthid() {
        return userauthid;
    }

    public void setUserauthid(String userauthid) {
        this.userauthid = userauthid;
    }

    public String getUsernickname() {
        return usernickname;
    }

    public void setUsernickname(String usernickname) {
        this.usernickname = usernickname;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getV() {
        return v;
    }

    public void setV(Integer v) {
        this.v = v;
    }

}