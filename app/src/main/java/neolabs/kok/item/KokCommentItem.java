package neolabs.kok.item;

public class KokCommentItem {
    public String koktext;
    public String kokuserauthid;
    public Boolean ismycomment;

    public KokCommentItem(String koktext, String kokuserauthid, Boolean ismycomment) {
        this.koktext = koktext;
        this.kokuserauthid = kokuserauthid;
        this.ismycomment = ismycomment;
    }
}
