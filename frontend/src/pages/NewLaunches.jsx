import React from 'react'
import SectionHeader from '../components/SectionHeader'
import ProductCard from '../components/ProductCard'
import { useProducts } from '../hooks/useProducts'

export default function NewLaunches() {
  const { products, loading, error } = useProducts()

  const newItems = products.length ? [
    products.find(p => p.id === 'db-almond'),
    products.find(p => p.id === 'pb-iron'),
    products.find(p => p.id === 'mk-peri'),
  ].filter(Boolean) : []

  if (loading) {
    return (
      <div style={{ padding: 20, textAlign: 'center' }}>
        <div>Loading products...</div>
      </div>
    )
  }

  if (error) {
    return (
      <div style={{ padding: 20, textAlign: 'center', color: 'red' }}>
        <div>Error loading products: {error}</div>
      </div>
    )
  }

  return (
    <div>
      <SectionHeader
        title="New Launches / New Arrivals"
        subtitle="Highlight new SKUs and seasonal drops."
      />

      <div className="card" style={{padding:18}}>
        <div style={{display:'flex', alignItems:'center', justifyContent:'space-between', gap:12, flexWrap:'wrap'}}>
          <div>
            <div style={{fontWeight:900, color:'var(--purple-800)'}}>What's new this week</div>
            <div style={{color:'var(--muted)', fontSize:13, marginTop:6}}>
              Rotate this section with real release notes later.
            </div>
          </div>
          <span className="badge">⭐ New</span>
        </div>
      </div>

      <hr className="sep" />

      <div className="grid" style={{gridTemplateColumns:'repeat(auto-fit, minmax(260px, 1fr))'}}>
        {newItems.map(p => <ProductCard key={p.id} product={p} />)}
      </div>
    </div>
  )
}
