package org.disco.easyb.delegates

import javax.imageio.spi.ServiceRegistry

class PlugableDelegate {
    /**
     *
     */
    def invokeMethod(String method, Object args) {
        Iterator providers = ServiceRegistry.lookupProviders(Plugable.class, ClassLoader.getSystemClassLoader())
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