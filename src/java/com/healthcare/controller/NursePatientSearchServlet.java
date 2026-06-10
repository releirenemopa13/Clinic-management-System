package com.healthcare.controller;

import com.healthcare.model.User;
import com.healthcare.model.PatientSummary;
import com.healthcare.dao.PatientDAO;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/nurse/search-patients")
public class NursePatientSearchServlet extends HttpServlet {

    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        User user = session != null ? (User) session.getAttribute("user") : null;

        if (user == null || !"NURSE".equalsIgnoreCase(user.getRole())) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().print("[]");
            return;
        }

        String term = request.getParameter("term");
        try (PrintWriter out = response.getWriter()) {
            if (term == null || term.trim().length() < 2) {
                out.print("[]");
                return;
            }

            PatientDAO patientDAO = new PatientDAO();
            List<PatientSummary> patients = patientDAO.searchPatientSummaries(term.trim());
            out.print(gson.toJson(patients));
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print("[]");
        }
    }
}
