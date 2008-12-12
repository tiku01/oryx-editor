<%-- 
    Document   : index
    Created on : 19.10.2008, 15:03:50
    Author     : fwe
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.sql.*"%>
<%@page import="java.io.*"%>
<%@page import="java.util.*"%>
 
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Register</title>
    </head>
    
<body alink="#ffffff" link="#ffffff" vlink="#ffffff">
<h2>&nbsp;</h2>
        
        <%
        try
        {
            Class.forName("org.gjt.mm.mysql.Driver");
        }
            
        catch(ClassNotFoundException ex)
        {
            System.out.println("JDBC Treiber ist leider nicht zu finden");
        }
       try{ 
        //Verbindung 
         Connection con =DriverManager.getConnection("jdbc:mysql://localhost:3306/root", "root", "root");
        
        Statement stat =con.createStatement();
        
     
        String firstname =request.getParameter("vorname");
        String lastname =request.getParameter("nachname");
        String username =request.getParameter("user");
        String psw = request.getParameter("psw");
        String email=request.getParameter("email");
        
        
        
       
        Statement st = con.createStatement();
        st.executeUpdate("INSERT INTO customer (First_Name,Last_Name,User_Name,Psw,Email) " +
                "VALUES('"+firstname+"','"+lastname+"','"+username+"','"+psw+"','"+email+"')");
        
        
        
        
       
    

        stat.close();
        con.close();
      }

catch (SQLException sql)
{                     
 System.out.println("FEHLER IN DER DB"); 
System.out.println("SQL-Fehler: " + sql);     
        }
      
        
%>
        
        
        
        
    </body>
</html>
