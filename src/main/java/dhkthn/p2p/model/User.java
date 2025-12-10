package dhkthn.p2p.model;

import lombok.*;

@NoArgsConstructor @AllArgsConstructor @Builder @Data
public class User{
    private int id;
    private String username;
    private String password;
}
