/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.baltrad.dex.registry.model;

/**
 *
 * @author szewczenko
 */
public interface IDeliveryRegistryManager {
    public boolean entryExists(int userId, String uuid);
}
