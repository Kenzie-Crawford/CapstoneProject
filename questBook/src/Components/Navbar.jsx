import { Link } from 'react-router-dom'
import questbook from '../assets/questbooklogo.png'

function NavBar() {
  return (
    <nav className="navbar navbar-expand-sm bg-light navbar-light">
      <div className="container-fluid">
        <Link className="navbar-brand" to="/">
      <img src={questbook} alt="QuestBook" height="80" />
    </Link>
     <ul className="navbar-nav">
        <li className="nav-item">
          <Link className="nav-link" to="/">Dashboard</Link>
        </li>
        <li className="nav-item">
          <Link className="nav-link" to="/questboard">QuestBoard</Link>
        </li>
      <li className="nav-item">
        <Link className="nav-link" to="/create-quest">Create Quest</Link>
      </li>
      <li className="nav-item">
        <Link className="nav-link" to="/logout">Logout</Link>
      </li>
    </ul>
  </div>
</nav>
  )
}
export default NavBar;