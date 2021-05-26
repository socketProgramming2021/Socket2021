package Server;

import Http.HttpMessage;
import po.user;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable{

    private Socket client;

    private BufferedReader reader;

    private PrintWriter writer;

    private HttpServerProcessor httpServerProcessor;

    //注册用户表
    private ArrayList<user> userList; // 注册用户表

    public ClientHandler(ArrayList<user> userList, Socket client){
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(client.getInputStream());
            reader = new BufferedReader(inputStreamReader);
            writer = new PrintWriter(client.getOutputStream());
            this.client = client;
            this.userList = userList;
            httpServerProcessor = new HttpServerProcessor(this.userList);

        }catch (IOException ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        String message;
        System.out.println("clientHandler开始监听");
        try{
            while(!client.isClosed()){
                if(client.getInputStream().available()>0) {
                    StringBuilder httpMessage = new StringBuilder();
                    //监听输入

                    while ((message = reader.readLine()) != null&&client.getInputStream().available()>0) {
                        System.out.println("接收信息：" + message.toString());
                        httpMessage.append(message);
                        message = null;
                    }
                    //处理

                    HttpMessage httpResponse = httpServerProcessor.resolve(HttpMessage.stringToHttpRequest(httpMessage.toString()));
                    //发送
                    sendHttpResponse(httpResponse);
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }


        try {
            reader.close();
            writer.close();
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("客户端断开连接");

    }

    /**
     * 发送httpResponse
     */
    public void sendHttpResponse(HttpMessage httpResponse){
        System.out.println("发送报文：" + httpResponse.toString());
        writer.println(httpResponse.toString());
        writer.flush();
    }
}