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

package com.github.softwarebymark.lex.domain;

import com.github.softwarebymark.lex.domain.action.DialogAction;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * A Lex Response
 *
 * @author Mark Borner
 */
public class LexResponse {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    static {
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    private final DialogAction dialogAction;
    private Map<String,String> sessionAttributes;

    public LexResponse(DialogAction dialogAction) {
        this.dialogAction = dialogAction;
    }

    public byte[] toJson() throws Exception {
        return OBJECT_MAPPER.writeValueAsBytes(this);
    }

    public DialogAction getDialogAction() {
        return dialogAction;
    }

    public Map<String, String> getSessionAttributes() {
        return sessionAttributes;
    }

    public void setSessionAttributes(Map<String, String> sessionAttributes) {
        this.sessionAttributes = sessionAttributes;
    }
}
