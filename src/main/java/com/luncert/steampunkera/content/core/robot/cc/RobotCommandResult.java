package com.luncert.steampunkera.content.core.robot.cc;

import mcp.MethodsReturnNonnullByDefault;

import javax.annotation.Nullable;

@MethodsReturnNonnullByDefault
public class RobotCommandResult {

  public enum Status {
    Succeed,
    Failed,
    Executing
  }

  private static final RobotCommandResult EMPTY_SUCCESS =
      new RobotCommandResult(Status.Succeed, null, null);
  private static final RobotCommandResult EMPTY_FAILURE =
      new RobotCommandResult(Status.Failed, null, null);
  private static final RobotCommandResult EMPTY_EXECUTING =
      new RobotCommandResult(Status.Executing, null, null);

  private final Status status;
  private final String errorMessage;
  private final Object[] results;

  public static RobotCommandResult success() {
    return EMPTY_SUCCESS;
  }

  public static RobotCommandResult success(Object...results) {
    return results != null && results.length != 0
        ? new RobotCommandResult(Status.Succeed, null, results) : EMPTY_SUCCESS;
  }

  public static RobotCommandResult failure() {
    return EMPTY_FAILURE;
  }

  public static RobotCommandResult failure(@Nullable String errorMessage) {
    return errorMessage != null
        ? new RobotCommandResult(Status.Failed, errorMessage, null) : EMPTY_FAILURE;
  }

  public static RobotCommandResult executing() {
    return EMPTY_EXECUTING;
  }

  public static boolean isSuccess(RobotCommandResult result) {
    return result != null && Status.Succeed.equals(result.getStatus());
  }

  public static boolean isExecuting(RobotCommandResult result) {
    return result != null && Status.Executing.equals(result.getStatus());
  }

  public RobotCommandResult(Status status, String errorMessage, Object[] results) {
    this.status = status;
    this.errorMessage = errorMessage;
    this.results = results;
  }

  public Status getStatus() {
    return status;
  }

  @Nullable
  public String getErrorMessage() {
    return this.errorMessage;
  }

  @Nullable
  public Object[] getResults() {
    return this.results;
  }
}
