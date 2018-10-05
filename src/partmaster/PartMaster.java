package partmaster;

import java.io.File;

public class PartMaster
{
    private static final String OS = System.getProperty("os.name");
    public static final String CONFIG_FILEPATH = ".pmc";
    private static final File DATA_FILE = new File("data.pmd");
    protected static final String VERSION = "0.5.0";
    
    public static void main(String[] args) {
        Logger.setLoggingMode(Logger.LoggingMode.CONSOLE);
//        Logger.println(System.getProperty("user.name"));
        Logger.println("Part Master " + VERSION);
        if (OS.startsWith("Windows"))
            Logger.println("Windows OS detected");
        else
            Logger.println("Unsupported OS detected: "+OS);
        FileHandler.openDataFile(DATA_FILE);
        Config.load();
        PartMasterGUI.getInstance().loadUI();
    }
	
    public static File getDataFile()
    {
            return DATA_FILE;
    }
    
    private static void loadConfigFile(File file)
    {
        
    }
    
}
