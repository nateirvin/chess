package ui;

public interface DisplaySink {
    void print(char c);
    void print(int i);
    void print(String s);
    void println();
    void println(String s);
    void printf(String template, Object... args);
}
