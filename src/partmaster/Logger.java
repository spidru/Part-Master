package partmaster;

import java.io.File;
import java.io.PrintWriter;

public class Logger
{
    public enum LoggingMode {OFF,CONSOLE,LOCAL,REMOTE};
    private static LoggingMode mode;
    
    private static PrintWriter pw;
    
    public static void setLoggingMode(LoggingMode mode)
    {
        Logger.mode = mode;
    }
    
    public static void print(String msg)
    {
        switch (mode)
        {
            case OFF:
                break;
            case CONSOLE:
                System.out.print(msg);
                break;
            case LOCAL:
                if (pw == null)
                    pw = FileHandler.openLogFile(new File("log.txt"));
                pw.print(msg);
                break;
            case REMOTE:
                
        }
    }
    
    public static void println(String msg)
    {
        print(msg+"\r\n");
    }
    
    public static void println()
    {
        print("\r\n");
    }
    
    public static void flush()
    {
        if (pw != null)
            pw.flush();
    }
    
}
