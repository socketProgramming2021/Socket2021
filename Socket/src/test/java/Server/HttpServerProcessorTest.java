package Server;

import Http.HttpMessage;
import Server.HttpServerProcessor;
import org.junit.Before;
import org.junit.Test;
import po.User;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class HttpServerProcessorTest {

    ArrayList<User> users=new ArrayList<>();

    HttpServerProcessor httpServerProcessor;

    @Before
    public void setup(){
        users.add(new User("admin","123456"));
        httpServerProcessor=new HttpServerProcessor(users);
    }
    @Test
    public void resolve() {
        HttpMessage httpMessage=new HttpMessage();
        LinkedHashMap<String,String> line = httpMessage.getLine();
        LinkedHashMap<String,String> header = httpMessage.getHeaders();
        line.put("Method","POST");
        header.put("Cookie","0");
        line.put("URL","/files/ball.png");
        httpMessage.setBody("");
        HttpMessage result=httpServerProcessor.resolve(httpMessage);
        int a=0;
    }


    @org.junit.Test
    public void response() {
    }
}