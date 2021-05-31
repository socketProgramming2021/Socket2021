package Http;

import lombok.Data;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class HttpMessage {

    /**
     * 报文状态行/请求行
     */

    private LinkedHashMap<String, String> line;

    /**
     * 报文头部
     */

    private LinkedHashMap<String, String> headers;

    /**
     *  报文主体
     */

    private String body;

    public HttpMessage(){
        line = new LinkedHashMap<>();
        headers = new LinkedHashMap<>();
        body = "";
    }


    @Override
    public String toString(){
        //todo:http报文转换成字符串
        Iterator<Map.Entry<String,String>> iterator = line.entrySet().iterator();
        StringBuilder output = new StringBuilder();
        stringGenerator(iterator, output);

        iterator = headers.entrySet().iterator();
        stringGenerator(iterator, output);
        output.append(body);
        return output.toString();
    }

    private void stringGenerator(Iterator<Map.Entry<String, String>> iterator, StringBuilder output) {
        while(iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            output.append(entry.getKey());
            output.append(":");
            output.append(entry.getValue());
            output.append("\n");
        }
        output.append("\n\n");
    }

    /**
     * 解析http报文字符串，返回HttpRequest对象
     * @return httpMessage
     * @param s
     */
    public static HttpMessage stringToHttpRequest(String s) {
        //todo:字符串转换成httpMessage

        HttpMessage httpMessage = new HttpMessage();

        String[] message = s.split("\n\n", 3);

        if (!message[0].equals("")) {
            String[] subMessage = message[0].split("\n");
            for (String value : subMessage) {
                String[] pairs = value.split(":", 2);
                httpMessage.line.put(pairs[0], pairs[1]);
            }
        }

        if (!message[1].equals("")) {
            String[] subMessage = message[1].split("\n", 2);
            for (String value : subMessage) {
                String[] pairs = value.split(":", 2);
                httpMessage.headers.put(pairs[0], pairs[1]);
            }
        }

        httpMessage.body = message[2];
        return httpMessage;
    }

//    public static void main(String[] args) {
//        String s = "\n\nlove:1\n\ngood:sdfsdf\nbad:xs\n\nsfsdgdsgsgdfgdfg\n\nsds\nsdf";
//        HttpMessage httpMessage = HttpMessage.stringToHttpRequest(s);
//        System.out.println(httpMessage.line);
//        System.out.println(httpMessage.headers);
//        System.out.println(httpMessage.body);
//        String s1 = httpMessage.toString();
//        System.out.println(s1);
//    }

}
