package model;

public class UpsertUserResult extends UserData
{
    private final boolean isNew;

    public UpsertUserResult(UserData userData, boolean isNew)
    {
        super(userData.getUsername(), userData.getPassword(), userData.getEmail());
        setId(userData.getId());
        this.isNew = isNew;
    }

    public boolean isNew() 
    {
        return isNew;
    }
}
