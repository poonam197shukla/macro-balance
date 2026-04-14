import { useState, useEffect } from 'react'
import { PRODUCTS } from '../data/products'

export function useProducts() {
  const [products, setProducts] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    const fetchProducts = async () => {
      try {
        console.log('Fetching products from API...')
        setLoading(true)
        
        // Create abort controller for timeout
        const controller = new AbortController()
        const timeoutId = setTimeout(() => controller.abort(), 5000) // 5 second timeout
        
        const response = await fetch('http://localhost:8080/api/products', {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
          },
          signal: controller.signal
        })
        
        clearTimeout(timeoutId)
        
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`)
        }
        const responseData = await response.json()
        console.log('API Response:', responseData)
        
        // Extract content array from nested structure
        const productsArray = responseData.data?.content || []
        
        // Transform API response to match component expectations
        const transformedProducts = productsArray.map(product => ({
          id: product.id,
          name: product.name,
          image: `https://via.placeholder.com/300?text=${encodeURIComponent(product.name)}`, // Placeholder image
          category: product.categoryName,
          price: product.price,
          tag: product.avgRating ? `⭐ ${product.avgRating}` : 'New',
          description: `${product.protein}g protein • ${product.fiber}g fiber • ${product.sugar}g sugar`,
          nutrition: {
            protein_g: product.protein,
            fiber_g: product.fiber,
            sugar_g: product.sugar
          }
        }))
        
        console.log('Products fetched successfully:', transformedProducts)
        setProducts(transformedProducts)
        setError(null)
      } catch (err) {
        console.warn('API fetch failed, using fallback data:', err.message)
        console.log('Error details:', err)
        console.log('Falling back to static data...')
        // Fallback to static data when API is not available
        setProducts(PRODUCTS)
        setError(null)
      } finally {
        setLoading(false)
      }
    }

    fetchProducts()
  }, [])

  return { products, loading, error }
}