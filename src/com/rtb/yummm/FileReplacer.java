/*
 * Roderick Buenviaje
 * April 28, 2013
 * 
 */

package com.rtb.yummm;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;

public class FileReplacer
{
    public File     path;
    private Context ctx;

    public FileReplacer(Context ctx, String path)
    {
        this.path = new File(path);
        this.ctx = ctx;
        installIfNeeded();
    }

    public void installIfNeeded()
    {
        // Code to copy SUE configuration file from sdcard to desired location
        if ((new File("/sdcard/restaurantList.in").exists()))
        {
            try
            {
                File file = new File(path + "/files/restaurantList.in");
                InputStream is = new BufferedInputStream(new FileInputStream("/sdcard/files/restaurantList.in"));
                FileOutputStream fout = new FileOutputStream(file);
                int c = -1;
                while ((c = is.read()) != -1)
                {
                    fout.write(c);
                }

                fout.flush();
                fout.close();
                is.close();
            }
            catch (FileNotFoundException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
