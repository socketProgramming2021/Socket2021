package Client;

import Http.Cookie;
import Http.HttpMessage;
import Http.StatusCode;
import org.junit.Assert;
import org.junit.Test;

public class HttpClientProcessorTest {

    HttpClientProcessor httpClientProcessor = new HttpClientProcessor(new Cookie(0));

    @Test
    public void resolve_redirect_302(){
        //301与302处理相同，均为重定向
        HttpMessage httpMessage = new HttpMessage();
        httpMessage.getLine().put("Version", "HTTP/1.1");
        httpMessage.getLine().put("Code", "302");
        httpMessage.getLine().put("Code_Description", StatusCode.MOVED_TEMPORARILY.description);

        httpMessage.getHeaders().put("Server", "server");
        httpMessage.getHeaders().put("Connection","Keep-Alive");
        httpMessage.getHeaders().put("Location", "/files/newFiles/302.png");//重新以此为url发送get请求
        httpMessage.getHeaders().put("Content-Type", "text/plain");
        httpMessage.getHeaders().put("Content-Length","0");
        httpMessage.getHeaders().put("Accept","*/*");
        httpMessage.getHeaders().put("Cookie","-1");

        HttpMessage httpMessage_redirect = httpClientProcessor.resolve(httpMessage);

        String URL = httpMessage_redirect.getLine().get("URL");

        Assert.assertEquals("/files/newFiles/302.png", URL);

    }

}
