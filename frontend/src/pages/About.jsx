import React from 'react'
import SectionHeader from '../components/SectionHeader'

export default function About() {
  return (
    <div className="grid" style={{gap:16}}>
      <SectionHeader
        title="About / Made With Purpose"
        subtitle="A simple brand-story page aligned to your site map."
      />

      <div className="card" style={{padding:18}}>
        <div style={{display:'grid', gap:10}}>
          <div style={{fontWeight:900, color:'var(--purple-800)'}}>Our promise</div>
          <p className="p">
            MacroBalance is built around clean-label snacking—simple ingredients, balanced macros,
            and a no-drama approach to everyday nutrition.
          </p>
        </div>
      </div>

      <div className="grid" style={{gridTemplateColumns:'repeat(auto-fit, minmax(260px, 1fr))'}}>
        <div className="card" style={{padding:16}}>
          <div style={{fontWeight:900}}>Clean Snacks</div>
          <p className="p" style={{marginTop:6}}>Minimal ingredients. Transparent nutrition. Easy to trust.</p>
        </div>

        <div className="card" style={{padding:16}}>
          <div style={{fontWeight:900}}>Made with Purpose</div>
          <p className="p" style={{marginTop:6}}>Built for energy, focus, and long days—office or travel.</p>
        </div>

        <div className="card" style={{padding:16}}>
          <div style={{fontWeight:900}}>Balance Rewards</div>
          <p className="p" style={{marginTop:6}}>Earn points, unlock perks, and reorder faster.</p>
        </div>
      </div>
    </div>
  )
}
