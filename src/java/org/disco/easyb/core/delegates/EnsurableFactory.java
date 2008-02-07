package org.disco.easyb.core.delegates;

import org.disco.easyb.core.delegates.RichlyEnsurable;


/**
 * this class is needed it seems to support compilation--
 * we've hit a wall with using groovy and java together in 
 * different directories creating a circular dependency; hence,
 * this factory is used as an interface so this java code can be 
 * first compiled followed by some Groovy code that uses it.
 * 
 * @author aglover
 *
 */
public class EnsurableFactory {
	
	private static final Class FLEXIBLE_DELEGATE;
	//ostensibly this should be done once but 
	//it seems the newInstance() call is slower than
	//forName.
	static{
		try{
			FLEXIBLE_DELEGATE = Class.forName("org.disco.easyb.core.delegates.RichEnsureProxyDelegate");
		}catch(ClassNotFoundException e){
			throw new RuntimeException("can't load fundamental class to easyb");
		}
	}
	/**
	 * this factory method needs to be thought out-- could we 
	 * end up creating different types of delegates that could be 
	 * plugged in at runtime? If so the static stuff above must 
	 * go!
	 * @return
	 * @throws Exception
	 */
	public static RichlyEnsurable manufacture() throws Exception{
		return (RichlyEnsurable)FLEXIBLE_DELEGATE.newInstance();
	}
}
