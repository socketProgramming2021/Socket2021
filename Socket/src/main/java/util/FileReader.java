package util;

import vo.FileVo;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FileReader {

    String path;

    FileInputStream fileInputStream;

    public FileReader(String path){
        this.path=path;
    }

//    读取文件并返回文件字节长度
    public FileVo read(String url){
        try {
            fileInputStream=new FileInputStream(path+url);
            byte[] data = new byte[fileInputStream.available()];
            int contentLength = fileInputStream.read(data);
            return new FileVo(data,contentLength);
        } catch (IOException e) {
            return null;
        }
    }
}
