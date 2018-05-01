/*
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
 *
 * **************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.pentaho.di.ui.core.dialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.core.gui.GUIResource;
import org.pentaho.di.ui.spoon.Spoon;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

import java.net.MalformedURLException;
import java.net.URL;

public class ShowHelpDialog extends Dialog {

  private static Class<?> PKG = Spoon.class;

  private static final String DOC_URL = Spoon.DOCUMENTATION_URL;
  private static final String PREFIX = "https://";
  private static final String PRINT_PREFIX = "https://f1.";
  private static final int TOOLBAR_HEIGHT = 25;
  private static final int TOOL_ITEM_WIDTH = 47;
  private static final int TOOL_ITEM_SPACING = 4;
  private static final int MARGIN = 5;

  private boolean fromBackOrForward;
  private boolean fromPrint;
  private boolean loading;

  private String dialogTitle;
  private String url;
  private String homeURL;

  private Browser wBrowser;

  private ToolBar toolBarBack;
  private ToolItem tltmBack;
  private ToolBar toolBarForward;
  private ToolItem tltmForward;
  private ToolItem tltmRefresh;
  private ToolItem tltmHome;
  private ToolItem tltmPrint;

  private Image imageBackEnabled;
  private Image imageBackDisabled;
  private Image imageForwardEnabled;
  private Image imageForwardDisabled;
  private Image imageRefreshEnabled;
  private Image imageRefreshDisabled;
  private Image imageHomeEnabled;
  private Image imageHomeDisabled;
  private Image imagePrintEnabled;
  private Image imagePrintDisabled;
  private Text textURL;

  private Cursor cursorEnabled;
  private Cursor cursorDisabled;

  private Shell shell;
  private Display display;
  private PropsUI props;

  public ShowHelpDialog( Shell parent, String dialogTitle, String url, String header ) {
    super( parent, SWT.NONE );
    props = PropsUI.getInstance();
    this.dialogTitle = BaseMessages.getString( PKG, "Spoon.Documentation.Pentaho.Title" );
    this.url = url;
    try {
      URL docURL = new URL( DOC_URL );
      this.homeURL = docURL.toString();
    } catch ( MalformedURLException e ) {
    }
  }

  public ShowHelpDialog( Shell parent, String dialogTitle, String url ) {
    this( parent, dialogTitle, url, "" );
  }

  protected Shell createShell( Shell parent ) {
    return new Shell( parent, SWT.RESIZE | SWT.MAX | SWT.MIN | SWT.DIALOG_TRIM );
  }

  public void open() {
    Shell parent = getParent();
    display = parent.getDisplay();

    shell = createShell( parent );
    shell.setImage( GUIResource.getInstance().getImageSpoon() );
    props.setLook( shell );

    FormLayout formLayout = new FormLayout();

    shell.setLayout( formLayout );
    shell.setText( dialogTitle );

    //Set Images
    setImages();

    // Canvas
    wBrowser = new Browser( shell, SWT.NONE );
    props.setLook( wBrowser );

    FormData fdBrowser = new FormData();
    fdBrowser.top = new FormAttachment( 0, TOOLBAR_HEIGHT + MARGIN );
    fdBrowser.right = new FormAttachment( 100, 0 );
    fdBrowser.bottom = new FormAttachment( 100, 0 );
    fdBrowser.left = new FormAttachment( 0, 0 );
    wBrowser.setLayoutData( fdBrowser );

    toolBarBack = new ToolBar( shell, SWT.FLAT );
    FormData fdtoolBarBack = new FormData();
    fdtoolBarBack.top = new FormAttachment( 0, MARGIN );
    fdtoolBarBack.right = new FormAttachment( 0, 27 );
    fdtoolBarBack.bottom = new FormAttachment( 0, TOOLBAR_HEIGHT );
    fdtoolBarBack.left = new FormAttachment( 0, MARGIN + 1 );
    toolBarBack.setLayoutData( fdtoolBarBack );
    toolBarBack.setCursor( cursorDisabled );
    toolBarBack.setBackground( toolBarBack.getParent().getBackground() );

    tltmBack = new ToolItem( toolBarBack, SWT.NONE );
    tltmBack.setImage( imageBackEnabled );
    tltmBack.setDisabledImage( imageBackDisabled );
    tltmBack.setToolTipText( BaseMessages.getString( PKG, "Spoon.Documentation.Tooltip.Back" ) );
    tltmBack.setEnabled( false );

    toolBarForward = new ToolBar( shell, SWT.FLAT );
    FormData fdtoolBarForward = new FormData();
    fdtoolBarForward.top = new FormAttachment( 0, MARGIN );
    fdtoolBarForward.right = new FormAttachment( toolBarBack, TOOL_ITEM_WIDTH );
    fdtoolBarForward.bottom = new FormAttachment( 0, TOOLBAR_HEIGHT );
    fdtoolBarForward.left = new FormAttachment( toolBarBack, TOOL_ITEM_SPACING );
    toolBarForward.setLayoutData( fdtoolBarForward );
    toolBarForward.setCursor( cursorDisabled );
    toolBarForward.setBackground( toolBarForward.getParent().getBackground() );

    tltmForward = new ToolItem( toolBarForward, SWT.NONE );
    tltmForward.setImage( imageForwardEnabled );
    tltmForward.setDisabledImage( imageForwardDisabled );
    tltmForward.setToolTipText( BaseMessages.getString( PKG, "Spoon.Documentation.Tooltip.Forward" ) );
    tltmForward.setEnabled( false );

    ToolBar toolBarRefresh = new ToolBar( shell, SWT.FLAT );
    FormData fdtoolBarRefresh = new FormData();
    fdtoolBarRefresh.top = new FormAttachment( 0, MARGIN );
    fdtoolBarRefresh.right = new FormAttachment( toolBarForward, TOOL_ITEM_WIDTH );
    fdtoolBarRefresh.bottom = new FormAttachment( 0, TOOLBAR_HEIGHT );
    fdtoolBarRefresh.left = new FormAttachment( toolBarForward, TOOL_ITEM_SPACING - 1 );
    toolBarRefresh.setLayoutData( fdtoolBarRefresh );
    toolBarRefresh.setCursor( cursorEnabled );
    toolBarRefresh.setBackground( toolBarRefresh.getParent().getBackground() );

    tltmRefresh = new ToolItem( toolBarRefresh, SWT.NONE );
    tltmRefresh.setImage( imageRefreshEnabled );
    tltmRefresh.setDisabledImage( imageRefreshDisabled );
    tltmRefresh.setToolTipText( BaseMessages.getString( PKG, "Spoon.Documentation.Tooltip.Refresh" ) );
    tltmRefresh.setEnabled( true );

    ToolBar toolBarHome = new ToolBar( shell, SWT.FLAT );
    FormData fdtoolBarHome = new FormData();
    fdtoolBarHome.top = new FormAttachment( 0, MARGIN );
    fdtoolBarHome.right = new FormAttachment( toolBarRefresh, TOOL_ITEM_WIDTH );
    fdtoolBarHome.bottom = new FormAttachment( 0, TOOLBAR_HEIGHT );
    fdtoolBarHome.left = new FormAttachment( toolBarRefresh, TOOL_ITEM_SPACING );
    toolBarHome.setLayoutData( fdtoolBarHome );
    toolBarHome.setCursor( cursorEnabled );
    toolBarHome.setBackground( toolBarHome.getParent().getBackground() );

    tltmHome = new ToolItem( toolBarHome, SWT.NONE );
    tltmHome.setImage( imageHomeEnabled );
    tltmHome.setDisabledImage( imageHomeDisabled );
    tltmHome.setToolTipText( BaseMessages.getString( PKG, "Spoon.Documentation.Tooltip.Home" ) );
    tltmHome.setEnabled( true );

    ToolBar toolBarPrint = new ToolBar( shell, SWT.FLAT );
    FormData fdtoolBarPrint = new FormData();
    fdtoolBarPrint.top = new FormAttachment( 0, MARGIN );
    fdtoolBarPrint.right = new FormAttachment( 100, -7 );
    fdtoolBarPrint.bottom = new FormAttachment( 0, TOOLBAR_HEIGHT );
    toolBarPrint.setLayoutData( fdtoolBarPrint );
    toolBarPrint.setCursor( cursorEnabled );
    toolBarPrint.setBackground( toolBarPrint.getParent().getBackground() );

    tltmPrint = new ToolItem( toolBarPrint, SWT.NONE );
    tltmPrint.setImage( imagePrintEnabled );
    tltmPrint.setDisabledImage( imagePrintDisabled );
    tltmPrint.setToolTipText( BaseMessages.getString( PKG, "Spoon.Documentation.Tooltip.Print" ) );
    tltmPrint.setEnabled( true );

    textURL = new Text( shell, SWT.BORDER );
    FormData fdtext = new FormData();
    fdtext.top = new FormAttachment( 0, MARGIN );
    fdtext.right = new FormAttachment( toolBarPrint, -7 );
    fdtext.bottom = new FormAttachment( 0, 25 );
    fdtext.left = new FormAttachment( toolBarHome, TOOL_ITEM_SPACING );
    textURL.setLayoutData( fdtext );
    textURL.setForeground( new Color( display, 101, 101, 101 ) );

    wBrowser.setUrl( url );

    setUpListeners();

    // Specs are 760/530, but due to rendering differences, we need to adjust the actual hgt/wdt used
    BaseStepDialog.setSize( shell, 755, 538, true );
    shell.setMinimumSize( 515, 408 );


    shell.open();
    while ( !shell.isDisposed() ) {
      if ( !display.readAndDispatch() ) {
        display.sleep();
      }
    }
  }

  private void setImages() {
    imageBackEnabled = GUIResource.getInstance().getImageBackEnabled();
    imageBackDisabled = GUIResource.getInstance().getImageBackDisabled();
    imageForwardEnabled = GUIResource.getInstance().getImageForwardEnabled();
    imageForwardDisabled = GUIResource.getInstance().getImageForwardDisabled();
    imageRefreshEnabled = GUIResource.getInstance().getImageRefreshEnabled();
    imageRefreshDisabled = GUIResource.getInstance().getImageRefreshDisabled();
    imageHomeEnabled = GUIResource.getInstance().getImageHomeEnabled();
    imageHomeDisabled = GUIResource.getInstance().getImageHomeDisabled();
    imagePrintEnabled = GUIResource.getInstance().getImagePrintEnabled();
    imagePrintDisabled = GUIResource.getInstance().getImagePrintDisabled();
    cursorEnabled = new Cursor( display, SWT.CURSOR_HAND );
    cursorDisabled = new Cursor( display, SWT.CURSOR_ARROW );
  }

  private void setUpListeners() {
    setUpSelectionListeners();
    addLocationListener();
    addShellListener();
  }

  private void setUpSelectionListeners() {
    SelectionListener selectionListenerBack = new SelectionListener() {
      @Override
      public void widgetSelected( SelectionEvent arg0 ) {
        back();
      }

      @Override
      public void widgetDefaultSelected( SelectionEvent arg0 ) {
      }
    };

    SelectionListener selectionListenerForward = new SelectionListener() {
      @Override
      public void widgetSelected( SelectionEvent arg0 ) {
        forward();
      }

      @Override
      public void widgetDefaultSelected( SelectionEvent arg0 ) {
      }
    };

    SelectionListener selectionListenerRefresh = new SelectionListener() {
      @Override
      public void widgetSelected( SelectionEvent arg0 ) {
        refresh();
      }

      @Override
      public void widgetDefaultSelected( SelectionEvent arg0 ) {
      }
    };

    SelectionListener selectionListenerHome = new SelectionListener() {
      @Override
      public void widgetSelected( SelectionEvent arg0 ) {
        home();
      }

      @Override
      public void widgetDefaultSelected( SelectionEvent arg0 ) {
      }
    };

    SelectionListener selectionListenerPrint = new SelectionListener() {
      @Override
      public void widgetSelected( SelectionEvent arg0 ) {
        print();
      }

      @Override
      public void widgetDefaultSelected( SelectionEvent arg0 ) {
      }
    };
    tltmBack.addSelectionListener( selectionListenerBack );
    tltmForward.addSelectionListener( selectionListenerForward );
    tltmRefresh.addSelectionListener( selectionListenerRefresh );
    tltmHome.addSelectionListener( selectionListenerHome );
    tltmPrint.addSelectionListener( selectionListenerPrint );
  }

  private void addLocationListener() {
    LocationListener listener = new LocationListener() {
      @Override
      public void changing( LocationEvent event ) {
        if ( event.location.endsWith( ".pdf" ) ) {
          Program.launch( event.location );
          event.doit = false;
        }
      }

      @Override
      public void changed( LocationEvent event ) {
        if ( !fromBackOrForward && !fromPrint ) {
          setForwardBackEnable();
        } else if ( fromPrint ) {
          wBrowser.execute( "javascript:window.print();" );
        }
        fromPrint = fromBackOrForward = false;
        textURL.setText( event.location );
        loading = false;
      }
    };
    wBrowser.addLocationListener( listener );
  }

  private void addShellListener() {
    // Detect [X] or ALT-F4 or something that kills this window...
    shell.addShellListener( new ShellAdapter() {
      public void shellClosed( ShellEvent e ) {
        ok();
      }
    } );
  }

  private void back() {
    fromBackOrForward = true;
    wBrowser.back();
    setForwardBackEnable( wBrowser.isBackEnabled(), true );
  }

  private void forward() {
    fromBackOrForward = true;
    wBrowser.forward();
    setForwardBackEnable( true, wBrowser.isForwardEnabled() );
  }

  private void refresh() {
    wBrowser.refresh();
  }

  private void print() {
    String currentURL = wBrowser.getUrl();
    boolean currentBackEnabled = tltmBack.isEnabled();
    boolean currentForwardEnabled = tltmForward.isEnabled();
    String f1URL = currentURL.replace( PREFIX, PRINT_PREFIX );
    fromPrint = true;
    wBrowser.setUrl( f1URL );
    loading = true;
    while ( loading ) {
      if ( !display.readAndDispatch() ) {
        display.sleep();
      }
    }
    wBrowser.setUrl( currentURL );
    setForwardBackEnable( currentBackEnabled, currentForwardEnabled );
  }

  private void home() {
    wBrowser.setUrl( homeURL != null ? homeURL : url );
    setForwardBackEnable( tltmBack.isEnabled(), false );
  }

  private void setForwardBackEnable() {
    setForwardBackEnable( wBrowser.isBackEnabled(), wBrowser.isForwardEnabled() );
  }

  private void setForwardBackEnable( boolean enableBack, boolean enableForward ) {
    setBackEnable( enableBack );
    setForwardEnable( enableForward );
  }

  private void setBackEnable( boolean enable ) {
    tltmBack.setEnabled( enable );
    toolBarBack.setCursor( enable ? cursorEnabled : cursorDisabled );
  }

  private void setForwardEnable( boolean enable ) {
    tltmForward.setEnabled( enable );
    toolBarForward.setCursor( enable ? cursorEnabled : cursorDisabled );
  }

  public void dispose() {
    shell.dispose();
  }

  private void ok() {
    dispose();
  }
}
