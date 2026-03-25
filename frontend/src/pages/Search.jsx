import React, { useEffect, useMemo, useState } from 'react'
import SectionHeader from '../components/SectionHeader'
import ProductCard from '../components/ProductCard'
import { PRODUCTS } from '../data/products'

const POPULAR = ['Gut Health', 'No Added Sugar', 'High Fiber', 'Clean Snacks']
const QUICK_FILTERS = [
  { label: 'High protein (>=18g)', fn: (p) => p.nutrition.protein_g >= 18 },
  { label: 'No added sugar (<=1g)', fn: (p) => p.nutrition.sugar_g <= 1 },
  { label: 'High fiber (>=6g)', fn: (p) => p.nutrition.fiber_g >= 6 },
]

export default function Search() {
  const [q, setQ] = useState('')
  const [recent, setRecent] = useState(() => {
    try { return JSON.parse(localStorage.getItem('mb_recent_search') || '[]') } catch { return [] }
  })
  const [activeFilter, setActiveFilter] = useState(null)

  const results = useMemo(() => {
    const term = q.trim().toLowerCase()
    let arr = PRODUCTS
    if (term) {
      arr = arr.filter(p =>
        p.name.toLowerCase().includes(term) ||
        p.category.toLowerCase().includes(term) ||
        p.tag.toLowerCase().includes(term)
      )
    }
    if (activeFilter) arr = arr.filter(activeFilter.fn)
    return arr
  }, [q, activeFilter])

  useEffect(() => {
    localStorage.setItem('mb_recent_search', JSON.stringify(recent.slice(0, 8)))
  }, [recent])

  function commitSearch(val) {
    const s = (val ?? q).trim()
    if (!s) return
    setQ(s)
    setRecent(prev => [s, ...prev.filter(x => x !== s)].slice(0, 8))
  }

  return (
    <div className="grid" style={{gap:16}}>
      <SectionHeader
        title="Search"
        subtitle="Popular searches, suggestions, and results."
      />

      <div className="card" style={{padding:16}}>
        <div style={{display:'flex', gap:10, flexWrap:'wrap', alignItems:'center'}}>
          <input
            className="input"
            value={q}
            onChange={(e)=>setQ(e.target.value)}
            onKeyDown={(e)=> e.key === 'Enter' ? commitSearch() : null}
            placeholder="Search: products, categories, benefits…"
            style={{flex:1, minWidth:260}}
          />
          <button className="btn" onClick={()=>commitSearch()}>Search</button>
          <button className="btn secondary" onClick={()=>{ setQ(''); setActiveFilter(null) }}>Clear</button>
        </div>

        <div style={{marginTop:12, display:'flex', gap:10, flexWrap:'wrap'}}>
          <span className="badge">Popular searches:</span>
          {POPULAR.map(p => (
            <button key={p} className="btn secondary" onClick={()=>commitSearch(p)}>{p}</button>
          ))}
        </div>

        <div style={{marginTop:12, display:'flex', gap:10, flexWrap:'wrap'}}>
          <span className="badge">Quick filters:</span>
          {QUICK_FILTERS.map(f => (
            <button
              key={f.label}
              className={`btn ${activeFilter?.label === f.label ? '' : 'secondary'}`}
              onClick={()=> setActiveFilter(activeFilter?.label === f.label ? null : f)}
            >
              {f.label}
            </button>
          ))}
        </div>

        {recent.length ? (
          <div style={{marginTop:12, display:'flex', gap:10, flexWrap:'wrap'}}>
            <span className="badge">Recent:</span>
            {recent.map(r => (
              <button key={r} className="btn secondary" onClick={()=>commitSearch(r)}>{r}</button>
            ))}
          </div>
        ) : null}
      </div>

      <div>
        <div style={{display:'flex', alignItems:'center', justifyContent:'space-between', gap:12, flexWrap:'wrap'}}>
          <h2 className="h2" style={{margin:0}}>Results</h2>
          <span className="badge">{results.length} items</span>
        </div>

        <div className="grid" style={{marginTop:12, gridTemplateColumns:'repeat(auto-fit, minmax(260px, 1fr))'}}>
          {results.map(p => <ProductCard key={p.id} product={p} />)}
        </div>
      </div>
    </div>
  )
}
