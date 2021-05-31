package Client;
import Http.HttpMessage;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

public class TerminalTest {

    Terminal terminal = new Terminal(-1);

    @Test
    public void resolve_help(){
        HttpMessage httpRequest = terminal.resolve("help");
        Assert.assertNull(httpRequest);
    }

    @Test
    public void resolve_error1(){
        //命令错误
        HttpMessage httpRequest = terminal.resolve("hap");
        Assert.assertNull(httpRequest);
        httpRequest = terminal.resolve("legin admin 123456");
        Assert.assertNull(httpRequest);
    }

    @Test
    public void resolve_error2(){
        //参数个数错误,应当输出提示
        HttpMessage httpRequest = terminal.resolve("post /files/hello.txt");
        Assert.assertNull(httpRequest);
        httpRequest = terminal.resolve("post");
        Assert.assertNull(httpRequest);
        httpRequest = terminal.resolve("get /files");
        Assert.assertNull(httpRequest);
        httpRequest = terminal.resolve("get");
        Assert.assertNull(httpRequest);
    }

    @Test
    public void resolve_login(){
        HttpMessage httpRequest = terminal.resolve("login admin 123456");
        String expected_body = "{\"username\":\"admin\",\"password\":\"123456\"}";
        String expected_URL = "/login";
        String expected_Method = "POST";
        Assert.assertEquals(expected_body, httpRequest.getBody());
        Assert.assertEquals(expected_URL, httpRequest.getLine().get("URL"));
        Assert.assertEquals(expected_Method, httpRequest.getLine().get("Method"));
    }

    @Test
    public void resolve_register(){
        HttpMessage httpRequest = terminal.resolve("register admin 123456");
        String expected_body = "{\"username\":\"admin\",\"password\":\"123456\"}";
        String expected_URL = "/register";
        String expected_Method = "POST";

        Assert.assertEquals(expected_body, httpRequest.getBody());
        Assert.assertEquals(expected_URL, httpRequest.getLine().get("URL"));
        Assert.assertEquals(expected_Method, httpRequest.getLine().get("Method"));

    }

    @Test
    public void resolve_get(){
        HttpMessage httpRequest = terminal.resolve("get /files/world.txt");
        String expected_URL = "/files/world.txt";
        String expected_Method = "GET";
        String expected_Cookie = "-1";

        Assert.assertEquals(expected_URL, httpRequest.getLine().get("URL"));
        Assert.assertEquals(expected_Method, httpRequest.getLine().get("Method"));
        Assert.assertEquals(expected_Cookie, httpRequest.getHeaders().get("Cookie"));
    }

    @Test
    public void resolve_post(){
        HttpMessage httpRequest = terminal.resolve("post /files/world.txt files/hello1.html");
        String expected_body = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Title</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <h1>Hello No.1</h1>\n" +
                "</body>\n" +
                "</html>";
        String expected_URL = "/files/world.txt";
        String expected_Method = "POST";
        String expected_Cookie = "-1";

        Assert.assertEquals(expected_body, httpRequest.getBody());
        Assert.assertEquals(expected_URL, httpRequest.getLine().get("URL"));
        Assert.assertEquals(expected_Method, httpRequest.getLine().get("Method"));
        Assert.assertEquals(expected_Cookie, httpRequest.getHeaders().get("Cookie"));
    }
}


