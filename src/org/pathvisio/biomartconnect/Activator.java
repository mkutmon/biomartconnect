package org.pathvisio.biomartconnect;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.pathvisio.desktop.plugin.Plugin;
import org.pathvisio.biomartconnect.impl.BiomartConnectPlugin;


/**
 * 
 * @author rohan
 *
 */
public class Activator implements BundleActivator {
	
	 private BiomartConnectPlugin plugin;

	 @Override
	 public void start(BundleContext context) throws Exception {
	    plugin = new BiomartConnectPlugin();
	    context.registerService(Plugin.class.getName(), plugin, null);
	 }

	 @Override
	 public void stop(BundleContext context) throws Exception {
	    plugin.done();	 
	 }


	
}