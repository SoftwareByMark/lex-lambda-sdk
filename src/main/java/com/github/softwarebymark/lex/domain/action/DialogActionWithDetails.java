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

import com.github.softwarebymark.lex.domain.Message;
import com.github.softwarebymark.lex.domain.ResponseCard;

/**
 * An abstract Dialog Action with a Response Card and Message
 *
 * @author Mark Borner
 */
public abstract class DialogActionWithDetails extends DialogAction {

    private Message message;
    private ResponseCard responseCard;

    public DialogActionWithDetails(String type) {
        super(type);
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public ResponseCard getResponseCard() {
        return responseCard;
    }

    public void setResponseCard(ResponseCard responseCard) {
        this.responseCard = responseCard;
    }

}