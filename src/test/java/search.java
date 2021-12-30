import com.oracle.javafx.jmx.json.JSONException;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.runner.Request;
import org.testng.Assert;
import org.testng.annotations.Test;
import sun.lwawt.macosx.CSystemTray;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.sessionId;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.testng.Assert.*;

public class search {
    String baseUrl="https://public.trendyol.com/discovery-web-searchgw-service/v2/api/";
    private static List<OilList> _allOilCount;
    @Test(priority=1,description = "Query parametresini tekli olarak kullanma işlemi")

    public void searchParamsSingle() {
        RequestSpecification httpRequest = RestAssured.given();
        Response response = httpRequest.accept(ContentType.JSON)
                .param("q","ayçiçek%20yağı")
                .when()
                .request(Method.GET, "https://www.trendyol.com/sr");
        System.out.println("Response Body is =>  " + response.prettyPrint());
        int statusCode = response.getStatusCode();
        // Assert that correct status code is returned.
        Assert.assertEquals(statusCode /*actual value*/, 200 /*expected value*/, "Correct status code returned");


       /* given()
                .accept(ContentType.JSON)
                .param("q","ayçiçek%20yağı")
                .when()
                .get("https://www.trendyol.com/sr")
                .then()
                .log()
                .body()
                .statusCode(HttpStatus.SC_OK); // status200 dönmesi beklenir*/


    }

    @Test(priority=2,description = "Query parametresini çoklu olarak kullanma işlemi")

    public void searchParamsMultiple() {
        RestAssured.baseURI = "https://www.trendyol.com/";
        RequestSpecification request = given();

        Response response = request
                .accept(ContentType.JSON)
                .queryParam("q", "ayçiçek+yağı")
                .queryParam("qt", "ayçiçek+yağı") // Searchbox a ayçiçek yağı 5lt yazdım yaz
                .queryParam("st", "ayçiçek+yağı")
                .queryParam("lc","103982")// lc >> filtreden : SıvıYağ işaretli olanları
                .queryParam("wb","110454")
                .queryParam("os", "1") // Sayfa sayısını ifade eder kaçıncı sayfa çağrılacaksa ; sayfada belli sayıda gösteriliyor her scroll edildiğinde sayfa bir diğer sayfadaki datalar listeleniyor
                .queryParam("pi", "3")
                .queryParam("prc", "60-125")
                .when()
                .get("/sr")
                .then()
                .statusCode(200) // 200 status dönmesi beklenir dönmez ise hata basar
                .extract()
                .response();
        response.body().asString(); //sonuç response yazdırılır

      //  String jsonBody = response.getBody().asString();
        String jsonString = response.asString();
        System.out.println(response.getStatusCode());
        assertEquals(jsonString.contains("Sıvı Yağ"), true);// response da Sıvı Yap var mı yok mu kontrolü
        assertEquals(jsonString.contains("Yudum"), true);
    }

    @Test(priority=3,description = "Başarısız arama işlemi")

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


        String jsonString = response.asString();
        System.out.println("Status Code" +response.getStatusCode());
        assertEquals(jsonString.contains("200"), true);

    }

    @Test(priority=4,description = "Query params olarak değil url ile api testi")

    public void searchApiUrl() {



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
        System.out.println("Status Code :" + " " +response.getStatusCode());
        assertEquals(jsonString.contains("Sıvı Yağ"), true,"Sıvı yağ arama sonucu hatalı");// response da Sıvı Yap var mı yok mu kontrolü
        assertEquals(jsonBody.contains("sivi-yaglar"),true,"Sıvı yağ arama sonucu hatalı"); //response da  sivi-yagla var mı yok mu kontrolü



    }


    @Test(priority=5,description = "Query parametresini ve Path ile çoklu olarak kullanma işlemi")

    public void searchParamsMultipleFiltre() {
        RestAssured.baseURI = "https://public.trendyol.com/discovery-web-searchgw-service/v2/api/filter/";
        RequestSpecification request = given();

        Response response = request
                .accept(ContentType.JSON)
                .queryParam("wb","108894")
                .queryParam("lc","103982")// lc >> filtreden : SıvıYağ işaretli olanları
                .queryParam("q", "ayçiçek yağı")
                .queryParam("qt", "ayçiçek yağı") // Searchbox a ayçiçek yağı 5lt yazdım yaz
                .queryParam("st", "ayçiçek yağı")
                .queryParam("prc", "30-50")
                .queryParam("os", "1") // Sayfa sayısını ifade eder kaçıncı sayfa çağrılacaksa ; sayfada belli sayıda gösteriliyor her scroll edildiğinde sayfa bir diğer sayfadaki datalar listeleniyor
                .when()
                .get("/sr")
                .then()
                .statusCode(200) // 200 status dönmesi beklenir dönmez ise hata basar
                .extract()
                .response();
        response.prettyPrint();
        String jsonString = response.asString();
        Integer countTotal = JsonPath.from(jsonString).get("result.totalCount");

        System.out.println("Status Code " + " " + response.getStatusCode());
        System.out.println("Ayçiçek yağı araması için " + " " + countTotal + " " + "sonuç listeleniyor");
        assertEquals(jsonString.contains("Migros"), true);
        assertEquals(jsonString.contains("Migros Ayçiçek Yağı 2 lt"), true);


    }

}


