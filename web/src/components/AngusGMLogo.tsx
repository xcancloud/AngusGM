export function AngusGMLogo({ className = "w-8 h-8" }: { className?: string }) {
  return (
    <svg
      className={className}
      viewBox="0 0 100 100"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
    >
      {/* Background Circle */}
      <rect width="100" height="100" rx="20" fill="url(#gmGradient)" />
      
      {/* Letter G */}
      <path
        d="M50 25C35.5 25 25 35.5 25 50C25 64.5 35.5 75 50 75C60 75 68 69 71 61H55V51H75C75.5 53 75.5 55 75.5 57C75.5 69 64.5 80 50 80C32.5 80 20 67.5 20 50C20 32.5 32.5 20 50 20C58 20 65 23.5 69.5 29L62 36.5C59 32.5 54.5 30 50 30C38.5 30 30 38.5 30 50C30 61.5 38.5 70 50 70C58.5 70 65.5 64.5 68 57H50V51Z"
        fill="white"
        fillOpacity="0.95"
      />
      
      {/* Management Circuit Pattern */}
      <circle cx="78" cy="25" r="2.5" fill="white" fillOpacity="0.6" />
      <circle cx="78" cy="75" r="2.5" fill="white" fillOpacity="0.6" />
      <circle cx="22" cy="25" r="2.5" fill="white" fillOpacity="0.6" />
      <line x1="78" y1="25" x2="75" y2="30" stroke="white" strokeOpacity="0.4" strokeWidth="1.5" />
      <line x1="22" y1="25" x2="25" y2="30" stroke="white" strokeOpacity="0.4" strokeWidth="1.5" />
      <line x1="78" y1="75" x2="75" y2="70" stroke="white" strokeOpacity="0.4" strokeWidth="1.5" />
      
      {/* Gradient Definition */}
      <defs>
        <linearGradient id="gmGradient" x1="0" y1="0" x2="100" y2="100">
          <stop offset="0%" stopColor="#3B82F6" />
          <stop offset="100%" stopColor="#1D4ED8" />
        </linearGradient>
      </defs>
    </svg>
  );
}
