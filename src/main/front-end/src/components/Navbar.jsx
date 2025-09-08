import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap/dist/js/bootstrap.bundle.min.js'

const Navbar = () => {

    return (
        <nav className="navbar navbar-expand navbar-dark bg-dark"
            aria-label="Second navbar example">
            <div className="container-fluid">
                <a className="navbar-brand" href="http://localhost:8080/home">SpringBoot project</a>
                <div className="collapse navbar-collapse" id="navbarsExample02">
                    <ul className="navbar-nav me-auto">
                        <li className="nav-item"><a className="nav-link active" aria-current="page" href="http://localhost:8080//home">Home</a></li>
                        <li className="nav-item"><a className="nav-link active" href="http://localhost:8080//board/list">Board</a></li>
                        <li className="nav-item"><a className="nav-link active" href="http://localhost:8080//attm/list">Photo</a></li>
                    </ul> 
                </div>
            </div>
        </nav>
    );

}


export default Navbar;