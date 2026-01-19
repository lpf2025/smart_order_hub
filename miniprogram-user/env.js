function getEnv() {
  const accountInfo = wx.getAccountInfoSync();
  const envVersion = accountInfo.miniProgram.envVersion;
  
  if (envVersion === 'release') {
    return 'prod';
  } else if (envVersion === 'trial') {
    return 'trial';
  } else {
    return 'dev';
  }
}

module.exports = {
  getEnv
};
