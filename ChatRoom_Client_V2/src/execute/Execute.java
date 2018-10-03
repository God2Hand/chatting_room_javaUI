/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package execute;

import dao.Client;
import view.LoginFrame;

/**
 *
 * @author Shinelon
 */
public class Execute {
    public static void main(String[] args) {
        Client client = new Client();
        LoginFrame login = new LoginFrame(client);
        login.setVisible(true);
    }
}
