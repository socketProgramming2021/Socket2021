package Client;

import Http.HttpMessage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.*;


public class Terminal {

    private Integer cookie;

    Terminal(Integer cookie){
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
        if(orders.length>3){
            System.out.println("Invalid Input!");
            return null;
        }
        HttpMessage res = new HttpMessage();
        LinkedHashMap<String, String> headers = res.getHeaders();
        LinkedHashMap<String, String> line = res.getLine();
        headers.put("Accept", "*/*");
        headers.put("Accept-Encoding", "gzip, deflate, br");
        headers.put("Accept-Language", "zh-CN,zh;q=0.9");
        headers.put("Connection", "keep-alive");
        headers.put("Cookie", cookie+"");
        res.setHeaders(headers);
        String tmp = "";
        switch (order.charAt(0)){
            case 'h':
                System.out.println("命令标准输入格式如下,其中参数均以空格为分隔符:");
                System.out.println("help: 输出帮助信息");
                System.out.println("register @username @password");
                System.out.println("login @username @password");
                System.out.println("get @url");
                System.out.println("post @url @body");
                //不发送报文时返回null
                return null;
            case 'r':
            case 'l':
                tmp = orders[1] + " " + orders[2];
                res.setBody(tmp);
                break;
            case 'g':
                line.put("GET", orders[1]);
                res.setLine(line);
                break;
            case 'p':
                line.put("POST", orders[1]);
                File f = new File(orders[2]);
                if(!orders[2].endsWith("png")){
                    if(f.exists()){
                        try{
                            BufferedReader input = new BufferedReader(new FileReader(f));
                            String curline;
                            while((curline = input.readLine()) != null){
                                tmp += curline;
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
                        tmp = Base64.getEncoder().encode(data).toString();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                res.setLine(line);
                res.setBody(tmp);
                break;
            default:
                System.out.println("Invalid Command!");
                return null;
        }
        return res;
    }
}
