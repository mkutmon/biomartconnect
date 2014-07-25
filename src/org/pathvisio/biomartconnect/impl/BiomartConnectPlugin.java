package org.pathvisio.biomartconnect.impl;



import javax.swing.JPanel;

import org.pathvisio.core.ApplicationEvent;
import org.pathvisio.core.Engine.ApplicationEventListener;
import org.pathvisio.core.model.PathwayElementEvent;
import org.pathvisio.core.model.PathwayElementListener;
import org.pathvisio.core.view.SelectionBox.SelectionEvent;
import org.pathvisio.core.view.SelectionBox.SelectionListener;
import org.pathvisio.core.view.VPathway;
import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.desktop.plugin.Plugin;
import org.pathvisio.inforegistry.IInfoProvider;
import org.pathvisio.inforegistry.InfoRegistry;


/**
 * 
 * @author rohansaxena
 * @author martina
 *
 */
public class BiomartConnectPlugin extends JPanel implements  SelectionListener, ApplicationEventListener, PathwayElementListener, Plugin {
	
	private InfoRegistry registry;
	private IInfoProvider basic;
	private IInfoProvider var;

	
	@Override
	public void done() {
		registry.unregisterInfoProvider(basic);
		registry.unregisterInfoProvider(var);
	}
	
	@Override
	public void init(PvDesktop desktop) {
		
		// registers plugin as information provider
		registry = InfoRegistry.getInfoRegistry();
		IInfoProvider basic = new BasicBiomartProvider(desktop);
		IInfoProvider var = new BasicBiomartProvider(desktop);
		registry.registerInfoProvider(basic);
		registry.registerInfoProvider(var);
		
		desktop.getSwingEngine().getEngine().addApplicationEventListener(this);
		VPathway vp = desktop.getSwingEngine().getEngine().getActiveVPathway();
		if(vp != null) vp.addSelectionListener(this);	
	}
	
	public void selectionEvent(SelectionEvent e) {
		switch(e.type) {
		case SelectionEvent.OBJECT_ADDED:
			break;
		case SelectionEvent.OBJECT_REMOVED:
			break;
		case SelectionEvent.SELECTION_CLEARED:
			break;
		}
	}

	public void applicationEvent(ApplicationEvent e) {
		switch (e.getType()) {
		case VPATHWAY_CREATED:
			((VPathway) e.getSource()).addSelectionListener(this);
			break;
		case VPATHWAY_DISPOSED:
			((VPathway) e.getSource()).removeSelectionListener(this);
			break;
		default:
			break;
		}
	}
	
	@Override
	public void gmmlObjectModified(PathwayElementEvent e) { }
}
