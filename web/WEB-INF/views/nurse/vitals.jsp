<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.healthcare.model.Vital, java.util.*" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Record Vitals - Nurse Dashboard</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        body {
            background-color: #f8f9fa;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
        .sidebar {
            background: linear-gradient(135deg, #20B2AA 0%, #008080 100%);
            min-height: 100vh;
            color: white;
            position: fixed;
            left: 0;
            top: 0;
            width: 260px;
            z-index: 1000;
        }
        .sidebar .nav-link {
            color: rgba(255, 255, 255, 0.8);
            border-radius: 10px;
            margin: 5px 10px;
            transition: all 0.3s ease;
            padding: 10px 15px;
        }
        .sidebar .nav-link:hover, .sidebar .nav-link.active {
            background: rgba(255, 255, 255, 0.15);
            color: white;
        }
        .main-content {
            margin-left: 260px;
            padding: 20px;
        }
        .form-card {
            background: white;
            border-radius: 15px;
            padding: 25px;
            margin-bottom: 20px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.08);
        }
        .vital-history-card {
            background: white;
            border-radius: 15px;
            padding: 20px;
            margin-bottom: 20px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.08);
        }
        .vital-sign {
            background: #f8f9fa;
            border-radius: 10px;
            padding: 12px;
            text-align: center;
            transition: all 0.2s;
        }
        .vital-sign:hover {
            background: #e9ecef;
            transform: translateY(-2px);
        }
        .input-group-custom {
            margin-bottom: 15px;
        }
        .input-group-custom label {
            font-weight: 500;
            margin-bottom: 5px;
            color: #2c3e50;
        }
        .input-group-custom input, .input-group-custom select, .input-group-custom textarea {
            border-radius: 8px;
            border: 1px solid #ddd;
            padding: 10px;
            width: 100%;
        }
        .abnormal-value {
            color: #dc3545;
            font-weight: bold;
        }
        .normal-value {
            color: #28a745;
        }
        .btn-submit {
            background: linear-gradient(135deg, #20B2AA 0%, #008080 100%);
            color: white;
            border: none;
            padding: 12px 30px;
            border-radius: 25px;
            font-weight: 500;
        }
        .btn-submit:hover:not(:disabled) {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(32, 178, 170, 0.3);
        }
        .btn-submit:disabled {
            opacity: 0.6;
            cursor: not-allowed;
        }
        .search-container {
            position: relative;
        }
        .search-results {
            position: absolute;
            top: 100%;
            left: 0;
            right: 0;
            max-height: 300px;
            overflow-y: auto;
            background: white;
            border: 1px solid #ddd;
            border-radius: 8px;
            z-index: 1000;
            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
            display: none;
        }
        .search-result-item {
            padding: 10px 15px;
            cursor: pointer;
            border-bottom: 1px solid #eee;
            transition: background 0.2s;
        }
        .search-result-item:hover {
            background-color: #f0f8ff;
        }
        .search-result-item strong {
            color: #20B2AA;
        }
        .search-result-item small {
            color: #6c757d;
            font-size: 12px;
        }
        .readonly-field {
            background-color: #e9ecef;
            cursor: not-allowed;
        }
        .selected-patient-card {
            background: linear-gradient(135deg, #d4edda 0%, #c3e6cb 100%);
            border-left: 4px solid #28a745;
            margin-bottom: 20px;
        }
        .loading-spinner {
            display: inline-block;
            width: 20px;
            height: 20px;
            border: 3px solid #f3f3f3;
            border-top: 3px solid #20B2AA;
            border-radius: 50%;
            animation: spin 1s linear infinite;
        }
        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }
    </style>
</head>
<body>
    <div class="container-fluid">
        <div class="row">
            <!-- Sidebar -->
            <div class="sidebar p-3">
                <div class="text-center mb-4">
                    <i class="fas fa-user-nurse fa-2x mb-2"></i>
                    <h5>HealthCare Plus</h5>
                    <small class="text-white-50">Nurse Portal</small>
                </div>
                
                <nav class="nav flex-column">
                    <a class="nav-link" href="${pageContext.request.contextPath}/nurse/dashboard">
                        <i class="fas fa-tachometer-alt me-2"></i>Dashboard
                    </a>
                    <a class="nav-link" href="${pageContext.request.contextPath}/nurse/patients">
                        <i class="fas fa-users me-2"></i>My Patients
                    </a>
                    <a class="nav-link" href="${pageContext.request.contextPath}/nurse/tasks">
                        <i class="fas fa-tasks me-2"></i>Tasks
                    </a>
                    <a class="nav-link active" href="${pageContext.request.contextPath}/nurse/vitals">
                        <i class="fas fa-heartbeat me-2"></i>Record Vitals
                    </a>
                    <a class="nav-link" href="${pageContext.request.contextPath}/nurse/shifts">
                        <i class="fas fa-clock me-2"></i>My Shifts
                    </a>
                    <a class="nav-link" href="${pageContext.request.contextPath}/nurse/profile">
                        <i class="fas fa-user me-2"></i>Profile
                    </a>
                    <hr class="my-3" style="border-color: rgba(255,255,255,0.2);">
                    <a class="nav-link" href="${pageContext.request.contextPath}/logout">
                        <i class="fas fa-sign-out-alt me-2"></i>Logout
                    </a>
                </nav>
            </div>
            
            <!-- Main Content -->
            <div class="main-content">
                <!-- Header -->
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <div>
                        <h2><i class="fas fa-heartbeat me-2"></i>Record Vital Signs</h2>
                        <p class="text-muted mb-0">Enter and track patient vital signs</p>
                    </div>
                    <div>
                        <span class="badge bg-success">
                            <i class="fas fa-circle me-1" style="font-size: 8px;"></i>
                            Online
                        </span>
                    </div>
                </div>
                
                <!-- Display validation error if patient doesn't exist -->
                <c:if test="${not empty validationError}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        <i class="fas fa-exclamation-triangle me-2"></i>
                        <strong>Error:</strong> ${validationError}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </c:if>
                
                <!-- Display success/error messages from session -->
                <c:if test="${not empty sessionScope.success}">
                    <div class="alert alert-success alert-dismissible fade show">
                        <i class="fas fa-check-circle me-2"></i> ${sessionScope.success}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                    <% session.removeAttribute("success"); %>
                </c:if>
                
                <c:if test="${not empty sessionScope.error}">
                    <div class="alert alert-danger alert-dismissible fade show">
                        <i class="fas fa-exclamation-triangle me-2"></i> ${sessionScope.error}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                    <% session.removeAttribute("error"); %>
                </c:if>
                
                <div class="row">
                    <!-- Vital Signs Form -->
                    <div class="col-md-5">
                        <div class="form-card">
                            <h5 class="mb-4"><i class="fas fa-edit me-2 text-primary"></i>Enter Vital Signs</h5>
                            
                            <form id="vitalsForm" method="POST" action="${pageContext.request.contextPath}/nurse/vitals">
                                <!-- Patient Search -->
                                <div class="input-group-custom">
                                    <label><i class="fas fa-search me-2"></i>Search Patient *</label>
                                    <div class="search-container">
                                        <input type="text" class="form-control" id="patientSearch" 
                                               placeholder="Type at least 2 characters to search..." autocomplete="off">
                                        <div id="searchResults" class="search-results"></div>
                                    </div>
                                    <small class="text-muted">Search by patient name to get the correct ID</small>
                                </div>
                                
                                <!-- Patient Details (Read-only) -->
                                <div class="input-group-custom">
                                    <label><i class="fas fa-user me-2"></i>Patient Name</label>
                                    <input type="text" class="form-control readonly-field" name="patientName" id="patientName" 
                                           value="${selectedPatientName}" readonly>
                                </div>
                                
                                <div class="input-group-custom">
                                    <label><i class="fas fa-id-card me-2"></i>Patient ID</label>
                                    <input type="number" class="form-control readonly-field" name="patientId" id="patientId" 
                                           value="${selectedPatientId}" readonly>
                                </div>
                                
                                <!-- Selected Patient Info -->
                                <c:if test="${not empty patient}">
                                    <div class="alert alert-success selected-patient-card">
                                        <i class="fas fa-check-circle me-2"></i>
                                        <strong>Selected Patient:</strong> ${patient.name}<br>
                                        <small>Patient ID: ${patient.id} | Email: ${patient.email}</small>
                                    </div>
                                </c:if>
                                
                                <hr>
                                
                                <!-- Vital Signs -->
                                <div class="row">
                                    <div class="col-md-6">
                                        <div class="input-group-custom">
                                            <label><i class="fas fa-tachometer-alt me-2"></i>BP Systolic *</label>
                                            <input type="number" class="form-control" name="bpSystolic" id="bpSystolic" 
                                                   placeholder="e.g., 120" required>
                                            <small class="text-muted">Normal: 90-120</small>
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div class="input-group-custom">
                                            <label><i class="fas fa-tachometer-alt me-2"></i>BP Diastolic *</label>
                                            <input type="number" class="form-control" name="bpDiastolic" id="bpDiastolic" 
                                                   placeholder="e.g., 80" required>
                                            <small class="text-muted">Normal: 60-80</small>
                                        </div>
                                    </div>
                                </div>
                                
                                <div class="row">
                                    <div class="col-md-6">
                                        <div class="input-group-custom">
                                            <label><i class="fas fa-heartbeat me-2"></i>Heart Rate *</label>
                                            <input type="number" class="form-control" name="heartRate" id="heartRate" 
                                                   placeholder="e.g., 72" required>
                                            <small class="text-muted">Normal: 60-100 bpm</small>
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div class="input-group-custom">
                                            <label><i class="fas fa-thermometer-half me-2"></i>Temperature (°C) *</label>
                                            <input type="number" step="0.1" class="form-control" name="temperature" id="temperature" 
                                                   placeholder="e.g., 36.5" required>
                                            <small class="text-muted">Normal: 36.1-37.2°C</small>
                                        </div>
                                    </div>
                                </div>
                                
                                <div class="row">
                                    <div class="col-md-6">
                                        <div class="input-group-custom">
                                            <label><i class="fas fa-weight-scale me-2"></i>Weight (kg)</label>
                                            <input type="number" step="0.1" class="form-control" name="weight" id="weight" 
                                                   placeholder="e.g., 70.5">
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div class="input-group-custom">
                                            <label><i class="fas fa-droplet me-2"></i>Blood Sugar (mg/dL)</label>
                                            <input type="number" step="0.1" class="form-control" name="bloodSugar" id="bloodSugar" 
                                                   placeholder="e.g., 95">
                                            <small class="text-muted">Fasting: 70-100</small>
                                        </div>
                                    </div>
                                </div>
                                
                                <div class="input-group-custom">
                                    <label><i class="fas fa-stethoscope me-2"></i>Additional Notes</label>
                                    <textarea class="form-control" name="notes" rows="3" 
                                              placeholder="Any additional observations..."></textarea>
                                </div>
                                
                                <div class="text-center mt-4">
                                    <button type="submit" class="btn btn-submit" id="submitBtn" disabled>
                                        <i class="fas fa-save me-2"></i> Save Vital Signs
                                    </button>
                                    <button type="button" class="btn btn-secondary ms-2" onclick="resetForm()">
                                        <i class="fas fa-undo me-2"></i> Reset
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                    
                    <!-- Recent Vitals History -->
                    <div class="col-md-7">
                        <div class="vital-history-card">
                            <div class="d-flex justify-content-between align-items-center mb-4">
                                <h5 class="mb-0"><i class="fas fa-history me-2 text-primary"></i>Recent Vital Signs</h5>
                                <c:if test="${not empty selectedPatientName}">
                                    <span class="badge bg-info">${selectedPatientName}</span>
                                </c:if>
                            </div>
                            
                            <c:choose>
                                <c:when test="${empty recentVitals}">
                                    <div class="text-center py-4">
                                        <i class="fas fa-chart-line fa-3x text-muted mb-3"></i>
                                        <p class="text-muted">No vital signs recorded yet.</p>
                                        <p class="text-muted small">Search and select a patient to see their history.</p>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="table-responsive">
                                        <table class="table table-hover">
                                            <thead>
                                                <tr>
                                                    <th>Date/Time</th>
                                                    <th>BP</th>
                                                    <th>HR</th>
                                                    <th>Temp</th>
                                                    <th>Weight</th>
                                                    <th>BS</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:forEach var="vital" items="${recentVitals}">
                                                    <tr>
                                                        <td><small>${vital.recordedAt}</small></td>
                                                        <td class="${vital.bpSystolic > 140 or vital.bpSystolic < 90 ? 'abnormal-value' : 'normal-value'}">
                                                            ${vital.bpSystolic}/${vital.bpDiastolic}
                                                        </td>
                                                        <td class="${vital.heartRate > 100 or vital.heartRate < 60 ? 'abnormal-value' : 'normal-value'}">
                                                            ${vital.heartRate}
                                                        </td>
                                                        <td class="${vital.temperature > 37.2 or vital.temperature < 36.1 ? 'abnormal-value' : 'normal-value'}">
                                                            ${vital.temperature}°C
                                                        </td>
                                                        <td>${vital.weight != null ? vital.weight : '-'} kg</td>
                                                        <td class="${vital.bloodSugar > 140 or vital.bloodSugar < 70 ? 'abnormal-value' : 'normal-value'}">
                                                            ${vital.bloodSugar != null ? vital.bloodSugar : '-'}
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        
                        <!-- Normal Ranges Reference -->
                        <div class="vital-history-card">
                            <h6 class="mb-3"><i class="fas fa-info-circle me-2 text-info"></i>Normal Vital Sign Ranges</h6>
                            <div class="row">
                                <div class="col-md-6">
                                    <div class="vital-sign">
                                        <i class="fas fa-tachometer-alt text-primary fa-lg"></i>
                                        <strong>Blood Pressure</strong>
                                        <div class="small">Normal: 90-120 / 60-80</div>
                                        <div class="small text-warning">Elevated: 120-129 / &lt;80</div>
                                        <div class="small text-danger">High: ≥130 / ≥80</div>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="vital-sign">
                                        <i class="fas fa-heartbeat text-danger fa-lg"></i>
                                        <strong>Heart Rate</strong>
                                        <div class="small">Normal: 60-100 bpm</div>
                                        <div class="small text-warning">Bradycardia: &lt;60</div>
                                        <div class="small text-warning">Tachycardia: &gt;100</div>
                                    </div>
                                </div>
                                <div class="col-md-6 mt-2">
                                    <div class="vital-sign">
                                        <i class="fas fa-thermometer-half text-info fa-lg"></i>
                                        <strong>Temperature</strong>
                                        <div class="small">Normal: 36.1-37.2°C</div>
                                        <div class="small text-warning">Low-grade fever: 37.3-38°C</div>
                                        <div class="small text-danger">Fever: ≥38°C</div>
                                    </div>
                                </div>
                                <div class="col-md-6 mt-2">
                                    <div class="vital-sign">
                                        <i class="fas fa-droplet text-success fa-lg"></i>
                                        <strong>Blood Sugar</strong>
                                        <div class="small">Fasting: 70-100 mg/dL</div>
                                        <div class="small">After meals: &lt;140 mg/dL</div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        const contextPath = '${pageContext.request.contextPath}';
        const patientSearch = document.getElementById('patientSearch');
        const searchResults = document.getElementById('searchResults');
        let searchTimeout;
        
        // Patient Search Function
        patientSearch.addEventListener('input', function() {
            clearTimeout(searchTimeout);
            const searchTerm = this.value.trim();
            
            if (searchTerm.length < 2) {
                searchResults.style.display = 'none';
                return;
            }
            
            searchTimeout = setTimeout(function() {
                // Show loading
                searchResults.innerHTML = '<div class="search-result-item text-muted"><div class="loading-spinner"></div> Searching...</div>';
                searchResults.style.display = 'block';
                
                fetch(`${contextPath}/nurse/search-patients?term=${encodeURIComponent(searchTerm)}`)
                    .then(response => response.json())
                    .then(patients => {
                        if (!patients || patients.length === 0) {
                            searchResults.innerHTML = '<div class="search-result-item text-muted">No patients found. Try a different name.</div>';
                            return;
                        }
                        
                        searchResults.innerHTML = patients.map(patient => `
                            <div class="search-result-item" onclick="selectPatient(${patient.id}, '${escapeHtml(patient.name)}')">
                                <strong>${escapeHtml(patient.name)}</strong><br>
                                <small>Patient ID: ${patient.id} | Email: ${escapeHtml(patient.email || 'N/A')}</small>
                            </div>
                        `).join('');
                    })
                    .catch(error => {
                        console.error('Search error:', error);
                        searchResults.innerHTML = '<div class="search-result-item text-danger">Search failed. Please try again.</div>';
                    });
            }, 300);
        });
        
        // Select Patient Function
        function selectPatient(patientId, patientName) {
            document.getElementById('patientId').value = patientId;
            document.getElementById('patientName').value = patientName;
            patientSearch.value = patientName;
            searchResults.style.display = 'none';
            document.getElementById('submitBtn').disabled = false;
            
            // Reload page with selected patient
            window.location.href = `${contextPath}/nurse/vitals?patientId=${patientId}&patientName=${encodeURIComponent(patientName)}`;
        }
        
        // Escape HTML
        function escapeHtml(text) {
            const div = document.createElement('div');
            div.textContent = text;
            return div.innerHTML;
        }
        
        // Reset Form
        function resetForm() {
            document.getElementById('vitalsForm').reset();
            document.getElementById('patientId').value = '';
            document.getElementById('patientName').value = '';
            document.getElementById('patientSearch').value = '';
            document.getElementById('submitBtn').disabled = true;
            
            // Reset border colors
            ['bpSystolic', 'bpDiastolic', 'heartRate', 'temperature'].forEach(id => {
                const el = document.getElementById(id);
                if (el) el.style.borderColor = '#ddd';
            });
        }
        
        // Real-time validation for vital signs
        function checkVitalRanges() {
            const bpSystolic = document.getElementById('bpSystolic').value;
            const bpDiastolic = document.getElementById('bpDiastolic').value;
            const heartRate = document.getElementById('heartRate').value;
            const temperature = document.getElementById('temperature').value;
            
            document.getElementById('bpSystolic').style.borderColor = 
                (bpSystolic && (bpSystolic > 140 || bpSystolic < 90)) ? '#dc3545' : (bpSystolic ? '#28a745' : '#ddd');
            
            document.getElementById('bpDiastolic').style.borderColor = 
                (bpDiastolic && (bpDiastolic > 90 || bpDiastolic < 60)) ? '#dc3545' : (bpDiastolic ? '#28a745' : '#ddd');
            
            document.getElementById('heartRate').style.borderColor = 
                (heartRate && (heartRate > 100 || heartRate < 60)) ? '#dc3545' : (heartRate ? '#28a745' : '#ddd');
            
            document.getElementById('temperature').style.borderColor = 
                (temperature && (temperature > 37.2 || temperature < 36.1)) ? '#dc3545' : (temperature ? '#28a745' : '#ddd');
        }
        
        // Form validation
        document.getElementById('vitalsForm').addEventListener('submit', function(e) {
            const patientId = document.getElementById('patientId').value;
            const bpSystolic = document.getElementById('bpSystolic').value;
            const bpDiastolic = document.getElementById('bpDiastolic').value;
            const heartRate = document.getElementById('heartRate').value;
            const temperature = document.getElementById('temperature').value;
            
            if (!patientId) {
                e.preventDefault();
                alert('Please search and select a patient first');
                return false;
            }
            
            if (!bpSystolic || !bpDiastolic || !heartRate || !temperature) {
                e.preventDefault();
                alert('Please fill in all required vital signs');
                return false;
            }
            
            return true;
        });
        
        // Add event listeners
        document.getElementById('bpSystolic')?.addEventListener('input', checkVitalRanges);
        document.getElementById('bpDiastolic')?.addEventListener('input', checkVitalRanges);
        document.getElementById('heartRate')?.addEventListener('input', checkVitalRanges);
        document.getElementById('temperature')?.addEventListener('input', checkVitalRanges);
        
        // Close search results when clicking outside
        document.addEventListener('click', function(e) {
            if (!patientSearch.contains(e.target) && !searchResults.contains(e.target)) {
                searchResults.style.display = 'none';
            }
        });
        
        // Initialize from URL parameters
        document.addEventListener('DOMContentLoaded', function() {
            const urlParams = new URLSearchParams(window.location.search);
            const patientId = urlParams.get('patientId');
            const patientName = urlParams.get('patientName');
            
            if (patientId && patientName) {
                document.getElementById('patientId').value = patientId;
                document.getElementById('patientName').value = patientName;
                patientSearch.value = patientName;
                document.getElementById('submitBtn').disabled = false;
            }
            
            // Active nav link highlighting
            const currentPath = window.location.pathname;
            document.querySelectorAll('.sidebar .nav-link').forEach(link => {
                if (link.getAttribute('href') && currentPath.includes(link.getAttribute('href'))) {
                    link.classList.add('active');
                }
            });
        });
        
        // Prevent Enter key in search field from submitting form
        patientSearch.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                e.preventDefault();
                return false;
            }
        });
    </script>
</body>
</html>