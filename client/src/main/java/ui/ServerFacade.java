package ui;

import java.util.UUID;

public class ServerFacade {
    public String registerUser(String userName, String plainTextPassword, String email) {
        return UUID.randomUUID().toString(); //TODO: actually implement
    }
}
