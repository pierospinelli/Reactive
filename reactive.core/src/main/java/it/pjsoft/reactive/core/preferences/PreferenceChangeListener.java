
package it.pjsoft.reactive.core.preferences;


public interface PreferenceChangeListener extends java.util.EventListener {
    /**
     * This method gets called when a preference is added, removed or when
     * its value is changed.
     * <p>
     * @param evt A PreferenceChangeEvent object describing the event source
     *          and the preference that has changed.
     */
    void preferenceChange(PreferenceChangeEvent evt);
}
