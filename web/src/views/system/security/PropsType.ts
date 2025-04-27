export type SigninLimit = {
  enabled: boolean;
  signoutPeriodInMinutes: string;
  passwordErrorIntervalInMinutes: string;
  lockedPasswordErrorNum: string;
  lockedDurationInMinutes: string;
}

export type SignupAllow = {
  enabled: boolean;
  invitationCode: string;
}

export type PasswordPolicy = {
  minLength: string;
}
export type Alarm = {
  enabled: boolean;
  alarmWay: { value: 'SMS' | 'EMAIL', message: string }[];
  receiveUser: { id: string, fullName: string }[];
}

export type SafetyConfig = {
  signinLimit: SigninLimit;
  signupAllow: SignupAllow;
  passwordPolicy: PasswordPolicy;
  alarm: Alarm
}

export type Operation = 'signinSwitch' | 'registSwitch' | 'earlySwitch' | 'resetButton' | 'safetyCheckBox'
