/***************************************
 * Copyright (c) 2008
 * Bjoern Wagner
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 ****************************************/

package org.b3mn.poem.handler;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.HttpsURL;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.b3mn.poem.Identity;
import org.b3mn.poem.business.User;
import org.b3mn.poem.util.HandlerWithoutModelContext;
import org.json.JSONException;
import org.json.JSONObject;

@HandlerWithoutModelContext(uri = "/fblogin")
public class FacebookLogin extends LoginHandler {
	public static final SimpleDateFormat df = new SimpleDateFormat( "yyyy/MM/dd");

	public static class Facebook{
		/*
		 * secret stuff
		 */
		public static final String API_KEY = "da18bd2a5256c4a1a3da8061613bb40f";
		public static final String CLIENT_ID = "143588048986685";
		public static final String CLIENT_SECRET = "c0e728be5ca9a46ff573ed4fe113c171";
	    private static final String URI_EXT = "fblogin";
	    private static final String REDIRECT_URI = "http://localhost:8080/backend/poem/fblogin";

	    /// set this to the list of extended permissions you want
	    private static final String[] perms = new String[] {"user_birthday", "email", "user_hometown"};

	    public static String getAPIKey() {
	        return API_KEY;
	    }

	    public static String getSecret() {
	        return CLIENT_SECRET;
	    }
	    public static String getUriExt(){
	    	return URI_EXT;
	    }
	    public static String getLoginRedirectURL() {
	    	String result=GRAPH_URL+"oauth/authorize?client_id=" +
	    	CLIENT_ID + "&display=page" +"&scope=";
	    	for(int i=0;i<(perms.length-1);i++){
	    		result+=perms[i]+",";
	    	}
			result+=perms[perms.length-1];
			return result+"&redirect_uri=";

	    }

	    public static String getAuthURL(String authCode) {
	        return GRAPH_URL+"oauth/access_token?type=web_server&client_id=" +
	        CLIENT_ID+"&redirect_uri=" +
	            REDIRECT_URI+"&client_secret="+CLIENT_SECRET+"&code="+authCode;
	    }
	}
	private static final String USER = "_user";
	private static final String LOCALE = "locale";
	private static final String GENDER = "gender";
	private static final String MAIL = "mail";
	private static final String LAST_NAME = "last_name";
	private static final String FIRST_NAME = "first_name";
	private static final String NAME = "name";
	private static final String GRAPH_URL = "https://graph.facebook.com/";

	@Override
	public void init() {
		ServletContext context = getServletContext();
		
		setupProxy(context);
	}

	/*
	 * fb_login fname _opener guid 0.8449521668519836 installed 1 session
	 * {"session_key"
	 * :"2.ohxlbGGa7yqSCbD1XhjVEw__.3600.1279216800-1422022193","uid"
	 * :1422022193,
	 * "expires":1279216800,"secret":"ilIvo2OQ_4T8HKNhrmI3yQ__","sig"
	 * :"83988080805e70eb8aff65abb8e9830a"}
	 */
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res,
			Identity s, Identity object) throws Exception {
		String rPage = req.getParameter("redirect");
		tryToLogin(req, res, rPage);
	}

	/**
	 * Tries to login the given request over facebook
	 * @param req
	 * @param res
	 * @param rPage
	 * @throws JSONException
	 * @throws URIException
	 * @throws IOException
	 * @throws HttpException
	 * @throws Exception
	 * @throws ServletException
	 */
	private void tryToLogin(HttpServletRequest req, HttpServletResponse res,
			String rPage) throws JSONException, URIException, IOException,
			HttpException, Exception, ServletException {
		User user = doFilter(req);
		if (user == null) {
			res.setStatus(HttpStatus.SC_FORBIDDEN);
		} else {

			// authentication successful.
			user.addAuthentificationAttributes(this.getServletContext(), req,
					res);
			user.login(req, res);
			res.sendRedirect( rPage != null ? rPage : REPOSITORY_REDIRECT);
		}
	}

	/**
	 * Retrieves the given ID from the Identity manager,
	 * if no user is linked, than try to contact facebook for user information
	 * and create a new user
	 * @param id
	 * @param json
	 * @return
	 * @throws URIException
	 * @throws IOException
	 * @throws HttpException
	 * @throws Exception
	 */
	public static User getFBUser(String id) throws Exception {
		Identity subject = Identity.instance(id);
		if (subject == null) {
			HttpsURL url = new HttpsURL(GRAPH_URL + id);
			HttpClient client = new HttpClient();
			GetMethod get = new GetMethod();
			get.setURI(url);
			client.executeMethod(get);
			return parseResponse(get);
		}
		User user = new User(id);
		return user;
	}

	/**
	 * Retrieves the facebook user id from the given cookie array and tries to load the
	 * corresponding user from the database
	 * @param cookies
	 * @return
	 * @throws Exception
	 */
	public static User getFBUserFromCookies(Cookie[] cookies) throws Exception {
		if (cookies == null) {
			return null;
		}
		for (Cookie c : cookies) {
			if ((Facebook.getAPIKey() + USER).equals(c.getName())) {
				return getFBUser(c.getValue());
			}
		}
		return null;
	}

	/**
	 * Each facebook relevant key, beginning with the api key, get unset
	 * @param req
	 * @param res
	 */
	public static void removeAuthInformation(HttpServletRequest req,
			HttpServletResponse res) {
		for (Cookie c : req.getCookies()) {
			if (c.getName().startsWith(Facebook.getAPIKey())) {
				Cookie newC = new Cookie(c.getName(), " ");
				newC.setMaxAge(0);
				res.addCookie(newC);
			}
		}

	}
	
	
    public User doFilter(HttpServletRequest request) throws IOException, ServletException {
        String code = request.getParameter("code");
//        code=java.net.URLEncoder.encode(code, "UTF-8");
        if (!"".equals(code)) {
            String authURL = Facebook.getAuthURL(code);
            try {
    			HttpsURL url = new HttpsURL(authURL);
    			HttpClient client = new HttpClient();
    			GetMethod get = new GetMethod();
    			get.setURI(url);
    			
    			client.executeMethod(get);
    			
                String result = get.getResponseBodyAsString();
                String accessToken = null;
                Integer expires = null;
                String[] pairs = result.split("&");
                for (String pair : pairs) {
                    String[] kv = pair.split("=");
                    if (kv.length != 2) {
                        throw new RuntimeException("Unexpected auth response" + result);
                    } else {
                        if (kv[0].equals("access_token")) {
                            accessToken = kv[1];
                        }
                        if (kv[0].equals("expires")) {
                            expires = Integer.valueOf(kv[1]);
                        }
                    }
                }
                if (accessToken != null) {
                   return authFacebookLogin(accessToken, expires);
                } else {
                    return null;
                }
            } catch (IOException e) {
               return null;
            }
        }
		return null;
    }
	private User authFacebookLogin(String accessToken, Integer expires)  {
        String graphUri="https://graph.facebook.com/me?access_token=" + accessToken;
        try {
			HttpsURL url = new HttpsURL(graphUri);
			HttpClient client = new HttpClient();
			GetMethod get = new GetMethod();
			get.setURI(url);
			
			client.executeMethod(get);
			
			return parseResponse(get);
		} catch (Exception e) {
           return null;
		}
		
	}

	/**
	 * @param get
	 * @return
	 * @throws JSONException
	 * @throws IOException
	 * @throws Exception
	 */
	private static User parseResponse(GetMethod get) throws JSONException,
			IOException, Exception {
		JSONObject json = new JSONObject(get.getResponseBodyAsString());
		Date parse=null;
		try {
			parse = df.parse(json.optString("birthday "));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String openId = json.getString("id");

		Identity subject = Identity.instance(openId);
		if (subject == null) {
			return User.CreateNewUser(openId, json.getString(NAME), json
					.getString(FIRST_NAME)
					+ " " + json.optString(LAST_NAME), json.optString(MAIL),
					parse
					, json.optString(GENDER), null, json.optString("hometown"), null, json
							.optString(LOCALE), json.optString(LOCALE), null,
					null);
		}
		return new User(openId);
	}

    

}
