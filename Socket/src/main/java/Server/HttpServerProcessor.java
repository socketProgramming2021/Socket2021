package Server;

import Http.HttpMessage;
import po.user;

import java.util.ArrayList;

public class HttpServerProcessor {

    private ArrayList<user> userList; // 注册用户表

    public HttpServerProcessor(ArrayList<user> userList){
        this.userList = userList;
    }

    /**
     * 处理客户端发送的httpRequest
     * @param httpRequest
     * @return
     */
    public HttpMessage resolve(HttpMessage httpRequest){
        //todo
        System.out.println("处理客户端报文：" + httpRequest.toString());
        return new HttpMessage();
    }


}
