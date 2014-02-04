/*
 * Copyright (c) 2012, the Dart project authors.
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

import java.util.ArrayList;
import java.util.List;

/**
 * Create a sample web application with Pub support.
 *
 * @coverage madl.tools.core.generator
 */
public class BasicAppSample extends AbstractSample {

  public BasicAppSample() {
    super("Basic App", "Create basic sample application ");

    List<String[]> templates = new ArrayList<String[]>();

    templates.add(new String[] {"src/{name.lower}.madl", "app(name: \"{name.lower}\"){\n\n}"});
    templates.add(new String[] {"org.properties", "developer {\n"
        + "\tname = \"App Team\"\n"
        + "\torg = \"App Inc\"\n"
        + "\tdomain = \"apps.com\"\n}"
        + "\n"
        + "platform {\n"
        + "\tios{\n"
        + "\t\tversion = 6\n"
        + "\t}\n"
        + "\tandroid {\n"
        + "\t\tversion = 4.0\n"
        + "\t}\n"
        + "}\n"});
    setTemplates(templates);
    setMainFile("{name.lower}.madl");
  }

  @Override
  public boolean shouldBeDefault() {
    return true;
  }

}

