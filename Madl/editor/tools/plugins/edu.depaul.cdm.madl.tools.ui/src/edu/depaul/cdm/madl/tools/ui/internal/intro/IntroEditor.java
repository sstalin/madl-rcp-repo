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
package edu.depaul.cdm.madl.tools.ui.internal.intro;

import edu.depaul.cdm.madl.tools.core.MadlCore;
import edu.depaul.cdm.madl.tools.ui.MadlToolsPlugin;
import edu.depaul.cdm.madl.tools.ui.internal.projects.OpenNewApplicationWizardAction;
// import edu.depaul.cdm.madl.tools.ui.internal.util.ExternalBrowserUtil;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.part.EditorPart;

/**
 * A "fake" editor for showing intro content to first time users.
 */
public class IntroEditor extends EditorPart {
  public static final String ID = "edu.depaul.cdm.madl.tools.ui.intro.editor"; //$NON-NLS-1$

  public static final IEditorInput INPUT = new IEditorInput() {
    @Override
    public boolean exists() {
      return false;
    }

    @Override
    public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
      return null;
    }

    @Override
    public ImageDescriptor getImageDescriptor() {
      return null;
    }

    @Override
    public String getName() {
      return IntroMessages.IntroEditor_name;
    }

    @Override
    public IPersistableElement getPersistable() {
      return null;
    }

    @Override
    public String getToolTipText() {
      return IntroMessages.IntroEditor_tooltip;
    }
  };

  private static FontData[] getModifiedFontData(FontData[] originalData) {
    FontData[] styleData = new FontData[originalData.length];
    for (int i = 0; i < styleData.length; i++) {
      FontData base = originalData[i];
      styleData[i] = new FontData(base.getName(), base.getHeight() + 1, SWT.NORMAL);
    }
    return styleData;
  }

  private ScrolledForm form;

  private FormToolkit toolkit;

  private Font bigFont;

  public IntroEditor() {

  }

  @Override
  public void createPartControl(Composite parent) {
    toolkit = new FormToolkit(parent.getDisplay());

    // Create the form and header.
    form = toolkit.createScrolledForm(parent);
    bigFont = getBigFont(getModifiedFontData(form.getFont().getFontData()));
    form.setFont(bigFont);
    form.setText("Welcome to Madl Editor!");
    form.setImage(MadlToolsPlugin.getImage("icons/madl_16_16.gif"));
    toolkit.decorateFormHeading(form.getForm());
    form.getToolBarManager().update(true);

    TableWrapLayout layout = new TableWrapLayout();
    layout.verticalSpacing = 30;
    layout.topMargin = 12;
    form.getBody().setLayout(layout);

    // Create the actions area.
    Section section = toolkit.createSection(form.getBody(), Section.TITLE_BAR);
    section.setLayoutData(new TableWrapData(TableWrapData.FILL, TableWrapData.TOP, 1, 1));
    section.setText("Getting Started");
    section.setFont(bigFont);
    Composite client = toolkit.createComposite(section);
    TableWrapLayout tl = new TableWrapLayout();
    tl.numColumns = 3;
    tl.verticalSpacing = 10;
    tl.horizontalSpacing = 30;
    client.setLayout(tl);

    Button createButton = new Button(client, SWT.PUSH | SWT.LEFT);
    createButton.setText("Create an application...");
    createButton.setFont(bigFont);
    createButton.setImage(MadlToolsPlugin.getImage("icons/full/madl16/package_obj_new.png"));
    createButton.setLayoutData(new TableWrapData(TableWrapData.FILL, TableWrapData.MIDDLE));
    createButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        OpenNewApplicationWizardAction action = new OpenNewApplicationWizardAction();
        action.run();
      }
    });

    createExternalLink(client, "See more Madl samples...",
        "http://apollo.cti.depaul.edu/axiom/examples/index.html");
    createExternalLink(client, "Browse Madl libraries...",
        "http://apollo.cti.depaul.edu/axiom/madl/index.html");
    section.setClient(client);

    // Create the samples area.
    if (MadlCore.isWindowsXp()) {
      //ss
      /*
       * section = toolkit.createSection(form.getBody(), Section.DESCRIPTION | Section.TITLE_BAR);
       * section
       * .setDescription("The samples require the madl.js file to run. In the sample's HTML file, "
       * + "change src=\"packages/browser/madl.js\" to point to the local copy of madl.js, " +
       * "located in the Editor's samples directory.");
       */
    } else {
      section = toolkit.createSection(form.getBody(), Section.TITLE_BAR);
    }

    section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.FILL_GRAB, 1, 1));
    section.setText("Demos of Madl");
    section.setFont(bigFont);
    client = toolkit.createComposite(section);
    TableWrapLayout l = new TableWrapLayout();
    l.numColumns = 3;
    l.makeColumnsEqualWidth = true;
    l.verticalSpacing = 10;
    client.setLayout(l);

//ss from here down some  sample Madl programs ... 

    /*
     * for (final SampleDescription description : SampleDescriptionHelper.getDescriptions()) { if
     * (!description.name.equals("TodoMVC") && !description.name.equals("Angular Madl")) { Composite
     * composite = toolkit.createComposite(client); TableWrapLayout lc = new TableWrapLayout();
     * lc.numColumns = 2; composite.setLayout(lc); addSampleInfo(composite, description); } }
     * section.setClient(client);
     * 
     * // create the more info area section = toolkit.createSection(form.getBody(),
     * Section.TITLE_BAR); section.setText("More about Madl"); section.setFont(bigFont); client =
     * toolkit.createComposite(section); l = new TableWrapLayout(); l.numColumns = 3;
     * l.makeColumnsEqualWidth = true; l.verticalSpacing = 10; client.setLayout(l);
     * 
     * Composite composite = toolkit.createComposite(client); TableWrapLayout lc = new
     * TableWrapLayout(); lc.numColumns = 2; composite.setLayout(lc); //ss final String href =
     * "http://www.madllang.org/codelabs/darrrt/?utm_source=editor&utm_medium=welcome&utm_campaign=newuser"
     * ;
     * 
     * Label label = toolkit.createLabel(composite, "", SWT.NONE);
     * label.setImage(MadlToolsPlugin.getImage("samples/tutorial.png"));
     * label.setCursor(getSite().getShell().getDisplay().getSystemCursor(SWT.CURSOR_HAND));
     * label.addMouseListener(new MouseAdapter() {
     * 
     * @Override public void mouseUp(MouseEvent e) { //ss
     * //ExternalBrowserUtil.openInExternalBrowser(href); } });
     * 
     * FormText formText = toolkit.createFormText(composite, true);
     * formText.setText("<form><p><a href=\"a\">Code Lab</a><br></br>" +
     * "Learn Madl with pirates</p></form>", true, false); formText.setLayoutData(new
     * TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.TOP, 1, 1));
     * formText.addHyperlinkListener(openLink(href)); formText.setFont(bigFont);
     * 
     * composite = toolkit.createComposite(client); lc = new TableWrapLayout(); lc.numColumns = 2;
     * composite.setLayout(lc);
     * 
     * final String href2 =
     * "https://www.madllang.org/docs/?utm_source=editor&utm_medium=welcome&utm_campaign=newuser";
     * label = toolkit.createLabel(composite, "", SWT.NONE);
     * label.setImage(MadlToolsPlugin.getImage("samples/docs.png"));
     * label.setCursor(getSite().getShell().getDisplay().getSystemCursor(SWT.CURSOR_HAND));
     * label.addMouseListener(new MouseAdapter() {
     * 
     * @Override public void mouseUp(MouseEvent e) { //ss
     * //ExternalBrowserUtil.openInExternalBrowser(href2); } });
     * 
     * formText = toolkit.createFormText(composite, true);
     * formText.setText("<form><p><a href=\"a\">Programmers Guide</a><br></br>" +
     * "Docs, articles and more</p></form>", true, false); formText.setFont(bigFont);
     * formText.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.TOP, 1, 1));
     * formText.addHyperlinkListener(openLink(href2)); section.setClient(client);
     * 
     * // create the Early Access Area section = toolkit.createSection(form.getBody(),
     * Section.TITLE_BAR); section.setText("Early Access"); section.setFont(bigFont); client =
     * toolkit.createComposite(section); l = new TableWrapLayout(); l.numColumns = 3;
     * l.verticalSpacing = 10; l.makeColumnsEqualWidth = true; client.setLayout(l);
     * 
     * for (final SampleDescription description : SampleDescriptionHelper.getDescriptions()) { if
     * (description.name.equals("TodoMVC") || description.name.equals("Angular Madl")) { composite =
     * toolkit.createComposite(client); lc = new TableWrapLayout(); lc.numColumns = 2;
     * composite.setLayout(lc); addSampleInfo(composite, description); } }
     */
    section.setClient(client);

    form.reflow(true);
  }

  @Override
  public void dispose() {
    toolkit.dispose();

    super.dispose();
    if (bigFont != null) {
      bigFont.dispose();
    }

  }

  @Override
  public void doSave(IProgressMonitor monitor) {

  }

  @Override
  public void doSaveAs() {

  }

  @Override
  public void init(IEditorSite site, IEditorInput input) {
    setSite(site);
    setInput(input);
    setTitleToolTip(input.getToolTipText());
  }

  @Override
  public boolean isDirty() {
    return false;
  }

  @Override
  public boolean isSaveAsAllowed() {
    return false;
  }

  @Override
  public void setFocus() {

  }

  private void addSampleInfo(Composite client, final SampleDescription description) {

    Label label = toolkit.createLabel(client, "", SWT.NONE);
    label.setImage(MadlToolsPlugin.getImage(description.logoPath));
    label.setCursor(getSite().getShell().getDisplay().getSystemCursor(SWT.CURSOR_HAND));
    label.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseUp(MouseEvent e) {
        //SampleHelper.openSample(description, getSite());
      }
    });

    FormText formText = toolkit.createFormText(client, true);
    formText.setText("<form><p><a href=\"open:woot\">" + description.name + "</a><br></br>"
        + description.description + "</p></form>", true, false);
    formText.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.TOP, 1, 1));
    formText.setFont(bigFont);
    formText.addHyperlinkListener(new HyperlinkAdapter() {
      @Override
      public void linkActivated(HyperlinkEvent e) {
        //SampleHelper.openSample(description, getSite());
      }
    });
  }

  private void createExternalLink(Composite client, String text, final String href) {

    Hyperlink link = toolkit.createHyperlink(client, text, SWT.NONE);
    link.setFont(bigFont);
    link.setLayoutData(new TableWrapData(TableWrapData.FILL, TableWrapData.MIDDLE));
    link.addHyperlinkListener(new HyperlinkAdapter() {
      @Override
      public void linkActivated(HyperlinkEvent e) {
        //ss
        //ExternalBrowserUtil.openInExternalBrowser(href);
      }
    });

  }

  private Font getBigFont(FontData[] fontData) {
    FontData[] bigFontData = getModifiedFontData(fontData);
    Font bigFont = new Font(Display.getCurrent(), bigFontData);
    return bigFont;
  }

  private HyperlinkAdapter openLink(final String href) {
    return new HyperlinkAdapter() {
      @Override
      public void linkActivated(HyperlinkEvent e) {
        //ss
        //ExternalBrowserUtil.openInExternalBrowser(href);
      }
    };
  }

}
