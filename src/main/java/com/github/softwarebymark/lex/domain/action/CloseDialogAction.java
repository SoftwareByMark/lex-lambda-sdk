/*
 *    Copyright 2017 Mark Borner
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.github.softwarebymark.lex.domain.action;

import com.github.softwarebymark.lex.domain.ResponseCard;
import com.github.softwarebymark.lex.domain.FulfillmentState;
import com.github.softwarebymark.lex.domain.Message;

/**
 * The Close Dialog Action
 *
 * @author Mark Borner
 */
public class CloseDialogAction extends DialogActionWithDetails {

    private final FulfillmentState fulfillmentState;

    public CloseDialogAction(FulfillmentState fulfillmentState) {
        super("Close");
        if (fulfillmentState == null) {
            throw new IllegalArgumentException("FulfillmentState should not be null");
        }
        this.fulfillmentState = fulfillmentState;
    }

    public CloseDialogAction(FulfillmentState fulfillmentState, Message message) {
        this(fulfillmentState);
        setMessage(message);
    }

    public CloseDialogAction(FulfillmentState fulfillmentState, Message message, ResponseCard responseCard) {
        this(fulfillmentState, message);
        setResponseCard(responseCard);
    }

    public CloseDialogAction(FulfillmentState fulfillmentState, ResponseCard responseCard) {
        this(fulfillmentState);
        setResponseCard(responseCard);
    }

    public FulfillmentState getFulfillmentState() {
        return fulfillmentState;
    }

}
