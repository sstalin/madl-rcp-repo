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
package edu.depaul.cdm.madl.engine.internal.sdk;

//import edu.depaul.cdm.madl.engine.ast.BooleanLiteral;
import edu.depaul.cdm.madl.engine.ast.CompilationUnit;
import edu.depaul.cdm.madl.engine.ast.Expression;
//import edu.depaul.cdm.madl.engine.ast.InstanceCreationExpression;
import edu.depaul.cdm.madl.engine.ast.MapLiteralEntry;
//import edu.depaul.cdm.madl.engine.ast.NamedExpression;
import edu.depaul.cdm.madl.engine.ast.SimpleIdentifier;
//import edu.depaul.cdm.madl.engine.ast.SimpleStringLiteral;
import edu.depaul.cdm.madl.engine.ast.visitor.RecursiveASTVisitor;
//import edu.depaul.cdm.madl.engine.error.BooleanErrorListener;
//import edu.depaul.cdm.madl.engine.parser.Parser;
//import edu.depaul.cdm.madl.engine.scanner.CharSequenceReader;
import edu.depaul.cdm.madl.engine.scanner.Scanner;
import edu.depaul.cdm.madl.engine.source.FileBasedSource;
import edu.depaul.cdm.madl.engine.source.Source;
import edu.depaul.cdm.madl.engine.source.UriKind;

import java.io.File;
import java.util.List;

/**
 * Instances of the class {@code SdkLibrariesReader} read and parse the libraries file
 * (madl-sdk/lib/_internal/libraries.madl) for information about the libraries in an SDK. The
 * library information is represented as a Madl file containing a single top-level variable whose
 * value is a const map. The keys of the map are the names of libraries defined in the SDK and the
 * values in the map are info objects defining the library. For example, a subset of a typical SDK
 * might have a libraries file that looks like the following:
 *
 * <pre>
 * final Map&lt;String, LibraryInfo&gt; LIBRARIES = const &lt;LibraryInfo&gt; {
 *   // Used by VM applications
 *   "builtin" : const LibraryInfo(
 *     "builtin/builtin_runtime.madl",
 *     category: "Server",
 *     platforms: VM_PLATFORM),
 *
 *   "compiler" : const LibraryInfo(
 *     "compiler/compiler.madl",
 *     category: "Tools",
 *     platforms: 0),
 * };
 * </pre>
 *
 * @coverage madl.engine.sdk
 */
public class SdkLibrariesReader {
  private static class LibraryBuilder extends RecursiveASTVisitor<Void> {
    /**
     * The prefix added to the name of a library to form the URI used in code to reference the
     * library.
     */
    private static final String LIBRARY_PREFIX = "madl:"; //$NON-NLS-1$

    /**
     * The name of the optional parameter used to indicate whether the library is an implementation
     * library.
     */
    private static final String IMPLEMENTATION = "implementation"; //$NON-NLS-1$

    /**
     * The name of the optional parameter used to indicate whether the library is documented.
     */
    private static final String DOCUMENTED = "documented"; //$NON-NLS-1$

    /**
     * The name of the optional parameter used to specify the category of the library.
     */
    private static final String CATEGORY = "category"; //$NON-NLS-1$

    /**
     * The name of the optional parameter used to specify the platforms on which the library can be
     * used.
     */
    private static final String PLATFORMS = "platforms"; //$NON-NLS-1$

    /**
     * The value of the {@link #PLATFORMS platforms} parameter used to specify that the library can
     * be used on the VM.
     */
    private static final String VM_PLATFORM = "VM_PLATFORM"; //$NON-NLS-1$

    /**
     * The library map that is populated by visiting the AST structure parsed from the contents of
     * the libraries file.
     */
    private LibraryMap librariesMap = new LibraryMap();

    /**
     * Return the library map that was populated by visiting the AST structure parsed from the
     * contents of the libraries file.
     *
     * @return the library map describing the contents of the SDK
     */
    public LibraryMap getLibrariesMap() {
      return librariesMap;
    }
//ss
/*    @Override
    public Void visitMapLiteralEntry(MapLiteralEntry node) {
      //ss
      String libraryName = null;
      Expression key = node.getKey();
      if (key instanceof SimpleStringLiteral) {
        libraryName = LIBRARY_PREFIX + ((SimpleStringLiteral) key).getValue();
      }
      Expression value = node.getValue();
      if (value instanceof InstanceCreationExpression) {
        SdkLibraryImpl library = new SdkLibraryImpl(libraryName);
        List<Expression> arguments = ((InstanceCreationExpression) value).getArgumentList().getArguments();
        for (Expression argument : arguments) {
          if (argument instanceof SimpleStringLiteral) {
            library.setPath(((SimpleStringLiteral) argument).getValue());
          } else if (argument instanceof NamedExpression) {
            String name = ((NamedExpression) argument).getName().getLabel().getName();
            Expression expression = ((NamedExpression) argument).getExpression();
            if (name.equals(CATEGORY)) {
              library.setCategory(((SimpleStringLiteral) expression).getValue());
            } else if (name.equals(IMPLEMENTATION)) {
              library.setImplementation(((BooleanLiteral) expression).getValue());
            } else if (name.equals(DOCUMENTED)) {
              library.setDocumented(((BooleanLiteral) expression).getValue());
            } else if (name.equals(PLATFORMS)) {
              if (expression instanceof SimpleIdentifier) {
                String identifier = ((SimpleIdentifier) expression).getName();
                if (identifier.equals(VM_PLATFORM)) {
                  library.setVmLibrary();
                } else {
                  library.setMadl2JsLibrary();
                }
              }
            }
          }
        }
        librariesMap.setLibrary(libraryName, library);
      }
      return null;
    }*/
  }

  /**
   * Return the library map read from the given source.
   *
   * @param file the {@link File} of the library file
   * @param libraryFileContents the contents from the library file
   * @return the library map read from the given source
   */
  public LibraryMap readFrom(File file, String libraryFileContents) {
    return readFrom(new FileBasedSource(null, file, UriKind.FILE_URI), libraryFileContents);
  }

  /**
   * Return the library map read from the given source.
   *
   * @param source the source of the library file
   * @param libraryFileContents the contents from the library file
   * @return the library map read from the given source
   */
  public LibraryMap readFrom(Source source, String libraryFileContents) {
    //ss
 /*   BooleanErrorListener errorListener = new BooleanErrorListener();
    Scanner scanner = new Scanner(
        source,
        new CharSequenceReader(libraryFileContents),
        errorListener);
    Parser parser = new Parser(source, errorListener);
    CompilationUnit unit = parser.parseCompilationUnit(scanner.tokenize());
    LibraryBuilder libraryBuilder = new LibraryBuilder();
    // If any syntactic errors were found then don't try to visit the AST structure.
    if (!errorListener.getErrorReported()) {
      unit.accept(libraryBuilder);
    }
    return libraryBuilder.getLibrariesMap();*/
    return null;
  }
}