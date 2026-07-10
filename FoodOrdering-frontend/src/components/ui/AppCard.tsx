import type { ComponentProps, ReactNode } from 'react';
import { motion } from 'framer-motion';

type AppCardProps = ComponentProps<typeof motion.div> & {
  children: ReactNode;
  className?: string;
  noPadding?: boolean;
};

export const AppCard = ({ children, className = '', noPadding = false, ...props }: AppCardProps) => {
  return (
    <motion.div
      {...props}
      className={`bg-white dark:bg-slate-900 border border-slate-50 dark:border-slate-800/10 rounded-[2rem] shadow-sm hover:shadow-md transition-all duration-500 ${noPadding ? '' : 'p-6'} ${className}`}
    >
      {children}
    </motion.div>
  );
};
