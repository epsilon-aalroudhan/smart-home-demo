/*
 *  Copyright 2015 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.epsilon.smarthome.core.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Session;
import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.commons.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.crypto.CryptoException;
import com.adobe.granite.crypto.CryptoSupport;
import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.Route;
import com.adobe.granite.workflow.exec.WorkItem;

/**
 * Servlet that writes some sample content into the response. It is mounted for
 * all resources of a specific Sling resource type. The
 * {@link SlingSafeMethodsServlet} shall be used for HTTP methods that are
 * idempotent. For write operations use the {@link SlingAllMethodsServlet}.
 */
@SuppressWarnings("serial")
@SlingServlet(paths = "/bin/smarthome/workflow")
@Property(name = "sling.auth.requirements", value = "-/bin/smarthome/workflow")
public class WorkFlowApprovalServlet extends SlingAllMethodsServlet {
	private final Logger log = LoggerFactory.getLogger(WorkFlowApprovalServlet.class);

	private SlingHttpServletRequest wfRequest = null;
	private ResourceResolver resourceResolver = null;
	private Session session;

	@Reference
	private ResourceResolverFactory resolverFactory;
	
	@Reference
    private CryptoSupport cryptoSupport;

	@Override
	public void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		
		wfRequest = request;
		log.debug("Entered Do Post");
		try {
			this.resourceResolver = getResourceResolver();
			
				response.getWriter().write(wfAdvance());
			

		} catch (Exception e) {
			log.error("", e);
		} finally {
			closeResolver(this.resourceResolver);
		}
	}
	
	public String wfAdvance() throws LoginException, JSONException, WorkflowException{
		if(wfRequest.getParameter("wfid") != null){
			
			SlingHttpServletRequest slingReq = wfRequest;
			String wfID = wfRequest.getParameter("wfid");
			Map<String,Object> authenticationInfo = new HashMap<String, Object>(2);
			//to do remove hard coded credentials
	        authenticationInfo.put(ResourceResolverFactory.USER, "admin");
	        
	        String unprotectedPass;
	        try {
	            unprotectedPass = cryptoSupport.unprotect("admin");
	        } catch (CryptoException e) {
	            unprotectedPass = "admin";
	            log.error(e.getMessage());
	        }
	        authenticationInfo.put(ResourceResolverFactory.PASSWORD, unprotectedPass.toCharArray());
	        ResourceResolver rResourceResolver = resolverFactory.getResourceResolver(authenticationInfo);
			session = rResourceResolver.adaptTo(Session.class);
			WorkflowSession wfSession = rResourceResolver.adaptTo(WorkflowSession.class);
			
			WorkItem workItem = wfSession.getWorkflow(wfID).getWorkItems().get(0);
			
			// getting routes
			List<Route> routes = wfSession.getRoutes(workItem, false);
	        
			// completing or advancing to the next step
			wfSession.complete(workItem, routes.get(0));
			return "Approved";
		}else{
			return "no workflow id provided";
		}
	}

	private ResourceResolver getResourceResolver() {
		ResourceResolver resourceResolver = null;
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put(ResourceResolverFactory.SUBSERVICE, "readService");
			resourceResolver = resolverFactory.getServiceResourceResolver(param);
		} catch (LoginException e) {
			log.error("Error while getting Resource Resolver....", e);
		}
		return resourceResolver;
	}

	private void closeResolver(ResourceResolver resourceResolver) {
		if (null != resourceResolver && resourceResolver.isLive()) {
			log.debug("logged out from tResourceResolver");
			resourceResolver.close();
		}
	}
}
