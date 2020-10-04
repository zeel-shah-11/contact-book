package mas;

import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.*;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static mas.Constants.*;

public class Api implements ApiI{


    public void  createTable(DynamoDB dynamoDB)
    {
        List<AttributeDefinition> attributeDefinitions= new ArrayList<AttributeDefinition>();
        attributeDefinitions.add(new AttributeDefinition().withAttributeName(EMAILID).withAttributeType("S"));
        attributeDefinitions.add(new AttributeDefinition().withAttributeName(NAME).withAttributeType("S"));

        List<KeySchemaElement> keySchema = new ArrayList<KeySchemaElement>();
        keySchema.add(new KeySchemaElement().withAttributeName(EMAILID).withKeyType(KeyType.HASH));

        GlobalSecondaryIndex nameIndex = new GlobalSecondaryIndex()
                .withIndexName(GSI_NAME)
                .withProvisionedThroughput(new ProvisionedThroughput()
                        .withReadCapacityUnits((long) 10)
                        .withWriteCapacityUnits((long) 1))
                .withProjection(new Projection().withProjectionType(ProjectionType.ALL));

        ArrayList<KeySchemaElement> indexKeySchema = new ArrayList<KeySchemaElement>();

        indexKeySchema.add(new KeySchemaElement()
                .withAttributeName(NAME)
                .withKeyType(KeyType.HASH));  //Partition key


        nameIndex.setKeySchema(indexKeySchema);


        CreateTableRequest createTableRequest = new CreateTableRequest()
                .withTableName(TABLE)
                .withProvisionedThroughput(new ProvisionedThroughput()
                        .withReadCapacityUnits((long) 5)
                        .withWriteCapacityUnits((long) 1))
                .withAttributeDefinitions(attributeDefinitions)
                .withKeySchema(keySchema)
                .withGlobalSecondaryIndexes(nameIndex);

        Table table = dynamoDB.createTable(createTableRequest);

        try {
            loadJson(dynamoDB);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void  loadJson(DynamoDB dynamoDB) throws IOException {
        Table table = dynamoDB.getTable(TABLE);

        JsonParser parser = new JsonFactory().createParser(new File(System.getProperty(URL)));

        JsonNode rootNode = new ObjectMapper().readTree(parser);
        Iterator<JsonNode> iter = rootNode.iterator();

        ObjectNode currentNode;

        while (iter.hasNext()) {
            currentNode = (ObjectNode) iter.next();

            String email = currentNode.path(EMAILID).asText();

            try {
                table.putItem(new Item().withPrimaryKey(EMAILID, email).withJSON(NAME,
                        currentNode.path(NAME).toString()).withJSON(INFO,
                        currentNode.path(INFO).toString()));
            }
            catch (Exception e) {
                System.err.println("Unable to add email: " + email);
                System.err.println(e.getMessage());
                break;
            }
        }
        parser.close();
    }

    public Item queryEmail(String email,DynamoDB dynamoDB)
    {
        Table table = dynamoDB.getTable(TABLE);
        GetItemSpec spec = new GetItemSpec().withPrimaryKey("email", email);

        try {
            Item outcome = table.getItem(spec);
            return outcome;
        }
        catch (Exception e) {
            System.err.println("Unable to read item: " + email);
            System.err.println(e.getMessage());
            return null;
        }
    }

    public List<String> queryName(String name,DynamoDB dynamoDB) {
        Table table = dynamoDB.getTable(TABLE);

        Index index = table.getIndex(GSI_NAME);
        ItemCollection<QueryOutcome> items = index.query(NAME, name);
        Iterator<Item> iter = items.iterator();
        List<String> outCome = new ArrayList<>();
        while (iter.hasNext()) {
            outCome.add(iter.next().toJSONPretty());

        }
        return outCome;
    }
       public void editItem(String email,DynamoDB dynamoDB,String name,String contact,String address)
        {
            Table table = dynamoDB.getTable(TABLE);
            GetItemSpec spec = new GetItemSpec().withPrimaryKey(EMAILID, email);

            UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey(EMAILID, email)
                    .withUpdateExpression("set nam=:n,info.contactNo=:c,info.address=:a")
                    .withValueMap(new ValueMap().withString(":n", name).withString(":c", contact).withString(":a", address));

            try {
                System.out.println("Updating the item...");
                UpdateItemOutcome out = table.updateItem(updateItemSpec);
                Item outcome = table.getItem(spec);
            }
            catch (Exception e) {
               System.err.println("Unable to update item: " + email);
                System.err.println(e.getMessage());
            }


        }

        public void create(String email,DynamoDB dynamoDB,String name,String contact,String address)
        {

            Table table = dynamoDB.getTable(TABLE);
            final Map<String, String> infoMap = new HashMap<String,String>();
            infoMap.put(CONTACTNO, contact);
            infoMap.put(ADDRESS, address);

            try {

                PutItemOutcome outcome = table
                        .putItem(new Item().withPrimaryKey(EMAILID, email).withString(NAME,name).withMap(INFO, infoMap));

            }
            catch (Exception e) {
                System.err.println("Unable to add item: " + email);
                System.err.println(e.getMessage());
            }

        }


        public void del(String email,DynamoDB dynamoDB)
        {
            Table table = dynamoDB.getTable(TABLE);
            DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
                    .withPrimaryKey(new PrimaryKey("email", email));

            try {
                table.deleteItem(deleteItemSpec);
            }
            catch (Exception e) {
                System.err.println("Unable to delete item: " + email);
                System.err.println(e.getMessage());
            }

        }

}
