import Pojo.UserData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertEquals;

public class UpdateUser {

    // получаем ID созданного юзера из CreateUser
    CreateUser createUser = new CreateUser();
    String createdUserID = createUser.getCreatedUserID();
    String createdUserName = createUser.getCreatedUserName();
    String createdUserEmail = createUser.getCreatedUserEmail();


    final String baseUri = "https://gorest.co.in/public/v1/users";
    final String bearerToken = "cc5a426bdba401ebf3cff34c9d2fee87bfeffb5b38e3fd7b4ede627ae9ea43cc";

    // создаём объект класса POJO.UserData для сериализации в json
    UserData userData = new UserData();


    // PUT запрос обновление пользователя
    @Test(priority = 1)
    private void putUpdateUser() {


        System.out.println("ID = " + createdUserID);
        System.out.println("Name = " + createdUserName);
        System.out.println("Email = " + createdUserEmail);

        // заполняем POJO.UserData
        userData.setName(createdUserName + "PUTname");
        userData.setGender(UserData.GENDER_TYPE.male);
        userData.setEmail(createdUserEmail);
        userData.setStatus(UserData.STATUS_TYPE.active);

        // создаем объект класса Gson и конвертируем наш POJO.UserData в Json
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonString = gson.toJson(userData);
        System.out.println("RequestPutUser: \n" + jsonString);

        // делаем PUT запрос на обновление пользователя
        Response responsePut = given()
                .headers(
                        "Authorization",
                        "Bearer " + bearerToken)
                .baseUri(baseUri)
                .contentType(ContentType.JSON)
                .body(jsonString)
                .when()
                .put("/" + createdUserID)
                .then()
                .extract().response();

        System.out.println("ResponsePutUser: \n" + responsePut.asString());

        // делаем проверки
        assertEquals(responsePut.statusCode(), 200);
        assertEquals(responsePut.jsonPath().getString("data.name"), userData.getName());



    }

}
