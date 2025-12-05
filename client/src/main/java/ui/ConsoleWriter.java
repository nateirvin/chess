package ui;

public class ConsoleWriter implements DisplaySink {
    @Override
    public void print(char c) {
        System.out.print(c);
    }

    @Override
    public void print(int i) {
        System.out.print(i);
    }

    @Override
    public void print(String s) {
        System.out.print(s);
    }

    @Override
    public void println() {
        System.out.println();
    }

    @Override
    public void println(String s) {
        System.out.println(s);
    }

    @Override
    public void printf(String template, Object... args) {
        System.out.printf(template, args);
    }
}
