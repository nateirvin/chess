package service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SessionServiceTests
{
    @Test
    public void createSessionCreatesSessionWithIdAndUserName()
    {
        assertTrue(false);
    }

    @Test
    public void createSessionReturnsDifferentIdEachTime()
    {
        assertTrue(false);
    }

    @Test
    public void validateSessionThrowsExceptionIfAuthTokenMissing()
    {
        assertTrue(false);
    }

    @Test
    public void validateSessionReturnsAuthDataIfSessionFound()
    {
        assertTrue(false);
    }

    @Test
    public void closeSessionDeletesSessionIfSessionExists()
    {
        assertTrue(false);
    }

    @Test
    public void closeSessionDoesNothingIfSessionDoesNotExist()
    {
        assertTrue(false);
    }

    @Test
    public void resetDeletesAllSessionsIfSessionsExist()
    {
        assertTrue(false);
    }

    @Test
    public void resetDoesNothingIfNoSessionsExist()
    {
        assertTrue(false);
    }
}
