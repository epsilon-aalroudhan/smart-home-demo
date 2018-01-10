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

import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.api.resource.ResourceResolverFactory;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Servlet that writes some sample content into the response. It is mounted for
 * all resources of a specific Sling resource type. The
 * {@link SlingSafeMethodsServlet} shall be used for HTTP methods that are
 * idempotent. For write operations use the {@link SlingAllMethodsServlet}.
 */
@SuppressWarnings("serial")
@SlingServlet(paths = "/bin/smarthome/update")
public class SmartHomeServlet extends SlingAllMethodsServlet {
	private final Logger log = LoggerFactory.getLogger(SmartHomeServlet.class);
	private String title = "";
    private String body = "";
    
    @Reference
    private ResourceResolverFactory resolverFactory;
    
	@Override
    public void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
		
		try {

            
            if(request.getParameter("title") != null || request.getParameter("body") != null) {
                this.title = request.getParameter("title");
                this.body = request.getParameter("body");
                Map<String, Object> param = new HashMap<String, Object>();  
                param.put(ResourceResolverFactory.SUBSERVICE, "readService");
                ResourceResolver resourceResolver=null;
                resourceResolver = resolverFactory.getServiceResourceResolver(param);
                
                //todo change this to the path of the page where the component sits 
                //keep the /jcr:content/sh-article
                Resource res = resourceResolver.getResource("/content/xxx/xxxx/xxxx/xxxx/jcr:content/par/sh-article");
                Node myNode = res.adaptTo(Node.class);
                
                if (this.title != null && !title.isEmpty())
                myNode.setProperty("title", this.title);
                if (this.body != null && !this.body.isEmpty())
            	myNode.setProperty("body", this.body);
                
                Session session = resourceResolver.adaptTo(Session.class);
                session.save();

            }else{
                response.getWriter().write("please either providele or body text");
            }

        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }
}
