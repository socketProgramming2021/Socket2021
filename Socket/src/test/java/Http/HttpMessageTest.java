package Http;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

import java.util.LinkedHashMap;

public class HttpMessageTest {

    HttpMessage httpRequestTest1;
    HttpMessage httpResponseTest1;

    @Before
    public void setHttpMessageTest(){
        //httpRequestTest1
        httpRequestTest1 = new HttpMessage();

        httpRequestTest1.getLine().put("Method","POST");
        httpRequestTest1.getLine().put("URL","/login");
        httpRequestTest1.getLine().put("Version","HTTP/1.1");

        httpRequestTest1.getHeaders().put("Host","127.0.0.1");
        httpRequestTest1.getHeaders().put("Connection","Keep-Alive");
        httpRequestTest1.getHeaders().put("Cookie","-1");

        httpRequestTest1.setBody("{\"username\":\"admin\",\"password\":\"123456\"}");
        //httpResponseTest1
        httpResponseTest1 = new HttpMessage();

        httpResponseTest1.getLine().put("Version","HTTP/1.1");
        httpResponseTest1.getLine().put("Code","200");
        httpResponseTest1.getLine().put("Code-Description",StatusCode.SUCCESS.description);

        httpResponseTest1.getHeaders().put("Content-Type","text/plain");
        httpResponseTest1.getHeaders().put("Content-length","12");
        httpResponseTest1.getHeaders().put("Cookie","-1");

        httpResponseTest1.setBody("Hello World!");
    }


    @Test
    public void toStringTest1(){
        //请求报文
        String expected = "POST /login HTTP/1.1" +
                "\n" +
                "Host: 127.0.0.1\n" +
                "Connection: Keep-Alive\n" +
                "Cookie: -1\n" +
                "\n" +
                "{\"username\":\"admin\",\"password\":\"123456\"}";
        Assert.assertEquals(expected, httpRequestTest1.toString());
    }

    @Test
    public void toStringTest2(){
        //响应报文
        String expected = "HTTP/1.1 200 OK" +
                "\n" +
                "Content-Type: text/plain\n" +
                "Content-length: 12\n" +
                "Cookie: -1\n" +
                "\n" +
                "Hello World!";
        Assert.assertEquals(expected, httpResponseTest1.toString());
    }

    @Test
    public void stringToHttpMessageTest1(){
        //请求报文
        String temp = httpRequestTest1.toString();
        HttpMessage httpMessage = HttpMessage.stringToHttpMessage(temp);
        Assert.assertTrue(Equals(httpMessage, httpRequestTest1));
    }

    @Test
    public void stringToHttpMessageTest2(){
        //响应报文
        String temp = httpResponseTest1.toString();
        HttpMessage httpMessage = HttpMessage.stringToHttpMessage(temp);
        Assert.assertTrue(Equals(httpMessage, httpResponseTest1));
    }

    public Boolean Equals(HttpMessage h1, HttpMessage h2){

        return h1.getLine().equals(h2.getLine()) &&
                h1.getHeaders().equals(h2.getHeaders()) &&
                h1.getBody().equals(h2.getBody());
    }

}
