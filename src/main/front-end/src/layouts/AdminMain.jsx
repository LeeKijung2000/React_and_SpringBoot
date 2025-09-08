import { useContext } from "react"
import Adminbar from "../components/AdminBar"
import AdminSymbol from "../components/AdminSymbol"
import Navbar from "../components/Navbar"
import { AdminContext } from "../context/AdminContext"
import { Outlet } from "react-router-dom"


const AdminMain = () => {

    const { admin } = useContext(AdminContext);
    // console.log(admin);


    return (
        <div>
            <Navbar />
            <AdminSymbol />

            <div className="container-fluid">
                <div className="row">
                    <Adminbar />

                    <main className="col-md-9 ms-sm-auto col-lg-10 px-md-4">
                        <Outlet />
                    </main>
                </div>
            </div>
        </div>
    );

}

export default AdminMain;