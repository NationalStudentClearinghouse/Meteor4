/*******************************************************************************
 * Copyright 2002 National Student Clearinghouse
 * 
 * This code is part of the Meteor system as defined and specified 
 * by the National Student Clearinghouse and the Meteor Sponsors.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 ******************************************************************************/
package org.meteornetwork.meteor.samplelogin;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ArtifactResolverServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7408386657589904993L;

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		SecurityTokenManager tokenManager = SecurityTokenManager.getInstance();

		UUID uuid = UUID.fromString(req.getParameter("artifactId"));
		String token = null;
		synchronized (tokenManager) {
			token = tokenManager.getToken(uuid);
		}

		ServletOutputStream outstream = res.getOutputStream();
		outstream.write(token.getBytes(Charset.forName("UTF-8")));
		outstream.close();
	}
}
