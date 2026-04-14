import React from 'react'
import { Routes, Route, Navigate } from 'react-router-dom'
import Layout from './components/Layout'
import Home from './pages/Home'
import ShopAll from './pages/ShopAll'
import NewLaunches from './pages/NewLaunches'
import About from './pages/About'
import Login from './pages/Login'
import Cart from './pages/Cart'
import FindYourBalance from './pages/FindYourBalance'
import NotFound from './pages/NotFound'
import { CartProvider } from './state/cart'
import { UserProvider } from './state/user'

export default function App() {
  return (
    <UserProvider>
      <CartProvider>
        <Layout>
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/shop" element={<ShopAll />} />
            <Route path="/new" element={<NewLaunches />} />
            <Route path="/about" element={<About />} />
            <Route path="/login" element={<Login />} />
            <Route path="/cart" element={<Cart />} />
            <Route path="/balance" element={<FindYourBalance />} />
            <Route path="/home" element={<Navigate to="/" replace />} />
            <Route path="*" element={<NotFound />} />
          </Routes>
        </Layout>
      </CartProvider>
    </UserProvider>
  )
}
