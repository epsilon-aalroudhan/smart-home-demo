package com.epsilon.smarthome.core.servlets;

import java.io.IOException;
import java.rmi.ServerException;
import java.util.List;

import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
//Sling Imports
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.Route;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.Workflow;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.epsilon.smarthome.core.util.JcrUtilService;

@SlingServlet(paths = "/bin/smarthome/approveWFServlet", methods = "GET", metatype = true)
@Property(name = "sling.auth.requirements", value = "-/bin/smarthome/approveWFServlet")
public class ApproveWorkflowListServlet extends SlingAllMethodsServlet {
	private static final long serialVersionUID = 2598426539166789515L;

	// Inject a Sling ResourceResolverFactory
	@Reference
	private ResourceResolverFactory resolverFactory;

	@Reference
	private JcrUtilService jcrUtilService;

	private ResourceResolver resourceResolver;

	/** Default log. */
	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServerException, IOException {

		try {

			String[] states = { "RUNNING" };

			resourceResolver = jcrUtilService.getResourceResolver();
			PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
			WorkflowSession wfSession = resourceResolver.adaptTo(WorkflowSession.class);

			Workflow[] wf = wfSession.getWorkflows(states);
			log.info("********************* WORKFLOW COUNT: " + wf.length);
			JSONArray jArray = new JSONArray();
			int count = 0;
			for (Workflow cWf : wf) {
				// Get information about the 1st running workflow
				String id = cWf.getId();
				String state = cWf.getState();
				String init = cWf.getInitiator();
				WorkflowData wd = cWf.getWorkflowData();
				String payload = (String) wd.getPayload();
				// Page page = pageManager.getContainingPage(payload);
				// Encode the submitted form data to JSON
				WorkItem workItem = wfSession.getWorkflow(id).getWorkItems().get(0);
				// getting routes
				List<Route> routes = wfSession.getRoutes(workItem, false);
				// completing or advancing to the next step
				wfSession.complete(workItem, routes.get(0));
				count++;
			}
			// Get the JSON formatted data
			// Return the JSON formatted data
			response.getWriter().write(count + " workflow(s) approved for activation");
		} catch (Exception e) {
			log.error("", e);
			response.getWriter().write("Oops!! Something went wrong");
		} finally {
			jcrUtilService.closeResolver(resourceResolver);
		}
	}
}