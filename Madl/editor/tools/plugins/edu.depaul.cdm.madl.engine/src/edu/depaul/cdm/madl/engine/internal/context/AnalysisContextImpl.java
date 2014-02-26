/*
 * Copyright (c) 2012, the Madl project authors.
 *
 * Licensed under the Eclipse Public License v1.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package edu.depaul.cdm.madl.engine.internal.context;

import com.google.common.annotations.VisibleForTesting;
import edu.depaul.cdm.madl.engine.AnalysisEngine;
import edu.depaul.cdm.madl.engine.ast.ASTNode;
import edu.depaul.cdm.madl.engine.ast.AnnotatedNode;
import edu.depaul.cdm.madl.engine.ast.Comment;
import edu.depaul.cdm.madl.engine.ast.CompilationUnit;
import edu.depaul.cdm.madl.engine.ast.visitor.NodeLocator;
import edu.depaul.cdm.madl.engine.context.AnalysisContentStatistics;
import edu.depaul.cdm.madl.engine.context.AnalysisContext;
import edu.depaul.cdm.madl.engine.context.AnalysisErrorInfo;
import edu.depaul.cdm.madl.engine.context.AnalysisException;
import edu.depaul.cdm.madl.engine.context.AnalysisOptions;
import edu.depaul.cdm.madl.engine.context.AnalysisResult;
import edu.depaul.cdm.madl.engine.context.ChangeNotice;
import edu.depaul.cdm.madl.engine.context.ChangeSet;
import edu.depaul.cdm.madl.engine.element.CompilationUnitElement;
import edu.depaul.cdm.madl.engine.element.Element;
import edu.depaul.cdm.madl.engine.element.ElementLocation;
import edu.depaul.cdm.madl.engine.element.HtmlElement;
import edu.depaul.cdm.madl.engine.element.LibraryElement;
import edu.depaul.cdm.madl.engine.error.AnalysisError;
import edu.depaul.cdm.madl.engine.html.ast.HtmlUnit;
import edu.depaul.cdm.madl.engine.internal.cache.AnalysisCache;
import edu.depaul.cdm.madl.engine.internal.cache.CacheRetentionPolicy;
import edu.depaul.cdm.madl.engine.internal.cache.CacheState;
import edu.depaul.cdm.madl.engine.internal.cache.MadlEntry;
import edu.depaul.cdm.madl.engine.internal.cache.MadlEntryImpl;
import edu.depaul.cdm.madl.engine.internal.cache.DataDescriptor;
import edu.depaul.cdm.madl.engine.internal.cache.HtmlEntry;
import edu.depaul.cdm.madl.engine.internal.cache.HtmlEntryImpl;
import edu.depaul.cdm.madl.engine.internal.cache.RetentionPriority;
import edu.depaul.cdm.madl.engine.internal.cache.SourceEntry;
import edu.depaul.cdm.madl.engine.internal.cache.SourceEntryImpl;
import edu.depaul.cdm.madl.engine.internal.element.ElementImpl;
import edu.depaul.cdm.madl.engine.internal.element.ElementLocationImpl;
import edu.depaul.cdm.madl.engine.internal.resolver.Library;
import edu.depaul.cdm.madl.engine.internal.resolver.LibraryResolver;
import edu.depaul.cdm.madl.engine.internal.resolver.TypeProvider;
import edu.depaul.cdm.madl.engine.internal.resolver.TypeProviderImpl;
import edu.depaul.cdm.madl.engine.internal.scope.Namespace;
import edu.depaul.cdm.madl.engine.internal.scope.NamespaceBuilder;
import edu.depaul.cdm.madl.engine.internal.task.AnalysisTask;
import edu.depaul.cdm.madl.engine.internal.task.AnalysisTaskVisitor;
import edu.depaul.cdm.madl.engine.internal.task.GenerateMadlErrorsTask;
import edu.depaul.cdm.madl.engine.internal.task.GenerateMadlHintsTask;
import edu.depaul.cdm.madl.engine.internal.task.IncrementalAnalysisTask;
import edu.depaul.cdm.madl.engine.internal.task.ParseMadlTask;
import edu.depaul.cdm.madl.engine.internal.task.ParseHtmlTask;
import edu.depaul.cdm.madl.engine.internal.task.ResolveMadlLibraryTask;
import edu.depaul.cdm.madl.engine.internal.task.ResolveMadlUnitTask;
import edu.depaul.cdm.madl.engine.internal.task.ResolveHtmlTask;
import edu.depaul.cdm.madl.engine.scanner.Token;
import edu.depaul.cdm.madl.engine.sdk.MadlSdk;
import edu.depaul.cdm.madl.engine.source.Source;
import edu.depaul.cdm.madl.engine.source.SourceContainer;
import edu.depaul.cdm.madl.engine.source.SourceFactory;
import edu.depaul.cdm.madl.engine.source.SourceKind;
import edu.depaul.cdm.madl.engine.utilities.collection.ListUtilities;
import edu.depaul.cdm.madl.engine.utilities.os.OSUtilities;
import edu.depaul.cdm.madl.engine.utilities.source.LineInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Instances of the class {@code AnalysisContextImpl} implement an {@link AnalysisContext analysis
 * context}.
 *
 * @coverage madl.engine
 */
public class AnalysisContextImpl implements InternalAnalysisContext {
  /**
   * Instances of the class {@code AnalysisTaskResultRecorder} are used by an analysis context to
   * record the results of a task.
   */
  private class AnalysisTaskResultRecorder implements AnalysisTaskVisitor<SourceEntry> {
    @Override
    public SourceEntry visitGenerateMadlErrorsTask(GenerateMadlErrorsTask task)
        throws AnalysisException {
      return recordGenerateMadlErrorsTask(task);
    }

    @Override
    public SourceEntry visitGenerateMadlHintsTask(GenerateMadlHintsTask task)
        throws AnalysisException {
      return recordGenerateMadlHintsTask(task);
    }

    @Override
    public SourceEntry visitIncrementalAnalysisTask(IncrementalAnalysisTask task)
        throws AnalysisException {
      return recordIncrementalAnalysisTaskResults(task);
    }

    @Override
    public HtmlEntry visitParseHtmlTask(ParseHtmlTask task) throws AnalysisException {
      return recordParseHtmlTaskResults(task);
    }

    @Override
    public MadlEntry visitParseMadlTask(ParseMadlTask task) throws AnalysisException {
      return recordParseMadlTaskResults(task);
    }

    @Override
    public SourceEntry visitResolveHtmlTask(ResolveHtmlTask task) throws AnalysisException {
      return recordResolveHtmlTaskResults(task);
    }

    @Override
    public MadlEntry visitResolveMadlLibraryTask(ResolveMadlLibraryTask task)
        throws AnalysisException {
      return recordResolveMadlLibraryTaskResults(task);
    }

    @Override
    public SourceEntry visitResolveMadlUnitTask(ResolveMadlUnitTask task) throws AnalysisException {
      return recordResolveMadlUnitTaskResults(task);
    }
  }

  private class ContextRetentionPolicy implements CacheRetentionPolicy {
    @Override
    public RetentionPriority getAstPriority(Source source, SourceEntry sourceEntry) {
      for (Source prioritySource : priorityOrder) {
        if (source.equals(prioritySource)) {
          return RetentionPriority.HIGH;
        }
      }
      if (sourceEntry instanceof MadlEntry) {
        MadlEntry madlEntry = (MadlEntry) sourceEntry;
        if (astIsNeeded(madlEntry)) {
          return RetentionPriority.MEDIUM;
        }
      }
      return RetentionPriority.LOW;
    }

    private boolean astIsNeeded(MadlEntry madlEntry) {
      return madlEntry.hasInvalidData(MadlEntry.HINTS)
          || madlEntry.hasInvalidData(MadlEntry.VERIFICATION_ERRORS)
          || madlEntry.hasInvalidData(MadlEntry.RESOLUTION_ERRORS);
    }
  }

  /**
   * The difference between the maximum cache size and the maximum priority order size. The priority
   * list must be capped so that it is less than the cache size. Failure to do so can result in an
   * infinite loop in performAnalysisTask() because re-caching one AST structure can cause another
   * priority source's AST structure to be flushed.
   */
  private static final int PRIORITY_ORDER_SIZE_DELTA = 4;

  /**
   * The set of analysis options controlling the behavior of this context.
   */
  private AnalysisOptionsImpl options = new AnalysisOptionsImpl();

  /**
   * The source factory used to create the sources that can be analyzed in this context.
   */
  private SourceFactory sourceFactory;

  /**
   * A table mapping the sources known to the context to the information known about the source.
   */
  private final AnalysisCache cache;

  /**
   * An array containing sources for which data should not be flushed.
   */
  private Source[] priorityOrder = Source.EMPTY_ARRAY;

  /**
   * A table mapping sources to the change notices that are waiting to be returned related to that
   * source.
   */
  private HashMap<Source, ChangeNoticeImpl> pendingNotices = new HashMap<Source, ChangeNoticeImpl>();

  /**
   * The object used to synchronize access to all of the caches. The rules related to the use of
   * this lock object are
   * <ul>
   * <li>no analysis work is done while holding the lock, and</li>
   * <li>no analysis results can be recorded unless we have obtained the lock and validated that the
   * results are for the same version (modification time) of the source as our current cache
   * content.</li>
   * </ul>
   */
  private Object cacheLock = new Object();

  /**
   * The object used to record the results of performing an analysis task.
   */
  private AnalysisTaskResultRecorder resultRecorder;

  /**
   * Cached information used in incremental analysis or {@code null} if none. Synchronize against
   * {@link #cacheLock} before accessing this field.
   */
  private IncrementalAnalysisCache incrementalAnalysisCache;

  /**
   * Initialize a newly created analysis context.
   */
  public AnalysisContextImpl() {
    super();
    resultRecorder = new AnalysisTaskResultRecorder();
    cache = new AnalysisCache(AnalysisOptionsImpl.DEFAULT_CACHE_SIZE, new ContextRetentionPolicy());
  }

  @Override
  public void addSourceInfo(Source source, SourceEntry info) {
    // This implementation assumes that the access to the cache does not need to be synchronized
    // because no other object can have access to this context while this method is being invoked.
    cache.put(source, info);
  }

  @Override
  public void applyChanges(ChangeSet changeSet) {
    if (changeSet.isEmpty()) {
      return;
    }
    synchronized (cacheLock) {
      //
      // First, compute the list of sources that have been removed.
      //
      ArrayList<Source> removedSources = new ArrayList<Source>(changeSet.getRemoved());
      for (SourceContainer container : changeSet.getRemovedContainers()) {
        addSourcesInContainer(removedSources, container);
      }
      //
      // Then determine which cached results are no longer valid.
      //
      boolean addedMadlSource = false;
      for (Source source : changeSet.getAdded()) {
        if (sourceAvailable(source)) {
          addedMadlSource = true;
        }
      }
      for (Source source : changeSet.getChanged()) {
        sourceChanged(source);
      }
      for (Source source : removedSources) {
        sourceRemoved(source);
      }
      if (addedMadlSource) {
        // TODO(brianwilkerson) This is hugely inefficient, but we need to re-analyze any libraries
        // that might have been referencing the not-yet-existing source that was just added. Longer
        // term we need to keep track of which libraries are referencing non-existing sources and
        // only re-analyze those libraries.
        for (Map.Entry<Source, SourceEntry> mapEntry : cache.entrySet()) {
          SourceEntry sourceEntry = mapEntry.getValue();
          if (!mapEntry.getKey().isInSystemLibrary() && sourceEntry instanceof MadlEntry) {
            MadlEntryImpl madlCopy = ((MadlEntry) sourceEntry).getWritableCopy();
            madlCopy.invalidateAllResolutionInformation();
            mapEntry.setValue(madlCopy);
          }
        }
      }
    }
  }

  @Override
  public String computeDocumentationComment(Element element) throws AnalysisException {
    if (element == null) {
      return null;
    }
    Source source = element.getSource();
    if (source == null) {
      return null;
    }
    CompilationUnit unit = parseCompilationUnit(source);
    if (unit == null) {
      return null;
    }
    NodeLocator locator = new NodeLocator(element.getNameOffset());
    ASTNode nameNode = locator.searchWithin(unit);
    while (nameNode != null) {
      if (nameNode instanceof AnnotatedNode) {
        Comment comment = ((AnnotatedNode) nameNode).getDocumentationComment();
        if (comment == null) {
          return null;
        }
        StringBuilder builder = new StringBuilder();
        Token[] tokens = comment.getTokens();
        for (int i = 0; i < tokens.length; i++) {
          if (i > 0) {
            builder.append(OSUtilities.LINE_SEPARATOR);
          }
          builder.append(tokens[i].getLexeme());
        }
        return builder.toString();
      }
      nameNode = nameNode.getParent();
    }
    return null;
  }

  @Override
  public AnalysisError[] computeErrors(Source source) throws AnalysisException {
    boolean enableHints = options.getHint();
    SourceEntry sourceEntry = getReadableSourceEntry(source);
    if (sourceEntry instanceof MadlEntry) {
      ArrayList<AnalysisError> errors = new ArrayList<AnalysisError>();
      MadlEntry madlEntry = (MadlEntry) sourceEntry;
      ListUtilities.addAll(errors, getMadlParseData(source, madlEntry, MadlEntry.PARSE_ERRORS));
      madlEntry = getReadableMadlEntry(source);
      if (madlEntry.getValue(MadlEntry.SOURCE_KIND) == SourceKind.LIBRARY) {
        ListUtilities.addAll(
            errors,
            getMadlResolutionData(source, source, madlEntry, MadlEntry.RESOLUTION_ERRORS));
        ListUtilities.addAll(
            errors,
            getMadlVerificationData(source, source, madlEntry, MadlEntry.VERIFICATION_ERRORS));
        if (enableHints) {
          ListUtilities.addAll(errors, getMadlHintData(source, source, madlEntry, MadlEntry.HINTS));
        }
      } else {
        Source[] libraries = getLibrariesContaining(source);
        for (Source librarySource : libraries) {
          ListUtilities.addAll(
              errors,
              getMadlResolutionData(source, librarySource, madlEntry, MadlEntry.RESOLUTION_ERRORS));
          ListUtilities.addAll(
              errors,
              getMadlVerificationData(
                  source,
                  librarySource,
                  madlEntry,
                  MadlEntry.VERIFICATION_ERRORS));
          if (enableHints) {
            ListUtilities.addAll(
                errors,
                getMadlHintData(source, librarySource, madlEntry, MadlEntry.HINTS));
          }
        }
      }
      if (errors.isEmpty()) {
        return AnalysisError.NO_ERRORS;
      }
      return errors.toArray(new AnalysisError[errors.size()]);
    } else if (sourceEntry instanceof HtmlEntry) {
      HtmlEntry htmlEntry = (HtmlEntry) sourceEntry;
      return getHtmlResolutionData(source, htmlEntry, HtmlEntry.RESOLUTION_ERRORS);
    }
    return AnalysisError.NO_ERRORS;
  }

  @Override
  public Source[] computeExportedLibraries(Source source) throws AnalysisException {
    return getMadlParseData(source, MadlEntry.EXPORTED_LIBRARIES, Source.EMPTY_ARRAY);
  }

  @Override
  public HtmlElement computeHtmlElement(Source source) throws AnalysisException {
    return getHtmlResolutionData(source, HtmlEntry.ELEMENT, null);
  }

  @Override
  public Source[] computeImportedLibraries(Source source) throws AnalysisException {
    return getMadlParseData(source, MadlEntry.IMPORTED_LIBRARIES, Source.EMPTY_ARRAY);
  }

  @Override
  public SourceKind computeKindOf(Source source) {
    SourceEntry sourceEntry = getReadableSourceEntry(source);
    if (sourceEntry == null) {
      return SourceKind.UNKNOWN;
    } else if (sourceEntry instanceof MadlEntry) {
      try {
        return getMadlParseData(source, (MadlEntry) sourceEntry, MadlEntry.SOURCE_KIND);
      } catch (AnalysisException exception) {
        return SourceKind.UNKNOWN;
      }
    }
    return sourceEntry.getKind();
  }

  @Override
  public LibraryElement computeLibraryElement(Source source) throws AnalysisException {
    return getMadlResolutionData(source, source, MadlEntry.ELEMENT, null);
  }

  @Override
  public LineInfo computeLineInfo(Source source) throws AnalysisException {
    SourceEntry sourceEntry = getReadableSourceEntry(source);
    if (sourceEntry instanceof HtmlEntry) {
      return getHtmlParseData(source, SourceEntry.LINE_INFO, null);
    } else if (sourceEntry instanceof MadlEntry) {
      return getMadlParseData(source, SourceEntry.LINE_INFO, null);
    }
    return null;
  }

  @Override
  public ResolvableCompilationUnit computeResolvableCompilationUnit(Source source)
      throws AnalysisException {
    while (true) {
      synchronized (cacheLock) {
        MadlEntry madlEntry = getReadableMadlEntry(source);
        if (madlEntry == null) {
          throw new AnalysisException("computeResolvableCompilationUnit for non-Madl: "
              + source.getFullName());
        }
        if (madlEntry.getState(MadlEntry.PARSED_UNIT) == CacheState.ERROR) {
          AnalysisException cause = madlEntry.getException();
          if (cause == null) {
            throw new AnalysisException(
                "Internal error: computeResolvableCompilationUnit could not parse "
                    + source.getFullName());
          } else {
            throw new AnalysisException(
                "Internal error: computeResolvableCompilationUnit could not parse "
                    + source.getFullName(),
                cause);
          }
        }
        MadlEntryImpl madlCopy = madlEntry.getWritableCopy();
        CompilationUnit unit = madlCopy.getResolvableCompilationUnit();
        if (unit != null) {
          cache.put(source, madlCopy);
          return new ResolvableCompilationUnit(madlCopy.getModificationTime(), unit);
        }
      }
      cacheMadlParseData(source, getReadableMadlEntry(source), MadlEntry.PARSED_UNIT);
    }
  }

  @Override
  public ResolvableHtmlUnit computeResolvableHtmlUnit(Source source) throws AnalysisException {
    HtmlEntry htmlEntry = getReadableHtmlEntry(source);
    if (htmlEntry == null) {
      throw new AnalysisException("computeResolvableHtmlUnit invoked for non-HTML file: "
          + source.getFullName());
    }
    htmlEntry = cacheHtmlParseData(source, htmlEntry, HtmlEntry.PARSED_UNIT);
    HtmlUnit unit = htmlEntry.getValue(HtmlEntry.PARSED_UNIT);
    if (unit == null) {
      throw new AnalysisException("Internal error: computeResolvableHtmlUnit could not parse "
          + source.getFullName());
    }
    // If the unit is ever modified by resolution then we will need to create a copy of it.
    return new ResolvableHtmlUnit(htmlEntry.getModificationTime(), unit);
  }

  @Override
  public AnalysisContext extractContext(SourceContainer container) {
    return extractContextInto(
        container,
        (InternalAnalysisContext) AnalysisEngine.getInstance().createAnalysisContext());
  }

  @Override
  public InternalAnalysisContext extractContextInto(SourceContainer container,
      InternalAnalysisContext newContext) {
    ArrayList<Source> sourcesToRemove = new ArrayList<Source>();
    synchronized (cacheLock) {
      // Move sources in the specified directory to the new context
      for (Map.Entry<Source, SourceEntry> entry : cache.entrySet()) {
        Source source = entry.getKey();
        if (container.contains(source)) {
          sourcesToRemove.add(source);
          newContext.addSourceInfo(source, entry.getValue().getWritableCopy());
        }
      }

      // TODO (danrubel): Either remove sources or adjust contract described in AnalysisContext.
      // Currently, callers assume that sources have been removed from this context

//      for (Source source : sourcesToRemove) {
//        // TODO(brianwilkerson) Determine whether the source should be removed (that is, whether
//        // there are no additional dependencies on the source), and if so remove all information
//        // about the source.
//        sourceMap.remove(source);
//        parseCache.remove(source);
//        publicNamespaceCache.remove(source);
//        libraryElementCache.remove(source);
//      }
    }

    return newContext;
  }

  @Override
  public AnalysisOptions getAnalysisOptions() {
    return options;
  }

  @Override
  public Element getElement(ElementLocation location) {
    // TODO(brianwilkerson) This should not be a "get" method.
    try {
      String[] components = ((ElementLocationImpl) location).getComponents();
      Source librarySource = computeSourceFromEncoding(components[0]);
      ElementImpl element = (ElementImpl) computeLibraryElement(librarySource);
      for (int i = 1; i < components.length; i++) {
        if (element == null) {
          return null;
        }
        element = element.getChild(components[i]);
      }
      return element;
    } catch (AnalysisException exception) {
      return null;
    }
  }

  @Override
  public AnalysisErrorInfo getErrors(Source source) {
    SourceEntry sourceEntry = getReadableSourceEntry(source);
    if (sourceEntry instanceof MadlEntry) {
      MadlEntry madlEntry = (MadlEntry) sourceEntry;
      return new AnalysisErrorInfoImpl(
          madlEntry.getAllErrors(),
          madlEntry.getValue(SourceEntry.LINE_INFO));
    } else if (sourceEntry instanceof HtmlEntry) {
      HtmlEntry htmlEntry = (HtmlEntry) sourceEntry;
      return new AnalysisErrorInfoImpl(
          htmlEntry.getAllErrors(),
          htmlEntry.getValue(SourceEntry.LINE_INFO));
    }
    return new AnalysisErrorInfoImpl(AnalysisError.NO_ERRORS, null);
  }

  @Override
  public HtmlElement getHtmlElement(Source source) {
    SourceEntry sourceEntry = getReadableSourceEntry(source);
    if (sourceEntry instanceof HtmlEntry) {
      return ((HtmlEntry) sourceEntry).getValue(HtmlEntry.ELEMENT);
    }
    return null;
  }

  @Override
  public Source[] getHtmlFilesReferencing(Source source) {
    SourceKind sourceKind = getKindOf(source);
    if (sourceKind == null) {
      return Source.EMPTY_ARRAY;
    }
    synchronized (cacheLock) {
      ArrayList<Source> htmlSources = new ArrayList<Source>();
      switch (sourceKind) {
        case LIBRARY:
        default:
          for (Map.Entry<Source, SourceEntry> entry : cache.entrySet()) {
            SourceEntry sourceEntry = entry.getValue();
            if (sourceEntry.getKind() == SourceKind.HTML) {
              Source[] referencedLibraries = ((HtmlEntry) sourceEntry).getValue(HtmlEntry.REFERENCED_LIBRARIES);
              if (contains(referencedLibraries, source)) {
                htmlSources.add(entry.getKey());
              }
            }
          }
          break;
        case PART:
          Source[] librarySources = getLibrariesContaining(source);
          for (Map.Entry<Source, SourceEntry> entry : cache.entrySet()) {
            SourceEntry sourceEntry = entry.getValue();
            if (sourceEntry.getKind() == SourceKind.HTML) {
              Source[] referencedLibraries = ((HtmlEntry) sourceEntry).getValue(HtmlEntry.REFERENCED_LIBRARIES);
              if (containsAny(referencedLibraries, librarySources)) {
                htmlSources.add(entry.getKey());
              }
            }
          }
          break;
      }
      if (htmlSources.isEmpty()) {
        return Source.EMPTY_ARRAY;
      }
      return htmlSources.toArray(new Source[htmlSources.size()]);
    }
  }

  @Override
  public Source[] getHtmlSources() {
    return getSources(SourceKind.HTML);
  }

  @Override
  public SourceKind getKindOf(Source source) {
    SourceEntry sourceEntry = getReadableSourceEntry(source);
    if (sourceEntry == null) {
      return SourceKind.UNKNOWN;
    }
    return sourceEntry.getKind();
  }

  @Override
  public Source[] getLaunchableClientLibrarySources() {
    // TODO(brianwilkerson) This needs to filter out libraries that do not reference madl:html,
    // either directly or indirectly.
    ArrayList<Source> sources = new ArrayList<Source>();
    synchronized (cacheLock) {
      for (Map.Entry<Source, SourceEntry> entry : cache.entrySet()) {
        Source source = entry.getKey();
        SourceEntry sourceEntry = entry.getValue();
        if (sourceEntry.getKind() == SourceKind.LIBRARY && !source.isInSystemLibrary()) {
//          MadlEntry madlEntry = (MadlEntry) sourceEntry;
//          if (madlEntry.getValue(MadlEntry.IS_LAUNCHABLE) && madlEntry.getValue(MadlEntry.IS_CLIENT)) {
          sources.add(source);
//          }
        }
      }
    }
    return sources.toArray(new Source[sources.size()]);
  }

  @Override
  public Source[] getLaunchableServerLibrarySources() {
    // TODO(brianwilkerson) This needs to filter out libraries that reference madl:html, either
    // directly or indirectly.
    ArrayList<Source> sources = new ArrayList<Source>();
    synchronized (cacheLock) {
      for (Map.Entry<Source, SourceEntry> entry : cache.entrySet()) {
        Source source = entry.getKey();
        SourceEntry sourceEntry = entry.getValue();
        if (sourceEntry.getKind() == SourceKind.LIBRARY && !source.isInSystemLibrary()) {
//          MadlEntry madlEntry = (MadlEntry) sourceEntry;
//          if (madlEntry.getValue(MadlEntry.IS_LAUNCHABLE) && !madlEntry.getValue(MadlEntry.IS_CLIENT)) {
          sources.add(source);
//          }
        }
      }
    }
    return sources.toArray(new Source[sources.size()]);
  }

  @Override
  public Source[] getLibrariesContaining(Source source) {
    synchronized (cacheLock) {
      SourceEntry sourceEntry = cache.get(source);
      if (sourceEntry == null || sourceEntry.getKind() != SourceKind.PART) {
        return new Source[] {source};
      }
      ArrayList<Source> librarySources = new ArrayList<Source>();
      for (Map.Entry<Source, SourceEntry> entry : cache.entrySet()) {
        sourceEntry = entry.getValue();
        if (sourceEntry.getKind() == SourceKind.LIBRARY) {
          if (contains(((MadlEntry) sourceEntry).getValue(MadlEntry.INCLUDED_PARTS), source)) {
            librarySources.add(entry.getKey());
          }
        }
      }
      if (librarySources.isEmpty()) {
        return Source.EMPTY_ARRAY;
      }
      return librarySources.toArray(new Source[librarySources.size()]);
    }
  }

  @Override
  public Source[] getLibrariesDependingOn(Source librarySource) {
    synchronized (cacheLock) {
      ArrayList<Source> dependentLibraries = new ArrayList<Source>();
      for (Map.Entry<Source, SourceEntry> entry : cache.entrySet()) {
        SourceEntry sourceEntry = entry.getValue();
        if (sourceEntry.getKind() == SourceKind.LIBRARY) {
          if (contains(
              ((MadlEntry) sourceEntry).getValue(MadlEntry.EXPORTED_LIBRARIES),
              librarySource)) {
            dependentLibraries.add(entry.getKey());
          }
          if (contains(
              ((MadlEntry) sourceEntry).getValue(MadlEntry.IMPORTED_LIBRARIES),
              librarySource)) {
            dependentLibraries.add(entry.getKey());
          }
        }
      }
      if (dependentLibraries.isEmpty()) {
        return Source.EMPTY_ARRAY;
      }
      return dependentLibraries.toArray(new Source[dependentLibraries.size()]);
    }
  }

  @Override
  public LibraryElement getLibraryElement(Source source) {
    SourceEntry sourceEntry = getReadableSourceEntry(source);
    if (sourceEntry instanceof MadlEntry) {
      return ((MadlEntry) sourceEntry).getValue(MadlEntry.ELEMENT);
    }
    return null;
  }

  @Override
  public Source[] getLibrarySources() {
    return getSources(SourceKind.LIBRARY);
  }

  @Override
  public LineInfo getLineInfo(Source source) {
    SourceEntry sourceEntry = getReadableSourceEntry(source);
    if (sourceEntry != null) {
      return sourceEntry.getValue(SourceEntry.LINE_INFO);
    }
    return null;
  }

  @Override
  public Namespace getPublicNamespace(LibraryElement library) {
    // TODO(brianwilkerson) Rename this to not start with 'get'. Note that this is not part of the
    // API of the interface.
    Source source = library.getDefiningCompilationUnit().getSource();
    MadlEntry madlEntry = getReadableMadlEntry(source);
    if (madlEntry == null) {
      return null;
    }
    Namespace namespace = null;
    if (madlEntry.getValue(MadlEntry.ELEMENT) == library) {
      namespace = madlEntry.getValue(MadlEntry.PUBLIC_NAMESPACE);
    }
    if (namespace == null) {
      NamespaceBuilder builder = new NamespaceBuilder();
      namespace = builder.createPublicNamespace(library);
      synchronized (cacheLock) {
        madlEntry = getReadableMadlEntry(source);
        if (madlEntry == null) {
          AnalysisEngine.getInstance().getLogger().logError(
              new AnalysisException("A Madl file became a non-Madl file: " + source.getFullName()));
          return null;
        }
        if (madlEntry.getValue(MadlEntry.ELEMENT) == library) {
          MadlEntryImpl madlCopy = getReadableMadlEntry(source).getWritableCopy();
          madlCopy.setValue(MadlEntry.PUBLIC_NAMESPACE, namespace);
          cache.put(source, madlCopy);
        }
      }
    }
    return namespace;
  }

  @Override
  public Namespace getPublicNamespace(Source source) throws AnalysisException {
    // TODO(brianwilkerson) Rename this to not start with 'get'. Note that this is not part of the
    // API of the interface.
    MadlEntry madlEntry = getReadableMadlEntry(source);
    if (madlEntry == null) {
      return null;
    }
    Namespace namespace = madlEntry.getValue(MadlEntry.PUBLIC_NAMESPACE);
    if (namespace == null) {
      LibraryElement library = computeLibraryElement(source);
      if (library == null) {
        return null;
      }
      NamespaceBuilder builder = new NamespaceBuilder();
      namespace = builder.createPublicNamespace(library);
      synchronized (cacheLock) {
        madlEntry = getReadableMadlEntry(source);
        if (madlEntry == null) {
          throw new AnalysisException("A Madl file became a non-Madl file: " + source.getFullName());
        }
        if (madlEntry.getValue(MadlEntry.ELEMENT) == library) {
          MadlEntryImpl madlCopy = getReadableMadlEntry(source).getWritableCopy();
          madlCopy.setValue(MadlEntry.PUBLIC_NAMESPACE, namespace);
          cache.put(source, madlCopy);
        }
      }
    }
    return namespace;
  }

  @Override
  public Source[] getRefactoringUnsafeSources() {
    ArrayList<Source> sources = new ArrayList<Source>();
    synchronized (cacheLock) {
      for (Map.Entry<Source, SourceEntry> entry : cache.entrySet()) {
        SourceEntry sourceEntry = entry.getValue();
        if (sourceEntry instanceof MadlEntry) {
          if (!((MadlEntry) sourceEntry).isRefactoringSafe()) {
            sources.add(entry.getKey());
          }
        }
      }
    }
    return sources.toArray(new Source[sources.size()]);
  }

  @Override
  public CompilationUnit getResolvedCompilationUnit(Source unitSource, LibraryElement library) {
    if (library == null) {
      return null;
    }
    return getResolvedCompilationUnit(unitSource, library.getSource());
  }

  @Override
  public CompilationUnit getResolvedCompilationUnit(Source unitSource, Source librarySource) {
    SourceEntry sourceEntry = getReadableSourceEntry(unitSource);
    if (sourceEntry instanceof MadlEntry) {
      return ((MadlEntry) sourceEntry).getValue(MadlEntry.RESOLVED_UNIT, librarySource);
    }
    return null;
  }

  @Override
  public HtmlUnit getResolvedHtmlUnit(Source htmlSource) {
    SourceEntry sourceEntry = getReadableSourceEntry(htmlSource);
    if (sourceEntry instanceof HtmlEntry) {
      HtmlEntry htmlEntry = (HtmlEntry) sourceEntry;
      if (htmlEntry.getValue(HtmlEntry.ELEMENT) != null) {
        return htmlEntry.getValue(HtmlEntry.PARSED_UNIT);
      }
    }
    return null;
  }

  @Override
  public SourceFactory getSourceFactory() {
    return sourceFactory;
  }

  /**
   * Return a list of the sources that would be processed by {@link #performAnalysisTask()}. This
   * method duplicates, and must therefore be kept in sync with, {@link #getNextTaskAnalysisTask()}.
   * This method is intended to be used for testing purposes only.
   *
   * @return a list of the sources that would be processed by {@link #performAnalysisTask()}
   */
  @VisibleForTesting
  public List<Source> getSourcesNeedingProcessing() {
    HashSet<Source> sources = new HashSet<Source>();
    synchronized (cacheLock) {
      boolean hintsEnabled = options.getHint();
      //
      // Look for priority sources that need to be analyzed.
      //
      for (Source source : priorityOrder) {
        getSourcesNeedingProcessing(source, cache.get(source), true, hintsEnabled, sources);
      }
      //
      // Look for non-priority sources that need to be analyzed.
      //
      for (Map.Entry<Source, SourceEntry> entry : cache.entrySet()) {
        getSourcesNeedingProcessing(entry.getKey(), entry.getValue(), false, hintsEnabled, sources);
      }
    }
    return new ArrayList<Source>(sources);
  }

  @Override
  public AnalysisContentStatistics getStatistics() {
    AnalysisContentStatisticsImpl statistics = new AnalysisContentStatisticsImpl();
    synchronized (cacheLock) {
      for (Entry<Source, SourceEntry> mapEntry : cache.entrySet()) {
        SourceEntry entry = mapEntry.getValue();
        if (entry instanceof MadlEntry) {
          Source source = mapEntry.getKey();
          MadlEntry madlEntry = (MadlEntry) entry;
          SourceKind kind = madlEntry.getValue(MadlEntry.SOURCE_KIND);
          // get library independent values
          statistics.putCacheItem(madlEntry, MadlEntry.PARSE_ERRORS);
          statistics.putCacheItem(madlEntry, MadlEntry.PARSED_UNIT);
          statistics.putCacheItem(madlEntry, MadlEntry.SOURCE_KIND);
          statistics.putCacheItem(madlEntry, SourceEntry.LINE_INFO);
          if (kind == SourceKind.LIBRARY) {
            statistics.putCacheItem(madlEntry, MadlEntry.ELEMENT);
            statistics.putCacheItem(madlEntry, MadlEntry.EXPORTED_LIBRARIES);
            statistics.putCacheItem(madlEntry, MadlEntry.IMPORTED_LIBRARIES);
            statistics.putCacheItem(madlEntry, MadlEntry.INCLUDED_PARTS);
            statistics.putCacheItem(madlEntry, MadlEntry.IS_CLIENT);
            statistics.putCacheItem(madlEntry, MadlEntry.IS_LAUNCHABLE);
            // The public namespace isn't computed by performAnalysisTask() and therefore isn't
            // interesting.
            //statistics.putCacheItem(madlEntry, MadlEntry.PUBLIC_NAMESPACE);
          }
          // get library-specific values
          Source[] librarySources = getLibrariesContaining(source);
          for (Source librarySource : librarySources) {
            statistics.putCacheItem(madlEntry, librarySource, MadlEntry.HINTS);
            statistics.putCacheItem(madlEntry, librarySource, MadlEntry.RESOLUTION_ERRORS);
            statistics.putCacheItem(madlEntry, librarySource, MadlEntry.RESOLVED_UNIT);
            statistics.putCacheItem(madlEntry, librarySource, MadlEntry.VERIFICATION_ERRORS);
          }
//        } else if (entry instanceof HtmlEntry) {
//          HtmlEntry htmlEntry = (HtmlEntry) entry;
        }
      }
    }
    return statistics;
  }

  @Override
  public TypeProvider getTypeProvider() throws AnalysisException {
    Source coreSource = getSourceFactory().forUri(MadlSdk.MADL_CORE);
    return new TypeProviderImpl(computeLibraryElement(coreSource));
  }

  @Override
  public TimestampedData<CompilationUnit> internalResolveCompilationUnit(Source unitSource,
      LibraryElement libraryElement) throws AnalysisException {
    MadlEntry madlEntry = getReadableMadlEntry(unitSource);
    if (madlEntry == null) {
      throw new AnalysisException("internalResolveCompilationUnit invoked for non-Madl file: "
          + unitSource.getFullName());
    }
    Source librarySource = libraryElement.getSource();
    madlEntry = cacheMadlResolutionData(
        unitSource,
        librarySource,
        madlEntry,
        MadlEntry.RESOLVED_UNIT);
    return new TimestampedData<CompilationUnit>(
        madlEntry.getModificationTime(),
        madlEntry.getValue(MadlEntry.RESOLVED_UNIT, librarySource));
  }

  @Override
  public boolean isClientLibrary(Source librarySource) {
    SourceEntry sourceEntry = getReadableSourceEntry(librarySource);
    if (sourceEntry instanceof MadlEntry) {
      MadlEntry madlEntry = (MadlEntry) sourceEntry;
      return madlEntry.getValue(MadlEntry.IS_CLIENT) && madlEntry.getValue(MadlEntry.IS_LAUNCHABLE);
    }
    return false;
  }

  @Override
  public boolean isServerLibrary(Source librarySource) {
    SourceEntry sourceEntry = getReadableSourceEntry(librarySource);
    if (sourceEntry instanceof MadlEntry) {
      MadlEntry madlEntry = (MadlEntry) sourceEntry;
      return !madlEntry.getValue(MadlEntry.IS_CLIENT)
          && madlEntry.getValue(MadlEntry.IS_LAUNCHABLE);
    }
    return false;
  }

  @Override
  public void mergeContext(AnalysisContext context) {
    if (context instanceof InstrumentedAnalysisContextImpl) {
      context = ((InstrumentedAnalysisContextImpl) context).getBasis();
    }
    if (!(context instanceof AnalysisContextImpl)) {
      return;
    }
    synchronized (cacheLock) {
      // TODO(brianwilkerson) This does not lock against the other context's cacheLock.
      for (Map.Entry<Source, SourceEntry> entry : ((AnalysisContextImpl) context).cache.entrySet()) {
        Source newSource = entry.getKey();
        SourceEntry existingEntry = getReadableSourceEntry(newSource);
        if (existingEntry == null) {
          // TODO(brianwilkerson) Decide whether we really need to copy the info.
          cache.put(newSource, entry.getValue().getWritableCopy());
        } else {
          // TODO(brianwilkerson) Decide whether/how to merge the entries.
        }
      }
    }
  }

  @Override
  public CompilationUnit parseCompilationUnit(Source source) throws AnalysisException {
    return getMadlParseData(source, MadlEntry.PARSED_UNIT, null);
  }

  @Override
  public HtmlUnit parseHtmlUnit(Source source) throws AnalysisException {
    return getHtmlParseData(source, HtmlEntry.PARSED_UNIT, null);
  }

  @Override
  public AnalysisResult performAnalysisTask() {
    long getStart = System.currentTimeMillis();
    AnalysisTask task = getNextTaskAnalysisTask();
    long getEnd = System.currentTimeMillis();
    if (task == null) {
      return new AnalysisResult(getChangeNotices(true), getEnd - getStart, null, -1L);
    }
    //System.out.println(task);
    long performStart = System.currentTimeMillis();
    try {
      task.perform(resultRecorder);
    } catch (AnalysisException exception) {
      if (!(exception.getCause() instanceof IOException)) {
        AnalysisEngine.getInstance().getLogger().logError(
            "Internal error while performing the task: " + task,
            exception);
      }
    }
    long performEnd = System.currentTimeMillis();
    return new AnalysisResult(
        getChangeNotices(false),
        getEnd - getStart,
        task.getClass().getName(),
        performEnd - performStart);
  }

  @Override
  public void recordLibraryElements(Map<Source, LibraryElement> elementMap) {
    synchronized (cacheLock) {
      Source htmlSource = sourceFactory.forUri(MadlSdk.MADL_HTML);
      for (Map.Entry<Source, LibraryElement> entry : elementMap.entrySet()) {
        Source librarySource = entry.getKey();
        LibraryElement library = entry.getValue();
        //
        // Cache the element in the library's info.
        //
        MadlEntry madlEntry = getReadableMadlEntry(librarySource);
        if (madlEntry != null) {
          MadlEntryImpl madlCopy = madlEntry.getWritableCopy();
          recordElementData(madlCopy, library, htmlSource);
          cache.put(librarySource, madlCopy);
        }
      }
    }
  }

  @Override
  public CompilationUnit resolveCompilationUnit(Source unitSource, LibraryElement library)
      throws AnalysisException {
    if (library == null) {
      return null;
    }
    return resolveCompilationUnit(unitSource, library.getSource());
  }

  @Override
  public CompilationUnit resolveCompilationUnit(Source unitSource, Source librarySource)
      throws AnalysisException {
    return getMadlResolutionData(unitSource, librarySource, MadlEntry.RESOLVED_UNIT, null);
  }

  @Override
  public HtmlUnit resolveHtmlUnit(Source htmlSource) throws AnalysisException {
    computeHtmlElement(htmlSource);
    return parseHtmlUnit(htmlSource);
  }

  @Override
  public void setAnalysisOptions(AnalysisOptions options) {
    synchronized (cacheLock) {
      boolean needsRecompute = this.options.getAnalyzeFunctionBodies() != options.getAnalyzeFunctionBodies()
          || this.options.getMadl2jsHint() != options.getMadl2jsHint()
          || (this.options.getHint() && !options.getHint())
          || this.options.getPreserveComments() != options.getPreserveComments();

      int cacheSize = options.getCacheSize();
      if (this.options.getCacheSize() != cacheSize) {
        this.options.setCacheSize(cacheSize);
        cache.setMaxCacheSize(cacheSize);
        //
        // Cap the size of the priority list to being less than the cache size. Failure to do so can
        // result in an infinite loop in performAnalysisTask() because re-caching one AST structure
        // can cause another priority source's AST structure to be flushed.
        //
        int maxPriorityOrderSize = cacheSize - PRIORITY_ORDER_SIZE_DELTA;
        if (priorityOrder.length > maxPriorityOrderSize) {
          Source[] newPriorityOrder = new Source[maxPriorityOrderSize];
          System.arraycopy(priorityOrder, 0, newPriorityOrder, 0, maxPriorityOrderSize);
          priorityOrder = newPriorityOrder;
        }
      }
      this.options.setAnalyzeFunctionBodies(options.getAnalyzeFunctionBodies());
      this.options.setMadl2jsHint(options.getMadl2jsHint());
      this.options.setHint(options.getHint());
      this.options.setIncremental(options.getIncremental());
      this.options.setPreserveComments(options.getPreserveComments());

      if (needsRecompute) {
        invalidateAllResolutionInformation();
      }
    }
  }

  @Override
  public void setAnalysisPriorityOrder(List<Source> sources) {
    synchronized (cacheLock) {
      if (sources == null || sources.isEmpty()) {
        priorityOrder = Source.EMPTY_ARRAY;
      } else {
        while (sources.remove(null)) {
          // Nothing else to do.
        }
        if (sources.isEmpty()) {
          priorityOrder = Source.EMPTY_ARRAY;
        }
        //
        // Cap the size of the priority list to being less than the cache size. Failure to do so can
        // result in an infinite loop in performAnalysisTask() because re-caching one AST structure
        // can cause another priority source's AST structure to be flushed.
        //
        int count = Math.min(sources.size(), options.getCacheSize() - PRIORITY_ORDER_SIZE_DELTA);
        priorityOrder = new Source[count];
        for (int i = 0; i < count; i++) {
          priorityOrder[i] = sources.get(i);
        }
      }
    }
  }

  @Override
  public void setChangedContents(Source source, String contents, int offset, int oldLength,
      int newLength) {
    synchronized (cacheLock) {
      String originalContents = sourceFactory.setContents(source, contents);
      if (contents != null) {
        if (!contents.equals(originalContents)) {
          if (options.getIncremental()) {
            incrementalAnalysisCache = IncrementalAnalysisCache.update(
                incrementalAnalysisCache,
                source,
                originalContents,
                contents,
                offset,
                oldLength,
                newLength,
                getReadableSourceEntry(source));
          }
          sourceChanged(source);
        }
      } else if (originalContents != null) {
        incrementalAnalysisCache = IncrementalAnalysisCache.clear(incrementalAnalysisCache, source);
        sourceChanged(source);
      }
    }
  }

  @Override
  public void setContents(Source source, String contents) {
    synchronized (cacheLock) {
      String originalContents = sourceFactory.setContents(source, contents);
      if (contents != null) {
        if (!contents.equals(originalContents)) {
          incrementalAnalysisCache = IncrementalAnalysisCache.clear(
              incrementalAnalysisCache,
              source);
          sourceChanged(source);
        }
      } else if (originalContents != null) {
        incrementalAnalysisCache = IncrementalAnalysisCache.clear(incrementalAnalysisCache, source);
        sourceChanged(source);
      }
    }
  }

  @Override
  public void setSourceFactory(SourceFactory factory) {
    synchronized (cacheLock) {
      if (sourceFactory == factory) {
        return;
      } else if (factory.getContext() != null) {
        throw new IllegalStateException("Source factories cannot be shared between contexts");
      }
      if (sourceFactory != null) {
        sourceFactory.setContext(null);
      }
      factory.setContext(this);
      sourceFactory = factory;
      invalidateAllResolutionInformation();
    }
  }

  @Override
  @Deprecated
  public Iterable<Source> sourcesToResolve(Source[] changedSources) {
    List<Source> librarySources = new ArrayList<Source>();
    for (Source source : changedSources) {
      if (computeKindOf(source) == SourceKind.LIBRARY) {
        librarySources.add(source);
      }
    }
    return librarySources;
  }

  /**
   * Record the results produced by performing a {@link ResolveMadlLibraryTask}. If the results were
   * computed from data that is now out-of-date, then the results will not be recorded.
   *
   * @param task the task that was performed
   * @return an entry containing the computed results
   * @throws AnalysisException if the results could not be recorded
   */
  protected MadlEntry recordResolveMadlLibraryTaskResults(ResolveMadlLibraryTask task)
      throws AnalysisException {
    LibraryResolver resolver = task.getLibraryResolver();
    AnalysisException thrownException = task.getException();
    MadlEntry unitEntry = null;
    Source unitSource = task.getUnitSource();
    if (resolver != null) {
      //
      // The resolver should only be null if an exception was thrown before (or while) it was
      // being created.
      //
      Set<Library> resolvedLibraries = resolver.getResolvedLibraries();
      if (resolvedLibraries == null) {
        //
        // The resolved libraries should only be null if an exception was thrown during resolution.
        //
        unitEntry = getReadableMadlEntry(unitSource);
        if (unitEntry == null) {
          throw new AnalysisException("A Madl file became a non-Madl file: "
              + unitSource.getFullName());
        }
        MadlEntryImpl madlCopy = unitEntry.getWritableCopy();
        madlCopy.recordResolutionError();
        madlCopy.setException(thrownException);
        cache.put(unitSource, madlCopy);
        if (thrownException != null) {
          throw thrownException;
        }
        return madlCopy;
      }
      synchronized (cacheLock) {
        if (allModificationTimesMatch(resolvedLibraries)) {
          Source htmlSource = getSourceFactory().forUri(MadlSdk.MADL_HTML);
          RecordingErrorListener errorListener = resolver.getErrorListener();
          for (Library library : resolvedLibraries) {
            Source librarySource = library.getLibrarySource();
            for (Source source : library.getCompilationUnitSources()) {
              CompilationUnit unit = library.getAST(source);
              AnalysisError[] errors = errorListener.getErrors(source);
              LineInfo lineInfo = getLineInfo(source);
              MadlEntry madlEntry = (MadlEntry) cache.get(source);
              long sourceTime = source.getModificationStamp();
              if (madlEntry.getModificationTime() != sourceTime) {
                // The source has changed without the context being notified. Simulate notification.
                sourceChanged(source);
                madlEntry = getReadableMadlEntry(source);
                if (madlEntry == null) {
                  throw new AnalysisException("A Madl file became a non-Madl file: "
                      + source.getFullName());
                }
              }
              MadlEntryImpl madlCopy = madlEntry.getWritableCopy();
              if (thrownException == null) {
                madlCopy.setValue(SourceEntry.LINE_INFO, lineInfo);
                madlCopy.setState(MadlEntry.PARSED_UNIT, CacheState.FLUSHED);
                madlCopy.setValue(MadlEntry.RESOLVED_UNIT, librarySource, unit);
                madlCopy.setValue(MadlEntry.RESOLUTION_ERRORS, librarySource, errors);
                if (source == librarySource) {
                  recordElementData(madlCopy, library.getLibraryElement(), htmlSource);
                }
              } else {
                madlCopy.recordResolutionError();
              }
              madlCopy.setException(thrownException);
              cache.put(source, madlCopy);
              if (source.equals(unitSource)) {
                unitEntry = madlCopy;
              }

              ChangeNoticeImpl notice = getNotice(source);
              notice.setCompilationUnit(unit);
              notice.setErrors(madlCopy.getAllErrors(), lineInfo);
            }
          }
        } else {
          for (Library library : resolvedLibraries) {
            for (Source source : library.getCompilationUnitSources()) {
              MadlEntry madlEntry = getReadableMadlEntry(source);
              if (madlEntry != null) {
                long resultTime = library.getModificationTime(source);
                MadlEntryImpl madlCopy = madlEntry.getWritableCopy();
                if (thrownException == null || resultTime >= 0L) {
                  //
                  // The analysis was performed on out-of-date sources. Mark the cache so that the
                  // sources will be re-analyzed using the up-to-date sources.
                  //
                  madlCopy.recordResolutionNotInProcess();
                } else {
                  //
                  // We could not determine whether the sources were up-to-date or out-of-date. Mark
                  // the cache so that we won't attempt to re-analyze the sources until there's a
                  // good chance that we'll be able to do so without error.
                  //
                  madlCopy.recordResolutionError();
                }
                madlCopy.setException(thrownException);
                cache.put(source, madlCopy);
                if (source.equals(unitSource)) {
                  unitEntry = madlCopy;
                }
              }
            }
          }
        }
      }
    }
    if (thrownException != null) {
      throw thrownException;
    }
    if (unitEntry == null) {
      unitEntry = getReadableMadlEntry(unitSource);
      if (unitEntry == null) {
        throw new AnalysisException("A Madl file became a non-Madl file: "
            + unitSource.getFullName());
      }
    }
    return unitEntry;
  }

  /**
   * Add all of the sources contained in the given source container to the given list of sources.
   * <p>
   * Note: This method must only be invoked while we are synchronized on {@link #cacheLock}.
   *
   * @param sources the list to which sources are to be added
   * @param container the source container containing the sources to be added to the list
   */
  private void addSourcesInContainer(ArrayList<Source> sources, SourceContainer container) {
    for (Map.Entry<Source, SourceEntry> entry : cache.entrySet()) {
      Source source = entry.getKey();
      if (container.contains(source)) {
        sources.add(source);
      }
    }
  }

  /**
   * Return {@code true} if the modification times of the sources used by the given library resolver
   * to resolve one or more libraries are consistent with the modification times in the cache.
   *
   * @param resolver the library resolver used to resolve one or more libraries
   * @return {@code true} if we should record the results of the resolution
   * @throws AnalysisException if any of the modification times could not be determined (this should
   *           not happen)
   */
  private boolean allModificationTimesMatch(Set<Library> resolvedLibraries)
      throws AnalysisException {
    boolean allTimesMatch = true;
    for (Library library : resolvedLibraries) {
      for (Source source : library.getCompilationUnitSources()) {
        MadlEntry madlEntry = getReadableMadlEntry(source);
        if (madlEntry == null) {
          // This shouldn't be possible because we should never have performed the task if the
          // source didn't represent a Madl file, but check to be safe.
          throw new AnalysisException(
              "Internal error: attempting to reolve non-Madl file as a Madl file: "
                  + source.getFullName());
        }
        long sourceTime = source.getModificationStamp();
        long resultTime = library.getModificationTime(source);
        if (sourceTime != resultTime) {
          // The source has changed without the context being notified. Simulate notification.
          sourceChanged(source);
          allTimesMatch = false;
        }
      }
    }
    return allTimesMatch;
  }

  /**
   * Given a source for an HTML file, return a cache entry in which all of the data represented by
   * the given descriptors is available. This method assumes that the data can be produced by
   * parsing the source if it is not already cached.
   *
   * @param source the source representing the HTML file
   * @param htmlEntry the cache entry associated with the HTML file
   * @param descriptor the descriptor representing the data to be returned
   * @return a cache entry containing the required data
   * @throws AnalysisException if data could not be returned because the source could not be
   *           resolved
   */
  private HtmlEntry cacheHtmlParseData(Source source, HtmlEntry htmlEntry,
      DataDescriptor<?> descriptor) throws AnalysisException {
    //
    // Check to see whether we already have the information being requested.
    //
    CacheState state = htmlEntry.getState(descriptor);
    while (state != CacheState.ERROR && state != CacheState.VALID) {
      //
      // If not, compute the information. Unless the modification date of the source continues to
      // change, this loop will eventually terminate.
      //
      htmlEntry = (HtmlEntry) new ParseHtmlTask(this, source).perform(resultRecorder);
      state = htmlEntry.getState(descriptor);
    }
    return htmlEntry;
  }

  /**
   * Given a source for an HTML file, return a cache entry in which the the data represented by the
   * given descriptor is available. This method assumes that the data can be produced by resolving
   * the source if it is not already cached.
   *
   * @param source the source representing the HTML file
   * @param madlEntry the cache entry associated with the HTML file
   * @param descriptor the descriptor representing the data to be returned
   * @return a cache entry containing the required data
   * @throws AnalysisException if data could not be returned because the source could not be
   *           resolved
   */
  private HtmlEntry cacheHtmlResolutionData(Source source, HtmlEntry htmlEntry,
      DataDescriptor<?> descriptor) throws AnalysisException {
    //
    // Check to see whether we already have the information being requested.
    //
    CacheState state = htmlEntry.getState(descriptor);
    while (state != CacheState.ERROR && state != CacheState.VALID) {
      //
      // If not, compute the information. Unless the modification date of the source continues to
      // change, this loop will eventually terminate.
      //
      htmlEntry = (HtmlEntry) new ResolveHtmlTask(this, source).perform(resultRecorder);
      state = htmlEntry.getState(descriptor);
    }
    return htmlEntry;
  }

  /**
   * Given a source for a Madl file and the library that contains it, return a cache entry in which
   * the data represented by the given descriptor is available. This method assumes that the data
   * can be produced by generating hints for the library if the data is not already cached.
   *
   * @param unitSource the source representing the Madl file
   * @param librarySource the source representing the library containing the Madl file
   * @param madlEntry the cache entry associated with the Madl file
   * @param descriptor the descriptor representing the data to be returned
   * @return a cache entry containing the required data
   * @throws AnalysisException if data could not be returned because the source could not be parsed
   */
  private MadlEntry cacheMadlHintData(Source unitSource, Source librarySource, MadlEntry madlEntry,
      DataDescriptor<?> descriptor) throws AnalysisException {
    //
    // Check to see whether we already have the information being requested.
    //
    CacheState state = madlEntry.getState(descriptor, librarySource);
    while (state != CacheState.ERROR && state != CacheState.VALID) {
      //
      // If not, compute the information. Unless the modification date of the source continues to
      // change, this loop will eventually terminate.
      //
      madlEntry = (MadlEntry) new GenerateMadlHintsTask(this, getLibraryElement(librarySource)).perform(resultRecorder);
      state = madlEntry.getState(descriptor, librarySource);
    }
    return madlEntry;
  }

  /**
   * Given a source for a Madl file, return a cache entry in which the data represented by the given
   * descriptor is available. This method assumes that the data can be produced by parsing the
   * source if it is not already cached.
   *
   * @param source the source representing the Madl file
   * @param madlEntry the cache entry associated with the Madl file
   * @param descriptor the descriptor representing the data to be returned
   * @return a cache entry containing the required data
   * @throws AnalysisException if data could not be returned because the source could not be
   *           resolved
   */
  private MadlEntry cacheMadlParseData(Source source, MadlEntry madlEntry,
      DataDescriptor<?> descriptor) throws AnalysisException {
    if (descriptor == MadlEntry.PARSED_UNIT) {
      CompilationUnit unit = madlEntry.getAnyParsedCompilationUnit();
      if (unit != null) {
        return madlEntry;
      }
    }
    //
    // Check to see whether we already have the information being requested.
    //
    CacheState state = madlEntry.getState(descriptor);
    while (state != CacheState.ERROR && state != CacheState.VALID) {
      //
      // If not, compute the information. Unless the modification date of the source continues to
      // change, this loop will eventually terminate.
      //
      madlEntry = (MadlEntry) new ParseMadlTask(this, source).perform(resultRecorder);
      state = madlEntry.getState(descriptor);
    }
    return madlEntry;
  }

  /**
   * Given a source for a Madl file and the library that contains it, return a cache entry in which
   * the data represented by the given descriptor is available. This method assumes that the data
   * can be produced by resolving the source in the context of the library if it is not already
   * cached.
   *
   * @param unitSource the source representing the Madl file
   * @param librarySource the source representing the library containing the Madl file
   * @param madlEntry the cache entry associated with the Madl file
   * @param descriptor the descriptor representing the data to be returned
   * @return a cache entry containing the required data
   * @throws AnalysisException if data could not be returned because the source could not be parsed
   */
  private MadlEntry cacheMadlResolutionData(Source unitSource, Source librarySource,
      MadlEntry madlEntry, DataDescriptor<?> descriptor) throws AnalysisException {
    //
    // Check to see whether we already have the information being requested.
    //
    CacheState state = (descriptor == MadlEntry.ELEMENT) ? madlEntry.getState(descriptor)
        : madlEntry.getState(descriptor, librarySource);
    while (state != CacheState.ERROR && state != CacheState.VALID) {
      //
      // If not, compute the information. Unless the modification date of the source continues to
      // change, this loop will eventually terminate.
      //
      // TODO(brianwilkerson) As an optimization, if we already have the element model for the
      // library we can use ResolveMadlUnitTask to produce the resolved AST structure much faster.
      madlEntry = (MadlEntry) new ResolveMadlLibraryTask(this, unitSource, librarySource).perform(resultRecorder);
      state = (descriptor == MadlEntry.ELEMENT) ? madlEntry.getState(descriptor)
          : madlEntry.getState(descriptor, librarySource);
    }
    return madlEntry;
  }

  /**
   * Given a source for a Madl file and the library that contains it, return a cache entry in which
   * the data represented by the given descriptor is available. This method assumes that the data
   * can be produced by verifying the source in the given library if the data is not already cached.
   *
   * @param unitSource the source representing the Madl file
   * @param librarySource the source representing the library containing the Madl file
   * @param madlEntry the cache entry associated with the Madl file
   * @param descriptor the descriptor representing the data to be returned
   * @return a cache entry containing the required data
   * @throws AnalysisException if data could not be returned because the source could not be parsed
   */
  private MadlEntry cacheMadlVerificationData(Source unitSource, Source librarySource,
      MadlEntry madlEntry, DataDescriptor<?> descriptor) throws AnalysisException {
    //
    // Check to see whether we already have the information being requested.
    //
    CacheState state = madlEntry.getState(descriptor, librarySource);
    while (state != CacheState.ERROR && state != CacheState.VALID) {
      //
      // If not, compute the information. Unless the modification date of the source continues to
      // change, this loop will eventually terminate.
      //
      madlEntry = (MadlEntry) new GenerateMadlErrorsTask(
          this,
          unitSource,
          getLibraryElement(librarySource)).perform(resultRecorder);
      state = madlEntry.getState(descriptor, librarySource);
    }
    return madlEntry;
  }

  /**
   * Given the encoded form of a source, use the source factory to reconstitute the original source.
   *
   * @param encoding the encoded form of a source
   * @return the source represented by the encoding
   */
  private Source computeSourceFromEncoding(String encoding) {
    synchronized (cacheLock) {
      return sourceFactory.fromEncoding(encoding);
    }
  }

  /**
   * Return {@code true} if the given array of sources contains the given source.
   *
   * @param sources the sources being searched
   * @param targetSource the source being searched for
   * @return {@code true} if the given source is in the array
   */
  private boolean contains(Source[] sources, Source targetSource) {
    for (Source source : sources) {
      if (source.equals(targetSource)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Return {@code true} if the given array of sources contains any of the given target sources.
   *
   * @param sources the sources being searched
   * @param targetSources the sources being searched for
   * @return {@code true} if any of the given target sources are in the array
   */
  private boolean containsAny(Source[] sources, Source[] targetSources) {
    for (Source targetSource : targetSources) {
      if (contains(sources, targetSource)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Create a source information object suitable for the given source. Return the source information
   * object that was created, or {@code null} if the source should not be tracked by this context.
   *
   * @param source the source for which an information object is being created
   * @return the source information object that was created
   */
  private SourceEntry createSourceEntry(Source source) {
    String name = source.getShortName();
    if (AnalysisEngine.isHtmlFileName(name)) {
      HtmlEntryImpl htmlEntry = new HtmlEntryImpl();
      htmlEntry.setModificationTime(source.getModificationStamp());
      cache.put(source, htmlEntry);
      return htmlEntry;
    } else {
      MadlEntryImpl madlEntry = new MadlEntryImpl();
      madlEntry.setModificationTime(source.getModificationStamp());
      cache.put(source, madlEntry);
      return madlEntry;
    }
  }

  /**
   * Return an array containing all of the change notices that are waiting to be returned. If there
   * are no notices, then return either {@code null} or an empty array, depending on the value of
   * the argument.
   *
   * @param nullIfEmpty {@code true} if {@code null} should be returned when there are no notices
   * @return the change notices that are waiting to be returned
   */
  private ChangeNotice[] getChangeNotices(boolean nullIfEmpty) {
    synchronized (cacheLock) {
      if (pendingNotices.isEmpty()) {
        if (nullIfEmpty) {
          return null;
        }
        return ChangeNoticeImpl.EMPTY_ARRAY;
      }
      ChangeNotice[] notices = pendingNotices.values().toArray(
          new ChangeNotice[pendingNotices.size()]);
      pendingNotices.clear();
      return notices;
    }
  }

  /**
   * Given a source for an HTML file, return the data represented by the given descriptor that is
   * associated with that source, or the given default value if the source is not an HTML file. This
   * method assumes that the data can be produced by parsing the source if it is not already cached.
   *
   * @param source the source representing the Madl file
   * @param descriptor the descriptor representing the data to be returned
   * @param defaultValue the value to be returned if the source is not an HTML file
   * @return the requested data about the given source
   * @throws AnalysisException if data could not be returned because the source could not be parsed
   */
  private <E> E getHtmlParseData(Source source, DataDescriptor<E> descriptor, E defaultValue)
      throws AnalysisException {
    HtmlEntry htmlEntry = getReadableHtmlEntry(source);
    if (htmlEntry == null) {
      return defaultValue;
    }
    htmlEntry = cacheHtmlParseData(source, htmlEntry, descriptor);
    return htmlEntry.getValue(descriptor);
  }

  /**
   * Given a source for an HTML file, return the data represented by the given descriptor that is
   * associated with that source, or the given default value if the source is not an HTML file. This
   * method assumes that the data can be produced by resolving the source if it is not already
   * cached.
   *
   * @param source the source representing the HTML file
   * @param descriptor the descriptor representing the data to be returned
   * @param defaultValue the value to be returned if the source is not an HTML file
   * @return the requested data about the given source
   * @throws AnalysisException if data could not be returned because the source could not be
   *           resolved
   */
  private <E> E getHtmlResolutionData(Source source, DataDescriptor<E> descriptor, E defaultValue)
      throws AnalysisException {
    HtmlEntry htmlEntry = getReadableHtmlEntry(source);
    if (htmlEntry == null) {
      return defaultValue;
    }
    return getHtmlResolutionData(source, htmlEntry, descriptor);
  }

  /**
   * Given a source for an HTML file, return the data represented by the given descriptor that is
   * associated with that source. This method assumes that the data can be produced by resolving the
   * source if it is not already cached.
   *
   * @param source the source representing the HTML file
   * @param htmlEntry the entry representing the HTML file
   * @param descriptor the descriptor representing the data to be returned
   * @return the requested data about the given source
   * @throws AnalysisException if data could not be returned because the source could not be
   *           resolved
   */
  private <E> E getHtmlResolutionData(Source source, HtmlEntry htmlEntry,
      DataDescriptor<E> descriptor) throws AnalysisException {
    htmlEntry = cacheHtmlResolutionData(source, htmlEntry, descriptor);
    return htmlEntry.getValue(descriptor);
  }

  /**
   * Given a source for a Madl file and the library that contains it, return the data represented by
   * the given descriptor that is associated with that source. This method assumes that the data can
   * be produced by generating hints for the library if it is not already cached.
   *
   * @param unitSource the source representing the Madl file
   * @param librarySource the source representing the library containing the Madl file
   * @param madlEntry the entry representing the Madl file
   * @param descriptor the descriptor representing the data to be returned
   * @return the requested data about the given source
   * @throws AnalysisException if data could not be returned because the source could not be
   *           resolved
   */
  private <E> E getMadlHintData(Source unitSource, Source librarySource, MadlEntry madlEntry,
      DataDescriptor<E> descriptor) throws AnalysisException {
    madlEntry = cacheMadlHintData(unitSource, librarySource, madlEntry, descriptor);
    if (descriptor == MadlEntry.ELEMENT) {
      return madlEntry.getValue(descriptor);
    }
    return madlEntry.getValue(descriptor, librarySource);
  }

  /**
   * Given a source for a Madl file, return the data represented by the given descriptor that is
   * associated with that source, or the given default value if the source is not a Madl file. This
   * method assumes that the data can be produced by parsing the source if it is not already cached.
   *
   * @param source the source representing the Madl file
   * @param descriptor the descriptor representing the data to be returned
   * @param defaultValue the value to be returned if the source is not a Madl file
   * @return the requested data about the given source
   * @throws AnalysisException if data could not be returned because the source could not be parsed
   */
  private <E> E getMadlParseData(Source source, DataDescriptor<E> descriptor, E defaultValue)
      throws AnalysisException {
    MadlEntry madlEntry = getReadableMadlEntry(source);
    if (madlEntry == null) {
      return defaultValue;
    }
    return getMadlParseData(source, madlEntry, descriptor);
  }

  /**
   * Given a source for a Madl file, return the data represented by the given descriptor that is
   * associated with that source. This method assumes that the data can be produced by parsing the
   * source if it is not already cached.
   *
   * @param source the source representing the Madl file
   * @param madlEntry the cache entry associated with the Madl file
   * @param descriptor the descriptor representing the data to be returned
   * @return the requested data about the given source
   * @throws AnalysisException if data could not be returned because the source could not be parsed
   */
  @SuppressWarnings("unchecked")
  private <E> E getMadlParseData(Source source, MadlEntry madlEntry, DataDescriptor<E> descriptor)
      throws AnalysisException {
    madlEntry = cacheMadlParseData(source, madlEntry, descriptor);
    if (descriptor == MadlEntry.PARSED_UNIT) {
      return (E) madlEntry.getAnyParsedCompilationUnit();
    }
    return madlEntry.getValue(descriptor);
  }

  /**
   * Given a source for a Madl file and the library that contains it, return the data represented by
   * the given descriptor that is associated with that source, or the given default value if the
   * source is not a Madl file. This method assumes that the data can be produced by resolving the
   * source in the context of the library if it is not already cached.
   *
   * @param unitSource the source representing the Madl file
   * @param librarySource the source representing the library containing the Madl file
   * @param descriptor the descriptor representing the data to be returned
   * @param defaultValue the value to be returned if the source is not a Madl file
   * @return the requested data about the given source
   * @throws AnalysisException if data could not be returned because the source could not be
   *           resolved
   */
  private <E> E getMadlResolutionData(Source unitSource, Source librarySource,
      DataDescriptor<E> descriptor, E defaultValue) throws AnalysisException {
    MadlEntry madlEntry = getReadableMadlEntry(unitSource);
    if (madlEntry == null) {
      return defaultValue;
    }
    return getMadlResolutionData(unitSource, librarySource, madlEntry, descriptor);
  }

  /**
   * Given a source for a Madl file and the library that contains it, return the data represented by
   * the given descriptor that is associated with that source. This method assumes that the data can
   * be produced by resolving the source in the context of the library if it is not already cached.
   *
   * @param unitSource the source representing the Madl file
   * @param librarySource the source representing the library containing the Madl file
   * @param madlEntry the entry representing the Madl file
   * @param descriptor the descriptor representing the data to be returned
   * @return the requested data about the given source
   * @throws AnalysisException if data could not be returned because the source could not be
   *           resolved
   */
  private <E> E getMadlResolutionData(Source unitSource, Source librarySource, MadlEntry madlEntry,
      DataDescriptor<E> descriptor) throws AnalysisException {
    madlEntry = cacheMadlResolutionData(unitSource, librarySource, madlEntry, descriptor);
    if (descriptor == MadlEntry.ELEMENT) {
      return madlEntry.getValue(descriptor);
    }
    return madlEntry.getValue(descriptor, librarySource);
  }

  /**
   * Given a source for a Madl file and the library that contains it, return the data represented by
   * the given descriptor that is associated with that source. This method assumes that the data can
   * be produced by verifying the source within the given library if it is not already cached.
   *
   * @param unitSource the source representing the Madl file
   * @param librarySource the source representing the library containing the Madl file
   * @param madlEntry the entry representing the Madl file
   * @param descriptor the descriptor representing the data to be returned
   * @return the requested data about the given source
   * @throws AnalysisException if data could not be returned because the source could not be
   *           resolved
   */
  private <E> E getMadlVerificationData(Source unitSource, Source librarySource,
      MadlEntry madlEntry, DataDescriptor<E> descriptor) throws AnalysisException {
    madlEntry = cacheMadlVerificationData(unitSource, librarySource, madlEntry, descriptor);
    return madlEntry.getValue(descriptor, librarySource);
  }

  /**
   * Look through the cache for a task that needs to be performed. Return the task that was found,
   * or {@code null} if there is no more work to be done.
   *
   * @return the next task that needs to be performed
   */
  private AnalysisTask getNextTaskAnalysisTask() {
    synchronized (cacheLock) {
      boolean hintsEnabled = options.getHint();
      //
      // Look for incremental analysis
      //
      if (incrementalAnalysisCache != null && incrementalAnalysisCache.hasWork()) {
        AnalysisTask task = new IncrementalAnalysisTask(this, incrementalAnalysisCache);
        incrementalAnalysisCache = null;
        return task;
      }
      //
      // Look for a priority source that needs to be analyzed.
      //
      for (Source source : priorityOrder) {
        AnalysisTask task = getNextTaskAnalysisTask(source, cache.get(source), true, hintsEnabled);
        if (task != null) {
          return task;
        }
      }
      //
      // Look for a non-priority source that needs to be analyzed.
      //
      for (Map.Entry<Source, SourceEntry> entry : cache.entrySet()) {
        AnalysisTask task = getNextTaskAnalysisTask(
            entry.getKey(),
            entry.getValue(),
            false,
            hintsEnabled);
        if (task != null) {
          return task;
        }
      }
      return null;
    }
  }

  /**
   * Look at the given source to see whether a task needs to be performed related to it. Return the
   * task that should be performed, or {@code null} if there is no more work to be done for the
   * source.
   * <p>
   * <b>Note:</b> This method must only be invoked while we are synchronized on {@link #cacheLock}.
   *
   * @param source the source to be checked
   * @param sourceEntry the cache entry associated with the source
   * @param isPriority {@code true} if the source is a priority source
   * @param hintsEnabled {@code true} if hints are currently enabled
   * @return the next task that needs to be performed for the given source
   */
  private AnalysisTask getNextTaskAnalysisTask(Source source, SourceEntry sourceEntry,
      boolean isPriority, boolean hintsEnabled) {
    if (sourceEntry instanceof MadlEntry) {
      MadlEntry madlEntry = (MadlEntry) sourceEntry;
      if (!source.exists()) {
        MadlEntryImpl madlCopy = madlEntry.getWritableCopy();
        madlCopy.recordParseError();
        madlCopy.setException(new AnalysisException("Source does not exist"));
        cache.put(source, madlCopy);
        return null;
      }
      CacheState parseErrorsState = madlEntry.getState(MadlEntry.PARSE_ERRORS);
      if (parseErrorsState == CacheState.INVALID
          || (isPriority && parseErrorsState == CacheState.FLUSHED)) {
        MadlEntryImpl madlCopy = madlEntry.getWritableCopy();
        madlCopy.setState(MadlEntry.PARSE_ERRORS, CacheState.IN_PROCESS);
        cache.put(source, madlCopy);
        return new ParseMadlTask(this, source);
      }
      if (isPriority && parseErrorsState != CacheState.ERROR) {
        CompilationUnit parseUnit = madlEntry.getAnyParsedCompilationUnit();
        if (parseUnit == null) {
          MadlEntryImpl madlCopy = madlEntry.getWritableCopy();
          madlCopy.setState(MadlEntry.PARSED_UNIT, CacheState.IN_PROCESS);
          cache.put(source, madlCopy);
          return new ParseMadlTask(this, source);
        }
      }
      for (Source librarySource : getLibrariesContaining(source)) {
        SourceEntry libraryEntry = cache.get(librarySource);
        if (libraryEntry instanceof MadlEntry) {
          CacheState elementState = libraryEntry.getState(MadlEntry.ELEMENT);
          if (elementState == CacheState.INVALID
              || (isPriority && elementState == CacheState.FLUSHED)) {
            MadlEntryImpl libraryCopy = ((MadlEntry) libraryEntry).getWritableCopy();
            libraryCopy.setState(MadlEntry.ELEMENT, CacheState.IN_PROCESS);
            cache.put(librarySource, libraryCopy);
            return new ResolveMadlLibraryTask(this, source, librarySource);
          }
          CacheState resolvedUnitState = madlEntry.getState(MadlEntry.RESOLVED_UNIT, librarySource);
          if (resolvedUnitState == CacheState.INVALID
              || (isPriority && resolvedUnitState == CacheState.FLUSHED)) {
            //
            // The commented out lines below are an optimization that doesn't quite work yet. The
            // problem is that if the source was not resolved because it wasn't part of any library,
            // then there won't be any elements in the element model that we can use to resolve it.
            //
            //LibraryElement libraryElement = libraryEntry.getValue(MadlEntry.ELEMENT);
            //if (libraryElement != null) {
            MadlEntryImpl madlCopy = madlEntry.getWritableCopy();
            madlCopy.setState(MadlEntry.RESOLVED_UNIT, librarySource, CacheState.IN_PROCESS);
            cache.put(source, madlCopy);
            //return new ResolveMadlUnitTask(this, source, libraryElement);
            return new ResolveMadlLibraryTask(this, source, librarySource);
            //}
          }
          CacheState verificationErrorsState = madlEntry.getState(
              MadlEntry.VERIFICATION_ERRORS,
              librarySource);
          if (verificationErrorsState == CacheState.INVALID
              || (isPriority && verificationErrorsState == CacheState.FLUSHED)) {
            LibraryElement libraryElement = libraryEntry.getValue(MadlEntry.ELEMENT);
            if (libraryElement != null) {
              MadlEntryImpl madlCopy = madlEntry.getWritableCopy();
              madlCopy.setState(MadlEntry.VERIFICATION_ERRORS, librarySource, CacheState.IN_PROCESS);
              cache.put(source, madlCopy);
              return new GenerateMadlErrorsTask(this, source, libraryElement);
            }
          }
          if (hintsEnabled) {
            CacheState hintsState = madlEntry.getState(MadlEntry.HINTS, librarySource);
            if (hintsState == CacheState.INVALID
                || (isPriority && hintsState == CacheState.FLUSHED)) {
              LibraryElement libraryElement = libraryEntry.getValue(MadlEntry.ELEMENT);
              if (libraryElement != null) {
                MadlEntryImpl madlCopy = madlEntry.getWritableCopy();
                madlCopy.setState(MadlEntry.HINTS, librarySource, CacheState.IN_PROCESS);
                cache.put(source, madlCopy);
                return new GenerateMadlHintsTask(this, libraryElement);
              }
            }
          }
        }
      }
    } else if (sourceEntry instanceof HtmlEntry) {
      HtmlEntry htmlEntry = (HtmlEntry) sourceEntry;
      if (!source.exists()) {
        HtmlEntryImpl htmlCopy = htmlEntry.getWritableCopy();
        htmlCopy.recordParseError();
        htmlCopy.setException(new AnalysisException("Source does not exist"));
        cache.put(source, htmlCopy);
        return null;
      }
      CacheState parsedUnitState = htmlEntry.getState(HtmlEntry.PARSED_UNIT);
      if (parsedUnitState == CacheState.INVALID
          || (isPriority && parsedUnitState == CacheState.FLUSHED)) {
        HtmlEntryImpl htmlCopy = htmlEntry.getWritableCopy();
        htmlCopy.setState(HtmlEntry.PARSED_UNIT, CacheState.IN_PROCESS);
        cache.put(source, htmlCopy);
        return new ParseHtmlTask(this, source);
      }
      CacheState elementState = htmlEntry.getState(HtmlEntry.ELEMENT);
      if (elementState == CacheState.INVALID || (isPriority && elementState == CacheState.FLUSHED)) {
        HtmlEntryImpl htmlCopy = htmlEntry.getWritableCopy();
        htmlCopy.setState(HtmlEntry.ELEMENT, CacheState.IN_PROCESS);
        cache.put(source, htmlCopy);
        return new ResolveHtmlTask(this, source);
      }
    }
    return null;
  }

  /**
   * Return a change notice for the given source, creating one if one does not already exist.
   *
   * @param source the source for which changes are being reported
   * @return a change notice for the given source
   */
  private ChangeNoticeImpl getNotice(Source source) {
    ChangeNoticeImpl notice = pendingNotices.get(source);
    if (notice == null) {
      notice = new ChangeNoticeImpl(source);
      pendingNotices.put(source, notice);
    }
    return notice;
  }

  /**
   * Return the cache entry associated with the given source, or {@code null} if the source is not
   * an HTML file.
   *
   * @param source the source for which a cache entry is being sought
   * @return the source cache entry associated with the given source
   */
  private HtmlEntry getReadableHtmlEntry(Source source) {
    synchronized (cacheLock) {
      SourceEntry sourceEntry = cache.get(source);
      if (sourceEntry == null) {
        sourceEntry = createSourceEntry(source);
      }
      if (sourceEntry instanceof HtmlEntry) {
        cache.accessed(source);
        return (HtmlEntry) sourceEntry;
      }
      return null;
    }
  }

  /**
   * Return the cache entry associated with the given source, or {@code null} if the source is not a
   * Madl file.
   *
   * @param source the source for which a cache entry is being sought
   * @return the source cache entry associated with the given source
   */
  private MadlEntry getReadableMadlEntry(Source source) {
    synchronized (cacheLock) {
      SourceEntry sourceEntry = cache.get(source);
      if (sourceEntry == null) {
        sourceEntry = createSourceEntry(source);
      }
      if (sourceEntry instanceof MadlEntry) {
        cache.accessed(source);
        return (MadlEntry) sourceEntry;
      }
      return null;
    }
  }

  /**
   * Return the cache entry associated with the given source, or {@code null} if there is no entry
   * associated with the source.
   *
   * @param source the source for which a cache entry is being sought
   * @return the source cache entry associated with the given source
   */
  private SourceEntry getReadableSourceEntry(Source source) {
    synchronized (cacheLock) {
      SourceEntry sourceEntry = cache.get(source);
      if (sourceEntry == null) {
        sourceEntry = createSourceEntry(source);
      }
      if (sourceEntry != null) {
        cache.accessed(source);
      }
      return sourceEntry;
    }
  }

  /**
   * Return an array containing all of the sources known to this context that have the given kind.
   *
   * @param kind the kind of sources to be returned
   * @return all of the sources known to this context that have the given kind
   */
  private Source[] getSources(SourceKind kind) {
    ArrayList<Source> sources = new ArrayList<Source>();
    synchronized (cacheLock) {
      for (Map.Entry<Source, SourceEntry> entry : cache.entrySet()) {
        if (entry.getValue().getKind() == kind) {
          sources.add(entry.getKey());
        }
      }
    }
    return sources.toArray(new Source[sources.size()]);
  }

  /**
   * Look at the given source to see whether a task needs to be performed related to it. If so, add
   * the source to the set of sources that need to be processed. This method duplicates, and must
   * therefore be kept in sync with,
   * {@link #getNextTaskAnalysisTask(Source, SourceEntry, boolean, boolean)}. This method is
   * intended to be used for testing purposes only.
   * <p>
   * <b>Note:</b> This method must only be invoked while we are synchronized on {@link #cacheLock}.
   *
   * @param source the source to be checked
   * @param sourceEntry the cache entry associated with the source
   * @param isPriority {@code true} if the source is a priority source
   * @param hintsEnabled {@code true} if hints are currently enabled
   * @param sources the set to which sources should be added
   */
  private void getSourcesNeedingProcessing(Source source, SourceEntry sourceEntry,
      boolean isPriority, boolean hintsEnabled, HashSet<Source> sources) {
    if (sourceEntry instanceof MadlEntry) {
      MadlEntry madlEntry = (MadlEntry) sourceEntry;
      CacheState parseErrorsState = madlEntry.getState(MadlEntry.PARSE_ERRORS);
      if (parseErrorsState == CacheState.INVALID
          || (isPriority && parseErrorsState == CacheState.FLUSHED)) {
        sources.add(source);
        return;
      }
      if (isPriority) {
        CompilationUnit parseUnit = madlEntry.getAnyParsedCompilationUnit();
        if (parseUnit == null) {
          sources.add(source);
          return;
        }
      }
      for (Source librarySource : getLibrariesContaining(source)) {
        SourceEntry libraryEntry = cache.get(librarySource);
        if (libraryEntry instanceof MadlEntry) {
          CacheState elementState = libraryEntry.getState(MadlEntry.ELEMENT);
          if (elementState == CacheState.INVALID
              || (isPriority && elementState == CacheState.FLUSHED)) {
            sources.add(source);
            return;
          }
          CacheState resolvedUnitState = madlEntry.getState(MadlEntry.RESOLVED_UNIT, librarySource);
          if (resolvedUnitState == CacheState.INVALID
              || (isPriority && resolvedUnitState == CacheState.FLUSHED)) {
            LibraryElement libraryElement = libraryEntry.getValue(MadlEntry.ELEMENT);
            if (libraryElement != null) {
              sources.add(source);
              return;
            }
          }
          CacheState verificationErrorsState = madlEntry.getState(
              MadlEntry.VERIFICATION_ERRORS,
              librarySource);
          if (verificationErrorsState == CacheState.INVALID
              || (isPriority && verificationErrorsState == CacheState.FLUSHED)) {
            LibraryElement libraryElement = libraryEntry.getValue(MadlEntry.ELEMENT);
            if (libraryElement != null) {
              sources.add(source);
              return;
            }
          }
          if (hintsEnabled) {
            CacheState hintsState = madlEntry.getState(MadlEntry.HINTS, librarySource);
            if (hintsState == CacheState.INVALID
                || (isPriority && hintsState == CacheState.FLUSHED)) {
              LibraryElement libraryElement = libraryEntry.getValue(MadlEntry.ELEMENT);
              if (libraryElement != null) {
                sources.add(source);
                return;
              }
            }
          }
        }
      }
    } else if (sourceEntry instanceof HtmlEntry) {
      HtmlEntry htmlEntry = (HtmlEntry) sourceEntry;
      CacheState parsedUnitState = htmlEntry.getState(HtmlEntry.PARSED_UNIT);
      if (parsedUnitState == CacheState.INVALID
          || (isPriority && parsedUnitState == CacheState.FLUSHED)) {
        sources.add(source);
        return;
      }
      CacheState elementState = htmlEntry.getState(HtmlEntry.ELEMENT);
      if (elementState == CacheState.INVALID || (isPriority && elementState == CacheState.FLUSHED)) {
        sources.add(source);
        return;
      }
    }
  }

  /**
   * Invalidate all of the resolution results computed by this context.
   * <p>
   * <b>Note:</b> This method must only be invoked while we are synchronized on {@link #cacheLock}.
   */
  private void invalidateAllResolutionInformation() {
    for (Map.Entry<Source, SourceEntry> mapEntry : cache.entrySet()) {
      SourceEntry sourceEntry = mapEntry.getValue();
      if (sourceEntry instanceof HtmlEntry) {
        HtmlEntryImpl htmlCopy = ((HtmlEntry) sourceEntry).getWritableCopy();
        htmlCopy.invalidateAllResolutionInformation();
        mapEntry.setValue(htmlCopy);
      } else if (sourceEntry instanceof MadlEntry) {
        MadlEntryImpl madlCopy = ((MadlEntry) sourceEntry).getWritableCopy();
        madlCopy.invalidateAllResolutionInformation();
        mapEntry.setValue(madlCopy);
      }
    }
  }

  /**
   * In response to a change to at least one of the compilation units in the given library,
   * invalidate any results that are dependent on the result of resolving that library.
   * <p>
   * <b>Note:</b> This method must only be invoked while we are synchronized on {@link #cacheLock}.
   *
   * @param librarySource the source of the library being invalidated
   */
  private void invalidateLibraryResolution(Source librarySource) {
    // TODO(brianwilkerson) This could be optimized. There's no need to flush all of these caches if
    // the public namespace hasn't changed, which will be a fairly common case. The question is
    // whether we can afford the time to compute the namespace to look for differences.
    MadlEntry libraryEntry = getReadableMadlEntry(librarySource);
    if (libraryEntry != null) {
      Source[] includedParts = libraryEntry.getValue(MadlEntry.INCLUDED_PARTS);
      MadlEntryImpl libraryCopy = libraryEntry.getWritableCopy();
      libraryCopy.invalidateAllResolutionInformation();
      libraryCopy.setState(MadlEntry.INCLUDED_PARTS, CacheState.INVALID);
      cache.put(librarySource, libraryCopy);
      for (Source partSource : includedParts) {
        SourceEntry partEntry = cache.get(partSource);
        if (partEntry instanceof MadlEntry) {
          MadlEntryImpl partCopy = ((MadlEntry) partEntry).getWritableCopy();
          partCopy.invalidateAllResolutionInformation();
          cache.put(partSource, partCopy);
        }
      }
    }
  }

  /**
   * Return {@code true} if this library is, or depends on, madl:html.
   *
   * @param library the library being tested
   * @param visitedLibraries a collection of the libraries that have been visited, used to prevent
   *          infinite recursion
   * @return {@code true} if this library is, or depends on, madl:html
   */
  private boolean isClient(LibraryElement library, Source htmlSource,
      HashSet<LibraryElement> visitedLibraries) {
    if (visitedLibraries.contains(library)) {
      return false;
    }
    if (library.getSource().equals(htmlSource)) {
      return true;
    }
    visitedLibraries.add(library);
    for (LibraryElement imported : library.getImportedLibraries()) {
      if (isClient(imported, htmlSource, visitedLibraries)) {
        return true;
      }
    }
    for (LibraryElement exported : library.getExportedLibraries()) {
      if (isClient(exported, htmlSource, visitedLibraries)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Given a cache entry and a library element, record the library element and other information
   * gleaned from the element in the cache entry.
   *
   * @param madlCopy the cache entry in which data is to be recorded
   * @param library the library element used to record information
   * @param htmlSource the source for the HTML library
   */
  private void recordElementData(MadlEntryImpl madlCopy, LibraryElement library, Source htmlSource) {
    madlCopy.setValue(MadlEntry.ELEMENT, library);
    madlCopy.setValue(MadlEntry.IS_LAUNCHABLE, library.getEntryPoint() != null);
    madlCopy.setValue(
        MadlEntry.IS_CLIENT,
        isClient(library, htmlSource, new HashSet<LibraryElement>()));
    ArrayList<Source> unitSources = new ArrayList<Source>();
    unitSources.add(library.getDefiningCompilationUnit().getSource());
    for (CompilationUnitElement part : library.getParts()) {
      Source partSource = part.getSource();
      unitSources.add(partSource);
    }
    madlCopy.setValue(MadlEntry.INCLUDED_PARTS, unitSources.toArray(new Source[unitSources.size()]));
  }

  /**
   * Record the results produced by performing a {@link GenerateMadlErrorsTask}. If the results were
   * computed from data that is now out-of-date, then the results will not be recorded.
   *
   * @param task the task that was performed
   * @return an entry containing the computed results
   * @throws AnalysisException if the results could not be recorded
   */
  private MadlEntry recordGenerateMadlErrorsTask(GenerateMadlErrorsTask task)
      throws AnalysisException {
    Source source = task.getSource();
    Source librarySource = task.getLibraryElement().getSource();
    AnalysisException thrownException = task.getException();
    MadlEntry madlEntry = null;
    synchronized (cacheLock) {
      SourceEntry sourceEntry = cache.get(source);
      if (!(sourceEntry instanceof MadlEntry)) {
        // This shouldn't be possible because we should never have performed the task if the source
        // didn't represent a Madl file, but check to be safe.
        throw new AnalysisException(
            "Internal error: attempting to verify non-Madl file as a Madl file: "
                + source.getFullName());
      }
      madlEntry = (MadlEntry) sourceEntry;
      cache.accessed(source);
      long sourceTime = source.getModificationStamp();
      long resultTime = task.getModificationTime();
      if (sourceTime == resultTime) {
        if (madlEntry.getModificationTime() != sourceTime) {
          // The source has changed without the context being notified. Simulate notification.
          sourceChanged(source);
          madlEntry = getReadableMadlEntry(source);
          if (madlEntry == null) {
            throw new AnalysisException("A Madl file became a non-Madl file: "
                + source.getFullName());
          }
        }
        MadlEntryImpl madlCopy = madlEntry.getWritableCopy();
        if (thrownException == null) {
          madlCopy.setValue(MadlEntry.VERIFICATION_ERRORS, librarySource, task.getErrors());
          ChangeNoticeImpl notice = getNotice(source);
          notice.setErrors(madlCopy.getAllErrors(), madlCopy.getValue(SourceEntry.LINE_INFO));
        } else {
          madlCopy.setState(MadlEntry.VERIFICATION_ERRORS, librarySource, CacheState.ERROR);
        }
        madlCopy.setException(thrownException);
        cache.put(source, madlCopy);
        madlEntry = madlCopy;
      } else {
        MadlEntryImpl madlCopy = madlEntry.getWritableCopy();
        if (thrownException == null || resultTime >= 0L) {
          //
          // The analysis was performed on out-of-date sources. Mark the cache so that the source
          // will be re-verified using the up-to-date sources.
          //
          madlCopy.setState(MadlEntry.VERIFICATION_ERRORS, librarySource, CacheState.INVALID);
        } else {
          //
          // We could not determine whether the sources were up-to-date or out-of-date. Mark the
          // cache so that we won't attempt to re-verify the source until there's a good chance
          // that we'll be able to do so without error.
          //
          madlCopy.setState(MadlEntry.VERIFICATION_ERRORS, librarySource, CacheState.ERROR);
        }
        madlCopy.setException(thrownException);
        cache.put(source, madlCopy);
        madlEntry = madlCopy;
      }
    }
    if (thrownException != null) {
      throw thrownException;
    }
    return madlEntry;
  }

  /**
   * Record the results produced by performing a {@link GenerateMadlHintsTask}. If the results were
   * computed from data that is now out-of-date, then the results will not be recorded.
   *
   * @param task the task that was performed
   * @return an entry containing the computed results
   * @throws AnalysisException if the results could not be recorded
   */
  private MadlEntry recordGenerateMadlHintsTask(GenerateMadlHintsTask task)
      throws AnalysisException {
    Source librarySource = task.getLibraryElement().getSource();
    AnalysisException thrownException = task.getException();
    MadlEntry libraryEntry = null;
    HashMap<Source, TimestampedData<AnalysisError[]>> hintMap = task.getHintMap();
    if (hintMap == null) {
      synchronized (cacheLock) {
        // We don't have any information about which sources to mark as invalid other than the library
        // source.
        SourceEntry sourceEntry = cache.get(librarySource);
        if (!(sourceEntry instanceof MadlEntry)) {
          // This shouldn't be possible because we should never have performed the task if the source
          // didn't represent a Madl file, but check to be safe.
          throw new AnalysisException(
              "Internal error: attempting to generate hints for non-Madl file as a Madl file: "
                  + librarySource.getFullName());
        }
        if (thrownException == null) {
          thrownException = new AnalysisException(
              "GenerateMadlHintsTask returned a null hint map without throwing an exception: "
                  + librarySource.getFullName());
        }
        MadlEntryImpl madlCopy = ((MadlEntry) sourceEntry).getWritableCopy();
        madlCopy.setState(MadlEntry.HINTS, librarySource, CacheState.ERROR);
        madlCopy.setException(thrownException);
        cache.put(librarySource, madlCopy);
      }
      throw thrownException;
    }
    for (Map.Entry<Source, TimestampedData<AnalysisError[]>> entry : hintMap.entrySet()) {
      Source unitSource = entry.getKey();
      TimestampedData<AnalysisError[]> results = entry.getValue();
      synchronized (cacheLock) {
        SourceEntry sourceEntry = cache.get(unitSource);
        if (!(sourceEntry instanceof MadlEntry)) {
          // This shouldn't be possible because we should never have performed the task if the source
          // didn't represent a Madl file, but check to be safe.
          throw new AnalysisException(
              "Internal error: attempting to parse non-Madl file as a Madl file: "
                  + unitSource.getFullName());
        }
        MadlEntry madlEntry = (MadlEntry) sourceEntry;
        if (unitSource.equals(librarySource)) {
          libraryEntry = madlEntry;
        }
        cache.accessed(unitSource);
        long sourceTime = unitSource.getModificationStamp();
        long resultTime = results.getModificationTime();
        if (sourceTime == resultTime) {
          if (madlEntry.getModificationTime() != sourceTime) {
            // The source has changed without the context being notified. Simulate notification.
            sourceChanged(unitSource);
            madlEntry = getReadableMadlEntry(unitSource);
            if (madlEntry == null) {
              throw new AnalysisException("A Madl file became a non-Madl file: "
                  + unitSource.getFullName());
            }
          }
          MadlEntryImpl madlCopy = madlEntry.getWritableCopy();
          if (thrownException == null) {
            madlCopy.setValue(MadlEntry.HINTS, librarySource, results.getData());
            ChangeNoticeImpl notice = getNotice(unitSource);
            notice.setErrors(madlCopy.getAllErrors(), madlCopy.getValue(SourceEntry.LINE_INFO));
          } else {
            madlCopy.setState(MadlEntry.HINTS, librarySource, CacheState.ERROR);
          }
          madlCopy.setException(thrownException);
          cache.put(unitSource, madlCopy);
          madlEntry = madlCopy;
        } else {
          if (madlEntry.getState(MadlEntry.HINTS, librarySource) == CacheState.IN_PROCESS) {
            MadlEntryImpl madlCopy = madlEntry.getWritableCopy();
            if (thrownException == null || resultTime >= 0L) {
              //
              // The analysis was performed on out-of-date sources. Mark the cache so that the sources
              // will be re-analyzed using the up-to-date sources.
              //
              madlCopy.setState(MadlEntry.HINTS, librarySource, CacheState.INVALID);
            } else {
              //
              // We could not determine whether the sources were up-to-date or out-of-date. Mark the
              // cache so that we won't attempt to re-analyze the sources until there's a good chance
              // that we'll be able to do so without error.
              //
              madlCopy.setState(MadlEntry.HINTS, librarySource, CacheState.ERROR);
            }
            madlCopy.setException(thrownException);
            cache.put(unitSource, madlCopy);
            madlEntry = madlCopy;
          }
        }
      }
    }
    if (thrownException != null) {
      throw thrownException;
    }
    return libraryEntry;
  }

  /**
   * Record the results produced by performing a {@link IncrementalAnalysisTask}.
   *
   * @param task the task that was performed
   * @return an entry containing the computed results
   * @throws AnalysisException if the results could not be recorded
   */
  private MadlEntry recordIncrementalAnalysisTaskResults(IncrementalAnalysisTask task)
      throws AnalysisException {
    synchronized (cacheLock) {
      CompilationUnit unit = task.getCompilationUnit();
      if (unit != null) {
        ChangeNoticeImpl notice = getNotice(task.getSource());
        notice.setCompilationUnit(unit);
        incrementalAnalysisCache = IncrementalAnalysisCache.cacheResult(task.getCache(), unit);
      }
    }
    return null;
  }

  /**
   * Record the results produced by performing a {@link ParseHtmlTask}. If the results were computed
   * from data that is now out-of-date, then the results will not be recorded.
   *
   * @param task the task that was performed
   * @return an entry containing the computed results
   * @throws AnalysisException if the results could not be recorded
   */
  private HtmlEntry recordParseHtmlTaskResults(ParseHtmlTask task) throws AnalysisException {
    Source source = task.getSource();
    AnalysisException thrownException = task.getException();
    HtmlEntry htmlEntry = null;
    synchronized (cacheLock) {
      SourceEntry sourceEntry = cache.get(source);
      if (!(sourceEntry instanceof HtmlEntry)) {
        // This shouldn't be possible because we should never have performed the task if the source
        // didn't represent an HTML file, but check to be safe.
        throw new AnalysisException(
            "Internal error: attempting to parse non-HTML file as a HTML file: "
                + source.getFullName());
      }
      htmlEntry = (HtmlEntry) sourceEntry;
      cache.accessed(source);
      long sourceTime = source.getModificationStamp();
      long resultTime = task.getModificationTime();
      if (sourceTime == resultTime) {
        if (htmlEntry.getModificationTime() != sourceTime) {
          // The source has changed without the context being notified. Simulate notification.
          sourceChanged(source);
          htmlEntry = getReadableHtmlEntry(source);
          if (htmlEntry == null) {
            throw new AnalysisException("An HTML file became a non-HTML file: "
                + source.getFullName());
          }
        }
        HtmlEntryImpl htmlCopy = ((HtmlEntry) sourceEntry).getWritableCopy();
        if (thrownException == null) {
          LineInfo lineInfo = task.getLineInfo();
          HtmlUnit unit = task.getHtmlUnit();
          htmlCopy.setValue(SourceEntry.LINE_INFO, lineInfo);
          htmlCopy.setValue(HtmlEntry.PARSED_UNIT, unit);
          htmlCopy.setValue(HtmlEntry.PARSE_ERRORS, task.getErrors());
          htmlCopy.setValue(HtmlEntry.REFERENCED_LIBRARIES, task.getReferencedLibraries());

          ChangeNoticeImpl notice = getNotice(source);
          notice.setErrors(htmlEntry.getAllErrors(), lineInfo);
        } else {
          htmlCopy.recordParseError();
        }
        htmlCopy.setException(thrownException);
        cache.put(source, htmlCopy);
        htmlEntry = htmlCopy;
      } else {
        HtmlEntryImpl htmlCopy = ((HtmlEntry) sourceEntry).getWritableCopy();
        if (thrownException == null || resultTime >= 0L) {
          //
          // The analysis was performed on out-of-date sources. Mark the cache so that the sources
          // will be re-analyzed using the up-to-date sources.
          //
          if (htmlCopy.getState(SourceEntry.LINE_INFO) == CacheState.IN_PROCESS) {
            htmlCopy.setState(SourceEntry.LINE_INFO, CacheState.INVALID);
          }
          if (htmlCopy.getState(HtmlEntry.PARSED_UNIT) == CacheState.IN_PROCESS) {
            htmlCopy.setState(HtmlEntry.PARSED_UNIT, CacheState.INVALID);
          }
          if (htmlCopy.getState(HtmlEntry.REFERENCED_LIBRARIES) == CacheState.IN_PROCESS) {
            htmlCopy.setState(HtmlEntry.REFERENCED_LIBRARIES, CacheState.INVALID);
          }
        } else {
          //
          // We could not determine whether the sources were up-to-date or out-of-date. Mark the
          // cache so that we won't attempt to re-analyze the sources until there's a good chance
          // that we'll be able to do so without error.
          //
          htmlCopy.setState(SourceEntry.LINE_INFO, CacheState.ERROR);
          htmlCopy.setState(HtmlEntry.PARSED_UNIT, CacheState.ERROR);
          htmlCopy.setState(HtmlEntry.REFERENCED_LIBRARIES, CacheState.ERROR);
        }
        htmlCopy.setException(thrownException);
        cache.put(source, htmlCopy);
        htmlEntry = htmlCopy;
      }
    }
    if (thrownException != null) {
      throw thrownException;
    }
    return htmlEntry;
  }

  /**
   * Record the results produced by performing a {@link ParseMadlTask}. If the results were computed
   * from data that is now out-of-date, then the results will not be recorded.
   *
   * @param task the task that was performed
   * @return an entry containing the computed results
   * @throws AnalysisException if the results could not be recorded
   */
  private MadlEntry recordParseMadlTaskResults(ParseMadlTask task) throws AnalysisException {
    Source source = task.getSource();
    AnalysisException thrownException = task.getException();
    MadlEntry madlEntry = null;
    synchronized (cacheLock) {
      SourceEntry sourceEntry = cache.get(source);
      if (!(sourceEntry instanceof MadlEntry)) {
        // This shouldn't be possible because we should never have performed the task if the source
        // didn't represent a Madl file, but check to be safe.
        throw new AnalysisException(
            "Internal error: attempting to parse non-Madl file as a Madl file: "
                + source.getFullName());
      }
      madlEntry = (MadlEntry) sourceEntry;
      cache.accessed(source);
      long sourceTime = source.getModificationStamp();
      long resultTime = task.getModificationTime();
      if (sourceTime == resultTime) {
        if (madlEntry.getModificationTime() != sourceTime) {
          // The source has changed without the context being notified. Simulate notification.
          sourceChanged(source);
          madlEntry = getReadableMadlEntry(source);
          if (madlEntry == null) {
            throw new AnalysisException("A Madl file became a non-Madl file: "
                + source.getFullName());
          }
        }
        MadlEntryImpl madlCopy = madlEntry.getWritableCopy();
        if (thrownException == null) {
          LineInfo lineInfo = task.getLineInfo();
          madlCopy.setValue(SourceEntry.LINE_INFO, lineInfo);
          if (task.hasPartOfDirective() && !task.hasLibraryDirective()) {
            madlCopy.setValue(MadlEntry.SOURCE_KIND, SourceKind.PART);
          } else {
            madlCopy.setValue(MadlEntry.SOURCE_KIND, SourceKind.LIBRARY);
          }
          madlCopy.setValue(MadlEntry.PARSED_UNIT, task.getCompilationUnit());
          madlCopy.setValue(MadlEntry.PARSE_ERRORS, task.getErrors());
          madlCopy.setValue(MadlEntry.EXPORTED_LIBRARIES, task.getExportedSources());
          madlCopy.setValue(MadlEntry.IMPORTED_LIBRARIES, task.getImportedSources());
          madlCopy.setValue(MadlEntry.INCLUDED_PARTS, task.getIncludedSources());

          ChangeNoticeImpl notice = getNotice(source);
          notice.setErrors(madlEntry.getAllErrors(), lineInfo);

          // Verify that the incrementally parsed and resolved unit in the incremental cache
          // is structurally equivalent to the fully parsed unit
          incrementalAnalysisCache = IncrementalAnalysisCache.verifyStructure(
              incrementalAnalysisCache,
              source,
              task.getCompilationUnit());
        } else {
          madlCopy.recordParseError();
        }
        madlCopy.setException(thrownException);
        cache.put(source, madlCopy);
        madlEntry = madlCopy;
      } else {
        MadlEntryImpl madlCopy = madlEntry.getWritableCopy();
        if (thrownException == null || resultTime >= 0L) {
          //
          // The analysis was performed on out-of-date sources. Mark the cache so that the sources
          // will be re-analyzed using the up-to-date sources.
          //
          madlCopy.recordParseNotInProcess();
        } else {
          //
          // We could not determine whether the sources were up-to-date or out-of-date. Mark the
          // cache so that we won't attempt to re-analyze the sources until there's a good chance
          // that we'll be able to do so without error.
          //
          madlCopy.recordParseError();
        }
        madlCopy.setException(thrownException);
        cache.put(source, madlCopy);
        madlEntry = madlCopy;
      }
    }
    if (thrownException != null) {
      throw thrownException;
    }
    return madlEntry;
  }

  /**
   * Record the results produced by performing a {@link ResolveHtmlTask}. If the results were
   * computed from data that is now out-of-date, then the results will not be recorded.
   *
   * @param task the task that was performed
   * @return an entry containing the computed results
   * @throws AnalysisException if the results could not be recorded
   */
  private SourceEntry recordResolveHtmlTaskResults(ResolveHtmlTask task) throws AnalysisException {
    Source source = task.getSource();
    AnalysisException thrownException = task.getException();
    HtmlEntry htmlEntry = null;
    synchronized (cacheLock) {
      SourceEntry sourceEntry = cache.get(source);
      if (!(sourceEntry instanceof HtmlEntry)) {
        // This shouldn't be possible because we should never have performed the task if the source
        // didn't represent an HTML file, but check to be safe.
        throw new AnalysisException(
            "Internal error: attempting to reolve non-HTML file as an HTML file: "
                + source.getFullName());
      }
      htmlEntry = (HtmlEntry) sourceEntry;
      cache.accessed(source);
      long sourceTime = source.getModificationStamp();
      long resultTime = task.getModificationTime();
      if (sourceTime == resultTime) {
        if (htmlEntry.getModificationTime() != sourceTime) {
          // The source has changed without the context being notified. Simulate notification.
          sourceChanged(source);
          htmlEntry = getReadableHtmlEntry(source);
          if (htmlEntry == null) {
            throw new AnalysisException("An HTML file became a non-HTML file: "
                + source.getFullName());
          }
        }
        HtmlEntryImpl htmlCopy = htmlEntry.getWritableCopy();
        if (thrownException == null) {
          htmlCopy.setValue(HtmlEntry.ELEMENT, task.getElement());
          htmlCopy.setValue(HtmlEntry.RESOLUTION_ERRORS, task.getResolutionErrors());
          ChangeNoticeImpl notice = getNotice(source);
          notice.setHtmlUnit(task.getResolvedUnit());
          notice.setErrors(htmlCopy.getAllErrors(), htmlCopy.getValue(SourceEntry.LINE_INFO));
        } else {
          htmlCopy.recordResolutionError();
        }
        htmlCopy.setException(thrownException);
        cache.put(source, htmlCopy);
        htmlEntry = htmlCopy;
      } else {
        HtmlEntryImpl htmlCopy = htmlEntry.getWritableCopy();
        if (thrownException == null || resultTime >= 0L) {
          //
          // The analysis was performed on out-of-date sources. Mark the cache so that the sources
          // will be re-analyzed using the up-to-date sources.
          //
          if (htmlCopy.getState(HtmlEntry.ELEMENT) == CacheState.IN_PROCESS) {
            htmlCopy.setState(HtmlEntry.ELEMENT, CacheState.INVALID);
          }
          if (htmlCopy.getState(HtmlEntry.RESOLUTION_ERRORS) == CacheState.IN_PROCESS) {
            htmlCopy.setState(HtmlEntry.RESOLUTION_ERRORS, CacheState.INVALID);
          }
        } else {
          //
          // We could not determine whether the sources were up-to-date or out-of-date. Mark the
          // cache so that we won't attempt to re-analyze the sources until there's a good chance
          // that we'll be able to do so without error.
          //
          htmlCopy.recordResolutionError();
        }
        htmlCopy.setException(thrownException);
        cache.put(source, htmlCopy);
        htmlEntry = htmlCopy;
      }
    }
    if (thrownException != null) {
      throw thrownException;
    }
    return htmlEntry;
  }

  /**
   * Record the results produced by performing a {@link ResolveMadlUnitTask}. If the results were
   * computed from data that is now out-of-date, then the results will not be recorded.
   *
   * @param task the task that was performed
   * @return an entry containing the computed results
   * @throws AnalysisException if the results could not be recorded
   */
  private SourceEntry recordResolveMadlUnitTaskResults(ResolveMadlUnitTask task)
      throws AnalysisException {
    Source unitSource = task.getSource();
    Source librarySource = task.getLibrarySource();
    AnalysisException thrownException = task.getException();
    MadlEntry madlEntry = null;
    synchronized (cacheLock) {
      SourceEntry sourceEntry = cache.get(unitSource);
      if (!(sourceEntry instanceof MadlEntry)) {
        // This shouldn't be possible because we should never have performed the task if the source
        // didn't represent a Madl file, but check to be safe.
        throw new AnalysisException(
            "Internal error: attempting to reolve non-Madl file as a Madl file: "
                + unitSource.getFullName());
      }
      madlEntry = (MadlEntry) sourceEntry;
      cache.accessed(unitSource);
      long sourceTime = unitSource.getModificationStamp();
      long resultTime = task.getModificationTime();
      if (sourceTime == resultTime) {
        if (madlEntry.getModificationTime() != sourceTime) {
          // The source has changed without the context being notified. Simulate notification.
          sourceChanged(unitSource);
          madlEntry = getReadableMadlEntry(unitSource);
          if (madlEntry == null) {
            throw new AnalysisException("A Madl file became a non-Madl file: "
                + unitSource.getFullName());
          }
        }
        MadlEntryImpl madlCopy = madlEntry.getWritableCopy();
        if (thrownException == null) {
          madlCopy.setValue(MadlEntry.RESOLVED_UNIT, librarySource, task.getResolvedUnit());
        } else {
          madlCopy.setState(MadlEntry.RESOLVED_UNIT, librarySource, CacheState.ERROR);
        }
        madlCopy.setException(thrownException);
        cache.put(unitSource, madlCopy);
        madlEntry = madlCopy;
      } else {
        MadlEntryImpl madlCopy = madlEntry.getWritableCopy();
        if (thrownException == null || resultTime >= 0L) {
          //
          // The analysis was performed on out-of-date sources. Mark the cache so that the sources
          // will be re-analyzed using the up-to-date sources.
          //
          if (madlCopy.getState(MadlEntry.RESOLVED_UNIT) == CacheState.IN_PROCESS) {
            madlCopy.setState(MadlEntry.RESOLVED_UNIT, librarySource, CacheState.INVALID);
          }
        } else {
          //
          // We could not determine whether the sources were up-to-date or out-of-date. Mark the
          // cache so that we won't attempt to re-analyze the sources until there's a good chance
          // that we'll be able to do so without error.
          //
          madlCopy.setState(MadlEntry.RESOLVED_UNIT, librarySource, CacheState.ERROR);
        }
        madlCopy.setException(thrownException);
        cache.put(unitSource, madlCopy);
        madlEntry = madlCopy;
      }
    }
    if (thrownException != null) {
      throw thrownException;
    }
    return madlEntry;
  }

  /**
   * Create an entry for the newly added source. Return {@code true} if the new source is a Madl
   * file.
   * <p>
   * <b>Note:</b> This method must only be invoked while we are synchronized on {@link #cacheLock}.
   *
   * @param source the source that has been added
   * @return {@code true} if the new source is a Madl file
   */
  private boolean sourceAvailable(Source source) {
    SourceEntry sourceEntry = cache.get(source);
    if (sourceEntry == null) {
      sourceEntry = createSourceEntry(source);
    } else {
      SourceEntryImpl sourceCopy = sourceEntry.getWritableCopy();
      sourceCopy.setModificationTime(source.getModificationStamp());
      cache.put(source, sourceCopy);
    }
    return sourceEntry instanceof MadlEntry;
  }

  /**
   * <b>Note:</b> This method must only be invoked while we are synchronized on {@link #cacheLock}.
   *
   * @param source the source that has been changed
   */
  private void sourceChanged(Source source) {
    SourceEntry sourceEntry = cache.get(source);
    if (sourceEntry == null || sourceEntry.getModificationTime() == source.getModificationStamp()) {
      // Either we have removed this source, in which case we don't care that it is changed, or we
      // have already invalidated the cache and don't need to invalidate it again.
      return;
    }
    if (sourceEntry instanceof HtmlEntry) {
      HtmlEntryImpl htmlCopy = ((HtmlEntry) sourceEntry).getWritableCopy();
      htmlCopy.setModificationTime(source.getModificationStamp());
      htmlCopy.invalidateAllInformation();
      cache.put(source, htmlCopy);
    } else if (sourceEntry instanceof MadlEntry) {
      Source[] containingLibraries = getLibrariesContaining(source);
      HashSet<Source> librariesToInvalidate = new HashSet<Source>();
      for (Source containingLibrary : containingLibraries) {
        librariesToInvalidate.add(containingLibrary);
        for (Source dependentLibrary : getLibrariesDependingOn(containingLibrary)) {
          librariesToInvalidate.add(dependentLibrary);
        }
      }

      for (Source library : librariesToInvalidate) {
//    for (Source library : containingLibraries) {
        invalidateLibraryResolution(library);
      }

      MadlEntryImpl madlCopy = ((MadlEntry) sourceEntry).getWritableCopy();
      madlCopy.setModificationTime(source.getModificationStamp());
      madlCopy.invalidateAllInformation();
      cache.put(source, madlCopy);
    }
  }

  /**
   * <b>Note:</b> This method must only be invoked while we are synchronized on {@link #cacheLock}.
   *
   * @param source the source that has been deleted
   */
  private void sourceRemoved(Source source) {
    SourceEntry sourceEntry = cache.get(source);
    if (sourceEntry instanceof MadlEntry) {
      HashSet<Source> libraries = new HashSet<Source>();
      for (Source librarySource : getLibrariesContaining(source)) {
        libraries.add(librarySource);
        for (Source dependentLibrary : getLibrariesDependingOn(librarySource)) {
          libraries.add(dependentLibrary);
        }
      }
      for (Source librarySource : libraries) {
        invalidateLibraryResolution(librarySource);
      }
    }
    cache.remove(source);
  }
}
