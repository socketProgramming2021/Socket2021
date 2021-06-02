package util;

import Http.HttpMessage;

public class Log {
    //用于输出报文
    public static void print(HttpMessage httpMessage, String prefix){
        System.out.println("\n---------------\n" + prefix+"\n"+httpMessage.toString()+"\n---------------\n");
    }
}
