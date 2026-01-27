import Navbar from "./Navbar";
import "../styles/dashboard.css";

function StudentDashboard() {
  return (
    <>
      <Navbar />

      <div className="dashboard">

        {/* 🔔 ANNOUNCEMENTS */}
        <div className="card announcements">
          <div className="card-header">
            <h2>📢 Announcements</h2>
          </div>

          <div className="card-body">
            {/* future: announcements.map(...) */}
            <div className="empty-state">
              No announcements yet.  
              <br />
              You’ll see important updates from your teachers here.
            </div>
          </div>
        </div>

        {/* 🗓 TIMETABLE */}
        <div className="card timetable">
          <div className="card-header">
            <h2>🗓 Weekly Timetable</h2>
          </div>

          <div className="card-body">
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
                    Timetable will be available once the coordinator uploads it.
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

      </div>
    </>
  );
}

export default StudentDashboard;
