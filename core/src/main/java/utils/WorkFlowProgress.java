package utils;

import java.util.List;

import org.apache.sling.api.SlingHttpServletRequest;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.Route;
import com.adobe.granite.workflow.exec.WorkItem;

	//to do before calling class verify that the user clicked on the approve workflow button. 
public class WorkFlowProgress {
	private SlingHttpServletRequest wfRequest = null;
	
	public void WorkFlowProgress(SlingHttpServletRequest request){
		this.wfRequest = request;
	}
	
	public String wfAdvance() throws WorkflowException{
		SlingHttpServletRequest slingReq = wfRequest;
		WorkflowSession wfSession = slingReq.getResourceResolver().adaptTo(WorkflowSession.class);
		WorkItem[] workItems = wfSession.getActiveWorkItems();
		WorkItem workItem = wfSession.getWorkItem("");
		 
		// getting routes
		List<Route> routes = wfSession.getRoutes(workItem, false);
		 
		 
		// completing or advancing to the next step
		wfSession.complete(workItem, routes.get(0));
		
		return "";
	}
}
