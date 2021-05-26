package Http;

import lombok.Data;

import java.util.Map;

@Data
public class HttpMessage {

    /**
     * 报文状态行/请求行
     */

    private Map<String, String> line;

    /**
     * 报文头部
     */

    private Map<String, String> headers;

    /**
     *  报文主体
     */

    private String body;


    @Override
    public String toString(){
        //todo:http报文转换成字符串
        return "Test";
    }

    /**
     * 解析http报文字符串，返回HttpRequest对象
     * @return
     * @param s
     */
    public static HttpMessage stringToHttpRequest(String s){
        //todo:字符串转换成
        return new HttpMessage();
    }



}
