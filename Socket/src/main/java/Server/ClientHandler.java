package Server;

import Http.HttpMessage;
import po.User;
import util.Log;

import java.io.*;
import java.net.Socket;
import java.sql.Time;
import java.util.ArrayList;

public class ClientHandler implements Runnable{

    private Socket client;

    private BufferedReader reader;

    private PrintWriter writer;

    private HttpServerProcessor httpServerProcessor;

    //注册用户表
    private ArrayList<User> userList; // 注册用户表

    public ClientHandler(ArrayList<User> userList, Socket client){
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

                    String request = read(client);

                    //处理
                    Log.print(HttpMessage.stringToHttpMessage(request), "接收报文");
                    HttpMessage httpResponse = httpServerProcessor.resolve(HttpMessage.stringToHttpMessage(request));
                    //发送
                    sendHttpResponse(httpResponse);
                }
            }
        }catch (IOException | InterruptedException e){
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

    public static String read(Socket client) throws InterruptedException, IOException {
        int lenInput = -1;
        String request = null;
        Thread.sleep(2000);
        while (lenInput <= 0) {
            byte inputData[] = new byte[client.getInputStream().available()];   //准备一个缓存数组
            lenInput = client.getInputStream().read(inputData);
            request = new String(inputData, 0, lenInput);  //将输入的字节数组转化为可操作的字符串
        }
        return request;
    }

    /**
     * 发送httpResponse
     */
    public void sendHttpResponse(HttpMessage httpResponse){
        Log.print(httpResponse,"发送报文");
        writer.println(httpResponse.toString());
        writer.flush();
    }

}
