package Client;

import Http.Cookie;
import Http.HttpMessage;
import util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedHashMap;

public class HttpClientProcessor {

    private Cookie cookie;

    HttpClientProcessor(Cookie cookie){
        this.cookie = cookie;
    }

    /**
     * 处理服务端返回的报文
     * @param httpResponse
     */
    public HttpMessage resolve(HttpMessage httpResponse){
        //todo:客户端处理返回的报文
        int new_cookie = Integer.parseInt(httpResponse.getHeaders().get("Cookie"));
        if(new_cookie != -1)cookie.setValue(new_cookie);
        if(httpResponse.getLine().get("Code").equals("301")||httpResponse.getLine().get("Code").equals("302")){
            Log.print(httpResponse, "接收重定向报文");
            String[] tmp = httpResponse.getBody().split(" ");
            HttpMessage res = new HttpMessage();
            LinkedHashMap<String, String> headers = res.getHeaders();
            LinkedHashMap<String, String> line = res.getLine();
            String url = httpResponse.getHeaders().get("Location");
            headers.put("Accept", "*/*");
            headers.put("Accept-Encoding", "gzip, deflate, br");
            headers.put("Accept-Language", "zh-CN,zh;q=0.9");
            headers.put("Connection", "keep-alive");
            headers.put("Cookie", cookie.getValue()+"");
            res.setHeaders(headers);
            if(tmp.length>1){
                line.put("Method", "POST");
                line.put("URL", url);
                res.setLine(line);
                File f = new File(tmp[0]);
                StringBuilder s = new StringBuilder();
                if(!tmp[1].endsWith("png")){
                    if(f.exists()){
                        try{
                            BufferedReader input = new BufferedReader(new FileReader(f));
                            String curline;
                            while((curline = input.readLine()) != null){
                                s.append(curline);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    else{
                        System.out.println("Not Found!");
                        return null;
                    }
                }
                else{
                    try{
                        FileInputStream fileInputStream = new FileInputStream(tmp[0]);
                        byte[] data = new byte[fileInputStream.available()];
                        s = new StringBuilder(Arrays.toString(Base64.getEncoder().encode(data)));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                res.setBody(s.toString());
                Log.print(res,"发送重定向报文");
                return res;
            }
            else{
                line.put("Method", "GET");
                line.put("URL", url);
                line.put("Version", "HTTP/1.1");
                res.setLine(line);
                Log.print(res,"发送重定向报文");
                return res;
            }
        }
        Log.print(httpResponse,"返回报文");
        return null;
    }

}