import React, { useMemo, useState } from 'react'
import { useLocation } from 'react-router-dom'
import SectionHeader from '../components/SectionHeader'
import ProductCard from '../components/ProductCard'
import { CATEGORIES, PRODUCTS } from '../data/products'

function useQuery() {
  const { search } = useLocation()
  return useMemo(() => new URLSearchParams(search), [search])
}

export default function ShopAll() {
  const query = useQuery()
  const initialCategory = query.get('category') || 'All'
  const [category, setCategory] = useState(initialCategory)
  const [sort, setSort] = useState('featured')

  const filtered = useMemo(() => {
    let arr = [...PRODUCTS]
    if (category !== 'All') arr = arr.filter(p => p.category === category)

    if (sort === 'price_asc') arr.sort((a,b) => a.price - b.price)
    if (sort === 'price_desc') arr.sort((a,b) => b.price - a.price)
    if (sort === 'protein_desc') arr.sort((a,b) => (b.nutrition.protein_g - a.nutrition.protein_g))

    return arr
  }, [category, sort])

  return (
    <div>
      <SectionHeader
        title="Shop All"
        subtitle="Grid layout showing MacroBalance products."
        right={
          <div style={{display:'flex', gap:10, flexWrap:'wrap'}}>
            <select className="input" value={category} onChange={(e)=>setCategory(e.target.value)} style={{width:220}}>
              <option value="All">All categories</option>
              {CATEGORIES.map(c => <option key={c} value={c}>{c}</option>)}
            </select>

            <select className="input" value={sort} onChange={(e)=>setSort(e.target.value)} style={{width:220}}>
              <option value="featured">Sort: Featured</option>
              <option value="price_asc">Price: Low → High</option>
              <option value="price_desc">Price: High → Low</option>
              <option value="protein_desc">Protein: High → Low</option>
            </select>
          </div>
        }
      />

      <div className="grid" style={{gridTemplateColumns:'repeat(auto-fit, minmax(260px, 1fr))'}}>
        {filtered.map(p => <ProductCard key={p.id} product={p} />)}
      </div>
    </div>
  )
}
