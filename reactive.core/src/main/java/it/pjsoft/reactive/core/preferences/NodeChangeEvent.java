
package it.pjsoft.reactive.core.preferences;

import java.io.NotSerializableException;

import org.osgi.service.prefs.Preferences;


public class NodeChangeEvent extends java.util.EventObject {
    /**
     * The node that was added or removed.
     *
     * @serial
     */
    private Preferences child;

    /**
     * Constructs a new <code>NodeChangeEvent</code> instance.
     *
     * @param parent  The parent of the node that was added or removed.
     * @param child   The node that was added or removed.
     */
    public NodeChangeEvent(Preferences parent, Preferences child) {
        super(parent);
        this.child = child;
    }

    /**
     * Returns the parent of the node that was added or removed.
     *
     * @return  The parent Preferences node whose child was added or removed
     */
    public Preferences getParent() {
        return (Preferences) getSource();
    }

    /**
     * Returns the node that was added or removed.
     *
     * @return  The node that was added or removed.
     */
    public Preferences getChild() {
        return child;
    }

    /**
     * Throws NotSerializableException, since NodeChangeEvent objects are not
     * intended to be serializable.
     */
     private void writeObject(java.io.ObjectOutputStream out)
                                               throws NotSerializableException {
         throw new NotSerializableException("Not serializable.");
     }

    /**
     * Throws NotSerializableException, since NodeChangeEvent objects are not
     * intended to be serializable.
     */
     private void readObject(java.io.ObjectInputStream in)
                                               throws NotSerializableException {
         throw new NotSerializableException("Not serializable.");
     }

    // Defined so that this class isn't flagged as a potential problem when
    // searches for missing serialVersionUID fields are done.
    private static final long serialVersionUID = 8068949086596572957L;
}
