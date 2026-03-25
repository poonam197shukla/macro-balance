import React, { createContext, useContext, useMemo, useState } from 'react'

const UserContext = createContext(null)

const seedUser = {
  isAuthed: false,
  profile: {
    name: 'Guest',
    contact: '',
    dietary: 'Veg',
    comms: 'WhatsApp',
  },
  addresses: [],
  rewards: { points: 0, tier: 'Starter' },
  orders: [],
}

export function UserProvider({ children }) {
  const [user, setUser] = useState(seedUser)

  const api = useMemo(() => ({
    user,
    login: (contact) => {
      setUser(prev => ({
        ...prev,
        isAuthed: true,
        profile: { ...prev.profile, name: 'MacroBalance Member', contact },
        rewards: { ...prev.rewards, points: 120, tier: 'Balance Rewards' }
      }))
    },
    logout: () => setUser(seedUser),
    addAddress: (addr) => setUser(prev => ({ ...prev, addresses: [...prev.addresses, addr] })),
    updateProfile: (patch) => setUser(prev => ({ ...prev, profile: { ...prev.profile, ...patch } })),
    addOrder: (order) => setUser(prev => ({ ...prev, orders: [order, ...prev.orders] })),
  }), [user])

  return <UserContext.Provider value={api}>{children}</UserContext.Provider>
}

export function useUser() {
  const ctx = useContext(UserContext)
  if (!ctx) throw new Error('useUser must be used within UserProvider')
  return ctx
}
