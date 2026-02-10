import React, { useEffect, useState, useRef } from "react";
import axios from "axios";
import Navbar from "./Navbar";
import "../styles/dashboard.css";
import "../styles/CoordinatorDashboard.css";

const COORDINATOR_API = "http://localhost:8080/api/coordinator";
const days = ["MON", "TUE", "WED", "THU", "FRI", "SAT"];

export default function CoordinatorDashboard() {
  const [classes, setClasses] = useState([]);
  const [selectedClass, setSelectedClass] = useState(null);

  const [divisions, setDivisions] = useState([]);
  const [selectedDivision, setSelectedDivision] = useState(null);
  const [studentList, setStudentList] = useState([]);
  const [timetable, setTimetable] = useState({});
  const [teachers, setTeachers] = useState([]);

  const [showAddStudentModal, setShowAddStudentModal] = useState(false);
  const [showAddTeacherModal, setShowAddTeacherModal] = useState(false);
  const [showAnnouncementModal, setShowAnnouncementModal] = useState(false);
  const [showBulkUploadModal, setShowBulkUploadModal] = useState(false);
  const [showBulkTimetableModal, setShowBulkTimetableModal] = useState(false);
  const [showAddTimetableModal, setShowAddTimetableModal] = useState(false);
  const [successPopup, setSuccessPopup] = useState(null);
  const [studentSearch, setStudentSearch] = useState('');
  const [viewMode, setViewMode] = useState('classes'); // 'classes', 'divisions', 'overview'

  const [selectedSlot, setSelectedSlot] = useState(null);
  const [lectureDetails, setLectureDetails] = useState(null);
  const [showManageLectureModal, setShowManageLectureModal] = useState(false);
  const [isResolving, setIsResolving] = useState(false);

  useEffect(() => {
    fetchClasses();
  }, []);

  useEffect(() => {
    if (successPopup) {
      const timer = setTimeout(() => setSuccessPopup(null), 3000);
      return () => clearTimeout(timer);
    }
  }, [successPopup]);

  const fetchClasses = async () => {
    try {
      const res = await axios.get(`${COORDINATOR_API}/classes`, {
        withCredentials: true,
      });

      console.log("STATUS:", res.status);
      console.log("DATA:", res.data);

      setClasses(res.data);
    } catch (err) {
      console.error("Classes fetch error", err.response?.status, err.response?.data);
    }
  };


  const openClassView = async (cls) => {
    setSelectedClass(cls);
    setSelectedDivision(null);
    setStudentList([]);
    setTimetable({});
    setViewMode('divisions');

    try {
      const res = await axios.get(
        `${COORDINATOR_API}/classes/${cls.classId}/divisions`,
        { withCredentials: true }
      );
      setDivisions(res.data);
    } catch (err) {
      console.error("Divisions fetch error", err);
      setDivisions(["A", "B", "C"]);
    }
  };

  const fetchStudents = async (div) => {
    try {
      const res = await axios.get(
        `${COORDINATOR_API}/division/${div}/students`,
        { withCredentials: true }
      );
      setStudentList(res.data);
    } catch (err) {
      console.error("Students fetch error", err);
    }
  };

  const fetchTimetable = async (classId, div) => {
    try {
      const res = await axios.get(
        `${COORDINATOR_API}/timetable/${classId}/${div}`,
        { withCredentials: true }
      );

      const grouped = (res.data || []).reduce((acc, cur) => {
        if (!acc[cur.day]) acc[cur.day] = [];
        acc[cur.day].push(cur);
        return acc;
      }, {});

      setTimetable(grouped);
    } catch (err) {
      console.error("Timetable fetch error", err);
    }
  };

  const handleDivisionSelect = (div) => {
    setSelectedDivision(div);
    setViewMode('overview');
    fetchStudents(div);
    fetchTimetable(selectedClass.classId, div);
    fetchTeachers();
  };

  const fetchTeachers = async () => {
    try {
      const res = await axios.get(`${COORDINATOR_API}/teachers`, { withCredentials: true });
      console.log("Teachers fetched:", res.data);
      setTeachers(res.data);
    } catch (err) {
      console.error("Teachers fetch error", err);
    }
  };

  const downloadTemplate = async () => {
    try {
      const res = await axios.get(`${COORDINATOR_API}/student/template`, {
        responseType: 'blob',
        withCredentials: true
      });
      const url = window.URL.createObjectURL(new Blob([res.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', 'student_template.xlsx');
      document.body.appendChild(link);
      link.click();
    } catch (err) {
      alert("Failed to download template");
    }
  };

  const downloadTimetableTemplate = async () => {
    try {
      const res = await axios.get(`${COORDINATOR_API}/timetable/template`, {
        responseType: 'blob',
        withCredentials: true
      });
      const url = window.URL.createObjectURL(new Blob([res.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', 'timetable_template.xlsx');
      document.body.appendChild(link);
      link.click();
    } catch (err) {
      alert("Failed to download timetable template");
    }
  };

  const handleSlotClick = async (slot) => {
    setSelectedSlot(slot);
    setIsResolving(true);
    setShowManageLectureModal(true);
    try {
      const res = await axios.post("http://localhost:8080/api/lecture/resolve",
        { timetableId: slot.timetableId },
        { withCredentials: true }
      );
      setLectureDetails(res.data);
    } catch (err) {
      console.error("Resolve error", err);
      setLectureDetails(null);
    } finally {
      setIsResolving(false);
    }
  };

  const handleCancelLecture = async (slotId) => {
    if (!window.confirm("Remove this slot from the weekly schedule permanently?")) return;
    try {
      await axios.delete(`${COORDINATOR_API}/timetable/slot/${slotId}`, { withCredentials: true });
      if (selectedDivision) fetchTimetable(selectedClass.classId, selectedDivision);
    } catch {
      alert("Failed to remove timetable slot");
    }
  };

  const handleRemoveStudent = async (id) => {
    if (!window.confirm("Remove this student?")) return;
    try {
      await axios.delete(`${COORDINATOR_API}/student/${id}`, { withCredentials: true });
      setSuccessPopup("Student removed");
      fetchStudents(selectedDivision);
    } catch {
      alert("Failed to remove student");
    }
  };

  const goBackToClasses = () => {
    setViewMode('classes');
    setSelectedClass(null);
    setSelectedDivision(null);
  };

  const goBackToDivisions = () => {
    setViewMode('divisions');
    setSelectedDivision(null);
  };

  /* ================= CLASS VIEW ================= */


  /* ================= DASHBOARD VIEW ================= */

  return (
    <div className="coordinator-container">
      <Navbar />

      <div className="page-header">
        <h1>Coordinator Hub</h1>
        <p>Advanced Academic & Resource Control Cabinet</p>
      </div>

      <div className="drill-down-nav">
        <span className="nav-item" onClick={goBackToClasses}>Classes</span>
        {selectedClass && (
          <>
            <span className="nav-separator">/</span>
            <span className="nav-item" onClick={goBackToDivisions}>{selectedClass.className}</span>
          </>
        )}
        {selectedDivision && (
          <>
            <span className="nav-separator">/</span>
            <span className="nav-item">Division {selectedDivision}</span>
          </>
        )}
      </div>

      {viewMode === 'classes' && (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '40px' }}>
          <section>
            <h2 style={{ marginBottom: '20px', fontSize: '1.5rem', fontWeight: 600 }}>Academic Departments</h2>
            <div className="selection-grid">
              {classes.length === 0 ? (
                <div className="glass-card" style={{ textAlign: 'center', padding: '40px', gridColumn: 'span 3' }}>No departmental classes found.</div>
              ) : (
                classes.map(cls => (
                  <div key={cls.classId} className="glass-card action-cube" onClick={() => openClassView(cls)}>
                    <div className="icon">🏛️</div>
                    <div>
                      <h3>{cls.className}</h3>
                      <p>View Divisions & Enrolments</p>
                    </div>
                  </div>
                ))
              )}
            </div>
          </section>

          <section>
            <h2 style={{ marginBottom: '20px', fontSize: '1.5rem', fontWeight: 600 }}>Administrative Control Hub</h2>
            <div className="selection-grid">
              <div className="glass-card action-cube" onClick={() => setShowAddStudentModal(true)}>
                <div className="icon" style={{ color: '#38bdf8' }}>🎓</div>
                <h3>Enroll Student</h3>
                <p>Add individual records</p>
              </div>
              <div className="glass-card action-cube" onClick={() => setShowAddTeacherModal(true)}>
                <div className="icon" style={{ color: '#8b5cf6' }}>👨‍🏫</div>
                <h3>Register Faculty</h3>
                <p>Onboard new teachers</p>
              </div>
              <div className="glass-card action-cube" onClick={() => setShowAnnouncementModal(true)}>
                <div className="icon" style={{ color: '#fb923c' }}>📢</div>
                <h3>Broadcast</h3>
                <p>Post department news</p>
              </div>
              <div className="glass-card action-cube" onClick={() => setShowBulkUploadModal(true)}>
                <div className="icon" style={{ color: '#10b981' }}>👥</div>
                <h3>Bulk Import</h3>
                <p>Process Excel Student files</p>
              </div>
              <div className="glass-card action-cube" onClick={() => setShowBulkTimetableModal(true)}>
                <div className="icon" style={{ color: '#ec4899' }}>📅</div>
                <h3>Bulk Schedule</h3>
                <p>Process Timetable Excel</p>
              </div>
            </div>
          </section>
        </div>
      )}

      {viewMode === 'divisions' && (
        <section>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '25px' }}>
            <h2 style={{ margin: 0 }}>Select Division for {selectedClass.className}</h2>
            <button className="btn-outline" onClick={goBackToClasses}>← All Classes</button>
          </div>
          <div className="selection-grid">
            {divisions.length === 0 ? <p className="empty-state">No divisions found</p> : divisions.map(div => (
              <div
                key={div}
                className="glass-card action-cube"
                onClick={() => handleDivisionSelect(div)}
              >
                <div className="icon">📂</div>
                <h3>Division {div}</h3>
                <p>Access Schedule & Student List</p>
              </div>
            ))}
          </div>
        </section>
      )}

      {viewMode === 'overview' && (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '30px' }}>
          <div className="glass-card" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div>
              <h2 style={{ margin: 0, fontSize: '1.8rem' }}>{selectedClass.className} - {selectedDivision}</h2>
              <p style={{ color: '#94a3b8', margin: '5px 0 0 0' }}>Comprehensive Overview & Management</p>
            </div>
            <div style={{ display: 'flex', gap: '15px' }}>
              <button className="btn-premium" onClick={() => setShowAddTimetableModal(true)}>
                <span>+</span> New Slot
              </button>
              <button className="btn-outline" onClick={goBackToDivisions}>Change Division</button>
            </div>
          </div>

          <div className="glass-card">
            <h3 style={{ margin: '0 0 20px 0', borderBottom: '1px solid var(--glass-border)', paddingBottom: '15px' }}>Weekly Schedule</h3>
            <TimetableGrid timetable={timetable} onCancel={handleCancelLecture} onSlotClick={handleSlotClick} />
          </div>

          <div className="glass-card">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px', borderBottom: '1px solid var(--glass-border)', paddingBottom: '15px' }}>
              <h3 style={{ margin: 0 }}>Registered Students</h3>
              <div className="search-wrapper">
                <span className="search-icon">🔍</span>
                <input
                  type="text"
                  placeholder="Filter by name..."
                  className="search-input"
                  value={studentSearch}
                  onChange={(e) => setStudentSearch(e.target.value)}
                />
              </div>
            </div>
            <StudentTable
              students={studentList.filter(s => s.user?.name?.toLowerCase().includes(studentSearch.toLowerCase()))}
              onRemove={handleRemoveStudent}
            />
          </div>
        </div>
      )}


      {/* MODALS */}
      {showAddStudentModal && (
        <AddStudentModal
          onClose={() => setShowAddStudentModal(false)}
          classes={classes}
          onSuccess={(msg) => setSuccessPopup(msg)}
        />
      )}
      {showAddTeacherModal && (
        <AddTeacherModal
          classes={classes}
          onClose={() => setShowAddTeacherModal(false)}
          onSuccess={(msg) => setSuccessPopup(msg)}
        />
      )}
      {showAnnouncementModal && <AddAnnouncementModal onClose={() => setShowAnnouncementModal(false)} classes={classes} />}
      {showBulkUploadModal && (
        <BulkUploadModal
          onClose={() => setShowBulkUploadModal(false)}
          onDownloadTemplate={downloadTemplate}
          onSuccess={() => {
            fetchStudents(selectedDivision);
            setShowBulkUploadModal(false);
            setSuccessPopup("Bulk students uploaded successfully");
          }}
        />
      )}
      {showBulkTimetableModal && (
        <BulkTimetableUploadModal
          onClose={() => setShowBulkTimetableModal(false)}
          onDownloadTemplate={downloadTimetableTemplate}
          onSuccess={() => {
            if (selectedClass && selectedDivision) fetchTimetable(selectedClass.classId, selectedDivision);
            setShowBulkTimetableModal(false);
            setSuccessPopup("Bulk timetable uploaded successfully");
          }}
        />
      )}
      {showAddTimetableModal && (
        <AddTimetableSlotModal
          onClose={() => setShowAddTimetableModal(false)}
          classId={selectedClass?.classId}
          division={selectedDivision}
          teachers={teachers}
          onSuccess={() => {
            if (selectedClass && selectedDivision) fetchTimetable(selectedClass.classId, selectedDivision);
            setSuccessPopup("Schedule slot added successfully");
          }}
        />
      )}

      {showManageLectureModal && (
        <ManageLectureModal
          details={lectureDetails}
          isResolving={isResolving}
          slot={selectedSlot}
          onClose={() => {
            setShowManageLectureModal(false);
            setLectureDetails(null);
          }}
        />
      )}

      {successPopup && (
        <div style={{
          position: 'fixed',
          bottom: '30px',
          right: '30px',
          background: '#059669',
          color: '#fff',
          padding: '16px 24px',
          borderRadius: '12px',
          boxShadow: '0 10px 25px -5px rgba(0,0,0,0.2)',
          display: 'flex',
          alignItems: 'center',
          gap: '12px',
          zIndex: 9999,
        }}>
          <span style={{ fontSize: '20px' }}>✅</span>
          <span style={{ fontWeight: 'bold' }}>{successPopup}</span>
          <button onClick={() => setSuccessPopup(null)} style={{ background: 'transparent', border: 'none', color: '#fff', cursor: 'pointer', marginLeft: '10px', fontSize: '18px' }}>×</button>
        </div>
      )}
    </div>
  );
}

/* ================= SUB COMPONENTS ================= */

function FeatureCard({ title, icon, onClick }) {
  return (
    <div className="class-box" onClick={onClick} style={{ textAlign: "center", display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', gap: '10px', padding: '25px' }}>
      <div style={{ fontSize: 36 }}>{icon}</div>
      <strong style={{ fontSize: '16px' }}>{title}</strong>
    </div>
  );
}

function StudentTable({ students, onRemove }) {
  if (!students.length) return <div className="empty-state">No students enrolled in this division.</div>;

  return (
    <div className="table-responsive" style={{ maxHeight: '500px', overflowY: 'auto', border: '1px solid #e2e8f0', borderRadius: '12px' }}>
      <table className="premium-table">
        <thead style={{ position: 'sticky', top: 0, zIndex: 10 }}>
          <tr>
            <th style={{ width: '120px' }}>PRN</th>
            <th style={{ textAlign: 'left' }}>Full Name</th>
            <th style={{ textAlign: 'left' }}>Email Address</th>
            <th>Integrity</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {students.map(s => (
            <tr key={s.studentId}>
              <td style={{ fontWeight: 'bold', color: '#1e40af' }}>{s.prn}</td>
              <td style={{ textAlign: 'left' }}>{s.user?.name}</td>
              <td style={{ textAlign: 'left', color: '#64748b' }}>{s.user?.email}</td>
              <td style={{ textAlign: 'center' }}>
                <div style={{
                  padding: '4px 12px',
                  borderRadius: '20px',
                  background: (s.integrityScore?.integrityPercentage || 100) < 75 ? '#fee2e2' : '#dcfce7',
                  color: (s.integrityScore?.integrityPercentage || 100) < 75 ? '#dc2626' : '#166534',
                  fontSize: '12px',
                  fontWeight: '700',
                  display: 'inline-block'
                }}>
                  {s.integrityScore?.integrityPercentage ? `${s.integrityScore.integrityPercentage}%` : '100%'}
                </div>
              </td>
              <td style={{ textAlign: 'center' }}>
                <button
                  onClick={() => onRemove(s.studentId)}
                  className="btn-outline"
                  style={{ padding: '6px 12px', color: '#dc2626', borderColor: '#fecaca', fontSize: '12px' }}
                >
                  Remove
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

function TimetableGrid({ timetable, onCancel, onSlotClick }) {
  if (!timetable || Object.keys(timetable).length === 0) {
    return <div className="empty-state">No lecture schedule found for this division.</div>;
  }

  const times = [...new Set(Object.values(timetable).flat().map(t => `${t.startTime}-${t.endTime}`))].sort();

  if (!times.length) return <div className="empty-state">No lecture schedule found for this division.</div>;

  return (
    <div style={{ overflowX: 'auto' }}>
      <table className="premium-table" style={{ marginTop: '10px' }}>
        <thead>
          <tr>
            <th className="sticky-col">Time Slot</th>
            {(days || []).map(d => <th key={d}>{d}</th>)}
          </tr>
        </thead>
        <tbody>
          {times.map(time => (
            <tr key={time}>
              <td className="time-col sticky-col">{time}</td>
              {(days || []).map(day => {
                const slot = timetable[day]?.find(t => `${t.startTime}-${t.endTime}` === time);

                // Status Overlay Logic
                const isCancelled = slot?.status === 'CANCELLED';
                const isCompleted = slot?.status === 'COMPLETED';

                return (
                  <td key={day} style={{ textAlign: 'center', position: 'relative', verticalAlign: 'top', minWidth: '120px' }}>
                    {slot ? (
                      <div className="slot-bubble" style={{ position: 'relative', cursor: 'pointer', background: isCancelled ? '#fff0f0' : undefined, opacity: isCompleted ? 0.7 : 1 }} onClick={() => onSlotClick(slot)}>
                        {isCancelled && <div style={{ position: 'absolute', top: 5, right: 5, fontSize: '10px', fontWeight: 'bold', color: '#dc2626' }}>CANCELLED</div>}

                        <span className="subject" style={{ textDecoration: isCancelled ? 'line-through' : 'none' }}>{slot.subject}</span>
                        <span className="div-tag" style={{ marginBottom: '5px' }}>{slot.teacher?.name || slot.teacherName || "No Faculty"}</span>

                        <button
                          onClick={(e) => {
                            e.stopPropagation();
                            onCancel(slot.timetableId);
                          }}
                          className="btn-delete-slot"
                          style={{
                            marginTop: 'auto',
                            padding: '4px 8px',
                            background: '#f1f5f9',
                            color: '#64748b',
                            border: '1px solid #cbd5e1',
                            borderRadius: '4px',
                            cursor: 'pointer',
                            fontSize: '10px',
                            width: '100%',
                            fontWeight: '600'
                          }}
                          title="Delete this permanent slot"
                        >
                          Delete Slot
                        </button>
                      </div>
                    ) : (
                      <span className="dash" style={{ color: '#cbd5e1', fontSize: '1.5rem', fontWeight: '300' }}>—</span>
                    )}
                  </td>
                );
              })}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

function ManageLectureModal({ details, isResolving, slot, onClose }) {
  if (isResolving) return (
    <div className="modal-overlay">
      <div className="modal-content" style={{ textAlign: 'center' }}>
        <h2>Resolving Lecture...</h2>
        <p>Connecting to session data</p>
      </div>
    </div>
  );

  if (!details) return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content">
        <h2 style={{ color: '#e11d48' }}>Error</h2>
        <p>Could not load lecture details.</p>
        <button onClick={onClose} className="btn-outline">Close</button>
      </div>
    </div>
  );

  const downloadTemplate = async () => {
    try {
      // NOTE: Using details.lectureId (not slot.timetableId) as details comes from /resolve
      if (!details.lectureId) {
        alert("Lecture has not started yet (no ID generated). Cannot download attendance.");
        return;
      }

      const res = await axios.get(`http://localhost:8080/api/coordinator/lecture/${details.lectureId}/attendance-template`, {
        responseType: 'blob', // Important
        withCredentials: true
      });

      const url = window.URL.createObjectURL(new Blob([res.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `template_lecture_${details.lectureId}.xlsx`);
      document.body.appendChild(link);
      link.click();
    } catch (err) {
      console.error(err);
      alert("Failed to download template. Ensure lecture is created.");
    }
  };

  const handleUpload = async (e) => {
    const file = e.target.files[0];
    if (!file) return;
    const formData = new FormData();
    formData.append("file", file);
    try {
      await axios.post(`http://localhost:8080/api/coordinator/lecture/${details.lectureId}/attendance-upload`, formData, {
        withCredentials: true
      });
      alert("Attendance uploaded successfully!");
      onClose(); // Close to refresh? Or reload details?
    } catch (err) {
      alert(err.response?.data?.message || "Failed to upload attendance");
    }
  };

  const handleCancelLecture = async () => {
    if (!window.confirm("Are you sure you want to CANCEL this specific lecture? This will notify all students.")) return;
    try {
      await axios.post(`http://localhost:8080/api/coordinator/lecture/${details.lectureId}/cancel`, {}, { withCredentials: true });
      alert("Lecture cancelled successfully.");
      onClose();
      // Ideally refresh timetable here, but requires passing refresher
      window.location.reload();
    } catch (err) {
      alert(err.response?.data?.message || "Failed to cancel lecture.");
    }
  };

  const isCancelled = details.status === 'CANCELLED';

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={e => e.stopPropagation()} style={{ maxWidth: '800px', width: '90%' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
          <h2 style={{ margin: 0 }}>Manage Lecture: {details.subject || slot.subject}</h2>
          <button className="btn-outline" onClick={onClose}>×</button>
        </div>

        <div className="lecture-meta-grid" style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '15px', marginBottom: '30px' }}>
          <div className="stat-box" style={{ background: '#f8fafc', padding: '15px', borderRadius: '12px', textAlign: 'center' }}>
            <div style={{ fontSize: '12px', color: '#64748b' }}>Total Students</div>
            <div style={{ fontSize: '20px', fontWeight: 'bold' }}>{details.totalStudents || 0}</div>
          </div>
          <div className="stat-box" style={{ background: '#f0fdf4', padding: '15px', borderRadius: '12px', textAlign: 'center' }}>
            <div style={{ fontSize: '12px', color: '#166534' }}>Actual Present</div>
            <div style={{ fontSize: '20px', fontWeight: 'bold', color: '#15803d' }}>{details.presentCount || 0}</div>
          </div>
          <div className="stat-box" style={{ background: isCancelled ? '#fef2f2' : '#fff7ed', padding: '15px', borderRadius: '12px', textAlign: 'center' }}>
            <div style={{ fontSize: '12px', color: isCancelled ? '#b91c1c' : '#9a3412' }}>Status</div>
            <div style={{ fontSize: '18px', fontWeight: 'bold', color: isCancelled ? '#dc2626' : '#c2410c' }}>{details.status || 'Scheduled'}</div>
          </div>
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '25px' }}>
          <section>
            <h3 style={{ fontSize: '16px', marginBottom: '15px' }}>Attendance Control</h3>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
              <button onClick={downloadTemplate} disabled={isCancelled} style={{ padding: '10px', background: isCancelled ? '#e2e8f0' : '#eff6ff', border: '1px solid #bfdbfe', color: isCancelled ? '#94a3b8' : '#1e40af', borderRadius: '8px', cursor: isCancelled ? 'not-allowed' : 'pointer', fontWeight: '600' }}>
                📥 Download Template
              </button>
              <label style={{ display: 'block', padding: '10px', background: isCancelled ? '#e2e8f0' : '#f0fdf4', border: '1px solid #bbf7d0', color: isCancelled ? '#94a3b8' : '#166534', borderRadius: '8px', cursor: isCancelled ? 'not-allowed' : 'pointer', textAlign: 'center', fontWeight: '600' }}>
                📤 Upload Marks (Excel)
                <input type="file" hidden accept=".xlsx" onChange={handleUpload} disabled={isCancelled} />
              </label>

              {!isCancelled && (
                <button onClick={handleCancelLecture} style={{ marginTop: '15px', padding: '10px', background: '#fff1f2', border: '1px solid #fecdd3', color: '#e11d48', borderRadius: '8px', cursor: 'pointer', fontWeight: '600' }}>
                  🚫 Cancel Lecture Instance
                </button>
              )}
            </div>
          </section>

          <section>
            <h3 style={{ fontSize: '16px', marginBottom: '15px' }}>Live Feedback ({details.feedbacks?.length || 0})</h3>
            <div style={{ maxHeight: '200px', overflowY: 'auto', border: '1px solid #e2e8f0', borderRadius: '8px', padding: '10px' }}>
              {(details.feedbacks || []).length === 0 ? <p style={{ fontSize: '13px', color: '#94a3b8' }}>No feedbacks yet.</p> :
                (details.feedbacks || []).map((f, i) => (
                  <div key={i} style={{ padding: '8px', borderBottom: i < (details.feedbacks || []).length - 1 ? '1px solid #f1f5f9' : 'none' }}>
                    <div style={{ fontSize: '12px', fontWeight: 'bold' }}>Rating: {f.rating}⭐ | Understood: {f.understand}</div>
                    <p style={{ margin: '4px 0 0 0', fontSize: '13px', color: '#475569' }}>{f.comment || "No comment"}</p>
                  </div>
                ))}
            </div>
          </section>
        </div>
      </div>
    </div>
  );
}

/* ===== ENHANCED MODALS ===== */

function AddStudentModal({ onClose, classes, onSuccess }) {
  const [formData, setFormData] = useState({ name: '', email: '', prn: '', division: '', classId: '', phone: '', dob: '', password: '' });
  const [loading, setLoading] = useState(false);

  const submit = async e => {
    e.preventDefault();
    setLoading(true);
    try {
      await axios.post(`${COORDINATOR_API}/student/add`, formData, { withCredentials: true });
      onSuccess("Student added successfully");
      onClose();
    } catch (err) {
      alert(err.response?.data?.message || "Error adding student");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={e => e.stopPropagation()}>
        <h2 style={{ color: '#1e3a8a', marginTop: 0 }}>Enrol New Student</h2>
        <form onSubmit={submit} style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '15px' }}>
          <div style={{ gridColumn: 'span 2' }}>
            <label style={{ display: 'block', fontSize: '13px', marginBottom: '5px' }}>Full Name</label>
            <input placeholder="Ex: John Doe" required style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #ddd' }} onChange={e => setFormData({ ...formData, name: e.target.value })} />
          </div>
          <div>
            <label style={{ display: 'block', fontSize: '13px', marginBottom: '5px' }}>Email</label>
            <input type="email" placeholder="john@example.com" required style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #ddd' }} onChange={e => setFormData({ ...formData, email: e.target.value })} />
          </div>
          <div>
            <label style={{ display: 'block', fontSize: '13px', marginBottom: '5px' }}>PRN</label>
            <input placeholder="Permanent Reg No" required style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #ddd' }} onChange={e => setFormData({ ...formData, prn: e.target.value })} />
          </div>
          <div>
            <label style={{ display: 'block', fontSize: '13px', marginBottom: '5px' }}>Class</label>
            <select required style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #ddd' }} onChange={e => setFormData({ ...formData, classId: e.target.value })}>
              <option value="">Select Class</option>
              {classes.map(c => <option key={c.classId} value={c.classId}>{c.className}</option>)}
            </select>
          </div>
          <div>
            <label style={{ display: 'block', fontSize: '13px', marginBottom: '5px' }}>Division</label>
            <input placeholder="Ex: A, B, C" required style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #ddd' }} onChange={e => setFormData({ ...formData, division: e.target.value })} />
          </div>
          <div>
            <label style={{ display: 'block', fontSize: '13px', marginBottom: '5px' }}>Phone</label>
            <input placeholder="10-digit number" style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #ddd' }} onChange={e => setFormData({ ...formData, phone: e.target.value })} />
          </div>
          <div>
            <label style={{ display: 'block', fontSize: '13px', marginBottom: '5px' }}>DOB</label>
            <input type="date" style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #ddd' }} onChange={e => setFormData({ ...formData, dob: e.target.value })} />
          </div>
          <div style={{ gridColumn: 'span 2' }}>
            <label style={{ display: 'block', fontSize: '13px', marginBottom: '5px' }}>Password (Optional - defaults to PRN)</label>
            <input type="password" placeholder="Set initial password" style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #ddd' }} onChange={e => setFormData({ ...formData, password: e.target.value })} />
          </div>
          <div style={{ gridColumn: 'span 2', display: 'flex', justifyContent: 'flex-end', gap: '10px', marginTop: '10px' }}>
            <button type="button" onClick={onClose} style={{ padding: '10px 20px', borderRadius: '8px', border: '1px solid #ddd', background: '#f8fafc', cursor: 'pointer' }}>Cancel</button>
            <button type="submit" disabled={loading} style={{ padding: '10px 20px', borderRadius: '8px', border: 'none', background: '#2563eb', color: '#fff', cursor: 'pointer', fontWeight: 'bold' }}>{loading ? "Processing..." : "Enrol Student"}</button>
          </div>
        </form>
      </div>
    </div>
  );
}

function AddTeacherModal({ onClose, classes, onSuccess }) {
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    subject: '',
    password: '',
    assignedClasses: '',
    assignedDivisions: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const submit = async e => {
    e.preventDefault();
    setLoading(true);
    setError(null);
    try {
      await axios.post(`${COORDINATOR_API}/teacher/add`, formData, { withCredentials: true });
      onSuccess("Teacher added successfully");
      onClose();
    } catch (err) {
      setError(err.response?.data?.message || err.message || "Error adding teacher");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={e => e.stopPropagation()}>
        <h2 style={{ color: '#1e3a8a', marginTop: 0 }}>Register New Faculty</h2>
        {error && <div style={{ color: '#e11d48', background: '#fff1f2', padding: '10px', borderRadius: '6px', marginBottom: '15px', fontSize: '14px', border: '1px solid #fecdd3' }}>{error}</div>}
        <form onSubmit={submit} style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
          <div>
            <label style={{ display: 'block', fontSize: '13px', marginBottom: '5px' }}>Full Name</label>
            <input placeholder="Ex: Prof. Alan Turing" required style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #ddd' }} onChange={e => setFormData({ ...formData, name: e.target.value })} />
          </div>
          <div>
            <label style={{ display: 'block', fontSize: '13px', marginBottom: '5px' }}>Email</label>
            <input type="email" placeholder="turing@classspace.edu" required style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #ddd' }} onChange={e => setFormData({ ...formData, email: e.target.value })} />
          </div>
          <div>
            <label style={{ display: 'block', fontSize: '13px', marginBottom: '5px' }}>Primary Subject</label>
            <input placeholder="Ex: Mathematics" required style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #ddd' }} onChange={e => setFormData({ ...formData, subject: e.target.value })} />
          </div>
          <div>
            <label style={{ display: 'block', fontSize: '13px', marginBottom: '5px' }}>Initial Password</label>
            <input type="password" required style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #ddd' }} onChange={e => setFormData({ ...formData, password: e.target.value })} />
          </div>
          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '10px', marginTop: '10px' }}>
            <button type="button" onClick={onClose} style={{ padding: '10px 20px', borderRadius: '8px', border: '1px solid #ddd', background: '#f8fafc', cursor: 'pointer' }}>Cancel</button>
            <button type="submit" disabled={loading} style={{ padding: '10px 20px', borderRadius: '8px', border: 'none', background: '#6366f1', color: '#fff', cursor: 'pointer', fontWeight: 'bold' }}>{loading ? "Processing..." : "Add Faculty"}</button>
          </div>
        </form>
      </div>
    </div>
  );
}

function AddAnnouncementModal({ onClose, classes }) {
  const [formData, setFormData] = useState({ title: '', message: '', classId: '', division: '' });
  const [loading, setLoading] = useState(false);

  const submit = async e => {
    e.preventDefault();
    setLoading(true);
    const dataToSend = { ...formData, classId: formData.classId === "" ? null : formData.classId };
    try {
      await axios.post(`${COORDINATOR_API}/announcement/create`, dataToSend, { withCredentials: true });
      alert("Announcement published successfully!");
      onClose();
    } catch (err) {
      alert("Error publishing announcement");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={e => e.stopPropagation()}>
        <h2 style={{ color: '#1e3a8a', marginTop: 0 }}>Create Official Announcement</h2>
        <form onSubmit={submit} style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
          <div>
            <label style={{ display: 'block', fontSize: '13px', marginBottom: '5px' }}>Title</label>
            <input placeholder="Short descriptive title" required style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #ddd' }} onChange={e => setFormData({ ...formData, title: e.target.value })} />
          </div>
          <div>
            <label style={{ display: 'block', fontSize: '13px', marginBottom: '5px' }}>Message Body</label>
            <textarea rows="4" placeholder="Detailed announcement details..." required style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #ddd', fontFamily: 'inherit' }} onChange={e => setFormData({ ...formData, message: e.target.value })} />
          </div>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '15px' }}>
            <div>
              <label style={{ display: 'block', fontSize: '13px', marginBottom: '5px' }}>Target Class (Optional)</label>
              <select style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #ddd' }} onChange={e => setFormData({ ...formData, classId: e.target.value })}>
                <option value="">All Classes</option>
                {classes.map(c => <option key={c.classId} value={c.classId}>{c.className}</option>)}
              </select>
            </div>
            <div>
              <label style={{ display: 'block', fontSize: '13px', marginBottom: '5px' }}>Target Division (Optional)</label>
              <input placeholder="Ex: A" style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #ddd' }} onChange={e => setFormData({ ...formData, division: e.target.value })} />
            </div>
          </div>
          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '10px', marginTop: '10px' }}>
            <button type="button" onClick={onClose} style={{ padding: '10px 20px', borderRadius: '8px', border: '1px solid #ddd', background: '#f8fafc', cursor: 'pointer' }}>Cancel</button>
            <button type="submit" disabled={loading} style={{ padding: '10px 20px', borderRadius: '8px', border: 'none', background: '#db2777', color: '#fff', cursor: 'pointer', fontWeight: 'bold' }}>{loading ? "Processing..." : "Publish Announcement"}</button>
          </div>
        </form>
      </div>
    </div>
  );
}

function BulkTimetableUploadModal({ onClose, onDownloadTemplate, onSuccess }) {
  const [file, setFile] = useState(null);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!file) return alert("Please select a file");
    setLoading(true);

    const formData = new FormData();
    formData.append("file", file);

    try {
      const res = await axios.post(`${COORDINATOR_API}/timetable/upload-bulk`, formData, {
        withCredentials: true,
        responseType: 'arraybuffer'
      });

      const contentType = res.headers['content-type'] || res.headers['Content-Type'];
      if (contentType && contentType.includes('application/octet-stream')) {
        const blob = new Blob([res.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', 'failed_timetable_slots.xlsx');
        document.body.appendChild(link);
        link.click();
        alert("Some slots failed to upload. Check the downloaded file for details.");
      } else {
        alert("Timetable uploaded successfully!");
        onSuccess();
      }
    } catch (err) {
      alert("Error uploading timetable");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={e => e.stopPropagation()}>
        <h2 style={{ color: '#1e3a8a', marginTop: 0 }}>Bulk Timetable Upload</h2>

        <div style={{ marginBottom: '20px', padding: '15px', background: '#f0f9ff', borderRadius: '8px', border: '1px solid #bae6fd' }}>
          <p style={{ margin: '0 0 10px 0', fontSize: '14px', color: '#0369a1' }}>
            <strong>Step 1:</strong> Get the Timetable Excel template.
          </p>
          <button onClick={onDownloadTemplate} style={{ padding: '8px 16px', background: '#fff', border: '1px solid #0369a1', color: '#0369a1', borderRadius: '6px', cursor: 'pointer', fontWeight: 'bold' }}>
            Download Template
          </button>
        </div>

        <form onSubmit={handleSubmit}>
          <p style={{ margin: '0 0 10px 0', fontSize: '14px', color: '#1e3a8a' }}>
            <strong>Step 2:</strong> Upload the filled schedule.
          </p>
          <input type="file" accept=".xlsx, .xls" required onChange={e => setFile(e.target.files[0])} style={{ marginBottom: '20px' }} />
          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '10px' }}>
            <button type="button" onClick={onClose} style={{ padding: '10px 20px', borderRadius: '8px', border: '1px solid #ddd', background: '#f8fafc', cursor: 'pointer' }}>Cancel</button>
            <button type="submit" disabled={loading} style={{ padding: '10px 20px', borderRadius: '8px', border: 'none', background: '#9333ea', color: '#fff', cursor: 'pointer', fontWeight: 'bold' }}>
              {loading ? "Uploading..." : "Upload Schedule"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

function BulkUploadModal({ onClose, onDownloadTemplate, onSuccess }) {
  const [file, setFile] = useState(null);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!file) return alert("Please select a file");
    setLoading(true);

    const formData = new FormData();
    formData.append("file", file);

    try {
      const res = await axios.post(`${COORDINATOR_API}/student/upload-bulk`, formData, {
        withCredentials: true,
        responseType: 'arraybuffer'
      });

      // Check if response is a file (failed rows) or text
      const contentType = res.headers['content-type'] || res.headers['Content-Type'];
      if (contentType && contentType.includes('application/octet-stream')) {
        const blob = new Blob([res.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', 'failed_students.xlsx');
        document.body.appendChild(link);
        link.click();
        alert("Some students failed to upload. Check the downloaded file for details.");
      } else {
        alert("Bulk upload processed successfully!");
        onSuccess();
      }
    } catch (err) {
      console.error(err);
      alert("Error uploading students");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={e => e.stopPropagation()}>
        <h2 style={{ color: '#1e3a8a', marginTop: 0 }}>Bulk Student Upload</h2>
        <div style={{ marginBottom: '20px', padding: '15px', background: '#f0f9ff', borderRadius: '8px', border: '1px solid #bae6fd' }}>
          <p style={{ margin: '0 0 10px 0', fontSize: '14px', color: '#0369a1' }}>
            <strong>Step 1:</strong> Download the Excel template with required columns.
          </p>
          <button onClick={onDownloadTemplate} style={{ padding: '8px 16px', background: '#fff', border: '1px solid #0369a1', color: '#0369a1', borderRadius: '6px', cursor: 'pointer', fontWeight: 'bold' }}>
            Download Template
          </button>
        </div>
        <form onSubmit={handleSubmit}>
          <p style={{ margin: '0 0 10px 0', fontSize: '14px', color: '#1e3a8a' }}>
            <strong>Step 2:</strong> Fill the template and upload it here.
          </p>
          <input type="file" accept=".xlsx, .xls" required onChange={e => setFile(e.target.files[0])} style={{ marginBottom: '20px' }} />
          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '10px' }}>
            <button type="button" onClick={onClose} style={{ padding: '10px 20px', borderRadius: '8px', border: '1px solid #ddd', background: '#f8fafc', cursor: 'pointer' }}>Cancel</button>
            <button type="submit" disabled={loading} style={{ padding: '10px 20px', borderRadius: '8px', border: 'none', background: '#059669', color: '#fff', cursor: 'pointer', fontWeight: 'bold' }}>
              {loading ? "Uploading..." : "Upload & Sync"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

function AddTimetableSlotModal({ onClose, classId, division, teachers, onSuccess }) {
  const [form, setForm] = useState({
    classId: classId,
    weekNumber: 1,
    day: "MON",
    startTime: "10:00",
    endTime: "11:00",
    subject: "",
    division: division,
    teacherId: ""
  });
  const [loading, setLoading] = useState(false);

  const submit = async e => {
    e.preventDefault();
    if (form.startTime >= form.endTime) {
      alert("End time must be after start time");
      return;
    }
    setLoading(true);
    try {
      await axios.post(`${COORDINATOR_API}/timetable/slot`, form, { withCredentials: true });
      alert("Timetable slot created!");
      onSuccess();
      onClose();
    } catch (err) {
      alert(err.response?.data?.message || "Error creating slot");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={e => e.stopPropagation()}>
        <h2 style={{ color: '#1e3a8a', marginTop: 0 }}>Add New Schedule Slot</h2>
        <form onSubmit={submit} style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '15px' }}>
          <div style={{ gridColumn: 'span 2' }}>
            <label style={{ display: 'block', fontSize: '13px', marginBottom: '5px' }}>Subject</label>
            <input placeholder="Ex: Machine Learning" required style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #ddd' }} value={form.subject} onChange={e => setForm({ ...form, subject: e.target.value })} />
          </div>
          <div>
            <label style={{ display: 'block', fontSize: '13px', marginBottom: '5px' }}>Day</label>
            <select style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #ddd' }} value={form.day} onChange={e => setForm({ ...form, day: e.target.value })}>
              {["MON", "TUE", "WED", "THU", "FRI", "SAT"].map(d => <option key={d} value={d}>{d}</option>)}
            </select>
          </div>
          <div>
            <label style={{ display: 'block', fontSize: '13px', marginBottom: '5px' }}>Week</label>
            <input type="number" min="1" required style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #ddd' }} value={form.weekNumber} onChange={e => setForm({ ...form, weekNumber: Number(e.target.value) })} />
          </div>
          <div>
            <label style={{ display: 'block', fontSize: '13px', marginBottom: '5px' }}>Start Time</label>
            <input type="time" required style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #ddd' }} value={form.startTime} onChange={e => setForm({ ...form, startTime: e.target.value })} />
          </div>
          <div>
            <label style={{ display: 'block', fontSize: '13px', marginBottom: '5px' }}>End Time</label>
            <input type="time" required style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #ddd' }} value={form.endTime} onChange={e => setForm({ ...form, endTime: e.target.value })} />
          </div>
          <div style={{ gridColumn: 'span 2' }}>
            <label style={{ display: 'block', fontSize: '13px', marginBottom: '5px' }}>Assign Faculty</label>
            <select required style={{ width: '100%', padding: '10px', borderRadius: '6px', border: '1px solid #ddd' }} value={form.teacherId} onChange={e => setForm({ ...form, teacherId: e.target.value })}>
              <option value="">Select Teacher</option>
              {teachers.map(t => (
                <option key={t.teacherId} value={t.teacherId}>
                  {t.user?.name || t.name || "Unknown"} ({t.subject})
                </option>
              ))}
            </select>
          </div>
          <div style={{ gridColumn: 'span 2', display: 'flex', justifyContent: 'flex-end', gap: '10px', marginTop: '10px' }}>
            <button type="button" onClick={onClose} style={{ padding: '10px 20px', borderRadius: '8px', border: '1px solid #ddd', background: '#f8fafc', cursor: 'pointer' }}>Cancel</button>
            <button type="submit" disabled={loading} style={{ padding: '10px 20px', borderRadius: '8px', border: 'none', background: '#6366f1', color: '#fff', cursor: 'pointer', fontWeight: 'bold' }}>
              {loading ? "Saving..." : "Create Slot"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
