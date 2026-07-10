import { Star } from 'lucide-react';

interface RatingBadgeProps {
  rating: number;
  count?: number;
  className?: string;
  variant?: 'white' | 'glass' | 'orange';
}

export const RatingBadge = ({
  rating,
  count,
  className = '',
  variant = 'white',
}: RatingBadgeProps) => {
  const styles = {
    white: 'bg-white text-slate-800 shadow-sm border border-slate-100',
    glass: 'bg-white/20 backdrop-blur-md text-white border border-white/20',
    orange: 'bg-primary text-white shadow-lg shadow-primary/20',
  };

  return (
    <div className={`flex items-center gap-1 px-3 py-1 rounded-full text-[11px] font-black uppercase tracking-wider ${styles[variant]} ${className}`}>
      <Star size={12} className="fill-current" />
      <span>{rating.toFixed(1)}</span>
      {count && (
        <span className="opacity-40 ml-1">({count})</span>
      )}
    </div>
  );
};
