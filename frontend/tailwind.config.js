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
            dark: '#166534',
            light: '#22c55e',
            bg: '#f0fdf4',
          },
          blue: {
            DEFAULT: '#0284c7',
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
