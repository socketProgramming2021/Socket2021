package Client;

import Http.Cookie;
import Http.HttpMessage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.*;


public class Terminal {

    private Cookie cookie;

    Terminal(Cookie cookie){
        this.cookie = cookie;
    }

    /**
     * 循环输入命令，若指令有误则继续输入命令直到命令正确且返回HttpRequest对象
     * @return
     */
    public HttpMessage input() {
        HttpMessage httpRequest;
        String order;
        Scanner scanner = new Scanner(System.in);
        do {
            //输入命令
            order = scanner.nextLine();
            //处理命令
            httpRequest = resolve(order);
        } while (httpRequest == null);

        return httpRequest;
    }

    /**
     * 处理命令
     * 若命令会发送报文则返回相应的httpRequest
     * 若不会或者命令输入错误则输出提示信息返回null
     *
     * @param order
     * @return
     */
    public HttpMessage resolve(String order){
        //todo:终端处理客户端输入命令
        String[] orders = order.split(" ");
        HttpMessage res = new HttpMessage();
        LinkedHashMap<String, String> headers = res.getHeaders();
        LinkedHashMap<String, String> line = res.getLine();
        headers.put("Accept", "*/*");
        headers.put("Accept-Encoding", "gzip, deflate, br");
        headers.put("Accept-Language", "zh-CN,zh;q=0.9");
        headers.put("Connection", "keep-alive");
        headers.put("Cookie", cookie.getValue()+"");
        res.setHeaders(headers);
        StringBuilder tmp = new StringBuilder();
        switch (orders[0]){
            case "help":
                printHelp();
                //不发送报文时返回null
                return null;
            case "register":
                if(orders.length!=3){
                    printHelp();
                    return null;
                }
                tmp = new StringBuilder("{\"username\":\"" + orders[1] + "\",\"password\":\"" + orders[2] + "\"}");
                line.put("Method", "POST");
                line.put("URL", "/register");
                res.setLine(line);
                res.setBody(tmp.toString());
                break;
            case "login":
                if(orders.length!=3){
                    printHelp();
                    return null;
                }
                tmp = new StringBuilder("{\"username\":\"" + orders[1] + "\",\"password\":\"" + orders[2] + "\"}");
                line.put("Method", "POST");
                line.put("URL", "/login");
                res.setLine(line);
                res.setBody(tmp.toString());
                break;
            case "get":
                if(orders.length!=2){
                    printHelp();
                    return null;
                }
                if(!checkURL(orders[1])){
                    printHelp();
                    return null;
                }
                line.put("Method", "GET");
                line.put("URL", orders[1]);
                res.setLine(line);
                break;
            case "post":
                if(orders.length!=3){
                    printHelp();
                    return null;
                }
                if(!checkURL(orders[2])){
                    printHelp();
                    return null;
                }
                line.put("Method", "POST");
                line.put("URL", orders[1]);
                File f = new File(System.getProperty("user.dir") + File.separator + "Socket" +File.separator+ orders[2]);
                if(!orders[2].endsWith("png")){
                    if(f.exists()){
                        try{
                            BufferedReader input = new BufferedReader(new FileReader(f));
                            String curline;
                            while((curline = input.readLine()) != null){
                                tmp.append(curline + "\n");
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
                        FileInputStream fileInputStream = new FileInputStream(orders[2]);
                        byte[] data = new byte[fileInputStream.available()];
                        tmp = new StringBuilder(Arrays.toString(Base64.getEncoder().encode(data)));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                res.setLine(line);
                res.setBody(tmp.substring(0, tmp.toString().length()-1));
                break;
            default:
                System.out.println("Invalid Command!");
                return null;
        }
        res.getLine().put("Version", "HTTP/1.1");
        return res;
    }

    private void printHelp(){
        System.out.println("命令标准输入格式如下,其中参数均以空格为分隔符:");
        System.out.println("help: 输出帮助信息");
        System.out.println("register @username @password");
        System.out.println("login @username @password");
        System.out.println("get @url");
        System.out.println("post @url @body");
    }

    private boolean checkURL(String url){
        url = url.toLowerCase();
        return url.endsWith(".png") || url.endsWith(".txt") || url.endsWith(".html");
    }
}
