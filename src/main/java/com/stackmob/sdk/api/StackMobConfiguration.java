/**
 * Copyright 2012 StackMob
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stackmob.sdk.api;

import com.stackmob.sdk.callback.StackMobRedirectedCallback;

import java.util.Map;

public class StackMobConfiguration {
    public static final String DEFAULT_API_KEY = "DEFAULT_API_KEY";//do not change this
    public static final String DEFAULT_API_SECRET = "DEFAULT_API_SECRET";//do not change this

    public static final String API_KEY = DEFAULT_API_KEY;
    public static final String API_SECRET = DEFAULT_API_SECRET;
    public static String USER_OBJECT_NAME = "user";
    public static Integer API_VERSION = 0;

    public static String API_URL_FORMAT = "api.mob1.stackmob.com";
    public static String PUSH_API_URL_FORMAT = "push.mob1.stackmob.com";

    public static StackMobRedirectedCallback redirectedCallback = new StackMobRedirectedCallback() {
        @Override public void redirected(String originalURL, Map<String, String> redirectHeaders, String redirectBody, String newURL) {
            //do nothing for now
        }
    };
    
    public static StackMob newStackMob() {
        return new StackMob(API_KEY,
                            API_SECRET,
                            USER_OBJECT_NAME,
                            API_VERSION,
                            API_URL_FORMAT,
                            PUSH_API_URL_FORMAT,
                            redirectedCallback);
    }

}
