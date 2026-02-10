import axios from "axios";

export async function resolveLecture(timetableId, date = null) {
  const payload = date ? { timetableId, date } : { timetableId };

  const res = await axios.post(
    "http://localhost:8080/api/lecture/resolve",
    payload,
    { withCredentials: true }
  );

  return res.data; // LectureDetailsDto { lectureId, subject, lectureDate, ... }
}
