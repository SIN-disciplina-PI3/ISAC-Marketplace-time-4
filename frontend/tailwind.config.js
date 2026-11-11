/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        isac: {
          green: {
            DEFAULT: '#15803d',
            50:  '#f0fdf4',
            100: '#dcfce7',
            200: '#bbf7d0',
            300: '#86efac',
            400: '#4ade80',
            500: '#22c55e',
            600: '#16a34a',
            700: '#15803d',
            800: '#166534',
            900: '#14532d',
            dark: '#166534',
            light: '#22c55e',
            bg: '#f0fdf4',
          },
          blue: {
            DEFAULT: '#0284c7',
            50:  '#f0f9ff',
            100: '#e0f2fe',
            200: '#bae6fd',
            300: '#7dd3fc',
            400: '#38bdf8',
            500: '#0ea5e9',
            600: '#0284c7',
            700: '#0369a1',
            800: '#075985',
            900: '#0c4a6e',
            dark: '#0369a1',
            light: '#38bdf8',
            bg: '#f0f9ff',
          },
          sand: {
            DEFAULT: '#fef3c7',
            light: '#fffbeb',
          }
        }
      }
    },
  },
  plugins: [],
}
