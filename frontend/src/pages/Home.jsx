import React from 'react'
import { Link } from 'react-router-dom'
import SectionHeader from '../components/SectionHeader'
import { CATEGORIES } from '../data/products'
import ProductCard from '../components/ProductCard'
import { useProducts } from '../hooks/useProducts'

export default function Home() {
  const { products, loading, error } = useProducts()

  return (
    <div className="grid" style={{gap:18}}>
      <SectionHeader
        title="Fuel your day with MacroBalance"
        subtitle="Shop clean snacks, explore new launches, or take the ‘Find Your Balance’ quiz."
        right={<Link to="/shop" className="btn secondary">Browse all products</Link>}
      />

      <div className="card" style={{padding:18}}>
        <div style={{display:'flex', gap:10, alignItems:'center', flexWrap:'wrap', justifyContent:'space-between'}}>
          <div style={{display:'grid', gap:6}}>
            <div style={{fontWeight:900, color:'var(--purple-800)'}}>Quick paths from your diagram</div>
            <div style={{color:'var(--muted)', fontSize:13}}>HOME → Shop All / New / About / Login / Search / Cart</div>
          </div>
          <div style={{display:'flex', gap:10, flexWrap:'wrap'}}>
            <Link to="/new" className="btn">New Launches</Link>
            <Link to="/about" className="btn secondary">Made With Purpose</Link>
            <Link to="/balance" className="btn secondary">Find Your Balance</Link>
          </div>
        </div>
      </div>

      <div className="grid" style={{gridTemplateColumns:'repeat(auto-fit, minmax(180px, 1fr))'}}>
        {CATEGORIES.map(c => (
          <Link key={c} to={`/shop?category=${encodeURIComponent(c)}`} className="card" style={{padding:16}}>
            <div style={{fontWeight:900, color:'var(--purple-800)'}}>{c}</div>
            <div style={{fontSize:13, color:'var(--muted)', marginTop:6}}>Explore {c.toLowerCase()} collection</div>
          </Link>
        ))}
      </div>

      <div>
        <h2 className="h2">Top picks</h2>
        {loading ? (
          <div style={{ padding: 20, textAlign: 'center' }}>
            <div>Loading products...</div>
          </div>
        ) : error ? (
          <div style={{ padding: 20, textAlign: 'center', color: 'red' }}>
            <div>Error loading products: {error}</div>
          </div>
        ) : (
          <div className="grid" style={{gridTemplateColumns:'repeat(auto-fit, minmax(250px, 1fr))'}}>
            {products.slice(0, 4).map(p => <ProductCard key={p.id} product={p} />)}
          </div>
        )}
      </div>
    </div>
  )
}
