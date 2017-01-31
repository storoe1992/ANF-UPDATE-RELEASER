/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package anf.updatereleaser.utils;

/**
 *
 * @author Sergio
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Jordy
 */
public class PropertiesUtil {

    private final String propertiesName = "updatereleaser.properties";
    private final String propertiesFilePath;
    private static PropertiesUtil instance = null;

    private PropertiesUtil() {
        propertiesFilePath = System.getProperty("user.dir") + System.getProperty("file.separator") + propertiesName;
    }

    /**
     * Devuelve el nombre de la aplicacion cuyo hash id se correponde con el
     * valor del hash
     *
     * @param hashAppID
     * @return
     * @throws Exception
     */
   

    public static PropertiesUtil getInstance() {
        if (instance == null) {
            instance = new PropertiesUtil();
        }
        return instance;
    }

//    public static String getPrintableCurrentVersion() {
//        Properties conf = PropertiesUtil.getProperties();
//        return conf.getProperty("version");
//    }
   

   

    public synchronized Properties getProperties() {

        Properties prop = new Properties();
        try {
            InputStream is = new FileInputStream(propertiesFilePath);
            prop.load(is);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PropertiesUtil.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PropertiesUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return prop;
    }

}

