import React from 'react'
import { useCart } from '../state/cart'

export default function ProductCard({ product }) {
  const { add } = useCart()

  return (
    <div className="card" style={{padding:12, display:'flex', flexDirection:'column', gap:10, transition:'transform .18s ease'}}>
      <div style={{width:'100%', height:160, overflow:'hidden', borderRadius:8, background:'linear-gradient(180deg, #fff, #f7f7fb)'}}>
        <img
          src={product.image}
          alt={product.name}
          loading="lazy"
          onError={(e) => { e.currentTarget.onerror = null; e.currentTarget.src = 'data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="300" height="300"><rect width="100%" height="100%" fill="%23f3f4f6"/><text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" fill="%23999" font-size="16">Image unavailable</text></svg>' }}
          style={{width:'100%', height:'100%', objectFit:'cover', display:'block'}}
        />
      </div>

      <div style={{display:'flex', justifyContent:'space-between', gap:10, alignItems:'center'}}>
        <div>
          <div style={{fontWeight:900}}>{product.name}</div>
          <div style={{color:'var(--muted)', fontSize:12}}>{product.category}</div>
        </div>
        <span className="badge">{product.tag}</span>
      </div>

      <div style={{color:'var(--muted)', fontSize:13, lineHeight:1.4}}>
        {product.description}
      </div>

      <div style={{display:'flex', gap:10, flexWrap:'wrap', fontSize:12, color:'var(--muted)'}}>
        <span>Protein: <b style={{color:'var(--purple-800)'}}>{product.nutrition.protein_g}g</b></span>
        <span>Fiber: <b style={{color:'var(--purple-800)'}}>{product.nutrition.fiber_g}g</b></span>
        <span>Sugar: <b style={{color:'var(--purple-800)'}}>{product.nutrition.sugar_g}g</b></span>
      </div>

      <div style={{marginTop:'auto', display:'flex', alignItems:'center', justifyContent:'space-between', gap:12}}>
        <div style={{fontWeight:900, color:'var(--purple-800)'}}>₹{product.price}</div>
        <button
          onClick={() => add(product, 1)}
          style={{background:'#28a745', color:'#fff', border:'none', padding:'8px 12px', borderRadius:8, cursor:'pointer'}}
          onMouseOver={e => e.currentTarget.style.opacity = 0.9}
          onMouseOut={e => e.currentTarget.style.opacity = 1}
        >
          Add to Cart
        </button>
      </div>
    </div>
  )
}
