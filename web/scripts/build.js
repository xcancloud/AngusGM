/* eslint-disable no-template-curly-in-string */
const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');

const packageInfo = require('../package.json');
const deployEnv = process.env.mode_env;
const editionType = process.env.edition_type || 'COMMUNITY';

function resolve (p) {
  return path.join(__dirname, p);
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

  // 2. Update common env configuration file
  const envReplaceList = [
    { key: 'VITE_EDITION_TYPE', value: editionType },
    { key: 'VITE_PROFILE', value: deployEnv }
  ];
  let envContent = fs.readFileSync(resolve('../.env'), 'utf8');
  envContent = replace(envContent, envReplaceList);
  fs.writeFileSync(resolve('../public/meta/env'), envContent, 'utf8');

  // 3. Copy the environment configuration file to the public/meta/directory based on the environment variables
  const deployEnvContent = fs.readFileSync(resolve(`../conf/.env.${deployEnv}`), 'utf8');
  fs.writeFileSync(resolve(`../public/meta/env.${deployEnv}`), deployEnvContent, 'utf8');

  // 4. Copy the nginx configuration file to the public/
  if (deployEnv !== 'priv') { // Not configuring Nginx in a private environment
    const nginxContent = fs.readFileSync(resolve(`../nginx/nginx_${deployEnv}_${packageInfo.name}.conf`), 'utf8');
    fs.writeFileSync(resolve(`../public/nginx_${deployEnv}_${packageInfo.name}.conf`), nginxContent, 'utf8');
  }

  // 5. Execute a dynamically generated npm script command to trigger the Vite build tool for deploy environment build workflows
  execSync(`npm run vite:build:${deployEnv}`, { stdio: 'inherit' });
}

start();
