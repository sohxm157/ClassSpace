import { useState, useEffect } from "react";
import axios from "axios";

function AttendanceSection({ lectureId }) {
  const [status, setStatus] = useState("YES");
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(true);
  const [updating, setUpdating] = useState(false);
  const [messageType, setMessageType] = useState(""); // 'success', 'error', 'info'
  const studentId = 5; // later auth se aayega

  // Fetch current attendance status on mount
  useEffect(() => {
    if (!lectureId) return;

    axios
      .get(`http://localhost:8080/api/attendance/status/${lectureId}`, {
        withCredentials: true,
      })
      .then((res) => {
        if (res.data) {
          setStatus(res.data);
        }
      })
      .catch(() => {
        // Default to YES if fetch fails
        setStatus("YES");
      })
      .finally(() => setLoading(false));
  }, [lectureId]);

  const declareAttendance = async (newStatus) => {
    if (updating) return; // Prevent double-clicks

    setUpdating(true);
    setMessage("");

    try {
      const response = await axios.post("http://localhost:8080/api/attendance/declare", {
        lectureId,
        studentId,
        status: newStatus
      }, { withCredentials: true });

      setStatus(newStatus);
      const successMsg = response.data?.message || "Attendance updated successfully";
      setMessage(successMsg);
      setMessageType("success");

      // Clear success message after 3 seconds
      setTimeout(() => {
        setMessage("");
        setMessageType("");
      }, 3000);

    } catch (err) {
      const errorMsg = err.response?.data?.message ||
        err.response?.data?.error ||
        err.response?.data ||
        "Failed to update attendance";
      setMessage(errorMsg);
      setMessageType("error");
    } finally {
      setUpdating(false);
    }
  };

  if (loading) {
    return (
      <div>
        <h3 className="lp-section-title">Mark Attendance</h3>
        <div className="lp-status">Loading attendance status...</div>
      </div>
    );
  }

  return (
    <div>
      <h3 className="lp-section-title">Mark Attendance</h3>

      <div className="lp-pill-row">
        <button
          type="button"
          className="lp-pill lp-pill--present"
          data-active={status === "YES"}
          aria-pressed={status === "YES"}
          onClick={() => declareAttendance("YES")}
          disabled={updating}
        >
          ✓ PRESENT
        </button>

        <span className="lp-sep">|</span>

        <button
          type="button"
          className="lp-pill lp-pill--absent"
          data-active={status === "NO"}
          aria-pressed={status === "NO"}
          onClick={() => declareAttendance("NO")}
          disabled={updating}
        >
          ✗ ABSENT
        </button>
      </div>

      <div className={`lp-status ${messageType ? `lp-status--${messageType}` : ''}`}>
        {updating ? (
          <span className="lp-loading">Updating...</span>
        ) : message ? (
          <span className={messageType === 'error' ? 'lp-error-msg' : 'lp-success-msg'}>
            {messageType === 'error' ? '⚠️ ' : '✓ '}
            {message}
          </span>
        ) : (
          <>
            Current Status: <b>{status === "YES" ? "Present" : "Absent"}</b>
          </>
        )}
      </div>
    </div>
  );
}

export default AttendanceSection;
