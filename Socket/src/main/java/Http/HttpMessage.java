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
        while(iterator.hasNext()){
            Map.Entry<String, String> entry = iterator.next();
            output.append(entry.getValue());
            output.append(" ");
        }
        output.deleteCharAt(output.length()-1);
        output.append("\n");


        iterator = headers.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String, String> entry = iterator.next();
            output.append(entry.getKey());
            output.append(": ");
            output.append(entry.getValue());
            output.append("\n");
        }
        output.append("\n");


        output.append(body);

        return output.toString();
    }


    /**
     * 解析http报文字符串，返回HttpRequest对象
     * @return httpMessage
     * @param s
     */
    public static HttpMessage stringToHttpMessage(String s) {
        //todo:字符串转换成httpMessage

        HttpMessage httpMessage = new HttpMessage();

        String[] lineAndRest = s.split("\n", 2);
        String[] headersAndBody = lineAndRest[1].split("\n\n",2);

        if (!lineAndRest[0].equals("")) {
            String[] subMessage = lineAndRest[0].split(" ",3);
            if(subMessage[0].equals("GET")||subMessage[0].equals("POST")){
                httpMessage.line.put("Method",subMessage[0]);
                httpMessage.line.put("URL", subMessage[1]);
                httpMessage.line.put("Version", subMessage[2]);
            }
            else {
                httpMessage.line.put("Version", subMessage[0]);
                httpMessage.line.put("Code", subMessage[1]);
                httpMessage.line.put("Code-Description", subMessage[2]);
            }
        }

        if (!headersAndBody[0].equals("")) {
            String[] subMessage = headersAndBody[0].split("\n");
            for (String value : subMessage) {
                String[] pairs = value.split(": ", 2);
                httpMessage.headers.put(pairs[0], pairs[1]);
            }
        }

        httpMessage.body = headersAndBody[1];
        return httpMessage;
    }
}
