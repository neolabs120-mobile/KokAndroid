package neolabs.kok.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Comment {

    @SerializedName("comment_date")
    @Expose
    private String commentDate;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("contents")
    @Expose
    private String contents;
    @SerializedName("authorauthid")
    @Expose
    private String authorauthid;
    @SerializedName("authorusernickname")
    @Expose

    private String authorusernickname;

    public String getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(String commentDate) {
        this.commentDate = commentDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getAuthorauthid() {
        return authorauthid;
    }

    public void setAuthorauthid(String authorauthid) {
        this.authorauthid = authorauthid;
    }

    public String getAuthorusernickname() {
        return authorusernickname;
    }

    public void setAuthorusernickname(String authorusernickname) {
        this.authorusernickname = authorusernickname;
    }
}
