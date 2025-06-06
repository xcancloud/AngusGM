package cloud.xcan.angus.api.commonlink.email;

import cloud.xcan.angus.spec.ValueObject;
import cloud.xcan.angus.spec.experimental.Value;
import lombok.Getter;

/**
 * Annotation for template encoding
 */
@Getter
public enum EmailBizKey implements ValueObject<EmailBizKey>, Value<String> {
  // EVENT
  EVENT_NOTICE /*template email*/,
  // VERIFICATION_CODE
  SIGNUP, SIGNIN, PASSWORD_FORGET, PASSWORD_UPDATE, SIGN_CANCEL, MODIFY_EMAIL, BIND_EMAIL, PAY_PASSWORD_UPDATE,
  // ACCEPTANCE_SUBMIT
  REALNAME_AUTH_SUBMIT,
  // ACCEPTANCE_PASSED
  REALNAME_AUTH_PASSED,
  // ACCEPTANCE_FAILURE
  REALNAME_AUDIT_FAILURE,
  // SYS_EXCEPTION_NOTICE
  SYS_EXCEPTION_NOTICE,
  // SYS_RECOVERY_NOTICE
  SYS_RECOVERY_NOTICE,
  // SYS_SECURITY_NOTICE
  SYS_SECURITY_NOTICE,
  // LICENSE_SENT
  LICENSE_SENT,
  // CHANNEL_TEST
  CHANNEL_TEST,
  // OPERATION_TASK_REMINDER
  TODO_REALNAME_AUTH, TODO_BUSINESS_CONSULTATION, TODO_OFFLINE_SERVICE, TODO_WORKORDER, TODO_TRANSFER_CONFIRM, TODO_INVOICE_CONFIRM, TODO_WITHDRAW_CONFIRM,
  // EXPENSES_REMINDER
  RECHARGE_SUCCESS, WITHDRAW_SUCCESS,
  // ORDER_PAY_SUCCESS
  ORDER_PAY_SUCCESS,
  // ORDER_PAY_PENDING
  ORDER_PAY_PENDING,
  // ORDER_PAY_TIMEOUT
  ORDER_PAY_TIMEOUT,
  // ORDER_TO_EXPIRE
  ORDER_TO_EXPIRE,
  // ORDER_CHANGE_SUCCESS
  ORDER_CHANGE_SUCCESS,
  // WORKORDER_PROCESSING
  // WORKORDER_PROCESSING,
  // WORKORDER_REPLY_REMINDER
  WORKORDER_REPLY_REMINDER,
  // INVOICE_APPLY_SUCCESS
  INVOICE_APPLY_SUCCESS,
  // COUPON_DISTRIBUTION
  COUPON_DISTRIBUTION,
  // COUPON_TO_EXPIRE
  COUPON_TO_EXPIRE,
  // BLOG_SUBSCRIPTION_NOTICE
  BLOG_SUBSCRIPTION_NOTICE, // Only email support type
  // TESTING_EXEC_STARTED,TESTING_EXEC_FINISHED,TESTING_EXEC_FAILED
  TESTING_EXEC_STARTED, TESTING_EXEC_FINISHED, TESTING_EXEC_FAILED,
  // TESTING_TASK_OVERDUE
  TESTING_TASK_OVERDUE,
  // INDICATOR_MODIFY_SUBMIT, INDICATOR_AUDIT_PASSED, INDICATOR_AUDIT_FAILED
  INDICATOR_MODIFY_SUBMIT, INDICATOR_AUDIT_PASSED, INDICATOR_AUDIT_FAILED;

  public boolean isEventNotice() {
    return this == EVENT_NOTICE;
  }

  public boolean isSignup() {
    return this == SIGNUP;
  }

  @Override
  public String getValue() {
    return this.name();
  }
}
