package com.liferay.shared.dependencies.jericho.html;

import net.htmlparser.jericho.Attributes;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class JerichoHtmlBundleActivator implements BundleActivator {

	@Override
	public void start(BundleContext bundleContext) throws Exception {
		Attributes.setDefaultMaxErrorCount(20);
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
	}

}
