/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.baltrad.dex.user.model;

/**
 *
 * @author szewczenko
 */
public interface IUserManager {
    
    public User getByName(String userName);
    
    public int saveOrUpdatePeer(User user) throws Exception; 
    
}
