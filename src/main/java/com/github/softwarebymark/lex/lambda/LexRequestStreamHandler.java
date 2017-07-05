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

package com.github.softwarebymark.lex.lambda;

import com.github.softwarebymark.lex.LexRequestHandler;
import com.github.softwarebymark.lex.domain.Bot;
import com.github.softwarebymark.lex.domain.LexRequest;
import com.github.softwarebymark.lex.domain.LexResponse;
import com.github.softwarebymark.lex.verifier.LexResponseVerifier;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * A Lambda Request Stream Handler for processing a request from Lex
 *
 * @author Mark Borner
 */
public abstract class LexRequestStreamHandler implements RequestStreamHandler {

    private Logger logger = LoggerFactory.getLogger(getClass().getName());
    private LexResponseVerifier lexResponseVerifier = new LexResponseVerifier();
    private final LexRequestHandler lexRequestHandler;
    private final String botName;

    public LexRequestStreamHandler(String botName, LexRequestHandler lexRequestHandler) {
        if (botName == null) {
            throw new IllegalArgumentException("Bot name should not be null");
        }
        if (lexRequestHandler == null) {
            throw new IllegalArgumentException("LexRequestHandler should not be null");
        }
        this.lexRequestHandler = lexRequestHandler;
        this.botName = botName;
    }

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        byte[] requestBytes = IOUtils.toByteArray(inputStream);
        if (logger.isDebugEnabled()) {
            logger.debug("Request Json:\n {}", new String(requestBytes));
        }
        byte[] responseBytes;
        try {
            LexRequest lexRequest = LexRequest.fromJson(requestBytes);
            String requestBotName = nullSafeGetBotName(lexRequest);
            if (!botName.equals(requestBotName)) {
                throw new RuntimeException("This bot cannot handle request for bot named: " + requestBotName);
            }
            Map<String,String> sessionAttributes = new HashMap<>(lexRequest.getSessionAttributes());
            LexResponse lexResponse = lexRequestHandler.handleRequest(lexRequest, sessionAttributes);
            lexResponse.setSessionAttributes(sessionAttributes);
            lexResponseVerifier.verify(lexResponse);
            responseBytes = lexResponse.toJson();
            if (logger.isDebugEnabled()) {
                logger.debug("Response Json:\n {}", new String(responseBytes));
            }
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
        outputStream.write(responseBytes);
    }

    private String nullSafeGetBotName(LexRequest lexRequest) {
        Bot bot = lexRequest.getBot();
        if (bot == null) {
            return null;
        } else {
            return bot.getName();
        }
    }

}
