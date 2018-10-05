package partmaster;

import java.awt.Image;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

public class FileHandler
{
    private static FileHandler instance = null;

    /* Image file names */
    public static final String MAIN_ICON = "pm-icon.png";
    public static final String ADD_ICON = "add-icon.png";
    public static final String DELETE_ICON = "delete-icon.png";
    public static final String SEARCH_ICON = "search-icon.gif";

    protected FileHandler(){}

    /**
     * Returns the instance of FileHandler. If this hasn't been done yet, a new instance will be created and returned.
     * @return the singleton instance of FileHandler
     */
    public static FileHandler getInstance()
    {
        if (instance == null)
            instance = new FileHandler();
        return instance;
    }

    public static PrintWriter openLogFile(File file)
    {
        PrintWriter pw = null;
        if (!file.exists())
        {
            try
            {
                file.createNewFile();
                System.out.println("New log file created");
            }
            catch (IOException ioe)
            {
                System.out.println("Cannot create log file");   // FIXME always writing to stdout
            }
        }
        try
        {
            pw = new PrintWriter(new BufferedWriter(new FileWriter(file,true)));
        }
        catch (IOException ioe)
        {
            // TODO Add exception handler
        }
        return pw;
    }
   
    public static void openDataFile(File file)
    {
        Object obj;

        if (!file.exists())
        {
            try
            {
                    file.createNewFile();
                    Logger.println("New data file created");
            }
            catch (IOException ioe)
            {
                    Logger.println("Cannot create data file");
            }
        }
        else
        {
            try
            {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
                try
                {
                    while (ois.available() == 0)
                    {
                        obj = ois.readObject();
                        Component.addComponent(new Component(obj));
                    }
                }
                finally
                {
                    ois.close();
                }
            }
            catch (EOFException e)
            {
//				Logger.println("End of file reached");
            }
            catch (ClassNotFoundException|IOException ioe)
            {
                Logger.println("Error occured while reading data file");
            }
        }
    }

    public static void saveDataFile(File file)
    {
            ObjectOutputStream oos;
            FileOutputStream fos;
            try
            {
                    fos = new FileOutputStream(file);
                    oos = new ObjectOutputStream(fos);
                    Logger.print("Saving parts:\t");
                    for (int i=0; i<Component.getList().size(); i++)
                    {
                            oos.writeObject(Component.getComponentObject(i));
                            Logger.print(Component.getList().get(i).getName()+"|");
                    }
                    Logger.println();
                    oos.close();
            }
            catch (FileNotFoundException e)
            {
                    Logger.println("Cannot find data file");
            }
            catch (IOException ioe)
            {
                    Logger.println("Cannot write to data file");
            }
    }

    public Image getImage(String file)
    {
            Image img = null;
//                ImageIcon icon = null;
            try {
                img = ImageIO.read(getClass().getClassLoader().getResource(file));
            } catch (IOException ex) {
                Logger.println("Cannot load icon: "+file);
            }
            return img;
    }

    public ImageIcon getImageIcon(String file)
    {
            Image img = null;
//                ImageIcon icon = null;
            try {
                img = ImageIO.read(getClass().getClassLoader().getResource(file));
            } catch (IOException ex) {
                Logger.println("Cannot load icon: "+file);
            }
            return new ImageIcon(img);
    }

    private static String getMD5(File file) // FIXME remove throws
    {
        String hex = null;
        try (InputStream in = new FileInputStream(file))
        {
            
            byte[] buffer = new byte[1024];
            MessageDigest hash = MessageDigest.getInstance("MD5");
            int bytesCount;
            do
            {
                bytesCount = in.read(buffer);
                if (bytesCount > 0)
                    hash.update(buffer,0,bytesCount);
            }
            while (bytesCount != -1);
            hex = (new HexBinaryAdapter()).marshal(hash.digest(buffer));
        }
        catch (IOException|NoSuchAlgorithmException e)
        {
            Logger.println("Error occured while generating MD5");
        }
        
        return hex;
    }
}
