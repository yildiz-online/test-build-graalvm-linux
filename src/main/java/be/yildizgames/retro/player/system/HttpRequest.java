/*
 * This file is part of the Yildiz-Engine project, licenced under the MIT License  (MIT)
 *
 *  Copyright (c) 2019 Grégory Van den Borre
 *
 *  More infos available: https://engine.yildiz-games.be
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 *  documentation files (the "Software"), to deal in the Software without restriction, including without
 *  limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 *  of the Software, and to permit persons to whom the Software is furnished to do so,
 *  subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all copies or substantial
 *  portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 *  WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 *  OR COPYRIGHT  HOLDERS BE LIABLE FOR ANY CLAIM,
 *  DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE  SOFTWARE.
 *
 */
package be.yildizgames.retro.player.system;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;

/**
 * Http call to retrieve text or binary content.
 *
 * @author Grégory Van den Borre
 */
public class HttpRequest {

    /**
     * Logger.
     */
    private static final System.Logger LOGGER = System.getLogger(HttpRequest.class.toString());

    private final HttpClient client = HttpClient.newHttpClient();

    final Reader getReader(final URI uri) {
        return new InputStreamReader(this.getStream(uri, HttpResponse.BodyHandlers.ofInputStream()));
    }

    final Reader getReader(final String uri) {
        return this.getReader(URI.create(uri));
    }


    /**
     * Call to an HTTP get method, return the stream generated by the response.
     *
     * @param url Url to request.
     * @return The stream for the request url.
     * @throws IllegalStateException If an exception occurs.
     */
    private <T> T getStream(final URI url, HttpResponse.BodyHandler<T> bodyHandler){
        java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder(url).build();
        try {
            HttpResponse<T> response = this.client.send(request, bodyHandler);
            if (response.statusCode() > 299) {
                LOGGER.log(System.Logger.Level.ERROR, "Error retrieving content: {0} status: {1}", url, response.statusCode());
                throw new IllegalStateException("error.http.content.retrieve");
            }
            return response.body();
        } catch (IOException e) {
            LOGGER.log(System.Logger.Level.ERROR, "Error retrieving content: {0}", url, e);
            throw new IllegalStateException("error.http.content.retrieve");
        } catch (InterruptedException e) {
            LOGGER.log(System.Logger.Level.ERROR, "Error retrieving content: {0}", url, e);
            Thread.currentThread().interrupt();
            throw new IllegalStateException("error.http.content.retrieve");
        }
    }
}
