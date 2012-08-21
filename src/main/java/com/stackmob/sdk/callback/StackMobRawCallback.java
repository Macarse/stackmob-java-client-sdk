/**
 * Copyright 2011 StackMob
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

package com.stackmob.sdk.callback;

import com.stackmob.sdk.net.HttpVerb;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public abstract class StackMobRawCallback {

    protected HttpVerb requestVerb;
    protected String requestURL;
    protected List<Map.Entry<String, String>> requestHeaders;
    protected String requestBody;
    protected Integer responseStatusCode;
    protected List<Map.Entry<String, String>> responseHeaders;
    protected byte[] responseBody;
    protected int retriesRemaining = 3;


    /**
     * the method that will be called when the call to StackMob is complete. may be executed in a background thread
     * @param requestVerb the HTTP verb that was requested
     * @param requestURL the URL that was requested
     * @param requestHeaders the headers in the request
     * @param requestBody the body of the request. will be an empty string for GET, DELETE, etc...
     * @param responseStatusCode the status code of the HTTP response from StackMob
     * @param responseHeaders the response headers from StackMob
     * @param responseBody the response body from StackMob
     */
    public void setDone(HttpVerb requestVerb,
                        String requestURL,
                        List<Map.Entry<String, String>> requestHeaders,
                        String requestBody,
                        Integer responseStatusCode,
                        List<Map.Entry<String, String>> responseHeaders,
                        byte[] responseBody) {
        this.requestVerb = requestVerb;
        this.requestURL = requestURL;
        this.requestHeaders = requestHeaders;
        this.requestBody = requestBody;
        this.responseStatusCode = responseStatusCode;
        this.responseHeaders = responseHeaders;
        this.responseBody = responseBody;
        done(requestVerb, requestURL, requestHeaders, requestBody, responseStatusCode, responseHeaders, responseBody);
    }

    /**
     * the method that will be called when the call to StackMob is complete. may be executed in a background thread
     * @param requestVerb the HTTP verb that was requested
     * @param requestURL the URL that was requested
     * @param requestHeaders the headers in the request
     * @param requestBody the body of the request. will be an empty string for GET, DELETE, etc...
     * @param responseStatusCode the status code of the HTTP response from StackMob
     * @param responseHeaders the response headers from StackMob
     * @param responseBody the response body from StackMob
     */
    public abstract void done(HttpVerb requestVerb,
                              String requestURL,
                              List<Map.Entry<String, String>> requestHeaders,
                              String requestBody,
                              Integer responseStatusCode,
                              List<Map.Entry<String, String>> responseHeaders,
                              byte[] responseBody);

    /**
     * The method that will be called when a retry is possible. This is triggered when there is a distinct
     * and short term reason your request failed, and it should be successful on retry after the specified
     * interval. By default at most three attempts are made before failing. Override to implement your own
     * logic on retry, and return false to stop the request from being automatically retried.
     * @param afterMilliseconds the number of milliseconds to wait until retrying the request.
     * @return whether or not to automatically retry
     */
    public boolean retry(int afterMilliseconds) {
        try {
            Thread.currentThread().wait(afterMilliseconds);
        } catch (InterruptedException ignore) { }
        return true;
    }

    /**
     *
     * @return The number of times the request will be automatically retried if necessary
     */
    public int getRetriesRemaining() {
        return retriesRemaining;
    }

    /**
     *
     * @param remaining the number of times the request should be retried
     */
    public void setRetriesRemaining(int remaining) {
        retriesRemaining = remaining;
    }

    /**
     * get the total number of items from the Content-Range header
     * @return the total number of items returned in the Content-Range header, -1 if there was no Content-Range header
     * or it was malformed, -2 if the Content-Length header was present and well formed but the instance length was "*"
     */
    public int getTotalObjectCountFromPagination() {
        return getTotalNumberOfItemsFromContentRange(responseHeaders);
    }

    /**
     * get the total number of items from the Content-Range header
     * @param responseHeaders the headers that were returned in the response
     * @return the total number of items returned in the Content-Range header, -1 if there was no Content-Range header
     * or it was malformed, -2 if the Content-Length header was present and well formed but the instance length was "*"
     */
    public static Integer getTotalNumberOfItemsFromContentRange(List<Map.Entry<String, String>> responseHeaders) {
        if(responseHeaders == null) return -1;
        Map.Entry<String, String> contentLengthHeader = null;

        for(Map.Entry<String, String> header: responseHeaders) {
            if(header.getKey() != null && header.getKey().toLowerCase().equals("content-range")) {
                contentLengthHeader = header;
            }
        }

        if(contentLengthHeader != null) {
            List<String> hyphenSplit = Arrays.asList(contentLengthHeader.getValue().split("\\-"));
            if(hyphenSplit.size() != 2) {
                return -1;
            }
            List<String> slashSplit = Arrays.asList(hyphenSplit.get(1).split("/"));
            if(slashSplit.size() != 2) {
                return -1;
            }
            String instanceLengthString = slashSplit.get(1).trim();
            if(instanceLengthString.equals("*")) {
                return -2;
            }
            try {
                return Integer.parseInt(instanceLengthString);
            }
            catch(Throwable t) {
                return -1;
            }
        }
        else {
            return -1;
        }
    }
}
