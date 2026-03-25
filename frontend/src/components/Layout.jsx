import React from 'react'
import Navbar from './Navbar'
import Footer from './Footer'

export default function Layout({ children }) {
  return (
    <div style={{minHeight:'100vh', display:'flex', flexDirection:'column'}}>
      <Navbar />
      <main className="container" style={{flex:1, padding:'22px 0 36px'}}>
        {children}
      </main>
      <Footer />
    </div>
  )
}
