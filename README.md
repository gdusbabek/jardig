# Jardig

I wrote jardig some time in 2004 to help me find the locations of some runtime dependencies in an application I was working on.

### Building

    ant jar

### Running
`'ant run'` or `'java -jar jardig.jar'`

### Help
The **Help** button in the upper right corner produced a useful message.

* Enter the name of the class you are looking for in the **Class Name:** field.
* Then check your **Options**.
  * **Recurse Directories** tells the digger to exhaustively search subdirectories.
  * **Recurse Archives** tells the digger to digger to dig into archives nested inside of other archives (for example: .ear and .war).
  * **Summarize at end** is useful if you have **Log INFO** turned on.  It prints a succint summary of the results after scanning is finished.
  * **Log INFO** turns on rich messages.  You will be notified every time a directory is entered or a file is scanned.
  * **Log ERROR** logs error conditions.
  * **Log EXCEPTION** logs exceptions.
  * **Log FIND** logs class name matches.  You must have this or **Summarize at end** turned on in order to find out the results of your scan.
  * **Log NEST** enables additional output when recursively scanning archives within other archives.
* Drag the directories you wish to scan into the **Directories to scan** list.  If you wish to remove a directory, select it and press delete (or backspace).
* Click the **Go!** button.
* You should see output (as much as you opted for) in the **Scan log** page on the bottom.  To clear the output pane, triple-click it.

