package po;

import lombok.Data;

@Data
public class User {
    private String userName;
    private String password;

    public boolean equals(User user){
        return userName.equals(user.getUserName()) && password.equals(user.getPassword());
    }


    public User(String userName, String password){
        this.userName = userName;
        this.password = password;
    }
}
