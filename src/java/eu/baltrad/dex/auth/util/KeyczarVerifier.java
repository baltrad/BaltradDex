/*
Copyright 2012 Estonian Meteorological and Hydrological Institute

This file is part of BaltradFrame.

BaltradFrame is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

BaltradFrame is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with BaltradFrame. If not, see <http://www.gnu.org/licenses/>.
*/

package eu.baltrad.dex.auth.util;

import org.keyczar.exceptions.KeyczarException;

public class KeyczarVerifier implements Verifier {
  private org.keyczar.Verifier verifier;

  /**
   * Constructor.
   * @param keyLocation Location of the key to use for signing
   * @throws KeyczarException
   */
  public KeyczarVerifier(String keyLocation) throws KeyczarException {
      verifier = new org.keyczar.Verifier(keyLocation);
  }
  
  /**
   * Verifies message.
   * @param message Message to verify
   * @param signature Signature used for verification
   * @return True in case of successful verification, false upon failure
   * @throws KeyczarException 
   */
  public boolean verify(String message, String signature) 
          throws KeyczarException {
      return verifier.verify(message, signature);
  }
}
