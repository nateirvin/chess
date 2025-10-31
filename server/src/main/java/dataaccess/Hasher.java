package dataaccess;

import org.mindrot.jbcrypt.BCrypt;

public class Hasher
{
    static String hash(String plainText) {
        return BCrypt.hashpw(plainText, BCrypt.gensalt());
    }
}
