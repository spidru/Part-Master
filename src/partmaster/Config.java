/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package partmaster;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *
 * @author spidru
 */
public class Config
{
    private static final String CONFIG_FILEPATH;
    
    static
    {
        CONFIG_FILEPATH = PartMaster.CONFIG_FILEPATH;
    }
    
    public static void load()
    {
        File file = new File(CONFIG_FILEPATH);
        try
        {
            if (!file.exists())
            {
                file.createNewFile();
            }
        }
        catch (IOException e)
        {
            Logger.println("Could not create file: " + CONFIG_FILEPATH);
        }
        try
        (
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
        )
        {
            Octopart.setApiKey(br.readLine());
        }
        catch (IOException e)
        {
            Logger.println("Could not load file: " + CONFIG_FILEPATH);
        }
    }
    
    public static void save()
    {
        try
        (
            FileWriter fw = new FileWriter(new File(CONFIG_FILEPATH));
            BufferedWriter bw = new BufferedWriter(fw);
        )
        {    
            bw.write(Octopart.getApiKey());
        }
        catch (IOException e)
        {
            Logger.println("Could not save file: " + CONFIG_FILEPATH);
        }
    }
}
