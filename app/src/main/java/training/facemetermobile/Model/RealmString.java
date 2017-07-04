package training.facemetermobile.Model;

import io.realm.RealmObject;

/**
 * Created by agrfiqie6136 on 26/10/2016.
 */

public class RealmString extends RealmObject {
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
