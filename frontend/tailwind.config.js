/** @type {import('tailwindcss').Config} */
export default {
  darkMode: ['class'],
  content: ['./index.html', './src/**/*.{ts,tsx}'],
  theme: {
    container: {
      center: true,
      padding: '1.5rem',
      screens: { '2xl': '1280px' },
    },
    extend: {
      colors: {
        // Lo-Fi Café palette
        bg: '#FDFBF7',            // warm cream background
        primary: {
          DEFAULT: '#8A9A86',     // muted sage — calm primary
          600: '#73836F',
          300: '#A8B5A4',
        },
        secondary: { DEFAULT: '#E6D9C8' },  // soft almond
        accent: { DEFAULT: '#D35400' },     // terracotta — warm CTA pop
        ochre: { DEFAULT: '#A67C52' },      // warm ochre
        ink: '#3E2723',           // deep coffee-brown primary text
        // shadcn semantic mappings
        border: 'rgba(62, 39, 35, 0.10)',
        input: 'rgba(62, 39, 35, 0.14)',
        ring: '#8A9A86',
        background: '#FDFBF7',
        foreground: '#3E2723',
        muted: { DEFAULT: '#F1EAE0', foreground: '#7A6B63' },
        card: { DEFAULT: '#FFFFFF', foreground: '#3E2723' },
        destructive: { DEFAULT: '#B23A2E', foreground: '#FFFFFF' },
      },
      fontFamily: {
        display: ['Fraunces', 'ui-serif', 'Georgia', 'serif'],
        sans: ['Inter', 'ui-sans-serif', 'system-ui', 'sans-serif'],
      },
      borderRadius: {
        control: '12px',
        card: '20px',
        hero: '28px',
      },
      boxShadow: {
        soft: '0 6px 24px rgba(62, 39, 35, 0.07)',
        lift: '0 14px 44px rgba(62, 39, 35, 0.12)',
      },
      backdropBlur: { glass: '18px' },
      keyframes: {
        'fade-up': {
          '0%': { opacity: '0', transform: 'translateY(16px)' },
          '100%': { opacity: '1', transform: 'translateY(0)' },
        },
        float: {
          '0%,100%': { transform: 'translateY(0)' },
          '50%': { transform: 'translateY(-10px)' },
        },
      },
      animation: {
        'fade-up': 'fade-up 0.5s ease-out both',
        float: 'float 6s ease-in-out infinite',
      },
    },
  },
  plugins: [require('tailwindcss-animate')],
};
