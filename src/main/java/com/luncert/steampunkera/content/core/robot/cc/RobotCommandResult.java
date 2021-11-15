package com.luncert.steampunkera.content.core.robot.cc;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RobotCommandResult {

  private static final RobotCommandResult EMPTY_SUCCESS = new RobotCommandResult(true, null, null);
  private static final RobotCommandResult EMPTY_FAILURE = new RobotCommandResult(false, null, null);

  private final boolean success;
  private final String errorMessage;
  private final Object[] results;

  @Nonnull
  public static RobotCommandResult success() {
    return EMPTY_SUCCESS;
  }

  @Nonnull
  public static RobotCommandResult success(@Nullable Object[] results) {
    return results != null && results.length != 0
        ? new RobotCommandResult(true, null, results) : EMPTY_SUCCESS;
  }

  @Nonnull
  public static RobotCommandResult failure() {
    return EMPTY_FAILURE;
  }

  @Nonnull
  public static RobotCommandResult failure(@Nullable String errorMessage) {
    return errorMessage != null
        ? new RobotCommandResult(false, errorMessage, null) : EMPTY_FAILURE;
  }

  public RobotCommandResult(boolean success, String errorMessage, Object[] results) {
    this.success = success;
    this.errorMessage = errorMessage;
    this.results = results;
  }

  public boolean isSuccess() {
    return this.success;
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
