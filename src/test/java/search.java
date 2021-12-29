import com.oracle.javafx.jmx.json.JSONException;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.runner.Request;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.testng.Assert.*;

public class search {
    String baseUrl="https://public.trendyol.com/discovery-web-searchgw-service/v2/api/";
    private static List<OilList> _allOilCount;
    @Test(priority=1)

    public void searchParamsSingle() {
        given()
                .accept(ContentType.JSON)
                .param("q","ayçiçek%20yağı")
                .when()
                .get("https://www.trendyol.com/sr")
                .then()
                .log()
                .body()
                .statusCode(HttpStatus.SC_OK); // status200 dönmesi beklenir


    }

    @Test(priority=2)

    public void searchParamsMultiple() {
        RestAssured.baseURI = "https://www.trendyol.com/";
        RequestSpecification request = given();

        Response response = request
                .accept(ContentType.JSON)
                .queryParam("q", "ayçiçek%20yağı")
                .queryParam("q", "5%20Lt") // Searchbox a ayçiçek yağı 5lt yazdım yaz
                .queryParam("lc","103982")// lc >> filtreden : SıvıYağ işaretli olanları
                .queryParam("os", "3") // Sayfa sayısını ifade eder kaçıncı sayfa çağrılacaksa ; sayfada belli sayıda gösteriliyor her scroll edildiğinde sayfa bir diğer sayfadaki datalar listeleniyor
                .when()
                .get("/sr")
                .then()
                .statusCode(200) // 200 status dönmesi beklenir dönmez ise hata basar
                .extract()
                .response();
        response.prettyPrint(); //sonuç response yazdırılır

        String jsonBody = response.getBody().asString();
        String jsonString = response.asString();
        System.out.println(response.getStatusCode());
        assertEquals(jsonString.contains("Sıvı Yağ"), true);// response da Sıvı Yap var mı yok mu kontrolü
        assertEquals(jsonBody.contains("sivi-yagla"),true); //response da  sivi-yagla var mı yok mu kontrolü
    }

    @Test(priority=3)

    public void searchNotResult() {
        RestAssured.baseURI = "https://www.trendyol.com/";
        RequestSpecification request = given();

        Response response = request
                .accept(ContentType.JSON)
                .queryParam("q", "asdasfasfdewfgwwefwefewfe")
                .when()
                .get("/sr")
                .then()
                .statusCode(200)
                .extract()
                .response();
        response.prettyPrint();

        String jsonBody = response.getBody().asString();

        String jsonString = response.asString();
        System.out.println(response.getStatusCode());
        assertEquals(jsonString.contains("200"), true);
        assertEquals(jsonString.contains("false"), true);

    }

    @Test(priority=4)

    public void searchApiUrl() {

        RequestSpecification request = given();
        request.given()
                .accept(ContentType.JSON)
                .param("q","ayçiçek%20yağı")
                .when()
                .get(baseUrl+"aggregations/sr?q=ay%C3%A7i%C3%A7ek%20ya%C4%9F%C4%B1&qt=ay%C3%A7i%C3%A7ek%20ya%C4%9F%C4%B1&st=ay%C3%A7i%C3%A7ek%20ya%C4%9F%C4%B1&os=1&culture=tr-TR&categoryRelevancyEnabled=false&priceAggregationType=DYNAMIC_GAUSS&searchTestTypeAbValue=A&initialSearchText=ay%C3%A7i%C3%A7ek+ya%C4%9F%C4%B1")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .log()
                .all()
                .body("isSuccess",equalTo(true));

        RequestSpecification requestSpecification = given();
        Response response = requestSpecification.accept(ContentType.JSON)
                .param("q","ayçiçek%20yağı")
                .when()
                .get(baseUrl+"aggregations/sr?q=ay%C3%A7i%C3%A7ek%20ya%C4%9F%C4%B1&qt=ay%C3%A7i%C3%A7ek%20ya%C4%9F%C4%B1&st=ay%C3%A7i%C3%A7ek%20ya%C4%9F%C4%B1&os=1&culture=tr-TR&categoryRelevancyEnabled=false&priceAggregationType=DYNAMIC_GAUSS&searchTestTypeAbValue=A&initialSearchText=ay%C3%A7i%C3%A7ek+ya%C4%9F%C4%B1");

        response.prettyPrint();
        JsonPath jsonPathEvaluator = response.jsonPath();
        List<OilList> allDeviceId = jsonPathEvaluator.getList("result.aggregations",OilList.class);
        _allOilCount=allDeviceId;
        System.out.println("Total Group Sayısı:" + _allOilCount.size());

        String jsonBody = response.getBody().asString();
        String jsonString = response.asString();
        System.out.println(response.getStatusCode());
        assertEquals(jsonString.contains("Sıvı Yağ"), true);// response da Sıvı Yap var mı yok mu kontrolü
        assertEquals(jsonBody.contains("sivi-yagla"),true); //response da  sivi-yagla var mı yok mu kontrolü


    }
}


