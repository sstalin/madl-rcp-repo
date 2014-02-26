/*
 * Copyright (c) 2012, the Madl project authors.
 *
 * Licensed under the Eclipse Public License v1.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package edu.depaul.cdm.madl.engine.source;

import edu.depaul.cdm.madl.engine.sdk.MadlSdk;

import java.net.URI;

/**
 * Instances of the class {@code MadlUriResolver} resolve {@code madl} URI's.
 *
 * @coverage madl.engine.source
 */
public class MadlUriResolver extends UriResolver {
  /**
   * The Madl SDK against which URI's are to be resolved.
   */
  private final MadlSdk sdk;

  /**
   * The name of the {@code madl} scheme.
   */
  private static final String MADL_SCHEME = "madl";

  /**
   * Return {@code true} if the given URI is a {@code madl:} URI.
   *
   * @param uri the URI being tested
   * @return {@code true} if the given URI is a {@code madl:} URI
   */
  public static boolean isMadlUri(URI uri) {
    return MADL_SCHEME.equals(uri.getScheme());
  }

  /**
   * Initialize a newly created resolver to resolve Madl URI's against the given platform within the
   * given Madl SDK.
   *
   * @param sdk the Madl SDK against which URI's are to be resolved
   */
  public MadlUriResolver(MadlSdk sdk) {
    this.sdk = sdk;
  }

  @Override
  public Source fromEncoding(ContentCache contentCache, UriKind kind, URI uri) {
    if (kind == UriKind.MADL_URI) {
      //ss
      //return sdk.fromEncoding(contentCache, kind, uri);
    }
    return null;
  }

  /**
   * Return the {@link MadlSdk} against which URIs are to be resolved.
   *
   * @return the {@link MadlSdk} against which URIs are to be resolved.
   */
  public MadlSdk getMadlSdk() {
    return sdk;
  }

  @Override
  public Source resolveAbsolute(ContentCache contentCache, URI uri) {
    if (!isMadlUri(uri)) {
      return null;
    }
    return sdk.mapMadlUri(uri.toString());
  }
}
