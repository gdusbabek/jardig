package org.dusbabek.tools.jardig;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.GridBagLayout;
import java.awt.*;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JList;
import javax.swing.JSplitPane;
import javax.swing.BorderFactory;
import java.awt.Color;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.io.File;
import javax.swing.DefaultComboBoxModel;
import java.io.OutputStream;
import java.io.PrintStream;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetListener;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.io.*;
import java.awt.datatransfer.*;
import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import java.net.URL;

/**
 * Front end for Digger.
 */
public class DigFrame
    extends JFrame {
    private DefaultComboBoxModel fileModel = new DefaultComboBoxModel();
    private final OutputStream os;

    /**
     * Sets up components.
     */
    public DigFrame() {
        try {
            DropTargetListener dtl = new DropTargetListener() {
                public void drop(DropTargetDropEvent e) {
                    if (okFlavor(e.getCurrentDataFlavors())) {
                        handleDrop(e);
                    }
                }
                public void dragExit(DropTargetEvent e) {
                }
                public void dragEnter(DropTargetDragEvent e) {
                    if (okFlavor(e.getCurrentDataFlavors()))
                        e.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
                    else
                        e.rejectDrag();
                }
                public void dragOver(DropTargetDragEvent e) {
                    if (okFlavor(e.getCurrentDataFlavors()))
                        e.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
                    else
                        e.rejectDrag();
                }
                public void dropActionChanged(DropTargetDragEvent e) {
                    System.out.println("ACTION CHANGED");
                }

            };
            DropTarget dt = new DropTarget(fileList,0,dtl);
            fileList.setDropTarget(dt);
            jbInit();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
        os = new TextAreaOutputStream(this.out);
    }

    // handle a file drop.
    private void handleDrop(File file) {
        ScanFile sf = new ScanFile(file);
        fileModel.addElement(sf);
    }

    // handle a generic dnd drop.
    private void handleDrop(DropTargetDropEvent e) {
        e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
        DataFlavor[] flavors = e.getTransferable().getTransferDataFlavors();
        // handle each flavor individually. We're looking for files, by the way.
        for (int i = 0; i < flavors.length; i++) {
            try {
                Object o = e.getTransferable().getTransferData(flavors[i]);
                if (o instanceof Collection) {
                    Collection c = (Collection)o;
                    for (Iterator it = c.iterator(); it.hasNext();) {
                        Object ob = it.next();
                        if (ob instanceof File)
                            handleDrop((File)ob);
                    }
                }
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
            catch (UnsupportedFlavorException ex) {
                ex.printStackTrace();
            }
        }
        e.dropComplete(true);
    }

    // see if the flavor is ok.
    private boolean okFlavor(DataFlavor[] flavor) {
        for (int i = 0; i < flavor.length; i++) {
            if (flavor[i].equals(DataFlavor.javaFileListFlavor))
                /** @todo add support for dropping in a string path */
//                || flavor[i].equals(DataFlavor.stringFlavor))
                return true;
        }
        return false;
    }

    private void jbInit() throws Exception {
        this.getContentPane().setLayout(gridBagLayout1); jLabel1.setText(
            "Class Name:");
        jPanel1.setLayout(gridBagLayout2); go.setMnemonic('G');
        go.setText("Go!"); go.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                go_actionPerformed(e);
            }
        }); jPanel2.setLayout(gridBagLayout3); recurseDirectories.
            setText("Recurse directories"); logInfo.setText(
            "Log INFO (verbose)"); logFind.setToolTipText("");
        logFind.setText("Log FIND"); logException.setText("Log EXCEPTION");
            logError.setToolTipText("");
        logError.setText("Log ERROR"); out.setEditable(false); out.setTabSize(4); out.setWrapStyleWord(true);
            out.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                out_mouseClicked(e);
            }
        }); jSplitPane1.setOrientation(JSplitPane.VERTICAL_SPLIT); jScrollPane2.
            setBorder(border2); jScrollPane1.setBorder(border8); jPanel2.
            setBorder(border6); fileList.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                fileList_keyPressed(e);
            }
        }); fileList.setModel(fileModel); summarize.setText("Summarize at end"); this.
            setTitle("Jardig"); recurseArchive.setText("Recurse archives");
            logNest.setToolTipText("Extra logging for archive recursion");
        logNest.setText("Log NEST"); help.setMnemonic('H'); help.setText("Help");
            help.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    help_actionPerformed(e);
                }
                catch (IOException ex) {
                    ex.printStackTrace(new PrintStream(os));
                }
            }
        });
        jPanel2.add(recurseDirectories,
                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
                                           , GridBagConstraints.WEST,
                                           GridBagConstraints.NONE,
                                           new Insets(0, 0, 0, 0), 0, 0));
        jSplitPane1.add(
            jScrollPane2, JSplitPane.TOP); jSplitPane1.add(jScrollPane1,
            JSplitPane.BOTTOM); this.getContentPane().add(jSplitPane1,
            new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0
                                   , GridBagConstraints.CENTER,
                                   GridBagConstraints.BOTH,
                                   new Insets(0, 0, 0, 0), 0, 0));
        jScrollPane1.getViewport().add(out);
        jScrollPane2.getViewport().add(fileList); this.
            getContentPane().add(jPanel1,
                                 new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0
            , GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
            new Insets(0, 0, 0, 0), 0, 0)); this.getContentPane().add(jPanel2,
            new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0
                                   , GridBagConstraints.WEST,
                                   GridBagConstraints.HORIZONTAL,
                                   new Insets(0, 0, 0, 0), 0, 0)); jPanel2.add(logError,
            new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
                                   , GridBagConstraints.WEST,
                                   GridBagConstraints.NONE,
                                   new Insets(0, 0, 0, 0), 0, 0)); jPanel2.add(
            summarize, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
                                              , GridBagConstraints.WEST,
                                              GridBagConstraints.NONE,
                                              new Insets(0, 0, 0, 0), 0, 0)); jPanel2.add(recurseArchive,
            new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
                                   , GridBagConstraints.WEST,
                                   GridBagConstraints.NONE,
                                   new Insets(0, 0, 0, 0), 0, 0)); jPanel2.add(
            logFind, new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0
                                            , GridBagConstraints.WEST,
                                            GridBagConstraints.NONE,
                                            new Insets(0, 0, 0, 0), 0, 0)); jPanel2.add(
            logInfo, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
                                            , GridBagConstraints.WEST,
                                            GridBagConstraints.NONE,
                                            new Insets(0, 0, 0, 0), 0, 0));
        jPanel2.add(
            logException, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0
                                                 , GridBagConstraints.WEST,
                                                 GridBagConstraints.NONE,
                                                 new Insets(0, 0, 0, 0), 0, 0));
            jPanel2.add(logNest, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            , GridBagConstraints.WEST, GridBagConstraints.NONE,
            new Insets(0, 0, 0, 0), 0, 0)); jPanel1.add(jLabel1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            , GridBagConstraints.EAST, GridBagConstraints.NONE,
            new Insets(0, 0, 0, 2), 0, 0)); jPanel1.add(className,
            new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
                                   , GridBagConstraints.CENTER,
                                   GridBagConstraints.HORIZONTAL,
                                   new Insets(0, 0, 0, 0), 0, 0)); jPanel1.add(
            go, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
                                       , GridBagConstraints.CENTER,
                                       GridBagConstraints.NONE,
                                       new Insets(0, 0, 0, 0), 0, 0)); jPanel1.add(help,
            new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0
                                   , GridBagConstraints.CENTER,
                                   GridBagConstraints.NONE,
                                   new Insets(0, 3, 0, 3), 0, 0)); jSplitPane1.setDividerLocation(100);
    }

    private GridBagLayout gridBagLayout1 = new GridBagLayout();
    private JPanel jPanel1 = new JPanel();
    private JPanel jPanel2 = new JPanel();
    private GridBagLayout gridBagLayout2 = new GridBagLayout();
    private JLabel jLabel1 = new JLabel();
    private JTextField className = new JTextField();
    private JButton go = new JButton();
    private GridBagLayout gridBagLayout3 = new GridBagLayout();
    private JCheckBox recurseDirectories = new JCheckBox();
    private JCheckBox logInfo = new JCheckBox();
    private JCheckBox logFind = new JCheckBox();
    private JCheckBox logException = new JCheckBox();
    private JCheckBox logError = new JCheckBox();
    private JScrollPane jScrollPane1 = new JScrollPane();
    private JTextArea out = new JTextArea();
    private JScrollPane jScrollPane2 = new JScrollPane();
    private JList fileList = new JList();
    private JSplitPane jSplitPane1 = new JSplitPane();
    private Border border1 = BorderFactory.createLineBorder(Color.black, 1);
    private Border border2 = new TitledBorder(border1, "Directories to scan");
    private Border border5 = BorderFactory.createLineBorder(Color.black, 1);
    private Border border6 = new TitledBorder(border5, "Options");
    private Border border7 = BorderFactory.createLineBorder(Color.black, 1);
    private Border border8 = new TitledBorder(border7, "Scan Log");
    private JCheckBox summarize = new JCheckBox();
    private JCheckBox recurseArchive = new JCheckBox();
    private JCheckBox logNest = new JCheckBox();
    private JButton help = new JButton();

    // handle a mouse click in the output pane.
    private void out_mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 3)
            out.setText("");
    }

    // enable or disable key components.
    private void enableComponents(boolean b) {
        go.setEnabled(b);
        recurseDirectories.setEnabled(b);
        recurseArchive.setEnabled(b);
        logInfo.setEnabled(b);
        logError.setEnabled(b);
        logException.setEnabled(b);
        logFind.setEnabled(b);
        logNest.setEnabled(b);
        fileList.setEnabled(b);
        summarize.setEnabled(b);
    }

    // handle the go button getting pressed.
    private synchronized void go_actionPerformed(ActionEvent e) {
        Thread digThread = new Thread("Dig Thread") {
            public void run() {
                enableComponents(false);
                int logLevel = 0;
                if (logInfo.isSelected())
                    logLevel |= Digger.INFO;
                if (logError.isSelected())
                    logLevel |= Digger.ERRORS;
                if (logException.isSelected())
                    logLevel |= Digger.EXCEPTIONS;
                if (logFind.isSelected())
                    logLevel |= Digger.FINDS;
                if (logNest.isSelected())
                    logLevel |= Digger.NESTED;
                ArrayList<Digger.Result[]> res = new ArrayList<Digger.Result[]>();
                for (int i = 0; i < fileModel.getSize(); i++) {
                    ScanFile sf = (ScanFile)fileModel.getElementAt(i);
                    Digger digger = new Digger(sf.file,recurseDirectories.isSelected(),recurseArchive.isSelected(),new PrintStream(os),logLevel);
                    res.add(digger.dig(className.getText()));
                }
                if (summarize.isSelected()) {
                    String EOL = System.getProperty("line.separator");
                    out.append("Summary follows..." + EOL);
                    out.append("Searched for " + className.getText() + EOL);
                    int resultCount = 0;
                    for (Iterator<Digger.Result[]> it = res.iterator(); it.hasNext();) {
                        Digger.Result[] r = it.next();
                        for (int i = 0; i < r.length; i++)
                            resultCount++;
                    }
                    out.append("There were " + resultCount + " hits." + EOL);
                    for (Iterator<Digger.Result[]> it = res.iterator(); it.hasNext();) {
                        Digger.Result[] r = it.next();
                        for (int i = 0; i < r.length; i++)
                            out.append("Found in " + r[i].heritage() + EOL);
                    }
                    out.append("End of summary." + EOL);
                }
                out.append("Done.");
                out.setCaretPosition(out.getText().length());

                enableComponents(true);
            }
        };
        digThread.start();
    }

    // handle a key being pressed on the file list (for deletes).
    private void fileList_keyPressed(KeyEvent e) {
        if (KeyEvent.VK_DELETE == e.getKeyCode()) {
            Object[] selected = fileList.getSelectedValues();
            for (int i = 0; i < selected.length; i++)
                fileModel.removeElement(selected[i]);
        }
    }

    private class ScanFile {
        private File file;
        ScanFile(File f) {
            file = f;
        }
        public String toString() { return file.getAbsolutePath(); }
        public boolean equals(Object obj) {
            if(!(obj instanceof ScanFile)) return false;
            ScanFile other = (ScanFile)obj;
            return other.file.equals(file);
        }
    }

    public static void main(String args[]) {
        DigFrame df = new DigFrame();
        df.setSize(800,480);
        df.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        df.setVisible(true);
    }

    // respond to the help button getting pressed.
    private void help_actionPerformed(ActionEvent e) throws IOException {
        // grab help document from this.
        URL url = DigFrame.class.getClassLoader().getResource("help/help.html");
        InputStream in = null;
        try {
            in = url.openStream();
        }
        catch (Throwable ex) {
            in = new FileInputStream("help/help.html");
        }
        boolean ok = true;
        int zero = 0;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        while (ok && zero < 100) {
            int avail = in.available();
            if (avail == 0) {
                zero++;
                continue;
            }
            else if (avail < 0) {
                ok = false;
                continue;
            }
            else {
                byte[] buf = new byte[avail];
                int read = in.read(buf);
                out.write(buf,0,read);
            }
        }
        String html = new String(out.toByteArray());
        out.close();
        HelpFrame help = new HelpFrame(html,this);
        help.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        help.setVisible(true);
    }
}
