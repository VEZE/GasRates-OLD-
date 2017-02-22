package wiksinc.currencyrates.MapModule;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class GooglePlace  implements ClusterItem {

    private String name;
    private String category;
    private String address;
    private Double lat;
    private Double lng;
    private String open;

    public GooglePlace() {
        this.name = "";
        this.address = "";
        this.lat = 0.0;
        this.lng = 0.0;
        this.open = "";
        this.category = "";
    }

    public GooglePlace(String name,String category,String address,String open,Double lat,Double lng) {
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.open = open;
        this.category = category;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLat() {
        return lat;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Double getLng() {
        return lng;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setOpenNow(String open) {
        this.open = open;
    }

    public String getOpenNow() {
        return open;
    }

    public boolean equals(GooglePlace o) {
        if (o == null)
            return false;
        if (o == this)
            return true;
        return this.getLat().equals(o.getLat()) && this.getLng().equals(o.getLng());
    }

    @Override
    public boolean equals(Object o) {
        return !(o == null || !(o instanceof GooglePlace)) && (o == this || equals((GooglePlace) o));
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(31, 17).
                        append(name).
                        append(category).
                        append(address).
                        append(lat).
                        append(lng).
                        append(open).
                        toHashCode();
    }

    @Override
    public LatLng getPosition() {
        return new LatLng(lat,lng);
    }
}
