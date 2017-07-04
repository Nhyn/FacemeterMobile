package training.facemetermobile.Model;

/**
 * Created by agrfiqie6136 on 04/10/2016.
 */

public class Person  {

    private String name;
    private String email;
    private String age;
    private String weight;
    private String height;
    private String description;
    private double latitude;
    private double longitude;

    public Person() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.name = email;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getHeight() { return height;}

    public void setHeight(String height) {
        this.height = height;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) { this.latitude = latitude;}

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) { this.longitude = longitude;}
}
