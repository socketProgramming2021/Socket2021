package vo;

import lombok.Data;

@Data
public class FileVo {
    private byte[] data;
    private int length;
    public FileVo(byte[] data,int length){
        this.data=data;
        this.length=length;
    }
}
