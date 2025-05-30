import vue from '@vitejs/plugin-vue';
import { defineConfig } from 'vite';
import { resolve } from 'path';
import viteCompression from 'vite-plugin-compression';
import vueJsx from '@vitejs/plugin-vue-jsx';

//import server from './server/dev-server';
import server from './server/local-server';

export default defineConfig({
  server,
  plugins: [
    vue(),
    vueJsx(),
    viteCompression({
      algorithm: 'gzip',
      ext: '.gz',
      threshold: 2048,
      deleteOriginFile: false,
      verbose: true
    })
  ],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  envDir: 'conf',
  clearScreen: false,
  json: {
    namedExports: true,
    stringify: false
  },
  build: {
    outDir: 'dist',
    target: 'es2020',
    minify: 'esbuild',
    manifest: false,
    assetsDir: 'assets',
    cssCodeSplit: true,
    assetsInlineLimit: 4096,
    chunkSizeWarningLimit: 500,
    reportCompressedSize: false
  }
});
