package Http;

import lombok.Data;

@Data
public class Cookie {
    private int value;

    public Cookie(){
        value = -1;
    }

    public Cookie(int value){
        this.value = value;
    }

}
