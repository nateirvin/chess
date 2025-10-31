package model;

public class UpsertUserResult extends UserData
{
    private final boolean isNew;

    public UpsertUserResult(UserData userData, boolean isNew)
    {
        super(userData.username(), userData.password(), userData.email());
        setId(userData.getId());
        this.isNew = isNew;
    }

    public boolean isNew() 
    {
        return isNew;
    }
}
