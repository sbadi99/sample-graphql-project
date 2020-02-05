package com.evgo.sample.viewmodel;

/**
 * ViewModel interface for error-handling
 */
public interface ViewModelInterface {
  void setError(Object error);
  Object getError();
}
