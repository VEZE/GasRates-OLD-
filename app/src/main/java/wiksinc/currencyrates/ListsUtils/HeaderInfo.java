package wiksinc.currencyrates.ListsUtils;

import java.util.ArrayList;

public class HeaderInfo {

    private String image  = "";
    private String name  = "";
    private Integer amount  = 0;
    private String transcription  = "";


    private ArrayList<DetailInfo> productList = new ArrayList<DetailInfo>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String name) {
        this.image = name;
    }

    public void setAmount (Integer amount) { this.amount = amount; }

    public Integer getAmount()
    {
        return amount;
    }

    public String getTranscription() {
        return transcription;
    }

    public void setTranscription(String transcription) {
        this.transcription = transcription;
    }

    public ArrayList<DetailInfo> getProductList() {
        return productList;
    }

    public void setProductList(ArrayList<DetailInfo> productList) {
        this.productList = productList;
    }

}