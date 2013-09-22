/*
 * Pentaho Data Integration Android Push Notifications https://github.com/latinojoel/pdi-apple-pushnotifications
 * 
 * Copyright (c) 2009 about.me/latinojoel
 * 
 * Licensed under the GNU General Public License, Version 3.0; you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 * http://www.gnu.org/licenses/gpl-3.0.html
 * 
 * The program is provided "as is" without any warranty express or implied, including the warranty of non-infringement
 * and the implied warranties of merchantibility and fitness for a particular purpose. The Copyright owner will not be
 * liable for any damages suffered by you as a result of using the Program. In no event will the Copyright owner be
 * liable for any special, indirect or consequential damages or lost profits even if the Copyright owner has been
 * advised of the possibility of their occurrence.
 */
package com.latinojoel.gcm.server;

import java.io.IOException;

/**
 * Exception thrown when GCM returned an error due to an invalid request.
 * <p>
 * This is equivalent to GCM posts that return an HTTP error different of 200.
 * 
 * @author <a href="mailto:jlatino@sapo.pt">Joel Latino</a>
 * @version $Revision: 666 $
 * 
 */
public final class InvalidRequestException extends IOException {

    /** The serial Version. **/
    private static final long serialVersionUID = 8339582610185567958L;
    private final int status;
    private final String description;

    public InvalidRequestException(int status) {
        this(status, null);
    }

    public InvalidRequestException(int status, String description) {
        super(getMessage(status, description));
        this.status = status;
        this.description = description;
    }

    /**
     * Gets exception message.
     * 
     * @param status the HTTP status code.
     * @param description the description message.
     * @return the exception message.
     */
    private static String getMessage(int status, String description) {
        final StringBuilder base = new StringBuilder("HTTP Status Code: ").append(status);
        if (description != null) {
            base.append("(").append(description).append(")");
        }
        return base.toString();
    }

    /**
     * Gets the HTTP Status Code.
     * 
     * @return the HTTP status code.
     */
    public int getHttpStatusCode() {
        return status;
    }

    /**
     * Gets the error description.
     * 
     * @return the description.
     */
    public String getDescription() {
        return description;
    }
}