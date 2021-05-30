package test;

import Http.HttpMessage;
import Server.HttpServerProcessor;
import org.junit.Before;
import po.User;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import static org.junit.Assert.*;

public class HttpServerProcessorTest {

    ArrayList<User> users=new ArrayList<>();

    HttpServerProcessor httpServerProcessor;

    @Before
    public void setup(){
        users.add(new User("1","1"));
        httpServerProcessor=new HttpServerProcessor(users);
    }
    @org.junit.Test
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