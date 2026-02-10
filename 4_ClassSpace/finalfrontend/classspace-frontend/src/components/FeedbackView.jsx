import React, { useEffect, useMemo, useState } from "react";
import axios from "axios";
import Navbar from "./Navbar";
import "../styles/teacherFeedback.css";

function bgByStars(stars) {
  switch (Number(stars)) {
    case 1: return "rgba(255, 77, 77, 0.85)";  // red
    case 2: return "rgba(255, 154, 203, 0.85)";  // pink
    case 3: return "rgba(217, 217, 217, 0.85)";  // gray
    case 4: return "rgba(125, 183, 255, 0.85)";  // blue
    case 5: return "rgba(99, 212, 113, 0.85)";  // green
    default: return "rgba(238, 238, 238, 0.85)";
  }
}

export default function FeedbackView({ lectureId }) {
  const [loading, setLoading] = useState(true);
  const [feedbackData, setFeedbackData] = useState(null);
  const [lectureInfo, setLectureInfo] = useState(null);
  const [cancelling, setCancelling] = useState(false);

  const fetchData = async () => {
    if (!lectureId || Number.isNaN(lectureId)) {
      setLoading(false);
      return;
    }

    setLoading(true);
    try {
      const fRes = await axios.get(`http://localhost:8080/api/feedback/lecture/${lectureId}`, { withCredentials: true });
      setFeedbackData(fRes.data);

      const lRes = await axios.get(`http://localhost:8080/api/lecture/${lectureId}`, { withCredentials: true });
      setLectureInfo(lRes.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, [lectureId]);

  const [showModal, setShowModal] = useState(false);
  const [cancelReason, setCancelReason] = useState("");
  const [toast, setToast] = useState({ show: false, message: "", type: "success" });

  const showToast = (message, type = "success") => {
    setToast({ show: true, message, type });
    setTimeout(() => setToast({ show: false, message: "", type: "success" }), 4000);
  };

  const handleCancelClick = () => {
    setShowModal(true);
  };

  const handleCancelConfirm = async () => {
    if (!cancelReason.trim()) {
      showToast("Please provide a reason for cancellation", "error");
      return;
    }
    setCancelling(true);
    try {
      await axios.post(`http://localhost:8080/api/lecture/${lectureId}/cancel`,
        { reason: cancelReason },
        { withCredentials: true }
      );
      showToast("Lecture cancelled and students notified!", "success");
      setShowModal(false);
      fetchData(); // refresh
    } catch (err) {
      const msg = err.response?.data?.message || "Failed to cancel lecture";
      showToast(msg, "error");
    } finally {
      setCancelling(false);
    }
  };

  const items = useMemo(() => feedbackData?.feedbackList || [], [feedbackData]);

  // Calculate if lecture has already started
  const hasStarted = () => {
    if (!lectureInfo?.lectureDate || !lectureInfo?.startTime) return false;
    const [hours, minutes] = lectureInfo.startTime.split(':').map(Number);
    const lectureStart = new Date(lectureInfo.lectureDate);
    lectureStart.setHours(hours, minutes, 0, 0);
    return new Date() > lectureStart;
  };

  const isCancelled = lectureInfo?.status === "CANCELLED";
  const canCancel = !hasStarted() && !isCancelled;

  if (loading) return <><Navbar /><div className="tf-wrap">Loading lecture details…</div></>;

  return (
    <>
      <Navbar />
      <div className="tf-page tf-wrap glass-container animated fadeIn">
        <header className="tf-header">
          <div className="header-info">
            <h1 className="tf-main-title">{lectureInfo?.subject || "Lecture Insights"}</h1>
            <p className="tf-subtitle">
              {lectureInfo?.lectureDate} | {lectureInfo?.startTime} - {lectureInfo?.endTime}
            </p>
          </div>
          <div className="header-actions">
            {isCancelled ? (
              <span className="status-badge cancelled">CANCELLED</span>
            ) : hasStarted() ? (
              <span className="status-badge active">ONGOING/COMPLETED</span>
            ) : (
              <button
                className="cancel-lecture-btn"
                onClick={handleCancelClick}
                disabled={cancelling}
              >
                {cancelling ? "Processing..." : "Cancel Lecture"}
              </button>
            )}
          </div>
        </header>

        {/* ... existing stats grid ... */}
        <div className="stats-grid">
          <div className="stat-card total">
            <div className="stat-icon">👥</div>
            <div className="stat-content">
              <span className="stat-label">Total Enrolled</span>
              <span className="stat-value">{lectureInfo?.totalStudents || 0}</span>
            </div>
          </div>
          <div className="stat-card expected">
            <div className="stat-icon">✅</div>
            <div className="stat-content">
              <span className="stat-label">Expected (YES)</span>
              <span className="stat-value">{lectureInfo?.expectedStudents || 0}</span>
            </div>
          </div>
          <div className="stat-card absent">
            <div className="stat-icon">❌</div>
            <div className="stat-content">
              <span className="stat-label">Likely Absent</span>
              <span className="stat-value">{lectureInfo?.likelyAbsentStudents || 0}</span>
            </div>
          </div>
        </div>

        <div className="tf-summary">
          <div className="tf-summary-card">
            <div className="tf-label">Average Stars</div>
            <div className="tf-value">⭐ {Number(feedbackData?.averageStars || 0).toFixed(1)}</div>
          </div>
          <div className="tf-summary-card">
            <div className="tf-label">Understood</div>
            <div className="tf-value">👍 {feedbackData?.understoodCount || 0}</div>
          </div>
          <div className="tf-summary-card">
            <div className="tf-label">Total Feedback</div>
            <div className="tf-value">📝 {feedbackData?.totalFeedback || 0}</div>
          </div>
        </div>

        <h2 className="tf-title">Student Comments</h2>

        {items.length === 0 ? (
          <div className="tf-empty">No anonymous feedback yet.</div>
        ) : (
          <div className="tf-scroll-grid">
            {items.map((fb, idx) => (
              <div
                key={idx}
                className="tf-card glass-card animated slideInUp"
                style={{
                  borderLeft: `6px solid ${bgByStars(fb.stars)}`,
                  animationDelay: `${idx * 0.1}s`
                }}
              >
                <div className="tf-card-top">
                  <div className="tf-stars">⭐ {fb.stars} / 5</div>
                  <div className={`tf-understand ${fb.understoodTopic ? 'yes' : 'no'}`}>
                    {fb.understoodTopic ? "✅ Understood" : "❌ Confused"}
                  </div>
                </div>
                <div className="tf-comment">"{fb.comment || "No comment provided."}"</div>
                <div className="tf-anon">— Anonymous Student</div>
              </div>
            ))}
          </div>
        )}

        {/* CUSTOM TOAST */}
        {toast.show && (
          <div className={`custom-toast ${toast.type} animated slideInRight`}>
            {toast.message}
          </div>
        )}

        {/* CANCELLATION MODAL */}
        {showModal && (
          <div className="custom-modal-overlay">
            <div className="custom-modal animated scaleIn">
              <h3>Cancel Lecture</h3>
              <p>Please provide a reason for cancelling this lecture. An announcement will be sent to all students.</p>
              <textarea
                placeholder="e.g., Medical Emergency, Technical Issues, etc."
                value={cancelReason}
                onChange={(e) => setCancelReason(e.target.value)}
                rows={4}
              />
              <div className="modal-actions">
                <button className="btn-secondary" onClick={() => setShowModal(false)}>Keep Lecture</button>
                <button
                  className="btn-danger"
                  onClick={handleCancelConfirm}
                  disabled={cancelling || !cancelReason.trim()}
                >
                  {cancelling ? "Processing..." : "Confirm Cancellation"}
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </>
  );
}
