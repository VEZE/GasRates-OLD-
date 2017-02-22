package wiksinc.currencyrates.ListsUtils;

/**
 * Created by Ilya on 15.12.2016.
 */

public class FavoriteObject {
    String name;
    Boolean isChecked;

    public FavoriteObject(String name, Boolean isChecked)
    {
        this.name = name;
        this.isChecked = isChecked;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName() {return name;}

    public void setIsChecked(Boolean isChecked)
    {
        this.isChecked = isChecked;
    }

    public Boolean IsChecked()
    {
        return isChecked;
    }
}
