import { Minus, Plus } from 'lucide-react';
import { motion } from 'framer-motion';

interface QuantitySelectorProps {
  quantity: number;
  onIncrease: () => void;
  onDecrease: () => void;
  className?: string;
  size?: 'sm' | 'md' | 'lg';
}

export const QuantitySelector = ({
  quantity,
  onIncrease,
  onDecrease,
  className = '',
  size = 'md',
}: QuantitySelectorProps) => {
  const sizes = {
    sm: 'p-1 gap-2 text-xs',
    md: 'p-1.5 gap-4 text-sm',
    lg: 'p-2.5 gap-8 text-base',
  };

  const btnSizes = {
    sm: 'w-6 h-6',
    md: 'w-10 h-10',
    lg: 'w-14 h-14',
  };

  const iconSizes = {
    sm: 12,
    md: 18,
    lg: 24,
  };

  return (
    <div className={`flex items-center bg-warm-100 dark:bg-slate-800 rounded-full border border-warm-200 dark:border-slate-700/50 ${sizes[size]} shadow-inner ${className}`}>
      <motion.button
        whileTap={{ scale: 0.9 }}
        onClick={onDecrease}
        className={`${btnSizes[size]} flex items-center justify-center bg-white dark:bg-slate-700 text-slate-900 dark:text-white rounded-full shadow-sm hover:shadow-md transition-all active:bg-slate-50`}
      >
        <Minus size={iconSizes[size]} strokeWidth={2.5} />
      </motion.button>
      
      <motion.span 
        key={quantity}
        initial={{ y: 10, opacity: 0 }}
        animate={{ y: 0, opacity: 1 }}
        className="font-black text-slate-900 dark:text-white"
      >
        {quantity}
      </motion.span>
      
      <motion.button
        whileTap={{ scale: 0.9 }}
        onClick={onIncrease}
        className={`${btnSizes[size]} flex items-center justify-center bg-primary text-white rounded-full shadow-lg transition-all hover:shadow-primary/20`}
      >
        <Plus size={iconSizes[size]} strokeWidth={2.5} />
      </motion.button>
    </div>
  );
};
