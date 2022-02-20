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

public class GoRESTApiTests {

    // создаём переменные для хранения ID, name, email созданного пользователя.
    public String createdUserID;
    public String createdUserName;
    public String createdUserEmail;

    final String baseUri = "https://gorest.co.in/public/v1/users";
    final String bearerToken = "cdb7fd7c3dc25b7a540c907ee82397782303b23495b86632c91485e296717af0";

    // сохраняем текущую дату и время в переменную в нужном формате для добавления её в email
    Date dateNow = new Date();
    SimpleDateFormat formatForDateNow = new SimpleDateFormat("MM.dd.hh.mm.ss");
    private final String currentDate = formatForDateNow.format(dateNow);

    // создаём объект класса POJO.UserData для сериализации в json
    UserData userData = new UserData();

    // POST запрос создание пользователя
    @Test(priority = 1, groups={"GoREST"})
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
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        // делаем проверки
        assertEquals(responsePost.statusCode(), 201);
        assertEquals(responsePost.jsonPath().getString("data.email"), userData.getEmail());

        // записываем данные созданного пользователя в переменные
        createdUserID = responsePost.jsonPath().getString("data.id");
        createdUserName = responsePost.jsonPath().getString("data.name");
        createdUserEmail = responsePost.jsonPath().getString("data.email");
   }


    // Проверяем созданного пользователя GET запросом
    @Test(priority = 2, groups={"GoREST"})
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
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        // делаем проверки
        assertEquals(responseGet.statusCode(), 200);
        assertEquals(responseGet.jsonPath().getString("data.email"), userData.getEmail());
    }


    // PUT запрос обновление пользователя
    @Test(priority = 3, groups={"GoREST"})
    private void putUpdateUser() {

        userData.setName(createdUserName + "_PUTname");
        userData.setGender(UserData.GENDER_TYPE.female);

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
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        // делаем проверки
        assertEquals(responsePut.statusCode(), 200);
        assertEquals(responsePut.jsonPath().getString("data.name"), userData.getName());
        assertEquals(responsePut.jsonPath().getString("data.gender"), userData.getGender().toString());

    }

    // Запрос на удаление пользователя
    @Test(priority = 4, groups={"GoREST"})
    private void deleteUser() {

        // делаем GET запрос
        Response responseDelete = given()
                .headers(
                        "Authorization",
                        "Bearer " + bearerToken)
                .baseUri(baseUri)
                .contentType(ContentType.JSON)
                .when()
                .delete("/" + createdUserID)
                .then()
                .extract().response();

        System.out.println("User was deleted");



        // делаем проверки
        assertEquals(responseDelete.statusCode(), 204);

    }


    // Проверяем удалился ли пользователь
    @Test(priority = 5, groups={"GoREST"})
    private void getDeleteUser() {

        // делаем GET запрос
        Response responsegetDeleteUser = given()
                .headers(
                        "Authorization",
                        "Bearer " + bearerToken)
                .baseUri(baseUri)
                .contentType(ContentType.JSON)
                .when()
                .get("/" + createdUserID)
                .then()
                .extract().response();


        // делаем проверки
        assertEquals(responsegetDeleteUser.statusCode(), 404);

    }



}
