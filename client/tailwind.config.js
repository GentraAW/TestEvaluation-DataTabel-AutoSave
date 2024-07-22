/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{vue,js,ts,jsx,tsx}"],
  theme: {
    extend: {
      fontFamily: {
        sans: ["Poppins", "sans-serif"],
      },
      spacing: {
        72: "18rem",
        84: "21rem",
      },
      screens: {
        mobile: "320px",
        tablet: "640px",
        laptop: "1024px",
        desktop: "1280px",
      },
    },
  },
  plugins: [],
};
