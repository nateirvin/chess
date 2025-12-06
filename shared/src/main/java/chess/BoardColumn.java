package chess;

public class BoardColumn {
    public static char numberToLetter(int columnNumber){
        if(columnNumber == 1) {
            return 'a';
        } else if(columnNumber == 2) {
            return 'b';
        } else if(columnNumber == 3) {
            return 'c';
        } else if(columnNumber == 4) {
            return 'd';
        } else if(columnNumber == 5) {
            return 'e';
        } else if(columnNumber == 6) {
            return 'f';
        } else if(columnNumber == 7) {
            return 'g';
        } else if(columnNumber == 8) {
            return 'h';
        } else {
            throw new IllegalArgumentException();
        }
    }

    public static Integer letterToNumber(String columnLetter) {
        switch (columnLetter.toLowerCase()) {
            case "a" -> {
                return 1;
            }
            case "b" -> {
                return 2;
            }
            case "c" -> {
                return 3;
            }
            case "d" -> {
                return 4;
            }
            case "e" -> {
                return 5;
            }
            case "f" -> {
                return 6;
            }
            case "g" -> {
                return 7;
            }
            case "h" -> {
                return 8;
            }
            default -> {
                return null;
            }
        }
    }
}
