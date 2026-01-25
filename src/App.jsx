import { BrowserRouter, Routes, Route } from "react-router-dom";
import StudentDashboard from "./components/StudentDashboard";
import Profile from "./components/Profile";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Student Dashboard */}
        <Route path="/" element={<StudentDashboard />} />

        {/* Profile Page */}
        <Route path="/profile" element={<Profile />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
