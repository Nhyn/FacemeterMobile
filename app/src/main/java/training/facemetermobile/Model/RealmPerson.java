package training.facemetermobile.Model;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * Created by agrfiqie6136 on 27/10/2016.
 */

public class RealmPerson
        extends RealmObject {
    @PrimaryKey
    private String nameRealm;


    @Index
    private String ageRealm;
    private String weightRealm;
    private String heightRealm;
    private String descriptionRealm;


    public String getNameRealm() {
        return nameRealm;
    }

    public void setNameRealm(String nameRealm) {
        this.nameRealm = nameRealm;
    }

    public String getAgeRealm() {
        return ageRealm;
    }

    public void setAgeRealm(String ageRealm) {
        this.ageRealm = ageRealm;
    }

    public String getWeightRealm() {
        return weightRealm;
    }

    public void setWeightRealm(String weightRealm) {
        this.weightRealm = weightRealm;
    }

    public String getHeightRealm() { return heightRealm;}

    public void setHeightRealm(String heightRealm) {
        this.heightRealm = heightRealm;
    }

    public String getDescriptionRealm() {
        return descriptionRealm;
    }

    public void setDescriptionRealm(String descriptionRealm) {
        this.descriptionRealm = descriptionRealm;
    }
}
