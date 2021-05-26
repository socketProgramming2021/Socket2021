package Client;

import Http.HttpMessage;

import java.util.Scanner;


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
        if(order.equals("send"))return new HttpMessage();
        return null;
    }
}
