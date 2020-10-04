package mas;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;

import java.util.List;

public interface ApiI {

    public void  createTable(DynamoDB dynamoDB);
    public Item queryEmail(String email, DynamoDB dynamoDB);
    public List<String> queryName(String name, DynamoDB dynamoDB);
    public void editItem(String email,DynamoDB dynamoDB,String name,String contact,String address);
    public void create(String email,DynamoDB dynamoDB,String name,String contact,String address);
    public void del(String email,DynamoDB dynamoDB);

}
