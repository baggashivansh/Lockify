/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        apple: {
          blue: '#0071e3',
          'blue-light': '#47a3ff',
          'blue-pale': '#e8f2ff',
          'blue-glass': 'rgba(0, 113, 227, 0.08)',
        },
      },
      fontFamily: {
        sans: ['-apple-system', 'BlinkMacSystemFont', 'SF Pro Display', 'Segoe UI', 'Roboto', 'sans-serif'],
      },
      backdropBlur: {
        glass: '20px',
      },
      boxShadow: {
        glass: '0 8px 32px rgba(0, 113, 227, 0.08), 0 2px 8px rgba(0, 0, 0, 0.04)',
        'glass-lg': '0 16px 48px rgba(0, 113, 227, 0.12), 0 4px 16px rgba(0, 0, 0, 0.06)',
      },
    },
  },
  plugins: [],
}
