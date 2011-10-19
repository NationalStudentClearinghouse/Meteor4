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

	public void doGet (HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		SecurityTokenManager tokenManager = SecurityTokenManager.getInstance();
		
		UUID uuid = UUID.fromString(req.getParameter("artifactId"));
		String token = tokenManager.getToken(uuid);
		
		ServletOutputStream outstream = res.getOutputStream();
		outstream.write(token.getBytes(Charset.forName("UTF-8")));
		outstream.close();
	}
}
