import { Link } from "react-router-dom"
import questbook from "../assets/questbooklogo.png"

function HomePage() {
  return (
    <div>

      {/* HERO SECTION */}
      <div className="container mt-5">
        <div className="row align-items-center">

          <div className="col-md-6">
            <h1 className="display-4">Level Up Your Productivity</h1>
            <p className="lead">
              Complete tasks, earn stars, and unlock achievements with QuestBoard.
            </p>

            <Link to="/register" className="btn btn-primary me-3">
              Get Started
            </Link>

            <Link to="/login" className="btn btn-outline-secondary">
              Login
            </Link>
          </div>

          <div className="col-md-6">
            <img src={questbook} alt="Hero" className="img-fluid" />
          </div>

        </div>
      </div>


      {/* FEATURES SECTION */}
      <div className="container mt-5 text-center">
        <h2>Why QuestBoard?</h2>

        <div className="row mt-4">

          <div className="col-md-4">
            <div className="card p-3">
              <h5> Complete Quests</h5>
              <p>Turn your tasks into fun missions.</p>
            </div>
          </div>

          <div className="col-md-4">
            <div className="card p-3">
              <h5>Earn Rewards</h5>
              <p>Gain stars and level up as you progress.</p>
            </div>
          </div>

          <div className="col-md-4">
            <div className="card p-3">
              <h5> Achievements</h5>
              <p>Unlock achievements and track your growth.</p>
            </div>
          </div>

        </div>
      </div>


      {/* HOW IT WORKS */}
      <div className="container mt-5 text-center">
        <h2>How It Works</h2>

        <div className="row mt-4">
          <div className="col-md-4">
            <p>Sign up</p>
          </div>
          <div className="col-md-4">
            <p>Claim quests</p>
          </div>
          <div className="col-md-4">
            <p>Earn stars & level up</p>
          </div>
        </div>
      </div>

    </div>
  )
}

export default HomePage