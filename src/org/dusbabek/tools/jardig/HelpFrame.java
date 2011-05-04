// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   HelpFrame.java

package org.dusbabek.tools.jardig;

import java.awt.*;
import javax.swing.*;

class HelpFrame extends JFrame
{

    HelpFrame(String html, Frame owner)
    {
        gridBagLayout1 = new GridBagLayout();
        jScrollPane1 = new JScrollPane();
        text = new JEditorPane();
        try
        {
            text.setContentType("text/html");
            text.setText(html);
            text.setCaretPosition(0);
            jbInit();
            setSize(640, 480);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void jbInit()
        throws Exception
    {
        getContentPane().setLayout(gridBagLayout1);
        setTitle("Help");
        text.setEditable(false);
        jScrollPane1.getViewport().add(text);
        jScrollPane1.getViewport().add(text);
        getContentPane().add(jScrollPane1, new GridBagConstraints(0, 0, 1, 1, 1.0D, 1.0D, 10, 1, new Insets(0, 0, 0, 0), 0, 0));
    }

    private GridBagLayout gridBagLayout1;
    private JScrollPane jScrollPane1;
    private JEditorPane text;
}
