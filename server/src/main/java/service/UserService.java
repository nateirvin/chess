package service;

import dataaccess.UsersDataAccess;
import model.*;

import javax.security.auth.login.LoginException;

public class UserService
{
    private final SessionService sessionService;
    private final UsersDataAccess dataAccess;

    public UserService(SessionService sessionService, UsersDataAccess dataAccess)
    {
        this.sessionService = sessionService;
        this.dataAccess = dataAccess;
    }

    public LoginResult register(RegisterRequest registration)
    {
        UpsertUserResult userData = dataAccess.findOrCreateUser(new UserData(registration));

        if (!userData.isNew()) {
            throw new AlreadyTakenException("username", registration.username());
        }

        AuthData authData = sessionService.createSession(userData.username());

        return new LoginResult(registration.username(), authData.token());
    }

    public AuthData login(LoginRequest challenge) throws LoginException
    {
        UserData user = dataAccess.getUser(challenge.username(), challenge.password());

        if(user == null) {
            throw new LoginException("unauthorized");
        }

        return sessionService.createSession(user.username());
    }
}
