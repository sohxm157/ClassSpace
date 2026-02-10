import React, { useState } from "react";
import axios from "axios";

const days = ["MON", "TUE", "WED", "THU", "FRI", "SAT"];

export default function CreateTimetableSlot({ onCreated, onClose }) {
  const [form, setForm] = useState({
    classId: "2",
    weekNumber: "1",
    day: "MON",
    startTime: "10:00",
    endTime: "11:00",
    subject: "DBMS",
    division: "",
  });

  const [loading, setLoading] = useState(false);

  const onChange = (e) => {
    setForm((p) => ({ ...p, [e.target.name]: e.target.value }));
  };

  const submit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      const payload = {
        classId: Number(form.classId),
        weekNumber: Number(form.weekNumber),
        day: form.day,
        startTime: form.startTime, // "10:00"
        endTime: form.endTime,     // "11:00"
        subject: form.subject,
        division: form.division.trim() === "" ? null : form.division.trim(),
      };

      const res = await axios.post(
        "http://localhost:8080/api/teacher/timetable/slot",
        payload,
        { withCredentials: true }
      );

      alert("Timetable slot created!");
      onCreated?.(res.data);
      onClose?.(); // close after success (optional)
    } catch (err) {
      console.error(err?.response?.data || err.message);
      alert(err?.response?.data?.message || "Failed to create slot");
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={submit} style={{ padding: 16 }}>
      <h3 style={{ marginTop: 0 }}>Create Timetable Slot</h3>

      <div style={{ display: "grid", gap: 10, gridTemplateColumns: "1fr 1fr" }}>
        <label>
          Class ID
          <input name="classId" value={form.classId} onChange={onChange} required />
        </label>

        <label>
          Week Number
          <input name="weekNumber" value={form.weekNumber} onChange={onChange} required />
        </label>

        <label>
          Day
          <select name="day" value={form.day} onChange={onChange}>
            {days.map((d) => (
              <option key={d} value={d}>{d}</option>
            ))}
          </select>
        </label>

        <label>
          Subject
          <input name="subject" value={form.subject} onChange={onChange} required />
        </label>

        <label>
          Start Time
          <input type="time" name="startTime" value={form.startTime} onChange={onChange} required />
        </label>

        <label>
          End Time
          <input type="time" name="endTime" value={form.endTime} onChange={onChange} required />
        </label>

        <label style={{ gridColumn: "1 / -1" }}>
          Division (optional)
          <input name="division" value={form.division} onChange={onChange} />
        </label>
      </div>

      <div style={{ display: "flex", gap: 8, marginTop: 12, justifyContent: "flex-end" }}>
        <button type="button" onClick={onClose} disabled={loading}>
          Cancel
        </button>
        <button type="submit" disabled={loading}>
          {loading ? "Creating..." : "Create Slot"}
        </button>
      </div>
    </form>
  );
}
