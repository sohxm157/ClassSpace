import { useState, useEffect } from "react";

import axios from "axios";

function FeedbackSection({ lectureId }) {
  const studentId = 5; // later auth se

  const [loading, setLoading] = useState(true);
  const [allowed, setAllowed] = useState(false); // lecture ended or not

  const [understand, setUnderstand] = useState("YES");
  const [stars, setStars] = useState(5);
  const [comment, setComment] = useState("");
  const [submitted, setSubmitted] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [message, setMessage] = useState("");
  const [messageType, setMessageType] = useState(""); // 'success', 'error', 'info'

  useEffect(() => {
    if (!lectureId) return; // 🔥 very important guard

    axios
      .get(`http://localhost:8080/api/feedback/status/${lectureId}`, {
        withCredentials: true,
      })
      .then((res) => {
        setSubmitted(res.data.submitted);
        setAllowed(res.data.allowed);
      })
      .catch(() => {
        // fallback: allow UI but backend will still protect
        setAllowed(true);
      })
      .finally(() => setLoading(false));
  }, [lectureId]);


  const submitFeedback = async () => {
    if (submitting) return; // Prevent double submission

    setSubmitting(true);
    setMessage("");

    try {
      await axios.post("http://localhost:8080/api/feedback/submit", {
        lectureId,
        studentId,
        understand,
        starRating: stars,
        comment
      }, { withCredentials: true });

      setSubmitted(true);
      setMessage("Feedback submitted successfully!");
      setMessageType("success");

    } catch (err) {
      // Parse error message from backend
      const errorMsg = err.response?.data?.message ||
        err.response?.data?.error ||
        err.response?.data ||
        "Failed to submit feedback";
      setMessage(errorMsg);
      setMessageType("error");
    } finally {
      setSubmitting(false);
    }
  };


  if (loading) {
    return <p className="lp-status lp-status--info">Loading feedback…</p>;
  }

  if (!allowed) {
    return (
      <div className="lp-feedback-disabled">
        <p className="lp-status lp-status--warning">
          ⏳ Feedback will be available after the lecture ends
        </p>
      </div>
    );
  }


  if (submitted) {
    return (
      <div className="lp-feedback-success">
        <p className="lp-status lp-status--success">
          ✅ Feedback submitted successfully. Thank you!
        </p>
      </div>
    );
  }

  return (
    <div>
      <h3 className="lp-section-title">Submit Feedback</h3>

      <div className="lp-pill-row">
        <button
          type="button"
          className="lp-pill lp-pill--positive"
          data-active={understand === "YES"}
          aria-pressed={understand === "YES"}
          onClick={() => setUnderstand("YES")}
          disabled={submitting}
        >
          ✓ UNDERSTOOD
        </button>

        <span className="lp-sep">|</span>

        <button
          type="button"
          className="lp-pill lp-pill--negative"
          data-active={understand === "NO"}
          aria-pressed={understand === "NO"}
          onClick={() => setUnderstand("NO")}
          disabled={submitting}
        >
          ✗ NOT UNDERSTOOD
        </button>
      </div>

      <div className="lp-stars" aria-label="Star rating (1 to 5)">
        {[1, 2, 3, 4, 5].map((n) => (
          <button
            key={n}
            type="button"
            className="lp-star-btn"
            data-on={n <= stars}
            aria-label={`${n} star`}
            onClick={() => setStars(n)}
            disabled={submitting}
          >
            ⭐
          </button>
        ))}
      </div>

      <textarea
        className="lp-textarea"
        placeholder="Write your feedback..."
        value={comment}
        onChange={(e) => setComment(e.target.value)}
        disabled={submitting}
      />

      <div className="lp-actions">
        <button
          type="button"
          className="lp-submit"
          onClick={submitFeedback}
          disabled={!comment.trim() || submitting}
        >
          {submitting ? "Submitting..." : "Submit Feedback"}
        </button>
      </div>

      <div className={`lp-status ${messageType ? `lp-status--${messageType}` : ''}`}>
        {message ? (
          <span className={messageType === 'error' ? 'lp-error-msg' : 'lp-success-msg'}>
            {messageType === 'error' ? '⚠️ ' : '✓ '}
            {message}
          </span>
        ) : (
          <>
            Rating: <b>{stars}</b>/5
          </>
        )}
      </div>
    </div>
  );
}

export default FeedbackSection;
