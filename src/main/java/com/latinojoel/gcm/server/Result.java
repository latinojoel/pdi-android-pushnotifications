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

import java.io.Serializable;

/**
 * Result of a GCM message request that returned HTTP status code 200.
 * 
 * @author <a href="mailto:jlatino@sapo.pt">Joel Latino</a>
 * @version $Revision: 666 $
 * 
 */
public final class Result implements Serializable {

    /** The serial Version. **/
    private static final long serialVersionUID = 8360789618407781082L;

    private final String messageId;
    private final String canonicalRegistrationId;
    private final String errorCode;

    /**
     * The GCM Result builder class.
     * 
     * @author <a href="mailto:jlatino@sapo.pt">Joel Latino</a>
     * @version $Revision: 666 $
     * 
     */
    public static final class Builder {

        // optional parameters
        private String messageId;
        private String canonicalRegistrationId;
        private String errorCode;

        /**
         * Sets the canonical registration id.
         * 
         * @param value the canonical registration id.
         * @return the GCM result builder.
         */
        public Builder canonicalRegistrationId(String value) {
            canonicalRegistrationId = value;
            return this;
        }

        /**
         * Sets the message id.
         * 
         * @param value the message id.
         * @return the GCM result builder.
         */
        public Builder messageId(String value) {
            messageId = value;
            return this;
        }

        /**
         * Sets the code error.
         * 
         * @param value the code error.
         * @return the GCM result builder.
         */
        public Builder errorCode(String value) {
            errorCode = value;
            return this;
        }

        /**
         * Builds the GCM result.
         * 
         * @return the GCM result.
         */
        public Result build() {
            return new Result(this);
        }
    }

    private Result(Builder builder) {
        canonicalRegistrationId = builder.canonicalRegistrationId;
        messageId = builder.messageId;
        errorCode = builder.errorCode;
    }

    /**
     * Gets the message id, if any.
     * 
     * @return the message id.
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * Gets the canonical registration id, if any.
     * 
     * @return the canonical registration id.
     */
    public String getCanonicalRegistrationId() {
        return canonicalRegistrationId;
    }

    /**
     * Gets the error code, if any.
     * 
     * @return the error code.
     */
    public String getErrorCodeName() {
        return errorCode;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder("[");
        if (messageId != null) {
            builder.append(" messageId=").append(messageId);
        }
        if (canonicalRegistrationId != null) {
            builder.append(" canonicalRegistrationId=").append(canonicalRegistrationId);
        }
        if (errorCode != null) {
            builder.append(" errorCode=").append(errorCode);
        }
        return builder.append(" ]").toString();
    }

}