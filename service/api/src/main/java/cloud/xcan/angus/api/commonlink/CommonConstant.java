package cloud.xcan.angus.api.commonlink;


import cloud.xcan.angus.api.enums.Percentile;
import cloud.xcan.angus.spec.unit.TimeValue;
import java.math.BigDecimal;

public interface CommonConstant {

  String BID_INVITATION_CODE_KEY = "invitationCode";

  int MAX_THEME_CODE_LENGTH = 40;
  int MAX_SOCIAL_USER_ID_LENGTH = 40;

  int DEFAULT_PERF_THREADS = 5000;
  int DEFAULT_STABILITY_THREADS = 200;

  TimeValue DEFAULT_STABILITY_DURATION = TimeValue.ofMinutes(30);
  TimeValue DEFAULT_PERF_DURATION = TimeValue.ofMinutes(50);

  int DEFAULT_PERF_RAMP_UP_THREADS = 100;
  TimeValue DEFAULT_PERF_RAMP_UP_INTERVAL = TimeValue.ofMinutes(1);

  long DEFAULT_ART = 500;
  Percentile DEFAULT_PERCENTILE = Percentile.P90;
  int DEFAULT_PERF_TPS = 500;
  int DEFAULT_STABILITY_TPS = 500;

  BigDecimal DEFAULT_ERROR_RATE = BigDecimal.valueOf(0.01);
  Double DEFAULT_RES_USED_RATE = 75D;

  /**
   * Set the maximum cache theme duration of the browser
   */
  int DEFAULT_THEME_CACHE_AGE = 10 * 60;
  String THEME_MEDIA_TYPE = "text/css";
  String THEME_CSS_FILE_NAME = "main.css";

  int MAX_HEALTH_ALARM_RECIPIENT = 50;

  int MIN_SIGN_OUT_PERIOD = 10;
  int MAX_SIGN_OUT_PERIOD = 30 * 24 * 60;
  int DEFAULT_SIGN_OUT_PERIOD = 30;
  int MAX_PASSWORD_ERROR_INTERVAL = 3 * 24 * 60;
  int DEFAULT_PASSWORD_ERROR_INTERVAL = 30;
  int MAX_LOCKED_PASSWORD_ERROR_NUM = 50;
  int DEFAULT_LOCKED_PASSWORD_ERROR_NUM = 6;
  int DEFAULT_LOCKED_DURATION = 2 * 60;

  int MIN_PASSWORD_LENGTH = 6;
  int MAX_PASSWORD_LENGTH = 50;
  int DEFAULT_PASSWORD_MIN_LENGTH = 10;

  int DEFAULT_MAX_RESOURCE_ACTIVITIES = 200;
  int DEFAULT_MAX_METRICS_DAYS = 15;

  /********* Default quota value *********/
  //// Important, the following settings must be consistent with the pricing resource quota limit ////

  int MAX_CONSUMER = 1000000;
  int MAX_TEST_CONCURRENCY = 2000000;
  int MAX_TEST_NODE = 1000;
  int MAX_TEST_CONCURRENT_TASK = 1000;
  int MAX_FREE_CONSUMER = 50;
  int MAX_FREE_TEST_CONCURRENCY = 100000;
  int MAX_FREE_TEST_NODE = 10;
  int MAX_FREE_TEST_CONCURRENT_TASK = 10;
  int DEFAULT_REAL_NAME_TENANT_FREE_CONSUMER = 50;
  int DEFAULT_REAL_NAME_TENANT_FREE_TEST_CONCURRENCY = 20000;
  int DEFAULT_REAL_NAME_TENANT_FREE_TEST_NODE = 2;
  int DEFAULT_REAL_NAME_TENANT_FREE_TEST_CONCURRENT_TASK = 2;
  int DEFAULT_SIGNUP_TENANT_FREE_CONSUMER = 25;
  int DEFAULT_SIGNUP_TENANT_FREE_TEST_CONCURRENCY = 10000;
  int DEFAULT_SIGNUP_TENANT_FREE_TEST_NODE = 1;
  int DEFAULT_SIGNUP_TENANT_FREE_TEST_CONCURRENT_TASK = 1;

  int MAX_CPU = 64;
  int MAX_MEMORY = 128; // GB
  int MAX_SYSTEM_DISK = 2048; // GB
  int MAX_DATA_DISK = 2048; // GB
  int MAX_BANDWIDTH = 100; // MB

  long MAX_CLOUD_SPACE_DATA_DISK = 2048; // GB
  long DEFAULT_MIN_CLOUD_SPACE_DATA_DISK = 2; // GB
  long DISK_GB_BYTES = 1024 * 1024 * 1024; // 1GB, used by add order
  long DISK_MB_BYTES = 1024 * 1024; // 1MB, used by add order
  /********* Default quota value *********/


}
