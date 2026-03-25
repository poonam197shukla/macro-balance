import React from 'react'

export default function SectionHeader({ title, subtitle, right }) {
  return (
    <div style={{display:'flex', alignItems:'flex-end', justifyContent:'space-between', gap:16, flexWrap:'wrap', margin:'10px 0 16px'}}>
      <div>
        <h1 className="h1">{title}</h1>
        {subtitle ? <p className="p">{subtitle}</p> : null}
      </div>
      {right ? <div>{right}</div> : null}
    </div>
  )
}
