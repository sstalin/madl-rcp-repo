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
package edu.depaul.cdm.madl.tools.core.generator;

import edu.depaul.cdm.madl.tools.core.MadlCore;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * Generator for new file wizard in the Files view.
 *
 * @coverage madl.tools.core.generator
 */
public class NewFileGenerator extends AbstractGenerator {

  private InputStream stream;

  private String fileName;

  @Override
  public void execute(IProgressMonitor monitor) throws CoreException {
    stream = null;
    final HashMap<String, String> substitutions = new HashMap<String, String>();

    String nameOfSrcTxt = "";

    if (MadlCore.isMadlLikeFileName(fileName)) {
      String className = fileName.substring(0, fileName.indexOf('.'));
      className = MadlIdentifierUtil.createClassName(className);
      substitutions.put("className", className); //$NON-NLS-1$
      nameOfSrcTxt = "generated-madl-class-empty.txt";
    }
//ss
   /* if (MadlCore.isHtmlLikeFileName(fileName)) {
      String className = fileName.substring(0, fileName.indexOf('.'));
      substitutions.put("title", className); //$NON-NLS-1$
      substitutions.put("fileName", className); //$NON-NLS-1$
      substitutions.put("madlSrcPath", className); //$NON-NLS-1$
      nameOfSrcTxt = "generated-html-new.txt";
    }*/

    if (MadlCore.PUBSPEC_FILE_NAME.equals(fileName)) {
      substitutions.put("className", "sample_pubspec"); //$NON-NLS-1$
      nameOfSrcTxt = "generated-pubspec.txt"; //$NON-NLS-N$
    }

    if (nameOfSrcTxt.length() != 0) {
      try {
        stream = new ByteArrayInputStream(
            readExpectedContent(nameOfSrcTxt, substitutions).getBytes());
      } catch (IOException e) {

      }
    }
  }

  public InputStream getStream() {
    return stream;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  @Override
  public IStatus validate() {
    return Status.OK_STATUS;
  }
}
