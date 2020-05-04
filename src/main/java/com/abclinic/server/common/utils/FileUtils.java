package com.abclinic.server.common.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * @author tmduc
 * @package com.abclinic.server.common.utils
 * @created 5/4/2020 10:35 AM
 */
public class FileUtils {
    public static File toFile(String name, String type, byte[] bytes, String uploadDirectory) throws IOException {
            File file = new File(uploadDirectory + name + "." + type);
            org.apache.commons.io.FileUtils.writeByteArrayToFile(file, bytes);
            try (InputStream is = new FileInputStream(file)) {
                BufferedImage image = ImageIO.read(is);
                try (OutputStream os = new FileOutputStream(file)) {
                    ImageIO.write(image, type, os);
                } catch (Exception exp) {
                    exp.printStackTrace();
                }
            } catch (Exception exp) {
                exp.printStackTrace();
            }
            return file;
    }
}
