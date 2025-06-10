/* eslint-disable no-template-curly-in-string */
const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');

const args = process.argv.slice(2); // Remove node and script path
console.log('> args: ', args);

const params = {};
for (const arg of args) {
  if (arg.startsWith('--')) {
    const [key, value] = arg.slice(2).split('=');
    params[key] = value || true;
  }
}
const deployEnv = params.env;
const editionType = params.edition_type || 'COMMUNITY';
console.log('> deployEnv: ', deployEnv, 'editionType: ', editionType);

const packageInfo = require('../package.json');

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
  console.log('> Generate version information to public/meta/');
  const versionContent = JSON.stringify({ version: packageInfo.version, uuid: uuid() }, null, 2);
  fs.writeFileSync(resolve('../public/meta/version.json'), versionContent, 'utf8');

  console.log('> Update common env configuration file');
  const envReplaceList = [
    { key: 'VITE_EDITION_TYPE', value: editionType },
    { key: 'VITE_PROFILE', value: deployEnv }
  ];
  let envContent = fs.readFileSync(resolve('../conf/.env'), 'utf8');
  envContent = replace(envContent, envReplaceList);
  fs.writeFileSync(resolve('../public/meta/env'), envContent, 'utf8');

  console.log('> Copy the env configuration file to the public/meta/directory based on the environment variables');
  const deployEnvContent = fs.readFileSync(resolve(`../conf/.env.${deployEnv}`), 'utf8');
  fs.writeFileSync(resolve(`../public/meta/env.${deployEnv}`), deployEnvContent, 'utf8');

  console.log('> Execute a dynamically generated npm script command to trigger the Vite build tool for deploy environment build workflows');
  execSync(`npm run vite:build:${deployEnv}`, { stdio: 'inherit' });
}

start();
