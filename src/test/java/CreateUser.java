import Pojo.UserData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertEquals;

public class CreateUser {

    final String baseUri = "https://gorest.co.in/";
    final String bearerToken = "cc5a426bdba401ebf3cff34c9d2fee87bfeffb5b38e3fd7b4ede627ae9ea43cc";

    // сохраняем текущую дату и время в переменную в нужном формате для добавления её в emailK
    Date dateNow = new Date();
    SimpleDateFormat formatForDateNow = new SimpleDateFormat("MM.dd.hh.mm.ss");
    private String currentDate = formatForDateNow.format(dateNow);

    // создаём объект класса POJO.UserData для сериализации в json
    UserData userData = new UserData();

    @Test(priority = 1)
    public void PostCreateUser() {

        // заполняем POJO.OrderData
        userData.setName("Burunduk1");
        userData.setGender(UserData.GENDER_TYPE.male);
        userData.setEmail("burunduki" + currentDate + "@forest.com");
        userData.setStatus(UserData.STATUS_TYPE.active);

        // создаем объект класса Gson и конвертируем наш POJO.UserData в Json
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonString = gson.toJson(userData);
        System.out.println("RequestCreateOrderPOST: \n" + jsonString);

        // делаем POST запрос на создание ордера
        Response responsePost = given()
                .headers(
                        "Authorization",
                        "Bearer " + bearerToken)
                .baseUri(baseUri)
                .contentType(ContentType.JSON)
                .body(jsonString)
                .when()
                .post("/public/v1/users")
                .then()
                .extract().response();

        System.out.println("ResponseCreateUserPOST: \n" + responsePost.asString());

        // делаем проверки
        assertEquals(responsePost.statusCode(), 201);
        assertEquals(responsePost.jsonPath().getString("data.name"), userData.getName());

    }

}
