package core.io;


/**
 * Convenience base class for {@link Resource} implementations,
 * pre-implementing typical behavior.
 *
 * <p>The "exists" method will check whether a File or InputStream can
 * be opened; "isOpen" will always return false; "getURL" and "getFile"
 * throw an exception; and "toString" will return the description.
 *
 * @author Juergen Hoeller
 * @since 28.12.2003
 */
public class AbstractResource{


    public AbstractResource(){
        // cry();
    }
    /**
     * This implementation always returns {@code null},
     * assuming that this resource type does not have a filename.
     */
    public String getFilename() {
        return null;
    }

}