import React, { useMemo, useState } from 'react'
import SectionHeader from '../components/SectionHeader'
import { PRODUCTS } from '../data/products'
import ProductCard from '../components/ProductCard'

const QUESTIONS = [
  {
    key: 'goal',
    q: 'What are you optimizing for today?',
    options: [
      { label: 'Energy', tag: 'Energy' },
      { label: 'Gut health', tag: 'Gut Health' },
      { label: 'High protein', tag: 'High protein' },
      { label: 'Low sugar', tag: 'No added sugar' },
    ],
  },
  {
    key: 'time',
    q: 'When do you snack most?',
    options: [
      { label: 'Morning', tag: 'Clean snacks' },
      { label: 'Afternoon', tag: 'High fiber' },
      { label: 'Late-night', tag: 'No added sugar' },
      { label: 'Pre-workout', tag: 'High protein' },
    ],
  },
  {
    key: 'texture',
    q: 'Pick a texture',
    options: [
      { label: 'Soft bites', tag: 'Date Bits' },
      { label: 'Crunchy', tag: 'Roasted Makhana' },
      { label: 'Chewy bar', tag: 'Protein Bar' },
      { label: 'Mix & munch', tag: 'Trail Mix' },
    ],
  },
]

export default function FindYourBalance() {
  const [answers, setAnswers] = useState({})

  function select(key, opt) {
    setAnswers(prev => ({ ...prev, [key]: opt }))
  }

  const done = Object.keys(answers).length === QUESTIONS.length

  const recommendations = useMemo(() => {
    if (!done) return []
    const tags = Object.values(answers).map(a => a.tag.toLowerCase())
    let scored = PRODUCTS.map(p => {
      const hay = `${p.name} ${p.category} ${p.tag}`.toLowerCase()
      const score = tags.reduce((s, t) => s + (hay.includes(t) ? 1 : 0), 0)
      return { p, score }
    })
    scored.sort((a,b) => b.score - a.score)
    return scored.filter(x => x.score > 0).slice(0, 4).map(x => x.p)
  }, [answers, done])

  return (
    <div className="grid" style={{gap:16}}>
      <SectionHeader
        title="Find Your Balance"
        subtitle="A small quiz to recommend products (demo logic)."
      />

      <div className="card" style={{padding:16}}>
        <div className="grid" style={{gap:14}}>
          {QUESTIONS.map((qq) => (
            <div key={qq.key}>
              <div style={{fontWeight:900, marginBottom:8}}>{qq.q}</div>
              <div style={{display:'flex', gap:10, flexWrap:'wrap'}}>
                {qq.options.map(opt => {
                  const active = answers[qq.key]?.label === opt.label
                  return (
                    <button
                      key={opt.label}
                      className={`btn ${active ? '' : 'secondary'}`}
                      onClick={() => select(qq.key, opt)}
                    >
                      {opt.label}
                    </button>
                  )
                })}
              </div>
            </div>
          ))}
        </div>
      </div>

      {done ? (
        <div>
          <h2 className="h2">Your recommendations</h2>
          {recommendations.length ? (
            <div className="grid" style={{gridTemplateColumns:'repeat(auto-fit, minmax(260px, 1fr))'}}>
              {recommendations.map(p => <ProductCard key={p.id} product={p} />)}
            </div>
          ) : (
            <div className="card" style={{padding:16, color:'var(--muted)'}}>No match found in demo catalog. Try different answers.</div>
          )}
        </div>
      ) : (
        <div className="card" style={{padding:16, color:'var(--muted)'}}>Answer all questions to see recommendations.</div>
      )}
    </div>
  )
}
