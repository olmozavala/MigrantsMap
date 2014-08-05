/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mapviewer.business.servlets;

import com.mapviewer.tools.HtmlTools;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author olmozavala
 */
public class AddShelterServlet extends HttpServlet {

	/**
	 * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
	 * methods.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String nextPage = "/Pages/AddAlbergues.jsp";
		
		String task = request.getParameter("task");
		String language = HtmlTools.getLanguage(request.getHeader("Accept-Language"));

		if(task!=null){
			Connection con = null;
			Statement st = null;
			ResultSet rs = null;
			String url = "jdbc:postgresql://192.168.1.23/DataFest";
			String user = "postgres";
			String password = "o1q4ellv";
			try {
				Class.forName("org.postgresql.Driver");
				con = DriverManager.getConnection(url, user, password);

				String name = request.getParameter("name");
				String addr = request.getParameter("addr");
				String link = request.getParameter("link");
				String phone = request.getParameter("phone");
				String loc = request.getParameter("loc");
				String[] coord = loc.split(",");

				String sqlInsert ="INSERT INTO \"Albergues\"(nombre, pagina, direcccion, telefonos, latitude, longitude)"
										+"VALUES ('"+name+"','"+link+"','"+addr+"','"+phone+"',"+coord[1]+","+coord[0]+")";
				st = con.createStatement();
				st.executeUpdate(sqlInsert);

				String sqlUpdate = "UPDATE \"Albergues\" SET the_geom = ST_GeomFromText('SRID=4326;POINT('|| longitude ||' '|| latitude ||')')";
				st.executeUpdate(sqlUpdate);
				
				nextPage = "/mapviewer";
			} catch (SQLException ex) {
				nextPage = "/Error";
				Logger.getLogger(AddShelterServlet.class.getName()).log(Level.SEVERE, null, ex);
			} catch (ClassNotFoundException ex) {
				Logger.getLogger(AddShelterServlet.class.getName()).log(Level.SEVERE, null, ex);
			} finally {
				try {
					if (rs != null) { rs.close(); }
					if (st != null) { st.close(); }
					if (con != null) { con.close(); }
				} catch (SQLException ex) {
					Logger.getLogger(AddShelterServlet.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}		

		request.setAttribute("language", language);
		RequestDispatcher view = request.getRequestDispatcher(nextPage);
		view.forward(request, response);
		
	}
	
	// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
	/**
	 * Handles the HTTP <code>GET</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}
	
	/**
	 * Handles the HTTP <code>POST</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}
	
	/**
	 * Returns a short description of the servlet.
	 *
	 * @return a String containing servlet description
	 */
	@Override
	public String getServletInfo() {
		return "Short description";
	}// </editor-fold>
	
}
