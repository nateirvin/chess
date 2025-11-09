package ui;

public class RegisterUserCommand implements MenuCommand
{
    public static final String GUEST_USERNAME = "guest";

    private final AppState appState;
    private final ServerFacade serverFacade;

    public RegisterUserCommand(AppState appState, ServerFacade serverFacade) {
        this.appState = appState;
        this.serverFacade = serverFacade;
    }

    @Override
    public String execute(String... arguments)
    {
        if(arguments.length == 2 || arguments.length == 3)
        {
            String userName = arguments[0];
            String plainTextPassword = arguments[1];
            String email = null;
            if(arguments.length == 3) {
                email = arguments[2];
            }

            if(userName.equalsIgnoreCase(GUEST_USERNAME)) {
                return "This username is reserved and cannot be used.";
            }

            var authToken = serverFacade.registerUser(userName, plainTextPassword, email);
            appState.setSession(authToken, userName);

            System.out.println("You are registered and logged in!");
            System.out.println();

            return null;
        }
        else
        {
            return "Invalid inputs";
        }
    }
}
