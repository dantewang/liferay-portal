package com.liferay.portal.cache.ehcache.configuration;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.portal.kernel.cache.PortalCacheManager;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsKeys;

@Component(
	immediate = true,
	property = {
		PortalCacheManager.PORTAL_CACHE_MANAGER_NAME + "=" + PortalCacheManagerNames.MULTI_VM
	},
	service = CacheManagerConfigurator.class
)
public class MultiVMEhcacheManagerConfigurator
	extends AbstractEhcacheManagerConfigurator {

	@Activate
	protected void activate() {
		String configFile = props.get(
			PropsKeys.EHCACHE_MULTI_VM_CONFIG_LOCATION);

		processConfigFile(configFile, _DEFAULT_CONFIG_FILE_NAME);
	}

	@Reference(unbind = "-")
	protected void setProps(Props props) {
		this.props = props;
	}

	private static final String _DEFAULT_CONFIG_FILE_NAME =
		"/ehcache/liferay-multi-vm-clustered.xml";

	protected Props props;

}