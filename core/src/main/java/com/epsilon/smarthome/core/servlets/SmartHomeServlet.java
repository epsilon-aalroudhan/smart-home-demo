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
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.model.WorkflowModel;

/**
 * Servlet that writes some sample content into the response. It is mounted for
 * all resources of a specific Sling resource type. The
 * {@link SlingSafeMethodsServlet} shall be used for HTTP methods that are
 * idempotent. For write operations use the {@link SlingAllMethodsServlet}.
 */
@SuppressWarnings("serial")
@SlingServlet(paths = "/bin/smarthome/update")
@Property(name = "sling.auth.requirements", value = "-/bin/smarthome/update")
public class SmartHomeServlet extends SlingAllMethodsServlet {
	private final Logger log = LoggerFactory.getLogger(SmartHomeServlet.class);
	private String title = "";
	private String body = "";
	private SlingHttpServletRequest wfRequest = null;
	private ResourceResolver resourceResolver = null;
	private Session session;

	@Reference
	private ResourceResolverFactory resolverFactory;

	@Override
	public void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {

		wfRequest = request;
		log.debug("Entered Do Post");
		try {
			this.resourceResolver = getResourceResolver();
			if (request.getParameter("title") != null || request.getParameter("body") != null) {
				this.title = request.getParameter("title");
				this.body = request.getParameter("body");
				// todo change this to the path of the page where the component sits
				// keep the /jcr:content/sh-article
				Resource res = this.resourceResolver.getResource("/content/smart-home/en/jcr:content/par/sh_article");
				if (res != null) {
					Node myNode = res.adaptTo(Node.class);

					if (this.title != null && !title.isEmpty())
						myNode.setProperty("title", this.title);
					if (this.body != null && !this.body.isEmpty())
						myNode.setProperty("body", this.body);

					Session session = this.resourceResolver.adaptTo(Session.class);
					session.save();
					WorkflowSession wfSession = resourceResolver.adaptTo(WorkflowSession.class);
					// Get the workflow model
					WorkflowModel wfModel = wfSession.getModel("/etc/workflow/models/request_for_smart_activation/jcr:content/model");

					// Get the workflow data
					// The first param in the newWorkflowData method is the payloadType. Just a
					// fancy name to let it know what type of workflow it is working with.
					WorkflowData wfData = wfSession.newWorkflowData("JCR_PATH", "/content/smart-home/en");
					// Run the Workflow.
					wfSession.startWorkflow(wfModel, wfData);
					response.getWriter().write("Title and Body Updated successfully");
				}
			} else {
				response.getWriter().write("Please provide title or body");
			}

		} catch (Exception e) {
			log.error("", e);
			response.getWriter().write("Error while updating content");
		} finally {
			closeResolver(this.resourceResolver);
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
