import { useEffect, useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import "../styles/navbar.css";
import CreateTimetableSlot from "./CreateTimetableSlot";

function Navbar({ onSlotCreated }) {
  const navigate = useNavigate();
  const location = useLocation();

  const [user, setUser] = useState(null);
  const [studentInfo, setStudentInfo] = useState(null);

  // No more create slot logic for teachers

  useEffect(() => {
    fetch("http://localhost:8080/auth/me", {
      credentials: "include",
    })
      .then((res) => {
        if (!res.ok) throw new Error("Not logged in");
        return res.json();
      })
      .then((data) => {
        setUser(data);

        // Student-only profile fetch
        if (data.role === "STUDENT") {
          fetch("http://localhost:8080/api/student/profile", {
            credentials: "include",
          })
            .then((res) => res.json())
            .then((student) => setStudentInfo(student));
        } else {
          setStudentInfo(null);
        }
      })
      .catch(() => navigate("/login"));
  }, [navigate]);

  const logout = async () => {
    await fetch("http://localhost:8080/auth/logout", {
      method: "POST",
      credentials: "include",
    });
    navigate("/login");
  };

  if (!user) return null;

  // ✅ Role-based home + profile routes
  const isTeacher = user.role === "TEACHER";
  const isStudent = user.role === "STUDENT";
  const isCoordinator = user.role === "COORDINATOR";

  let homePath = "/login";
  if (isTeacher) homePath = "/teacher/dashboard";
  if (isStudent) homePath = "/student/dashboard";
  if (isCoordinator) homePath = "/coordinator/dashboard";

  const profilePath = isTeacher ? "/teacher/profile" : (isStudent ? "/student/profile" : "/coordinator/dashboard");


  return (
    <>
      <div className="navbar">
        {/* LEFT */}
        <div className="nav-left">
          <span className="brand clickable" onClick={() => navigate(homePath)}>
            ClassSpace
          </span>

          {/* Student: show class info */}
          {isStudent && studentInfo?.className && (
            <span className="class-division-text">
              {studentInfo.className}
              {studentInfo.division && ` | ${studentInfo.division}`}
            </span>
          )}
        </div>

        {/* RIGHT */}
        <div className="nav-right">
          <Link to={profilePath} className="profile-box">
            <div className="avatar">
              {user?.name?.charAt(0)?.toUpperCase() || "U"}
            </div>
            <span className="username">{user?.name || "User"}</span>
          </Link>

          <button className="logout-btn" onClick={logout}>
            Logout
          </button>
        </div>
      </div>
    </>
  );
}

export default Navbar;
