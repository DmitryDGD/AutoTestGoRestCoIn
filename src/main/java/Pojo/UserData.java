package Pojo;

import lombok.Data;

@Data
public class UserData {

    // создаём класс под JSON
    private String  name;
    private GENDER_TYPE gender;
    private String  email;
    private STATUS_TYPE status;

    public enum STATUS_TYPE {
        active, inactive
    }

    public enum GENDER_TYPE {
        male, female
    }

}
