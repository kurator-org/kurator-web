/** WorkflowPackageVerifier.java
 *
 * Copyright 2017 President and Fellows of Harvard College
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * Verify the integrity of a workflow zip package file that was signed using the jarsigner tool
 */
public class WorkflowPackageVerifier {

    static class JarVerifier {

        private URL jarURL = null;
        private JarFile jarFile = null;

        JarVerifier(URL jarURL) {
            this.jarURL = jarURL;
        }

        /**
         * Retrive the jar file from the specified url.
         */
        private JarFile retrieveJarFileFromURL(URL url)
                throws PrivilegedActionException, MalformedURLException {
            JarFile jf = null;

            // Prep the url with the appropriate protocol.
            jarURL =
                    url.getProtocol().equalsIgnoreCase("jar") ?
                            url :
                            new URL("jar:" + url.toString() + "!/");
            // Retrieve the jar file using JarURLConnection
            jf = AccessController.doPrivileged(
                    new PrivilegedExceptionAction<JarFile>() {
                        public JarFile run() throws Exception {
                            JarURLConnection conn =
                                    (JarURLConnection) jarURL.openConnection();
                            // Always get a fresh copy, so we don't have to
                            // worry about the stale file handle when the
                            // cached jar is closed by some other application.
                            conn.setUseCaches(false);
                            return conn.getJarFile();
                        }
                    });
            return jf;
        }

        /**
         * First, retrieve the jar file from the URL passed in constructor.
         * Then, compare it to the expected X509Certificate.
         * If everything went well and the certificates are the same, no
         * exception is thrown.
         */
        public void verify(X509Certificate targetCert)
                throws IOException {
            // Sanity checking
            if (targetCert == null) {
                throw new SecurityException("Provider certificate is invalid");
            }

            try {
                if (jarFile == null) {
                    jarFile = retrieveJarFileFromURL(jarURL);
                }
            } catch (Exception ex) {
                SecurityException se = new SecurityException();
                se.initCause(ex);
                throw se;
            }

            Vector<JarEntry> entriesVec = new Vector<JarEntry>();

            // Ensure the jar file is signed.
            Manifest man = jarFile.getManifest();
            if (man == null) {
                throw new SecurityException("The provider is not signed");
            }

            // Ensure all the entries' signatures verify correctly
            byte[] buffer = new byte[8192];
            Enumeration entries = jarFile.entries();

            while (entries.hasMoreElements()) {
                JarEntry je = (JarEntry) entries.nextElement();

                // Skip directories.
                if (je.isDirectory()) continue;
                entriesVec.addElement(je);
                InputStream is = jarFile.getInputStream(je);

                // Read in each jar entry. A security exception will
                // be thrown if a signature/digest check fails.
                int n;
                while ((n = is.read(buffer, 0, buffer.length)) != -1) {

                }
                is.close();
            }

            // Get the list of signer certificates
            Enumeration e = entriesVec.elements();

            while (e.hasMoreElements()) {
                JarEntry je = (JarEntry) e.nextElement();

                // Every file must be signed except files in META-INF.
                Certificate[] certs = je.getCertificates();
                if ((certs == null) || (certs.length == 0)) {
                    if (!je.getName().startsWith("META-INF"))
                        throw new SecurityException("The provider " +
                                "has unsigned " +
                                "class files.");
                } else {
                    // Check whether the file is signed by the expected
                    // signer. The jar may be signed by multiple signers.
                    // See if one of the signers is 'targetCert'.
                    int startIndex = 0;
                    X509Certificate[] certChain;
                    boolean signedAsExpected = false;

                    while ((certChain = getAChain(certs, startIndex)) != null) {
                        if (certChain[0].equals(targetCert)) {
                            // Stop since one trusted signer is found.
                            signedAsExpected = true;
                            break;
                        }
                        // Proceed to the next chain.
                        startIndex += certChain.length;
                    }

                    if (!signedAsExpected) {
                        throw new SecurityException("The provider " +
                                "is not signed by a " +
                                "trusted signer");
                    }
                }
            }
        }
    }

    /**
     * Extracts ONE certificate chain from the specified certificate array
     * which may contain multiple certificate chains, starting from index
     * 'startIndex'.
     */
    private static X509Certificate[] getAChain(Certificate[] certs,
                                               int startIndex) {
        if (startIndex > certs.length - 1)
            return null;

        int i;
        // Keep going until the next certificate is not the
        // issuer of this certificate.
        for (i = startIndex; i < certs.length - 1; i++) {
            if (!((X509Certificate)certs[i + 1]).getSubjectDN().
                    equals(((X509Certificate)certs[i]).getIssuerDN())) {
                break;
            }
        }
        // Construct and return the found certificate chain.
        int certChainSize = (i-startIndex) + 1;
        X509Certificate[] ret = new X509Certificate[certChainSize];
        for (int j = 0; j < certChainSize; j++ ) {
            ret[j] = (X509Certificate) certs[startIndex + j];
        }
        return ret;
    }

    public static final synchronized boolean checkIntegrity(File file, String keystoreLocation, char[] keystorePass)
            throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {

        // Provider's signing cert which is used to sign the jar.
        X509Certificate providerCert = null;

        // Provider URL from file argument
        URL providerURL = file.toURI().toURL();

        // Open a connnection to the provider ZIP file
        JarVerifier jv = new JarVerifier(providerURL);

        KeyStore ks = KeyStore.getInstance("JKS");
        InputStream readStream = new FileInputStream(keystoreLocation);
        ks.load(readStream, keystorePass);

        Enumeration<String> aliases = ks.aliases();

        // Check to see if the keystore contains a matching certificate
        while (aliases.hasMoreElements()) {
            boolean verifiedIntegrity = false;

            String alias = aliases.nextElement();
            providerCert = (X509Certificate) ks.getCertificate(alias);

            try {
                jv.verify(providerCert);
                verifiedIntegrity = true;
            } catch (Exception e) { }

            if (verifiedIntegrity)
                return true;
        }

        return false;
    }
}
