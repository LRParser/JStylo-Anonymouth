/*
 * JGAAP -- a graphical program for stylometric authorship attribution
 * Copyright (C) 2009,2011 by Patrick Juola
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/**
 **/
package com.jgaap.eventDrivers;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;

import org.apache.log4j.Logger;

import com.jgaap.JGAAPConstants;
import com.jgaap.backend.EventDriverFactory;
import com.jgaap.generics.Document;
import com.jgaap.generics.Event;
import com.jgaap.generics.EventDriver;
import com.jgaap.generics.EventGenerationException;
import com.jgaap.generics.EventSet;

/**
 * Filters all Event strings against named file and removes named events.
 * Compare to WhiteListEventSet, which removes all BUT named events
 * 
 * @see WhiteListEventDriver
 */
public class BlackListEventDriver extends EventDriver {

	private static Logger logger = Logger.getLogger(BlackListEventDriver.class);
	
    @Override
    public String displayName(){
    	return "Black-List";
    }
    
    @Override
    public String tooltipText(){
    	return "Filtered Event Set with Named Events Removed";
    }

    @Override
    public String longDescription(){
    	return "Filtered Event Set with Named Events Removed";
    }
    
    @Override
    public boolean showInGUI(){
    	return false;
    }

    private EventDriver underlyingEvents;

    private String      filename;

    @Override
    public EventSet createEventSet(Document ds) throws EventGenerationException {
        String param;
        HashSet<String> blacklist = new HashSet<String>();

        String word;

        if (!(param = (getParameter("underlyingEvents"))).equals("")) {
        	try {
                underlyingEvents = EventDriverFactory.getEventDriver(param);
            } catch (Exception e) {
                logger.error("Error: cannot create EventDriver " + param+" Using NaiveWordEventSet",e);
                underlyingEvents = new NaiveWordEventDriver();
            }
        } else { // no underlyingEventsParameter, use NaiveWordEventSet
            underlyingEvents = new NaiveWordEventDriver();
        }

        if (!(param = (getParameter("filename"))).equals("")) {
            filename = param;
        } else { // no underlyingfilename,
            filename = null;
        }

        EventSet es = underlyingEvents.createEventSet(ds);

        EventSet newEs = new EventSet();
        newEs.setAuthor(es.getAuthor());
        newEs.setNewEventSetID(es.getAuthor());

        BufferedReader br = null;

        if (filename != null) {
            try {
            	InputStream is;
            	if(filename.startsWith(JGAAPConstants.JGAAP_RESOURCE_PACKAGE)){
            		is = getClass().getResourceAsStream(filename);
            	} else {
            		is = new FileInputStream(filename);
            	}
                br = new BufferedReader(new InputStreamReader(is));

                while ((word = br.readLine()) != null) {
                    blacklist.add(word.trim());
                }

            } catch (IOException e) {
                // catch io errors from FileInputStream or readLine()
                System.out.println("Cannot open/read " + filename);
                System.out.println("IOException error! " + e.getMessage());
            } finally {
                // if the file opened okay, make sure we close it
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException ioe) {
                    }
                }
            }
        } else {
            blacklist.clear();
        }

        for (Event event : es) {
            String s = (event).toString();
            if (!blacklist.contains(s)) {
                newEs.addEvent(event);
            }
        }
        return newEs;
    }

}
