package Server;

import Http.HttpMessage;
import Http.StatusCode;
//import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import po.User;
import util.FileReader;
import util.JSONHelper;
import vo.FileVo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class HttpServerProcessor {

    private ArrayList<User> userList; // 注册用户表

    private static String path= System.getProperty("user.dir");

    private static String config_path=path + "/configure";

    private FileReader fileReader=new FileReader(path);

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
        HttpMessage httpResponse = new HttpMessage();
        Integer cookie = Integer.parseInt(httpRequest.getHeaders().get("Cookie"));
        String RequestMethod=httpRequest.getLine().get("Method");
        String URL = httpRequest.getLine().get("URL");
        String Body=httpRequest.getBody();
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
                if(isAuthorized(cookie)){
                    //todo 服务端处理客户端报文
                    Map<String,String> config=new HashMap<>();
                    config.put("RequestMethod",RequestMethod);
                    config.put("URL",URL);
                    config.put("Body",Body);
                    try {
                        //读取配置文件
                        File configfile =new File(config_path+URL+".txt");
                        if(configfile.exists()){
                            //文件读取
                            BufferedReader in=new BufferedReader(new java.io.FileReader(configfile));
                            String line;
                            while((line=in.readLine())!=null){
                                config.put(line.substring(0,line.indexOf(":")),line.substring(line.indexOf(":")+1));
                            }

                            if(httpRequest.getHeaders().containsKey("If_Modified_Since")&&
                                config.containsKey("Last_Modified")&&
                                compare(httpRequest.getHeaders().get("If_Modified_Since"),config.get("Last_Modified")))
                            {
                                httpResponse=response(StatusCode.NOT_MODIFIED,config);
                            }
                            else if(config.get("allow").contains(config.get("RequestMethod"))){
                                if(config.get("redirect").equals("true"))
                                {
                                    if(config.get("moved").equals("permanently"))
                                    {
                                        httpResponse=response(StatusCode.MOVED_PERMANENTLY,config);//永久重定向
                                    }
                                    else httpResponse=response(StatusCode.MOVED_TEMPORARILY,config);//暂时重定向
                                }
                                else httpResponse=response(StatusCode.SUCCESS,config);//访问成功
                            }
                            else httpResponse=response(StatusCode.METHOD_NOT_ALLOWED,config);//不允许客户端请求方式

                        }
                        else httpResponse=response(StatusCode.NOT_FOUND,config);//没有找到目标文件
                    }catch (Exception e){
                        httpResponse=response(StatusCode.INTERNAL_SERVER_ERROR,config);//服务器内部错误
                    }
                    httpResponse.getHeaders().put("Cookie", String.valueOf(cookie));
                }
                else{
                    //未登录
                    LinkedHashMap<String,String> headers = new LinkedHashMap<>();
                    String body = "未登录";
                    //设置头部
                    setHttpResponseLine(httpResponse, StatusCode.UNAUTHORIZED);
                    httpResponse.setHeaders(headers);
                    setHttpResponseDefaultHeaders(httpResponse);
                    httpResponse.setBody(body);
                }
                break;

        }


        return httpResponse;
    }


    /**
     * 比较文件修改日期判断是否修改过
     * return true 表示客户端之前访问并保存的东西服务端没有改变过
     * return false 表示客户端之前访问并保存的东西服务端改变过
     */
    public boolean compare(String If_Modified_Since,String Last_Modified){
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date IfModifiedSince = simpleDateFormat.parse(If_Modified_Since);
            Date LastModified = simpleDateFormat.parse(Last_Modified);
            if(IfModifiedSince.getTime() >= LastModified.getTime())return true;
        } catch(ParseException e) {
            e.printStackTrace();
        }
        return false;
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
        headers.put("Server","server");
        headers.put("Connection","Keep-Alive");
        headers.put("Content-Type", "text/plain");
        headers.put("Content-Length","0");
        headers.put("Accept","*/*");
        headers.put("Cookie", "-1");
    }


    /**
     * 登录
     */
    private HttpMessage login(HttpMessage httpRequest){
        boolean isSuccess = false;
        HttpMessage httpResponse = new HttpMessage();
        //设置默认headers
        setHttpResponseDefaultHeaders(httpResponse);
        LinkedHashMap<String,String> headers = httpResponse.getHeaders();
        String body = "";
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
        httpResponse.setBody(body);
        return httpResponse;
    }

    /**
     * 注册
     */
    private HttpMessage register(HttpMessage httpRequest){
        boolean isSuccess = true;
        HttpMessage httpResponse = new HttpMessage();
        //设置默认headers
        setHttpResponseDefaultHeaders(httpResponse);
        String body = "";
        User user = new User((String)JSONHelper.getByProperty(httpRequest.getBody(), "username"),
                (String)JSONHelper.getByProperty(httpRequest.getBody(), "password"));
        for (User value : userList) {
            if (value.getUserName().equals(user.getUserName())) {
                //用户名重复
                isSuccess = false;
                break;
            }
        }
        if(isSuccess){
            userList.add(user);
            setHttpResponseLine(httpResponse, StatusCode.SUCCESS);
            httpResponse.getHeaders().put("Cookie",String.valueOf(userList.size()-1));
            body = "注册成功";
        }
        else{
            //注册失败，用户名重复
            setHttpResponseLine(httpResponse, StatusCode.UNAUTHORIZED);
            body = "注册失败，用户名重复";
        }
        httpResponse.setBody(body);
        return httpResponse;
    }

//    根据状态码构建HttpMessage
    public HttpMessage response(StatusCode statusCode,Map<String,String> config){
        HttpMessage httpMessage=new HttpMessage();
        LinkedHashMap<String,String> headers = httpMessage.getHeaders();
        headers.put("Server","server");
        headers.put("Connection","Keep-Alive");
        String RequestMethod=config.get("RequestMethod");
        String URL=config.get("URL");
        setHttpResponseLine(httpMessage,statusCode);
        //处理其余状态码
        if (statusCode == StatusCode.SUCCESS) {
            if (RequestMethod.equals("POST")) {
                try {
                    byte[] data;
                    //写图片
                    if (URL.contains(".png")) {
                        data = Base64.getDecoder().decode(config.get("Body"));
                        //处理数据
                        for (int i = 0; i < data.length; i++) {
                            if (data[i] < 0) data[i] += 256;
                        }
                    }//写txt/html
                    else {
                        data = config.get("Body").getBytes();
                    }
                    FileOutputStream fileOutputStream = new FileOutputStream(path + URL);
                    fileOutputStream.write(data);
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    headers.put("Content-Type", "text/plain");
                    headers.put("Content-Length", "0");
                    headers.put("Accept", "*/*");
                    return httpMessage;
                }
            }
            FileVo fileVo = fileReader.read(URL);
            if (URL.contains(".png")) {
                headers.put("Content-Type", "image/png");
                byte[] encodedata = Base64.getEncoder().encode(fileVo.getData());
                httpMessage.setBody(new String(encodedata, 0, encodedata.length));
            } else {
                if (URL.contains(".txt")) {
                    headers.put("Content-Type", "text/txt");
                } else {
                    headers.put("Content-Type", "text/html");
                }
                httpMessage.setBody(new String(fileVo.getData(), 0, fileVo.getLength()));
            }
            headers.put("Content-Length", String.valueOf(fileVo.getLength()));
        } else {
            if (config.containsKey("location")) {
                headers.put("Location", config.get("location"));
            }
            headers.put("Content-Type", "text/plain");
            headers.put("Content-Length", "0");
            headers.put("Accept", "*/*");
        }
        return httpMessage;
    }
}
