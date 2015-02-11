/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.pluto.container.impl;

import java.util.Enumeration;
import java.util.Map;

import javax.portlet.CacheControl;
import javax.portlet.PortletRequest;
import javax.portlet.ResourceRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.pluto.container.PortletResourceRequestContext;
import org.apache.pluto.container.PortletResourceResponseContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceRequestImpl extends ClientDataRequestImpl implements ResourceRequest
{

   /** Internal logger. */
   private static final Logger LOG = LoggerFactory.getLogger(ResourceRequestImpl.class);

    private PortletResourceRequestContext requestContext;
    private CacheControl cacheControl;
    
    public ResourceRequestImpl(PortletResourceRequestContext requestContext, PortletResourceResponseContext responseContext)
    {
        super(requestContext, responseContext, PortletRequest.RESOURCE_PHASE);
        this.requestContext = requestContext;
        this.cacheControl = responseContext.getCacheControl();
    }
    
    @Override
    public String getProperty(String name)
    {
        String result = getMimeRequestProperty(name, cacheControl);
        return result != null ? result : super.getProperty(name);
    }

    public String getCacheability()
    {
        return requestContext.getCacheability();
    }

    public String getETag()
    {
        return cacheControl.getETag();
    }

    public Map<String, String[]> getPrivateRenderParameterMap()
    {
        return cloneParameterMap(requestContext.getPrivateRenderParameterMap());
    }

    public String getResourceID()
	{
		return requestContext.getResourceID();
	}

    public String getResponseContentType()
    {
        return getServletRequest().getHeader("accept");
    }

    @SuppressWarnings("unchecked")
    public Enumeration<String> getResponseContentTypes()
    {
        return getServletRequest().getHeaders("accept");
    }

   /* (non-Javadoc)
    * @see javax.portlet.ResourceRequest#getPageState()
    */
   public String getPageState() {
      // The page state is provided through the request context.
      String ps = requestContext.getPageState();
      if (LOG.isDebugEnabled()) {
         LOG.debug("ResourceRequest#getPageState called.");
      }
      return ps;
   }
   
   // Debug code - intercept getParameter call & dump all parameters to trace
   @Override
   public String getParameter(String name) {
      String val = super.getParameter(name);
      if (LOG.isDebugEnabled()) {
         Map<String, String[]> pmap = super.getParameterMap();
         StringBuffer txt = new StringBuffer(1024);
         txt.append("Resource Request parameter map dump:");
         for (String n : pmap.keySet()) {
            txt.append("\nName: " + n + ", Values: ");
            String[] vals = pmap.get(n);
            String sep = "";
            for (String v : vals) {
               txt.append(sep + v);
               sep = ", ";
            }
         }
         LOG.debug(txt.toString());
      }
      return val;
   }
}
