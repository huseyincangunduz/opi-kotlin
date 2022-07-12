import com.gmail.maystruks08.opi_core.Terminal;
import com.gmail.maystruks08.opi_core.connector.OPILogger;

public class Main {
    public static class Logger implements OPILogger {

        public void log(String message) {
            System.out.println(message);
        }

        public void logError(Exception exception, String message) {
            System.out.println(message);
            exception.printStackTrace();
        }

        public void removeOutdateLogFiles() {

        }
    }
    public static void main(String[] args) {
        final Terminal terminal = (new Terminal.Builder()).ipAddress("192.168.1.190")
                .inputPort(20002).outputPort(20007).logger(new Logger()).build();
        try {
            terminal.login();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println(Terminal.class.toString() );

    }
}
