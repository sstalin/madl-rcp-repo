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
package edu.depaul.cdm.madl.tools.ui.actions;

import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;

/**
 * Defines the definition IDs for the Madl editor actions.
 * <p>
 * This interface is not intended to be implemented or extended.
 * </p>
 * . Provisional API: This class/interface is part of an interim API that is still under development
 * and expected to change significantly before reaching stability. It is being made available at
 * this early stage to solicit feedback from pioneering adopters on the understanding that any code
 * that uses this API will almost certainly be broken (repeatedly) as the API evolves.
 */
public interface MadlEditorActionDefinitionIds extends ITextEditorActionDefinitionIds {

  // edit

  /**
   * Action definition ID of the edit -> smart typing action (value
   * <code>"edu.depaul.cdm.madl.tools.smartTyping.toggle"</code>).
   */
  public static final String TOGGLE_SMART_TYPING = "edu.depaul.cdm.madl.tools.smartTyping.toggle"; //$NON-NLS-1$

  /**
   * Action definition ID of the edit -> go to matching bracket action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.goto.matching.bracket"</code> ).
   */
  public static final String GOTO_MATCHING_BRACKET = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.goto.matching.bracket"; //$NON-NLS-1$

  /**
   * Action definition ID of the edit -> go to next member action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.goto.next.member"</code>).
   */
  public static final String GOTO_NEXT_MEMBER = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.goto.next.member"; //$NON-NLS-1$

  /**
   * Action definition ID of the edit -> go to previous member action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.goto.previous.member"</code> ).
   */
  public static final String GOTO_PREVIOUS_MEMBER = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.goto.previous.member"; //$NON-NLS-1$

  /**
   * Action definition ID of the edit -> select enclosing action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.select.enclosing"</code>).
   */
  public static final String SELECT_ENCLOSING = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.select.enclosing"; //$NON-NLS-1$

  /**
   * Action definition ID of the edit -> select next action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.select.next"</code>).
   */
  public static final String SELECT_NEXT = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.select.next"; //$NON-NLS-1$

  /**
   * Action definition ID of the edit -> select previous action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.select.previous"</code>).
   */
  public static final String SELECT_PREVIOUS = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.select.previous"; //$NON-NLS-1$

  /**
   * Action definition ID of the edit -> select restore last action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.select.last"</code>).
   */
  public static final String SELECT_LAST = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.select.last"; //$NON-NLS-1$

  /**
   * Action definition ID of the edit -> content assist complete prefix action (value:
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.complete.prefix"</code>).
   */
  public static final String CONTENT_ASSIST_COMPLETE_PREFIX = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.complete.prefix"; //$NON-NLS-1$

  /**
   * Action definition ID of the navigate -> Show Outline action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.show.outline"</code>).
   */
  public static final String SHOW_OUTLINE = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.show.outline"; //$NON-NLS-1$

  /**
   * Action definition ID of the navigate -> Show Hierarchy action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.open.hierarchy"</code>).
   */
  public static final String OPEN_HIERARCHY = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.open.hierarchy"; //$NON-NLS-1$

  /**
   * Action definition ID of the Navigate -> Open Structure action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.navigate.madl.open.structure"</code>).
   */
  public static final String OPEN_STRUCTURE = "edu.depaul.cdm.madl.tools.ui.navigate.madl.open.structure"; //$NON-NLS-1$

  // source

  /**
   * Action definition ID of the source -> comment action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.comment"</code>).
   */
  public static final String COMMENT = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.comment"; //$NON-NLS-1$

  /**
   * Action definition ID of the source -> uncomment action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.uncomment"</code>).
   */
  public static final String UNCOMMENT = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.uncomment"; //$NON-NLS-1$

  /**
   * Action definition ID of the source -> toggle comment action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.toggle.comment"</code>).
   */
  public static final String TOGGLE_COMMENT = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.toggle.comment"; //$NON-NLS-1$

  /**
   * Action definition ID of the source -> add block comment action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.add.block.comment"</code>).
   */
  public static final String ADD_BLOCK_COMMENT = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.add.block.comment"; //$NON-NLS-1$

  /**
   * Action definition ID of the source -> remove block comment action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.remove.block.comment"</code> ).
   */
  public static final String REMOVE_BLOCK_COMMENT = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.remove.block.comment"; //$NON-NLS-1$

  /**
   * Action definition ID of the source -> indent action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.indent"</code>).
   */
  public static final String INDENT = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.indent"; //$NON-NLS-1$

  /**
   * Action definition ID of the source -> format action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.format"</code>).
   */
  public static final String FORMAT = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.format"; //$NON-NLS-1$

  /**
   * Action definition id of the Madl quick format action (value:
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.quick.format"</code>).
   */
  public static final String QUICK_FORMAT = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.quick.format"; //$NON-NLS-1$

  /**
   * Action definition ID of the source -> add import action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.add.import"</code>).
   */
//  public static final String ADD_IMPORT = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.add.import"; //$NON-NLS-1$

  /**
   * Action definition ID of the source -> organize imports action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.organize.imports"</code>).
   */
  public static final String ORGANIZE_IMPORTS = "edu.depaul.cdm.madl.tools.ui.edit.text.organize.imports"; //$NON-NLS-1$

  /**
   * Action definition ID of the source -> sort order action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.sort.members"</code>).
   */
  public static final String SORT_MEMBERS = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.sort.members"; //$NON-NLS-1$

  /**
   * Action definition ID of the source -> add madldoc comment action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.add.madldoc.comment"</code> ).
   */
  public static final String ADD_JAVADOC_COMMENT = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.add.madldoc.comment"; //$NON-NLS-1$

  /**
   * Action definition ID of the source -> surround with try/catch action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.surround.with.try.catch"</code> ).
   */
  public static final String SURROUND_WITH_TRY_CATCH = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.surround.with.try.catch"; //$NON-NLS-1$

  /**
   * Action definition ID of the source -> override methods action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.override.methods"</code>).
   */
  public static final String OVERRIDE_METHODS = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.override.methods"; //$NON-NLS-1$

  /**
   * Action definition ID of the source -> add unimplemented constructors action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.add.unimplemented.constructors"</code> ).
   */
  public static final String ADD_UNIMPLEMENTED_CONTRUCTORS = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.add.unimplemented.constructors"; //$NON-NLS-1$

  /**
   * Action definition ID of the source ->generate constructor using fields action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.generate.constructor.using.fields"</code> ).
   */
  public static final String GENERATE_CONSTRUCTOR_USING_FIELDS = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.generate.constructor.using.fields"; //$NON-NLS-1$

  /**
   * Action definition ID of the source ->generate hashcode() and equals() action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.generate.hashcode.equals"</code> ).
   */
  public static final String GENERATE_HASHCODE_EQUALS = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.generate.hashcode.equals"; //$NON-NLS-1$

  /**
   * Action definition ID of the source -> generate setter/getter action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.create.getter.setter"</code> ).
   */
  public static final String CREATE_GETTER_SETTER = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.create.getter.setter"; //$NON-NLS-1$

  /**
   * Action definition ID of the source -> generate delegates action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.create.delegate.methods"</code> ).
   */
  public static final String CREATE_DELEGATE_METHODS = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.create.delegate.methods"; //$NON-NLS-1$

  /**
   * Action definition ID of the source -> externalize strings action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.externalize.strings"</code> ).
   */
  public static final String EXTERNALIZE_STRINGS = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.externalize.strings"; //$NON-NLS-1$

  /**
   * Action definition ID of the refactor -> pull up action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.pull.up"</code>).
   */
  public static final String PULL_UP = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.pull.up"; //$NON-NLS-1$

  /**
   * Action definition ID of the refactor -> push down action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.push.down"</code>).
   */
  public static final String PUSH_DOWN = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.push.down"; //$NON-NLS-1$

  /**
   * Action definition ID of the refactor -> rename element action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.rename.element"</code>).
   */
  public static final String RENAME_ELEMENT = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.rename.element"; //$NON-NLS-1$

  /**
   * Action definition ID of the refactor -> modify method parameters action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.modify.method.parameters"</code> ).
   */
  public static final String MODIFY_METHOD_PARAMETERS = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.modify.method.parameters"; //$NON-NLS-1$

  /**
   * Action definition ID of the refactor -> move element action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.move.element"</code>).
   */
  public static final String MOVE_ELEMENT = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.move.element"; //$NON-NLS-1$

  /**
   * Action definition ID of the refactor -> extract local variable action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.extract.local.variable"</code> ).
   */
  public static final String EXTRACT_LOCAL_VARIABLE = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.extract.local.variable"; //$NON-NLS-1$

  /**
   * Action definition ID of the refactor -> extract constant action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.extract.constant"</code>).
   */
  public static final String EXTRACT_CONSTANT = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.extract.constant"; //$NON-NLS-1$

  /**
   * Action definition ID of the refactor -> introduce parameter action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.introduce.parameter"</code> ).
   */
  public static final String INTRODUCE_PARAMETER = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.introduce.parameter"; //$NON-NLS-1$

  /**
   * Action definition ID of the refactor -> introduce factory action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.introduce.factory"</code>).
   */
  public static final String INTRODUCE_FACTORY = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.introduce.factory"; //$NON-NLS-1$

  /**
   * Action definition ID of the refactor -> self encapsulate field action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.self.encapsulate.field"</code> ).
   */
  public static final String SELF_ENCAPSULATE_FIELD = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.self.encapsulate.field"; //$NON-NLS-1$

  /**
   * Action definition ID of the refactor -> extract method action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.extract.method"</code>).
   */
  public static final String EXTRACT_METHOD = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.extract.method"; //$NON-NLS-1$

  /**
   * Action definition ID of the refactor -> inline action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.inline"</code>).
   */
  public static final String INLINE = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.inline"; //$NON-NLS-1$

  public static final String CONVERT_METHOD_TO_GETTER = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.convertMethodToGetter"; //$NON-NLS-1$
  public static final String CONVERT_GETTER_TO_METHOD = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.convertGetterToMethod"; //$NON-NLS-1$

  /**
   * Action definition ID of the refactor -> replace invocations action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.replace.invocations"</code> ).
   */
  public static final String REPLACE_INVOCATIONS = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.replace.invocations"; //$NON-NLS-1$

  /**
   * Action definition ID of the refactor -> introduce indirection action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.create.indirection"</code>).
   */
  public static final String INTRODUCE_INDIRECTION = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.introduce.indirection"; //$NON-NLS-1$

  /**
   * Action definition ID of the refactor -> extract interface action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.extract.interface"</code>).
   */
  public static final String EXTRACT_INTERFACE = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.extract.interface"; //$NON-NLS-1$

  /**
   * Action definition ID of the refactor -> change type action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.change.type"</code>).
   */
  public static final String CHANGE_TYPE = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.change.type"; //$NON-NLS-1$

  /**
   * Action definition ID of the refactor -> move inner type to top level action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.move.inner.to.top.level"</code> ).
   */
  public static final String MOVE_INNER_TO_TOP = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.move.inner.to.top.level"; //$NON-NLS-1$

  /**
   * Action definition ID of the refactor -> use supertype action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.use.supertype"</code>).
   */
  public static final String USE_SUPERTYPE = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.use.supertype"; //$NON-NLS-1$

  /**
   * Action definition ID of the refactor -> infer generic type arguments action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.infer.type.arguments"</code> ).
   */
  public static final String INFER_TYPE_ARGUMENTS_ACTION = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.infer.type.arguments"; //$NON-NLS-1$

  /**
   * Action definition ID of the refactor -> promote local variable action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.promote.local.variable"</code> ).
   */
  public static final String PROMOTE_LOCAL_VARIABLE = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.promote.local.variable"; //$NON-NLS-1$

  /**
   * Action definition ID of the refactor -> convert anonymous to nested action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.convert.anonymous.to.nested"</code> ).
   */
  public static final String CONVERT_ANONYMOUS_TO_NESTED = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.convert.anonymous.to.nested"; //$NON-NLS-1$

  // navigate

  /**
   * Action definition ID of the navigate -> open action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.open.editor"</code>).
   */
  public static final String OPEN_EDITOR = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.open.editor"; //$NON-NLS-1$

  /**
   * Action definition ID of the navigate -> open super implementation action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.open.super.implementation"</code> ).
   */
  public static final String OPEN_SUPER_IMPLEMENTATION = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.open.super.implementation"; //$NON-NLS-1$

  /**
   * Action definition ID of the navigate -> open external madldoc action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.open.external.madldoc"</code> ).
   */
  public static final String OPEN_EXTERNAL_DARTDOC = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.open.external.madldoc"; //$NON-NLS-1$

  /**
   * Action definition ID of the navigate -> open type hierarchy action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.open.type.hierarchy"</code> ).
   */
  public static final String OPEN_TYPE_HIERARCHY = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.open.type.hierarchy"; //$NON-NLS-1$

  /**
   * Action definition ID of the navigate -> open call hierarchy action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.open.call.hierarchy"</code> ).
   */
  public static final String OPEN_CALL_HIERARCHY = "edu.depaul.cdm.madl.tools.ui.edit.text.open.call.hierarchy"; //$NON-NLS-1$

  /**
   * Action definition ID of the navigate -> open call hierarchy action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.analyze.call.hierarchy"</code> ).
   */
  public static final String ANALYZE_CALL_HIERARCHY = "edu.depaul.cdm.madl.tools.ui.edit.text.analyze.call.hierarchy"; //$NON-NLS-1$

  /**
   * Action definition ID of the navigate -> show in package explorer action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.show.in.package.view"</code> ).
   */
  public static final String SHOW_IN_PACKAGE_VIEW = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.show.in.package.view"; //$NON-NLS-1$

  /**
   * Action definition ID of the navigate -> show in navigator action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.show.in.navigator.view"</code> ).
   */
  public static final String SHOW_IN_NAVIGATOR_VIEW = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.show.in.navigator.view"; //$NON-NLS-1$

  // search

  /**
   * Action definition ID of the search -> references in workspace action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.search.references.in.workspace"</code> ).
   */
  public static final String SEARCH_REFERENCES_IN_WORKSPACE = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.search.references.in.workspace"; //$NON-NLS-1$

  /**
   * Action definition ID of the search -> references in project action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.search.references.in.project"</code> ).
   */
  public static final String SEARCH_REFERENCES_IN_PROJECT = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.search.references.in.project"; //$NON-NLS-1$

  /**
   * Action definition ID of the search -> references in hierarchy action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.search.references.in.hierarchy"</code> ).
   */
  public static final String SEARCH_REFERENCES_IN_HIERARCHY = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.search.references.in.hierarchy"; //$NON-NLS-1$

  /**
   * Action definition ID of the search -> references in working set action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.search.references.in.working.set"</code> ).
   */
  public static final String SEARCH_REFERENCES_IN_WORKING_SET = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.search.references.in.working.set"; //$NON-NLS-1$

  /**
   * Action definition ID of the search -> read access in workspace action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.search.read.access.in.workspace"</code> ).
   */
  public static final String SEARCH_READ_ACCESS_IN_WORKSPACE = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.search.read.access.in.workspace"; //$NON-NLS-1$

  /**
   * Action definition ID of the search -> read access in project action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.search.read.access.in.project"</code> ).
   */
  public static final String SEARCH_READ_ACCESS_IN_PROJECT = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.search.read.access.in.project"; //$NON-NLS-1$

  /**
   * Action definition ID of the search -> read access in hierarchy action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.search.read.access.in.hierarchy"</code> ).
   */
  public static final String SEARCH_READ_ACCESS_IN_HIERARCHY = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.search.read.access.in.hierarchy"; //$NON-NLS-1$

  /**
   * Action definition ID of the search -> read access in working set action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.search.read.access.in.working.set"</code> ).
   */
  public static final String SEARCH_READ_ACCESS_IN_WORKING_SET = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.search.read.access.in.working.set"; //$NON-NLS-1$

  /**
   * Action definition ID of the search -> write access in workspace action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.search.write.access.in.workspace"</code> ).
   */
  public static final String SEARCH_WRITE_ACCESS_IN_WORKSPACE = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.search.write.access.in.workspace"; //$NON-NLS-1$

  /**
   * Action definition ID of the search -> write access in project action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.search.write.access.in.project"</code> ).
   */
  public static final String SEARCH_WRITE_ACCESS_IN_PROJECT = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.search.write.access.in.project"; //$NON-NLS-1$

  /**
   * Action definition ID of the search -> write access in hierarchy action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.search.write.access.in.hierarchy"</code> ).
   */
  public static final String SEARCH_WRITE_ACCESS_IN_HIERARCHY = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.search.write.access.in.hierarchy"; //$NON-NLS-1$

  /**
   * Action definition ID of the search -> write access in working set action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.search.write.access.in.working.set"</code>
   * ).
   */
  public static final String SEARCH_WRITE_ACCESS_IN_WORKING_SET = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.search.write.access.in.working.set"; //$NON-NLS-1$

  /**
   * Action definition ID of the search -> declarations in workspace action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.search.declarations.in.workspace"</code> ).
   */
  public static final String SEARCH_DECLARATIONS_IN_WORKSPACE = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.search.declarations.in.workspace"; //$NON-NLS-1$
  /**
   * Action definition ID of the search -> declarations in project action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.search.declarations.in.project"</code> ).
   */
  public static final String SEARCH_DECLARATIONS_IN_PROJECTS = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.search.declarations.in.project"; //$NON-NLS-1$
  /**
   * Action definition ID of the search -> declarations in hierarchy action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.search.declarations.in.hierarchy"</code> ).
   */
  public static final String SEARCH_DECLARATIONS_IN_HIERARCHY = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.search.declarations.in.hierarchy"; //$NON-NLS-1$
  /**
   * Action definition ID of the search -> declarations in working set action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.search.declarations.in.working.set"</code>
   * ).
   */
  public static final String SEARCH_DECLARATIONS_IN_WORKING_SET = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.search.declarations.in.working.set"; //$NON-NLS-1$
  /**
   * Action definition ID of the search -> implementors in workspace action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.search.implementors.in.workspace"</code> ).
   */
  public static final String SEARCH_IMPLEMENTORS_IN_WORKSPACE = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.search.implementors.in.workspace"; //$NON-NLS-1$
  /**
   * Action definition ID of the search -> implementors in working set action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.search.implementors.in.working.set"</code>
   * ).
   */
  public static final String SEARCH_IMPLEMENTORS_IN_WORKING_SET = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.search.implementors.in.working.set"; //$NON-NLS-1$

  /**
   * Action definition ID of the search -> implementors in project action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.search.implementors.in.project"</code> ).
   */
  public static final String SEARCH_IMPLEMENTORS_IN_PROJECT = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.search.implementors.in.project"; //$NON-NLS-1$

  /**
   * Action definition ID of the search -> occurrences in file quick menu action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.search.occurrences.in.file.quickMenu"</code>
   * ).
   */
  public static final String SEARCH_OCCURRENCES_IN_FILE_QUICK_MENU = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.search.occurrences.in.file.quickMenu"; //$NON-NLS-1$

  /**
   * Action definition ID of the search -> occurrences in file > elements action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.search.occurrences.in.file"</code> ).
   */
  public static final String SEARCH_OCCURRENCES_IN_FILE = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.search.occurrences.in.file"; //$NON-NLS-1$

  /**
   * Action definition ID of the search -> occurrences in file > exceptions action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.search.exception.occurrences"</code> ).
   */
  public static final String SEARCH_EXCEPTION_OCCURRENCES_IN_FILE = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.search.exception.occurrences"; //$NON-NLS-1$

  /**
   * Action definition ID of the search -> occurrences in file > implements action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.search.implement.occurrences"</code> ).
   */
  public static final String SEARCH_IMPLEMENT_OCCURRENCES_IN_FILE = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.search.implement.occurrences"; //$NON-NLS-1$

  // miscellaneous

  /**
   * Action definition ID of the toggle text hover tool bar button action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.toggle.text.hover"</code>).
   */
  public static final String TOGGLE_TEXT_HOVER = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.toggle.text.hover"; //$NON-NLS-1$

  /**
   * Action definition ID of the remove occurrence annotations action (value
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.remove.occurrence.annotations"</code> ).
   */
  public static final String REMOVE_OCCURRENCE_ANNOTATIONS = "edu.depaul.cdm.madl.tools.ui.edit.text.remove.occurrence.annotations"; //$NON-NLS-1$

  /**
   * Action definition id of toggle mark occurrences action (value:
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.toggleMarkOccurrences"</code> ).
   */
  public static final String TOGGLE_MARK_OCCURRENCES = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.toggleMarkOccurrences"; //$NON-NLS-1$

  /**
   * Action definition id of the collapse members action (value:
   * <code>"edu.depaul.cdm.madl.tools.ui.text.folding.collapseMembers"</code> ).
   */
  public static final String FOLDING_COLLAPSE_MEMBERS = "edu.depaul.cdm.madl.tools.ui.text.folding.collapseMembers"; //$NON-NLS-1$

  /**
   * Action definition id of the collapse comments action (value:
   * <code>"edu.depaul.cdm.madl.tools.ui.text.folding.collapseComments"</code> ).
   */
  public static final String FOLDING_COLLAPSE_COMMENTS = "edu.depaul.cdm.madl.tools.ui.text.folding.collapseComments"; //$NON-NLS-1$

  /**
   * Action definition id of the code clean up action (value:
   * <code>"edu.depaul.cdm.madl.tools.ui.edit.text.madl.clean.up"</code>).
   */
  public static final String CLEAN_UP = "edu.depaul.cdm.madl.tools.ui.edit.text.madl.clean.up"; //$NON-NLS-1$
}
