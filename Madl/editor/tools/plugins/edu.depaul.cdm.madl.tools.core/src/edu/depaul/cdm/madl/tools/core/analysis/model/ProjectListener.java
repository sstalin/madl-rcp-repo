package edu.depaul.cdm.madl.tools.core.analysis.model;

/**
 * Used by {@link ProjectManager} to notify others when a project has been analyzed.
 * 
 * @coverage dart.tools.core.model
 */
public interface ProjectListener {

  /**
   * Called on the builder thread (non-UI thread) immediately after a project has been analyzed.
   * 
   * @param event the event describing the analysis (not {@code null})
   */
  void projectAnalyzed(ProjectEvent event);
}
