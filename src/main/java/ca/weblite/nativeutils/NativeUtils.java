package ca.weblite.nativeutils;
 
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
 
/**
 * Simple library class for working with JNI (Java Native Interface)
 *
 * @see <a href="http://frommyplayground.com/how-to-load-native-jni-library-from-jar">http://frommyplayground.com/how-to-load-native-jni-library-from-jar</a>
 * @author Adam Heirnich <a href="mailto:mailto:adam@adamh.cz"></a>, <a href="http://www.adamh.cz"></a>
 * @version $Id: $Id
 * @since 1.1
 */
public class NativeUtils {
 
    /**
     * Private constructor - this class will never be instanced
     */
    private NativeUtils() {
    }
 
    /**
     * <p>loadLibraryFromJar.</p>
     *
     * @param path a {@link java.lang.String} object.
     * @throws java.io.IOException if any.
     */
    public static void loadLibraryFromJar(String path) throws IOException {
        loadLibraryFromJar(path, NativeUtils.class);
    }
    
    /**
     * Loads library from current JAR archive
     *
     * The file from JAR is copied into system temporary directory and then loaded. The temporary file is deleted after exiting.
     * Method uses String as filename because the pathname is "abstract", not system-dependent.
     *
     * @throws java.lang.IllegalArgumentException If the path is not absolute or if the filename is shorter than three characters (restriction of @see File#createTempFile(java.lang.String, java.lang.String)).
     * @throws java.lang.IllegalArgumentException If the path is not absolute or if the filename is shorter than three characters (restriction of @see File#createTempFile(java.lang.String, java.lang.String)).
     * @throws java.lang.IllegalArgumentException If the path is not absolute or if the filename is shorter than three characters (restriction of @see File#createTempFile(java.lang.String, java.lang.String)).
     * @param path a {@link java.lang.String} object.
     * @param source a {@link java.lang.Class} object.
     * @throws java.io.IOException if any.
     */
    public static void loadLibraryFromJar(String path, Class source) throws IOException {
 
        
        // Finally, load the library
        System.load(loadFileFromJar(path, source).getAbsolutePath());
    }
    
    /**
     * <p>loadFileFromJar.</p>
     *
     * @param path a {@link java.lang.String} object.
     * @param source a {@link java.lang.Class} object.
     * @return a {@link java.io.File} object.
     * @throws java.io.IOException if any.
     */
    public static File loadFileFromJar(String path, Class source) throws IOException {
        if (!path.startsWith("/")) {
            throw new IllegalArgumentException("The path has to be absolute (start with '/').");
        }
 
        // Obtain filename from path
        String[] parts = path.split("/");
        String filename = (parts.length > 1) ? parts[parts.length - 1] : null;
 
        // Split filename to prexif and suffix (extension)
        String prefix = "";
        String suffix = null;
        if (filename != null) {
            parts = filename.split("\\.", 2);
            prefix = parts[0];
            suffix = (parts.length > 1) ? "."+parts[parts.length - 1] : null; // Thanks, davs! :-)
        }
 
        // Check if the filename is okay
        if (filename == null || prefix.length() < 3) {
            throw new IllegalArgumentException("The filename has to be at least 3 characters long.");
        }
 
        // Prepare temporary file
        File temp = File.createTempFile(prefix, suffix);
        temp.deleteOnExit();
 
        if (!temp.exists()) {
            throw new FileNotFoundException("File " + temp.getAbsolutePath() + " does not exist.");
        }
 
        // Prepare buffer for data copying
        byte[] buffer = new byte[1024];
        int readBytes;
 
        // Open and check input stream
        InputStream is = source.getResourceAsStream(path);
        if (is == null) {
            throw new FileNotFoundException("File " + path + " was not found inside JAR.");
        }
 
        // Open output stream and copy data between source file in JAR and the temporary file
        OutputStream os = new FileOutputStream(temp);
        try {
            while ((readBytes = is.read(buffer)) != -1) {
                os.write(buffer, 0, readBytes);
            }
        } finally {
            // If read/write fails, close streams safely before throwing an exception
            os.close();
            is.close();
        }
        return temp;
 
    }
}
