package cloud.xcan.angus.core.gm.application.cmd.authuser;


public interface AuthUserCurrentCmd {

  void updateCurrentPassword(String oldPassword, String newPassword);

  void checkCurrentPassword(String password);

  void initCurrentPassword(String newPassword);
}
