import React from 'react'
import SectionHeader from '../components/SectionHeader'
import { useCart } from '../state/cart'
import { useUser } from '../state/user'

export default function Cart() {
  const { itemsArr, subtotal, shipping, total, setQty, remove, clear } = useCart()
  const { user, addOrder } = useUser()

  function checkout() {
    if (itemsArr.length === 0) return
    const order = {
      id: 'MB' + String(Date.now()).slice(-6),
      date: new Date().toISOString(),
      items: itemsArr.map(i => ({ id: i.product.id, name: i.product.name, qty: i.qty, price: i.product.price })),
      total,
      status: 'Processing',
    }
    addOrder(order)
    clear()
    alert(user.isAuthed
      ? `Order placed: ${order.id} (saved under Orders)`
      : `Order placed: ${order.id} (login to keep order history)`)
  }

  return (
    <div className="grid" style={{gap:16}}>
      <SectionHeader
        title="Cart"
        subtitle="Shows cart summary and enables checkout."
      />

      <div className="grid" style={{gridTemplateColumns:'1.4fr .6fr', gap:16}}>
        <div className="card" style={{padding:16}}>
          {itemsArr.length === 0 ? (
            <div style={{color:'var(--muted)'}}>Your cart is empty. Go to Shop All to add items.</div>
          ) : (
            <div className="grid" style={{gap:12}}>
              {itemsArr.map(({ product, qty }) => (
                <div key={product.id} className="card" style={{padding:12, boxShadow:'none', display:'flex', gap:12, alignItems:'center'}}>
                  <img
                    src={product.image}
                    alt={product.name}
                    loading="lazy"
                    onError={(e) => { e.currentTarget.onerror = null; e.currentTarget.src = 'data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="84" height="84"><rect width="100%" height="100%" fill="%23f3f4f6"/><text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" fill="%23999" font-size="10">No image</text></svg>' }}
                    style={{width:84, height:84, objectFit:'cover', borderRadius:8}}
                  />

                  <div style={{flex:1}}>
                    <div style={{fontWeight:900}}>{product.name}</div>
                    <div style={{color:'var(--muted)', fontSize:13, marginTop:4}}>{product.category} • ₹{product.price}</div>
                  </div>

                  <div style={{display:'flex', gap:8, alignItems:'center'}}>
                    <button onClick={()=>setQty(product.id, qty-1)} style={{background:'#ffc107', border:'none', padding:'8px 10px', borderRadius:8, cursor:'pointer'}}>–</button>
                    <input
                      className="input"
                      value={qty}
                      onChange={(e)=>setQty(product.id, Number(e.target.value || 0))}
                      style={{width:70, textAlign:'center'}}
                    />
                    <button onClick={()=>setQty(product.id, qty+1)} style={{background:'#28a745', color:'#fff', border:'none', padding:'8px 10px', borderRadius:8, cursor:'pointer'}}>+</button>
                    <button onClick={()=>remove(product.id)} style={{background:'#dc3545', color:'#fff', border:'none', padding:'8px 10px', borderRadius:8, cursor:'pointer'}}>Remove</button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        <div className="card" style={{padding:16, height:'fit-content'}}>
          <div style={{fontWeight:900, color:'var(--purple-800)'}}>Order Summary</div>
          <div style={{marginTop:10, display:'grid', gap:8, color:'var(--muted)', fontSize:13}}>
            <Row label="Subtotal" value={`₹${subtotal}`} />
            <Row label="Shipping" value={shipping === 0 ? 'Free' : `₹${shipping}`} />
            <hr className="sep" />
            <Row label={<b style={{color:'var(--purple-800)'}}>Total</b>} value={<b style={{color:'var(--purple-800)'}}>₹{total}</b>} />
          </div>

          <button className="btn" style={{width:'100%', marginTop:12}} onClick={checkout}>
            Checkout
          </button>
          <button className="btn secondary" style={{width:'100%', marginTop:10}} onClick={clear}>
            Clear cart
          </button>

          <div style={{marginTop:10, color:'var(--muted)', fontSize:12, lineHeight:1.5}}>
            Tip: Login to see your order history under <b>Login → Orders</b>.
          </div>
        </div>
      </div>
    </div>
  )
}

function Row({ label, value }) {
  return (
    <div style={{display:'flex', justifyContent:'space-between', gap:12}}>
      <div>{label}</div>
      <div>{value}</div>
    </div>
  )
}
