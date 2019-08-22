/*
 * Copyright (C) 2003  ATM Express, Inc [shawnw@atmreports.com]
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * $Id: NoFail.java 114 2005-12-09 15:51:51Z christianc $
 */
package org.barracudamvc.taskdefs;

// java imports:
import java.util.*;

// 3rd-party imports:
import org.apache.tools.ant.*;


/**
 * Task used to execute other tasks that may throw an exception when the build should continue.
 * Simply nest any tasks to be executed within this task. If any task throws a BuildException then
 * an optional property (named by the "setProperty" attribute) will be set but any other tasks will
 * still get executed. This task will never throw a BuildException, thus it always completes
 * successfully even if a nested task fails.
 *
 * @author shawnw@atmreports.com
 * @since saw_060403_1
 */
public class NoFail extends Task implements TaskContainer {
    private List tasks = new ArrayList();
    private String setProperty = null;
    private boolean echo = true;

    public void addTask(Task task) {
        tasks.add(task);
    }

    public void setSetProperty(String setProperty) {
        this.setProperty = setProperty;
    }

    public void setEcho(boolean echo) {
        this.echo = echo;
    }

    public void execute() throws BuildException {
        Iterator it = tasks.iterator();
        while (it.hasNext()) {
            Task task = (Task) it.next();
            try {
                task.perform();
            } catch (BuildException e) {
                if (echo) {
                    String message = e.getMessage();
                    if (message!=null && !message.equals("")) handleErrorOutput(message);
                }

                if (setProperty!=null) {
                    getOwningTarget().getProject().setProperty(setProperty, "foobar");
                }
            }
        }
    }
};

/*
 * $Log: NoFail.java,v $
 */
