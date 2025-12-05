package client;

import model.UserEntryResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ui.menu.GameScopedCommandHandler;

public class GameScopedCommandHandlerTests
{
    @Test
    public void getGameNumberDoesNotThrowOnNull() {
        getGameNumberDoesNotThrowOnBadInput(null);
    }

    @DisplayName("Bad inputs don't break parsing")
    @ParameterizedTest()
    @ValueSource(strings = {" ","", "    ", "\n", "\t"})
    public void getGameNumberDoesNotThrowOnBadInput(String input) {
        UserEntryResult<Integer> result = GameScopedCommandHandler.getGameNumber(input);
        Assertions.assertFalse(result.success());
    }
}
