import { BrowserRouter, Route, Routes } from "react-router-dom"
import { AdminProvider } from "./context/AdminContext"
import AdminMain from "./layouts/adminMain"
import BoardsManagement from "./pages/BoardsManagement"
import AttmsManagement from "./pages/AttmsManagement"
import MembersManagement from "./pages/MembersManagement"
import Dashboard from "./pages/Dashboard"

function App() {

  return (
    //인증권한
    <AdminProvider>

      <BrowserRouter>
        <Routes>
          <Route path="/" element={<AdminMain/>}> 
            <Route index element={<Dashboard/>}/>
            <Route path="members" element={<MembersManagement/>}/>
            <Route path="boards" element={<BoardsManagement/>}/>
            <Route path="attms" element={<AttmsManagement/>}/>
          </Route>
        </Routes>
      </BrowserRouter>

    </AdminProvider>
  )

}

export default App
