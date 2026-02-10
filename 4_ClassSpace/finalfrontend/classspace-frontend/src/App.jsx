import { Routes, Route, Navigate } from "react-router-dom";
import Login from "./pages/Login";
import Register from "./pages/Register";
import ChangePassword from "./pages/ChangePassword";
import ForgotPassword from "./pages/ForgotPassword";
import StudentDashboard from "./components/StudentDashboard";
import TeacherDashboard from "./components/TeacherDashboard";
import LecturePage from "./pages/LecturePage";
import Profile from "./components/Profile";
import TeacherProfile from "./components/TeacherProfile";
import FeedbackViewWrapper from "./components/FeedbackViewWrapper";
import CoordinatorDashboard from "./components/CoordinatorDashboard";

function App() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/login" replace />} />
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route path="/change-password" element={<ChangePassword />} />
      <Route path="/forgot-password" element={<ForgotPassword />} />
      <Route path="/student/dashboard" element={<StudentDashboard />} />
      <Route path="/student/profile" element={<Profile />} />
      <Route path="/teacher/dashboard" element={<TeacherDashboard />} />
      <Route path="/teacher/profile" element={<TeacherProfile />} />
      <Route path="/lecture/:lectureId" element={<LecturePage />} />
      <Route path="/teacher/lecture/:lectureId/feedback" element={<FeedbackViewWrapper />} />
      <Route path="/coordinator/dashboard" element={<CoordinatorDashboard />} />


    </Routes>
  );
}

export default App;
