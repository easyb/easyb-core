package org.easyb.delegates

import javax.imageio.spi.ServiceRegistry
import org.easyb.plugin.EasybPlugin

class PlugableDelegate {
    /**
     *  The "given" delegate supports plug-ins, consequently,
     * the flex guys are utlized. Currently, there is a DbUnit
     * "given" plug-in and it is envisioned that there could be
     * others like Selenium.
     */
    def invokeMethod(String method, Object args) {
        Iterator providers = ServiceRegistry.lookupProviders(EasybPlugin.class, ClassLoader.getSystemClassLoader())
        def found = false
        while (providers.hasNext() && !found) {
            Object provider = providers.next()
            if (provider.metaClass.respondsTo(provider, method, args)) {
                provider.invokeMethod(method, args)
                found = true
            }
        }
        if (!found) {
            throw new MissingMethodException(method, PlugableDelegate.class, args)
        }
    }
}
