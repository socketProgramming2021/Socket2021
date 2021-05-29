package Server;

import Http.HttpMessage;
import Http.StatusCode;
import po.User;
import util.JSONHelper;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class HttpServerProcessor {

    private ArrayList<User> userList; // 注册用户表

    public HttpServerProcessor(ArrayList<User> userList){
        this.userList = userList;
    }

    /**
     * 处理客户端发送的httpRequest
     * @param httpRequest
     * line:请求报文头
     *      Method:GET/POST
     *      URL:/login  登录接口
     *          /register  注册接口
     *          /files/..  文件存储区
     *      Version:HTTP/1.1
     *      ------
     *      响应报文
     *      Version:HTTP/1.1
     *      Code:状态码
     *      Code-Description:状态码描述
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
        HttpMessage httpResponse = new HttpMessage();

        if(isAuthorized(Integer.parseInt(httpRequest.getHeaders().get("Cookie")))){
            String URL = httpRequest.getLine().get("URL");
            switch (URL){
                case "/login":
                    System.out.println("用户请求登录服务");
                    httpResponse = login(httpRequest);
                    break;
                case "/register":
                    System.out.println("用户请求注册服务");
                    httpResponse = register(httpRequest);
                    break;
                default:
                    break;

            }
        }
        else{
            //未登录
            LinkedHashMap<String,String> headers = new LinkedHashMap<>();
            String body = "未登录";
            //设置头部
            setHttpResponseLine(httpResponse, StatusCode.UNAUTHORIZED);
            httpResponse.setHeaders(headers);
            httpResponse.setBody(body);
        }
        return httpResponse;
    }



    /**
     * 通过cookie检验是否已登录
     * cookie采用用户序号来简化
     * @param cookie
     * @return
     */
    private boolean isAuthorized(Integer cookie){
        return cookie < userList.size() && cookie >= 0;
    }

    /**
     * 传入StatusCode,设置返回报文头部
     * @param httpResponse
     * @param statusCode
     */
    private void setHttpResponseLine(HttpMessage httpResponse, StatusCode statusCode){
        LinkedHashMap<String,String> line = httpResponse.getLine();
        line.put("Version","HTTP/1.1");
        line.put("Code", String.valueOf(statusCode.code));
        line.put("Code-Description", statusCode.description);
    }

    /**
     * 设置返回报文默认headers
     * @param httpResponse
     *
     */
    private void setHttpResponseDefaultHeaders(HttpMessage httpResponse){
        LinkedHashMap<String,String> headers = httpResponse.getHeaders();
        headers.put("Content-Type", "text/plain");
        headers.put("Content-Length","0");
        headers.put("Accept","*/*");
        headers.put("Cookie", "-1");
    }
    /**
     * 登录
     * @param httpRequest
     * @return
     */
    private HttpMessage login(HttpMessage httpRequest){
        boolean isSuccess = false;
        HttpMessage httpResponse = new HttpMessage();
        //设置默认headers
        setHttpResponseDefaultHeaders(httpResponse);
        LinkedHashMap<String,String> headers = httpResponse.getHeaders();
        String body = httpResponse.getBody();
        User user = new User((String)JSONHelper.getByProperty(httpRequest.getBody(), "username"),
                (String)JSONHelper.getByProperty(httpRequest.getBody(), "password"));
        for(int id = 0; id < userList.size(); id++){
            if(userList.get(id).equals(user)){
                isSuccess = true;
                headers.put("Cookie", String.valueOf(id));
                break;
            }
        }
        //设置头部
        setHttpResponseLine(httpResponse, isSuccess?StatusCode.SUCCESS:StatusCode.UNAUTHORIZED);
        body = isSuccess?"登录成功":"登录失败，用户名或密码错误";
        return httpResponse;
    }

    /**
     * 注册
     */
    private HttpMessage register(HttpMessage httpRequest){
        return null;
    }
}
