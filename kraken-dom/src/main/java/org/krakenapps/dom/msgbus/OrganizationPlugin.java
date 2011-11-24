/*
 * Copyright 2011 Future Systems, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.krakenapps.dom.msgbus;

import java.util.Map;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Requires;
import org.krakenapps.api.PrimitiveConverter;
import org.krakenapps.dom.api.OrganizationApi;
import org.krakenapps.dom.model.Organization;
import org.krakenapps.msgbus.Request;
import org.krakenapps.msgbus.Response;
import org.krakenapps.msgbus.handler.MsgbusMethod;
import org.krakenapps.msgbus.handler.MsgbusPermission;
import org.krakenapps.msgbus.handler.MsgbusPlugin;

@Component(name = "dom-org-plugin")
@MsgbusPlugin
public class OrganizationPlugin {
	@Requires
	private OrganizationApi orgApi;

	@MsgbusMethod
	public void getOrganization(Request req, Response response) {
		String domain = req.has("domain") ? req.getString("domain") : req.getOrgDomain();
		Organization organization = orgApi.getOrganization(domain);
		response.put("result", PrimitiveConverter.serialize(organization));
	}

	@MsgbusMethod
	@MsgbusPermission(group = "dom.org", code = "manage")
	public void createOrganization(Request req, Response resp) {
		Organization organization = PrimitiveConverter.parse(Organization.class, req.getParams());
		orgApi.createOrganization(organization);
	}

	@MsgbusMethod
	@MsgbusPermission(group = "dom.org", code = "manage")
	public void updateOrganization(Request req, Response resp) {
		Organization organization = PrimitiveConverter.parse(Organization.class, req.getParams());
		orgApi.updateOrganization(organization);
	}

	@MsgbusMethod
	@MsgbusPermission(group = "dom.org", code = "manage")
	public void removeOrganization(Request req, Response resp) {
		String domain = req.getString("domain");
		orgApi.removeOrganization(domain);
	}

	@MsgbusMethod
	@MsgbusPermission(group = "dom.org", code = "manage")
	public void getOrganizationParameters(Request req, Response resp) {
		String domain = req.getString("domain");
		Map<String, Object> params = orgApi.getOrganizationParameters(domain);
		resp.put("result", PrimitiveConverter.serialize(params));
	}

	@MsgbusMethod
	@MsgbusPermission(group = "dom.org", code = "manage")
	public void getOrganizationParameter(Request req, Response resp) {
		String domain = req.getString("domain");
		String key = req.getString("key");
		Object param = orgApi.getOrganizationParameter(domain, key);
		resp.put("result", PrimitiveConverter.serialize(param));
	}

	@MsgbusMethod
	@MsgbusPermission(group = "dom.org", code = "manage")
	public void setOrganizationParameter(Request req, Response resp) {
		String domain = req.getString("domain");
		String key = req.getString("key");
		Object value = req.get("value");
		orgApi.setOrganizationParameter(domain, key, value);
	}

	@MsgbusMethod
	@MsgbusPermission(group = "dom.org", code = "manage")
	public void unsetOrganizationParameter(Request req, Response resp) {
		String domain = req.getString("domain");
		String key = req.getString("key");
		orgApi.unsetOrganizationParameter(domain, key);
	}
}
