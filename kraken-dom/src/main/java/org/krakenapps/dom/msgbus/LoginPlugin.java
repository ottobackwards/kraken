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

import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Requires;
import org.krakenapps.dom.api.AdminApi;
import org.krakenapps.dom.exception.InvalidPasswordException;
import org.krakenapps.dom.exception.AdminNotFoundException;
import org.krakenapps.dom.model.GlobalParameter;
import org.krakenapps.dom.model.Admin;
import org.krakenapps.jpa.ThreadLocalEntityManagerService;
import org.krakenapps.jpa.handler.JpaConfig;
import org.krakenapps.jpa.handler.Transactional;
import org.krakenapps.msgbus.Request;
import org.krakenapps.msgbus.Response;
import org.krakenapps.msgbus.Session;
import org.krakenapps.msgbus.handler.AllowGuestAccess;
import org.krakenapps.msgbus.handler.MsgbusMethod;
import org.krakenapps.msgbus.handler.MsgbusPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(name = "dom-login-plugin")
@MsgbusPlugin
@JpaConfig(factory = "dom")
public class LoginPlugin {
	private final Logger logger = LoggerFactory.getLogger(LoginPlugin.class.getName());

	@Requires
	private AdminApi adminApi;

	@Requires
	private ThreadLocalEntityManagerService entityManagerService;

	@AllowGuestAccess
	@MsgbusMethod
	public void login(Request req, Response resp) throws AdminNotFoundException, InvalidPasswordException {
		Session session = req.getSession();
		String nick = req.getString("nick");
		String hash = req.getString("hash");
		String nonce = (String) session.get("nonce");

		logger.info("watchcat login plugin: login attempt nick [{}] hash [{}] nonce [{}]", new Object[] { nick, hash,
				nonce });

		Admin admin = adminApi.login(nick, hash, nonce);
		resp.put("result", "success");
		resp.put("use_idle_timeout", admin.isUseIdleTimeout());
		if (admin.isUseIdleTimeout())
			resp.put("idle_timeout", admin.getIdleTimeout());

		session.unsetProperty("nonce");
		session.setProperty("org_id", admin.getUser().getOrganization().getId());
		session.setProperty("admin_id", admin.getId());
		session.setProperty("locale", admin.getLang());
	}

	@SuppressWarnings("unchecked")
	@AllowGuestAccess
	@Transactional
	@MsgbusMethod
	public void hello(Request req, Response resp) {
		String nonce = UUID.randomUUID().toString();
		String lang = req.getString("lang");
		if (lang == null)
			lang = "en";

		req.getSession().setProperty("nonce", nonce);
		req.getSession().setProperty("lang", lang);

		resp.put("nonce", nonce);
		resp.put("message", "login please.");

		EntityManager em = entityManagerService.getEntityManager();
		List<GlobalParameter> params = em.createQuery("FROM GlobalParameter g WHERE isHidden = false").getResultList();
		for (GlobalParameter p : params) {
			resp.put(p.getName(), p.getValue());
		}

	}
}
