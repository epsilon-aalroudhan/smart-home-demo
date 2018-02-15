package com.epsilon.smarthome.core.util;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true, label = "JCR  Service", description = "JCR Service", metatype = true)
@Properties({ @Property(name = Constants.SERVICE_DESCRIPTION, value = "JCR Service"),
		@Property(name = Constants.SERVICE_VENDOR, value = "Smart Home"),
		@Property(name = Constants.SERVICE_RANKING, intValue = 100) })
@Service(JcrUtilService.class)
/**
 * @description This JcrUtilServicen class acts as OSGI Service which gives JCR
 *              resourceResolver and Session Objects to access and manipulate
 *              the content in the repository
 */
public class JcrUtilService {

	private static final Logger LOG = LoggerFactory.getLogger(JcrUtilService.class);

	@Reference
	private SlingRepository slingRepository;
	@Reference
	private ResourceResolverFactory resolverFactory;

	/**
	 * 
	 * @return @link {@link Session} Object Instance
	 */
	public Session getSession() {
		Session session = null;
		try {
			// session=repository.loginAdministrative(null);
			session = slingRepository.loginService("readService", null);
		} catch (RepositoryException e) {
			LOG.error("Error while getting session....", e);
		}
		return session;
	}

	public void logout(Session session) {
		if (null != session && session.isLive()) {
			LOG.debug("logged out from session");
			session.logout();
		}
	}

	/**
	 * 
	 * @return {@link ResourceResolver} Instance
	 */
	public ResourceResolver getResourceResolver() {
		ResourceResolver resourceResolver = null;
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put(ResourceResolverFactory.SUBSERVICE, "readService");
			resourceResolver = resolverFactory.getServiceResourceResolver(param);
		} catch (LoginException e) {
			LOG.error("Error while getting Resource Resolver....", e);
		}
		return resourceResolver;
	}

	public void closeResolver(ResourceResolver resourceResolver) {
		if (null != resourceResolver && resourceResolver.isLive()) {
			LOG.debug("logged out from tResourceResolver");
			resourceResolver.close();
		}
	}

	@Activate
	protected void activate(final ComponentContext componentContext)
			throws RepositoryException, org.apache.sling.api.resource.LoginException {
		LOG.info("JCR Util Service :: Activate Method Service");
		if (null != slingRepository) {
			LOG.debug("slingRepository Service is Initialized...");
		}
		if (null != resolverFactory) {
			LOG.debug("resourceResolverFactory Service is Initialized...");
		}
	}

	/**
	 * Used to get the Session by passing UserName and Password
	 * 
	 * @param userName
	 * @param password
	 * @return {@link Session} Object
	 * @throws RepositoryException
	 */

	@Deactivate
	protected void deactivate(final ComponentContext componentContext) {
		LOG.info("JCR Util Service :: Unregistering Service");
		try {
			System.gc();
		} catch (Exception e) {
			LOG.error("Error while clearing session or resourceResolver Objects", e);
		}
	}

	public SlingRepository getRepository() {
		return slingRepository;
	}
}
