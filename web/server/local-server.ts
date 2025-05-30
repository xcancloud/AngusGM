const proxy = {
  '/gm': {
    target: 'http://dev-host.xcan.cloud:1806',
    changeOrigin: true,
    rewrite: (path) => path.replace(/^\/gm/, '')
  },
  '/gm/ws': {
    target: 'ws://dev-host.xcan.cloud:1806',
    ws: true,
    changeOrigin: true,
    rewrite: (path) => path.replace(/^\/gm/, '')
  },
  '/storage': {
    target: 'http://dev-files.xcan.cloud',
    //target: 'http://localhost:1819',
    changeOrigin: true,
    //rewrite: (path) => path.replace(/^\/storage/, '')
  },
  '/defender': {
    target: 'http://dev-apis.xcan.cloud',
    //target: 'http://localhost:1823',
    changeOrigin: true,
    //rewrite: (path) => path.replace(/^\/defender/, '')
  }
};

export default {
  host: '0.0.0.0',
  port: 80,
  allowedHosts: ['dev-host.xcan.cloud'],
  strictPort: true,
  open: false,
  proxy
};
