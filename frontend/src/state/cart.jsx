import React, { createContext, useContext, useMemo, useReducer } from 'react'

const CartContext = createContext(null)

function reducer(state, action) {
  switch (action.type) {
    case 'ADD': {
      const { product, qty = 1 } = action
      const existing = state.items[product.id]
      const nextQty = (existing?.qty ?? 0) + qty
      return {
        ...state,
        items: {
          ...state.items,
          [product.id]: { product, qty: nextQty }
        }
      }
    }
    case 'REMOVE': {
      const { productId } = action
      const next = { ...state.items }
      delete next[productId]
      return { ...state, items: next }
    }
    case 'SET_QTY': {
      const { productId, qty } = action
      if (qty <= 0) {
        const next = { ...state.items }
        delete next[productId]
        return { ...state, items: next }
      }
      return {
        ...state,
        items: {
          ...state.items,
          [productId]: { ...state.items[productId], qty }
        }
      }
    }
    case 'CLEAR':
      return { ...state, items: {} }
    default:
      return state
  }
}

const initial = { items: {} }

export function CartProvider({ children }) {
  const [state, dispatch] = useReducer(reducer, initial)

  const api = useMemo(() => {
    const itemsArr = Object.values(state.items)
    const subtotal = itemsArr.reduce((sum, it) => sum + it.product.price * it.qty, 0)
    const shipping = subtotal >= 499 ? 0 : (subtotal === 0 ? 0 : 49)
    const total = subtotal + shipping
    const count = itemsArr.reduce((sum, it) => sum + it.qty, 0)

    return {
      state,
      itemsArr,
      subtotal,
      shipping,
      total,
      count,
      add: (product, qty=1) => dispatch({ type:'ADD', product, qty }),
      remove: (productId) => dispatch({ type:'REMOVE', productId }),
      setQty: (productId, qty) => dispatch({ type:'SET_QTY', productId, qty }),
      clear: () => dispatch({ type:'CLEAR' }),
    }
  }, [state])

  return <CartContext.Provider value={api}>{children}</CartContext.Provider>
}

export function useCart() {
  const ctx = useContext(CartContext)
  if (!ctx) throw new Error('useCart must be used within CartProvider')
  return ctx
}
