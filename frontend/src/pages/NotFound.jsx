import React from 'react'
import { Link } from 'react-router-dom'

export default function NotFound() {
  return (
    <div className="card" style={{padding:18}}>
      <div style={{fontWeight:900, fontSize:18}}>Page not found</div>
      <div style={{color:'var(--muted)', marginTop:8}}>The page you requested doesn’t exist.</div>
      <Link to="/" className="btn" style={{display:'inline-block', marginTop:12}}>Go Home</Link>
    </div>
  )
}
