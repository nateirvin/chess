import server.Server;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        server.run(8080);

        System.out.println();
        System.out.println("The Chess Server is running.");
        System.out.println();

        System.out.print("Press any key to shutdown the server...");
        try {
            System.in.read();
        } catch (IOException e) {
            System.out.printf("(Not that it matters, but: %s)%n", e.getMessage());
        }

        server.stop();
    }
}