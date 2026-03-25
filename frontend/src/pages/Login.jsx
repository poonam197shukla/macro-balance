import React, { useMemo, useState } from 'react'
import SectionHeader from '../components/SectionHeader'
import { useUser } from '../state/user'

function CardTitle({ children }) {
  return <div style={{fontWeight:900, color:'var(--purple-800)', marginBottom:8}}>{children}</div>
}

export default function Login() {
  const { user, login, logout, updateProfile, addAddress } = useUser()
  const [contact, setContact] = useState('')
  const [tab, setTab] = useState('orders')

  const isEmail = useMemo(() => contact.includes('@'), [contact])

  function onLogin() {
    if (!contact.trim()) return
    login(contact.trim())
  }

  return (
    <div className="grid" style={{gap:16}}>
      <SectionHeader
        title="Login"
        subtitle="User can login via email or phone number, then manage orders, addresses, rewards, and profile."
      />

      <div className="card" style={{padding:18}}>
        {!user.isAuthed ? (
          <div className="grid" style={{gridTemplateColumns:'1.2fr .8fr', gap:16}}>
            <div>
              <CardTitle>Sign in</CardTitle>
              <div style={{color:'var(--muted)', fontSize:13, marginBottom:10}}>
                Enter email or phone number (demo: no OTP).
              </div>
              <input className="input" value={contact} onChange={(e)=>setContact(e.target.value)} placeholder="Email or phone number" />
              <div style={{display:'flex', gap:10, marginTop:12, flexWrap:'wrap'}}>
                <button className="btn" onClick={onLogin}>Continue</button>
                <span className="badge">{isEmail ? 'Email flow' : 'Phone flow'}</span>
              </div>
            </div>

            <div className="card" style={{padding:14, boxShadow:'none', border:'1px dashed var(--border)'}}>
              <CardTitle>After login you get</CardTitle>
              <ol style={{margin:0, paddingLeft:18, color:'var(--muted)', lineHeight:1.6}}>
                <li>Order history + status</li>
                <li>Saved addresses</li>
                <li>Balance Rewards</li>
                <li>Profile & preferences</li>
                <li>Help & support</li>
              </ol>
            </div>
          </div>
        ) : (
          <div style={{display:'flex', alignItems:'center', justifyContent:'space-between', gap:12, flexWrap:'wrap'}}>
            <div>
              <div style={{fontWeight:900}}>Welcome, {user.profile.name}</div>
              <div style={{color:'var(--muted)', fontSize:13}}>Signed in as: {user.profile.contact}</div>
            </div>
            <button className="btn secondary" onClick={logout}>Logout</button>
          </div>
        )}
      </div>

      {user.isAuthed ? (
        <div className="grid" style={{gridTemplateColumns:'260px 1fr', gap:16}}>
          <div className="card" style={{padding:12}}>
            <CardTitle>Account</CardTitle>
            {[
              ['orders','1. Orders'],
              ['addresses','2. Saved Addresses'],
              ['rewards','3. Balance Rewards'],
              ['profile','4. Profile & Preferences'],
              ['support','5. Help & Support'],
            ].map(([key,label]) => (
              <button
                key={key}
                className={`btn ${tab===key ? '' : 'secondary'}`}
                style={{width:'100%', justifyContent:'center', marginBottom:8}}
                onClick={() => setTab(key)}
              >
                {label}
              </button>
            ))}
          </div>

          <div className="card" style={{padding:16}}>
            {tab === 'orders' ? (
              <div>
                <CardTitle>Orders</CardTitle>
                <div style={{color:'var(--muted)', fontSize:13}}>
                  Demo actions shown in the diagram: order history, order status, reorder button, invoice download, tracking link.
                </div>

                <hr className="sep" />

                <div className="grid" style={{gridTemplateColumns:'repeat(auto-fit, minmax(240px, 1fr))'}}>
                  <MiniAction title="Order history" desc="View past orders and items" />
                  <MiniAction title="Order status" desc="Check current order state" />
                  <MiniAction title="Reorder" desc="One-tap reorder your favorites" />
                  <MiniAction title="Invoice download" desc="Download GST invoice PDF (demo)" />
                  <MiniAction title="Tracking link" desc="Open shipping tracking link" />
                </div>
              </div>
            ) : null}

            {tab === 'addresses' ? (
              <div>
                <CardTitle>Saved Addresses</CardTitle>
                <AddressManager addresses={user.addresses} addAddress={addAddress} />
              </div>
            ) : null}

            {tab === 'rewards' ? (
              <div>
                <CardTitle>Balance Rewards</CardTitle>
                <div className="card" style={{padding:14, boxShadow:'none'}}>
                  <div style={{display:'flex', gap:12, alignItems:'center', flexWrap:'wrap', justifyContent:'space-between'}}>
                    <div>
                      <div style={{fontWeight:900}}>Tier: {user.rewards.tier}</div>
                      <div style={{color:'var(--muted)', fontSize:13}}>Points: {user.rewards.points}</div>
                    </div>
                    <span className="badge">Rewards wallet</span>
                  </div>
                  <div style={{marginTop:12, color:'var(--muted)', fontSize:13, lineHeight:1.6}}>
                    Next: Add real reward rules (earn per ₹, referral, birthday bonus, etc.)
                  </div>
                </div>
              </div>
            ) : null}

            {tab === 'profile' ? (
              <div>
                <CardTitle>Profile & Preferences</CardTitle>
                <ProfileForm profile={user.profile} updateProfile={updateProfile} />
              </div>
            ) : null}

            {tab === 'support' ? (
              <div>
                <CardTitle>Help & Support</CardTitle>
                <div className="grid" style={{gridTemplateColumns:'repeat(auto-fit, minmax(240px, 1fr))'}}>
                  <MiniAction title="Order issues" desc="Raise a ticket for missing/damaged item" />
                  <MiniAction title="WhatsApp chat" desc="Open WhatsApp support (demo)" />
                  <MiniAction title="FAQ shortcut" desc="Browse FAQs for quick answers" />
                </div>
              </div>
            ) : null}
          </div>
        </div>
      ) : null}
    </div>
  )
}

function MiniAction({ title, desc }) {
  return (
    <div className="card" style={{padding:14, boxShadow:'none'}}>
      <div style={{fontWeight:900}}>{title}</div>
      <div style={{color:'var(--muted)', fontSize:13, marginTop:6}}>{desc}</div>
      <button className="btn secondary" style={{marginTop:10}}>Open</button>
    </div>
  )
}

function ProfileForm({ profile, updateProfile }) {
  const [form, setForm] = useState(profile)

  function set(key, val) { setForm(prev => ({ ...prev, [key]: val })) }
  function save() { updateProfile(form) }

  return (
    <div className="grid" style={{gridTemplateColumns:'repeat(auto-fit, minmax(240px, 1fr))', gap:12}}>
      <Field label="Name">
        <input className="input" value={form.name} onChange={(e)=>set('name', e.target.value)} />
      </Field>
      <Field label="Email / Phone">
        <input className="input" value={form.contact} onChange={(e)=>set('contact', e.target.value)} />
      </Field>
      <Field label="Dietary preference">
        <select className="input" value={form.dietary} onChange={(e)=>set('dietary', e.target.value)}>
          <option>Veg</option>
          <option>Vegan</option>
          <option>Non-veg</option>
          <option>High protein</option>
          <option>Low sugar</option>
        </select>
      </Field>
      <Field label="Communication preference">
        <select className="input" value={form.comms} onChange={(e)=>set('comms', e.target.value)}>
          <option>WhatsApp</option>
          <option>Email</option>
          <option>SMS</option>
        </select>
      </Field>

      <div style={{gridColumn:'1 / -1', display:'flex', gap:10, marginTop:4}}>
        <button className="btn" onClick={save}>Save changes</button>
        <span className="badge">Stored in memory (session only)</span>
      </div>
    </div>
  )
}

function Field({ label, children }) {
  return (
    <div>
      <div style={{fontWeight:800, fontSize:12, color:'var(--purple-800)', marginBottom:6}}>{label}</div>
      {children}
    </div>
  )
}

function AddressManager({ addresses, addAddress }) {
  const [name, setName] = useState('')
  const [line1, setLine1] = useState('')
  const [city, setCity] = useState('')
  const [pincode, setPincode] = useState('')

  function add() {
    if (!line1.trim()) return
    addAddress({ id: String(Date.now()), name: name.trim() || 'Home', line1: line1.trim(), city: city.trim(), pincode: pincode.trim() })
    setName(''); setLine1(''); setCity(''); setPincode('')
  }

  return (
    <div className="grid" style={{gap:12}}>
      <div className="grid" style={{gridTemplateColumns:'repeat(auto-fit, minmax(220px, 1fr))', gap:10}}>
        <input className="input" placeholder="Label (Home/Office)" value={name} onChange={(e)=>setName(e.target.value)} />
        <input className="input" placeholder="Address line" value={line1} onChange={(e)=>setLine1(e.target.value)} />
        <input className="input" placeholder="City" value={city} onChange={(e)=>setCity(e.target.value)} />
        <input className="input" placeholder="Pincode" value={pincode} onChange={(e)=>setPincode(e.target.value)} />
      </div>
      <div style={{display:'flex', gap:10}}>
        <button className="btn" onClick={add}>Add address</button>
        <span className="badge">{addresses.length} saved</span>
      </div>

      {addresses.length ? (
        <div className="grid" style={{gridTemplateColumns:'repeat(auto-fit, minmax(260px, 1fr))'}}>
          {addresses.map(a => (
            <div key={a.id} className="card" style={{padding:14, boxShadow:'none'}}>
              <div style={{fontWeight:900}}>{a.name}</div>
              <div style={{color:'var(--muted)', fontSize:13, marginTop:6}}>
                {a.line1}<br />{a.city} {a.pincode}
              </div>
            </div>
          ))}
        </div>
      ) : (
        <div style={{color:'var(--muted)', fontSize:13}}>No addresses yet. Add one above.</div>
      )}
    </div>
  )
}
