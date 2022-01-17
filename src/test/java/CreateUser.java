import Pojo.UserData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import lombok.Getter;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertEquals;

public class CreateUser {

    final String baseUri = "https://gorest.co.in/public/v1/users";
    final String bearerToken = "cc5a426bdba401ebf3cff34c9d2fee87bfeffb5b38e3fd7b4ede627ae9ea43cc";

    // сохраняем текущую дату и время в переменную в нужном формате для добавления её в emailK
    Date dateNow = new Date();
    SimpleDateFormat formatForDateNow = new SimpleDateFormat("MM.dd.hh.mm.ss");
    private final String currentDate = formatForDateNow.format(dateNow);

    // создаём переменные для хранения ID, name, email созданного пользователя. (Для того чтобы сделать PUT запрос)
    @Getter public String createdUserID;
    @Getter public String createdUserName;
    @Getter public String createdUserEmail;




    // создаём объект класса POJO.UserData для сериализации в json
    UserData userData = new UserData();


    // POST запрос создание пользователя
    @Test(priority = 1)
    public void postCreateUser() {

        // заполняем POJO.UserData
        userData.setName("Burunduk");
        userData.setGender(UserData.GENDER_TYPE.male);
        userData.setEmail("burunduki" + currentDate + "@forest.com");
        userData.setStatus(UserData.STATUS_TYPE.active);

        // создаем объект класса Gson и конвертируем наш POJO.UserData в Json
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonString = gson.toJson(userData);
        System.out.println("RequestCreateUser: \n" + jsonString);

        // делаем POST запрос на создание пользователя
        Response responsePost = given()
                .headers(
                        "Authorization",
                        "Bearer " + bearerToken)
                .baseUri(baseUri)
                .contentType(ContentType.JSON)
                .body(jsonString)
                .when()
                .post("")
                .then()
                .extract().response();

        System.out.println("ResponseCreateUser: \n" + responsePost.asString());

        // делаем проверки
        assertEquals(responsePost.statusCode(), 201);
        assertEquals(responsePost.jsonPath().getString("data.email"), userData.getEmail());

        // записываем данные созданного пользователя в переменные
        createdUserID = responsePost.jsonPath().getString("data.id");
        createdUserName = responsePost.jsonPath().getString("data.name");
        createdUserEmail = responsePost.jsonPath().getString("data.email");

        System.out.println("createdUserID = " + createdUserID);
        System.out.println("createdUserName = " + createdUserName);
        System.out.println("createdUserEmail = " + createdUserEmail);






    }

    // Проверяем созданного пользователя GET запросом
    @Test(priority = 2)
    private void getUser() {

        // делаем GET запрос
        Response responseGet = given()
                .headers(
                        "Authorization",
                        "Bearer " + bearerToken)
                .baseUri(baseUri)
                .contentType(ContentType.JSON)
                .when()
                .get("/" + createdUserID)
                .then()
                .extract().response();

        System.out.println("ResponseGetUser: \n" + responseGet.asString());

        // делаем проверки
        assertEquals(responseGet.statusCode(), 200);
        assertEquals(responseGet.jsonPath().getString("data.email"), userData.getEmail());



    }



}
