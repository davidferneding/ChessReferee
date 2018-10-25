package Utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Classloader zum Laden von Klassen aus jar-Dateien
 *
 * @author Dietrich Boles (Uni Oldenburg)
 */
public final class JARClassLoader extends URLClassLoader {
    /**
     * Initialisiert einen Classloader zum Laden von Klassen aus jar-Dateien*
     *
     * @param jarPathAndFilename Pfad (Ordner + Name) der jar-Datei
     * @throws MalformedURLException wird bei fehlerhaften Angaben geworfen
     */
    public JARClassLoader(String jarPathAndFilename)
            throws MalformedURLException {
        super(new URL[]{});
        this.addURL(new URL("file:" + jarPathAndFilename));
    }
}