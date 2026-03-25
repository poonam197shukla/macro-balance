import React from 'react'

export default function Footer() {
  return (
    <footer style={{borderTop:'1px solid var(--border)', background:'rgba(255,255,255,.65)'}}>
      <div className="container" style={{padding:'16px 0', display:'flex', alignItems:'center', justifyContent:'space-between', gap:16, flexWrap:'wrap'}}>
        <div style={{fontWeight:900, color:'var(--purple-800)'}}>MacroBalance</div>
        <div style={{color:'var(--muted)', fontSize:13}}>Purple + White UI • Demo data only</div>
      </div>
    </footer>
  )
}
