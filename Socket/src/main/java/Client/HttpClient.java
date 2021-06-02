package Client;

import Http.Cookie;
import Http.HttpMessage;
import Server.ClientHandler;
import util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class HttpClient {
    //连接服务器主机
    private String host = "127.0.0.1";
    //端口
    private int port = 5000;

    private Socket socket;

    private BufferedReader reader;

    private PrintWriter writer;

    private Cookie cookie = new Cookie(-1);

    //处理终端输入命令
    private Terminal terminal;

    //处理接收httpResponse
    private HttpClientProcessor httpClientProcessor;

    /**
     * 完成网络连接启动工作
     */
    public void setUpNetWorking(){
        try {
            socket = new Socket(host, port);
            InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
            reader = new BufferedReader(inputStreamReader);
            writer = new PrintWriter(socket.getOutputStream());
            terminal = new Terminal(cookie);
            httpClientProcessor = new HttpClientProcessor(cookie);
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }

    /**
     * 主程序
     */
    public static void main(String[] args){
        HttpClient client = new HttpClient();
        client.go();
    }

    /**
     * 启动客户端
     */

    public void go(){
        //连接服务器,初始化网络配置
        setUpNetWorking();
        System.out.println("连接服务端成功");
        Thread incomingReader = new Thread(new IncomingReader());
        incomingReader.start();
        HttpMessage httpMessage;
        //开始
        while(!socket.isClosed()){
            //终端输入命令,返回解析后要发送的报文
            httpMessage = terminal.input();
            //发送http请求
            sendRequest(httpMessage);
            Log.print(httpMessage,"发送报文");
        }
        try{
            reader.close();
            writer.close();
            socket.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * 发送http报文
     */

    public void sendRequest(HttpMessage httpMessage){
        writer.print(httpMessage.toString());
        writer.flush();

    }


    public class IncomingReader implements Runnable{
        String message;

        public void run(){
            System.out.println("客户端开始监听服务端");
            try{
                while(!socket.isClosed()){
                    if(socket.getInputStream().available()>0) {
                        String request = ClientHandler.read(socket);
                        //处理服务端返回报文
                        HttpMessage httpRequest = httpClientProcessor.resolve(HttpMessage.stringToHttpMessage(request));
                        if(httpRequest!=null){
                            sendRequest(httpRequest);
                        }
                    }
                }
            }catch (IOException | InterruptedException e){
                e.printStackTrace();
            }
        }
    }

}
