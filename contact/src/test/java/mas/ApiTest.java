package mas;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import io.swagger.v3.parser.util.ClasspathHelper;
import io.vertx.core.json.JsonObject;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/*import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;*/

public class ApiTest {

 private static String INPUT="input";
 private static String EXPECTED="expected";
 private static DynamoDB dynamoDB;
 private static JsonObject jsonObj;
 Api api = new Api();

 @BeforeClass
 public static <JSONObject> void setup() throws IOException{
     AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withEndpointConfiguration(
             new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "us-west-2"))
             .build();
     dynamoDB = new DynamoDB(client);
     jsonObj = new JsonObject(ClasspathHelper.loadFileFromClasspath(""));
 }
    @Test
    public void testQueryEmail_P()
    {
           final String testCaseName="testQueryEmail_P";
           final String testInput = jsonObj.getJsonObject(testCaseName).getString(INPUT);
           final String testExpected = jsonObj.getJsonObject(testCaseName).getString(EXPECTED);
           String result= api.queryEmail(testInput,dynamoDB).toString();
           assertEquals(result,testExpected);
    }

    @Test
    public void testQueryEmail_N()
    {
        final String testCaseName="testQueryEmail_P";
        final String testInput = jsonObj.getJsonObject(testCaseName).getString(INPUT);
        final String testExpected = jsonObj.getJsonObject(testCaseName).getString(EXPECTED);
        assertNull(api.queryEmail(testInput,dynamoDB));

    }





}
