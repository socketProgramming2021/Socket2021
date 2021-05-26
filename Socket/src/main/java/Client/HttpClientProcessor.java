package Client;

import Http.HttpMessage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.Base64;
import java.util.LinkedHashMap;

public class HttpClientProcessor {

    private Integer cookie;

    HttpClientProcessor(Integer cookie){
        this.cookie = cookie;
    }

    /**
     * 处理服务端返回的报文
     * @param httpResponse
     */
    public HttpMessage resolve(HttpMessage httpResponse){
        //todo:客户端处理返回的报文
//        目前暂时认为301时body包含原有内容+url
        if(httpResponse.getLine().get("StatusCode").equals("301")){
            String[] tmp = httpResponse.getBody().split(" ");
            HttpMessage res = new HttpMessage();
            LinkedHashMap<String, String> headers = res.getHeaders();
            LinkedHashMap<String, String> line = res.getLine();
            headers.put("Accept", "*/*");
            headers.put("Accept-Encoding", "gzip, deflate, br");
            headers.put("Accept-Language", "zh-CN,zh;q=0.9");
            headers.put("Connection", "keep-alive");
            headers.put("Cookie", cookie+"");
            res.setHeaders(headers);
            if(tmp.length>1){
                line.put("POST", tmp[1]);
                File f = new File(tmp[0]);
                String s = "";
                if(!tmp[1].endsWith("png")){
                    if(f.exists()){
                        try{
                            BufferedReader input = new BufferedReader(new FileReader(f));
                            String curline;
                            while((curline = input.readLine()) != null){
                                s += curline;
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
                        s = Base64.getEncoder().encode(data).toString();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                res.setBody(s);
            }
            else{
                line.put("GET", tmp[0]);
            }
            res.setLine(line);
        }
        System.out.println("返回报文：" + httpResponse.toString());
        return null;
    }
}