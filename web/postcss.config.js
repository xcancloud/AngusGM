const postcssDiscardComments = require('postcss-discard-comments');
const tailwindcss = require('tailwindcss');

module.exports = {
  plugins: [
    postcssDiscardComments({
      removeAll: true
    }),
    tailwindcss
  ]
};
