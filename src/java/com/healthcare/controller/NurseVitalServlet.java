package com.healthcare.controller;

import com.healthcare.model.User;
import com.healthcare.model.Vital;
import com.healthcare.model.PatientSummary;
import com.healthcare.dao.VitalDAO;
import com.healthcare.dao.PatientDAO;
import java.io.IOException;
import java.time.LocalDateTime;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/nurse/vitals")
public class NurseVitalServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = session != null ? (User) session.getAttribute("user") : null;

        if (user == null || !"NURSE".equalsIgnoreCase(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/unauthorized.jsp");
            return;
        }

        try {
            String patientIdParam = request.getParameter("patientId");

            if (patientIdParam != null && !patientIdParam.trim().isEmpty()) {
                int patientId = Integer.parseInt(patientIdParam.trim());
                PatientDAO patientDAO = new PatientDAO();
                PatientSummary patient = patientDAO.getPatientSummaryById(patientId);

                if (patient != null) {
                    VitalDAO vitalDAO = new VitalDAO();
                    request.setAttribute("patient", patient);
                    request.setAttribute("selectedPatientId", patient.getId());
                    request.setAttribute("selectedPatientName", patient.getName());
                    request.setAttribute("recentVitals", vitalDAO.getRecentVitals(patientId, 10));
                } else {
                    request.setAttribute("validationError",
                            "No patient found with the selected ID. Please search and select a valid patient.");
                }
            }

            request.getRequestDispatcher("/WEB-INF/views/nurse/vitals.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            request.setAttribute("validationError", "Invalid patient ID.");
            request.getRequestDispatcher("/WEB-INF/views/nurse/vitals.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/error.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = session != null ? (User) session.getAttribute("user") : null;

        if (user == null || !"NURSE".equalsIgnoreCase(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/unauthorized.jsp");
            return;
        }

        String patientIdParam = request.getParameter("patientId");
        String patientName = request.getParameter("patientName");

        try {
            if (patientIdParam == null || patientIdParam.trim().isEmpty()) {
                session.setAttribute("error", "Please search and select a patient before saving vitals.");
                response.sendRedirect(request.getContextPath() + "/nurse/vitals");
                return;
            }

            int patientId = Integer.parseInt(patientIdParam.trim());

            Vital vital = new Vital();
            vital.setPatientId(patientId);
            vital.setBpSystolic(Integer.parseInt(request.getParameter("bpSystolic").trim()));
            vital.setBpDiastolic(Integer.parseInt(request.getParameter("bpDiastolic").trim()));
            vital.setHeartRate(Integer.parseInt(request.getParameter("heartRate").trim()));
            vital.setTemperature(Double.parseDouble(request.getParameter("temperature").trim()));
            vital.setWeight(parseOptionalDouble(request.getParameter("weight")));
            vital.setBloodSugar(parseOptionalDouble(request.getParameter("bloodSugar")));
            vital.setRecordedAt(LocalDateTime.now());

            VitalDAO vitalDAO = new VitalDAO();
            if (vitalDAO.createVital(vital)) {
                session.setAttribute("success", "Vital signs recorded successfully!");
            } else {
                session.setAttribute("error", "Failed to record vital signs. Please try again.");
            }

            response.sendRedirect(request.getContextPath() + "/nurse/vitals?patientId=" + patientId
                    + "&patientName=" + java.net.URLEncoder.encode(patientName != null ? patientName : "", "UTF-8"));

        } catch (NumberFormatException | NullPointerException e) {
            session.setAttribute("error", "Please fill in all required vital signs with valid numbers.");
            String redirect = request.getContextPath() + "/nurse/vitals";
            if (patientIdParam != null && !patientIdParam.trim().isEmpty()) {
                redirect += "?patientId=" + patientIdParam.trim();
            }
            response.sendRedirect(redirect);
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "An error occurred while saving vital signs.");
            response.sendRedirect(request.getContextPath() + "/nurse/vitals");
        }
    }

    private double parseOptionalDouble(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0;
        }
        return Double.parseDouble(value.trim());
    }
}
