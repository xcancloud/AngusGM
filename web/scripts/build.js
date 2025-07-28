/* eslint-disable no-template-curly-in-string */
const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');
const compressing = require("compressing");

/**
 * Parse command line arguments
 * @returns {Object} Parsed parameters object
 */
function parseArgs() {
  const args = process.argv.slice(2);
  console.log('> args: ', args);

  const params = {};
  for (const arg of args) {
    if (arg.startsWith('--')) {
      const [key, value] = arg.slice(2).split('=');
      params[key] = value || true;
    }
  }
  return params;
}

/**
 * Generate UUID
 * @returns {Function} UUID generation function
 */
const uuid = (() => {
  let index = 0;
  return (str = '') => {
    index++;
    return str + (Math.floor(Math.random() * 10000)) + index + ('' + Date.now());
  };
})();

/**
 * Path resolution utility function
 * @param {string} p Relative path
 * @returns {string} Absolute path
 */
function resolve(p) {
  return path.join(__dirname, p);
}

/**
 * Replace variables in string
 * @param {string} str Original string
 * @param {Array} data Replacement data array
 * @returns {string} Replaced string
 */
function replace(str, data) {
  for (let i = 0, len = data.length; i < len; i++) {
    const { key, value } = data[i];
    const rex = new RegExp(`(${key}=)\\S+`, 'gmi');
    str = str.replace(rex, '$1' + value);
  }
  return str;
}

/**
 * Safely read file
 * @param {string} filePath File path
 * @param {string} encoding Encoding format
 * @returns {string} File content
 */
function safeReadFile(filePath, encoding = 'utf8') {
  try {
    if (!fs.existsSync(filePath)) {
      console.warn(`⚠️  File does not exist: ${filePath}`);
      return '';
    }
    return fs.readFileSync(filePath, encoding);
  } catch (error) {
    console.error(`❌ Failed to read file: ${filePath}`, error.message);
    return '';
  }
}

/**
 * Safely write file
 * @param {string} filePath File path
 * @param {string} content File content
 * @param {string} encoding Encoding format
 */
function safeWriteFile(filePath, content, encoding = 'utf8') {
  try {
    // Ensure directory exists
    const dir = path.dirname(filePath);
    if (!fs.existsSync(dir)) {
      fs.mkdirSync(dir, { recursive: true });
    }
    fs.writeFileSync(filePath, content, encoding);
    console.log(`✅ File written successfully: ${filePath}`);
  } catch (error) {
    console.error(`❌ Failed to write file: ${filePath}`, error.message);
    throw error;
  }
}

/**
 * Merge environment configuration files
 * @param {string} baseEnvPath Base environment configuration file path
 * @param {string} deployEnvPath Deployment environment configuration file path
 * @param {Array} replaceList Replacement list
 * @returns {string} Merged environment configuration content
 */
function mergeEnvFiles(baseEnvPath, deployEnvPath, replaceList) {
  console.log('> Reading base environment configuration file');
  let envContent = safeReadFile(baseEnvPath);

  console.log('> Reading deployment environment configuration file');
  const deployEnvContent = safeReadFile(deployEnvPath);

  // Apply replacement rules
  envContent = replace(envContent, replaceList);

  // Merge environment-specific configuration
  if (deployEnvContent) {
    // Add newline if base configuration is not empty
    if (envContent && !envContent.endsWith('\n\n')) {
      envContent += '\n\n';
    }
    envContent += deployEnvContent;
  }

  return envContent;
}

/**
 * Main function
 */
function start() {
  const params = parseArgs();
  const deployEnv = params.env;
  const editionType = params.edition_type || 'COMMUNITY';
  const zipDist = params.zip_dist || false;

  if (!deployEnv) {
    console.error('❌ Missing required environment parameter --env');
    process.exit(1);
  }

  console.log('> Deploy environment:', deployEnv, 'Edition type:', editionType);

  const packageInfo = require('../package.json');

  try {
    console.log('> Start generating configuration information to public/meta/');

    // Generate replacement list
    const envReplaceList = [
      { key: 'VITE_BUILD_UUID', value: uuid() },
      { key: 'VITE_VERSION', value: packageInfo.version },
      { key: 'VITE_EDITION_TYPE', value: editionType },
      { key: 'VITE_PROFILE', value: deployEnv }
    ];

    // File paths
    const baseEnvPath = resolve('../conf/.env');
    const deployEnvPath = resolve(`../conf/.env.${deployEnv}`);
    const outputPath = resolve('../public/meta/env');

    // Merge environment configuration files
    console.log('> Merging environment configuration files');
    const mergedEnvContent = mergeEnvFiles(baseEnvPath, deployEnvPath, envReplaceList);

    // Write merged configuration file
    safeWriteFile(outputPath, mergedEnvContent, 'utf8');

    console.log('> Execute Vite build command');
    execSync(`npm run vite:build:${deployEnv}`, { stdio: 'inherit' });

    // Package compiled static resources and modified configurations
    if (zipDist) {
      const source = './dist';
      const dest = 'dist.zip';
      compressing.zip.compressDir(source, dest);
    }

    console.log('✅ Build completed');
  } catch (error) {
    console.error('❌ Build failed:', error.message);
    process.exit(1);
  }
}

start();
