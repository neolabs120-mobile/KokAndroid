package neolabs.kok.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Profile {

    @SerializedName("fieldname")
    @Expose
    private String fieldname;
    @SerializedName("originalname")
    @Expose
    private String originalname;
    @SerializedName("encoding")
    @Expose
    private String encoding;
    @SerializedName("mimetype")
    @Expose
    private String mimetype;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("filename")
    @Expose
    private String filename;
    @SerializedName("metadata")
    @Expose
    private Object metadata;
    @SerializedName("bucketName")
    @Expose
    private String bucketName;
    @SerializedName("chunkSize")
    @Expose
    private Integer chunkSize;
    @SerializedName("size")
    @Expose
    private Integer size;
    @SerializedName("md5")
    @Expose
    private String md5;
    @SerializedName("uploadDate")
    @Expose
    private String uploadDate;
    @SerializedName("contentType")
    @Expose
    private String contentType;

    public String getFieldname() {
        return fieldname;
    }

    public void setFieldname(String fieldname) {
        this.fieldname = fieldname;
    }

    public String getOriginalname() {
        return originalname;
    }

    public void setOriginalname(String originalname) {
        this.originalname = originalname;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Object getMetadata() {
        return metadata;
    }

    public void setMetadata(Object metadata) {
        this.metadata = metadata;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public Integer getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(Integer chunkSize) {
        this.chunkSize = chunkSize;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
