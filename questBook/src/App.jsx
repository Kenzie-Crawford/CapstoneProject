import { useState } from 'react'
import { BrowserRouter, Routes, Route } from 'react-router-dom'
import './App.css'
import NavBar from './Components/Navbar'
import HomePage from './Pages/Homepage'

function App() {
  

  return (
    <BrowserRouter>
      <NavBar />
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/questboard" element={<h1>QuestBoard</h1>} />
        <Route path="/create-quest" element={<h1>Create Quest</h1>} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
