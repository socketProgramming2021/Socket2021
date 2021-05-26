package Client;

import Http.HttpMessage;

public class HttpClientProcessor {

    private Integer cookie;

    HttpClientProcessor(Integer cookie){
        this.cookie = cookie;
    }

    /**
     * 处理服务端返回的报文
     * @param httpResponse
     */
    public void resolve(HttpMessage httpResponse){
        //todo:客户端处理返回的报文
        System.out.println("返回报文：" + httpResponse.toString());
    }
}
