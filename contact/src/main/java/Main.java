import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import mas.Api;
import mas.Constants;

import java.io.IOException;

public class Main {
    private static DynamoDB dynamoDB;
    public static void main(String[] args) throws IOException {

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withEndpointConfiguration(
                new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "us-west-2"))
                .build();
        dynamoDB = new DynamoDB(client);
        Api api = new Api();
        Table table = dynamoDB.getTable(Constants.TABLE);

        table.delete();


       api.createTable(dynamoDB);


       /* api.queryEmail("slaff@hotmail.com",dynamoDB);
        System.out.println(table.getDescription());
       //api.queryName("leakin",dynamoDB);
        System.out.println(table.getDescription());
        api.editItem("slaff@hotmail.com",dynamoDB,"abc","123","xyz");
        api.create("sl@hotmail.com",dynamoDB,"abc","123","xyz");
        api.del("wenzlaff@live.com",dynamoDB);

        api.queryName("leakin",dynamoDB);*/
    }

}
