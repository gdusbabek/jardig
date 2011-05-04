package org.dusbabek.tools.jardig;

import java.io.File;
import java.util.ArrayList;
import java.io.FileFilter;
import java.util.Collection;
import java.util.jar.*;
import java.util.Enumeration;
import java.io.*;

/**
 * Digs an archive for class files.
 * A sigle digger digs only in one path, but can dig more than one time for
 * different classes.
 */
class Digger {
    // possible options.
    final static int ERRORS =                     0x00000001;
    final static int EXCEPTIONS =                 0x00000002;
    final static int FINDS =                      0x00000004;
    final static int INFO =                       0x00000008;
    final static int NESTED =                     0x00000010;
    final static int EVERYTHING =                 0xffffffff;

    // path to scan
    private final File path;
    // true to exhaustively scan subdirectories.
    private final boolean recurseDirectories;
    // true to exhaustively scan archives within archives.
    private final boolean recurseArchives;
    private final FileFilter filter = new Filter();
    private final PrintStream out;
    private final int logLevel;

    Digger(File path, boolean recurseDirectories, boolean recurseArchives, PrintStream log, int logLevel) {
        this.path = path;
        this.recurseDirectories = recurseDirectories;
        this.recurseArchives = recurseArchives;
        this.out = log;
        this.logLevel = logLevel;
    }

    /** main entry for digging. surveys path and calls examine() */
    Result[] dig(String className) {
        if (logging(INFO))
            out.println("Looking for " + className + " in " + path.getAbsolutePath());
        ArrayList<Result> res = new ArrayList<Result>();
        // get a list of scannable files.
        File[] list = path.listFiles(filter);
        for (int i = 0; i < list.length; i++)
            // examine each file, adding its results to the common results.
            res.addAll(examine(list[i],className,null));
        return res.toArray(new Result[res.size()]);
    }

    // scan a file.
    // parent can be null, but it can also point to a parent result in the case
    // of scanning archives contained within archives.
    private Collection<Result> examine(File f, String className, Result parent) {
        ArrayList<Result> res = new ArrayList<Result>();
        if (f.isDirectory() && !recurseDirectories)
            return res;
        else if (f.isDirectory()) {
            if (logging(INFO))
                out.println("Diving into " + f.getAbsolutePath());
            File[] list = f.listFiles(filter);
            for (int i = 0; i < list.length; i++)
                res.addAll(examine(list[i],className,null));
        }
        else {
            if (logging(INFO)) {
                if (parent == null)
                    out.println("Examining " + f.getAbsolutePath());
                else
                    out.println("Examining " + parent.className + " within " + parent.heritage());
            }
            // dig into f.
            JarFile jf = null;
            try {
                jf = new JarFile(f);
            }
            catch (IOException ex) {
                if (logging(ERRORS))
                    out.println("ERROR: " + ex.getMessage());
                if (logging(EXCEPTIONS))
                    ex.printStackTrace();
                return res; // whatever is in it.
            }
            // enumerate over jar entries.
            for (Enumeration<JarEntry> e = jf.entries(); e.hasMoreElements();) {
                JarEntry entry = e.nextElement();
                String name = entry.getName();
                // if this is an embedded archive, drill down and search it too.
                if (isArchive(name) && recurseArchives) {
                    try {
//                        if (logging(NESTED))
//                            out.println("nesting into " + name + " which is in " + f.getAbsolutePath());
                        File extracted = extract(entry, jf);
                        res.addAll(examine(extracted, className,new Result(name,f)));
                        extracted.delete();
                    }
                    catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                // else, this may be a class. see if it is the one we are
                // looking for.
                else if (match(name,className)) {
                    Result r = parent == null ? new Result(className,f) : new NestedResult(className,f,parent);
                    res.add(r);
                    if (logging(FINDS))
                        out.println("Found in " + r.heritage());
                }
            }
        }
        return res;
    }

    // extract a file out of a jar using the default OS temp space.
    private static File extract(JarEntry e, JarFile f) throws IOException {
        String name = f.getName();
        name = name.replace('/','_');
        name = name.replace('\\','_');
        name = name.replace(':','_');
        File temp = File.createTempFile(name, ".zip");
        OutputStream out = new FileOutputStream(temp,false);
        long size = e.getSize();
        long read = 0;
        InputStream in = f.getInputStream(e);
        while (read < size) {
            byte[] buf = new byte[Math.min(0x00100000,(int)(size-read))];
            int justRead = in.read(buf);
            out.write(buf,0,justRead);
            read += justRead;
        }
        out.close();
        return temp;
    }

    // return true if entryName is a match for className.
    private boolean match(String entryName, String className) {
        if (entryName.endsWith(".class"))
            entryName = entryName.substring(0,entryName.length() - ".class".length());
        entryName = entryName.replace('/','.');
        return entryName.equals(className);
    }

    // return true if we are logging a particular constant. handy utility method.
    private boolean logging(int constant) {
        return (constant & logLevel) > 0;
    }

    // return true if name is a suspected archive.
    private static boolean isArchive(String name) {
        return (name.endsWith(".jar") || name.endsWith(".zip")
                || name.endsWith(".war") || name.endsWith(".ear")
                || name.endsWith(".sar"));
    }

    // simple file filter.
    private class Filter
        implements FileFilter {
        public boolean accept(File f) {
            String name = f.getName().toLowerCase();
            if (isArchive(name))
                return true;
            else
                return f.isDirectory() && recurseDirectories;
        }

    }

    // class to hold results.
    class Result {
        private final String className;
        private final File file;

        Result(String className, File file) {
            this.className = className;
            this.file = file;
        }

        String getClassName() {
            return className;
        }

        File getFile() {
            return file;
        }

        public String heritage() {
            return file.getAbsolutePath();
        }
    }

    // class to hold a result that is contained within another result.
    class NestedResult
        extends Digger.Result {
        private final Result parent;
        private final String entryName;
        NestedResult(String className, File file, Result parent) {
            super(className, file);
            this.parent = parent;
            this.entryName = parent.getClassName();
        }

        Result getParent() {
            return parent;
        }

        public String heritage() {
            return entryName + " within " + parent.heritage();
        }
    }

}
