const config = require('./config.js');

let ENV = 'dev';

if (wx.getAccountInfoSync) {
  const accountInfo = wx.getAccountInfoSync();
  const envVersion = accountInfo.miniProgram.envVersion;
  
  if (envVersion === 'release') {
    ENV = 'prod';
  } else if (envVersion === 'trial') {
    ENV = 'prod';
  } else {
    ENV = 'dev';
  }
}

module.exports = {
  baseUrl: config.env[ENV].baseUrl,
  env: ENV
};
