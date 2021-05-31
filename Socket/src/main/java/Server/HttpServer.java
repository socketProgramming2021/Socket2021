package Server;

import po.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

public class HttpServer {

    private ArrayList<User> userList; // 注册用户表

    //监听端口号
    private int port = 5000;

    public static void main(String[] args) {
        HttpServer httpServer = new HttpServer();
        httpServer.go();
    }

    HttpServer(){
        userList = new ArrayList<>();
    }


    public void go(){
        try{
            ServerSocket serverSocket = new ServerSocket(port);
            while (true){
                Socket socket = serverSocket.accept();
                System.out.println("客户端连接成功");
                Thread clientHandler = new Thread(new ClientHandler(this.userList, socket));
                System.out.println("clientHandler开启");
                clientHandler.start();
            }
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }

}
