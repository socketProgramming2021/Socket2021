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
     * line:Method:GET/POST
     *      URL:/login  登录接口
     *          /register  注册接口
     *          /files/..  文件存储区
     *      Version:HTTP/1.1
     * Headers:Host:主机IP
     *         Content-Type:text/html ： HTML格式
     *                      text/plain ：纯文本格式
     *                      image/png : png图片格式
     *         Content-Length:
     *         Accept:
     *         Cookie:用户Id
     * Body:若为登录或注册接口 {”username“:"admin", "password":"123456"}
     */
    public HttpMessage resolve(HttpMessage httpRequest){
        //todo 服务端处理客户端报文
        System.out.println("处理客户端报文：" + httpRequest.toString());




        return new HttpMessage();
    }


}
