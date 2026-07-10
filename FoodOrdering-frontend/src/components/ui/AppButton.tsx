import { motion } from 'framer-motion';
import type { ReactNode } from 'react';

interface AppButtonProps {
  children: ReactNode;
  onClick?: () => void;
  className?: string;
  variant?: 'primary' | 'secondary' | 'outline' | 'ghost';
  disabled?: boolean;
  type?: 'button' | 'submit' | 'reset';
  isLoading?: boolean;
}

export const AppButton = ({
  children,
  onClick,
  className = '',
  variant = 'primary',
  disabled = false,
  type = 'button',
  isLoading = false,
}: AppButtonProps) => {
  const baseStyles = 'relative flex items-center justify-center gap-2 font-black transition-all active:scale-95 disabled:opacity-50 disabled:cursor-not-allowed overflow-hidden';
  
  const variants = {
    primary: 'btn-primary',
    secondary: 'bg-warm-100 text-primary hover:bg-warm-200 px-8 py-4 rounded-full',
    outline: 'border-2 border-primary text-primary hover:bg-primary/5 px-8 py-4 rounded-full',
    ghost: 'text-slate-500 hover:text-primary hover:bg-primary/5 px-4 py-2 rounded-xl',
  };

  return (
    <motion.button
      whileTap={{ scale: 0.98 }}
      whileHover={{ scale: 1.02 }}
      type={type}
      disabled={disabled || isLoading}
      onClick={onClick}
      className={`${baseStyles} ${variants[variant]} ${className}`}
    >
      {isLoading ? (
        <div className="w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin" />
      ) : (
        children
      )}
      
      {/* Glossy inner reflection */}
      {variant === 'primary' && (
        <div className="absolute inset-x-0 top-0 h-[1px] bg-white/20" />
      )}
    </motion.button>
  );
};
