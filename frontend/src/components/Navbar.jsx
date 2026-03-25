import React from 'react'
import { NavLink, useNavigate } from 'react-router-dom'
import { useCart } from '../state/cart'

const baseLink = {
  padding: '8px 12px',
  borderRadius: 8,
  fontWeight: 800,
  fontSize: 13,
  color: 'var(--purple-800)'
}

function LinkItem({ to, children }) {
  return (
    <li style={{listStyle:'none'}}>
      <NavLink to={to} style={({ isActive }) => ({
        ...baseLink,
        background: isActive ? 'var(--purple-700)' : 'transparent',
        color: isActive ? '#fff' : baseLink.color,
        border: isActive ? 'none' : '1px solid var(--border)'
      })}>
        {children}
      </NavLink>
    </li>
  )
}

export default function Navbar() {
  const { count } = useCart()
  const navigate = useNavigate()

  const primary = [
    { to: '/shop', label: 'Shop' },
    { to: '/new', label: 'New' },
    { to: '/about', label: 'About' },
  ]

  const secondary = [
    { to: '/search', label: 'Search' },
    { to: '/login', label: 'Login' },
  ]

  return (
    <header style={{
      position:'sticky', top:0, zIndex:10, backdropFilter:'blur(10px)',
      background:'rgba(255,255,255,.90)', borderBottom:'1px solid var(--border)'
    }}>
      <div className="container" style={{display:'flex', alignItems:'center', gap:12, padding:'12px 0'}}>
        <button
          className="btn secondary"
          onClick={() => navigate('/')}
          style={{display:'flex', alignItems:'center', gap:10}}
          aria-label="Go to Home"
        >
          <span style={{width:14, height:14, borderRadius:4, background:'linear-gradient(135deg, var(--purple-700), var(--purple-500))', boxShadow:'0 10px 22px rgba(79,26,163,.12)'}}/>
          <span style={{fontWeight:900, letterSpacing:0.6}}>MACROBALANCE</span>
        </button>

        <nav role="navigation" aria-label="Primary Navigation" style={{display:'flex', gap:18, alignItems:'center'}}>
          <ul style={{display:'flex', gap:8, margin:0, padding:0, alignItems:'center'}}>
            {primary.map(p => <LinkItem key={p.to} to={p.to}>{p.label}</LinkItem>)}
          </ul>

          <ul style={{display:'flex', gap:8, margin:0, padding:0, alignItems:'center'}}>
            {secondary.map(s => <LinkItem key={s.to} to={s.to}>{s.label}</LinkItem>)}
            <li style={{listStyle:'none'}}>
              <NavLink to="/cart" style={({isActive}) => ({...baseLink, display:'inline-flex', alignItems:'center', gap:8, padding:'6px 10px', background:isActive ? 'var(--purple-700)' : 'transparent', color: isActive ? '#fff' : baseLink.color, border: isActive ? 'none' : '1px solid var(--border)', borderRadius:8 })}>
                <span style={{display:'inline-flex', alignItems:'center', gap:8}}>
                  <span aria-hidden>🛒</span>
                  <span style={{fontWeight:800}}>Cart</span>
                </span>
                <span style={{background:'var(--purple-700)', color:'#fff', borderRadius:999, padding:'2px 8px', fontSize:12, marginLeft:6}}>{count}</span>
              </NavLink>
            </li>
          </ul>
        </nav>

        <div style={{marginLeft:'auto'}}>
          <NavLink to="/balance" className="btn">Find Your Balance</NavLink>
        </div>
      </div>
    </header>
  )
}
