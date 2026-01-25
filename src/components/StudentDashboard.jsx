import Navbar from "./Navbar";
import "../styles/dashboard.css";

function StudentDashboard() {
  return (
    <>
      <Navbar />

      <div className="dashboard">
        <div className="announcements">
          <h2>📢 Announcements</h2>
          <div className="empty-state">
            Your announcements will be shown here.
          </div>
        </div>

        <div className="timetable">
          <h2>🗓 Weekly Timetable</h2>
          <table>
            <thead>
              <tr>
                <th>Time</th>
                <th>Mon</th>
                <th>Tue</th>
                <th>Wed</th>
                <th>Thu</th>
                <th>Fri</th>
                <th>Sat</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td colSpan="7" className="empty-table">
                  Timetable will be available once coordinator uploads it.
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </>
  );
}

export default StudentDashboard;
