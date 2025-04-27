/* eslint-disable no-template-curly-in-string */
const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');
const compressing = require('compressing');

const packageInfo = require('../package.json');

const deployEnv = process.env.mode_env;
const zipDist = process.env.zip_dist || false;
const allEnv = ['dev', 'prod', 'priv'];
const allEnvFileName = ['env', 'env.dev', 'env.prod', 'env.priv'];

const editionType = process.env.edition_type || 'COMMUNITY';

function resolve (p) {
  return path.join(__dirname, p);
}

function replace (str, data) {
  for (let i = 0, len = data.length; i < len; i++) {
    const { key, value } = data[i];
    const rex = new RegExp(`(${key}=)\\S+`, 'gmi');
    str = str.replace(rex, '$1' + value);
  }
  return str;
}

const uuid = (() => {
  let index = 0;
  return (str = '') => {
    index++;
    return str + (Math.floor(Math.random() * 10000)) + index + ('' + Date.now());
  };
})();

function start () {
  // 1. Generate version information to public/meta/
  const versionContent = JSON.stringify({ version: packageInfo.version, uuid: uuid() }, null, 2);
  fs.writeFileSync(resolve('../public/meta/version.json'), versionContent, 'utf8');

  // 2. Delete the old environment variable configuration file under public/meta
  for (let i = 0, len = allEnvFileName.length; i < len; i++) {
    const _path = resolve('../public/meta/' + allEnvFileName[i]);
    if (fs.existsSync(_path)) {
      fs.unlinkSync(_path);
    }
  }

  // 3. Update common env configuration file
  const envReplaceList = [
    { key: 'VITE_EDITION_TYPE', value: editionType },
    { key: 'VITE_PROFILE', value: deployEnv }
  ];
  let envContent = fs.readFileSync(resolve('../.env'), 'utf8');
  envContent = replace(envContent, envReplaceList);
  fs.writeFileSync(resolve('../public/meta/env'), envContent, 'utf8');

  // 4. Update deploy env configuration file
  const deployEnvUrlPrefix = [
    { key: 'VITE_GM_URL_PREFIX', value: 'http://192.168.0.102:8802' },
    { key: 'VITE_DISCOVERY_URL_PREFIX', value: 'http://192.168.0.102:8801' }
  ];
  let deployEnvContent = fs.readFileSync(resolve(`../.env.${deployEnv}`), 'utf8');
  deployEnvContent = replace(deployEnvContent, deployEnvUrlPrefix);
  fs.writeFileSync(resolve(`../public/meta/env.${deployEnv}`), deployEnvContent, 'utf8');

  // 5. Delete all nginx configuration files under public/
  if (deployEnv === 'priv') { // Not configuring Nginx in a private environment
    const nginxFileNames = allEnv.map(item => {
      return `nginx_${item}_${packageInfo.name}`;
    });
    for (let i = 0, len = nginxFileNames.length; i < len; i++) {
      const _path = resolve('../public/' + nginxFileNames[i] + '.conf');
      if (fs.existsSync(_path)) {
        fs.unlinkSync(_path);
      }
    }
  }

  // 6. Execute a dynamically generated npm script command to trigger the Vite build tool for deploy privatization environment build workflows
  execSync(`npm run vite:build:${deployEnv}`, { stdio: 'inherit' });

  // 7. Package compiled static resources and modified configurations
  if (zipDist) {
    const source = './dist';
    const dest = 'dist.zip';
    compressing.zip.compressDir(source, dest);
  }
}

start();
