package be.hcpl.android.sensors.core

import androidx.fragment.app.Fragment
import be.hcpl.android.sensors.MainActivity

/**
 * A base class for all fragments that will show up in the navigation bar. These all need a
 * properly implemented toString() implementation since we use a default listAdapter implementation
 */
open class BaseFragment : Fragment() {
    override fun toString(): String {
        return this.javaClass.simpleName
    }

    /**
     * this isn't really needed but makes it easier to get the parent activity fragment for
     * switching content without having to cast and check for types and so.
     *
     * @return the parent activity as a MainActivity instance
     */
    protected val parentActivity: MainActivity?
        protected get() = activity as MainActivity?
}