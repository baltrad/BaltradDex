/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.baltrad.dex.net.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;

/**
 *
 * @author szewczenko
 */
@Controller
public class GetSubscriptionServlet {
    
    @RequestMapping("/get_subscription.htm")
    public ModelAndView handleRequest(HttpServletRequest request, 
            HttpServletResponse response) {
        return new ModelAndView();
    }
    
    
}
