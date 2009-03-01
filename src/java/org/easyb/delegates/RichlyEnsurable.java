package org.easyb.delegates;

import org.easyb.plugin.EasybPlugin;

/**
 * this is essentially a marker interface
 *
 * @author aglover
 */
public interface RichlyEnsurable extends EasybPlugin {
    /**
     * ideally, flexible delegates can do interesting things to
     * the object passed in....
     */
    void setVerified(Object verified);
}
