import { motion, AnimatePresence } from 'framer-motion';
import { X, ShoppingBag, Star, Clock, Info } from 'lucide-react';
import { AppButton } from './AppButton';
import { QuantitySelector } from './QuantitySelector';
import { useState } from 'react';

interface FoodDetailModalProps {
  food: any;
  isOpen: boolean;
  onClose: () => void;
  onAddToCart: (foodId: number, quantity: number) => void;
  isAdding: boolean;
}

export const FoodDetailModal = ({
  food,
  isOpen,
  onClose,
  onAddToCart,
  isAdding,
}: FoodDetailModalProps) => {
  const [quantity, setQuantity] = useState(1);
  const [selectedSize, setSelectedSize] = useState<'S' | 'M' | 'L'>('M');

  if (!food) return null;

  return (
    <AnimatePresence>
      {isOpen && (
        <>
          {/* Backdrop */}
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            onClick={onClose}
            className="fixed inset-0 bg-slate-950/60 backdrop-blur-sm z-[100]"
          />
          
          {/* Slide-up Sheet */}
          <motion.div
            initial={{ y: '100%' }}
            animate={{ y: 0 }}
            exit={{ y: '100%' }}
            transition={{ type: 'spring', damping: 25, stiffness: 200 }}
            className="fixed inset-x-0 bottom-0 max-h-[92vh] bg-white dark:bg-slate-900 rounded-t-[3rem] shadow-2xl z-[101] overflow-hidden flex flex-col"
          >
            {/* Header / Close Button */}
            <div className="absolute top-6 right-6 z-10">
              <button 
                onClick={onClose}
                className="w-10 h-10 bg-white/20 backdrop-blur-md border border-white/20 rounded-full flex items-center justify-center text-white shadow-xl hover:scale-110 transition-transform"
              >
                <X size={20} />
              </button>
            </div>

            <div className="overflow-y-auto pb-32">
              {/* Image Section */}
              <div className="relative h-80 w-full overflow-hidden">
                <img 
                  src={food.images?.[0] || 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?q=80&w=1760&auto=format&fit=crop'} 
                  alt={food.name}
                  className="w-full h-full object-cover"
                />
                <div className="absolute inset-0 bg-gradient-to-t from-white dark:from-slate-900 via-transparent to-transparent" />
              </div>

              {/* Content Section */}
              <div className="px-8 -mt-12 relative z-10">
                <div className="flex justify-between items-start gap-4 mb-6">
                  <div>
                    <div className="flex items-center gap-2 mb-2">
                       <span className="bg-primary/10 text-primary text-[10px] font-black uppercase tracking-widest px-3 py-1 rounded-lg">Chef's Choice</span>
                       {food.isVegetarian && (
                         <div className="w-4 h-4 rounded-full border border-emerald-500 flex items-center justify-center"><div className="w-1.5 h-1.5 bg-emerald-500 rounded-full"/></div>
                       )}
                    </div>
                    <h2 className="text-3xl md:text-4xl font-black text-slate-900 dark:text-white leading-tight">{food.name}</h2>
                  </div>
                  <div className="text-3xl font-black text-primary tracking-tighter self-end">₹{food.price * quantity}</div>
                </div>

                <div className="flex items-center gap-6 mb-8 py-4 border-y border-slate-50 dark:border-slate-800/50">
                  <div className="flex items-center gap-1.5 font-bold text-sm text-slate-500">
                    <Star size={16} className="text-amber-400 fill-current" />
                    <span className="text-slate-900 dark:text-white">4.9</span> (120+)
                  </div>
                  <div className="flex items-center gap-1.5 font-bold text-sm text-slate-500">
                    <Clock size={16} className="text-primary/50" />
                    20 min
                  </div>
                  <div className="flex items-center gap-1.5 font-bold text-sm text-slate-500">
                    <Info size={16} className="text-slate-300" />
                    350 kcal
                  </div>
                </div>

                <div className="space-y-8">
                  {/* Description */}
                  <div>
                    <h3 className="text-xs font-black uppercase tracking-[0.2em] text-slate-400 mb-3">Description</h3>
                    <p className="text-slate-500 dark:text-slate-400 font-medium leading-relaxed">
                      {food.description || 'Our signature dish, crafted with fresh seasonal ingredients and authentic spices. Perfectly balanced and served hot.'}
                    </p>
                  </div>

                  {/* Size Selector */}
                  <div>
                    <h3 className="text-xs font-black uppercase tracking-[0.2em] text-slate-400 mb-3">Choice of Size</h3>
                    <div className="flex gap-4">
                      {['S', 'M', 'L'].map((size: any) => (
                        <button
                          key={size}
                          onClick={() => setSelectedSize(size)}
                          className={`flex-1 py-4 rounded-2xl font-black text-xs border transition-all ${
                            selectedSize === size 
                              ? 'bg-primary border-primary text-white shadow-lg shadow-primary/20 scale-105' 
                              : 'bg-white dark:bg-slate-800 border-slate-100 dark:border-slate-800 text-slate-400'
                          }`}
                        >
                          {size === 'S' ? 'Small' : size === 'M' ? 'Medium' : 'Large'}
                        </button>
                      ))}
                    </div>
                  </div>
                </div>
              </div>
            </div>

            {/* Bottom Actions */}
            <div className="absolute bottom-0 inset-x-0 p-8 bg-white/80 dark:bg-slate-900/80 backdrop-blur-xl border-t border-slate-50 dark:border-slate-800/50 flex items-center justify-between gap-8 shadow-[0_-20px_40px_rgba(0,0,0,0.03)]">
              <QuantitySelector 
                size="lg"
                quantity={quantity} 
                onIncrease={() => setQuantity(q => q + 1)} 
                onDecrease={() => setQuantity(q => Math.max(1, q - 1))} 
              />
              <AppButton 
                onClick={() => onAddToCart(food.id, quantity)}
                isLoading={isAdding}
                className="flex-1 h-14"
              >
                <ShoppingBag size={18} /> Add to Order
              </AppButton>
            </div>
          </motion.div>
        </>
      )}
    </AnimatePresence>
  );
};
