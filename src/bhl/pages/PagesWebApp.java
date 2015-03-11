/*
 * This file is part of TILT.
 *
 *  TILT is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  TILT is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with TILT.  If not, see <http://www.gnu.org/licenses/>.
 *  (c) copyright Desmond Schmidt 2014
 */

package bhl.pages;

import calliope.core.database.Connector;
import calliope.core.database.Repository;
import calliope.core.exception.CalliopeExceptionMessage;
import calliope.core.exception.CalliopeException;
import java.util.Enumeration;
import bhl.pages.handler.*;
import bhl.pages.exception.PagesException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author desmond
 */
public class PagesWebApp extends HttpServlet
{
    public static String host = "localhost";
    public static String user ="admin";
    public static String password = "jabberw0cky";
    public static int dbPort = 27017;
    public static int wsPort = 8080;
    public static String webRoot = "/var/www";
    Repository repository = Repository.MONGO;
    /**
     * Safely convert a string to a Repository enum
     * @param value the value probably a repo type
     * @param def the default if it is not
     * @return the value or the default
     */
    private Repository getRepository( String value, Repository def )
    {
        Repository res = def;
        try
        {
            res = Repository.valueOf(value);
        }
        catch ( IllegalArgumentException e )
        {
        }
        return res;
    }
    /**
     * Safely convert a string to an integer
     * @param value the value probably an integer
     * @param def the default if it is not
     * @return the value or the default
     */
    private int getInteger( String value, int def )
    {
        int res = def;
        try
        {
            res = Integer.parseInt(value);
        }
        catch ( NumberFormatException e )
        {
        }
        return res;
    }
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, java.io.IOException
    {
        try
        {
            String method = req.getMethod();
            String target = req.getRequestURI();
            Enumeration params = 
                getServletConfig().getInitParameterNames();
            while (params.hasMoreElements()) 
            {
                String param = (String) params.nextElement();
                String value = 
                    getServletConfig().getInitParameter(param);
                if ( param.equals("webRoot") )
                    webRoot = value;
                else if ( param.equals("dbPort") )
                    dbPort = getInteger(value,27017);
                else if (param.equals("wsPort"))
                    wsPort= getInteger(value,8080);
                else if ( param.equals("username") )
                    user = value;
                else if ( param.equals("password") )
                    password = value;
                else if ( param.equals("repository") )
                    repository = getRepository(value,Repository.MONGO);
                else if ( param.equals("host") )
                    host = value;
            }
            Connector.init( repository, user, 
                password, host, "bhlgaming", dbPort, wsPort, webRoot );
            target = Utils.pop( target );
            PagesGetHandler handler;
            if ( method.equals("GET") )
                handler = new PagesGetHandler();
            else
                throw new PagesException("Unknown http method "+method);
            resp.setStatus(HttpServletResponse.SC_OK);
            handler.handle( req, resp, target );
        }
        catch ( Exception e )
        {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            CalliopeException he = new CalliopeException( e );
            resp.setContentType("text/html");
            try 
            {
                resp.getWriter().println(
                    new CalliopeExceptionMessage(he).toString() );
            }
            catch ( Exception e2 )
            {
                e.printStackTrace( System.out );
            }
        }
    }
}