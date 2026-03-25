export const CATEGORIES = [
  'Date Bits',
  'Protein Bar',
  'Roasted Makhana',
  'Prime Seeds',
  'Trail Mix',
]

/**
 * Minimal mock catalog. Replace with API later.
 */
import dateBiites from '../assets/images/Date-biites.jpg'
import dateBiites2 from '../assets/images/Dates-2.jpg'
import dateBiites3 from '../assets/images/Dates-3.jpg'
import dateBiites4 from '../assets/images/Dates-4.jpg'
import dateBiites5 from '../assets/images/Dates-5.jpg'
import dateBiites6 from '../assets/images/Dates-6.jpg'
import dateBiites7 from '../assets/images/Dates-7.jpg'
import dateBiites8 from '../assets/images/Dates-8.jpg'

export const PRODUCTS = [
  { id: 'db-classic', name: 'Date Bits – Classic Cocoa', image: dateBiites, category: 'Date Bits', price: 199, tag: 'Clean label', description: 'Soft date bites with cocoa + nuts. No refined sugar.', nutrition: { protein_g: 6, sugar_g: 0, fiber_g: 5 } },
  { id: 'db-almond', name: 'Date Bits – Almond Crunch', image: dateBiites3, category: 'Date Bits', price: 219, tag: 'High fiber', description: 'Dates + almonds + seeds for a crunchy bite.', nutrition: { protein_g: 7, sugar_g: 0, fiber_g: 6 } },
  { id: 'pb-iron', name: 'Protein Bar – Iron (Stronger Every Day)', image: dateBiites2, category: 'Protein Bar', price: 249, tag: 'High protein', description: 'Balanced macros with rich chocolate taste.', nutrition: { protein_g: 20, sugar_g: 1, fiber_g: 7 } },
  { id: 'pb-vanilla', name: 'Protein Bar – Vanilla Almond', image: dateBiites4, category: 'Protein Bar', price: 239, tag: 'No added sugar', description: 'Smooth vanilla with almond pieces.', nutrition: { protein_g: 18, sugar_g: 1, fiber_g: 6 } },
  { id: 'mk-peri', name: 'Roasted Makhana – Peri Peri', image: dateBiites5, category: 'Roasted Makhana', price: 149, tag: 'Guilt-free', description: 'Crispy makhana with peri peri spices.', nutrition: { protein_g: 5, sugar_g: 0, fiber_g: 4 } },
  { id: 'mk-salt', name: 'Roasted Makhana – Himalayan Salt', image: dateBiites6, category: 'Roasted Makhana', price: 139, tag: 'Clean snacks', description: 'Lightly salted, ultra-crispy makhana.', nutrition: { protein_g: 5, sugar_g: 0, fiber_g: 4 } },
  { id: 'seed-mix', name: 'Prime Seeds – Daily Mix', image: dateBiites7, category: 'Prime Seeds', price: 299, tag: 'Nutrient dense', description: 'Pumpkin + sunflower + flax + chia mix.', nutrition: { protein_g: 10, sugar_g: 0, fiber_g: 8 } },
  { id: 'trail-nut', name: 'Trail Mix – Nut & Berry', image: dateBiites8, category: 'Trail Mix', price: 349, tag: 'Energy', description: 'Nuts + berries for hikes, office, and travel.', nutrition: { protein_g: 9, sugar_g: 3, fiber_g: 6 } },
]
